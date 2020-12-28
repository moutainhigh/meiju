package cn.visolink.firstplan.firstopen.service.impl;
import cn.visolink.common.security.security.JwtUser;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.TaskLand.service.TakeLandService;
import cn.visolink.firstplan.firstopen.dao.FirstOpenBroadcastDao;
import cn.visolink.firstplan.firstopen.service.FirstOpenBroadcastService;
import cn.visolink.firstplan.opening.dao.OpeningDao;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author sjl
 * @Created date 2020/7/8 11:10 上午
 */
@Service
@Transactional
public class FirstOpenBroadcastServiceImpl implements FirstOpenBroadcastService {
    @Autowired
    private FirstOpenBroadcastDao firstOpenBroadcastDao;

    @Autowired
    private OpeningDao openingDao;

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${oaflow.fpFlowCode}")
    private String fpFlowCode;

    @Autowired
    private TakeLandService takeLandService;

    /**
     * 首开播报表渲染数据
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody viewOpenBroadcastData(Map map) {
        Map<Object, Object> resultMap = new HashMap<>();
        String create = map.get("create") + "";
        try {
            String plan_node_id = map.get("plan_node_id") + "";
            String plan_id=map.get("plan_id")+"";
            if("".equals(plan_id)||"null".equals(plan_id)){
                //查询计划
                plan_id = firstOpenBroadcastDao.getPlanDataByPlanNodeData(plan_node_id);
            }
            map.put("plan_id",plan_id);
            //查询当前计划首开节点是否已经完成（项目是否已开盘）
            //Map openNodeInfo = firstOpenBroadcastDao.getOpenNodeInfo(map);

            //根据节点id查询首开当日播报的数据
            Map versionMap = firstOpenBroadcastDao.getThisNodeStatus(plan_node_id);
            String thisdayIds = "";
            String morrowdayId = "";
            Map<Object, Object> applayMap = new HashMap<>();
            if (versionMap != null && versionMap.size() > 0) {
                String thisDayid = versionMap.get("thisDayid") + "";
                if (!"".equals(thisDayid) && !"null".equals(thisDayid) && !"new".equals(create)) {
                    String plan_approval = versionMap.get("plan_approval") + "";
                    //查询当日数据
                    Map thisDayData = openingDao.selectOpenThisDayByNodeId(plan_node_id);
                    //查询次日数据
                    versionMap.put("plan_node_id", plan_node_id);
                    Map morrowDayData = firstOpenBroadcastDao.getOpenMorrowDayByPlanNodeId(versionMap);
                    List<Map> thisDayAvg = openingDao.selectOpenThisDayAvgByNodeId(plan_node_id);
                    if (thisDayData != null && thisDayData.size() > 0) {
                        thisdayIds = thisDayData.get("thisdayId") + "";
                    }
                    if (morrowDayData != null && morrowDayData.size() > 0) {
                        morrowdayId = morrowDayData.get("id") + "";
                    } else {
                        //根据计划id查询次日数据
                        Map morrowDayByPlanId = openingDao.selectOpenMorrowDayByPlanId(map);
                        if (morrowDayByPlanId != null && morrowDayByPlanId.size() > 0) {
                            morrowdayId = morrowDayByPlanId.get("id") + "";
                            morrowDayData = morrowDayByPlanId;
                        }
                    }
                    List<Map> morrowDayAvg = openingDao.selectOpenMorrowDayAvgByPlanNodeId(plan_node_id);
                    if (morrowDayAvg == null || morrowDayAvg.size() <= 0) {
                        morrowDayAvg = openingDao.selectOpenMorrowDayAvgById(morrowdayId);
                    }
                    resultMap.put("thisDayData", thisDayData);
                    resultMap.put("morrowDayData", morrowDayData);
                    resultMap.put("thisDayAvg", thisDayAvg);
                    resultMap.put("morrowDayAvg", morrowDayAvg);
                    //如果当前数据版本不是审批中货审批通过，实时更新数据
                        if (!"3".equals(plan_approval) && !"4".equals(plan_approval)) {
                            updateData(morrowDayData, thisDayData,map, resultMap, thisdayIds, morrowdayId);
                        }
                    return ResultBody.success(resultMap);
                } else {
                    //如果是创建版本，查询最新审批通过的节点数据
                    Map openNodeInfo = firstOpenBroadcastDao.getOpenNodeInfo(map);
                    //如果有最新有效节点
                    if (openNodeInfo != null && openNodeInfo.size() > 0) {
                        //查询当日数据
                        Map thisDayData = openingDao.selectOpenThisDayByNodeId(openNodeInfo.get("id") + "");
                        //查询次日数据
                        openNodeInfo.put("plan_node_id", openNodeInfo.get("id") + "");
                        Map morrowDayData = firstOpenBroadcastDao.getOpenMorrowDayByPlanNodeId(openNodeInfo);
                        List<Map> morrowDayAvg = openingDao.selectOpenMorrowDayAvgByPlanNodeId(plan_node_id);
                        List<Map> thisDayAvg = openingDao.selectOpenThisDayAvgByNodeId(plan_node_id);

                        resultMap.put("thisDayData", thisDayData);
                        resultMap.put("morrowDayData", morrowDayData);
                        resultMap.put("thisDayAvg", thisDayAvg);
                        resultMap.put("morrowDayAvg", morrowDayAvg);
                        //如果当前数据版本不是审批中货审批通过，实时更新数据
                        updateData(morrowDayData,thisDayData, map, resultMap, "", "");
                        return ResultBody.success(resultMap);
                    }//初始化数据
                    else {
                        updateData(null,null, map ,resultMap, null, null);
                        return ResultBody.success(resultMap);
                    }
                }
            } else {
                return ResultBody.error(-1005, "没有查询到该节点信息!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(1006, "数据查询失败:" + e.toString());
        }
    }

    @Override
    public ResultBody getBroadcasVersionData(Map map) {
        try {
            List<Map> versionList = openingDao.selectPlanNodeByPlanIdAndNodeLevle(map.get("plan_id") + "");
            return ResultBody.success(versionList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(-1007, "查询版本信息失败:" + e.toString());
        }
    }

    public void updateNodeStatus(String id){
        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("old_plan_node_id",id);
        paramMap.put("new_plan_node_id",UUID.randomUUID().toString());
        openingDao.updateNodeStatus(paramMap);
    }

    /**
     * 保存数据
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody saveOpenBroadCastData(Map map) {
        String username = map.get("username") + "";
        //获取全局参数
        String plan_id = map.get("plan_id") + "";
        String plan_node_id = map.get("plan_node_id") + "";
        String project_id = map.get("project_id") + "";
        String button = map.get("button") + "";
        try {
            if ("null".equals(plan_node_id) || "".equals(plan_node_id)) {
                //计划节点ID
                String newplan_node_id = UUID.randomUUID() + "";
                //新增计划节点
                Map planNode = new HashMap();
                planNode.put("id", newplan_node_id);
                planNode.put("plan_id", plan_id);
                planNode.put("creator", username);
                planNode.put("node_level", "8");
                openingDao.insertPlanNodeOpen(planNode);
                plan_node_id = newplan_node_id;
            }
            //获取次日数据
            Map morrowDayData = (Map) map.get("morrowDayData");
            //获取当日数据
            Map thisDayData = (Map) map.get("thisDayData");
            int actualSellingNum=0;
            if (thisDayData != null) {
                String sellingNum = String.valueOf(thisDayData.get("actual_selling_num"));
                if(!"null".equals(sellingNum)&&!"".equals(sellingNum)){
                    actualSellingNum= Integer.parseInt(sellingNum);
                }else {
                    actualSellingNum=0;
                }
                thisDayData.put("plan_id", plan_id);
                thisDayData.put("plan_node_id", plan_node_id);
                String thisDayId = thisDayData.get("thisDayId") + "";
                if (!"null".equals(thisDayId) && !"".equals(thisDayId)) {
                    //修改数
                    firstOpenBroadcastDao.updateThisDayData(thisDayData);
                } else {
                    firstOpenBroadcastDao.insertThisDayData(thisDayData);
                    //新增数据
                }
            }

            if (morrowDayData != null) {
                morrowDayData.put("plan_id", plan_id);
                morrowDayData.put("plan_node_id", plan_node_id);
                String morrowDayId = morrowDayData.get("morrowDayId") + "";
                //过滤数据
                Map filterMap = filterMap(morrowDayData);
                if (!"null".equals(morrowDayId) && !"".equals(morrowDayId)) {
                    //修改数据
                    firstOpenBroadcastDao.updateMorrowDayData(filterMap);
                } else {
                    //新增数据
                    firstOpenBroadcastDao.insertMorrowDayData(filterMap);
                }
            }
            //清空均价数据
            firstOpenBroadcastDao.clearAvgPriceData(plan_node_id);
            //获取当日均价数据
            List<Map> thisDayAvg = (List<Map>) map.get("thisDayAvg");
            if (thisDayAvg != null && thisDayAvg.size() > 0) {
                for (Map thisDayavgmap : thisDayAvg) {
                    thisDayavgmap.put("plan_id", plan_id);
                    thisDayavgmap.put("plan_node_id", plan_node_id);
                    //保存当日均价数据
                    firstOpenBroadcastDao.insertThisDayAvg(thisDayavgmap);
                }
            }
            //获取次日均价数据
            List<Map> morrowDayAvg = (List<Map>) map.get("morrowDayAvg");
            if (morrowDayAvg != null && morrowDayAvg.size() > 0) {
                for (Map morrowAvgMap : morrowDayAvg) {
                    morrowAvgMap.put("plan_id", plan_id);
                    morrowAvgMap.put("plan_node_id", plan_node_id);
                    firstOpenBroadcastDao.inertMorrowDayAvg(morrowAvgMap);
                }
            }
            //回参
            Map res = new HashMap();

            if (button.equals("submit")) {
                //校验实际去化套数，如果为0，不可发起审批
                if(actualSellingNum==0){
                    return ResultBody.error(-1005,"实际去化套数为0时,不可发起审批,请核对数据后重试!");
                }
                JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(username);
                String jobName = null;
                if (jwtUser != null) {
                    jobName = jwtUser.getJob().get("JobName") + "";
                }
                Map flowParams = new HashMap();
                flowParams.put("json_id", plan_node_id);
                flowParams.put("project_id", project_id);
                flowParams.put("creator", username);
                flowParams.put("flow_code", fpFlowCode);
                flowParams.put("TITLE", "首开当日播报审批表");
                flowParams.put("post_name", jobName);
                flowParams.put("orgName", "fp_open");
                takeLandService.insertFlow(flowParams);
                res.put("BSID", "FP");
                res.put("BTID", fpFlowCode);
                res.put("BOID", plan_node_id);
                res.put("UserID", username);
                res.put("LoginKey", "");
            }
            return ResultBody.success(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(-1056, "数据保存失败!" + e.toString());
        }
    }

    //更新数据
    public Map updateData(Map dataMap,Map thisDayDatas, Map paramMap, Map resultMap, String thisDayId, String morrowDayId) {
        DecimalFormat dF = new DecimalFormat("0.00");

        //初始化首开当日数据
        Map morrowDayData = firstOpenBroadcastDao.InitOpenMorrowDayData(paramMap);
        Map openThisDayData = firstOpenBroadcastDao.InitOpenThisDayData(paramMap);
        if(thisDayDatas!=null){
            openThisDayData.putAll(thisDayDatas);
        }
        if (dataMap == null) {
            dataMap = new HashMap();
        }
        if(thisDayDatas==null){
            thisDayDatas=new HashMap();
        }
        String openTime="";
        if (morrowDayData != null && morrowDayData.size() > 0) {
            //前七天预估开盘时间
            openTime = morrowDayData.get("estimate_open_node") + "";
            if (openTime.equals("null")) {
                openTime = "";
            }
            dataMap.putAll(morrowDayData);
        }

        //如果开盘数据已经保存  那么首开时间去首开当日的
        Map map = openingDao.selectLastOpening(paramMap.get("plan_id") + "");
        if (map != null && map.get("open_time") != null) {
            openTime = map.get("open_time") + "";
        }
        String endTime = "";
        if (openTime != null && openTime.length() >= 10) {
            endTime = openTime.substring(0, 10) + " 06:00:00";
        }
        openThisDayData.put("open_time", openTime);
        openThisDayData.put("end_time", endTime);
        openThisDayData.put("start_time", openTime);
        openThisDayData.put("project_id",paramMap.get("project_id"));
        Map buildingSell = openingDao.getOrderByProjectIdPriceNum(openThisDayData);

        //实际去化套数
        int acNum = 0;
        if (buildingSell != null) {
            acNum = buildingSell.get("num") == null ? 0 : Integer.parseInt(buildingSell.get("num") + "");
            openThisDayData.put("actual_selling_num", acNum);
            openThisDayData.put("actual_selling_value", dF.format(buildingSell.get("price")));
        } else {
            openThisDayData.put("actual_selling_num", 0);
            openThisDayData.put("actual_selling_value", 0);
        }

        //查询来访、小卡、大卡数据(实际)
        openThisDayData.put("endTime",openTime);
        Map guestMap = firstOpenBroadcastDao.queryGuestAcutalData(openThisDayData);
        if(guestMap!=null){
            guestMap.remove("actual_this_client");
        }
        //顶设2平均价格
        List<Map> designtwoAvg = openingDao.selectdesigntwoAvg(openThisDayData.get("node_id") + "");
        if (designtwoAvg != null && designtwoAvg.size() > 0) {
            //实际去化均价®
            for (Map data : designtwoAvg) {
                Map actualParams = new HashMap();
                actualParams.put("project_id", paramMap.get("project_id"));
                //根据业态名称获取业态code，到明源获取货值
                Map productMap = openingDao.getProductTypeCode(data.get("product_type") + "");
                String productCode = "";
                if (productMap != null) {
                    productCode = productMap.get("DictCode") + "";
                }
                actualParams.put("productCode", productCode);
                actualParams.put("end_time", endTime);
                actualParams.put("start_time", openTime);
                Map actualAvg = openingDao.getOrderByProjectIdAvg(actualParams);
                //顶设2
                Double targ_avg = data.get("targ_avg") == null ? 0 : Double.parseDouble(data.get("targ_avg") + "");
                Double priceavg = actualAvg == null || actualAvg.get("priceavg") == null ? 0 : Double.parseDouble(actualAvg.get("priceavg") + "");
                Double bias_price = targ_avg - priceavg;
                String bias_per = "0";
                if (bias_price == 0 || targ_avg == 0) {

                } else {
                    bias_per = dF.format((bias_price / targ_avg * 100));
                }
                data.put("targ_avg", dF.format(targ_avg));
                data.put("actual_avg", dF.format(priceavg));
                data.put("bias_price", dF.format(bias_price));
                data.put("bias_per", bias_per);
            }
        }
        if (guestMap != null) {
            openThisDayData.putAll(guestMap);
        }

        //投资版 战规版 首开前7天预估版
        Map info = openingDao.selectSevenDayIndex(paramMap.get("plan_id") + "");
        //获取开盘当日的取证货值去化率
       /* Map thisDayData = openingDao.getThisDayData(paramMap.get("plan_id") + "");
        if (thisDayData != null) {
            dataMap.putAll(thisDayData);
        }*/
        if (info != null) {
            dataMap.putAll(info);
        }
        //openingDao.getThisDayData();

