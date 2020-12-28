package cn.visolink.firstplan.buildbigprice.service.impl;
import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.buildbigprice.dao.BuildBigPriceDao;
import cn.visolink.firstplan.buildbigprice.service.BuildBigPriceService;
import cn.visolink.firstplan.fpdesigntwo.service.DesignTwoIndexService;
import cn.visolink.salesmanage.fileupload.service.UploadService;
import cn.visolink.utils.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author sjl
 * @Created date 2020/3/16 4:45 下午
 */
@Service
@Transactional
public class BuildBigPriceServiceImpl implements BuildBigPriceService {

    @Autowired
    private UploadService uploadService;
    @Autowired
    private BuildBigPriceDao buildBigPriceDao;
    @Autowired
    private DesignTwoIndexService designTwoIndexService;

    @Value(("${uploadPath}"))
    private String uploadPath;
    @Value(("${relepath}"))
    private  String relepath;
    @Override
    public ResultBody exportExcelTemplate(HttpServletRequest request, HttpServletResponse response, Map map) {
        VisolinkResultBody<Object> resultBody = new VisolinkResultBody<>();
        //定义模版路径

        //服务器模版读取
       String filePath;
        String realpath = request.getServletContext().getRealPath("/");

        //导出模版路径
        String templatePath = File.separator + "TemplateExcel" + File.separator + "buildBigPriceTemplate.xlsx";
        filePath = realpath + templatePath;
        File templateFile = new File(filePath);
        if (!templateFile.exists()) {
            throw new BadRequestException(1001, "未读取到配置的导出模版，请先配置导出模版!");
        }

        FileInputStream fileInputStream = null;
        try {
            //使用poi读取模版文件
            fileInputStream = new FileInputStream(filePath);
            if (fileInputStream == null) {
                throw new BadRequestException(1001, "未读取到模版文件!");
            }

            //定义模版路径 本地模版读取

/*
      String filePath;
        String realpath = this.getClass().getResource("/").getPath();

        //导出模版路径
        String templatePath = File.separator + "TemplateExcel" + File.separator + "buildBigPriceTemplate.xlsx";
        realpath = realpath.substring(0, realpath.indexOf("/target")) + File.separator + "src" + File.separator + "main" + File.separator + "webapp" + templatePath;
        realpath="/Users/WorkSapce/Java/旭辉集团/Java/marketing-control-api/cifimaster/visolink-sales-api/src/main/webapp/TemplateExcel/buildBigPriceTemplate.xlsx";
        File templateFile = new File(realpath);
        System.out.println(realpath);
        if (!templateFile.exists()) {
            throw new BadRequestException(1001, "未读取到配置的导出模版，请先配置导出模版!");
        }
         FileInputStream fileInputStream = null;
        try {
            //使用poi读取模版文件
            fileInputStream = new FileInputStream(templateFile);
            if (fileInputStream == null) {
                throw new BadRequestException(1001, "未读取到模版文件!");
            }*/
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            //获取sheet页
            XSSFSheet sheetAt = workbook.getSheetAt(0);
            //从第二行开始创建
            Map projectMap = (Map) map.get("projectData");
            String project_name = projectMap.get("project_name") + "";
            //获取分期数据
            List<String> data = (List<String>) map.get("buildData");
            List<Map> listbuild = new ArrayList<>();
            //根据楼栋id查询楼栋详细数据
            for (String str : data) {
                String replace = str.replace(" ", "+");
                Map buildInfo = buildBigPriceDao.getBuildInfo(replace);
                if (buildInfo != null) {
                    listbuild.add(buildInfo);
                }

            }
            XSSFRow row = sheetAt.createRow(3);
            XSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) 12);

