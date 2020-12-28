package cn.visolink.salesmanage.groupmanagement.dao;

import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GroupManageUpdate {
    /*
    * 修改表一暂存后的数据
    *
    *
    *
    * */
    public Integer updateMonthlyPlan(Map<String, Object> map);



    String getProjectTableOneStatus(@Param("projectId") String projectId, @Param("months") String month);

    /**
     * 暂存，下达，上报 （修改状态）
     * @param
     * @return
     */
    int updatePlanEffective(Map<String, Object> map);

    List<Map> selectAllCode();

}
