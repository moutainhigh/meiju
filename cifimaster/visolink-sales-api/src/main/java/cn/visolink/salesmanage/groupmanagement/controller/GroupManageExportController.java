package cn.visolink.salesmanage.groupmanagement.controller;


import cn.visolink.common.security.utils.HttpRequestUtil;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.groupmanagement.dao.GroupManageUpdate;
import cn.visolink.salesmanage.groupmanagement.dao.GroupManagerExportDao;
import cn.visolink.salesmanage.groupmanagement.service.GroupManageExportService;
import com.google.gson.Gson;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wjc
 */
@RestController
@RequestMapping("/GroupManagementExport")
@Api(tags = "导出")
public class GroupManageExportController {

    @Autowired
    private GroupManageUpdate groupManagerUpdate;

    @Autowired
    private GroupManageExportService groupManageExportService;
    @Log("导出")
    @CessBody
    @ApiOperation(value = "导出")
    @PostMapping("/getRegionalMonthPlans")
    public Map<String,Object> IndicatorDataExport(HttpServletRequest request, HttpServletResponse response,@RequestBody String months) throws IOException {

        Map<String,Object> result=new HashMap<>();

        result =  groupManageExportService.indicatorDataExport(request,response,months);

        return result;
    }



    @Log("导出")
    @CessBody
    @ApiOperation(value = "导出")
    @GetMapping("/getRealretData")
    public void getRealretData(HttpServletRequest request, HttpServletResponse response,String months) throws IOException, ServiceException {

        String prcDimProjGoal="https://service.cifi.com.cn/datacollector/load/prcDimProjGoalMMy";
        String  prcDimProjapikey="9c82bcfcc52390bf1346106f5d9f3c9f";
        String createTimeriskresult = HttpRequestUtil.httpGet(prcDimProjGoal+"?params="+months+"&apikey="+prcDimProjapikey,false);
        System.out.println(prcDimProjGoal+"?params="+months+"&apikey="+prcDimProjapikey+"-----prcDimProjapikey");
        Gson gson=new Gson();
        Map<String,Object> GsonMap=new HashMap();
        GsonMap=gson.fromJson((createTimeriskresult+""),GsonMap.getClass());
        System.out.println(GsonMap+"GsonMap");
        /*返回值*/

        /*得到所有来自数据湖的数据，然后遍历到返回值里*/
        List<Map> AllProject=(List<Map>)GsonMap.get("retData");
        DecimalFormat df = new DecimalFormat("#.00");

            String planName = "项目";
      String basePath = request.getServletContext().getRealPath("/");


      String  templatePath = File.separator + "TemplateExcel" + File.separator + "retData.xlsx";
        //导出临时文件文件夹。
       String targetFileDir = "Uploads" + File.separator + "DownLoadTemporaryFiles";
        // 目标文件路径。
        String targetFilePath = targetFileDir + File.separator + planName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
        planName = URLEncoder.encode(planName + ".xlsx", "utf-8").replace("+", "%20");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //设置content-disposition响应头控制浏览器以下载的形式打开文件
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(planName.getBytes(), "utf-8"));
        //验证模板文件是否存在
       //   String  realpath= this.getClass().getResource("/").getPath()  ;


       //  realpath=realpath.substring(0,realpath.indexOf("/target"))+File.separator+"src"+File.separator+"main"+File.separator+"webapp"+templatePath;

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
        FileInputStream  templateInputStream = new FileInputStream(templateFile);
        Workbook  targetWorkBook = new XSSFWorkbook(templateInputStream);
        XSSFSheet targetSheet = (XSSFSheet) targetWorkBook.getSheetAt(0);
        targetWorkBook.setSheetName(0, "集团下达月度营销指标");


            List<Map> mapList = AllProject;
                int startRows = 1;  //起始行
                int xuhao = 1; //起始列

                List<Map> allCode=  groupManagerUpdate.selectAllCode();

                for (int z = 0; z < mapList.size(); z++) {
                    Map<String, Object> jobrow = mapList.get(z);

                    for(int b=0;b<allCode.size();b++){
                        if( (jobrow.get("idmProjId")+"").equals(allCode.get(b).get("projectID")+"")){
                            jobrow.put("projectCode",allCode.get(b).get("projectCode")+"");
                            jobrow.put("projectName",allCode.get(b).get("projectName")+"");
                        }
                    }
                    /* Map<String, String> jobrow = (Map<String, String>) mapList.get(z);*/
                    //处理数据 第1行
                    //写每行
                    XSSFRow positionRow = targetSheet.createRow(startRows);
                    positionRow.setHeightInPoints(20);
                    //依次将查询到的数据插入其中
                    /*XSSFCell numberCell = positionRow.createCell(0);*/
                    /*numberCell.setCellValue(xuhao);*/

                    positionRow.createCell(0).setCellValue(jobrow.get("projectName")+"");
                    positionRow.createCell(1).setCellValue(jobrow.get("projectCode")+"");
                    positionRow.createCell(2).setCellValue( df.format(Double.parseDouble(jobrow.get("cntrtAmtGoalM")+"")/10000));

                    startRows++;
                    xuhao++;
                }


                //-----------------
        FileOutputStream fileOutputStream =null;
        try{
            fileOutputStream = new FileOutputStream(targetFilePath);
         //页面输出
                targetWorkBook.write(response.getOutputStream());
                //服务器硬盘输出
                targetWorkBook.write(fileOutputStream);
                //导出成功，
                //   wresponse.setErrcode("0");
                //  wresponse.setErrmsg("成功");


             } catch (Exception e) {
                e.printStackTrace();
                // wresponse.setErrcode("-1");
                // wresponse.setErrmsg("计划导出失败。"+e.getMessage());
            } finally {
                if (templateInputStream != null) {
                    try {
                        templateInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }



        }


}