            //解锁样式
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER); // 居中
            font.setBold(true);//设置是否加粗
            cellStyle.setFont(font);
            cellStyle.setLocked(false);

            //时间格式
            CellStyle dateCellStyle = workbook.createCellStyle();
            XSSFDataFormat format = workbook.createDataFormat();
            dateCellStyle.setDataFormat(format.getFormat("yyyy年m月d日"));
            dateCellStyle.setAlignment(HorizontalAlignment.CENTER); // 居中
            font.setBold(true);//设置是否加粗
            dateCellStyle.setFont(font);
            dateCellStyle.setLocked(false);


            //锁定单元格
            CellStyle cellStylelock = workbook.createCellStyle();
            cellStylelock.setLocked(true);
            font.setBold(true);//设置是否加粗
            cellStylelock.setFont(font);

            short index = IndexedColors.GREY_25_PERCENT.getIndex();
            cellStylelock.setFillForegroundColor(index);
            cellStylelock.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            //将分期数据写入excel
            row.setHeightInPoints(40);
            row.setRowStyle(cellStyle);
            //将业态数据写入excel
            int count = 3;
            int s = 3;
            String gs1 = "";
            String gs2 = "";
            String gs3 = "";
            String gs4 = "";
            CellRangeAddress region;


            for (int j = s; j < listbuild.size() + 3; j++) {
                XSSFRow rows2;
                if (j == 3) {
                    rows2 = row;
                } else {
                    rows2 = sheetAt.createRow(j);
                }
                rows2.createCell(0).setCellValue(listbuild.get(j - 3).get("projectName") + "");
                rows2.createCell(1).setCellValue(listbuild.get(j - 3).get("stageName") + "");
                rows2.createCell(2).setCellValue(listbuild.get(j - 3).get("productType") + "");
                rows2.createCell(3).setCellValue(listbuild.get(j - 3).get("buildingName") + "");
                XSSFCell cell4 = rows2.createCell(4);
                //解锁
                cell4.setCellStyle(cellStyle);

                XSSFCell cell5 = rows2.createCell(5);
                cell5.setCellStyle(cellStyle);

                String gl = "SUM(E" + (j + 1) + "*F" + (j + 1) + ")";
                XSSFCell cell6 = rows2.createCell(6);
                cell6.setCellFormula(gl);
                cell6.setCellStyle(cellStylelock);

                XSSFCell cell7 = rows2.createCell(7);
                cell7.setCellStyle(cellStyle);


                String il = "SUM(G" + (j + 1) + "*" + "H" + (j + 1) + ")";
                XSSFCell cell8 = rows2.createCell(8);
                cell8.setCellFormula(il);
                cell8.setCellStyle(cellStylelock);
                //解锁
                XSSFCell cell9 = rows2.createCell(9);
                cell9.setCellStyle(dateCellStyle);

                //解锁
                XSSFCell cell10 = rows2.createCell(10);
                cell10.setCellStyle(cellStyle);

                //加锁
                String ll = "SUM(I" + (j + 1) + "*" + "K" + (j + 1) + ")";
                XSSFCell cell11 = rows2.createCell(11);
                cell11.setCellFormula(ll);
                cell11.setCellStyle(cellStylelock);

                //解锁
                XSSFCell cell12 = rows2.createCell(12);
                cell12.setCellStyle(cellStyle);

                String nl = "SUM(AA" + (j + 1) + ")";
                XSSFCell cell13 = rows2.createCell(13);
                cell13.setCellFormula(nl);
                cell13.setCellStyle(cellStylelock);
                String ol = "SUM(I" + (j + 1) + "*" + "M" + (j + 1) + ")";
                XSSFCell cell14 = rows2.createCell(14);
                cell14.setCellFormula(ol);
                cell14.setCellStyle(cellStylelock);
                String ss;
                String pl = "SUM(O" + (j + 1) + "/AB" + (j + 1) + "-1)";
                ss = "IFERROR(" + pl + ",0)";
                XSSFCell cell15 = rows2.createCell(15);
                cell15.setCellFormula(ss);
                cell15.setCellStyle(cellStylelock);
                String ql = "SUM(N" + (j + 1) + "*O" + (j + 1) + "/10000)";
                ss = "IFERROR(" + ql + ",0)";
                XSSFCell cell16 = rows2.createCell(16);
                cell16.setCellFormula(ss);
                cell16.setCellStyle(cellStylelock);

                String rl = "SUM(Q" + (j + 1) + "-AC" + (j + 1) + ")";
                ss = "IFERROR(" + rl + ",0)";

                XSSFCell cell17 = rows2.createCell(17);
                cell17.setCellFormula(ss);
                cell17.setCellStyle(cellStylelock);
                String sl = "SUM(O" + (j + 1) + ")";
                XSSFCell cell18 = rows2.createCell(18);
                cell18.setCellFormula(sl);
                cell18.setCellStyle(cellStylelock);

                String tl = "SUM(Q" + (j + 1) + ")";
                ss = "IFERROR(" + tl + ",0)";
                XSSFCell cell19 = rows2.createCell(19);
                cell19.setCellFormula(ss);
                cell19.setCellStyle(cellStylelock);


                //解锁
                XSSFCell cell20 = rows2.createCell(20);
                cell20.setCellStyle(cellStyle);


                //解锁
                XSSFCell cell21 = rows2.createCell(21);
                cell21.setCellStyle(cellStyle);
                //解锁
                XSSFCell cell22 = rows2.createCell(22);
                cell22.setCellStyle(cellStyle);

                String xl = "SUM(AA" + (j + 1) + "-U" + (j + 1) + ")";
                XSSFCell cell23 = rows2.createCell(23);
                cell23.setCellFormula(xl);
                cell23.setCellStyle(cellStylelock);

                //解锁
                XSSFCell cell24 = rows2.createCell(24);
                cell24.setCellStyle(cellStyle);

                String zl = "SUM(AC" + (j + 1) + "-W" + (j + 1) + ")";
                ss = "IFERROR(" + zl + ",0)";
                XSSFCell cell25 = rows2.createCell(25);
                cell25.setCellFormula(ss);
                cell25.setCellStyle(cellStylelock);


                //解锁
                XSSFCell cell26 = rows2.createCell(26);
                cell26.setCellStyle(cellStyle);

                //解锁
                XSSFCell cell27 = rows2.createCell(27);
                cell27.setCellStyle(cellStyle);
                //加锁
                String acl = "SUM(AA" + (j + 1) + "*AB" + (j + 1) + "/10000)";
                ss = "IFERROR(" + acl + ",0)";
                XSSFCell cell28 = rows2.createCell(28);
                cell28.setCellFormula(ss);
                cell28.setCellStyle(cellStylelock);
                //解锁
                XSSFCell cell29 = rows2.createCell(29);
                cell29.setCellStyle(cellStyle);

                rows2.setHeight((short) (25 * 20));
                gs1 += "T" + (j + 1) + "+";
                gs2 += "Z" + (j + 1) + "+";
                gs3 += "AA" + (j + 1) + "+";
                gs4 += "AC" + (j + 1) + "+";

            }

            count = listbuild.size() + 3;

            //添加合计
            XSSFRow row7 = sheetAt.createRow(count);

            row7.createCell(3).setCellValue("合计");
            gs1 = gs1.substring(0, gs1.length() - 1);
            gs2 = gs2.substring(0, gs2.length() - 1);
            gs3 = gs3.substring(0, gs3.length() - 1);
            gs4 = gs4.substring(0, gs4.length() - 1);

            XSSFCell cell19 = row7.createCell(19);
            cell19.setCellFormula("SUM(" + gs1 + ")");
            cell19.setCellStyle(cellStylelock);

            XSSFCell cell25 = row7.createCell(25);
            cell25.setCellFormula("SUM(" + gs2 + ")");
            cell25.setCellStyle(cellStylelock);
            XSSFCell cell26 = row7.createCell(26);
            cell26.setCellFormula("SUM(" + gs3 + ")");
            cell26.setCellStyle(cellStylelock);
            XSSFCell cell28 = row7.createCell(28);


            /**
             * 添加系统标识代码块
             * <--->开始</--->
             */
            XSSFFont font2 = workbook.createFont();
            font2.setFontHeightInPoints((short) 12);
            //锁定单元格
            CellStyle cellStylelock2 = workbook.createCellStyle();
            cellStylelock2.setLocked(true);
            font2.setBold(true);//设置是否加粗
            font2.setColor(HSSFColor.RED.index);
            cellStylelock2.setFont(font2);
            XSSFRow row8 = sheetAt.createRow(count+1);

            XSSFCell cellbs = row8.createCell(0);
            cellbs.setCellValue("系统标识:营销管理");
            cellbs.setCellStyle(cellStylelock2);
