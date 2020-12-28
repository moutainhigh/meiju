package cn.visolink.common.security.service;
import com.google.common.collect.ImmutableMap;
import org.apache.wicket.core.dbhelper.sql.DBSQLServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author WCL
 */
@Service
@CacheConfig(cacheNames = "role")
public class JwtPermissionService {


    @Autowired
    private DBSQLServiceImpl dbsqlService;
    /**
     * key的名称如有修改，请同步修改 UserServiceImpl 中的 update 方法
     * @return
     */
    @Cacheable(key = "'loadPermissionByUser:' + #p0")
    public Collection<GrantedAuthority> mapToGrantedAuthorities(String UserId) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        ImmutableMap<String, String> immutableMap = ImmutableMap.<String, String>builder().put("UserId", UserId).build();
        List<Map> jobsList = dbsqlService.getListBySqlID("mJobsListByUserId_Select", immutableMap);
        List<Map> menusList = dbsqlService.getListBySqlID("mMenusListByUserId_Select",immutableMap);
//        Map<String, List<Map<String, Object>>> jobMenusMap  = menusList.stream().collect(Collectors.groupingBy(e -> e.get("jobId").toString()));
//        Set<Role> roles = roleRepository.findByUsers_Id(user.getId());
        //遍历所有岗位
        for (Map map : jobsList) {
            //遍历岗位对应菜单
//            List<Map<String, Object>> menus = jobMenusMap.get(map.get("ID"));
//            for (Map<String, Object> menu : menus) {
//                authorities.add(new SimpleGrantedAuthority(String.format("%s-%s", menu.get(""), null)));
//            }
        }
        return null;
    }
}
