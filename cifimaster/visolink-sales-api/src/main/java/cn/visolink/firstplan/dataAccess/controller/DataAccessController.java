package cn.visolink.firstplan.dataAccess.controller;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.utils.HttpRequestUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.dataAccess.service.DataAccessService;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.datainterface.dao.DatainterfaceDao;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * <p>
 * 数据接口
 * </p>
 * @author
 * @since
 */
@RestController
@Api(tags = "首开计划数据接口")
@Slf4j
@RequestMapping("/dataAccess")
public class DataAccessController {

    @Autowired
    private DataAccessService dataAccessService;

    @Resource(name = "jdbcTemplateXUKE")
    private JdbcTemplate jdbcXuke;

    @Resource(name = "jdbcTemplatemy")
    private JdbcTemplate jdbcTemplatemy;

    @Autowired
    private DatainterfaceDao interfacedao;

    //钉钉发送消息地址
    @Value("${DingDing.httpIp}")
    private String httpIp;

    @Autowired
    private RedisUtil redisUtil;
    private static String CACHE_LOCK1= "MESSAGE_PUSH_LOCK7";
    private static String CACHE_LOCK2 = "MESSAGE_PUSH_LOCK8";
    private static String CACHE_LOCK3 = "MESSAGE_PUSH_LOCK9";
    private static String CACHE_LOCK4 = "MESSAGE_PUSH_LOCK10";
    private static String CACHE_LOCK5 = "MESSAGE_PUSH_LOCK11";
    private static int EXPIRE_PERIOD = (int) DateUtils.MILLIS_PER_MINUTE * 5 / 1000;

    @Log("更新fp_comm_panorama_project数据全景计划")
    @ApiOperation(value = "更新fp_comm_panorama_project数据")
    @PostMapping(value = "/insertPanoramaProject")
    public Map insertPanoramaProject(@RequestBody Map params) {
        Map maplist = new HashMap<>();
        Map maplistt = new HashMap<>();
        Map esbInfo = (Map) params.get("esbInfo");
        try {
            //写入全景计划记录表
            dataAccessService.insertPanoramaProject(params);
        } catch (Exception e) {
            maplist.put("instId", esbInfo.get("instId"));
            maplist.put("returnStatus", "E");
            maplist.put("returnCode", "A0001-SMS");
            maplist.put("returnMsg", "调用失败");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            maplist.put("requstTime", df.format(new Date()));
            maplist.put("reponsTime", df.format(new Date()));
            maplist.put("attr1", null);
            maplist.put("attr2", null);
            maplist.put("attr3", null);
            maplistt.put("esbInfo", maplist);
            return maplistt;
        }
        maplist.put("instId", esbInfo.get("instId"));
        maplist.put("returnStatus", "S");
        maplist.put("returnCode", "A0001-SMS");
        maplist.put("returnMsg", "调用成功");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        maplist.put("requstTime", df.format(new Date()));
        maplist.put("reponsTime", df.format(new Date()));
        maplist.put("attr1", null);
        maplist.put("attr2", null);
        maplist.put("attr3", null);
        maplistt.put("esbInfo", maplist);
        return maplistt;
    }


