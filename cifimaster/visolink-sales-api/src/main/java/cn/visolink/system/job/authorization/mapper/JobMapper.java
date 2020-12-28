package cn.visolink.system.job.authorization.mapper;

import cn.visolink.system.org.model.vo.OrganizationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.09
 */
@Mapper
public interface JobMapper {


    /**
     * 获取所有岗位
     */
    List<Map> getJobByAuthId(Map map);

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
     * 获取岗位下的人员
     *
     * @param map
     * @return
     */
    List<Map> getSystemJobUserList(Map map);
    Integer getSystemJobUserListCount(Map map);

    /**
     * 获取当前和下属所有组织岗位
     *
     * @param map
     * @return
     */
    List<Map> getSystemJobAllList(Map map);

    /**
     * 新增岗位-插入Jobs信息
     *
     * @param map
     * @return
     */

    int saveSystemJobForManagement(Map map);
    int saveSystemJobForManagement2(Map map);

    Map getFourOrgLevelByJobId(String jobId);

    Map getParentOrg(String orgId);

    int insertJobOrgRel(Map map);
    String getJobCodeMax();
    /**
     * 登录人有权限的菜单
     *
     * @param map
     * @return
     */
    List<Map> userMenusByUserId(Map map);

    /**
     * 登录人有权限的功能
     *
     * @param map
     * @return
     */
    List<Map> userFunctionsByUserId(Map map);

    /**
     * 该岗位已有的菜单和功能
     *
     * @param map
     * @return
     */
    List<Map> jobFunctionsByUserId(Map map);

    /**
     * 查询组织岗位功能授权
     *
     * @param map
     * @return
     */
    List<Map> getSystemJobMenusID(Map map);

    /**
     * 删除组织岗位功能授权
     *
     * @param map
     * @return
     */
    int removeSystemJobAuth(Map map);

    /**
     * 保存组织岗位功能授权
     *
     * @param map
     * @return
     */
    int saveSystemJobAuthManagement(Map map);

    /**
     * 更新Jobs信息
     *
     * @param map
     * @return
     */
    int modifySystemJobByUserId(Map map);

    /**
     * 管理端删除岗位
     *
     * @param map
     * @return
     */
    int removeSystemJobByUserId(Map map);

    /**
     * 查询用户名是否存在
     *
     * @param map
     * @return
     */
    Map getSystemUserNameExists(Map map);

    /**
     * 新增用户
     *
     * @param map
     * @return
     */
    int saveSystemUser(Map map);

    /**
     * 获取用户在岗位与平台账号关系表中是否有数据
     * @param map
     * @return
     */
    Map getJobSuserrel(Map map);

    /**
     * 保存岗位与平台账号关系表
     * @param
     * @return
     */
    int saveJobSuserrel(Map map);

    /**
     * 删除用户人员信息
     * @param map
     * @return
     */
    int removeSystemJobUserRel(Map map);
    /**
     * 从C_User表查询用户数据
     *
     * @param map
     * @return
     */
    Map getUserFromCuser(Map map);

    /**
     * 根据岗位ID查询组织信息
     *
     * @param map
     * @return
     */
    Map getOrgInfoByJobID(Map map);

    /**
     * 引入普通用户插入关联关系数据
     *
     * @param map
     * @return
     */
    int saveAccountToJobUserURl(Map map);

    /**
     * 查询引入OA账户时是否有重复
     *
     * @param map
     * @return
     */
    Map getCuserToAccount(Map map);

    /**
     * 组织岗位引入人员插入人员表
     *
     * @param map
     * @return
     */
    Map saveCuserToAccount(Map map);

    /**
     * 组织岗位引入人员插入人员表
     *
     * @param map
     * @return
     */
    int insertCuserToAccount(Map map);

    /**
     * 更新线索中的拓客信息
     *
     * @param map
     * @return
     */
    int modifyProjectClueTokerAttribution(Map map);

    /**
     * 更新机会中的拓客信息
     *
     * @param map
     * @return
     */
    int modifyProjectOppoTokerAttribution(Map map);

    /**
     * 更新线索中的案场信息
     *
     * @param map
     * @return
     */
    int modifyProjectClueSalesAttribution(Map map);

    /**
     * 更新机会中的案场信息
     *
     * @param map
     * @return
     */
    int modifyProjectOppoSalesAttribution(Map map);

    Map getUserProxyregisterByUserID(Map map);

    int saveUserProxyregisterInvitationCode(Map map);

    /**
     * 引入用户信息
     * @param map
     * @return
     */
    List<Map> getIntroducingUsers(Map map);

    /**
     * 统计引入用户的信息数量
     * @return
     */
    Integer getIntroducingUsersCount(Map map);

    /**
     * 保存引入用户
     */
    int saveIntroducingUsers(Map map);

    /**
     * 修改用户信息
     * @param reqMap
     * @return
     */
    int modifySystemJobUserRel(Map reqMap);


    /**
     * 判断岗位是否存在
     * @param
     * @return
     */
    Map isRepeat(@Param("accountId") String accountId, @Param("jobId") String jobId);

    /**
     * 判断是否存在当前岗位
     * @param
     * @return
     */
 Map isCurrentJob(@Param("accountId") String accountId);
    /**
     * 获取所有菜单
     * @return
     */
    List<Map> getAllMenu();
    List<Map> getAllReportMenu();

    /***
     * 获取指定岗位菜单
     * */

    List<Map> getJobMenu(@Param("jobId") String jobId);

    /***
     * 获取通用岗位指定菜单
     * */
    List<Map> getCommonMenu(@Param("jobId") String jobId);
    List<Map> getCommonAllReportMenu(@Param("jobId")String jobId);
    /***
     * 删除指定岗位菜单
     * */
    int delJobMRelMenu(@Param("jobId") String jobId);
    /***
     * 删除指定岗位菜单
     * */
    int delCommonJobMRelMenu(@Param("jobId") String jobId);
    int delCommonReportMRelMenu(@Param("jobId")String jobId);
    /***
     * 添加指定岗位菜单
     * */
    int saveJobMenu(@Param("jobId") String jobId, @Param("menuId") String menuId);
    /***
     * 添加指定岗位菜单
     * */
    int saveCommonJobMenu(@Param("jobId") String jobId, @Param("menuId") String menuId);

    int saveCommonReportMenu(Map map);
    /**
     * 中介公司
     * @retu通用rn
     */
    List<Map> getAllCompanyInfo();
    /**
     * 项目组织
     *
     */
    List<Map> getAllOrgProject();
    List<Map> getAllOrgProject2();

    int updateProjectId(Map map);

    String getFullPath(@Param("orgId") String orgId);
    //获取所有报表功能列表
    List<Map> getAllFunctions(Map map);
    //获取当前岗位已经授权的的报表功能
    List getAccreditFunctions(Map map);

    void deletBeAuthorized(Map map);
    void saveAuthorizeds(Map map);
    Map getParentMenu(Map map);
    String getAuthorizedsParent(@Param("id") String id);

    int updateOrg(@Param("projectId") String projectId, @Param("fullPath") String fullPath);

    List<OrganizationVO> getOrgData(Map map);
}
