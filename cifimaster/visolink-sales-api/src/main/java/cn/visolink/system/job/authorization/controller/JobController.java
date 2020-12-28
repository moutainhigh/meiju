package cn.visolink.system.job.authorization.controller;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.job.authorization.service.JobService;
import com.github.pagehelper.PageInfo;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.09
 */
@RestController
@Api(tags = "岗位授权")
@RequestMapping("system/job")
public class JobController {

    @Autowired
    private JobService jobService;

    /**
     * 获取指定岗位
     */

    @Log("获取指定岗位")
    @CessBody
    @ApiOperation(value = "获取指定岗位", notes = "获取指定岗位")
    @RequestMapping(value = "/getJobByAuthId.action", method = RequestMethod.GET)
    public PageInfo<Map> getJobByAuthId(@ApiParam(name = "fullPath", value = "组织路径") String fullPath,
                                        @ApiParam(name = "OrgID", value = "组织ID") String OrgID, String pageSize, String pageNum,String isIdm) {
        Map map = new HashMap();
        map.put("fullPath", fullPath);
        map.put("OrgID", OrgID);
        map.put("pageSize", pageSize);
        map.put("pageNum", pageNum);
        map.put("isIdm", isIdm);
        System.out.println("=======" + map);
        return jobService.getJobByAuthId(map);
    }

    /**
     * 查询所有的组织结构
     */

    @Log("查询所有的组织结构")
    @CessBody
    @ApiOperation(value = "查询所有的组织结构", notes = "查询所有的组织结构")
    @RequestMapping(value = "/getAllOrg.action", method = RequestMethod.GET)
    public List<Map> getAllOrg(@ApiParam(name = "AuthCompanyID", value = "认证公司Id") String AuthCompanyID,
                               @ApiParam(name = "ProductID", value = "产品ID") String ProductID,
                               @ApiParam(name = "OrgID", value = "组织ID") String OrgID,
                               @ApiParam(name = "PID", value = "父ID") String PID) {
        Map map = new HashMap();
        map.put("AuthCompanyID", AuthCompanyID);
        map.put("ProductID", ProductID);
        map.put("OrgID", OrgID);
        map.put("PID", PID);
        return jobService.getAllOrg(map);
    }

    /**
     * 获取通用岗位列表
     */
    @Log("获取通用岗位列表")
    @CessBody
    @ApiOperation(value = "获取通用岗位列表", notes = "获取通用岗位列表")
    @GetMapping("/getAllCommonJob.action")
    public List<Map> getAllCommonJob(@ApiParam(name = "AuthCompanyID", value = "认证公司Id") String AuthCompanyID,
                                     @ApiParam(name = "ProductID", value = "产品ID") String ProductID,
                                     @ApiParam(name = "JobName", value = "岗位名称") String JobName
    ) {
        Map map = new HashMap();
        map.put("AuthCompanyID", AuthCompanyID);
        map.put("ProductID", ProductID);
        map.put("JobName", JobName);
        return jobService.getAllCommonJob(map);
    }

