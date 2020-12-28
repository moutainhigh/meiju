package cn.visolink.firstplan.planmonitoring.service;

import cn.visolink.common.bean.VisolinkResultBody;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/2/25 2:06 下午
 */
public interface PlanMontitorService {
    Map montitorIndex(Map map, HttpServletRequest request);

    void updateNodeStatusTiming();
    VisolinkResultBody getIdmBuinessData();
    void updateProjectRelationship();
    void updatePlanProjectName();

   void updateSoonNode();
}
