package cn.visolink.salesmanage.pricing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.XML;
import cn.visolink.exception.ResultBody;
import cn.visolink.exception.ResultUtil;
import cn.visolink.firstplan.message.dao.MessageManagerDao;
import cn.visolink.salesmanage.packageanddiscount.dao.PackageanddiscountDao;
import cn.visolink.salesmanage.pricing.dao.PricingMapper;
import cn.visolink.salesmanage.pricing.entity.PricingAttached;
import cn.visolink.salesmanage.pricing.mapper.PricingAttachedDao;
import cn.visolink.salesmanage.pricing.service.PricingService;
import cn.visolink.utils.HttpRequestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author sjl
 * @Created date 2019/11/11 4:00 下午
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class PricingServiceImpl implements PricingService {

    @Autowired
    private PricingMapper pricingMapper;
    @Value("${resultUrl.url}")
    private String resultUrl;
    @Autowired
    private MessageManagerDao messageManagerDao;

    @Value(("${uploadPath}"))
    private String uploadPath;
    @Value(("${relepath}"))
    private String relepath;
    @Autowired
    private PackageanddiscountDao packageanddiscountDao;

    @Resource(name = "jdbcTemplategxc")
    private JdbcTemplate jdbcTemplategxc;

    @Resource(name = "jdbcTemplatemy")
    private JdbcTemplate jdbcTemplatemy;

    @Autowired
    private PricingAttachedDao pricingAttachedDao;

    @Value("${pricingC1.controlDateUrl}")
    String controlDateUrl;
    @Value("${pricingC1.dtjqHzUrl}")
    String dtjqHzUrl;

    /**
     * 定调价：解析明源推送数据接口
     *
     * @param paramMap
     * @return
     */
    @Override
    public Map analysisMyData(Map paramMap) {
        Map<Object, Object> resultMap2 = new HashMap<>();
        try {
            Map<Object, Object> map = new HashMap<>();
            List dataList = (List) paramMap.get("Data");
            //获取推送数据的jsonID
            String jsonID = dataList.get(2) + "";
            map.put("json_id", jsonID);
            //查询该数据是否在本系统已存在
            Map jonisExist = pricingMapper.findJonisExist(map);
            //流程名称
            String BSID = dataList.get(0).toString();
            //流程类型
            String BTID = dataList.get(1).toString();
            //数据标识（jsonID）
            String BOID = dataList.get(2).toString();
            //数据（xml格式数据集）
            String BSXML = dataList.get(3).toString();
            String userName = dataList.get(5).toString();
            cn.hutool.json.JSONObject xmlJSONObj = XML.toJSONObject(BSXML);
            String result = xmlJSONObj.getStr("DATA");
            JSONObject jsonObject = JSONObject.parseObject(result);
            String projectCode = jsonObject.get("KdProjCode") + "";
            //根据projectcode查询项目id
            Map projectData = pricingMapper.getProjectData(projectCode);
            if (projectData != null && projectData.size() > 0) {
                String project_id = projectData.get("project_id") + "";
                map.put("project_id", project_id);
                map.put("stage_id", project_id);
            }
            map.put("flow_type", BSID);
            map.put("flow_code", BTID);
            map.put("json_id", BOID);
            String dtjjType = jsonObject.get("DTjType") + "";
            if ("调表价".equals(dtjjType)) {
                map.put("flow_version", 1);
            }

            map.put("flow_json", result);
            map.put("userName", userName);
            String baseId = UUID.randomUUID().toString();
            Map resultMap = gettingData(map, result);
            resultMap.put("editor", userName);
            resultMap.put("creator", userName);
            //系统数据已存在，修改
            if (jonisExist != null && jonisExist.size() > 0) {
                //获取系统数据主键id
                String id = jonisExist.get("id") + "";
                resultMap.put("id", id);
                pricingMapper.updateData(resultMap);
                resultMap.put("id", jonisExist.get("base_id") + "");
                String baseid = jonisExist.get("base_id") + "";
                if (baseid == null || "".equals(baseId) || "null".equalsIgnoreCase(baseId)) {
                    resultMap.put("id", baseId);
                    pricingMapper.insertDecideMasterData(resultMap);
                } else {
                    pricingMapper.updateDecideMasterData(resultMap);
                }
                //明源推送数据在本系统不存在记录
            } else {
                //获取主数据id
                resultMap.put("base_id", baseId);
                resultMap.put("id", UUID.randomUUID().toString());
                pricingMapper.saveData(resultMap);
                resultMap.put("id", baseId);
                pricingMapper.insertDecideMasterData(resultMap);
            }
            resultMap2.put("code", "1");
            resultMap2.put("msg", "发起成功");
            paramMap.put("Result", resultMap2.toString());
            return paramMap;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            resultMap2.put("code", "0");
            resultMap2.put("msg", "数据解析失败!");
            paramMap.put("Result", resultMap2.toString());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return paramMap;
        }
    }

    /**
     * 定调价数据渲染接口
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody getApplyData(Map map) {
        ResultBody<Object> resultBody = new ResultBody<>();
        //获取工作流数据
        String boid = map.get("BOID") + "";
        if (boid == null || "".equals(boid) || "null".equalsIgnoreCase(boid)) {
            resultBody.setCode(-10045);
            resultBody.setMessages("没有查询到该审批单，请重新发起审批!");
            return resultBody;
        }
        boolean flag = false;
        Map flowData = pricingMapper.getApplyData(map);
        //获取明源推送数据
        String jsonData = flowData.get("jsonData") + "";
        Map josnMap = JSON.parseObject(jsonData, Map.class);
        String bucode = josnMap.get("BUCode") + "";
        if ("hfsyb".equals(bucode) || "SHSYB".equals(bucode) || "CQSYB".equals(bucode)
                || "SNSYB".equals(bucode) || "ZJSYB".equals(bucode)) {
            flag = true;
        } else {
            flag = false;
        }
        //获取本系统主数据
        Map<String, Object> baseData = pricingMapper.getPricingBaseData(flowData);
        if (baseData == null) {
            baseData = new HashMap<>();
        }

        Map filterMap = filterMap(baseData);
        baseData.clear();
        baseData.putAll(filterMap);

        //查询金蝶项目id
        String jdProjID = packageanddiscountDao.getJDProjectID(flowData.get("project_id") + "");
        baseData.put("project_id", jdProjID);
        List<Object> messageList = new ArrayList<>();
        if (baseData != null && baseData.size() > 0) {
            //判断查询出的数据是否包含该数据值
            if (baseData.containsKey("dpcZgbValue") &&
                    baseData.containsKey("thisPricingValue")) {

                //获取当批次定调价货值 A
                double thisPricingValue = Double.parseDouble(baseData.get("thisPricingValue") + "");
                //获取单批次战规矩版预计实现货值 B
                double theprojectedValue = Double.parseDouble(baseData.get("dpcZgbValue") + "");
                //整盘预计实现货值B-整盘定价前货值G
                double zpValue = thisPricingValue - theprojectedValue;
                if (theprojectedValue == 0 || theprojectedValue == 0.00) {
                    theprojectedValue = 0;
                }
                double countNumber = getCountNumber(zpValue);
                //得到单批次定调价货值折损率
                if (countNumber < 0) {
                    //被除数不能为0
                    Double dpc_syL = counterData(zpValue / theprojectedValue);
                    //对标战规版本批次货值减损
                    Map<Object, Object> messageMap1 = new HashMap<>();
                    //如果是区域集团
                    if (flag) {
                        if ((-dpc_syL) >= 5) {
                            if (zpValue < 0) {
                                messageMap1.put("thisPricingValue", "对标战规版本批次货值减损" +
                                        -countNumber + "万元，本批次货值折损率为" +
                                        -dpc_syL + "%；本批次货值折损≥5%时，系统将红灯预警");
                                messageList.add(messageMap1);
                            } else {
                                messageMap1.put("thisPricingValue", "对标战规版本批次货值减损" +
                                        countNumber + "万元，本批次货值折损率为" +
                                        -dpc_syL + "%；本批次货值折损≥5%时，系统将红灯预警");
                                messageList.add(messageMap1);
                            }
                        }
                    } else {
                        if ((-dpc_syL) >= 3) {
                            if (zpValue < 0) {
                                messageMap1.put("thisPricingValue", "对标战规版本批次货值减损" +
                                        -countNumber + "万元，本批次货值折损率为" +
                                        -dpc_syL + "%；本批次货值折损≥3%时，系统将红灯预警");
                                messageList.add(messageMap1);
                            } else {
                                messageMap1.put("thisPricingValue", "对标战规版本批次货值减损" +
                                        countNumber + "万元，本批次货值折损率为" +
                                        -dpc_syL + "%；本批次货值折损≥3%时，系统将红灯预警");
                                messageList.add(messageMap1);
                            }
                        }
                    }
                }
            }

            if (baseData.containsKey("theprojectedValue") &&
                    baseData.containsKey("quietValue")) {
                //获取整盘预计实现货值B
                double theprojectedValue = Double.parseDouble(baseData.get("theprojectedValue") + "");
                //获取整盘战规版货值C
                double quietValue = Double.parseDouble(baseData.get("quietValue") + "");
                if (quietValue == 0 || quietValue == 0.00) {
                    quietValue = 0;
                }
                //整盘预计实现货值B-整盘战规版货值C=对标战规版整盘货值减损
                double zgb_hzzs = theprojectedValue - quietValue;
                double countNumber = getCountNumber(zgb_hzzs);
                //被除数不为0
                if (quietValue != 0) {
                    //整盘货值折损率(B-C)/C
                    double zphz_zsl = counterData((zgb_hzzs / quietValue));
                    //Double doubleValue = (zgb_hzzs);
                    //int intValue = doubleValue.intValue();
                    Map<Object, Object> messageMap2 = new HashMap<>();
                    if (countNumber < 0) {
                        //区域集团
                        if (flag) {
                            if ((-zphz_zsl) >= 2) {
                                if (zgb_hzzs < 0) {
                                    messageMap2.put("theprojectedValue", "对标战规版整盘货值减损 " +
                                            -countNumber + "万元，整盘货值折损率为" +
                                            -zphz_zsl + "%；整盘货值折损≥2%时，系统将红灯预警");
                                    messageList.add(messageMap2);
                                } else {
                                    messageMap2.put("theprojectedValue", "对标战规版整盘货值减损 " +
                                            countNumber + "万元，整盘货值折损率为" +
                                            -zphz_zsl + "%；整盘货值折损≥2%时，系统将红灯预警");
                                    messageList.add(messageMap2);
                                }
                            }
                        } else {
                            if ((-zphz_zsl) > 0) {
                                if (countNumber < 0) {
                                    messageMap2.put("theprojectedValue", "对标战规版整盘货值减损 " +
                                            -countNumber + "万元，整盘货值折损率为" +
                                            -zphz_zsl + "%；整盘货值折损>0%时，系统将红灯预警");
                                    messageList.add(messageMap2);
                                } else {
                                    messageMap2.put("theprojectedValue", "对标战规版整盘货值减损 " +
                                            countNumber + "万元，整盘货值折损率为" +
                                            -zphz_zsl + "%；整盘货值折损>0%时，系统将红灯预警");
                                    messageList.add(messageMap2);
                                }

                            }
                        }

                    }
                }


            }

            if (baseData.containsKey("theprojectedProfitPrice")
                    && baseData.containsKey("quietProfitPrice")) {
                //整盘预计实现利润额F
                double theprojectedValue = Double.parseDouble(baseData.get("theprojectedProfitPrice") + "");
                //获取整盘战规版利润额D
                double quietValue = Double.parseDouble(baseData.get("quietProfitPrice") + "");
                //对标战规版整盘利润额折损F-D
                double zgb_lre = theprojectedValue - quietValue;
                double countNumber = getCountNumber(zgb_lre);
                if (zgb_lre < 0) {
                    Map<Object, Object> messageMap3 = new HashMap<>();
                    if (flag) {
                        if ((-zgb_lre) >= 3000) {
                            messageMap3.put("theprojectedProfitPrice", "对标战规版整盘利润额折损 " +
                                    -countNumber + "万元，对标战规版整盘利润折损额≥3000万，系统将红灯预警");
                            messageList.add(messageMap3);
                        }
                    } else {
                        if ((-zgb_lre) >= 1000) {
                            messageMap3.put("theprojectedProfitPrice", "对标战规版整盘利润额折损 " +
                                    -countNumber + "万元，对标战规版整盘利润折损额≥1000万，系统将红灯预警");
                            messageList.add(messageMap3);
                        }
                    }

                }
            }
            if (baseData.containsKey("quietProfitMargin")
                    && baseData.containsKey("theprojectedProfitMargin")) {
                //整盘预计实现利润率d
                double theprojectedValue = Double.parseDouble(baseData.get("theprojectedProfitMargin") + "");
                //获取整盘战规版利润率f
                double quietValue = Double.parseDouble(baseData.get("quietProfitMargin") + "");
                //对标战规版整盘净利率折损
                double zgb_lrv = (theprojectedValue - quietValue);
                if (flag) {
                    if (-zgb_lrv >= 3) {
                        Map<Object, Object> messageMap4 = new HashMap<>();
                        messageMap4.put("theprojectedQuietProfitMargin",
                                "对标战规版整盘净利率折损≥3%时系统将红灯预警");
                        messageList.add(messageMap4);
                    }
                } else {
                    if (-zgb_lrv >= 1) {
                        Map<Object, Object> messageMap4 = new HashMap<>();
                        messageMap4.put("theprojectedQuietProfitMargin",
                                "对标战规版整盘净利率折损≥1%时系统将红灯预警");
                        messageList.add(messageMap4);
                    }
                }

            }
        }


        //获取明源推送的定调价类型
        String dtjtype = josnMap.get("DTjType") + "";
        baseData.put("pricingType", dtjtype);

        //折扣信息数据
        String PriceDiscountInfo = josnMap.get("PriceDiscountInfo") + "";
        isJsonArrayOrObj("PriceDiscountInfo", PriceDiscountInfo, baseData);
        //支付方式信息数据
        String PayFormInfoDetails = josnMap.get("PayFormInfoDetails") + "";
        isJsonArrayOrObj("PayFormInfoDetails", PayFormInfoDetails, baseData);
        //获取一房一价信息
        String PriceDetail = josnMap.get("PriceDetail") + "";
        isJsonArrayOrObj("PriceDetail", PriceDetail, baseData);
        //获取价格提示数据
        String priceInfoDetails = josnMap.get("PriceInfoDetails") + "";
        Map resultMap = isJsonArrayOrObj("PriceInfoDetails", priceInfoDetails, baseData);
        //获取价格对比数据
        String id = resultMap.get("id") + "";
        List<Map> priceComparisonList = pricingMapper.getPriceComparison(id);
        //获取定调价结果数据
        List<Map> pricingResultList = pricingMapper.getPricingResult(id);
        // 获取 货值压力测算
        List<Map> pressureComputeList = pricingMapper.getPressureCompute(id);
        // 获取 价格偏离度
        List<Map> priceDeviationList = pricingMapper.getPriceDeviation(id);
        // 获取 第四、第五、第六部分
        PricingAttached pricingAttached = pricingAttachedDao.selectById(id);
        if (pricingAttached == null) {
            pricingAttached = new PricingAttached();
        }


        //获取一房一价数据
        //String sql="SELECT top 10  *FROM s_TjDetail;";
        //List<Map<String, Object>> priceDetailList = jdbcTemplatemy.queryForList(sql);
        //一房一价数据
        //resultMap.put("oneRoomOnePrice",priceDetailList);

        resultMap.put("priceComparison", priceComparisonList);
        resultMap.put("pricingResult", pricingResultList);
        resultMap.put("pressureCompute", pressureComputeList);
        resultMap.put("priceDeviation", priceDeviationList);
        resultMap.put("pricingAttached", pricingAttached);
        //获取上传的文件数据
        List<Map> fileList = pricingMapper.getFileList(flowData.get("base_id") + "");
        //存放文件数据
        resultMap.put("fileList", fileList);
        // 存放定调价类型
        resultMap.put("TjPlanType", josnMap.get("TjPlanType"));


        resultMap.put("messageArray", messageList);
        //查询本次调价的预警消息
        String flow_status = flowData.get("flow_status") + "";
        if ("3".equals(flow_status) || "4".equals(flow_status)) {
            //查询预警消息
            Map messageMap = messageManagerDao.getMessageByProjectId(boid);
            resultMap.put("messageMap", messageMap);
        }
        resultBody.setData(resultMap);
        resultBody.setMessages("定调价编制数据获取成功!");
        resultBody.setCode(200);
        return resultBody;
    }


    /**
     * 数据计算
     *
     * @param number
     * @return
     */
    public Double counterData(double number) {
        BigDecimal b = new BigDecimal(number * 100);
        DecimalFormat formater = new DecimalFormat("#0.##");
        formater.setRoundingMode(RoundingMode.FLOOR);
        String format = formater.format(b);
        double parseDouble = Double.parseDouble(format);
        return parseDouble;
    }

    /**
     * 获取明源推送的数据信息
     *
     * @param paramStr
     * @return
     */

    public Map gettingData(Map maps, String paramStr) {
        Map map = JSONObject.parseObject(paramStr, Map.class);
        //部门信息
        maps.put("departmentId", map.get("BUCode") + "");
        maps.put("departmentName", map.get("BUName") + "");
        //项目信息
        maps.put("projectName", map.get("ProjName") + "");
        //发起时间
        maps.put("applicantName", map.get("Zdr") + "");
        String ZdDate = map.get("ZdDate") + "";
        if (ZdDate != null && !"".equals(ZdDate) && !"null".equalsIgnoreCase(ZdDate)) {
            maps.put("zddate", map.get("ZdDate") + "");
            maps.put("applicantTime", map.get("ZdDate") + "");
        }
        return maps;
    }


    /**
     * 获取定调价字典数据
     */

    @Override
    public ResultBody getPricingDictoryList(Map map) {
        ResultBody<Object> resultBody = new ResultBody<>();
        List<Map> pricingDictoryList = pricingMapper.getPricingDictoryList(map);
        resultBody.setData(pricingDictoryList);
        return resultBody;
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

    /**
     * 定调价编制接口-/提交审批/保存
     *
     * @return
     */
    @Override
    public ResultBody pricingAuthorizedStrength(Map<String, Object> maps) {

        ResultBody<Object> resultBody = new ResultBody<>();
        try {
            //过滤数据
            Map map = filterMap(maps);
            String isTs = map.get("isTS") + "";
            String userName = map.get("userName") + "";
            String json_id = map.get("json_id") + "";
            //清空OA审批流程数据
            pricingMapper.clearFlowData(json_id);
            //清空主数据
            //获取主数据id
            Map flowIdMap = pricingMapper.getFlowId(json_id);
            //获取主数据id
            String id = flowIdMap.get("id") + "";
            pricingMapper.clearZhuData(id);
            //获取当前发起人的所属组织类型
            String json = flowIdMap.get("flow_json") + "";
            Map jsonMap = JSON.parseObject(json, Map.class);
            String BUCode = jsonMap.get("BUCode") + "";
            map.put("id", id);
            //修改定调价主数据
            pricingMapper.updateDecideMasterData(map);

            //清除价格对比数据及定调价结果数据
            pricingMapper.clearPricingResultData(map);
            // 清除 货值压力测算 和 货值偏差说明
            pricingMapper.clearPressureCompute(map);
            // 清除 价格偏离度
            pricingMapper.clearPriceDeviation(map);
            // 清除 第四、第五、第六部分
            pricingAttachedDao.deleteById(id);

            //获取价格对比数据
            List<Map> priceComparisonList = (List<Map>) map.get("priceComparison");
            if (priceComparisonList != null && priceComparisonList.size() > 0) {
                for (Map priceComparison : priceComparisonList) {
                    priceComparison.put("pricingId", id);
                    priceComparison.put("id", UUID.randomUUID().toString());
                    pricingMapper.insertPricingComparison(priceComparison);
                }
            }
            //获取定调价结果数据
            List<Map> pricingResultList = (List<Map>) map.get("pricingResult");
            if (pricingResultList != null && pricingResultList.size() > 0) {
                for (Map pricingResultMap : pricingResultList) {
                    pricingResultMap.put("id", UUID.randomUUID().toString());
                    pricingResultMap.put("priceItemId", id);
                    pricingMapper.insertPricingResult(pricingResultMap);
                }
            }
            // 获取 货值压力测算数据 并插入
            List<Map> pressureComputeList = (List<Map>) map.get("pressureCompute");
            if (pressureComputeList != null && pressureComputeList.size() > 0) {
                for (Map pressureComputeMap : pressureComputeList) {
                    pressureComputeMap.put("id", UUID.randomUUID().toString());
                    pressureComputeMap.put("priceItemId", id);
                    pricingMapper.insertPressureCompute(pressureComputeMap);
                }
            }
            // 获取 价格偏离度 并插入
            List<Map> priceDeviationList = (List<Map>) map.get("priceDeviation");
            if (priceDeviationList != null && priceDeviationList.size() > 0) {
                for (Map priceDeviationMap : priceDeviationList) {
                    priceDeviationMap.put("id", UUID.randomUUID().toString());
                    priceDeviationMap.put("price_item_id", id);
                    pricingMapper.insertPriceDeviation(priceDeviationMap);
                }
            }
            // 获取 第四、第五、第六部分数据 并插入
            Map pricingAttachedMap = (Map) map.get("pricingAttached");
            PricingAttached pricingAttached = BeanUtil.fillBeanWithMap(pricingAttachedMap, new PricingAttached(), false);
            pricingAttached.setId(id);
            pricingAttachedDao.insert(pricingAttached);

            //判断当前发起审批的部门-区域/事业部
            boolean f = false;
            if ("hfsyb".equals(BUCode) || "SHSYB".equals(BUCode) || "CQSYB".equals(BUCode)
                    || "SNSYB".equals(BUCode) || "ZJSYB".equals(BUCode)) {
                f = true;
            }

            Map<Object, Object> paramMap = new HashMap<>();
            //修改文件关联主数据
            String fileids = map.get("fileList") + "";
            //判定工作流走向条件
            boolean flag1 = false;
            boolean flag2 = false;
            boolean flag3 = false;
            boolean flag4 = false;
            boolean flag5 = false;
            HashMap<Object, Object> fileMap = new HashMap<>();
            //获取定调价审批类型
            String pricingTypeCode = map.get("pricingTypeCode") + "";

            if (fileids != null && !"".equals(fileids) && !"null".equals(fileids)) {
                List<Map> list = (List<Map>) map.get("fileList");
                for (Map fileMaps : list) {
                    fileMap.put("id", fileMaps.get("id") + "");
                    fileMap.put("baseId", id);
                    pricingMapper.updateFileBaseId(fileMap);
                }
            }


            if (map.containsKey("theprojectedValue") &&
                    map.containsKey("quietValue")
            ) {

                //获取整盘预计实现货值B 763751
                double theprojectedValue = Double.parseDouble(map.get("theprojectedValue") + "");
                //获取整盘战规版货值C 833700
                double quietValue = Double.parseDouble(map.get("quietValue") + "");
                //整盘预计实现货值B-整盘战规版货值C=对标战规版整盘货值减损
                double zgb_hzzs = theprojectedValue - quietValue;
                double zphz_zsl = 0;
                if (quietValue == 0 || quietValue == 0.00) {
                    zphz_zsl = 1;
                } else {
                    //整盘货值折损率(B-C)/C
                    zphz_zsl = counterData((zgb_hzzs / quietValue));
                }
                zphz_zsl = -zphz_zsl;
                paramMap.put("zphzZs", zphz_zsl);
                if (f) {
                    if ((zphz_zsl) >= 2) {
                        flag1 = true;
                    }
                    {
                        flag2 = true;
                    }
                } else {
                    if ((zphz_zsl) > 0) {
                        flag1 = true;
                    }
                    {
                        flag2 = true;
                    }
                }

            }


            if (map.containsKey("dpcZgbValue") &&
                    map.containsKey("thisPricingValue")
            ) {
                //获取整盘定价前货值G
                double dpcZgbValue = Double.parseDouble(map.get("dpcZgbValue") + "");//1
                //获取当批次定调价货值 A
                double thisPricingValue = Double.parseDouble(map.get("thisPricingValue") + "");//1

                //整盘预计实现货值B-整盘定价前货值G
                double zpValue = thisPricingValue - dpcZgbValue;
                double dpc_syL = 0;
                if (dpcZgbValue == 0 || dpcZgbValue == 0.00) {
                    dpc_syL = 1;
                } else {
                    //得到单批次定调价货值折损率
                    dpc_syL = counterData(zpValue / dpcZgbValue);
                }
                dpc_syL = -dpc_syL;
                paramMap.put("dpdjZs", dpc_syL);
                if (f) {
                    if (dpc_syL >= 5) {
                        flag3 = true;
                    } else {
                        flag3 = false;
                    }
                } else {
                    if (dpc_syL >= 3) {
                        flag3 = true;
                    } else {
                        flag3 = false;
                    }
                }

            }


            if (map.containsKey("theprojectedProfitPrice")
                    && map.containsKey("quietProfitPrice")
            ) {
                //整盘预计实现利润额F
                double theprojectedValue = Double.parseDouble(map.get("theprojectedProfitPrice") + "");
                //获取整盘战规版利润额D
                double quietValue = Double.parseDouble(map.get("quietProfitPrice") + "");
                //对标战规版整盘利润额折损F-D
                double zgb_lre = theprojectedValue - quietValue;
                zgb_lre = -zgb_lre;
                paramMap.put("dbzgLr", zgb_lre);
                if (f) {
                    if (zgb_lre < 3000) {
                        flag4 = true;
                    } else {
                        flag4 = false;
                    }
                } else {
                    if (zgb_lre < 1000) {
                        flag4 = true;
                    } else {
                        flag4 = false;
                    }
                }

            }

            if (map.containsKey("quietProfitMargin")
                    && map.containsKey("theprojectedProfitMargin")
            ) {
                //整盘预计实现利润率d
                double theprojectedValue = Double.parseDouble(map.get("theprojectedProfitMargin") + "");
                //获取整盘战规版利润率f
                double quietValue = Double.parseDouble(map.get("quietProfitMargin") + "");
                //对标战规版整盘净利率折损
                double zgb_lrv = counterData((theprojectedValue - quietValue)) / 100;
                zgb_lrv = -zgb_lrv;
                paramMap.put("dbzgLl", zgb_lrv);
                if (f) {
                    if (-zgb_lrv < 3) {
                        flag5 = true;
                    } else {
                        flag5 = false;
                    }
                } else {
                    if (-zgb_lrv < 1) {
                        flag5 = true;
                    } else {
                        flag5 = false;
                    }
                }
            }

            //只要当前判定结果中包含整盘货值折损，就移除涨价判定
            if (paramMap.containsKey("zphzZs")) {
                paramMap.remove("zj");
            }

            if (flag1) {
                paramMap.remove("dpdjZs");
                paramMap.remove("dbzgLr");
                paramMap.remove("dbzgLl");
            }


            if (flag2) {
                if (flag3 == false && (flag4 || flag5)) {
                    paramMap.remove("zj");
                } else if (flag3 == false && (flag4 == false || flag5 == false)) {
                    paramMap.remove("zj");
                } else if (flag3) {
                    paramMap.remove("zj");
                }
            }
            if ("价格上调".equals(pricingTypeCode)) {
                paramMap.put("zj", "价格上调");
                paramMap.remove("zphzZs");
                paramMap.remove("dpdjZs");
                paramMap.remove("dbzgLr");
                paramMap.remove("dbzgLl");
                paramMap.remove("dbzgLl");
            } else {
                paramMap.put("zj", pricingTypeCode);
            }

            if ("hfsyb".equals(BUCode)) {
                paramMap.put("orgname", "皖赣");
            } else if ("SHSYB".equals(BUCode)) {
                paramMap.put("orgname", "上海");
            } else if ("BJSYB".equals(BUCode)) {
                paramMap.put("orgname", "华北");
            } else if ("XASYB".equals(BUCode)) {
                paramMap.put("orgname", "西北");
            } else if ("CQSYB".equals(BUCode)) {
                paramMap.put("orgname", "西南");
            } else if ("SNSYB".equals(BUCode)) {
                paramMap.put("orgname", "苏南");
            } else if ("ZJSYB".equals(BUCode)) {
                paramMap.put("orgname", "浙江");
            } else if ("QDSYB".equals(BUCode)) {
                paramMap.put("orgname", "山东");
            } else if ("GZSYB".equals(BUCode) || "NNSYB".equals(BUCode)) {
                paramMap.put("orgname", "广桂");
            } else {
                paramMap.put("orgname", "事业部");
            }
            paramMap.put("id", flowIdMap.get("kid") + "");
            paramMap.put("title", map.get("examineTitle") + "");
            pricingMapper.updateFlowData(paramMap);
            if ("1".equals(isTs)) {
                resultBody.setCode(200);
                resultBody.setMessages("暂存成功!");
                return resultBody;
            }
            resultBody.setCode(200);
            resultBody.setMessages("定调价数据编制成功，请发起工作流审批!");
            return resultBody;

        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            resultBody.setCode(-2045);
            resultBody.setMessages("数据保存失败!");
            return resultBody;
        }
    }

    /**
     * 删除附件
     *
     * @param multipartFile
     * @return
     */
    @Override
    public ResultBody uploadAttachment(MultipartFile multipartFile) {
        ResultBody<Object> resultBody = new ResultBody<>();
        HashMap<Object, Object> resultMap = new HashMap<>();
        try {
            if (!multipartFile.isEmpty()) {
                //获取文件名称
                String originalFilename = multipartFile.getOriginalFilename();
                //获取文件后缀名称
                String hzName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                //获取文件前缀名
                String beforeFileName = originalFilename.substring(0, originalFilename.lastIndexOf("."));

                //获取文件类型
                String contentType = multipartFile.getContentType();
                //获取文件大小
                long fileSize = multipartFile.getSize();
                //重新命名文件
                String fileTime = DateUtil.format(new Date(), "yyyyMMddHHmmss");
                String newFileName = beforeFileName + fileTime + "." + hzName;
                File saveFile = new File(uploadPath + "/pricing");
                if (!saveFile.exists()) {
                    saveFile.mkdirs();
                }
                multipartFile.transferTo(new File(saveFile, newFileName));
                //记录上传信息
                HashMap<Object, Object> paramMap = new HashMap<>();
                String id = UUID.randomUUID().toString();

                paramMap.put("id", id);
                paramMap.put("fileName", originalFilename);
                resultMap.put("name", originalFilename);
                paramMap.put("fileHz", hzName);
                String showName = URLEncoder.encode(originalFilename, "UTF-8");
                paramMap.put("fileUrl", relepath + "/pricing/" + newFileName + "?n=" + showName);
                paramMap.put("fileType", contentType);
                paramMap.put("fileSize", fileSize);
                pricingMapper.saveFile(paramMap);
                resultMap.put("url", relepath + "/pricing/" + newFileName + "?n=" + showName);
                resultMap.put("id", id);

            }
            resultBody.setMessages("文件上传成功!");
            resultBody.setData(resultMap);
            return resultBody;
        } catch (Exception e) {
            e.printStackTrace();
            resultBody.setMessages("文件上传失败，请重新上传!");
            resultBody.setCode(-7089);
            return resultBody;
        }
    }

    //判断json字符串中是Object还是Array
    public Map isJsonArrayOrObj(String key, String json, Map map) {
        if (json != null && json.length() > 0) {
            Map paramMap = JSON.parseObject(json, Map.class);
            String item = paramMap.get("Item") + "";
            Object object = JSON.parse(item);
            if (object instanceof JSONObject) {
                Map maps = JSON.parseObject(item, Map.class);
                map.put(key, maps);
            } else if (object instanceof JSONArray) {
                List list = JSON.parseObject(item, List.class);
                map.put(key, list);
            }
            return map;
        } else {
            return map;
        }

    }

    /**
     * 删除附件
     */
    @Override
    public ResultBody deleteFile(Map map) {
        ResultBody<Object> resultBody = new ResultBody<>();
        try {
            Integer integer = pricingMapper.deleteFile(map.get("id") + "");
            if (integer <= 0) {
                resultBody.setMessages("删除失败，找不到此文件!");
                resultBody.setCode(-20097);
                return resultBody;
            }
            resultBody.setMessages("删除附件成功!");
            resultBody.setCode(200);
            return resultBody;
        } catch (Exception e) {
            resultBody.setMessages("删除附件失败!");
            resultBody.setCode(-20098);
            return resultBody;
        }

    }

    /**
     * 获取定调价-经营对标-货值数据
     */
    @Override
    public ResultBody getBusinessIndicatorsValue(Map map) {
        // 0 封装数据 map
        Map<String, Object> hashMap = new HashMap<>(8);

        // 1.1 获取 整盘（投资版）货值
        String wholePlateInvestmentValueSql = "SELECT ROUND((CASE WHEN SUM(houseCommodityValue) IS NULL AND SUM(parkingNotCommodityValue) IS NULL THEN NULL \n" +
                "ELSE IFNULL(SUM(houseCommodityValue),0) + IFNULL(SUM(parkingNotCommodityValue),0) END) / 10000,2) AS wholePlateInvestmentValue \n" +
                "FROM `v_sman_value_plan_invest` AS t1 INNER JOIN \n" +
                "(SELECT stageCode,MAX(update_time) AS update_time FROM `v_sman_value_plan_invest` \n" +
                "WHERE kingdeeProjectId = '" + map.get("projectId") + "' GROUP BY stageCode) AS t2 \n" +
                "ON t1.kingdeeProjectId = '" + map.get("projectId") + "' AND t1.stageCode = t2.stageCode AND t1.update_time = t2.update_time";
        try {
            Map<String, Object> wholePlateInvestmentValueMap = jdbcTemplategxc.queryForMap(wholePlateInvestmentValueSql);
            hashMap.put("wholePlateInvestmentValue", wholePlateInvestmentValueMap.get("wholePlateInvestmentValue"));
        } catch (Exception e) {
            hashMap.put("wholePlateInvestmentValue", null);
        }

        // 1.2 获取 整盘（战规版）货值
        String quietValueSql = "SELECT ROUND((CASE WHEN SUM(houseCommodityValue) IS NULL AND SUM(parkingNotCommodityValue) IS NULL THEN NULL \n" +
                "ELSE IFNULL(SUM(houseCommodityValue),0) + IFNULL(SUM(parkingNotCommodityValue),0) END) / 10000,2) AS quietValue \n" +
                "FROM `v_sman_value_plan_strategy` AS t1 INNER JOIN \n" +
                "(SELECT stageCode,MAX(update_time) AS update_time FROM `v_sman_value_plan_strategy` \n" +
                "WHERE kingdeeProjectId = '" + map.get("projectId") + "' GROUP BY stageCode) AS t2 \n" +
                "ON t1.kingdeeProjectId = '" + map.get("projectId") + "' AND t1.stageCode = t2.stageCode AND t1.update_time = t2.update_time";
        try {
            Map<String, Object> quietValueMap = jdbcTemplategxc.queryForMap(quietValueSql);
            hashMap.put("quietValue", quietValueMap.get("quietValue"));
        } catch (Exception e) {
            hashMap.put("quietValue", null);
        }

        // 1.3 获取 整盘（动态版）货值
        String actValueSql = "SELECT ROUND((CASE WHEN SUM(houseCommodityValue) IS NULL AND SUM(parkingNotCommodityValue) IS NULL THEN NULL \n" +
                "ELSE IFNULL(SUM(houseCommodityValue),0) + IFNULL(SUM(parkingNotCommodityValue),0) END) / 10000,2) AS actValue \n" +
                "FROM `v_sman_value_plan_dynamic` AS t1 INNER JOIN \n" +
                "(SELECT stageCode,MAX(update_time) AS update_time FROM `v_sman_value_plan_dynamic` \n" +
                "WHERE kingdeeProjectId = '" + map.get("projectId") + "' GROUP BY stageCode) AS t2 \n" +
                "ON t1.kingdeeProjectId = '" + map.get("projectId") + "' AND t1.stageCode = t2.stageCode AND t1.update_time = t2.update_time";
        try {
            Map<String, Object> actValueMap = jdbcTemplategxc.queryForMap(actValueSql);
            hashMap.put("actValue", actValueMap.get("actValue"));
        } catch (Exception e) {
            hashMap.put("actValue", null);
        }

        // 1.4 获取 当批次定调价 货值 (一房一价：DjTotal 调价后底总价)
        String thisPricingValueSql = "SELECT CAST(SUM(DjTotal) / 10000 AS DECIMAL(20,2)) AS thisPricingValue \n" +
                "FROM dotnet_erp60.dbo.VS_XSGL_TjDetail WHERE TjPlanGUID = '" + map.get("TjPlanGUID") + "'";
        try {
            Map<String, Object> thisPricingValueMap = jdbcTemplatemy.queryForMap(thisPricingValueSql);
            hashMap.put("thisPricingValue", thisPricingValueMap.get("thisPricingValue"));
        } catch (Exception e) {
            hashMap.put("thisPricingValue", null);
        }

        // 1.5 获取 累计已签约部分 货值
        String addSignValueSql = "SELECT CAST(SUM(t1.金额) / 10000 AS DECIMAL(20,2)) AS addSignValue \n" +
                "FROM (SELECT SUM(CjRmbTotal) AS 金额 \n" +
                "FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT WHERE ISNULL(CloseReason, '') <> '撤销签约' AND ISNULL(CloseReason, '') <> 'nos退房' \n" +
                "AND ISNULL(CloseReason, '') <> '补差' AND YwgsDate <= GETDATE() AND KINGDEEPROJECTID = '" + map.get("projectId") + "' \n" +
                "UNION ALL SELECT -1 * SUM(CjRmbTotal) AS 金额 \n" +
                "FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT WHERE ISNULL(CloseReason, '') IN('nos退房','退房','换房') \n" +
                "AND CloseDate <= GETDATE() AND KINGDEEPROJECTID = '" + map.get("projectId") + "' \n" +
                "UNION ALL SELECT SUM(BcTotal) AS 金额 FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT \n" +
                "WHERE Status = '激活' AND AuditDate <= GETDATE() AND KINGDEEPROJECTID = '" + map.get("projectId") + "' ) AS t1";
        try {
            Map<String, Object> addSignValueMap = jdbcTemplatemy.queryForMap(addSignValueSql);
            hashMap.put("addSignValue", addSignValueMap.get("addSignValue"));
        } catch (Exception e) {
            hashMap.put("addSignValue", null);
        }

        // 1.6 获取 整盘预计实现 货值
        BigDecimal theprojectedValue = getTheprojectedValue(map);
        hashMap.put("theprojectedValue", theprojectedValue);

        return ResultUtil.success(hashMap);
    }

    /**
     * 获取 整盘预计实现 货值
     *
     * @param map map
     * @return return
     */
    private BigDecimal getTheprojectedValue(Map map) {
        // 获取 明源推送json
        String jsonData = getMYJson(map);
        // 获取 同业态上批次定价集合
        String priceHzInfo = analyticalDataToStr(jsonData, "PriceHzInfo");
        BigDecimal theprojectedValue;
        try {
            theprojectedValue = new BigDecimal(priceHzInfo).divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            theprojectedValue = null;
        }
        return theprojectedValue;
    }

    /**
     * 获取定调价-价格对比数据
     */
    @Override
    public ResultBody getPriceComparisonData(Map map) {
        // 1 获取明源业态 和 业态均价（一房一价：DjTotal 调价后底总价 / BldArea 建筑面积）（车位：以个数作除数）
        String sql = "SELECT Code,Name AS commercial,CAST(SUM(DjTotal) / " +
                "CASE WHEN Name LIKE '车位%' THEN COUNT(*) ELSE (CASE WHEN SUM(BldArea) = 0 THEN NULL ELSE SUM(BldArea) END) END AS DECIMAL(20,2)) AS price " +
                "FROM dotnet_erp60.dbo.VS_XSGL_TjDetail " +
                "WHERE TjPlanGUID = '" + map.get("TjPlanGUID") + "' AND Code IS NOT NULL AND Name IS NOT NULL GROUP BY Code,Name ORDER BY Code";
        List<Map<String, Object>> list = sqlExceptionHandling(sql, jdbcTemplatemy);

        // 2 判断是否为 定价
        if (StrUtil.equals("0", map.get("isPricing").toString())) {
            // 定价
            // 2 获取 同业态战规版首开均价
            /*String zgskAvgPriceSql = "SELECT t2.product_code,t1.avg_price_plan AS zgskAvgPrice FROM `v_sman_sign_plan_dynamic` AS t1 INNER JOIN \n" +
                    "(SELECT CONCAT(product_code,'.',free_type,'.',business_type,'.',pro_type) AS product_code,MIN(sign_plan_month) AS sign_plan_month " +
                    "FROM `v_sman_sign_plan_dynamic` WHERE sign_version_stage = 'strategy' AND kingdee_project_id = '" + map.get("projectId") + "' \n" +
                    "GROUP BY product_code,free_type,business_type,pro_type) AS t2 \n" +
                    "ON CONCAT(t1.product_code,'.',t1.free_type,'.',t1.business_type,'.',t1.pro_type) = t2.product_code \n" +
                    "AND t1.sign_plan_month = t2.sign_plan_month AND t1.kingdee_project_id = '" + map.get("projectId") + "' \n" +
                    "GROUP BY t2.product_code ORDER BY t2.product_code";*/
            String zgskAvgPriceSql = "SELECT t2.product_code,t1.avg_price_plan AS zgskAvgPrice FROM `v_sman_sign_plan_dynamic` AS t1 INNER JOIN \n" +
                    "(SELECT CONCAT(product_code,'.',free_type,'.',business_type,'.',pro_type) AS product_code,MIN(sign_plan_month) AS sign_plan_month " +
                    "FROM `v_sman_sign_plan_dynamic` WHERE kingdee_project_id = '" + map.get("projectId") + "' \n" +
                    "GROUP BY product_code,free_type,business_type,pro_type) AS t2 \n" +
                    "ON CONCAT(t1.product_code,'.',t1.free_type,'.',t1.business_type,'.',t1.pro_type) = t2.product_code \n" +
                    "AND t1.sign_plan_month = t2.sign_plan_month AND t1.kingdee_project_id = '" + map.get("projectId") + "' \n" +
                    "GROUP BY t2.product_code ORDER BY t2.product_code";
            List<Map<String, Object>> zgskAvgPriceList = sqlExceptionHandling(zgskAvgPriceSql, jdbcTemplategxc);

            // 3 获取 同业态规划部分均价
            String ghbfAvgPriectSql = "SELECT t2.product_code AS product_code \n" +
                    ",ROUND((CASE WHEN SUM(plan_stall_price) IS NULL AND SUM(plan_room_price) IS NULL THEN NULL \n" +
                    "ELSE IFNULL(SUM(plan_stall_price),0) + IFNULL(SUM(plan_room_price),0) END) / " +
                    "(CASE WHEN IFNULL(SUM(plan_area),0) = 0 THEN \n" +
                    "CASE WHEN SUM(plan_stall_num) = 0 THEN NULL ELSE SUM(plan_stall_num) END \n" +
                    "ELSE SUM(plan_area) END),2) AS ghbfAvgPriect \n" +
                    "FROM `v_sman_value_report` AS t1 INNER JOIN \n" +
                    "(SELECT CONCAT(product_code,'.',free_type_code,'.',business_type_code,'.',pro_type_code) AS product_code \n" +
                    ",MAX(end_date) AS max_end_date \n" +
                    "FROM `v_sman_value_report` WHERE kingdee_project_id = '" + map.get("projectId") + "' \n" +
                    "GROUP BY product_code,free_type_code,business_type_code,pro_type_code) AS t2 \n" +
                    "ON CONCAT(t1.product_code,'.',t1.free_type_code,'.',t1.business_type_code,'.',t1.pro_type_code) = t2.product_code \n" +
                    "AND t1.end_date = t2.max_end_date AND t1.kingdee_project_id = '" + map.get("projectId") + "' GROUP BY t2.product_code ORDER BY t2.product_code";
            List<Map<String, Object>> ghbfAvgPriectList = sqlExceptionHandling(ghbfAvgPriectSql, jdbcTemplategxc);

            // 封装数据
            for (Map<String, Object> ytMap : list) {
                // 2 封装 同业态战规版首开均价
                ytMap.put("zgskAvgPrice", null);
                for (Map<String, Object> zgskAvgPriceMap : zgskAvgPriceList) {
                    if (ytMap.get("Code").equals(zgskAvgPriceMap.get("product_code"))) {
                        ytMap.put("zgskAvgPrice", zgskAvgPriceMap.get("zgskAvgPrice"));
                    }
                }

                // 3 封装 同业态规划部分均价
                ytMap.put("ghbfAvgPriect", null);
                for (Map<String, Object> ghbfAvgPriectMap : ghbfAvgPriectList) {
                    if (ytMap.get("Code").equals(ghbfAvgPriectMap.get("product_code"))) {
                        ytMap.put("ghbfAvgPriect", ghbfAvgPriectMap.get("ghbfAvgPriect"));
                    }
                }
            }
        } else {
            // 非定价
            // 1 获取 同业态近3月累计已签约均价：明源签约表中近3个月的均价（{CjRmbTotal、BcTotal} 签约金额 / NowBldArea 签约房间面积）
//            String sql2 = "SELECT t1.Code,CAST(SUM(t1.金额) / " +
//                    "(CASE WHEN SUM(t1.面积) = 0 THEN NULL ELSE SUM(t1.面积) END) AS DECIMAL(20,2)) AS tytthreeAvgprice \n" +
//                    "FROM (SELECT Code,SUM(NowBldArea) AS 面积,SUM(CjRmbTotal) AS 金额 \n" +
//                    "FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT WHERE ISNULL(CloseReason, '') <> '撤销签约' AND ISNULL(CloseReason, '') <> 'nos退房' \n" +
//                    "AND ISNULL(CloseReason, '') <> '补差' AND YwgsDate <= GETDATE() AND YwgsDate >= DATEADD(mm, DATEDIFF(mm, 0, DATEADD(month, -3, GETDATE())), 0) \n" +
//                    "AND KINGDEEPROJECTID = '" + map.get("projectId") + "' GROUP BY Code \n" +
//                    "UNION ALL SELECT Code, -1 * SUM(NowBldArea) AS 面积,-1 * SUM(CjRmbTotal) AS 金额 \n" +
//                    "FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT WHERE ISNULL(CloseReason, '') IN('nos退房','退房','换房') \n" +
//                    "AND CloseDate <= GETDATE() AND CloseDate >= DATEADD(mm, DATEDIFF(mm, 0, DATEADD(month, -3, GETDATE())), 0) " +
//                    "AND YwgsDate <= GETDATE() AND YwgsDate >= DATEADD(mm, DATEDIFF(mm, 0, DATEADD(month, -3, GETDATE())), 0) \n" +
//                    "AND KINGDEEPROJECTID = '" + map.get("projectId") + "' GROUP BY Code\n" +
//                    "UNION ALL SELECT Code,0 AS 面积,SUM(BcTotal) AS 金额 FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT \n" +
//                    "WHERE Status = '激活' AND AuditDate <= GETDATE() AND AuditDate >= DATEADD(mm, DATEDIFF(mm, 0, DATEADD(month, -3, GETDATE())), 0) \n" +
//                    "AND KINGDEEPROJECTID = '" + map.get("projectId") + "' GROUP BY Code) AS t1\n" +
//                    "GROUP BY t1.Code ORDER BY t1.Code";
            String sql2 = "SELECT t1.Code,CAST(SUM(t1.金额) / " +
                    "(CASE WHEN SUM(t1.面积) = 0 THEN NULL ELSE SUM(t1.面积) END) AS DECIMAL(20,2)) AS tytthreeAvgprice \n" +
                    "FROM (SELECT Code,SUM(NowBldArea) AS 面积,SUM(CjRmbTotal) AS 金额 \n" +
                    "FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT WHERE ISNULL(CloseReason, '') <> '撤销签约' AND ISNULL(CloseReason, '') <> 'nos退房' \n" +
                    "AND ISNULL(CloseReason, '') <> '补差' AND YwgsDate <= GETDATE() AND YwgsDate >= DATEADD(month, -3, GETDATE()) \n" +
                    "AND KINGDEEPROJECTID = '" + map.get("projectId") + "' GROUP BY Code \n" +
                    "UNION ALL SELECT Code, -1 * SUM(NowBldArea) AS 面积,-1 * SUM(CjRmbTotal) AS 金额 \n" +
                    "FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT WHERE ISNULL(CloseReason, '') IN('nos退房','退房','换房') \n" +
                    "AND CloseDate <= GETDATE() AND CloseDate >= DATEADD(month, -3, GETDATE()) " +
                    "AND YwgsDate <= GETDATE() AND YwgsDate >= DATEADD(month, -3, GETDATE()) \n" +
                    "AND KINGDEEPROJECTID = '" + map.get("projectId") + "' GROUP BY Code\n" +
                    "UNION ALL SELECT Code,0 AS 面积,SUM(BcTotal) AS 金额 FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT \n" +
                    "WHERE Status = '激活' AND AuditDate <= GETDATE() AND AuditDate >= DATEADD(month, -3, GETDATE()) \n" +
                    "AND KINGDEEPROJECTID = '" + map.get("projectId") + "' GROUP BY Code) AS t1\n" +
                    "GROUP BY t1.Code ORDER BY t1.Code";
            List<Map<String, Object>> list2 = sqlExceptionHandling(sql2, jdbcTemplatemy);

            // 2 获取 该批次房源系统单价：从房源表中计算该业态的表均价（OriginalTotal 调价前表总价 / BldArea 建筑面积）
            String sql3 = "SELECT Code,Name AS commercial,CAST(SUM(OriginalTotal) / " +
                    "CASE WHEN Name LIKE '车位%' THEN COUNT(*) ELSE \n" +
                    "(CASE WHEN SUM(BldArea) = 0 THEN NULL ELSE SUM(BldArea) END) END AS DECIMAL(20,2)) AS fyxtDprice \n" +
                    "FROM dotnet_erp60.dbo.VS_XSGL_TjDetail \n" +
                    "WHERE TjPlanGUID = '" + map.get("TjPlanGUID") + "' AND Code IS NOT NULL AND Name IS NOT NULL \n" +
                    "AND (OriginalTotal IS NOT NULL OR OriginalTotal <> 0) GROUP BY Code,Name ORDER BY Code";
            List<Map<String, Object>> list3 = sqlExceptionHandling(sql3, jdbcTemplatemy);

            // 3 同业态待售库存均价（（un_sale_stall_price 存货车位金额 + un_sale_room_price 存货房间金额）/ un_sale_area 存货面积） 和 同业态规划部分均价（类似）
            String sql4 = "SELECT t2.product_code AS product_code \n" +
                    ",ROUND((CASE WHEN SUM(un_sale_stall_price) IS NULL AND SUM(un_sale_room_price) IS NULL THEN NULL \n" +
                    "ELSE IFNULL(SUM(un_sale_stall_price),0) + IFNULL(SUM(un_sale_room_price),0) END) / " +
                    "(CASE WHEN IFNULL(SUM(un_sale_area),0) = 0 THEN \n" +
                    "CASE WHEN SUM(un_sale_stall_num) = 0 THEN NULL ELSE SUM(un_sale_stall_num) END \n" +
                    "ELSE SUM(un_sale_area) END),2) AS tytDskcavgPrice \n" +
                    ",ROUND((CASE WHEN SUM(plan_stall_price) IS NULL AND SUM(plan_room_price) IS NULL THEN NULL \n" +
                    "ELSE IFNULL(SUM(plan_stall_price),0) + IFNULL(SUM(plan_room_price),0) END) / " +
                    "(CASE WHEN IFNULL(SUM(plan_area),0) = 0 THEN \n" +
                    "CASE WHEN SUM(plan_stall_num) = 0 THEN NULL ELSE SUM(plan_stall_num) END \n" +
                    "ELSE SUM(plan_area) END),2) AS tytGhbfjPrice \n" +
                    "FROM `v_sman_value_report` AS t1 INNER JOIN \n" +
                    "(SELECT CONCAT(product_code,'.',free_type_code,'.',business_type_code,'.',pro_type_code) AS product_code \n" +
                    ",MAX(end_date) AS max_end_date \n" +
                    "FROM `v_sman_value_report` WHERE kingdee_project_id = '" + map.get("projectId") + "' \n" +
                    "GROUP BY product_code,free_type_code,business_type_code,pro_type_code) AS t2 " +
                    "ON CONCAT(t1.product_code,'.',t1.free_type_code,'.',t1.business_type_code,'.',t1.pro_type_code) = t2.product_code " +
                    "AND t1.end_date = t2.max_end_date AND t1.kingdee_project_id = '" + map.get("projectId") + "' GROUP BY t2.product_code ORDER BY t2.product_code";
            List<Map<String, Object>> list4 = sqlExceptionHandling(sql4, jdbcTemplategxc);

            // 5 获取 同业态上批次定价（明源传递）
            // 获取 明源推送json
            String jsonData = getMYJson(map);
            // 获取 同业态上批次定价集合
            List<Map> tytToppriceList = analyticalData(jsonData, "PriceRoomPcAvgPriceDetails");

            // 封装属性
            for (Map<String, Object> ytMap : list) {
                // 1 封装 同业态近3月累计已签约均价
                ytMap.put("tytthreeAvgprice", null);
                for (Iterator<Map<String, Object>> iterator = list2.iterator(); iterator.hasNext(); ) {
                    Map<String, Object> next = iterator.next();
                    if (ytMap.get("Code").equals(next.get("Code"))) {
                        ytMap.put("tytthreeAvgprice", next.get("tytthreeAvgprice"));
                        iterator.remove();
                    }
                }

                // 2 封装 该批次房源系统单价
                ytMap.put("fyxtDprice", null);
                for (Iterator<Map<String, Object>> iterator = list3.iterator(); iterator.hasNext(); ) {
                    Map<String, Object> next = iterator.next();
                    if (ytMap.get("Code").equals(next.get("Code"))) {
                        ytMap.put("fyxtDprice", next.get("fyxtDprice"));
                        iterator.remove();
                    }
                }

                // 3 封装 同业态待售库存均价 和 同业态规划部分均价
                ytMap.put("tytDskcavgPrice", null);
                ytMap.put("tytGhbfjPrice", null);
                for (Iterator<Map<String, Object>> iterator = list4.iterator(); iterator.hasNext(); ) {
                    Map<String, Object> next = iterator.next();
                    if (ytMap.get("Code").equals(next.get("product_code"))) {
                        ytMap.put("tytDskcavgPrice", next.get("tytDskcavgPrice"));
                        ytMap.put("tytGhbfjPrice", next.get("tytGhbfjPrice"));
                        iterator.remove();
                    }
                }

                // 5 封装 同业态上批次定价
                ytMap.put("tytTopprice", null);
                for (Map<String, Object> tytToppriceMap : tytToppriceList) {
                    if (ytMap.get("Code").equals(tytToppriceMap.get("Code"))) {
                        ytMap.put("tytTopprice", tytToppriceMap.get("PcAvgPrice"));
                    }
                }
            }
        }

        // 3 获取 同业态战规版整盘均价
        String zgzpAvgPriceListSql = "SELECT t2.layoutID,ROUND((CASE WHEN SUM(houseCommodityValue) IS NULL AND SUM(parkingNotCommodityValue) IS NULL THEN NULL \n" +
                "ELSE IFNULL(SUM(houseCommodityValue),0) + IFNULL(SUM(parkingNotCommodityValue),0) END) / \n" +
                "CASE WHEN layoutName LIKE '车位%' THEN SUM(TotalSaleCount) ELSE SUM(TotalSaleArea) END,2) AS zgzpAvgPrice \n" +
                "FROM `v_sman_value_plan_strategy` AS t1 INNER JOIN \n" +
                "(SELECT CONCAT(layoutID,'.',freeType,'.',businessType,'.',proType) AS layoutID,stageCode,MAX(update_time) AS update_time FROM `v_sman_value_plan_strategy` \n" +
                "WHERE kingdeeProjectId = '" + map.get("projectId") + "' GROUP BY layoutID,freeType,businessType,proType,stageCode) AS t2 \n" +
                "ON t1.kingdeeProjectId = '" + map.get("projectId") + "' AND t1.stageCode = t2.stageCode AND t1.update_time = t2.update_time \n" +
                "AND CONCAT(t1.layoutID,'.',t1.freeType,'.',t1.businessType,'.',t1.proType) = t2.layoutID GROUP BY t2.layoutID";
        List<Map<String, Object>> zgzpAvgPriceList = sqlExceptionHandling(zgzpAvgPriceListSql, jdbcTemplategxc);

        // 4 封装 同业态战规版整盘均价
        for (Map<String, Object> ytMap : list) {
            ytMap.put("zgzpAvgPrice", null);
            for (Map<String, Object> zgzpAvgPriceMap : zgzpAvgPriceList) {
                if (ytMap.get("Code").equals(zgzpAvgPriceMap.get("layoutID"))) {
                    ytMap.put("zgzpAvgPrice", zgzpAvgPriceMap.get("zgzpAvgPrice"));
                }
            }
        }

        return ResultUtil.success(list);
    }

    /**
     * sql 执行异常，返回空集合
     *
     * @param sql          sql
     * @param jdbcTemplate jdbcTemplate
     * @return return
     */
    private List<Map<String, Object>> sqlExceptionHandling(String sql, JdbcTemplate jdbcTemplate) {
        List<Map<String, Object>> list;
        try {
            list = jdbcTemplate.queryForList(sql);
        } catch (DataAccessException e) {
            list = new ArrayList<>();
        }
        return list;
    }

    /**
     * 解析明源特定格式数据
     */
    private List<Map> analyticalData(String jsonData, String paramName) {
        JSONObject jsonObject = JSON.parseObject(jsonData).getJSONObject(paramName);
        if (jsonObject == null) {
            return new ArrayList<>();
        }
        List<Map> list = new ArrayList<>();
        try {
            JSONArray item = jsonObject.getJSONArray("Item");
            list = item.toJavaList(Map.class);
        } catch (Exception e) {
            try {
                Map<String, Object> map = jsonObject.getObject("Item", Map.class);
                list.add(map);
            } catch (Exception e1) {
                return new ArrayList<>();
            }
        }
        return list;
    }

    /**
     * 解析明源特定格式数据，to string
     */
    private String analyticalDataToStr(String jsonData, String paramName) {
        JSONObject jsonObject = JSON.parseObject(jsonData).getJSONObject(paramName);
        if (jsonObject == null) {
            return null;
        }
        String hzAmountAfter;
        try {
            Map<String, Object> map = jsonObject.getObject("Item", Map.class);
            hzAmountAfter = map.get("HzAmountAfter").toString();
        } catch (Exception e) {
            hzAmountAfter = null;
        }
        return hzAmountAfter;
    }

    /**
     * 获取 明源推送json
     */
    private String getMYJson(Map map) {
        Map<String, Object> BOIDMap = new HashMap<>(4);
        BOIDMap.put("BOID", map.get("TjPlanGUID"));
        Map flowData = pricingMapper.getApplyData(BOIDMap);
        return (String) flowData.get("jsonData");
    }

    /**
     * 获取定调价-定调价结果
     */
    @Override
    public ResultBody getPriceAdjustmentResult(Map map) {
        List<Map<String, Object>> list;
        // 判断是否为 定价
        if (StrUtil.equals("0", map.get("isPricing").toString())) {
            // 定调价
            // 1 获取 明源定调价结果数据；调整货量（BldArea 建筑面积）；申请均价（DjTotal 调价后底总价 / BldArea 建筑面积）；调整后货值（DjTotal 调价后底总价）
            String sql = "SELECT t1.*,CAST(t1.DjTotal / 10000 AS DECIMAL(20,2)) AS priceAdafterCargo \n" +
                    ",CAST(t1.DjTotal / t1.priceAdCargo AS DECIMAL(20,2)) AS priceAdsqAvg FROM \n" +
                    "(SELECT bldprdid,(bldname + '(' + Name + ')') AS priceAdObject, \n" +
                    "CAST(CASE WHEN Name LIKE '车位%' THEN COUNT(*) \n" +
                    "ELSE (CASE WHEN SUM(BldArea) = 0 THEN NULL ELSE SUM(BldArea) END) END AS DECIMAL(20,2)) AS priceAdCargo, \n" +
                    "CAST(SUM(DjTotal) AS DECIMAL(20,2)) AS DjTotal \n" +
                    "FROM dotnet_erp60.dbo.VS_XSGL_TjDetail \n" +
                    "WHERE TjPlanGUID = '" + map.get("TjPlanGUID") + "' AND bldprdid IS NOT NULL AND bldname IS NOT NULL \n" +
                    "GROUP BY Name,bldprdid,bldname) AS t1 ORDER BY bldprdid";
            list = sqlExceptionHandling(sql, jdbcTemplatemy);
            if (list == null || list.size() == 0) {
                return ResultUtil.success(list);
            }

            // 3 获取 明源定调价结果数据；调整货量（BldArea 建筑面积）；当前均价（OriginalDjTotal 调价前底总价 / BldArea 建筑面积）；调整前货值（OriginalDjTotal 调价前底总价）
            String sql3 = "SELECT t1.*,CAST(t1.OriginalDjTotal / t1.priceAdCargo AS DECIMAL(20,2)) AS priceAdAvg \n" +
                    ",CAST(t1.OriginalDjTotal / 10000 AS DECIMAL(20,2)) AS priceAdbeforeCargo FROM \n" +
                    "(SELECT bldprdid,(bldname + '(' + Name + ')') AS priceAdObject, \n" +
                    "CAST(CASE WHEN Name LIKE '车位%' THEN COUNT(*) \n" +
                    "ELSE (CASE WHEN SUM(BldArea) = 0 THEN NULL ELSE SUM(BldArea) END) END AS DECIMAL(20,2)) AS priceAdCargo, \n" +
                    "CAST(SUM(OriginalDjTotal) AS DECIMAL(20,2)) AS OriginalDjTotal \n" +
                    "FROM dotnet_erp60.dbo.VS_XSGL_TjDetail \n" +
                    "WHERE TjPlanGUID = '" + map.get("TjPlanGUID") + "' AND bldprdid IS NOT NULL AND bldname IS NOT NULL \n" +
                    "AND OriginalDjTotal IS NOT NULL AND OriginalDjTotal != 0\n" +
                    "GROUP BY Name,bldprdid,bldname) AS t1 ORDER BY bldprdid";
            List<Map<String, Object>> list3 = sqlExceptionHandling(sql3, jdbcTemplatemy);

            DecimalFormat df = new DecimalFormat("#.00");
            // 遍历
            for (Map<String, Object> priceAdsqAvgMap : list) {
                priceAdsqAvgMap.put("priceAdAvg", null);
                priceAdsqAvgMap.put("priceAdbeforeCargo", null);
                for (Map<String, Object> priceAdAvgMap : list3) {
                    if (priceAdsqAvgMap.get("bldprdid").equals(priceAdAvgMap.get("bldprdid"))) {
                        priceAdsqAvgMap.put("priceAdAvg", priceAdAvgMap.get("priceAdAvg"));
                        try {
                            String priceAdbeforeCargo = df.format(new BigDecimal(priceAdAvgMap.get("priceAdAvg").toString()).multiply(new BigDecimal(priceAdsqAvgMap.get("priceAdCargo").toString())).divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP));
                            priceAdsqAvgMap.put("priceAdbeforeCargo", priceAdbeforeCargo);
                        } catch (Exception e) {
                            priceAdsqAvgMap.put("priceAdbeforeCargo", null);
                        }
                    }
                }
            }

            // 2 获取 当前均价：供销存动态版的该楼栋均价（total_stall_price 总车位货值 + total_room_price 总房间货值）/ total_area 总面积
            StringBuilder sb = new StringBuilder();
            for (Map<String, Object> ldMap : list) {
                sb.append("'").append(ldMap.get("bldprdid").toString()).append("',");
            }
            String bldprdids = sb.substring(0, sb.length() - 1);

            String sql2 = "SELECT t1.building_id,ROUND((CASE WHEN SUM(total_stall_price) IS NULL AND SUM(total_room_price) IS NULL THEN NULL \n" +
                    "ELSE IFNULL(SUM(total_stall_price),0) + IFNULL(SUM(total_room_price),0) END) \n" +
                    "/ CASE WHEN IFNULL(SUM(total_area),0) = 0 THEN \n" +
                    "(CASE WHEN SUM(total_stall_num) = 0 THEN NULL ELSE SUM(total_stall_num) END) ELSE SUM(total_area) END,2) AS priceAdAvg \n" +
                    "FROM `v_sman_value_report` AS t1 INNER JOIN \n" +
                    "(SELECT building_id,MAX(end_date) AS max_end_date FROM `v_sman_value_report` WHERE building_id IN(" + bldprdids + ") " +
                    "AND kingdee_project_id = '" + map.get("projectId") + "' GROUP BY building_id) AS t2 " +
                    "ON t1.building_id = t2.building_id AND t1.end_date = t2.max_end_date AND t1.kingdee_project_id = '" + map.get("projectId") + "' \n" +
                    "GROUP BY t1.building_id";
            List<Map<String, Object>> list2 = sqlExceptionHandling(sql2, jdbcTemplategxc);

            // 3 封装数据
            for (Map<String, Object> ldMap : list) {
                if (ldMap.get("priceAdAvg") == null || StrUtil.equals("0.00", ldMap.get("priceAdAvg").toString())) {
                    ldMap.put("priceAdAvg", null);
                    ldMap.put("priceAdbeforeCargo", null);
                    for (Map<String, Object> priceAdAvgMap : list2) {
                        if (ldMap.get("bldprdid").equals(priceAdAvgMap.get("building_id"))) {
                            // 添加 楼栋均价
                            ldMap.put("priceAdAvg", priceAdAvgMap.get("priceAdAvg"));
                            // 添加 调整前货值：当前均价 * 调整货量 / 10000 (万元)，取两位小数
                            try {
                                String priceAdbeforeCargo = df.format(new BigDecimal(priceAdAvgMap.get("priceAdAvg").toString()).multiply(new BigDecimal(ldMap.get("priceAdCargo").toString())).divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP));
                                ldMap.put("priceAdbeforeCargo", priceAdbeforeCargo);
                            } catch (Exception e) {
                                ldMap.put("priceAdbeforeCargo", null);
                            }
                        }
                        // 货值差异：调整后货值-调整前货值（前端计算）
                    }
                }
            }
        } else {
            // 调表价
            // 2、定调价结果中价格调整对象为明源一房一价表中进行归集的楼栋，括号中显示楼栋的业态
            // 3、调整货量：一房一价表中该楼栋下调价房间的面积和（BldArea 建筑面积）
            // 4、当前底均价：一房一价表中该楼栋下调价房间的调价前低总价/总面积（OriginalDjTotal 调价前底总价 / BldArea 建筑面积）
            // 5、当前表均价：一房一价表中该楼栋下调价房间的调价前表总价/总面积（OriginalTotal 调价前表总价 / BldArea 建筑面积）
            // 6、申请表均价：一房一价表中该楼栋下调价房间的调价后表总价/总面积（Total 调价后表总价 / BldArea 建筑面积）
            // 7、调整前表底价关系：当前表均价/当前底均价
            // 8、调整后表底价关系：申请表均价/当前底均价
            // 9、表均价差异：申请表均价-当前表均价
            String sql = "SELECT t1.*,(CASE WHEN t1.CurrentBottomAveragePrice = 0 THEN NULL " +
                    "ELSE LTRIM(CAST(t1.CurrentTableAveragePrice / t1.CurrentBottomAveragePrice * 100 AS DECIMAL(20,2))) + '%' END) AS AdjustBeforeRelation \n" +
                    ",(CASE WHEN t1.CurrentBottomAveragePrice = 0 THEN NULL " +
                    "ELSE LTRIM(CAST(t1.ApplyTableAveragePrice / t1.CurrentBottomAveragePrice * 100 AS DECIMAL(20,2))) + '%' END) AS AdjustAfterRelation \n" +
                    ",CAST(t1.ApplyTableAveragePrice - t1.CurrentTableAveragePrice AS DECIMAL(20,2)) AS TableAveragePriceDifference \n" +
                    "FROM " +
                    "(SELECT (bldname + '(' + Name + ')') AS priceAdObject" +
                    ",CAST(CASE WHEN Name LIKE '车位%' THEN COUNT(*) ELSE SUM(BldArea) END AS DECIMAL(20,2)) AS priceAdCargo,\n" +
                    "CAST((SUM(OriginalDjTotal) " +
                    "/ CASE WHEN Name LIKE '车位%' THEN COUNT(*) ELSE " +
                    "(CASE WHEN SUM(BldArea) = 0 THEN NULL ELSE SUM(BldArea) END) END) AS DECIMAL(20,2)) AS CurrentBottomAveragePrice,\n" +
                    "CAST((SUM(OriginalTotal) " +
                    "/ CASE WHEN Name LIKE '车位%' THEN COUNT(*) ELSE " +
                    "(CASE WHEN SUM(BldArea) = 0 THEN NULL ELSE SUM(BldArea) END) END) AS DECIMAL(20,2)) AS CurrentTableAveragePrice,\n" +
                    "CAST((SUM(Total) " +
                    "/ CASE WHEN Name LIKE '车位%' THEN COUNT(*) ELSE " +
                    "(CASE WHEN SUM(BldArea) = 0 THEN NULL ELSE SUM(BldArea) END) END) AS DECIMAL(20,2)) AS ApplyTableAveragePrice \n" +
                    "FROM dotnet_erp60.dbo.VS_XSGL_TjDetail \n" +
                    "WHERE TjPlanGUID = '" + map.get("TjPlanGUID") + "' AND bldprdid IS NOT NULL AND bldname IS NOT NULL AND Name IS NOT NULL " +
                    "GROUP BY bldprdid,bldname,Name) AS t1 ORDER BY priceAdObject";
            list = jdbcTemplatemy.queryForList(sql);
        }
        return ResultUtil.success(list);
    }

    /**
     * 获取定调价-规划货值压力测算
     */
    @Override
    public ResultBody getPressureMeasurement(Map map) {
        // 1 该业态整盘货值（total_stall_price 总车位货值 + total_room_price 总房间货值） 和 该业态整盘面积（total_area 总面积）
        String sql = "SELECT t2.product_code AS product_code,ROUND(IFNULL(SUM(total_stall_price),0) + IFNULL(SUM(total_room_price),0),2) AS total_price \n" +
                ",ROUND(CASE WHEN IFNULL(SUM(total_area),0) = 0 THEN IFNULL(SUM(total_stall_num),0) ELSE IFNULL(SUM(total_area),0) END,2) AS total_area \n" +
                "FROM `v_sman_value_report` AS t1 INNER JOIN \n" +
                "(SELECT CONCAT(product_code,'.',free_type_code,'.',business_type_code,'.',pro_type_code) AS product_code,MAX(end_date) AS max_end_date \n" +
                "FROM `v_sman_value_report` WHERE kingdee_project_id = '" + map.get("projectId") + "' \n" +
                "GROUP BY product_code,free_type_code,business_type_code,pro_type_code) AS t2 \n" +
                "ON CONCAT(t1.product_code,'.',t1.free_type_code,'.',t1.business_type_code,'.',t1.pro_type_code) = t2.product_code \n" +
                "AND t1.kingdee_project_id = '" + map.get("projectId") + "' AND t1.end_date = t2.max_end_date\n" +
                "GROUP BY t2.product_code ORDER BY t2.product_code";
        List<Map<String, Object>> list = sqlExceptionHandling(sql, jdbcTemplategxc);

        // 2 已定价已售的货值（该业态累计签约金额）
        String sql2 = "SELECT t1.Code,CAST(ISNULL(SUM(t1.金额), 0) AS DECIMAL(20,2)) AS cumulativeContractAmount \n" +
                "FROM (SELECT Code,SUM(CjRmbTotal) AS 金额 \n" +
                "FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT WHERE ISNULL(CloseReason, '') <> '撤销签约' \n" +
                "AND ISNULL(CloseReason, '') <> 'nos退房' AND ISNULL(CloseReason, '') <> '补差' \n" +
                "AND YwgsDate <= GETDATE() AND KINGDEEPROJECTID = '" + map.get("projectId") + "' GROUP BY Code \n" +
                "UNION ALL SELECT Code,-1 * SUM(CjRmbTotal) AS 金额 \n" +
                "FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT WHERE ISNULL(CloseReason, '') IN('nos退房','退房','换房') \n" +
                "AND CloseDate <= GETDATE() AND KINGDEEPROJECTID = '" + map.get("projectId") + "' GROUP BY Code \n" +
                "UNION ALL SELECT Code,SUM(BcTotal) AS 金额 FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT \n" +
                "WHERE Status = '激活' AND AuditDate <= GETDATE() \n" +
                "AND KINGDEEPROJECTID = '" + map.get("projectId") + "' GROUP BY Code) AS t1 " +
                "GROUP BY t1.Code ORDER BY t1.Code";
        List<Map<String, Object>> list2 = sqlExceptionHandling(sql2, jdbcTemplatemy);

        // 3 本次审批中该业态调价的房间货值（OriginalDjTotal 调价前底总价之和）和 本次审批中该业态调价的房间面积（BldArea 建筑面积）
        String sql3 = "SELECT Code,Name AS priceAdObject,CAST(ISNULL(SUM(OriginalDjTotal),0) AS DECIMAL(20,2)) AS tjRoomValue \n" +
                ",CAST(ISNULL(CASE WHEN Name LIKE '车位%' THEN COUNT(*) ELSE SUM(BldArea) END,0) AS DECIMAL(20,2)) AS tjRoomArea \n" +
                "FROM dotnet_erp60.dbo.VS_XSGL_TjDetail \n" +
                "WHERE TjPlanGUID = '" + map.get("TjPlanGUID") + "' AND Code IS NOT NULL AND Name IS NOT NULL \n" +
                "AND OriginalDjTotal > 0 GROUP BY Code,Name ORDER BY Code";
        List<Map<String, Object>> list3 = sqlExceptionHandling(sql3, jdbcTemplatemy);

        // 4 该业态剩余月份
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String format = sdf.format(new Date());
        String sql4 = "SELECT CONCAT(product_code,'.',free_type,'.',business_type,'.',pro_type) AS product_code \n" +
                ",TIMESTAMPDIFF(MONTH,'" + format + "-01',MAX(sign_plan_month)) AS remainingMonth " +
                "FROM `v_sman_sign_plan_dynamic` WHERE kingdee_project_id = '" + map.get("projectId") + "' \n" +
                "GROUP BY product_code,free_type,business_type,pro_type";
        List<Map<String, Object>> list4 = sqlExceptionHandling(sql4, jdbcTemplategxc);

        // 5 本次审批中该业态的房间货值（DjTotal 调价后底总价）和 本次审批中该业态的房间面积（BldArea 建筑面积）
        String sql5 = "SELECT Code,Name AS priceAdObject,CAST(ISNULL(SUM(DjTotal),0) AS DECIMAL(20,2)) AS roomValue \n" +
                ",CAST(ISNULL(CASE WHEN Name LIKE '车位%' THEN COUNT(*) ELSE SUM(BldArea) END,0) AS DECIMAL(20,2)) AS roomArea \n" +
                "FROM dotnet_erp60.dbo.VS_XSGL_TjDetail \n" +
                "WHERE TjPlanGUID = '" + map.get("TjPlanGUID") + "' AND Code IS NOT NULL AND Name IS NOT NULL \n" +
                "GROUP BY Code,Name ORDER BY Code";
        List<Map<String, Object>> list5 = sqlExceptionHandling(sql5, jdbcTemplatemy);

        // 5 已定价未售的货值 和 该业态明源已定价面积（明源传递）
        // 获取 明源推送json
        String jsonData = getMYJson(map);
        // 获取 已定价未售的货值 和 该业态明源已定价面积
        List<Map> oldPricedUnsoldValueList = analyticalData(jsonData, "PriceUnsoldDetails");
        List<Map> oldPricedAreaList = analyticalData(jsonData, "PriceRoomAreaDetails");
        // 同业态累加
        ArrayList<Map> pricedUnsoldValueList = new ArrayList<>();
        for (Map pricedUnsoldValueMap : oldPricedUnsoldValueList) {
            Boolean flag = true;
            for (Map newPricedUnsoldValueMap : pricedUnsoldValueList) {
                if (pricedUnsoldValueMap.get("Code").toString().equals(newPricedUnsoldValueMap.get("Code"))) {
                    BigDecimal SUMDjTotal = null;
                    try {
                        SUMDjTotal = new BigDecimal(pricedUnsoldValueMap.get("SUMDjTotal").toString());
                    } catch (Exception e) {
                        SUMDjTotal = new BigDecimal("0");
                    }
                    BigDecimal newSUMDjTotal = null;
                    try {
                        newSUMDjTotal = new BigDecimal(newPricedUnsoldValueMap.get("SUMDjTotal").toString());
                    } catch (Exception e) {
                        newSUMDjTotal = new BigDecimal("0");
                    }
                    newPricedUnsoldValueMap.put("SUMDjTotal", SUMDjTotal.add(newSUMDjTotal));
                    flag = false;
                }
            }
            if (flag) {
                pricedUnsoldValueList.add(pricedUnsoldValueMap);
            }
        }
        // 同业态累加
        ArrayList<Map> pricedAreaList = new ArrayList<>();
        for (Map pricedAreaMap : oldPricedAreaList) {
            Boolean flag = true;
            for (Map newPricedAreaMap : pricedAreaList) {
                if (pricedAreaMap.get("Code").toString().equals(newPricedAreaMap.get("Code"))) {
                    BigDecimal sumBldArea = null;
                    try {
                        sumBldArea = new BigDecimal(pricedAreaMap.get("SUMBldArea").toString());
                    } catch (Exception e) {
                        sumBldArea = new BigDecimal("0");
                    }
                    BigDecimal newSumBldArea = null;
                    try {
                        newSumBldArea = new BigDecimal(newPricedAreaMap.get("SUMBldArea").toString());
                    } catch (Exception e) {
                        newSumBldArea = new BigDecimal("0");
                    }
                    newPricedAreaMap.put("SUMBldArea", sumBldArea.add(newSumBldArea));
                    flag = false;
                }
            }
            if (flag) {
                pricedAreaList.add(pricedAreaMap);
            }
        }

        DecimalFormat df = new DecimalFormat("0.00");
        // 6 处理数据
        for (Map<String, Object> ytMap : list5) {
            // 本次审批中该业态的房间货值 和 本次审批中该业态的房间面积
            BigDecimal roomValue = new BigDecimal(ytMap.get("roomValue").toString());
            BigDecimal roomArea = new BigDecimal(ytMap.get("roomArea").toString());
            // 本次审批中该业态调价的房间货值 和 本次审批中该业态调价的房间面积
            BigDecimal tjRoomValue = new BigDecimal(0);
            BigDecimal tjRoomArea = new BigDecimal(0);
            // 该业态整盘货值 和 该业态整盘面积
            BigDecimal totalPrice = new BigDecimal(0);
            BigDecimal totalArea = new BigDecimal(0);
            // 已定价已售的货值（该业态累计签约金额）
            BigDecimal cumulativeContractAmount = new BigDecimal(0);
            // 已定价未售的货值 和 该业态明源已定价面积
            BigDecimal pricedUnsoldValue = new BigDecimal(0);
            BigDecimal pricedArea = new BigDecimal(0);
            // 该业态剩余月份
            BigDecimal remainingMonth = new BigDecimal(1);

            // 封装 已定价未售的货值
            for (Map<String, Object> pricedUnsoldValueMap : pricedUnsoldValueList) {
                if (ytMap.get("Code").equals(pricedUnsoldValueMap.get("Code"))) {
                    pricedUnsoldValue = new BigDecimal(pricedUnsoldValueMap.get("SUMDjTotal") == null ? "0" : pricedUnsoldValueMap.get("SUMDjTotal").toString());
                }
            }
            // 封装 该业态明源已定价面积
            for (Map<String, Object> pricedAreaMap : pricedAreaList) {
                if (ytMap.get("Code").equals(pricedAreaMap.get("Code"))) {
                    pricedArea = new BigDecimal(pricedAreaMap.get("SUMBldArea") == null ? "0" : pricedAreaMap.get("SUMBldArea").toString());
                }
            }
            // 封装 该业态整盘货值 和 该业态整盘面积
            for (Map<String, Object> totalMap : list) {
                if (ytMap.get("Code").equals(totalMap.get("product_code"))) {
                    totalPrice = new BigDecimal(totalMap.get("total_price") == null ? "0" : totalMap.get("total_price").toString());
                    totalArea = new BigDecimal(totalMap.get("total_area") == null ? "0" : totalMap.get("total_area").toString());
                }
            }
            // 封装 已定价已售的货值（该业态累计签约金额）
            for (Map<String, Object> cumulativeContractAmountMap : list2) {
                if (ytMap.get("Code").equals(cumulativeContractAmountMap.get("Code"))) {
                    cumulativeContractAmount = new BigDecimal(cumulativeContractAmountMap.get("cumulativeContractAmount") == null ? "0" : cumulativeContractAmountMap.get("cumulativeContractAmount").toString());
                }
            }
            // 封装 该业态剩余月份
            for (Map<String, Object> remainingMonthMap : list4) {
                if (ytMap.get("Code").equals(remainingMonthMap.get("product_code"))) {
                    Integer remainingMonthInt = Integer.parseInt(remainingMonthMap.get("remainingMonth").toString());
                    remainingMonthInt = remainingMonthInt < 1 ? 1 : remainingMonthInt;
                    remainingMonth = new BigDecimal(remainingMonthInt);
                }
            }
            // 本次审批中该业态调价的房间货值 和 本次审批中该业态调价的房间面积
            for (Map<String, Object> roomValueMap : list3) {
                if (ytMap.get("Code").equals(roomValueMap.get("Code"))) {
                    tjRoomValue = new BigDecimal(roomValueMap.get("tjRoomValue") == null ? "0" : roomValueMap.get("tjRoomValue").toString());
                    tjRoomArea = new BigDecimal(roomValueMap.get("tjRoomArea") == null ? "0" : roomValueMap.get("tjRoomArea").toString());
                }
            }

            // 1 调价前规划货值：该业态整盘货值 - 该业态已定价货值（已定价已售的货值+已定价未售的货值） + 本次审批中该业态调价的房间货值
            BigDecimal adjustmentBeforePlannedValue = null;
            try {
                adjustmentBeforePlannedValue = totalPrice.subtract(cumulativeContractAmount).subtract(pricedUnsoldValue).add(tjRoomValue).divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP);
                ytMap.put("adjustmentBeforePlannedValue", df.format(adjustmentBeforePlannedValue));
            } catch (Exception e) {
                ytMap.put("adjustmentBeforePlannedValue", null);
            }

            // 2 预算版货值：（该业态整盘面积 - 该业态明源已定价面积 + 本次审批中该业态调价的房间面积) * 本次审批该业态批次均价（DjTotal 调价后底总价 / BldArea建筑面积）
            BigDecimal budgetValue = null;
            try {
                budgetValue = totalArea.subtract(pricedArea).add(tjRoomArea).multiply(roomValue.divide(roomArea, 3, RoundingMode.HALF_UP)).divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP);
                ytMap.put("budgetValue", df.format(budgetValue));
            } catch (Exception e) {
                ytMap.put("budgetValue", null);
            }

            // 3 货值差异：测算版货值 - 调价前规划货值
            BigDecimal valueDifference = null;
            try {
                valueDifference = budgetValue.subtract(adjustmentBeforePlannedValue);
                ytMap.put("valueDifference", df.format(valueDifference));
            } catch (Exception e) {
                ytMap.put("valueDifference", null);
            }

            // 4 测算货值偏差率：((|测算版-规划货值版|) / 规划货值版) / 该业态剩余月份
            try {
                BigDecimal valueDeviationRate = valueDifference.abs().multiply(new BigDecimal("100")).divide(adjustmentBeforePlannedValue, 3, RoundingMode.HALF_UP).divide(remainingMonth, 2, RoundingMode.HALF_UP);
                ytMap.put("valueDeviationRate", df.format(valueDeviationRate));
            } catch (Exception e) {
                ytMap.put("valueDeviationRate", null);
            }
        }
        return ResultUtil.success(list5);
    }

    /**
     * 获取定调价-所有统计数据
     */
    @Override
    public ResultBody getAllStatistics(Map map) {
        // 1 获取审批状态
        HashMap<String, Object> hashMap = new HashMap<>(4);
        hashMap.put("BOID", map.get("TjPlanGUID"));
        Map flowData = pricingMapper.getApplyData(hashMap);
        if (flowData == null) {
            return ResultUtil.error(-11_1037, "参数异常！");
        }
        // 审批中、审批通过，直接返回
        Object flowStatus = flowData.get("flow_status");
        Map<String, Object> result = new HashMap<>(8);
        if ("3".equals(flowStatus) || "4".equals(flowStatus)) {
            return ResultUtil.success(result);
        }

        // 返回统计数据
        map.put("baseId", flowData.get("base_id"));
        map.put("projectId2", flowData.get("project_id"));
        if (ObjectUtil.equal("1", map.get("isNewEdition")) && ObjectUtil.equal("0", map.get("isPricing"))) {
            // 改版后 且 定价
            return getAllStatisticsRevisionAfter(map, result);
        } else {
            // 改版前 或 调价
            return getAllStatisticsRevisionBefore(map, result);
        }
    }

    /**
     * 改版前 获取定调价-所有统计数据
     *
     * @param map    map
     * @param result result
     * @return return
     */
    private ResultBody getAllStatisticsRevisionBefore(Map map, Map<String, Object> result) {
        // 1 判断是否为 定价
        if (StrUtil.equals("0", map.get("isPricing").toString())) {
            // 定价
            // 2.1 获取定调价-经营对标-货值数据
            ResultBody businessIndicatorsValue = this.getBusinessIndicatorsValue(map);

            // 2.2 获取定调价-价格对比数据
            ResultBody priceComparisonDataFdj = getPriceComparisonDataFdjAndDJ(map);

            // 2.3 获取定调价-规划货值压力测算
            ResultBody pressureMeasurement = this.getPressureMeasurement(map);

            // 封装数据
            result.put("businessIndicatorsValue", businessIndicatorsValue.getData());
            result.put("priceComparisonData", priceComparisonDataFdj.getData());
            result.put("pressureMeasurement", pressureMeasurement.getData());
        }

        // 2 获取定调价-定调价结果
        ResultBody priceAdjustmentResult = this.getPriceAdjustmentResult(map);
        result.put("priceAdjustmentResult", priceAdjustmentResult.getData());

        return ResultUtil.success(result);
    }

    /**
     * 获取定调价-价格对比数据(首开定价 和 首开非定价)
     *
     * @param map
     * @return
     */
    private ResultBody getPriceComparisonDataFdjAndDJ(Map map) {
        // 1 获取定调价-价格对比数据(首开非定价)
        map.put("isPricing", 1);
        ResultBody priceComparisonDataFdj = this.getPriceComparisonData(map);
        // 2 获取定调价-价格对比数据(首开定价)
        map.put("isPricing", 0);
        ResultBody priceComparisonDataDJ = this.getPriceComparisonData(map);
        List<Map<String, Object>> priceComparisonDataFdjList = (List<Map<String, Object>>) priceComparisonDataFdj.getData();
        List<Map<String, Object>> priceComparisonDataDJList = (List<Map<String, Object>>) priceComparisonDataDJ.getData();
        for (Map<String, Object> priceComparisonDataFdjMap : priceComparisonDataFdjList) {
            for (Map<String, Object> priceComparisonDataDJMap : priceComparisonDataDJList) {
                if (StrUtil.equals(priceComparisonDataFdjMap.get("Code").toString(), priceComparisonDataDJMap.get("Code").toString())) {
                    priceComparisonDataFdjMap.put("zgskAvgPrice", priceComparisonDataDJMap.get("zgskAvgPrice"));
                    priceComparisonDataFdjMap.put("ghbfAvgPriect", priceComparisonDataDJMap.get("ghbfAvgPriect"));
                }
            }
        }

        return priceComparisonDataFdj;
    }

    /**
     * 改版后 获取定调价-所有统计数据
     *
     * @param map    map
     * @param result result
     * @return return
     */
    private ResultBody getAllStatisticsRevisionAfter(Map map, Map<String, Object> result) {
        // 1 获取定调价-定调价结果
        ResultBody priceAdjustmentResult = this.getPriceAdjustmentResult(map);
        result.put("priceAdjustmentResult", priceAdjustmentResult.getData());

        // 2 获取定调价-价格对比数据
        ResultBody priceComparisonDataFdj = getPriceComparisonDataFdjAndDJ(map);
        result.put("priceComparisonData", priceComparisonDataFdj.getData());

        // 3 获取价格偏离度，价格预警（第三部分）
        List<Map> three = priceMonitorThirdPart(map, false, priceComparisonDataFdj.getData());
        result.put("priceDeviation", three);

        // 4 获取第四部分指标
        Map<String, Object> pricingAttachedMap = new HashMap<>(8);
        // 获取主数据分期编号
        List<Map> stageCodeList = pricingMapper.getStageCode(map);
        if (CollUtil.isNotEmpty(stageCodeList)) {
            // 获取供销存分期编号
            String getStageCodeGXCSql = "SELECT DISTINCT stageCode FROM v_sman_value_plan_invest WHERE kingdeeProjectId = '" + map.get("projectId") + "'";
            List<Map<String, Object>> getStageCodeGXCList = sqlExceptionHandling(getStageCodeGXCSql, jdbcTemplategxc);

            if (CollUtil.isNotEmpty(getStageCodeGXCList) && getStageCodeGXCList.containsAll(stageCodeList)) {
                // 4.1 投资版货值
                String wholePlateInvestmentValueSql = "SELECT ROUND((CASE WHEN SUM(houseCommodityValue) IS NULL AND SUM(parkingNotCommodityValue) IS NULL THEN NULL \n" +
                        "ELSE IFNULL(SUM(houseCommodityValue),0) + IFNULL(SUM(parkingNotCommodityValue),0) END) / 10000,2) AS wholePlateInvestmentValue \n" +
                        "FROM `v_sman_value_plan_invest` AS t1 INNER JOIN \n" +
                        "(SELECT stageCode,MAX(update_time) AS update_time FROM `v_sman_value_plan_invest` \n" +
                        "WHERE kingdeeProjectId = '" + map.get("projectId") + "' GROUP BY stageCode) AS t2 \n" +
                        "ON t1.kingdeeProjectId = '" + map.get("projectId") + "' AND t1.stageCode = t2.stageCode AND t1.update_time = t2.update_time";
                try {
                    Map<String, Object> wholePlateInvestmentValueMap = jdbcTemplategxc.queryForMap(wholePlateInvestmentValueSql);
                    pricingAttachedMap.put("tzbHz", wholePlateInvestmentValueMap.get("wholePlateInvestmentValue"));
                } catch (Exception e) {
                    pricingAttachedMap.put("tzbHz", null);
                }
            } else {
                pricingAttachedMap.put("tzbHz", null);
            }
        } else {
            pricingAttachedMap.put("tzbHz", null);
        }

        // 4.2 年初版货值
        Map<String, Object> wholePlateInvestmentValue = pricingMapper.getWholePlateInvestmentValue(map);
        if (CollUtil.isNotEmpty(wholePlateInvestmentValue)) {
            pricingAttachedMap.put("ncbHz", wholePlateInvestmentValue.get("wholePlateInvestmentValue"));
        } else {
            pricingAttachedMap.put("ncbHz", null);
        }


        // 4.3 定调价后（整盘预计实现货值）
        BigDecimal dtjhHz = getTheprojectedValue(map);
        pricingAttachedMap.put("dtjhHz", dtjhHz);

        // 4.4 定调价前C1定稿版
        try {
            // 控制日期
            String controlDateResult = HttpRequestUtil.httpGet(controlDateUrl, false);
            JSONObject controlDateJsonObject = JSON.parseObject(controlDateResult);
            Object controlDateStr = controlDateJsonObject.getJSONObject("retData").get("val");
            DateTime controlDate = DateUtil.parse(controlDateStr.toString());

            // 查询日期
            DateTime queryDate = DateUtil.date();
            if (queryDate.before(controlDate)) {
                // 查询日期 小于 控制日期，取 上月最后一天
                queryDate = DateUtil.endOfMonth(DateUtil.lastMonth());
            }

            // 总货值
            String dtjqHzUrl2 = StrUtil.format(dtjqHzUrl, queryDate.toString("yyyyMMdd"), controlDate.toString("yyyyMMdd"), map.get("projectId2"));
            String dtjqHzResult = HttpRequestUtil.httpGet(dtjqHzUrl2, false);
            JSONObject jsonObject = JSON.parseObject(dtjqHzResult);
            Object dtjqHzStr = jsonObject.getJSONArray("retData").getJSONObject(0).get("totalAmt");
            BigDecimal dtjqHz = new BigDecimal(dtjqHzStr.toString()).divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP);
            pricingAttachedMap.put("dtjqHz", dtjqHz);
        } catch (Exception e) {
            e.printStackTrace();
            pricingAttachedMap.put("dtjqHz", null);
        }

        result.put("pricingAttached", pricingAttachedMap);
        return ResultUtil.success(result);
    }

    /**
     * 获取定调价-所有统计数据
     */
    /*@Override
    public ResultBody getAllStatistics(Map map) {
        // 1 判断是不是第一次保存
        HashMap<String, Object> hashMap = new HashMap<>(4);
        hashMap.put("BOID", map.get("TjPlanGUID"));
        Map flowData = pricingMapper.getApplyData(hashMap);
        if (flowData == null) {
            return ResultUtil.error(-11_1037, "参数异常！");
        }
        // 获取 定调价结果数据
        Object flowStatus = flowData.get("flow_status");
        Map<String, Object> result = new HashMap<>(8);
        if ("3".equals(flowStatus) || "4".equals(flowStatus)) {
            return ResultUtil.success(result);
        }

        // 2 判断是否为 定价
        if (StrUtil.equals("0", map.get("isPricing").toString())) {
            // 定价
            // 1 获取定调价-经营对标-货值数据
            ResultBody businessIndicatorsValue = this.getBusinessIndicatorsValue(map);

            // 2 获取定调价-价格对比数据(首开非定价)
            map.put("isPricing", 1);
            ResultBody priceComparisonDataFdj = this.getPriceComparisonData(map);
            // 2 获取定调价-价格对比数据(首开定价)
            map.put("isPricing", 0);
            ResultBody priceComparisonDataDJ = this.getPriceComparisonData(map);
            List<Map<String, Object>> priceComparisonDataFdjList = (List<Map<String, Object>>) priceComparisonDataFdj.getData();
            List<Map<String, Object>> priceComparisonDataDJList = (List<Map<String, Object>>) priceComparisonDataDJ.getData();
            for (Map<String, Object> priceComparisonDataFdjMap : priceComparisonDataFdjList) {
                for (Map<String, Object> priceComparisonDataDJMap : priceComparisonDataDJList) {
                    if (StrUtil.equals(priceComparisonDataFdjMap.get("Code").toString(), priceComparisonDataDJMap.get("Code").toString())) {
                        priceComparisonDataFdjMap.put("zgskAvgPrice", priceComparisonDataDJMap.get("zgskAvgPrice"));
                        priceComparisonDataFdjMap.put("ghbfAvgPriect", priceComparisonDataDJMap.get("ghbfAvgPriect"));
                    }
                }
            }

            // 3 获取定调价-规划货值压力测算
            ResultBody pressureMeasurement = this.getPressureMeasurement(map);

            // 封装数据
            result.put("businessIndicatorsValue", businessIndicatorsValue.getData());
            result.put("priceComparisonData", priceComparisonDataFdj.getData());
            result.put("pressureMeasurement", pressureMeasurement.getData());
        }

        // 3 获取定调价-定调价结果
        ResultBody priceAdjustmentResult = this.getPriceAdjustmentResult(map);
        result.put("priceAdjustmentResult", priceAdjustmentResult.getData());

        return ResultUtil.success(result);
    }*/

    /**
     * 数据计算
     *
     * @param number number
     * @return return
     */
    public double getCountNumber(double number) {
        if (number != 0) {
            DecimalFormat df = new DecimalFormat("#0.00");
            String format = df.format(Double.parseDouble(number + ""));
            double parseDouble = Double.parseDouble(format);
            return parseDouble;
        }
        return 0.00;

    }

    /**
     * 定调价-价格监测
     *
     * @param map map
     * @return return
     */
    @Override
    public ResultBody priceMonitor(Map map) {
        // 获取 第四、第五、第六部分
        PricingAttached pricingAttached = pricingAttachedDao.selectById(map.get("baseId").toString());

        // 1 获取 主数据
        Map data = pricingMapper.priceMonitor(map);
        // 调价后，整盘预计实现货值
        BigDecimal theprojectedValue;
        try {
            if (ObjectUtil.equal("1", map.get("isNewEdition"))) {
                theprojectedValue = new BigDecimal(pricingAttached.getDtjhHz());
            } else {
                theprojectedValue = new BigDecimal(data.get("theprojected_value").toString());
            }
        } catch (Exception e) {
            theprojectedValue = BigDecimal.ZERO;
        }
        // 调价前，整盘货值（动态版）
        BigDecimal actValue;
        try {
            if (ObjectUtil.equal("1", map.get("isNewEdition"))) {
                actValue = new BigDecimal(pricingAttached.getDtjqHz());
            } else {
                actValue = new BigDecimal(data.get("act_value").toString());
            }
        } catch (Exception e) {
            actValue = BigDecimal.ZERO;
        }
        // 调价前，整盘货值（投资版）
        BigDecimal wholePlateInvestmentValue;
        // 获取 整盘（投资版）货值
//        String wholePlateInvestmentValueSql = "SELECT ROUND((CASE WHEN SUM(houseCommodityValue) IS NULL AND SUM(parkingNotCommodityValue) IS NULL THEN NULL \n" +
//                "ELSE IFNULL(SUM(houseCommodityValue),0) + IFNULL(SUM(parkingNotCommodityValue),0) END) / 10000,2) AS wholePlateInvestmentValue \n" +
//                "FROM `v_sman_value_plan_invest` AS t1 INNER JOIN \n" +
//                "(SELECT stageCode,MAX(update_time) AS update_time FROM `v_sman_value_plan_invest` \n" +
//                "WHERE kingdeeProjectId = '" + map.get("projectId") + "' GROUP BY stageCode) AS t2 \n" +
//                "ON t1.kingdeeProjectId = '" + map.get("projectId") + "' AND t1.stageCode = t2.stageCode AND t1.update_time = t2.update_time";
        try {
//            Map<String, Object> wholePlateInvestmentValueMap = jdbcTemplategxc.queryForMap(wholePlateInvestmentValueSql);
            Map<String, Object> wholePlateInvestmentValueMap = pricingMapper.getWholePlateInvestmentValue(map);
            wholePlateInvestmentValue = new BigDecimal(wholePlateInvestmentValueMap.get("wholePlateInvestmentValue").toString());
        } catch (Exception e) {
            wholePlateInvestmentValue = BigDecimal.ZERO;
        }

        // 2 计算 年度整盘累计货值折损
        BigDecimal lossOnFullValue = wholePlateInvestmentValue.subtract(theprojectedValue);
        BigDecimal lossRateOnFullValue;
        try {
            lossRateOnFullValue = lossOnFullValue.multiply(new BigDecimal("100")).divide(wholePlateInvestmentValue, 2, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e) {
            lossRateOnFullValue = BigDecimal.ZERO;
        }
        HashMap<String, Object> one = new HashMap<>(8);
        one.put("NCZPHZ1", wholePlateInvestmentValue);
        one.put("TJHZPHZ1", theprojectedValue);
        one.put("ZSL1", lossRateOnFullValue);
        if (lossOnFullValue.compareTo(new BigDecimal("10000")) > 0 || lossRateOnFullValue.compareTo(new BigDecimal("5")) > 0) {
            one.put("BS", 1);
        } else {
            one.put("BS", 0);
        }

        // 3 计算 本次调价后货值折损
        BigDecimal tjhhzzs = actValue.subtract(theprojectedValue);
        BigDecimal tjhhzzsl;
        try {
            tjhhzzsl = tjhhzzs.multiply(new BigDecimal("100")).divide(actValue, 2, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e) {
            tjhhzzsl = BigDecimal.ZERO;
        }
        HashMap<String, Object> two = new HashMap<>(8);
        two.put("TJQZPHZ2", actValue);
        two.put("TJHZPHZ2", theprojectedValue);
        two.put("ZSL2", tjhhzzsl);
        if (tjhhzzs.compareTo(new BigDecimal("4000")) > 0 || tjhhzzsl.compareTo(new BigDecimal("2")) > 0) {
            two.put("BS", 1);
        } else {
            two.put("BS", 0);
        }

        // 获取价格预警（第三部分）
        List<Map> three = priceMonitorThirdPart(map, true);

        // 8 获取 楼栋范围
        String sql3 = "SELECT building_id FROM `v_sman_value_report` WHERE kingdee_project_id = '" + map.get("projectId") + "' \n" +
                "GROUP BY building_id HAVING CASE WHEN SUM(plan_stall_price) IS NULL THEN 0 ELSE SUM(plan_stall_price) END + \n" +
                "CASE WHEN SUM(plan_room_price) IS NULL THEN 0 ELSE SUM(plan_room_price) END > 0 ORDER BY building_id";
        List<Map<String, Object>> list3 = sqlExceptionHandling(sql3, jdbcTemplategxc);

        // 9 获取 楼栋规划均价
        String sql4 = "SELECT x_JDBldID,x_JDBldName,x_ProductType,CASE WHEN area = 0 THEN 0 ELSE CAST(amount / area AS DECIMAL(20,2)) END AS average_price FROM \n" +
                "(SELECT x_JDBldID,MAX(x_JDBldName) AS x_JDBldName,x_ProductType,CASE WHEN x_ProductType LIKE '车位%' THEN SUM(x_Ts) ELSE SUM(x_Area) END AS area,\n" +
                "SUM(x_Amount) AS amount FROM dotnet_erp60.dbo.VS_XG_HZ \n" +
                "WHERE x_AjustType = '调整后' AND x_JDBldID IS NOT NULL AND x_JDBldID != '' \n" +
                "AND TjPlanGUID = '" + map.get("TjPlanGUID") + "' AND x_kingdeeProjectID = '" + map.get("projectId") + "' " +
                "GROUP BY x_JDBldID,x_ProductType) as t1";
        List<Map<String, Object>> list4 = sqlExceptionHandling(sql4, jdbcTemplatemy);

        // 10 获取 业态规划均价
        String sql5 = "SELECT x_ProductType,CASE WHEN area = 0 THEN 0 ELSE CAST(amount / area AS DECIMAL(20,2)) END AS average_price FROM \n" +
                "(SELECT x_ProductType,CASE WHEN x_ProductType LIKE '车位%' THEN SUM(x_Ts) ELSE SUM(x_Area) END AS area,\n" +
                "SUM(x_Amount) AS amount FROM dotnet_erp60.dbo.VS_XG_HZ \n" +
                "WHERE x_AjustType = '调整后' AND x_JDBldID IS NOT NULL AND x_JDBldID != '' \n" +
                "AND TjPlanGUID = '" + map.get("TjPlanGUID") + "' AND x_kingdeeProjectID = '" + map.get("projectId") + "' " +
                "GROUP BY x_ProductType) as t1";
        List<Map<String, Object>> list5 = sqlExceptionHandling(sql5, jdbcTemplatemy);

        // 循环 赋值
        for (Iterator<Map<String, Object>> iterator = list3.iterator(); iterator.hasNext(); ) {
            Map<String, Object> map3 = iterator.next();

            boolean flag = true;
            // 楼栋均价
            for (int j = 0; j < list4.size(); j++) {
                Map<String, Object> map4 = list4.get(j);
                if (ObjectUtil.equal(map3.get("building_id"), map4.get("x_JDBldID"))) {
                    flag = false;
                    map3.put("building_name", map4.get("x_JDBldName"));
                    BigDecimal buildingAveragePrice;
                    try {
                        buildingAveragePrice = new BigDecimal(map4.get("average_price").toString());
                    } catch (Exception e) {
                        buildingAveragePrice = BigDecimal.ZERO;
                    }
                    map3.put("building_average_price", buildingAveragePrice);

                    // 业态均价
                    for (int k = 0; k < list5.size(); k++) {
                        Map<String, Object> map5 = list5.get(k);
                        if (ObjectUtil.equal(map5.get("x_ProductType"), map4.get("x_ProductType"))) {
                            BigDecimal ytAveragePrice;
                            try {
                                ytAveragePrice = new BigDecimal(map5.get("average_price").toString());
                            } catch (Exception e) {
                                ytAveragePrice = BigDecimal.ZERO;
                            }
                            map3.put("yt_average_price", ytAveragePrice);

                            // 楼栋价格等于业态价格
                            if (buildingAveragePrice.compareTo(ytAveragePrice) == 0) {
                                flag = true;
                            }
                            break;
                        }
                    }
                }
                break;
            }
            if (flag) {
                iterator.remove();
            }
        }

        // 返回
        HashMap<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("one", one);
        resultMap.put("two", two);
        resultMap.put("three", three);
        resultMap.put("four", list3);
        resultMap.put("five", pricingAttached);
        return ResultUtil.success(resultMap);
    }

    /**
     * 获取价格预警（第三部分）
     *
     * @param map map
     * @return return
     */
    private List<Map> priceMonitorThirdPart(Map map, boolean flag) {
        return priceMonitorThirdPart(map, flag, null);
    }

    /**
     * 获取价格预警（第三部分）
     *
     * @param map map
     * @return return
     */
    private List<Map> priceMonitorThirdPart(Map map, boolean flag, Object priceComparisonObj) {
        // 4 获取 价格对比数据
        List<Map> priceComparisonList;
        if (flag) {
            priceComparisonList = pricingMapper.getPriceComparison(map.get("baseId").toString());
        } else {
            priceComparisonList = (List<Map>) priceComparisonObj;
        }

        // 5 获取 上月累计签约金额
        /*String sql = "SELECT t1.Code,CAST(ISNULL(SUM(t1.金额), 0) AS DECIMAL(20,2)) AS cumulativeContractAmount \n" +
                "FROM (SELECT Code,SUM(CjRmbTotal) AS 金额 \n" +
                "FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT WHERE ISNULL(CloseReason, '') <> '撤销签约' \n" +
                "AND ISNULL(CloseReason, '') <> 'nos退房' AND ISNULL(CloseReason, '') <> '补差' " +
                "AND productType NOT LIKE '车位%' \n" +
                "AND YwgsDate < DATEADD(mm, DATEDIFF(mm, 0, GETDATE()), 0) AND KINGDEEPROJECTID = '" + map.get("projectId") + "' GROUP BY Code \n" +
                "UNION ALL SELECT Code,-1 * SUM(CjRmbTotal) AS 金额 \n" +
                "FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT WHERE ISNULL(CloseReason, '') IN('nos退房','退房','换房') " +
                "AND productType NOT LIKE '车位%' \n" +
                "AND YwgsDate < DATEADD(mm, DATEDIFF(mm, 0, GETDATE()), 0) \n" +
                "AND CloseDate < DATEADD(mm, DATEDIFF(mm, 0, GETDATE()), 0) AND KINGDEEPROJECTID = '" + map.get("projectId") + "' GROUP BY Code \n" +
                "UNION ALL SELECT Code,SUM(BcTotal) AS 金额 FROM dotnet_erp60.dbo.VS_XSGL_CONTRACT \n" +
                "WHERE Status = '激活' AND AuditDate < DATEADD(mm, DATEDIFF(mm, 0, GETDATE()), 0) \n" +
                "AND productType NOT LIKE '车位%' " +
                "AND KINGDEEPROJECTID = '" + map.get("projectId") + "' GROUP BY Code) AS t1 " +
                "GROUP BY t1.Code ORDER BY t1.Code";
        List<Map<String, Object>> list = sqlExceptionHandling(sql, jdbcTemplatemy);*/
//        List<Map<String, Object>> list = pricingMapper.getCumulativeContractedAmountLastMonth(map);
        Map<String, Object> cumulativeContractAmountMap = pricingMapper.getCumulativeContractedAmountLastMonth(map);

        // 6 获取 剩余月份
        /*String sql2 = "SELECT CONCAT(product_code,'.',free_type,'.',business_type,'.',pro_type) AS product_code \n" +
                ",sign_plan_month,SUM(amount_plan) AS amount_plan \n" +
                "FROM `v_sman_sign_plan_dynamic` WHERE is_parking = 0 AND kingdee_project_id = '" + map.get("projectId") + "' \n" +
                "AND sign_plan_month >= DATE_ADD(CURDATE(),INTERVAL - DAY(CURDATE()) + 1 DAY) \n" +
                "GROUP BY product_code,free_type,business_type,pro_type,sign_plan_month " +
                "ORDER BY product_code,free_type,business_type,pro_type,sign_plan_month";*/
        String sql2 = "SELECT sign_plan_month,SUM(amount_plan) AS amount_plan \n" +
                "FROM `v_sman_sign_plan_dynamic` WHERE is_parking = 0 AND kingdee_project_id = '" + map.get("projectId") + "' \n" +
                "AND sign_plan_month >= DATE_ADD(CURDATE(),INTERVAL - DAY(CURDATE()) + 1 DAY) \n" +
                "GROUP BY sign_plan_month " +
                "ORDER BY sign_plan_month";
        List<Map<String, Object>> list2 = sqlExceptionHandling(sql2, jdbcTemplategxc);

        // 7 月份
        // 7.1 上月累计签约金额
        BigDecimal cumulativeContractAmount;
        try {
            cumulativeContractAmount = new BigDecimal(cumulativeContractAmountMap.get("cumulativeContractAmount").toString());
        } catch (Exception e) {
            cumulativeContractAmount = BigDecimal.ZERO;
        }
        // 7.2 基准（整盘货值 * 0.9）
        BigDecimal fullValue = BigDecimal.ZERO;
        for (Map<String, Object> map2 : list2) {
            BigDecimal amountPlan;
            try {
                amountPlan = new BigDecimal(map2.get("amount_plan").toString());
            } catch (Exception e) {
                amountPlan = BigDecimal.ZERO;
            }
            fullValue = fullValue.add(amountPlan);
        }
        fullValue = fullValue.add(cumulativeContractAmount).multiply(new BigDecimal("0.9"));
        // 7.3 月份
        String signPlanMonth = null;
        BigDecimal amount = new BigDecimal(cumulativeContractAmount.toString());
        for (Map<String, Object> map2 : list2) {
            BigDecimal amountPlan;
            try {
                amountPlan = new BigDecimal(map2.get("amount_plan").toString());
            } catch (Exception e) {
                amountPlan = BigDecimal.ZERO;
            }
            amount = amount.add(amountPlan);
            if (amount.compareTo(fullValue) > -1) {
                signPlanMonth = map2.get("sign_plan_month").toString();
                break;
            }
        }
        long x;
        try {
            x = DateUtil.betweenMonth(new Date(), DateUtil.parse(signPlanMonth), true);
            x = x < 1 ? 1 : x;
        } catch (Exception e) {
            x = 1;
        }

        // 7 遍历 处理
        List<Map> three = new ArrayList<>(priceComparisonList.size());
        for (Map priceComparison : priceComparisonList) {
            // 1 同业态近3月累计已售均价
            BigDecimal price;
            try {
                price = new BigDecimal(priceComparison.get("price").toString());
            } catch (Exception e) {
                price = BigDecimal.ZERO;

            }
            // 2 同业态规划部分均价
            BigDecimal tytGhbfjPrice;
            try {
                tytGhbfjPrice = new BigDecimal(priceComparison.get("tytGhbfjPrice").toString());
            } catch (Exception e) {
                tytGhbfjPrice = BigDecimal.ZERO;
            }

            // 4 总偏离度
            BigDecimal totalDeviation;
            try {
                totalDeviation = tytGhbfjPrice.divide(price, 5, BigDecimal.ROUND_HALF_UP)
                        .subtract(new BigDecimal("1")).abs().multiply(new BigDecimal("100"));
            } catch (Exception e) {
                totalDeviation = BigDecimal.ZERO;
            }

            // 7 偏离度
            BigDecimal deviation = totalDeviation.divide(new BigDecimal(x), 2, BigDecimal.ROUND_HALF_UP);

            // 封装
            Map<String, Object> hashMap = new HashMap<>(8);
            if (flag) {
                hashMap.put("YT", priceComparison.get("commercial"));
                hashMap.put("GHJJ", tytGhbfjPrice);
                hashMap.put("QYJJ", price);
                hashMap.put("PLD", deviation);
                if (deviation.compareTo(new BigDecimal("0.5")) > 0) {
                    hashMap.put("BS", 1);
                } else {
                    hashMap.put("BS", 0);
                }
                three.add(hashMap);
            } else {
                hashMap.put("commercial", priceComparison.get("commercial"));
                hashMap.put("adjustment_after_price", tytGhbfjPrice);
                hashMap.put("average_price", price);
                hashMap.put("price_deviation", deviation);
                hashMap.put("month", x);
                three.add(hashMap);
            }
        }
        return three;
    }


    /**
     * 获取规划楼栋调整
     *
     * @param map map
     * @return return
     */
    @Override
    public ResultBody getAdjustmentInfo(Map map) {
        // 1 获取 规划楼栋调整
        String sql = "SELECT t1.*,CASE WHEN area = 0 THEN 0 ELSE CAST(amount / area AS DECIMAL(20,2)) END AS average_price FROM \n" +
                "(SELECT x_JDBldID,MAX(x_JDBldName) AS x_JDBldName,x_ProductType,x_AjustType,\n" +
                "CASE WHEN x_ProductType LIKE '车位%' THEN SUM(x_Ts) ELSE SUM(x_Area) END AS area,\n" +
                "SUM(x_Amount) AS amount FROM dotnet_erp60.dbo.VS_XG_HZ \n" +
                "WHERE x_JDBldID IS NOT NULL AND x_JDBldID != '' AND TjPlanGUID = '" + map.get("TjPlanGUID") + "' \n" +
                "GROUP BY x_JDBldID,x_ProductType,x_AjustType) as t1 ORDER BY x_JDBldID";
        List<Map<String, Object>> list = sqlExceptionHandling(sql, jdbcTemplatemy);

        // 2 分组（调整前、调整后）
        // 调整前 集合
        ArrayList<Map<String, Object>> beforePriceAdjustment = new ArrayList<>(list.size());
        // 调整后 集合
        ArrayList<Map<String, Object>> afterPriceAdjustment = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> oldMap = list.get(i);
            if (ObjectUtil.equal(oldMap.get("x_AjustType"), "调整前")) {
                beforePriceAdjustment.add(oldMap);
            } else if (ObjectUtil.equal(oldMap.get("x_AjustType"), "调整后")) {
                afterPriceAdjustment.add(oldMap);
            }
        }

        // 3 遍历 处理
        for (Iterator<Map<String, Object>> iterator = beforePriceAdjustment.iterator(); iterator.hasNext(); ) {
            Map<String, Object> beforePriceAdjustmentMap = iterator.next();
            for (Iterator<Map<String, Object>> iterator2 = afterPriceAdjustment.iterator(); iterator2.hasNext(); ) {
                Map<String, Object> afterPriceAdjustmentMap = iterator2.next();
                // 3.1 判断 是否是同一楼栋
                if (ObjectUtil.equal(beforePriceAdjustmentMap.get("x_JDBldID"), afterPriceAdjustmentMap.get("x_JDBldID"))) {
                    // 3.2 判断 货值差异是否为零
                    BigDecimal amount;
                    try {
                        amount = new BigDecimal(beforePriceAdjustmentMap.get("amount").toString()).divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP);
                    } catch (Exception e) {
                        amount = BigDecimal.ZERO;
                    }
                    BigDecimal amount2;
                    try {
                        amount2 = new BigDecimal(afterPriceAdjustmentMap.get("amount").toString()).divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP);
                    } catch (Exception e) {
                        amount2 = BigDecimal.ZERO;
                    }
                    if (amount.compareTo(amount2) == 0) {
                        // 货值相等
                        iterator.remove();
                    } else {
                        beforePriceAdjustmentMap.put("area2", afterPriceAdjustmentMap.get("area"));
                        beforePriceAdjustmentMap.put("average_price2", afterPriceAdjustmentMap.get("average_price"));
                        BigDecimal average_price;
                        try {
                            average_price = new BigDecimal(beforePriceAdjustmentMap.get("average_price").toString());
                        } catch (Exception e) {
                            average_price = BigDecimal.ZERO;
                        }
                        BigDecimal average_price2;
                        try {
                            average_price2 = new BigDecimal(afterPriceAdjustmentMap.get("average_price").toString());
                        } catch (Exception e) {
                            average_price2 = BigDecimal.ZERO;
                        }
                        beforePriceAdjustmentMap.put("amount", amount);
                        beforePriceAdjustmentMap.put("amount2", amount2);
                        BigDecimal jjcy = average_price2.subtract(average_price);
                        beforePriceAdjustmentMap.put("jjcy", jjcy);
                        beforePriceAdjustmentMap.put("hzcy", amount2.subtract(amount));
                        iterator2.remove();
                    }
                }
            }
        }

        return ResultUtil.success(beforePriceAdjustment);
    }
}
