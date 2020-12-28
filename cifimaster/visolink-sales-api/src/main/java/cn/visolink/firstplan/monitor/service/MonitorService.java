package cn.visolink.firstplan.monitor.service;

import java.util.List;
import java.util.Map;

public interface MonitorService {

    public Map getPlanNodeInfo(Map params);

    public List queryMonitorNewNode(Map params);

    public List selectMonitorPlanNode(String plan_id);
}
