package cn.visolink.firstplan.planmonitoring.service.impl;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.exception.BadRequestException;
import cn.visolink.firstplan.planmonitoring.dao.PlanMontitorDao;
import cn.visolink.firstplan.planmonitoring.service.PlanMontitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/2/25 2:07 下午
 */
@Service
@Transactional
public class PlanMontitorServiceImpl implements PlanMontitorService {


    @Autowired
    private PlanMontitorDao planMontitorDao;
    @Override
    public Map montitorIndex(Map map, HttpServletRequest request) {
        //获取分页数据
       int pageIndex = Integer.parseInt(map.get("pageIndex").toString());
        int pageSize = Integer.parseInt(map.get("pageSize").toString());
        int i = (pageIndex - 1) * pageSize;
        map.put("pageIndex", i);
        String userid = request.getHeader("userid");
        map.put("userID",userid);
        Map<Object, Object> returnMap = new HashMap<>();
        //获取每个计划下的当前节点

        List<Map> planNodeData = planMontitorDao.getPlanNodeData(map);

        //获取状态的统计数据
       /* List<Map> statistData = planMontitorDao.statisticsStatusData(map);
        returnMap.put("statusData",statistData);*/
       String thisnode=map.get("thisNode")+"";
       if("0".equals(thisnode)){
           map.remove("thisNode");
       }
        //查询项目的计划数据
        List<Map> projectData = planMontitorDao.getProjectData(map);
        Map count = planMontitorDao.getProjectDataCount(map);
        int thisNode=0;
        Map<Object, Object> paramMap = new HashMap<>();
        for (Map projectMap : projectData) {
            int isShow=0;
            Map<Object, Object> countMap = new HashMap<>();
            projectMap.put("planID",projectMap.get("plan_id"));
            String this_node=projectMap.get("this_node")+"";
            if(!"".equals(this_node)&&!"null".equals(this_node)){
                //获取当前最新完成的节点
                thisNode=Integer.parseInt(this_node)-1;
            }
            if(thisNode>3&&thisNode<9) {
                paramMap.put("plan_id", projectMap.get("plan_id"));
                paramMap.put("node_level", thisNode);
                Map newestPlanNode = planMontitorDao.getNewestPlanNode(paramMap);
                if (newestPlanNode != null && newestPlanNode.size() > 0) {
                    String beOverdue_days=newestPlanNode.get("beOverdue_days")+"";
                    if(!"".equals(beOverdue_days)&&!"null".equals(beOverdue_days)){
                        int parseInt = Integer.parseInt(beOverdue_days);
                        if(parseInt<0){
                            newestPlanNode.put("beOverdue_days",0);
                        }

                    }
                    paramMap.put("plan_node_id", newestPlanNode.get("id"));
                    //当前节点计划数据
                    Map thisNodePlanData = planMontitorDao.getThisNodePlanData(paramMap);
                    //当前节点实际数据
                    Map thisNodeActualData = planMontitorDao.getThisNodeActualData(paramMap);
                    if (thisNodePlanData != null && thisNodePlanData.size() > 0) {
                        countMap.putAll(thisNodePlanData);
                    }
                    if (thisNodeActualData != null && thisNodeActualData.size() > 0) {
                        countMap.putAll(thisNodeActualData);
                    }
                    paramMap.put("node_level",8);
                    Map openPlanData = planMontitorDao.getOpenPlanData(paramMap);
                    //计算实时来访偏离度
                    Map resultMap = countVisitPer(thisNodePlanData, thisNodeActualData, openPlanData);
                    if (resultMap != null && resultMap.size() > 0) {
                        countMap.putAll(resultMap);
                    }
                    countMap.putAll(newestPlanNode);
                    if(countMap!=null&&countMap.size()>0){
                        isShow=1;
                    }else{
                        isShow=0;
                    }
                }
            }
            List<Map> nodeData = planMontitorDao.getPlanNodeByPlan(projectMap);
            projectMap.put("nodeList",nodeData);
            projectMap.put("isShow",isShow);
            projectMap.put("earlyWarningData",countMap);
        }

        // 添加'全部'计划
        HashMap<String, Object> hashMap = new HashMap<>(1);
        hashMap.put("nodeName","全部");
        hashMap.put("this_node","0");
        Long nodeNum = 0L;
        for (Map planNodeDataMap : planNodeData) {
            nodeNum += (Long)planNodeDataMap.get("nodeNum");
        }
        hashMap.put("nodeNum",nodeNum);
        planNodeData.add(0,hashMap);

        returnMap.put("headData",planNodeData);
        returnMap.put("total",count.get("total"));
        returnMap.put("bodyData",projectData);

        Map<Object, Object> maps = new HashMap<>();
        maps.put("code",200);
        maps.put("result",returnMap);
        maps.put("message","数据获取成功!");
        return maps;
    }
    public Map countVisitPer(Map planMap,Map actualMap,Map openMap){
        Map<Object, Object> resultMap = new HashMap<>();
        if(planMap!=null&&planMap.size()>0){
            //获取当前节点的计划来人
            float plan_visit_num = Float.parseFloat(planMap.get("plan_visit_num") + "");
            if(actualMap!=null&&actualMap.size()>0){
                //获取当前节点的实际来人
                float actual_visit_num = Float.parseFloat(actualMap.get("actual_visit_num") + "");
                if(plan_visit_num!=0&&plan_visit_num!=0.00){
                    resultMap.put("deviate_visit_per",(plan_visit_num-actual_visit_num)/plan_visit_num*100+"%");
                }else{
                    resultMap.put("deviate_visit_per","100%");
                }
            }else{
                resultMap.put("deviate_visit_per","0%");
            }
        }
        if(actualMap!=null&&actualMap.size()>0){
            float actual_visit_num = Float.parseFloat(actualMap.get("actual_visit_num") + "");
            if(openMap!=null&&openMap.size()>0){
                float plan_visit_num = Float.parseFloat(openMap.get("plan_open_visit_num") + "");
                if(plan_visit_num!=0&&plan_visit_num!=0.00){
                    resultMap.put("speed_visit_per",(actual_visit_num/plan_visit_num)*100+"%");
                }else{
                    resultMap.put("speed_visit_per","100%");
                }
            }
        }
        return resultMap;
    }

