package cn.visolink.salesmanage.inventedflowsend.controller;


import cn.visolink.salesmanage.inventedflowsend.service.InventedFlowSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟明源工作流发起
 * 2019-11-20
 * lihuan
 */
@RestController
@RequestMapping("/testmingyuan")
public class InventedFlowSendController {
    @Autowired
    InventedFlowSendService inventedFlowSendService;



    @RequestMapping(value = "/flowSend",method = RequestMethod.POST)
    public Map InventedFlowSend(@RequestBody Map map){
//    public Map InventedFlowSend(@RequestBody Map<String,String> map){

        return inventedFlowSendService.InventedFlowSend(map);
//        return inventedFlowSendService.InventedFlowSend(map);

    }



}
