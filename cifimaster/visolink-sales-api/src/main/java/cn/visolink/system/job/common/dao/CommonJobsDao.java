package cn.visolink.system.job.common.dao;

import cn.visolink.system.job.common.model.CommonJobs;
import cn.visolink.system.job.common.model.form.CommonJobsForm;
import cn.visolink.system.job.common.model.vo.CommonJobsVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author autoJob
 * @since 2019-08-29
 */
public interface CommonJobsDao extends BaseMapper<CommonJobs> {
    /**
     * 根据AuthCompanyID 和ProductID 和JobName 查询通用岗位列表
     * @param
     * @return
     */
    public List<CommonJobsVO> findListBySystemCommonJobs(CommonJobsForm commonjobsForm);


    List<Map> getJobSByCommonJob(@Param("commonJobId") String commonJobId);


    /*四层级数据授权*/
    List<Map> getFourOrgData();

    /**
     * 查找已授权的菜单列表
     * @param map
     * @return
     */
    public List<Map> findMenusListByPermissions(Map map);


    /**
     * 查找已授权的功能列表
     * @param map
     * @return
     */
    public List<Map> findFunctionsListByPermissions(Map map);

    /**
     * 查找该岗位已有的菜单和功能
     * @param map
     * @return
     */
    public List<Map> findCommonJobFunctionsByPermissions(Map map);

    /**
     * 查找该岗位已有的菜单
     * @param map
     * @return
     */
    public List<String> findOldMenusByPermissions(Map map);

    /**
     * 添加通用岗位
     * @return
     */
    int systemCommonJob_Insert(CommonJobsForm commonJobsForm);

    /*
     * *获取岗位最大Code
     * */
    String getJobCodeMax();

    /**
     * 删除通用岗位
     * @return
     */
    Integer systemCommonJobDelete(CommonJobsForm commonJobsForm);

    /**
     * 启用禁用通用岗位
     * @param commonJobsForm
     * @return
     */
    Integer systemCommonJobStatusUpdate(CommonJobsForm commonJobsForm);

    /**
     * 更新通用岗位
     * @param commonJobsForm
     * @return
     */
    Integer systemCommonJobUpdate(CommonJobsForm commonJobsForm);

}
