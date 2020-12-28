package cn.visolink.salesmanage.plandatainterface.service.impl;

import cn.visolink.salesmanage.plandatainterface.dao.PlanDataInterfaceDao;
import cn.visolink.salesmanage.plandatainterface.service.PlanDataInterfaceservice;
import cn.visolink.system.timelogs.bean.SysLog;
import cn.visolink.system.timelogs.dao.TimeLogsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 李欢
 * @date 2019-11-18
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public
class PlanDataInterfaceserviceImpl implements PlanDataInterfaceservice {
    @Autowired
    private PlanDataInterfaceDao interfacedao;

    @Autowired
    private TimeLogsDao timeLogsDao;

    @Resource(name = "jdbcTemplatemy")
    private JdbcTemplate jdbcTemplatemy;
    /**
     * 新增MonthPlan
     */
    @Transactional(rollbackFor=Exception.class)
    @Override
    public Map<String , Object> insertMonthPlan(Map datas) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            String time = format.format(new Date());
            String recordSql1 = "SELECT  p.ProjCode as project_id,p.p_projectId as projectId,s.x_YearMonth as months,s.x_ArrivalVolume as come_client_quantity,s.x_PlanSignTotal as total_sign_funds" +
                    "  FROM dbo.x_s_Sale4TableProjectDetail s" +
                    "  left join p_Project  p on  p.p_projectId = x_ProjGUID" +

                    "  where x_Status='已通过' and x_YearMonth = '"+ datas.get("months") +"'" +
                    "and (p.ParentCode is null or p.ParentCode = '')";
            String recordSql2 = "SELECT  p.ProjCode as project_id,p.p_projectId as projectId,s.x_YearMonth as months ," +
                            datas.get("how_week")+" as week_serial_number\n" +
                    "                    ,sum(s.x_LrNum) as visit_quantity\n" +
                    "                    ,sum(s.x_QyAmount) as sign_target\n" +
                    "                    FROM dbo.x_s_Sale4TableWeekWorkPlanDetail s\n" +
                    "                    left join p_Project  p on  p.p_projectId = s.x_ProjGUID\n" +
                    " left join x_s_Sale4TableProjectDetail detail on detail.x_ProjGUID=s.x_ProjGUID and detail.x_YearMonth=s.x_YearMonth\n "+
                    "                    where detail.x_Status='已通过' and  s.x_YearMonth = '"+ datas.get("months") +"'\n" +
                    "\t\t\t\t\t\t\t\t\t\t and \n" +
                    "\t\t\t\t\t\t\t\t\t\t (p.ParentCode is null or p.ParentCode = '') \n" +
                    "\t\t\t\t\t\t\t\t\t\t and \n" +
                    "                     ((case when "+ datas.get("weekSerialNumber") +"=12 then 1 else 99 end)=x_WeekNum\n" +
                    "\t\t\t\t\t\t\t\t\t\t or  (case when "+ datas.get("weekSerialNumber") + "=12 then 2 else 99 end)=x_WeekNum\n" +
                    "\t\t\t\t\t\t\t\t\t\t )\n" +
                    "\t\t\t\t\t\t\t\t\t\t group by p.p_projectId,s.x_ProjGUID,s.x_YearMonth,p.ProjCode\n" +
                    "\t\t\t\t\t\t\t\t\t\t \n" +
                    "\t\t\t\t\t\t\t\t\t\t union all\n" +
                    "\t\t\t\t\t\t\t\t\t\t \n" +
                    "\t\t\t\t\t\t\t\t\t\t SELECT  p.ProjCode as project_id,p.p_projectId as projectId,s.x_YearMonth as months ," +
                        datas.get("how_week")+" as week_serial_number\n" +
                    "                    ,s.x_LrNum as visit_quantity\n" +
                    "                    ,s.x_QyAmount as sign_target\n" +
                    "                    FROM dbo.x_s_Sale4TableWeekWorkPlanDetail s\n" +
                    "                    left join p_Project  p on  p.p_projectId = s.x_ProjGUID\n" +
                    " left join x_s_Sale4TableProjectDetail detail on detail.x_ProjGUID=s.x_ProjGUID and detail.x_YearMonth=s.x_YearMonth\n "+
                    "                    where detail.x_Status='已通过' and  s.x_YearMonth = '"+ datas.get("months") +"'\n" +

                    "\t\t\t\t\t\t\t\t\t\tand \n" +
                    "\t\t\t\t\t\t\t\t\t\t (p.ParentCode is null or p.ParentCode = '') \n" +
                    "\t\t\t\t\t\t\t\t\t\t and \n" +
                    "                     (\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t (case when  "+ datas.get("weekSerialNumber") +" =1 then 1 else 99 end)=x_WeekNum\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t\t or (case when "+ datas.get("weekSerialNumber") +"=2 then 2 else 99 end)=x_WeekNum\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t\t or (case when "+ datas.get("weekSerialNumber") +"=3 then 3 else 99 end)=x_WeekNum\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t\t or (case when "+ datas.get("weekSerialNumber") +"=4 then 4 else 99 end)=x_WeekNum\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t\t or (case when "+ datas.get("weekSerialNumber") +"=5 then 5 else 99 end)=x_WeekNum\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t\t or (case when "+ datas.get("weekSerialNumber") +"=6 then 6 else 99 end)=x_WeekNum\n" +
                    "\t\t\t\t\t\t\t\t\t\t )";


            SysLog sysLog = new SysLog();

