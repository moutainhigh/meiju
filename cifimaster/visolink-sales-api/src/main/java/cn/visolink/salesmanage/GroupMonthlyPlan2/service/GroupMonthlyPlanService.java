package cn.visolink.salesmanage.GroupMonthlyPlan2.service;

import java.util.List;
import java.util.Map;

public interface GroupMonthlyPlanService {
    /**
     * 查询事业部
     * @return
     */
    public  int GetMonthlyPlanByTheMonthCount(String months);

    public  int SetMonthlyPlanInsert(String months);



}
