package cn.visolink.firstplan.receipt.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.common.security.utils.HttpRequestUtil;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.exception.ResultUtil;
import cn.visolink.firstplan.commission.service.CommissionService;
import cn.visolink.firstplan.plannode.service.TopSettingTwoExcelService;
import cn.visolink.firstplan.receipt.dao.ReceiptDao;
import cn.visolink.firstplan.receipt.service.ReceiptService;
import cn.visolink.salesmanage.checklist.service.ChecklistService;
import cn.visolink.salesmanage.homeapply.dao.HomeApplyDao;
import cn.visolink.salesmanage.homeapply.entity.HomeApply;
import cn.visolink.salesmanage.packageanddiscount.dao.PackageanddiscountDao;
import cn.visolink.salesmanage.vlink.service.VlinkService;
import cn.visolink.system.timelogs.bean.SysLog;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * ??????????????? Service ?????????
 * </p>
 *
 * @author baoql
 * @since 2020-05-25
 */

@Service
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptDao receiptDao;
    private final CommissionService commissionServiceImpl;
    private final TopSettingTwoExcelService topSettingTwoExcelServiceImpl;
    private final VlinkService vlinkServiceImpl;
    private final HomeApplyDao homeApplyDao;
    private final PackageanddiscountDao packageanddiscountDao;


    @Resource(name = "jdbcTemplatemy352")
    private JdbcTemplate jdbcTemplatemy352;

    @Resource(name = "jdbcTemplatemy")
    private JdbcTemplate jdbcTemplatemy;

    @Value("${fkEsbTrade.url}")
    private String fkEsbTradeUrl;

    @Value("${fkEsbTrade.userId}")
    private String fkEsbTradeUserId;

    @Value("${fkEsbTrade.password}")
    private String fkEsbTradePassword;

    @Value("${createTimerisk.url}")
    private String createTimerisk;

    @Value("${createTimerisk.userId}")
    private String createTimeriskUserId;

    @Value("${createTimerisk.password}")
    private String createTimeriskPassword;



    private static final String NORMAL = "??????";
    private static final String RISK = "??????";
    private static final String UNKNOWN = "??????";

    public ReceiptServiceImpl(ReceiptDao receiptDao, CommissionService commissionServiceImpl, TopSettingTwoExcelService topSettingTwoExcelServiceImpl, VlinkService vlinkServiceImpl, HomeApplyDao homeApplyDao, PackageanddiscountDao packageanddiscountDao) {
        this.receiptDao = receiptDao;
        this.commissionServiceImpl = commissionServiceImpl;
        this.topSettingTwoExcelServiceImpl = topSettingTwoExcelServiceImpl;
        this.vlinkServiceImpl = vlinkServiceImpl;
        this.homeApplyDao = homeApplyDao;
        this.packageanddiscountDao = packageanddiscountDao;
    }

    /**
     * ???????????????
     *
     * @param map map
     * @return ResultBody
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody initReceipt(Map<String, Object> map) {
        ResultBody resultBody = new ResultBody();
        try {
            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("list");
            String uuid = UUID.randomUUID().toString();
            map.put("uuid", uuid);
            map.put("receipt_code", getFkdCode());

            /*??????????????????????????????*/
            for (Map<String, Object> mm : list) {
                this.getNowRisk(mm);
                mm.put("uuid", uuid);
                if(0 > (mm.get("project_amount") == null? 0.00 : Double.parseDouble(mm.get("project_amount")+""))){
                    mm.put("application_amount", mm.get("project_amount") );
                }
            }
            /*???????????????*/
            receiptDao.initReceipt(map);
            /*?????????????????????*/
            receiptDao.initReceiptDetail(list, map);
            resultBody.setCode(200);
            resultBody.setMessages("????????????");
        } catch (Exception e) {
            resultBody.setCode(-1);
            resultBody.setMessages("??????");
            return resultBody;
        }
        return resultBody;
    }

    /**
     * ?????????????????????
     *
     * @param map map
     * @return ResultBody
     */
    @Override
    public ResultBody initReceiptDetail(Map<String, Object> map) {
        ResultBody resultBody = new ResultBody();
        try {
            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("list");
            String uuid = map.get("uuid") + "";
            StringBuilder ids = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> mm = list.get(i);
                this.getNowRisk(map);
                mm.put("uuid", uuid);
                if(list.size()==1){
                    ids.append(mm.get("id"));
                }else{
                    if (i == 0) {
                        ids.append(mm.get("id")).append("'");
                    }else if (i == list.size() - 1){
                        ids.append("'").append(mm.get("id"));
                    }else{
                        ids.append("'").append(mm.get("id")).append("'");
                    }
                    if (i < list.size() - 1) {
                        ids.append(",");
                    }
                }
            }
            map.put("ids",ids);
            /*?????????????????????*/
            resultBody = this.getPaymentStatus(map);
            if(Integer.parseInt(resultBody.getData()+"")!=1){
                resultBody.setCode(-1);
                return resultBody;
            }
            receiptDao.initReceiptDetail(list, map);
            resultBody.setCode(200);
            resultBody.setMessages("????????????");
        } catch (Exception e) {
            resultBody.setCode(-1);
            resultBody.setMessages("??????");
            return resultBody;
        }
        return resultBody;
    }

    /**
     * ???????????????
     *
     * @param map map
     * @return ResultBody
     */
    @Override
    public ResultBody selectReceipt(Map<String, Object> map) {
        map = commissionServiceImpl.setSourceTypeDesc(map);
        PageHelper.startPage(Integer.parseInt(map.get("pageIndex") + ""), Integer.parseInt(map.get("pageSize") + ""));
        return commissionServiceImpl.getResultBody(map, receiptDao.selectReceipt(map));
    }

    /**
     * ???????????????
     *
     * @param map map
     * @return ResultBody
     */
    @Override
    public ResultBody selectWaitReceipt(Map<String, Object> map) {
        map = commissionServiceImpl.setSourceTypeDesc(map);
        PageHelper.startPage(Integer.parseInt(map.get("pageIndex") + ""), Integer.parseInt(map.get("pageSize") + ""));
        return commissionServiceImpl.getResultBody(map, receiptDao.selectWaitReceipt(map));
    }

    /**
     * ??????????????????
     *
     * @param map map
     * @return ResultBody
     */
    @Override
    public ResultBody selectCommissionStanding(Map<String, Object> map) {
        map = commissionServiceImpl.setSourceTypeDesc(map);
        PageHelper.startPage(Integer.parseInt(map.get("pageIndex") + ""), Integer.parseInt(map.get("pageSize") + ""));
        return commissionServiceImpl.getResultBody(map, receiptDao.selectCommissionStanding(map));
    }

    /**
     * ?????????????????????
     *
     * @param request  request
     * @param response response
     * @param ids      ids
     */
    @Override
    public void selectExcelCommissionStanding(HttpServletRequest request, HttpServletResponse response, String ids, Map<String, Object> map, String type) {
        List<Map<String,String>> result;
        int num = 35;
        String excelName = "CommissionStanding.xlsx";
        if ("2".equals(type)) {
            num = 31;
            excelName = "CommissionStanding2.xlsx";
        }
        if (map == null) {
            Map<String, Object> m = new HashMap<>(6);
            m.put("ids", ids);
            /*????????????????????????*/
            result = receiptDao.selectExcelCommissionStanding(m);
        } else {
            map = commissionServiceImpl.setSourceTypeDesc(map);
            result = receiptDao.selectCommissionStanding(map);
        }
        try {
            XSSFWorkbook workbook = topSettingTwoExcelServiceImpl.getWorkbook(request, excelName);
            XSSFSheet sheet = workbook.getSheetAt(0);
            CellStyle rowStyle = sheet.getRow(1).createCell(0).getCellStyle();

            for (int i = 0; i < result.size(); i++) {
                Map mm = result.get(i);
                Row row = sheet.createRow(i + 1);
                for (int c = 0; c < num; c++) {
                    row.createCell(c).setCellStyle(rowStyle);
                }
                row.getCell(0).setCellValue(mm.get("checklist_code") != null ? mm.get("checklist_code") + "" : "");
                row.getCell(1).setCellValue(mm.get("project_status") != null ? mm.get("project_status") + "" : "");
                row.getCell(2).setCellValue(mm.get("payment_status") != null ? mm.get("payment_status") + "" : "");
                row.getCell(3).setCellValue(mm.get("business_unit_name") != null ? mm.get("business_unit_name") + "" : "");
                row.getCell(4).setCellValue(mm.get("project_name") != null ? mm.get("project_name") + "" : "");
                row.getCell(5).setCellValue(mm.get("room_name") != null ? mm.get("room_name") + "" : "");
                row.getCell(6).setCellValue(mm.get("transaction_status") != null ? mm.get("transaction_status") + "" : "");
                row.getCell(7).setCellValue(mm.get("now_price") != null ? mm.get("now_price") + "" : "");
                row.getCell(8).setCellValue(mm.get("back_price") != null ? mm.get("back_price") + "" : "");
                row.getCell(9).setCellValue(mm.get("collection_proportion") != null ? mm.get("collection_proportion") + "" : "");
                row.getCell(10).setCellValue(mm.get("built_up_area") != null ? mm.get("built_up_area") + "" : "");
                row.getCell(11).setCellValue(mm.get("project_amount") != null ? mm.get("project_amount") + "" : "");
                row.getCell(12).setCellValue(mm.get("commission_point") != null ? mm.get("commission_point") + "" : "");
                row.getCell(13).setCellValue(mm.get("amount_closed") != null ? mm.get("amount_closed") + "" : "");
                row.getCell(14).setCellValue(mm.get("outstanding_amount") != null ? mm.get("outstanding_amount") + "" : "");
                row.getCell(15).setCellValue(mm.get("amount_paid") != null ? mm.get("amount_paid") + "" : "");
                row.getCell(16).setCellValue(mm.get("com_payment_ratio") != null ? mm.get("com_payment_ratio") + "" : "");
                row.getCell(17).setCellValue(mm.get("gain_by") != null ? mm.get("gain_by") + "" : "");
                row.getCell(18).setCellValue(mm.get("commission_type") != null ? mm.get("commission_type") + "" : "");
                row.getCell(19).setCellValue(mm.get("source_type_desc") != null ? mm.get("source_type_desc") + "" : "");
                if ("2".equals(type)) {
                    row.getCell(20).setCellValue(mm.get("employee_name") != null ? mm.get("employee_name") + "" : "");
                    row.getCell(21).setCellValue(mm.get("customer_name") != null ? mm.get("customer_name") + "" : "");
                    row.getCell(22).setCellValue(mm.get("subscription_date") != null ? mm.get("subscription_date") + "" : "");
                    row.getCell(23).setCellValue(mm.get("signing_date") != null ? mm.get("signing_date") + "" : "");
                    row.getCell(24).setCellValue(mm.get("commission_money") != null ? mm.get("commission_money") + "" : "");
                } else {
                    row.getCell(20).setCellValue(mm.get("current_role") != null ? mm.get("current_role") + "" : "");
                    row.getCell(21).setCellValue(mm.get("employee_name") != null ? mm.get("employee_name") + "" : "");
                    row.getCell(22).setCellValue(mm.get("customer_name") != null ? mm.get("customer_name") + "" : "");
                    row.getCell(23).setCellValue(mm.get("subscription_date") != null ? mm.get("subscription_date") + "" : "");
                    row.getCell(24).setCellValue(mm.get("signing_date") != null ? mm.get("signing_date") + "" : "");
                    row.getCell(25).setCellValue(mm.get("bank_num") != null ? mm.get("bank_num") + "" : "");
                    row.getCell(26).setCellValue(mm.get("bank_name") != null ? mm.get("bank_name") + "" : "");
                    row.getCell(27).setCellValue(mm.get("reportIdCard") != null ? mm.get("reportIdCard") + "" : "");
                    row.getCell(28).setCellValue(mm.get("customer_mobile") != null ? mm.get("customer_mobile") + "" : "");
                }
            }

            topSettingTwoExcelServiceImpl.exportExcelResponse(response, "????????????????????????", workbook);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(1001, "????????????????????????!");
        }
    }

    /**
     * ?????????????????????
     *
     * @param request  request
     * @param response response
     * @param ids      ids
     */
    @Override
    public void selectExcelWaitReceipt(HttpServletRequest request, HttpServletResponse response, String ids, Map<String, Object> map, String type) {
        List<Map<String,String>> result;
        int num = 35;
        String excelName = "WaitReceipt.xlsx";
        if ("2".equals(type)) {
            num = 31;
            excelName = "WaitReceipt2.xlsx";
        }
        if (map == null) {
            Map<String, Object> m = new HashMap<>(6);
            m.put("ids", ids);
            /*????????????????????????*/
            result = receiptDao.selectExcelWaitReceipt(m);
        } else {
            map = commissionServiceImpl.setSourceTypeDesc(map);
            result = receiptDao.selectWaitReceipt(map);
        }
        try {
            XSSFWorkbook workbook = topSettingTwoExcelServiceImpl.getWorkbook(request, excelName);
            XSSFSheet sheet = workbook.getSheetAt(0);
            CellStyle rowStyle = sheet.getRow(1).createCell(0).getCellStyle();

            for (int i = 0; i < result.size(); i++) {
                Map mm = result.get(i);
                Row row = sheet.createRow(i + 1);
                for (int c = 0; c < num; c++) {
                    row.createCell(c).setCellStyle(rowStyle);
                }
                row.getCell(0).setCellValue(mm.get("checklist_code") != null ? mm.get("checklist_code") + "" : "");
                row.getCell(1).setCellValue(mm.get("business_unit_name") != null ? mm.get("business_unit_name") + "" : "");
                row.getCell(2).setCellValue(mm.get("project_name") != null ? mm.get("project_name") + "" : "");
                row.getCell(3).setCellValue(mm.get("room_name") != null ? mm.get("room_name") + "" : "");
                row.getCell(4).setCellValue(mm.get("transaction_status") != null ? mm.get("transaction_status") + "" : "");
                row.getCell(5).setCellValue(mm.get("now_price") != null ? mm.get("now_price") + "" : "");
                row.getCell(6).setCellValue(mm.get("back_price") != null ? mm.get("back_price") + "" : "");
                row.getCell(7).setCellValue(mm.get("collection_proportion") != null ? mm.get("collection_proportion") + "" : "");
                row.getCell(8).setCellValue(mm.get("built_up_area") != null ? mm.get("built_up_area") + "" : "");
                row.getCell(9).setCellValue(mm.get("project_amount") != null ? mm.get("project_amount") + "" : "");
                row.getCell(10).setCellValue(mm.get("commission_point") != null ? mm.get("commission_point") + "" : "");
                row.getCell(11).setCellValue(mm.get("amount_closed") != null ? mm.get("amount_closed") + "" : "");
                row.getCell(12).setCellValue(mm.get("outstanding_amount") != null ? mm.get("outstanding_amount") + "" : "");
                row.getCell(13).setCellValue(mm.get("amount_paid") != null ? mm.get("amount_paid") + "" : "");
                row.getCell(14).setCellValue(mm.get("com_payment_ratio") != null ? mm.get("com_payment_ratio") + "" : "");
                row.getCell(15).setCellValue(mm.get("gain_by") != null ? mm.get("gain_by") + "" : "");
                row.getCell(16).setCellValue(mm.get("source_type_desc") != null ? mm.get("source_type_desc") + "" : "");
                if ("2".equals(type)) {
                    row.getCell(17).setCellValue(mm.get("employee_name") != null ? mm.get("employee_name") + "" : "");
                    row.getCell(18).setCellValue(mm.get("customer_name") != null ? mm.get("customer_name") + "" : "");
                    row.getCell(19).setCellValue(mm.get("subscription_date") != null ? mm.get("subscription_date") + "" : "");
                    row.getCell(20).setCellValue(mm.get("signing_date") != null ? mm.get("signing_date") + "" : "");
                    row.getCell(21).setCellValue(mm.get("commission_money") != null ? mm.get("commission_money") + "" : "");
                } else {
                    row.getCell(17).setCellValue(mm.get("current_role") != null ? mm.get("current_role") + "" : "");
                    row.getCell(18).setCellValue(mm.get("employee_name") != null ? mm.get("employee_name") + "" : "");
                    row.getCell(19).setCellValue(mm.get("customer_name") != null ? mm.get("customer_name") + "" : "");
                    row.getCell(20).setCellValue(mm.get("subscription_date") != null ? mm.get("subscription_date") + "" : "");
                    row.getCell(21).setCellValue(mm.get("signing_date") != null ? mm.get("signing_date") + "" : "");
                    row.getCell(22).setCellValue(mm.get("bank_num") != null ? mm.get("bank_num") + "" : "");
                    row.getCell(23).setCellValue(mm.get("bank_name") != null ? mm.get("bank_name") + "" : "");
                    row.getCell(24).setCellValue(mm.get("reportIdCard") != null ? mm.get("reportIdCard") + "" : "");
                    row.getCell(25).setCellValue(mm.get("commission_money") != null ? mm.get("commission_point") + "" : "");
                }
            }

            topSettingTwoExcelServiceImpl.exportExcelResponse(response, "?????????????????????", workbook);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(1001, "????????????????????????!");
        }
    }


    /**
     * ?????????????????????
     *
     * @param map map
     * @return ResultBody
     */
    @Override
    public ResultBody selectReceiptDetail(Map<String, Object> map) {
        map = commissionServiceImpl.setSourceTypeDesc(map);
        PageHelper.startPage(Integer.parseInt(map.get("pageIndex") + ""), Integer.parseInt(map.get("pageSize") + ""));
        List<Map<String, Object>> result = receiptDao.selectReceiptDetail(map);

        List<Object> tList = result.stream().map(obj -> obj.get("transaction_id")).collect(Collectors.toList());
        List<Map> tradeList = getTradeDate(result.get(0).get("mproject_id")+"",tList);

        for (Map<String, Object> detail : result) {
            if(tradeList != null && tradeList.size() > 0){
                for(Map trade : tradeList){
                    if(detail.get("transaction_id").equals(trade.get("tradeNo"))){
                        if(trade.get("sysRisk") !=null && !"".equals(trade.get("sysRisk"))){
                            detail.put("system_risk", getRisk(Integer.parseInt(trade.get("sysRisk")+"")));
                        }else{
                            detail.put("system_risk", "-");
                        }
                        if(trade.get("labourRisk") !=null && !"".equals(trade.get("labourRisk"))){
                            detail.put("artificial_risk", getRisk(Integer.parseInt(trade.get("labourRisk")+"")));
                        }else{
                            detail.put("artificial_risk", "-");
                        }
                    }
                }
            }else{
                map.put("system_risk", "??????");
                map.put("artificial_risk", "??????");
            }
        }
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(result);
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setData(pageInfo);
        resultBody.setCode(200);
        return resultBody;
    }



    /**
     * ??????????????????????????????
     *
     * @param projectId ??????id
     * @param tList ??????ids
     * @return List<Map>
     * */
    private List<Map> getTradeDate(String projectId,List<Object> tList){
        Map createTimeAndMap = new HashMap();
        Map esbInfoTime = new HashMap<>();
        esbInfoTime.put("instId", "47f1a9db3f434426baf8993b5df07e86");
        esbInfoTime.put("requestTime", "1577182054138");
        Map requestInfoTime = new HashMap<>();
        requestInfoTime.put("thirdPrjId", projectId);
        createTimeAndMap.put("esbInfo", esbInfoTime);
        createTimeAndMap.put("requestInfo", requestInfoTime);

        System.out.println( JSONObject.parseObject(JSONObject.toJSONString(createTimeAndMap)));
        JSONObject createTimeriskresult = HttpRequestUtil.httpPost(createTimerisk, createTimeriskUserId, createTimeriskPassword, JSONObject.parseObject(JSONObject.toJSONString(createTimeAndMap)), false);  //????????????

        Map aa = JSONObject.toJavaObject(createTimeriskresult,Map.class);
        Map date = (Map) aa.get("resultInfo");
        if(date == null){
            return null;
        }
        List<Map> projectInfo = (List<Map>) date.get("data");
        if(projectInfo ==null || projectInfo.size()==0){
            return null;
        }
        Map<String, Object> jsonMap = new HashMap<>();
        Map esbInfoId = new HashMap();
        esbInfoId.put("instId", "31be626ab2674c3181cbc3d68000c78c");
        esbInfoId.put("requestTime", "1577244179623");
        Map queryInfo = new HashMap();
        queryInfo.put("currentPage", 1);
        queryInfo.put("pageSize", 200);
        Map requestInfo = new HashMap();
        Map dataInfo = new HashMap();
        dataInfo.put("thirdPrjId", projectId);
        dataInfo.put("tradeNos", tList);
        requestInfo.put("data", dataInfo);

        jsonMap.put("esbInfo", esbInfoId);
        jsonMap.put("queryInfo", queryInfo);
        jsonMap.put("requestInfo", requestInfo);
        System.out.println(jsonMap + "queryInfoABC");
        System.out.println(JSONObject.toJSONString(jsonMap));
        System.out.println( JSONObject.parseObject(JSONObject.toJSONString(jsonMap)));
        JSONObject realResult = HttpRequestUtil.httpPost(fkEsbTradeUrl, fkEsbTradeUserId, fkEsbTradePassword, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)), false);  //????????????
        System.out.println("============????????????:"+realResult.toJSONString()+"============");
        Map realMap = JSONObject.toJavaObject(realResult,Map.class);
        Map data = (Map) realMap.get("data");
        Map createTimeriskMap = (Map) data.get("resultInfo");
        return (List<Map>) createTimeriskMap.get("tradeDate");
    }

    private String getRisk(int i){
        if(i == 0){
            return "??????";
        }else if(i == 1){
            return "?????????";
        }else if(i == 2){
            return "??????";
        }else if(i == 3){
            return "??????";
        }else if(i == 4){
            return "?????????";
        }else{
            return "??????";
        }
    }

    /**
     * ?????????????????????
     *
     * @param request  request
     * @param response response
     * @param rids     rids
     * @param ids      ids
     */
    @Override
    public void selectExcelReceiptDetail(HttpServletRequest request, HttpServletResponse response, String rids, String ids, Map<String, Object> map, String type) {
        List<Map<String, Object>> result;
        int num = 35;
        String excelName = "CommissionReceipt.xlsx";
        if ("2".equals(type)) {
            num = 31;
            excelName = "CommissionReceipt2.xlsx";
        }
        if (map == null) {
            Map<String, Object> m = new HashMap<>(6);
            m.put("rids", rids);
            m.put("ids", ids);
            /*????????????????????????*/
            result = receiptDao.selectExcelReceiptDetail(m);
        } else {
            map = commissionServiceImpl.setSourceTypeDesc(map);
            result = receiptDao.selectExcelWaitReceipt2(map);
        }
        try {
            XSSFWorkbook workbook = topSettingTwoExcelServiceImpl.getWorkbook(request, excelName);
            XSSFSheet sheet = workbook.getSheetAt(0);

            // ??????????????????????????????
            sheet.protectSheet("100010111000001001");

            // ??????
            XSSFFont font = workbook.createFont();
            font.setFontName("Microsoft YaHei Light");
            font.setFontHeightInPoints((short) 12);

            // ????????????
            CellStyle rowStyle = workbook.createCellStyle();
            rowStyle.setLocked(true);
            rowStyle.setFont(font);
            short index = IndexedColors.GREY_25_PERCENT.getIndex();
            rowStyle.setFillForegroundColor(index);
            rowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // ????????????
            CellStyle rowStyleUnLocke = workbook.createCellStyle();
            rowStyleUnLocke.setLocked(false);
            rowStyleUnLocke.setFont(font);

            for (int i = 0; i < result.size(); i++) {
                Map mm = result.get(i);
                Row row = sheet.createRow(i + 1);
                for (int c = 0; c < num; c++) {
                    row.createCell(c).setCellStyle(rowStyle);
                }
                row.getCell(0).setCellValue(mm.get("business_unit_name") != null ? mm.get("business_unit_name") + "" : "");
                row.getCell(1).setCellValue(mm.get("project_name") != null ? mm.get("project_name") + "" : "");
                row.getCell(2).setCellValue(mm.get("receipt_code") != null ? mm.get("receipt_code") + "" : "");
                row.getCell(3).setCellValue(mm.get("payment_code") != null ? mm.get("payment_code") + "" : "");
                row.getCell(4).setCellValue(mm.get("paymentStatus") != null ? mm.get("paymentStatus") + "" : "");
                row.getCell(5).setCellValue(mm.get("checklist_code") != null ? mm.get("checklist_code") + "" : "");
                row.getCell(6).setCellValue(mm.get("project_code") != null ? mm.get("project_code") + "" : "");
                row.getCell(7).setCellValue("????????????");
                row.getCell(8).setCellValue(mm.get("roomName") != null ? mm.get("roomName") + "" : "");
                row.getCell(9).setCellValue(mm.get("customer_name") != null ? mm.get("customer_name") + "" : "");
                row.getCell(10).setCellValue(mm.get("system_risk") != null ? mm.get("system_risk") + "" : "");
                row.getCell(11).setCellValue(mm.get("artificial_risk") != null ? mm.get("artificial_risk") + "" : "");
                row.getCell(12).setCellValue(mm.get("gain_by") != null ? mm.get("gain_by") + "" : "");
                row.getCell(13).setCellValue(mm.get("source_type_desc") != null ? mm.get("source_type_desc") + "" : "");

                if ("2".equals(type)) {
                    row.getCell(14).setCellValue(mm.get("transaction_status") != null ? mm.get("transaction_status") + "" : "");
                    row.getCell(15).setCellValue(mm.get("subscription_date") != null ? mm.get("subscription_date") + "" : "");
                    row.getCell(16).setCellValue(mm.get("signing_date") != null ? mm.get("signing_date") + "" : "");
                    row.getCell(17).setCellValue(mm.get("now_price") != null ? mm.get("now_price") + "" : "");
                    row.getCell(18).setCellValue(mm.get("back_price") != null ? mm.get("back_price") + "" : "");
                    row.getCell(19).setCellValue(mm.get("collection_proportion") != null ? mm.get("collection_proportion") + "" : "");
                    row.getCell(20).setCellValue(mm.get("project_amount") != null ? mm.get("project_amount") + "" : "");
                    row.getCell(21).setCellValue(mm.get("commission_point") != null ? mm.get("commission_point") + "" : "");
                    row.getCell(22).setCellValue(mm.get("amount_closed") != null ? mm.get("amount_closed") + "" : "");
                    Double outstanding_amount = 0.00;
                    if(mm.get("outstanding_amount") != null && !"".equals(mm.get("outstanding_amount"))){
                        outstanding_amount = Double.parseDouble(mm.get("outstanding_amount") + "");
                    }
                    row.getCell(23).setCellValue(outstanding_amount);
                    row.getCell(24).setCellValue(mm.get("com_payment_ratio") != null ? mm.get("com_payment_ratio") + "" : "");
                    row.createCell(25).setCellStyle(rowStyleUnLocke);
                    Double application_amount = 0.00;
                    if(mm.get("application_amount") != null && !"".equals(mm.get("application_amount"))){
                        application_amount = Double.parseDouble(mm.get("application_amount") + "");
                    }
                    row.getCell(25).setCellValue(application_amount);
                    row.getCell(26).setCellValue(mm.get("employee_name") != null ? mm.get("employee_name") + "" : "");
                    row.getCell(27).setCellValue(mm.get("customer_mobile") != null ? mm.get("customer_mobile") + "" : "");
                    row.getCell(28).setCellValue(mm.get("EmployeeName") != null ? mm.get("EmployeeName") + "" : "");
                    row.getCell(29).setCellValue(mm.get("create_time") != null ? mm.get("create_time") + "" : "");
                    row.getCell(30).setCellValue(mm.get("id") != null ? mm.get("id") + "" : "");
                } else {
                    row.getCell(14).setCellValue(mm.get("current_role") != null ? mm.get("current_role") + "" : "");
                    row.getCell(15).setCellValue(mm.get("transaction_status") != null ? mm.get("transaction_status") + "" : "");
                    row.getCell(16).setCellValue(mm.get("subscription_date") != null ? mm.get("subscription_date") + "" : "");
                    row.getCell(17).setCellValue(mm.get("signing_date") != null ? mm.get("signing_date") + "" : "");
                    row.getCell(18).setCellValue(mm.get("now_price") != null ? mm.get("now_price") + "" : "");
                    row.getCell(19).setCellValue(mm.get("back_price") != null ? mm.get("back_price") + "" : "");
                    row.getCell(20).setCellValue(mm.get("collection_proportion") != null ? mm.get("collection_proportion") + "" : "");
                    row.getCell(21).setCellValue(mm.get("project_amount") != null ? mm.get("project_amount") + "" : "");
                    row.getCell(22).setCellValue(mm.get("commission_point") != null ? mm.get("commission_point") + "" : "");
                    row.getCell(23).setCellValue(mm.get("amount_closed") != null ? mm.get("amount_closed") + "" : "");
                    Double outstanding_amount = 0.00;
                    if(mm.get("outstanding_amount") != null && !"".equals(mm.get("outstanding_amount"))){
                        outstanding_amount = Double.parseDouble(mm.get("outstanding_amount") + "");
                    }
                    row.getCell(24).setCellValue(outstanding_amount);
                    row.getCell(25).setCellValue(mm.get("com_payment_ratio") != null ? mm.get("com_payment_ratio") + "" : "");
                    row.createCell(26).setCellStyle(rowStyleUnLocke);
                    Double application_amount = 0.00;
                    if(mm.get("application_amount") != null && !"".equals(mm.get("application_amount"))){
                        application_amount = Double.parseDouble(mm.get("application_amount") + "");
                    }
                    row.getCell(26).setCellValue(application_amount);
                    row.getCell(27).setCellValue(mm.get("employee_name") != null ? mm.get("employee_name") + "" : "");
                    row.getCell(28).setCellValue(mm.get("customer_mobile") != null ? mm.get("customer_mobile") + "" : "");
                    row.getCell(29).setCellValue(mm.get("bank_num") != null ? mm.get("bank_num") + "" : "");
                    row.getCell(30).setCellValue(mm.get("bank_name") != null ? mm.get("bank_name") + "" : "");
                    row.getCell(31).setCellValue(mm.get("reportIdCard") != null ? mm.get("reportIdCard") + "" : "");
                    row.getCell(32).setCellValue(mm.get("EmployeeName") != null ? mm.get("EmployeeName") + "" : "");
                    row.getCell(33).setCellValue(mm.get("create_time") != null ? mm.get("create_time") + "" : "");
                    row.getCell(34).setCellValue(mm.get("id") != null ? mm.get("id") + "" : "");
                }
            }

            topSettingTwoExcelServiceImpl.exportExcelResponse(response, "?????????????????????", workbook);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(1001, "????????????????????????!");
        }
    }

    /**
     * ?????????????????????
     *
     * @param file   file
     * @param months months
     * @return ResultBody
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody receiptDetailImport(String userName, MultipartFile file, String months) {
        ResultBody resultBody = new ResultBody();
        try {
            File f = File.createTempFile("tmp", null);
            file.transferTo(f);
            f.deleteOnExit();
            FileInputStream fileInputStream;
            fileInputStream = new FileInputStream(f);
            XSSFWorkbook workBook = new XSSFWorkbook(fileInputStream);

            Sheet planSheet = workBook.getSheetAt(0);

            //?????????????????????
            int planSheetTotalRows = planSheet.getLastRowNum();
            int startRow = 1;
            int applicationAmount = 26;
            int outstandingAmount = 24;
            int id = 34;
            if ("2".equals(months)) {
                applicationAmount--;
                outstandingAmount--;
                id--;
            }
            Row row;
            /*????????????????????????*/
            for (int i = startRow; i <= planSheetTotalRows; i++) {
                row = planSheet.getRow(i);
                Map<String, Object> map = new HashMap<>(6);
                Double a = Double.parseDouble(getCellValueByCell(row.getCell(applicationAmount),0)+"");
                Double o = Double.parseDouble(getCellValueByCell(row.getCell(outstandingAmount),0)+"");
                if(a>o){
                    return ResultUtil.error(500, "?????????????????????????????????????????????");
                }
                map.put("application_amount", a);
                map.put("id", row.getCell(id).getStringCellValue());
                map.put("username", userName);
                Integer importChecklistDetail = receiptDao.updateReceiptDetail(map);
                receiptDao.updateReceiptAmount(map);
                if (importChecklistDetail < 0) {
                    return ResultUtil.error(500, "????????????????????????????????????????????????");
                }
            }
            resultBody.setCode(200);
            resultBody.setMessages("????????????");
        } catch (IllegalStateException e) {
            throw new BadRequestException(-15_1001, "Excel????????????????????????????????????????????????????????????");
        } catch (Exception e) {
            throw new BadRequestException(-15_1002, "?????????????????????????????????");
        }
        return resultBody;
    }

    /**
     * ?????????????????????????????????
     *
     * @param list list
     * @return Integer
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateReceiptDetail(List<Map<String, Object>> list,String userId) {
        long i = list.stream().map(map -> map.get("application_amount")).filter(s -> !s.equals("")).count();
        if(i>0){
            return receiptDao.updateReceiptDetailList(list, userId);
        }else{
            return 0;
        }
    }

    /**
     * ???????????????????????????
     *
     * @param map map
     * @return Integer
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateReceiptAmount(Map<String, Object> map) {
        ResultBody resultBody = new ResultBody();
        try {
            /*???????????????????????????*/
            /*map.put("username", SecurityUtils.getUsername());*/
            receiptDao.updateReceiptAmountAll(map);
            resultBody.setCode(200);
            resultBody.setMessages("????????????");
        } catch (Exception e) {
            resultBody.setCode(-1);
            resultBody.setMessages("??????");
            return resultBody;
        }
        return resultBody;
    }

    /**
     * ?????????????????????????????????
     *
     * @param map map
     * @return Integer
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map receiptAddApproval(HttpServletRequest request,Map map) {
        String requstTime = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
        Integer i = receiptDao.getCmreceipt(map);
        if (i > 0) {
            receiptDao.updatePaymentStatus(map);
            String paymentStatus = map.get("paymentStatus") + "";
            if ("4".equals(paymentStatus)) {
                receiptDao.updateAmountClosed(map);
                // vlinkServiceImpl.vlinkProjectApprove(request,map.get("receiptId")+"");
            } else if ("3".equals(paymentStatus)) {
                receiptDao.updateApplicationTime(map);
            }else if("2".equals(paymentStatus) && i == 4){
                receiptDao.updateAmountClosed2(map);
                receiptDao.updateApplicationTime(map);
            }
            return ResultUtil.getSuccessMap(requstTime);
        } else {
            return ResultUtil.getErrorMap(requstTime, "?????????????????????????????????");
        }
    }



    /**
     * ?????????????????????????????????
     *
     * @param map map
     * @return Integer
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updatePaymentStatus(Map map) {
        ResultBody resultBody = new ResultBody();
        try {
            /*???????????????????????????*/
            receiptDao.updatePaymentStatus2(map);
            resultBody.setCode(200);
            resultBody.setMessages("????????????");
        } catch (Exception e) {
            resultBody.setCode(-1);
            resultBody.setMessages("??????");
            return resultBody;
        }
        return resultBody;
    }

    /**
     * ???????????????????????????
     *
     * @param map map
     * @return Integer
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody deleteReceiptDetail(Map map) {
        ResultBody resultBody = new ResultBody();
        try {
            /*???????????????????????????*/
            receiptDao.deleteReceiptDetail(map);
            resultBody.setCode(200);
            resultBody.setMessages("????????????");
        } catch (Exception e) {
            resultBody.setCode(-1);
            resultBody.setMessages("??????");
            return resultBody;
        }
        return resultBody;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param map map
     * @return Integer
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody deleteReceipt(Map map) {
        ResultBody resultBody = new ResultBody();
        try {
            /*???????????????????????????*/
            receiptDao.deleteReceipt(map);
            receiptDao.deleteReceiptDetailAll(map);
            resultBody.setCode(200);
            resultBody.setMessages("????????????");
        } catch (Exception e) {
            resultBody.setCode(-1);
            resultBody.setMessages("??????");
            return resultBody;
        }
        return resultBody;
    }

    /**
     * ?????????????????????
     *
     * @param map map
     * @return Integer
     */
    @Override
    public ResultBody getPaymentStatus(Map map) {
        ResultBody<Object> resultBody = new ResultBody<>();
        List<Map<String, Object>> result = receiptDao.getPaymentStatus(map);

        double number = 0.0;
        String  checklistId = "";

        if (result != null && result.size() > 0) {
            int size = result.size();
            for (int i = 0;i<result.size();i++){
                Map<String, Object> mm = result.get(i);
                if(i < size -1){
                    if(!mm.get("checklistId").equals(result.get(i+1).get("checklistId"))){
                        resultBody.setData(3);
                        resultBody.setMessages("????????????????????????????????????????????????");
                        return resultBody;
                    }
                }

                int qk = mm.get("qk") == null ? 0:Integer.parseInt(mm.get("qk")+"");
                if(qk>0){
                    resultBody.setData(3);
                    resultBody.setMessages("???????????????????????????");
                    return resultBody;
                }
                String roomName = mm.get("room_name") == null ? "" : mm.get("room_name") + "";
                if (mm.get("outstanding_amount") == null || Double.parseDouble(mm.get("outstanding_amount") + "") == 0) {
                    resultBody.setData(3);
                    resultBody.setMessages("[" + roomName + "]  ??????????????????????????????????????????");
                    return resultBody;
                }
                if (mm.get("my_STATUS") == null || "??????".equals(mm.get("my_STATUS") + "")) {
                    resultBody.setData(3);
                    resultBody.setMessages("[" + roomName + "]  ???????????????????????????????????????");
                    return resultBody;
                }
                number = number + Double.parseDouble(mm.get("project_amount") + "");
                if (mm.get("payment_status") == null || "4".equals(mm.get("payment_status") + "")) {
                    resultBody.setData(1);
                } else {
                    resultBody.setData(3);
                    resultBody.setMessages("[" + roomName + "] ?????????????????????????????????????????????????????????");
                    return resultBody;
                }
            }
            if (number < 0) {
                resultBody.setData(3);
                resultBody.setMessages("????????????????????????0???????????????????????????");
                return resultBody;
            }
        } else {
            resultBody.setData(1);
        }
        resultBody.setCode(200);
        return resultBody;
    }

    /**
     * ???????????????????????????????????????
     *
     * @param map map
     * @return Integer
     */
    @Override
    public ResultBody getFileList(Map<String, Object> map) {
        map = commissionServiceImpl.setSourceTypeDesc(map);
        return commissionServiceImpl.getResultBody(map, receiptDao.getFileLists(map.get("id") + ""));
    }

    /**
     * ???????????????????????????????????????
     *
     * @param map map
     * @return Integer
     */
    @Override
    public ResultBody delFile(Map map) {
        ResultBody resultBody = new ResultBody();
        try {
            /*?????????????????????????????????*/
            receiptDao.delFile(map.get("id") + "");
            resultBody.setCode(200);
            resultBody.setMessages("????????????");
        } catch (Exception e) {
            resultBody.setCode(-1);
            resultBody.setMessages("??????");
            return resultBody;
        }
        return resultBody;
    }


    /**
     * ?????????????????????
     *
     * @param map map
     * @return Integer
     */
    @Override
    public ResultBody updateReceiptMyTrade(Map map) {
        List<Map<String, Object>> result = receiptDao.selectReceiptDetail(map);
        List<String> list = new ArrayList<>();
        for (Map<String, Object> mm : result) {
            list.add(mm.get("transaction_id") + "");
        }
        return commissionServiceImpl.updateMyTrade(list);
    }

    /**
     * ?????????????????????????????????
     *
     * @param map map
     * @return Integer
     */
    @Override
    public ResultBody getMyStatus(Map map) {
        ResultBody resultBody = new ResultBody();
        List<Map<String, Object>> result = receiptDao.selectReceiptDetail(map);

        List<Object> tList = result.stream().map(obj -> obj.get("transaction_id")).collect(Collectors.toList());
        List<Map> tradeList = getTradeDate(result.get(0).get("mproject_id")+"",tList);
        double all=0.00;
        for (Map<String, Object> mm : result) {
            String transactionIdsStr =  mm.get("transaction_id") == null ? "" : mm.get("transaction_id") + "";
            if (StrUtil.isBlank(transactionIdsStr)) {
                resultBody.setData("false");
                resultBody.setMessages("??????????????????????????????");
                return resultBody;
            }
            String commissionType = mm.get("commission_type")+"";
            if(!"".equals(commissionType) && !"null".equals(commissionType) && "???????????????".equals(commissionType)){
                String isAuthentication = mm.get("isAuthentication")+"";
                if(mm.get("reportIdCard") != null && !"".equals(mm.get("reportIdCard")) && !"null".equals(mm.get("reportIdCard"))){
                    if("".equals(isAuthentication) || "null".equals(isAuthentication)  || !"1".equals(isAuthentication)){
                        resultBody.setData("false");
                        resultBody.setMessages("???????????????:"+mm.get("bank_num")+""+"??????????????????!");
                        return resultBody;
                    }
                }else{
                    resultBody.setMessages("???????????????????????????");
                    return resultBody;
                }
            }
            String sql = "SELECT roominfo FROM dotnet_erp60.dbo.VS_XSGL_TFDETAIL WHERE TradeGUID IN ('" + transactionIdsStr+ "')";
            List<Map<String, Object>> roomInfoList = jdbcTemplatemy.queryForList(sql);
            roomInfoList.removeIf(obj -> obj.isEmpty());
            if (CollUtil.isNotEmpty(roomInfoList)) {
                List<Object> list = roomInfoList.stream().map(obj -> obj.get("roominfo")).collect(Collectors.toList());
                String join = CollUtil.join(list.iterator(), ",");
                resultBody.setData("false");
                resultBody.setMessages("[" + join + "]  ????????????????????????");
                return resultBody;
            }
            if (!"??????".equals(mm.get("my_STATUS"))) {
                resultBody.setData("false");
                resultBody.setMessages("[" + mm.get("roomName") + "]" + "?????????????????????????????????!");
                return resultBody;
            }
            if (mm.get("application_amount") == null || mm.get("application_amount") == "" || mm.get("application_amount") == "null") {
                resultBody.setData("false");
                resultBody.setMessages("[" + mm.get("roomName") + "]" + "?????????????????????????????????!");
                return resultBody;
            }else{
                all = all+Double.parseDouble(mm.get("application_amount")+"");
            }
            if(tradeList !=null ){
                if(tradeList.size()>0){
                    boolean b = false;
                    for(Map trade : tradeList){
                        if(mm.get("transaction_id").equals(trade.get("tradeNo"))){
                            if(trade.get("labourRisk") == null || Integer.parseInt(trade.get("labourRisk")+"") != 3 ){
                                resultBody.setData("false");
                                resultBody.setMessages("[" + mm.get("roomName") + "]" + "???????????????????????????!");
                                return resultBody;
                            }
                            b = false;
                            break;
                        }else{
                            b = true;
                        }
                    }
                    if(b){
                        resultBody.setData("false");
                        resultBody.setMessages("[" + mm.get("roomName") + "]" + "???????????????????????????!");
                        return resultBody;
                    }
                }else{
                    resultBody.setData("false");
                    resultBody.setMessages("[" + mm.get("roomName") + "]" + "???????????????????????????!");
                    return resultBody;
                }
            }
        }
        if(all<0){
            resultBody.setData("false");
            resultBody.setMessages("????????????????????????0!");
            return resultBody;
        }
        resultBody.setData("true");
        resultBody.setMessages("?????????????");
        return resultBody;
    }

    /**
     * ??????????????????????????????
     *
     * @param map map
     * @return Map
     */
    @Override
    public ResultBody selectInvoiceApplication(Map<String,Object> map){
        String boId = map.get("BOID")+"";
        Map<String,Object> Receipt = receiptDao.getReceiptInvoiceByJsonId(boId);
        String id = Receipt.get("receipt_id")+"";
        Receipt.putAll(receiptDao.selectReceiptById(id));
        map.put("receipt_id",id);
        Receipt.put("list",receiptDao.selectReceiptDetail(map));
        return ResultUtil.success(Receipt);
    }

    /**
     * ??????????????????????????????
     *
     * @param map map
     * @return Map
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody initInvoiceApplication(Map<String, Object> map) {
        Map<String,Object> Receipt = receiptDao.selectReceiptById(map.get("id")+"");
        if(Receipt!=null && "???????????????".equals(Receipt.get("commission_type"))){
            try{
                /* ???????????????????????? */
                String receiptId = map.get("id")+"";
                String uuId = UUID.randomUUID().toString();
                String userName =map.get("username")+"";

                /* ?????????????????? */
                map.put("receipt_id",receiptId);
                Receipt.put("list",receiptDao.selectReceiptDetail(map));

                map.putAll(homeApplyDao.getBelongDepartment(userName));
                map.put("apply_time", DateUtil.format(new Date(),"yyyy-MM-dd"));
                map.put("apply_title", "????????????");
                map.put("receipt_id", receiptId);
                map.put("flow_code", "Commission_Receipt");
                String receiptInvoiceId = receiptDao.selectReceiptApplyById(receiptId);
                if(receiptInvoiceId != null && !"".equals(receiptInvoiceId)){
                    receiptDao.updateReceiptApply(map);
                }else{
                    map.put("uuid",uuId);
                    receiptDao.initReceiptApply(map);
                }
                String jsonMap = JSONObject.toJSONString(Receipt);
                Map<String,Object> flowMap = new HashMap<>();
                flowMap.put("orgName", Receipt.get("business_unit_name"));
                flowMap.put("comcommon", jsonMap);
                flowMap.put("flow_json", jsonMap);
                flowMap.put("project_id", map.get("main_data_project_id")+"");
                flowMap.put("flow_type", "commission");
                flowMap.put("flow_code","Commission_Receipt");
                flowMap.put("TITLE", "????????????");
                flowMap.put("creator", userName);
                flowMap.put("post_name", userName);
                int i = homeApplyDao.selectFlowInfoById(receiptInvoiceId);
                if(i>0){
                    flowMap.put("json_id", receiptInvoiceId);
                    homeApplyDao.updateBrokerPolicyFlow(flowMap);
                }else{
                    flowMap.put("json_id", uuId);
                    homeApplyDao.saveBrokerPolicyFlow(flowMap);
                }
                return ResultUtil.success(flowMap);

            }catch (Exception e){
                e.printStackTrace();
                return ResultUtil.error(500, "???????????????");
            }
        }else{
            return null;
        }
    }




    /**
     * ???????????????????????????????????????
     *
     * @return Integer
     */
    public Integer getMYhtfkapply() {
        System.out.println("----------------------???????????????????????????????????????-------------------------");
        System.out.println("----------------------???????????????????????????????????????-------------------------");
        System.out.println("----------------------???????????????????????????????????????-------------------------");
        String mySql = "select a.CommissionFKGUID,\n" +
                "(select top 1 Paydate from VS_XSGL_MYhtfkapply b where a.CommissionFKGUID =b.CommissionFKGUID ORDER BY Paydate desc)as Paydate \n" +
                "from VS_XSGL_MYhtfkapply a \n" +
                "where a.Paystate = '?????????'  and a.CommissionFKGUID <> '' and a.CommissionFKCode <> '' \n" +
                "GROUP BY a.CommissionFKGUID ";
        List<Map<String, Object>> xkList = jdbcTemplatemy352.queryForList(mySql);
        Integer i = 0;
        if (xkList != null && xkList.size() != 0) {
            i = receiptDao.updateActualPaymentStatus(xkList);
        }
        System.out.println("----------------------???????????????????????????????????????-------------------------");
        System.out.println("----------------------???????????????????????????????????????-------------------------");
        System.out.println("----------------------???????????????????????????????????????-------------------------");
        return i;
    }

    /**
     * ??????????????????
     *
     * @param a ???????????????????????????
     * @param b ????????????????????????
     */
    private static String getRisk(String a, String b) {
        switch (a) {
            case NORMAL:
                if ("".equals(b)) {
                    return a;
                } else {
                    if (a.equals(b)) {
                        return a;
                    } else {
                        return b;
                    }
                }
            case UNKNOWN:
                if (!b.equals(RISK)) {
                    return a;
                } else {
                    return b;
                }
            case RISK:
                return a;
            default:
                return UNKNOWN;
        }
    }


    /**
     * ?????????????????????
     *
     * @return String
     */
    private String getFkdCode() {
        Date date = new Date();
        // 2.1 ?????? ???????????????
        String checklistCodePrefix = "FKD";
        String checklistCodeDate = DateUtil.format(date, "yyyyMMdd");
        String checklistCodePrefixAndDate = checklistCodePrefix + checklistCodeDate;//+generateWord()
        String maxChecklistCode = receiptDao.getMaxReceipCode(checklistCodeDate);
        String checklistCode;
        if (StrUtil.isBlank(maxChecklistCode)) {
            checklistCode = checklistCodePrefixAndDate + "0001";
        } else {
            String checklistCodeNumber = String.format("%04d", Integer.parseInt(maxChecklistCode.substring(maxChecklistCode.length() - 4)) + 1);
            checklistCode = checklistCodePrefixAndDate + checklistCodeNumber;
        }
        return checklistCode;
    }

    private void getNowRisk(Map<String, Object> map) {
        List<Map> listFk = receiptDao.selectOrderFk(map);
        String systemRisk = "";
        String artificialRisk = "";
        for (Map fk : listFk) {
            if (fk == null) {
                systemRisk = getRisk(UNKNOWN, systemRisk);
                artificialRisk = getRisk(UNKNOWN, artificialRisk);
            } else {
                systemRisk = getRisk(fk.get("system_risk") + "", systemRisk);
                artificialRisk = getRisk(fk.get("risk_approve_status") + "", artificialRisk);
            }
        }
        map.put("system_risk", systemRisk);
        map.put("artificial_risk", artificialRisk);
    }

    private String generateWord() {
        String[] beforeShuffle = new String[]{
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
                "U", "V", "W", "X", "Y", "Z"};
        List list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        String afterShuffle = sb.toString();
        String result = afterShuffle.substring(5, 9);
        return result;
    }
    /**
     * ???????????????????????????????????????????????????
     *
     * @param cell         cell
     * @param defaultValue defaultValue
     * @return return
     */
    private static Object getCellValueByCell(Cell cell, Object defaultValue) {
        // ???????????????null?????????
        if (cell == null || StrUtil.isBlank(cell.toString())) {
            return defaultValue;
        }

        Object cellValue;
        int cellType = cell.getCellType();

        switch (cellType) {
            case Cell.CELL_TYPE_STRING:
                // ???????????????
                cellValue = cell.getStringCellValue().trim();
                boolean boo = cellValue == null || StrUtil.isBlank(cellValue.toString());
                cellValue = boo ? defaultValue : cellValue;
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                // ????????????
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                // ????????????
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    // ??????????????????
                    cellValue = DateUtil.format(cell.getDateCellValue(), "yyyy-MM-dd");
                } else {  //???
                    cellValue = new DecimalFormat("#.##").format(cell.getNumericCellValue());
                }
                break;
            default: //???????????????????????????
                cellValue = defaultValue;
                break;
        }
        return cellValue;
    }

}
