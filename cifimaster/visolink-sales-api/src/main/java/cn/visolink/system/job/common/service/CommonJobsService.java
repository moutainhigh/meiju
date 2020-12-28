package cn.visolink.system.job.common.service;

import cn.visolink.system.job.common.model.CommonJobs;
import cn.visolink.system.job.common.model.form.CommonJobsForm;
import cn.visolink.system.job.common.model.vo.CommonJobsVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * CommonJobs服务类
 * </p>
 *
 * @author autoJob
 * @since 2019-08-29
 */
public interface CommonJobsService extends IService<CommonJobs> {
    /**
     * 保存信息对象
     *
     * @param record 信息对象
     * @return 影响记录数
     */
    Integer save(CommonJobsForm record);

    /**
     * 根据主键更新信息对象
     *
     * @param record 信息对象
     * @return 影响记录数
     */
    Integer updateById(CommonJobsForm record);

    /**
     * 根据主键删除信息对象
     * 逻辑删除,字段改为删除态
     *
     * @param id 主键
     * @return 影响记录数
     */
    Integer deleteById(String id);

    /**
     * 根据主键查询信息对象
     *
     * @param id 主键
     * @return 信息对象
     */
    CommonJobsVO selectById(String id);

    /**
     * 根据主键查询信息对象
     *
     * @param record 查询请求条件
     * @return 信息列表
     */
    List<CommonJobsVO> selectAll(CommonJobsForm record);

    /**
     * 分页查询信息对象
     *
     * @param record 查询请求条件
     * @return 信息列表
     */
    IPage<CommonJobsVO> selectPage(CommonJobsForm record);

    /**
     * 通用岗位列表
     * @param commonJobsForm
     * @return
     */
    PageInfo<CommonJobsVO> commonJobsSelectAll(CommonJobsForm commonJobsForm);

    /**
     * 岗位组明细
     * */
    PageInfo getJobSByCommonJob(String commonJobId,Integer pageSize,Integer pageNum);

    /*
    * 四层级数据授权
    * */
    List<Map> getFourOrgData();

    /**
     * 通用岗位授权列表
     * @param map
     * @return
     */
    Map findSystemCommonJobAuth(Map map);


    /*
    * 添加通用岗位
    * */
    int systemCommonJob_Insert(CommonJobsForm commonJobsForm);

    /*
    * *获取岗位最大Code
    * */
    String getJobCodeMax();
}


