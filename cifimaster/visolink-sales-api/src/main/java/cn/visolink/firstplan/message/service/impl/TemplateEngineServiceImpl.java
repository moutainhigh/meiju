package cn.visolink.firstplan.message.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.message.dao.MessageManagerDao;
import cn.visolink.firstplan.message.dao.TemplateEnginedao;
import cn.visolink.firstplan.message.service.MessageManagerService;
import cn.visolink.firstplan.message.service.TemplateEngineService;
import cn.visolink.firstplan.openbeforeseven.dao.OpenBeforeSevenDayDao;
import cn.visolink.firstplan.openbeforetwentyone.dao.OpenbeforetwentyoneDao;
import cn.visolink.system.timelogs.dao.TimeLogsDao;
import com.alibaba.fastjson.JSON;
import net.sf.jsqlparser.statement.select.First;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sjl
 * @Created date 2020/6/6 12:46 上午
 * 模版消息生成
 */
@Service
@Transactional
public class TemplateEngineServiceImpl implements TemplateEngineService {

    @Autowired
    private OpenBeforeSevenDayDao openBeforeSevenDayDao;
    @Autowired
    private OpenbeforetwentyoneDao openbeforetwentyoneDao;
    @Autowired
    private TemplateEnginedao templateEnginedao;
    @Autowired
    private TimeLogsDao timeLogsDao;
    @Autowired
    private MessageManagerService messageManagerService;
    @Autowired
    private MessageManagerDao messageManagerDao;
    @Value("${firstOpenSystemUrl}")
    private  String firstOpenSystemUrl ;
    /**
     * 首开计划节点消息生成
     */
    @Override
    public ResultBody firstPlanMessage(Map map) {
        //存放日志
        Map logParams = new HashMap();
        try {
            String node_level = map.get("node_level") + "";
            String json_id = map.get("businesskey") + "";
            String flow_code = map.get("flowKey") + "";
            String plan_id = null;
            Map<String, Object> dataMap = new HashMap<>();
            Map<Object, Object> nodeMap = new HashMap<>();
            String project_id = null;

            //开盘前3月and开盘前2个月and开盘前21天客储数据
            if ("fp_open_two".equals(flow_code) || "fp_open_three".equals(flow_code) || "fp_open_twentyone_node".equals(flow_code)) {
                //查询计划id
                if ("4".equals(node_level)) {
                    map.put("template_name", "首开前3月完成");
                } else if (node_level.equals("5")) {
                    map.put("template_name", "首开前2月完成");
                } else if (node_level.equals("6")) {
                    map.put("template_name", "首开前21天完成");
                }
                nodeMap = templateEnginedao.getPlanDataBeforeMonth(json_id);
                //开盘前21天延期开盘and开盘前7天延期开盘
            } else if ("fp_open_twentyone_off".equals(flow_code) || "fp_open_seven_off".equals(flow_code)) {
                //查询计划id
                map.put("template_name", "延期开盘");
                nodeMap = templateEnginedao.getDelayOpenApplayPlanid(json_id);
            } else if ("fp_open_seven_approve".equals(flow_code)) {
                map.put("template_name", "首开前7天完成");
                nodeMap = templateEnginedao.getSevenOpenApplayPlanid(json_id);
            }
            if (nodeMap != null && nodeMap.size() > 0) {
                String plan_node_id = nodeMap.get("plan_node_id") + "";
                plan_id = nodeMap.get("plan_id") + "";
                //查询节点详细信息
                Map planNodeInfo = templateEnginedao.getPlanNodeInfo(plan_node_id);
                if (planNodeInfo == null) {
                    planNodeInfo = new HashMap();
                }
                //查询计划详细信息
                Map planInfo = templateEnginedao.getPlanInfo(plan_id);

                if (planInfo == null) {
                    planInfo = new HashMap();
                }
                //获取计划所属的项目id
                project_id = planInfo.get("project_id") + "";
                //将节点详细信息和计划详细信息合并为变量集
                dataMap.putAll(planNodeInfo);
                dataMap.putAll(planInfo);

                //查询延期开盘数据
                Map openApplyData = openbeforetwentyoneDao.selectExtensionOpenApplyData(json_id);
                //查询开盘数据
                Map openData = templateEnginedao.getOpenData(plan_id);
                if (openData != null && openData.size() > 0) {
                    String open_countdown = openData.get("open_countdown") + "";
                    if (!"null".equals(open_countdown) && !"".equals(open_countdown)) {
                        int parseInt = Integer.parseInt(open_countdown);
                        if (parseInt < 0) {
                            openData.put("open_countdown", "已逾期" + -parseInt);
                        }
                    }
                    dataMap.putAll(openData);
                }

                if (openApplyData != null && openApplyData.size() > 0) {
                    //放入数据渲染数据集合，作为储备数据
                    dataMap.putAll(openApplyData);
                }
                Map<Object, Object> paramMap = new HashMap<>();
                paramMap.put("plan_id", plan_id);
                //三大件信息(样板房、样板段、售楼处)
                Map threeDataView = threeDataView(paramMap);
                if (threeDataView != null) {
                    dataMap.putAll(threeDataView);
                }
                paramMap.put("node_level", node_level);
                /**
                 *  客储偏差情况
                 */
                 //客储偏差情况-计划数据
                Map planData = templateEnginedao.getPlanData(paramMap);
                //客储偏差情况-实际数据
                Map actualData = templateEnginedao.getActualData(paramMap);
                //计算偏差率
                Map perMap = countPer(planData, actualData);
                if (planData != null) {
                    dataMap.putAll(planData);
                }
                if (actualData != null) {
                    dataMap.putAll(actualData);
                }
                if (perMap != null) {
                    dataMap.putAll(perMap);
                }

                dataMap.put("plan_id", paramMap.get("plan_id") + "");
                //获取首开整体客储偏差情况
                getOpenZtPcInfo(dataMap);
                //查询首开前7天数据
                Map openApplayMainData = openBeforeSevenDayDao.getOpenApplayMainData(json_id);
                if (openApplayMainData != null && openApplayMainData.size() > 0) {
                    //放入数据渲染数据集合，作为储备数据
                    dataMap.putAll(openApplayMainData);
                }
            }
            //查询对应的模版
            Map templateInfo = templateEnginedao.getTemplateInfo(map);
            if (templateInfo != null) {
                //获取模版标题
                String template_title = templateInfo.get("template_title") + "";
                //获取模版内容
                String template_info = templateInfo.get("template_info") + "";
                //替换模版变量，返回替换后的模版信息
                Map resultMap = replaceData(dataMap, template_title, template_info);
                if (resultMap != null) {
                    //生成消息
                    messageGeneration(resultMap, templateInfo, project_id);
                }

            }
        } catch (Exception e) {
            logParams.put("TaskName", "消息生成失败");
            logParams.put("content", "失败信息:" + e.toString() + "失败原因:" + e.toString());
            e.printStackTrace();
            return ResultBody.error(-1006, "消息生成失败!");
        } finally {
            //无论执行成功或者失败，都添加日志记录
            timeLogsDao.insertLog(logParams);
        }

        return ResultBody.success(null);
    }