    @Override
    //定时更改节点状态
    public void updateNodeStatusTiming(){
        //查询每个计划的当前节点
        try {
           planMontitorDao.updateNodeLightStatus();
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

    }


    //定时更新项目关系表数据
   // @Scheduled(cron = "0 0 0 * * ?")
    @Override
    public void updateProjectRelationship(){
        try {
            List<String> projectIDList = planMontitorDao.selectProjectRelationship();
            Map<Object, Object> projectMap = new HashMap<>();
            projectMap.put("projectIdList",projectIDList);
            List<Map> mapList = planMontitorDao.selectMainDataProject(projectMap);
            if(mapList!=null&&mapList.size()>0){
                for (Map projectMaps : mapList) {
                    planMontitorDao.addProjectRelationship(projectMaps);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

    }

    @Override
    public void updatePlanProjectName() {
        planMontitorDao.updatePlanProjectName();
        planMontitorDao.updateProjectFlagName();
        planMontitorDao.updatePlanAreaData();
    }

    //更改即将完成的节点
    @Override
    public void updateSoonNode() {
        //获取所有的计划
        List<String> planData = planMontitorDao.getPlanData();
        for (String plan_id : planData) {
            planMontitorDao.updateSoonNode(plan_id);
        }
    }
    @Override
    public VisolinkResultBody getIdmBuinessData() {
        VisolinkResultBody<Object> resopnse = new VisolinkResultBody<>();
        try {
            List<Map> idmBuinessData = planMontitorDao.getIdmBuinessData();
            resopnse.setResult(idmBuinessData);
            resopnse.setCode(200);
            return  resopnse;
        }catch (Exception e){
            e.printStackTrace();
            resopnse.setCode(400);
            return resopnse;
        }

    }



}