    @Log("定时更新储客数据报备，来访，小卡，小卡率，大卡，大卡率，认购，成交率")
    @ApiOperation(value = "定时更新储客数据报备，来访，小卡，小卡率，大卡，大卡率，认购，成交率")
    @PostMapping(value = "/refGuestStorage")
    //@Scheduled(cron = "0 30 6 * * ?")
    public VisolinkResultBody refGuestStorage() {
        if (redisUtil.get(CACHE_LOCK1) == null) {
            //线程锁
            redisUtil.set(CACHE_LOCK1, true, EXPIRE_PERIOD);
        VisolinkResultBody res = new VisolinkResultBody();
        try {
/*            //写入储客记录表定时获取昨天到今天的报备跟来访
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -1);
            Date start = c.getTime();
            String startTime = format.format(start);
            //来访报备
            String sql = "select * from vs_yxgk_report_vist_num WHERE reportData='" + startTime + "'";
            List<Map<String, Object>> list = jdbcXuke.queryForList(sql);
            //小卡大卡认购
            String sql1 = "select * from dotnet_erp60.dbo.VS_XSGL_PKQY where cdate='" + startTime + "'";
            List<Map<String, Object>> list1 = jdbcTemplatemy.queryForList(sql1);
            if (list != null && list.size() > 0) {
                //写入销售管理系统
                dataAccessService.insertReport(list);
            }
            if (list1 != null && list1.size() > 0) {
                dataAccessService.insertCard(list1);
            }*/

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar begin=Calendar.getInstance();
            begin.setTime(format.parse(format.format(new Date())));
            begin.add(Calendar.MONTH,-8);
            String startTime = format.format(begin.getTime());
            String endTime=format.format(new Date());
            Map map =new HashMap();
            map.put("startTime",startTime);
            map.put("endTime",endTime);
            //查询数据临时表
            String sql = "select * from vs_yxgk_report_vist_num WHERE reportData>='" + startTime + "' and reportData<='" + endTime + "'";
            List<Map<String, Object>> list = jdbcXuke.queryForList(sql);
            //小卡大卡认购
            String sql1 = "select * from dotnet_erp60.dbo.VS_XSGL_PKQY where cdate>='" + startTime + "' and cdate<='" + endTime + "'";
            List<Map<String, Object>> list1 = jdbcTemplatemy.queryForList(sql1);
            //删除
            dataAccessService.delGuestStorage(map);
            //写入销售管理系统
            if (list != null && list.size() > 0) {
                //写入销售管理系统
                dataAccessService.insertReport(list);
            }
            if (list1 != null && list1.size() > 0) {
                dataAccessService.insertCard(list1);
            }
        } catch (Exception e) {
            res.setCode(1);
            res.setMessages(e.getMessage());
        }
        return res;
        } else {
            log.info("周计划其他服务正在执行此项任务!!!!!!!!!!!!!,休眠：" + EXPIRE_PERIOD);
            return null;
        }
    }