    /**
     * 首开计划三大件延期提醒
     */
    @Override
    public ResultBody threepiecesRemind(Map map) {
        //查询当前所有上线首开项目的三大件，逾期五天未提报的
        try {
            String plan_id = "";
            if (map == null) {
                plan_id = null;
            } else {
                plan_id = map.get("plan_id") + "";
                if ("".equals(plan_id) || "null".equals(plan_id)) {
                    plan_id = null;
                }
            }

            Map<Object, Object> paramMap = new HashMap<>();
            paramMap.put("node_level",3);
            List<Map> twoApplayApproved = templateEnginedao.getDesignTwoApplayApproved(paramMap);
            if(twoApplayApproved!=null&&twoApplayApproved.size()>0){
                for (Map map1 : twoApplayApproved) {
                    //查询三大件延期提醒的消息模版
                    List<Map> threePiecesData = templateEnginedao.selectThreePiecesData(map1.get("plan_id")+"");
                    paramMap.put("template_name", "三大件延期提醒");
                    Map templateInfo = templateEnginedao.getTemplateInfo(paramMap);
                    if (templateInfo != null) {
                        //获取对应消息模版的消息标题
                        String template_title = templateInfo.get("template_title") + "";
                        //获取对应消息模版的消息内容
                        String template_info = templateInfo.get("template_info") + "";
                        //获取三大件延期
                        if (threePiecesData != null && threePiecesData.size() > 0) {
                            for (Map threePiecesDatum : threePiecesData) {
                                Map threeDataView = threeDataView(threePiecesDatum);
                                threePiecesDatum.putAll(threeDataView);
                                //获取延期开盘数据
                                getOpenInfo(threePiecesDatum);
                                String project_id = threePiecesDatum.get("project_id") + "";
                                //返回替换后的数据集
                                Map resultMap = replaceData(threePiecesDatum, template_title, template_info);
                                //生成消息
                                messageGeneration(resultMap, templateInfo, project_id);
                            }
                        } else {
                            return ResultBody.error(-1005, "未找到计划的逾期信息");
                        }
                    }
                }
            }
           // List<Map> threePiecesData = templateEnginedao.selectThreePiecesData(plan_id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(-1005, "消息生成失败:" + e.toString());
        }
        return ResultBody.success(null);
    }

    /**
     * 首开节点偏差预警递归查询
     */
    @Override
    public ResultBody customerStorageDeviation(Map map) {
        String plan_id = "";
        if (map != null && map.size() > 0) {
            plan_id = map.get("plan_id") + "";
            if ("".equals(plan_id) || "null".equals(plan_id)) {
                plan_id = null;
            }
        } else {
            plan_id = null;
        }

        //从level=3（顶设2开始查询）
        int node_level = 3;
        //到首开前21天结束
        while (node_level < 7) {
            gengecaMessage(node_level, plan_id);
            node_level++;
        }
        return null;
    }

