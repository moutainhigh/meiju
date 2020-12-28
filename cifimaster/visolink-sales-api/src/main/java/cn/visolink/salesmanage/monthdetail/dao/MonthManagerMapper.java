package cn.visolink.salesmanage.monthdetail.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
/**
 * @author 孙林
 * @date:2019-9-16
 * */
@Mapper
@Repository
public interface MonthManagerMapper {

   /**
    * 通过项目ID和月份来查询表二信息（月度计划明细）
    * @param map
    * @return
    */
   public Map<String,Object> mouthPlanSelect(Map<String, Object> map);



   /**
    * 通过项目ID和月份来判断当前是否存在
    * @param map
    * @return
    */
   int selectMouthPlan(Map<String, Object> map);

   /**
    * 通过项目ID和月份来初始化（月度计划明细）
    * @param map
    * @return
    */
   public Integer initialMouthPlan(Map<String, Object> map);

   /**
    * 查找月度计划指标里的合计签约金额，合金签约套，计划认购套，计划认购金额来初始化到表二（月度计划明细）
    * @param map
    * @return
    */
   public Map<String,Object> selectTotalAndPlan(Map<String, Object> map);

   /**
    * 查找月度计划指标里二下版本里的营销推广费用来初始化到表二（月度计划明细）
    * @param map
    * @return
    */
   public Map<String,Object> selectPromotionCost(Map<String, Object> map);
   /**
    * 通过项目ID和月份来跟新表二信息（月度计划明细）
    * @param map
    * @return
    */
   public Integer mouthPlanUpdate(Map<String, Object> map);

   /**
    * 通过项目ID和月份来设置表二（月度计划明细）是否上报状态
    * @param map
    * @return
    */
   public Integer mouthPlanEffective(Map<String, Object> map);




   /**
    * 通过项目ID和月份来设置表 四（月度计划明细）里面的风险
    * @param map
    * @return
    */
   public Integer mouthPlanUpdateRisk(Map<String, Object> map);

   /**
    * 将所有的招揽客人的渠道表示到前端去
    *
    * @return
    */
   public List<Map> allChannelSelect();

   /**
    * 初始化渠道费用明细
    *
    * @return
    */
   public Integer initialMouthChannelDetail(Map<String, Object> map);

   /**
    * 通过项目ID和月份查找渠道费用明细
    *
    * @return
    */
   public List<Map> selectMouthChannelDetail(Map<String, Object> map);

   /**
    * 通过项目ID和月份跟新渠道费用明细
    *
    * @return
    */
   public Integer  updateChannelDetail(Map<String, Object> map);

   /**
    * 通过项目ID和月份跟新渠道费用明细(批量更新)
    *    bql 2020.07.28
    *
    * @param List list
    * @return Integer
    */
    Integer  updateChannelDetailBatch(List<Map> List);


   /**
    * 查找费用渠道里需要遍历到图表的字段，前三个月渠道费用，前三月成交率(事项)
    *
    * @return
    */
   public Map selectMouthChannelDetailAction(Map<String, Object> map);
   /**
    * 查找费用渠道里需要遍历到图表的字段，前三个月渠道费用，前三月成交率(动作)
    *
    * @return
    */
   public Map selectAllTheMouthChannelDetailAction(Map<String, Object> map);
   /**
    * 通过项目ID和月份来设置渠道费用明细是否上报可用状态
    *
    * @return
    */
   public Integer channelDetailEffective(Map<String, Object> map);

   /**
    * 通过项目ID和月份来查找周计划
    *
    * @return
    */
   public List<Map> selectWeeklyPlan(Map<String, Object> map);

   /**
    * 通过项目ID和月份来初始化周计划
    *
    * @return
    */
   public Integer initialWeeklyPlan(Map<String, Object> map);

   /**
    * 通过项目ID和月份来跟新周计划
    *
    * @return
    */
   public Integer updateWeeklyPlan(Map<String, Object> map);


   /**
    * 在表四上报的时候测试是否和表二的数据一致
    *
    * @return
    */
   public Map testWeeklyPlanSum(Map<String, Object> map);
   /**
    * 通过项目ID和月份来决定周计划上报状态
    *
    * @return
    */
   public Integer weeklyPlanIsEffective(Map<String, Object> map);
   /**
    * 附件的上传
    *
    * @return
    */
   public Integer insertAttach(Map<String, Object> map);

   /**
    * 附件的查找
    *
    * @return
    */
   public List<Map> selectAttach(String projectId);

   /**
    * 附件的删除
    *
    * @return
    */
   public Integer deleteAttach(Map<String, Object> map);

   /**
    * 查找表三的来人量
    *
    * @return
    */
   public Map selectCnt(Map<String, Object> map);

   /**
    * 找到表三里的前三个月均成交套数
    *
    * @return
    */
   public List<Map> selectAverageMonthlySets(Map<String, Object> map);

   /**
    * 找到表三里需要求出前三月月均成交成本和前三月月均来人成本的数据
    *
    * @return
    */
   public List<Map>  threeMonthDealCost(Map<String, Object> map);

   /**
    * 查找项目名
    *
    * @return
    */
   public String  selectProjectName(String projectId);
}
