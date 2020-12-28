package cn.visolink.firstplan.opening.service.impl;
import cn.visolink.firstplan.message.dao.TemplateEnginedao;
import cn.visolink.firstplan.message.service.impl.TemplateEngineServiceImpl;
import cn.visolink.firstplan.opening.dao.OpeningDao;
import cn.visolink.firstplan.opening.service.OpenThisDayService;
import cn.visolink.utils.CommUtils;
import cn.visolink.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * <p>
 * OpenThisDay服务实现类
 * </p>
 *
 * @author autoJob
 * @since 2020-02-19
 */
@Service
public class OpenThisDayServiceImpl implements OpenThisDayService {

    @Autowired
    private OpeningDao openingDao;


    @Autowired
    private TemplateEnginedao templateEnginedao;

    @Autowired
    private TemplateEngineServiceImpl templateEngineService;

    @Override
    public int updatePlanNodeFlowApproval(String id) {
        return openingDao.updatePlanNodeFlowApproval(id);
    }


    @Override
    public int updatePlanThisNodeById(Map params) {
        return openingDao.updatePlanThisNodeById(params);
    }

    @Override
    public int insertOpenThisDay(Map params) {
        //当日播报表
        Map thisDayInfo = (Map) params.get("paramData");
        openingDao.insertOpenThisDay(thisDayInfo);
        //首开均价
        List thisDayAvg = (List) thisDayInfo.get("avg");
        if (thisDayAvg != null && thisDayAvg.size() > 0) {
            Map avgParams = new HashMap();
            avgParams.put("plan_id", thisDayInfo.get("plan_id"));
            avgParams.put("plan_node_id", thisDayInfo.get("plan_node_id"));
            avgParams.put("list", thisDayAvg);
            openingDao.insertOpenAvg(avgParams);
        }
        return 0;
    }

    @Override
    public int updateOpenThisDay(Map params) {
        Map thisDayInfo = (Map) params.get("paramData");
        //修改当日播报信息
        openingDao.updateOpenThisDay(thisDayInfo);
        //删除首开均价
        openingDao.delOpenAvgByPlanNodeId(thisDayInfo.get("plan_node_id") + "");
        //写入首开均价
        List thisDayAvg = (List) thisDayInfo.get("avg");
        if (thisDayAvg != null && thisDayAvg.size() > 0) {
            Map avgParams = new HashMap();
            avgParams.put("plan_id", thisDayInfo.get("plan_id"));
            avgParams.put("plan_node_id", thisDayInfo.get("plan_node_id"));
            avgParams.put("list", thisDayAvg);
            openingDao.insertOpenAvg(avgParams);
        }
        return 0;
    }

    @Override
    public Map selectOpenThisDayInfo(Map params) {
        Map res = openingDao.selectOpenThisDayByNodeId(params.get("plan_node_id") + "");
        if (res != null && res.size() > 0) {
            res.put("avg", openingDao.selectOpenThisDayAvgByNodeId(params.get("plan_node_id") + ""));
            //顶设1业态
            List<Map> dseAvg = openingDao.selectDesignoneValueByPlanId(params.get("plan_id") + "");
            res.put("dseAvg", dseAvg);
        }

        return res;
    }

    /**
     * 新增首开次日播报
     *
     * @param params
     * @return
     */
    @Override
    public int insertOpenMorrowBroadcast(Map params) {
        //版本
        Integer version = openingDao.getOpenMorrowBroadcastByPlanIdVersionNum(params.get("plan_id") + "") + 1;
        params.put("version", version);
        openingDao.insertOpenMorrowBroadcast(params);
        //首开均价
        List<Map> thisDayAvg = (List) params.get("avg");
        if (thisDayAvg != null && thisDayAvg.size() > 0) {
            Map avgParams = new HashMap();
            avgParams.put("plan_id", params.get("plan_id"));
            avgParams.put("plan_node_id", params.get("plan_node_id"));
            avgParams.put("list", thisDayAvg);
            avgParams.put("morrow_id", params.get("id"));
            openingDao.insertOpenMorrowAvg(avgParams);
        }
        return 0;
    }

