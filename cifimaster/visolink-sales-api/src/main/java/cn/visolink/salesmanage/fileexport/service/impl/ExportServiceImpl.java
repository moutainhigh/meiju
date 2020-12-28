package cn.visolink.salesmanage.fileexport.service.impl;

import cn.visolink.common.security.utils.HttpRequestUtil;
import cn.visolink.salesmanage.fileexport.model.ListThree;
import cn.visolink.salesmanage.fileexport.model.MonthFour;
import cn.visolink.salesmanage.fileexport.model.MonthPlan;

import cn.visolink.salesmanage.groupmanagement.service.GroupManageService;
import cn.visolink.salesmanage.monthdetail.service.MonthManagerService;
import cn.visolink.utils.Constant;
import cn.visolink.salesmanage.fileexport.dao.ExportDao;
import cn.visolink.salesmanage.fileexport.service.ExportService;
import com.google.gson.Gson;
import io.lettuce.core.ScriptOutputType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.wicket.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;

/**
 * @Auther: Gr
 * @Date: 2019/9/24 10:45
 * @Description: Excel导出功能
 */
@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    ExportDao exportDao;


    @Autowired
    MonthManagerService monthManagerService;

    @Autowired
    GroupManageService groupManageService;


    @Value("${prcDimProjGoal.httpIp}")
    private String prcDimProjGoal;

    @Value("${prcDimProjGoal.apikey}")
    private String prcDimProjapikey;
    /**
     * EXCEL 序号自增
     */
    private int rowNum = 0;

    @Override
    public List<MonthPlan> selectAll() {

        return exportDao.selectAll();
    }


    @Override
    public void monthlyPlanExport(HttpServletRequest request, HttpServletResponse response, String month, int preparedByUnitType, String businessId, List preparedByLevel) {
        String planName;
        String basePath;
        String templatePath;
        String targetFileDir;
        String targetFilePath;
        FileInputStream templateInputStream = null;
        FileOutputStream fileOutputStream = null;
        Workbook targetWorkBook;
        XSSFSheet targetSheet;

        /*preparedByUnitType=0是集团编制导出*/
        int isWrite = 0;
        if (preparedByUnitType == 0) {
            isWrite = 1;
            preparedByUnitType = 1;
        }

        switch (preparedByUnitType) {
            case Constant.PREPARED_BY_UNIT_TYPE_GROUP:
                planName = Constant.GROUP_EXCEL_NAME;
                break;
            case Constant.PREPARED_BY_UNIT_TYPE_REGION:
                planName = Constant.REGION_EXCEL_NAME;
                break;
            case Constant.PREPARED_BY_UNIT_TYPE_PROJECT:
                planName = Constant.PROJECT_EXCEL_NAME;
                break;

            default:
                planName = "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new Date());
        planName += time;

        try {
            //basePath = "D:/资料/公司/旭辉/营销管控/newCode/marketing-control-api/cifimaster/visolink-sales-api/src";
            basePath = request.getServletContext().getRealPath("/");

           String a="";
            //模板文件路径。
            if (preparedByUnitType == Constant.PREPARED_BY_UNIT_TYPE_REGION) {
                templatePath = File.separator + "TemplateExcel" + File.separator + "region_issued_indicator_data.xlsx";
              // a = "region_issued_indicator_data.xlsx";
            } else if (preparedByUnitType == Constant.PREPARED_BY_UNIT_TYPE_GROUP) {
                templatePath = File.separator + "TemplateExcel" + File.separator + "group_issued_indicator_data.xlsx";
               // a = "group_issued_indicator_data.xlsx";
            } else {
                templatePath = File.separator + "TemplateExcel" + File.separator + "project_issued_indicator_data.xlsx";
                //a = "project_issued_indicator_data.xlsx";
            }
            //导出临时文件文件夹。
            targetFileDir = "Uploads" + File.separator + "DownLoadTemporaryFiles";
            // 目标文件路径。
            targetFilePath = targetFileDir + File.separator + planName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            planName = URLEncoder.encode(planName + ".xlsx", "utf-8").replace("+", "%20");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //设置content-disposition响应头控制浏览器以下载的形式打开文件
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(planName.getBytes(), "utf-8"));

            //验证模板文件是否存在
            //  String  realpath= this.getClass().getResource("/").getPath()  ;


            //  realpath=realpath.substring(0,realpath.indexOf("/target"))+File.separator+"src"+File.separator+"main"+File.separator+"webapp"+templatePath;

            //  File templateFile = new File(realpath);
            templatePath = basePath + templatePath;
            //File templateFile = new File("D:\\资料\\公司\\旭辉\\营销管控\\newCode\\marketing-control-api\\cifimaster\\visolink-sales-api\\src\\main\\webapp\\TemplateExcel\\"+a);
            //basePath="/Users/WorkSapce/Java/旭辉集团/Java/marketing-control-api/cifimaster/visolink-sales-api/src/main/webapp/TemplateExcel/"+a;

            File templateFile = new File(templatePath);

            if (!templateFile.exists()) {
                templateFile.mkdirs();
                throw new ServiceException("-15_1003", "认购确认导出失败。模板文件不存在");
            }
            //验证目标文件夹是否存在
            File targetFileDirFile = new File(targetFileDir);
            if (!targetFileDirFile.exists()) {
                targetFileDirFile.mkdirs();
            }
            //创建输出文档。
            templateInputStream = new FileInputStream(templateFile);
            targetWorkBook = new XSSFWorkbook(templateInputStream);
            targetSheet = (XSSFSheet) targetWorkBook.getSheetAt(0);
            targetWorkBook.setSheetName(0, "集团下达月度营销指标");
            //模板文件中最大行
            int maxTemplateRows = targetSheet.getLastRowNum();
            //清空原模板剩余数据
            for (int i = 3; i <= maxTemplateRows; i++) {
                Row removeRow = targetSheet.getRow(i);

                targetSheet.removeRow(removeRow);
            }
            List<MonthPlan> mapList = new ArrayList<>();
            Map temMap = new HashMap(8);
            temMap.put("month", month);
            temMap.put("businessId", businessId);

            temMap.put("prepared_by_unit_type", preparedByUnitType);

            MonthPlan monthPlanMap = exportDao.selectMonthPlanByGuid(temMap);
            monthPlanMap.setRow(Constant.START_ROW);
            mapList.add(monthPlanMap);
            selectAllMonthPlanByFatherId(mapList, Constant.START_ROW, monthPlanMap, preparedByLevel);

            //起始行
            int startRows = Constant.START_ROW;
            //1a 2b 3c 4d 5e  数字与字符之间的转换
            int toChar = 97;
            Row row0 = targetSheet.getRow(0);
            int maxCellNum = row0.getPhysicalNumberOfCells();
            /*若是集团编制页面都不限制单元格输入*/
            if (isWrite == 0) {
                targetSheet.protectSheet("edit");
            }


            /**
             * 单元格样色共三种：
             * 1.白底 居中加粗 style1
             * 2.灰底 居中 加粗
             * 3.灰底 正常
             * 4.动态判断
             */
            XSSFDataFormat format = (XSSFDataFormat) targetWorkBook.createDataFormat();
            CellStyle style1 = targetWorkBook.createCellStyle();
            CellStyle style2 = targetWorkBook.createCellStyle();
            CellStyle style3 = targetWorkBook.createCellStyle();
            CellStyle style4 = targetWorkBook.createCellStyle();
            CellStyle style5 = targetWorkBook.createCellStyle();
            //   style1.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            style2.cloneStyleFrom(style1);
            style2.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style1.setLocked(false);
            style1.setBorderBottom(BorderStyle.THIN);
            style1.setBorderTop(BorderStyle.THIN);
            style1.setBorderLeft(BorderStyle.THIN);
            style1.setBorderRight(BorderStyle.THIN);

            style2.setBorderBottom(BorderStyle.THIN);
            style2.setBorderTop(BorderStyle.THIN);
            style2.setBorderLeft(BorderStyle.THIN);
            style2.setBorderRight(BorderStyle.THIN);


            style2.setDataFormat(format.getFormat("0.00"));
            style3.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style3.setBorderBottom(BorderStyle.THIN);
            style3.setBorderTop(BorderStyle.THIN);
            style3.setBorderLeft(BorderStyle.THIN);
            style3.setBorderRight(BorderStyle.THIN);

            style5.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style5.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style5.setBorderBottom(BorderStyle.THIN);
            style5.setBorderTop(BorderStyle.THIN);
            style5.setBorderLeft(BorderStyle.THIN);
            style5.setBorderRight(BorderStyle.THIN);


            style3.setDataFormat(format.getFormat("0.00"));
            List<MonthPlan> groupRowlist = new ArrayList<MonthPlan>();


            String months = month;
            /*截取到年月*/
            months = months.substring(0, months.lastIndexOf("-"));
            /*换成符合要求的字符串*/
            months = months.replace("-", "");
            String createTimeriskresult = HttpRequestUtil.httpGet(prcDimProjGoal + "?params=" + months + "&apikey=" + prcDimProjapikey, false);
            System.out.println(prcDimProjGoal + "?params=" + months + "&apikey=" + prcDimProjapikey + "-----prcDimProjapikey");
            Gson gson = new Gson();
            Map<String, Object> GsonMap = new HashMap();
            GsonMap = gson.fromJson((createTimeriskresult + ""), GsonMap.getClass());

            /*得到所有来自数据湖的数据，然后遍历到返回值里*/
            List<Map> AllProject = (List<Map>) GsonMap.get("retData");
            /*if (AllProject != null && AllProject.size() > 0) {
                DecimalFormat df = new DecimalFormat("#.00");
                for (int i = 0; i < mapList.size(); i++) {
                    String businessId1 = mapList.get(i).getBusiness_id();
                    *//* if (businessId1 == null) {
                        System.out.println("空了");
                    } *//*
                    System.out.println(businessId1 + "idmProjIdh");


                    for (Map map2 : AllProject) {

                        String businessId2 = map2.get("idmProjId") + "";
                        if (businessId1.equals(businessId2)
                                && mapList.get(i).getTotal_sign_funds() < 1 && mapList.get(i).getTotal_sign_funds() > -1

                        ) {
                            mapList.get(i).setTotal_sign_funds(Double.parseDouble(map2.get("cntrtAmtGoalM") + "") / 10000);
                        }
                        if (businessId1.equals(businessId2)) {
                            mapList.get(i).setYear_check_funds(map2.get("cntrtAmtBudgetY") == null ? 0 : Double.parseDouble(df.format(Double.parseDouble(map2.get("cntrtAmtBudgetY") + "") / 10000)));
                            mapList.get(i).setMonths_check_funds(map2.get("cntrtAmtBudgetMAccu") == null ? 0 : Double.parseDouble(df.format(Double.parseDouble(map2.get("cntrtAmtBudgetMAccu") + "") / 10000)));
                            mapList.get(i).setYear_check_funds_per(Double.parseDouble(map2.get("cntrtAmtBudgetY") + "") == 0 ? null : Double.parseDouble(df.format(mapList.get(i).getYear_grand_total_sign() / (Double.parseDouble(map2.get("cntrtAmtBudgetY") + "") / 10000))) * 100);
                            mapList.get(i).setMonths_check_funds_per(Double.parseDouble(map2.get("cntrtAmtBudgetMAccu") + "") == 0 ? null : Double.parseDouble(df.format(mapList.get(i).getYear_grand_total_sign() / (Double.parseDouble(map2.get("cntrtAmtBudgetMAccu") + "") / 10000))) * 100);

                        }
                    }
                }


            }*/
            DecimalFormat df = new DecimalFormat("#.00");
            for (MonthPlan map1 : mapList) {
                if (map1.getType() == 2) {
                    Double region = 0.00;

                    for (MonthPlan map2 : mapList) {
                        if ((map2.getFather_id()).equals(map1.getBusiness_id())) {
                            Double Project = map2.getTotal_sign_funds();

                            region += Project;
                        }
                    }
                    map1.setTotal_sign_funds(Double.parseDouble(df.format(region)));
                }
            }

            for (MonthPlan map1 : mapList) {
                if (map1.getType() == 1) {
                    Double region = 0.00;

                    for (MonthPlan map2 : mapList) {
                        if (map1.getType() == 2) {
                            Double Project = map2.getTotal_sign_funds();

                            region += Project;
                        }
                    }
                    map1.setTotal_sign_funds(Double.parseDouble(df.format(region)));
                }
            }

            int i=1;
            //循环遍历所有数据
            for (MonthPlan parentMap : mapList) {


                XSSFRow positionRow = targetSheet.createRow(startRows);
                Row row2 = targetSheet.getRow(startRows);
                positionRow.setHeightInPoints(20);
                //创建第一列并添加文本居左样式 即business_name列
                XSSFCell cell0 = positionRow.createCell(0);
                cell0.setCellStyle(style3);
                //获取最大列数 并在+1创建对应位置单元格用于存储basisGuid 后续将单元格内容隐藏
                XSSFCell lastCell = positionRow.createCell(maxCellNum);
                lastCell.setCellValue(parentMap.getBasisguid());
                //此条数据类型为集团 type=1
                if (parentMap.getType() == 1) {
                    cell0.setCellValue(parentMap.getBusiness_name()+" "+parentMap.getProjectCode());
                    System.out.println(parentMap.getBusiness_name()+" "+parentMap.getProjectCode());
                    //循环当前行的每一列 对其赋值 添加样式
                    for (int n = 1; n < maxCellNum; n++) {
                        String tem = "";
                        for (MonthPlan childMap : mapList) {
                            if ((int) childMap.getType() == 2) {
                                int row = (int) childMap.getRow();
                                char c = (char) (toChar + n);
                                if(c=='z'){
                                    System.err.println(c);
                                }
                                tem += c + "" + (row) + "+";
                            }
                        }
                        XSSFCell cell2 = positionRow.createCell(n);
                        if (n == 19) {
                            cell2.setCellStyle(style2);
                        } else if (n == 1 || n == 3 || n == 5 || n == 13 || n == 15) {
                            cell2.setCellStyle(style5);
                        } else {
                            cell2.setCellStyle(style3);
                        }
                        Integer rownum = positionRow.getRowNum();
                        if (n == 8) {

                            cell2.setCellFormula("IFERROR(ROUND(M" + (startRows + 1) + "/" + "H" + (startRows + 1) + "*10000/100,2),0)");
                        }
                        if (n == 10) {
                            cell2.setCellFormula("IFERROR(ROUND(M" + (startRows + 1) + "/" + "J" + (startRows + 1) + "*10000/100,2),0)");

                        }
                        //对单元格设置公式
                        if (tem != "" && n != 8 && n != 10) {
                            cell2.setCellFormula("SUM(" + tem.substring(0, tem.length() - 1) + ")");
                        }
                    }

                }
                //此条数据类型为区域 type=2
                else if ((int) parentMap.getType() == 2) {
                    cell0.setCellValue("---" + parentMap.getBusiness_name()+" "+parentMap.getProjectCode());
                    int row = parentMap.getRow();
//                    int startIndex = row;
//                    int endIndex = 0;
                    //给单元格设置公式，并且记录合并结尾条数
                    for (int n = 1; n < maxCellNum; n++) {
                        String tem = "";
                        for (MonthPlan childMap : mapList) {
                            String fatherId = childMap.getFather_id().toString();
                            row = (int) childMap.getRow();
                            char c = (char) (toChar + n);
                            if (parentMap.getGuid().equals(fatherId)) {
//                                endIndex = row - 1;
                                tem += c + "" + (row) + "+";
                            }
                        }
                        XSSFCell cell2 = positionRow.createCell(n);
                        if (n == 19) {
                            cell2.setCellStyle(style2);
                        } else if (n == 1 || n == 3 || n == 5 || n == 13 || n == 15) {
                            cell2.setCellStyle(style5);
                        } else {
                            cell2.setCellStyle(style3);
                        }

                        if (tem != "" && n < 17) {
                            cell2.setCellFormula("SUM(" + tem.substring(0, tem.length() - 1) + ")");
                        }
                        if (n == 8) {

                            cell2.setCellFormula("IFERROR(ROUND(M" + (startRows + 1) + "/" + "H" + (startRows + 1) + "*10000/100,2),0)");
                        }
                        if (n == 10) {
                            cell2.setCellFormula("IFERROR(ROUND(M" + (startRows + 1) + "/" + "J" + (startRows + 1) + "*10000/100,2),0)");

                        }
                        if (n == 17) {
                            if (parentMap.getReserve_sign_funds() > 0 || parentMap.getReserve_sign_funds() < 0) {
                                cell2.setCellValue(parentMap.getReserve_sign_funds());
                            } else {
                                cell2.setCellFormula("SUM(" + tem.substring(0, tem.length() - 1) + ")");

                            }
                        }
                        if (n == 18) {
                            if (parentMap.getNew_sign_funds() > 0 || parentMap.getNew_sign_funds() < 0) {
                                cell2.setCellValue(parentMap.getNew_sign_funds());
                            } else {
                                cell2.setCellFormula("SUM(" + tem.substring(0, tem.length() - 1) + ")");

                            }
                        }
                        if (n == 19) {
                            if (parentMap.getTotal_sign_funds() > 0 || parentMap.getTotal_sign_funds() < 0) {
                                System.err.println(parentMap.getTotal_sign_funds());
                                cell2.setCellValue(parentMap.getTotal_sign_funds());
                            } else {
                                cell2.setCellFormula("SUM(" + tem.substring(0, tem.length() - 1) + ")");

                            }
                        }
                        if(i==1&&n==20){
                            cell2.setCellFormula("SUM(" + tem.substring(0, tem.length() - 1) + ")");
                        }
                    }


                } else {
                    if (preparedByUnitType == Constant.PREPARED_BY_UNIT_TYPE_GROUP) {
                        setDataToProjectCellGroup(startRows + 1, positionRow, parentMap, style1, style3, style4, style5, preparedByUnitType);

                    } else {
                        setDataToProjectCell(startRows + 1, positionRow, parentMap, style1, style3, style4, style5, preparedByUnitType);

                    }
                    //给type=3（项目）各列字段赋值
                }
                startRows++;
                i=2;
            }

            //根据行数给Excel设置一二级合并
//            for (Map<String, String> rowMap : groupRowlist) {
//                int startIndex = Integer.valueOf(rowMap.get("startIndex"));
//                int endIndex = Integer.valueOf(rowMap.get("endIndex"));
//                targetSheet.groupRow(startIndex, endIndex);
//            }
            targetSheet.setColumnHidden(maxCellNum, true);
            targetSheet.setRowSumsBelow(false);
            targetSheet.createFreezePane(1, 3, 1, 3);
            targetSheet.setForceFormulaRecalculation(true);
            fileOutputStream = new FileOutputStream(targetFilePath);
            //页面输出
            targetWorkBook.write(response.getOutputStream());
            /*立即计算*/
            targetSheet.setForceFormulaRecalculation(true);
            //服务器硬盘输出
            // targetWorkBook.write(fileOutputStream);
        } catch (ServiceException se) {
            se.printStackTrace();
            System.out.println(se.getResponseMsg());
        } catch (UnsupportedEncodingException e) {
            System.out.print("中文字符转换异常");
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


    @Override
    public void monthlyPlanUpExport(HttpServletRequest request, HttpServletResponse response, String month, int preparedByUnitType, String businessId, List preparedByLevel) {
        String planName;
        String basePath;
        String templatePath;
        String targetFileDir;
        String targetFilePath;
        FileInputStream templateInputStream = null;
        FileOutputStream fileOutputStream = null;
        Workbook targetWorkBook;
        XSSFSheet targetSheet;

        switch (preparedByUnitType) {
            //集团
            case Constant.PREPARED_BY_UNIT_TYPE_GROUP:
                planName = Constant.GROUP_EXCEL_NAME;
                break;
                //区域/事业部
            case Constant.PREPARED_BY_UNIT_TYPE_REGION:
                planName = Constant.REGION_EXCEL_NAME;
                break;
                //项目
            case Constant.PREPARED_BY_UNIT_TYPE_PROJECT:
                planName = Constant.PROJECT_EXCEL_NAME;
                break;
            default:
                planName = "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new Date());
        planName += time;

        try {
//            basePath = "E:/xuhui/marketing-control-api/cifimaster/visolink-sales-api/src";
            basePath = request.getServletContext().getRealPath("/");
            //模板文件路径。
            if (preparedByUnitType == Constant.PREPARED_BY_UNIT_TYPE_REGION) {
                templatePath = File.separator + "TemplateExcel" + File.separator + "region_issued_indicator_up_data.xlsx";
            } else if (preparedByUnitType == Constant.PREPARED_BY_UNIT_TYPE_GROUP) {
                templatePath = File.separator + "TemplateExcel" + File.separator + "group_issued_indicator_up_data.xlsx";
            } else {
                templatePath = File.separator + "TemplateExcel" + File.separator + "project_issued_indicator_data.xlsx";
            }
            //导出临时文件文件夹。
            targetFileDir = "Uploads" + File.separator + "DownLoadTemporaryFiles";
            // 目标文件路径。
            targetFilePath = targetFileDir + File.separator + planName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            planName = URLEncoder.encode(planName + ".xlsx", "utf-8").replace("+", "%20");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //设置content-disposition响应头控制浏览器以下载的形式打开文件
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(planName.getBytes(), "utf-8"));

            //验证模板文件是否存在
            //String  realpath= this.getClass().getResource("/").getPath()  ;


            //realpath=realpath.substring(0,realpath.indexOf("/target"))+File.separator+"src"+File.separator+"main"+File.separator+"webapp"+templatePath;

            // File templateFile = new File(realpath);

           // basePath="/Users/WorkSapce/Java/旭辉集团/Java/marketing-control-api/cifimaster/visolink-sales-api/src/main/webapp";
            String filePath =  basePath+ templatePath;

            File templateFile = new File(filePath);

            if (!templateFile.exists()) {
                templateFile.mkdirs();
                throw new ServiceException("-15_1003", "认购确认导出失败。模板文件不存在");
            }
            //验证目标文件夹是否存在
            File targetFileDirFile = new File(targetFileDir);
            if (!targetFileDirFile.exists()) {
                targetFileDirFile.mkdirs();
            }
            //创建输出文档。
            templateInputStream = new FileInputStream(templateFile);
            targetWorkBook = new XSSFWorkbook(templateInputStream);
            targetSheet = (XSSFSheet) targetWorkBook.getSheetAt(0);
            targetWorkBook.setSheetName(0, "集团下达月度营销指标");
            //模板文件中最大行
            int maxTemplateRows = targetSheet.getLastRowNum();
            //清空原模板剩余数据
            for (int i = 3; i <= maxTemplateRows; i++) {
                Row removeRow = targetSheet.getRow(i);

                targetSheet.removeRow(removeRow);
            }
            List<MonthPlan> mapList = new ArrayList<>();
            Map temMap = new HashMap(8);
            temMap.put("month", month);
            temMap.put("businessId", businessId);
            temMap.put("prepared_by_unit_type", preparedByUnitType);
            MonthPlan map = exportDao.selectMonthPlanUpByGuid(temMap);
            map.setRow(Constant.START_ROW);
            mapList.add(map);
            selectAllMonthPlanUpByFatherId(mapList, Constant.START_ROW, map, preparedByLevel);

            //起始行
            int startRows = Constant.START_ROW;
            //1a 2b 3c 4d 5e  数字与字符之间的转换
            int toChar = 97;
            Row row0 = targetSheet.getRow(0);
            int maxCellNum = row0.getPhysicalNumberOfCells();
            targetSheet.protectSheet("edit");

            /**
             * 单元格样色共三种：
             * 1.白底 居中加粗 style1
             * 2.灰底 居中 加粗
             * 3.灰底 正常
             * 4.动态判断
             */
            XSSFDataFormat format = (XSSFDataFormat) targetWorkBook.createDataFormat();
            CellStyle style1 = targetWorkBook.createCellStyle();
            CellStyle style2 = targetWorkBook.createCellStyle();
            CellStyle style3 = targetWorkBook.createCellStyle();
            CellStyle style4 = targetWorkBook.createCellStyle();
            CellStyle style5 = targetWorkBook.createCellStyle();
            //   style1.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            style2.cloneStyleFrom(style1);
            style2.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style1.setLocked(false);
            style1.setBorderBottom(BorderStyle.THIN);
            style1.setBorderTop(BorderStyle.THIN);
            style1.setBorderLeft(BorderStyle.THIN);
            style1.setBorderRight(BorderStyle.THIN);

            style2.setBorderBottom(BorderStyle.THIN);
            style2.setBorderTop(BorderStyle.THIN);
            style2.setBorderLeft(BorderStyle.THIN);
            style2.setBorderRight(BorderStyle.THIN);
            style2.setDataFormat(format.getFormat("0.00"));
            style3.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style3.setBorderBottom(BorderStyle.THIN);
            style3.setBorderTop(BorderStyle.THIN);
            style3.setBorderLeft(BorderStyle.THIN);
            style3.setBorderRight(BorderStyle.THIN);

            style5.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style5.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style5.setBorderBottom(BorderStyle.THIN);
            style5.setBorderTop(BorderStyle.THIN);
            style5.setBorderLeft(BorderStyle.THIN);
            style5.setBorderRight(BorderStyle.THIN);


            style4.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style4.setBorderBottom(BorderStyle.THIN);
            style4.setBorderTop(BorderStyle.THIN);
            style4.setBorderLeft(BorderStyle.THIN);
            style4.setBorderRight(BorderStyle.THIN);

            style4.setAlignment(HorizontalAlignment.CENTER);
            CellStyle style6 = style4;

            style3.setDataFormat(format.getFormat("0.00"));
            List<Map<String, String>> groupRowlist = new ArrayList<Map<String, String>>();
            //循环遍历所有数据
            for (MonthPlan parentMap : mapList) {
                XSSFRow positionRow = targetSheet.createRow(startRows);
                Row row2 = targetSheet.getRow(startRows);
                positionRow.setHeightInPoints(20);
                //创建第一列并添加文本居左样式 即business_name列
                XSSFCell cell0 = positionRow.createCell(0);
                cell0.setCellStyle(style3);
                //获取最大列数 并在+1创建对应位置单元格用于存储basisGuid 后续将单元格内容隐藏
                XSSFCell lastCell = positionRow.createCell(maxCellNum);
                lastCell.setCellValue(parentMap.getBasisguid());
                //此条数据类型为集团 type=1
                if ((int) parentMap.getType() == 1) {
                    cell0.setCellValue(parentMap.getBusiness_name()+" "+parentMap.getProjectCode());

                    //循环当前行的每一列 对其赋值 添加样式
                    for (int n = 1; n < maxCellNum; n++) {
                        String tem = "";
                        for (MonthPlan childMap : mapList) {
                            if ((int) childMap.getType() == 2) {
                                int row = (int) childMap.getRow();
                                char c = (char) (toChar + n);
                                tem += c + "" + (row) + "+";
                            }
                        }
                        XSSFCell cell2 = positionRow.createCell(n);
                        if (n == 19) {
                            cell2.setCellStyle(style2);
                        } else if (n == 1 || n == 3 || n == 5 || n == 13 || n == 15 || n == 24) {
                            cell2.setCellStyle(style5);
                        } else if (n == 22 || n == 23 || n == 24) {

                            cell2.setCellStyle(style4);
                        } else {
                            cell2.setCellStyle(style3);
                        }

                        //对单元格设置公式
                        if (tem != "" || n != 24 || n != 23 || n != 22) {

                            cell2.setCellFormula("SUM(" + tem.substring(0, tem.length() - 1) + ")");

                        }

                        //对单元格设置公式

                        if (n == 22) {

                            cell2.setCellFormula(parentMap.getTop_three_month_average_turnover_rate() * 100 + "&\"%\"");
                        }
                        if (n == 23) {
                            cell2.setCellFormula(parentMap.getLast_month_turnover_rate() * 100 + "&\"%\"");
                        }
                        if (n == 24) {
                            if(parentMap.getPlan_turnover_rate()!=null){
                                cell2.setCellFormula(parentMap.getPlan_turnover_rate() * 100 + "&\"%\"");
                            }
                        }

                    }
                    //此条数据类型为区域 type=2
                } else if ((int) parentMap.getType() == 2) {
                    cell0.setCellValue("---" + parentMap.getBusiness_name()+" "+parentMap.getProjectCode());
                    int row = parentMap.getRow();
//                    int startIndex = row;
//                    int endIndex = 0;
                    //给单元格设置公式，并且记录合并结尾条数
                    for (int n = 1; n < maxCellNum; n++) {
                        String tem = "";
                        for (MonthPlan childMap : mapList) {
                            String fatherId = childMap.getFather_id();
                            row = (int) childMap.getRow();
                            char c = (char) (toChar + n);
                            if (parentMap.getGuid().equals(fatherId)) {
//                                endIndex = row - 1;
                                tem += c + "" + (row) + "+";
                            }
                        }
                        XSSFCell cell2 = positionRow.createCell(n);
                        if (n == 19) {
                            cell2.setCellStyle(style2);
                        } else if (n == 1 || n == 3 || n == 5 || n == 13 || n == 15 || n == 20) {
                            cell2.setCellStyle(style5);
                        } else if (n == 22 || n == 23 || n == 24) {

                            cell2.setCellStyle(style4);
                        } else {
                            cell2.setCellStyle(style3);
                        }

                        //对单元格设置公式
                        if (tem != "" || n != 24 || n != 23 || n != 22) {
                            cell2.setCellFormula("SUM(" + tem.substring(0, tem.length() - 1) + ")");

                        }

                        //对单元格设置公式

                        if (n == 22) {
                            if(parentMap.getTop_three_month_average_turnover_rate()!=null){
                                cell2.setCellFormula(parentMap.getTop_three_month_average_turnover_rate() * 100 + "&\"%\"");
                            }else{
                                cell2.setCellFormula(0+ "&\"%\"");
                            }
                        }
                        if (n == 23) {
                            if(parentMap.getLast_month_turnover_rate() !=null){
                                cell2.setCellFormula(parentMap.getLast_month_turnover_rate() * 100 + "&\"%\"");
                            }else{
                                cell2.setCellFormula(0 + "&\"%\"");
                            }
                        }
                        if (n == 24) {
                            if (parentMap.getPlan_turnover_rate()!=null){
                                cell2.setCellFormula(parentMap.getPlan_turnover_rate() + "&\"%\"");
                            }else{
                                cell2.setCellFormula(0 + "&\"%\"");
                            }
                        }
                    }
//                    Map groupIndex = new HashMap(16);
//                    groupIndex.put("startIndex", String.valueOf(startIndex));
//                    groupIndex.put("endIndex", String.valueOf(endIndex));
//                    groupIndex.put("list", groupIndex);
//                    groupRowlist.add(groupIndex);
                } else {
                    //给type=3（项目）各列字段赋值
                    setDataToProjectUpCell(startRows + 1, positionRow, parentMap, style1, style3, style4, style5, style6, preparedByUnitType);
                }
                startRows++;
            }

            //根据行数给Excel设置一二级合并
//            for (Map<String, String> rowMap : groupRowlist) {
//                int startIndex = Integer.valueOf(rowMap.get("startIndex"));
//                int endIndex = Integer.valueOf(rowMap.get("endIndex"));
//                targetSheet.groupRow(startIndex, endIndex);
//            }
            targetSheet.setColumnHidden(maxCellNum, true);
            targetSheet.setRowSumsBelow(false);
            targetSheet.createFreezePane(1, 3, 1, 3);
            targetSheet.setForceFormulaRecalculation(true);
            fileOutputStream = new FileOutputStream(targetFilePath);
            //页面输出
            targetWorkBook.write(response.getOutputStream());
            /*立即计算*/
            targetSheet.setForceFormulaRecalculation(true);
            //服务器硬盘输出
            // targetWorkBook.write(fileOutputStream);
        } catch (ServiceException se) {
            se.printStackTrace();
            System.out.println(se.getResponseMsg());
        } catch (UnsupportedEncodingException e) {
            System.out.print("中文字符转换异常");
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


    @Override
    public void monthlyPlanProjectExport(HttpServletRequest request, HttpServletResponse response, String month, int preparedByUnitType, String businessId, List preparedByLevel) {
        String planName;
        String basePath;
        String templatePath;
        String targetFileDir;
        String targetFilePath;
        FileInputStream templateInputStream = null;
        FileOutputStream fileOutputStream = null;
        Workbook targetWorkBook;
        XSSFSheet targetSheet;

        planName = Constant.PROJECT_EXCEL_NAME;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new Date());
        planName += time;

        try {
            String filePath;
            //配置本地模版路径
            String realpath = null;

            //本地模版路径
          /*  realpath = "/Users/WorkSapce/Java/旭辉集团/Java/marketing-control-api/cifimaster/visolink-sales-api/src/main/webapp/TemplateExcel/project_issued_indicator_data.xlsx";
            filePath = realpath;*/
            //服务器模版路径
            realpath = request.getServletContext().getRealPath("/");

            templatePath = File.separator + "TemplateExcel" + File.separator + "project_issued_indicator_data.xlsx";
            filePath = realpath + templatePath;
            File templateFile = new File(filePath);
            //导出临时文件文件夹。
            targetFileDir = "Uploads" + File.separator + "DownLoadTemporaryFiles";
            // 目标文件路径。
            targetFilePath = targetFileDir + File.separator + planName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            planName = URLEncoder.encode(planName + ".xlsx", "utf-8").replace("+", "%20");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //设置content-disposition响应头控制浏览器以下载的形式打开文件
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(planName.getBytes(), "utf-8"));
            if (!templateFile.exists()) {
                templateFile.mkdirs();
                throw new ServiceException("-15_1003", "认购确认导出失败。模板文件不存在");
            }
            //创建输出文档。
            templateInputStream = new FileInputStream(templateFile);
            targetWorkBook = new XSSFWorkbook(templateInputStream);
            targetSheet = (XSSFSheet) targetWorkBook.getSheetAt(0);
            targetWorkBook.setSheetName(0, "项目上报月度营销指标");
            //模板文件中最大行
            int maxTemplateRows = targetSheet.getLastRowNum();
            //清空原模板剩余数据
            for (int i = 3; i <= maxTemplateRows; i++) {
                Row removeRow = targetSheet.getRow(i);

                targetSheet.removeRow(removeRow);
            }
            List<MonthPlan> mapList = new ArrayList<>();
            Map temMap = new HashMap(8);
            temMap.put("month", month);
            temMap.put("businessId", businessId);
            temMap.put("prepared_by_unit_type", preparedByUnitType);
            MonthPlan map = exportDao.selectMonthPlanByGuid(temMap);

            map.setRow(Constant.START_ROW);
            mapList.add(map);
            selectAllMonthPlanByFatherId(mapList, Constant.START_ROW, map, preparedByLevel);
            //起始行
            int startRows = Constant.START_ROW;
            //1a 2b 3c 4d 5e  数字与字符之间的转换
            int toChar = 97;
            Row row0 = targetSheet.getRow(0);
            int maxCellNum = row0.getPhysicalNumberOfCells();
            targetSheet.protectSheet("edit");


            /**
             * 单元格样色共三种：
             * 1.白底 居中加粗 style1
             * 2.灰底 居中 加粗
             * 3.灰底 正常
             * 4.动态判断
             */
            XSSFDataFormat format = (XSSFDataFormat) targetWorkBook.createDataFormat();
            CellStyle style1 = row0.getCell(0).getCellStyle();
            CellStyle style2 = targetWorkBook.createCellStyle();
            CellStyle style3 = targetWorkBook.createCellStyle();
            CellStyle style4 = targetWorkBook.createCellStyle();
            CellStyle style5 = targetWorkBook.createCellStyle();
            CellStyle style6 = targetWorkBook.createCellStyle();
            //  style1.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            style2.cloneStyleFrom(style1);
            style1.setBorderBottom(BorderStyle.THIN);
            style1.setBorderTop(BorderStyle.THIN);
            style1.setBorderLeft(BorderStyle.THIN);
            style1.setBorderRight(BorderStyle.THIN);
            style2.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style1.setLocked(false);
            style2.setDataFormat(format.getFormat("0.00"));
            style3.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style3.setBorderBottom(BorderStyle.THIN);
            style3.setBorderTop(BorderStyle.THIN);
            style3.setBorderLeft(BorderStyle.THIN);
            style3.setBorderRight(BorderStyle.THIN);
            style3.setAlignment(HorizontalAlignment.RIGHT);
            style5.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style5.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style5.setBorderBottom(BorderStyle.THIN);
            style5.setBorderTop(BorderStyle.THIN);
            style5.setBorderLeft(BorderStyle.THIN);
            style5.setBorderRight(BorderStyle.THIN);
            style5.setAlignment(HorizontalAlignment.CENTER);

            style6.setBorderBottom(BorderStyle.THIN);
            style6.setBorderTop(BorderStyle.THIN);
            style6.setBorderLeft(BorderStyle.THIN);
            style6.setBorderRight(BorderStyle.THIN);
            style6.setLocked(false);
            style3.setDataFormat(format.getFormat("0.00"));

            List<Map<String, String>> groupRowlist = new ArrayList<Map<String, String>>();
            //循环遍历所有数据,判断是否有面积段，没有的话就以业态为基础可输入
            mapList = getDataLake(mapList, month);
            for (MonthPlan parentMap : mapList) {


                XSSFRow positionRow = targetSheet.createRow(startRows);
                Row row2 = targetSheet.getRow(startRows);
                positionRow.setHeightInPoints(20);
                //创建第一列并添加文本居左样式 即business_name列
                XSSFCell cell0 = positionRow.createCell(0);
                cell0.setCellStyle(style3);
                //获取最大列数 并在+1创建对应位置单元格用于存储basisGuid 后续将单元格内容隐藏
                //此条数据类型为项目 type=3
                XSSFCell lastCell = positionRow.createCell(maxCellNum);
                lastCell.setCellValue(parentMap.getBusiness_id());
                if ((int) parentMap.getType() == 3) {
                    cell0.setCellValue(parentMap.getBusiness_name()+" "+parentMap.getProjectCode());
                    //循环当前行的每一列 对其赋值 添加样式
                    for (int n = 1; n < maxCellNum; n++) {
                        String tem = "";
                        for (MonthPlan childMap : mapList) {
                            if ((int) childMap.getType() == 4) {
                                int row = (int) childMap.getRow();
                                char c = (char) (toChar + n);
                                if (n > 25) {
                                    c = (char) (toChar + n - 26);
                                    tem += "A" + c + "" + (row) + "+";
                                    ;
                                } else {
                                    tem += c + "" + (row) + "+";
                                }


                            }

                        }
                        XSSFCell cell2 = positionRow.createCell(n);
                        if (n == 1 || n == 3 || n == 5 || n == 13 || n == 15 || n == 17 || n == 19 || n == 21 || n == 23) {
                            cell2.setCellStyle(style5);
                        } else {
                            cell2.setCellStyle(style3);
                        }
                        //对单元格设置公式
                        System.out.println("SUM(" + tem.substring(0, tem.length() - 1) + ")");
                        if (tem != "") {
                            cell2.setCellFormula("SUM(" + tem.substring(0, tem.length() - 1) + ")");
                        }
                    }


                    setDataToProjectCellSpecial(startRows + 1, positionRow, parentMap, style1, style3, style4, style5, style6, (int) parentMap.getType());
                } else if ((int) parentMap.getType() == 4) {
                    cell0.setCellValue("---" + parentMap.getBusiness_name().toString()+" "+parentMap.getProjectCode());
                    int row = (int) parentMap.getRow();
//                    int startIndex = row;
//                    int endIndex = 0;
                    //给单元格设置公式，并且记录合并结尾条数
                    for (int n = 1; n < maxCellNum; n++) {
                        String tem = "";
                        for (MonthPlan childMap : mapList) {
                            String fatherId = childMap.getFather_id();
                            row = (int) childMap.getRow();

                            char c = (char) (toChar + n);
                            if (n > 25) {
                                c = (char) (toChar + n - 26);
                            }

                            if (parentMap.getGuid().equals(fatherId)) {
//                                endIndex = row - 1;

                                if (n > 25) {
                                    tem += "A" + c + "" + (row) + "+";
                                    ;
                                } else {
                                    tem += c + "" + (row) + "+";
                                }
                            }
                        }
                        XSSFCell cell2 = positionRow.createCell(n);
                        if (n == 1 || n == 3 || n == 5 || n == 13 || n == 15 || n == 17 || n == 19 || n == 21 || n == 23) {
                            cell2.setCellStyle(style5);
                        } else {
                            cell2.setCellStyle(style3);
                        }

                        if (tem != "") {
                            cell2.setCellFormula("SUM(" + tem.substring(0, tem.length() - 1) + ")");
                        }
                    }
                    setDataToProjectCellSpecial(startRows + 1, positionRow, parentMap, style1, style3, style4, style5, style6, (int) parentMap.getType());
//                    Map groupIndex = new HashMap(16);
//                    groupIndex.put("startIndex", String.valueOf(startIndex));
//                    groupIndex.put("endIndex", String.valueOf(endIndex));
//                    groupIndex.put("list", groupIndex);
//                    groupRowlist.add(groupIndex);
                } else if ((int) parentMap.getType() == 5) {
                    cell0.setCellValue("---" + parentMap.getBusiness_name()+" "+parentMap.getProjectCode());
                    int row = (int) parentMap.getRow();
//                    int startIndex = row;
//                    int endIndex = 0;
                    //给单元格设置公式，并且记录合并结尾条数
                    for (int n = 1; n < maxCellNum; n++) {
                        String tem = "";
                        for (MonthPlan childMap : mapList) {
                            String fatherId = childMap.getFather_id();
                            row = (int) childMap.getRow();
                            char c = (char) (toChar + n);
                            if (n > 25) {
                                c = (char) (toChar + n - 26);
                            }
                            if (parentMap.getGuid().equals(fatherId)) {
//                                endIndex = row - 1;
                                if (n > 25) {
                                    tem += "A" + c + "" + (row) + "+";
                                    ;
                                } else {
                                    tem += c + "" + (row) + "+";
                                }
                            }
                        }
                        XSSFCell cell2 = positionRow.createCell(n);
                        if (n == 1 || n == 3 || n == 5 || n == 13 || n == 15 || n == 17 || n == 19 || n == 21 || n == 23) {
                            cell2.setCellStyle(style5);
                        } else {
                            cell2.setCellStyle(style3);
                        }

                        if (tem != "") {
                            cell2.setCellFormula("SUM(" + tem.substring(0, tem.length() - 1) + ")");
                        }
                    }


                    setDataToProjectCellSpecial(startRows + 1, positionRow, parentMap, style1, style3, style4, style5, style6, parentMap.getType());
                } else if ((int) parentMap.getType() == 6) {

                    cell0.setCellValue("---" + parentMap.getBusiness_name().toString()+" "+parentMap.getProjectCode());

                    int row = (int) parentMap.getRow();


//                    int startIndex = row;
//                    int endIndex = 0;
                    //给单元格设置公式，并且记录合并结尾条数
                    for (int n = 1; n < maxCellNum; n++) {
                        String tem = "";
                        for (MonthPlan childMap : mapList) {
                            String fatherId = childMap.getFather_id();
                            row = (int) childMap.getRow();
                            char c = (char) (toChar + n);
                            if (n > 25) {
                                c = (char) (toChar + n - 26);
                            }
                            if (parentMap.getGuid().equals(fatherId)) {
//                                endIndex = row - 1;
                                if (n > 25) {
                                    tem += "A" + c + "" + (row) + "+";
                                    ;
                                } else {
                                    tem += c + "" + (row) + "+";
                                }
                            }
                        }
                        XSSFCell cell2 = positionRow.createCell(n);
                        if (n == 1 || n == 3 || n == 5 || n == 13 || n == 15 || n == 17 || n == 19 || n == 21 || n == 23) {
                            cell2.setCellStyle(style5);
                        } else {
                            cell2.setCellStyle(style3);
                        }

                        if (tem != "") {
                            cell2.setCellFormula("SUM(" + tem.substring(0, tem.length() - 1) + ")");
                        }
                    }
                    /*若没有面积段，直接以业态为输入项*/


                    setDataToProjectCellSpecial(startRows + 1, positionRow, parentMap, style1, style3, style4, style5, style6, parentMap.getType());


                } else if ((int) parentMap.getType() == 7) {
                    //给type=7面积段）各列字段赋值
                    /*若没有面积段，直接以业态为输入项*/


                    setDataToProjectCellSpecial(startRows + 1, positionRow, parentMap, style1, style3, style4, style5, style6, parentMap.getType());
                }

                startRows++;
            }

            //根据行数给Excel设置一二级合并
//            for (Map<String, String> rowMap : groupRowlist) {
//                int startIndex = Integer.valueOf(rowMap.get("startIndex"));
//                int endIndex = Integer.valueOf(rowMap.get("endIndex"));
//                targetSheet.groupRow(startIndex, endIndex);
//            }
            targetSheet.setColumnHidden(maxCellNum, true);
            targetSheet.setRowSumsBelow(false);
            targetSheet.createFreezePane(1, 3, 1, 3);
            /*立即计算*/
            targetSheet.setForceFormulaRecalculation(true);
            fileOutputStream = new FileOutputStream(targetFilePath);
            //页面输出
            targetWorkBook.write(response.getOutputStream());
            //服务器硬盘输出
            //  targetWorkBook.write(fileOutputStream);
        } catch (ServiceException se) {
            se.printStackTrace();
            System.out.println(se.getResponseMsg());
        } catch (UnsupportedEncodingException e) {
            System.out.print("中文字符转换异常");
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


    private Boolean selectAllMonthPlanByFatherId(List<MonthPlan> lists, int row, MonthPlan map, List preparedByLevel) {
        rowNum = row + 1;
        map.setRow(rowNum);
        map.setPreparedByLevels(preparedByLevel);

        /* 递归方法优化 bql 2020.08.11 */
        /*List<MonthPlan> childPlanData = exportDao.selectAllMonthPlanByFatherIdAll(map);
        lists.addAll(childPlanData);*/

        /* 递归原方法 */
        List<MonthPlan> childPlanData = exportDao.selectAllMonthPlanByFatherId(map);
        if (childPlanData != null && childPlanData.size() != 0) {
            for (MonthPlan child : childPlanData) {
                lists.add(child);
                selectAllMonthPlanByFatherId(lists, rowNum, child, preparedByLevel);
            }
        }

        /*判断是否有下一层，没有的话就以等级6为输入项 */
        if (childPlanData == null || childPlanData.size() < 1) {
            if (map.getType() == 6) {
                map.setFlag("isSix");
            }
        }
        return true;
    }


    private Boolean selectAllMonthPlanUpByFatherId(List<MonthPlan> lists, int row, MonthPlan map, List preparedByLevel) {
        rowNum = row + 1;
        map.setRow(rowNum);
        map.setPreparedByLevels(preparedByLevel);
        List<MonthPlan> childPlanData = exportDao.selectAllMonthPlanUpByFatherId(map);


        if (childPlanData != null && childPlanData.size() != 0) {
            for (MonthPlan child : childPlanData) {
                lists.add(child);
                selectAllMonthPlanUpByFatherId(lists, rowNum, child, preparedByLevel);
            }
        }

        /*判断是否有下一层，没有的话就以等级6为输入项 */
        if (childPlanData == null || childPlanData.size() < 1) {
            if (map.getType() == 6) {
                map.setFlag("isSix");
            }
        }
        return true;
    }


    private void setDataToProjectCell(int startRows, XSSFRow positionRow, MonthPlan map, CellStyle style1, CellStyle style3, CellStyle style4, CellStyle style5, int preparedByUnitType) {


        XSSFCell cell0 = positionRow.createCell(0);
        cell0.setCellStyle(style3);
        String type = "type";
        //项目导出时，用"---"动态表现出层级，且单元格无需颜色样式
        if (preparedByUnitType == Constant.PREPARED_BY_UNIT_TYPE_PROJECT) {
            String preCellValue = "---";
            for (int i = 3; i < map.getType(); i++) {
                preCellValue += " ---";
            }
            cell0.setCellValue(preCellValue + map.getBusiness_name()+" "+map.getProjectCode());
            style4 = style1;

        } else {
            cell0.setCellValue("--- ---" + map.getBusiness_name()+" "+map.getProjectCode());
            style4 = style3;
        }
        XSSFCell cell1 = positionRow.createCell(1);
        cell1.setCellStyle(style5);
        if (map.getReserve_can_sell_set() == null) {
            cell1.setCellValue("");
        } else {
            cell1.setCellValue(map.getReserve_can_sell_set());
        }
        XSSFCell cell2 = positionRow.createCell(2);
        cell2.setCellStyle(style4);

        if (map.getReserve_can_sell_funds() == null) {
            cell2.setCellValue("");
        } else {
            cell2.setCellValue(map.getReserve_can_sell_funds());
        }
        XSSFCell cell3 = positionRow.createCell(3);
        cell3.setCellStyle(style5);

        if (map.getNew_reserve_set() == null) {
            cell3.setCellValue("");
        } else {
            cell3.setCellValue(map.getNew_reserve_set());
        }

        XSSFCell cell4 = positionRow.createCell(4);
        cell4.setCellStyle(style4);

        if (map.getNew_reserve_funds() == null) {
            cell4.setCellValue("");
        } else {
            cell4.setCellValue(map.getNew_reserve_funds());
        }


        XSSFCell cell5 = positionRow.createCell(5);
        cell5.setCellStyle(style5);
        String tem = "B" + startRows + "+D" + startRows;
        cell5.setCellFormula("SUM(" + tem + ")");
        XSSFCell cell6 = positionRow.createCell(6);
        cell6.setCellStyle(style4);

        tem = "C" + startRows + "+E" + startRows;
        cell6.setCellFormula("SUM(" + tem + ")");

        XSSFCell cell7 = positionRow.createCell(7);
        cell7.setCellStyle(style4);
        if (map.getYear_check_funds() == null) {
            cell7.setCellValue(0);
        } else {
            cell7.setCellValue(map.getYear_check_funds());
        }

        XSSFCell cell8 = positionRow.createCell(8);
        cell8.setCellStyle(style4);
        if (map.getYear_check_funds_per() == null) {
            cell8.setCellValue("");
        } else {
            cell8.setCellValue(map.getYear_check_funds_per());
        }

        XSSFCell cell9 = positionRow.createCell(9);
        cell9.setCellStyle(style4);
        if (map.getMonths_check_funds() == null) {
            cell9.setCellValue(0);
        } else {
            cell9.setCellValue(map.getMonths_check_funds());
        }

        XSSFCell cell10 = positionRow.createCell(10);
        cell10.setCellStyle(style4);
        if (map.getMonths_check_funds_per() == null) {
            cell10.setCellValue("");
        } else {
            cell10.setCellValue(map.getMonths_check_funds_per());
        }


        XSSFCell cell11 = positionRow.createCell(11);
        cell11.setCellStyle(style4);
        if (map.getYear_plan_sign() == null) {
            cell11.setCellValue("");
        } else {
            cell11.setCellValue(map.getYear_plan_sign());
        }
        XSSFCell cell12 = positionRow.createCell(12);
        cell12.setCellStyle(style4);

        if (map.getYear_grand_total_sign() == null) {
            cell12.setCellValue("");
        } else {
            cell12.setCellValue(map.getYear_grand_total_sign());
        }

        XSSFCell cell13 = positionRow.createCell(13);
        cell13.setCellStyle(style5);
        if (map.getTop_three_month_average_sign_set() == null) {
            cell13.setCellValue("");
        } else {
            cell13.setCellValue(map.getTop_three_month_average_sign_set());
        }
        XSSFCell cell14 = positionRow.createCell(14);
        cell14.setCellStyle(style4);
        if (map.getTop_three_month_average_sign_funds() == null) {
            cell14.setCellValue("");
        } else {
            cell14.setCellValue(map.getTop_three_month_average_sign_funds());
        }
        XSSFCell cell15 = positionRow.createCell(15);
        cell15.setCellStyle(style5);
        if (map.getUpper_moon_sign_set() == null) {
            cell15.setCellValue("");
        } else {
            cell15.setCellValue(map.getUpper_moon_sign_set());
        }
        XSSFCell cell16 = positionRow.createCell(16);
        cell16.setCellStyle(style4);
        if (map.getUpper_moon_sign_funds() == null) {
            cell16.setCellValue("");
        } else {
            cell16.setCellValue(map.getUpper_moon_sign_funds());
        }
        XSSFCell cell17 = positionRow.createCell(17);
        cell17.setCellStyle(style1);
        if (map.getReserve_sign_funds() == null) {
            cell17.setCellValue("");
        } else {
            cell17.setCellValue(map.getReserve_sign_funds());
        }
        XSSFCell cell18 = positionRow.createCell(18);
        cell18.setCellStyle(style1);
        if (map.getNew_sign_funds() == null) {
            cell18.setCellValue("");
        } else {
            cell18.setCellValue(map.getNew_sign_funds());
        }
        XSSFCell cell19 = positionRow.createCell(19);
        cell19.setCellStyle(style4);
        tem = "R" + startRows + "+S" + startRows;
        cell19.setCellFormula("SUM(" + tem + ")");
        /**
         * 根据不同的导出类型适应不同的模板
         */
        if (Constant.PREPARED_BY_UNIT_TYPE_REGION == preparedByUnitType) {
            XSSFCell cell20 = positionRow.createCell(20);
            cell20.setCellStyle(style1);
            if (map.getMarketing_promotion_cost() == null) {
                cell20.setCellValue("");
            } else {
                cell20.setCellValue(map.getMarketing_promotion_cost());
            }
        }
        if (Constant.PREPARED_BY_UNIT_TYPE_PROJECT == preparedByUnitType) {
            XSSFCell cell20 = positionRow.createCell(20);
            cell20.setCellStyle(style1);
            if (map.getPlan_subscription_set() == null) {
                cell20.setCellValue("");
            } else {
                cell20.setCellValue(map.getPlan_subscription_set());
            }
            XSSFCell cell21 = positionRow.createCell(21);
            cell21.setCellStyle(style3);
            if (map.getPlan_subscription_funds() == null) {
                cell21.setCellValue("");
            } else {
                cell21.setCellValue(map.getPlan_subscription_funds());
            }
            XSSFCell cell22 = positionRow.createCell(22);
            cell22.setCellStyle(style3);
            if (map.getTop_three_month_average_turnover_rate() == null) {
                cell22.setCellValue("");
            } else {
                cell22.setCellValue(map.getTop_three_month_average_turnover_rate());
            }
            XSSFCell cell23 = positionRow.createCell(19);
            cell23.setCellStyle(style3);

            if (map.getLast_month_turnover_rate() == null) {
                cell23.setCellValue("");
            } else {
                cell23.setCellValue(map.getLast_month_turnover_rate());
            }
            XSSFCell cell24 = positionRow.createCell(24);
            cell24.setCellStyle(style3);
            if (map.getPlan_turnover_rate() == null) {
                cell24.setCellValue("");
            } else {
                cell24.setCellValue(map.getPlan_turnover_rate());
            }


        }
    }

    private void setDataToProjectCellGroup(int startRows, XSSFRow positionRow, MonthPlan map, CellStyle style1, CellStyle style3, CellStyle style4, CellStyle style5, int preparedByUnitType) {


        XSSFCell cell0 = positionRow.createCell(0);
        cell0.setCellStyle(style3);
        String type = "type";
        //项目导出时，用"---"动态表现出层级，且单元格无需颜色样式
        if (preparedByUnitType == Constant.PREPARED_BY_UNIT_TYPE_PROJECT) {
            String preCellValue = "---";
            for (int i = 3; i < map.getType(); i++) {
                preCellValue += " ---";
            }
            cell0.setCellValue(preCellValue + map.getBusiness_name()+" "+map.getProjectCode());
            style4 = style1;

        } else {
            cell0.setCellValue("--- ---" + map.getBusiness_name()+" "+map.getProjectCode());
            style4 = style3;
        }
        XSSFCell cell1 = positionRow.createCell(1);
        cell1.setCellStyle(style5);
        if (map.getReserve_can_sell_set() == null) {
            cell1.setCellValue("");
        } else {
            cell1.setCellValue(map.getReserve_can_sell_set());
        }
        XSSFCell cell2 = positionRow.createCell(2);
        cell2.setCellStyle(style4);

        if (map.getReserve_can_sell_funds() == null) {
            cell2.setCellValue("");
        } else {
            cell2.setCellValue(map.getReserve_can_sell_funds());
        }
        XSSFCell cell3 = positionRow.createCell(3);
        cell3.setCellStyle(style5);

        if (map.getNew_reserve_set() == null) {
            cell3.setCellValue("");
        } else {
            cell3.setCellValue(map.getNew_reserve_set());
        }

        XSSFCell cell4 = positionRow.createCell(4);
        cell4.setCellStyle(style4);

        if (map.getNew_reserve_funds() == null) {
            cell4.setCellValue("");
        } else {
            cell4.setCellValue(map.getNew_reserve_funds());
        }


        XSSFCell cell5 = positionRow.createCell(5);
        cell5.setCellStyle(style5);
        String tem = "B" + startRows + "+D" + startRows;
        cell5.setCellFormula("SUM(" + tem + ")");

        XSSFCell cell6 = positionRow.createCell(6);
        cell6.setCellStyle(style4);

        tem = "C" + startRows + "+E" + startRows;
        cell6.setCellFormula("SUM(" + tem + ")");

        XSSFCell cell7 = positionRow.createCell(7);
        cell7.setCellStyle(style4);
        if (map.getYear_check_funds() == null) {
            cell7.setCellValue(0);
        } else {
            cell7.setCellValue(map.getYear_check_funds());
        }

        XSSFCell cell8 = positionRow.createCell(8);
        cell8.setCellStyle(style4);
        if (map.getYear_check_funds_per() == null) {
            cell8.setCellValue("");
        } else {
            cell8.setCellValue(map.getYear_check_funds_per());
        }

        XSSFCell cell9 = positionRow.createCell(9);
        cell9.setCellStyle(style4);
        if (map.getMonths_check_funds() == null) {
            cell9.setCellValue(0);
        } else {
            cell9.setCellValue(map.getMonths_check_funds());
        }

        XSSFCell cell10 = positionRow.createCell(10);
        cell10.setCellStyle(style4);
        if (map.getMonths_check_funds_per() == null) {
            cell10.setCellValue("");
        } else {
            cell10.setCellValue(map.getMonths_check_funds_per());
        }

        XSSFCell cell11 = positionRow.createCell(11);
        cell11.setCellStyle(style4);
        if (map.getYear_plan_sign() == null) {
            cell11.setCellValue("");
        } else {
            cell11.setCellValue(map.getYear_plan_sign());
        }
        XSSFCell cell12 = positionRow.createCell(12);
        cell12.setCellStyle(style4);

        if (map.getYear_grand_total_sign() == null) {
            cell12.setCellValue("");
        } else {
            cell12.setCellValue(map.getYear_grand_total_sign());
        }

        XSSFCell cell13 = positionRow.createCell(13);
        cell13.setCellStyle(style5);
        if (map.getTop_three_month_average_sign_set() == null) {
            cell13.setCellValue("");
        } else {
            cell13.setCellValue(map.getTop_three_month_average_sign_set());
        }
        XSSFCell cell14 = positionRow.createCell(14);
        cell14.setCellStyle(style4);
        if (map.getTop_three_month_average_sign_funds() == null) {
            cell14.setCellValue("");
        } else {
            cell14.setCellValue(map.getTop_three_month_average_sign_funds());
        }
        XSSFCell cell15 = positionRow.createCell(15);
        cell15.setCellStyle(style5);
        if (map.getUpper_moon_sign_set() == null) {
            cell15.setCellValue("");
        } else {
            cell15.setCellValue(map.getUpper_moon_sign_set());
        }
        XSSFCell cell16 = positionRow.createCell(16);
        cell16.setCellStyle(style4);
        if (map.getUpper_moon_sign_funds() == null) {
            cell16.setCellValue("");
        } else {
            cell16.setCellValue(map.getUpper_moon_sign_funds());
        }
        XSSFCell cell17 = positionRow.createCell(17);
        cell17.setCellStyle(style1);
        if (map.getReserve_sign_funds() == null) {
            cell17.setCellValue("");
        } else {
            cell17.setCellValue(map.getReserve_sign_funds());
        }
        XSSFCell cell18 = positionRow.createCell(18);
        cell18.setCellStyle(style1);
        if (map.getNew_sign_funds() == null) {
            cell18.setCellValue("");
        } else {
            cell18.setCellValue(map.getNew_sign_funds());
        }
        XSSFCell cell19 = positionRow.createCell(19);
        cell19.setCellStyle(style4);

        cell19.setCellValue(map.getTotal_sign_funds());
        /**
         * 根据不同的导出类型适应不同的模板
         */
        if (Constant.PREPARED_BY_UNIT_TYPE_REGION == preparedByUnitType) {
            XSSFCell cell20 = positionRow.createCell(20);
            cell20.setCellStyle(style1);
            if (map.getMarketing_promotion_cost() == null) {
                cell20.setCellValue("");
            } else {
                cell20.setCellValue(map.getMarketing_promotion_cost());
            }
        }
        if (Constant.PREPARED_BY_UNIT_TYPE_PROJECT == preparedByUnitType) {
            XSSFCell cell20 = positionRow.createCell(20);
            cell20.setCellStyle(style3);
            if (map.getPlan_subscription_set() == null) {
                cell20.setCellValue("");
            } else {
                cell20.setCellValue(map.getPlan_subscription_set());
            }
            XSSFCell cell21 = positionRow.createCell(21);
            cell21.setCellStyle(style3);
            if (map.getPlan_subscription_funds() == null) {
                cell21.setCellValue("");
            } else {
                cell21.setCellValue(map.getPlan_subscription_funds());
            }
            XSSFCell cell22 = positionRow.createCell(22);
            cell22.setCellStyle(style3);
            if (map.getTop_three_month_average_turnover_rate() == null) {
                cell22.setCellValue("");
            } else {
                cell22.setCellValue(map.getTop_three_month_average_turnover_rate());
            }
            XSSFCell cell23 = positionRow.createCell(23);
            cell23.setCellStyle(style3);

            if (map.getLast_month_turnover_rate() == null) {
                cell23.setCellValue("");
            } else {
                cell23.setCellValue(map.getLast_month_turnover_rate());
            }
            XSSFCell cell24 = positionRow.createCell(24);
            cell24.setCellStyle(style3);
            if (map.getPlan_turnover_rate() == null) {
                cell24.setCellValue("");
            } else {
                cell24.setCellValue(map.getPlan_turnover_rate());
            }


        }
    }


    private void setDataToProjectUpCell(int startRows, XSSFRow positionRow, MonthPlan map, CellStyle style1, CellStyle style3, CellStyle style4, CellStyle style5, CellStyle style6, int preparedByUnitType) {

        XSSFCell cell0 = positionRow.createCell(0);
        cell0.setCellStyle(style3);
        String type = "type";
        //项目导出时，用"---"动态表现出层级，且单元格无需颜色样式
        if (preparedByUnitType == Constant.PREPARED_BY_UNIT_TYPE_PROJECT) {
            String preCellValue = "---";
            for (int i = 3; i < map.getType(); i++) {
                preCellValue += " ---";
            }
            cell0.setCellValue(preCellValue + map.getBusiness_name()+" "+map.getProjectCode());
            style4 = style1;

        } else {
            cell0.setCellValue("--- ---" + map.getBusiness_name()+" "+map.getProjectCode());
            style4 = style3;
        }
        XSSFCell cell1 = positionRow.createCell(1);
        cell1.setCellStyle(style5);
        if (map.getReserve_can_sell_set() == null) {
            cell1.setCellValue("");
        } else {
            cell1.setCellValue(map.getReserve_can_sell_set());
        }
        XSSFCell cell2 = positionRow.createCell(2);
        cell2.setCellStyle(style4);

        if (map.getReserve_can_sell_funds() == null) {
            cell2.setCellValue("");
        } else {
            cell2.setCellValue(map.getReserve_can_sell_funds());
        }
        XSSFCell cell3 = positionRow.createCell(3);
        cell3.setCellStyle(style5);

        if (map.getNew_reserve_set() == null) {
            cell3.setCellValue("");
        } else {
            cell3.setCellValue(map.getNew_reserve_set());
        }

        XSSFCell cell4 = positionRow.createCell(4);
        cell4.setCellStyle(style4);

        if (map.getNew_reserve_funds() == null) {
            cell4.setCellValue("");
        } else {
            cell4.setCellValue(map.getNew_reserve_funds());
        }


        XSSFCell cell5 = positionRow.createCell(5);
        cell5.setCellStyle(style5);
        String tem = "B" + startRows + "+D" + startRows;
        cell5.setCellFormula("SUM(" + tem + ")");
        XSSFCell cell6 = positionRow.createCell(6);
        cell6.setCellStyle(style4);

        tem = "C" + startRows + "+E" + startRows;
        cell6.setCellFormula("SUM(" + tem + ")");


        XSSFCell cell7 = positionRow.createCell(7);
        cell7.setCellStyle(style4);
        if (map.getYear_check_funds() == null) {
            cell7.setCellValue(0);
        } else {
            cell7.setCellValue(map.getYear_check_funds());
        }

        XSSFCell cell8 = positionRow.createCell(8);
        cell8.setCellStyle(style4);
        if (map.getYear_check_funds_per() == null) {
            cell8.setCellValue("");
        } else {
            cell8.setCellValue(map.getYear_check_funds_per());
        }

        XSSFCell cell9 = positionRow.createCell(9);
        cell9.setCellStyle(style4);
        if (map.getMonths_check_funds() == null) {
            cell9.setCellValue(0);
        } else {
            cell9.setCellValue(map.getMonths_check_funds());
        }

        XSSFCell cell10 = positionRow.createCell(10);
        cell10.setCellStyle(style4);
        if (map.getMonths_check_funds_per() == null) {
            cell10.setCellValue("");
        } else {
            cell10.setCellValue(map.getMonths_check_funds_per());
        }


        XSSFCell cell11 = positionRow.createCell(11);
        cell11.setCellStyle(style4);
        if (map.getYear_plan_sign() == null) {
            cell11.setCellValue("");
        } else {
            cell11.setCellValue(map.getYear_plan_sign());
        }
        XSSFCell cell12 = positionRow.createCell(12);
        cell12.setCellStyle(style4);

        if (map.getYear_grand_total_sign() == null) {
            cell12.setCellValue("");
        } else {
            cell12.setCellValue(map.getYear_grand_total_sign());
        }

        XSSFCell cell13 = positionRow.createCell(13);
        cell13.setCellStyle(style5);
        if (map.getTop_three_month_average_sign_set() == null) {
            cell13.setCellValue("");
        } else {
            cell13.setCellValue(map.getTop_three_month_average_sign_set());
        }
        XSSFCell cell14 = positionRow.createCell(14);
        cell14.setCellStyle(style4);
        if (map.getTop_three_month_average_sign_funds() == null) {
            cell14.setCellValue("");
        } else {
            cell14.setCellValue(map.getTop_three_month_average_sign_funds());
        }
        XSSFCell cell15 = positionRow.createCell(15);
        cell15.setCellStyle(style5);
        if (map.getUpper_moon_sign_set() == null) {
            cell15.setCellValue("");
        } else {
            cell15.setCellValue(map.getUpper_moon_sign_set());
        }
        XSSFCell cell16 = positionRow.createCell(16);
        cell16.setCellStyle(style4);
        if (map.getUpper_moon_sign_funds() == null) {
            cell16.setCellValue("");
        } else {
            cell16.setCellValue(map.getUpper_moon_sign_funds());
        }
        XSSFCell cell17 = positionRow.createCell(17);
        cell17.setCellStyle(style1);
        if (map.getReserve_sign_funds() == null) {
            cell17.setCellValue("");
        } else {
            cell17.setCellValue(map.getReserve_sign_funds());
        }
        XSSFCell cell18 = positionRow.createCell(18);
        cell18.setCellStyle(style1);
        if (map.getNew_sign_funds() == null) {
            cell18.setCellValue("");
        } else {
            cell18.setCellValue(map.getNew_sign_funds());
        }
        XSSFCell cell19 = positionRow.createCell(19);
        cell19.setCellStyle(style4);
        tem = "R" + startRows + "+S" + startRows;
        cell19.setCellFormula("SUM(" + tem + ")");


        XSSFCell cell20 = positionRow.createCell(20);
        cell20.setCellStyle(style5);
        if (map.getPlan_subscription_set() == null) {
            cell20.setCellValue("");
        } else {
            cell20.setCellValue(map.getPlan_subscription_set());
        }
        XSSFCell cell21 = positionRow.createCell(21);
        cell21.setCellStyle(style3);
        if (map.getPlan_subscription_funds() == null) {
            cell21.setCellValue("");
        } else {
            cell21.setCellValue(map.getPlan_subscription_funds());
        }
        XSSFCell cell22 = positionRow.createCell(22);
        cell22.setCellStyle(style6);
        if (map.getTop_three_month_average_turnover_rate() == null) {
            cell22.setCellValue("");
        } else {
            cell22.setCellFormula(map.getTop_three_month_average_turnover_rate() * 100 + "&\"%\"");
        }
        XSSFCell cell23 = positionRow.createCell(23);
        cell23.setCellStyle(style6);

        if (map.getLast_month_turnover_rate() == null) {
            cell23.setCellValue("");
        } else {
            cell23.setCellFormula(map.getLast_month_turnover_rate() * 100 + "&\"%\"");
        }
        XSSFCell cell24 = positionRow.createCell(24);
        cell24.setCellStyle(style6);
        if (map.getPlan_turnover_rate() == null) {
            cell24.setCellValue("");
        } else {
            cell24.setCellFormula(map.getPlan_turnover_rate() + "&\"%\"");
        }

        XSSFCell cell25 = positionRow.createCell(25);
        cell25.setCellStyle(style3);
        if (map.getMarketing_promotion_cost() == null) {
            cell25.setCellValue("");
        } else {
            cell25.setCellValue(map.getMarketing_promotion_cost());
        }


    }


    private void setDataToProjectCellSpecial(int startRows, XSSFRow positionRow, MonthPlan map, CellStyle style1, CellStyle style3, CellStyle style4, CellStyle style5, CellStyle style6, int preparedByUnitType) {

        XSSFCell cell0 = positionRow.createCell(0);
        style4.setAlignment(HorizontalAlignment.LEFT);
        style4.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style4.setBorderBottom(BorderStyle.THIN);
        style4.setBorderTop(BorderStyle.THIN);
        style4.setBorderLeft(BorderStyle.THIN);
        style4.setBorderRight(BorderStyle.THIN);

        cell0.setCellStyle(style4);

        // style7.setAlignment(HorizontalAlignment.CENTER);

        String type = "type";
        //项目导出时，用"---"动态表现出层级，且单元格无需颜色样式

        if (preparedByUnitType == Constant.PREPARED_BY_UNIT_TYPE_PROJECT) {
            String preCellValue = "---";
            for (int i = 3; i < (int) map.getType(); i++) {
                preCellValue += "-";
            }
            cell0.setCellValue(preCellValue + map.getBusiness_name()+" "+map.getProjectCode());
            style4 = style1;
            // style4.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        } else {
            cell0.setCellValue("--- ---" + map.getBusiness_name()+" "+map.getProjectCode());
            style4 = style3;
        }
        String tem = null;


        /*若为 项目级，这些不渲染直接取合计的*/
        //  if(preparedByUnitType != Constant.PREPARED_BY_UNIT_TYPE_PROJECT) {
        XSSFCell cell1 = positionRow.createCell(1);
        cell1.setCellStyle(style3);
        if (map.getReserve_can_sell_set() == null) {
            cell1.setCellValue("");
        } else {
            cell1.setCellValue(map.getReserve_can_sell_set());
        }
        XSSFCell cell2 = positionRow.createCell(2);
        cell2.setCellStyle(style3);

        if (map.getReserve_can_sell_funds() == null) {
            cell2.setCellValue("");
        } else {
            cell2.setCellValue(map.getReserve_can_sell_funds());
        }
        XSSFCell cell3 = positionRow.createCell(3);
        cell3.setCellStyle(style5);

        if (map.getNew_reserve_set() == null) {
            cell3.setCellValue("");
        } else {
            cell3.setCellValue(map.getNew_reserve_set());
        }

        XSSFCell cell4 = positionRow.createCell(4);
        cell4.setCellStyle(style3);

        if (map.getNew_reserve_funds() == null) {
            cell4.setCellValue("");
        } else {
            cell4.setCellValue(map.getNew_reserve_funds());
        }


        XSSFCell cell5 = positionRow.createCell(5);
        cell5.setCellStyle(style5);
        tem = "B" + startRows + "+D" + startRows;
        cell5.setCellFormula("SUM(" + tem + ")");
        XSSFCell cell6 = positionRow.createCell(6);
        cell6.setCellStyle(style3);

        tem = "C" + startRows + "+E" + startRows;
        cell6.setCellFormula("SUM(" + tem + ")");


        XSSFCell cell7 = positionRow.createCell(7);
        cell7.setCellStyle(style3);
        if (map.getYear_check_funds() == null) {
            cell7.setCellValue(0);
        } else {
            cell7.setCellValue(map.getYear_check_funds());
        }

        XSSFCell cell8 = positionRow.createCell(8);
        cell8.setCellStyle(style3);
        if (map.getYear_check_funds_per() == null) {
            cell8.setCellValue("");
        } else {
            cell8.setCellValue(map.getYear_check_funds_per());
        }

        XSSFCell cell9 = positionRow.createCell(9);
        cell9.setCellStyle(style3);
        if (map.getMonths_check_funds() == null) {
            cell9.setCellValue(0);
        } else {
            cell9.setCellValue(map.getMonths_check_funds());
        }

        XSSFCell cell10 = positionRow.createCell(10);
        cell10.setCellStyle(style3);
        if (map.getMonths_check_funds_per() == null) {
            cell10.setCellValue("");
        } else {
            cell10.setCellValue(map.getMonths_check_funds_per());
        }


        XSSFCell cell11 = positionRow.createCell(11);
        cell11.setCellStyle(style3);
        if (map.getYear_plan_sign() == null) {
            cell11.setCellValue("");
        } else {
            cell11.setCellValue(map.getYear_plan_sign());
        }
        XSSFCell cell12 = positionRow.createCell(12);
        cell12.setCellStyle(style3);

        if (map.getYear_grand_total_sign() == null) {
            cell12.setCellValue("");
        } else {
            cell12.setCellValue(map.getYear_grand_total_sign());
        }

        XSSFCell cell13 = positionRow.createCell(13);
        cell13.setCellStyle(style5);
        if (map.getTop_three_month_average_sign_set() == null) {
            cell13.setCellValue("");
        } else {
            cell13.setCellValue(map.getTop_three_month_average_sign_set());
        }
        XSSFCell cell14 = positionRow.createCell(14);
        cell14.setCellStyle(style3);
        if (map.getTop_three_month_average_sign_funds() == null) {
            cell14.setCellValue("");
        } else {
            cell14.setCellValue(map.getTop_three_month_average_sign_funds());
        }
        XSSFCell cell15 = positionRow.createCell(15);
        cell15.setCellStyle(style5);
        if (map.getUpper_moon_sign_set() == null) {
            cell15.setCellValue("");
        } else {
            cell15.setCellValue(map.getUpper_moon_sign_set());
        }
        XSSFCell cell16 = positionRow.createCell(16);
        cell16.setCellStyle(style3);
        if (map.getUpper_moon_sign_funds() == null) {
            cell16.setCellValue("");
        } else {
            cell16.setCellValue(map.getUpper_moon_sign_funds());
        }

        if (preparedByUnitType > 6 || map.getFlag() != null) {

            //  style4 = style1;
            style4.setAlignment(HorizontalAlignment.CENTER);
            style4.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            XSSFCell cell17 = positionRow.createCell(17);
            cell17.setCellStyle(style6);
            if (map.getReserve_sign_set() == null) {
                cell17.setCellValue(0);
            } else {
                cell17.setCellValue(map.getReserve_sign_set());
            }


            XSSFCell cell18 = positionRow.createCell(18);
            cell18.setCellStyle(style6);
            if (map.getReserve_sign_funds() == null) {
                cell18.setCellValue("");
            } else {
                cell18.setCellValue(map.getReserve_sign_funds());
            }
            XSSFCell cell19 = positionRow.createCell(19);
            cell19.setCellStyle(style6);
            if (map.getNew_sign_set() == null) {
                cell19.setCellValue(0);

            } else {
                cell19.setCellValue(map.getNew_sign_set());
            }


            XSSFCell cell20 = positionRow.createCell(20);
            cell20.setCellStyle(style6);
            if (map.getNew_sign_funds() == null) {
                cell20.setCellValue("");
            } else {
                cell20.setCellValue(map.getNew_sign_funds());
            }
            XSSFCell cell21 = positionRow.createCell(21);
            cell21.setCellStyle(style5);
            tem = "R" + startRows + "+T" + startRows;
            cell21.setCellFormula("SUM(" + tem + ")");

            XSSFCell cell22 = positionRow.createCell(22);
            cell22.setCellStyle(style3);
            tem = "S" + startRows + "+U" + startRows;
            cell22.setCellFormula("SUM(" + tem + ")");

            XSSFCell cell23 = positionRow.createCell(23);
            cell23.setCellStyle(style6);

            if (map.getPlan_subscription_set() == null) {
                cell23.setCellValue("");
            } else {
                cell23.setCellValue(map.getPlan_subscription_set());
            }
            XSSFCell cell24 = positionRow.createCell(24);
            cell24.setCellStyle(style6);
            if (map.getPlan_subscription_funds() == null) {
                cell24.setCellValue("");
            } else {
                cell24.setCellValue(map.getPlan_subscription_funds());
            }
        }
        style3.setAlignment(HorizontalAlignment.CENTER);
        style3.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        XSSFCell cell25 = positionRow.createCell(25);
        cell25.setCellStyle(style3);
        if (map.getTop_three_month_average_turnover_rate() == null) {
            cell25.setCellValue("");
        } else {
            cell25.setCellFormula(map.getTop_three_month_average_turnover_rate() * 100 + "&\"%\"");
        }
        XSSFCell cell26 = positionRow.createCell(26);
        cell26.setCellStyle(style3);
        if (map.getLast_month_turnover_rate() == null) {
            cell26.setCellValue("");
        } else {
            cell26.setCellFormula(map.getLast_month_turnover_rate() * 100 + "&\"%\"");
        }
        XSSFCell cell27 = positionRow.createCell(27);
        cell27.setCellStyle(style3);
        if (map.getPlan_turnover_rate() == null) {
            cell27.setCellValue("");
        } else {
            cell27.setCellFormula(map.getPlan_turnover_rate() + "&\"%\"");
        }


    }


    @Override
    public void listThreeExport(HttpServletRequest request, HttpServletResponse response, String month, String businessId) throws Exception {
        String planName;
        String basePath;
        String templatePath;
        String realpath;
        String targetFilePath;
        FileInputStream templateInputStream = null;
        FileOutputStream fileOutputStream = null;
        Workbook targetWorkBook;
        XSSFSheet targetSheet;

        planName = "表三月度签约计划模板";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new Date());
        planName += time;

        //本地模板文件路径。
       /* basePath = "/Users/WorkSapce/Java/旭辉集团/Java/marketing-control-api/cifimaster/visolink-sales-api/src/main/webapp/TemplateExcel/list3_issued_indicator_data.xlsx";
        realpath = basePath;*/
        //服务器模版路径
        basePath = request.getServletContext().getRealPath("/");

        templatePath = File.separator + "TemplateExcel" + File.separator + "list3_issued_indicator_data.xlsx";
        realpath = basePath + templatePath;
        // 目标文件路径。
        planName = URLEncoder.encode(planName + ".xlsx", "utf-8").replace("+", "%20");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //设置content-disposition响应头控制浏览器以下载的形式打开文件
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(planName.getBytes(), "utf-8"));

        /*本地用路径*/
        File templateFile = new File(realpath);
//"D:\\资料\\公司\\旭辉\\营销管控\\newCode\\marketing-control-api\\cifimaster\\visolink-sales-api\\src\\main\\webapp\\TemplateExcel\\list3_issued_indicator_data.xlsx"
        if (!templateFile.exists()) {
            templateFile.mkdirs();
            throw new ServiceException("-15_1003", "认购确认导出失败。模板文件不存在");
        }

        //创建输出文档。
        templateInputStream = new FileInputStream(templateFile);
        targetWorkBook = new XSSFWorkbook(templateInputStream);
        targetSheet = (XSSFSheet) targetWorkBook.getSheetAt(0);


        //模板文件中最大行
        int maxTemplateRows = targetSheet.getLastRowNum();
        //清空原模板剩余数据
        for (int i = 4; i <= maxTemplateRows; i++) {
            Row removeRow = targetSheet.getRow(i);

            targetSheet.removeRow(removeRow);
        }
        List<ListThree> groupRowlist = new ArrayList<>();

        /*
         * 存储了所有表三的数据
         * */
        Map map = new HashMap();
        map.put("projectId", businessId);
        map.put("months", month);


        groupRowlist = exportDao.selectMouthChannelDetail(map);
        targetWorkBook.setSheetName(0, "表三");
        //起始行
        int startRows = 4;
        //1a 2b 3c 4d 5e  数字与字符之间的转换
        int toChar = 97;
        Row row0 = targetSheet.getRow(0);

        targetSheet.protectSheet("edit");


        int maxCellNum = row0.getPhysicalNumberOfCells();

        /**
         * 单元格样色共三种：
         * 1.白底 居中加粗 style1
         * 2.灰底 居中 加粗
         * 3.灰底 正常
         * 4.动态判断
         */
        XSSFDataFormat format = (XSSFDataFormat) targetWorkBook.createDataFormat();
        CellStyle style1 = targetWorkBook.createCellStyle();
        CellStyle style2 = targetWorkBook.createCellStyle();
        CellStyle style3 = targetWorkBook.createCellStyle();
        CellStyle style4 = targetWorkBook.createCellStyle();
        CellStyle style5 = targetWorkBook.createCellStyle();

        style2.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style2.setBorderBottom(BorderStyle.THIN);
        style2.setBorderTop(BorderStyle.THIN);
        style2.setBorderLeft(BorderStyle.THIN);
        style2.setBorderRight(BorderStyle.THIN);
        style1.setLocked(false);
        style2.setAlignment(HorizontalAlignment.CENTER);

        style3.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style3.setBorderBottom(BorderStyle.THIN);
        style3.setBorderTop(BorderStyle.THIN);
        style3.setBorderLeft(BorderStyle.THIN);
        style3.setBorderRight(BorderStyle.THIN);

        style5.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style5.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style5.setBorderBottom(BorderStyle.THIN);
        style5.setBorderTop(BorderStyle.THIN);
        style5.setBorderLeft(BorderStyle.THIN);
        style5.setBorderRight(BorderStyle.THIN);


        style3.setDataFormat(format.getFormat("0.00"));
        style4.setDataFormat(format.getFormat("0.00"));
        style4.setLocked(false);

        /*行*/
        XSSFRow row = null;
        //循环遍历所有数据
        /*
         * MATTER比较特殊，单独存储，他要占用多个单元格
         * */
        int matterRows = (startRows + 1);


        for (int i = 0; i <= groupRowlist.size()-1; i++) {

            row = targetSheet.createRow(4 + i);

            if (i == 0) {
                CellRangeAddress regionone = new CellRangeAddress(4, 4, 0, 1);
                targetSheet.addMergedRegion(regionone);
                setDataToListThreeCell(startRows + i, row, null, style1, style2, style3, style4, style5, groupRowlist.size(), format);

            } else if (i != 0) {
                /*去掉合计列*/
               // String getmatter = groupRowlist.get(i - 1).getMatter();
        /*        if ("合计".equals(getmatter)) {
                    continue;
                }*/
                setDataToListThreeCell(startRows + i, row, groupRowlist.get(i - 1), style1, style2, style3, style4, style5, groupRowlist.size(), format);
                /*遍历matterming*/
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(groupRowlist.get(i).getMatter());
                cell0.setCellStyle(style2);
                /*最后一个存储渠道ID来更新*/
                Cell celllast = row.createCell(maxCellNum - 1);
                celllast.setCellValue(groupRowlist.get(i - 1).getChannel_id());
                /*设置mattername,当此mattername和下一个相同的时候，进行渲染，否则进下一个,此处合并*/

                if (i < groupRowlist.size() - 1 && !(groupRowlist.get(i).getMatter().equals(groupRowlist.get(i + 1).getMatter()))) {

                    int matterEnds = startRows + i;

                    CellRangeAddress region = new CellRangeAddress(matterRows, matterEnds + 1, 0, 0);


                    /*计算需要合并的列 */
                    if (matterEnds - matterRows >= 0) {

                        targetSheet.addMergedRegion(region);

                    }


                    matterRows = matterEnds + 2;
                }

            }
        }
        /*立即计算公式*/

        targetSheet.setForceFormulaRecalculation(true);

        targetSheet.setColumnHidden(maxCellNum - 1, true);
        targetSheet.setRowSumsBelow(false);
        targetSheet.createFreezePane(2, 4, 2, 4);


        //页面输出
        targetWorkBook.write(response.getOutputStream());
        //服务器硬盘输出
        //  targetWorkBook.write(fileOutputStream);
        response.getOutputStream().flush();

        if (templateInputStream != null) {
            templateInputStream.close();
        }
        /* if (fileOutputStream != null) {
            fileOutputStream.close();
        } */


    }

    private void setDataToListThreeCell(int startRows, XSSFRow positionRow, ListThree map, CellStyle style1, CellStyle style2, CellStyle style3, CellStyle style4, CellStyle style5, int size, XSSFDataFormat format) {
        XSSFCell cell0 = positionRow.createCell(0);


        String type = "type";
        //MATTERname ，事项
        if (startRows == 4) {
            cell0.setCellStyle(style2);

            cell0.setCellValue("合计");
        }

        //动作
        XSSFCell cell1 = positionRow.createCell(1);
        cell1.setCellStyle(style2);
        if (startRows == 4) {

        } else {
            cell1.setCellValue(map.getAction());
        }
        //认购套数
        XSSFCell cell2 = positionRow.createCell(2);
        cell2.setCellStyle(style1);

        if (startRows == 4) {
            cell2.setCellStyle(style2);
            cell2.setCellFormula("SUM(C" + (startRows + 2) + ":C" + (startRows + size) + ")");
        } else {
            cell2.setCellValue(map.getSubscription_number());
        }
        //认购金额
        XSSFCell cell3 = positionRow.createCell(3);
        cell3.setCellStyle(style4);

        if (startRows == 4) {
            cell3.setCellStyle(style3);
            cell3.setCellFormula("SUM(D" + (startRows + 2) + ":D" + (startRows + size) + ")");
        } else {
            cell3.setCellValue(map.getSubscription_amount());
        }
        //前三月月均
        //成交套数
        XSSFCell cell4 = positionRow.createCell(4);
        cell4.setCellStyle(style5);

        if (startRows == 4) {

            cell4.setCellFormula("SUM(E" + (startRows + 2) + ":E" + (startRows + size) + ")");
        } else {
            cell4.setCellValue(map.getFirst_three_months_average_monthly_sets());
        }


        //成交占比
        XSSFCell cell5 = positionRow.createCell(5);
        cell5.setCellStyle(style2);
        if (startRows == 4) {
            cell5.setCellFormula(100 + "&\"%\"");
        } else {

            cell5.setCellFormula("IFERROR(ROUND(C" + (startRows + 1) + "/C5" + "*10000/100,2),0.0)&\"%\"");
        }
        //来人量
        XSSFCell cell6 = positionRow.createCell(6);
        cell6.setCellStyle(style1);
        if (startRows == 4) {
            cell6.setCellStyle(style2);
            cell6.setCellFormula("SUM(G" + (startRows + 2) + ":G" + (startRows + size) + ")");
        } else {
            cell6.setCellValue(map.getCome_client_quantity());
        }

        //前三月均
        //来人量
        XSSFCell cell7 = positionRow.createCell(7);
        cell7.setCellStyle(style5);
        if (startRows == 4) {
            cell7.setCellFormula("SUM(H" + (startRows + 2) + ":H" + (startRows + size) + ")");
        } else {
            cell7.setCellValue(map.getFirst_three_months_monthly_average_monthly_coming_amount());
        }
        // 来人占比
        XSSFCell cell8 = positionRow.createCell(8);
        cell8.setCellStyle(style2);

        if (startRows == 4) {
            cell8.setCellFormula(100 + "&\"%\"");
        } else {

            cell8.setCellFormula("IFERROR(ROUND(G" + (startRows + 1) + "/G5" + "*10000/100,2),0.0)&\"%\"");
        }
        //前三月月均
        //      成交率
        XSSFCell cell9 = positionRow.createCell(9);
        cell9.setCellStyle(style2);
        if (startRows == 4) {
            cell9.setCellFormula("IFERROR(ROUND(SUM(E" + (startRows + 2) + ":E" + (startRows + size) + ")" + "/" + "SUM(H" + (startRows + 2) + ":H" + (startRows + size) + ")*10000/100,2),0.0)&\"%\"");
        } else {
            cell9.setCellValue(map.getFirst_three_months_monthly_average_turnover_rate() + "%");
        }
        // 成交率
        XSSFCell cell10 = positionRow.createCell(10);
        cell10.setCellStyle(style2);
        String tem = "ROUND(IF(AND(C" + (startRows + 1) + "<>0,G" + (startRows + 1) + "<>0)," + "C" + (startRows + 1) + "/" + "G" + (startRows + 1) + ",0.0)*10000/100,2)&\"%\"";
        if (startRows == 4) {
            cell10.setCellFormula("ROUND(IF(AND(SUM(C" + (startRows + 2) + ":C" + (startRows + size) + ")<>0," + "SUM(G" + (startRows + 2) + ":G" + (startRows + size) + ")<>0)," + "SUM(C" + (startRows + 2) + ":C" + (startRows + size) + ")" + "/" + "SUM(G" + (startRows + 2) + ":G" + (startRows + size) + ")" + ",0.0)*10000/100,2)&\"%\"");
        } else {
            cell10.setCellFormula(tem);
        }

        //合同金额
        XSSFCell cell11 = positionRow.createCell(11);
        cell11.setCellStyle(style4);
        if (startRows == 4) {
            cell11.setCellStyle(style3);
            cell11.setCellFormula("SUM(L" + (startRows + 2) + ":L" + (startRows + size) + ")");
        } else {
            cell11.setCellValue(map.getContract_amount());
        }
        //
        XSSFCell cell12 = positionRow.createCell(12);
        cell12.setCellStyle(style4);
        if (startRows == 4) {
            cell12.setCellStyle(style3);
            cell12.setCellFormula("SUM(M" + (startRows + 2) + ":M" + (startRows + size) + ")");
        } else {
            cell12.setCellValue(map.getRight_responsibility_amount());
        }
        XSSFCell cell13 = positionRow.createCell(13);
        cell13.setCellStyle(style3);
        if (startRows == 4) {
            cell13.setCellFormula("SUM(N" + (startRows + 2) + ":N" + (startRows + size) + ")/19");
        } else {
            cell13.setCellValue(map.getFirst_three_months_average_monthly_transaction_cost() / 10000);
        }
        XSSFCell cell14 = positionRow.createCell(14);
        cell14.setCellStyle(style3);
        if (startRows == 4) {
            cell14.setCellFormula("IF(AND(SUM(M" + (startRows + 2) + ":M" + (startRows + size) + ")<>0," + "SUM(C" + (startRows + 2) + ":C" + (startRows + size) + ")<>0)," + "SUM(M" + (startRows + 2) + ":M" + (startRows + size) + ")" + "/" + "SUM(C" + (startRows + 2) + ":C" + (startRows + size) + ")" + ",0)");
        } else {
            cell14.setCellFormula("IF(AND(M" + (startRows + 1) + "<>0,C" + (startRows + 1) + "<>0)," + "M" + (startRows + 1) + "/" + "C" + (startRows + 1) + ",0)");
        }
        XSSFCell cell15 = positionRow.createCell(15);
        cell15.setCellStyle(style3);
        if (startRows == 4) {
            cell15.setCellFormula("SUM(P" + (startRows + 2) + ":P" + (startRows + size) + ")/19");
        } else {
            cell15.setCellValue(map.getFirst_three_months_monthly_average_coming_cost() / 10000);
        }

        XSSFCell cell16 = positionRow.createCell(16);
        cell16.setCellStyle(style3);
        if (startRows == 4) {
            cell16.setCellFormula("IF(AND(SUM(M" + (startRows + 2) + ":M" + (startRows + size) + ")<>0," + "SUM(G" + (startRows + 2) + ":G" + (startRows + size) + ")<>0)," + "SUM(M" + (startRows + 2) + ":M" + (startRows + size) + ")" + "/" + "SUM(G" + (startRows + 2) + ":G" + (startRows + size) + ")" + ",0)");
        } else {
            tem = "IF(AND(M" + (startRows + 1) + "<>0,G" + (startRows + 1) + "<>0)," + "M" + (startRows + 1) + "/" + "G" + (startRows + 1) + ",0)";

            cell16.setCellFormula(tem);

        }

    }

    /*表四导出*/
    @Override
    public void listFourExport(HttpServletRequest request, HttpServletResponse response, String month, String businessId) throws IOException, ServiceException {
        String planName;
        String basePath;
        String templatePath;
        String targetFileDir;
        String targetFilePath;
        FileInputStream templateInputStream = null;
        FileOutputStream fileOutputStream = null;
        Workbook targetWorkBook;
        XSSFSheet targetSheet;

        planName = "表四月度签约计划模板";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new Date());
        planName += time;


//            basePath = "E:/xuhui/marketing-control-api/cifimaster/visolink-sales-api/src";
        basePath = request.getServletContext().getRealPath("/");


        //模板文件路径。

        templatePath = File.separator + "TemplateExcel" + File.separator + "list4_issued_indicator_data.xlsx";

        //导出临时文件文件夹。
        targetFileDir = "Uploads" + File.separator + "DownLoadTemporaryFiles";
        // 目标文件路径。
        targetFilePath = targetFileDir + File.separator + planName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
        planName = URLEncoder.encode(planName + ".xlsx", "utf-8").replace("+", "%20");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //设置content-disposition响应头控制浏览器以下载的形式打开文件
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(planName.getBytes(), "utf-8"));

        //验证模板文件是否存在
        //  String  realpath= this.getClass().getResource("/").getPath()  ;

        //  realpath=realpath.substring(0,realpath.indexOf("/target"))+File.separator+"src"+File.separator+"main"+File.separator+"webapp"+templatePath;

        /*本地用路径*/
        //   File templateFile = new File(realpath);
        templatePath = basePath + templatePath;
        File templateFile = new File(templatePath);

        if (!templateFile.exists()) {
            templateFile.mkdirs();
            throw new ServiceException("-15_1003", "认购确认导出失败。模板文件不存在");
        }
        //验证目标文件夹是否存在
        File targetFileDirFile = new File(targetFileDir);
        if (!targetFileDirFile.exists()) {
            targetFileDirFile.mkdirs();
        }
        //创建输出文档。
        templateInputStream = new FileInputStream(templateFile);
        targetWorkBook = new XSSFWorkbook(templateInputStream);
        targetSheet = (XSSFSheet) targetWorkBook.getSheetAt(0);


        //模板文件中最大行
        int maxTemplateRows = targetSheet.getLastRowNum();


        List<MonthFour> groupRowlist = new ArrayList<>();

        /*
         * 存储了所有表si的数据
         * */
        Map<String, Object> groupRowlistMap = new HashMap<>();
        groupRowlistMap.put("projectId", businessId);
        groupRowlistMap.put("months", month);
        groupRowlist = exportDao.selectWeeklyPlan(groupRowlistMap);

        MonthFour groupRowTwo = exportDao.mouthPlanSelect(groupRowlistMap);

        targetWorkBook.setSheetName(0, "表四");
        //起始行
        int startRows = 2;
        //1a 2b 3c 4d 5e  数字与字符之间的转换

        Row row0 = targetSheet.getRow(0);
        Row row2 = targetSheet.getRow(1);
        int maxCellNum = row0.getPhysicalNumberOfCells();

        /**
         * 单元格样色共三种：
         * 1.白底 居中加粗 style1
         * 2.灰底 居中 加粗
         * 3.灰底 正常
         * 4.动态判断
         */
        XSSFDataFormat format = (XSSFDataFormat) targetWorkBook.createDataFormat();
        CellStyle style1 = row0.getCell(0).getCellStyle();
        CellStyle style2 = row2.getCell(2).getCellStyle();

        CellStyle style3 = targetWorkBook.createCellStyle();
        CellStyle style4 = targetWorkBook.createCellStyle();
        CellStyle style5 = targetWorkBook.createCellStyle();

        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style2.setBorderTop(BorderStyle.THIN);
        style2.setAlignment(HorizontalAlignment.CENTER);

        //  style3.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        //  style3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style3.setBorderBottom(BorderStyle.THIN);
        style3.setBorderTop(BorderStyle.THIN);
        style3.setBorderLeft(BorderStyle.THIN);
        style3.setBorderRight(BorderStyle.THIN);


        style5.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style5.setBorderBottom(BorderStyle.THIN);
        style5.setBorderTop(BorderStyle.THIN);
        style5.setBorderLeft(BorderStyle.THIN);
        style5.setBorderRight(BorderStyle.THIN);

        style4.setBorderBottom(BorderStyle.THIN);
        style4.setBorderTop(BorderStyle.THIN);
        style4.setBorderLeft(BorderStyle.THIN);
        style4.setBorderRight(BorderStyle.THIN);
        style3.setAlignment(HorizontalAlignment.CENTER);
        style4.setAlignment(HorizontalAlignment.CENTER);
        style3.setAlignment(HorizontalAlignment.RIGHT);
        style4.setAlignment(HorizontalAlignment.RIGHT);

        style3.setDataFormat(format.getFormat("0.00"));
        //  style4.setDataFormat(format.getFormat("0.00"));
        style4.setLocked(false);

        /*行*/
        XSSFRow row = targetSheet.createRow(2);
        //先存月度的
        /*锁表*/
        //   targetSheet.protectSheet("edit");
        /*第一行第一段要写标题名*/
        String projectName = monthManagerService.selectProjectName(businessId);
        String projectMonth = month.substring(month.indexOf("-") + 1, month.indexOf("-") + 3);
        Cell titleCell = row0.createCell(0);
        titleCell.setCellValue(projectName + "项目" + projectMonth + "月份周度计划");
        titleCell.setCellStyle(style1);
        System.out.println();
        setDataToListFourCell(startRows, row, groupRowTwo, style5, style2, style3, style4, null);
        row.setHeight((short) (29.25 * 20));  //行高
        startRows = startRows + 2;
        for (int i = 0; i < groupRowlist.size(); i++) {

            Map maprange = new HashMap();
            String rangeMonth = groupRowlist.get(i).getMonths().substring(0, groupRowlist.get(i).getMonths().lastIndexOf("-"));
            maprange.put("start_time", rangeMonth);
            maprange.put("how_week", groupRowlist.get(i).getWeek_serial_number());
            // Map<String,Object> timeRange=  exportDao.timeRangeSelect(maprange);
            Map<String, Object> timeRange = exportDao.selectMonthWeek(maprange);
            System.out.println(timeRange.toString());
            /*找到当月的时间范围里有几个周，有的有4周有的有5周*/
            if (timeRange != null && timeRange.size() > 0) {
                String timeover = timeRange.get("start_time") + "-" + timeRange.get("end_time");

                row = targetSheet.createRow(startRows + i);
                row.setHeight((short) (29.25 * 20));

                setDataToListFourCell(startRows + i, row, groupRowlist.get(i), style5, style2, style3, style4, timeover);
            }
        }
        /*立即计算公式*/

        targetSheet.setForceFormulaRecalculation(true);

        //  targetSheet.setColumnHidden(maxCellNum, true);
        targetSheet.setRowSumsBelow(false);
        // targetSheet.createFreezePane(2, 4,2,4);

        try {
            fileOutputStream = new FileOutputStream(targetFilePath);
            //页面输出
            targetWorkBook.write(response.getOutputStream());
            //服务器硬盘输出
            // targetWorkBook.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if(templateInputStream!=null){
                try {
                    templateInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileOutputStream!=null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setDataToListFourCell(int startRows, XSSFRow positionRow, MonthFour map, CellStyle style5, CellStyle style2, CellStyle style3, CellStyle style4, String timeRange) {
        if (startRows == 2) {
            XSSFCell cell0 = positionRow.createCell(0);
            cell0.setCellStyle(style2);


        }
        //时间范围
        XSSFCell cell1 = positionRow.createCell(1);
        cell1.setCellStyle(style2);

        cell1.setCellValue(timeRange);

        // 来访量
        XSSFCell cell2 = positionRow.createCell(2);
        cell2.setCellStyle(style4);
        if (startRows == 2) {
            cell2.setCellValue(map.getCome_client_quantity());

        } else {
            /*第几周*/
            XSSFCell cell0 = positionRow.createCell(0);

            cell0.setCellValue(map.getHowWeek());
            cell0.setCellStyle(style2);

            cell2.setCellValue(map.getVisit_quantity());
        }
        //小卡
        XSSFCell cell3 = positionRow.createCell(3);
        cell3.setCellStyle(style4);


        cell3.setCellValue(map.getSmall_card());


        //打卡
        XSSFCell cell4 = positionRow.createCell(4);
        cell4.setCellStyle(style4);


        cell4.setCellValue(map.getBig_card());


        //认购套数
        XSSFCell cell5 = positionRow.createCell(5);
        cell5.setCellStyle(style4);
        if (startRows == 2) {
            cell5.setCellValue(map.getSubscription_number());

        } else {
            cell5.setCellValue(map.getSubscription_number_set());
        }
        //签约套数
        XSSFCell cell6 = positionRow.createCell(6);
        cell6.setCellStyle(style4);

        cell6.setCellValue(map.getSign_number_set());


        //签约目标
        XSSFCell cell7 = positionRow.createCell(7);
        cell7.setCellStyle(style3);
        if (startRows == 2) {
            cell7.setCellValue(map.getSign_funds());

        } else {
            cell7.setCellValue(map.getSign_target());
        }
        if (startRows != 2) {
            // 动作分解
            XSSFCell cell8 = positionRow.createCell(8);
            cell8.setCellStyle(style5);
            cell8.setCellValue("");
            if (map.getCore_action() != null) {
                cell8.setCellValue(map.getCore_action());
            }
        }
        if (startRows == 2) {

            XSSFCell cell8 = positionRow.createCell(8);
            cell8.setCellStyle(style5);
            cell8.setCellValue("");
            if (map.getRisk_point() != null) {
                cell8.setCellValue(map.getRisk_point());
            }
            //      风险点
            XSSFCell cell9 = positionRow.createCell(9);
            cell9.setCellStyle(style5);
            cell9.setCellValue("");
            if (map.getCountermeasures() != null) {
                cell9.setCellValue(map.getCountermeasures());
            }
            // 对策
            XSSFCell cell10 = positionRow.createCell(10);
            cell10.setCellStyle(style5);
            cell10.setCellValue("");
            if (map.getPolicy_use() != null) {
                cell10.setCellValue(map.getPolicy_use());
            }

            //政策使用
            XSSFCell cell11 = positionRow.createCell(11);
            cell11.setCellStyle(style5);
            cell11.setCellValue("");
            if (map.getCore_action() != null) {
                cell11.setCellValue(map.getCore_action());
            }


            //核心动作


        }


    }

    /*获取来自数据湖的数据*/
    public List<MonthPlan> getDataLake(List<MonthPlan> result, String months) {

        /*截取到年月*/
        months = months.substring(0, months.lastIndexOf("-"));
        /*换成符合要求的字符串*/
        months = months.replace("-", "");
        String createTimeriskresult = HttpRequestUtil.httpGet(prcDimProjGoal + "?params=" + months + "&apikey=" + prcDimProjapikey, false);
        System.out.println(prcDimProjGoal + "?params=" + months + "&apikey=" + prcDimProjapikey + "-----prcDimProjapikey");
        Gson gson = new Gson();
        Map<String, Object> GsonMap = new HashMap();
        GsonMap = gson.fromJson((createTimeriskresult + ""), GsonMap.getClass());
        System.out.println(GsonMap + "GsonMap");

        /*得到所有来自数据湖的数据，然后遍历到返回值里*/
        List<Map> AllProject = (List<Map>) GsonMap.get("retData");
        DecimalFormat df = new DecimalFormat("#.00");
        if (result != null && result.size() > 0 && AllProject != null && AllProject.size() > 0) {

            for (MonthPlan map1 : result) {

                String businessId = map1.getBusiness_id();
                for (Map map2 : AllProject) {
                    if (businessId.equals(map2.get("idmProjId") + "")) {

                        map1.setYear_check_funds(map2.get("cntrtAmtBudgetY") == null ? 0 : Double.parseDouble(df.format(Double.parseDouble(map2.get("cntrtAmtBudgetY") + "") / 10000)));
                        map1.setMonths_check_funds(map2.get("cntrtAmtBudgetMAccu") == null ? 0 : Double.parseDouble(df.format(Double.parseDouble(map2.get("cntrtAmtBudgetMAccu") + "") / 10000)));

                        map1.setYear_check_funds_per(Double.parseDouble(map2.get("cntrtAmtBudgetY") + "") == 0 ? null : Double.parseDouble(df.format(map1.getYear_grand_total_sign() / (Double.parseDouble(map2.get("cntrtAmtBudgetY") + "") / 10000))) * 100);
                        map1.setMonths_check_funds_per(Double.parseDouble(map2.get("cntrtAmtBudgetMAccu") + "") == 0 ? null : Double.parseDouble(df.format(map1.getYear_grand_total_sign() / (Double.parseDouble(map2.get("cntrtAmtBudgetMAccu") + "") / 10000))) * 100);
                    }

                }
            }
            for (MonthPlan map1 : result) {
                if (map1.getType() == 2) {

                    Double region_year_check_funds = 0.00;
                    Double region_months_check_funds = 0.00;
                    for (MonthPlan map2 : result) {
                        if ((map2.getFather_id()).equals(map1.getGuid())) {

                            region_year_check_funds += map2.getYear_check_funds();
                            region_months_check_funds += map2.getMonths_check_funds();

                        }
                    }

                    map1.setYear_check_funds(region_year_check_funds);
                    map1.setMonths_check_funds(region_months_check_funds);
                    map1.setYear_check_funds_per(region_year_check_funds == 0 ? null : Double.parseDouble(df.format(map1.getYear_grand_total_sign() / region_year_check_funds)) * 100);
                    map1.setMonths_check_funds_per(region_months_check_funds == 0 ? null : Double.parseDouble(df.format(map1.getYear_grand_total_sign() / region_months_check_funds)) * 100);

                }
            }

            for (MonthPlan map1 : result) {
                if (map1.getType() == 1) {

                    Double region_year_check_funds = 0.00;
                    Double region_months_check_funds = 0.00;
                    for (MonthPlan map2 : result) {
                        if ((map2.getFather_id()).equals(map1.getGuid())) {

                            region_year_check_funds += map2.getYear_check_funds();
                            region_months_check_funds += map2.getMonths_check_funds();

                        }
                    }

                    map1.setYear_check_funds(region_year_check_funds);
                    map1.setMonths_check_funds(region_months_check_funds);
                    map1.setYear_check_funds_per(region_year_check_funds == 0 ? null : Double.parseDouble(df.format(map1.getYear_grand_total_sign() / region_year_check_funds)) * 100);
                    map1.setMonths_check_funds_per(region_months_check_funds == 0 ? null : Double.parseDouble(df.format(map1.getYear_grand_total_sign() / region_months_check_funds)) * 100);

                }
            }

        }
        return result;
    }

}


