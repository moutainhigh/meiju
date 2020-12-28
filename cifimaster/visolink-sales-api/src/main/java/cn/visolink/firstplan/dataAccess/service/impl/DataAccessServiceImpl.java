package cn.visolink.firstplan.dataAccess.service.impl;

import cn.visolink.common.security.utils.HttpRequestUtil;
import cn.visolink.firstplan.dataAccess.dao.DataAccessDao;
import cn.visolink.firstplan.dataAccess.service.DataAccessService;
import cn.visolink.system.timelogs.bean.SysLog;
import cn.visolink.system.timelogs.dao.TimeLogsDao;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author
 * @date 2019-9-20
 */
@Service
public class DataAccessServiceImpl implements DataAccessService {
    @Autowired
    private DataAccessDao dataAccessDao;

    @Autowired
    private TimeLogsDao timeLogsDao;

    //钉钉发送消息地址
    @Value("${DingDing.httpIp}")
    private String httpIp;

    @Override
    public int insertPanoramaProject(Map params) {
        int res=0;
        //当前时间参数
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SysLog sysLog1 = new SysLog();
        sysLog1.setStartTime(sf.format(new Date()));
        sysLog1.setTaskName("实时同步全景计划新增数据");
        sysLog1.setNote(JSON.toJSONString(params));
        sysLog1.setResultStatus(3);
        timeLogsDao.insertLogs(sysLog1);
        Map requestInfo=(Map)params.get("requestInfo");
        Map panoramicPlan=(Map)requestInfo.get("panoramicPlan");
        List panoramicPlanInfoLine=(List)panoramicPlan.get("panoramicPlanInfoLine");
        Map info=(Map)panoramicPlanInfoLine.get(0);
        //节点信息
        List<Map> nodeDetail=(List)info.get("nodeDetail");
        String open_time=null;
        if(nodeDetail!=null && nodeDetail.size()>0){
            for(Map data :nodeDetail){
                //JT-YJ-00002 "取得《预售许可证》
                if(data.get("nodeCode").equals("JT-YJ-00002")){
                    if(data.get("actualFinishDate")!=null){
                        open_time=data.get("actualFinishDate").toString().substring(0,10);
                    }
                    break;
                }
            }
        }
        String projectID=info.get("projectID")+"";
        if(projectID==null || projectID=="" || projectID.equals("null")){
            res=1;
        }else{
            //通过版本ID查询
            Map reslt=dataAccessDao.getPanoramaProjectById(info);
            //开盘时间
            info.put("open_time",open_time);
            //时间戳转时间
            long takeTime = new Long(info.get("takeTime")+"");
            Date date = new Date(takeTime);
            String resDate = sf.format(date);
            info.put("takeTime",resDate);
            if(reslt==null || reslt.size()==0 || reslt.isEmpty()){
                //数据库不存在新增到库里
                dataAccessDao.insertPanoramaProject(info);
            }else{
                //数据库存在修改这条数据
                dataAccessDao.updatePanoramaProject(info);
            }
        }
        return res;
    }

    @Override
    public int insertReport(List list) {
        return dataAccessDao.insertReport(list);
    }

    @Override
    public int updateReport(List list) {
        return dataAccessDao.updateReport(list);
    }

    @Override
    public int insertCard(List list) {
        return dataAccessDao.insertCard(list);
    }

    @Override
    public int delGuestStorage(Map params) {
        return dataAccessDao.delGuestStorageByDate(params);
    }
    @Override
    public int delGuestStorageByProject(Map params) {
        return dataAccessDao.delGuestStorageByProject(params);
    }
    @Override
    public int delGuestStorageAll() {
        return dataAccessDao.delGuestStorageAll();
    }

