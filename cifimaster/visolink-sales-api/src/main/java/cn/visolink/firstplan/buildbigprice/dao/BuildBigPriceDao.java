package cn.visolink.firstplan.buildbigprice.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/3/21 7:58 下午
 */
@Mapper
public interface BuildBigPriceDao {
    List<Map> getProjectStagesData(String project_id);
    List<Map> getProductDataaByProjectFid(String projectFID);
    List<Map> getBuildData(Map map);
    void clearAllThisVersionData(String id);
    void insertBuildData(Map map);
    List<Map> getBuilldBigPriceData(Map map);
    Map getBuildInfo(String buildId);
    void updateBigPriceIsSave(String id);
    List<Map> getWeiSaveBuildData(String plan_node_id);

    String getNewPlanNodeData(String plan_id);
}
