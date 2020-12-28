package cn.visolink.salesmanage.commissionpolicy.controller;


import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.commissionpolicy.dao.CmPolicyApplyMapper;
import cn.visolink.salesmanage.commissionpolicy.service.CmPolicyApplyService;
import com.alibaba.fastjson.JSONArray;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/policy")
@Api(tags = "佣金政策申请")
public class CmPolicyApplyController {


    @Autowired
    private CmPolicyApplyService cmPolicyApplyService;


    @Autowired
    private CmPolicyApplyMapper cmPolicyApplyMapper;


    @Log("查询佣金政策")
    @ApiOperation("查询佣金政策")
    @PostMapping("/getPolicy")
    public ResultBody getPolicy(@ApiParam(name = "map",value = "{\"policy_type\":\"政策类型  传1 查询中介政策 传0查询全民营销政策\",\"policy_code\":\"政策编码\",\"policy_name\":\"政策名称\",\"approve_status\":\"审批状态\"}") @RequestBody Map map){
        return ResultBody.success(cmPolicyApplyService.getPolicy(map));
    }

    @Log("查询佣金政策明细")
    @ApiOperation("查询佣金政策明细")
    @GetMapping("/getPolicyDetail")
    public ResultBody getPolicyDetail(@ApiParam(name = "policyId",value = "政策ID") String policyId,@ApiParam(name = "policyType",value = "政策类型") String policyType){
        return ResultBody.success(cmPolicyApplyService.getPolicyDetail(policyId,policyType));
    }

    @Log("查询佣金政策项目分期")
    @ApiOperation("查询佣金政策项目分期")
    @GetMapping("/getProjectStage")
    public ResultBody getProjectStage(@ApiParam(name = "policyId",value = "政策ID") String projectId){
        return ResultBody.success(cmPolicyApplyService.getProjectStage(projectId));
    }

    @Log("删除政策")
    @ApiOperation("删除政策")
    @GetMapping("/deletePolicy")
    public ResultBody deletePolicy(@ApiParam(name = "policyId",value = "政策ID") String policyId){
        return ResultBody.success(cmPolicyApplyService.deletePolicy(policyId));
    }

    @Log("禁用启用政策")
    @ApiOperation("删除政策")
    @GetMapping("/updatePolicyStatus")
    public ResultBody updatePolicyStatus(@ApiParam(name = "policyId",value = "政策ID") String policyId,@ApiParam(name = "state",value = "状态")String state){
        return ResultBody.success(cmPolicyApplyService.updatePolicyStatus(policyId,state));
    }

    @Log("全民经纪人-佣金政策申请")
    @ApiOperation("全民经纪人-佣金政策申请")
    @PostMapping("/brokerPolicyApply")
    public ResultBody brokerPolicyApply(@RequestBody Map map,HttpServletRequest request) throws IOException {
        String policyId = UUID.randomUUID().toString();
        String job_id = request.getHeader("jobid");
        String job_org_id = request.getHeader("joborgid");
        String org_id = request.getHeader("orgid");
        String username = request.getHeader("username");
        String employeeName = request.getHeader("employeeName");
        String org_level = request.getHeader("orglevel");
        employeeName=java.net.URLDecoder.decode(employeeName,"UTF-8");
        map.put("policyId",policyId);
        map.put("job_id",job_id);
        map.put("job_org_id",job_org_id);
        map.put("org_id",org_id);
        map.put("creator_name",employeeName);
        map.put("creator",username);
        map.put("org_level",org_level);
        String ss =map.get("org_list").toString();
        if(ss.length()>5){
            ss = ss.replace("{","{\"").replace("}","\"}");
            ss = ss.replace("},","}.");
            ss = ss.replace("=","\":\"");
            ss = ss.replace(",","\",\"");
            ss = ss.replace(".",",");
            ss = ss.replace(" ","");
            System.out.println(ss);
            List<Map<String,String>> listObjectFir = (List<Map<String,String>>) JSONArray.parse(ss);
            cmPolicyApplyMapper.saveOrgList(policyId,listObjectFir);
        }
        cmPolicyApplyService.brokerPolicyApply(map);
        return ResultBody.success(policyId);
    }

    @Log("佣金政策修改")
    @ApiOperation("佣金政策修改")
    @PostMapping("/updatePolicy")
    public ResultBody updatePolicy(@RequestBody Map map,HttpServletRequest request){
        String org_level = request.getHeader("orglevel");
        map.put("org_level",org_level);
        cmPolicyApplyMapper.delOrgList(map.get("policyId")+"");
        String ss =map.get("org_list").toString();
        if(ss.length()>5) {
            ss = ss.replace("{", "{\"").replace("}", "\"}");
            ss = ss.replace("},", "}.");
            ss = ss.replace("=", "\":\"");
            ss = ss.replace(",", "\",\"");
            ss = ss.replace(".", ",");
            ss = ss.replace(" ", "");
            System.out.println(ss);
            List<Map<String, String>> listObjectFir = (List<Map<String, String>>) JSONArray.parse(ss);
            cmPolicyApplyMapper.saveOrgList(map.get("policyId") + "", listObjectFir);
        }
        return ResultBody.success(cmPolicyApplyService.updatePolic(map));
    }