    /**
     * 修改首开次日播报
     *
     * @param params
     * @return
     */
    @Override
    public int updateOpenMorrowBroadcast(Map params) {
        //修改当日播报信息
        openingDao.updateOpenMorrowBroadcast(params);
        //删除首开均价
        String id = params.get("id") + "";
        openingDao.delOpenMorrowByPlanNodeId(id);
        //写入首开均价
        List<Map> thisDayAvg = (List) params.get("avg");
        if (thisDayAvg != null && thisDayAvg.size() > 0) {
            Map avgParams = new HashMap();
            avgParams.put("plan_id", params.get("plan_id"));
            avgParams.put("plan_node_id", params.get("plan_node_id"));
            avgParams.put("list", thisDayAvg);
            avgParams.put("morrow_id", id);
            openingDao.insertOpenMorrowAvg(avgParams);
        }
        return 0;
    }

    @Override
    public Map getOpenPageInfo(Map params) {
        DecimalFormat dF = new DecimalFormat("0.00");
        DecimalFormat dFNo = new DecimalFormat("0");
        Map res = new HashMap();
        //操盘手
        String project_id = params.get("project_id") + "";
        Map trader = openingDao.getTraderByProjectId(project_id);
        if (trader != null && trader.size() > 0) {
            res.put("project_duty", trader.get("trader"));
        }
        //获取 去化情况 目标（顶设2）
        Map designtwo = openingDao.selectdesigntwoSellCase(params.get("plan_id") + "");

        if (designtwo == null) {
            return res;
        }
        res.putAll(designtwo);
        //获取 去化情况 实际
        Map orderParams = new HashMap();
        orderParams.put("project_id", project_id);
        //前七天预估开盘时间
        String openTime = designtwo.get("estimate_open_node") + "";
        //如果开盘数据已经保存  那么首开时间去首开当日的
        Map map = openingDao.selectLastOpening(params.get("plan_id") + "");
        if (map != null && map.get("open_time") != null) {
            openTime = map.get("open_time") + "";
        }

        if (openTime.equals("null")) {
            openTime = "";
        }
        String endTime = "";
        if (openTime != null && openTime.length() >= 10) {
            endTime = openTime.substring(0, 10) + " 06:00:00";
        }
        res.put("open_time", openTime);
        orderParams.put("end_time", endTime);
        orderParams.put("start_time", openTime);
        Map buildingSell = openingDao.getOrderByProjectIdPriceNum(orderParams);
        //实际去化套数
        int acNum = 0;
        if (buildingSell != null) {
            acNum = buildingSell.get("num") == null ? 0 : Integer.parseInt(buildingSell.get("num") + "");
            res.put("actual_selling_num", acNum);
            res.put("actual_selling_value", dF.format(buildingSell.get("price")));
        } else {
            res.put("actual_selling_num", 0);
            res.put("actual_selling_value", 0);
        }
        //实际累计来访(组)累计大卡(组)大卡转化率成交率
        Map acparams = new HashMap();
        acparams.put("project_id", project_id);
        acparams.put("endTime", openTime);
        Map guest = openingDao.getGuestStorageSum(acparams);
        if (guest != null) {
            int visitCntTotal = Integer.parseInt(guest.get("visitCnt") + "");
            res.put("actual_add_visit", visitCntTotal);
            res.put("actual_add_big", guest.get("big_card"));
            Integer big_card = Integer.parseInt(guest.get("big_card") + "");
            if (big_card != null && big_card != 0) {
                res.put("actual_add_big_per", dF.format(((float) acNum / big_card) * 100));//guest.get("bigPer")
            } else {
                res.put("actual_add_big_per", 0);
            }
            if (visitCntTotal != 0) {
                res.put("actual_finish", dF.format((float) acNum / visitCntTotal * 100));//guest.get("cjper")
            } else {
                res.put("actual_finish", 0);
            }
        } else {
            res.put("actual_add_visit", 0);
            res.put("actual_add_big", 0);
            res.put("actual_add_big_per", 0);
            res.put("actual_finish", 0);
        }
        acparams.put("startTime", openTime);
        //当天到场客户量(组)
        Map thisDay = openingDao.getGuestSum(acparams);
        if (thisDay != null) {

            //res.put("actual_this_client",thisDay.get("visitCnt"));
            int visitCnt = Integer.parseInt(thisDay.get("visitCnt") + "");
            if (visitCnt != 0) {
                res.put("actual_this_clientper", dF.format((float) acNum / visitCnt * 100));
            } else {
                res.put("actual_this_clientper", 0);
            }
        } else {
            // res.put("actual_this_client",0);
            res.put("actual_this_clientper", 0);
        }

        //顶设2平均价格
        List<Map> designtwoAvg = openingDao.selectdesigntwoAvg(designtwo.get("node_id") + "");
        if (designtwoAvg != null && designtwoAvg.size() > 0) {
            //实际去化均价
            for (Map data : designtwoAvg) {
                Map actualParams = new HashMap();
                actualParams.put("project_id", project_id);
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
                    bias_per = "0";
                } else {
                    bias_per = dF.format((bias_price / targ_avg * 100));
                }
                data.put("targ_avg", dF.format(targ_avg));
                data.put("actual_avg", dF.format(priceavg));
                data.put("bias_price", dF.format(bias_price));
                data.put("bias_per", bias_per);
            }
        }
        //目标
        int designtwo_selling_num = Integer.parseInt(res.get("designtwo_selling_num") == null ? "0" : res.get("designtwo_selling_num") + "");
        double designtwo_selling_value = Double.parseDouble(res.get("designtwo_selling_value") == null ? "0" : res.get("designtwo_selling_value") + "");
        double designtwo_selling_takeper = Double.parseDouble(res.get("designtwo_selling_takeper") == null ? "0" : res.get("designtwo_selling_takeper") + "");
        int designtwo_add_visit = Integer.parseInt(res.get("designtwo_add_visit") == null ? "0" : res.get("designtwo_add_visit") + "");
        int designtwo_add_big = Integer.parseInt(res.get("designtwo_add_big") == null ? "0" : res.get("designtwo_add_big") + "");
        int designtwo_this_client = Integer.parseInt(res.get("designtwo_this_client") == null ? "0" : res.get("designtwo_this_client") + "");
        double designtwo_this_clientper = Double.parseDouble(res.get("designtwo_this_clientper") == null ? "0" : res.get("designtwo_this_clientper") + "");
        double designtwo_finish = Double.parseDouble(res.get("designtwo_finish") == null ? "0" : res.get("designtwo_finish") + "");
        //实际
        int actual_selling_num = Integer.parseInt(res.get("actual_selling_num") == null ? "0" : res.get("actual_selling_num") + "");
        double actual_selling_value = Double.parseDouble(res.get("actual_selling_value") == null ? "0" : res.get("actual_selling_value") + "");
        double actual_selling_takeper = Double.parseDouble(res.get("actual_selling_takeper") == null ? "0" : res.get("actual_selling_takeper") + "");
        int actual_add_visit = Integer.parseInt(res.get("actual_add_visit") == null ? "0" : res.get("actual_add_visit") + "");
        int actual_add_big = Integer.parseInt(res.get("actual_add_big") == null ? "0" : res.get("actual_add_big") + "");
        double actual_add_big_per = Double.parseDouble(res.get("actual_add_big_per") == null ? "0" : res.get("actual_add_big_per") + "");
        int actual_this_client = Integer.parseInt(res.get("actual_this_client") == null ? "0" : res.get("actual_this_client") + "");
        double actual_this_clientper = Double.parseDouble(res.get("actual_this_clientper") == null ? "0" : res.get("actual_this_clientper") + "");

