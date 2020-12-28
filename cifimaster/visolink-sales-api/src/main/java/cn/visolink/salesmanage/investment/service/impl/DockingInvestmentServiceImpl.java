package cn.visolink.salesmanage.investment.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.exception.ResultUtil;
import cn.visolink.salesmanage.investment.mapper.DockingInvestmentMapper;
import cn.visolink.salesmanage.investment.service.DockingInvestmentService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yangjie
 * @date 2020-10-26
 */
@Service
public class DockingInvestmentServiceImpl implements DockingInvestmentService {

    private final DockingInvestmentMapper dockingInvestmentMapper;

    @Value("${dockingInvestment.supplyPlanDataValueUrl}")
    String supplyPlanDataValueUrl;
    @Value("${dockingInvestment.salesTargetUrl}")
    String salesTargetUrl;

    @Autowired
    public DockingInvestmentServiceImpl(DockingInvestmentMapper dockingInvestmentMapper) {
        this.dockingInvestmentMapper = dockingInvestmentMapper;
    }

    /**
     * 获取上会版、拿地后数据（投资系统）
     *
     * @param map map
     * @return return
     */
    @Override
    public ResultBody getInvestmentSystemData(Map<String, Object> map) {
        // 根据项目id，获取项目编码
        Map<String, Object> projectCode = dockingInvestmentMapper.getProjectNumByProjectId(map);
        if (CollUtil.isEmpty(projectCode)) {
            return ResultUtil.error(500, "获取投资系统数据失败");
        }

        // 调用投资系统接口
        String supplyPlanDataValueReturnStatus;
        String salesTargetReturnStatus;
        JSONObject supplyPlanDataValueContent;
        JSONObject salesTargetJsonContent;
        List<Map> beforeSupplyPlans;
        List<Map> investmentSupplyPlans;
        List<Map> beforeSignValue;
        List<Map> investmentSignValue;
        try {
            Map<String, Object> projectMap = new HashMap<>(2);
            projectMap.put("projectNum", projectCode.get("projectCode"));
            String supplyPlanDataValueResult = HttpUtil.post(supplyPlanDataValueUrl, getRequestParam(projectMap));
            String salesTargetResult = HttpUtil.post(salesTargetUrl, getRequestParam(projectMap));

            // 格式化 货值接口结果、时间节点和销售目标接口结果
            JSONObject supplyPlanDataValueJsonObject = JSON.parseObject(supplyPlanDataValueResult);
            JSONObject salesTargetJsonObject = JSON.parseObject(salesTargetResult);
            // 返回状态
            supplyPlanDataValueReturnStatus = getReturnStatus(supplyPlanDataValueJsonObject);
            salesTargetReturnStatus = getReturnStatus(salesTargetJsonObject);
            // 响应内容
            supplyPlanDataValueContent = getContent(supplyPlanDataValueJsonObject);
            salesTargetJsonContent = getContent(salesTargetJsonObject);
            // 货值接口，上会班、拿地后数据
            beforeSupplyPlans = supplyPlanDataValueContent.getJSONArray("beforeSupplyPlans").toJavaList(Map.class);
            investmentSupplyPlans = supplyPlanDataValueContent.getJSONArray("investmentSupplyPlans").toJavaList(Map.class);
            // 时间节点和销售目标接口，上会班、拿地后数据
            beforeSignValue = salesTargetJsonContent.getJSONArray("beforeSignValue").toJavaList(Map.class);
            investmentSignValue = salesTargetJsonContent.getJSONArray("investmentSignValue").toJavaList(Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(500, "获取投资系统数据失败");
        }

        if (!StrUtil.equals("S", supplyPlanDataValueReturnStatus, true) &&
                !StrUtil.equals("S", salesTargetReturnStatus, true)) {
            return ResultUtil.error(500, "获取投资系统数据失败");
        }

        // 货值接口结果解析
        Map<String, Map<String, Object>> results = new HashMap<>(16);
        List<Map<String, Object>> resultList = new ArrayList<>(16);
        /*Map<String, Object> shbSk = new HashMap<>(8);
        Map<String, Object> ndhSk = new HashMap<>(8);*/
        String investmentCostLevelName = null;
        if (StrUtil.equals("S", supplyPlanDataValueReturnStatus, true)) {
            // 产品系
            investmentCostLevelName = supplyPlanDataValueContent.getString("investmentCostLevelName").substring(0, 1);

            // 上会版 面积、货值
            for (Map beforeSupplyPlan : beforeSupplyPlans) {
                if (ObjectUtil.isNull(beforeSupplyPlan.get("extnProductType"))) {
                    continue;
                }

                String extnProductType = beforeSupplyPlan.get("extnProductType").toString();
                Map<String, Object> resultMap = results.get(extnProductType);
                if (CollUtil.isEmpty(resultMap)) {
                    HashMap<String, Object> newResultMap = new HashMap<>(16);
                    // 上会版面积
                    newResultMap.put("will_area", getBigDecimal(beforeSupplyPlan, "areaValue"));
                    // 上会版货值
                    newResultMap.put("will_front_value", getBigDecimal(beforeSupplyPlan, "signValue"));
                    // 上会版开盘价格
                    if (BigDecimal.ZERO.compareTo(getBigDecimal(beforeSupplyPlan, "priceValue")) < 0) {
                        newResultMap.put("year2", new BigDecimal("2999"));
                        newResultMap.put("year", getBigDecimal(beforeSupplyPlan, "year"));
                        newResultMap.put("month", getBigDecimal(beforeSupplyPlan, "month"));
                        newResultMap.put("will_front_open_price", getBigDecimal(beforeSupplyPlan, "priceValue").multiply(new BigDecimal("10000")));
                    }
                    // 上会版目标月流量
                    if (BigDecimal.ZERO.compareTo(getBigDecimal(beforeSupplyPlan, "areaValue")) < 0) {
                        Map<String, BigDecimal> areaValueShb = new HashMap<>(64);
                        areaValueShb.put(getBigDecimal(beforeSupplyPlan, "year") + "" +
                                        getBigDecimal(beforeSupplyPlan, "month"),
                                getBigDecimal(beforeSupplyPlan, "areaValue"));
                        newResultMap.put("areaValueShb", areaValueShb);
                    }

                    newResultMap.put("extnProductType", extnProductType);
                    newResultMap.put("will_front_cost_standard", null);
                    results.put(extnProductType, newResultMap);
                } else {
                    // 上会版面积
                    BigDecimal areaValue = getBigDecimal(beforeSupplyPlan, "areaValue");
                    resultMap.put("will_area", NumberUtil.add(areaValue, (BigDecimal) resultMap.get("will_area")));
                    // 上会版货值
                    BigDecimal signValue = getBigDecimal(beforeSupplyPlan, "signValue");
                    resultMap.put("will_front_value", NumberUtil.add(signValue, (BigDecimal) resultMap.get("will_front_value")));
                    // 上会版开盘价格
                    if (timeComparison(beforeSupplyPlan, resultMap)) {
                        resultMap.put("year", getBigDecimal(beforeSupplyPlan, "year"));
                        resultMap.put("month", getBigDecimal(beforeSupplyPlan, "month"));
                        resultMap.put("will_front_open_price", getBigDecimal(beforeSupplyPlan, "priceValue").multiply(new BigDecimal("10000")));
                    }
                    // 上会版目标月流量
                    if (BigDecimal.ZERO.compareTo(getBigDecimal(beforeSupplyPlan, "areaValue")) < 0) {
                        Map<String, BigDecimal> areaValueShb = (Map<String, BigDecimal>) resultMap.get("areaValueShb");
                        if (CollUtil.isEmpty(areaValueShb)) {
                            areaValueShb = new HashMap<>(64);
                            areaValueShb.put(getBigDecimal(beforeSupplyPlan, "year") + "" +
                                            getBigDecimal(beforeSupplyPlan, "month"),
                                    getBigDecimal(beforeSupplyPlan, "areaValue"));
                            resultMap.put("areaValueShb", areaValueShb);
                        } else {
                            BigDecimal bigDecimal = areaValueShb.get(getBigDecimal(beforeSupplyPlan, "year") + "" +
                                    getBigDecimal(beforeSupplyPlan, "month"));
                            if (ObjectUtil.isNull(bigDecimal)) {
                                areaValueShb.put(getBigDecimal(beforeSupplyPlan, "year") + "" +
                                                getBigDecimal(beforeSupplyPlan, "month"),
                                        getBigDecimal(beforeSupplyPlan, "areaValue"));
                            } else {
                                areaValueShb.put(getBigDecimal(beforeSupplyPlan, "year") + "" +
                                                getBigDecimal(beforeSupplyPlan, "month"),
                                        NumberUtil.add(bigDecimal, getBigDecimal(beforeSupplyPlan, "areaValue")));
                            }
                        }
                    }
                }

                // 上会版 首开
                /*getSk(shbSk, beforeSupplyPlan);*/
            }

            // 拿地后 面积、货值
            for (Map investmentSupplyPlan : investmentSupplyPlans) {
                if (ObjectUtil.isNull(investmentSupplyPlan.get("extnProductType"))) {
                    continue;
                }

                String extnProductType = investmentSupplyPlan.get("extnProductType").toString();
                Map<String, Object> resultMap = results.get(extnProductType);
                if (CollUtil.isEmpty(resultMap)) {
                    HashMap<String, Object> newResultMap = new HashMap<>(16);
                    // 拿地后面积
                    newResultMap.put("land_back_area", getBigDecimal(investmentSupplyPlan, "areaValue"));
                    // 拿地后货值
                    newResultMap.put("land_back_value", getBigDecimal(investmentSupplyPlan, "signValue"));
                    // 拿地后开盘价格
                    if (BigDecimal.ZERO.compareTo(getBigDecimal(investmentSupplyPlan, "priceValue")) < 0) {
                        newResultMap.put("year2", getBigDecimal(investmentSupplyPlan, "year"));
                        newResultMap.put("month2", getBigDecimal(investmentSupplyPlan, "month"));
                        newResultMap.put("land_back_open_price", getBigDecimal(investmentSupplyPlan, "priceValue").multiply(new BigDecimal("10000")));
                    }
                    // 拿地后目标月流量
                    if (BigDecimal.ZERO.compareTo(getBigDecimal(investmentSupplyPlan, "areaValue")) < 0) {
                        Map<String, BigDecimal> areaValueShb = new HashMap<>(64);
                        areaValueShb.put(getBigDecimal(investmentSupplyPlan, "year") + "" +
                                        getBigDecimal(investmentSupplyPlan, "month"),
                                getBigDecimal(investmentSupplyPlan, "areaValue"));
                        newResultMap.put("areaValueShb", areaValueShb);
                    }

                    newResultMap.put("extnProductType", extnProductType);
                    newResultMap.put("land_front_cost_standard", null);
                    results.put(extnProductType, newResultMap);
                } else {
                    // 拿地后面积
                    BigDecimal areaValue = getBigDecimal(investmentSupplyPlan, "areaValue");
                    resultMap.put("land_back_area", NumberUtil.add(areaValue, (BigDecimal) resultMap.get("land_back_area")));
                    // 拿地后货值
                    BigDecimal signValue = getBigDecimal(investmentSupplyPlan, "signValue");
                    resultMap.put("land_back_value", NumberUtil.add(signValue, (BigDecimal) resultMap.get("land_back_value")));
                    // 拿地后开盘价格
                    if (timeComparisonNdh(investmentSupplyPlan, resultMap)) {
                        resultMap.put("year2", getBigDecimal(investmentSupplyPlan, "year"));
                        resultMap.put("month2", getBigDecimal(investmentSupplyPlan, "month"));
                        resultMap.put("land_back_open_price", getBigDecimal(investmentSupplyPlan, "priceValue").multiply(new BigDecimal("10000")));
                    }
                    // 拿地后目标月流量
                    if (BigDecimal.ZERO.compareTo(getBigDecimal(investmentSupplyPlan, "areaValue")) < 0) {
                        Map<String, BigDecimal> areaValueNdh = (Map<String, BigDecimal>) resultMap.get("areaValueNdh");
                        if (CollUtil.isEmpty(areaValueNdh)) {
                            areaValueNdh = new HashMap<>(64);
                            areaValueNdh.put(getBigDecimal(investmentSupplyPlan, "year") + "" +
                                            getBigDecimal(investmentSupplyPlan, "month"),
                                    getBigDecimal(investmentSupplyPlan, "areaValue"));
                            resultMap.put("areaValueNdh", areaValueNdh);
                        } else {
                            BigDecimal bigDecimal = areaValueNdh.get(getBigDecimal(investmentSupplyPlan, "year") + "" +
                                    getBigDecimal(investmentSupplyPlan, "month"));
                            if (ObjectUtil.isNull(bigDecimal)) {
                                areaValueNdh.put(getBigDecimal(investmentSupplyPlan, "year") + "" +
                                                getBigDecimal(investmentSupplyPlan, "month"),
                                        getBigDecimal(investmentSupplyPlan, "areaValue"));
                            } else {
                                areaValueNdh.put(getBigDecimal(investmentSupplyPlan, "year") + "" +
                                                getBigDecimal(investmentSupplyPlan, "month"),
                                        NumberUtil.add(bigDecimal, getBigDecimal(investmentSupplyPlan, "areaValue")));
                            }
                        }
                    }
                }

                // 拿地后 首开
                /*getSk(ndhSk, investmentSupplyPlan);*/
            }

            // 上会版、拿地后 均价
            resultList = new ArrayList<>(results.values());
            for (Map<String, Object> result : resultList) {
                // 上会版均价
                result.put("will_avg_price", NumberUtil.div(NumberUtil.mul((BigDecimal) result.get("will_front_value"), 10000), (BigDecimal) result.get("will_area"), 2));
                // 拿地后均价
                result.put("land_back_avg_price", NumberUtil.div(NumberUtil.mul((BigDecimal) result.get("land_back_value"), 10000), (BigDecimal) result.get("land_back_area"), 2));
                // 上会版目标月流量
                Map<String, BigDecimal> areaValueShb = (Map<String, BigDecimal>) result.get("areaValueShb");
                List<BigDecimal> areaValueShbList = new ArrayList<>(areaValueShb.values());
                if (CollUtil.isEmpty(areaValueShbList)) {
                    result.put("will_front_avg_flow", BigDecimal.ZERO);
                } else {
                    result.put("will_front_avg_flow", areaValueShbList.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(areaValueShb.size()), 2, BigDecimal.ROUND_HALF_UP));
                }
                // 拿地后目标月流量
                Map<String, BigDecimal> areaValueNdh = (Map<String, BigDecimal>) result.get("areaValueNdh");
                List<BigDecimal> areaValueNdhList = new ArrayList<>(areaValueNdh.values());
                if (CollUtil.isEmpty(areaValueNdhList)) {
                    result.put("land_back_avg_flow", BigDecimal.ZERO);
                } else {
                    result.put("land_back_avg_flow", areaValueNdhList.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(areaValueNdh.size()), 2, BigDecimal.ROUND_HALF_UP));
                }
            }
        }