//<--->结束</--->
            CellRangeAddress region2 = new CellRangeAddress(count+1, count+1, 0, 1);
            sheetAt.addMergedRegion(region2);
            cell28.setCellFormula("SUM(" + gs4 + ")");
            cell28.setCellStyle(cellStylelock);
            sheetAt.setForceFormulaRecalculation(true);

            row7.setHeight((short) (25 * 20));
            //设置列宽
            for (int i = 0; i < count; i++) {
                //column width is set in units of 1/256th of a character width
                sheetAt.setColumnWidth(i, 256 * 15);
            }
            //设置工作表保护密码，不准更改
            sheetAt.protectSheet("123456");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String formatDate = sdf.format(new Date());
            String fileName = "楼栋大定价模版文件" + formatDate + ".xlsx";
            response.setContentType("application/vnd.ms-excel;charset=utf-8");

            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
            System.out.println(response.getOutputStream());
            //服务器保存导出文件文件夹
            String fileSavepath="buildBigprice";
            //服务器文件夹地址
            String dicoryPaths=uploadPath+"/"+fileSavepath;

            //创建文件夹
            File saveFile = new File(dicoryPaths);
            if (!saveFile.exists()) {
                saveFile.mkdirs();
            }
            //将导出的文件保存到服务器
            String filePaths=dicoryPaths+"/"+fileName;
            FileOutputStream fileOutputStream = new FileOutputStream(filePaths);
            workbook.write(fileOutputStream);

            Map<Object, Object> resultMap = new HashMap<>();
            resultMap.put("fileUrl", relepath+"/"+fileSavepath+"/"+fileName+"?n="+fileName);
            return ResultBody.success(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(1001, "读取模版文件失败!");
        }
    }

    @Override
    public VisolinkResultBody getProjectStages(Map map) {
        VisolinkResultBody<Object> visolinkResultBody = new VisolinkResultBody<>();
        String project_id = map.get("project_id") + "";
        if ("null".equals(project_id)) {
            visolinkResultBody.setCode(400);
            visolinkResultBody.setMessages("项目分期数据获取失败,未获取到项目ID");
            return visolinkResultBody;
        }
        try {
            //获取项目分期数据
            List<Map> stagesData = buildBigPriceDao.getProjectStagesData(project_id);
            Map<Object, Object> paramMap = new HashMap<>();
            for (Map stagesDatum : stagesData) {
                //根据分期id查询业态数据
                /*List<Map> productData = buildBigPriceDao.getProductDataaByProjectFid(projectFID);
                stagesDatum.put("productData", productData);*/
                List<Map> buildData = buildBigPriceDao.getBuildData(stagesDatum);
                stagesDatum.put("buildData", buildData);
             /*   for (Map productDatum : productData) {
                    paramMap.put("projectFID", projectFID);
                    paramMap.put("productCode", productDatum.get("productCode"));
                    //根据业态查询楼栋

                }*/
            }
            visolinkResultBody.setResult(stagesData);
            visolinkResultBody.setMessages("数据获取成功!");
        } catch (Exception e) {
            visolinkResultBody.setMessages("数据获取失败!");
            visolinkResultBody.setCode(400);
        }
        return visolinkResultBody;
    }

    @Override
    public VisolinkResultBody importExcelTemplate(MultipartFile multipartFile, Map map) {
        VisolinkResultBody<Object> resultBody = new VisolinkResultBody<>();

        System.err.println("导入文件");
        //获取当前版本/节点id
        String plan_node_id = map.get("plan_node_id") + "";
        //"
        String plan_id = map.get("plan_id") + "";
        buildBigPriceDao.clearAllThisVersionData(plan_node_id);
        //存放读取数据
        MultipartFile multipartFile2=multipartFile;
        Workbook workbook = null;
        InputStream inputStream = null;
        int totalNum = 0;
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            //获取导入文件后缀名称
            String s = originalFilename.substring(originalFilename.indexOf("."), originalFilename.length());

            inputStream = multipartFile.getInputStream();
            //判断excel版本
            if (".xlsx".equals(s)) {
                //创建2007版工作簿
                workbook = new XSSFWorkbook(inputStream);
            } else if (".xls".equals(s)) {
                //创建2003版工作簿
                workbook = new HSSFWorkbook(inputStream);
            } else {
                resultBody.setCode(500);
                resultBody.setMessages("导入失败，请导入正确的Excel文件(.xls/.xlsx)文件!");
                return resultBody;
            }
            //获取sheet页
            Sheet sheetAt = workbook.getSheetAt(0);
            //获取exel的行数
            int lastRowNum = sheetAt.getLastRowNum();
            //获取excel列数
            short lastCellNum = sheetAt.getRow(0).getLastCellNum();


            //获取系统标识
            Row lastRow = sheetAt.getRow(lastRowNum);
            Cell cellbs = lastRow.getCell(0);
            String stringCellValue = cellbs.getStringCellValue();
            if(stringCellValue!=null&&!"".equals(stringCellValue)&&!"null".equals(stringCellValue)){
                System.err.println(stringCellValue);
                if(!stringCellValue.contains("营销管理")){
                    resultBody.setCode(500);
                    resultBody.setMessages("导入失败，请导入本系统指定的模版文件!");
                    return resultBody;
                }
            }else{
                resultBody.setCode(500);
                resultBody.setMessages("导入失败，未获取到系统标识，请导入本系统指定的模版文件");
                return resultBody;
            }
            //行
            for (int r = 3; r <= lastRowNum-1; r++) {

                Map importMap = new HashMap();
                importMap.put("sort", r);
                importMap.put("id", UUID.randomUUID().toString());
                importMap.put("plan_id", plan_id);
                importMap.put("plan_node_id", plan_node_id);
                importMap.put("isSave", 1);

                //校验数据类型
                boolean numberFlag = true;

                //获取除表头以外的行
                Row row = sheetAt.getRow(r);
                //列
                for (int c = 0; c < lastCellNum; c++) {
                    //获取每一行的每一列数据
                    Cell cell = row.getCell(c);
                    if (cell != null && !cell.toString().trim().equals("")) {
                        if (c != 0 && c != 1 && c != 2 && c != 3) {
                            CellType cellTypeEnum = cell.getCellTypeEnum();
                            if (c == 9) {
                                try {
                                    Date toString = cell.getDateCellValue();
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/M/d");
                                    System.out.println(toString);
                                    String parse = simpleDateFormat.format(toString);
                                    getExcelData(importMap, c, cell);
                                } catch (Exception e) {
                                    //手动回滚
                                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                    resultBody.setCode(500);
                                    resultBody.setMessages("导入失败,第" + (r + 1) + "行第" + (c + 1) + "列不是正确的日期格式(yyyy/M/d),请改正!");
                                    return resultBody;
                                }
                            }
                            switch (cellTypeEnum) {
                                case STRING:
                                    //手动回滚
                                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                    resultBody.setCode(500);
                                    resultBody.setMessages("导入失败,第" + (r + 1) + "行第" + (c + 1) + "列不是数字,请改正!");
                                    return resultBody;
                                case FORMULA:
                                    getExcelData(importMap, c, cell);
                                    break;
                                case NUMERIC:
                                    getExcelData(importMap, c, cell);
                                    break;
                            }
                        } else {
                            getExcelData(importMap, c, cell);
                        }

                    }
                }
                //将导入的数据入库
                buildBigPriceDao.insertBuildData(importMap);
            }
            List<Map> weiSaveBuildData = buildBigPriceDao.getWeiSaveBuildData(plan_node_id);
            resultBody.setResult(weiSaveBuildData);
            //将导入的文件上传到服务器留痕
           ResultBody importHirtory = uploadService.uploadFile_2(multipartFile2, "importHirtory", plan_node_id, "0");
            long code = importHirtory.getCode();
            if(code!=200){
                resultBody.setCode(500);
                resultBody.setMessages("文件留痕失败,请联系管理员!");
                return resultBody;
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            resultBody.setCode(400);
            resultBody.setMessages("文件导入失败!");
            return resultBody;
        }
        resultBody.setCode(200);
        resultBody.setMessages("导入成功!");
        return resultBody;
    }

    public Map getExcelData(Map importMap, int c, Cell cell) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        switch (c) {
            case 0:
                //项目
                importMap.put("project_name", cell.getStringCellValue());
                break;
            case 1:
                importMap.put("product_type", cell.getStringCellValue());
                break;
            case 2:
                //业态
                importMap.put("operation_type", cell.getStringCellValue());
                break;
            case 3:
                //楼栋
                CellType cellTypeEnum = cell.getCellTypeEnum();
                if (cellTypeEnum == CellType.NUMERIC) {
                    importMap.put("building_name", cell.getNumericCellValue());
                } else {
                    importMap.put("building_name", cell.getStringCellValue());
                }
                break;
            case 4:

                //楼栋水平系数
                importMap.put("level_coefficient", cell.getNumericCellValue());
                break;
            case 5:
                //楼栋业态系数
                importMap.put("operation_coefficient", cell.getNumericCellValue());
                break;
            case 6:
                //楼栋综合系数
                importMap.put("static_coefficient", cell.getNumericCellValue());
                break;
            case 7:
                //业态基价格
                importMap.put("product_price", cell.getNumericCellValue());
                break;
            case 8:
                //楼栋静态均价
                importMap.put("static_price", cell.getNumericCellValue());
                break;
            case 9:
                //开盘时间
                importMap.put("open_time", simpleDateFormat.format(new Date(cell.getDateCellValue().getTime())));
                break;
            case 10:
                //大定价版周期系数
                importMap.put("def_week", cell.getNumericCellValue());
                break;
            case 11:
                //大定价版楼栋均价
                importMap.put("this_building_avg", cell.getNumericCellValue());
                break;
            case 12:
                //本次定价楼栋周期系数修正
                importMap.put("this_pric_week_correction", cell.getNumericCellValue());
                break;
            case 13:
                //本地定价楼栋面积
                importMap.put("this_building_area", cell.getNumericCellValue());
                break;
            case 14:
                //本次定价楼栋均价
                importMap.put("this_pric_build_avgprice", cell.getNumericCellValue());
                break;
            case 15:
                //本次定价楼栋偏离度
                importMap.put("this_building_raw_avg", cell.getNumericCellValue());
                break;
            case 16:
                //本次货值
                importMap.put("this_pric_value", cell.getNumericCellValue());
                break;
            case 17:
                //较原系统货值损益
                importMap.put("this_pric_raw_value", cell.getNumericCellValue());
                break;
            case 18:
                //本次定价后所有楼栋货值
                importMap.put("this_pric_back_raw_value", cell.getNumericCellValue());
                break;
            case 19:
                //所有楼栋均价
                importMap.put("this_pric_back_raw_avg", cell.getNumericCellValue());
                break;
            case 20:
                //取自供销存-已售面积
                importMap.put("task_kingdee_sell_area", cell.getNumericCellValue());
                break;
            case 21:
                //-已售均价
                importMap.put("task_kingdee_sell_avg", cell.getNumericCellValue());
                break;
            case 22:
                //-已售货值
                importMap.put("task_kingdee_sell_value", cell.getNumericCellValue());
                break;
            case 23:
                //未售面积
                importMap.put("task_kingdee_not_are", cell.getNumericCellValue());
                break;
            case 24:
                //未售均价
                importMap.put("task_kingdee_not_avg", cell.getNumericCellValue());
                break;
            case 25:
                //未售货值
                importMap.put("task_kingdee_not_value", cell.getNumericCellValue());
                break;
            case 26:
                //整栋面积
                importMap.put("task_kingdee_not_area", cell.getNumericCellValue());
                break;
            case 27:
                //整栋均价
                importMap.put("task_kingdee_dynamic_avg", cell.getNumericCellValue());
                break;
            case 28:
                //整栋货值
                importMap.put("task_kingdee_dynamic_value", cell.getNumericCellValue());
                break;
            case 29:
                //已售比例
                importMap.put("task_kingdee_dynamic_sell", cell.getNumericCellValue());
                break;
        }
        return importMap;
    }

    @Override
    public VisolinkResultBody viewBigBuildData(Map map,HttpServletRequest request) {
        VisolinkResultBody<Object> resultBody = new VisolinkResultBody<>();
        try {
            String plan_node_id=map.get("plan_node_id")+"";
            String plan_id=map.get("plan_id")+"";
            String create=map.get("create")+"";

            //获取当前计划下最新审批通过的节点
            if("new".equals(create)){
                //获取审批通过的最新版本数据
                String newplanNodeId = buildBigPriceDao.getNewPlanNodeData(plan_id);
                plan_node_id=newplanNodeId;
                map.put("plan_node_id",plan_node_id);
            }
            List<Map> builldBigPriceData = buildBigPriceDao.getBuilldBigPriceData(map);
            if("new".equals(create)){
                map.remove("plan_node_id");
                String planNodeId=  designTwoIndexService.forPlanNode(map,request);
                plan_node_id=planNodeId;
                if(builldBigPriceData!=null){
                    for (Map builldBigPriceDatum : builldBigPriceData) {
                        builldBigPriceDatum.put("id", UUID.randomUUID().toString());
                        builldBigPriceDatum.put("plan_id", plan_id);
                        builldBigPriceDatum.put("plan_node_id", plan_node_id);
                        builldBigPriceDatum.put("isSave", 1);
                        //将导入的数据入库
                        buildBigPriceDao.insertBuildData(builldBigPriceDatum);
                    }
                }
            }
            resultBody.setResult(builldBigPriceData);
            resultBody.setCode(200);
            resultBody.setMessages("数据渲染成功!");
        } catch (Exception e) {
            e.printStackTrace();
            resultBody.setMessages("数据渲染失败!");
            resultBody.setCode(400);
        }
        //查询楼栋大定价数据
        return resultBody;
    }

    @Override
    public VisolinkResultBody updateBigPriceIsSave(Map map) {
        VisolinkResultBody<Object> response = new VisolinkResultBody<>();
        List<Map> list = (List<Map>) map.get("buildData");
        try {
           /* if (list != null && list.size() > 0) {
                for (Map buildMap : list) {
                    buildBigPriceDao.updateBigPriceIsSave(buildMap.get("id") + "");
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(500);
            response.setMessages("保存失败");
            return response;
        }
        response.setCode(200);
        response.setMessages("保存成功");
        return response;
    }

    @Override
    public Map filterMap(Map map) {
        Set set = map.keySet();
        Map<Object, Object> resultMap = new HashMap<>();
        for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
            Object obj = (Object) iterator.next();

            Object value = (Object) map.get(obj);
            remove(obj, value, iterator, resultMap);
        }
        return resultMap;
    }

    /**
     * 楼栋大定价数据导出
     * @param request
     * @param response
     * @return
     */
    @Override
    public ResultBody exportBuildBigPriceData(HttpServletRequest request, HttpServletResponse response) {
        //获取主数据id
        String planNodeid=request.getParameter("plan_node_id");
        //获取项目名称
        String projectName=request.getParameter("projectName");
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("plan_node_id",planNodeid);
        //定义文件标题
        String fileHeader=projectName+"-"+"楼栋大定价-数据导出";
        String filePath;
        //配置本地模版路径
        String realpath = null;
        //创建文件输入流
        FileInputStream fileInputStream = null;
        try {
            realpath="/Users/WorkSapce/Java/旭辉集团/Java/marketing-control-api/cifimaster/visolink-sales-api/src/main/webapp/TemplateExcel/buildBigPriceExportTemplate.xlsx";
            filePath=realpath;
            //读取文件
            File templateFile = new File(filePath);
            if(!templateFile.exists()){
                throw new BadRequestException(1004, "未读取到配置的导出模版，请先配置导出模版!");
            }
            //读取文件-获取文件流
            fileInputStream = new FileInputStream(templateFile);
            boolean empty = StringUtils.isEmpty(fileInputStream);
            if(empty){
                throw new BadRequestException(1004, "未读取到模版文件!");
            }
            //创建工作簿对象，
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            //获取楼栋大定价导出所需要的模版sheet页
            XSSFSheet sheetAt = workbook.getSheetAt(0);
            //获取第一行--标题
            XSSFRow row0 = sheetAt.getRow(0);
            //获取第一行第一个单元格
            XSSFCell cell0 = row0.getCell(0);
            //将拼接的标题写入文件
            cell0.setCellValue(fileHeader);


            //获取模版第5行第一列的单元格样式
            XSSFRow row5 = sheetAt.getRow(4);
            //获取第一格
            XSSFCell cell5_0 = row5.getCell(0);
            //获取样式
            XSSFCellStyle cell5_0cellStyle = cell5_0.getCellStyle();
            //获取需要导出的数据
            List<Map> builldBigPriceData = buildBigPriceDao.getBuilldBigPriceData(paramMap);

            //定义数据字段
            String[] dataArray={"project_name","product_type","operation_type","building_name","level_coefficient","operation_coefficient","static_coefficient","product_price","static_price","open_time",
                    "def_week","this_building_avg","this_pric_week_correction","this_building_area","this_pric_build_avgprice","this_building_raw_avg","this_pric_value","this_pric_raw_value",
                    "this_pric_back_raw_value","this_pric_back_raw_avg","task_kingdee_sell_area","task_kingdee_sell_avg","task_kingdee_sell_value","task_kingdee_not_are","task_kingdee_not_avg",
                    "task_kingdee_not_value","task_kingdee_not_area","task_kingdee_not_avg","task_kingdee_not_value","task_kingdee_not_area","task_kingdee_dynamic_avg","task_kingdee_dynamic_value","task_kingdee_dynamic_sell"};
            CreationHelper createHelper=workbook.getCreationHelper();

            if(builldBigPriceData!=null&&builldBigPriceData.size()>0){
                //从模版第4行开始创建
                for (int i=4;i<4+builldBigPriceData.size();i++){
                    //依次创建行
                    XSSFRow row = sheetAt.createRow(i);
                    //获取数据
                    Map buildPriceMap = builldBigPriceData.get(i - 4);
                    //创建单元格
                    for (int j=0;j<30;j++){
                        XSSFCell cell = row.createCell(j);
                        String value=buildPriceMap.get(dataArray[j])+"";
                            //给新建单元格设置第五行第一列的样式
                            cell.setCellStyle(cell5_0cellStyle);
                            if(!"null".equals(value)){
                                cell.setCellValue(value);
                        }
                    }
                }
            }else{
                return  ResultBody.error(-1005,"当前版本没有可以导出的数据，换个版本试试!");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String formatDate = sdf.format(new Date());
            String fileName = fileHeader+"-"+formatDate+".xlsx";
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        }catch (Exception e){
            //打印异常信息
            e.printStackTrace();
            return  ResultBody.error(-1009,"导出失败:"+e.getMessage());
        }
        return null;
    }

    private static void remove(Object key, Object obj, Iterator iterator, Map resultMap) {
        List<Object> list = new ArrayList<>();


        if (obj instanceof String) {
            String str = (String) obj;
            if (!"".equals(str) && !"null".equals(str)) {  //过滤掉为null和""的值 主函数输出结果map：{2=BB, 1=AA, 5=CC, 8=  }
//            if("".equals(str.trim())){  //过滤掉为null、""和" "的值 主函数输出结果map：{2=BB, 1=AA, 5=CC}
                //iterator.remove();
            }

        } else if (obj instanceof Collection) {
            Collection col = (Collection) obj;

            if (col == null || col.isEmpty()) {
                iterator.remove();
            } else {
                for (Object o : col) {
                    Map<Object, Object> maps = new HashMap<>();
                    Map<String, Object> mapd = (Map) o;
                    if (mapd != null) {
                        for (Map.Entry<String, Object> entry : mapd.entrySet()) {
                            String value = entry.getValue() + "";
                            System.out.println(value);
                            if (!"".equals(value) && !"null".equals(value)) {
                                maps.put(entry.getKey(), entry.getValue());
                            }
                        }
                        list.add(maps);
                    }
                }
                resultMap.put(key, list);
            }

        } else if (obj instanceof Map) {
            Map<Object, Object> temp = (Map) obj;
            if (temp != null) {
                for (Map.Entry entry : temp.entrySet()) {
                    String value = entry.getKey() + "";
                    if (!"".equals(value) && !"null".equals(value)) {
                        resultMap.put(entry.getKey(), entry.getValue());
                    } else {
                        temp.remove(entry);
                    }
                }
            }

        } else if (obj instanceof Object[]) {
            Object[] array = (Object[]) obj;
            if (array == null || array.length <= 0) {
                iterator.remove();
            }
        } else {
            if (obj == null) {
                iterator.remove();
            }
        }
    }


}
