package cn.visolink.system.projectmanager.controller;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.projectmanager.service.projectmanagerService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 孙林
 * @date:2019-9-10
 * */

@RestController
@Api(tags = "基础数据-项目管理")
@RequestMapping("/manager")
public class projectmanagerController {
    @Autowired
   public projectmanagerService managerservice;
    /**
     * 项目管理查询
     * */
    @Log("项目管理查询")
    @CessBody
    @ApiOperation(value = "项目管理查询")
    @PostMapping("/projectListSelect")
    public Map projectListSelect(@RequestBody Map<String,Object> map){
        Map result = managerservice.projectListSelect(map);
        return  result;
            }


    @Log("项目管理导出")
    @CessBody
    @ApiOperation(value = "项目管理导出")
    @GetMapping("/exportProject")
    public void exportProject(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String realpath = request.getServletContext().getRealPath("/");
        Map map = new HashMap();
        map.put("ProjectName",request.getParameter("ProjectName"));
        map.put("trader_type",request.getParameter("trader_type"));
        map.put("project_stage",request.getParameter("project_stage"));
        map.put("sales_master_type",request.getParameter("sales_master_type"));
        map.put("cifi_assume_money",request.getParameter("cifi_assume_money"));
        map.put("tenement_wp_project",request.getParameter("tenement_wp_project"));
        map.put("business_travel_project",request.getParameter("business_travel_project"));
        List<Map> result = managerservice.exportProject(map);

        //导出模版路径
        String templatePath = File.separator + "TemplateExcel" + File.separator + "projectManager.xlsx";
        //String templatePath="D:\\资料\\公司\\旭辉\\营销管控\\marketing-control-api\\cifimaster\\visolink-sales-api\\src\\main\\webapp\\TemplateExcel\\projectManager.xlsx";
        InputStream is = new FileInputStream(new File(realpath+templatePath));
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet =workbook.getSheetAt(0);
        for (int i = 0; i < result.size(); i++) {
            sheet.createRow(i+1).createCell(0);
            sheet.getRow(i+1).createCell(1);
            sheet.getRow(i+1).createCell(2);
            sheet.getRow(i+1).createCell(3);
            sheet.getRow(i+1).createCell(4);
            sheet.getRow(i+1).createCell(5);
            sheet.getRow(i+1).createCell(6);
            sheet.getRow(i+1).createCell(7);
            sheet.getRow(i+1).createCell(8);
            sheet.getRow(i+1).createCell(9);
            sheet.getRow(i+1).getCell(0).setCellValue(result.get(i).get("ID")+"");
            sheet.getRow(i+1).getCell(1).setCellValue(result.get(i).get("ProjectName")+"");
            sheet.getRow(i+1).getCell(2).setCellValue(result.get(i).get("flag")+"");
            sheet.getRow(i+1).getCell(3).setCellValue(result.get(i).get("monthly_type")+"");
            sheet.getRow(i+1).getCell(4).setCellValue(result.get(i).get("trader_type")+"");
            sheet.getRow(i+1).getCell(5).setCellValue(result.get(i).get("project_stage")+"");
            sheet.getRow(i+1).getCell(6).setCellValue(result.get(i).get("sales_master_type")+"");
            sheet.getRow(i+1).getCell(7).setCellValue(result.get(i).get("cifi_assume_money")+"");
            sheet.getRow(i+1).getCell(8).setCellValue(result.get(i).get("tenement_wp_project")+"");
            sheet.getRow(i+1).getCell(9).setCellValue(result.get(i).get("business_travel_project")+"");
        }
        String planName = URLEncoder.encode( "项目管理导出.xlsx", "utf-8").replace("+", "%20");
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
    /**
     * 启用禁用项目
     * */
    @Log("启用禁用项目")
    @CessBody
    @ApiOperation(value = "启用禁用项目")
    @PostMapping("/projectIsEnableUpdate")
    public Integer projectIsEnableUpdate(@RequestBody Map<String,Object> map){
        Integer result=managerservice.projectIsEnableUpdate(map);
        Integer number=null;
        if(result==1){
            number = 0;
        }else {
            number =1;
        }
                return number;
    }

    /**
     * 删除项目
     * */
    @Log("删除项目")
    @CessBody
    @ApiOperation(value = "删除项目")
   @PostMapping("/projectDeleteUpdate")
    public Integer projectDeleteUpdate(@RequestBody Map<String,Object> map){
        Integer result=managerservice.projectDeleteUpdate(map);
                return result;
    }


    @Log("增加项目和修改项目的调用方法")
    @CessBody
    @ApiOperation(value = "增加项目和修改项目的调用方法")
    @PostMapping("/projectexecute")
    @Transactional(rollbackFor = Exception.class)
    public String projectexecute(@RequestBody Map<String,String> dataMap, HttpServletRequest request, HttpServletResponse response)
    {
        String result=managerservice.projectexecute(dataMap,request,response);
        return result;
    }



    /**
     * 增加项目
     * */
    @Log("增加项目")
    @CessBody
    @ApiOperation(value = "增加项目")
    @PostMapping("/addNewProjectInfo")
    public Map <String, Object> addNewProjectInfo( @RequestBody Map <String, String> projectMap){
        Map <String, Object> result=managerservice.addNewProjectInfo(projectMap);
             return result;
        }


    @Log("添加完成修改对应组织的项目ID,查询当前组织的项目ID")
    @CessBody
    @ApiOperation(value = "添加完成修改对应组织的项目ID,查询当前组织的项目ID")
    @PostMapping("/updateOrgProject")
    public void updateOrgProject(@RequestBody Map <String, String> projectMap) {
        managerservice.updateOrgProject(projectMap);
    }


    @Log("修改项目信息")
    @CessBody
    @ApiOperation(value = "修改项目信息")
    @PostMapping("/updateProjectInfo")
  public  Map <String, Object> updateProjectInfo(@RequestBody Map <String, String> projectMap) {
        Map <String, Object> result=managerservice.updateProjectInfo(projectMap);
        return result;
    }

    @Log("查询单条项目的数据")
    @CessBody
    @ApiOperation(value = "修改项目信息")
    @PostMapping("/selectOneProject")
  public  List selectOneProject(@RequestBody Map <String, Object> projectMap) {
        List result=managerservice.selectOneProject(projectMap);
        System.out.println(result);
        return result;
    }

    @Log("修改参数的状态")
    @CessBody
    @ApiOperation(value = "修改项目信息")
    @PostMapping("/updateMenuStatus")
  public  Integer updateMenuStatus(@RequestBody Map <String, Object> projectMap) {
        Integer in = managerservice.updateMenuStatus(projectMap);
        System.out.println(in);
        return 0;
    }

    @Log("获取引入项目列表")
    @CessBody
    @ApiOperation(value = "获取引入项目列表")
    @PostMapping("/getWglProject")
    public List getWglProject(@RequestBody Map <String, Object> map){
        return  managerservice.getWglProject(map);
    }
    @Log("引入项目")
    @CessBody
    @ApiOperation(value = "引入项目")
    @PostMapping("/addGlProject")
    public ResultBody addGlProject(@RequestBody Map <String, Object> map){
        return managerservice.addGlProject(map);
    }
    @Log("修改项目")
    @CessBody
    @ApiOperation(value = "修改项目")
    @PostMapping("/updateProject")
    public ResultBody updateProject(@RequestBody Map<String, Object> map){
        return managerservice.updateProject(map);
    }
}