        // 时间节点和销售目标接口结果解析
        Map<String, Object> timeNode = new HashMap<>(8);
        Map<String, Object> cost = new HashMap<>(8);
        Map<Object, Map<String, Object>> sales = new HashMap<>(8);
        if (StrUtil.equals("S", salesTargetReturnStatus, true)) {
            // 摘牌时间
            timeNode.put("delisting_time", DateUtil.parseDate((String) salesTargetJsonContent.get("auctionTime")).getTime());
            // 开盘时间
            timeNode.put("open_time", DateUtil.parseDate((String) salesTargetJsonContent.get("investmentFirstOpeningTime")).getTime());
            // 上会版营销费用(万元)
            cost.put("will_price", salesTargetJsonContent.get("beforeMarketingFee"));
            // 上会版营销费率(%)
            cost.put("will_per", salesTargetJsonContent.get("beforeMarketingRate"));
            // 拿地后营销费用(万元)
            cost.put("land_back_price", salesTargetJsonContent.get("investmentMarketingFee"));
            // 拿地后营销费率(%)
            cost.put("land_per", salesTargetJsonContent.get("investmentMarketingRate"));

            // 上会版销售目标
            for (Map dataValue : beforeSignValue) {
                HashMap<String, Object> sale = new HashMap<>(8);
                sale.put("sales_time", dataValue.get("year"));
                sale.put("will_price", BigDecimal.ZERO);
                /*sale.put("will_price", dataValue.get("dataValue"));*/
                sales.put(dataValue.get("year"), sale);
            }
            // 拿地后销售目标
            for (Map dataValue : investmentSignValue) {
                Map<String, Object> sale = sales.get(dataValue.get("year"));
                if (CollUtil.isEmpty(sale)) {
                    sale = new HashMap<>(8);
                    sale.put("sales_time", dataValue.get("year"));
                    sale.put("land_back_price", BigDecimal.ZERO);
                    /*sale.put("land_back_price", dataValue.get("dataValue"));*/
                    sales.put(dataValue.get("year"), sale);
                } else {
                    sale.put("land_back_price", BigDecimal.ZERO);
                    /*sale.put("land_back_price", dataValue.get("dataValue"));*/
                }
            }
        }

