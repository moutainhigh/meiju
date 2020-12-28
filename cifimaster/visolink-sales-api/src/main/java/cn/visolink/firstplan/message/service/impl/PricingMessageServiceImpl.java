package cn.visolink.firstplan.message.service.impl;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.message.dao.MessageManagerDao;
import cn.visolink.firstplan.message.dao.TemplateEnginedao;
import cn.visolink.firstplan.message.service.PricingMessageService;
import cn.visolink.salesmanage.pricing.entity.PricingAttached;
import cn.visolink.salesmanage.pricing.service.PricingService;
import cn.visolink.utils.OAEncryptionUtil;
import cn.visolink.utils.PageUtil;
import cn.visolink.utils.UUID;
import com.alibaba.fastjson.JSON;
import jdk.nashorn.internal.runtime.options.LoggingOption;
import oracle.jdbc.util.Login;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sjl 定调价消息生成
 * @Created date 2020/6/24 6:02 下午
 */
@Service
@Transactional
public class PricingMessageServiceImpl implements PricingMessageService {
    @Autowired
    private TemplateEnginedao templateEnginedao;
    @Autowired
    private PricingService pricingService;
    @Autowired
    private MessageManagerDao messageManagerDao;

    @Autowired
    private OAEncryptionUtil oaEncryptionUtil;
    //定调价查看审批页面地址
    @Value("${Pricing.Pcinfourl}")
    private String PcpricingInfoUrl;

    //定调价查看审批页面地址
    @Value("${Pricing.mobileInfoUrl}")
    private String MobilepricingInfoUrl;
    @Autowired
    private TemplateEngineServiceImpl templateEngineService;

