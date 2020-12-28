package cn.visolink.salesmanage.groupmanagement.service.imple;

import cn.visolink.salesmanage.groupmanagement.dao.GroupManageDao;
import cn.visolink.salesmanage.groupmanagement.service.GroupManageExportService;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wjc
 *
 */
@Service
public class GroupManageExportServiceImpl implements GroupManageExportService  {
    @Autowired
    private GroupManageDao groupManageDao;
    /**
     * 导出excel表
     * @param request
     * @param response
     * @return
     */
    @Override
    public Map<String, Object> indicatorDataExport(HttpServletRequest request, HttpServletResponse response,String months)  {

        Map<String,Object> result=new HashMap<>();


        String PlanName = "";

        /*String basePath = "D:\\Work\\XV_旭辉\\YX_营销管理\\05_code\\api\\marketing-control-api\\cifimaster\\visolink-sales-api\\src\\";*/
        String basePath = "E:\\Users\\";
        String templatePath =  "TemplateExcel" + File.separator + "group_issued_indicator_data.xlsx";//模板文件路径。
        String targetFileDir =   "Uploads" + File.separator + "DownLoadTemporaryFiles";//导出临时文件文件夹。
        String targetfilePath = targetFileDir + File.separator + PlanName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";// 目标文件路径。
        try {
            PlanName = URLEncoder.encode(PlanName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx", "utf-8").replace("+","%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String returnUrl = "/" +  targetFileDir + File.separator + PlanName; //成功后返回的文件相对路径。

        //HttpServletRequest request;
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //设置content-disposition响应头控制浏览器以下载的形式打开文件
        try {
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(PlanName.getBytes(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // String basePath = request.getServletContext().getRealPath("/");

        targetfilePath = basePath + targetfilePath;//TODO 指向文件系统。暂时放在应用服务器
        FileInputStream templateInputStream = null;
        FileOutputStream fileOutputStream = null;
        Workbook targetWorkBook = null;
        XSSFSheet targetSheet = null;
        XSSFSheet positionSheet = null;
        int startRow = 0;
        try {
            //验证模板文件是否存在
            templatePath = basePath + templatePath;
            File templateFile = new File(templatePath);
            if(!templateFile.exists()){
                throw new ServiceException("-1","认购确认导出失败。模板文件不存在");
            }
            //验证目标文件夹是否存在
            // targetFileDir = basePath + targetFileDir;
            File targetFileDirFile = new File(targetFileDir);
            if(!targetFileDirFile.exists()){
                targetFileDirFile.mkdirs();
            }
            //创建输出文档。
            templateInputStream = new FileInputStream(templateFile);
            targetWorkBook = new XSSFWorkbook(templateInputStream);
            targetSheet = (XSSFSheet) targetWorkBook.getSheetAt(0);
            //模板文件中最大行
            int maxTemplateRows = targetSheet.getLastRowNum();

            // ----------------- 开始查询业务数据

            /*List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();*/
            Map map=new HashMap();
            List<Map> mapList = groupManageDao.getGroupAllMessage(map);
            int startRows = 3;  //起始行
            int xuhao = 1; //起始列

            for (int z = 0; z < mapList.size(); z++) {
                Map<String,Object> jobrow= mapList.get(z);
                /* Map<String, String> jobrow = (Map<String, String>) mapList.get(z);*/
                //处理数据 第1行
                XSSFRow row1 = targetSheet.getRow(1);
//                row1.getCell(0).setCellValue( "跟投组织报表");
                //写每行
                XSSFRow positionRow = targetSheet.createRow(startRows);
                positionRow.setHeightInPoints(20);
                //依次将查询到的数据插入其中
                /*XSSFCell numberCell = positionRow.createCell(0);*/
                /*numberCell.setCellValue(xuhao);*/

                positionRow.createCell(0).setCellValue(jobrow.get("business_name").toString());
                positionRow.createCell(1).setCellValue(jobrow.get("reserve_can_sell_set").toString());
                positionRow.createCell(2).setCellValue(jobrow.get("reserve_can_sell_funds").toString());
                positionRow.createCell(3).setCellValue(jobrow.get("new_reserve_set").toString());
                positionRow.createCell(4).setCellValue(jobrow.get("new_reserve_funds").toString());
                positionRow.createCell(5).setCellValue(jobrow.get("total_reserve_set").toString());
                positionRow.createCell(6).setCellValue(jobrow.get("total_reserve_funds").toString());
                positionRow.createCell(7).setCellValue(jobrow.get("year_plan_sign").toString());
                positionRow.createCell(8).setCellValue(jobrow.get("year_grand_total_sign").toString());
                positionRow.createCell(9).setCellValue(jobrow.get("top_three_month_average_sign_set").toString());
                positionRow.createCell(10).setCellValue(jobrow.get("top_three_month_average_sign_funds").toString());
                positionRow.createCell(11).setCellValue(jobrow.get("upper_moon_sign_set").toString());
                positionRow.createCell(12).setCellValue(jobrow.get("upper_moon_sign_funds").toString());
                positionRow.createCell(13).setCellValue(jobrow.get("reserve_sign_funds").toString());
                positionRow.createCell(14).setCellValue(jobrow.get("new_sign_funds").toString());
                positionRow.createCell(15).setCellValue(jobrow.get("total_sign_funds").toString());

                startRows++;
                xuhao++;
            }



            //-----------------

            fileOutputStream = new FileOutputStream(targetfilePath);
            //页面输出
            targetWorkBook.write(response.getOutputStream());
            //服务器硬盘输出
            targetWorkBook.write(fileOutputStream);
            //导出成功，
            //   wresponse.setErrcode("0");
            //  wresponse.setErrmsg("成功");
            Map<String,String> resultData = new HashMap<String,String>();
            resultData.put("url",returnUrl);
            //  wresponse.setData(resultData);

        }catch(ServiceException se) {
            se.printStackTrace();
            System.out.println(se.getResponseMsg());
            result.put("key", se.getResponseMsg());

            // wresponse.setErrcode(se.getResponseCode());
            // wresponse.setErrmsg("计划导出失败。"+se.getResponseMsg());
        } catch (Exception e) {
            e.printStackTrace();
            // wresponse.setErrcode("-1");
            // wresponse.setErrmsg("计划导出失败。"+e.getMessage());
        } finally {
            if(templateInputStream != null){
                try {
                    templateInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }






        return result;
    }


}
