package cn.visolink.salesmanage.groupmanagement.service;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UpdateMonthlyService {
    /*
     * 修改表一暂存后的数据
     * */
    public Integer updateMonthlyPlan(List<Map> listmap);

    /*
    * 查询项目表一状态
    * */
    String getProjectTableOneStatus(String projectId, String months);
    /*
     * 表一提交
     * */
    public int updatePlanEffective(Map<String,Object> map);

}