        double actual_finish = Double.parseDouble(res.get("actual_finish") == null ? "0" : res.get("actual_finish") + "");
        //偏差
        double bias_selling_num = designtwo_selling_num - (double)actual_selling_num;
        double bias_selling_value = designtwo_selling_value - actual_selling_value;
        double bias_selling_takeper = designtwo_selling_takeper - actual_selling_takeper;
        Integer bias_add_visit = designtwo_add_visit - actual_add_visit;
        Integer bias_add_big = designtwo_add_big - actual_add_big;

        Integer bias_this_client = designtwo_this_client - actual_this_client;
        double bias_this_clientper = designtwo_this_clientper - actual_this_clientper;
        double bias_finish = designtwo_finish - actual_finish;
        res.put("bias_selling_num", dF.format(bias_selling_num));
        res.put("bias_selling_value", dF.format(bias_selling_value));
        res.put("bias_selling_takeper", dF.format(bias_selling_takeper));
        res.put("bias_add_visit", dF.format(bias_add_visit));
        res.put("bias_add_big", dF.format(bias_add_big));

        res.put("bias_this_client", dF.format(bias_this_client));
        res.put("bias_this_clientper", dF.format(bias_this_clientper));
        res.put("bias_finish", dF.format(bias_finish));
        //偏差率