        if (info != null) {
            List<Map> sevenDayAvg = openingDao.selectSevenDayAvg(info.get("dayId") + "");
            if (sevenDayAvg != null && sevenDayAvg.size() > 0) {
                for (Map smap : sevenDayAvg) {
                    String product_type = smap.get("product_type") + "";
                    if (designtwoAvg != null && designtwoAvg.size() > 0) {
                        for (Map dmap : designtwoAvg) {
                            String dproduct_type = dmap.get("product_type") + "";
                            if (product_type.equals(dproduct_type)) {
                                //投资版
                                float invest_avg = Float.parseFloat(smap.get("invest_avg") + "");
                                float actual_avg = Float.parseFloat(dmap.get("actual_avg") + "");
                                //战规版
                                float rules_avg = Float.parseFloat(smap.get("rules_avg") + "");
                                //兑现版
                                smap.put("cash_price", actual_avg);
                                //投资版偏差
                                smap.put("bias_price", dF.format(invest_avg - actual_avg));
                                //战规版偏差
                                smap.put("bias_per", dF.format(rules_avg - actual_avg));
                            }
                        }
                    }
                }
            }
            resultMap.put("morrowDayAvg", sevenDayAvg);
        }
        //查询审批人信息
        Map daoApplayData = openingDao.getApplayData(paramMap.get("plan_node_id") + "");
       if(daoApplayData!=null&&daoApplayData.size()>0){
           openThisDayData.putAll(daoApplayData);
       }
        //顶设1业态
        List<Map> dseAvg = openingDao.selectDesignoneValueByPlanId(paramMap.get("plan_id") + "");
        dataMap.put("productArray", dseAvg);
        //计算偏差/偏差率
        countData(openThisDayData);
        openThisDayData.put("thisDayId", thisDayId);

