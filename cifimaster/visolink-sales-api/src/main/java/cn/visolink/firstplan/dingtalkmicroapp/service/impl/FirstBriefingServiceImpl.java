package cn.visolink.firstplan.dingtalkmicroapp.service.impl;
import cn.hutool.core.date.DateUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.dingtalkmicroapp.dao.FirstBriefingDao;
import cn.visolink.firstplan.dingtalkmicroapp.service.FirstBriefingService;
import cn.visolink.firstplan.message.dao.TemplateEnginedao;
import cn.visolink.firstplan.message.service.impl.TemplateEngineServiceImpl;
import cn.visolink.salesmanage.flow.dao.WorkflowDao;
import cn.visolink.salesmanage.flow.service.FlowService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.*;
/**
 * @author sjl
 * @Created date 2020/8/27 10:49 上午
 */
@Service
@Transactional
public class FirstBriefingServiceImpl implements FirstBriefingService {
    @Autowired
    private FirstBriefingDao firstBriefingDao;
    @Autowired
    private TemplateEngineServiceImpl templateEngineService;
    @Autowired
    private TemplateEnginedao templateEnginedao;
    @Autowired
    private FlowService flowService;
    @Autowired
    private WorkflowDao workflowDao;
    @Resource(name = "jdbcTemplatemy")
    private JdbcTemplate jdbcTemplatemy;
    @Override
    public ResultBody getFirstBriefingList(Map map, HttpServletRequest request) {
        String username = request.getHeader("username");
        if(StringUtils.isEmpty(username)){
            username="shenyl02";
        }
        String userid = request.getHeader("userid");
        if(StringUtils.isEmpty(userid)){
            userid="000000662";
        }
        if(map==null){
            map=new HashMap();
        }
        try {
            //获取当前时间
            String startTime = DateUtil.format(new Date(), "yyyy-MM-dd");
            //String endTime="2020-08-31";
            //startTime="2020-07-01";
            System.err.println(startTime);
            map.put("userName",username);
            map.put("user_id",userid);
            String  batch_id = map.get("batch_id")+"";
            StringBuilder sb=new StringBuilder();
            String projectids="";
            //如果传递了项目id，不校验权限
            //校验权限
            map.put("function_name","开盘播报");
            // List<Map> permission = firstBriefingDao.getUserFunctionPermission(map);
            List<Map> permission = firstBriefingDao.getUserFunctionPermissionOptimiza(map);

            if(permission==null||permission.size()<=0){
                return ResultBody.error(-1945,"您无权限查看开盘播报,请联系管理员授权!");
            }
            List<Map<String, String>> userProjectData = firstBriefingDao.getUserProjectData(map);
            if (userProjectData != null && userProjectData.size() > 0) {
                for (Map userProjectDatum : userProjectData) {
                    String myProjectId = userProjectDatum.get("projectid")+"";
                    if(!"".equals(myProjectId)&&!"null".equals(myProjectId)){
                        sb.append("'" + myProjectId + "'").append(",");
                    }
                    //sb.append("'" + userProjectDatum.get("projectid") + "'").append(",");
                }
            }

            String toString = sb.toString();
            System.err.println(toString);
            projectids = toString.substring(0, toString.length() - 1);
            System.err.println(projectids);
            String sql ="SELECT\n" +
                        "\tx_OpenRoomBatchGUID AS batch_id,\n" +
                        "\tx_OpenRoomBatchName AS batch_name\n" +
                        "FROM\n" +
                        "\tVS_XK_KPHZ \n" +
                        "WHERE\n" +
                        "\t x_projectid  in ("+projectids+") \n" +
                        "\tAND BgnTime >=DATEADD(DAY,-1,'"+startTime+"') AND BgnTime<DATEADD(DAY,1,'"+startTime+"')\n" +
                        "GROUP BY\n" +
                        "\tx_OpenRoomBatchGUID,\n" +
                        "\tx_OpenRoomBatchName\n";
            //
            System.err.println(sql);

            List<Map<String, Object>> forProjectList = jdbcTemplatemy.queryForList(sql);
            //开盘项目个数
            int openProjcetNum=0;
            //查询不同批次下不同业态的去化情况
                if(forProjectList!=null&&forProjectList.size()>0){
                    openProjcetNum= forProjectList.size();
                    Map<String, Object> projectMap = forProjectList.get(0);
                    batch_id=projectMap.get("batch_id")+"";
                    projectMap.put("openProjcetNum",openProjcetNum);
                }else{
                    return ResultBody.error(-1434,"未查询到开盘播报数据!");
                }
            return ResultBody.success(forProjectList);
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-1434,"查询失败，请联系管理员");
        }
    }

    @Override
    public ResultBody getFirstBriefingInfo(Map map) {
        try {
            String batch_id=map.get("batch_id")+"";
            System.err.println("查询批次详细数据=====>");
            String startTime = DateUtil.format(new Date(), "yyyy-MM-dd");
            startTime="2020-08-14";
           String  sql="SELECT\n" +
                   "\tSUM(roomNum) roomNum,\n"+
                   "\tProjName AS project_name,\n" +
                    "\tx_OpenRoomBatchGUID AS batch_id,\n" +
                    "\tx_OpenRoomBatchName AS batch_name,\n" +
                    "\tCONVERT ( VARCHAR ( 100 ), BgnTime, 23 ) AS open_time \n" +
                    "FROM\n" +
                    "\tVS_XK_KPHZ \n" +
                    "WHERE\n" +
                    "\tx_OpenRoomBatchGUID =  '"+batch_id+"'\n" +
/*
                    "\tAND BgnTime >='"+startTime+"' AND BgnTime<=DATEADD(DAY,1,'"+startTime+"')\n" +
*/
                    "GROUP BY\n" +
                    "\tx_OpenRoomBatchGUID,\n" +
                    "\tx_OpenRoomBatchName,\n" +
                    "\tBgnTime,\n" +
                    "\tProjName;\n";
            List<Map<String, Object>> forProjectList = jdbcTemplatemy.queryForList(sql);
            System.out.println(sql);
            if(forProjectList!=null&&forProjectList.size()>0){
                Map<String, Object> projectMap = forProjectList.get(0);
                String sqlInfo="SELECT \n" +
                        "ISNULL(mx.pk_num, 0) as  pk_num,\n" +
                        "ISNULL(mx.qd_num, 0) as  qd_num,\n" +
                        "ISNULL(orde.rg_num, 0) as rg_num,\n" +
                        "ISNULL(orde.ysk_num, 0) as ysk_num\n" +
                        "FROM VS_XK_KPHZ hz\n" +
                        "LEFT JOIN\n" +
                        "(SELECT \n" +
                        "x_OpenRoomBatchGUID batchid,\n" +
                        "COUNT(*) as  pk_num,\n" +
                        "SUM(IsQd) as  qd_num\n" +
                        "FROM VS_XK_KPMX GROUP BY x_OpenRoomBatchGUID\n" +
                        ")  as mx on mx.batchid=hz.x_OpenRoomBatchGUID\n" +
                        "LEFT JOIN (\n" +
                        "SELECT \n" +
                        "x_OpenRoomBatchGUID,\n" +
                        "SUM(RG) rg_num,\n" +
                        "SUM(YJK) ysk_num\n" +
                        "FROM \n" +
                        "VS_XK_KPMXORDER GROUP BY x_OpenRoomBatchGUID\n" +
                        ") orde on orde.x_OpenRoomBatchGUID=hz.x_OpenRoomBatchGUID\n" +
                        "WHERE hz.x_OpenRoomBatchGUID='"+batch_id+"'\n" +
                        "GROUP BY mx.pk_num,mx.qd_num,orde.rg_num,orde.ysk_num";

                Map<String, Object> queryForMap = jdbcTemplatemy.queryForMap(sqlInfo);
                if(queryForMap!=null&&queryForMap.size()>0){
                    projectMap.putAll(queryForMap);
                }
                //计算
                countNumber(projectMap);
                 String ytSql="SELECT \n" +
                         "Name,\n" +
                         "\tSUM( roomnum ) AS roomNum,\n" +
                         "\tISNULL( SUM( rg_num ), 0 ) AS rg_num \n" +
                         "FROM\n" +
                         "\t(\n" +
                         "\tSELECT NAME\n" +
                         "\t\t,\n" +
                         "\t\tSUM( roomnum ) roomnum,\n" +
                         "\t\t0 AS rg_num \n" +
                         "\tFROM\n" +
                         "\t\tVS_XK_KPHZ \n" +
                         "\tWHERE\n" +
                         "\t\tx_OpenRoomBatchGUID = '"+batch_id+"' \n" +
                         "\tGROUP BY\n" +
                         "\tNAME UNION\n" +
                         "\tSELECT NAME\n" +
                         "\t\t,\n" +
                         "\t\t0 AS roomNum,\n" +
                         "\t\tSUM( RG ) rg_num \n" +
                         "\tFROM\n" +
                         "\t\tVS_XK_KPMXORDER \n" + "\tWHERE\n" +
                         "\t\tx_OpenRoomBatchGUID ='"+batch_id+"' \n" +
                         "\t\tAND  NAME IS NOT NULL\n"+
                         "\tGROUP BY\n" +
                         "\tNAME \n" +
                         "\t) sb \n" +
                         " GROUP by name";
                List<Map<String, Object>> ytList = jdbcTemplatemy.queryForList(ytSql);
                if(ytList!=null&&ytList.size()>0){
                    for (Map<String, Object> stringObjectMap : ytList) {
                        countNumber(stringObjectMap);
                    }
                }
                projectMap.put("ytInfoData",ytList);
                return ResultBody.success(projectMap);
            }

        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-1995,"查询失败!");
        }
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public ResultBody firstBriefingMessage() {
        String nowDate=DateUtil.format(new Date(),"yyyy-MM-dd");
        String sql="SELECT\n" +
                "x_projectID,\n"+
                "\txmProjName,\n" +
                "\tSUM(roomNum) roomNum,\n" +
                "\tCONVERT(varchar(100), BgnTime, 23) open_time\n" +
                "FROM\n" +
                "\tVS_XK_KPHZ \n" +
                "WHERE\n" +
                "\t CONVERT(varchar(100), BgnTime, 23) ='"+nowDate+"'\n" +
                "GROUP BY\n" +
                "\txmProjName," +
                "x_projectID," +
                "BgnTime";
        //查询消息模版
        //使用新版模版
        List<Map<String, Object>> projectList = jdbcTemplatemy.queryForList(sql);
        if(projectList!=null&&projectList.size()>0){
            for (Map<String, Object> projectMap : projectList) {
                Object projectID = projectMap.get("x_projectID");
                //生成消息
                templateEngineService.createMessageCommon("开盘播报",projectMap,projectID+"");
            }
        }
        return ResultBody.success(null);
    }

    /**
     * 定时流程比对推送
     */
    @Override
    public void flowComparisonPush() {
        //查询最近半个小时发起的流程
        List<Map> flowNewData = workflowDao.getFlowNewData();
        if(flowNewData!=null&&flowNewData.size()>0){
            for (Map flowNewDatum : flowNewData) {
                //查询OA最新推送的状态
                Map apushNewApplay = workflowDao.getOApushNewApplay(flowNewDatum);
                if(apushNewApplay!=null&&apushNewApplay.size()>0){
                    String jsonStr = String.valueOf(apushNewApplay.get("jsonStr"));
                    Map map = JSON.parseObject(jsonStr, Map.class);
                    String eventType = String.valueOf(map.get("eventType"));
                    //查询最近一次的下游系统推送
                    Map systemNew = workflowDao.getPushDownstreamSystemNew(flowNewDatum);
                    if(systemNew!=null){
                        Map systemMap = JSON.parseObject(String.valueOf(systemNew.get("param")), Map.class);
                        String statusType = String.valueOf(systemMap.get("eventType"));
                        if(!"".equals(statusType)&&!"null".equals(statusType)){
                            if(!eventType.equals(statusType)){
                                //重新推送
                                flowService.tOAcallback(map);
                            }
                        }
                    }else{
                        //重新推送
                        flowService.tOAcallback(map);
                    }
                }
            }
        }
    }

    public void countNumber(Map map){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingUsed(false);
       String qdNum=map.get("qd_num")+"";
        int qd_num=0;
        if(!"".equals(qdNum)&&!"null".equals(qdNum)){
            qd_num= Integer.parseInt(qdNum);
        }

        String pknum=map.get("pk_num")+"";
        int pk_num=0;
        if(!"".equals(pknum)&&!"null".equals(pknum)){
            pk_num= Integer.parseInt(pknum);
        }
        if(pk_num!=0){
            map.put("qd_per",df.format(((float)qd_num/pk_num)*100)+"%");
        }else{
            map.put("qd_per","0%");
        }
        String rgnum=map.get("rg_num")+"";
        int rg_num=0;
        if(!"".equals(rgnum)&&!"null".equals(rgnum)){
            rg_num= Integer.parseInt(rgnum);
        }
        String roomNum=map.get("roomNum")+"";
        int room_Num=0;
        if(!"".equals(roomNum)&&!"null".equals(roomNum)){
            room_Num= Integer.parseInt(roomNum);
        }
        if(room_Num!=0){
            map.put("qh_per",df.format(((float)rg_num/room_Num)*100)+"%");
        }else{
            map.put("qh_per","0%");
        }

        String ysknum=map.get("ysk_num")+"";
        int ysk_num=0;
        if(!"".equals(ysknum)&&!"null".equals(ysknum)){
            ysk_num= Integer.parseInt(ysknum);
        }
        map.put("wsk_num",rg_num-ysk_num);

        if(rg_num!=0){
            map.put("sk_per",df.format(((float)ysk_num/rg_num)*100)+"%");
        }else{
            map.put("sk_per","0%");
        }

    }
}
