package cn.visolink.salesmanage.riskcontrolmanager.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.visolink.common.security.utils.HttpRequestUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.datainterface.service.Datainterfaceservice;
import cn.visolink.salesmanage.riskcontrolmanager.dao.RiskContolDao;
import cn.visolink.salesmanage.riskcontrolmanager.model.InsideBe;
import cn.visolink.salesmanage.riskcontrolmanager.model.OutsideBe;
import cn.visolink.salesmanage.riskcontrolmanager.service.RiskControlService;
import cn.visolink.system.timelogs.bean.SysLog;
import cn.visolink.system.timelogs.dao.TimeLogsDao;
import cn.visolink.utils.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.wicket.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RiskControlServiceImpl implements RiskControlService {
    @Autowired
    RiskContolDao riskContolDao;

    @Autowired
    private TimeLogsDao timeLogsDao;
    @Autowired
    Datainterfaceservice datainterfaceservice;

    @Resource(name = "jdbcTemplatemy")
    private JdbcTemplate jdbcTemplatemy;

    @Value("${qdfkesb.url}")
    private String mingyuanrisk;

    @Value("${qdfkesb.userId}")
    private String mingyuanriskUserId;

    @Value("${qdfkesb.password}")
    private String mingyuanriskPassword;

    @Value("${createTimerisk.url}")
    private String createTimerisk;

    @Value("${createTimerisk.userId}")
    private String createTimeriskUserId;

    @Value("${createTimerisk.password}")
    private String createTimeriskPassword;

    @Value("${uploadPath}")
    private String uplodepath;

    @Value("${relepath}")
    private String relepath;

    /*????????????????????????????????????*/
    @Override
    public void setBuyer(String queryStartDate, String queryEndDate) {

        try {
            SysLog sysLog = new SysLog();
            sysLog.setTaskName("?????????????????????");
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sysLog.setStartTime(format1.format(new Date()));
            sysLog.setNote("?????????????????????");
            timeLogsDao.insertLogs(sysLog);
            /*???????????????????????????????????????????????????????????????*/
            Map map = new HashMap();
            map.put("queryStartDate", queryStartDate);
            map.put("queryEndDate", queryEndDate);
            riskContolDao.updateBuyerBefore(map);
            int size = 0;
            for(int i=0;i<3;i++){

                String recordSql1 = "select b.* from VS_XSGL_ORDER a inner join \n" +
                        "s_buyer b on a.OrderGUID=b.SaleGUID\n" +
                        "where (a.QSDate>'" + DateUtil.offsetDay(DateUtil.parse(queryStartDate),10*i) + "' " +
                        "and a.QSDate<='" + DateUtil.offsetDay(DateUtil.parse(queryEndDate),-10*(2-i)) + "')";
                List<Map<String, Object>> flowLists1 = jdbcTemplatemy.queryForList(recordSql1);
                size = size + flowLists1.size();
                /*???????????????*/
                if (flowLists1 != null && flowLists1.size() > 0) {
                    List<List<Map<String, Object>>> inserlist1 = getList(flowLists1);
                    for (List<Map<String, Object>> lists : inserlist1) {
                        riskContolDao.insertBuyer(lists);
                    }
                }else{
                    throw new NullPointerException();
                }
            }

            sysLog.setTaskName("?????????????????????");
            sysLog.setStartTime(format1.format(new Date()));
            sysLog.setNote("?????????????????????,????????????" + size + ",???????????????" + queryStartDate + ",???????????????" + queryEndDate+"??????");
            timeLogsDao.insertLogs(sysLog);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    @Override
    public Boolean getData(Map querymap) {
        SysLog sysLog = new SysLog();
        sysLog.setTaskName("??????????????????????????????");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sysLog.setStartTime(format1.format(new Date()));
        sysLog.setNote("??????????????????");
        timeLogsDao.insertLogs(sysLog);

        System.out.println("\n\t\t==========??????????????????==========\n" +
                "\n\t\t==========??????????????????==========\n" +
                "\n\t\t==========??????????????????==========\n" +
                "\n\t\t==========??????????????????==========\n" +
                "\n\t\t==========??????????????????==========\n");


        String queryStartDate = querymap.get("queryStartDate") + "";
        String queryEndDate = querymap.get("queryEndDate") + "";
        String queryStartDateRisk = querymap.get("queryStartDate") + "";
        System.out.println(querymap.toString() + "querymap");
        /*?????????ORDER???,?????????onlyset?????????????????????*/
        if (querymap.get("onlyset") == null) {
            //???????????????????????????
            datainterfaceservice.intiOrderAddData(null);
        }

        /*if(1==1){
            return ;
        }
        */

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        /*??????????????????NULL???????????????????????????????????????????????????*/
        if (queryStartDate.indexOf("-") < 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            /*???????????????????????????????????????????????????????????????*/
            /*???????????????DATE*/
            calendar.add(Calendar.DATE, -30);
            Date date1 = calendar.getTime();
            queryStartDate = simpleDateFormat.format(date1);
            /*???????????????DATE*/
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(date);
            calendar2.add(Calendar.DATE, -30);
            Date dateRisk = calendar2.getTime();
            queryStartDateRisk = simpleDateFormat.format(dateRisk);
            /*?????????3???25?????????*/
            queryStartDateRisk = "2019-03-25";
        }


        if (queryEndDate.indexOf("-") < 0) {
            queryEndDate = simpleDateFormat.format(date);
        }
        /*??????????????????*/
        if (querymap.get("onlyset") == null) {
            setBuyer(queryStartDate, queryEndDate);
        }
        /*?????????????????????????????????1????????????????????????????????????????????????*/
        /*????????????*/


        Map createTimeAndMap = new HashMap();
        Map esbInfoTime = new HashMap<>();
        esbInfoTime.put("instId", "47f1a9db3f434426baf8993b5df07e86");
        esbInfoTime.put("requestTime", "1577182054138");
        Map requestInfoTime = new HashMap<>();
        requestInfoTime.put("thirdPrjId", "");
        createTimeAndMap.put("esbInfo", esbInfoTime);
        createTimeAndMap.put("requestInfo", requestInfoTime);

        //   Object createTimeriskresult =HttpRequestUtil.httpPost2(createTimerisk,JSONObject.parseObject(JSONObject.toJSONString(createTimeAndMap)),false);
        JSONObject createTimeriskresult = HttpRequestUtil.httpPost(createTimerisk, createTimeriskUserId, createTimeriskPassword, JSONObject.parseObject(JSONObject.toJSONString(createTimeAndMap)), false);  //????????????

        System.out.println("============"+createTimeriskresult.toJSONString()+"============");
        sysLog.setTaskName("?????????????????????????????????");
        sysLog.setStartTime(format1.format(new Date()));
        sysLog.setNote(createTimeriskresult.toJSONString());
        timeLogsDao.insertLogs(sysLog);
        System.err.println("????????????:"+createTimeriskresult);
        Gson gson = new Gson();
        Map<String, Object> GsonMap = new HashMap();
        GsonMap = gson.fromJson((createTimeriskresult + ""), GsonMap.getClass());
        System.out.println(GsonMap + "GsonMap");


        /*??????????????????????????????????????????*/

        Map createTimeriskMap = (Map) GsonMap.get("resultInfo");


        List<Map> createTimeMap = (List<Map>) createTimeriskMap.get("data");
        /*?????????*/
        String unitString = null;
        if (querymap.get("projectId") == null || querymap.get("projectId") == "") {
            List<String> unitList = new ArrayList<>();
            if (createTimeMap != null && createTimeMap.size() > 0) {
                for (Map RiskMap : createTimeMap) {
                    unitList.add(RiskMap.get("thirdPrjId") + "");
                }
            }


            unitString = unitList.toString();
            unitString = unitString.substring(1, unitString.length() - 1);
            unitString = unitString.replace(" ", "");
            System.out.println(unitString);

        } else {
            unitString = querymap.get("projectId") + "";
        }
        /*?????????????????????????????????????????????????????????*/
        String startTime = null;
        String endTime = null;
        if ((querymap.get("query") + "").equals("??????")) {
            startTime = "queryStartDate";
            endTime = "queryEndDate";
        } else {
            startTime = "startSyncTime";
            endTime = "endSyncTime";
        }


        Map<String, Object> jsonMap = new HashMap<>();
        Map esbInfoId = new HashMap();
        esbInfoId.put("instId", "6a9bd5cfa5c645d5a721d9c64fd55e70");
        esbInfoId.put("requestTime", "1577186401098");
        Map queryInfo = new HashMap();
        queryInfo.put("currentPage", 1);
        queryInfo.put("pageSize", 1);
        Map requestInfo = new HashMap();

        Map dataInfo = new HashMap();
        dataInfo.put("startSyncTime", querymap.get("startSyncTime"));
        dataInfo.put("endSyncTime",  querymap.get("endSyncTime"));
        dataInfo.put("thirdPrjIds", unitString);
        dataInfo.put(startTime, queryStartDateRisk);
        dataInfo.put(endTime, queryEndDate);
        requestInfo.put("data", dataInfo);

        jsonMap.put("esbInfo", esbInfoId);
        jsonMap.put("queryInfo", queryInfo);
        jsonMap.put("requestInfo", requestInfo);
        System.out.println(jsonMap + "queryInfoABC");

        /*????????????*/


        //  Object result =HttpRequestUtil.httpPost2(mingyuanrisk,JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
        JSONObject result = HttpRequestUtil.httpPost(mingyuanrisk, mingyuanriskUserId, mingyuanriskPassword, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)), false);  //????????????

        Map resultmap = (Map) result;
        /*?????????????????????*/
        if (resultmap == null) {
            return true;
        }
        Map resultdata = (Map) resultmap.get("queryInfo");

        /*?????????????????????????????????*/
        Double totalRecords = Double.parseDouble(resultdata.get("totalRecord") + "");

        System.out.println();
        /*????????????LIST???????????????????????????*/
        List<Map> realResultMessage = new ArrayList<>();
        Double queryPageSize = Math.ceil(totalRecords / 3000);
        /*??????????????????*/
        for (int i = 1; i <= queryPageSize; i++) {


            queryInfo.put("pageSize", 3000);
            queryInfo.put("currentPage", i);

            jsonMap.put("queryInfo", queryInfo);
            System.out.println(JSONObject.parseObject(JSONObject.toJSONString(jsonMap)) + "JSONObject");
            //   Object realResult =HttpRequestUtil.httpPost2(mingyuanrisk,JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
            JSONObject realResult = HttpRequestUtil.httpPost(mingyuanrisk, mingyuanriskUserId, mingyuanriskPassword, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)), false);  //????????????
            System.out.println("\n\t\t==========??????????????????,????????????" + i + "?????????" + queryPageSize + "????????????3000???==========\n" +
                    "\n\t\t==========??????????????????,????????????" + i + "?????????" + queryPageSize + "????????????3000???==========\n" +
                    "\n\t\t==========??????????????????,????????????" + i + "?????????" + queryPageSize + "????????????3000???==========\n" +
                    "\n\t\t==========??????????????????,????????????" + i + "?????????" + queryPageSize + "????????????3000???==========\n" +
                    "\n\t\t==========??????????????????,????????????" + i + "?????????" + queryPageSize + "????????????3000???==========\n");
            Map resultInfomap = (Map) realResult;
            sysLog.setTaskName("??????????????????");
            sysLog.setStartTime(format1.format(new Date()));
            sysLog.setNote("???"+i+"???=="+JSON.toJSONString(resultInfomap));
            timeLogsDao.insertLogs(sysLog);
            if (resultInfomap != null) {
                System.out.println(resultInfomap + "resultInfomap");
                Map realResultmap = (Map) resultInfomap.get("resultInfo");
                if (realResultmap != null) {
                    List<Map> realResultdata = (List<Map>) realResultmap.get("data");

                    /*???????????????*/
                    realResultMessage.addAll(realResultdata);
                }
            }
        }


        /*?????????????????????????????????LIST?????????*/
        List<Map> newResultMessage = new ArrayList<>();
        if (realResultMessage != null && realResultMessage.size() > 0) {

            for (Map MessageMap : realResultMessage) {

                for (Map RiskMap : createTimeMap) {
                    if ((RiskMap.get("thirdPrjId") + "").equals((MessageMap.get("thirdPrjId") + ""))) {
                        MessageMap.put("openingTime", RiskMap.get("createTime"));
                    }
                }
                newResultMessage.add(MessageMap);
            }
        }
        System.out.println(newResultMessage.toString() + "newResultMessage");
        /*??????????????????*/
        if (newResultMessage != null && newResultMessage.size() > 0) {

            sysLog.setTaskName("??????????????????????????????");
            sysLog.setStartTime(format1.format(new Date()));
            sysLog.setNote("???????????????" + newResultMessage.size() + "????????????" + queryStartDate + "????????????" + queryEndDate + "????????????");
            timeLogsDao.insertLogs(sysLog);


            List<List<Map<String, Object>>> listtemp = getList(newResultMessage);

            for (int k = 0; k < listtemp.size(); k++) {
                List<Map<String, Object>> lists = listtemp.get(k);
                System.out.println("\n\t\t==========????????????????????????????????????,????????????" + k + 1 + "?????????" + listtemp.size() + "???????????????" + lists.size() + "???==========\n" +
                        "\n\t\t==========????????????????????????????????????,????????????" + k + 1 + "?????????" + listtemp.size() + "???????????????" + lists.size() + "???==========\n" +
                        "\n\t\t==========????????????????????????????????????,????????????" + k + 1 + "?????????" + listtemp.size() + "???????????????" + lists.size() + "???==========\n" +
                        "\n\t\t==========????????????????????????????????????,????????????" + k + 1 + "?????????" + listtemp.size() + "???????????????" + lists.size() + "???==========\n" +
                        "\n\t\t==========????????????????????????????????????,????????????" + k + 1 + "?????????" + listtemp.size() + "???????????????" + lists.size() + "???==========\n");
                /*????????????????????????????????????????????????ID?????????,?????????????????????????????????LIST???????????????*/
                List<Map<String, Object>> removelist = new ArrayList<>();
                for (int i = 0; i < lists.size(); i++) {
                    Map map1 = riskContolDao.selectInfoDetail(lists.get(i));
                    if (map1 != null && map1.size() > 0) {
                        riskContolDao.deleteInfoByClient(lists.get(i));
                    } else {
                        removelist.add(lists.get(i));
                    }

                }
                System.out.println(removelist.toString() + "lists.sizereal()");
                if (removelist.size() > 0) {
                    riskContolDao.updateRiskControl(removelist);
                }

            }

        }
        /*??????ORder?????????*/
        /*?????????????????????????????????INFO???*/
        if (querymap.get("onlygetData") == null) {
            System.out.println("\n\t\t==========???????????????==========\n" +
                    "\n\t\t==========???????????????==========\n" +
                    "\n\t\t==========???????????????==========\n" +
                    "\n\t\t==========???????????????==========\n" +
                    "\n\t\t==========???????????????==========\n");
            riskContolDao.deleteOrderFkUpBefore();
            riskContolDao.insertOrderFkUp();
            /*??????????????????????????????*/
            /*?????????????????????*/
            riskContolDao.deleteRiskSurfaceBefore();
            riskContolDao.selectRiskSurface();
        }
        sysLog.setTaskName("????????????????????????");
        sysLog.setStartTime(format1.format(new Date()));
        sysLog.setNote("??????????????????????????????");
        timeLogsDao.insertLogs(sysLog);
        System.out.println("\n\t\t==========??????????????????????????????==========\n" +
                "\n\t\t==========??????????????????????????????==========\n" +
                "\n\t\t==========??????????????????????????????==========\n" +
                "\n\t\t==========??????????????????????????????==========\n" +
                "\n\t\t==========??????????????????????????????==========\n");
        return  true;
    }

    /*?????????????????????*/
    @Override
    public ResultBody<Object> selectRiskInfor(Map map) {
        PageHelper pageHelper = new PageHelper();

        PageHelper.startPage(Integer.parseInt(map.get("pageNum").toString()), Integer.parseInt(map.get("pageSize").toString()));
        ResultBody<Object> resultBody = new ResultBody<>();
        List<Map> result = riskContolDao.selectOutsideBe(map);

        PageInfo pageInfo = new PageInfo(result);
        map.remove("pageNum");
        map.remove("pageSize");
        Map total = AllNoGroup(map);

        pageInfo.getList().add(0, total);


        resultBody.setData(pageInfo);
        return resultBody;


    }

    /*????????????*/
    public Map AllNoGroup(Map map) {


        Map result = riskContolDao.selectOutsideGroupAll(map);

        return result;

    }


    /*??????????????????*/
    @Override
    public ResultBody<Object> selectRiskInside(Map map) {


        ResultBody<Object> resultBody = new ResultBody<>();

        if ((map.get("choice") + "").equals("normal_check_normal") || (map.get("choice") + "").equals("normal_check_fly_alone")
                || (map.get("choice") + "").equals("normal_nocheck") || (map.get("choice") + "").equals("risk_check_normal") || (map.get("choice") + "").equals("risk_check_fly_alone")
                || (map.get("choice") + "").equals("risk_nocheck") || (map.get("choice") + "").equals("nocard") || (map.get("choice") + "").equals("brush_card_nosnap")
                || (map.get("choice") + "").equals("brush_card_total")
                || (map.get("choice") + "").equals("normal_normal_total") || (map.get("choice") + "").equals("risk_risk_total")
                || (map.get("choice") + "").equals("unknown_total") || (map.get("choice") + "").equals("no_report_time")
                || (map.get("choice") + "").equals("no_frist_snap")
        ) {
            map.put("Buyer", 1);
        }

        PageHelper.startPage(Integer.parseInt(map.get("pageNum") + ""), Integer.parseInt(map.get("pageSize") + ""));
        List<Map> result = riskContolDao.selectRiskInside(map);
        PageInfo pageInfo = new PageInfo(result);


        /*?????????????????????????????????????????????????????????????????????????????????????????????,??????????????????Map,???????????????????????????*/

        System.out.println(result.toString() + "resultreal.toString()");

        resultBody.setData(pageInfo);
        return resultBody;

    }

    @Override
    public void riskDataExport(HttpServletRequest request, HttpServletResponse response, Map map, String inside) {

        Map<String, Object> result = new HashMap<>(16);
        String planName = "??????????????????";
        String basePath;
        String templatePath;
        String targetFileDir;
        String targetfilePath;
        FileInputStream templateInputStream = null;
        FileOutputStream fileOutputStream = null;
        Workbook targetWorkBook;
        XSSFSheet targetSheet;


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new Date());
        planName += time;

        try {
//            basePath = "E:/xuhui/marketing-control-api/cifimaster/visolink-sales-api/src";

            basePath = request.getServletContext().getRealPath("/");
            templatePath = null;
            if (inside == null) {
                templatePath = File.separator + "TemplateExcel" + File.separator + "riskControl.xlsx";
            } else {
                templatePath = File.separator + "TemplateExcel" + File.separator + "riskControlInside.xlsx";
                //????????????
                //basePath="";
                //templatePath="/Users/WorkSapce/Java/????????????/Java/marketing-control-api/cifimaster/visolink-sales-api/src/main/webapp/TemplateExcel/riskControlInside.xlsx";
            }
            //??????????????????????????????
            targetFileDir = "Uploads" + File.separator + "DownLoadTemporaryFiles";
            // ?????????????????????
            targetfilePath = targetFileDir + File.separator + planName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            planName = URLEncoder.encode(planName + ".xlsx", "utf-8").replace("+", "%20");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //??????content-disposition??????????????????????????????????????????????????????
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(planName.getBytes(), "utf-8"));
            //??????????????????????????????

            //??????????????????????????????
            //    String  realpath= this.getClass().getResource("/").getPath()  ;

            //  realpath=realpath.substring(0,realpath.indexOf("/target"))+File.separator+"src"+File.separator+"main"+File.separator+"webapp"+templatePath;

            //  File templateFile = new File(realpath);


            templatePath = basePath + templatePath;

            File templateFile = new File(templatePath);
            if (!templateFile.exists()) {
                templateFile.mkdirs();
                throw new ServiceException("-1", "????????????????????????????????????????????????");
            }
            //?????????????????????????????????
            File targetFileDirFile = new File(targetFileDir);
            if (!targetFileDirFile.exists()) {
                targetFileDirFile.mkdirs();
            }
            //?????????????????????
            templateInputStream = new FileInputStream(templateFile);
            targetWorkBook = new XSSFWorkbook(templateInputStream);
            targetSheet = (XSSFSheet) targetWorkBook.getSheetAt(0);
            targetWorkBook.setSheetName(0, "??????????????????");
            //????????????????????????
            int maxTemplateRows = targetSheet.getLastRowNum();


            //?????????
            int startRows = 1;
            Row row0 = targetSheet.getRow(0);
            CellStyle stylegreen = null;

            CellStyle stylegray = null;

            CellStyle styleblue = null;
            CellStyle stylesky = null;
            /*?????????????????????3?????????????????????STYLE*/
            if (inside == null) {
                startRows = 3;
                row0 = targetSheet.getRow(2);
                stylegreen = row0.getCell(22).getCellStyle();

                stylegray = row0.getCell(26).getCellStyle();

                styleblue = row0.getCell(18).getCellStyle();
                stylesky = row0.getCell(17).getCellStyle();
                stylegreen.setWrapText(true);
                stylegreen.setLocked(true);

                stylegray.setWrapText(true);
                stylegray.setLocked(true);

                styleblue.setWrapText(true);
                styleblue.setLocked(true);

                stylesky.setWrapText(true);
                stylesky.setLocked(true);
            }

            int maxCellNum = row0.getPhysicalNumberOfCells();
            CellStyle style = row0.getCell(0).getCellStyle();
            style.setLocked(true);
            style.setWrapText(true);


            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setAlignment(HorizontalAlignment.CENTER);
            if (inside == null) {
                targetSheet.createFreezePane(2, 3, 2, 3);
            } else {
                targetSheet.createFreezePane(2, 1, 2, 1);

            }
            /*?????????LIST?????????????????????????????????????????????0?????????????????????????????????*/
            List groupRowlist = null;
            Map groupRowMap = new HashMap();
            if (inside == null) {

                groupRowlist = riskContolDao.selectOutsideBeByExport(map);


            } else {
                List<InsideBe> list = new ArrayList<>();
                ArrayList<Object[]> dataset = new ArrayList<>();//????????????????????????
                list = riskContolDao.selectRiskInsideByExport(map);
                //????????????????????????????????????
                for (InsideBe model : list) {
                    Object[] objects = model.toExportExcelData();
                    dataset.add(objects);
                }
                //???????????????????????????
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcelTemplate(templatePath, dataset, planName, response);
                return;
            }

            for (Object weekMap : groupRowlist) {

                XSSFRow positionRow = targetSheet.createRow(startRows);
                positionRow.setHeightInPoints(20);

                int startIndex = 0;
                int endIndex = 0;

                startRows++;


                if (inside == null) {
                    OutsideBe realweekMap = (OutsideBe) weekMap;
                    setValueToExcelCell(targetWorkBook, positionRow, realweekMap, stylegreen, stylegray, styleblue, stylesky, startRows);
                } else {
                    InsideBe realweekMap = (InsideBe) weekMap;
                    setValueToExcelCellInside(targetWorkBook, positionRow, realweekMap, stylegreen, stylegray, styleblue, stylesky, startRows);
                }
            }

            /*???????????????????????????*/
            if (inside == null) {
                /*???????????????MAP*/
                Map SumAllMap = AllNoGroup(map);

                CellStyle cs = targetWorkBook.createCellStyle();
                cs.setBorderBottom(BorderStyle.THIN);
                cs.setBorderTop(BorderStyle.THIN);
                cs.setBorderLeft(BorderStyle.THIN);
                cs.setBorderRight(BorderStyle.THIN);
                cs.setAlignment(HorizontalAlignment.CENTER);
                cs.setWrapText(true);
                Integer lastNum = targetSheet.getLastRowNum();

                int toChar = 97;
                System.out.println((char) (121) + "toChar");

                XSSFRow lastRow = targetSheet.createRow(startRows);
                XSSFCell cell1 = lastRow.createCell(0);
                XSSFCell ce1 = lastRow.createCell(1);
                cell1.setCellStyle(style);
                ce1.setCellStyle(style);

                cell1.setCellValue("??????");
                CellRangeAddress region = new CellRangeAddress(startRows, startRows, 0, 1);
                targetSheet.addMergedRegion(region);
                for (int n = 2; n < maxCellNum; n++) {

                    String tem = "";
                    /*??????Z??????????????????EXCEL??????*/
                    char c = 1;

                    if (n > 25) {
                        c = (char) (toChar + n - 26);
                        tem += "A" + c + "" + (startRows) + "+";
                    }
                    if (n <= 25) {
                        c = (char) (toChar + n);
                        tem += c + "" + (startRows) + "+";
                    }


                    XSSFCell cell2 = lastRow.createCell(n);
                    cell2.setCellStyle(cs);
                    /*???????????????5??????*/
                    if (n < 5) {
                        continue;
                    }

                    if (tem != "") {
                        if (n > 25) {
                            cell2.setCellFormula("SUM(" + tem.substring(0, 2) + "4:" + tem.substring(0, tem.length() - 1) + ")");

                        } else {
                            cell2.setCellFormula("SUM(" + tem.substring(0, 1) + "4:" + tem.substring(0, tem.length() - 1) + ")");

                        }
                    }
                    if (n == 7) {
                        cell2.setCellFormula("IFERROR(ROUND(G" + (startRows + 1) + "/" + "F" + (startRows + 1) + "*10000/100,2),0)&\"%\"");
                    }
                    /*??????????????????MAP?????????*/
                    if (n == 8) {
                        cell2.setCellFormula(SumAllMap.get("agency_volume_per") + "&\"%\"");
                    }
                    if (n == 9) {
                        cell2.setCellFormula(SumAllMap.get("agochannel_volume_per") + "&\"%\"");
                    }
                    if (n == 10) {
                        cell2.setCellFormula(SumAllMap.get("own_volume_per") + "&\"%\"");
                    }
                    if (n == 11) {
                        cell2.setCellFormula("IFERROR(ROUND(S" + (startRows + 1) + "/" + "F" + (startRows + 1) + "*10000/100,2),0)&\"%\"");
                    }
                    if (n == 12) {
                        cell2.setCellFormula("IFERROR(ROUND(U" + (startRows + 1) + "/" + "S" + (startRows + 1) + "*10000/100,2),0)&\"%\"");
                    }
                    if (n == 13) {
                        cell2.setCellFormula("IFERROR(ROUND((V" + (startRows + 1) + "+" + "Z" + (startRows + 1) + ")/" + "(S" + (startRows + 1) + "+W" + (startRows + 1) + ")*10000/100,2),0)&\"%\"");
                    }
                    if (n == 14) {
                        cell2.setCellFormula("IFERROR(ROUND(AA" + (startRows + 1) + "/" + "R" + (startRows + 1) + "*10000/100,2),0)&\"%\"");
                    }
                    if (n == 15) {
                        cell2.setCellFormula("IFERROR(ROUND(AE" + (startRows + 1) + "/" + "F" + (startRows + 1) + "*10000/100,2),0)&\"%\"");
                    }
                    if (n == 16) {
                        cell2.setCellFormula("IFERROR(ROUND(AF" + (startRows + 1) + "/" + "F" + (startRows + 1) + "*10000/100,2),0)&\"%\"");
                    }
                }

            }


            targetSheet.setColumnHidden(maxCellNum, true);
            targetSheet.setRowSumsBelow(false);

            targetSheet.setForceFormulaRecalculation(true);

            if (inside != null) {
                Integer namelist = 0;
                targetSheet.setColumnHidden(maxCellNum, true);
                int planSheetTotalRows = targetSheet.getLastRowNum();
                for (int i = 1; i <= planSheetTotalRows; i++) {
                    Row row = targetSheet.getRow(i);
                    Cell cell = row.getCell(maxCellNum);
                    String cellValue1 = FileUtils.getCellValue(cell, null);
                    Cell cell3 = row.getCell(10);
                    String cellValue3 = FileUtils.getCellValue(cell3, null);
                    for (int j = 1; j <= planSheetTotalRows; j++) {
                        Row rowj = targetSheet.getRow(j);
                        Cell cellj = rowj.getCell(maxCellNum);
                        String cellValue2 = FileUtils.getCellValue(cellj, null);
                        Cell cell4 = rowj.getCell(10);
                        String cellValue4 = FileUtils.getCellValue(cell4, null);
                        if (cellValue1 != "" && cellValue2 != "") {
                            if (cellValue1.equals(cellValue2) && (!cellValue3.equals(cellValue4))) {
                                System.out.println(cellValue1 + "cellValue1" + cellValue2 + "j" + j + "i" + i);
                                System.out.println(cellValue3 + "cellValue2" + cellValue4 + "j" + j + "i" + i);
                                namelist++;
                                cellj.setCellValue("");
                            }
                        }

                    }
                    if (namelist > 0) {
                        for (int k = 0; k < 9; k++) {
                            CellRangeAddress region = new CellRangeAddress(i, i + namelist, k, k);
                            targetSheet.addMergedRegion(region);
                        }
                        namelist = 0;
                    }

                    String cellValue2 = FileUtils.getCellValue(cell, null);
                }
            }

            //????????????

            fileOutputStream = new FileOutputStream(targetfilePath);
            targetWorkBook.write(response.getOutputStream());


            //?????????????????????

            File saveFile = new File(uplodepath + File.separator + "weekAreaFile");

            if (!saveFile.exists()) {
                saveFile.mkdirs();

                targetfilePath = uplodepath + File.separator + "weekAreaFile" + File.separator + planName + ".xlsx";
                fileOutputStream = new FileOutputStream(targetfilePath);
                targetWorkBook.write(fileOutputStream);
            }

        } catch (ServiceException se) {
            se.printStackTrace();
            System.out.println(se.getResponseMsg());
            result.put("key", se.getResponseMsg());
        } catch (UnsupportedEncodingException e) {
            System.out.print("????????????????????????");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (templateInputStream != null) {
                    templateInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setValueToExcelCell(Workbook targetWorkBook, XSSFRow positionRow, OutsideBe weekMap, CellStyle stylegreen, CellStyle stylegray, CellStyle styleblue, CellStyle stylesky, int startRows) {
        CellStyle cs = targetWorkBook.createCellStyle();
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setWrapText(true);

        CellStyle csone = targetWorkBook.createCellStyle();

        XSSFDataFormat format = (XSSFDataFormat) targetWorkBook.createDataFormat();
        csone.setBorderBottom(BorderStyle.THIN);
        csone.setBorderTop(BorderStyle.THIN);
        csone.setBorderLeft(BorderStyle.THIN);
        csone.setBorderRight(BorderStyle.THIN);
        csone.setAlignment(HorizontalAlignment.CENTER);


        XSSFCell cell0 = positionRow.createCell(0);
        XSSFCell cell1 = positionRow.createCell(1);
        XSSFCell cell2 = positionRow.createCell(2);
        XSSFCell cell3 = positionRow.createCell(3);
        XSSFCell cell4 = positionRow.createCell(4);
        XSSFCell cell5 = positionRow.createCell(5);
        XSSFCell cell6 = positionRow.createCell(6);
        XSSFCell cell7 = positionRow.createCell(7);
        XSSFCell cell8 = positionRow.createCell(8);
        XSSFCell cell9 = positionRow.createCell(9);
        XSSFCell cell10 = positionRow.createCell(10);
        XSSFCell cell11 = positionRow.createCell(11);
        XSSFCell cell12 = positionRow.createCell(12);
        XSSFCell cell13 = positionRow.createCell(13);
        XSSFCell cell14 = positionRow.createCell(14);
        XSSFCell cell15 = positionRow.createCell(15);
        XSSFCell cell16 = positionRow.createCell(16);
        XSSFCell cell17 = positionRow.createCell(17);
        XSSFCell cell18 = positionRow.createCell(18);
        XSSFCell cell19 = positionRow.createCell(19);
        XSSFCell cell20 = positionRow.createCell(20);
        XSSFCell cell21 = positionRow.createCell(21);
        XSSFCell cell22 = positionRow.createCell(22);
        XSSFCell cell23 = positionRow.createCell(23);
        XSSFCell cell24 = positionRow.createCell(24);
        XSSFCell cell25 = positionRow.createCell(25);
        XSSFCell cell26 = positionRow.createCell(26);
        XSSFCell cell27 = positionRow.createCell(27);
        XSSFCell cell28 = positionRow.createCell(28);
        XSSFCell cell29 = positionRow.createCell(29);
        XSSFCell cell30 = positionRow.createCell(30);
        XSSFCell cell31 = positionRow.createCell(31);
        XSSFCell cell32 = positionRow.createCell(32);
        XSSFCell cell33 = positionRow.createCell(33);
        XSSFCell cell34 = positionRow.createCell(34);
        XSSFCell cell35 = positionRow.createCell(35);
        XSSFCell cell36 = positionRow.createCell(36);

        cell0.setCellStyle(csone);
        if (weekMap.getArea_name() == null) {
            cell0.setCellValue("");
        } else {
            cell0.setCellValue(weekMap.getArea_name());
        }


        cell1.setCellStyle(csone);
        if (weekMap.getProject_name() == null) {
            cell1.setCellValue("");
        } else {
            cell1.setCellValue(weekMap.getProject_name());
        }

        cell2.setCellStyle(csone);
        if (weekMap.getTrader() == null) {
            cell2.setCellValue("");
        } else {
            cell2.setCellValue(weekMap.getTrader());
        }

        cell3.setCellStyle(csone);
        if (weekMap.getProject_code() == null) {
            cell3.setCellValue("");
        } else {
            cell3.setCellValue(weekMap.getProject_code());
        }

        cell4.setCellStyle(csone);
        if (weekMap.getOpening_time() == null) {
            cell4.setCellValue("");
        } else {
            cell4.setCellValue(weekMap.getOpening_time());
        }

        cell5.setCellStyle(csone);
        if (weekMap.getDeal_total() == null) {
            cell5.setCellValue("");
        } else {
            cell5.setCellValue(weekMap.getDeal_total());
        }


        cell6.setCellStyle(csone);
        if (weekMap.getChannel_volume_total() == null) {
            cell6.setCellValue("");
        } else {
            cell6.setCellValue(weekMap.getChannel_volume_total());
        }
        cell7.setCellStyle(csone);
        if (weekMap.getChannel_volume_per() == null) {
            cell7.setCellValue("");
        } else {
            cell7.setCellFormula(weekMap.getChannel_volume_per() + "&\"%\"");
        }
        cell8.setCellStyle(csone);
        if (weekMap.getAgency_volume_per() == null) {
            cell8.setCellValue("");
        } else {
            cell8.setCellFormula(weekMap.getAgency_volume_per() + "&\"%\"");
        }
        cell9.setCellStyle(cs);
        if (weekMap.getAgochannel_volume_per() == null) {
            cell9.setCellValue("");
        } else {
            cell9.setCellFormula(weekMap.getAgochannel_volume_per() + "&\"%\"");
        }


        cell10.setCellStyle(csone);
        if (weekMap.getOwn_volume_per() == null) {
            cell10.setCellValue("");
        } else {
            cell10.setCellFormula(weekMap.getOwn_volume_per() + "&\"%\"");
        }

        cell11.setCellStyle(csone);
        if (weekMap.getRisk_rate() == null) {
            cell11.setCellValue("");
        } else {
            cell11.setCellFormula(weekMap.getRisk_rate() + "&\"%\"");
        }


        cell12.setCellStyle(csone);
        if (weekMap.getRisk_check_rate() == null) {
            cell12.setCellValue("");
        } else {
            cell12.setCellFormula(weekMap.getRisk_check_rate() + "&\"%\"");
        }

        cell13.setCellStyle(cs);
        if (weekMap.getRisk_nocheck_rate() == null) {
            cell13.setCellValue("");
        } else {
            cell13.setCellFormula(weekMap.getRisk_nocheck_rate() + "&\"%\"");
        }

        cell14.setCellStyle(csone);
        if (weekMap.getUnknown_brush_rate() == null) {
            cell14.setCellValue("");
        } else {
            cell14.setCellFormula(weekMap.getUnknown_brush_rate() + "&\"%\"");
        }

        cell15.setCellStyle(cs);
        if (weekMap.getChannel_nocard_rate() == null) {
            cell15.setCellValue("");
        } else {
            cell15.setCellFormula(weekMap.getChannel_nocard_rate() + "&\"%\"");
        }


        cell16.setCellStyle(csone);
        if (weekMap.getDeal_nocard_rate() == null) {
            cell16.setCellValue("");
        } else {
            cell16.setCellFormula(weekMap.getDeal_nocard_rate() + "&\"%\"");
        }

        cell17.setCellStyle(stylesky);
        if (weekMap.getBrush_card_total() == null) {
            cell17.setCellValue("");
        } else {
            cell17.setCellValue(weekMap.getBrush_card_total());
        }

        cell18.setCellStyle(styleblue);
        if (weekMap.getRisk_risk_total() == null) {
            cell18.setCellValue("");
        } else {
            cell18.setCellValue(weekMap.getRisk_risk_total());
        }

        cell19.setCellStyle(csone);
        if (weekMap.getRisk_check_fly_alone() == null) {
            cell19.setCellValue("");
        } else {
            cell19.setCellValue(weekMap.getRisk_check_fly_alone());
        }

        cell20.setCellStyle(csone);
        if (weekMap.getRisk_check_normal() == null) {
            cell20.setCellValue("");
        } else {
            cell20.setCellValue(weekMap.getRisk_check_normal());
        }

        cell21.setCellStyle(csone);
        cell22.setCellStyle(stylegreen);
        cell23.setCellStyle(csone);
        if (weekMap.getRisk_nocheck() == null) {
            cell21.setCellValue("");
        } else {
            cell21.setCellValue(weekMap.getRisk_nocheck());
        }
        if (weekMap.getNormal_normal_total() == null) {
            cell22.setCellValue("");
        } else {
            cell22.setCellValue(weekMap.getNormal_normal_total());
        }
        if (weekMap.getNormal_check_fly_alone() == null) {
            cell23.setCellValue("");
        } else {
            cell23.setCellValue(weekMap.getNormal_check_fly_alone());
        }
        cell24.setCellStyle(cs);
        if (weekMap.getNormal_check_normal() == null) {
            cell24.setCellValue("");
        } else {
            cell24.setCellValue(weekMap.getNormal_check_normal());
        }

        cell25.setCellStyle(csone);
        if (weekMap.getNormal_nocheck() == null) {
            cell25.setCellValue("");
        } else {
            cell25.setCellValue(weekMap.getNormal_nocheck());
        }
        cell26.setCellStyle(stylegray);
        if (weekMap.getUnknown_total() == null) {
            cell26.setCellValue("");
        } else {
            cell26.setCellValue(weekMap.getUnknown_total());
        }

        if (weekMap.getNo_report_time() == null) {
            cell27.setCellValue("");
        } else {
            cell27.setCellValue(weekMap.getNo_report_time());
        }
        cell27.setCellStyle(csone);

        /*cell28.setCellStyle(cs);
        if (weekMap.getNo_frist_snap() == null) {
            cell28.setCellValue("");
        } else {
            cell28.setCellValue(weekMap.getNo_frist_snap());
        }*/
        cell28.setCellStyle(cs);
        if (weekMap.getNo_frist_snap() == null) {
            cell28.setCellValue("");
        } else {
            cell28.setCellValue(weekMap.getNo_frist_danger());
        }
        cell29.setCellStyle(cs);
        if (weekMap.getNo_frist_snap() == null) {
            cell29.setCellValue("");
        } else {
            cell29.setCellValue(weekMap.getNo_frist_nature());
        }
        cell30.setCellStyle(cs);
        if (weekMap.getNo_frist_snap() == null) {
            cell30.setCellValue("");
        } else {
            cell30.setCellValue(weekMap.getNo_frist_unkown());
        }

        /*cell29.setCellStyle(styleblue);
        if (weekMap.getBrush_card_nosnap() == null) {
            cell29.setCellValue("");
        } else {
            cell29.setCellValue(weekMap.getBrush_card_nosnap());
        }*/

        if (weekMap.getChannel_nocard_total() == null) {
            cell31.setCellValue("");
        } else {
            cell31.setCellValue(weekMap.getChannel_nocard_total());
        }
        cell31.setCellStyle(csone);

        if (weekMap.getDeal_nocard_total() == null) {
            cell32.setCellValue("");
        } else {
            cell32.setCellValue(weekMap.getDeal_nocard_total());
        }
        cell32.setCellStyle(csone);
    }


    private void setValueToExcelCellInside(Workbook targetWorkBook, XSSFRow positionRow, InsideBe weekMap, CellStyle stylegreen, CellStyle stylegray, CellStyle styleblue, CellStyle stylesky, int startRows) {
        CellStyle cs = targetWorkBook.createCellStyle();
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setWrapText(true);

        CellStyle csone = targetWorkBook.createCellStyle();

        XSSFDataFormat format = (XSSFDataFormat) targetWorkBook.createDataFormat();
        csone.setBorderBottom(BorderStyle.THIN);
        csone.setBorderTop(BorderStyle.THIN);
        csone.setBorderLeft(BorderStyle.THIN);
        csone.setBorderRight(BorderStyle.THIN);
        csone.setAlignment(HorizontalAlignment.CENTER);


        XSSFCell cell0 = positionRow.createCell(0);
        XSSFCell cell1 = positionRow.createCell(1);
        XSSFCell cell2 = positionRow.createCell(2);
        XSSFCell cell3 = positionRow.createCell(3);
        XSSFCell cell4 = positionRow.createCell(4);
        XSSFCell cell5 = positionRow.createCell(5);
        XSSFCell cell6 = positionRow.createCell(6);
        XSSFCell cell7 = positionRow.createCell(7);
        XSSFCell cell8 = positionRow.createCell(8);
        XSSFCell cell9 = positionRow.createCell(9);
        XSSFCell cell10 = positionRow.createCell(10);
        XSSFCell cell11 = positionRow.createCell(11);
        XSSFCell cell12 = positionRow.createCell(12);
        XSSFCell cell13 = positionRow.createCell(13);
        XSSFCell cell14 = positionRow.createCell(14);
        XSSFCell cell15 = positionRow.createCell(15);
        XSSFCell cell16 = positionRow.createCell(16);
        XSSFCell cell17 = positionRow.createCell(17);
        XSSFCell cell18 = positionRow.createCell(18);
        XSSFCell cell19 = positionRow.createCell(19);
        XSSFCell cell20 = positionRow.createCell(20);
        XSSFCell cell21 = positionRow.createCell(21);
        XSSFCell cell22 = positionRow.createCell(22);
        XSSFCell cell23 = positionRow.createCell(23);
        XSSFCell cell24 = positionRow.createCell(24);
        XSSFCell cell25 = positionRow.createCell(25);

        cell0.setCellStyle(csone);
        if (weekMap.getBusinessUnit() == null) {
            cell0.setCellValue("");
        } else {
            cell0.setCellValue(weekMap.getBusinessUnit());
        }


        cell1.setCellStyle(csone);
        if (weekMap.getProject_name() == null) {
            cell1.setCellValue("");
        } else {
            cell1.setCellValue(weekMap.getProject_name());
        }

        cell2.setCellStyle(csone);
        if (weekMap.getProjectCode() == null) {
            cell2.setCellValue("");
        } else {
            cell2.setCellValue(weekMap.getProjectCode());
        }

        cell3.setCellStyle(csone);
        if (weekMap.getRoominfo() == null) {
            cell3.setCellValue("");
        } else {
            cell3.setCellValue(weekMap.getRoominfo());
        }

        cell4.setCellStyle(csone);
        if (weekMap.getYwgsDate() == null) {
            cell4.setCellValue("");
        } else {
            cell4.setCellValue(weekMap.getYwgsDate());
        }

        cell5.setCellStyle(csone);
        if (weekMap.getCloseReason() == null) {
            cell5.setCellValue("");
        } else {
            cell5.setCellValue(weekMap.getCloseReason());
        }


        cell6.setCellStyle(csone);
        if (weekMap.getCounselor_name() == null) {
            cell6.setCellValue("");
        } else {
            cell6.setCellValue(weekMap.getCounselor_name());
        }
        cell7.setCellStyle(csone);
        if (weekMap.getChannel() == null) {
            cell7.setCellValue("");
        } else {
            cell7.setCellValue(weekMap.getChannel());
        }
        cell8.setCellStyle(csone);
        if (weekMap.getAgent() == null) {
            cell8.setCellValue("");
        } else {
            cell8.setCellValue(weekMap.getAgent());
        }
        cell9.setCellStyle(cs);
        if (weekMap.getClient_name() == null) {
            cell9.setCellValue("");
        } else {
            cell9.setCellValue(weekMap.getClient_name());
        }


        cell10.setCellStyle(csone);
        if (weekMap.getId_number() == null) {
            cell10.setCellValue("");
        } else {
            cell10.setCellValue(weekMap.getId_number());
        }

        cell11.setCellStyle(csone);
        if (weekMap.getFreshcard_time() == null) {
            cell11.setCellValue("");
        } else {
            cell11.setCellValue(weekMap.getFreshcard_time());
        }


        cell12.setCellStyle(csone);
        if (weekMap.getReport_time() == null) {
            cell12.setCellValue("");
        } else {
            cell12.setCellValue(weekMap.getReport_time());
        }

        cell13.setCellStyle(cs);
        if (weekMap.getFirstphoto_time() == null) {
            cell13.setCellValue("");
        } else {
            cell13.setCellValue(weekMap.getFirstphoto_time());
        }

        cell14.setCellStyle(csone);
        if (weekMap.getImport_time() == null) {
            cell14.setCellValue("");
        } else {
            cell14.setCellValue(weekMap.getImport_time());
        }

        cell15.setCellStyle(cs);
        if (weekMap.getRisk_time() == null) {
            cell15.setCellValue("");
        } else {
            cell15.setCellValue(weekMap.getRisk_time());
        }


        cell16.setCellStyle(csone);
        if (weekMap.getRisk_reason() == null) {
            cell16.setCellValue("");
        } else {
            cell16.setCellValue(weekMap.getRisk_reason());
        }

        cell17.setCellStyle(cs);
        if (weekMap.getSystem_risk() == null) {
            cell17.setCellValue("");
        } else {
            cell17.setCellValue(weekMap.getSystem_risk());
        }

        cell18.setCellStyle(cs);
        if (weekMap.getRisk_approve_status() == null) {
            cell18.setCellValue("");
        } else {
            cell18.setCellValue(weekMap.getRisk_approve_status());
        }

        cell19.setCellStyle(csone);
        if (weekMap.getRisk_approve_time() == null) {
            cell19.setCellValue("");
        } else {
            cell19.setCellValue(weekMap.getRisk_approve_time());
        }

        cell20.setCellStyle(csone);
        if (weekMap.getRiskSpan() == null) {
            cell20.setCellValue("");
        } else {
            cell20.setCellValue(weekMap.getRiskSpan());
        }

        cell21.setCellStyle(csone);
        cell22.setCellStyle(cs);
        cell23.setCellStyle(csone);
        if (weekMap.getRisk_approve_remark() == null) {
            cell21.setCellValue("");
        } else {
            cell21.setCellValue(weekMap.getRisk_approve_remark());
        }
        if (weekMap.getReject_time() == null) {
            cell22.setCellValue("");
        } else {
            cell22.setCellValue(weekMap.getReject_time());
        }
        if (weekMap.getReject_content() == null) {
            cell23.setCellValue("");
        } else {
            cell23.setCellValue(weekMap.getReject_content());
        }
        cell24.setCellStyle(cs);
        if (weekMap.getRemark() == null) {
            cell24.setCellValue("");
        } else {
            cell24.setCellValue(weekMap.getRemark());
        }
        cell25.setCellStyle(cs);
        if (weekMap.getOrderGuid() == null) {
            cell25.setCellValue("");
        } else {
            cell25.setCellValue(weekMap.getOrderGuid());
        }

    }


    @Override
    public List<Map> selectBusinessName() {
        return riskContolDao.selectBusinessName();
    }


    //????????????
    private List<List<Map<String, Object>>> getList(List reqMap) {
        //list ???????????????
        int batchCount = 3000; //??????????????????
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
                    index = batchLastIndex;// ?????????????????????
                    batchLastIndex = index + (batchCount - 1);
                }
            }
        }
        return shareList;
    }

}
