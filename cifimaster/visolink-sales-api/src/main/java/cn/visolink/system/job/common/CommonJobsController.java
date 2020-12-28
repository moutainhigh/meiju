package cn.visolink.system.job.common;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.job.common.dao.CommonJobsDao;
import cn.visolink.system.job.common.model.form.CommonJobsForm;
import cn.visolink.system.job.common.model.vo.CommonJobsVO;
import cn.visolink.system.job.common.service.CommonJobsService;
import cn.visolink.utils.CommUtilsUpdate;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 通用岗位前端控制器
 * </p>
 *
 * @author autoJob
 * @since 2019-08-29
 */
@RestController
@Api(tags = "通用岗位")
@RequestMapping("/job/common")
public class CommonJobsController {

    @Autowired
    public CommonJobsService commonjobsService;

    @Autowired
    private CommonJobsDao commonJobsDao;

    /**
     * 保存单条
     * @param param 保存参数
     * @return 是否添加成功
     */
   /* @Log("保存数据到Commonjobs")
    @CessBody
    @ApiOperation(value = "保存", notes = "保存数据到Commonjobs")
    @PostMapping(value = "/add.action")
    public Integer addCommonjobs(@RequestBody(required = false) CommonjobsForm param){
            Integer result= commonjobsService.save(param);
            return result;
            }*/

    /**
     * 更新(根据主键id更新)
     * @param param 修改参数
     * @return 是否更改成功
     */

    /*@Log("更新(根据主键id更新)Commonjobs")
    @CessBody
    @ApiOperation(value = "更新数据", notes = "根据主键id更新Commonjobs数据")
    @PostMapping(value = "/updateById.action")
    public Integer updateCommonjobsById(@RequestBody(required = false) CommonjobsForm param){
            Integer result= commonjobsService.updateById(param);
            return result;
            }*/

    /**
     * 删除(根据主键id伪删除)
     * @param id 主键id
     * @return 是否删除成功
     */

    /*@Log("删除(根据主键id伪删除)Commonjobs")
    @CessBody
    @ApiOperation(value = "删除数据", notes = "根据主键id伪删除Commonjobs数据")
    @PostMapping(value = "/deleteById.action")
    public Integer deleteCommonjobsById(String id){
            Integer result= commonjobsService.deleteById(id);
            return result;
            }*/

    /**
     * 根据主键id查询单条
     * @param
     * @return 查询结果
     */

    /*@Log("根据主键id查询单条Commonjobs")
    @CessBody
    @ApiOperation(value = "获取单条数据", notes = "根据主键id获取Commonjobs数据")
    @RequestMapping(value = "/getById.action", method = RequestMethod.POST)
    public CommonjobsVO getCommonjobsById(@RequestBody(required = false) String id){
        CommonjobsVO result= commonjobsService.selectById(id);
            return result;
            }*/


    @Log("查询通用岗位")
    @CessBody
    @ApiOperation(value = "全部查询", notes = "全部岗位数据")
    @RequestMapping(value = "/commonJobsSelectAll", method = RequestMethod.POST)
    public PageInfo<CommonJobsVO> commonJobsSelectAll(@RequestBody CommonJobsForm commonjobsForm) {
        PageInfo<CommonJobsVO> result = commonjobsService.commonJobsSelectAll(commonjobsForm);
        return result;
    }

    @Log("岗位明细")
    @ApiOperation(value = "岗位明细",notes = "岗位明细")
    @GetMapping("/getJobSByCommonJob")
    public ResultBody getJobSByCommonJob(String commonJobId,Integer pageSize,Integer pageNum){
        PageHelper.startPage(pageNum,pageSize);
        List<Map> list=commonJobsDao.getJobSByCommonJob(commonJobId);
        return ResultBody.success(new PageInfo<Map>(list));
    }

    @ApiOperation(value = "四级组织授权")
    @PostMapping("/getFourOrgData")
    public Map getFourOrgData(){
        List<Map> mapRegion = commonjobsService.getFourOrgData();
        Map menusMap = CommUtilsUpdate.buildTree(mapRegion);
        return menusMap;
    }


    @Log("添加通用岗位")
    @CessBody
    @ApiOperation(value = "添加通用岗位", notes = "岗位数据")
    @RequestMapping(value = "/systemCommonJob_Insert.action", method = RequestMethod.POST)
    public Integer systemCommonJob_Insert(@RequestBody CommonJobsForm commonjobsForm) {
        //先获取数据库最大的自定义code
        String jobCode = commonjobsService.getJobCodeMax();
        String newCode=(Integer.parseInt(jobCode.substring(3,jobCode.length()))+1)+"";
        commonjobsForm.setJobCode("ZDY"+newCode);
        //String newJobcode = jobCode.substring()
        Integer in = commonjobsService.systemCommonJob_Insert(commonjobsForm);
        Integer res = null;
        if(in==1){
            res = 0;
        }else {
            res = 1;
        }
        return res;
    }

    @Log("删除通用岗位")
    @CessBody
    @ApiOperation(value = "删除通用岗位", notes = "岗位数据")
    @RequestMapping(value = "/systemCommonJobDelete.action", method = RequestMethod.POST)
    public Integer systemCommonJobDelete(@RequestBody CommonJobsForm commonjobsForm) {
        Integer in = commonJobsDao.systemCommonJobDelete(commonjobsForm);
        return 0;
    }

    @Log("启用禁用通用岗位")
    @CessBody
    @ApiOperation(value = "启用禁用通用岗位 ", notes = "岗位数据")
    @RequestMapping(value = "/systemCommonJobStatusUpdate.action", method = RequestMethod.POST)
    public Integer systemCommonJobStatusUpdate(@RequestBody CommonJobsForm commonjobsForm) {
        Integer in = commonJobsDao.systemCommonJobStatusUpdate(commonjobsForm);
        return 0;
    }

    @Log("更新通用岗位")
    @CessBody
    @ApiOperation(value = "更新通用岗位", notes = "岗位数据")
    @RequestMapping(value = "/systemCommonJobUpdate.action", method = RequestMethod.POST)
    public Integer systemCommonJobUpdate(@RequestBody CommonJobsForm commonjobsForm) {
        Integer in = commonJobsDao.systemCommonJobUpdate(commonjobsForm);
        System.out.println(in);
        return 0;
    }




}

