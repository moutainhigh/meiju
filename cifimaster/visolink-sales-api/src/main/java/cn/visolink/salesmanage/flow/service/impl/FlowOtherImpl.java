package cn.visolink.salesmanage.flow.service.impl;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.constant.VisolinkConstant;
import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.flow.dao.FlowOtherDao;
import cn.visolink.salesmanage.flow.dao.WorkflowDao;
import cn.visolink.salesmanage.flow.service.FlowOtherService;
import cn.visolink.salesmanage.packageanddiscount.dao.PackageanddiscountDao;
import cn.visolink.utils.FlowUtil;
import cn.visolink.utils.flowpojo.FlowStateResult;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class FlowOtherImpl implements FlowOtherService {


    @Autowired
    private FlowUtil flowUtil;

    @Autowired
    private FlowOtherDao dao;

    @Autowired
    private WorkflowDao workflowDao;
    @Autowired
    private PackageanddiscountDao packageanddiscountDao;

    /**
     * 废弃流程
     * @param instanceId
     */
    @Override
    public ResultBody endFlow(String instanceId) {
        FlowStateResult result;
        //将本系统的流程作废掉
        String id = workflowDao.selectPrimaryByFlowId(instanceId);

        //如果是首开的流程，调用删除接口
        String code = workflowDao.getFlowCodeByFlowId(instanceId);
        //定义流程code数组，首开、风控
        String[] codeArray={"SkAuthorized","SkAuthorized_2020","SkUnAuthorized","SK_SD","Risk_Approval_risk"};

        List list = CollectionUtils.arrayToList(codeArray);
        if(code!=null&&!"".equals(code)){
            //如果当前作废的流程为首开流程or风控流程
            boolean contains = list.contains(code);
            if(contains){
                //调用删除流程接口
                result=flowUtil.deleteFlow(instanceId);
            }else{
                //调用作废流程接口
                result = flowUtil.endFlow(instanceId);
            }
        }else{
            result = flowUtil.endFlow(instanceId);
        }
        Map<Object, Object> flowMap = new HashMap<>();
        flowMap.put("id",id);
        flowMap.put("flow_status",7);
        workflowDao.updateFlowStatusById(flowMap);
        if(result.isState()){
            return ResultBody.success(null);
        }else{
            return ResultBody.error(-1,result.getMessage());
        }
    }

    /**
     * 删除流程
     * @param instanceId
     */
    @Override
    public ResultBody deleteFlow(String instanceId) {
        FlowStateResult result = flowUtil.deleteFlow(instanceId);
        //将本系统的流程作废掉
        String id = workflowDao.selectPrimaryByFlowId(instanceId);
        Map<Object, Object> flowMap = new HashMap<>();
        flowMap.put("id",id);
        flowMap.put("flow_status",7);
        workflowDao.updateFlowStatusById(flowMap);
        if(result.isState()){
            return ResultBody.success(null);
        }else{
            return ResultBody.error(-1,result.getMessage());
        }

    }

    @Override
    public Map queryFlowId(String BOID) {
        Map oldMap = dao.queryFlowId(BOID);
        if(oldMap!=null){
            Map flowJson = JSON.parseObject(oldMap.get("flow_json") + "", Map.class);
            if(flowJson!=null&&flowJson.size()>0){
                String tjPlanType=flowJson.get("TjPlanType")+"";
                if("调表价".equals(tjPlanType)){
                    oldMap.put("flow_version",1);
                }
            }
            String flow_status=oldMap.get("flow_status")+"";
            if("7".equals(flow_status)){
                oldMap.clear();
                oldMap= dao.queryFlowIdByBaseid(BOID);
            }
        }
        return oldMap;
    }

}
