package cn.visolink.common.menu.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.visolink.common.menu.service.MenuService;
import cn.visolink.exception.BadRequestException;
import com.google.common.collect.ImmutableMap;
import org.apache.wicket.core.dbhelper.sql.DBSQLServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author wcl
 * @Description
 * @Date 2019/7/18 14:19
 * @Version 1.0
 **/
@Service
public class menuServiceImpl implements MenuService {
    @Autowired
    private DBSQLServiceImpl dbsqlService;
    @Override
    public List<Map> getMenusListByUserIdAndPid(String userId,String pId,String JobID) {
        if(StrUtil.isEmpty(userId)){
            throw  new BadRequestException(-10_0012,"参数格式不正确！");
        }
        if(StrUtil.isEmpty(pId)){
            throw  new BadRequestException(-10_0012,"参数格式不正确！");
        }
        Map<String, String> param = ImmutableMap.<String, String>builder()
                .put("pId",pId)
                .put("JobID",JobID)
                .put("userId", userId).build();
        List<Map> menusList= this.dbsqlService.getListBySqlID("mMenusListByUserIdAndPid_Select", param);

        return menusList;
    }
}
