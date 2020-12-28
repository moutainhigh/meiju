package cn.visolink.salesmanage.GroupMonthlyPlan2.service.imple;

import cn.visolink.salesmanage.GroupMonthlyPlan2.dao.GroupMonthlyPlanDao;
import cn.visolink.salesmanage.GroupMonthlyPlan2.service.GroupMonthlyPlanService;
import cn.visolink.salesmanage.groupmanagement.dao.GroupManageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupMonthlyPlanImpl implements GroupMonthlyPlanService {


    //MonthlyPlanMapper
    @Autowired
    private GroupMonthlyPlanDao groupMonthlyPlanDao;

    @Autowired
    private GroupManageDao groupManageDao;

    /**
    * 集团月度计划查询
    * @param months
    * @return
    */
    @Override
    public int GetMonthlyPlanByTheMonthCount(String months) {

        int groupMonthPlan = groupMonthlyPlanDao.GetMonthlyPlanByTheMonthCount(months);

        return groupMonthPlan;
    }

    @Override
    public int SetMonthlyPlanInsert(String months) {
        return 0;
    }
   /* @Override
    public int SetMonthlyPlanInsert(String months) {

      int theMonthCount =  GetMonthlyPlanByTheMonthCount(months);
        int groupMonthPlan = 0;

        if(theMonthCount > 0){

            groupMonthPlan = groupMonthlyPlanDao.SetMonthlyPlanInsert(months);
            Map<String,Object> map = new HashMap<>();
            //添加月度计划主表
            String oneId = UUID.randomUUID().toString();
            map.put("guId", oneId);
            map.put("planName", months+ "2019年八月份集团计划");

            groupManageDao.insertMonthPlan(map);

            //查询事业部
            List<Map> message = groupManageDao.getBusiness(map);
            for (int i = 0; i < message.size(); i++) {
                String twoId = UUID.randomUUID().toString();
                String type = (String) message.get(i).get("type").toString();
                String businessName = (String) message.get(i).get("business_name").toString();
                String businessId = (String) message.get(i).get("business_id").toString();
                //添加月度计划表
                map.put("preparedByLevel", type);
                map.put("preparedByLevelName", businessName);
                map.put("preparedByUnitOrgId", businessId);
                map.put("guId", twoId);
                map.put("monthlyPlanId", oneId);

               // groupMonthlyPlanDao.insertMonthPlanBasis(map);

                //添加月度计划详细
                String threeId = UUID.randomUUID().toString();
                map.put("guID", threeId);
                map.put("monthlyPlaniId", oneId);
                map.put("monthlyPlanBasisId", twoId);
                map.put("preparedByUnitOrgId", businessId);
                map.put("preparedByLevel", type);
                map.put("preparedByLevelName", businessName);
                groupManageDao.insertMonthPlanIndex(map);

            }

        }
        return groupMonthPlan;
    }

*/

}