        // 首开销售目标
        Map<String, Object> sale = new HashMap<>(8);
        sale.put("sales_time", "首开");
        sale.put("will_price", BigDecimal.ZERO);
        sale.put("land_back_price", BigDecimal.ZERO);
        /*if (CollUtil.isNotEmpty(shbSk)) {
            sale.put("will_price", shbSk.get("signValue"));
        }
        if (CollUtil.isNotEmpty(ndhSk)) {
            sale.put("land_back_price", ndhSk.get("signValue"));
        }*/
        sales.put("首开", sale);
        List<Map<String, Object>> salesList = new ArrayList<>(sales.values());

        // 返回数据结构调整
        // 获取业态名称
        Map<String, Map<String, Object>> tollers = new HashMap<>(8);
        List<Map<String, Object>> dictNameList = dockingInvestmentMapper.getDictName();
        for (Map<String, Object> dictName : dictNameList) {
            for (Map<String, Object> result : resultList) {
                if (ObjectUtil.equal(dictName.get("DictCode"), result.get("extnProductType"))) {
                    result.put("product_type", dictName.get("DictName"));
                    result.put("operation_type", dictName.get("fDictName"));
                    result.remove("areaValueShb");
                    result.remove("areaValueNdh");

                    Map<String, Object> toller = tollers.get(dictName.get("DictCode").toString());
                    if (CollUtil.isEmpty(toller)) {
                        List<Map<String, Object>> child = new ArrayList<>();
                        child.add(result);

                        toller = new HashMap<>(8);
                        toller.put("operation_type", dictName.get("fDictName"));
                        toller.put("child", child);

                        tollers.put(dictName.get("DictCode").toString(), toller);
                    } else {
                        List<Map<String, Object>> child = (List<Map<String, Object>>) toller.get("child");
                        child.add(result);
                    }
                }
            }
        }

