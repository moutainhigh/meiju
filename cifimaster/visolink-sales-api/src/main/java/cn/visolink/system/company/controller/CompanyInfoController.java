package cn.visolink.system.company.controller;

import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.company.model.CompanyInfo;
import cn.visolink.system.company.model.form.CompanyInfoForm;
import cn.visolink.system.company.model.vo.CompanyInfoVO;
import cn.visolink.system.company.service.CompanyInfoService;
import cn.visolink.system.company.service.CompanyService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * CompanyInfo前端控制器
 * </p>
 *
 * @author autoJob
 * @since 2019-08-31
 */
@RestController
@Api(tags = "公司管理")
@RequestMapping("/company")
public class CompanyInfoController {

    @Autowired
    public CompanyInfoService companyInfoService;

    @Autowired
    private CompanyService companyService;

    /**
     * 保存单条
     *
     * @return 是否添加成功
     */
    @Log("保存数据到CompanyInfo")
    @CessBody
    @ApiOperation(value = "保存", notes = "保存数据到CompanyInfo")
    @PostMapping(value = "/add.action")
    public Integer addCompanyInfo(@RequestBody(required = false) Map map) {
        String startTime = map.get("startTime") + "";
        if (map.get("startTime") != null && !"".equals(startTime) && !"null".equals(startTime)) {
            map.put("startTime", map.get("startTime").toString().replace("T16:00:00.000Z", ""));
        }

        String endTime = map.get("endTime") + "";
        if (map.get("endTime") != null && !"".equals(endTime) && !"null".equals(endTime)) {
            map.put("endTime", map.get("endTime").toString().replace("T16:00:00.000Z", ""));
        }
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateNowStr = sdf.format(d);
        int i = (int) (Math.random() * 900 + 100);
        String myStr = Integer.toString(i);
        String code = dateNowStr.substring(2, 4) + dateNowStr.substring(5, 7) + dateNowStr.substring(8, 10) + myStr;
        //去数据库查一下如果存在就重新生成
        map.put("companyCode", code);
        return companyService.insertCompany(map);
    }

    /**
     * 更新(根据主键id更新)
     *
     * @param param 修改参数
     * @return 是否更改成功
     */

    @Log("更新(根据主键id更新)CompanyInfo")
    @CessBody
    @ApiOperation(value = "更新数据", notes = "根据主键id更新CompanyInfo数据")
    @PostMapping(value = "/updateById.action")
    public Integer updateCompanyInfoById(@RequestBody(required = false) CompanyInfoForm param) {
        Integer result = companyInfoService.updateById(param);
        return result;
    }

    /**
     * 删除(根据主键id伪删除)
     *
     * @param id 主键id
     * @return 是否删除成功
     */

    @Log("删除(根据主键id伪删除)CompanyInfo")
    @CessBody
    @ApiOperation(value = "删除数据", notes = "根据主键id伪删除CompanyInfo数据")
    @PostMapping(value = "/deleteById.action")
    public Integer deleteCompanyInfoById(String id) {
        Integer result = companyInfoService.deleteById(id);
        return result;
    }

    /**
     * 根据主键id查询单条
     *
     * @param id 主键id
     * @return 查询结果
     */

    @Log("根据主键id查询单条CompanyInfo")
    @CessBody
    @ApiOperation(value = "获取单条数据", notes = "根据主键id获取CompanyInfo数据")
    @RequestMapping(value = "/getById.action", method = RequestMethod.POST)
    public CompanyInfoVO getCompanyInfoById(@RequestBody(required = false) String id) {
        CompanyInfoVO result = companyInfoService.selectById(id);
        return result;
    }

    /**
     * 查询全部
     *
     * @return 查询结果
     */

    @Log("查询全部CompanyInfo")
    @CessBody
    @ApiOperation(value = "全部查询", notes = "查询CompanyInfo全部数据")
    @RequestMapping(value = "/queryAll.action", method = RequestMethod.POST)
    public Map getCompanyInfoAll(@RequestBody Map map) {

        if (map.get("date") != null && !map.get("date").equals("")) {
            map.put("startTime", map.get("date").toString().substring(1, 11));
            map.put("endTime", map.get("date").toString().substring(27, 37));
        }
        int pageIndex = Integer.parseInt(map.get("pageIndex").toString());
        int pageSize = Integer.parseInt(map.get("pageSize").toString());
        int i = (pageIndex - 1) * pageSize;
        map.put("pageIndex", i);
        List<Map> result = companyService.getAllList(map);
        Integer count = companyService.getAllListCount(map);
        Map<Object, Object> resultMap = new HashMap<>();
        resultMap.put("list", result);
        resultMap.put("total", count);
        return resultMap;
    }

    @Log("查询全部项目")
    @CessBody
    @ApiOperation(value = "全部项目", notes = "查询全部项目")
    @RequestMapping(value = "/getAllProject.action", method = RequestMethod.GET)
    public List<Map> getAllProject() {
        return companyService.getAllProject();
    }

    /**
     * 分页查询
     *
     * @param param 查询条件
     * @return 查询结果
     */

    @Log("分页查询CompanyInfo")
    @CessBody
    @ApiOperation(value = "分页查询", notes = "分页查询CompanyInfo全部数据")
    @RequestMapping(value = "/queryPage.action", method = RequestMethod.POST)
    public IPage<CompanyInfo> getCompanyInfoPage(@RequestBody CompanyInfoForm param) {
        System.out.println(param.toString());
        IPage<CompanyInfo> result = companyInfoService.selectPage(param);
        return result;
    }

    @Log("分页查询公司的关联信息")
    @CessBody
    @ApiOperation(value = "查询公司的关联信息", notes = "查询公司的关联信息")
    @RequestMapping(value = "/queryAssInforData.action", method = RequestMethod.POST)
    public PageInfo getAssInforData(@RequestBody Map paramMap) {

        PageInfo inforData = companyService.getAssInforData(paramMap);
        return inforData;
    }

    @Log("修改公司信息")
    @CessBody
    @ApiOperation(value = "修改公司信息", notes = "修改信息")
    @RequestMapping(value = "/updateCompanyById.action", method = RequestMethod.POST)
    public Integer updateCompanyById(@RequestBody Map paramMap) {
        String startTime = paramMap.get("startTime") + "";

        if (paramMap.get("startTime") != null && !"".equals(startTime) && !"null".equals(startTime)) {
            paramMap.put("startTime", paramMap.get("startTime").toString().replace("T16:00:00.000Z", ""));
        }
        String endTime = paramMap.get("endTime") + "";
        if (paramMap.get("endTime") != null && !"".equals(endTime) && !"null".equals(endTime)) {
            paramMap.put("endTime", paramMap.get("endTime").toString().replace("T16:00:00.000Z", ""));
        }
        int result = companyService.updateCompanyById(paramMap);
        return result;
    }

    @Log("删除公司信息")
    @CessBody
    @ApiOperation(value = "删除公司信息", notes = "删除公司信息")
    @PostMapping(value = "/deleteCompanyById")
    public Integer deleteCompanyById(@RequestBody Map paramMap) {
        return companyService.deleteCompanyById(paramMap.get("companyId") + "");
    }
}

