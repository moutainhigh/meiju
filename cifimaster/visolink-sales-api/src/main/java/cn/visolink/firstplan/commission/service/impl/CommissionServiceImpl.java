package cn.visolink.firstplan.commission.service.impl;

import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.commission.dao.CommissionDao;
import cn.visolink.firstplan.commission.dao.CommissionProcessingDao;
import cn.visolink.firstplan.commission.service.CommissionService;
import cn.visolink.firstplan.plannode.service.TopSettingTwoExcelService;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * <p>
 * 待结佣 Service实现类
 * </p>
 *
 * @author baoql
 * @since 2020-05-25
 */
@Service
public class CommissionServiceImpl implements CommissionService {


    private final CommissionDao commissionDao;

    private final TopSettingTwoExcelService topSettingTwoExcelServiceImpl;

    private final CommissionProcessingDao commissionProcessingDao;

    @Resource(name = "jdbcTemplateXUKE")
    private JdbcTemplate jdbcXuke;
    @Resource(name = "jdbcTemplatemy")
    private JdbcTemplate jdbcTemplatemy;

    private static final String QMJJR = "全民经纪人";
    private static final String ZJCJ = "中介成交";
    private static final String ZQCJ = "自渠成交";

    private static final String ZERO = "0";
    private static final String ONE = "1";
    private static final String TWO = "2";
    private static final String THREE = "3";

    public CommissionServiceImpl(CommissionDao commissionDao, TopSettingTwoExcelService topSettingTwoExcelServiceImpl,CommissionProcessingDao commissionProcessingDao) {
        this.commissionDao = commissionDao;
        this.topSettingTwoExcelServiceImpl = topSettingTwoExcelServiceImpl;
        this.commissionProcessingDao = commissionProcessingDao;
    }

