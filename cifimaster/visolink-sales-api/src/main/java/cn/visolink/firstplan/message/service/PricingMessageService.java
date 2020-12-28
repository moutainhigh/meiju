package cn.visolink.firstplan.message.service;

import cn.visolink.exception.ResultBody;

/**
 * @author sjl 定调价消息生成
 * @Created date 2020/6/24 6:01 下午
 */
public interface PricingMessageService {

    //生成消息
    ResultBody pricingMessageGen(String json_id);
}
