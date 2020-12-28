package cn.visolink.salesmanage.onlineretailersuse.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/11/26 9:14 下午
 */
@Mapper
@Repository
public interface OnlineretailersUseDao {
    public List<Map> getOnlineretailersUseApplayList(Map map);

    public List<Map> getOnlineretailersUseApplayCount(Map map);

    public void saveOnlineretailersUseApplay(Map map);

    public void saveOnlineretailersUseItem(Map map);

    public void updateOnlineretailersUseApplay(Map map);

    public void clearItemData(@Param("BOID") String BOID);

    public Map getOnlineretailersMainData(Map map);

    public List<Map<String, Object>> getOnlineretailersItemData(Map map);

    public List<Map> getOnlineretailersCompanyList();

    public void insertCmPolicySalesForOnlineretailers(Map map);

    public List<Map<String, Object>> getOnlineretailersItemDataForMy(Map map);

    public void insertParamLogForPushSystem(Map map);

    public String getUserName(@Param("userId") String userId);

    public void deleteMainData(@Param("BOID") String boid);
}
