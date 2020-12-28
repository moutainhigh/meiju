package cn.visolink.utils;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 文件操作工具类
 *
 * @author cdg visolink_com
 * @date 2017/6/5 22:14
 */
public class FileUtils {
    public static final String separator = "/";
    /**
     * 删除文件
     * @param url
     * @return
     */
    public static boolean deleteFile(String url){
        try {
            File targetFile = new File(url);
            if(targetFile.exists()){
                targetFile.delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 读取单元格值
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell){
        String cellValue = "";
        //System.out.println(cell.getCellTypeEnum().getCode());
        if(cell != null){
            switch (cell.getCellType())
            {
                case Cell.CELL_TYPE_NUMERIC: // 数字
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING: // 字符串
                    cellValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_BOOLEAN: // Boolean
                    cellValue = cell.getBooleanCellValue() + "";
                    break;
                case Cell.CELL_TYPE_FORMULA: // 公式
                    cellValue = cell.getCellFormula() + "";
                    break;
                case Cell.CELL_TYPE_BLANK: // 空值
                    cellValue = "";
                    break;
                case Cell.CELL_TYPE_ERROR: // 故障
                    cellValue = "非法字符";
                    break;
                default:
                    cellValue = "未知类型";
                    break;
            }
        }
        return cellValue;
    }

    public static String getCellValue(Cell cell,FormulaEvaluator formulaEvaluator){
        String cellValue = "";
        //CellReference cellReference = new CellReference(cell);
        //System.out.println(cell.getCellTypeEnum().getCode());
        if(cell != null){
            switch (cell.getCellType())
            {
                case Cell.CELL_TYPE_NUMERIC: // 数字
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING: // 字符串
                    cellValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_BOOLEAN: // Boolean
                    cellValue = cell.getBooleanCellValue() + "";
                    break;
                case Cell.CELL_TYPE_FORMULA: // 公式
                    try {
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    } catch (IllegalStateException e) {
                        cellValue = String.valueOf(cell.getRichStringCellValue());
                    }
                    //cellValue = cell.getCellFormula() + "";
                    //CellValue formulaCellValue = formulaEvaluator.evaluate(cell);
                    /*switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
                        case Cell.CELL_TYPE_BOOLEAN:
                            cellValue = String.valueOf(cell.getBooleanCellValue());
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            cellValue = String.valueOf(cell.getNumericCellValue());
                            break;
                        case Cell.CELL_TYPE_STRING:
                            cellValue = cell.getStringCellValue();
                            break;
                        case Cell.CELL_TYPE_BLANK:
                            cellValue = "";
                            break;
                        case Cell.CELL_TYPE_ERROR:
                            cellValue = "非法字符";
                            break;
                        case Cell.CELL_TYPE_FORMULA:
                            break;
                    }*/
                    break;
                case Cell.CELL_TYPE_BLANK: // 空值
                    cellValue = "";
                    break;
                case Cell.CELL_TYPE_ERROR: // 故障
                    cellValue = "非法字符";
                    break;
                default:
                    cellValue = "未知类型";
                    break;
            }
        }
        return cellValue;
    }

    /**
     * 往sheet设置值
     * @param sheet
     * @param startRowNum
     * @param startColumnNum
     * @throws Exception
     */
    public static void setSheetValue(List dataList, Sheet sheet, int startRowNum, int startColumnNum) throws Exception{
        //新增行
        Row newRow = null;
        int targetRowNum = startRowNum;
        for(int k = 0;k < dataList.size(); k++) {
            //插入新行
            newRow = sheet.createRow(targetRowNum);
            //新增新行的每个列
            Map<String,String> columns = (Map<String,String>)dataList.get(k);
            Cell newCell = null;
            if(columns != null && columns.size() > 0) {
                int m = startColumnNum;
                for (Map.Entry<String, String> entry : columns.entrySet()) {
                    newCell = newRow.createCell(m);
                    newCell.setCellValue(entry.getValue());
                    m++;
                }
            }
            targetRowNum++;
        }
    }
    /**
     * 为防止一个目录下面出现太多文件，要使用hash算法打散存储
     *
     * @param filename 文件名，要根据文件名生成存储目录
     * @param savePath 文件存储路径
     * @return 新的存储目录
     */
    public static String findFileSavePathByFileName(String filename, String savePath) {
        //得到文件名的hashCode的值，得到的就是filename这个字符串对象在内存中的地址
        int hashcode = filename.hashCode();
        /**
         * 0--15
         */
        int dir1 = hashcode & 0xf;
        /**
         * 0-15
         */
        int dir2 = (hashcode & 0xf0) >> 4;
        /**
         * 构造新的保存目录,比如：upload\2\3  upload\3\5
         */
        String dir = savePath + File.separator + dir1 + File.separator + dir2;
        /**
         * File既可以代表文件也可以代表目录
         */
        File file = new File(dir);
        //如果目录不存在
        if (!file.exists()) {
            //创建目录
            file.mkdirs();
        }
        return dir;
    }

    public static String getFileName(String path) {
        if (path != null && !"".equals(path)) {
            path = path.replace('\\','/');
            return path.substring(path.lastIndexOf("/") + 1);
        }
        else {
            return "";
        }
    }

    public static String getExtName(String path) {
        if (path != null && !"".equals(path)) {
            return path.substring(path.lastIndexOf(".") + 1);
        }
        else {
            return "";
        }
    }

    public static String getSaveFileName(String fileName) {
        if(fileName == null) {
            return UUID.randomUUID().toString();
        }
        else {
            return UUID.randomUUID().toString() + "_" + fileName;
        }
    }

    public static String processPath(String path, Boolean isBase) {
        if (path != null && !"".equals(path)) {
            path = path.replace('\\','/');
            if (!isBase && path.startsWith("/")) {
                path = path.substring(1);
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            return path;
        }
        else {
            return "";
        }

    }
}
