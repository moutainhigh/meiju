package cn.visolink.system.usermanager.service.impl;
import cn.visolink.exception.BadRequestException;
import cn.visolink.system.usermanager.dao.UserManagerDao;
import cn.visolink.system.usermanager.service.UserManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wjc
 * @date 2019/09/11
 */
@Service
public class UserManagerServiceImpl implements UserManagerService {
    @Autowired
    private UserManagerDao userMessageDao;

    /**
     * 获取用户信息
     *
     * @param map
     * @return
     */
    @Override
    public Map findMessage(Map map) {
        Map resultMap=new HashMap();
        int pageIndex = Integer.parseInt(map.get("pageIndex").toString());
        int pageSize = Integer.parseInt(map.get("pageSize").toString());
        int i = (pageIndex - 1) * pageSize;
        map.put("pageIndex", i);
        List<Map> message = userMessageDao.findMessage(map);
        Integer messageCount = userMessageDao.findMessageCount(map);
        resultMap.put("list", message);
        resultMap.put("total", messageCount);
        return resultMap;
    }

    /**
     * 人员信息更新
     *
     * @param map
     * @return
     */
    @Override
    public int systemUserUpdate(Map map) {
        Map data = (Map) map.get("data");
        if("启用".equals(data.get("Status"))){
            data.put("Status",1);
        }
        if("禁用".equals(data.get("Status"))){
            data.put("Status",0);
        }
        if("普通账号".equals(data.get("AccountType"))){
            data.put("AccountType",2);
        }
        if("Saas账号".equals(data.get("AccountType"))){
            data.put("AccountType",1);
        }
        if("男".equals(data.get("Gender"))){
            data.put("Gender",1);
        }
        if("女".equals(data.get("Gender"))){
            data.put("Gender",2);
        }
        int i = userMessageDao.modifySystemUser(data);
        return i;
    }

    /**
     * 禁用/启用 用户账号
     *
     * @param map
     * @return
     */
    @Override
    public int updateUserStatus(Map map) {
        try {
            if (!map.isEmpty()) {
                return userMessageDao.updateUserStatus(map);
            }
        } catch (Exception e) {
            throw new BadRequestException(-20_0001, e);
        }
        return 0;
    }

    /**
     * 移除用户
     *
     * @param map
     * @return
     */
    @Override
    public int deleteUser(Map map) {

        return userMessageDao.deleteUser(map);
    }

    /**
     * 查询用户是否存在
     *
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> userNameExists(Map map) {
        return userMessageDao.userNameExists(map);
    }

    /**
     * 从C_User表查询用户数据
     *
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> getUserFromCuser(Map map) {
        return userMessageDao.getUserFromCuser(map);
    }

    /**
     * 岗位人员新增
     *
     * @param map
     * @return
     */
    @Override
    public int insertSystemJobUser(Map map) {
        return userMessageDao.insertSystemJobUser(map);
    }

    @Override
    public int insertSystemJobUsersRel(Map map) {
        return userMessageDao.insertSystemJobUsersRel(map);
    }

    /**
     * excel导出测试
     *
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> userComomJobCodeByJobId(Map map) {
        return userMessageDao.userComomJobCodeByJobId(map);
    }

    /**
     * excel导出测试
     *
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> userProxyRegisterByUserId(Map map) {
        return userMessageDao.userProxyRegisterByUserId(map);
    }

    /**
     * excel导出测试
     *
     * @param map
     * @return
     */
    @Override
    public List<Map> userProxyRegisterInvitationCode(Map map) {
        return userMessageDao.userProxyRegisterInvitationCode(map);
    }

    /**
     * excel导出测试
     *
     * @param map
     * @return
     */
    @Override
    public List<Map> saleAccountLogInsert(Map map) {
        return userMessageDao.saleAccountLogInsert(map);
    }

    /**
     * 人员信息更新
     *
     * @param map
     * @return
     */
    @Override
    public int systemUserUpdateTwo(Map map) {
        return userMessageDao.systemUserUpdateTwo(map);
    }

    @Override
    public int systemLogInsert(Map map) {
        return userMessageDao.systemLogInsert(map);
    }
}