            List<Map<String, Object>> flowLists1 = jdbcTemplatemy.queryForList(recordSql1);
            List<Map<String, Object>> flowLists2 = jdbcTemplatemy.queryForList(recordSql2);
            if(12==Integer.parseInt(datas.get("weekSerialNumber").toString())){
                datas.put("weekSerialNumber",1);
            }
            if (flowLists1 == null || flowLists1.size() == 0 || flowLists1.get(0) == null || flowLists1.get(0).size() == 0) {
                throw new NullPointerException();
            } else {
                List<List<Map<String, Object>>> inserlist = getList(flowLists1);
                if (inserlist != null && inserlist.size() > 0) {
                    //批量插入
                    interfacedao.deleteMonthPlan(datas);
                    for (List<Map<String, Object>> lists : inserlist) {
                        interfacedao.insertMonthPlanCurrent(lists);
                    }
                }
            }
            if (flowLists2 == null || flowLists2.size() == 0 || flowLists2.get(0) == null || flowLists2.get(0).size() == 0) {
                throw new NullPointerException();
            } else {
                List<List<Map<String, Object>>> inserlist = getList(flowLists2);
                if (inserlist != null && inserlist.size() > 0) {
                    //批量插入
                    interfacedao.deleteWeekPlan(datas);
                    for (List<Map<String, Object>> lists : inserlist) {
                        interfacedao.insertWeekPlan(lists);
                    }
                }
            }

            sysLog.setTaskName("月报导入完成");
            sysLog.setTaskName("周报导入完成");


            sysLog.setStartTime(format1.format(new Date()));
            sysLog.setNote("月计划数据：" + flowLists1.size() + "-周计划数据：" + flowLists2.size()  +
                    "----执行sql：" + recordSql1 + "," + recordSql2  );
            timeLogsDao.insertLogs(sysLog);


            List<Map> list1 = reportMonthSelect(datas);

            List<Map> list2 = reportWeekSelect(datas);

            Map<String , Object> allMap = new HashMap<String,Object>();

            allMap.put("monthData",list1);
            allMap.put("weekData",list2);

            return allMap;

        } catch (Exception e) {
           SysLog sysLog = new SysLog();
            sysLog.setTaskName("明源数据导入失败！");
            sysLog.setStartTime(format1.format(new Date()));
            sysLog.setNote("明源数据导入失败消息" + e.getMessage());
            timeLogsDao.insertLogs(sysLog);
        }

        return new HashMap<String , Object>();

    }


    /**
     * 新增basicInfo
     * @return
     */
    @Override
    public void insertBasic() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM");
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            String time = format.format(new Date());
            String recordSql1 =
                    "SELECT ProjStatus as project_status,IsUsed as is_used,ProjName as project_name,p.ProjCode as project_id,x_IsWp as is_late_trader,x_IsYxCp as is_trader,x_trader as trader " +
                    "FROM p_Project  p where (ParentCode is null or ParentCode = '')";
            SysLog sysLog = new SysLog();

            List<Map<String, Object>> flowLists1 = jdbcTemplatemy.queryForList(recordSql1);

            if (flowLists1 == null || flowLists1.size() == 0 || flowLists1.get(0) == null || flowLists1.get(0).size() == 0) {
                throw new NullPointerException();
            } else {
                List<List<Map<String, Object>>> inserlist = getList(flowLists1);
                if (inserlist != null && inserlist.size() > 0) {
                    //批量插入
                    interfacedao.deleteBasic();
                    for (List<Map<String, Object>> lists : inserlist) {
                        interfacedao.insertBasic(lists);
                    }
                }
            }
            sysLog.setTaskName("明源基础数据导入完成");
            sysLog.setStartTime(format1.format(new Date()));
            sysLog.setNote("明源基础数据：" + flowLists1.size() +" sql: " + recordSql1 );
            timeLogsDao.insertLogs(sysLog);
        } catch (Exception e) {
            SysLog sysLog = new SysLog();
            sysLog.setTaskName("明源数据导入失败！");
            sysLog.setStartTime(format1.format(new Date()));
            sysLog.setNote("明源数据导入失败消息" + e.getMessage());
            timeLogsDao.insertLogs(sysLog);
        }
    }

    /**
     * 根据传参是否携带项目ID来判断是否查询全部项目的月报表
     *
     * @param map
     * @return
     */
    @Override
    public List<Map> reportMonthSelect(Map map) {
        return interfacedao.reportMonthSelect(map);
    }

    /**
     * 根据传参是否携带项目ID来判断是否查询全部项目的周报表
     *  mm_common_week_plan
     * @param map
     * @return
     */
    @Override
    public List<Map> reportWeekSelect(Map map) {
        return interfacedao.reportWeekSelect(map);
    }

    /**
     * 根据传参是否携带项目ID来判断是否查询全部项目的周报表
     *  mm_common_week_plan
     * @param map
     * @return
     */
    @Override
    public List<Map> reportBasicSelect(Map map) {
        return interfacedao.reportWeekSelect(map);
    }


    //批量方法
    private List<List<Map<String, Object>>> getList(List reqMap) {
        //list 为全量集合
        int batchCount = 3000; //每批插入数目
        int batchLastIndex = batchCount;
        List<List<Map<String, Object>>> shareList = new ArrayList<>();
        if (reqMap != null) {
            for (int index = 0; index < reqMap.size(); ) {
                if (batchLastIndex >= reqMap.size()) {
                    batchLastIndex = reqMap.size();
                    shareList.add(reqMap.subList(index, batchLastIndex));
                    break;
                } else {
                    shareList.add(reqMap.subList(index, batchLastIndex));
                    index = batchLastIndex;// 设置下一批下标
                    batchLastIndex = index + (batchCount - 1);
                }
            }
        }
        return shareList;
    }


}
