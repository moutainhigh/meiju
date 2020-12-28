package cn.visolink.system.job.authorization.service;

import cn.visolink.exception.ResultBody;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.10
 */

public interface JobService {

    /**
     * 获取所有岗位
     */
    PageInfo<Map> getJobByAuthId(Map map);

    /**
     * 查询所有组织架构
     */

    List<Map> getAllOrg(Map map);

    /**
     * 获取通用岗位列表
     *
     * @param map
     * @return
     */
    List<Map> getAllCommonJob(Map map);

    /**
     * 查询岗位下的人员列表，或根据姓名查询人员
     *
     * @param reqMap
     * @return
     */
    Map getSystemUserList(Map reqMap);

    /**
     * 获取当前和下属所有组织岗位
     *
     * @param reqMap
     * @return
     */
    List<Map> getSystemJobAllList(Map reqMap);

    /**
     * 新增岗位-插入Jobs信息
     *
     * @param reqMap
     * @return
     */
    int saveSystemJobForManagement(Map reqMap);

    /**
     * 组织岗位功能列表查询(前端功能授权)
     *
     * @param reqMap
     * @return
     */
    Map getSystemJobAuthByUserId(Map reqMap);

    /**
     * 前后端功能授权保存
     *
     * @param reqMap
     * @return
     */
    String saveSystemJobAuthByManagement(Map reqMap);

    /**
     * 更新岗位信息
     *
     * @param reqMap
     * @return
     */
    int modifySystemJobByUserId(Map reqMap);

    /**
     * 删除岗位信息
     *
     * @param reqMap
     * @return
     */
    int removeSystemJobByUserId(Map reqMap);

    /**
     * 查询引入用户
     *
     * @param reqMap
     * @return
     */
    Map pullinUser(Map reqMap);

    /**
     * 保存用户
     *
     * @param reqMap
     * @return
     */
    int saveSystemUser(Map reqMap);


    /**
     * 删除用户
     *
     * @param reqMap
     * @return
     */
    int removeSystemJobUserRel(Map reqMap);

    /**
     *修改用户
     * @param reqMap
     * @return
     */
    int modifySystemJobUserRel(Map reqMap);

    /**
     * 保存用户信息
     * @param reqMap
     * @return
     */
    int saveSystemJobUserRel(Map reqMap);


    /**
     * 获取所有菜单
     * @return
     */
    ResultBody getAllMenu(String jobId);

    /**
     * 获取通用岗位所有菜单
     * @return
     */
    ResultBody getCommonAllMenu(String jobId);
    ResultBody getCommonAllReportMenu(String jobId);



    /**
     * 保存菜单
     * @return
     */
    ResultBody saveJobMenus(Map map, String jobId);
    /**
     * 保存菜单
     * @retu通用rn
     */
    ResultBody saveCommonJobMenus(Map map, String jobId);
    ResultBody saveCommonReportMenu(Map map);
    /**
     * 中介公司
     * @retu通用rn
     */
    ResultBody getAllCompanyInfo();
    /**
     * 项目组织
     * @retu通用rn
     */
    ResultBody getAllOrgProject();
    ResultBody getAllOrgProject2();

    /**
     * 更新项目
     * @retu通用rn
     */
    ResultBody updateProject(Map map);

    //报表功能授权
    ResultBody getAuthorizationData(Map map);

    ResultBody saveAuthorization(Map map);
    ResultBody getOrgData(Map map);
}
