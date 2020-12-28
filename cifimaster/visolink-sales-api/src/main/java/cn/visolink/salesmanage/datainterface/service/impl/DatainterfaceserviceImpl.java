package cn.visolink.salesmanage.datainterface.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.caopandata.service.CaoPanDataService;
import cn.visolink.salesmanage.datainterface.dao.DatainterfaceDao;
import cn.visolink.salesmanage.datainterface.service.Datainterfaceservice;
import cn.visolink.system.timelogs.bean.SysLog;
import cn.visolink.system.timelogs.dao.TimeLogsDao;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 刘昶SALEMAN-710
 * @date 2019-9-20
 */
@Service
//@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DatainterfaceserviceImpl implements Datainterfaceservice {
    @Autowired
    private DatainterfaceDao interfacedao;

    @Autowired
    private TimeLogsDao timeLogsDao;


    //当前时间参数
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Map insertProject(Map bats) {
        SysLog sysLog1 = new SysLog();
        sysLog1.setStartTime(sf.format(new Date()));
        sysLog1.setTaskName("实时同步项目新增数据");
        sysLog1.setNote(JSON.toJSONString(bats));
        sysLog1.setResultStatus(3);
        timeLogsDao.insertLogs(sysLog1);
        Map map = (Map) bats.get("requestInfo");
        List<Map> mapList = (List<Map>) map.get("datas");
        List<Map> projectmapList = new ArrayList<Map>();
        for (int i = 0; i < mapList.size(); i++) {
            Object projectID = mapList.get(i).get("projectID");
            Map resultMap = interfacedao.selectSysProject(mapList.get(i));
            if (null == resultMap || resultMap.isEmpty() || 0 == resultMap.size()) {
                if (!("".equals(projectID) || null == projectID)) {
                    projectmapList.add(mapList.get(i));
                }
            } else {
                interfacedao.updateProject(mapList.get(i));
            }
        }
        if (!(0 >= projectmapList.size() || null == projectmapList)) {
            interfacedao.insertSysProject(projectmapList);
        }
        Map maplist = new HashMap<>();
        maplist.put("instId", "RVNCTSEsZUASDGTEQ4658MYW5HH");
        maplist.put("returnStatus", "S");
        maplist.put("returnCode", "A0001-SMS");
        maplist.put("returnMsg", "调用成功");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        maplist.put("requstTime", df.format(new Date()));
        maplist.put("reponsTime", df.format(new Date()));
        maplist.put("attr1", null);
        maplist.put("attr2", null);
        maplist.put("attr3", null);
        Map maplistt = new HashMap<>();
        maplistt.put("esbInfo", maplist);
        return maplistt;
    }


    @Override
    public Map insertStaging(Map bats) {
        SysLog sysLog1 = new SysLog();
        sysLog1.setStartTime(sf.format(new Date()));
        sysLog1.setTaskName("实时同步分期新增数据");
        sysLog1.setNote(JSON.toJSONString(bats));
        sysLog1.setResultStatus(4);
        timeLogsDao.insertLogs(sysLog1);
        Map map = (Map) bats.get("requestInfo");
        List<Map> mapList = (List<Map>) map.get("datas");

        List<Map> projectmapList = new ArrayList<Map>();
        for (int i = 0; i < mapList.size(); i++) {
            Object stageCode = mapList.get(i).get("stageCode");
            Map resultMap = interfacedao.selectSysStaging(mapList.get(i));
            if (null == resultMap || resultMap.isEmpty() || 0 == resultMap.size()) {
                if (!("".equals(stageCode) || null == stageCode)) {
                    projectmapList.add(mapList.get(i));
                }
            } else {
                interfacedao.updateStaging(mapList.get(i));
            }
        }
        if (!(0 >= projectmapList.size() || null == projectmapList)) {
            interfacedao.insertSysStaging(projectmapList);
        }
        Map maplist = new HashMap<>();
        maplist.put("instId", "RVNCTSEsZUASDGTEQ4658MYW5HH");
        maplist.put("returnStatus", "S");
        maplist.put("returnCode", "A0001-SMS");
        maplist.put("returnMsg", "调用成功");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        maplist.put("requstTime", df.format(new Date()));
        maplist.put("reponsTime", df.format(new Date()));
        maplist.put("attr1", null);
        maplist.put("attr2", null);
        maplist.put("attr3", null);
        Map maplistt = new HashMap<>();
        maplistt.put("esbInfo", maplist);
        return maplistt;
    }

    @Override
    public ResultBody initMingyuanOrder() {
        ResultBody resultBody = new ResultBody();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            int time = Integer.parseInt(format.format(new Date()));
            for (int topTime = 1999; topTime <= time; topTime++) {
                //认购数据
                String topOrderSql = "SELECT * FROM dotnet_erp60.dbo.VS_XSGL_ORDER \n" +
                        " WHERE( CloseDate >= '" + topTime + "-01-01'\n" +
                        " AND CloseDate <= '" + topTime + "-12-31 23:59:59') or" +
                        " ( YwgsDate >= '" + topTime + "-01-01'\n" +
                        " AND YwgsDate <= '" + topTime + "-12-31 23:59:59') ";

                List<Map<String, Object>> flowOrderLists = jdbcTemplatemy.queryForList(topOrderSql);

                if (flowOrderLists == null || flowOrderLists.size() == 0 || flowOrderLists.get(0) == null || flowOrderLists.get(0).size() == 0) {
                    throw new NullPointerException();
                } else {
                    List<List<Map<String, Object>>> inserlist = getList(flowOrderLists);
                    if (inserlist != null && inserlist.size() > 0) {
                        //批量插入
                        interfacedao.deleteorder(topTime + "");
                        for (List<Map<String, Object>> lists : inserlist) {
                            interfacedao.insrtorder(lists);
                        }
                    }
                }
                flowOrderLists.clear();
            }
            //修改group_id
            interfacedao.updateorder();
            resultBody.setCode(200);
            resultBody.setMessages("初始化成功!");
        } catch (Exception e) {
            e.printStackTrace();
            resultBody.setCode(-5656);
            resultBody.setMessages("初始化失败!");
            return resultBody;

        }
        return resultBody;
    }

    @Override
    public void initOrderAndSignNull() {
        try {
            String recordSql1 = "SELECT roominfo,RoomGUID,bldprdid,buildingcode,buildingname,productcode,productType,projectID,KINGDEEPROJECTID,PROJECTNAME,PROJECTCODE,PROJECTFID,KINGDEEPROJECTFID,STAGENAME,STAGECODE,QSDate,YwgsDate,CloseDate,CloseReason,BldArea,CjRmbTotal,AuditDate,BcTotal,ModifiedTime,ContractGUID,BcArea,Status," +
                    " case when ScBldArea is null then YsBldArea when ScBldArea=0 then YsBldArea \n" +
                    " ELSE ScBldArea\n" +
                    " end as ScBldArea FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT \n" +
                    " WHERE ModifiedTime is null";
            String recordSql2 = "SELECT * FROM dotnet_erp60.dbo.VS_XSGL_ORDER " +
                    " WHERE  ModifiedTime is null ";
            List<Map<String, Object>> flowLists2 = jdbcTemplatemy.queryForList(recordSql2);
            if (flowLists2 == null || flowLists2.size() == 0 || flowLists2.get(0) == null || flowLists2.get(0).size() == 0) {
                throw new NullPointerException();
            } else {
                List<List<Map<String, Object>>> inserlist = getList(flowLists2);
                if (inserlist != null && inserlist.size() > 0) {
                    //删除
                    for (List<Map<String, Object>> lists : inserlist) {
                        //批量插入
                        interfacedao.insrtorder(lists);
                    }
                    interfacedao.updateorder();
                }
            }

            //清空数据
            flowLists2.clear();
            List<Map<String, Object>> flowLists1 = jdbcTemplatemy.queryForList(recordSql1);

            if (flowLists1 == null || flowLists1.size() == 0 || flowLists1.get(0) == null || flowLists1.get(0).size() == 0) {

            } else {
                List<List<Map<String, Object>>> inserlist = getList(flowLists1);
                if (inserlist != null && inserlist.size() > 0) {
                    //批量插入
                    for (List<Map<String, Object>> lists : inserlist) {
                        interfacedao.insrtconrtact(lists);
                    }
                    interfacedao.updateconrtact();
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }


    @Override
    public Map insertGroup(Map bats) {
        SysLog sysLog1 = new SysLog();
        sysLog1.setStartTime(sf.format(new Date()));
        sysLog1.setTaskName("实时同步组团新增数据");
        sysLog1.setNote(JSON.toJSONString(bats));
        sysLog1.setResultStatus(2);
        timeLogsDao.insertLogs(sysLog1);
        Map map = (Map) bats.get("requestInfo");
        List<Map> mapList = (List<Map>) map.get("datas");
        List<Map> projectmapList = new ArrayList<Map>();
        for (int i = 0; i < mapList.size(); i++) {
            Object bldPrdID = mapList.get(i).get("bldPrdID");
            Map resultMap = interfacedao.selectSysGroup(mapList.get(i));
            if (null == resultMap || resultMap.isEmpty() || 0 == resultMap.size()) {
                if (!("".equals(bldPrdID) || null == bldPrdID)) {
                    projectmapList.add(mapList.get(i));
                }
            } else {
                interfacedao.updateGroup(mapList.get(i));
            }
        }
        if (!(0 >= projectmapList.size() || null == projectmapList)) {
            interfacedao.insertSysGroup(projectmapList);
        }
        Map maplist = new HashMap<>();
        maplist.put("instId", "RVNCTSEsZUASDGTEQ4658MYW5HH");
        maplist.put("returnStatus", "S");
        maplist.put("returnCode", "A0001-SMS");
        maplist.put("returnMsg", "调用成功");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        maplist.put("requstTime", df.format(new Date()));
        maplist.put("reponsTime", df.format(new Date()));
        maplist.put("attr1", null);
        maplist.put("attr2", null);
        maplist.put("attr3", null);
        Map maplistt = new HashMap<>();
        maplistt.put("esbInfo", maplist);
        return maplistt;
    }


    @Override
    public Map insertDesignBuild(Map bats) {
        Date date = new Date();
        SysLog sysLog1 = new SysLog();
        sysLog1.setStartTime(sf.format(date));
        sysLog1.setTaskName("实时同步楼栋新增数据");
        sysLog1.setNote(JSON.toJSONString(bats));
        sysLog1.setResultStatus(1);
        timeLogsDao.insertLogs(sysLog1);
        Map map = (Map) bats.get("requestInfo");
        List<Map> mapList = (List<Map>) map.get("datas");

        List<Map> projectAddList = new ArrayList<>();
        List<Map> projectUpdateList = new ArrayList<>();
        /* 批量查询注释 */
        /* bql 2020.08.03 */
        List<String> list = interfacedao.selectSysDesignBuildBatch(mapList);
        for (Map m : mapList) {
            if (list.contains(m.get("bldPrdID"))) {
                projectUpdateList.add(m);
            } else {
                projectAddList.add(m);
            }
        }
        /* 批量修改 */
        /* bql 2020.07.28 */
        if (projectUpdateList.size() > 0) {
            interfacedao.updateDesignBuildBatch(projectUpdateList);
        }

        /* 批量添加 */
        if (projectAddList.size() > 0) {
            interfacedao.insertSysDesignBuild(projectAddList);
        }

        Map<String,String> maplist = new HashMap<>(11);
        maplist.put("instId", "RVNCTSEsZUASDGTEQ4658MYW5HH");
        maplist.put("returnStatus", "S");
        maplist.put("returnCode", "A0001-SMS");
        maplist.put("returnMsg", "调用成功");
        maplist.put("requstTime", DateUtil.format(date,"yyyy-MM-dd HH:mm:ss"));
        maplist.put("reponsTime", DateUtil.format(date,"yyyy-MM-dd HH:mm:ss"));
        maplist.put("attr1", null);
        maplist.put("attr2", null);
        maplist.put("attr3", null);
        Map<String,Object> maplistt = new HashMap<>();
        maplistt.put("esbInfo", maplist);
        return maplistt;
    }

    @Override
    public List<Map> seleteSysorg() {

        return interfacedao.seleteSysorg();
    }

    @Override
    public List<Map> selectCity(String city) {
        return interfacedao.seleteCity(city);
    }

    @Override
    public List<Map> selectProject(Integer city, int businessunit) {
        return interfacedao.selectProject(city, businessunit);
    }

    @Override
    public List<Map> selectStaging(String id) {
        return interfacedao.selectStaging(id);
    }

    @Override
    public List<Map> selectGroup(String projectid, String projectfid) {
        return interfacedao.selectGroup(projectid, projectfid);
    }

    @Override
    public List<Map> selectdesignbuild(String designbuild) {
        return interfacedao.selectDesignBuild(designbuild);
    }

    @Override
    public void insertbusiness(List<Map> list) {
        interfacedao.insertbusiness(list);
    }


    @Resource(name = "jdbcTemplatemy")
    private JdbcTemplate jdbcTemplatemy;

    //操盘数据
    @Autowired
    private CaoPanDataService caoPanDataService;

    @Override
    public void mingyuan() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        //前一年
        String time = format.format(new Date());
        String topTime = (Integer.parseInt(time) - 1) + "";
        try {
            //上一年
            //签约数据
            String topContractSql = "SELECT roominfo,RoomGUID,bldprdid,buildingcode,buildingname,productcode,productType,projectID,KINGDEEPROJECTID,PROJECTNAME,PROJECTCODE,PROJECTFID,KINGDEEPROJECTFID,STAGENAME,STAGECODE,QSDate,YwgsDate,CloseDate,CloseReason,BldArea,CjRmbTotal,AuditDate,BcTotal,ModifiedTime,ContractGUID,BcArea,Status," +
                    " case when ScBldArea is null then YsBldArea when ScBldArea=0 then YsBldArea \n" +
                    " ELSE ScBldArea\n" +
                    " end as ScBldArea FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT \n" +
                    " WHERE (AuditDate >= '" + topTime + "-01-01'\n" +
                    " AND AuditDate <= '" + topTime + "-12-31 23:59:59') or" +
                    "  ( CloseDate >= '" + topTime + "-01-01'\n" +
                    " AND CloseDate <= '" + topTime + "-12-31 23:59:59') or" +
                    " ( YwgsDate >= '" + topTime + "-01-01'\n" +
                    " AND YwgsDate <= '" + topTime + "-12-31 23:59:59') ";
            //认购数据
            String topOrderSql = "SELECT * FROM dotnet_erp60.dbo.VS_XSGL_ORDER \n" +
                    " WHERE( CloseDate >= '" + topTime + "-01-01'\n" +
                    " AND CloseDate <= '" + topTime + "-12-31 23:59:59') or" +
                    " ( YwgsDate >= '" + topTime + "-01-01'\n" +
                    " AND YwgsDate <= '" + topTime + "-12-31 23:59:59') ";

            List<Map<String, Object>> flowContractLists = jdbcTemplatemy.queryForList(topContractSql);
            List<Map<String, Object>> flowOrderLists = jdbcTemplatemy.queryForList(topOrderSql);

            if (flowOrderLists == null || flowOrderLists.size() == 0 || flowOrderLists.get(0) == null || flowOrderLists.get(0).size() == 0) {
                throw new NullPointerException();
            } else {
                List<List<Map<String, Object>>> inserlist = getList(flowOrderLists);
                if (inserlist != null && inserlist.size() > 0) {
                    //批量插入
                    interfacedao.deleteorder(topTime);
                    for (List<Map<String, Object>> lists : inserlist) {
                        interfacedao.insrtorder(lists);
                    }
                }
            }
            //签约
            if (flowContractLists == null || flowContractLists.size() == 0 || flowContractLists.get(0) == null || flowContractLists.get(0).size() == 0) {
                throw new NullPointerException();
            } else {
                List<List<Map<String, Object>>> inserlist = getList(flowContractLists);
                if (inserlist != null && inserlist.size() > 0) {
                    //批量插入
                    interfacedao.deletecontract(topTime);
                    for (List<Map<String, Object>> lists : inserlist) {
                        interfacedao.insrtconrtact(lists);
                    }
                }
            }
            //情况上一年map
            flowContractLists.clear();
            flowOrderLists.clear();

            String recordSql1 = "SELECT roominfo,RoomGUID,bldprdid,buildingcode,buildingname,productcode,productType,projectID,KINGDEEPROJECTID,PROJECTNAME,PROJECTCODE,PROJECTFID,KINGDEEPROJECTFID,STAGENAME,STAGECODE,QSDate,YwgsDate,CloseDate,CloseReason,BldArea,CjRmbTotal,AuditDate,BcTotal,ModifiedTime,ContractGUID,BcArea,Status," +
                    " case when ScBldArea is null then YsBldArea when ScBldArea=0 then YsBldArea \n" +
                    " ELSE ScBldArea\n" +
                    " end as ScBldArea FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT \n" +
                    " WHERE (AuditDate >= DateName(year,GETDATE()) + '-01-01'\n" +
                    " AND AuditDate <= DateName(year,GETDATE()) + '-12-31 23:59:59') or" +
                    " (CloseDate >= DateName(year,GETDATE()) + '-01-01'\n" +
                    " AND CloseDate <= DateName(year,GETDATE()) + '-12-31 23:59:59') or" +
                    " (YwgsDate >= DateName(year,GETDATE()) + '-01-01'\n" +
                    " AND YwgsDate <= DateName(year,GETDATE()) + '-12-31 23:59:59') ";
            String recordSql2 = "SELECT * FROM dotnet_erp60.dbo.VS_XSGL_ORDER \n" +
                    " WHERE (CloseDate >= DateName(year,GETDATE()) + '-01-01'\n" +
                    " AND CloseDate <= DateName(year,GETDATE()) + '-12-31 23:59:59') or" +
                    " (YwgsDate >= DateName(year,GETDATE()) + '-01-01'\n" +
                    " AND YwgsDate <= DateName(year,GETDATE()) + '-12-31 23:59:59') ";
            // String recordSql3 = "SELECT * FROM dotnet_erp352.dbo.VS_XSGL_FYQZKJ WHERE ftyear='" + time + "'";//费用
            String recordSql4 = "SELECT * FROM dotnet_erp60.dbo.VS_XSGL_VISITS WHERE \n" +
                    "\t GjDate>=(SELECT CONVERT(CHAR(10),DATEADD(month,-3,DATEADD(dd,-DAY(GETDATE())+1,GETDATE())),120)  )\n" +
                    "\t and GjDate<=GETDATE()";


            List<Map<String, Object>> flowLists2 = jdbcTemplatemy.queryForList(recordSql2);
            List<Map<String, Object>> flowLists4 = jdbcTemplatemy.queryForList(recordSql4);
            SysLog sysLog = new SysLog();


            if (flowLists2 == null || flowLists2.size() == 0 || flowLists2.get(0) == null || flowLists2.get(0).size() == 0) {
                throw new NullPointerException();
            } else {
                List<List<Map<String, Object>>> inserlist = getList(flowLists2);
                if (inserlist != null && inserlist.size() > 0) {
                    //批量插入
                    interfacedao.deleteorder(time);
                    for (List<Map<String, Object>> lists : inserlist) {
                        interfacedao.insrtorder(lists);
                    }
                    interfacedao.updateorder();
                }
            }

            if (flowLists4 == null || flowLists4.size() == 0 || flowLists4.get(0) == null || flowLists4.get(0).size() == 0) {
                throw new NullPointerException();
            } else {
                List<List<Map<String, Object>>> inserlist = getList(flowLists4);
                if (inserlist != null && inserlist.size() > 0) {
                    //批量插入
                    interfacedao.deletecstnums();
                    for (List<Map<String, Object>> lists : inserlist) {
                        interfacedao.insertcstnums(lists);
                    }
                }
            }

            int flowLists2size = flowLists2.size();
            int flowLists4size = flowLists4.size();
            //清空数据
            flowLists2.clear();
            flowLists4.clear();

            List<Map<String, Object>> flowLists1 = jdbcTemplatemy.queryForList(recordSql1);
            //List<Map<String, Object>> flowLists3 = jdbcTemplatemy.queryForList(recordSql3);

            if (flowLists1 == null || flowLists1.size() == 0 || flowLists1.get(0) == null || flowLists1.get(0).size() == 0) {
                throw new NullPointerException();
            } else {
                List<List<Map<String, Object>>> inserlist = getList(flowLists1);
                if (inserlist != null && inserlist.size() > 0) {
                    //批量插入
                    interfacedao.deletecontract(time);
                    for (List<Map<String, Object>> lists : inserlist) {
                        interfacedao.insrtconrtact(lists);
                    }
                    interfacedao.updateconrtact();
                }
            }


/*            if (flowLists3 == null || flowLists3.size() == 0 || flowLists3.get(0) == null || flowLists3.get(0).size() == 0) {

            } else {
                List<List<Map<String, Object>>> inserlist = getList(flowLists3);
                if (inserlist != null && inserlist.size() > 0) {
                    //批量插入
                    interfacedao.deletefyqzkl(time);
                    sysLog.setTaskName("费用删除成功！");
                    sysLog.setStartTime(format1.format(new Date()));
                    sysLog.setNote("费用删除成功成功");
                    timeLogsDao.insertLogs(sysLog);
                    for (List<Map<String, Object>> lists : inserlist) {
                        interfacedao.insertfyqzkl(lists);
                    }
                }
            }*/
            //nos签约数据
            caoPanDataService.getSigningData();

            sysLog.setTaskName("签约同步完成");
            sysLog.setStartTime(format1.format(new Date()));
            sysLog.setNote("总数据签约：" + flowLists1.size() + "-认购：" + flowLists2size + "-来人量：" + flowLists4size +
                    "----执行sql：" + recordSql1 + "," + recordSql2 + recordSql4);
            timeLogsDao.insertLogs(sysLog);
        } catch (Exception e) {
            SysLog sysLog = new SysLog();
            sysLog.setTaskName("明源数据导入失败!");
            sysLog.setStartTime(format1.format(new Date()));
            sysLog.setNote("明源数据导入失败消息" + e.getMessage());
            timeLogsDao.insertLogs(sysLog);
        }
    }

    @Override
    public void updataBusinsee() {
        List<Map> sysorg = interfacedao.selectquyu();//查询所有的区域集团跟事业部
        interfacedao.deletebu();
        Map map = new HashMap();
        map.put("org_id", "f20c7c1f-d941-11e9-abaa-00163e05721e");
        map.put("Sort_code", "-1");
        map.put("father_id", "-1");
        map.put("type", "1");
        map.put("orgdep_id", "00000001");
        map.put("org_name", "旭辉集团");
        map.put("is_delete_flag", "0");
        map.put("monthly_weekly_type", "1");
        sysorg.add(0, map);
        List<Map> list = new ArrayList<>();
        String uid = null;
        String puid = null;
        String suid = null;
        String guid = null;
        String buid = null;
        String luid = null;
        int code = 0;
        for (int i = 0; i <= sysorg.size(); i++) {
            if (i == sysorg.size()) {
                continue;
            }
            uid = UUID.randomUUID().toString();
            sysorg.get(i).put("Sort_code", code++);
            sysorg.get(i).put("guid", uid);
            if (i != 0) {
                sysorg.get(i).put("father_id", "f20c7c1f-d941-11e9-abaa-00163e05721e");
                sysorg.get(i).put("type", "2");
            }
            //添加前先修改表
            interfacedao.insertbusinessunit(sysorg.get(i));//添加事业部

            List<Map> businessunit = interfacedao.selectProjectt(String.valueOf(sysorg.get(i).get("orgdep_id")));


            //添加项目
            for (int j = 0; j <= businessunit.size(); j++) {
                if (j == businessunit.size()) {
                    continue;
                }
                //区分月度项目周度项目
                String monthly_weekly_type = businessunit.get(j).get("monthly_type") + "";
                monthly_weekly_type = monthly_weekly_type.equals("1") ? monthly_weekly_type : "0";
                puid = UUID.randomUUID().toString();
                businessunit.get(j).put("type", "3");//添加类型
                businessunit.get(j).put("father_id", sysorg.get(i).get("org_id"));//添加区域集团的父id
                businessunit.get(j).put("guid", puid);//添加事业表的guid
                businessunit.get(j).put("region_org_id", sysorg.get(i).get("orgdep_id"));
                businessunit.get(j).put("Sort_code", code++);
                interfacedao.insertprojects(businessunit.get(j));//添加项目
                String flag = businessunit.get(j).get("flag") + "";
                String monthly_type = businessunit.get(j).get("monthly_type") + "";

                //周度不生成分期之下
                if (monthly_type == null || monthly_type == "" || "null".equals(monthly_type)) {
                    continue;
                }
                //查询分期
                List<Map> businessstaging = interfacedao.selectStagingg(String.valueOf((businessunit.get(j).get("project_id"))));
                //查询分期
                for (int l = 0; l <= businessstaging.size(); l++) {
                    if (l == businessstaging.size()) {
                        continue;
                    }
                    suid = UUID.randomUUID().toString();
                    if (businessstaging == null || businessstaging.size() == 0 || businessstaging.get(l) == null || businessstaging.get(l).size() == 0) {
                        break;
                    }
                    businessstaging.get(l).put("type", "4");//添加类型
                    businessstaging.get(l).put("father_id", businessunit.get(j).get("pro_id"));//添加区域集团的父id
                    businessstaging.get(l).put("guid", suid);//添加事业表的guid
                    businessstaging.get(l).put("region_org_id", sysorg.get(i).get("orgdep_id"));
                    businessstaging.get(l).put("project_org_id", businessunit.get(j).get("project_id"));
                    businessstaging.get(l).put("Sort_code", code++);
                    businessstaging.get(l).put("monthly_weekly_type", monthly_weekly_type);
                    interfacedao.insertstagings(businessstaging.get(l));//添加分期


                    //查询出来的组团
                    List<Map> group = interfacedao.selectGroupp(String.valueOf(businessstaging.get(l).get("stage_id")));
                    //添加组团

                    for (int n = 0; n <= group.size(); n++) {
                        if (n == group.size()) {
                            continue;
                        }
                        if (group == null || group.size() == 0 || group.get(n) == null || group.get(n).size() == 0) {
                            break;
                        }
                        guid = UUID.randomUUID().toString();
                        group.get(n).put("type", "5");//添加类型
                        group.get(n).put("father_id", suid);//添加区域集团的父id
                        group.get(n).put("guid", guid);//添加事业表的guid
                        group.get(n).put("region_org_id", sysorg.get(i).get("orgdep_id"));
                        group.get(n).put("project_org_id", businessunit.get(j).get("project_id"));
                        group.get(n).put("stage_org_id", businessstaging.get(l).get("stage_code"));
                        group.get(n).put("Sort_code", code++);
                        group.get(n).put("monthly_weekly_type", monthly_weekly_type);
                        interfacedao.insertgroups(group.get(n));//添加组团

                        //             list.add(group.get(n));
                        //根据组团id查询业态
                        List<Map> designbuild = interfacedao.selectDesignBuildd(String.valueOf(group.get(n).get("group_id")));
                        String num = null;

                        for (int o = 0; o <= designbuild.size(); o++) {
                            if (o == designbuild.size()) {
                                continue;
                            }
                            if (designbuild == null || designbuild.size() == 0 || designbuild.get(o) == null || designbuild.get(o).size() == 0) {
                                break;
                            }
                            System.out.println(designbuild.get(o).get("product_code") + "product");
                            buid = UUID.randomUUID().toString();
                            designbuild.get(o).put("type", "6");//添加类型
                            designbuild.get(o).put("father_id", guid);//添加区域集团的父id
                            designbuild.get(o).put("guid", buid);//添加事业表的guid
                            designbuild.get(o).put("region_org_id", sysorg.get(i).get("orgdep_id"));
                            designbuild.get(o).put("project_org_id", businessunit.get(j).get("project_id"));
                            designbuild.get(o).put("stage_org_id", businessstaging.get(l).get("stage_code"));
                            designbuild.get(o).put("group_org_id", group.get(n).get("group_code"));
                            designbuild.get(o).put("Sort_code", code++);
                            System.out.println("------------------------新增了" + code + "条");
                            designbuild.get(o).put("monthly_weekly_type", monthly_weekly_type);
                            interfacedao.insertesignbuilds(designbuild.get(o));//添加业态

                            //nos数据不生产面积段
                            if ("1".equals(flag)) {
                                continue;
                            }
                            //根据业态查询面积段
                            List<Map> lou = interfacedao.selectArea(String.valueOf(designbuild.get(o).get("product_code")));

                            for (int p = 0; p <= lou.size(); p++) {
                                if (p == lou.size()) {
                                    continue;
                                }
                                if (lou == null || lou.size() == 0 || lou.get(p) == null || lou.get(p).size() == 0) {
                                    break;
                                }
                                luid = UUID.randomUUID().toString();
                                lou.get(p).put("type", "7");//添加类型
                                lou.get(p).put("father_id", buid);//添加区域集团的父id
                                lou.get(p).put("guid", luid);//添加事业表的guid
                                lou.get(p).put("region_org_id", sysorg.get(i).get("orgdep_id"));
                                lou.get(p).put("project_org_id", businessunit.get(j).get("project_id"));
                                lou.get(p).put("stage_org_id", businessstaging.get(l).get("stage_code"));
                                lou.get(p).put("group_org_id", group.get(n).get("group_code"));
                                lou.get(p).put("product_org_id", designbuild.get(o).get("product_code"));
                                lou.get(p).put("Sort_code", code++);
                                lou.get(p).put("monthly_weekly_type", monthly_weekly_type);
                                interfacedao.insertArea(lou.get(p));//添加面积段

                            }

                        }
                    }
                }
            }
        }
        //月计划初始化对应区域monthly_weekly_type=1字段
        interfacedao.updateBusinessUnit();
    }

    @Override
    public void initmmidm() {
        //删除并新增区域项目关系
        //先删除再新增
        interfacedao.delprojectrel();
        interfacedao.insertprojectrel();
        //修改区域排序
        interfacedao.updateprojectrel();
        //初始组织表（已使用idm组织信息）
        //初始化项目分期
        interfacedao.insertProjectStagerel();
        //初始化分期组团关系表
        interfacedao.insertStageGroup();
        //组团楼栋
        interfacedao.insertGroupDesignbuildrel();
        //组团业态
        interfacedao.insertproductgroup();
        //初始化产品楼栋关系表
        interfacedao.insertproductrel();
        //初始化产品面积段关系表
        interfacedao.insertproducareatrel();
        //集团到面积段关系表
        interfacedao.insertmainrel();
        //初始化整合关系
    }

    @Override
    public void selectsignset(String time5) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String time = format.format(new Date());
        String time3 = format.format(new Date());
        time3 = time3 + "-01";
        String time4 = format.format(new Date());
        time4 = time4 + "%";
        SimpleDateFormat fo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time2 = fo.format(new Date());
        List<Map> list = interfacedao.selectsignse(time5);
        for (int i = 0; i < list.size(); i++) {
            List<Map<String, Object>> list2 = jdbcTemplatemy.queryForList("SELECT * FROM p_Project where  ProjCode ='" + list.get(i).get("projectCode") + "'");//存项目id
            if (list2 == null || list2.size() == 0 || list2.get(0) == null || list2.get(0).size() == 0) {
                continue;
            }
            String recordSql4 = "select * FROM x_s_Sale4TableProjectDetail WHERE  CreatedTime = '" + time5 + "' and x_ProjGUID='" + list2.get(0).get("p_projectId") + "'";
            List<Map<String, Object>> list4 = jdbcTemplatemy.queryForList(recordSql4);
            List<Map<String, Object>> list3 = jdbcTemplatemy.queryForList("SELECT distinct  bu.[BUGUID],bu. [BUName]  ,p.ProjName,p.[p_projectId],p.[ProjCode],p.BUGUID from  [x_s_Sale4TableProjectDetail] stpd \n" +
                    "left join  p_Project  p on p.p_projectId = stpd.x_ProjGUID\n" +
                    "left join myBusinessUnit bu on bu.[BUGUID] = p.BUGUID\n" +
                    "where p.[ProjCode] ='" + list.get(i).get("projectCode") + "'");//公司id ,公司名字
            if (list3 == null || list3.size() == 0 || list3.get(0) == null || list3.get(0).size() == 0) {
                continue;
            }
            BigDecimal b1 = new BigDecimal(list.get(i).get("reserve_can_sell_funds").toString());
            BigDecimal b2 = new BigDecimal(list.get(i).get("new_reserve_funds").toString());
            String num = b1.add(b2).toString();//(Double.parseDouble(list.get(i).get("reserve_can_sell_funds").toString())) + (Double.parseDouble(list.get(i).get("new_reserve_funds").toString()));
            if (list4.size() == 0) {
                SimpleDateFormat format6 = new SimpleDateFormat("yyyy-MM-dd");
                String year = time5.substring(0, 4);
                //截取月份
                String month = time5.substring(5, 7);
                String time6 = year + "-" + month;
                String recordSql1 = " insert into dotnet_erp60.dbo.x_s_Sale4TableProjectDetail values" +
                        "( newId(),'" + time5 + "',newId(),'陈峥艳','" + time2 + "',newId(),'魏杰',null,'" + list3.get(0).get("BUGUID") + "','" + list3.get(0).get("BUName") + "','" + time6 + "','" + list2.get(0).get("p_projectId") + "','" + list2.get(0).get("ProjName") + "','已通过'" +
                        ",2,'" + list.get(i).get("reserve_can_sell_funds") + "','" + list.get(i).get("new_reserve_funds") + "'," + num + ",'" + list.get(i).get("total_sign_funds") + "','" + list.get(i).get("reserve_sign_funds") + "','" +
                        list.get(i).get("new_sign_funds") + "',0,0,0,0,0,0,0,0,0,0,0,0,0,0,'已通过',2,null,0,0,'" + list.get(i).get("total_sign_set") + "')";
                jdbcTemplatemy.update(recordSql1);
                System.out.println("新增1了第" + i + "条" + list.get(i).get("prepared_by_level_name") + "-----合集金额" + list.get(i).get("total_sign_funds"));
            }


            for (int j = 0; j < list4.size(); j++) {
                String year = time5.substring(0, 4);
                //截取月份
                String month = time5.substring(5, 7);
                String time6 = year + "-" + month;
                if (String.valueOf(list4.get(j).get("x_ProjGUID")).equals(String.valueOf(list2.get(0).get("p_projectId")))) {
                    String recordSql5 = "update x_s_Sale4TableProjectDetail set " +
                            "ModifiedTime='" + time5 + "',ModifiedGUID=newId(),ModifiedName='系统管理员',x_BUGUID='" + list3.get(0).get("BUGUID") + "',x_BUName='" + list3.get(0).get("BUName") + "',x_YearMonth='" + time6 +
                            "',x_KcTotal='" + list.get(i).get("reserve_can_sell_funds") + "',x_NewTotal='" + list.get(i).get("new_reserve_funds") + "',x_DyTotal='" + num + "',x_PlanSignTotal='" + list.get(i).get("total_sign_funds") + "',x_PlanSignCount='" + list.get(i).get("total_sign_set") + "'" +
                            ",x_KcSignTotal='" + list.get(i).get("reserve_sign_funds") + "'" + ",x_NewSignTotal='" + list.get(i).get("new_sign_funds") + "'" +
                            " where Convert(varchar,CreatedTime,120)  like '" + time4 + "' and x_ProjGUID='" + list4.get(j).get("x_ProjGUID") + "'";
                    jdbcTemplatemy.update(recordSql5);
                    System.out.println("修改了第" + i + "条-----  " + list.get(i).get("prepared_by_level_name") + "-----合集金额" + list.get(i).get("total_sign_funds"));
                } else {
                    String recordSql1 = " insert into dotnet_erp60.dbo.x_s_Sale4TableProjectDetail values" +
                            "( newId(),'" + time5 + "',newId(),'陈峥艳','" + time2 + "',newId(),'魏杰',null,'" + list3.get(0).get("BUGUID") + "','" + list3.get(0).get("BUName") + "','" + time6 + "','" + list2.get(0).get("p_projectId") + "','" + list2.get(0).get("ProjName") + "','已通过'" +
                            ",2,'" + list.get(i).get("reserve_can_sell_funds") + "','" + list.get(i).get("new_reserve_funds") + "'," + num + ",'" + list.get(i).get("total_sign_funds") + "','" + list.get(i).get("reserve_sign_funds") + "','" +
                            list.get(i).get("new_sign_funds") + "',0,0,0,0,0,0,0,0,0,0,0,0,0,'已通过',2,null,0,0,'" + list.get(i).get("total_sign_set") + "')";
                    jdbcTemplatemy.update(recordSql1);
                    System.out.println("新增2了第" + i + "条" + list.get(i).get("prepared_by_level_name") + "-----合集金额" + list.get(i).get("total_sign_funds"));
                }
            }
        }
    }


    /**
     * 发送esb接口数据查询
     */
    @Override
    public Map selectSendGXC(Map parmas) {
        Map<Object, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> list = interfacedao.selectSendGXC(parmas);
        resultMap.put("list", list);
        return resultMap;
    }

    @Override
    public void initlairenliang(String time) {
        String recordSql4 = "SELECT * FROM dotnet_erp60.dbo.VS_XSGL_VISITS WHERE GjDate>='" + time + "'";
        List<Map<String, Object>> flowLists4 = jdbcTemplatemy.queryForList(recordSql4);

        if (flowLists4 == null || flowLists4.size() == 0 || flowLists4.get(0) == null || flowLists4.get(0).size() == 0) {

        } else {
            List<List<Map<String, Object>>> inserlist = getList(flowLists4);
            if (inserlist != null && inserlist.size() > 0) {
                //批量插入
                interfacedao.deleteVisitsByGjDate(time);
                for (List<Map<String, Object>> lists : inserlist) {
                    interfacedao.insertcstnums(lists);
                }
            }
        }
    }


    @Override
    public void initmingyuan(String startTime, String endTime) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String recordSql1 = "SELECT roominfo,RoomGUID,bldprdid,buildingcode,buildingname,productcode,productType,projectID,KINGDEEPROJECTID,PROJECTNAME,PROJECTCODE,PROJECTFID,KINGDEEPROJECTFID,STAGENAME,STAGECODE,QSDate,YwgsDate,CloseDate,CloseReason,BldArea,CjRmbTotal,AuditDate,BcTotal,ModifiedTime,ContractGUID,BcArea,Status," +
                    " case when ScBldArea is null then YsBldArea when ScBldArea=0 then YsBldArea \n" +
                    " ELSE ScBldArea\n" +
                    " end as ScBldArea FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT \n" +
                    " WHERE ModifiedTime >= '" + startTime + "'" +
                    " AND ModifiedTime <= '" + endTime + "'";
            String recordSql2 = "SELECT * FROM dotnet_erp60.dbo.VS_XSGL_ORDER " +
                    " WHERE  ModifiedTime >= '" + startTime + "'" +
                    " AND ModifiedTime <= '" + endTime + "'";
            List<Map<String, Object>> flowLists2 = jdbcTemplatemy.queryForList(recordSql2);
            if (flowLists2 == null || flowLists2.size() == 0 || flowLists2.get(0) == null || flowLists2.get(0).size() == 0) {
                throw new NullPointerException();
            } else {
                List<List<Map<String, Object>>> inserlist = getList(flowLists2);
                if (inserlist != null && inserlist.size() > 0) {
                    //删除
                    interfacedao.deleteOrderByUpdateTime(startTime, endTime);
                    for (List<Map<String, Object>> lists : inserlist) {
                        //批量插入
                        interfacedao.insrtorder(lists);
                    }
                    interfacedao.updateorder();
                }
            }

            //清空数据
            flowLists2.clear();
            List<Map<String, Object>> flowLists1 = jdbcTemplatemy.queryForList(recordSql1);

            if (flowLists1 == null || flowLists1.size() == 0 || flowLists1.get(0) == null || flowLists1.get(0).size() == 0) {

            } else {
                List<List<Map<String, Object>>> inserlist = getList(flowLists1);
                if (inserlist != null && inserlist.size() > 0) {
                    //删除指定时间
                    interfacedao.deleteContractByUpdateDate(startTime, endTime);
                    //批量插入
                    for (List<Map<String, Object>> lists : inserlist) {
                        interfacedao.insrtconrtact(lists);
                    }
                    interfacedao.updateconrtact();
                }
            }
        } catch (Exception e) {
            SysLog sysLog = new SysLog();
            sysLog.setTaskName("明源数据导入失败！");
            sysLog.setStartTime(format1.format(new Date()));
            sysLog.setNote("明源数据导入失败消息" + e.getMessage());
            timeLogsDao.insertLogs(sysLog);
        }
    }


    @Override
    public void initSignByStart(String startTime, String endTime) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String recordSql1 = "SELECT roominfo,RoomGUID,bldprdid,buildingcode,buildingname,productcode,productType,projectID,KINGDEEPROJECTID,PROJECTNAME,PROJECTCODE,PROJECTFID,KINGDEEPROJECTFID,STAGENAME,STAGECODE,QSDate,YwgsDate,CloseDate,CloseReason,BldArea,CjRmbTotal,AuditDate,BcTotal,ModifiedTime,ContractGUID,BcArea,Status," +
                    " case when ScBldArea is null then YsBldArea when ScBldArea=0 then YsBldArea \n" +
                    " ELSE ScBldArea\n" +
                    " end as ScBldArea FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT \n" +
                    " WHERE ModifiedTime >= '" + startTime + "'" +
                    " AND ModifiedTime <= '" + endTime + "'";
            List<Map<String, Object>> flowLists1 = jdbcTemplatemy.queryForList(recordSql1);
            if (flowLists1 == null || flowLists1.size() == 0 || flowLists1.get(0) == null || flowLists1.get(0).size() == 0) {

            } else {
                List<List<Map<String, Object>>> inserlist = getList(flowLists1);
                if (inserlist != null && inserlist.size() > 0) {
                    //删除指定时间
                    interfacedao.deleteContractByUpdateDate(startTime, endTime);
                    //批量插入
                    for (List<Map<String, Object>> lists : inserlist) {
                        interfacedao.insrtconrtact(lists);
                    }
                    interfacedao.updateconrtact();
                }
            }
        } catch (Exception e) {
            SysLog sysLog = new SysLog();
            sysLog.setTaskName("明源数据导入失败！");
            sysLog.setStartTime(format1.format(new Date()));
            sysLog.setNote("明源数据导入失败消息" + e.getMessage());
            timeLogsDao.insertLogs(sysLog);
        }
    }

    /**
     * 初始化项目标识
     */
    @Override
    public int initProjectType() {
        interfacedao.initMonthProjectType();
        interfacedao.initFirstPlanProjectType();
        interfacedao.initProjectCpType();
        interfacedao.initProjectYxzc();
        return 0;
    }


    public int delRepeatData() {
        interfacedao.delRepeatDataBusiness();
        interfacedao.delRepeatDataBusinessRel();
        return 0;
    }

    //批量方法
    private List<List<Map<String, Object>>> getList(List reqMap) {
        //list 为全量集合
        int batchCount = 2000; //每批插入数目
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


    //批量方法
    private List<List<Map<String, Object>>> getList100(List reqMap) {
        //list 为全量集合
        int batchCount = 100; //每批插入数目
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

    @Override
    public ResultBody intiSingData(Map paramMap) {
        ResultBody<Object> resultBody = new ResultBody<>();
        try {
            /*Calendar calendar=Calendar.getInstance();
            //获取当前年份
            int year = calendar.get(Calendar.YEAR);
            //获取当前月份
            int month = calendar.get(Calendar.MONTH)+1;
            //将天数置为1
            String startTime=year+"-"+month+"-"+"01"+" 00:00:00";
            Map<Object, Object> paramMap = new HashMap<>();*/
            //paramMap.put("startTime",startTime);
            // paramMap.put("endTime", DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
            interfacedao.initializationSingingData(paramMap);

            resultBody.setCode(200);
            resultBody.setMessages("初始化成功!");
        } catch (Exception e) {
            e.printStackTrace();
            resultBody.setCode(-3434);
            resultBody.setMessages("初始化失败!");
        }

        return resultBody;

    }

    /**
     * 增量更新签约数据
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody intiSingAddData(Map map) {
        ResultBody resultBody = new ResultBody();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -1);
            Date start = c.getTime();
            String thisTime = "";
            if (map == null || map.size() == 0 || map.get("startTime") == null || map.get("startTime").equals("") || map.get("startTime").equals("null")) {
                thisTime = format.format(start);//前一天
            } else {
                thisTime = map.get("startTime") + "";
            }
            String contractSql = "SELECT roominfo,RoomGUID,bldprdid,buildingcode,buildingname,productcode,productType,projectID,KINGDEEPROJECTID,PROJECTNAME,PROJECTCODE,PROJECTFID,KINGDEEPROJECTFID,STAGENAME,STAGECODE,QSDate,YwgsDate,CloseDate,CloseReason,BldArea,CjRmbTotal,AuditDate,BcTotal,ModifiedTime,ContractGUID,BcArea,Status," +
                    " case when ScBldArea is null then YsBldArea when ScBldArea=0 then YsBldArea \n" +
                    " ELSE ScBldArea\n" +
                    " end as ScBldArea  from dotnet_erp60.dbo.VS_XSGL_CONTRACT where ModifiedTime>='" + thisTime + "'";
            List<Map<String, Object>> flowLists = jdbcTemplatemy.queryForList(contractSql);
            if (flowLists != null && flowLists.size() > 0) {
                List insertContractList = new ArrayList();
                List updateContractList = new ArrayList();
                for (Map data : flowLists) {
                    String contractGUID = data.get("ContractGUID") + "";
                    Map res = interfacedao.selectContractBycontractGUID(contractGUID);
                    if (res == null || res.size() == 0 || res.get("contractGUID") == null || res.get("contractGUID").equals("null") || res.get("contractGUID").equals("")) {
                        //需要新增的放进list
                        insertContractList.add(data);
                    } else {
                        //需要修改的放进list
                        updateContractList.add(data);
                    }
                }
                //新增
                if (insertContractList != null && insertContractList.size() > 0) {
                    interfacedao.insrtconrtact(insertContractList);
                }
                //修改
                if (updateContractList != null && updateContractList.size() > 0) {
                    List<List<Map<String, Object>>> inserlist = getList100(updateContractList);
                    for (List<Map<String, Object>> data : inserlist) {
                        interfacedao.updateConrtactByID(data);
                    }
                }
                //修改
                interfacedao.updateconrtactByStartTime(thisTime);
            }
            resultBody.setCode(200);
            resultBody.setMessages("初始化成功!");

        } catch (Exception e) {
            e.getMessage();
        }
        return resultBody;
    }


    /**
     * 增量更新认购数据
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody intiOrderAddData(Map map) {
        ResultBody resultBody = new ResultBody();
        try {
            SysLog sysLog = new SysLog();
            sysLog.setTaskName("开始更新认购表");
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sysLog.setStartTime(format1.format(new Date()));
            sysLog.setNote("开始更新认购表");
            timeLogsDao.insertLogs(sysLog);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -1);
            Date start = c.getTime();
            String thisTime = "";
            if (map == null || map.size() == 0 || map.get("startTime") == null || map.get("startTime").equals("")) {
                thisTime = format.format(start);//前一天
            } else {
                thisTime = map.get("startTime") + "";
            }
            // thisTime="2019-01-01";
            String orderSql = "SELECT * from dotnet_erp60.dbo.vs_xsgl_order where ModifiedTime>='" + thisTime + "'";
            List<Map<String, Object>> flowLists = jdbcTemplatemy.queryForList(orderSql);
            //System.err.println("共"+flowLists.size()+"条");

            if (flowLists != null && flowLists.size() > 0) {
                List insertOrderList = new ArrayList();
                List updateOrderList = new ArrayList();
                for (Map data : flowLists) {
                    String orderGuid = data.get("orderGuid") + "";
                    Map res = interfacedao.selectOrderByorderGuid(orderGuid);
                    if (res == null || res.size() == 0 || res.get("orderGuid") == null || res.get("orderGuid").equals("null") || res.get("orderGuid").equals("")) {
                        //需要新增的放进list
                        insertOrderList.add(data);
                    } else {
                        //需要修改的放进list
                        updateOrderList.add(data);
                    }
                }
                //新增
                if (insertOrderList != null && insertOrderList.size() > 0) {
                    interfacedao.insrtorder(insertOrderList);
                }
                //修改
                if (updateOrderList != null && updateOrderList.size() > 0) {
                    interfacedao.updateOrderByID(updateOrderList);
                }
                //修改组团等信息
                interfacedao.updateorderByStartTime(thisTime);
            }
          /*  List<List<Map<String, Object>>> lists = getList(flowLists);
            for (List<Map<String, Object>> maps : lists) {
                interfacedao.insrtorder(maps);
            }*/
            resultBody.setCode(200);
            resultBody.setMessages("初始化成功!");


            sysLog.setTaskName("更新认购表结束");
            sysLog.setStartTime(format1.format(new Date()));
            sysLog.setNote("认购数据同步完成！！！总条数：" + flowLists.size() + "开始时间：" + thisTime + "数据" + JSON.toJSONString(flowLists));
            timeLogsDao.insertLogs(sysLog);


        } catch (Exception e) {
            e.getMessage();
        }
        return resultBody;
    }

    @Override
    public ResultBody intiOrderAddDataSD(Map map) {
        ResultBody resultBody = new ResultBody();
        String startTime = map.get("startTime") + "";
        String endTime = map.get("endTime") + "";
        String orderSql = "SELECT * from dotnet_erp60.dbo.vs_xsgl_order where ModifiedTime>='" + startTime + "' and ModifiedTime<='" + endTime + "'";
        List<Map<String, Object>> flowLists = jdbcTemplatemy.queryForList(orderSql);
        //System.err.println("共"+flowLists.size()+"条");
        if (flowLists != null && flowLists.size() > 0) {
            System.out.println(startTime);
            System.out.println(endTime);
            interfacedao.deleteOrderByTimeSD(startTime, endTime);
            interfacedao.insrtorder(flowLists);
            //修改组团等信息
            interfacedao.updateorderByStartTimeSD(startTime, endTime);
        }
          /*  List<List<Map<String, Object>>> lists = getList(flowLists);
            for (List<Map<String, Object>> maps : lists) {
                interfacedao.insrtorder(maps);
            }*/

        return resultBody;
    }

    @Override
    public void intiOrderByProject(Map map) {
        try {
            // 获取项目id
            String projectId = map.get("projectId").toString();
            List<Map> list = interfacedao.getProjectId(projectId);
            List projectIdList = list.stream().map(obj -> obj.get("project_id")).collect(Collectors.toList());
            String projectIdListStr = CollUtil.join(projectIdList.iterator(), "','");

            // 获取上月时间
            String thisTime = DateUtil.lastMonth().toDateStr() + " 00:00:00";

            // 获取明源数据
            String orderSql = "SELECT * from dotnet_erp60.dbo.vs_xsgl_order where ModifiedTime>='" + thisTime + "' and projectid in('" + projectIdListStr + "')";
            List<Map<String, Object>> flowLists = jdbcTemplatemy.queryForList(orderSql);

            // 增量同步
            if (flowLists != null && flowLists.size() > 0) {
                List insertOrderList = new ArrayList();
                List updateOrderList = new ArrayList();
                for (Map data : flowLists) {
                    String orderGuid = data.get("orderGuid") + "";
                    Map res = interfacedao.selectOrderByorderGuid(orderGuid);
                    if (res == null || res.size() == 0 || res.get("orderGuid") == null || res.get("orderGuid").equals("null") || res.get("orderGuid").equals("")) {
                        //需要新增的放进list
                        insertOrderList.add(data);
                    } else {
                        //需要修改的放进list
                        updateOrderList.add(data);
                    }
                }
                //新增
                if (insertOrderList != null && insertOrderList.size() > 0) {
                    interfacedao.insrtorder(insertOrderList);
                }
                //修改
                if (updateOrderList != null && updateOrderList.size() > 0) {
                    interfacedao.updateOrderByID(updateOrderList);
                }
            }

            //修改组团等信息
            interfacedao.updateorderByStartTime(thisTime);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /*@Override
    public void intiOrderByProject(Map map) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 0:00:00");
            Calendar begin=Calendar.getInstance();
            begin.setTime(format.parse(format.format(new Date())));
            begin.add(Calendar.MONTH,-1);
            String thisTime = format.format(begin.getTime());
            String projectId = map.get("projectId")+"";
            List<Map> list = interfacedao.getProjectId(projectId);
            for (Map map1 : list) {
                projectId=map1.get("project_id")+"";
                String orderSql="SELECT * from dotnet_erp60.dbo.vs_xsgl_order where ModifiedTime>='"+thisTime+"' and projectid='"+projectId+"'";
                System.out.println("执行sql"+orderSql);
                List<Map<String, Object>> flowLists = jdbcTemplatemy.queryForList(orderSql);
                if(flowLists!=null && flowLists.size()>0){
                    List insertOrderList=new ArrayList();
                    List updateOrderList=new ArrayList();
                    for(Map data:flowLists){
                        String orderGuid=data.get("orderGuid")+"";
                        Map res=interfacedao.selectOrderByorderGuid(orderGuid);
                        if(res==null || res.size()==0|| res.get("orderGuid")==null ||  res.get("orderGuid").equals("null") || res.get("orderGuid").equals("") ){
                            //需要新增的放进list
                            insertOrderList.add(data);
                        }else{
                            //需要修改的放进list
                            updateOrderList.add(data);
                        }
                    }
                    //新增
                    if(insertOrderList!=null && insertOrderList.size()>0){
                        interfacedao.insrtorder(insertOrderList);
                    }
                    //修改
                    if(updateOrderList!=null && updateOrderList.size()>0) {
                        interfacedao.updateOrderByID(updateOrderList);
                    }
                }

            }
            //修改组团等信息
            interfacedao.updateorderByStartTime(thisTime);
        }catch (Exception e){
            e.getMessage();
        }
    }*/

    @Override
    public ResultBody insertVisllAdd(Map map) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        Date start = c.getTime();
        String thisTime = "";
        if (map == null || map.size() == 0 || map.get("startTime") == null || map.get("startTime").equals("")) {
            thisTime = format.format(start);//前一天
        } else {
            thisTime = map.get("startTime") + "";
        }
        String visllSql = "SELECT * FROM dotnet_erp60.dbo.VS_XSGL_VISITS WHERE GjDate>='" + thisTime + "'";
        List<Map<String, Object>> visllLists = jdbcTemplatemy.queryForList(visllSql);
        if (visllLists == null || visllLists.size() == 0 || visllLists.get(0) == null || visllLists.get(0).size() == 0) {
        } else {
            List<List<Map<String, Object>>> inserlist = getList(visllLists);
            if (inserlist != null && inserlist.size() > 0) {
                //批量插入
                interfacedao.deleteVisitsByGjDate(thisTime);
                for (List<Map<String, Object>> lists : inserlist) {
                    interfacedao.insertcstnums(lists);
                }
            }
        }
        return null;
    }

    @Override
    public ResultBody intiSingDataAll() {
        //设置时间格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat yearformat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthformat = new SimpleDateFormat("MM");
        Calendar ca = Calendar.getInstance();
        //当前年
        int nowYear = Integer.parseInt(yearformat.format(ca.getTime()));
        //当前月
        int nowMonth = Integer.parseInt(monthformat.format(ca.getTime()));
        //获得实体类
        int addYear = 2019;
        ca.set(Calendar.YEAR, addYear);
        //设置月份
        ca.set(Calendar.MONTH, 0);
        while (addYear <= nowYear) {
            //设置最后一天
            ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
            //最后一天格式化
            String lastDay = format.format(ca.getTime());
            //累计年度
            addYear = Integer.parseInt(yearformat.format(ca.getTime()));
            if (addYear > nowYear) {
                return null;
            }
            int year = ca.get(Calendar.YEAR);
            // 获取月，这里需要需要月份的范围为0~11，因此获取月份的时候需要+1才是当前月份值
            int month = ca.get(Calendar.MONTH) + 1;

            String monthAdd = "";
            if (month < 10) {
                monthAdd = "0" + month;
            } else {
                monthAdd = month + "";
            }
            Map paramMap = new HashMap();
            paramMap.put("monthStartTime", year + "-" + monthAdd + "-01 00:00:00");
            paramMap.put("monthSendTime", lastDay + " 23:59:59");
            //当前年月
            if (addYear == nowYear && month == nowMonth) {
                addYear++;
            }
            interfacedao.initializationSingingData(paramMap);
            ca.add(Calendar.MONTH, 1);
        }
        return null;
    }

    @Override
    public ResultBody intiSingDataMonth(int addYear, int nowYear) {
        //设置时间格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat yearformat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthformat = new SimpleDateFormat("MM");
        Calendar ca = Calendar.getInstance();
        //当前年
        //int nowYear = Integer.parseInt(yearformat.format(ca.getTime()));
        //当前月
        int nowMonth = Integer.parseInt(monthformat.format(ca.getTime()));
        //获得实体类
        // int addYear=2010;
        ca.set(Calendar.YEAR, addYear);
        //设置月份
        ca.set(Calendar.MONTH, 0);
        while (addYear <= nowYear) {
            //设置最后一天
            ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
            //最后一天格式化
            String lastDay = format.format(ca.getTime());
            //累计年度
            addYear = Integer.parseInt(yearformat.format(ca.getTime()));
            if (addYear > nowYear) {
                return null;
            }
            int year = ca.get(Calendar.YEAR);
            // 获取月，这里需要需要月份的范围为0~11，因此获取月份的时候需要+1才是当前月份值
            int month = ca.get(Calendar.MONTH) + 1;

            String monthAdd = "";
            if (month < 10) {
                monthAdd = "0" + month;
            } else {
                monthAdd = month + "";
            }
            Map paramMap = new HashMap();
            paramMap.put("monthStartTime", year + "-" + monthAdd + "-01 00:00:00");
            paramMap.put("monthSendTime", lastDay + " 23:59:59");
            //当前年月
            if (addYear == nowYear && month == nowMonth) {
                addYear++;
            }
            interfacedao.initializationSingingData(paramMap);
            ca.add(Calendar.MONTH, 1);
        }
        return null;
    }

    @Override
    public void initTwoYearOrder() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            int time = Integer.parseInt(format.format(new Date()));
            for (int topTime = 2018; topTime <= time; topTime++) {
                //初始化当前年数据
                //认购数据
                String recordSql2 = "SELECT * FROM dotnet_erp60.dbo.VS_XSGL_ORDER \n" +
                        " WHERE( CloseDate >= '" + topTime + "-01-01'\n" +
                        " AND CloseDate <= '" + topTime + "-12-31 23:59:59') or" +
                        " ( YwgsDate >= '" + topTime + "-01-01'\n" +
                        " AND YwgsDate <= '" + topTime + "-12-31 23:59:59') ";
                List<Map<String, Object>> flowLists2 = jdbcTemplatemy.queryForList(recordSql2);
                if (flowLists2 == null || flowLists2.size() == 0 || flowLists2.get(0) == null || flowLists2.get(0).size() == 0) {
                    throw new NullPointerException();
                } else {
                    List<List<Map<String, Object>>> inserlist = getList(flowLists2);
                    if (inserlist != null && inserlist.size() > 0) {
                        //批量插入
                        interfacedao.deleteorder(topTime + "");
                        for (List<Map<String, Object>> lists : inserlist) {
                            interfacedao.insrtorder(lists);
                        }
                    }
                }
                //情况上一年map
                flowLists2.clear();
            }
            //修改group_id
            interfacedao.updateorder();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public ResultBody initMingYuanTwo() {
        ResultBody resultBody = new ResultBody();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            int time = 2017;//Integer.parseInt(format.format(new Date()));
            for (int topTime = 2012; topTime <= time; topTime++) {
                String startTime = topTime + "-01-01 00:00:00";
                String endTime = topTime + "-12-31 23:59:59";
                String recordSql1 = "SELECT roominfo,RoomGUID,bldprdid,buildingcode,buildingname,productcode,productType,projectID,KINGDEEPROJECTID,PROJECTNAME,PROJECTCODE,PROJECTFID,KINGDEEPROJECTFID,STAGENAME,STAGECODE,QSDate,YwgsDate,CloseDate,CloseReason,BldArea,CjRmbTotal,AuditDate,BcTotal,ModifiedTime,ContractGUID,BcArea,Status," +
                        " case when ScBldArea is null then YsBldArea when ScBldArea=0 then YsBldArea \n" +
                        " ELSE ScBldArea\n" +
                        " end as ScBldArea FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT \n" +
                        " WHERE ModifiedTime >= '" + startTime + "'" +
                        " AND ModifiedTime <= '" + endTime + "'";
                String recordSql2 = "SELECT * FROM dotnet_erp60.dbo.VS_XSGL_ORDER " +
                        " WHERE  ModifiedTime >= '" + startTime + "'" +
                        " AND ModifiedTime <= '" + endTime + "'";
                List<Map<String, Object>> flowLists2 = jdbcTemplatemy.queryForList(recordSql2);
                if (flowLists2 == null || flowLists2.size() == 0 || flowLists2.get(0) == null || flowLists2.get(0).size() == 0) {
                    throw new NullPointerException();
                } else {
                    List<List<Map<String, Object>>> inserlist = getList(flowLists2);
                    if (inserlist != null && inserlist.size() > 0) {
                        //删除
                        interfacedao.deleteOrderByUpdateTime(startTime, endTime);
                        for (List<Map<String, Object>> lists : inserlist) {
                            //批量插入
                            interfacedao.insrtorder(lists);
                        }
                    }
                }

                //清空数据
                flowLists2.clear();
                List<Map<String, Object>> flowLists1 = jdbcTemplatemy.queryForList(recordSql1);

                if (flowLists1 == null || flowLists1.size() == 0 || flowLists1.get(0) == null || flowLists1.get(0).size() == 0) {

                } else {
                    List<List<Map<String, Object>>> inserlist = getList(flowLists1);
                    if (inserlist != null && inserlist.size() > 0) {
                        //删除指定时间
                        interfacedao.deleteContractByUpdateDate(startTime, endTime);
                        //批量插入
                        for (List<Map<String, Object>> lists : inserlist) {
                            interfacedao.insrtconrtact(lists);
                        }
                    }
                }
            }
            //修改group_id
            interfacedao.updateconrtact();
            interfacedao.updateorder();
            resultBody.setCode(200);
            resultBody.setMessages("初始化成功!");
            return resultBody;
        } catch (Exception e) {
            e.printStackTrace();
            resultBody.setCode(-5656);
            resultBody.setMessages("初始化失败!");
            return resultBody;

        }

    }


}