    @PostMapping("/test")
    public ResultBody test(@RequestBody Map map){
        if(map.get("fileList")!=""&&map.get("fileList")!=null){
            String fileStr = map.get("fileList")+"";
            fileStr = fileStr.substring(1,fileStr.length()-1);
            String[] arr = fileStr.split(",");
            for (String s : arr) {
                System.out.println(s);
            }
        }

        return ResultBody.success("");
    }

    @Log("中介-佣金政策申请")
    @ApiOperation("全民经纪人-佣金政策申请")
    @PostMapping("/agencyPolicyApply")
    public ResultBody agencyPolicyApply(@RequestBody Map map,HttpServletRequest request) throws UnsupportedEncodingException {
        String job_id = request.getHeader("jobid");
        String job_org_id = request.getHeader("joborgid");
        String org_id = request.getHeader("orgid");
        String username = request.getHeader("username");
        String employeeName = request.getHeader("employeeName");
        String org_level = request.getHeader("orglevel");
        employeeName=java.net.URLDecoder.decode(employeeName,"UTF-8");
        map.put("job_id",job_id);
        map.put("job_org_id",job_org_id);
        map.put("org_id",org_id);
        map.put("creator_name",employeeName);
        map.put("creator",username);
        String policyId = UUID.randomUUID().toString();
        map.put("policyId",policyId);
        map.put("org_level",org_level);
        String ss =map.get("org_list").toString();
        if(ss.length()>5) {
            ss = ss.replace("{", "{\"").replace("}", "\"}");
            ss = ss.replace("},", "}.");
            ss = ss.replace("=", "\":\"");
            ss = ss.replace(",", "\",\"");
            ss = ss.replace(".", ",");
            ss = ss.replace(" ", "");
            System.out.println(ss);
            List<Map<String, String>> listObjectFir = (List<Map<String, String>>) JSONArray.parse(ss);
            cmPolicyApplyMapper.saveOrgList(policyId, listObjectFir);
        }
     //   map.put("agency_json",map.get("agency_json")+"");
        cmPolicyApplyService.agencyPolicyApply(map);
        return ResultBody.success(policyId);
    }



    @Log("佣金政策导出")
    @CessBody
    @ApiOperation(value = "佣金政策导出")
    @GetMapping("/exportPolicy")
    public void exportPolicy(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String realpath = request.getServletContext().getRealPath("/");
        Map map = new HashMap();
        map.put("policy_type",request.getParameter("policy_type"));
        map.put("policy_code",request.getParameter("policy_code"));
        map.put("policy_name",request.getParameter("policy_name"));
        map.put("approve_status",request.getParameter("approve_status"));
        map.put("project_id",request.getParameter("project_id"));
        map.put("org_id",request.getParameter("org_id"));
        map.put("org_level",request.getParameter("org_level"));
        List<Map> result = cmPolicyApplyMapper.exportPolicy(map);

        //导出模版路径
        String templatePath = File.separator + "TemplateExcel" + File.separator + "policy.xlsx";
        //String templatePath="D:\\资料\\公司\\旭辉\\营销管控\\marketing-control-api\\cifimaster\\visolink-sales-api\\src\\main\\webapp\\TemplateExcel\\policy.xlsx";
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
            sheet.getRow(i+1).getCell(0).setCellValue(result.get(i).get("areaOrgName")+"");
            sheet.getRow(i+1).getCell(1).setCellValue(result.get(i).get("projectOrgName")+"");
            sheet.getRow(i+1).getCell(2).setCellValue(result.get(i).get("policy_code")+"");
            sheet.getRow(i+1).getCell(3).setCellValue(result.get(i).get("policy_name")+"");
            sheet.getRow(i+1).getCell(4).setCellValue(result.get(i).get("flow_status")+"");
            sheet.getRow(i+1).getCell(5).setCellValue(result.get(i).get("flow_id")+"");
            sheet.getRow(i+1).getCell(6).setCellValue(result.get(i).get("STATUS")+"");
            sheet.getRow(i+1).getCell(7).setCellValue(result.get(i).get("creator_name")+"");
            sheet.getRow(i+1).getCell(8).setCellValue(result.get(i).get("create_time")+"");
        }
        String planName = URLEncoder.encode( "政策导出.xlsx", "utf-8").replace("+", "%20");
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



    @GetMapping("/getProjectByPolicyID")
    @Log("获取项目ID")
    @ApiOperation(value = "获取项目ID")
    public ResultBody getProjectByPolicyID(String policyId) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String projectId=cmPolicyApplyMapper.getProjectByPolicyID(policyId);
        String flowId=cmPolicyApplyMapper.getFlowByPolicyID(policyId);
        String applyDate=cmPolicyApplyMapper.getDateByPolicyID(policyId);
        Map map =new HashMap();
        map.put("projectId",projectId);
        map.put("flowId",flowId);
        map.put("applyDate",simpleDateFormat.format(simpleDateFormat.parse(applyDate)));
        return ResultBody.success(map);
    }



}