    /**
     * 生成首开节点偏差预警消息
     */
    public void gengecaMessage(Integer node_level, String plan_id) {
        List<Map> designTwoaedList = null;
        boolean isMeagen = true;
        try {
            System.err.println("区间======" + node_level + "=====" + Integer.parseInt(node_level + 1 + ""));
            Map<Object, Object> paramMap = new HashMap<>();
            paramMap.put("node_level", node_level);
            paramMap.put("plan_id", plan_id);
            //查询已经审批通过节点段的起始节点（顶设2、首开前3月、首开前2月、首开前21天）
            // 顶设2-首开前3月
            // 首开前3月-首开前2月
            // 首开前2月-首开前21天
            //首开前21天-首开前7天
            designTwoaedList = templateEnginedao.getDesignTwoApplayApproved(paramMap);
            if (designTwoaedList != null && designTwoaedList.size() > 0) {
                for (Map map : designTwoaedList) {
                    //遍历出已经审批通过的起始节点

                    map.put("node_level", node_level + 1);
                    //查询起始节点审批通过但范围结束节点未审批通过的节点
                    Map notApplayApproved = templateEnginedao.getThreeMonthsNotApplayApproved(map);
                    if (notApplayApproved != null && notApplayApproved.size() > 0) {
                        //封装参数：顶设2计划完成时间，顶设2节点id
                        //查询顶设2的节点id
                        String plan_node_id = templateEnginedao.getDesignTwoPlanNode(map.get("plan_id") + "");
                        if (node_level == 3) {
                            //如果是第一区间 顶设2--首开前3个月。时间开始点为首开前3月的计划完成时间-30天
                            DateTime parse = DateUtil.parse(notApplayApproved.get("plan_end_time") + "");
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(parse);
                            cal.add(Calendar.DATE, -30);
                            Date date = cal.getTime();
                            String format = DateUtil.format(date, "yyyy-MM-dd");
                            notApplayApproved.put("design_two_plan_end_time", format);
                            Date nowDate = new Date();
                            boolean before = nowDate.before(date);
                            if (before) {
                                isMeagen = false;
                            }
                        } else {
                            notApplayApproved.put("design_two_plan_end_time", map.get("plan_end_time") + "");
                        }
                        notApplayApproved.put("this_node_time", notApplayApproved.get("plan_end_time"));
                        notApplayApproved.put("design_two_plan_node_id", plan_node_id);
                        notApplayApproved.put("node_level", node_level);
                        //查询出起始节点的计划数据
                        Map previousPlanData = templateEnginedao.getPreviousPlanData(notApplayApproved);
                        notApplayApproved.put("node_level", node_level + 1);
                        if (previousPlanData != null && previousPlanData.size() > 0) {
                            notApplayApproved.putAll(previousPlanData);
                        }
                        //获取顶设2填报各个节点计划数据，并平均到天、计算出对应的累计计划数据
                        Map dayNumData = templateEnginedao.getDesignTwoPlanDayNumData(notApplayApproved);
                        if (dayNumData != null && dayNumData.size() > 0) {
                            dayNumData.put("node_names", notApplayApproved.get("node_names"));
                            //第一节点 顶设2-首开前3月
                            //将顶设2的计划完成时间放入数据集
                            dayNumData.put("design_two_plan_end_time", map.get("plan_end_time") + "");
                            dayNumData.putAll(map);
                            dayNumData.put("end_time", notApplayApproved.get("this_node_time"));
                            if (node_level == 3) {
                                dayNumData.put("Gear", "1");
                            } else if (node_level == 4) {
                                dayNumData.put("Gear", "2");
                            } else if (node_level == 5) {
                                dayNumData.put("Gear", "3");
                            } else if (node_level == 6) {
                                dayNumData.put("Gear", "4");
                            }
                            countDayPer(dayNumData, isMeagen);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 计算日偏差率
     *
     * @param map
     * @return
     */
    public Map countDayPer(Map map, boolean isMeagen) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingUsed(false);
        //根据当前时间获取实际数据=》从明源和旭客同步的认购、大卡、小卡、来访、报备等数据
        Map numberForYesDay = templateEnginedao.getActuerNumberForYesDay(map);
        //获取所有的实际数据
        Map actuerTotalNumberForYesDay = templateEnginedao.getActuerTotalNumberForYesDay(map);
        //获取开盘信息
        Map openData = templateEnginedao.getOpenData(map.get("plan_id") + "");

        if (openData != null && openData.size() > 0) {
            String open_countdown = openData.get("open_countdown") + "";
            if (!"null".equals(open_countdown) && !"".equals(open_countdown)) {
                int parseInt = Integer.parseInt(open_countdown);
                if (parseInt < 0) {
                    openData.put("open_countdown", "已逾期" + -parseInt);
                }
            }
            map.putAll(openData);
        }
        if (numberForYesDay != null && numberForYesDay.size() > 0) {

            map.putAll(actuerTotalNumberForYesDay);
            //获取昨天的实际来访人数
            float visit_num = Float.parseFloat(numberForYesDay.get("visit_num") + "");
            if (map != null && map.size() > 0) {
                //获取顶设2-首开前3月的每天平均来访值
                float visit_day_num = Float.parseFloat(map.get("visit_day_num") + "");
                float per_visit = 0;
                if (visit_num != 0) {
                    //计算偏差率
                    per_visit = Float.parseFloat(df.format((visit_day_num - visit_num) / visit_day_num * 100));
                } else {
                    if(visit_day_num==0){
                        per_visit = 0;
                    }else{
                        per_visit = 100;
                    }
                }
                String cloumn = "";
                //查询当前计划所发送的偏差记录
                String Gear = map.get("Gear") + "";
                String gearData = templateEnginedao.getGearData(map);
                //档位1：顶设2-首开前三月
                if ("1".equals(Gear)) {
                    cloumn = "top2_firstthree";
                }
                //档位2：首开前3月-首开前两月
                else if ("2".equals(Gear)) {
                    cloumn = "firstthree_firsttwo";
                }
                //档位3：首开前2月-首开前21天
                else if ("3".equals(Gear)) {
                    cloumn = "firsttwo_firstone";
                }
                //档位4：首开前21天-首开前7天
                else if ("4".equals(Gear)) {
                    cloumn = "firstone_firstseven";
                }
                //查询三大件延期提醒的消息模版
                Map<Object, Object> paramMap = new HashMap<>();
                paramMap.put("template_name", "客储偏差预警");
                Map templateInfo = templateEnginedao.getTemplateInfo(paramMap);
                map.put("per_visit", per_visit);
                //获取首开整体客储偏差情况
                getOpenZtPcInfo(map);
                //三大件数据
                Map threeDataView = threeDataView(map);
                map.putAll(threeDataView);
                if (templateInfo != null) {
                    //获取模版标题
                    String template_title = templateInfo.get("template_title") + "";
                    //获取模版内容
                    String template_info = templateInfo.get("template_info") + "";
                    String jsonString = JSON.toJSONString(map);
                    System.err.println(jsonString);
                    Map data = replaceData(map, template_title, template_info);
                    if (data != null && data.size() > 0) {
                        map.putAll(data);
                    }
                }

                if (gearData != null && !"".equals(gearData)&&!"null".equals(gearData)) {
                    float aFloat = Float.parseFloat(gearData);
                    float ax = aFloat + 5;
                    if (per_visit >= ax) {
                        //更新偏差记录
                        map.put(cloumn, df.format(per_visit));
                        //todo 生成消息
                        //查询首开偏差预警对应的模版
                        if (isMeagen) {
                            templateEnginedao.updateSendPer(map);
                            messageGeneration(map, templateInfo, map.get("project_id") + "");
                        }
                    }
                } else {
                    //如果没有查询到偏差记录、以15为起步，如果>=15为第一次发送
                    if (per_visit >= 15) {
                        //记录发送偏差记录
                        map.put(cloumn, df.format(per_visit));
                        //todo 生成消息
                        //查询首开偏差预警对应的模版
                        if (isMeagen) {
                            templateEnginedao.saveSendPer(map);
                            messageGeneration(map, templateInfo, map.get("project_id") + "");
                        }
                    }
                }

            }

        }
        return map;
    }


    /**
     * 计算当前计划当前节点客储数据
     *
     * @param planMap
     * @param actualMap
     * @return
     */
    public Map countPer(Map planMap, Map actualMap) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingUsed(false);
        Map<Object, Object> perMap = new HashMap<>();
        if (planMap != null && planMap.size() > 0) {
            int total_plan_visitNum = Integer.parseInt(planMap.get("total_plan_visitNum") + "");
            int total_plan_littleNum = Integer.parseInt(planMap.get("total_plan_littleNum") + "");
            double total_plan_bigNum = Double.parseDouble(planMap.get("total_plan_bigNum") + "");
            if (actualMap != null && actualMap.size() > 0) {
                int total_actual_visitNum = Integer.parseInt(actualMap.get("total_actual_visitNum") + "");
                double total_actual_bigNum = Double.parseDouble(actualMap.get("total_actual_bigNum") + "");
                float total_actual_littleNum = Float.parseFloat(actualMap.get("total_actual_littleNum") + "");
                if (0 != total_plan_visitNum) {
                    float parseFloatPlan = Float.parseFloat(total_plan_visitNum + "");
                    float parseFloatActule = Float.parseFloat(total_actual_visitNum + "");
                    perMap.put("count_plan_visitPer", df.format(((parseFloatPlan - parseFloatActule) / parseFloatPlan) * 100));
                } else {
                    perMap.put("count_plan_visitPer", "0.00");
                }

                if (total_plan_littleNum != 0 && total_plan_littleNum != 0.00) {
                    float parseFloatPlan = Float.parseFloat(total_plan_littleNum + "");
                    // float total_actual_littleNum = Float.parseFloat(total_actual_littleNum + "");
                    perMap.put("count_plan_littlePer", df.format(((parseFloatPlan - total_actual_littleNum) / parseFloatPlan) * 100));
                } else {
                    perMap.put("count_plan_littlePer", "0.00");
                }
                if (total_plan_bigNum != 0 && total_plan_bigNum != 0.00) {
                    float parseFloatPlan = Float.parseFloat(total_plan_bigNum + "");
                    float parseFloatActule = Float.parseFloat(total_actual_bigNum + "");
                    perMap.put("count_plan_bigPer", df.format(((parseFloatPlan - parseFloatActule) / parseFloatPlan) * 100) + "%");
                } else {
                    perMap.put("count_plan_bigPer", "0.00");
                }
            } else {
                perMap.put("count_plan_visitPer", "0.00");
                perMap.put("count_plan_littlePer", "0.00");
                perMap.put("count_plan_bigPer", "0.00");

            }
        }
        return perMap;
    }

    /**
     * 计算开盘节点客储数据
     */
    public Map openCountPer(Map planMap, Map actualMap) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingUsed(false);
        Map<Object, Object> perMap = new HashMap<>();
        if (planMap != null && planMap.size() > 0) {
            int total_plan_visitNum = Integer.parseInt(planMap.get("open_plan_visitNum") + "");
            int total_plan_littleNum = Integer.parseInt(planMap.get("open_plan_littleNum") + "");
            double total_plan_bigNum = Double.parseDouble(planMap.get("open_plan_bigNum") + "");
            if (actualMap != null && actualMap.size() > 0) {
                int total_actual_visitNum = Integer.parseInt(actualMap.get("open_actual_visitNum") + "");
                int total_actual_littleNum = Integer.parseInt(actualMap.get("open_actual_littleNum") + "");
                double total_actual_bigNum = Double.parseDouble(actualMap.get("open_actual_bigNum") + "");
                if (total_plan_visitNum != 0) {
                    float parseFloatPlan = Float.parseFloat(total_plan_visitNum + "");
                    float parseFloatActule = Float.parseFloat(total_actual_visitNum + "");
                    if (parseFloatPlan != 0) {
                        perMap.put("open_plan_visitPer", df.format((parseFloatActule / parseFloatPlan) * 100));
                    } else {
                        perMap.put("open_plan_visitPer", "100");
                    }
                } else {
                    perMap.put("open_plan_visitPer", "0.00");
                }
                if (total_plan_littleNum != 0) {
                    float parseFloatPlan = Float.parseFloat(total_plan_littleNum + "");
                    float parseFloatActule = Float.parseFloat(total_actual_littleNum + "");
                    if (parseFloatPlan != 0) {
                        perMap.put("open_plan_littlePer", df.format((parseFloatActule / parseFloatPlan) * 100));
                    } else {
                        perMap.put("open_plan_littlePer", "100");
                    }
                } else {
                    perMap.put("open_plan_littlePer", "0.00");
                }
                if (total_plan_bigNum != 0) {
                    float parseFloatPlan = Float.parseFloat(total_plan_bigNum + "");
                    float parseFloatActule = Float.parseFloat(total_actual_bigNum + "");
                    if (parseFloatPlan != 0) {
                        perMap.put("open_plan_bigPer", df.format((parseFloatActule / parseFloatPlan) * 100));
                    } else {
                        perMap.put("open_plan_bigPer", "100");
                    }
                } else {
                    perMap.put("open_plan_bigPer", "0.00");

                }
            } else {
                perMap.put("open_plan_visitPer", "0.00");
                perMap.put("open_plan_littlePer", "0.00");
                perMap.put("open_plan_bigPer", "0.00");
            }
        }
        return perMap;
    }

    /**
     * 将html标签替换为变量通用方法
     *
     * @param dataMap        数据
     * @param template_title 模版标题
     * @param template_info  模版内容
     * @return
     */
    public Map replaceData(Map<String, Object> dataMap, String template_title, String template_info) {
        String blackur="<a href="+firstOpenSystemUrl+">"+"详情请登录首开计划管理系统进行查看!"+"</a>";
        dataMap.put("firstOpenSystemUrl",blackur);
        if (dataMap != null) {
            if (template_title != null && !template_title.equals("null")) {
                template_title = template_title.replaceAll("\\{", "A_");
                template_title = template_title.replaceAll("\\}", "_A");
                template_title = template_title.replaceAll("\\$", "");
            }
            if (template_info != null && !template_info.equals("null")) {
                template_info = template_info.replaceAll("\\{", "A_");
                template_info = template_info.replaceAll("\\}", "_A");
                template_info = template_info.replaceAll("\\$", "");
            }

            System.out.println(template_title);
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                //将数据map中的key取出，拼接为模版中的变量
                String key = "A_" + entry.getKey() + "_A";
                if (template_title.contains(key) || template_info.contains(key)) {
                    //取出相应的值
                    Object value = entry.getValue();
                    if (value != null && !"".equals(value) && !"null".equals(value)) {
                        //将模版标题中的
                        template_title = template_title.replaceAll(key, value + "");
                        template_info = template_info.replaceAll(key, value + "");
                    }
                }
            }
        }
        //封装替换以后的数据
        Map<Object, Object> resultMap = new HashMap<>();
        template_info = template_info.replaceAll("A_(.*?)_A", "");
        //将替换后的模版标题放入返回数据
        resultMap.put("template_title", template_title);
        //将替换后的模版内容放入返回数据
        resultMap.put("template_info", template_info);
        //返回数据
        return resultMap;
    }





    /**
     * 消息生成通用类
     */
    public void messageGeneration(Map dataMap, Map templateInfo, String project_id) {
        //存放日志
        Map logParams = new HashMap();
        Map<Object, Object> messageMap = new HashMap<>();
        String message_id = UUID.randomUUID().toString();
        try {
            messageMap.put("id", message_id);
            //消息标题
            messageMap.put("message_title", dataMap.get("template_title"));
            //消息详情
            messageMap.put("message_info", dataMap.get("template_info"));
            //消息所属业务模块
            messageMap.put("message_type_name", templateInfo.get("template_type_name"));
            //消息发送类型 手动||自动
            messageMap.put("message_send_type", templateInfo.get("template_send_type"));
            //消息发送方式 钉钉||邮箱||钉钉+邮箱
            String open_dingtalk = templateInfo.get("open_dingtalk") + "";
            String open_email = templateInfo.get("open_emaill") + "";
            String open = open_dingtalk + open_email;
            //如果没有设置，就默认为未开启此方式发送服务
            if (open != null) {
                open = open.replaceAll("null", "0");
            }
            System.out.println(open);
            if ("10".equals(open)) {
                messageMap.put("message_send_mode", 1);
            } else if ("01".equals(open)) {
                messageMap.put("message_send_mode", 2);
            } else if ("11".equals(open)) {
                messageMap.put("message_send_mode", 3);
            }
            messageMap.put("message_template_id", templateInfo.get("id"));
            //默认为发送失败状态也就是待发送状态
            messageMap.put("message_send_status", 0);
            //消息所属项目
            messageMap.put("project_id", project_id);
            //消息通知类型
            messageMap.put("notice_type", templateInfo.get("template_name"));
            //todo 生成消息
            templateEnginedao.saveMessage(messageMap);
            logParams.put("TaskName", "消息生成成功");
            logParams.put("content", "消息id:" + message_id);

            //生成消息待发送人员列表
            ResultBody resultBody = messageManagerService.queryUserList(messageMap);
            if(resultBody.getCode()==200){
                List<Map> userList=(List<Map>) resultBody.getData();
                if(userList!=null&&userList.size()>0){
                    for (Map userMap:userList){
                        userMap.put("message_id",message_id);
                        messageManagerDao.insertUserList(userMap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logParams.put("TaskName", "消息生成失败");
            logParams.put("content", "失败信息:" + e.getMessage() + "失败原因:" + e.getCause());
        } finally {
            //无论执行成功或者失败，都添加日志记录
            timeLogsDao.insertLog(logParams);
        }

    }


    /**
     * 九大节点延期提醒
     *
     * @return
     */
    @Override
    public ResultBody sendNodeOverdueMes(Map map) {
        try {
            String plan_id = "";
            if (map != null && map.size() > 0) {
                plan_id = map.get("plan_id") + "";
            } else {
                plan_id = null;
            }
            List<Map> planData = templateEnginedao.getSendPlanData(plan_id);
            if (planData != null && planData.size() > 0) {
                for (Map planMap : planData) {
                    //从顶设1节点开始查询
                    Integer node_level = 2;
                    //到首开后一个月结束循环
                    while (node_level < 13) {
                        planMap.put("node_level", node_level);
                        //查询节点信息
                        Map nodedata = queryOverdueNode(planMap);
                        node_level++;
                        //如果返回数据不为空，说明包含符合逾期一天的节点
                        if (nodedata != null && nodedata.size() > 0) {
                            //生成逾期消息
                            gengecaOverdueNodeMessage(nodedata);
                        } else {
                            //如果返回为空，结束本次循环，查询下一个节点
                            continue;
                        }
                    }
                }
            }
            return ResultBody.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(-2006, "逾期消息生成失败:" + e.toString());
        }

    }

    /**
     * 生成首开播报消息提醒
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody firstBroadcastMessageGen(Map map) {
        //根据流程业务主键查询节点/计划的详细信息
        //项目营销pM
        String projectPM = "";
        //项目总
        String projectZO = "";
        //城市总
        String cityZO = "";
        try {
            Map firstOpenInfo = templateEnginedao.getFirstOpenInfo(map);
            if (firstOpenInfo != null && firstOpenInfo.size() > 0) {

                String designtwo_avg="";
                String actual_avg="";
                //查询均价
                List<Map> firstOpenAvgData = templateEnginedao.getFirstOpenAvgData(map);
                if(firstOpenAvgData!=null&&firstOpenAvgData.size()>0){
                    for (Map firstOpenAvgDatum : firstOpenAvgData) {
                        designtwo_avg+=firstOpenAvgDatum.get("invest_avg")+"元</br>";
                        actual_avg+=firstOpenAvgDatum.get("cash_price")+"元</br>";
                    }
                }
                firstOpenInfo.put("designtwo_avg",designtwo_avg);
                firstOpenInfo.put("actual_avg",actual_avg);

                //todo 查询对应的项目总、城市总
                List<Map> leaderUsers = templateEnginedao.getLeaderUsers(firstOpenInfo);
                if (leaderUsers != null && leaderUsers.size() > 0) {
                    for (Map leaderUser : leaderUsers) {
                        String JobName = leaderUser.get("JobName") + "";
                        if ("项目总".equals(JobName)) {
                            projectZO = leaderUser.get("EmployeeName") + "";
                            continue;
                        } else if ("项目营销PM".equals(JobName)) {
                            projectPM = leaderUser.get("EmployeeName") + "";
                            continue;
                        } else if ("城市负责人".equals(JobName)) {
                            cityZO = leaderUser.get("EmployeeName") + "";
                            continue;
                        }
                    }
                }
                //项目总
                firstOpenInfo.put("projectZO", projectZO);
                //项目营销PM
                firstOpenInfo.put("projectPM", projectPM);
                //城市总
                firstOpenInfo.put("cityZO", cityZO);

                Map<Object, Object> paramMap = new HashMap<>();
                //查询对应模版
                paramMap.put("template_name", "首开简报");
                Map templateInfo = templateEnginedao.getTemplateInfo(paramMap);
                //获取模版标题
                String template_title = templateInfo.get("template_title") + "";
                //获取模版内容
                String template_info = templateInfo.get("template_info") + "";
                Map replaceData = replaceData(firstOpenInfo, template_title, template_info);
                messageGeneration(replaceData,templateInfo,firstOpenInfo.get("project_id")+"");
            }
            return ResultBody.success(null);
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-4006,"首开简报生成失败!");
        }
    }

    /**
     *
     * @param templateName 模版名称
     * @param dataMap 数据集
     * @param projectId 项目Id
     * @return
     */
    @Override
    public ResultBody createMessageCommon(String templateName,Map dataMap,String projectId) {
        //查询定调价模版
        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("template_name", templateName);
        Map templateInfo = templateEnginedao.getTemplateInfo(paramMap);
        //获取对应消息模版的消息标题
        String template_title = templateInfo.get("template_title") + "";
        //获取对应消息模版的消息内容
        String template_info = templateInfo.get("template_info") + "";
        Map map =replaceData(dataMap, template_title, template_info);
        messageGeneration(map,templateInfo,projectId);
        return null;
    }


    /**
     * 查询逾期一天的节点信息
     * @param map
     * @return
     */
    public Map queryOverdueNode(Map map) {
        try {
            System.out.println(map.get("node_level") + "");
            map.remove("complete");
            map.remove("notComplete");
            //查询当前节点未延期的
            map.put("complete", "true");
            //如果当前节点已经有有效的
            Map nodeData = templateEnginedao.queryOverdueNodeData(map);
            if (nodeData != null && nodeData.size() > 0) {
                //返回空，不生成消息
                return null;
            } else {
                //查询未完成的节点
                map.remove("complete");
                map.put("notComplete", "true");
                //else： 说明当前节点没有有效记录，查询无效的节点并且符合逾期一天的节点信息
                nodeData = templateEnginedao.queryOverdueNodeData(map);

                if (nodeData != null && nodeData.size() > 0) {
                    //查询是否存在逾期行为
                    Map isDelayOpen = templateEnginedao.selectIsDelayOpen(map.get("plan_id") + "");
                    if(isDelayOpen!=null&&isDelayOpen.size()>0){
                        //查询延期日期是否逾期
                        isDelayOpen.put("plan_node_id",nodeData.get("plan_node_id"));
                        Map enginedaoDelayIsYq = templateEnginedao.getDelayIsYq(isDelayOpen);
                        if(enginedaoDelayIsYq!=null&&enginedaoDelayIsYq.size()>0){
                            map.putAll(enginedaoDelayIsYq);
                            return map;
                        }else {
                            return null;
                        }
                    }
                    //返回节点信息
                    map.putAll(nodeData);
                    return map;
                } else {
                    //如果没有节点记录，返回空，不生成消息
                    return null;
                }
            }
        } catch (Exception e) {
            //手动回滚所有已执行的事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 生成逾期节点消息
     *
     * @return
     */
    public void gengecaOverdueNodeMessage(Map map) {
        //查询对应的消息模版
        String node_level = map.get("node_level") + "";
        //获取对应模版
        String nodeForTemplate = getNodeForTemplate(node_level);
        //查询三大件延期提醒的消息模版
        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("template_name", nodeForTemplate);
        Map templateInfo = templateEnginedao.getTemplateInfo(paramMap);
        //获取模版标题
        String template_title = templateInfo.get("template_title") + "";
        //获取模版内容
        String template_info = templateInfo.get("template_info") + "";
        Map data = replaceData(map, template_title, template_info);
        messageGeneration(data, templateInfo, map.get("project_id") + "");
    }

    /**
     * 获取对应节点逾期提醒的模版
     */
    public String getNodeForTemplate(String node_level) {
        switch (node_level) {
            case "2":
                return "顶设1逾期提醒";
            case "3":
                return "顶设2逾期提醒";
            case "4":
                return "首开前3月逾期提醒";
            case "5":
                return "首开前2月逾期提醒";
            case "6":
                return "首开前21天逾期提醒";
            case "7":
                return "首开前7天逾期提醒";
            case "8":
                return "首开逾期提醒";
            case "9":
                return "首开后1个月逾期提醒";
            case "10":
                return "售楼处开放资料上传提醒";
            case "11":
                return "景观样板房开放资料上传提醒";
            case "12":
                return "样板房开放资料上传提醒";
            default:
                return "";
        }
    }


    /**
     * 三大件数据获取-通用
     */
    public Map threeDataView(Map map) {
        //三大件数据
        map.put("node_level", 10);
        Map salesreepiecesNode = templateEnginedao.getTthreepiecesNode(map);
        if (salesreepiecesNode != null && salesreepiecesNode.size() > 0) {
            String sales_finish_time = salesreepiecesNode.get("sales_finish_time") + "";
            if (sales_finish_time != "未开放" && !"null".equals(sales_finish_time)) {
                String extension_days = salesreepiecesNode.get("sales_extension_daynum") + "";
                if (!"null".equals(extension_days)) {
                    int i = Integer.parseInt(extension_days);
                    if (i < 0) {
                        salesreepiecesNode.put("sales_finish_time", sales_finish_time + "  逾期" + -i + "天");
                    }
                }
            }
            map.putAll(salesreepiecesNode);
        }
        map.put("node_level", 11);
        Map sampleeepiecesNode = templateEnginedao.getTthreepiecesNode(map);
        if (sampleeepiecesNode != null && sampleeepiecesNode.size() > 0) {
            String sales_finish_time = sampleeepiecesNode.get("sample_finish_time") + "";
            if (sales_finish_time != "未开放" && !"null".equals(sales_finish_time)) {
                String extension_days = sampleeepiecesNode.get("sample_extension_daynum") + "";
                if (!"null".equals(extension_days)) {
                    int i = Integer.parseInt(extension_days);
                    if (i < 0) {
                        sampleeepiecesNode.put("sample_finish_time", sales_finish_time + "  逾期" + -i + "天");
                    }
                }
            }
            map.putAll(sampleeepiecesNode);
        }
        map.put("node_level", 12);
        Map modelleeepiecesNode = templateEnginedao.getTthreepiecesNode(map);
        if (modelleeepiecesNode != null && modelleeepiecesNode.size() > 0) {
            String sales_finish_time = modelleeepiecesNode.get("model_finish_time") + "";
            if (sales_finish_time != "未开放" && !"null".equals(sales_finish_time)) {
                String extension_days = modelleeepiecesNode.get("model_extension_daynum") + "";
                if (!"null".equals(extension_days)) {
                    int i = Integer.parseInt(extension_days);
                    if (i < 0) {
                        modelleeepiecesNode.put("model_finish_time", sales_finish_time + "  逾期" + -i + "天");
                    }
                }
            }
            map.putAll(modelleeepiecesNode);
        }
        return map;
    }


    /**
     * 获取首开整体客储偏差-通用
     *
     * @param paramMap
     */
    public void getOpenZtPcInfo(Map paramMap) {
        Map<Object, Object> planDataMap = null;
        Map<Object, Object> actualDataMap = null;
        //获取到节点存储的首开计划数据
        planDataMap = templateEnginedao.getOpenPlanData(paramMap);
        if (planDataMap == null || planDataMap.size() <= 0) {
            Map dingsgTwoPlanData = templateEnginedao.getDingsgTwoPlanData(paramMap);
            if (dingsgTwoPlanData != null && dingsgTwoPlanData.size() > 0) {
                planDataMap = dingsgTwoPlanData;
            }
        }
        //获取节点存储的首开实际数据
        //actualDataMap = templateEnginedao.getOpenActualData(paramMap);
        //if (actualDataMap == null || actualDataMap.size() <= 0) {
            //获取中间表同步的实际数据
            Map customerStorActualData = templateEnginedao.getCustomerStorActualData(paramMap);
            if (customerStorActualData != null && customerStorActualData.size() > 0) {
                actualDataMap = customerStorActualData;
            }
     //   }

        if (actualDataMap != null && actualDataMap.size() > 0) {
            paramMap.putAll(actualDataMap);
        } else {
            if(actualDataMap==null){
                actualDataMap=new HashMap<>();
            }
            actualDataMap.put("open_actual_visitNum", 0);
            actualDataMap.put("open_actual_littleNum", 0);
            actualDataMap.put("open_actual_bigNum", 0);
        }
        Map openCountPer = openCountPer(planDataMap, actualDataMap);
        if (planDataMap != null && planDataMap.size() > 0) {
            paramMap.putAll(planDataMap);
        }
        if (openCountPer != null) {
            paramMap.putAll(openCountPer);
        }
    }

    /**
     * 获取开盘日期、开盘倒计时数据
     */
    public void getOpenInfo(Map paramMap) {
        Map openData = templateEnginedao.getOpenData(paramMap.get("plan_id") + "");
        if (openData != null && openData.size() > 0) {
            paramMap.putAll(openData);
        }
    }

    /**
     * 首开播报消息
     */

}