    @Log("手动调度储客数据报备，来访，小卡，小卡率，大卡，大卡率，认购，成交率")
    @ApiOperation(value = "手动调度储客数据报备，来访，小卡，小卡率，大卡，大卡率，认购，成交率")
    @PostMapping(value = "/insertGuestStorage")
    public VisolinkResultBody insertGuestStorage(@RequestBody Map params) {
        VisolinkResultBody res = new VisolinkResultBody();
        try {
            //写入储客记录表定时获取昨天到今天的报备跟来访
            String startTime = params.get("startTime") + "";
            String endTime = params.get("endTime") + "";
        //    List<Map> table = dataAccessService.queryLsTable(params);
            //查询数据临时表
            //String sql = "select * from vs_yxgk_report_vist_num WHERE reportData>='" + startTime + "' and reportData<='" + endTime + "'";
            String sql = "select * from vs_yxgk_report_vist_num WHERE reportData>='" + startTime + "' and reportData<='" + endTime + "'";
            List<Map<String, Object>> list = jdbcXuke.queryForList(sql);
            //小卡大卡认购
            String sql1 = "select * from dotnet_erp60.dbo.VS_XSGL_PKQY where cdate>='" + startTime + "' and cdate<='" + endTime + "'";
            List<Map<String, Object>> list1 = jdbcTemplatemy.queryForList(sql1);
            //删除
            dataAccessService.delGuestStorage(params);
            //写入销售管理系统
            if (list != null && list.size() > 0) {
                //写入销售管理系统
                dataAccessService.insertReport(list);
            }
            if (list1 != null && list1.size() > 0) {
                dataAccessService.insertCard(list1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    @Log("手动调度储客数据报备，来访，小卡，小卡率，大卡，大卡率，认购，成交率")
    @ApiOperation(value = "手动调度储客数据报备，来访，小卡，小卡率，大卡，大卡率，认购，成交率")
    @PostMapping(value = "/insertGuestStorageByProject")
    public ResultBody insertGuestStorageByProject(@RequestBody Map params) {
        try {
            //写入储客记录表定时获取昨天到今天的报备跟来访
          //  String startTime = params.get("startTime") + "";
            String projectId = params.get("projectId") + "";
            List<Map> listProject = interfacedao.getProjectId(projectId);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar begin=Calendar.getInstance();
            begin.setTime(format.parse(format.format(new Date())));
            begin.add(Calendar.MONTH,-8);
            String xkStartTime = format.format(begin.getTime());
            //    List<Map> table = dataAccessService.queryLsTable(params);
            //查询数据临时表
            for (Map map : listProject) {
                projectId=map.get("project_id")+"";
                //String sql = "select * from vs_yxgk_report_vist_num WHERE reportData>='' and HKBProjectID='" + projectId + "'";
 /*               String sql = "SELECT\n" +
                        "\tp.KindeeProjectID AS KindeeProjectID,\n" +
                        "\tp.HKBProjectID AS HKBProjectID,\n" +
                        "\tdate_format( c.ReportCreateTime, '%Y-%m-%d' ) AS reportData,\n" +
                        "\tsum( 1 ) AS reportCnt,\n" +
                        "\tsum(\n" +
                        "\t( CASE WHEN ( ( c.ClueStatus > 1 ) AND ( c.ClueStatus <> 9 ) ) THEN 1 ELSE 0 END ) \n" +
                        "\t) AS visitCnt \n" +
                        "FROM\n" +
                        "\t\n" +
                        "\tvs_yxgk_clues c\n" +
                        "\tJOIN vs_yxgk_project p ON  p.ID = c.projectId AND  p.IsStages = 0 \n" +
                        "\t\n" +
                        "WHERE\n" +
                        "\t c.IsRepurchase = 0  AND c.ReportCreateTime >= '" + xkStartTime + "'   and p.HKBProjectID='" + projectId + "'\n" +
                        "GROUP BY\n" +
                        "\tdate_format( c.ReportCreateTime, '%Y-%m-%d' )\n";*/
 String sql="SELECT\n" +
         "\t`a`.`KindeeProjectID` AS `KindeeProjectID`,\n" +
         "\t`a`.`HKBProjectID` AS `HKBProjectID`,\n" +
         "\t`a`.`reportData` AS `reportData`,\n" +
         "\t`a`.`reportCnt` AS `reportCnt`,\n" +
         "\t`b`.`visitCnt` AS `visitCnt` \n" +
         "FROM\n" +
         "\t(\n" +
         "\t(\n" +
         "\t(\n" +
         "SELECT\n" +
         "\t`c`.`projectId` AS `projectId`,\n" +
         "\t`p`.`KindeeProjectID` AS `KindeeProjectID`,\n" +
         "\t`p`.`HKBProjectID` AS `HKBProjectID`,\n" +
         "\tdate_format( `c`.`ReportCreateTime`, '%Y-%m-%d' ) AS `reportData`,\n" +
         "\tsum( 1 ) AS `reportCnt` \n" +
         "FROM\n" +
         "\t(\n" +
         "\t`vs_yxgk_clues` `c`\n" +
         "\tJOIN `vs_yxgk_project` `p` ON ( ( ( `p`.`ID` = `c`.`projectId` ) AND ( `p`.`IsStages` = 0 ) and p.HKBProjectID='" + projectId + "')  ) \n" +
         "\t) \n" +
         "WHERE\n" +
         "\t( ( `c`.`IsRepurchase` = 0 ) AND ( `c`.`ReportCreateTime` >= '" + xkStartTime + "'    ) ) \n" +
         "GROUP BY\n" +
         "\t`c`.`projectId`,\n" +
         "\tdate_format( `c`.`ReportCreateTime`, '%Y-%m-%d' ) \n" +
         "\t) \n" +
         "\t) `a`\n" +
         "\tJOIN (\n" +
         "SELECT\n" +
         "\t`c`.`projectId` AS `projectId`,\n" +
         "\t`p`.`KindeeProjectID` AS `KindeeProjectID`,\n" +
         "\t`p`.`HKBProjectID` AS `HKBProjectID`,\n" +
         "\tdate_format( `c`.`TheFirstVisitDate`, '%Y-%m-%d' ) AS `visitData`,\n" +
         "\tsum( 1 ) AS `visitCnt` \n" +
         "FROM\n" +
         "\t(\n" +
         "\t`vs_yxgk_opportunity` `c`\n" +
         "\tJOIN `xuke`.`b_project` `p` ON ( ( ( `p`.`ID` = `c`.`projectId` ) AND ( `p`.`IsStages` = 0 ) and p.HKBProjectID='" + projectId + "')) \n" +
         "\t) \n" +
         "WHERE\n" +
         "\t(\n" +
         "\t( `c`.`IsRepurchase` = 0 ) \n" +
         "\tAND ( `c`.`TheFirstVisitDate` >= '" + xkStartTime + "' ) \n" +
         "\tAND ( `c`.`ClueStatus` > 1 ) \n" +
         "\tAND ( `c`.`ClueStatus` <> 9 ) \n" +
         "\t) \n" +
         "GROUP BY\n" +
         "\t`c`.`projectId`,\n" +
         "\tdate_format( `c`.`TheFirstVisitDate`, '%Y-%m-%d' ) \n" +
         "\t) `b` ON ( ( ( `a`.`projectId` = `b`.`projectId` ) AND ( `a`.`reportData` = `b`.`visitData` ) ) ) \n" +
         "\t)";
                System.out.println("同步旭客数据开始");
                System.out.println("执行sql"+sql);
                List<Map<String, Object>> list = jdbcXuke.queryForList(sql);
                System.out.println("数据集合长度"+list.size());

                //小卡大卡认购
                String sql1 = "select * from dotnet_erp60.dbo.VS_XSGL_PKQY where cdate>='" + xkStartTime + "' and projectId='" + projectId + "'";
                System.out.println("同步明源数据开始");
                System.out.println("执行sql"+sql1);
                List<Map<String, Object>> list1 = jdbcTemplatemy.queryForList(sql1);
                System.out.println("数据集合长度"+list1.size());
                //删除
                params.put("startTime",xkStartTime);
                params.put("projectId",projectId);
                dataAccessService.delGuestStorageByProject(params);
                //写入销售管理系统
                if (list != null && list.size() > 0) {
                    //写入销售管理系统
                    dataAccessService.insertReport(list);
                }
                if (list1 != null && list1.size() > 0) {
                    dataAccessService.insertCard(list1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultBody.success("同步客储成功！");
    }


    @Log("12大节点发送钉钉通知填报")
    @ApiOperation(value = "12大节点发送钉钉通知填报")
    @PostMapping(value = "/sendNodeReport")
    //@Scheduled(cron = "0 0 9 * * ?")
    public VisolinkResultBody sendNodeReport() {
        if (redisUtil.get(CACHE_LOCK2) == null) {
            //线程锁
            redisUtil.set(CACHE_LOCK2, true, EXPIRE_PERIOD);
            VisolinkResultBody res = new VisolinkResultBody();
            try {
                dataAccessService.sendNodeReport(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }else {
            log.info("周计划其他服务正在执行此项任务!!!!!!!!!!!!!,休眠：" + EXPIRE_PERIOD);
            return null;
        }
    }

    @Log("手动推送测试")
    @ApiOperation(value = "手动推送测试")
    @PostMapping(value = "/culSendNodeReport")
    public VisolinkResultBody culSendNodeReport(@RequestBody Map params) {
        VisolinkResultBody res = new VisolinkResultBody();
        try {
            String content="destination="+params.get("phone")+"&variables="+params.get("title")+"   \n #### "+params.get("title1")+"  \n  >"+params.get("content")+"";
            String json = HttpRequestUtil.doPost(httpIp,content);  //发送数据
            System.out.print(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  res;

    }
}




