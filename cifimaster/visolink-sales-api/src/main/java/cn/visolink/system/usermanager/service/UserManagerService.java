package cn.visolink.system.usermanager.service;

import java.util.List;
import java.util.Map;

/**
 * @author wjc
 * @date 2019/09/11
 */
public interface UserManagerService {
    /**
     * 获取用户信息
     *
     * @param map
     * @return
     */
    Map findMessage(Map map);
    /**
     * 禁用/启用  用户的账号
     *
     * @param map
     * @return
     */
    int updateUserStatus(Map map);
    /**
     * 移除用户
     *
     * @param map
     * @return
     */
    int deleteUser(Map map);

    /**
     * 查询用户是否存在
     *
     * @param map
     * @return
     */
    Map<String,Object> userNameExists(Map map);


    /**
     * 从C_User表查询用户数据
     *
     * @param map
     * @return
     */
    Map<String,Object> getUserFromCuser(Map map);
    /**
     *岗位人员新增
     *
     * @param map
     * @return
     */
    int insertSystemJobUser(Map map);
    /**
     *岗位人员新增
     *
     * @param map
     * @return
     */
    int insertSystemJobUsersRel(Map map);
    /**
     *excel导出测试
     *
     * @param map
     * @return
     */
    Map<String,Object> userComomJobCodeByJobId(Map map);

    /**
     *excel导出测试
     *
     * @param map
     * @return
     */
    Map<String,Object>   userProxyRegisterByUserId(Map map);
    /**
     *excel导出测试
     *
     * @param map
     * @return
     */
    List<Map> userProxyRegisterInvitationCode(Map map);
    /**
     *excel导出测试
     *
     * @param map
     * @return
     */
    List<Map> saleAccountLogInsert(Map map);

    /**
     *人员信息更新
     *
     * @param map
     * @return
     */
    int systemUserUpdate(Map map);

    /**
     *人员信息更新
     *
     * @param map
     * @return
     */
    int systemUserUpdateTwo(Map map);

    /**
     *添加操作日志列表
     *
     * @param map
     * @return
     */
    int systemLogInsert(Map map);
}