        System.err.println(designtwo_selling_num);
        System.err.println(designtwo_add_big);
        if(designtwo_add_big!=0){
            res.put("designtwo_add_big_per",dF.format(((float)designtwo_selling_num/(float) designtwo_add_big)*100));
        }
        double designtwo_add_big_per = Double.parseDouble(res.get("designtwo_add_big_per") == null ? "0" : res.get("designtwo_add_big_per") + "");
        double bias_add_big_per = designtwo_add_big_per - actual_add_big_per;
        res.put("bias_add_big_per", dF.format(bias_add_big_per));
        System.out.println(res.get("designtwo_add_big_per"));
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
        if (designtwo_add_big_per != 0) {
            res.put("biasper_add_big_per", dF.format((float) (bias_add_big_per / designtwo_add_big_per) * 100));
        } else {
            res.put("biasper_add_big_per", 0);
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
        res.put("avg", designtwoAvg);

        //顶设1业态
        res.put("dseAvg", openingDao.selectDesignoneValueByPlanId(params.get("plan_id") + ""));
        return res;
    }

    @Override
    public List selectProductTypeByPlanId(String plan_id) {
        return openingDao.selectProductTypeByPlanId(plan_id);
    }

    @Override
    public Map initOperMorrow(String plan_id) {
        Map resMap = new HashMap();
        //投资版 战规版 首开前7天预估版
        Map info = openingDao.selectSevenDayIndex(plan_id);
        //获取开盘当日的取证货值去化率
        Map thisDayData = openingDao.getThisDayData(plan_id);
        if (thisDayData != null) {
            info.putAll(thisDayData);
        }
        //openingDao.getThisDayData();

        if (info != null) {
            //首开七天首开均价
            info.put("avg", openingDao.selectSevenDayAvg(info.get("dayId") + ""));
        }
        resMap.put("info", info);
        return resMap;
    }

    @Override
    public Map selectOpenMorrowDayInfo(Map params) {
        Map morrowDay = openingDao.selectOpenMorrowDayByPlanId(params);
        if (morrowDay != null) {
            List avg = openingDao.selectOpenMorrowDayAvgById(morrowDay.get("id") + "");
            if (avg != null && avg.size() > 0) {
                morrowDay.put("avg", avg);
            }
        }
        return morrowDay;
    }