    @Override
    public ResultBody pricingMessageGen(String json_id) {
        //查询出主数据id
        Map flowMap = templateEnginedao.queryPricingBaseId(json_id);
        if (flowMap != null && flowMap.size() > 0) {
            boolean isUseNew=false;
            //判断定调价版本
            String flow_version=flowMap.get("flow_version")+"";
            if("".equals(flow_version)||"null".equals(flow_version)){
                isUseNew=true;
            }

            //如果调价类型为调表价，不生成消息
            String zj=flowMap.get("zj")+"";
            if("调表价".equals(zj)){
                return ResultBody.success(null);
            }
            String base_id = flowMap.get("base_id") + "";
            String project_id = flowMap.get("project_id") + "";
            Map<Object, Object> paramMap = new HashMap<>();

            paramMap.put("baseId", base_id);
            Map projectInfo = templateEnginedao.queryPricingProjectInfo(project_id);
            String jdProjID = projectInfo.get("kingdeeProjectID") + "";
            paramMap.put("projectId", jdProjID);
            paramMap.put("TjPlanGUID", json_id);
            //是否新版
            if (isUseNew) {
                paramMap.put("isNewEdition", "1");
            } else {
                paramMap.put("isNewEdition", "0");
            }
            String paramStr = JSON.toJSONString(paramMap);
            //是否发送总裁控制变量
            boolean isSendZc = false;
            System.err.println("参数:" + paramStr);
            ResultBody resultBody = pricingService.priceMonitor(paramMap);
            //改回5% 如过超过5% 发送当前配置的岗位
            //如果超过10%  给执行总裁和总裁发送邮箱消息
            String jsonString1 = JSON.toJSONString(resultBody);
            System.err.println("返回数据:\n" + jsonString1);
            String ytStr = "<p>";
            String oneHighlightRed = " <span style=\"color: #ff0000;\">年度累计整盘货值折损超过5%</span>。";
            String oneHighlightBlack = " <span>年度累计整盘货值折损未超过5%</span>。";

            String oneHighlightRedZc = " <span style=\"color: #ff0000;\">年度累计整盘货值折损超过10%</span>。";
            String oneHighlightBlackoneHighlightRedZc = " <span>年度累计整盘货值折损未超过10%</span>。";


            String twoHighlightRed = " <span style=\"color: #ff0000;\">与调价前最新动态货值相比，折损超过2%</span>。";
            String twoHighlightBlack = " <span>与调价前最新动态货值相比，折损未超过2%</span>。";

            boolean falg = false;
            String sbStr = "<p>";
            String threeHighlight = "3. ";
            String str = "";
            if (resultBody != null) {

                Map data = (Map) resultBody.getData();
                if(data!=null&&data.size()>0){
                    Map oneMap = (Map) data.get("one");
                    if(oneMap!=null){
                        String zsl1=oneMap.get("ZSL1")+"";
                        if(!"null".equals(zsl1)&&!"".equals(zsl1)) {
                            float zsl1num = Float.parseFloat(zsl1);
                            if (zsl1num > 5) {
                                if (zsl1num > 10) {
                                    isSendZc = true;
                                    oneMap.put("oneHighlightZc", oneHighlightRedZc);
                                }
                                falg = true;
                                oneMap.put("ZSL1", "<span style=\"color: #ff0000;\">" + zsl1 + "%</span>");
                                oneMap.put("oneHighlight", oneHighlightRed);
                            } else {
                                oneMap.put("ZSL1", "<span>" + zsl1 + "%</span>");
                                oneMap.put("oneHighlight", oneHighlightBlack);
                            }
                        }

                    }
                    Map twoMap = (Map) data.get("two");
                    if(twoMap!=null&&twoMap.size()>0){
                        String zsl2=twoMap.get("ZSL2")+"";
                        if(!"null".equals(zsl2)&&!"".equals(zsl2)){
                            float zsl2num = Float.parseFloat(zsl2);
                            if(zsl2num>2){
                                falg=true;
                                twoMap.put("ZSL2","<span style=\"color: #ff0000;\">"+zsl2+"%</span>");
                                twoMap.put("twoHighlight",twoHighlightRed);
                            }else{
                                twoMap.put("ZSL2","<span>"+zsl2+"%</span>");
                                twoMap.put("twoHighlight",twoHighlightBlack);
                            }
                        }
                    }
                    if(oneMap!=null){
                        projectInfo.putAll(oneMap);
                    }
                    if(twoMap!=null){
                        projectInfo.putAll(twoMap);
                    }
                    List<Map>  treeList = (List<Map>) data.get("three");
                    int i=0;
                    if(treeList!=null&&treeList.size()>0){
                        for (Map map : treeList) {
                            ytStr+=map.get("YT")+"</p>";
                            float ghjj = Float.parseFloat(map.get("GHJJ") + "");
                            if(ghjj<=0){
                                continue;
                            }
                            i++;
                            str+=""+i;
                            String pld=map.get("PLD")+"";
                            if(!"null".equals(pld)&&!"".equals(pld)){
                                float parseFloatPld = Float.parseFloat(pld);
                                if(parseFloatPld>0.5){
                                    falg=true;
                                    sbStr+="("+i+")"+map.get("YT")+"业态调整后规划价格"+map.get("GHJJ")+"元,同业态本次定价均价"
                                            +map.get("QYJJ")+"元,同业态规划价格偏离度<span style=\"color: #ff0000;\">"+map.get("PLD")+"%</span></p>";
                                    threeHighlight+="<span style=\"color: #ff0000;\">"+map.get("YT")+"业态规划价格偏离度超过0.5%;</span>";
                                }else{
                                    sbStr+="("+i+")"+map.get("YT")+"业态调整后规划价格"+map.get("GHJJ")+"元,同业态本次定价均价"
                                            +map.get("QYJJ")+"元,同业态规划价格偏离度<span>"+map.get("PLD")+"%</span></p>";
                                    threeHighlight+="<span>"+map.get("YT")+"业态规划价格偏离度未超过0.5%;</span>";
                                }
                            }
                        }
                        if("".equals(str)||"null".equals(str)){
                            threeHighlight="";
                        }
                        projectInfo.put("threeHighlight",threeHighlight);
                        projectInfo.put("adjustmentFormat",ytStr);
                        projectInfo.put("adjustMentFormatArray",sbStr);
                    }
                    int j=0;
                    String buildStr="<p>";
                    String fourHighlight="";
                    DecimalFormat decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                    //第四部分楼栋数据
                    List<Map> buildList= (List<Map>) data.get("four");
                    if(buildList!=null&&buildList.size()>0){
                        fourHighlight="4. ";
                        for (Map buildMap : buildList) {
                            j++;
                            float yt_price=0;
                            String yt_average_price=buildMap.get("yt_average_price")+"";
                            if(!"null".equals(yt_average_price)&&!"".equals(yt_average_price)){
                                yt_price = Float.parseFloat(yt_average_price);
                            }
                            float avg_price=0;
                            String building_average_price=buildMap.get("building_average_price")+"";
                            if(!"null".equals(building_average_price)&&!"".equals(building_average_price)){
                                avg_price=Float.parseFloat(building_average_price);
                            }
                            float pricePld=0;
                            if(avg_price!=0){
                                float s=avg_price-yt_price;
                                if(s<0){
                                    s=-s;
                                }
                                pricePld=s/avg_price*100;
                                String format = decimalFormat.format(pricePld);
                                pricePld= Float.parseFloat(format);
                            }
                            if(pricePld>5){
                                falg=true;
                                buildStr+="("+j+")"+buildMap.get("building_name")+"楼栋规划均价"+buildMap.get("building_average_price")+",业态规划均价"+buildMap.get("yt_average_price")+",楼栋规划均价偏离度<span style=\"color: #ff0000;\">"+pricePld+"%</span></p>";
                                fourHighlight+="<span style=\"color: #ff0000;\">"+buildMap.get("building_name")+"规划价格偏离度超过5%;"+"</span>";
                            }else{
                                buildStr+="("+j+")"+buildMap.get("building_name")+"楼栋规划均价"+buildMap.get("building_average_price")+",业态规划均价"+buildMap.get("yt_average_price")+",楼栋规划均价偏离度"+pricePld+"%</p>";
                                fourHighlight+="<span>"+buildMap.get("building_name")+"规划价格偏离度未超过5%;"+"</span>";
                            }
                        }
                    }
                    System.err.println(buildStr);
                    projectInfo.put("buildArray",buildStr);
                    projectInfo.put("fourHighlight",fourHighlight);

                    //如果是新版定调价，使用新版数据
                    if(isUseNew){
                        //获取第五部分表格数据，并强制转换为实体类
                        PricingAttached pricingAttached= (PricingAttached) data.get("five");
                        if(pricingAttached!=null){
                            //将实体类转换为json字符串
                            String toJSONString = JSON.toJSONString(pricingAttached);
                            //将json字符串转换为map
                            Map parseObject = JSON.parseObject(toJSONString, Map.class);
                            if(parseObject!=null&&parseObject.size()>0){
                                projectInfo.putAll(parseObject);
                            }
                        }

                    }

                }
            }

            //如果是旧版定调价，使用旧数据
            if(!isUseNew){
                //查询表格数据
                Map tableData = templateEnginedao.queryPricingTableData(base_id);
                if(tableData!=null&&tableData.size()>0){
                    projectInfo.putAll(tableData);
                }
            }




            //String valueTable="<td style=\"width: 20%; border-color: #000000; border-style: solid;\">业态规划均价</td>";



            Map<Object, Object> urlMap = new HashMap<>();
            //pricingInfoUrl
            urlMap.put("sysCode","xsgl");
            urlMap.put("proInstId",flowMap.get("flow_id"));
            String jsonString = JSON.toJSONString(urlMap);
            byte[] bytes = jsonString.getBytes();
         //  红色字体： <span style="color: #ff0000;">17.95%</span>
            //加密参数
            String encoded = Base64.getEncoder().encodeToString(bytes);

            String blackur="<a href="+PcpricingInfoUrl+encoded+">"+flowMap.get("title")+"</a>";
            //<a href="地址">超链接对象</a>
            //手机端查看审批页面参数
           String paramMobile="";
            //获取发起人账号
            String creator=flowMap.get("creator")+"";
            //对账号进行加密，生成token
            String encrypt = OAEncryptionUtil.encrypt(creator);
            paramMobile=MobilepricingInfoUrl+"?proInstId="+flowMap.get("flow_id")+"&token="+encrypt;

            System.err.println("手机端地址:"+paramMobile);
            System.err.println("PC端地址:"+PcpricingInfoUrl+encoded);
            //String Mobileblackur="<a href="+MobilepricingInfoUrl?+">"+flowMap.get("title")+"</a>";

            String mobileUrl="<a href="+paramMobile+">"+flowMap.get("title")+"</a>";
            projectInfo.put("pricinInfoUrl","PC端地址"+blackur);
            projectInfo.put("pricinMobileInfoUrl","移动端地址"+mobileUrl);


            //定义消息模版名称
            String templateName="";

            if (isSendZc) {
                templateName = "定调价预警提醒_总裁";
                //查询定调价模版
                paramMap.put("template_name", templateName);
                Map templateInfo_zc = templateEnginedao.getTemplateInfo(paramMap);
                //获取对应消息模版的消息标题
                String template_title_zc = templateInfo_zc.get("template_title") + "";
                //获取对应消息模版的消息内容
                String template_info_zc = templateInfo_zc.get("template_info") + "";
                Map map = templateEngineService.replaceData(projectInfo, template_title_zc, template_info_zc);
                //生成消息
                templateEngineService.messageGeneration(map, templateInfo_zc, project_id);
                Map<Object, Object> messageParamMap = new HashMap<>();
                projectInfo.put("price_id", json_id);
                //生成页面预警消息
                //createMessageForPage(projectInfo);
            } else {
                if (isUseNew) {
                    //使用新版模版
                    templateName = "定调价预警提醒_New";
                } else {
                    //旧版本消息模版
                    templateName = "定调价预警提醒";
                }
                //查询定调价模版
                paramMap.put("template_name", templateName);
                Map templateInfo = templateEnginedao.getTemplateInfo(paramMap);
                //获取对应消息模版的消息标题
                String template_title = templateInfo.get("template_title") + "";
                //获取对应消息模版的消息内容
                String template_info = templateInfo.get("template_info") + "";
                if (falg) {
                    Map map = templateEngineService.replaceData(projectInfo, template_title, template_info);
                    //生成消息
                    templateEngineService.messageGeneration(map, templateInfo, project_id);
                    Map<Object, Object> messageParamMap = new HashMap<>();
                    projectInfo.put("price_id", json_id);
                    //生成页面预警消息
                    createMessageForPage(projectInfo);
                }
            }
            return ResultBody.success(null);
/*
            return ResultBody.error(-1003, "消息生成失败，不满足生成条件!");
*/

        }
        return ResultBody.error(-1003,"消息生成失败，没有查询到相关流程信息!");
    }
    //生成定调价页面消息预警
    public void createMessageForPage(Map dataMap){
        try {
            //删除关于这个定调价流程的历史预警消息
            messageManagerDao.deleteMessageByProjectId(dataMap.get("price_id")+"");
            //查询定调价模版
            dataMap.put("template_name", "定调价页面显示");
            Map templateInfo = templateEnginedao.getTemplateInfo(dataMap);
            //获取对应消息模版的消息标题
            String template_title = templateInfo.get("template_title") + "";
            //获取对应消息模版的消息内容
            String template_info = templateInfo.get("template_info") + "";
            Map map = templateEngineService.replaceData(dataMap, template_title, template_info);
            //生成消息
            String message_id = UUID.randomUUID().toString();
            Map<Object, Object> messageMap = new HashMap<>();
            messageMap.put("id", message_id);
            //消息标题
            messageMap.put("message_title", map.get("template_title"));
            //消息详情
            messageMap.put("message_info", map.get("template_info"));
            //消息所属业务模块
            messageMap.put("message_type_name", templateInfo.get("template_type_name"));
            //消息发送类型 手动||自动
            messageMap.put("message_template_id", templateInfo.get("id"));
            //默认为发送失败状态也就是待发送状态
            messageMap.put("message_send_status", 1);
            messageMap.put("notice_type", templateInfo.get("template_name"));
            //消息所属项目
            messageMap.put("project_id", dataMap.get("price_id"));
            messageMap.put("is_del", 1);
            //todo 生成消息
            templateEnginedao.saveMessage(messageMap);
        }catch (Exception e){
           e.printStackTrace();
        }

    }
}
