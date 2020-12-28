package cn.visolink.firstplan.message.service;

import cn.visolink.exception.ResultBody;

import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/6/6 12:43 上午
 * 模版数据替换引擎
 */
public interface TemplateEngineService {
    //首开计划消息生成
    ResultBody firstPlanMessage(Map map);
    /**
     * 三大件延期提醒消息生成
     */
    ResultBody threepiecesRemind(Map map);
    /**
     * 首开节点客储偏差预警提醒
     */
    ResultBody customerStorageDeviation(Map map);
    /**
     * 九大节点延期提醒
     */
    ResultBody sendNodeOverdueMes(Map map);

    ResultBody firstBroadcastMessageGen(Map map);


    /**
     * 生成消息公共方法
     */
    ResultBody createMessageCommon(String templateName,Map dataMap,String projectId);
}
