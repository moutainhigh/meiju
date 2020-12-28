package cn.visolink.firstplan.monitor.service.impl;

import cn.visolink.firstplan.monitor.dao.MonitorDao;
import cn.visolink.firstplan.monitor.service.MonitorService;
import cn.visolink.firstplan.opening.dao.OpeningDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MonitorServiceImpl implements MonitorService {

    @Autowired
    private MonitorDao monitorDao;
    @Autowired
    private OpeningDao openingDao;

    /**
     * 监控详情
     * @param params
     * @return
     */
    @Override
    public Map getPlanNodeInfo(Map params) {
        Map info=new HashMap();
        String plan_id=params.get("plan_id")+"";
        //计划信息
        info.put("plan",monitorDao.selectMonitorPlan(plan_id));
        List<Map> node=monitorDao.selectMonitorPlanNode(plan_id);
        info.put("node",node);
        if(node!=null && node.size()>0){
            for(Map data:node){
                String plan_node_id=data.get("id")+"";
                Integer node_level=Integer.parseInt(data.get("node_level")+"");
                if(node_level==1){
                    //拿地
                    Map  land=monitorDao.selectMonitorLand(plan_node_id);
                    info.put("land",land);
                }else if(node_level==2){
                    //顶设1
                    Map  designone=monitorDao.selectMonitorDesignone(plan_node_id);
                    info.put("designone",designone);
                }else if(node_level==3){
                    //顶设2
                    Map designtwo=monitorDao.selectMonitorDesigntwo(plan_node_id);
                    if(designtwo!=null){
                        designtwo.put("avg",monitorDao.selectDesigntwoCodeIndexAvg(plan_node_id));
                    }
                    info.put("designtwo",designtwo);
                }
                Map nodeparmas=new HashMap();
                if(node_level==4||node_level==5||node_level==6||node_level==7){
                    Map temp=new HashMap();
                    nodeparmas.put("node_level",node_level);
                    nodeparmas.put("plan_id",plan_id);
                    List node1=monitorDao.selectMonitorThreeNode(nodeparmas);
                    temp.put("node",node1);
                    temp.put("week",monitorDao.selectMonitorThreeWeek(nodeparmas));
                    info.put("node_level"+node_level,temp);
                }
            }
        }
        //
        return info;
    }

    @Override
    public List queryMonitorNewNode(Map params) {
        //获取计划id
        String plan_id=params.get("plan_id") + "";
        //查询当前计划所属的项目-获取项目id
        String projectId = monitorDao.getProjectidByPlanId(plan_id);


        String new_21open_time="";
        String new_7open_time="";
        String new_open_time="";
        //查询当前计划是否有延期开盘行为
        Map delayApplayOpenDate = monitorDao.getDelayApplayOpenDate(plan_id);
        List<Object> customerStrorageList = new ArrayList<>();
        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("project_id",projectId);
        //查询客储数据
        if(delayApplayOpenDate!=null&&delayApplayOpenDate.size()>0){
            new_21open_time=delayApplayOpenDate.get("new_applay_21time")+"";
            new_7open_time=delayApplayOpenDate.get("new_applay_7time")+"";
            new_open_time=delayApplayOpenDate.get("new_first_time")+"";
        }
        List<Map> customerList = monitorDao.selectMonitorNewNode(params.get("plan_id") + "");
        if(customerList!=null&&customerList.size()>0){
            for (Map map : customerList) {
                //放入计划数据
                customerStrorageList.add(map);
                //获取实际数据
                String nide_name=map.get("nide_name")+"";
                String line_name=map.get("line_name")+"";
                String node_time=map.get("node_time")+"";
                if("首开".equals(nide_name)&&"计划".equals(line_name)){
                    if(!"".equals(new_open_time)&&!"null".equals(new_open_time)){
                        node_time=new_open_time;
                        map.put("node_time",node_time);
                    }
                    //如果开盘数据已经保存  那么首开时间去首开当日的
                    Map openmap = openingDao.selectLastOpening(params.get("plan_id") + "");
                    String sopen="";
                    if (openmap != null) {
                       String opentime=openmap.get("open_time")+"";
                       if(!"".equals(opentime)&&!"null".equals(opentime)){
                           sopen = openmap.get("open_time") + "";
                       }
                    }
                    paramMap.put("node_time",sopen);
                    Map actualMap = monitorDao.getActualCustomerStorage(paramMap);
                    paramMap.put("node_time",node_time);
                    actualMap.put("nide_name",nide_name);
                    //传入计划和实际数据。得出偏差率
                    Map perMap = countDeviationrate(map, actualMap);
                    //放入实际数据
                    customerStrorageList.add(actualMap);
                    //放入偏差率
                    customerStrorageList.add(perMap);
                }else if(!"首开".equals(nide_name)&&"计划".equals(line_name)){
                    if("首开前21天".equals(nide_name)){
                        if(!"".equals(new_21open_time)&&!"null".equals(new_21open_time)){
                            node_time=new_21open_time;
                            map.put("node_time",node_time);
                        }
                    }else if("首开前7天".equals(nide_name)){
                        if(!"".equals(new_7open_time)&&!"null".equals(new_7open_time)){
                            node_time=new_7open_time;
                            map.put("node_time",node_time);
                        }
                    }
                    paramMap.put("node_time",node_time);
                    Map actualMap = monitorDao.getActualCustomerStorage(paramMap);
                    actualMap.put("nide_name",nide_name);
                    //传入计划和实际数据。得出偏差率
                    Map perMap = countDeviationrate(map, actualMap);
                    //放入实际数据
                    customerStrorageList.add(actualMap);
                    //放入偏差率
                    customerStrorageList.add(perMap);
                }
            }

          /*  //如果只查询出来计划数据
            if(customerList.size()==1){
                //查询实际客储数据
                Map actualMap = monitorDao.getActualCustomerStorage(projectId);
                if(actualMap!=null){
                    //获取计划数据
                    customerList.add(actualMap);
                    //计算偏差率客储数据
                    Map perMap = countDeviationrate(customerList.get(0), actualMap);
                    customerList.add(perMap);
                }
            }*/
        }
        return customerStrorageList;
    }

    @Override
    public List selectMonitorPlanNode(String plan_id) {
        return monitorDao.selectMonitorPlanNode(plan_id);
    }

    public Map countDeviationrate(Map planMap,Map acterMap){
        DecimalFormat df = new DecimalFormat();

        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingUsed(false);
        Map<Object, Object> map = new HashMap<>();
       //获取计划报备
        float planReportNum=0;
        String report_num = planMap.get("report_num") + "";
        if(!"".equals(report_num)&&!"null".equals(report_num)){
            planReportNum=Float.parseFloat(report_num);
        }
        //获取实际报备
        float acterReportNum = Float.parseFloat(acterMap.get("report_num") + "");
        //偏差率报备，计划 - 实际 / 计划 * 100
        if(planReportNum!=0&&planReportNum!=0.0){
            map.put("report_num",df.format((planReportNum-acterReportNum)/planReportNum*100));
        }else{
            map.put("report_num",0);
        }
        //获取计划来访
        float planVisitNum = Float.parseFloat(planMap.get("visit_num") + "");
        //获取实际来访
        float acterVisitNum =  Float.parseFloat(acterMap.get("visit_num") + "");
        if(planVisitNum!=0&&planVisitNum!=0.0){
            map.put("visit_num",df.format((planVisitNum-acterVisitNum)/planVisitNum*100));
        }else{
            map.put("visit_num",0);
        }

        //获取计划小卡
        float planlittle_num = Float.parseFloat((planMap.get("little_num") + ""));
        //获取实际小卡
        float acterlittle_num = Float.parseFloat(acterMap.get("little_num") + "");
        if(planlittle_num!=0&&planlittle_num!=0.0){
            map.put("little_num",df.format((planlittle_num-acterlittle_num)/planlittle_num*100));
        }else{
            map.put("little_num",0);
        }
        //获取计划大卡
        float planBig_num = Float.parseFloat(planMap.get("big_num") + "");
        //获取实际大卡
        float acterBig_num = Float.parseFloat(acterMap.get("big_num") + "");
        if(planBig_num!=0&&acterBig_num!=0.0){
            map.put("big_num",df.format((planBig_num-acterBig_num)/planBig_num*100));
        }else{
            map.put("big_num",0);
        }
        //获取计划认购
        float planSub_num = Float.parseFloat(planMap.get("sub_num") + "");
        //获取实际认购
        float acterSub_num= Float.parseFloat(acterMap.get("sub_num") + "");
        if(planSub_num!=0&&planSub_num!=0.0){
            map.put("sub_num",df.format((planSub_num-acterSub_num)/planSub_num*100));
        }else{
            map.put("sub_num",0);
        }

        //获取计划小卡率
        double planLittleper = Double.parseDouble(planMap.get("little_per") + "");
        //获取实际小卡率
        double acterLittleper=Double.parseDouble(acterMap.get("little_per") + "");
        if(planLittleper!=0&&planLittleper!=0.00){
            map.put("little_per",df.format((planLittleper-acterLittleper)/planLittleper*100));
        }else{
            map.put("little_per",0);
        }

        //获取计划大卡率
        double planBigper = Double.parseDouble(planMap.get("big_per") + "");
        //获取实际大卡率
        double acterBigper=Double.parseDouble(acterMap.get("big_per") + "");
        if(planBigper!=0&&planBigper!=0.00){
            map.put("big_per",df.format((planBigper-acterBigper)/planBigper*100));
        }else{
            map.put("big_per",0);
        }

        //获取计划成交率
        double planMakper = Double.parseDouble(planMap.get("make_per") + "");
        //获取实际成交率
        double acterMakper=Double.parseDouble(acterMap.get("make_per") + "");
        if(planMakper!=0&&planMakper!=0.00){
            map.put("make_per",df.format((planMakper-acterMakper)/planMakper*100));
        }else{
            map.put("make_per",0);
        }
        map.put("node_level",7);
        map.put("line_name","偏差率");
        map.put("nide_name",planMap.get("nide_name"));
        return  map;
    }


}
