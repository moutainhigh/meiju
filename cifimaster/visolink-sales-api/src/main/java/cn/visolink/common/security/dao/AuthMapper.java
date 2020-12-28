package cn.visolink.common.security.dao;

import cn.visolink.common.security.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wcl
 * @version 1.0
 * @date 2019/9/4 11:21 上午
 */
@Mapper
public interface AuthMapper extends BaseMapper<User> {
    /**
     * 根据用户名称获取用户类型
     *
     * @param username
     * @return
     */
    public Map mGetAccountType(@Param("UserName") String username);

    /**
     * 根据用户名称获取用户Id
     *
     * @param username
     * @return
     */
    public String mGetIdByUserName(@Param("UserName") String username);

    /**
     * 根据用户名称获取用户最后一次登录信息
     *
     * @param username
     * @return
     */
    public Map mLastTimeLogin(@Param("UserName") String username);

    /**
     * 记录用户最后一次登录信息
     *
     * @param map
     * @return
     */
    public void mInsertLastTimeLogin(Map map);

    /**
     * 获取用户信息
     *
     * @param map
     * @return
     */
    public Map mGetUserInfo(Map map);

    /**
     * 根据UserId查找岗位列表
     *
     * @param UserId
     * @return
     */
    public Map  mJobsListByUserId(@Param("UserId") String UserId);

    /**
     * 根据UserId查找组织列表
     *
     * @param UserId
     * @return
     */
    List<Map> getOrgList(@Param("UserId") String UserId);

    /**
     * 根据用户Id和JobId和菜单类型获取菜单列表
     * @param UserId
     * @param JobID
     * @param menusType
     * @return
     */
    public List<Map> mMenusListByUserIdAndJobId(@Param("UserId") String UserId, @Param("JobID") String JobID, @Param("menusType") int menusType);

    /**
     * 根据OrgId获取项目列表
     * @param OrgId
     * @return
     */
    public Map mFindByProjectIdByOrgId(@Param("OrgId") String OrgId);


    /**
     * 根据用户Id获取已授权岗位列表
     * @param UserId
     * @return
     */
    public List<Map> getJobsListByUserId(@Param("UserId") String UserId);

    public List<Map> getChildMenus(Map map);


    public Map getUserInfoByMoblie(Map params);
}
