package cn.visolink.salesmanage.flow.service.impl;

import cn.visolink.salesmanage.flow.dao.WorkflowDao;
import cn.visolink.salesmanage.flow.service.PriceAdjustmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class PriceAdjustmentSericeImpl implements PriceAdjustmentService {

    //注入sql操作类
    @Resource(name="jdbcTemplategxc")
    private JdbcTemplate jdbcTemplategxc;

    @Autowired
    private WorkflowDao workflowDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map adjustment(String josnId) {

        Map resultMap = new HashMap();

        try {
            if("".equals(josnId) || "null".equals(josnId)){
                throw new RuntimeException("前端未传入主数据id");
            }
            //通过jsonid查询到项目id
            String projectId = workflowDao.selectJsonId(josnId);
            //供销存数据查询货值sql语句
            //动态
            String sqlsDynamic = "select sum(houseValue) as houseValue  from  v_sman_dynamic_value";
            //战规
            String sqlsPlan = "select sum(houseValue) as houseValue from v_sman_plan_value";
            if("".equals(projectId) || "null".equals(projectId)){
                System.out.println("缺少项目参数---项目id");
                throw new RuntimeException("缺少项目参数--项目id");
            }else {
                sqlsDynamic = sqlsDynamic+" where projectId = '"+projectId+"' ";
                sqlsPlan = sqlsPlan+" where projectId = '"+projectId+"' ";
            }
            //查询--动态--货值视图数据（整盘）
            List<Map<String, Object>> listDynamic = jdbcTemplategxc.queryForList(sqlsDynamic);
            //查询--战规--货值视图数据（整盘）
            List<Map<String, Object>> listPlan = jdbcTemplategxc.queryForList(sqlsPlan);
            //整盘(动态版)货值
            Object dynamicValue = listDynamic.get(0).get("houseValue");
            //整盘(战规版)货值
            Object planValue = listPlan.get(0).get("houseValue");

            resultMap.put("dynamicValue",dynamicValue);
            resultMap.put("planValue",planValue);

            System.out.println(resultMap);

        }catch (Exception e){
            e.getMessage();
        }
        return resultMap;
    }

}