        // 封装返回结果
        Map<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("tollerMap", new ArrayList<>(tollers.values()));
        resultMap.put("timeNode", timeNode);
        resultMap.put("cost", cost);
        resultMap.put("sales", salesList);
        resultMap.put("product_set", investmentCostLevelName);

        return ResultUtil.success(resultMap);
    }

    /**
     * 获取首开签约
     *
     * @param skMap skMap
     * @param map   map
     */
    private void getSk(Map<String, Object> skMap, Map map) {
        // 首开
        if (BigDecimal.ZERO.compareTo(getBigDecimal(map, "signValue")) < 0) {
            if (CollUtil.isEmpty(skMap)) {
                skMap.put("year", getBigDecimal(map, "year"));
                skMap.put("month", getBigDecimal(map, "month"));
                skMap.put("signValue", getBigDecimal(map, "signValue"));
            } else {
                if (skTimeComparison(map, skMap) == 1) {
                    // 小于
                    skMap.put("year", getBigDecimal(map, "year"));
                    skMap.put("month", getBigDecimal(map, "month"));
                    skMap.put("signValue", getBigDecimal(map, "signValue"));
                } else if (skTimeComparison(map, skMap) == 0) {
                    // 等于
                    BigDecimal signValue = getBigDecimal(map, "signValue");
                    skMap.put("signValue", NumberUtil.add(signValue, (BigDecimal) skMap.get("signValue")));
                }
            }
        }
    }

    /**
     * 获取响应内容
     *
     * @param jsonObject jsonObject
     * @return return
     */
    private JSONObject getContent(JSONObject jsonObject) {
        return jsonObject.getJSONObject("resultInfo").getJSONObject("body").getJSONObject("content");
    }

    /**
     * 获取返回状态
     *
     * @param jsonObject jsonObject
     * @return return
     */
    private String getReturnStatus(JSONObject jsonObject) {
        return (String) jsonObject.getJSONObject("esbInfo").get("returnStatus");
    }

    /**
     * 生成请求参数
     *
     * @param projectMap projectMap
     */
    private String getRequestParam(Map<String, Object> projectMap) {
        Map<String, Object> requestMap = new HashMap<>(4);
        Map<String, Object> esbInfoMap = new HashMap<>(8);
        esbInfoMap.put("instId", "31be626ab2674c3181cbc3d68000c78c");
        esbInfoMap.put("requestTime", DateUtil.now());
        esbInfoMap.put("attr1", "");
        esbInfoMap.put("attr2", "");
        esbInfoMap.put("attr3", "");
        Map<String, Object> requestInfoMap = new HashMap<>(2);
        requestInfoMap.put("project", projectMap);
        requestMap.put("esbInfo", esbInfoMap);
        requestMap.put("requestInfo", requestInfoMap);
        return JSON.toJSONString(requestMap);
    }

    /**
     * 开盘价格时间比较
     *
     * @param map       map
     * @param resultMap resultMap
     * @return return
     */
    private boolean timeComparison(Map map, Map<String, Object> resultMap) {
        if (BigDecimal.ZERO.compareTo(getBigDecimal(map, "priceValue")) < 0) {
            if (getBigDecimal(map, "year").compareTo((BigDecimal) resultMap.get("year")) < 0) {
                return true;
            }

            return getBigDecimal(map, "year").compareTo((BigDecimal) resultMap.get("year")) == 0 &&
                    getBigDecimal(map, "month").compareTo((BigDecimal) resultMap.get("month")) < 0;
        }
        return false;
    }

    /**
     * 开盘价格时间比较，拿地后
     *
     * @param map       map
     * @param resultMap resultMap
     * @return return
     */
    private boolean timeComparisonNdh(Map map, Map<String, Object> resultMap) {
        if (BigDecimal.ZERO.compareTo(getBigDecimal(map, "priceValue")) < 0) {
            if (getBigDecimal(map, "year").compareTo((BigDecimal) resultMap.get("year2")) < 0) {
                return true;
            }

            return getBigDecimal(map, "year").compareTo((BigDecimal) resultMap.get("year2")) == 0 &&
                    getBigDecimal(map, "month").compareTo((BigDecimal) resultMap.get("month2")) < 0;
        }
        return false;
    }

    /**
     * 首开价格时间比较
     *
     * @param map       map
     * @param resultMap resultMap
     * @return return
     */
    private int skTimeComparison(Map map, Map<String, Object> resultMap) {
        if (getBigDecimal(map, "year").compareTo((BigDecimal) resultMap.get("year")) < 0) {
            return 1;
        }

        if (getBigDecimal(map, "year").compareTo((BigDecimal) resultMap.get("year")) == 0) {
            if (getBigDecimal(map, "month").compareTo((BigDecimal) resultMap.get("month")) < 0) {
                return 1;
            } else if (getBigDecimal(map, "month").compareTo((BigDecimal) resultMap.get("month")) == 0) {
                return 0;
            }
        }

        return -1;
    }

    /**
     * 获取 BigDecimal
     *
     * @param map  map
     * @param name name
     * @return return
     */
    private BigDecimal getBigDecimal(Map map, String name) {
        BigDecimal result;
        try {
            result = new BigDecimal(map.get(name).toString());
        } catch (Exception e) {
            result = BigDecimal.ZERO;
        }
        return result;
    }
}
