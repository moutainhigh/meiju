package cn.visolink.firstplan.plannode.service.impl;

import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.plannode.dao.PlanNodeDao;
import cn.visolink.firstplan.plannode.service.PlanNodeService;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bao
 * @date 2020-04-14
 */


@Service
public class PlanNodeServiceImpl implements PlanNodeService {

    private final PlanNodeDao planNodeDao;

    public PlanNodeServiceImpl(PlanNodeDao planNodeDao) {
        this.planNodeDao = planNodeDao;
    }

    /**
     * 节点验证
     *
     * @param map projectId、planNodeId、node_level
     * @return map
     */
    @Override
    public Map<String, Object> getPlanNodePower(Map<String, Object> map) {


        Map<String, Object> rm = new HashMap<>();
        //获取顶设2
        int nodeOrder = Integer.parseInt(map.get("node_level") + "");
        if (StringUtil.isEmpty(map.get("projectId") + "")) {
            rm.put("power", false);
            rm.put("read", false);
            rm.put("error", "提示！数据异常 projectId为null！");
        } else {
            if (nodeOrder == 1 && StringUtil.isEmpty(map.get("planNodeId") + "")) {
                /*拿地后未编制，可编制拿地后节点*/
                rm.put("power", true);
                rm.put("read", true);
            } else if (StringUtil.isEmpty(map.get("planNodeId") + "")) {
                /*拿地后未编制，不可编制剩余后节点*/
                rm.put("power", false);
                rm.put("read", false);
                rm.put("error", "提示！前置节点无审批完成版，该节点不可重新创建版本");
            } else if (nodeOrder == 10 || nodeOrder == 11 || nodeOrder == 12) {
                /*售楼处开放、景观样板段开放、样板房开放 不受前后节点是否编制约束*/
                rm.put("power", true);
                rm.put("read", true);
            } else if (nodeOrder > 3) {
                /*顶设2之后节点判断*/
                Map<String, Object> openTwo = planNodeDao.getOpenTwoApproval(map);
                if (openTwo != null) {
                    Boolean isOpenTwo = Boolean.parseBoolean(openTwo.get("planApproval") + "");
                    if (isOpenTwo) {
                        rm = this.getPowerMap(map);
                    } else {
                        rm.put("error", "提示！当前存在顶设2审批中版本，无法创建版本！");
                        rm.put("power", isOpenTwo);
                    }
                    /*获取当前节点是否有有效的vison*/
                    Boolean read = Boolean.parseBoolean(planNodeDao.getPlanNodeApproval(map).get("planApproval") + "");
                    rm.put("read", read);
                } else {
                    rm.put("power", false);
                    rm.put("read", false);
                    rm.put("error", "提示！数据异常！");
                }
            } else {
                /*顶设2之前节点判断*/
                rm = this.getPowerMap(map);
                /* 获取当前节点是否有有效的vison */
                Boolean read = Boolean.parseBoolean(planNodeDao.getPlanNodeApproval(map).get("planApproval") + "");
                rm.put("read", read);
            }
        }
        Map<String, Object> paramMap = new HashMap<>();
        /* 判断略过的节点状态 */
        if (nodeOrder == 4 || nodeOrder == 5 || nodeOrder == 6) {
            /* 查询此节点是否为被略过节点 */
            paramMap.put("plan_node_id", map.get("planNodeId") + "");
            Map<String, Object> nodeIsSkipped = planNodeDao.queryThisNodeIsSkipped(paramMap);
            if (nodeIsSkipped != null && nodeIsSkipped.size() > 0) {
                /* 是否可以补录附件 */
                rm.put("isRecord", true);
            }
        }
        return rm;
    }


    /**
     * 判断前后节点
     *
     * @param map map
     * @return map
     */
    public Map<String, Object> getPowerMap(Map<String, Object> map) {
        Map<String, Object> ma = new HashMap<>();
        List<Map<String, Object>> list = planNodeDao.getPlanNodePower(map);
        boolean b = true;
        if (list.size() != 0) {
            for (Map<String, Object> m : list) {
                if (!Boolean.parseBoolean(m.get("power") + "")) {
                    b = false;
                    ma.put("error", m.get("error"));
                    ma.put("power", b);
                }
            }
        } else {
            b = false;
            ma.put("power", b);
            ma.put("error", "提示！数据异常！");
        }
        if (b) {
            ma.put("power", b);
        }
        return ma;
    }

    /**
     * 定时任务,每月1号0点生成周上报
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertCommonWeekPlan() {
        planNodeDao.deleteCommonWeekPlan();
        List<Map<String, Object>> mmp = planNodeDao.selectMonthSundayDay();
        planNodeDao.insertCommonWeekPlan(mmp);
    }


    /**
     * 删除首开草稿版数据
     *
     * @param map planNodeId
     * @return ResultBody
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody delPlanNodePower(Map<String, Object> map) {
        ResultBody resultBody = new ResultBody();
        try {
            int node = Integer.parseInt(map.get("node_level") + "");
            Integer i;
            if (null == map.get("node_level")) {
                resultBody.setCode(-1);
                resultBody.setMessages("当前节点比编码无效！");
                return resultBody;
            } else if (node <= 3) {
                i = planNodeDao.selectNodeNum(map);
            } else {
                i = 2;
            }
            if (i > 1) {
                Integer a = planNodeDao.delPlanNode(map);
                a = a + planNodeDao.delPlanNodeFlow(map);
                a = a + planNodeDao.delNodeSeven(map);
                resultBody.setCode(200);
                if (a > 0) {
                    resultBody.setMessages("删除成功");
                } else {
                    resultBody.setMessages("当前无删除数据,或版本处于审批中！");
                }
            } else if (i == 1) {
                resultBody.setCode(-1);
                resultBody.setMessages("当前节点只有一个有效版本不可删除！");
            }
            return resultBody;
        } catch (Exception e) {
            resultBody.setCode(-1);
            resultBody.setMessages("失败");
            return resultBody;
        }
    }

}
