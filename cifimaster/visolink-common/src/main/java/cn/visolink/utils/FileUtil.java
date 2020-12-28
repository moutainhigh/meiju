package cn.visolink.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * File工具类，扩展 hutool 工具包
 *
 * @author WCL
 * @date 2018-12-27
 */
@SuppressWarnings("all")
public class FileUtil<T> extends cn.hutool.core.io.FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);



    /**
     * 标题行样式
     */
    private static CellStyle titleStyle;
    /**
     * 标题行字体
     */
    private static Font titleFont;
    /**
     * 日期行样式
     */
    private static CellStyle dateStyle;
    /**
     * 日期行字体
     */
    private static Font dateFont;
    /**
     * 表头行样式
     */
    private static CellStyle headStyle;
    /**
     * 表头行字体
     */
    private static Font headFont;
    /**
     * 内容行样式
     */
    private static CellStyle contentStyle;
    /**
     * 内容行字体
     */
    private static Font contentFont;
    /**
     * 内容格式化
     */
    private static DataFormat format;
    /**
     * 默认下载日期格式
     */
    private static String pattern = "yyyy-MM-dd HH:mm:ss";



    /**
     * 定义GB的计算常量
     */
    private static final int GB = 1024 * 1024 * 1024;
    /**
     * 定义MB的计算常量
     */
    private static final int MB = 1024 * 1024;
    /**
     * 定义KB的计算常量
     */
    private static final int KB = 1024;

    /**
     * 格式化小数
     */
    private static final DecimalFormat DF = new DecimalFormat("0.00");

    /**
     * MultipartFile转File
     *
     * @param multipartFile
     * @return
     */
    public static File toFile(MultipartFile multipartFile) {
        // 获取文件名
        String fileName = multipartFile.getOriginalFilename();
        // 获取文件后缀
        String prefix = "." + getExtensionName(fileName);
        File file = null;
        try {
            // 用uuid作为文件名，防止生成的临时文件重复
            file = File.createTempFile(RandomUtil.simpleUUID(), prefix);
            // MultipartFile to File
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 删除
     *
     * @param files
     */
    public static void deleteFile(File... files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param filename
     * @return
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * Java文件操作 获取不带扩展名的文件名
     *
     * @param filename
     * @return
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 文件大小转换
     *
     * @param size
     * @return
     */
    public static String getSize(int size) {
        String resultSize = "";
        if (size / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            resultSize = DF.format(size / (float) GB) + "GB   ";
        } else if (size / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            resultSize = DF.format(size / (float) MB) + "MB   ";
        } else if (size / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            resultSize = DF.format(size / (float) KB) + "KB   ";
        } else {
            resultSize = size + "B   ";
        }
        return resultSize;
    }

    /**
     * 通用导出Excel 方法
     *
     * @param templateUrl 模板文件
     * @param exportData  数据来源
     * @param fileName    文件名称
     * @param response
     */
    public static void exportExcel(String templateUrl, Map exportData, String fileName, HttpServletResponse response) {
        try {
            TemplateExportParams params = new TemplateExportParams(
                    templateUrl);
            Workbook workbook = ExcelExportUtil.exportExcel(params, exportData);
            ServletOutputStream outputStream = response.getOutputStream();
            // 组装附件名称和格式
            fileName = new String((new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + fileName).getBytes("gbk"),
                    "ISO8859_1");
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");
            response.setContentType("application/octet-stream; charset=utf-8");
            outputStream.flush();
            workbook.write(outputStream);
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param dataList    数据来源集
     * @param datePattern 时间格式化规则
     * @param response    输出流
     * @param fileName    文件名称
     * @param sheetName   表头名称
     */
    /**
     * 列名
     */
    private static List<String> columnsList;
    /**
     * 列标题
     */
    private static List<String> headList;
    /**
     *
     */
    private static List<Map> dataList;

    /**
     * @param fileUrl      导出文件路径
     * @param isExportFile 是否导出到指定文件，false为输出流，页面直接下载
     * @param sqlId        数据SqlId
     * @param batch        每次取的条数
     * @param map          查询条件
     * @param headList     Excel标题列
     * @param columnsList  Excel 标题对应数据库数据key
     * @param dataList     数据源
     * @param datePattern  时间格式化标准
     * @param fileName     文件名称
     * @param sheetName    表头名称
     * @param titleName    Excel标题名称
     * @throws IOException
     * @example
     *  List<String> columnsList =new ArrayList<>();
     *         columnsList.add("ClueID");
     *         columnsList.add("CustomerID");
     *         columnsList.add("IntentionID");
     *         int SystemType = Integer.parseInt(request.getHeader("isExpotFile"));
     *         List<String> headList =new ArrayList<>();
     *         headList.add("线索Id");
     *         headList.add("客户Id");
     *         headList.add("问题编码");
     *         String xlsFile = "/data/excel"; //输出文件
     *         FileUtil fileUtil=new FileUtil(dbsqlService);
     *         if(SystemType==1){
     *             fileUtil.ExportBigExcel(xlsFile,true,"mBcstquestionanswer_Select"
     *                     ,20000,null,headList,columnsList,"yyyy-MM-dd HH:mm:ss",
     *                     "测试导出","测试表头","测试标题",response);
     *         }else{
     *             fileUtil.ExportBigExcel(xlsFile,false,"mBcstquestionanswer_Select"
     *                     ,20000,null,headList,columnsList,"yyyy-MM-dd HH:mm:ss",
     *                     "测试导出","测试表头","测试标题",response);
     *         }
     *
     *
     */
    @SuppressWarnings("dep-ann")
    public void ExportBigExcel(String fileUrl, boolean isExportFile, String sqlId, int batch, Map map, List<String> headList,
                               List<String> columnsList, String datePattern, String fileName, String sheetName, String titleName, HttpServletResponse response) throws IOException {
        // 最重要的就是使用SXSSFWorkbook，表示流的方式进行操作
        // 在内存中保持100行，超过100行将被刷新到磁盘
        SXSSFWorkbook wb = new SXSSFWorkbook(100);
        //工作表对象
        SXSSFSheet sheet = null;
        //行对象
        SXSSFRow nRow = null;
        //列对象
        SXSSFCell nCell = null;
        //开始时间
        long startTime = System.currentTimeMillis();
        //总行号
        int rowNo = 0;
        //页行号
        int pageRowNo = 1;
        //建立新的sheet对象
        sheet = wb.createSheet(sheetName);
        //动态指定当前的工作表
        sheet = wb.getSheetAt(rowNo);


        wb.setCompressTempFiles(true);

        /** 设置格式* */
        // ********标题样式
        XSSFCellStyle titleStyle = setTitleStyle(wb);


        // ********表头样式
        CellStyle headerStyle = setHeaderStyle(wb);


        // ********单元格样式
        CellStyle cellStyle = setCellStyle(wb);

        // 创建标题行对象
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        /**
         * 设置标题样式
         */
        titleCell.setCellValue(titleName);
        titleRow.setHeight((short) 700);
        titleCell.setCellStyle(titleStyle);
        //合并单元行
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, Integer.valueOf(headList.size()) - 1);
        sheet.addMergedRegion(cellRangeAddress);

        // 创建菜单行对象
        SXSSFRow row = sheet.createRow(1);
        row.setHeight((short) 500);

        //定义表头
        for (int i = 0; i < headList.size(); i++) {
            nCell = row.createCell(i);
            nCell.setCellValue(headList.get(i));
            //设置表头样式
            nCell.setCellStyle(headerStyle);
        }
        /**
         * 渲染数据
         */
        sheet = getRows(sqlId, batch, map, columnsList, dataList, datePattern, sheetName, wb, sheet, rowNo, pageRowNo, cellStyle);
        /**
         * 判断文件输出方式 ① 文件输出到指定位置,② 文件输入到返回流中,前端页面直接下载
         */
        if (isExportFile) {
            //判断文件夹是否存在
            File savefile = new File(fileUrl);
            if (!savefile.exists()) {
                savefile.mkdirs();
            }
            FileOutputStream fOut = new FileOutputStream(fileUrl + "/" + fileName + ".xlsx");
            wb.write(fOut);
            //刷新缓冲区
            fOut.flush();
            fOut.close();
        } else {
            //自动调整列宽
            sheet.trackAllColumnsForAutoSizing();
            for (int i = 0; i < headList.size(); i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 20 / 10);
            }
            //处理完成时间
            long finishedTime = System.currentTimeMillis();
            logger.info("数据处理完成 time: " + (finishedTime - startTime) / 1000 + "m");
            fileName = new String((new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "-" + fileName).getBytes("gbk"),
                    "ISO8859_1");
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xlsx");
            response.setContentType("application/octet-stream; charset=utf-8");
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.flush();
            wb.write(outputStream);
            outputStream.close();

        }

    }

    private SXSSFSheet getRows(String sqlId, int batch, Map map, List<String> columnsList, List<Map> dataList, String datePattern, String sheetName, SXSSFWorkbook wb, SXSSFSheet sheet, int rowNo, int pageRowNo, CellStyle cellStyle) {
        SXSSFRow nRow;
        SXSSFCell nCell;
        if (CollUtil.isEmpty(dataList)) {
            return getSheetByData(columnsList, dataList, datePattern, sheetName, wb, sheet, rowNo, pageRowNo, cellStyle);
        }
        return getSheetBySql(sqlId, batch, map, columnsList, datePattern, sheetName, wb, sheet, rowNo, pageRowNo, cellStyle);
    }

    /**
     * 根据数据List获取sheet
     *
     * @param columnsList
     * @param dataList
     * @param datePattern
     * @param sheetName
     * @param wb
     * @param sheet
     * @param rowNo
     * @param pageRowNo
     * @param cellStyle
     * @return
     */
    private SXSSFSheet getSheetByData(List<String> columnsList, List<Map> dataList, String datePattern, String sheetName, SXSSFWorkbook wb, SXSSFSheet sheet, int rowNo, int pageRowNo, CellStyle cellStyle) {
        SXSSFRow nRow;
        SXSSFCell nCell;
        for (int j = 0; j < dataList.size(); j++) {
            Map printMap = dataList.get(j);
            if (rowNo % 1000000 == 0) {
                System.out.println("Current Sheet:" + rowNo / 1000000);
                //建立新的sheet对象
                if (rowNo != 0) {
                    sheet = wb.createSheet("第" + (rowNo / 1000000) + "个" + sheetName);
                }
                //动态指定当前的工作表
                sheet = wb.getSheetAt(rowNo / 1000000);
                //每当新建了工作表就将当前工作表的行号重置为0
                pageRowNo = 2;
            }
            rowNo++;
            //新建行对象
            nRow = sheet.createRow(pageRowNo++);
            // 打印每行，每行有N列数据   rsmd.getColumnCount()==N --- 列属性的个数
            for (int q = 0; q < columnsList.size(); q++) {
                Object object = printMap.get(columnsList.get(q));
                nCell = nRow.createCell(q);
                String textValue = null;
                if (object == null) {
                    textValue = "";
                } else if (object instanceof Date) {
                    Date date = (Date) object;
                    SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                    textValue = sdf.format(date);
                } else {
                    // 其它数据类型都当作字符串简单处理
                    textValue = object.toString();
                }
                //单元格赋值
                nCell.setCellValue(textValue);
                //单元格格式设置
                nCell.setCellStyle(cellStyle);
            }

            if (rowNo % 10000 == 0) {
                System.out.println("row no: " + rowNo);
            }
        }
        return sheet;
    }

    /**
     * 数据库动态获取Sheet
     *
     * @param sqlId
     * @param batch
     * @param map
     * @param columnsList
     * @param datePattern
     * @param sheetName
     * @param wb
     * @param sheet
     * @param rowNo
     * @param pageRowNo
     * @param cellStyle
     * @return
     */
    private SXSSFSheet getSheetBySql(String sqlId, int batch, Map map, List<String> columnsList, String datePattern, String sheetName, SXSSFWorkbook wb, SXSSFSheet sheet, int rowNo, int pageRowNo, CellStyle cellStyle) {
        SXSSFRow nRow;
        SXSSFCell nCell;//查询出数据的总条数
        long countBySqlID = 0;
//                this.dbsqlService.getCountBySqlID(sqlId, map);
        //总页数
        double tp = countBySqlID / (double) batch;
        int totalPage = (int) Math.ceil(tp);
        //循环构造数据
        for (int i = 1; i <= totalPage; i++) {
            //查询数据
            List<Map> mBcstquestionanswer = null;
//                    this.dbsqlService.getDataByLimit("mBcstquestionanswer_Select", map, String.valueOf(batch), String.valueOf((i)));
            for (int j = 0; j < mBcstquestionanswer.size(); j++) {
                Map printMap = mBcstquestionanswer.get(j);
                if (rowNo % 1000000 == 0) {
                    System.out.println("Current Sheet:" + rowNo / 1000000);
                    //建立新的sheet对象
                    if (rowNo != 0) {
                        sheet = wb.createSheet("第" + (rowNo / 1000000) + "个" + sheetName);
                    }
                    //动态指定当前的工作表
                    sheet = wb.getSheetAt(rowNo / 1000000);
                    //每当新建了工作表就将当前工作表的行号重置为0
                    pageRowNo = 2;
                }
                rowNo++;
                //新建行对象
                nRow = sheet.createRow(pageRowNo++);
                // 打印每行，每行有N列数据   rsmd.getColumnCount()==N --- 列属性的个数
                for (int q = 0; q < columnsList.size(); q++) {
                    Object object = printMap.get(columnsList.get(q));
                    nCell = nRow.createCell(q);
                    String textValue = null;
                    if (object == null) {
                        textValue = "";
                    } else if (object instanceof Date) {
                        Date date = (Date) object;
                        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                        textValue = sdf.format(date);
                    } else {
                        // 其它数据类型都当作字符串简单处理
                        textValue = object.toString();
                    }
                    //单元格赋值
                    nCell.setCellValue(textValue);
                    //单元格格式设置
                    nCell.setCellStyle(cellStyle);
                }

                if (rowNo % 10000 == 0) {
                    System.out.println("row no: " + rowNo);
                }
            }
            mBcstquestionanswer.clear();
        }

        return sheet;
    }

    private static CellStyle setCellStyle(SXSSFWorkbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        //垂直居中
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        //设置边框
        cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);

        //设置自动换行
        cellStyle.setWrapText(true);

        //设置字体
        Font cellFont = wb.createFont();
        cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        cellFont.setFontName("宋体");
        cellStyle.setFont(cellFont);
        return cellStyle;
    }

    private static XSSFCellStyle setTitleStyle(SXSSFWorkbook wb) {
        XSSFCellStyle titleStyle = (XSSFCellStyle) wb.createCellStyle();

        //水平居中
        titleStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
        //垂直居中
        titleStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        //设置颜色
        titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        //设置边框
        titleStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        titleStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        titleStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        titleStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        Font titleFont = wb.createFont();
        titleFont.setFontName("宋体");
        titleFont.setFontHeightInPoints((short) 18);
        titleFont.setBoldweight((short) 800);
        titleStyle.setFont(titleFont);
        return titleStyle;
    }

    private static CellStyle setHeaderStyle(SXSSFWorkbook wb) {
        CellStyle headerStyle = wb.createCellStyle();

        //水平居中
        headerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        //垂直居中
        headerStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);

        //设置边框
        headerStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);

        //设置颜色
        headerStyle.setFillForegroundColor(IndexedColors.DARK_YELLOW.getIndex());
        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);


        Font headerFont = wb.createFont();
        headerFont.setFontName("微软雅黑");
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        headerStyle.setFont(headerFont);
        return headerStyle;
    }

}