    @Override
    public Map sendNodeReport(Map parmas) {
        //获取下个发送节点所属项目岗位
        List<Map> sedProject=dataAccessDao.selectPlanNodeSendNode();
        if(sedProject!=null && sedProject.size()>0){
            StringBuffer inserLog=new StringBuffer();
            //发送钉钉消息
            for(Map data:sedProject){
                if(data.get("jobName")!=null && data.get("project_id")!=null){
                    List<Map> user=dataAccessDao.sendUserName(data);
                    for(Map send:user){
                        String mobile=send.get("mobile")+"";
                        String project_name=data.get("project_name")+"";
                        String node_name=data.get("node_name")+"";
                        String type=data.get("type")+"";
                        String node_level=data.get("node_level")+"";
                        if(node_level.equals("8")){
                            if(type.equals("1")){
                                node_name="开盘当日";
                            }else{
                                node_name="开盘次日";
                            }
                        }
                        String plan_start_time=data.get("plan_start_time")+"";
                        String title=project_name+"项目"+node_name+"编制提醒";
                        String text="首开填报提醒";
                        String day=data.get("warning_day")+"";
                        String plan_end_time=data.get("plan_end_time")+"";
                        String open_time=data.get("open_time")+"";
                       if(node_level.equals("2")){
                           text="拿地后15天需完成顶设1过会和系统填报及审批，距离顶设1系统完成系统审批还有"+day+"天即将逾期，请尽快登录首开计划管理完成顶设1的编制并发起审批，"+plan_end_time+"前未完成审批，系统记为逾期状态。";
                       }else if(node_level.equals("3")){
                           text="依据顶设1提报的首开时间"+open_time+"，首开前4个月需完成顶设2过会和系统填报及审批，距离顶设2完成系统审批还有"+day+"天即将逾期，请尽快登录首开计划管理完成顶设2的编制并发起审批，"+plan_end_time+"前未完成审批，系统记为逾期状态。";
                       }else if(node_level.equals("4")){
                           text="依据顶设2提报的首开时间"+open_time+"，首开前3个月需完成首开前3月的过会和系统填报及审批，距离首开前3月完成系统审批还有"+day+"天即将逾期，请尽快登录首开计划管理完成首开前3月的编制并发起审批，"+plan_end_time+"前未完成审批，系统记为逾期状态。";
                       }else if(node_level.equals("5")){
                           text="依据顶设2提报的首开时间"+open_time+"，首开前2个月需完成首开前2月的过会和系统填报及审批，距离首开前2月提报还有"+day+"天即将逾期，请尽快登录首开计划管理完成首开前2月的编制并发起审批，"+plan_end_time+"前未完成审批，系统记为逾期状态。";
                       }else if(node_level.equals("6")){
                           text="依据顶设2提报的首开时间"+open_time+"，首开前21天需完成首开前21天的过会和系统填报及审批审批通过，距离首开前21天提报还有"+day+"天即将逾期，请尽快登录首开计划管理完成首开前21天的编制并发起审批，"+plan_end_time+"前未完成审批，系统记为逾期状态。";
                       }else if(node_level.equals("7")){
                           text="依据顶设2提报的首开时间"+open_time+"，首开前7天需完成首开前7天的过会和系统填报及审批审批，距离首开前7天提报还有"+day+"天即将逾期，请尽快登录首开计划管理完成首开前7天的编制并发起审批，"+plan_end_time+"前未完成审批，系统记为逾期状态。";
                       }else if(node_level.equals("8")){
                           text=open_time+"首开开盘简报数据已生成，请尽快登录首开计划管理完成首开编制并发起审批，"+plan_end_time+" 06:00前未完成审批，系统记为逾期状态。";
                       }else if(node_level.equals("9")){
                           text=project_name+"项目于"+open_time+"首开，距离首开后1月提报还有"+day+"天即将逾期，请尽快登录首开计划管理完成首开后1月的编制并发起审批，"+plan_end_time+"前未完成审批，系统记为逾期状态。";
                       }else if(node_level.equals("10")){
                           title=project_name+"项目"+node_name+"资料上传提醒";
                           text="依据顶设2提报的售楼处开放计划时间"+plan_end_time+"，距离售楼处开放资料提交还有"+day+"天即将逾期，请尽快登录首开计划管理完成售楼处开放的资料上传，"+plan_end_time+"前未完成审批，系统记为逾期状态。";
                       }else if(node_level.equals("11")){
                           title=project_name+"项目"+node_name+"资料上传提醒";
                           text="依据顶设2景观样板房开放计划时间"+plan_end_time+"，距离景观样板房开放资料提交还有"+day+"天即将逾期，请尽快登录首开计划管理完成景观样板房开放的资料上传，"+plan_end_time+"前未完成审批，系统记为逾期状态。";
                       }else if(node_level.equals("12")){
                           title=project_name+"项目"+node_name+"资料上传提醒";
                           text="依据顶设2样板房开放计划时间"+plan_end_time+"，距离景观样板房开放资料提交还有"+day+"天即将逾期，请尽快登录首开计划管理完成样板房开放的资料上传，"+plan_end_time+"前未完成审批，系统记为逾期状态。";
                       }
                        String content="destination="+mobile+"&variables="+title+" \n  >"+text;
                        int dingStatus = dataAccessDao.getDingPushStatus();
                        if (dingStatus==1){
                            HttpRequestUtil.doPost(httpIp,content);  //发送数据
                        }
                        inserLog.append("手机:").append(mobile).append("项目:").append(project_name).append("节点:").append(node_name).append("计划:").append(plan_start_time).append("->");
                    }
                    dataAccessDao.updatePlanNodeSendStatusById(data.get("plan_node_id")+"");
                }
            }
            Map logParams=new HashMap();
            logParams.put("TaskName","推送钉钉消息");
            logParams.put("content",inserLog.toString());
            timeLogsDao.insertLog(logParams);
        }
        return null;
    }

    /**
     * 两个提醒
     * @param parmas
     * @return
     */
    @Override
    public Map sendNodeWarn(Map parmas) {
        return null;
    }

    @Override
    public List<Map> queryLsTable(Map map) {
        return dataAccessDao.comcomguest(map);
    }
}