        resultMap.put("thisDayAvg", designtwoAvg);
        resultMap.put("thisDayData", openThisDayData);
        dataMap.put("morrowDayId", morrowDayId);
        resultMap.put("morrowDayData", dataMap);
        return resultMap;
    }

    //数据计算 偏差/偏差率
    public Map countData(Map res) {
        DecimalFormat dF = new DecimalFormat("0.00");
        //目标
        int designtwo_selling_num = Integer.parseInt(res.get("designtwo_selling_num") == null ? "0" : res.get("designtwo_selling_num") + "");
        double designtwo_selling_value = Double.parseDouble(res.get("designtwo_selling_value") == null ? "0" : res.get("designtwo_selling_value") + "");
        double designtwo_selling_takeper = Double.parseDouble(res.get("designtwo_selling_takeper") == null ? "0" : res.get("designtwo_selling_takeper") + "");
        int designtwo_add_visit = Integer.parseInt(res.get("designtwo_add_visit") == null ? "0" : res.get("designtwo_add_visit") + "");
        //目标大卡
        int designtwo_add_big = Integer.parseInt(res.get("designtwo_add_big") == null ? "0" : res.get("designtwo_add_big") + "");
        //目标小卡

        int designtwo_add_little = Integer.parseInt(res.get("designtwo_add_little") == null ? "0" : res.get("designtwo_add_little") + "");


        int designtwo_this_client = Integer.parseInt(res.get("designtwo_this_client") == null ? "0" : res.get("designtwo_this_client") + "");
        double designtwo_this_clientper = Double.parseDouble(res.get("designtwo_this_clientper") == null ? "0" : res.get("designtwo_this_clientper") + "");
        double designtwo_finish = Double.parseDouble(res.get("designtwo_finish") == null ? "0" : res.get("designtwo_finish") + "");
        //实际
        int actual_selling_num = Integer.parseInt(res.get("actual_selling_num") == null ? "0" : res.get("actual_selling_num") + "");
        double actual_selling_value = Double.parseDouble(res.get("actual_selling_value") == null ? "0" : res.get("actual_selling_value") + "");
        double actual_selling_takeper = Double.parseDouble(res.get("actual_selling_takeper") == null ? "0" : res.get("actual_selling_takeper") + "");
        int actual_add_visit = Integer.parseInt(res.get("actual_add_visit") == null ? "0" : res.get("actual_add_visit") + "");
        int actual_add_big = Integer.parseInt(res.get("actual_add_big") == null ? "0" : res.get("actual_add_big") + "");
        int actual_add_little = Integer.parseInt(res.get("actual_add_little") == null ? "0" : res.get("actual_add_little") + "");


        //实际大卡转化率
        double actual_add_big_per = Double.parseDouble(res.get("actual_add_big_per") == null ? "0" : res.get("actual_add_big_per") + "");

        //实际小卡转化率
        double actual_add_little_per = Double.parseDouble(res.get("actual_add_little_per") == null ? "0" : res.get("actual_add_little_per") + "");

        int actual_this_client = Integer.parseInt(res.get("actual_this_client") == null ? "0" : res.get("actual_this_client") + "");
        double actual_this_clientper = Double.parseDouble(res.get("actual_this_clientper") == null ? "0" : res.get("actual_this_clientper") + "");

        double actual_finish = Double.parseDouble(res.get("actual_finish") == null ? "0" : res.get("actual_finish") + "");
        //偏差
        double bias_selling_num = (double) designtwo_selling_num - actual_selling_num;
        double bias_selling_value = designtwo_selling_value - actual_selling_value;
        double bias_selling_takeper = designtwo_selling_takeper - actual_selling_takeper;
        Integer bias_add_visit = designtwo_add_visit - actual_add_visit;
        Integer bias_add_big = designtwo_add_big - actual_add_big;
        //小卡偏差（小卡计划-小卡实际）
        Integer bias_add_little = designtwo_add_little - actual_add_little;

        Integer bias_this_client = designtwo_this_client - actual_this_client;
        double bias_this_clientper = designtwo_this_clientper - actual_this_clientper;
        double bias_finish = designtwo_finish - actual_finish;
        res.put("bias_selling_num", dF.format(bias_selling_num));
        res.put("bias_selling_value", dF.format(bias_selling_value));
        res.put("bias_selling_takeper", dF.format(bias_selling_takeper));
        res.put("bias_add_visit", dF.format(bias_add_visit));
        //大卡偏差
        res.put("bias_add_big", dF.format(bias_add_big));

        //小卡偏差
        res.put("bias_add_little", dF.format(bias_add_little));
        res.put("bias_this_client", dF.format(bias_this_client));
        res.put("bias_this_clientper", dF.format(bias_this_clientper));
        res.put("bias_finish", dF.format(bias_finish));
        //偏差率
        if (designtwo_add_big != 0) {
            //目标大卡转化率
            res.put("designtwo_add_big_per", dF.format(((float) designtwo_selling_num / (float) designtwo_add_big) * 100));
        }
        if (designtwo_add_little != 0) {
            //目标大卡转化率
            res.put("designtwo_add_little_per", dF.format(((float) designtwo_selling_num / (float) designtwo_add_little) * 100));
        }

        //目标大卡转化率
        double designtwo_add_big_per = Double.parseDouble(res.get("designtwo_add_big_per") == null ? "0" : res.get("designtwo_add_big_per") + "");
        //目标小卡转化率
        double designtwo_add_little_per = Double.parseDouble(res.get("designtwo_add_little_per") == null ? "0" : res.get("designtwo_add_big_per") + "");


        double bias_add_big_per = designtwo_add_big_per - actual_add_big_per;

        double bias_add_little_per = designtwo_add_little_per - actual_add_little_per;

        //大卡转化率-偏差
        res.put("bias_add_big_per", dF.format(bias_add_big_per));
        //小卡转化率-偏差
        res.put("bias_add_little_per", dF.format(bias_add_little_per));

        if (designtwo_selling_num != 0) {
            res.put("biasper_selling_num", dF.format(((float) bias_selling_num / designtwo_selling_num) * 100));
        } else {
            res.put("biasper_selling_num", 0);
        }
        if (designtwo_selling_value != 0) {
            res.put("biasper_selling_value", dF.format(((float) bias_selling_value / designtwo_selling_value) * 100));
        } else {
            res.put("biasper_selling_value", 0);
        }
        if (designtwo_selling_takeper != 0) {
            res.put("biasper_selling_takeper", dF.format(((float) bias_selling_takeper / designtwo_selling_takeper) * 100));
        } else {
            res.put("biasper_selling_takeper", 0);
        }
        if (designtwo_add_visit != 0) {
            res.put("biasper_add_visit", dF.format(((float) bias_add_visit / designtwo_add_visit) * 100));
        } else {
            res.put("biasper_add_visit", 0);
        }
        if (designtwo_add_big != 0) {
            res.put("biasper_add_big", dF.format(((float) bias_add_big / designtwo_add_big) * 100));
        } else {
            res.put("biasper_add_big", 0);
        }

        //大卡转化率偏差率计算 偏差/计划
        if (designtwo_add_big_per != 0) {
            res.put("biasper_add_big_per", dF.format((float) (bias_add_big_per / designtwo_add_big_per) * 100));
        } else {
            res.put("biasper_add_big_per", 0);
        }

        //小卡转化率偏差率计算 偏差/计划
        if (designtwo_add_little_per != 0) {
            res.put("biasper_add_little_per", dF.format((float) (bias_add_little_per / designtwo_add_little_per) * 100));
        } else {
            res.put("biasper_add_little_per", 0);
        }

        if (designtwo_this_client != 0) {
            res.put("biasper_this_client", dF.format((float) bias_this_client / designtwo_this_client * 100));
        } else {
            res.put("biasper_this_client", 0);
        }
        if (designtwo_this_clientper != 0) {
            res.put("biasper_this_clientper", dF.format(((float) bias_this_clientper / designtwo_this_clientper) * 100));
        } else {
            res.put("biasper_this_clientper", 0);
        }

        if (designtwo_finish != 0) {
            res.put("biasper_finish", dF.format(((float) bias_finish / designtwo_finish) * 100));
        } else {
            res.put("biasper_finish", 0);
        }
        return res;
    }

    @Override
    public ResultBody exportExcelOpenBroadCastData(Map map, HttpServletRequest request, HttpServletResponse response) {
        //调用获取数据方法
        ResultBody resultBody = viewOpenBroadcastData(map);
        String filePath;
        //配置本地模版路径
        String realpath = null;
        long code = resultBody.getCode();

        try {
            realpath = request.getServletContext().getRealPath("/");
            String templatePath = File.separator + "TemplateExcel" + File.separator + "firstOpenBroadCast.xlsx";
            filePath = realpath + templatePath;

            //本地测试模版地址
            //realpath = "/Users/WorkSapce/Java/旭辉集团/Java/marketing-control-api/cifimaster/visolink-sales-api/src/main/webapp/TemplateExcel/firstOpenBroadCast.xlsx";
            //filePath = realpath;

            FileInputStream fileInputStream = null;
            String levelNode = "首开播报表";
            String id = map.get("id") + "";
            String projectName = map.get("projectName") + "";

            File templateFile = new File(filePath);
            if (!templateFile.exists()) {
                throw new BadRequestException(1004, "未读取到配置的导出模版，请先配置导出模版!");
            }
            //使用poi读取模版文件
            fileInputStream = new FileInputStream(templateFile);
            if (fileInputStream == null) {
                throw new BadRequestException(1004, "未读取到模版文件!");
            }
            if (code == 200) {
                Map bodyData = (Map) resultBody.getData();
                if (bodyData != null && bodyData.size() > 0) {
                    //创建工作簿对象，
                    XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                    //获取首开审批所需要的模版sheet页
                    XSSFSheet sheetAt = workbook.getSheetAt(0);
                    //获取第一行
                    XSSFRow row = sheetAt.getRow(0);
                    //替换模版中第一行，第一格数据
                    XSSFCellStyle cellType = row.getCell(0).getCellStyle();
                    row.getCell(0).setCellValue(projectName + levelNode + "数据导出");
                    row.getCell(0).setCellStyle(cellType);
                    //获取第一部分表格数据
                    Map thisDayData = (Map) bodyData.get("thisDayData");
                    //获取第四行-去化套数
                    XSSFRow row4 = sheetAt.getRow(4);
                    XSSFCellStyle xssfRowStyle = row4.getRowStyle();
                    XSSFCell xssfCell = row4.getCell(0);
                    XSSFCellStyle cellStyle = xssfCell.getCellStyle();
                    //获取第五行-去化货值
                    XSSFRow row5 = sheetAt.getRow(5);

                    //去化套数
                    String[] thisDayDataArray1 = {"designtwo_selling_num", "actual_selling_num", "bias_selling_num", "biasper_selling_num"};
                    //去化货值
                    String[] thisDayDataArray2 = {"designtwo_selling_value", "actual_selling_value", "bias_selling_value", "biasper_selling_value"};
                    //取证货值去化率
                    String[] thisDayDataArray3 = {"designtwo_selling_takeper", "actual_selling_takeper", "bias_selling_takeper", "biasper_selling_takeper"};
                    //累计来访组
                    String[] thisDayDataArray4 = {"designtwo_add_visit", "actual_add_visit", "bias_add_visit", "biasper_add_visit"};
                    //累计大卡
                    String[] thisDayDataArray5 = {"designtwo_add_big", "actual_add_big", "bias_add_big", "biasper_add_big"};
                    //大卡转化率
                    String[] thisDayDataArray6 = {"designtwo_add_big_per", "actual_add_big_per", "bias_add_big_per", "biasper_add_big_per"};

                    //当天到场客户量
                    String[] thisDayDataArray7 = {"designtwo_this_client", "actual_this_client", "bias_this_client", "biasper_this_client"};

                    //当天到场客户转化率
                    String[] thisDayDataArray8 = {"designtwo_this_clientper", "actual_this_clientper", "bias_this_clientper", "biasper_this_clientper"};

                    //成交率
                    String[] thisDayDataArray9 = {"designtwo_finish", "actual_finish", "bias_finish", "biasper_finish"};

                    if (thisDayData != null && thisDayData.size() > 0) {
                        for (int i = 2; i < 6; i++) {
                            row4.getCell(i).setCellValue(getFliterValue(thisDayData.get(thisDayDataArray1[i - 2]) + ""));
                        }
                        for (int i = 2; i < 6; i++) {
                            row5.getCell(i).setCellValue(getFliterValue(thisDayData.get(thisDayDataArray2[i - 2]) + ""));
                        }

                        int shiftNumber = 0;
                        List<Map> thisDayAvg = (List<Map>) bodyData.get("thisDayAvg");
                        if (thisDayAvg != null && thisDayAvg.size() > 0) {
                            shiftNumber = thisDayAvg.size();
                            //去化均价数据
                            String[] thisDayAvgArray = {"product_type", "targ_avg", "actual_avg", "bias_price", "bias_per"};
                            //插入去化均价行
                            sheetAt.shiftRows(6, sheetAt.getLastRowNum(), shiftNumber, true, false);
                            for (int i = 6; i < 6 + shiftNumber; i++) {
                                XSSFRow sheetAtRow = sheetAt.createRow(i);
                                Map thisDayAvgMap = thisDayAvg.get(i - 6);
                                if (i == 6) {
                                    XSSFCell cell = sheetAtRow.createCell(0);
                                    cell.setCellValue("去化均价");
                                    cell.setCellStyle(cellStyle);
                                }
                                for (int j = 1; j < 6; j++) {
                                    XSSFCell cell = sheetAtRow.createCell(j);
                                    cell.setCellValue(getFliterValue(thisDayAvgMap.get(thisDayAvgArray[j - 1]) + ""));
                                    cell.setCellStyle(cellStyle);
                                }
                                //sheetAtRow.setRowStyle(xssfRowStyle);
                            }
                            if (shiftNumber > 1) {
                                CellRangeAddress region = new CellRangeAddress(6, 5 + shiftNumber, 0, 0);
                                sheetAt.addMergedRegion(region);
                            }
                        }
                        int a = 5;
                        for (int j = 6 + thisDayAvg.size(); j <= 12 + thisDayAvg.size(); j++) {
                            a++;
                            XSSFRow sheetAtRow = sheetAt.getRow(j);
                            for (int i = 2; i < 6; i++) {
                                if (a == 6) {
                                    sheetAtRow.getCell(i).setCellValue(getFliterValue(thisDayData.get(thisDayDataArray3[i - 2]) + ""));
                                } else if (a == 7) {
                                    sheetAtRow.getCell(i).setCellValue(getFliterValue(thisDayData.get(thisDayDataArray4[i - 2]) + ""));
                                } else if (a == 8) {
                                    sheetAtRow.getCell(i).setCellValue(getFliterValue(thisDayData.get(thisDayDataArray5[i - 2]) + ""));
                                } else if (a == 9) {
                                    sheetAtRow.getCell(i).setCellValue(getFliterValue(thisDayData.get(thisDayDataArray6[i - 2]) + ""));
                                } else if (a == 10) {
                                    sheetAtRow.getCell(i).setCellValue(getFliterValue(thisDayData.get(thisDayDataArray7[i - 2]) + ""));
                                } else if (a == 11) {
                                    sheetAtRow.getCell(i).setCellValue(getFliterValue(thisDayData.get(thisDayDataArray8[i - 2]) + ""));
                                } else if (a == 12) {
                                    sheetAtRow.getCell(i).setCellValue(getFliterValue(thisDayData.get(thisDayDataArray9[i - 2]) + ""));
                                }
                            }
                        }
                        Map morrowDayData = (Map) bodyData.get("morrowDayData");
                        //首开节点
                        String[] array1 = {"invest_open_node", "rules_open_node", "estimate_open_node", "cash_open_node", "bias_open_node", "biasper_open_node"};
                        //首开物业类型
                        String[] array2 = {"invest_xreal_type", "rules_xreal_type", "estimate_xreal_type", "cash_xreal_type", "bias_xreal_type", "biasper_xreal_type"};
                        //首开取证面积(㎡)
                        String[] array3 = {"invest_take_card_area", "rules_take_card_area", "estimate_take_card_area", "cash_take_card_area", "bias_take_card_area", "biasper_take_card_area"};
                        //首开取证货值(万元)
                        String[] array4 = {"invest_take_card_value", "rules_take_card_value", "estimate_take_card_value", "cash_take_card_value", "bias_take_card_value", "biasper_take_card_value"};
                        //首开推售面积
                        String[] array5 = {"invest_push_area", "rules_push_area", "estimate_push_area", "cash_push_area", "bias_push_area", "biasper_push_area"};
                        //首开推售货值
                        String[] array6 = {"invest_push_value", "rules_push_value", "estimate_push_value", "cash_push_value", "bias_push_value", "biasper_push_value"};
                        //首开去化面积(㎡)
                        String[] array7 = {"invest_selling_area", "rules_selling_area", "estimate_selling_area", "cash_selling_area", "bias_selling_area", "biasper_selling_area"};
                        //首开去化货值
                        String[] array8 = {"invest_selling_value", "rules_selling_value", "estimate_selling_value", "cash_selling_value", "bias_selling_value", "biasper_selling_value"};

                        //首开取证货值去化率%
                        String[] array9 = {"invest_take_card_per", "rules_take_card_per", "estimate_take_card_per", "cash_take_card_per", "bias_take_card_per", "biasper_take_card_per"};

                        //创造利润率%
                        String[] array10 = {"invest_create_per", "rules_create_per", "estimate_create_per", "cash_create_per", "bias_create_per", "biasper_create_per"};
                        //整盘利润率%
                        String[] array11 = {"invest_all_per", "rules_all_per", "estimate_all_per", "cash_all_per", "bias_all_per", "biasper_all_per"};
                        //非融IRR%
                        String[] array12 = {"invest_irr", "rules_irr", "estimate_irr", "cash_irr", "bias_irr", "biasper_irr"};
                        //静态投资回收期(月)
                        String[] array13 = {"invest_payback", "rules_payback", "estimate_payback", "cash_payback", "bias_payback", "biasper_payback"};


                        if (morrowDayData != null && morrowDayData.size() > 0) {
                            int s = 15;
                            for (int i = 16 + shiftNumber; i <= 23 + shiftNumber; i++) {
                                XSSFRow row1 = sheetAt.getRow(i);
                                s++;
                                for (int j = 2; j < 8; j++) {
                                    XSSFCell cell = row1.getCell(j);
                                    if (s == 16) {
                                        System.err.println(morrowDayData.get(array1[j - 2]) + "");
                                        cell.setCellValue(getFliterValue(getFliterValue(morrowDayData.get(array1[j - 2]) + "")));
                                    }
                                    if (s == 17) {
                                        cell.setCellValue(getFliterValue(getFliterValue(morrowDayData.get(array2[j - 2]) + "")));
                                    }
                                    if (s == 18) {
                                        cell.setCellValue(getFliterValue(getFliterValue(morrowDayData.get(array3[j - 2]) + "")));
                                    }
                                    if (s == 19) {
                                        cell.setCellValue(getFliterValue(getFliterValue(morrowDayData.get(array4[j - 2]) + "")));
                                    }
                                    if (s == 20) {
                                        cell.setCellValue(getFliterValue(getFliterValue(morrowDayData.get(array5[j - 2]) + "")));
                                    }
                                    if (s == 21) {
                                        cell.setCellValue(getFliterValue(getFliterValue(morrowDayData.get(array6[j - 2]) + "")));
                                    }
                                    if (s == 22) {
                                        cell.setCellValue(getFliterValue(getFliterValue(morrowDayData.get(array7[j - 2]) + "")));
                                    }
                                    if (s == 23) {
                                        cell.setCellValue(getFliterValue(getFliterValue(morrowDayData.get(array8[j - 2]) + "")));
                                    }
                                }
                            }
                        }
                        int Number = 0;
                        int b=23 + shiftNumber;
                        List<Map> morrowDayAvg = (List<Map>) bodyData.get("morrowDayAvg");
                        if (morrowDayAvg != null && morrowDayAvg.size() > 0) {
                            Number = morrowDayAvg.size();
                            //去化均价数据
                            String[] morrowDayAvgArray = {"product_type", "invest_avg", "rules_avg", "estimate_price", "cash_price","bias_price","bias_per"};
                            //插入去化均价行
                            sheetAt.shiftRows(23 + shiftNumber, sheetAt.getLastRowNum(), Number, true, false);

                            for (int i =b; i < b+morrowDayAvg.size(); i++) {
                                XSSFRow sheetAtRow = sheetAt.createRow(i);
                                Map morrowDayAvgMap = morrowDayAvg.get(i - b);
                                if (i == b) {
                                    XSSFCell cell = sheetAtRow.createCell(0);
                                    cell.setCellValue("首开均价(元/㎡)");
                                    cell.setCellStyle(cellStyle);
                                }
                                for (int j = 1; j < 8; j++) {
                                    XSSFCell cell = sheetAtRow.createCell(j);
                                   cell.setCellValue(getFliterValue(morrowDayAvgMap.get(morrowDayAvgArray[j - 1]) + ""));
                                    cell.setCellStyle(cellStyle);
                                }
                                sheetAtRow.setRowStyle(xssfRowStyle);
                            }
                            if (Number > 1) {
                                CellRangeAddress region = new CellRangeAddress(b, b+Number-1, 0, 0);
                                sheetAt.addMergedRegion(region);
                            }


                        }

                        int sd = b + Number +1;
                        int sh=0;
                        for (int i=sd;i<sd+5;i++){
                            XSSFRow row1 = sheetAt.getRow(i);
                            sh++;
                            for (int j = 2; j < 8; j++) {
                                XSSFCell cell = row1.getCell(j);
                                if (sh == 1) {
                                    cell.setCellValue(getFliterValue(getFliterValue(morrowDayData.get(array9[j - 2]) + "")));
                                }
                                if (sh == 2) {
                                    cell.setCellValue(getFliterValue(getFliterValue(morrowDayData.get(array10[j - 2]) + "")));
                                }
                                if (sh == 3) {
                                    cell.setCellValue(getFliterValue(getFliterValue(morrowDayData.get(array11[j - 2]) + "")));
                                }
                                if (sh == 4) {
                                    cell.setCellValue(getFliterValue(getFliterValue(morrowDayData.get(array12[j - 2]) + "")));
                                }
                                if (sh == 5) {
                                    cell.setCellValue(getFliterValue(getFliterValue(morrowDayData.get(array13[j - 2]) + "")));
                                }
                            }

                        }

                        int bs=sd+5+2;
                        XSSFRow row1 = sheetAt.getRow(bs);
                        XSSFCell cell1 = row1.getCell(1);
                        cell1.setCellValue(getFliterValue(morrowDayData.get("content_bazaar")+""));
                        CellRangeAddress region = new CellRangeAddress(bs, bs+1, 0, 0);
                        sheetAt.addMergedRegion(region);

                        CellRangeAddress region2 = new CellRangeAddress(bs, bs+1, 1, 7);
                        sheetAt.addMergedRegion(region2);


                        System.out.println(bs+1);
                        bs=bs+3;
                        XSSFRow row2 = sheetAt.getRow(bs);
                        XSSFCell row2Cell = row2.getCell(1);
                        row2Cell.setCellValue(getFliterValue(morrowDayData.get("content_team")+""));
                        CellRangeAddress region3 = new CellRangeAddress(bs, bs+1, 0, 0);
                        sheetAt.addMergedRegion(region3);

                        CellRangeAddress region4 = new CellRangeAddress(bs, bs+1, 1, 7);
                        sheetAt.addMergedRegion(region4);





                        bs=bs+3;
                        XSSFRow row3 = sheetAt.getRow(bs);
                        XSSFCell row3Cell = row3.getCell(1);
                        row3Cell.setCellValue(getFliterValue(morrowDayData.get("content_product")+""));
                        CellRangeAddress region5 = new CellRangeAddress(bs, bs+1, 0, 0);
                        sheetAt.addMergedRegion(region5);

                        CellRangeAddress region6 = new CellRangeAddress(bs, bs+1, 1, 7);
                        sheetAt.addMergedRegion(region6);




                        bs=bs+3;
                        XSSFRow rows = sheetAt.getRow(bs);
                        XSSFCell row5Cell = rows.getCell(1);
                        row5Cell.setCellValue(getFliterValue(morrowDayData.get("content_policy")+""));
                        CellRangeAddress region7 = new CellRangeAddress(bs, bs+1, 0, 0);
                        sheetAt.addMergedRegion(region7);

                        CellRangeAddress region8 = new CellRangeAddress(bs, bs+1, 1, 7);
                        sheetAt.addMergedRegion(region8);
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String formatDate = sdf.format(new Date());
                    String fileName = projectName + "-" + levelNode + "-" + formatDate + ".xlsx";
                    response.setContentType("application/vnd.ms-excel;charset=utf-8");
                    response.setCharacterEncoding("UTF-8");
                    response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
                    workbook.write(response.getOutputStream());
                    response.getOutputStream().flush();
                } else {
                    return ResultBody.error(-1065, "没有查询到当前节点的数据版本，请先保存/提交!");
                }
            } else {
                return ResultBody.error(-1065, "查询数据失败!" + resultBody.getMessages());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(-1096, "导出Excel失败:" + e.toString());
        }

        return ResultBody.success(null);
    }

    public String getFliterValue(String oldValue) {
        if (oldValue != null) {
            if ("".equals(oldValue) || "null".equals(oldValue)) {
                return "0";
            } else {
                return oldValue;
            }
        } else {
            return "0";
        }
    }
    //过滤前端传递的参数map
    public Map filterMap(Map<String, Object> map) {
        Map<Object, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String value = entry.getValue() + "";
            if (!"".equals(value) && !"null".equalsIgnoreCase(value)) {
                resultMap.put(entry.getKey(), entry.getValue());
            }
        }
        return resultMap;
    }

}