    /**
     * 查询岗位下的人员列表，或根据姓名查询人员
     *
     * @param reqMap
     */
    @Log("查询岗位下的人员列表，或根据姓名查询人员")
    @CessBody
    @ApiOperation(value = "查询岗位下的人员列表，或根据姓名查询人员", notes = "查询岗位下的人员列表，或根据姓名查询人员")
    @PostMapping("getSystemUserList.action")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "AuthCompanyID", value = "认证公司ID"),
            @ApiImplicitParam(name = "ProductID", value = "产品ID"),
            @ApiImplicitParam(name = "JobID", value = "岗位ID"),
            @ApiImplicitParam(name = "UserName", value = "用户名称"),
            @ApiImplicitParam(name = "pageIndex", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量")
    })
    public Map getSystemUserList(@RequestBody Map reqMap) {
        return jobService.getSystemUserList(reqMap);
    }

    /**
     * 获取当前和下属所有组织岗位
     *
     * @param reqMap
     */
    @Log("获取当前和下属所有组织岗位")
    @CessBody
    @ApiOperation(value = "获取当前和下属所有组织岗位", notes = "获取当前和下属所有组织岗位")
    @PostMapping("getSystemJobAllList.action")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "AuthCompanyID", value = "认证公司ID"),
            @ApiImplicitParam(name = "ProductID", value = "产品ID"),
            @ApiImplicitParam(name = "OrgID", value = "组织ID")

    })
    public List<Map> getSystemJobAllList(@RequestBody Map reqMap) {

        return jobService.getSystemJobAllList(reqMap);
    }

    /**
     * 新增岗位-插入Jobs信息
     *
     * @param reqMap
     */
    @Log("新增岗位-插入Jobs信息")
    @CessBody
    @ApiOperation(value = "新增岗位-插入Jobs信息", notes = "新增岗位-插入Jobs信息")
    @PostMapping("saveSystemJobForManagement.action")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "JobCode", value = "岗位代码"),
            @ApiImplicitParam(name = "JobName", value = "岗位名称"),
            @ApiImplicitParam(name = "JobDesc", value = "岗位描述"),
            @ApiImplicitParam(name = "JobPID", value = "上级岗位ID"),
            @ApiImplicitParam(name = "CommonJobID", value = "通用岗位ID"),
            @ApiImplicitParam(name = "JobOrgID", value = "所属组织ID"),
            @ApiImplicitParam(name = "AuthCompanyID", value = "认证公司ID"),
            @ApiImplicitParam(name = "ProductID", value = "产品ID"),
            @ApiImplicitParam(name = "Creator", value = "创建人"),
            @ApiImplicitParam(name = "Editor", value = "编辑人"),
            @ApiImplicitParam(name = "Status", value = "状态"),

    })
    public int saveSystemJobForManagement(@RequestBody Map reqMap) {

        return jobService.saveSystemJobForManagement(reqMap);
    }

    /**
     * 组织岗位功能列表查询(前端后端功能授权)
     *
     * @param reqMap
     */
    @Log("组织岗位功能列表查询(前端后端功能授权)")
    @CessBody
    @ApiOperation(value = "组织岗位功能列表查询(前端后端功能授权)", notes = "组织岗位功能列表查询(前端后端功能授权)")
    @PostMapping("getSystemJobAuthByUserId.action")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "UserID", value = "用户ID"),
            @ApiImplicitParam(name = "AuthCompanyID", value = "认证公司ID"),
            @ApiImplicitParam(name = "ProductID", value = "产品ID"),
            @ApiImplicitParam(name = "JobID", value = "岗位ID"),
            @ApiImplicitParam(name = "menusType", value = "菜单类型"),
            @ApiImplicitParam(name = "CommomJobID", value = "通用岗位ID"),

    })
    public Map getSystemJobAuthByUserId(@RequestBody Map reqMap) {

        return jobService.getSystemJobAuthByUserId(reqMap);
    }

    /**
     * 前后端功能授权保存
     *
     * @param reqMap
     */
    @Log("前后端功能授权保存")
    @CessBody
    @ApiOperation(value = "前后端功能授权保存", notes = "前后端功能授权保存")
    @PostMapping("saveSystemJobAuthByManagement.action")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "JobID", value = "岗位ID"),
            @ApiImplicitParam(name = "OldMenus", value = "旧菜单"),
            @ApiImplicitParam(name = "OldFunctions", value = "旧功能"),
            @ApiImplicitParam(name = "Menus", value = "菜单"),
            @ApiImplicitParam(name = "Functions", value = "功能"),
            @ApiImplicitParam(name = "MenusType", value = "菜单类型"),

    })
    public String saveSystemJobAuthByManagement(@RequestBody Map reqMap) {

        return jobService.saveSystemJobAuthByManagement(reqMap);
    }

    /**
     * 更新岗位信息
     *
     * @param reqMap
     */
    @Log("更新岗位信息")
    @CessBody
    @ApiOperation(value = "更新岗位信息", notes = "更新岗位信息")
    @PostMapping("modifySystemJobByUserId.action")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ID", value = "ID"),
            @ApiImplicitParam(name = "JobCode", value = "岗位代码"),
            @ApiImplicitParam(name = "JobName", value = "岗位名称"),
            @ApiImplicitParam(name = "JobDesc", value = "岗位描述"),
            @ApiImplicitParam(name = "JobPID", value = "上级岗位ID"),
            @ApiImplicitParam(name = "CommonJobID", value = "通用岗位ID"),
            @ApiImplicitParam(name = "JobOrgID", value = "所属组织ID"),
            @ApiImplicitParam(name = "AuthCompanyID", value = "认证公司ID"),
            @ApiImplicitParam(name = "ProductID", value = "产品ID"),
            @ApiImplicitParam(name = "Creator", value = "创建人"),
            @ApiImplicitParam(name = "Editor", value = "编辑人"),
            @ApiImplicitParam(name = "Status", value = "状态"),

    })
    public int modifySystemJobByUserId(@RequestBody Map reqMap) {

        return jobService.modifySystemJobByUserId(reqMap);
    }

    /**
     * 管理端删除岗位信息
     *
     * @param reqMap
     */
    @Log("删除岗位信息")
    @CessBody
    @ApiOperation(value = "删除岗位信息", notes = "删除岗位信息")
    @PostMapping("removeSystemJobByUserId.action")
    @ApiModelProperty(name = "ID", value = "用户ID")
    public int removeSystemJobByUserId(@RequestBody Map reqMap) {

        return jobService.removeSystemJobByUserId(reqMap);
    }

    /**
     * 管理端查询引入用户
     *
     * @param reqMap
     */
    @Log("查询引入用户")
    @CessBody
    @ApiOperation(value = "查询引入用户", notes = "查询引入用户")
    @PostMapping("pullinUser.action")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "alias", value = "账号"),
            @ApiImplicitParam(name = "usercn", value = "用户姓名"),
            @ApiImplicitParam(name = "pageIndex", value = "页码"),
            @ApiImplicitParam(name = "pageSize", value = "数量")
    })
    public Map pullinUser(@RequestBody Map reqMap) {
        return jobService.pullinUser(reqMap);
    }

    /**
     * 管理端保存用户
     *
     * @param reqMap
     */
    @Log("保存用户")
    @CessBody
    @ApiOperation(value = "保存用户", notes = "保存引入用户")
    @PostMapping("saveSystemUser.action")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "JobID", value = "岗位ID"),
            @ApiImplicitParam(name = "UserIDS", value = "用户名称"),
            @ApiImplicitParam(name = "types", value = "员工姓名")
    })
    public int saveSystemUser(@RequestBody Map reqMap) {
        return jobService.saveSystemUser(reqMap);
    }

    /**
     * 删除岗位下的用户信息
     *
     * @param reqMap
     * @return
     */
    @Log("删除用户")
    @CessBody
    @ApiOperation(value = "删除用户", notes = "删除用户")
    @PostMapping("removeSystemJobUserRel.action")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "JobID", value = "岗位ID"),
            @ApiImplicitParam(name = "UserID", value = "用户ID")
    })
    public int removeSystemJobUserRel(@RequestBody Map reqMap) {

        return jobService.removeSystemJobUserRel(reqMap);
    }

    /**
     * 编辑岗位下的用户信息
     *
     * @param reqMap
     * @return
     */
    @Log("编辑用户")
    @CessBody
    @ApiOperation(value = "编辑用户", notes = "编辑用户")
    @PostMapping("modifySystemJobUserRel.action")
    @ApiModelProperty(name = "reqMap", value = "请求参数")
    public int modifySystemJobUserRel(@RequestBody Map reqMap) {

        return jobService.modifySystemJobUserRel(reqMap);
    }

    /**
     * 保存岗位下的用户信息
     *
     * @param reqMap
     * @return
     */
    @Log("保存用户")
    @CessBody
    @ApiOperation(value = "保存用户", notes = "保存用户")
    @PostMapping("saveSystemJobUserRel.action")
    @ApiModelProperty(name = "reqMap", value = "请求参数")
    public int saveSystemJobUserRel(@RequestBody Map reqMap) {
        return jobService.saveSystemJobUserRel(reqMap);
    }


    /**
     * 获取所有菜单
     */

    @Log("获取功能菜单")
    @ApiOperation(value = "获取功能菜单", notes = "获取功能菜单")
    @PostMapping("/menu/list")
    @ApiModelProperty(name = "jobId", value = "角色ID")
    public ResultBody getAllMenus(@RequestBody Map map) {
        return jobService.getAllMenu(map.get("jobId").toString());
    }

    /**
     * 保存所有菜单
     */

    @Log("保存所有菜单")
    @ApiOperation(value = "保存所有菜单", notes = "保存所有菜单")
    @PostMapping("/menu/save")
    @ApiModelProperty(name = "jobId", value = "角色ID")
    public ResultBody saveJobMenus(@RequestBody Map map) {
        return jobService.saveJobMenus(map, map.get("jobId").toString());
    }

    /**
     * 保存通用菜单
     */

    @Log("保存通用菜单")
    @ApiOperation(value = "保存通用菜单", notes = "保存通用菜单")
    @PostMapping("common/menu/save")
    @ApiModelProperty(name = "jobId", value = "角色ID")
    public ResultBody saveCommonJobMenus(@RequestBody Map map) {
        return jobService.saveCommonJobMenus(map, map.get("jobId").toString());
    }

    /**
     * 保存通用菜单
     */

    @Log("保存通用报表授权")
    @ApiOperation(value = "保存通用报表授权", notes = "保存通用报表授权")
    @PostMapping("common/reportMenu/save")
    @ApiModelProperty(name = "jobId", value = "通用岗位")
    public ResultBody saveCommonReportmenus(@RequestBody Map map) {
        return jobService.saveCommonReportMenu(map);
    }

    /***
     * 获取通用岗位菜单
     * **/
    @Log("获取通用功能菜单")
    @ApiOperation(value = "获取通用功能菜单", notes = "获取通用功能菜单")
    @PostMapping("/common/menu")
    @ApiModelProperty(name = "jobId", value = "角色ID")
    public ResultBody getCommonAllMenus(@RequestBody Map map) {
        return jobService.getCommonAllMenu(map.get("jobId").toString());
    }

    /***
     * 获取通用岗位菜单
     * **/
    @Log("获取通用报表功能")
    @ApiOperation(value = "获取通用报表功能", notes = "获取通用报表功能")
    @PostMapping("/common/ReportMenu")
    @ApiModelProperty(name = "jobId", value = "通用岗位id")
    public ResultBody getCommonAllReportMenu(@RequestBody Map map) {
        return jobService.getCommonAllReportMenu(map.get("jobId").toString());
    }

    /***
     * 获取所有中介公司
     * **/
    @Log("获取所有中介公司")
    @ApiOperation(value = "获取所有中介公司", notes = "获取所有中介公司")
    @GetMapping("/company/all")
    public ResultBody getAllCompanyInfo() {
        return jobService.getAllCompanyInfo();
    }


    /***
     * 项目组织
     * **/
    @Log("项目组织")
    @ApiOperation(value = "项目组织", notes = "项目组织")
    @GetMapping("/org/project")
    public ResultBody getAllOrgProject() {
        return jobService.getAllOrgProject();
    }

    /***
     * 项目组织
     * **/
    @Log("区域集团/事业部")
    @ApiOperation(value = "区域集团/事业部", notes = "区域集团/事业部")
    @GetMapping("/org/project2")
    public ResultBody getAllOrgProject2() {
        return jobService.getAllOrgProject2();
    }

    /***
     * 更新项目
     * **/
    @Log("更新项目")
    @ApiOperation(value = "更新项目", notes = "更新项目")
    @PostMapping("/project/update")
    public ResultBody updateProject(@RequestBody Map map) {
        System.out.println(map);
        String toker = "";
        String anchang = "";
        if (map.get("checkList").toString().indexOf("1") != -1) {
            toker += "1,";
        }
        if (map.get("checkList").toString().indexOf("2") != -1) {
            toker += "2,";
        }
        if (map.get("checkList").toString().indexOf("3") != -1) {
            toker += "3,";
        }
        if (map.get("checkList").toString().indexOf("4") != -1) {
            toker += "4,";
        }
        if (map.get("checkList").toString().indexOf("5") != -1) {
            toker += "5,";
        }
        if (map.get("checkList").toString().indexOf("6") != -1) {
            toker += "6,";
        }
        if (map.get("checkList").toString().indexOf("7") != -1) {
            toker += "7,";
        }
        if (map.get("checkList").toString().indexOf("8") != -1) {
            toker += "8,";
        }
        if (map.get("checkListwo").toString().indexOf("1") != -1) {
            anchang += "1,";
        }
        if (map.get("checkListwo").toString().indexOf("2") != -1) {
            anchang += "2,";
        }
        if (map.get("checkListwo").toString().indexOf("3") != -1) {
            anchang += "3,";
        }
        if (map.get("checkListwo").toString().indexOf("4") != -1) {
            anchang += "4,";
        }
        if (map.get("checkListwo").toString().indexOf("5") != -1) {
            anchang += "5,";
        }
        if (map.get("checkListwo").toString().indexOf("6") != -1) {
            anchang += "6,";
        }
        if (map.get("checkListwo").toString().indexOf("7") != -1) {
            anchang += "7,";
        }
        if (map.get("checkListwo").toString().indexOf("8") != -1) {
            anchang += "8,";
        }
        map.put("tokerResetType", toker);
        map.put("anChangResetType", anchang);
        if (map.get("projectStatus").toString().equals("在售")) {
            map.put("projectStatus", 5001);
        }
        if (map.get("projectStatus").toString().equals("待售")) {
            map.put("projectStatus", 5002);
        }
        if (map.get("projectStatus").toString().equals("热销")) {
            map.put("projectStatus", 5003);
        }
        if (map.get("projectStatus").toString().equals("售罄")) {
            map.put("projectStatus", 5004);
        }
        if (map.get("status").toString().equals("启用")) {
            map.put("status", 1);
        }
        if (map.get("status").toString().equals("禁用")) {
            map.put("status", 2);
        }
        return jobService.updateProject(map);
    }


    @Log("报表功能授权列表查询")
    @ApiOperation(value = "报表功能授权列表查询", notes = "报表功能授权列表查询")
    @PostMapping("/report/getFunctions")
    public ResultBody functonAuthorization(@RequestBody Map map) {
        try {
            return jobService.getAuthorizationData(map);
        } catch (Exception e) {
            ResultBody<Object> resultBody = new ResultBody<>();
            resultBody.setCode(-11045);
            resultBody.setMessages("授权功能列表查询失败!");
            e.printStackTrace();
            return resultBody;
        }
    }

    @Log("保存报表授权")
    @ApiOperation(value = "报表功能授权保存授权信息", notes = "报表功能授权保存")
    @PostMapping("/report/saveAuthorization")
    public ResultBody saveAuthorization(@RequestBody Map map) {
        try {
            return jobService.saveAuthorization(map);
        } catch (Exception e) {
            ResultBody<Object> resultBody = new ResultBody<>();
            resultBody.setCode(-11046);
            resultBody.setMessages("功能授权失败，请联系管理员!");
            return resultBody;
        }
    }


    @Log("新增岗位-查询组织下拉选择列表")
    @ApiOperation(value = "新增岗位-查询组织下拉选择列表", notes = "新增岗位-查询组织下拉选择列表")
    @PostMapping("/org/getOrgTreeData")
    public ResultBody getOrgTreeData(@RequestBody Map map) {
            return jobService.getOrgData(map);
    }
}