    /**
     * 待结佣数据初始化
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initCommission() {
        try {
            /*旭客数据录入*/
            this.initXkOrderCst();
            /*明源数据录入*/
            this.initMyTrade();
            /*待结佣数据清洗录入*/
            System.out.println("-------------------开始待结佣数据清洗录入-------------------");
            System.out.println("-------------------开始待结佣数据清洗录入-------------------");
            commissionDao.insertCommission();
            System.out.println("-------------------待结佣数据清洗录入完成-------------------");
            System.out.println("-------------------待结佣数据清洗录入完成-------------------");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(-11_1006, "数据库查询失败！");
        }
    }

    /**
     * 待结佣数据初始化
     * bql 2020.07.29
     *
     * @param modifiedTime 获取开始日期（结束时间为当前时间）
     */
    @Override
    public boolean initCommission(String modifiedTime) {
        try {
            /*旭客数据录入*/
            this.initXkOrderCst(modifiedTime);
            /*明源数据录入*/
            this.initMyTrade(modifiedTime);
            /*待结佣数据清洗录入*/
            System.out.println("-------------------开始待结佣数据清洗录入-------------------");
            System.out.println("-------------------开始待结佣数据清洗录入-------------------");
            commissionDao.insertCommission();
            System.out.println("-------------------待结佣数据清洗录入完成-------------------");
            System.out.println("-------------------待结佣数据清洗录入完成-------------------");
            return true;
        } catch (Exception e) {
            throw new BadRequestException(-11_1006, "数据库查询失败！");
        }
    }

    /**
     * 根据交易id待结佣数据初始化
     * bql 2020.09.21
     *
     * @param ids 交易id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean commissionProcessing(String ids){
        /* 修改立项后、渠道变更情况的，交易数据 */
        List<String> listStatus = commissionProcessingDao.selectMyStatus(ids);
        if(listStatus!=null && listStatus.size()>0){
            /* 将交易状态置为失效 */
            int row = commissionProcessingDao.updateMyStatus(listStatus);
        }
        /* 添加渠道变化后的新数据，交易id与原数据相同，状态置为激活 */
        if(commissionProcessingDao.selectNewSourceTypeDesc(ids)>0){
            commissionProcessingDao.initNewSourceTypeDesc(ids);
        }
        /* 更新明源为激活 and 本系统内为激活，的数据内容 */
        if(commissionProcessingDao.countUpdateCmCommission(ids)>0){
            commissionProcessingDao.updateCmCommission(ids);
        }
        /* 获取立项后退房的核算单 */
        List<String> listNewStatus = commissionProcessingDao.selectNewStatus(ids);
        if(listNewStatus!=null && listNewStatus.size()>0){
            /* 更新明源为关闭 and 本系统内为激活，的数据的状态置为关闭 */
            int row = commissionProcessingDao.updateNewStatus(listNewStatus);
        }
        /*查询立项后退房的核算单数据*/
        List<String> listIsAbnormal = commissionProcessingDao.selectIsAbnormal(ids);
        if(listIsAbnormal!=null && listIsAbnormal.size()>0){
            /* 把立项后退房的核算单状态置为异常 */
            int row = commissionProcessingDao.updateIsAbnormal(listNewStatus);
        }
        /* 同步orgId */
        commissionProcessingDao.updateOrgId(ids);
        return true;
    }

    /**
     * 待结佣数据初始化
     * bql 2020.07.29
     *
     * @param modifiedTime 获取开始日期（结束时间为当前时间）
     */
    @Override
    public boolean initCommission(String modifiedTime,String projectId) {
        try {
            String kingdee = commissionDao.selectProjectId(projectId);

            /*旭客数据录入*/
            this.initXkOrderCst(modifiedTime);
            /*明源数据录入*/
            this.initMyTrade(modifiedTime,kingdee);
            /*待结佣数据清洗录入*/
            System.out.println("-------------------开始待结佣数据清洗录入-------------------");
            System.out.println("-------------------开始待结佣数据清洗录入-------------------");
            commissionDao.insertCommissionByProject(projectId,kingdee);
            System.out.println("-------------------待结佣数据清洗录入完成-------------------");
            System.out.println("-------------------待结佣数据清洗录入完成-------------------");
            return true;
        } catch (Exception e) {
            throw new BadRequestException(-11_1006, "数据库查询失败！");
        }
    }


    /**
     * 旭客数据录入(全量)
     */
    private void initXkOrderCst() {
        int pageSize = 10000;
        int pageNum = 0;
        /*佣金管理旭客数据清除*/
        commissionDao.delXk();
        /*佣金管理客户数据获取、录入*/
        while (true) {
            String xkSql = "select * from vs_yxgk_ordercst limit " + pageNum * 10000 + "," + pageSize + " ";
            List<Map<String, Object>> xkList = jdbcXuke.queryForList(xkSql);
            if (!xkList.isEmpty() && xkList.size() == pageSize) {
                commissionDao.insertXkOrdercst(xkList);
                System.out.println("++++++++++++++++ 旭客数据录入第：" + pageNum+" 页 +++++++++++++++++");
                pageNum++;
            } else {
                commissionDao.insertXkOrdercst(xkList);
                break;
            }
        }
        System.out.println("-------------------旭客佣金数据录入成功-------------------");
        System.out.println("-------------------旭客佣金数据录入成功-------------------");
        System.out.println("-------------------旭客佣金数据录入成功-------------------");
    }

    /**
     * 旭客数据录入(增量量)
     * bql 2020.07.29
     *
     * @param modifiedTime 获取开始日期（结束时间为当前时间）
     */
    private void initXkOrderCst(String modifiedTime) {
        /*佣金管理客户数据获取、录入*/
        String xkSql = "select * from vs_yxgk_ordercst where EditTime > '" +modifiedTime+ "'";
        List<Map<String, Object>> xkList = jdbcXuke.queryForList(xkSql);
        int xk =xkList.size();
        XxlJobLogger.log("获取"+modifiedTime+"直至当前时间旭客交易数据："+xk+"条");
        if(xk>0){
            List<String> list = commissionDao.selectXKordercst(xkList);
            int a = list.size();
            if(xk>=a){
                XxlJobLogger.log("对比本库已存在数据："+a+"条");
                int b = xk- a;
                XxlJobLogger.log("需新增数据："+b+"条");

                List<Map<String, Object>> xkUpdate = new ArrayList<>(a);
                List<Map<String, Object>> xkadd = new ArrayList<>(b);
                for(Map<String, Object> map : xkList){
                    String tid = map.get("intentionid")+"";
                    long i = list.parallelStream().filter(str -> str.toLowerCase().equals(tid.toLowerCase())).count();
                    if(i>0){
                    // if(list.contains(tid.toLowerCase())){
                        xkUpdate.add(map);
                    }else{
                        xkadd.add(map);
                    }
                }
                if(a > 0){
                    List<List<Map<String, Object>>> xkUpdateList = getList(xkUpdate);
                    for (List<Map<String, Object>> lists : xkUpdateList) {
                        XxlJobLogger.log("修改 "+lists.size()+"条");
                        commissionDao.updateXkOrdercst(lists);
                        XxlJobLogger.log("修改 "+lists.size()+"条完成");
                    }
                    XxlJobLogger.log("修改完成！");
                }
                if(b > 0){
                    int init = 0;
                    List<List<Map<String, Object>>> xkaddList = getList(xkadd);
                    for (List<Map<String, Object>> lists : xkaddList) {
                        XxlJobLogger.log("添加 "+lists.size()+"条");
                        init =init+commissionDao.insertXkOrdercst(lists);
                        XxlJobLogger.log("添加 "+lists.size()+"条完成");
                    }
                    XxlJobLogger.log("新增完成："+init+"条");
                }
                System.out.println("-------------------旭客佣金数据录入成功-------------------");
                System.out.println("-------------------旭客佣金数据录入成功-------------------");
                System.out.println("-------------------旭客佣金数据录入成功-------------------");
            }else{
                XxlJobLogger.log("-------------------数据异常需要跑一遍跑一遍全量数据!!-------------------");
                XxlJobLogger.log("-------------------请先暂停增量更新，全量更新之后再次启动!!-------------------");
                System.out.println("-------------------数据异常需要跑一遍跑一遍全量数据!!-------------------");
            }
        }else{
            XxlJobLogger.log("新增完成：0条");
            System.out.println("-------------------旭客佣金数据录入成功-------------------");
        }
    }


    /**
     * 明源数据录入(全量)
     */
    private void initMyTrade() {
        int pageSize = 10000;
        int pageNum = 0;
        /*佣金管理明源数据清除*/
        commissionDao.delMy();
        /*佣金管理明源数据获取、录入*/
        while (true) {
            String mySql = " select * from vs_xsgl_trade order by OPPGUID desc offset " + pageSize + "*" + pageNum + " rows fetch next " + pageSize + " rows only ";
            List<Map<String, Object>> myList = jdbcTemplatemy.queryForList(mySql);
            if (!myList.isEmpty() && myList.size() == pageSize) {
                commissionDao.insertMyOrdercst(myList);
                System.out.println("++++++++++++++++ 明源数据录入第：" + pageNum+" 页 +++++++++++++++++");
                pageNum++;
            } else {
                commissionDao.insertMyOrdercst(myList);
                break;
            }
        }
        System.out.println("-------------------明源佣金数据录入成功-------------------");
        System.out.println("-------------------明源佣金数据录入成功-------------------");
        System.out.println("-------------------明源佣金数据录入成功-------------------");
    }

    /**
     * 明源数据录入(增量)
     * bql 2020.07.29
     *
     * @param modifiedTime 获取开始日期（结束时间为当前时间）
     */
    private void initMyTrade(String modifiedTime,String kingdee) {
        String mySql = "select  * from VS_XSGL_TRADE where KINGDEEPROJECTID = '"+kingdee+"' and MODIFIEDTIME > '"+modifiedTime+"'";
        List<Map<String, Object>> myList = jdbcTemplatemy.queryForList(mySql);
        int my =myList.size();
        XxlJobLogger.log("获取"+modifiedTime+"直至当前时间明源交易数据："+my+"条");
        if(my>0){
            List<String> list = commissionDao.selectMyOrdercst(myList);
            int a = list.size();
            XxlJobLogger.log("对比本库已存在数据："+a+"条");
            int b = my-a;
            XxlJobLogger.log("需新增数据："+b+"条");
            if(b>=0){
                List<Map<String, Object>> myUpdate = new ArrayList<>(a);
                List<Map<String, Object>> myadd = new ArrayList<>(b);
                for(Map<String, Object> map : myList){
                    String tid = map.get("TRADEGUID")+"";
                    // long i = list.parallelStream().filter(str -> str.toLowerCase().equals(tid.toLowerCase())).count();
                    //if(i>0){
                    if(list.contains(tid)){
                        myUpdate.add(map);
                    }else{
                        myadd.add(map);
                    }
                }
                if(a > 0){
                    List<List<Map<String, Object>>> myUpdateList = getList(myUpdate);
                    for (List<Map<String, Object>> lists : myUpdateList) {
                        XxlJobLogger.log("修改 "+lists.size()+"条");
                        commissionDao.updateMyOrdercst(lists);
                        XxlJobLogger.log("修改 "+lists.size()+"条完成");
                    }
                    XxlJobLogger.log("修改完成！");
                }
                if(b > 0){
                    int init = 0;
                    List<List<Map<String, Object>>> myaddList = getList(myadd);
                    for (List<Map<String, Object>> lists : myaddList) {
                        XxlJobLogger.log("添加 "+lists.size()+"条");
                        init=init+commissionDao.insertMyOrdercst(lists);
                        XxlJobLogger.log("添加 "+lists.size()+"条完成");
                    }
                    XxlJobLogger.log("新增完成："+init+"条");
                }

                System.out.println("-------------------明源佣金数据录入成功-------------------");
                System.out.println("-------------------明源佣金数据录入成功-------------------");
                System.out.println("-------------------明源佣金数据录入成功-------------------");
            }else{
                XxlJobLogger.log("-------------------数据异常需要跑一遍跑一遍全量数据!!-------------------");
                XxlJobLogger.log("-------------------请先暂停增量更新，全量更新之后再次启动!!-------------------");
                System.out.println("-------------------数据异常需要跑一遍跑一遍全量数据!!-------------------");
            }
        }else{
            XxlJobLogger.log("新增完成：0条");
            System.out.println("-------------------明源佣金数据录入成功-------------------");

        }
    }

    /**
     * 明源数据录入(增量)
     * bql 2020.07.29
     *
     * @param modifiedTime 获取开始日期（结束时间为当前时间）
     */
    private void initMyTrade(String modifiedTime) {
        String mySql = "select  * from VS_XSGL_TRADE where MODIFIEDTIME > '"+modifiedTime+"'";
        List<Map<String, Object>> myList = jdbcTemplatemy.queryForList(mySql);
        int my =myList.size();
        XxlJobLogger.log("获取"+modifiedTime+"直至当前时间明源交易数据："+my+"条");
        if(my>0){
            List<String> list = commissionDao.selectMyOrdercst(myList);
            int a = list.size();
            XxlJobLogger.log("对比本库已存在数据："+a+"条");
            int b = my-a;
            XxlJobLogger.log("需新增数据："+b+"条");
            if(b>=0){
                List<Map<String, Object>> myUpdate = new ArrayList<>(a);
                List<Map<String, Object>> myadd = new ArrayList<>(b);
                for(Map<String, Object> map : myList){
                    String tid = map.get("TRADEGUID")+"";
                    // long i = list.parallelStream().filter(str -> str.toLowerCase().equals(tid.toLowerCase())).count();
                    //if(i>0){
                    if(list.contains(tid)){
                        myUpdate.add(map);
                    }else{
                        myadd.add(map);
                    }
                }
                if(a > 0){
                    List<List<Map<String, Object>>> myUpdateList = getList(myUpdate);
                    for (List<Map<String, Object>> lists : myUpdateList) {
                        XxlJobLogger.log("修改 "+lists.size()+"条");
                        commissionDao.updateMyOrdercst(lists);
                        XxlJobLogger.log("修改 "+lists.size()+"条完成");
                    }
                    XxlJobLogger.log("修改完成！");
                }
                if(b > 0){
                    int init = 0;
                    List<List<Map<String, Object>>> myaddList = getList(myadd);
                    for (List<Map<String, Object>> lists : myaddList) {
                        XxlJobLogger.log("添加 "+lists.size()+"条");
                        init=init+commissionDao.insertMyOrdercst(lists);
                        XxlJobLogger.log("添加 "+lists.size()+"条完成");
                    }
                    XxlJobLogger.log("新增完成："+init+"条");
                }

                System.out.println("-------------------明源佣金数据录入成功-------------------");
                System.out.println("-------------------明源佣金数据录入成功-------------------");
                System.out.println("-------------------明源佣金数据录入成功-------------------");
            }else{
                XxlJobLogger.log("-------------------数据异常需要跑一遍跑一遍全量数据!!-------------------");
                XxlJobLogger.log("-------------------请先暂停增量更新，全量更新之后再次启动!!-------------------");
                System.out.println("-------------------数据异常需要跑一遍跑一遍全量数据!!-------------------");
            }
        }else{
            XxlJobLogger.log("新增完成：0条");
            System.out.println("-------------------明源佣金数据录入成功-------------------");

        }
    }



    /**
     * 明源数据局部更新；根据 交易id：transaction_id
     *
     * @param list list
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateMyTrade(List<String> list) {
        ResultBody<Object> resultBody = new ResultBody<>();
        try {
            /*佣金管理明源数据获取、录入，根据*/
            StringBuilder ids = new StringBuilder();
            StringBuilder mySql = new StringBuilder(" select * from vs_xsgl_trade where TRADEGUID in (");
            for (int i = 0; i < list.size(); i++) {
                if (i == list.size() - 1) {
                    mySql.append("'").append(list.get(i)).append("'");
                    ids.append(list.get(i));
                } else {
                    mySql.append("'").append(list.get(i)).append("',");
                    ids.append(list.get(i)).append("','");
                }
            }
            mySql.append(") order by OPPGUID desc ");
            List<Map<String, Object>> myList = jdbcTemplatemy.queryForList(mySql.toString());
            if(myList.size() != 0){
                /*删除明源局部数据*/
                commissionDao.delMyOrdercst(ids.toString());
                commissionDao.insertMyOrdercst(myList);
                //commissionDao.updateMyCommission(ids.toString());
                this.commissionProcessing(ids.toString());
            }
            resultBody.setCode(200);
            resultBody.setMessages("修改成功");
        } catch (Exception e) {
            resultBody.setCode(-1);
            resultBody.setMessages("失败");
            return resultBody;
        }
        return resultBody;
    }

    /**
     * 待结佣数据查询
     *
     * @param map 查询条件
     */
    @Override
    public ResultBody selectCommission(Map<String, Object> map) {
        map = this.setSourceTypeDesc(map);
        PageHelper.startPage(Integer.parseInt(map.get("pageIndex") + ""), Integer.parseInt(map.get("pageSize") + ""));
        return getResultBody(map, commissionDao.selectCommission(map));
    }

    /**
     * 待结佣数据导出
     *
     * @param request  request
     * @param response response
     * @param ids      id拼接in字符串
     */
    @Override
    public void exportExcelCommission(HttpServletRequest request, HttpServletResponse response, String ids,Map<String,Object> map,String type) {
        boolean b = Integer.parseInt(type) == 2;
        List<Map<String,String>> result ;
        if(map==null){
            List<String> list = Arrays.asList(ids.split(","));
            /*获取要导出的数据*/
             result = commissionDao.selectExcelCommission(list);
        }else{
            map = this.setSourceTypeDesc(map);
            result = commissionDao.selectCommission(map);
        }
        String excelName = "weekCommission.xlsx";
        int num = 16;
        if(b){
            excelName = "weekCommission2.xlsx";
            num --;
        }

        try {
            XSSFWorkbook workbook = topSettingTwoExcelServiceImpl.getWorkbook(request, excelName);
            XSSFSheet sheet = workbook.getSheetAt(0);
            CellStyle rowStyle = sheet.getRow(1).getCell(0).getCellStyle();
            for (int i = 0; i < result.size(); i++) {
                Map mm = result.get(i);
                Row row;
                if (i == 0) {
                    row = sheet.getRow(i + 1);
                } else {
                    row = sheet.createRow(i + 1);
                    row.setRowStyle(rowStyle);
                }
                for (int c = 0; c < num; c++) {
                    row.createCell(c).setCellStyle(rowStyle);
                }
                row.getCell(0).setCellValue(mm.get("business_unit_name") != null ? mm.get("business_unit_name") + "" : "");
                row.getCell(1).setCellValue(mm.get("project_name") != null ? mm.get("project_name") + "" : "");
                row.getCell(2).setCellValue(mm.get("room_name") != null ? mm.get("room_name") + "" : "");
                row.getCell(3).setCellValue(mm.get("customer_name") != null ? mm.get("customer_name") + "" : "");
                row.getCell(4).setCellValue(mm.get("gain_by") != null ? mm.get("gain_by") + "" : "");
                row.getCell(5).setCellValue(mm.get("source_type_desc") != null ? mm.get("source_type_desc") + "" : "");
                if(b){
                    row.getCell(6).setCellValue(mm.get("transaction_status") != null ? mm.get("transaction_status") + "" : "");
                    row.getCell(7).setCellValue(mm.get("subscription_date") != null ? mm.get("subscription_date") + "" : "");
                    row.getCell(8).setCellValue(mm.get("signing_date") != null ? mm.get("signing_date") + "" : "");
                    row.getCell(9).setCellValue(mm.get("now_price") != null ? mm.get("now_price") + "" : "");
                    row.getCell(10).setCellValue(mm.get("back_price") != null ? mm.get("back_price") + "" : "");
                    row.getCell(11).setCellValue(mm.get("collection_proportion") != null ? mm.get("collection_proportion") + "" : "");
                    row.getCell(12).setCellValue(mm.get("employee_name") != null ? mm.get("employee_name") + "" : "");
                    row.getCell(13).setCellValue(mm.get("customer_mobile") != null ? mm.get("customer_mobile") + "" : "");
                    row.getCell(14).setCellValue(mm.get("id") != null ? mm.get("id") + "" : "");
                }else{
                    row.getCell(6).setCellValue(mm.get("current_role") != null ? mm.get("current_role") + "" : "");
                    row.getCell(7).setCellValue(mm.get("transaction_status") != null ? mm.get("transaction_status") + "" : "");
                    row.getCell(8).setCellValue(mm.get("subscription_date") != null ? mm.get("subscription_date") + "" : "");
                    row.getCell(9).setCellValue(mm.get("signing_date") != null ? mm.get("signing_date") + "" : "");
                    row.getCell(10).setCellValue(mm.get("now_price") != null ? mm.get("now_price") + "" : "");
                    row.getCell(11).setCellValue(mm.get("back_price") != null ? mm.get("back_price") + "" : "");
                    row.getCell(12).setCellValue(mm.get("collection_proportion") != null ? mm.get("collection_proportion") + "" : "");
                    row.getCell(13).setCellValue(mm.get("employee_name") != null ? mm.get("employee_name") + "" : "");
                    row.getCell(14).setCellValue(mm.get("customer_mobile") != null ? mm.get("customer_mobile") + "" : "");
                    row.getCell(15).setCellValue(mm.get("id") != null ? mm.get("id") + "" : "");
                }
            }
            topSettingTwoExcelServiceImpl.exportExcelResponse(response, "待结佣数据导出", workbook);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(1001, "读取模版文件失败!");
        }
    }

    /**
     * 待结佣数据导出
     *
     * @param request  request
     * @param response response
     * @param map      map
     */
    @Override
    public void exportExcelCommissionNo(HttpServletRequest request, HttpServletResponse response,Map<String,Object> map) {
        /*获取要导出的数据*/
        boolean b = Integer.parseInt(map.get("source_type_desc") + "") == 2;
        map = this.setSourceTypeDesc(map);
        List<Map<String,String>> result = commissionDao.selectCommissionNo(map);
        String excelName = "CommissionNo1.xlsx";
        int num = 22;
        if(b){
            excelName = "CommissionNo2.xlsx";
            num=num -3;
        }

        try {
            XSSFWorkbook workbook = topSettingTwoExcelServiceImpl.getWorkbook(request, excelName);
            XSSFSheet sheet = workbook.getSheetAt(0);
            CellStyle rowStyle = sheet.getRow(1).getCell(0).getCellStyle();
            for (int i = 0; i < result.size(); i++) {
                Map mm = result.get(i);
                Row row;
                if (i == 0) {
                    row = sheet.getRow(i + 1);
                } else {
                    row = sheet.createRow(i + 1);
                    row.setRowStyle(rowStyle);
                }
                for (int c = 0; c < num; c++) {
                    row.createCell(c).setCellStyle(rowStyle);
                }
                row.getCell(0).setCellValue(mm.get("business_unit_name") != null ? mm.get("business_unit_name") + "" : "");
                row.getCell(1).setCellValue(mm.get("project_name") != null ? mm.get("project_name") + "" : "");
                row.getCell(2).setCellValue(mm.get("room_name") != null ? mm.get("room_name") + "" : "");
                row.getCell(3).setCellValue(mm.get("customer_name") != null ? mm.get("customer_name") + "" : "");
                row.getCell(4).setCellValue(mm.get("source_type_desc") != null ? mm.get("source_type_desc") + "" : "");
                row.getCell(5).setCellValue(mm.get("gain_by") != null ? mm.get("gain_by") + "" : "");
                if(b){
                    row.getCell(6).setCellValue(mm.get("transaction_status") != null ? mm.get("transaction_status") + "" : "");
                    row.getCell(7).setCellValue(mm.get("subscription_date") != null ? mm.get("subscription_date") + "" : "");
                    row.getCell(8).setCellValue(mm.get("signing_date") != null ? mm.get("signing_date") + "" : "");
                    row.getCell(9).setCellValue(mm.get("now_price") != null ? mm.get("now_price") + "" : "");
                    row.getCell(10).setCellValue(mm.get("back_price") != null ? mm.get("back_price") + "" : "");
                    row.getCell(11).setCellValue(mm.get("collection_proportion") != null ? mm.get("collection_proportion") + "" : "");
                    row.getCell(12).setCellValue(mm.get("employee_name") != null ? mm.get("employee_name") + "" : "");
                    row.getCell(13).setCellValue(mm.get("customer_mobile") != null ? mm.get("customer_mobile") + "" : "");
                    row.getCell(14).setCellValue(mm.get("EmployeeName") != null ? mm.get("EmployeeName") + "" : "");
                    row.getCell(15).setCellValue(mm.get("create_time") != null ? mm.get("create_time") + "" : "");
                    row.getCell(16).setCellValue(mm.get("commission_type") != null ? mm.get("commission_type") + "" : "");
                    row.getCell(17).setCellValue(mm.get("no_remarks") != null ? mm.get("no_remarks") + "" : "");
                    row.getCell(18).setCellValue(mm.get("id") != null ? mm.get("id") + "" : "");
                }else{
                    row.getCell(6).setCellValue(mm.get("current_role") != null ? mm.get("current_role") + "" : "");
                    row.getCell(7).setCellValue(mm.get("transaction_status") != null ? mm.get("transaction_status") + "" : "");
                    row.getCell(8).setCellValue(mm.get("subscription_date") != null ? mm.get("subscription_date") + "" : "");
                    row.getCell(9).setCellValue(mm.get("signing_date") != null ? mm.get("signing_date") + "" : "");
                    row.getCell(10).setCellValue(mm.get("now_price") != null ? mm.get("now_price") + "" : "");
                    row.getCell(11).setCellValue(mm.get("back_price") != null ? mm.get("back_price") + "" : "");
                    row.getCell(12).setCellValue(mm.get("collection_proportion") != null ? mm.get("collection_proportion") + "" : "");
                    row.getCell(13).setCellValue(mm.get("employee_name") != null ? mm.get("employee_name") + "" : "");
                    row.getCell(14).setCellValue(mm.get("customer_mobile") != null ? mm.get("customer_mobile") + "" : "");
                    row.getCell(15).setCellValue(mm.get("EmployeeName") != null ? mm.get("EmployeeName") + "" : "");
                    row.getCell(16).setCellValue(mm.get("create_time") != null ? mm.get("create_time") + "" : "");
                    row.getCell(17).setCellValue(mm.get("commission_type") != null ? mm.get("commission_type") + "" : "");
                    row.getCell(18).setCellValue(mm.get("no_remarks") != null ? mm.get("no_remarks") + "" : "");
                    row.getCell(19).setCellValue(mm.get("grant_status") != null ? mm.get("grant_status") + "" : "");
                    row.getCell(20).setCellValue(mm.get("grant_time") != null ? mm.get("grant_time") + "" : "");
                    row.getCell(21).setCellValue(mm.get("id") != null ? mm.get("id") + "" : "");
                }
            }
            topSettingTwoExcelServiceImpl.exportExcelResponse(response, "不结佣数据导出", workbook);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(1001, "读取模版文件失败!");
        }
    }


    /**
     * 不结佣修改备注
     *
     * @param map map
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateCommissionNo(Map map) {
        ResultBody resultBody = new ResultBody();
        map.put("username", SecurityUtils.getUsername());
        try {
            /*不结佣修改备注*/
            if (map.get("remarks") != null && map.get("remarks") != "") {
                commissionDao.updateCommissionNo(map);
            }
            resultBody.setCode(200);
            resultBody.setMessages("修改成功");
        } catch (Exception e) {
            resultBody.setCode(-1);
            resultBody.setMessages("失败");
            return resultBody;
        }
        return resultBody;
    }

    /**
     * 不结佣发放金额修改
     *
     * @param map map
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateCommissionNoPayment(Map map) {
        ResultBody resultBody = new ResultBody();
        map.put("username", SecurityUtils.getUsername());
        try {
            /*不结佣修改备注*/
            if (map.get("payment_amount") != null && map.get("payment_amount") != "") {
                commissionDao.updateNoPayment(map);
            }
            resultBody.setCode(200);
            resultBody.setMessages("修改成功");
        } catch (Exception e) {
            resultBody.setCode(-1);
            resultBody.setMessages("失败");
            return resultBody;
        }
        return resultBody;
    }

    /**
     * 待结佣数据导入
     *
     * @param file   文件
     * @param months months
     * @return ResultBody
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody commissionImport(MultipartFile file, String months) {
        ResultBody resultBody = new ResultBody();
        try {
            File f = File.createTempFile("tmp", null);
            file.transferTo(f);
            f.deleteOnExit();
            FileInputStream fileInputStream;
            fileInputStream = new FileInputStream(f);
            XSSFWorkbook workBook = new XSSFWorkbook(fileInputStream);
            Sheet planSheet = workBook.getSheetAt(0);
            //导入数据总行数
            int planSheetTotalRows = planSheet.getLastRowNum();
            int startRow = 2;
            Row row;
            /*项目级别单独存储*/
            for (int i = startRow; i <= planSheetTotalRows; i++) {
                row = planSheet.getRow(i);
                Map<String, Object> map = new HashMap<>(6);
                map.put("commission_money", row.getCell(16).getNumericCellValue());
                map.put("commission_percentage", row.getCell(17).getNumericCellValue());
                map.put("id", row.getCell(21).getNumericCellValue());
                map.put("username", SecurityUtils.getUsername());
                commissionDao.updateCommission(map);
            }
            resultBody.setCode(200);
            resultBody.setMessages("修改成功");
        } catch (IllegalStateException e) {
            throw new BadRequestException(-15_1001, "Excel表格内容输入格式不正确，请修改后重新导入");
        } catch (Exception e) {
            throw new BadRequestException(-15_1002, "导入失败，请联系管理员");
        }
        return resultBody;
    }


    /**
     * 待结佣修改已录佣金、佣金点位
     *
     * @param map map
     * @return ResultBody
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody initCommissionNo(Map map) {
        ResultBody resultBody = new ResultBody();
        map.put("username", SecurityUtils.getUsername());
        String isCommission = map.get("is_commission") + "";
        //noinspection unchecked
        List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("list");
        /*不结佣数据组装*/
        String remarks = map.get("remarks") + "";
        String commissionType = map.get("commission_type") + "";
        for (Map<String, Object> m : list) {
            m.put("remarks", remarks);
            m.put("is_commission", isCommission);
            m.put("commission_type", commissionType);
        }
        try {
            if (ZERO.equals(isCommission)) {
                /*修改是否不结佣状态*/
                commissionDao.updateCommissionIsNo(list, map.get("uaerId")+"");
                /*不结佣数据添加*/
                commissionDao.initCommissionNo(list, map);
            } else if (ONE.equals(isCommission)) {
                /*修改是否不结佣状态*/
                commissionDao.updateCommissionIsYse(list, map.get("uaerId")+"");
                /*不结佣数据删除*/
                commissionDao.delCommissionNo(list);
            }
            resultBody.setCode(200);
            resultBody.setMessages("修改成功");
        } catch (Exception e) {
            resultBody.setCode(-1);
            resultBody.setMessages("失败");
            return resultBody;
        }
        return resultBody;
    }


    /**
     * 不结佣数据查询
     *
     * @param map map
     * @return ResultBody
     */
    @Override
    public ResultBody selectCommissionNo(Map<String, Object> map) {
        map = this.setSourceTypeDesc(map);
        PageHelper.startPage(Integer.parseInt(map.get("pageIndex") + ""), Integer.parseInt(map.get("pageSize") + ""));
        return getResultBody(map, commissionDao.selectCommissionNo(map));
    }

    @Override
    public ResultBody getResultBody(Map<String, Object> map, List<Map<String,String>> maps) {
        PageInfo<Map<String,String>> pageInfo = new PageInfo<>(maps);
        ResultBody resultBody = new ResultBody<>();
        resultBody.setData(pageInfo);
        resultBody.setCode(200);
        return resultBody;
    }

    /**
     * 判断当前成交类型
     *
     * @param map map
     * @return map
     */
    @Override
    public Map<String, Object> setSourceTypeDesc(Map<String, Object> map) {
        String sourceTypeDesc = map.get("source_type_desc") + "";
        if (!StringUtil.isEmpty(sourceTypeDesc)) {
            switch (sourceTypeDesc) {
                case ONE:
                    map.put("source_type_desc", QMJJR);
                    break;
                case TWO:
                    map.put("source_type_desc", ZJCJ);
                    break;
                case THREE:
                    map.put("source_type_desc", ZQCJ);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + sourceTypeDesc);
            }
        }
        return map;
    }

    /**
     * 不结佣发放
     *
     * @param map map
     * @return ResultBody
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateGrant(Map map) {
        ResultBody resultBody = new ResultBody();
        try {
            List list = (List) map.get("list");
            /*不结佣发放*/
            commissionDao.updateGrant(list, SecurityUtils.getUsername());
            resultBody.setCode(200);
            resultBody.setMessages("修改成功");
        } catch (Exception e) {
            resultBody.setCode(-1);
            resultBody.setMessages("失败");
            return resultBody;
        }
        return resultBody;
    }

    /**
     * 查询经纪人身份
     *
     * @return ResultBody
     */
    @Override
    public ResultBody getCurrentRole() {
        List<Map> result = commissionDao.getCurrentRole();
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setData(result);
        resultBody.setCode(200);
        return resultBody;
    }

    /**
     * 查询业绩归属
     *
     * @param map  map
     * @return ResultBody
     */
    @Override
    public ResultBody getGainBy(Map map) {
        List<Map> result = commissionDao.getGainBy(map);
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setData(result);
        resultBody.setCode(200);
        return resultBody;
    }


    //批量方法
    private List<List<Map<String, Object>>> getList(List reqMap) {
        //list 为全量集合
        int batchCount = 5000; //每批插入数目
        int batchLastIndex = batchCount;
        List<List<Map<String, Object>>> shareList = new ArrayList<>();
        if (reqMap != null) {
            for (int index = 0; index < reqMap.size(); ) {
                if (batchLastIndex >= reqMap.size()) {
                    batchLastIndex = reqMap.size();
                    shareList.add(reqMap.subList(index, batchLastIndex));
                    break;
                } else {
                    shareList.add(reqMap.subList(index, batchLastIndex));
                    index = batchLastIndex;// 设置下一批下标
                    batchLastIndex = index + (batchCount - 1);
                }
            }
        }
        return shareList;
    }
}
