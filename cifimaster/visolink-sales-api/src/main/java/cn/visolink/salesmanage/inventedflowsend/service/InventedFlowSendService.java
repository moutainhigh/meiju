package cn.visolink.salesmanage.inventedflowsend.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface InventedFlowSendService {
    /**
     * 模拟调用明源的接口
     */
    Map InventedFlowSend(Map mingyuanData);
//    Map<String,Object> InventedFlowSend(Map<String,String> mingyuanData);
}
