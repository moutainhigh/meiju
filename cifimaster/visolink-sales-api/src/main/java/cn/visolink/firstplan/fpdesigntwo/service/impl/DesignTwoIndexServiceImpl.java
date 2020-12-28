package cn.visolink.firstplan.fpdesigntwo.service.impl;


import cn.visolink.common.security.security.JwtUser;
import cn.visolink.common.security.service.JwtUserDetailsServiceImpl;
import cn.visolink.firstplan.TaskLand.dao.TakeLandDao;
import cn.visolink.firstplan.TaskLand.service.impl.TakeLandServiceImpl;
import cn.visolink.firstplan.fpdesigntwo.dao.DesignTwoIndexDao;
import cn.visolink.firstplan.fpdesigntwo.service.DesignTwoIndexService;
import cn.visolink.firstplan.openbeforeseven.service.OpenBeforeSevenDayService;
import cn.visolink.firstplan.openbeforetwentyone.dao.OpenbeforetwentyoneDao;
import cn.visolink.firstplan.openbeforetwentyone.service.OpenBeforeTwentyoneService;
import cn.visolink.firstplan.opening.dao.OpeningDao;
import cn.visolink.utils.DateUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DesignTwoIndexServiceImpl implements DesignTwoIndexService {

    @Value("${oaflow.fpFlowCode}")
    private String fpFlowCode;


    @Autowired
    private DesignTwoIndexService designTwoIndexService;
    @Autowired
    private TakeLandServiceImpl takeLandService;

    @Autowired
    private OpenbeforetwentyoneDao openbeforetwentyoneDao;

    @Autowired
    private DesignTwoIndexDao designTwoIndexDao;

    @Autowired
    private TakeLandDao takeLandDao;
    @Autowired
    private JwtUserDetailsServiceImpl userDetailsService;
    @Autowired
    private OpenBeforeTwentyoneService openBeforeTwentyoneService;

    @Autowired
    private OpeningDao openingDao;

    @Autowired
    private OpenBeforeSevenDayService openBeforeSevenDayService;

    /**
     * 搜索顶设2核心指标里的信息，顶设2-核心指标-量+利+价+时间表
     * @param map
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map selectAllCodeIndex(Map map) {

        /*如果库里没有数据，说明没有初始化，
        那就先将顶设1的数据返回前端，待前端填好之后返回再初始化
        * 顶设2核心指标里的信息*/
        Map result = designTwoIndexDao.selectAllCodeIndex(map);
        if (result != null && result.size() > 0) {
          /*若库里有核心指标-量+利，价也有数
          价是一个LIST，要单独放在map里面*/


            List<Map> list = designTwoIndexDao.selectAllCodeIndexPrice(map);
            Map resultTime = designTwoIndexDao.selectDesignIndexTime(map);

            result.put("price", list);
            result.put("time", resultTime);
        } else {
            /*没有数，就搜索时间表和来自顶设1里的首开均价，首开均价战归是手填*/
            /*若为空，先取该节点里最新且已审批通过的数*/
            map.put("node_level", 3);
            List<Map> listNode = designTwoIndexDao.selectPlanNode(map);
            Map forListNode = new HashMap();
            if (listNode != null && listNode.size() > 0) {
                for (Map map1 : listNode) {

                    if ((map1.get("plan_approval") + "").equals("4")) {
                        forListNode = map1;
                        break;
                    }
                }
                result = designTwoIndexDao.selectAllCodeIndex(forListNode);
            }
            Map mapTime = new HashMap();
            Map resultTime = new HashMap();
            if (result != null) {
                /*时间表*/
                mapTime.putAll(map);
                mapTime.put("node_level", 2);
                resultTime = designTwoIndexDao.selectDesignIndexTime(forListNode);
                List<Map> list = designTwoIndexDao.selectAllCodeIndexPrice(forListNode);
                for (Map map1 : list) {
                    map1.remove("plan_node_id");
                }
                /*取拿地后全盘费率*/
                String landPer = designTwoIndexDao.selectLandPer(map);
                result.put("cost_invest_all_sales_per", landPer);
                result.put("price", list);
                result.remove("plan_node_id");
                resultTime.remove("plan_node_id");
                if (resultTime != null) {
                    result.put("time", resultTime);
                    result.put("designtwo_time", resultTime.get("open_time"));
                }
            } else {
                /*时间表*/
                result = new HashMap();

                mapTime.putAll(map);
                mapTime.put("node_level", 2);
                resultTime = designTwoIndexDao.selectAllCodeIndexTime(mapTime);
                if (resultTime != null) {
                    result.put("time", resultTime);
                    result.put("designtwo_time", resultTime.get("open_time"));
                }
                /*首开均价*/
                List<Map> list = designTwoIndexDao.selectAllCodeTypeForPrice(map);
                /*取拿地后全盘费率*/
                String landPer = designTwoIndexDao.selectLandPer(map);
                result.put("cost_invest_all_sales_per", landPer);
                if (result != null && result.size() > 0) {
                    result.put("price", list);
                }
            }
        }



        /*
         * 流程状态(0.实例被删除 1未发起 2流程草稿 3审批中
         *  4审批通过 5.驳回发起 6.撤回发起 7.流程放弃 8.开始专业审核 9.专业审核节点被撤回)
         * */
        result.put("judgeVersion", 1);
        /*判断是否可以发起一条新的版本*/
        if (map.get("plan_node_id") == null || map.get("plan_node_id") == "") {
            result.put("judgeVersion", -1);
        } else {
            List<Map> judgeone = designTwoIndexDao.designTwoCan(map);
            if (judgeone != null && judgeone.size() > 0) {
                result.put("judgeVersion", -1);
            }
            List<Map> judgeTwo = designTwoIndexDao.designTwoCanElse(map);
            if (StringUtils.isEmpty(judgeTwo)) {
                result.put("judgeVersion", -1);
            }
        }
        List<Map> price = (List<Map>) result.get("price");

        if(price==null||price.size()<=0){
            List<Map> list = designTwoIndexDao.selectAllCodeTypeForPrice(map);
            result.put("price", list);
        }

        /*查找所有的产品类型，在“价”里使用*/
        List<Map> productType = designTwoIndexDao.selectProjectCode(map);
        result.put("choice", productType);


        return result;
    }



    /*更新顶设2核心指标里的信息，顶设2-核心指标-量+利+时间表*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateAllCodeIndex(Map map, HttpServletRequest request) {
        if (map.get("cost_designtwo_all_sales_per") != null) {
            map.put("cost_designtwo_all_sales_per", Double.parseDouble(map.get("cost_designtwo_all_sales_per") + ""));

        }
        if (map.get("cost_designtwo_open_year_per") != null) {
            map.put("cost_designtwo_open_year_per", Double.parseDouble(map.get("cost_designtwo_open_year_per") + ""));

        }
        Integer result = 0;
        /*先在库里查找是否有顶设2的核心指标-量+利，没有就把跟新变成初始化*/
        designTwoIndexDao.deleteIndexPrice(map);

        Map mapindex = designTwoIndexDao.selectAllCodeIndex(map);

        if (mapindex == null || mapindex.size() < 1) {

            result += insertAllCodeIndex(map);

        } else {

            result += designTwoIndexDao.updateAllCodeIndex(map);
            /*得到要更新的顶设2-核心指标-价*/
            List<Map> price = (ArrayList) map.get("price");


            Map time = (Map) map.get("time");
            if (time != null) {
                time.put("plan_node_id", map.get("plan_node_id"));
                time.put("plan_id", map.get("plan_id"));
                /*判断是否修改计划开盘时间*/
                Map dateMap = designTwoIndexDao.getDesignTwoAndOpenDate(time);
                if(dateMap!=null&&dateMap.size()>0){
                    if(!dateMap.get("designtwo_time").equals(time.get("designtwo_time"))||!dateMap.get("open_time").equals(time.get("open_time"))){
                        time.put("isInitOpenCost","1");
                        designTwoIndexDao.updateInitOpenCostStatus(time);
                    }
                }

                designTwoIndexDao.updateAllCodeIndexTime(time);
                /*修改储客表时间段*/
                updatePlanTime(map);
                /*同时修改状态*/

            }

            /*得到要更新的顶设2-核心指标-价，逻辑：
             * 如果库里有这个业态，则更新，否则就插入一条新业态进去*/
            if (price != null && price.size() > 0) {
                for (Map mapprice : price) {
                    //过滤数据
                    filterMap(mapprice);
                    mapprice.put("plan_id", map.get("plan_id"));
                    mapprice.put("plan_node_id", map.get("plan_node_id"));
                }
                /*初始化价*/
                result += designTwoIndexDao.insertAllCodeIndexPrice(price);
            }

        }

        map.put("node_level", 3);
        updateLightStuat(map, request);


        return result;

    }


    /*初始化顶设2核心指标里的信息，顶设2-核心指标-量+利*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer insertAllCodeIndex(Map map) {
        if (map.get("cost_designtwo_all_sales_per") != null) {
            map.put("cost_designtwo_all_sales_per", Double.parseDouble(map.get("cost_designtwo_all_sales_per") + ""));

        }
        if (map.get("cost_designtwo_open_year_per") != null) {
            map.put("cost_designtwo_open_year_per", Double.parseDouble(map.get("cost_designtwo_open_year_per") + ""));

        }
        Integer result = 0;
        /*得到要初始化的顶设2-核心指标-价*/
        List<Map> price = (ArrayList) map.get("price");
        if (price != null && price.size() > 0) {
            for (Map mapprice : price) {
                mapprice.put("plan_id", map.get("plan_id"));
                mapprice.put("plan_node_id", map.get("plan_node_id"));
            }
            /*初始化价*/
            result += designTwoIndexDao.insertAllCodeIndexPrice(price);
        }
        /*初始化核心指标-量+利+时间*/

        /*修改时间信息*/


        Map time = (Map) map.get("time");
        if (time == null) {
            time = new HashMap();
        }
        time.put("plan_node_id", map.get("plan_node_id"));
        time.put("plan_id", map.get("plan_id"));
        if ((map.get("isUpdate") + "").equals("1")) {
            time.put("isUpdate", 1);
        }
        result += designTwoIndexDao.insertAllCodeIndexTime(time);


        result += designTwoIndexDao.insertAllCodeIndex(map);

        return result;


    }


    /*顶设2全盘量价规划查找和大定价版本对标*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map selectAllPlan(Map map) {
        Map realresult = new HashMap();

        List<Map> result = designTwoIndexDao.selectAllPlan(map);
        /*顶设2全盘量价规划查找和大定价版本对标若库里没有，也是先初始化，先查找顶设1的数据*/
        /*若为空，必定是plan_node_id=null*/
        if (result == null || result.size() < 1) {
            map.put("node_level", 3);
            List<Map> listNode = designTwoIndexDao.selectPlanNode(map);
            Map forListNode = new HashMap();
            if (listNode != null && listNode.size() > 0) {
                for (Map map1 : listNode) {

                    if ((map1.get("plan_approval") + "").equals("4")) {
                        forListNode = map1;
                        break;
                    }
                }
                result = designTwoIndexDao.selectAllPlan(forListNode);
            }
            /*查出上一个版本数据不为空，就用上一个版本的原本数据*/
            List<Map> Bigresult = new ArrayList<>();
            if (result != null && result.size() > 0) {
                for (Map map1 : result) {

                    List<Map> mapyear = designTwoIndexDao.selectAllPlanYear(map1);
                    for (Map map2 : mapyear) {
                        map2.remove("plan_node_id");
                    }
                    map1.put("child", mapyear);
                    map1.remove("plan_node_id");
                }
                Bigresult = designTwoIndexDao.selectAllPlanBig(forListNode);
                for (Map map1 : Bigresult) {
                    map1.remove("plan_node_id");
                }
            } else {
                result = designTwoIndexDao.selectTwoProductType(map);
                Bigresult = designTwoIndexDao.selectDesignTwoTypeBig(map);
                for(Map mm:Bigresult){
                    mm.put("rules_total", 0);
                }
                /*空数据也放个年进来*/

                if (result != null) {
                    /*查找来自顶设1的年份并继承到顶设2*/
                    List<String> designOneYear = designTwoIndexDao.selectDesignOneYear(map);
                    for (Map mapresult : result) {
                        List<Map> yearlist = new ArrayList<>();
                        if (designOneYear != null && designOneYear.size() > 0) {
                            for (int i = 0; i < designOneYear.size(); i++) {
                                Map mapyear = new HashMap();
                                mapyear.putAll(map);
                                mapyear.remove("plan_node_id");
                                mapyear.put("operation_type_code", mapresult.get("operation_type_code"));
                                mapyear.put("operation_type", mapresult.get("operation_type"));
                                mapyear.put("product_type_code", mapresult.get("product_type_code"));
                                mapyear.put("product_type", mapresult.get("product_type"));
                                mapyear.put("product_year", Integer.parseInt(designOneYear.get(i)));
                                mapyear.put("oneyear_avg_price", 0);
                                mapyear.put("oneyear_sell", 0);
                                mapyear.put("oneyear_supply", 0);
                                mapyear.put("oneyear_selling_per", 0);
                                mapyear.put("oneyear_selling_per_com", 0);
                                mapyear.put("id", mapresult.get("id"));
                                yearlist.add(mapyear);
                            }
                            mapresult.put("child", yearlist);
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                            Date date = new Date();
                            Map mapyear = new HashMap();
                            mapyear.putAll(map);
                            mapyear.remove("plan_node_id");
                            mapyear.put("operation_type_code", mapresult.get("operation_type_code"));
                            mapyear.put("operation_type", mapresult.get("operation_type"));
                            mapyear.put("product_type_code", mapresult.get("product_type_code"));
                            mapyear.put("product_type", mapresult.get("product_type"));
                            mapyear.put("product_year", Integer.parseInt(sdf.format(date)));
                            mapyear.put("oneyear_avg_price", 0);
                            mapyear.put("oneyear_sell", 0);
                            mapyear.put("oneyear_supply", 0);
                            mapyear.put("oneyear_selling_per", 0);
                            mapyear.put("oneyear_selling_per_com", 0);
                            yearlist.add(mapyear);
                            mapresult.put("child", yearlist);
                        }

                    }
                }
            }

            realresult.put("big", Bigresult);
        } else {
            /*通过不同的productType来查找不通年份的数据,并把它放到对应productType全盘量价规划的那一条MAP里*/
            for (int i = 0; i < result.size(); i++) {
                List<Map> mapyear = designTwoIndexDao.selectAllPlanYear(result.get(i));

                result.get(i).put("child", mapyear);
            }
            List<Map> big = designTwoIndexDao.selectAllPlanBig(map);

            realresult.put("big", big);
        }

        /*做三级，业态>产品类型>年份*/
        /*现在有一种需求，如果顶设2没有提报，那么要实时取来自顶设1的数据*/
        if (map.get("plan_node_id") != null) {
            List DesignTwoCanAdd = designTwoIndexDao.selectPlanNode(map);
            Map NowPlanNode = (Map) DesignTwoCanAdd.get(0);
            /*思路：如果是未提交的版本，那就实时去取顶设2最新的节点里的产品类型，有的就不动，没有的就增加*/
            /*要考虑到新增的产品类型的年份*/
            List<Map> needYear = new ArrayList<>();
            if (result != null) {
                for (Map map1 : result) {
                    if (map1.get("child") != null) {
                        needYear = (ArrayList) map1.get("child");
                        break;
                    }
                }
            }


            if (!((NowPlanNode.get("plan_approval") + "").equals("4"))) {
                List<Map> productList = designTwoIndexDao.selectTwoProductType(map);
                for (Map map1 : result) {
                    for (Map map2 : productList) {
                        if ((map1.get("fakeId") + "").equals(map2.get("fakeId") + "")) {
                            map1.put("all_big_avg_price", map2.get("all_big_avg_price"));
                            map1.put("all_big_value_price", map2.get("all_big_value_price"));
                            map1.put("all_invest_avg_price", map2.get("all_invest_avg_price"));
                            map1.put("all_invest_value_price", map2.get("all_invest_value_price"));
                            map2.putAll(map1);

                        }
                    }
                }
                /*往这些新的产品类型里添加年份*/
                if (needYear != null && needYear.size() > 0) {
                    for (Map map1 : productList) {
                        if (map1.get("child") == null) {
                            for (Map map2 : needYear) {

                                map2.put("operation_type_code", map1.get("operation_type_code"));
                                map2.put("operation_type", map1.get("operation_type"));
                                map2.put("product_type_code", map1.get("product_type_code"));
                                map2.put("product_type", map1.get("product_type"));

                                map2.put("oneyear_avg_price", 0);
                                map2.put("oneyear_sell", 0);
                                map2.put("oneyear_supply", 0);
                                map2.put("oneyear_selling_per", 0);
                                map2.put("oneyear_selling_per_com", 0);
                                map2.put("fakeId", map1.get("id"));
                            }
                            map1.put("child", needYear);
                        }
                    }
                }

                /*再把装好的值赋进去*/
                result = productList;
                /*大定价版本对标处理*/
                List<Map> Bigresult = designTwoIndexDao.selectDesignTwoTypeBig(map);
                List<Map> big = (ArrayList) realresult.get("big");
                for (Map map1 : big) {
                    for (Map map2 : Bigresult) {
                        if ((map1.get("fakeId") + "").equals(map2.get("fakeId") + "")) {
                            map1.put("big_total_value", map2.get("big_total_value"));
                            map1.put("invest_total_value", map2.get("invest_total_value"));
                            map1.put("vs_invest", map2.get("vs_invest"));


                            map2.putAll(map1);

                        }
                    }
                }
                realresult.put("big", Bigresult);
            }
        }
        result = analyticalData(result, "product");


        realresult.put("plan", result);


        return realresult;
    }

    /*顶设2全盘量价规划更新和大定价版本对标*/

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateAllPlan(Map map, HttpServletRequest request) {
        Integer result = 0;
        List<Map> planMap = (ArrayList) map.get("plan");

        List<Map> bigMap = (ArrayList) map.get("big");

        designTwoIndexDao.deleteAllYear(map);
        designTwoIndexDao.deleteAllPlan(map);
        designTwoIndexDao.deleteAllPlanBig(map);
        /*先删后加*/

        /*初始化全盘量价规划里的大定价全盘量价规划*/
        for (Map planR : planMap) {
            List<Map> listplan = (ArrayList) planR.get("product");
            for (Map plan : listplan) {
                plan.put("plan_node_id", map.get("plan_node_id"));
                plan.put("plan_id", map.get("plan_id"));
            }
            for (Map map100 : listplan) {
                if (map100.get("id") == null) {
                    map100.put("id", UUID.randomUUID().toString());
                }
            }
            result += designTwoIndexDao.insertAllPlan(listplan);
        }

        /*初始化全盘量价规划里的大定价全盘量价规划年信息*/
        if (planMap != null && planMap.size() > 0) {

            for (Map map1 : planMap) {
                List<Map> listplan = (ArrayList) map1.get("product");
                for (Map map2 : listplan) {
                    List<Map> yearMap = (ArrayList) map2.get("child");
                    if (yearMap != null && yearMap.size() > 0) {
                        for (Map yearMap1 : yearMap) {
                            //过滤数据
                            filterMap(yearMap1);
                            yearMap1.put("plan_node_id", map.get("plan_node_id"));
                            yearMap1.put("plan_id", map.get("plan_id"));
                            yearMap1.put("operation_type_code", map2.get("operation_type_code"));
                            yearMap1.put("operation_type", map2.get("operation_type"));
                            yearMap1.put("product_type_code", map2.get("product_type_code"));
                            yearMap1.put("product_type", map2.get("product_type"));
                            yearMap1.put("fakeId", map2.get("fakeId"));
                        }

                        result += designTwoIndexDao.insertAllPlanYear(yearMap);
                    }
                }
            }

        }

        /*初始化全盘量价规划里的大定价版本对标*/
        if (bigMap != null && bigMap.size() > 0) {
            for (Map plan : bigMap) {
                plan.put("plan_node_id", map.get("plan_node_id"));
                plan.put("plan_id", map.get("plan_id"));
            }
            for (Map map100 : bigMap) {
                if (map100.get("id") == null) {
                    map100.put("id", UUID.randomUUID().toString());
                }
            }

            result += designTwoIndexDao.insertAllPlanBig(bigMap);
        }

        updateLightStuat(map, request);

        return result;
    }

    /*顶设2楼栋大定价*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Map> selectBigPrice(Map map) {
        return designTwoIndexDao.selectBigPrice(map);
    }

    /*顶设2查找客储计划周拆分和节点储客计划*/

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map selectStorageNodePlan(Map map) {
        if (map == null || map.size() < 1) {
            return null;
        }
        map.put("node_level", 3);
        Map resultAll = new HashMap();

        List<Map> resultMap = designTwoIndexDao.selectStorageNodePlan(map);
        Integer testFileList = 0;
        /*若为空则返回初始化数据*/
        if ((map.get("plan_node_id")) == null || resultMap == null || resultMap.size() < 1) {
            List<Map> resultMapWeek = new ArrayList<>();
            map.put("node_level", 3);
            List<Map> listNode = designTwoIndexDao.selectPlanNode(map);
            Map forListNode = new HashMap();
            if (listNode != null && listNode.size() > 0) {
                for (Map map1 : listNode) {

                    if ((map1.get("plan_approval") + "").equals("4")) {
                        forListNode = map1;
                        break;
                    }
                }
                resultMap = designTwoIndexDao.selectStorageNodePlan(forListNode);
            }
            if (resultMap != null && resultMap.size() > 0) {
                resultMapWeek = designTwoIndexDao.selectStorageweek(forListNode);
                String fakeId = UUID.randomUUID().toString();
                for (Map map1 : resultMapWeek) {
                    map1.remove("plan_node_id");
                    map1.remove("flow_id");
                }
                for (Map map1 : resultMap) {
                    map1.remove("plan_node_id");
                    map1.remove("flow_id");
                }
                /*这个时候还要考虑到新的时间*/
                /*si路：用一个临时的planNodeId,用他来替换原有的Plannodeid，*/
            /*si路：先找到上一个版本的已经审批完成的数据，然后给它一个临时的PLANNODEID，然后把它存到库里去，
            * 接着再用上个版本的PLANNODEID找到已经完成审批的数据，给到前端，给前端前先拿掉他的PLANNODEID和FloWID*,
            再去库里删掉上个版本的已经审批完成的数据，然后把临时版本的ID改为上个已经审批完成的版本的ID/

             */
       /*     designTwoIndexDao.insertStorageWeek(resultMapWeek);
            designTwoIndexDao.insertStorageNodePlan(resultMap);*/
            /*这时候原有的listNode.get(0)里的数据的时间已经同步好了
            updatePlanTime(forListNode);
            resultMapWeek =   designTwoIndexDao.selectStorageweekFake(forListNode);

            resultMap=  designTwoIndexDao.selectStorageNodePlanFake(forListNode);*/
                /*解释起来太麻烦了看代码吧*/
         /*   Map mapfake=new HashMap();
            mapfake.put("fakeId",fakeId);
            mapfake.put("plan_node_id",forListNode.get("plan_node_id"));
            designTwoIndexDao.forNewNodePlanOne(mapfake);
            designTwoIndexDao.forNewNodePlanTwo(mapfake);
            designTwoIndexDao.forNewNodePlanThree(mapfake);
            designTwoIndexDao.forNewNodePlanFour(mapfake);*/

                resultAll.put("Week", resultMapWeek);
                resultAll.put("NodePlan", resultMap);
                resultAll.put("fileList", takeLandDao.getFileLists(forListNode.get("plan_node_id") + ""));
                testFileList = 1;
            } else {

                Map timeMap = new HashMap();
                timeMap.putAll(map);
                timeMap.put("node_level", 2);
                Map weekMap = designTwoIndexDao.selectPlanReal(timeMap);
                /*初始化也没有数据直接返回*/
                if (weekMap == null || weekMap.size() < 3) {
                    return null;
                }
                /*将一段时间拆分为周*/
                List<Map> forweekList = forNewWeek(weekMap);
                if(resultMap==null){
                    resultMap=new ArrayList<Map>();
                }
                resultMap.addAll(forweekList);
                /*节点储客计划每个节点的要素*/
                Map mapNode = new HashMap();
                mapNode.put("plan_node_id", weekMap.get("fakeid"));
                resultMapWeek = designTwoIndexDao.selectAllnodeplanTime(mapNode);

                resultAll.put("NodePlan", resultMapWeek);
                resultAll.put("Week", forweekList);
            }

        } else {
            List<Map> resultMapWeek = designTwoIndexDao.selectStorageweek(map);
            resultAll.put("Week", resultMapWeek);
            resultAll.put("NodePlan", resultMap);
        }

        resultAll.put("flow", designTwoIndexDao.selectStorageFlowTwo(map));
        if (testFileList == 0) {
            resultAll.put("fileList", takeLandDao.getFileLists(map.get("plan_node_id") + ""));

        }
        return resultAll;
    }

    /*将一段时间拆分为周*/
    public List<Map> forNewWeek(Map weekMap) {
        Map map1 = new HashMap();
        List<Map> resultMap = new ArrayList<>();
        map1.put("week", "完整波段");
        map1.put("start_time", weekMap.get("designtwo_time") + "");
        map1.put("end_time", weekMap.get("open_time") + "");

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdw = new SimpleDateFormat("E");
        SimpleDateFormat sdy = new SimpleDateFormat("yyyy.MM.dd");
        /*在这里初始化周*/
        try {
            map1.put("plan_id", weekMap.get("plan_id"));
            map1.put("plan_node_id", weekMap.get("plan_node_id"));
            map1.put("day_date", sdy.format(sd.parse(weekMap.get("designtwo_time") + "")) + "-" + sdy.format(sd.parse(weekMap.get("open_time") + "")));
            resultMap.add(map1);
            String date = weekMap.get("forweek") + "";
            String begin_date = date.split("-")[0];
            String end_date = date.split("-")[1];
            String begin_date_fm = begin_date.substring(0, 4) + "-" + begin_date.substring(4, 6) + "-" + begin_date.substring(6, 8);
            String end_date_fm = end_date.substring(0, 4) + "-" + end_date.substring(4, 6) + "-" + end_date.substring(6, 8);
            Date b = null;
            Date e = null;

            b = sd.parse(begin_date_fm);
            e = sd.parse(end_date_fm);

            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(b);
            Date time = b;
            String year = begin_date_fm.split("-")[0];
            String mon = begin_date_fm.split("-")[1];
            String day = begin_date_fm.split("-")[2];
            String timeb = year + "." + mon + "." + day;
            String timeone = year + "-" + mon + "-" + day;
            String timee = null;
            String timetwo = null;
            int weekday = 0;

            while (!(time.getTime() > e.getTime())) {
                rightNow.add(Calendar.DAY_OF_YEAR, 1);
                time = sd.parse(sd.format(rightNow.getTime()));
                String timew = sdw.format(time);
                if (("星期一").equals(timew) || ("Mon").equals(timew)) {
                    timeb = (sdy.format(time));
                    timeone = (sd.format(time));
                }

                if (("Sun").equals(timew) || ("星期日").equals(timew) || ("星期七").equals(timew) || time.getTime() == e.getTime()) {
                    if(time.getTime() > e.getTime()){

                    }else {
                        timee = (sdy.format(time));
                        timetwo = (sd.format(time));
                        System.out.println(timeb + "-" + timee);
                        weekday++;
                        Map map2 = new HashMap();
                        map2.put("plan_id", weekMap.get("plan_id"));
                        map2.put("plan_node_id", weekMap.get("plan_node_id"));
                        map2.put("week", "第" + int2chineseNum(weekday) + "周");
                        map2.put("start_time", timeone);
                        map2.put("end_time", timetwo);
                        map2.put("day_date", timeb + "-" + timee);
                        resultMap.add(map2);
                    }
                }
            }

        } catch (ParseException ee) {
            ee.printStackTrace();
        }
        return resultMap;
    }


    /*顶设2更新客储计划周拆分和节点储客计划*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateStorageNodePlan(Map map, HttpServletRequest request) {

        //给已经提交的文件做关联
        List<Map> fileList = (List<Map>) map.get("fileList");

        if (fileList != null && fileList.size() > 0) {
            designTwoIndexDao.DeleteAttach(map.get("plan_node_id") + "");
            for (int i = 0; i < fileList.size(); i++) {
                Map fileMap = fileList.get(i);
                fileMap.put("BizID", map.get("plan_node_id") + "");
                takeLandDao.updateSattach(fileMap);
            }
        }
        map.put("node_level", 3);
        Integer result = 0;
        List<Map> week = (ArrayList) map.get("Week");
        List<Map> NodePlan = (ArrayList) map.get("NodePlan");
        if (week == null || week.size() == 0) {
            return null;
        }
        String plan_node_id = map.get("plan_node_id") + "";

        if (map.get("flow_id") != null) {
            /*先删光再初始化*/
            designTwoIndexDao.deleteStorageweek(map);
            designTwoIndexDao.deleteStorageNodePlan(map);
            for (Map weekmap : week) {
                weekmap.put("plan_node_id", plan_node_id);
                weekmap.put("flow_id", map.get("flow_id"));
            }
            result += designTwoIndexDao.insertStorageWeek(week);
            for (Map NodePlanmap : NodePlan) {
                NodePlanmap.put("plan_node_id", plan_node_id);
                NodePlanmap.put("flow_id", map.get("flow_id"));
            }
            result += designTwoIndexDao.insertStorageNodePlan(NodePlan);
            /*无plan_node_id相等即要初始化*/
        } else {
            String uuid = UUID.randomUUID().toString();
            map.put("uuid", uuid);
            designTwoIndexDao.insertNodeFlow(map);
            for (Map mapweek : week) {
                mapweek.put("plan_node_id", plan_node_id);
                mapweek.put("flow_id", uuid);
            }
            for (Map mapNodePlan : NodePlan) {
                mapNodePlan.put("plan_node_id", plan_node_id);
                mapNodePlan.put("flow_id", uuid);
            }
            result += designTwoIndexDao.insertStorageWeek(week);
            result += designTwoIndexDao.insertStorageNodePlan(NodePlan);
        }
        /*更新完后要检查一下时间节点和计划表能否对的上，对不上需要重新初始化并保留原有数据*/
        //  updatePlanTime(map);
        /*暂存完修改状态*/
        updateLightStuat(map, request);
        return result;
    }

    /*判断是否暂存还是上报*/
    public void updateLightStuat(Map map, HttpServletRequest request) {

        if ((map.get("Fast") + "").equals("1")) {
            Map ForMap = new HashMap();
            ForMap.put("eventType", 4);
            if ((map.get("node_level") + "").equals("3")) {
                ForMap.put("businesskey", map.get("plan_node_id"));
                ForMap.put("orgName", "fp_designtwo");
            } else {
                ForMap.put("businesskey", map.get("flow_id"));
                ForMap.put("orgName", "1");
            }
            forUpdateNode(ForMap);
        }




        /*暂存完修改下状态,1是提交*/
        if ((map.get("isUpdate") + "").equals("1")) {


            String username = request.getHeader("username");

            /*发起审批*/
            JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(username);
            String jobName = null;
            if (jwtUser != null) {
                jobName = jwtUser.getJob().get("JobName") + "";
            }
            Integer node_level = Integer.parseInt(map.get("node_level") + "");
            String orgName = null;
            String TITLE = null;
            String json_id = null;
            switch (node_level) {
                case 3:
                    orgName = "fp_designtwo";
                    TITLE = "顶设2审批表";
                    json_id = map.get("plan_node_id") + "";
                    break;
                case 4:
                    orgName = "fp_open_three";
                    TITLE = "首开前3月客储达成进度审批表";
                    json_id = map.get("flow_id") + "";
                    break;
                case 5:
                    orgName = "fp_open_two";
                    TITLE = "首开前2月客储达成进度审批表";
                    json_id = map.get("flow_id") + "";
                    break;
                case 6:
                    orgName = "fp_open_twentyone_node";
                    TITLE = "首开前21天客储达成进度审批表";
                    json_id = map.get("flow_id") + "";
                    break;
            }

            Map projectName = designTwoIndexDao.selectProjectName(map.get("plan_id") + "");

            Map flowParams = new HashMap();

            flowParams.put("json_id", json_id);
            flowParams.put("project_id", projectName.get("project_id"));
            flowParams.put("creator", username);
            flowParams.put("flow_code", fpFlowCode);
            flowParams.put("TITLE", TITLE);
            flowParams.put("post_name", jobName);
            flowParams.put("orgName", orgName);
            Map comcommon = new HashMap();

            /*获取来自顶设1的产品系和建筑面积*/
            if (node_level == 3) {
                Map mapArea = designTwoIndexDao.selectAllArea(map);
                if ((mapArea.get("product_set") + "").equals("T") || (mapArea.get("designone_area") != null && Double.parseDouble(mapArea.get("designone_area") + "") >= 500000)) {
                    comcommon.put("designone_area", mapArea.get("designone_area") + "");//建筑面积
                    comcommon.put("product_set", mapArea.get("product_set") + ""); //产品系
                }
            }
            /*获取偏差率小于15且最接近认购的*/
            if (node_level == 6) {
                Map rate = designTwoIndexDao.selectDeviationRate(map);
                if (rate != null) {
                    comcommon.put("rateNum", rate.get("rate") + "");//偏差率

                }

            }

            comcommon.put("isChange",map.get("change")+"");
            flowParams.put("comcommon", JSONObject.toJSONString(comcommon));

            takeLandService.insertFlow(flowParams);
        } else {
            map.put("plan_approval", 10);
            /*更改FLOW表*/
            designTwoIndexDao.updateLightStuat(map);
            map.put("approval_stuat", 10);
            designTwoIndexDao.upateFlow(map);

        }


    }

    /*审批完成后调用*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void forUpdateNode(Map map) {


        //主键ID
        String jsonId = map.get("businesskey") + "";//主表Id
        String flowKey = map.get("flowKey") + "";//模板
        String eventType = map.get("eventType") + "";//审批状态
        String orgName = map.get("orgName") + "";//流程
        try {
            /*如果是顶设2提交审批返回*/
            Integer node_level = 4;
            if (orgName.equals("fp_designtwo")) {
                Map realMap = new HashMap();
                realMap.put("plan_node_id", jsonId);
                realMap.put("node_level", 3);

                if (eventType.equals("3")) {
                    /*顶设2审批中*/
                    realMap.put("plan_approval", 3);
                    realMap.put("light_stuat", 2);
                    designTwoIndexDao.updateRealLight(realMap);

                } else if (eventType.equals("5") || eventType.equals("6")) {
                    designTwoIndexDao.updatePlanForBack(realMap);
                    designTwoIndexDao.updateFlowForBack(realMap);
                } else if (eventType.equals("4")) {
                    /*先修改顶设2的结束时间*/
                    designTwoIndexDao.updateRealTwoTime(realMap);
                    /*更新计划表开盘时间*/
                    designTwoIndexDao.updatePlanTime(realMap);
                    /*审批完自动算灯的状态*/
                    designTwoIndexDao.updateRealDate(realMap);

                    realMap.put("approval_stuat", 4);
                    /*储客计划FLOw表的提交*/
                    designTwoIndexDao.upateFlow(realMap);

                    /*铺排时间*/
                    String username = map.get("creator") + "";
                    /*查找顶设2的时间*/
                    Map time = designTwoIndexDao.selectDesignIndexTime(realMap);
                    /*先查询顶设2后面已经通过审批的节点是否需要重新初始哈*/
                    List<Map> AllNode = designTwoIndexDao.selectEffectiveNode(time);

                    /*先将之前已经审批完成但修改时间后可以重新填报的节点进行处理*/
                    designTwoIndexDao.UpdateAllNodeTime(time);
                    /*获取项目名称*/

                    Map project = designTwoIndexDao.selectProjectName(time.get("plan_id") + "");
                    String projectName = project.get("project_id") + "";
                    /*将延期开盘申请作为历史版本*/
                    designTwoIndexDao.updateDelaySeven(time);

                    /*根据不同的节点来判断不同的初始化逻辑*/
                    for (Map map1 : AllNode) {
                        if ((map1.get("node_level") + "").equals("4") || (map1.get("node_level") + "").equals("5") || (map1.get("node_level") + "").equals("6")) {
                            /*首开前3个月2个月21天重新加一个flow表单*/
                            map1.put("uuid", UUID.randomUUID().toString());
                            designTwoIndexDao.insertNodeFlow(map1);
                        }
                        if ((map1.get("node_level") + "").equals("6") || (map1.get("node_level") + "").equals("7")) {
                            /*首开前21天7天调用接口表单*/
                            map1.put("project_id", projectName);
                            try {
                                map1.put("create", "new");
                                openBeforeTwentyoneService.viewdelayOpenApplay(map1);
                                if ((map1.get("node_level") + "").equals("7")) {
                                    map1.put("create", "new");
                                    openBeforeSevenDayService.viewOpenBeforeSevenDayOpenApplay(map1);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        /*开盘首日*/
                        if ((map1.get("node_level") + "").equals("8")) {
                            map1.put("creator", username);
                            map1.put("designtwo_time", time.get("open_time"));
                            map1.put("uuid", UUID.randomUUID().toString());
                            designTwoIndexDao.insertPlanNode(map1);
                        }
                        /*开盘后一个月*/
                        if ((map1.get("node_level") + "").equals("9")) {
                            Integer version = openingDao.getOpenMorrowBroadcastByPlanIdVersionNum(map1.get("plan_id") + "") + 1;
                            map1.put("version", version);
                            designTwoIndexDao.insertBroadcast(map1);
                        }

                    }

                    takeLandService.arrangeTime(time, username, time.get("plan_id") + "", null, "2");
                    /*查找已经提交的且没有版本需要新创建版本的节点,将节点放置为已完成*/
                    if (AllNode != null && AllNode.size() > 0) {

                        designTwoIndexDao.deleteForNo(time);

                    }


                    List<Map> needNode = designTwoIndexDao.selectNeedNewNode(time);
                    if (needNode != null && needNode.size() > 0) {
                        /*将节点标记为已完成*/
                        for (Map map1 : needNode) {
                            designTwoIndexDao.updateRealDate(map1);
                            /*创建一个FLOW表的版本*/
                            if (!(map1.get("node_level") + "").equals("7")) {
                                String uuid = UUID.randomUUID().toString();
                                Map flowMap = new HashMap();
                                flowMap.putAll(map1);
                                flowMap.put("uuid", uuid);
                                designTwoIndexDao.insertNodeFlow(flowMap);
                                /*更改FLOW表的状态*/
                                flowMap.put("approval_stuat", 7);
                                flowMap.put("flow_id", uuid);
                                designTwoIndexDao.upateFlow(flowMap);
                            }

                        }
                    }


                    /*更改当前计划的待填报节点为下一节点*/
                    Map map1 = designTwoIndexDao.selectPlanIDElse(map);
                    map1.put("node_level", node_level);
                    designTwoIndexDao.updateNodeName(map1);
                }
            } else {

                Map realMap = new HashMap();
                realMap.put("flow_id", jsonId);
                /*首开前3个月*/
                if (orgName.equals("fp_open_three")) {
                    realMap.put("node_level", 4);
                    node_level = 5;
                }
                /*首开前2个月*/
                if (orgName.equals("fp_open_two")) {
                    realMap.put("node_level", 5);
                    node_level = 6;
                }
                if (eventType.equals("3")) {
                    /*首开前几个月审批中*/
                    realMap.put("plan_approval", 3);
                    realMap.put("light_stuat", 2);
                    /*得到PlanNodeId和 节点的状态*/
                    Map planNodeId = designTwoIndexDao.selectPlanNodeId(realMap);
                    if (planNodeId != null) {
                        realMap.put("plan_node_id", planNodeId.get("plan_node_id"));
                    }
                    /*首开前几个月的审批中状态*/
                    /*首开前几个月的节点表审批中状态，若节点已完成审批则不受影响*/
                    designTwoIndexDao.updateThreeMonthsType(realMap);
                } else if (eventType.equals("5") || eventType.equals("6")) {
                    /*得到PlanNodeId和 节点的状态*/
                    Map planNodeId = designTwoIndexDao.selectPlanNodeId(realMap);
                    if (planNodeId != null) {
                        realMap.put("plan_node_id", planNodeId.get("plan_node_id"));
                    }
                    realMap.put("plan_approval", 10);
                    designTwoIndexDao.updateThreeMonthsType(realMap);
                } else if (eventType.equals("4")) {
                    /*得到PlanNodeId和 节点的状态*/
                    Map planNodeId = designTwoIndexDao.selectPlanNodeId(realMap);
                    if(planNodeId!=null){
                        /*审批完自动算灯的状态*/
                        realMap.put("plan_node_id", planNodeId.get("plan_node_id"));
                        designTwoIndexDao.updateRealDate(realMap);
                    }

                    /*更改FLOW表的状态*/
                    realMap.put("approval_stuat", 4);
                    designTwoIndexDao.upateFlow(realMap);
                    /*更改当前计划的待填报节点为下一节点*/
                    Map map1 = designTwoIndexDao.selectPlanIDElse(map);
                    map1.put("node_level", node_level);
                    designTwoIndexDao.updateNodeName(map1);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertOrUpOpenCost(Map params) {
        String plan_id = params.get("plan_id") + "";
        String plan_node_id = params.get("plan_node_id") + "";

        List<Map> costList = (List) params.get("costList");
        String commun = designTwoIndexDao.getSubjectByVersion(params.get("version")+"");
        String rescommun = "";
        designTwoIndexDao.delOpenCostByPlanNodeId(plan_node_id);
        if (costList != null && costList.size() > 0) {
            for (Map map : costList) {
                map.put("plan_node_id", plan_node_id);
            }
            if(commun.length()>0){
            String[] strArray = commun.split(",");
            for (int i = 0; i < strArray.length; i++) {
                rescommun+="#{item."+strArray[i]+"},";
            }
                commun=commun.substring(0,commun.length());
                rescommun = rescommun.substring(0,rescommun.length()-1);
            }
            System.out.println("列名："+commun);
            System.out.println("赋值："+rescommun);
            designTwoIndexDao.insertOpenCost(costList,commun,rescommun,params.get("version")+"");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map selectOpenCostByPlanNodeId(String plan_node_id, String plan_id) {
        Map resMap = new HashMap();
        Map params = new HashMap();
        params.put("plan_node_id", plan_node_id);
        params.put("plan_id", plan_id);
        //获取时间节点
        try {


            Map dataNode = designTwoIndexDao.selectDesignIndexTime(params);
            List<Map> subject = designTwoIndexDao.getNewSubject("");
            String designtwo_time = "";
            String open_time = "";
            if (dataNode != null && dataNode.size() > 0) {
                designtwo_time = dataNode.get("designtwo_time") + "";
                open_time = dataNode.get("open_time") + "";
            }
            //费用
            List<Map<String, Object>> costList = designTwoIndexDao.selectOpenCostByPlanNodeId(plan_node_id);
            if (costList != null && costList.size() > 0) {
                String version = costList.get(0).get("version") + "";
                String commun = designTwoIndexDao.getSubjectByVersion(version);
                subject = designTwoIndexDao.getNewSubject(version);
                costList = designTwoIndexDao.selectNewOpenCost(plan_node_id, commun);
                if (designtwo_time != null && !designtwo_time.equals("") && !designtwo_time.equals("null")) {
                    designtwo_time = designtwo_time.substring(0, 7);
                    open_time = open_time.substring(0, 7);
                    if ((costList.get(0).get("months") + "").equals(open_time) && (costList.get(costList.size() - 1).get("months") + "").equals(designtwo_time)) {

                    } else {
                        //时间改变，保存过保留有的   两个循环换一下   costList里面有的  costTemp里面没有  就把costList里面的删掉 并且删掉库
                        List<Map<String, Object>> costListTwo = getCostList(designtwo_time, open_time, subject);
                        if (costListTwo == null) {
                            costListTwo = new ArrayList<Map<String, Object>>();
                        }
                        System.out.println(costListTwo.size() + ">>>>>>>>>>>>>>>>>>>" + costList.size());
                        System.out.println(dataNode.get("isInitOpenCost"));
                        if (costListTwo.size() > costList.size() || !"1".equals(dataNode.get("isInitOpenCost"))) {
                            System.out.println("======走老逻辑");
                            for (int i = 0; i < costListTwo.size(); i++) {
                                Map mapTemp = costListTwo.get(i);
                                for (Map data1 : costList) {
                                    if (mapTemp.get("months").equals(data1.get("months"))) {
                                        costListTwo.remove(mapTemp);
                                        i--;
                                    }
                                }
                            }
                            costList.addAll(costListTwo);
                            Collections.sort(costList, new Comparator<Map<String, Object>>() {

                                @Override
                                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                                    String name1 = (String) o1.get("months");//name1是从你list里面拿出来的一个
                                    String name2 = (String) o2.get("months"); //name1是从你list里面拿出来的第二个name
                                    return name2.compareTo(name1);
                                }
                            });
                        } else if ("1".equals(dataNode.get("isInitOpenCost"))) {
                            System.out.println("======走新逻辑");
                            String months = "";
                            for (Map map : costListTwo) {
                                months += "'" + map.get("months") + "',";
                            }
                            System.out.println("生成的月份===============" + months);
                            String resMonths = months.substring(0, months.length() - 1);
                            costList = designTwoIndexDao.getOpenCostByMonth(resMonths, plan_node_id);
                            designTwoIndexDao.delOpenCostByMonth(resMonths, plan_node_id);
                            params.put("isInitOpenCost", "0");
                            designTwoIndexDao.updateInitOpenCostStatus(params);
                            for (int i = 0; i < costListTwo.size(); i++) {
                                Map mapTemp = costListTwo.get(i);
                                for (Map data1 : costList) {
                                    if (mapTemp.get("months").equals(data1.get("months"))) {
                                        costListTwo.remove(mapTemp);
                                        i--;
                                    }
                                }
                            }
                            costList.addAll(costListTwo);
                        }
                        Collections.sort(costList, new Comparator<Map<String, Object>>() {

                            @Override
                            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                                String name1 = (String) o1.get("months");//name1是从你list里面拿出来的一个
                                String name2 = (String) o2.get("months"); //name1是从你list里面拿出来的第二个name
                                return name2.compareTo(name1);
                            }
                        });
                        resMap.put("costList", costList);
                        resMap.put("subject", subject);
                        return resMap;
                    }
                } else {
                    resMap.put("costList", costList);
                    resMap.put("subject", subject);
                    return resMap;
                }
            } else {
                costList = getCostList(designtwo_time, open_time, subject);
            }

            resMap.put("costList", costList);
            resMap.put("subject", subject);
            return resMap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    private List getCostList(String delisting_time, String open_time,List<Map> subject) {
        List<Map<String,Object>> costList = null;

        if (delisting_time != null && !delisting_time.equals("") && !delisting_time.equals("null")) {
            costList = DateUtil.getMonthByYear(delisting_time, open_time);
            for (Map data : costList) {
                for (Map map : subject) {
                    data.put(map.get("subject_code"), 0);
                }
            }
        }
        return costList;
    }

    /*更新完后要检查一下时间节点和计划表能否对的上，对不上需要重新初始化并保留原有数据*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePlanTime(Map map) {
        String plan_node_id = map.get("plan_node_id") + "";
        Map planTime = designTwoIndexDao.selectPlanTime(map);
        if ((planTime.get("equal") + "").equals("NO") && planTime.get("plan_node_id") != null && planTime.get("designtwo_time") != null) {
            /*节点储客计划每个节点的要素*/
            /*现在查出来的符合计划表的时间*/
            List<Map> resultMapWeek = designTwoIndexDao.selectAllnodeplanTime(map);
            /*存储计划原本的数据*/
            List<Map> resultMap = designTwoIndexDao.selectStorageNodePlan(map);
            /*将数据库里的数据平滑到新的时间节点中，以开盘日期优先*/
            /*先删掉库里关于NODEPLAN的苏剧*/
            designTwoIndexDao.deleteStorageNodePlan(map);


            int j = 1;
            for (int i = resultMap.size() - 1; i >= 0; i--) {
                resultMap.get(i).remove("nide_name");
                resultMap.get(i).remove("node_time");
                resultMap.get(i).remove("node_time_code");
                if (resultMapWeek.size() - j < 0) {
                    break;
                }

                resultMapWeek.get(resultMapWeek.size() - j).putAll(resultMap.get(i));
                j++;
            }


            /*然后初始化*/
            designTwoIndexDao.insertStorageNodePlan(resultMapWeek);

            List<Map> newWeekList = forNewWeek(planTime);

            List<Map> resultWeek = designTwoIndexDao.selectStorageweek(map);
            /*先删光再初始化*/
            designTwoIndexDao.deleteStorageweek(map);
            int k = 1;
            String flowId = null;
            if (resultWeek != null && resultWeek.size() > 0) {
                flowId = resultWeek.get(0).get("flow_id") + "";

            }
            for (Map map1 : newWeekList) {
                map1.put("plan_node_id", map.get("plan_node_id"));
                map1.put("flow_id", flowId);
            }

            for (int i = newWeekList.size() - 1; i >= 0; i--) {
                if(resultWeek==null){
                    resultWeek=new ArrayList<>();
                }
                if (resultWeek.size() - k < 0) {
                    break;
                }
                /*思路：若改变后的开盘时和顶设2时大于储客表原有的时间，就增加，否则就删除，并平滑输入数据，且以最进的时间输入*/
                Map mapWeek = resultWeek.get(resultWeek.size() - k);
                mapWeek.remove("start_time");
                mapWeek.remove("end_time");
                mapWeek.remove("day_date");
                mapWeek.remove("week");
                newWeekList.get(i).putAll(mapWeek);

                k++;
            }

            designTwoIndexDao.insertStorageWeek(newWeekList);

        }
    }


    /*顶设2初始化客储计划周拆分和节点储客计划*/
    public Integer insertStorageNodePlan(Map map) {
        Integer result = 0;
        List<Map> week = (ArrayList) map.get("Week");
        List<Map> NodePlan = (ArrayList) map.get("NodePlan");
        if (week != null && week.size() > 0) {
            result += designTwoIndexDao.insertStorageWeek(week);
        }

        if (NodePlan != null && NodePlan.size() > 0) {
            result += designTwoIndexDao.insertStorageNodePlan(NodePlan);
        }
        return result;
    }


    /*将数字转文字*/
    public static String int2chineseNum(int src) {
        final String num[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        final String unit[] = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};
        String dst = "";
        int count = 0;
        while (src > 0) {
            dst = (num[src % 10] + unit[count]) + dst;
            src = src / 10;
            count++;
        }
        return dst.replaceAll("零[千百十]", "零").replaceAll("一十", "十").replaceAll("零+万", "万")
                .replaceAll("零+亿", "亿").replaceAll("亿万", "亿零")
                .replaceAll("零+", "零").replaceAll("零$", "");

    }

    /*查找顶设2左上角的版本选择*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Map> selectPlanNode(Map map) {
        map.put("node_level", 3);
        if (map.get("plan_node_id") == null) {
            /*创建一个临时版本*/
            String node_name = "顶设2版1";

            /*插入临时版本名*/
            Date date = new Date();
            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");

            node_name = node_name + sd.format(date);
            Map versionMap = new HashMap();
            versionMap.put("node_name", node_name);

            versionMap.put("plan_id", map.get("plan_id"));
            List<Map> list = new ArrayList<>();
            list.add(versionMap);
            return list;

        } else {
            return designTwoIndexDao.selectPlanNode(map);
        }

    }

    /*插入计划节点表信息*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String forPlanNode(Map map, HttpServletRequest request) {

        if (map.get("plan_node_id") == null || map.get("plan_node_id") == "") {
            /*先查找是否有暂存的版本*/
            String planNodeId = designTwoIndexDao.selectApprovalTen(map);
            if (planNodeId != null) {
                return planNodeId;
            }
            /*查该计划ID下有几个版本，用来拼凑版本名字*/
            Map mapExist = new HashMap();
            mapExist.put("plan_id", map.get("plan_id"));
            mapExist.put("node_level", 3);


            /*新创建的版本ID*/
            String uuid = UUID.randomUUID().toString();
            map.put("uuid", uuid);

            /*插入结束时间，顶设2结束时间是首开前120天*/

            String username = request.getHeader("username");
            map.put("creator", username);
            map.put("node_level", 3);
            Map TimeMap = designTwoIndexDao.selectDesignTwoTime(map);

            String Time = TimeMap.get("plan_end_time") + "";
            map.put("designtwo_time", Time);
            designTwoIndexDao.insertPlanNode(map);

            return uuid;
        } else {
            return map.get("plan_node_id") + "";
        }

    }

    /*判断是否可以创建新版本*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map judgeVersion(Map map) {

        return designTwoIndexDao.judgeVersion(map);

    }

    /*获取顶设2货值结构页面*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map getDesignTwoValue(Map map) {
        List<Map> listTwoValue = designTwoIndexDao.getDesignTwoValue(map);
        List<Map> listTwoRoom = new ArrayList<>();
        if (listTwoValue != null && listTwoValue.size() > 0) {

            listTwoRoom = designTwoIndexDao.getDesignTwoRoom(map);


        } else {
            map.put("node_level", 3);
            List<Map> listNode = designTwoIndexDao.selectPlanNode(map);
            Map forListNode = new HashMap();

            if (listNode != null && listNode.size() > 0) {
                for (Map map1 : listNode) {

                    if ((map1.get("plan_approval") + "").equals("4")) {
                        forListNode = map1;
                        break;
                    }
                }
                listTwoValue = designTwoIndexDao.getDesignTwoValue(forListNode);
            }
            if (listTwoValue != null && listTwoValue.size() > 0) {
                listTwoRoom = designTwoIndexDao.getDesignTwoRoom(forListNode);
            } else {
                listTwoValue = designTwoIndexDao.getTopOneValue(map);
                if(listTwoValue!=null){
                    for (Map listMap : listTwoValue) {
                        listMap.remove("vs_case_info");
                    }
                }
                listTwoRoom = designTwoIndexDao.getTopOneRoom(map);
                if(listTwoRoom!=null){
                    for (Map listMap : listTwoRoom) {
                        listMap.remove("vs_case_info");
                    }
                }
            }

        }


        listTwoValue = analyticalData(listTwoValue, "child");
        listTwoRoom = analyticalData(listTwoRoom, "child");
        Map map1 = new HashMap();
        map1.put("tollerMap", listTwoValue);
        map1.put("roomlist", listTwoRoom);
        return map1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateDesignTwoValue(Map map, HttpServletRequest request) {
        String plan_node_id = "" + map.get("plan_node_id");
        String plan_id = "" + map.get("plan_id");

        Integer isNeedNew = 0;

        List<Map> tollerMap = (ArrayList) map.get("tollerMap");
        List<Map> roomlist = (ArrayList) map.get("roomlist");
        tollerMap = analyticalWarehous(tollerMap, plan_node_id, plan_id);
        roomlist = analyticalWarehous(roomlist, plan_node_id, plan_id);
        tollerMap = publicNull(tollerMap);
        roomlist = publicNull(roomlist);
        List<Map> tollerMap2 = new ArrayList<>();
        tollerMap2.addAll(tollerMap);
        /*现在有一个逻辑，当获知结构产品类型变动，核心指标产品类型也要跟着变动*/
        List<Map> listTwoValue = designTwoIndexDao.getDesignTwoValue(map);

        if (listTwoValue != null && listTwoValue.size() > 0) {
            if (!(listTwoValue.size() == tollerMap2.size())) {
                /*看这个项目是不是暂存，若是实时取PRICE*/

                isNeedNew = 1;


            } else {
                /*思路：若库里的产品类型数量不等于前端返回的产品类型数量，肯定已经做了修改，若
                 * 不相等是也要考虑到是否每个都对的上
                 * */
                List<Map> deleteList = new ArrayList<>();
                for (Map map1 : listTwoValue) {
                    for (Map map2 : tollerMap2) {
                        if ((map1.get("fakeId") + "").equals(map2.get("fakeId") + "")) {
                            deleteList.add(map2);

                        }
                    }

                }

                /*删过后还有剩余说明产品类型有变动*/
                tollerMap2.removeAll(deleteList);
                if (tollerMap2.size() > 1) {
                    isNeedNew = 1;
                }
            }
        }


        designTwoIndexDao.deleteDesignTwoValue(map);
        designTwoIndexDao.deleteDesignTwoRoom(map);
        for (Map map100 : tollerMap) {
            if (map100.get("fakeId") == null) {
                map100.put("fakeId", UUID.randomUUID().toString());

            }
            map100.remove("is_del");
        }
        for (Map map100 : roomlist) {

            map100.remove("is_del");
        }
        if (tollerMap != null && tollerMap.size() > 0) {
            designTwoIndexDao.insertDesignTwoValue(tollerMap);
        }
        if (roomlist != null && roomlist.size() > 0) {
            designTwoIndexDao.insertDesignTwoRoom(roomlist);
        }


        if (isNeedNew == 1) {
            designTwoIndexDao.deleteIndexPrice(map);
            List<Map> list = designTwoIndexDao.selectAllCodeTypeForPrice(map);
            for (Map mapprice : list) {
                filterMap(mapprice);
                mapprice.put("plan_id", map.get("plan_id"));
                mapprice.put("plan_node_id", map.get("plan_node_id"));
            }
            /*初始化价*/
            designTwoIndexDao.insertAllCodeIndexPrice(list);

        }


        updateLightStuat(map, request);
        return null;

    }


    //公共方法解析货值结构和户型数据（页面展示）
    public List<Map> analyticalData(List<Map> list, String child) {
        List<Map> tollerMap = new ArrayList<>();
        if (list != null) {
            Set<Map> set = new HashSet<>();
            for (Map map1 : list) {
                Map map2 = new HashMap();
                map2.put("operation_type", map1.get("operation_type"));
                map2.put("operation_type_code", map1.get("operation_type_code"));
                String vs_case_info = map1.get("vs_case_info")+"";
                if("null".equals(vs_case_info)||"".equals(vs_case_info)){
                    map1.put("vs_case_info","");
                }
                map2.put("plan_id", map1.get("plan_id"));
                set.add(map2);
            }
            for (Map o : set) {
                Map resultvalue = new HashMap();
                resultvalue.putAll(o);

                List listResult = new ArrayList();
                for (Map map2 : list) {
                    if ((o.get("operation_type") + "").equals(map2.get("operation_type"))) {
                        listResult.add(map2);
                    }
                }
                resultvalue.put(child, listResult);
                tollerMap.add(resultvalue);
            }
        }
        return tollerMap;
    }

    //公共方法解析获知结构和户型数据（用于数据入库）
    public List<Map> analyticalWarehous(List<Map> mapValues, String plan_node_id, String plan_id) {
        List<Map> mapValuesResult = new ArrayList<>();
        for (int i = 0; i < mapValues.size(); i++) {
            String operation_type = mapValues.get(i).get("operation_type") + "";
            List<Map> listvalue = (List<Map>) mapValues.get(i).get("child");

            for (int j = 0; j < listvalue.size() - 1; j++) {
                String vs_case_info=listvalue.get(0).get("vs_case_info")+"";
                System.err.println(vs_case_info);
                Map value = (Map) listvalue.get(j).get("obj");
                String product_type = listvalue.get(j).get("product_type") + "";
                value.put("product_type", product_type);
                value.put("operation_type", operation_type);
                value.put("plan_node_id", plan_node_id);
                value.put("plan_id", plan_id);
                if(!"null".equals(vs_case_info)&&!"".equals(vs_case_info)){
                    value.put("vs_case_info",vs_case_info);
                }else{
                    value.put("vs_case_info","");
                }
                mapValuesResult.add(value);
            }
        }
        return mapValuesResult;
    }

    /**
     * 为空字段校验
     */
    public List<Map> publicNull(List<Map> listmap) {
        /*
         * 遍历Map所有值，若传进来的数有空，则将它默认为0
         * */
        for (int i = 0; i < listmap.size(); i++) {
            Iterator iterable = listmap.get(i).entrySet().iterator();
            while (iterable.hasNext()) {
                Map.Entry entry_d = (Map.Entry) iterable.next();
                Object key = entry_d.getKey();
                Object value = entry_d.getValue();
                if (value == "") {
                    value = null;
                }
                listmap.get(i).put(key.toString(), value);
            }
        }
        return listmap;
    }

    /*现在有一种机制，如果顶设1从新审批完成，顶设2要把当前草稿版清空重新初始化*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void backDesignTwo(String planId) {
        try {
            /*首先删除原有的顶设2草稿版,删除前先拿到用户ID和时间*/
            Map map = new HashMap();
            map.put("plan_id", planId);
            Map TimeMap = designTwoIndexDao.selectDesignTwoTime(map);
            String creator = TimeMap.get("creator") + "";
            String Time = TimeMap.get("plan_end_time") + "";

            Integer back = designTwoIndexDao.backDesignTwo(map);
            /*再插入一个新的版本*/
            if (back != null && back > 0) {
                map.put("designtwo_time", Time);
                map.put("creator", creator);
                map.put("node_level", 3);
                map.put("uuid", UUID.randomUUID().toString());
                designTwoIndexDao.insertPlanNode(map);

                Map<Object, Object> paramMaps = new HashMap<>();
                paramMaps.put("plan_id",planId);
                paramMaps.put("node_level",3);
                openbeforetwentyoneDao.updateSevenLight(paramMaps);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //过滤前端传递的参数map
    public void filterMap(Map<Object,Object> map){
        Map<Object, Object> resultMap = new HashMap<>();
        for (Map.Entry<Object,Object> entry:map.entrySet()){
            String value=entry.getValue()+"";
            if(!"".equals(value)&&!"null".equalsIgnoreCase(value)){
                resultMap.put(entry.getKey(),entry.getValue());
            }
        }
        map.clear();
        map.putAll(resultMap);
    }
}
