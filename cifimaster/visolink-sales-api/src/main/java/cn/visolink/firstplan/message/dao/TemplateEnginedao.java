package cn.visolink.firstplan.message.dao;

import org.apache.cxf.management.annotation.ManagedOperation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/6/6 8:39 下午
 * 消息生成
 */
@Mapper
public interface TemplateEnginedao {
    /**
     * 查询业务消息对应的模版
     */
    public Map getTemplateInfo(Map map);

    /**
     * 获取前两月前三月的计划id
     */
    public Map getPlanDataBeforeMonth(String json_id);

    /**
     * 查询延期开盘的计划id
     */
    public Map getDelayOpenApplayPlanid(String json_id);

    /**
     * 查询开盘前7天的计划id
     */
    public Map getSevenOpenApplayPlanid(String json_id);

    /**
     * 查询计划的详细数据
     */
    public Map getPlanInfo(String plan_id);

    /**
     * 查询节点的详细数据
     */
    public Map getPlanNodeInfo(String plan_node_id);

    /**
     * 生成消息
     */
    public void saveMessage(Map map);

    /**
     * 查询当前系统下所有的计划
     */
    public List<Map> getAllPlan(Map map);

    /**
     * 查询开盘数据
     */
    public Map getOpenData(String plan_id);

    /**
     * 查询三大件
     */
    public Map getTthreepiecesNode(Map map);

    /**
     * 获取计划数据
     */
    public Map getPlanData(Map map);

    /**
     * 获取实际数据
     */
    public Map getActualData(Map map);

    /**
     * 获取首开计划数据
     */
    public Map getOpenPlanData(Map map);

    //获取首开实际数据
    public Map getOpenActualData(Map map);

    /**
     * 查询所有项目下逾期一天的三大件
     */
    public List<Map> selectThreePiecesData(String plan_id);

    /**
     * 查询系统所有顶设2已经完成的计划/项目
     */
    public List<Map> getDesignTwoApplayApproved(Map map);

    /**
     * 查询顶设2的计划客储数据，平均到每天
     */
    public Map getDesignTwoPlanDayNumData(Map map);

    /**
     * 查询首开前三月未审批通过的节点
     */
    public Map getThreeMonthsNotApplayApproved(Map map);
    /**
     * 查询昨天的实际数据
     */
    public Map getActuerNumberForYesDay(Map map);
    /**
     * 查询当前计划档位发送的记录
     */
    public String getGearData(Map map);
    /**
     * 添加发送偏差记录
     */
    public void saveSendPer(Map map);
    /**
     * 更新档位偏差记录
     */
    public void  updateSendPer(Map map);
    /**
     * 查询当前计划下的顶设2节点id
     */
    public String getDesignTwoPlanNode(String plan_id);
    /**
     * 获取当前项目所有的实际数据
     */
    public Map getActuerTotalNumberForYesDay(Map map);

    /**
     * 九大节点延期提醒
     * @param date
     * @return
     */
    List<Map> getSendPlanData(String plan_id);
    /**
     * 根据节点id获取节点数据详情
     */
    Map getPlanNodeInfoByPlanNodeId(Map map);
    /**
     * 根据json_id查询base_id
     */
    public Map queryPricingBaseId(String json_id);
    /**
     * 查询定调价所需的价格信息
     */
    public Map queryPricingProjectInfo(String project_id);
    /**
     * 查询定调价表格数据
     */
    public Map queryPricingTableData(String base_id);
    /**
     * 获取上一个节点的计划数据
     */
    public Map getPreviousPlanData(Map map);
    /**
     * 获取顶设2存储的首开计划数据
     */
    public Map getDingsgTwoPlanData(Map map);
    /**
     * 获取首开实际来人
     */
    public Map getCustomerStorActualData(Map map);
    /**
     * 查询对应计划已经延期的节点
     */
    public Map queryOverdueNodeData(Map map);
    /**
     * 首开简报计划/节点详细数据
     */
    public Map getFirstOpenInfo(Map map);
    /**
     * 首开简报主体数据
     */
    public Map getFirstOpenSubjectData(Map map);
    /**
     * 查询项目对应的项目总、城市总...
     */
    public List<Map> getLeaderUsers(Map map);
    /**
     * 查询对应当日播报数据
     */
    public Map getFirstOpenThisDayInfo(Map map);
    /**
     * 查询首开简报的均价数据
     */
    public List<Map> getFirstOpenAvgData(Map map);
    /**
     * 获取顶设2部分数据
     */
    public Map getDesignTwoCoreIndex(String plan_node_id);
    /**
     * 获取推售货值
     */
    public String getFirstOpenPushNumber(Map map);
    /**
     * 查询是否逾期
     */
    public Map selectIsDelayOpen(String plan_id);
    /*
    获取延期日期是否逾期一天
     */
    public Map getDelayIsYq(Map map);
}
