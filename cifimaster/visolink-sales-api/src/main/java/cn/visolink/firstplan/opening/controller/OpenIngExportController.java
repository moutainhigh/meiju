package cn.visolink.firstplan.opening.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.opening.service.OpenThisDayService;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.utils.JsonDealUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFBorderFormatting;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2020.04.20
 */
@Controller
@Api(tags = "opening")
@RequestMapping("/opening")
public class OpenIngExportController {


    @Autowired
    public OpenThisDayService openThisDayService;


    @Log("当日播报导出")
    @ApiOperation(value = "当日播报导出")
    @GetMapping(value = "/exportOpenThisDayInfo")
    public void exportOpenThisDayInfo(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String filePath;
        String realpath = request.getServletContext().getRealPath("/");
        String plan_node_id = request.getParameter("plan_node_id");
        String plan_id = request.getParameter("plan_id");
        String project_id = request.getParameter("project_id");
        Map params = new HashMap(3);
        params.put("plan_node_id",plan_node_id);
        params.put("plan_id",plan_id);
        params.put("project_id",project_id);
        Map openThisDay=openThisDayService.selectOpenThisDayInfo(params);
        if(openThisDay==null || openThisDay.size()==0){
            openThisDay=openThisDayService.getOpenPageInfo(params);
        }
        else if(openThisDay.get("flow_status")==null ||openThisDay.get("flow_status").equals("") || openThisDay.get("flow_status").equals(10)||openThisDay.get("flow_status").equals(2)){
            //如果是保存状态
            Map initMap=openThisDayService.getOpenPageInfo(params);
            initMap.remove("open_time");
            openThisDay.putAll(initMap);
        }
         //导出模版路径
        String templatePath = File.separator + "TemplateExcel" + File.separator + "openIngExport.xlsx";
       //String templatePath="D:\\资料\\公司\\旭辉\\营销管控\\marketing-control-api\\cifimaster\\visolink-sales-api\\src\\main\\webapp\\TemplateExcel\\openIngExport.xlsx";
        InputStream is = new FileInputStream(new File(realpath+templatePath));
        Workbook workbook = new XSSFWorkbook(is);
        //获取到第一个工作表
        org.apache.poi.ss.usermodel.Sheet sheet =workbook.getSheetAt(0);
        workbook.removeSheetAt(1);
        ObjectMapper s = new JsonDealUtils();
        System.out.println();
        Map<String,Object> jsonToMap = JSONObject.parseObject(s.writeValueAsString(openThisDay));
        String projectName = openThisDayService.selectProjectNameById(project_id);
        sheet.getRow(0).getCell(0).setCellValue(projectName+" - 首开 -  当日 - 填报导出数据");
        sheet.getRow(2).getCell(2).setCellValue(jsonToMap.get("project_duty")+"");
        if(openThisDay!=null&&openThisDay.size()>1){
            if(jsonToMap.get("take_card_time")!=null){
                sheet.getRow(4).getCell(2).setCellValue(jsonToMap.get("take_card_time")+"");
                sheet.getRow(4).getCell(5).setCellValue(jsonToMap.get("open_time")+"");
                sheet.getRow(5).getCell(2).setCellValue(jsonToMap.get("take_card_type")+"");
                sheet.getRow(5).getCell(5).setCellValue(jsonToMap.get("push_real_type")+"");
                sheet.getRow(6).getCell(2).setCellValue(jsonToMap.get("take_num")+"");
                sheet.getRow(6).getCell(5).setCellValue(jsonToMap.get("push_num")+"");
                sheet.getRow(7).getCell(2).setCellValue(jsonToMap.get("take_value")+"");
                sheet.getRow(7).getCell(5).setCellValue(jsonToMap.get("push_value")+"");
                sheet.getRow(8).getCell(2).setCellValue(jsonToMap.get("take_avg_price")+"");
                sheet.getRow(8).getCell(5).setCellValue(jsonToMap.get("push_avg_price")+"");
            }
            sheet.getRow(12).getCell(2).setCellValue(jsonToMap.get("designtwo_selling_num")+"");
            sheet.getRow(12).getCell(3).setCellValue(jsonToMap.get("actual_selling_num")+"");
            sheet.getRow(12).getCell(4).setCellValue(jsonToMap.get("bias_selling_num")+"");
            sheet.getRow(12).getCell(5).setCellValue(jsonToMap.get("biasper_selling_num")+"");
            sheet.getRow(13).getCell(2).setCellValue(jsonToMap.get("designtwo_selling_value")+"");
            sheet.getRow(13).getCell(3).setCellValue(jsonToMap.get("actual_selling_value")+"");
            sheet.getRow(13).getCell(4).setCellValue(jsonToMap.get("bias_selling_value")+"");
            sheet.getRow(13).getCell(5).setCellValue(jsonToMap.get("biasper_selling_value")+"");
            String res = openThisDay.get("avg")+"";

            res = res.replace(" ","");
            JSONObject json = new JSONObject(openThisDay);
            JSONArray item = json.getJSONArray("avg");
            List<Map> list = new ArrayList<>();
            //获取模版第5行第一列的单元格样式
            XSSFRow row5 = (XSSFRow) sheet.getRow(12);
//获取第一格
            XSSFCell cell5_0 = row5.getCell(0);
            XSSFCell cell5_1 = row5.getCell(2);
//获取样式
            XSSFCellStyle cell5_0cellStyle = cell5_0.getCellStyle();
            XSSFCellStyle cell5_1cellStyle = cell5_1.getCellStyle();
            if(item!=null){

            list = item.toJavaList(Map.class);

            if(list.size()>0) {
               for (int i = 0; i < list.size(); i++) {
               //    sheet.createRow(i+14).createCell(0).setCellValue("去化均价(元/㎡)-" + list.get(i).get("product_type") + "");
                   sheet.createRow(i+14).createCell(0);
                   sheet.getRow(i+14).createCell(1).setCellValue(list.get(i).get("product_type") + "");
                   sheet.getRow(i+14).createCell(2).setCellValue(list.get(i).get("targ_avg") + "");
                   sheet.getRow(i+14).createCell(3).setCellValue(list.get(i).get("actual_avg") + "");
                   sheet.getRow(i+14).createCell(4).setCellValue(list.get(i).get("bias_price") + "");
                   sheet.getRow(i+14).createCell(5).setCellValue(list.get(i).get("bias_per") + "");
                   sheet.getRow(i+14).getCell(0).setCellStyle(cell5_0cellStyle);
                   sheet.getRow(i+14).getCell(1).setCellStyle(cell5_0cellStyle);
                   sheet.getRow(i+14).getCell(2).setCellStyle(cell5_1cellStyle);
                   sheet.getRow(i+14).getCell(3).setCellStyle(cell5_1cellStyle);
                   sheet.getRow(i+14).getCell(4).setCellStyle(cell5_1cellStyle);
                   sheet.getRow(i+14).getCell(5).setCellStyle(cell5_1cellStyle);
               }
           }
                sheet.getRow(14).getCell(0).setCellValue("去化均价(元/㎡)");
            }

            if(list.size()>1){
                CellRangeAddress region1 = new CellRangeAddress(14, 14+list.size()-1, (short) 0, (short) 0);
                sheet.addMergedRegion(region1);
            }
            CellRangeAddress region2 = new CellRangeAddress(14+list.size(), 14+list.size(), (short) 0, (short) 1);
            CellRangeAddress region3 = new CellRangeAddress(15+list.size(), 15+list.size(), (short) 0, (short) 1);
            CellRangeAddress region4 = new CellRangeAddress(16+list.size(), 16+list.size(), (short) 0, (short) 1);
            CellRangeAddress region5 = new CellRangeAddress(17+list.size(), 17+list.size(), (short) 0, (short) 1);
            CellRangeAddress region6 = new CellRangeAddress(18+list.size(), 18+list.size(), (short) 0, (short) 1);
            CellRangeAddress region7 = new CellRangeAddress(19+list.size(), 19+list.size(), (short) 0, (short) 1);
            CellRangeAddress region8 = new CellRangeAddress(20+list.size(), 20+list.size(), (short) 0, (short) 1);
//参数1：起始行 参数2：终止行 参数3：起始列 参数4：终止列

            sheet.addMergedRegion(region2);
            sheet.addMergedRegion(region3);
            sheet.addMergedRegion(region4);
            sheet.addMergedRegion(region5);
            sheet.addMergedRegion(region6);
            sheet.addMergedRegion(region7);
            sheet.addMergedRegion(region8);


            //合并的单元格样式
            sheet.createRow(14+ list.size()).createCell(0).setCellValue("取证货值去化率%");
            sheet.getRow(14+ list.size()).createCell(1);
            sheet.getRow(14+ list.size()).createCell(2).setCellValue(jsonToMap.get("designtwo_selling_takeper")+"");
            sheet.getRow(14+ list.size()).createCell(3).setCellValue(jsonToMap.get("actual_selling_takeper")+"");
            sheet.getRow(14+ list.size()).createCell(4).setCellValue(jsonToMap.get("bias_selling_takeper")+"");
            sheet.getRow(14+ list.size()).createCell(5).setCellValue(jsonToMap.get("biasper_selling_takeper")+"");

            sheet.getRow(14+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(14+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(14+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(14+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(14+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(14+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);

            sheet.createRow(15+ list.size()).createCell(0).setCellValue("累计来访(组)");
            sheet.getRow(15+ list.size()).createCell(1);
            sheet.getRow(15+ list.size()).createCell(2).setCellValue(jsonToMap.get("designtwo_add_visit")+"");
            sheet.getRow(15+ list.size()).createCell(3).setCellValue(jsonToMap.get("actual_add_visit")+"");
            sheet.getRow(15+ list.size()).createCell(4).setCellValue(jsonToMap.get("bias_add_visit")+"");
            sheet.getRow(15+ list.size()).createCell(5).setCellValue(jsonToMap.get("biasper_add_visit")+"");

            sheet.getRow(15+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(15+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(15+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(15+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(15+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(15+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);


            sheet.createRow(16+ list.size()).createCell(0).setCellValue("累计大卡(组)");
            sheet.getRow(16+ list.size()).createCell(1);
            sheet.getRow(16+ list.size()).createCell(2).setCellValue(jsonToMap.get("designtwo_add_big")+"");
            sheet.getRow(16+ list.size()).createCell(3).setCellValue(jsonToMap.get("actual_add_big")+"");
            sheet.getRow(16+ list.size()).createCell(4).setCellValue(jsonToMap.get("bias_add_big")+"");
            sheet.getRow(16+ list.size()).createCell(5).setCellValue(jsonToMap.get("biasper_add_big")+"");

            sheet.getRow(16+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(16+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(16+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(16+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(16+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(16+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);

            sheet.createRow(17+ list.size()).createCell(0).setCellValue("大卡转化率%");
            sheet.getRow(17+ list.size()).createCell(1);
            sheet.getRow(17+ list.size()).createCell(2).setCellValue(jsonToMap.get("designtwo_add_big_per")+"");
            sheet.getRow(17+ list.size()).createCell(3).setCellValue(jsonToMap.get("actual_add_big_per")+"");
            sheet.getRow(17+ list.size()).createCell(4).setCellValue(jsonToMap.get("bias_add_big_per")+"");
            sheet.getRow(17+ list.size()).createCell(5).setCellValue(jsonToMap.get("biasper_add_big_per")+"");

            sheet.getRow(17+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(17+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(17+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(17+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(17+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(17+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);

            sheet.createRow(18+ list.size()).createCell(0).setCellValue("当天到场客户量(组)");
            sheet.getRow(18+ list.size()).createCell(1);
            sheet.getRow(18+ list.size()).createCell(2).setCellValue(jsonToMap.get("designtwo_this_client")+"");
            sheet.getRow(18+ list.size()).createCell(3).setCellValue(jsonToMap.get("actual_this_client")+"");
            sheet.getRow(18+ list.size()).createCell(4).setCellValue(jsonToMap.get("bias_this_client")+"");
            sheet.getRow(18+ list.size()).createCell(5).setCellValue(jsonToMap.get("biasper_this_client")+"");

            sheet.getRow(18+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(18+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(18+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(18+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(18+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(18+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);

            sheet.createRow(19+ list.size()).createCell(0).setCellValue("当天到场客户转化率（%）");
            sheet.getRow(19+ list.size()).createCell(1);
            sheet.getRow(19+ list.size()).createCell(2).setCellValue(jsonToMap.get("designtwo_this_clientper")+"");
            sheet.getRow(19+ list.size()).createCell(3).setCellValue(jsonToMap.get("actual_this_clientper")+"");
            sheet.getRow(19+ list.size()).createCell(4).setCellValue(jsonToMap.get("bias_this_clientper")+"");
            sheet.getRow(19+ list.size()).createCell(5).setCellValue(jsonToMap.get("biasper_this_clientper")+"");

            sheet.getRow(19+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(19+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(19+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(19+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(19+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(19+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);

            sheet.createRow(20+ list.size()).createCell(0).setCellValue("成交率%");
            sheet.getRow(20+ list.size()).createCell(1);
            sheet.getRow(20+ list.size()).createCell(2).setCellValue(jsonToMap.get("designtwo_finish")+"");
            sheet.getRow(20+ list.size()).createCell(3).setCellValue(jsonToMap.get("actual_finish")+"");
            sheet.getRow(20+ list.size()).createCell(4).setCellValue(jsonToMap.get("bias_finish")+"");
            sheet.getRow(20+ list.size()).createCell(5).setCellValue(jsonToMap.get("biasper_finish")+"");

            sheet.getRow(20+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(20+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(20+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(20+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(20+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(20+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);


        /*


            sheet.getRow(21+list.size()).getCell(1).setCellValue(jsonToMap.get("designtwo_finish")+"");
            sheet.getRow(21+list.size()).getCell(2).setCellValue(jsonToMap.get("actual_finish")+"");
            sheet.getRow(21+list.size()).getCell(3).setCellValue(jsonToMap.get("bias_finish")+"");
            sheet.getRow(21+list.size()).getCell(4).setCellValue(jsonToMap.get("biasper_finish")+"");*/
        }

        //清空response
        String planName = URLEncoder.encode( "首开计划开盘当日数据导出.xlsx", "utf-8").replace("+", "%20");
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(planName.getBytes(), "utf-8"));
      //  response.addHeader("Content-Disposition", "attachment;filename=首开计划开盘当日数据导出.xlsx");
        OutputStream os = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/vnd.ms-excel;charset=gb2312");
        response.resetBuffer();
        response.setCharacterEncoding("UTF-8");
        //将excel写入到输出流中
        workbook.write(os);
        os.flush();
        os.close();
    }

    @Log("次日播报导出")
    @ApiOperation(value = "次日播报导出")
    @GetMapping(value = "/exportOpenNextDayInfo")
    public void exportOpenNextDayInfo(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String filePath;
        String realpath = request.getServletContext().getRealPath("/");
        String plan_node_id = request.getParameter("plan_node_id");
        String plan_id = request.getParameter("plan_id");
        String project_id = request.getParameter("project_id");
        //通过项目ID获取项目名称
        Map params = new HashMap(3);
        params.put("plan_node_id",plan_node_id);
        params.put("plan_id",plan_id);
        params.put("project_id",project_id);
        String projectName = openThisDayService.selectProjectNameById(project_id);
        Map openMorrow=openThisDayService.selectOpenMorrowDayInfo(params);
        String create=params.get("create")+"";
        if(openMorrow==null || openMorrow.size()==0 || create.equals("new")){
            openMorrow=openThisDayService.initOperMorrow(params.get("plan_id")+"");
        }
        String templatePath = File.separator + "TemplateExcel" + File.separator + "openIngExport.xlsx";
        //String templatePath="D:\\资料\\公司\\旭辉\\营销管控\\marketing-control-api\\cifimaster\\visolink-sales-api\\src\\main\\webapp\\TemplateExcel\\openIngExport.xlsx";
        InputStream is = new FileInputStream(new File(realpath+templatePath));
        Workbook workbook = new XSSFWorkbook(is);
        //获取到第一个工作表
        org.apache.poi.ss.usermodel.Sheet sheet =workbook.getSheetAt(1);
        workbook.removeSheetAt(0);
        ObjectMapper s = new JsonDealUtils();
        if(openMorrow==null){

        }else{
            //导出模版路径
            //String templatePath = File.separator + "TemplateExcel" + File.separator + "firstPlanNodeExportTemplate.xlsx";
            XSSFRow row5 = (XSSFRow) sheet.getRow(3);
            XSSFRow row0 = (XSSFRow) sheet.getRow(1);
//获取第一格
            XSSFCell cell5_0 = row5.getCell(0);
            XSSFCell cell5_1 = row5.getCell(1);
            XSSFCell cell5_2 = row5.getCell(0);
//获取样式
            XSSFCellStyle cell5_0cellStyle = cell5_0.getCellStyle();
            XSSFCellStyle cell5_1cellStyle = cell5_1.getCellStyle();
            XSSFCellStyle cell6_1cellStyle = cell5_2.getCellStyle();
            Map<String,Object> jsonToMap = JSONObject.parseObject(s.writeValueAsString(openMorrow));
            if(jsonToMap.size()==1){
                jsonToMap = JSONObject.parseObject(jsonToMap.get("info").toString());
            }
            sheet.getRow(0).getCell(0).setCellValue(projectName+"- 首开 -  次日 - 填报导出数据");
            sheet.getRow(3).getCell(2).setCellValue(jsonToMap.get("invest_open_node")+"");
            sheet.getRow(3).getCell(3).setCellValue(jsonToMap.get("rules_open_node")+"");
            sheet.getRow(3).getCell(4).setCellValue(jsonToMap.get("estimate_open_node")+"");
            sheet.getRow(3).getCell(5).setCellValue(jsonToMap.get("cash_open_node")+"");
            sheet.getRow(3).getCell(6).setCellValue(jsonToMap.get("bias_open_node")+"");
            sheet.getRow(3).getCell(7).setCellValue("/");

            sheet.getRow(4).getCell(2).setCellValue(jsonToMap.get("invest_xreal_type")+"");
            sheet.getRow(4).getCell(3).setCellValue(jsonToMap.get("rules_xreal_type")+"");
            sheet.getRow(4).getCell(4).setCellValue(jsonToMap.get("estimate_xreal_type")+"");
            sheet.getRow(4).getCell(5).setCellValue(jsonToMap.get("cash_xreal_type")+"");
            sheet.getRow(4).getCell(6).setCellValue("/");
            sheet.getRow(4).getCell(7).setCellValue("/");

            sheet.getRow(5).getCell(2).setCellValue(jsonToMap.get("invest_take_card_value")+"");
            sheet.getRow(5).getCell(3).setCellValue(jsonToMap.get("rules_take_card_value")+"");
            sheet.getRow(5).getCell(4).setCellValue(jsonToMap.get("estimate_take_card_value")+"");
            sheet.getRow(5).getCell(5).setCellValue(jsonToMap.get("cash_take_card_value")+"");
            sheet.getRow(5).getCell(6).setCellValue(jsonToMap.get("bias_take_card_value")+"");
            sheet.getRow(5).getCell(7).setCellValue(jsonToMap.get("biasper_take_card_value")+"");

            sheet.getRow(6).getCell(2).setCellValue(jsonToMap.get("invest_push_value")+"");
            sheet.getRow(6).getCell(3).setCellValue(jsonToMap.get("rules_push_value")+"");
            sheet.getRow(6).getCell(4).setCellValue(jsonToMap.get("estimate_push_value")+"");
            sheet.getRow(6).getCell(5).setCellValue(jsonToMap.get("cash_push_value")+"");
            sheet.getRow(6).getCell(6).setCellValue(jsonToMap.get("bias_push_value")+"");
            sheet.getRow(6).getCell(7).setCellValue(jsonToMap.get("biasper_push_value")+"");

            sheet.getRow(7).getCell(2).setCellValue(jsonToMap.get("invest_selling_value")+"");
            sheet.getRow(7).getCell(3).setCellValue(jsonToMap.get("rules_selling_value")+"");
            sheet.getRow(7).getCell(4).setCellValue(jsonToMap.get("estimate_selling_value")+"");
            sheet.getRow(7).getCell(5).setCellValue(jsonToMap.get("cash_selling_value")+"");
            sheet.getRow(7).getCell(6).setCellValue(jsonToMap.get("bias_selling_value")+"");
            sheet.getRow(7).getCell(7).setCellValue(jsonToMap.get("biasper_selling_value")+"");

            JSONObject json = new JSONObject(jsonToMap);
            JSONArray item = json.getJSONArray("avg");
            List<Map> list = new ArrayList<>();
            if(item!=null){
            System.out.println(json.get("avg"));

            list = item.toJavaList(Map.class);

            if(list.size()>0) {
                for (int i = 0; i < list.size(); i++) {
                    //    sheet.createRow(i+14).createCell(0).setCellValue("去化均价(元/㎡)-" + list.get(i).get("product_type") + "");
                    sheet.createRow(i+8).createCell(0);
                    sheet.getRow(i+8).createCell(1).setCellValue(list.get(i).get("product_type") + "");
                    sheet.getRow(i+8).createCell(2).setCellValue(list.get(i).get("invest_avg") + "");
                    sheet.getRow(i+8).createCell(3).setCellValue(list.get(i).get("rules_avg") + "");
                    sheet.getRow(i+8).createCell(4).setCellValue(list.get(i).get("estimate_price") + "");
                    sheet.getRow(i+8).createCell(5).setCellValue(list.get(i).get("cash_price") + "");
                    sheet.getRow(i+8).createCell(6).setCellValue(list.get(i).get("estimate_price") + "");
                    sheet.getRow(i+8).createCell(7).setCellValue(list.get(i).get("bias_per") + "");
                    sheet.getRow(i+8).getCell(0).setCellStyle(cell5_0cellStyle);
                    sheet.getRow(i+8).getCell(1).setCellStyle(cell5_0cellStyle);
                    sheet.getRow(i+8).getCell(2).setCellStyle(cell5_1cellStyle);
                    sheet.getRow(i+8).getCell(3).setCellStyle(cell5_1cellStyle);
                    sheet.getRow(i+8).getCell(4).setCellStyle(cell5_1cellStyle);
                    sheet.getRow(i+8).getCell(5).setCellStyle(cell5_1cellStyle);
                    sheet.getRow(i+8).getCell(6).setCellStyle(cell5_1cellStyle);
                    sheet.getRow(i+8).getCell(7).setCellStyle(cell5_1cellStyle);
                }
            }
                sheet.getRow(8).getCell(0).setCellValue("去化均价(元/㎡)");
            }
            if(list.size()>1){
                CellRangeAddress region1 = new CellRangeAddress(8, 8+list.size()-1, (short) 0, (short) 0);
                sheet.addMergedRegion(region1);
            }
            CellRangeAddress region2 = new CellRangeAddress(8+list.size(), 8+list.size(), (short) 0, (short) 1);
            CellRangeAddress region3 = new CellRangeAddress(9+list.size(), 9+list.size(), (short) 0, (short) 1);
            CellRangeAddress region4 = new CellRangeAddress(10+list.size(), 10+list.size(), (short) 0, (short) 1);
            CellRangeAddress region5 = new CellRangeAddress(11+list.size(), 11+list.size(), (short) 0, (short) 1);
            CellRangeAddress region6 = new CellRangeAddress(12+list.size(), 12+list.size(), (short) 0, (short) 1);
            CellRangeAddress region7 = new CellRangeAddress(13+list.size(), 13+list.size(), (short) 0, (short) 1);
            CellRangeAddress region8 = new CellRangeAddress(14+list.size(), 14+list.size(), (short) 0, (short) 1);

            CellRangeAddress region13 = new CellRangeAddress(15+list.size(), 15+list.size(), (short) 0, (short) 1);
            CellRangeAddress region14 = new CellRangeAddress(16+list.size(), 16+list.size(), (short) 0, (short) 1);
            CellRangeAddress region15 = new CellRangeAddress(17+list.size(), 17+list.size(), (short) 0, (short) 1);
            CellRangeAddress region16 = new CellRangeAddress(18+list.size(), 18+list.size(), (short) 0, (short) 1);

            CellRangeAddress region9 = new CellRangeAddress(15+list.size(), 15+list.size(), (short) 2, (short) 7);
            CellRangeAddress region10 = new CellRangeAddress(16+list.size(), 16+list.size(), (short) 2, (short) 7);
            CellRangeAddress region11 = new CellRangeAddress(17+list.size(), 17+list.size(), (short) 2, (short) 7);
            CellRangeAddress region12 = new CellRangeAddress(18+list.size(), 18+list.size(), (short) 2, (short) 7);
//参数1：起始行 参数2：终止行 参数3：起始列 参数4：终止列

            sheet.addMergedRegion(region2);
            sheet.addMergedRegion(region3);
            sheet.addMergedRegion(region4);
            sheet.addMergedRegion(region5);
            sheet.addMergedRegion(region6);
            sheet.addMergedRegion(region7);
            sheet.addMergedRegion(region8);
            sheet.addMergedRegion(region9);
            sheet.addMergedRegion(region10);
            sheet.addMergedRegion(region11);
            sheet.addMergedRegion(region12);
            sheet.addMergedRegion(region13);
            sheet.addMergedRegion(region14);
            sheet.addMergedRegion(region15);
            sheet.addMergedRegion(region16);


              sheet.createRow(8+list.size()).createCell(0).setCellValue("首开取证货值去化率%");
            sheet.getRow(8+ list.size()).createCell(1);
            sheet.getRow(8+ list.size()).createCell(2).setCellValue(jsonToMap.get("invest_take_card_per")+"");
            sheet.getRow(8+ list.size()).createCell(3).setCellValue(jsonToMap.get("rules_take_card_per")+"");
            sheet.getRow(8+ list.size()).createCell(4).setCellValue(jsonToMap.get("estimate_take_card_per")+"");
            sheet.getRow(8+ list.size()).createCell(5).setCellValue(jsonToMap.get("bias_take_card_per")+"");
            sheet.getRow(8+ list.size()).createCell(6).setCellValue(jsonToMap.get("biasper_take_card_per")+"");
            sheet.getRow(8+ list.size()).createCell(7).setCellValue(jsonToMap.get("cash_take_card_per")+"");

            sheet.getRow(8+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(8+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(8+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(8+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(8+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(8+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);
            sheet.getRow(8+ list.size()).getCell(6).setCellStyle(cell5_1cellStyle);
            sheet.getRow(8+ list.size()).getCell(7).setCellStyle(cell5_1cellStyle);


            sheet.createRow(9+list.size()).createCell(0).setCellValue("创造利润率%");
            sheet.getRow(9+ list.size()).createCell(1);
            sheet.getRow(9+ list.size()).createCell(2).setCellValue(jsonToMap.get("invest_create_per")+"");
            sheet.getRow(9+ list.size()).createCell(3).setCellValue(jsonToMap.get("rules_create_per")+"");
            sheet.getRow(9+ list.size()).createCell(4).setCellValue(jsonToMap.get("estimate_create_per")+"");
            sheet.getRow(9+ list.size()).createCell(5).setCellValue(jsonToMap.get("bias_create_per")+"");
            sheet.getRow(9+ list.size()).createCell(6).setCellValue(jsonToMap.get("biasper_create_per")+"");
            sheet.getRow(9+ list.size()).createCell(7).setCellValue(jsonToMap.get("cash_create_per")+"");

            sheet.getRow(9+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(9+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(9+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(9+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(9+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(9+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);
            sheet.getRow(9+ list.size()).getCell(6).setCellStyle(cell5_1cellStyle);
            sheet.getRow(9+ list.size()).getCell(7).setCellStyle(cell5_1cellStyle);


            sheet.createRow(10+list.size()).createCell(0).setCellValue("整盘利润率%");
            sheet.getRow(10+ list.size()).createCell(1);
            sheet.getRow(10+ list.size()).createCell(2).setCellValue(jsonToMap.get("invest_all_per")+"");
            sheet.getRow(10+ list.size()).createCell(3).setCellValue(jsonToMap.get("rules_all_per")+"");
            sheet.getRow(10+ list.size()).createCell(4).setCellValue(jsonToMap.get("estimate_all_per")+"");
            sheet.getRow(10+ list.size()).createCell(5).setCellValue(jsonToMap.get("bias_all_per")+"");
            sheet.getRow(10+ list.size()).createCell(6).setCellValue(jsonToMap.get("biasper_all_per")+"");
            sheet.getRow(10+ list.size()).createCell(7).setCellValue(jsonToMap.get("cash_all_per")+"");

            sheet.getRow(10+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(10+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(10+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(10+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(10+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(10+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);
            sheet.getRow(10+ list.size()).getCell(6).setCellStyle(cell5_1cellStyle);
            sheet.getRow(10+ list.size()).getCell(7).setCellStyle(cell5_1cellStyle);

            sheet.createRow(11+list.size()).createCell(0).setCellValue("非融IRR");
            sheet.getRow(11+ list.size()).createCell(1);
            sheet.getRow(11+ list.size()).createCell(2).setCellValue(jsonToMap.get("invest_irr")+"");
            sheet.getRow(11+ list.size()).createCell(3).setCellValue(jsonToMap.get("rules_irr")+"");
            sheet.getRow(11+ list.size()).createCell(4).setCellValue(jsonToMap.get("estimate_irr")+"");
            sheet.getRow(11+ list.size()).createCell(5).setCellValue(jsonToMap.get("bias_irr")+"");
            sheet.getRow(11+ list.size()).createCell(6).setCellValue(jsonToMap.get("biasper_irr")+"");
            sheet.getRow(11+ list.size()).createCell(7).setCellValue(jsonToMap.get("cash_irr")+"");

            sheet.getRow(11+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(11+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(11+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(11+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(11+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(11+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);
            sheet.getRow(11+ list.size()).getCell(6).setCellStyle(cell5_1cellStyle);
            sheet.getRow(11+ list.size()).getCell(7).setCellStyle(cell5_1cellStyle);

            sheet.createRow(12+list.size()).createCell(0).setCellValue("静态投资回收期(月)");
            sheet.getRow(12+ list.size()).createCell(1);
            sheet.getRow(12+ list.size()).createCell(2).setCellValue(jsonToMap.get("invest_payback")+"");
            sheet.getRow(12+ list.size()).createCell(3).setCellValue(jsonToMap.get("rules_payback")+"");
            sheet.getRow(12+ list.size()).createCell(4).setCellValue(jsonToMap.get("estimate_payback")+"");
            sheet.getRow(12+ list.size()).createCell(5).setCellValue(jsonToMap.get("bias_payback")+"");
            sheet.getRow(12+ list.size()).createCell(6).setCellValue(jsonToMap.get("biasper_payback")+"");
            sheet.getRow(12+ list.size()).createCell(7).setCellValue(jsonToMap.get("cash_payback")+"");

            sheet.getRow(12+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(12+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(12+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(12+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(12+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(12+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);
            sheet.getRow(12+ list.size()).getCell(6).setCellStyle(cell5_1cellStyle);
            sheet.getRow(12+ list.size()).getCell(7).setCellStyle(cell5_1cellStyle);

            sheet.createRow(13+list.size()).createCell(0);
            sheet.getRow(13+ list.size()).createCell(1);
            sheet.getRow(13+ list.size()).createCell(2);
            sheet.getRow(13+ list.size()).createCell(3);
            sheet.getRow(13+ list.size()).createCell(4);
            sheet.getRow(13+ list.size()).createCell(5);
            sheet.getRow(13+ list.size()).createCell(6);
            sheet.getRow(13+ list.size()).createCell(7);

            sheet.createRow(14+list.size()).createCell(0).setCellValue("偏差核心原因说明");
            sheet.getRow(14+ list.size()).getCell(0).setCellStyle(cell6_1cellStyle);
            sheet.getRow(14+ list.size()).createCell(1);
            sheet.getRow(14+ list.size()).createCell(2);
            sheet.getRow(14+ list.size()).createCell(3);
            sheet.getRow(14+ list.size()).createCell(4);
            sheet.getRow(14+ list.size()).createCell(5);
            sheet.getRow(14+ list.size()).createCell(6);
            sheet.getRow(14+ list.size()).createCell(7);

            sheet.getRow(14+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(14+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(14+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(14+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(14+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(14+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);
            sheet.getRow(14+ list.size()).getCell(6).setCellStyle(cell5_1cellStyle);
            sheet.getRow(14+ list.size()).getCell(7).setCellStyle(cell5_1cellStyle);

            sheet.createRow(15+list.size()).createCell(0).setCellValue("市场");
            sheet.getRow(15+ list.size()).createCell(1);
            sheet.getRow(15+ list.size()).createCell(2).setCellValue(jsonToMap.get("content_bazaar")+"");
            sheet.getRow(15+ list.size()).createCell(3);
            sheet.getRow(15+ list.size()).createCell(4);
            sheet.getRow(15+ list.size()).createCell(5);
            sheet.getRow(15+ list.size()).createCell(6);
            sheet.getRow(15+ list.size()).createCell(7);

            sheet.getRow(15+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(15+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(15+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(15+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(15+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(15+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);
            sheet.getRow(15+ list.size()).getCell(6).setCellStyle(cell5_1cellStyle);
            sheet.getRow(15+ list.size()).getCell(7).setCellStyle(cell5_1cellStyle);


            sheet.createRow(16+list.size()).createCell(0).setCellValue("团队");
            sheet.getRow(16+ list.size()).createCell(1);
            sheet.getRow(16+ list.size()).createCell(2).setCellValue(jsonToMap.get("content_team")+"");
            sheet.getRow(16+ list.size()).createCell(3);
            sheet.getRow(16+ list.size()).createCell(4);
            sheet.getRow(16+ list.size()).createCell(5);
            sheet.getRow(16+ list.size()).createCell(6);
            sheet.getRow(16+ list.size()).createCell(7);


            sheet.getRow(16+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(16+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(16+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(16+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(16+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(16+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);
            sheet.getRow(16+ list.size()).getCell(6).setCellStyle(cell5_1cellStyle);
            sheet.getRow(16+ list.size()).getCell(7).setCellStyle(cell5_1cellStyle);


            sheet.createRow(17+list.size()).createCell(0).setCellValue("产品");
            sheet.getRow(17+ list.size()).createCell(1);
            sheet.getRow(17+ list.size()).createCell(2).setCellValue(jsonToMap.get("content_product")+"");
            sheet.getRow(17+ list.size()).createCell(3);
            sheet.getRow(17+ list.size()).createCell(4);
            sheet.getRow(17+ list.size()).createCell(5);
            sheet.getRow(17+ list.size()).createCell(6);
            sheet.getRow(17+ list.size()).createCell(7);

            sheet.getRow(17+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(17+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(17+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(17+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(17+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(17+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);
            sheet.getRow(17+ list.size()).getCell(6).setCellStyle(cell5_1cellStyle);
            sheet.getRow(17+ list.size()).getCell(7).setCellStyle(cell5_1cellStyle);


            sheet.createRow(18+list.size()).createCell(0).setCellValue("政策");
            sheet.getRow(18+ list.size()).createCell(1);
            sheet.getRow(18+ list.size()).createCell(2).setCellValue(jsonToMap.get("content_policy")+"");
            sheet.getRow(18+ list.size()).createCell(3);
            sheet.getRow(18+ list.size()).createCell(4);
            sheet.getRow(18+ list.size()).createCell(5);
            sheet.getRow(18+ list.size()).createCell(6);
            sheet.getRow(18+ list.size()).createCell(7);

            sheet.getRow(18+ list.size()).getCell(0).setCellStyle(cell5_0cellStyle);
            sheet.getRow(18+ list.size()).getCell(1).setCellStyle(cell5_0cellStyle);
            sheet.getRow(18+ list.size()).getCell(2).setCellStyle(cell5_1cellStyle);
            sheet.getRow(18+ list.size()).getCell(3).setCellStyle(cell5_1cellStyle);
            sheet.getRow(18+ list.size()).getCell(4).setCellStyle(cell5_1cellStyle);
            sheet.getRow(18+ list.size()).getCell(5).setCellStyle(cell5_1cellStyle);
            sheet.getRow(18+ list.size()).getCell(6).setCellStyle(cell5_1cellStyle);
            sheet.getRow(18+ list.size()).getCell(7).setCellStyle(cell5_1cellStyle);
        /*
            sheet.getRow(15).getCell(1).setCellValue(jsonToMap.get("content_bazaar")+"");
            sheet.getRow(16).getCell(1).setCellValue(jsonToMap.get("content_team")+"");

            sheet.getRow(17).getCell(1).setCellValue(jsonToMap.get("content_product")+"");

            sheet.getRow(18).getCell(1).setCellValue(jsonToMap.get("content_policy")+"");*/
        }


        //清空response
       String planName = URLEncoder.encode( "首开计划开盘次日数据导出.xlsx", "utf-8").replace("+", "%20");
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(planName.getBytes(), "utf-8"));
       // response.addHeader("Content-Disposition", "attachment;filename=.xlsx","utf-8");
        OutputStream os = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/vnd.ms-excel;charset=gb2312");
        response.resetBuffer();
        response.setCharacterEncoding("UTF-8");
        //将excel写入到输出流中
        workbook.write(os);
        os.flush();
        os.close();
    }



    public static Map<String,String> mapStringToMap(String str){
        str=str.substring(2, str.length()-2);
        String[] strs=str.split(",");
        Map<String,String> map = new HashMap<String, String>();
        for (String string : strs) {
            String key=string.split("=")[0];
            String value=string.split("=")[1];
            map.put(key, value);
        }
        return map;
    }
}