    /**
     * 首开周拆分
     *
     * @param params
     * @return
     */
    @Override
    public Map getWeekSplit(Map params) {
        Map res = new HashMap();
        Map planInfo = openingDao.selectPlanByIdInfo(params.get("plan_id") + "");
        if (planInfo == null) {
            return res;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startdate = (Date) planInfo.get("designtwo_time");
        Date emddate = (Date) planInfo.get("open_time");
        // 1.开始时间 2019-06-09 13:16:04
        Long startTime = startdate.getTime();
        // 2.结束时间 2019-07-09 13:16:04
        Long endTime = emddate.getTime();
        // 3.开始时间段区间集合
        List<Long> beginDateList = new ArrayList<Long>();
        // 4.结束时间段区间集合
        List<Long> endDateList = new ArrayList<Long>();
        // 5.调用工具类
        DateUtil.getIntervalTimeByWeek(startTime, endTime, beginDateList, endDateList);
        // 6.打印输出
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List resList = new ArrayList();
        int visitTotal = 0;
        String plan_node_id = params.get("plan_node_id") + "";
        String plan_id = params.get("plan_id") + "";
        for (int i = 0; i < beginDateList.size(); i++) {
            int ii = i + 1;
            Map resData = new HashMap();
            resData.put("weekNum", "第" + CommUtils.arabicNumToChineseNum(ii) + "周");
            Long beginStr = beginDateList.get(i);
            if (endDateList == null || endDateList.size() == 0) {
                endDateList = beginDateList;
            }
            Long endStr = endDateList.get(i);
            String begin1 = sdf.format(new Date(beginStr));
            String end1 = sdf.format(new Date(endStr));
            resData.put("startTime", begin1);
            resData.put("endTime", end1);
            //获取时间段来访人数
            params.put("startTime", begin1);
            params.put("endTime", end1);
            Integer visitNum = openingDao.getAddVisit(params);
            resData.put("visitNum", visitNum);
            visitTotal = visitTotal + visitNum;
            resData.put("visitTotal", visitTotal);
            resData.put("plan_node_id", plan_node_id);
            resData.put("plan_id", plan_id);
            resData.put("day_date", begin1 + "-" + end1);
            resList.add(resData);
        }
        res.put("week", resList);
        res.put("endTime", sdf.format(emddate));
        return res;
    }

    @Override
    public int insertPlanNodeOpen(Map params) {
        return openingDao.insertPlanNodeOpen(params);
    }

    @Override
    public Map selectOpenThisDayByNodeId(String plan_node_id) {
        return openingDao.selectOpenThisDayByNodeId(plan_node_id);
    }

    @Override
    public int getWeekSplitList(Map params) {
        return 0;
    }

    @Override
    public Map initGuestStorage(Map params) {
        Map res = new HashMap();
        int isSave = 1;
        //节点流程信息
        String flowId = null;
        Map flow = openingDao.getGuestStorageFlowByPlanId(params);
        String createParams = params.get("create") + "";
        if (flow == null || flow.size() == 0 || createParams.equals("new")) {
            isSave = 0;
            params.put("node_level", "7");
            //前7天
            Map seven = openingDao.getGuestStorageFlowByPlanId(params);
            if (seven != null && seven.size() > 0) {
                flowId = seven.get("id") + "";
            }
        } else {
            res.put("flowInfo", flow);
            if (flow.get("approval_stuat").equals(1)) {
                isSave = 0;
            }
            flowId = flow.get("id") + "";
            //版本
            List version = openingDao.getGuestStorageFlowByPlanIdVersion(params.get("plan_id") + "");
            res.put("version", version);
        }

        List<Map> sevenNode = openingDao.getGuestStorageNodePlanByFlowId(flowId);
        for (Map data : sevenNode) {
            if (isSave == 0) {
                data.remove("id");
            }
            if (data.get("level").equals(2)) {
                params.put("endTime", data.get("node_time"));
                Map guest = openingDao.getGuestStorageSum(params);
                if (guest != null && guest.size() > 0) {
                    data.put("report_num", guest.get("reportCnt"));
                    data.put("visit_num", guest.get("visitCnt"));
                    data.put("little_num", guest.get("lesser_card"));
                    data.put("little_per", guest.get("lesserPer"));
                    data.put("big_num", guest.get("big_card"));
                    data.put("big_per", guest.get("bigPer"));
                    data.put("sub_num", guest.get("subscribe_num"));
                    data.put("make_per", guest.get("cjper"));
                }
            }
        }
        List<Map> sevenWeek = openingDao.getGuestStorageWeekByFlowId(flowId);
        if (sevenWeek != null && sevenWeek.size() > 0) {
            //计划累计
            Integer plan_total = Integer.parseInt(sevenWeek.get(sevenWeek.size() - 1).get("plan_total") + "");
            Map weekParams = new HashMap();
            Integer actual_total = 0;
            for (Map data : sevenWeek) {
                if (isSave == 0) {
                    data.remove("id");
                }
                weekParams.put("startTime", data.get("start_time"));
                weekParams.put("endTime", data.get("end_time"));
                weekParams.put("project_id", params.get("project_id"));
                Integer total = openingDao.getAddVisit(weekParams);
                data.put("plan_node_id", params.get("plan_node_id"));
                data.put("actual_add", total);
                actual_total = actual_total + total;
                data.put("actual_total", actual_total);
                data.put("actual_task_per", Math.ceil(((double) actual_total / plan_total) * 100));
                Integer plan_add = data.get("plan_add") == null ? 0 : Integer.parseInt(data.get("plan_add") + "");
                Integer planTotal = data.get("plan_total") == null ? 0 : Integer.parseInt(data.get("plan_total") + "");
                Integer week = plan_add - total;
                Integer weekPlanTotal = planTotal - actual_total;
                double weekTotal=0;
                if(planTotal!=0){
                    weekTotal= ((double) planTotal - actual_total) / (double)planTotal;
                }
                data.put("week_bais_value", week);
                data.put("bias_value", weekPlanTotal);
                data.put("bias_per", weekTotal);
            }
        }

        res.put("isSave", isSave);
        res.put("monthNode", sevenNode);
        res.put("weekNode", sevenWeek);
        return res;
    }

    @Override
    public Map selectGuestStorageFlow(String flow_id) {
        Map res = new HashMap();
        List sevenNode = openingDao.getGuestStorageNodePlanByFlowId(flow_id);
        List sevenWeek = openingDao.getGuestStorageWeekByFlowId(flow_id);
        res.put("isSave", 1);
        res.put("monthNode", sevenNode);
        res.put("weekNode", sevenWeek);
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertGuestStorage(Map params) {
        String plan_id = null;
        String plan_node_id = null;
        String id = params.get("flow_id") + "";
        String approval_stuat = "2";
        //暂存
        if (params.get("button").equals("save")) {
            approval_stuat = "1";
        }
        if (id.equals("null") || id.equals("")) {
            plan_id = params.get("plan_id") + "";
            plan_node_id = params.get("plan_node_id") + "";
            id = UUID.randomUUID() + "";
            Map flowParams = new HashMap();
            flowParams.put("id", id);
            flowParams.put("plan_id", plan_id);
            flowParams.put("plan_node_id", plan_node_id);
            flowParams.put("node_level", "8");
            flowParams.put("approval_stuat", approval_stuat);
            flowParams.put("version", openingDao.getGuestStorageFlowByPlanIdVersionNum(plan_id) + 1);
            openingDao.insertGuestStorageFlow(flowParams);
        } else {
            //删除之前的
            openingDao.deleteNodeOrWeek(id);
            Map appr = new HashMap();
            appr.put("id", id);
            appr.put("approval_stuat", approval_stuat);
            openingDao.updateGuestStorageFlowApprovalStuat(appr);
        }
        List<Map> weekNode = (List) params.get("weekNode");
        List<Map> monthNode = (List) params.get("monthNode");
        //节点
        if (weekNode != null) {
            Map node = new HashMap();
            node.put("plan_id", plan_id);
            node.put("plan_node_id", plan_node_id);
            node.put("flow_id", id);
            node.put("list", monthNode);
            node.put("node_level", "8");
            openingDao.insertGuestStorageNodePlan(node);
        }
        if (weekNode != null) {
            //周拆分
            Map week = new HashMap();
            week.put("plan_id", plan_id);
            week.put("plan_node_id", plan_node_id);
            week.put("flow_id", id);
            week.put("node_level", "8");
            week.put("list", weekNode);
            openingDao.insertGuestStorageWeek(week);
        }
             /*
                String plan_id=params.get("plan_id")+"";
                String id=UUID.randomUUID()+"";
                Map flowParams=new HashMap();
                flowParams.put("id",id);
                flowParams.put("plan_id",plan_id);
                flowParams.put("plan_node_id",params.get("plan_node_id"));
                flowParams.put("node_level","8");
                flowParams.put("version",openingDao.getGuestStorageFlowByPlanIdVersionNum(plan_id));
                Integer count=openingDao.insertGuestStorageFlow(flowParams);
                //节点流程信息
                String flowId=null;
                        params.put("node_level","7");
                        //前7天
                        Map seven=openingDao.getGuestStorageFlowByPlanId(params);
                        if(seven!=null && seven.size()>0){
                                flowId=seven.get("id")+"";
                        }
                List<Map> sevenNode=openingDao.getGuestStorageNodePlanByFlowId(flowId);
                for(Map data:sevenNode){
                        if(data.get("level").equals(2)){
                                params.put("endTime",data.get("node_time"));
                                Map guest=openingDao.getGuestStorageSum(params);
                                if(guest!=null &&  guest.size()>0){
                                        data.put("report_num",guest.get("reportCnt"));
                                        data.put("visit_num",guest.get("visitCnt"));
                                        data.put("little_num",guest.get("lesser_card"));
                                        data.put("little_per",guest.get("lesserPer"));
                                        data.put("big_num",guest.get("big_card"));
                                        data.put("big_per",guest.get("bigPer"));
                                        data.put("sub_num",guest.get("subscribe_num"));
                                        data.put("make_per",guest.get("cjper"));
                                }
                        }
                }
                //节点
                openingDao.insertGuestStorageNodePlan(sevenNode);
                List<Map> sevenWeek=openingDao.getGuestStorageWeekByFlowId(flowId);
                if(sevenWeek!=null && sevenWeek.size()>0){
                        //计划累计
                        Integer plan_total=Integer.parseInt(sevenWeek.get(sevenWeek.size()-1).get("plan_total")+"");
                        Map weekParams=new HashMap();
                        Integer actual_total=0;
                        for(Map data:sevenWeek){
                                weekParams.put("startTime",data.get("start_time"));
                                weekParams.put("endTime",data.get("end_time"));
                                weekParams.put("project_id",params.get("project_id"));
                                Integer total=openingDao.getAddVisit(weekParams);
                                data.put("plan_node_id",params.get("plan_node_id"));
                                data.put("actual_add",total);
                                actual_total=actual_total+total;
                                data.put("actual_total",actual_total);
                                data.put("actual_task_per",Math.ceil((actual_total/plan_total)*100));
                                Integer plan_add=data.get("plan_add")==null?0:Integer.parseInt(data.get("plan_add")+"");
                                Integer planTotal=data.get("plan_total")==null?0:Integer.parseInt(data.get("plan_total")+"");
                                Integer week=plan_add-total;
                                Integer weekPlanTotal=planTotal-actual_total;
                                Integer weekTotal=(planTotal-actual_total)/planTotal;
                                data.put("week_bais_value",week);
                                data.put("bias_value",weekPlanTotal);
                                data.put("bias_per",weekTotal);
                        }
                }*/

        return 0;
    }

    @Override
    public List getOpenMorrowBroadcastByPlanId(String plan_id) {
        return openingDao.getOpenMorrowBroadcastByPlanId(plan_id);
    }

    @Override
    public Map getOpenMorrowDayIByIdnfo(String id) {
        Map resMap = new HashMap();
        Map seven = openingDao.selectOpenMorrowDayById(id);
        if (seven != null && seven.size() > 0) {
            //首开七天首开均价
            seven.put("avg", openingDao.selectOpenMorrowDayAvgById(id));
        }
        resMap.put("info", seven);
        return resMap;
    }

    @Override
    public List getOpenVersionByPlanId(String plan_id) {
        return openingDao.selectPlanNodeByPlanIdAndNodeLevle(plan_id);
    }

    @Override
    public List selectPlanNodeVersionByPlanId(Map params) {
        return openingDao.selectPlanNodeVersionByPlanId(params);
    }

    @Override
    public List selectBuildingName(String project_id) {
        return openingDao.selectBuildingName(project_id);
    }

    /**
     * 审批完成修改开盘信息
     *
     * @return
     */
    @Override
    public Map approveOpenNodeInfo(Map params) {
        //首日开播
        //主键ID
        String jsonId = params.get("businesskey") + "";//主表Id
        String flowKey = params.get("flowKey") + "";//模板
        String eventType = params.get("eventType") + "";//审批状态
        String orgName = params.get("orgName") + "";//流程
        String project_id = params.get("project_id") + "";//项目ID
        String plan_id = params.get("plan_id") + "";//项目ID
        if (orgName.equals("fp_open_morrow")) {
            if (eventType.equals("3")) {
                Map morrow = new HashMap();
                morrow.put("id", jsonId);
                morrow.put("plan_approval", eventType);
                openingDao.updateMorrowFlowApprovalStatus(morrow);
            } else if (eventType.equals("4")) {
                //次日
                openingDao.updateMorrowFlowApproval(jsonId);
            } else if ("5".equals(eventType) || "6".equals(eventType)) {
                Map morrow = new HashMap();
                morrow.put("id", jsonId);
                morrow.put("plan_approval", 2);
                openingDao.updateMorrowFlowApprovalStatus(morrow);
            }
        } else {
            if (eventType.equals("3")) {
                //审批中
                Map thisday = new HashMap();
                thisday.put("id", jsonId);
                thisday.put("plan_approval", eventType);
                openingDao.updatePlanNodeFlowApprovalStatus(thisday);
            } else if (eventType.equals("4")) {
                //修改审批状态
                openingDao.updatePlanNodeFlowApproval(jsonId);
                if (orgName.equals("fp_open")) {
                    Map thisNode = new HashMap();
                    thisNode.put("project_id", project_id);
                    thisNode.put("node_level", "9");
                    thisNode.put("plan_id", plan_id);
                    if (project_id.equals("") || project_id.equals("null")) {
                        openingDao.updatePlanThisNodeByPlanId(thisNode);
                    } else {
                        openingDao.updatePlanThisNodeByProjectId(thisNode);
                    }
                }
            } else if ("5".equals(eventType) || "6".equals(eventType)) {
                //审批中
                Map thisday = new HashMap();
                thisday.put("id", jsonId);
                thisday.put("plan_approval", 2);
                openingDao.updatePlanNodeFlowApprovalStatus(thisday);
            }

        }
        return null;
    }

    @Override
    public Map selectOpenFileFlow(String plan_node_id) {
        return openingDao.selectOpenFileFlow(plan_node_id);
    }

    @Override
    public Map selectFlowMove(String plan_id) {
        return openingDao.selectFlowMove(plan_id);
    }

    @Override
    public String selectProjectNameById(String projectId) {
        return openingDao.selectProjectNameById(projectId);
    }

    @Override
    public Map selectLastOpeningService(Map map) {
        Map m = openingDao.selectLastOpening(map.get("plan_id") + "");
        return m;
    }

    /**
     * 生成待发送消息到消息推送列表
     *
     * @param map
     * @return
     */
    @Override
    public Map generateMessage(Map map) {
        Map planNodeMap = templateEnginedao.getPlanNodeInfoByPlanNodeId(map);
        map.put("node_level", 10);
        map.put("plan_id",planNodeMap.get("plan_id"));
        Map threeDataView = templateEngineService.threeDataView(map);
        if(threeDataView!=null){
            planNodeMap.putAll(threeDataView);
        }
        if(planNodeMap!=null&&planNodeMap.size()>0){
           String open_countdown= planNodeMap.get("open_countdown")+"";
           if(!"".equals(open_countdown)&&!"null".equals(open_countdown)){
               int parseInt = Integer.parseInt(open_countdown);
               if(parseInt<0){
                   planNodeMap.put("open_countdown","已逾期"+-parseInt);
               }
           }
        }
        //获取对应的模版
        map.put("template_name","三大件开放提醒");
        //查询对应的模版
        Map templateInfo = templateEnginedao.getTemplateInfo(map);
        if (templateInfo != null) {
            //获取模版标题
            String template_title = templateInfo.get("template_title") + "";
            //获取模版内容
            String template_info = templateInfo.get("template_info") + "";
            //替换模版变量，返回替换后的模版信息
            Map resultMap = templateEngineService.replaceData(planNodeMap, template_title, template_info);
            if (resultMap != null) {
                //生成消息
                templateEngineService.messageGeneration(resultMap, templateInfo, planNodeMap.get("project_id")+"");
            }
        }
        return null;
    }

    @Override
    public Map queryOpenThisDayIsSubmit(Map map) {
        Map dayIsSubmit = openingDao.queryOpenThisDayIsSubmit(map.get("plan_id")+"");
        return dayIsSubmit;
    }


}
