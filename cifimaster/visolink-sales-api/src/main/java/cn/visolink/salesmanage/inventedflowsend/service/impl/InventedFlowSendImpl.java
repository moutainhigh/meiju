package cn.visolink.salesmanage.inventedflowsend.service.impl;


import cn.hutool.json.XML;
import cn.visolink.salesmanage.inventedflowsend.service.InventedFlowSendService;
import cn.visolink.utils.FlowUtil;
import cn.visolink.utils.HttpClientUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class InventedFlowSendImpl implements InventedFlowSendService {

    @Value("${testmingyuan.url}")
    private String url;

    @Value("${testmingyuanflow.url}")
    private String myurl;


    /**
     * 模拟调用明源的接口
     */
    @Override
    public Map InventedFlowSend(Map map) {
        Map<String,Object> resultBody = new HashMap<String,Object>();
        resultBody = FlowUtil.sendPostRequest(url,map);
//        String responseString1 = HttpClientUtil.doPostJson(url,map.toString());
//        JSONObject jsonObject1 = JSON.parseObject(responseString1);
        JSONObject jsonObject1 = JSON.parseObject(resultBody.get("Result").toString());
        Map<String,Object> data = changeToJson(map);
        if("1".equals(jsonObject1.getString("code"))){
            Map<String,Object> map2  = new HashMap<String,Object>();
            String BTID = data.get("flow_code").toString();
            String bkUserID= data.get("creator").toString();
            String codeBOID= data.get("json_id").toString();
            String instanceId= "";
            Map<String,String> param = new HashMap<String,String>();
            String paramstr = JSONObject.toJSONString(param);
            map2.put("params",paramstr);
            map2.put("BTID",BTID);
            map2.put("bkUserID",bkUserID);
            map2.put("codeBOID",codeBOID);
            map2.put("instanceId",instanceId);
            String responseString2 = HttpClientUtil.doGet(myurl,map2);
            JSONObject jsonObject2 = JSON.parseObject(responseString2);
            resultBody.put("",jsonObject2.getString("Result"));
            return resultBody;

        }else{
            resultBody.put("code","-1");
            resultBody.put("msg","发起失败");

        }
        return resultBody;

    }


    /**
     * 明源json，xml数据转化
     * @param mingyuanData
     * @return
     */
    private static  Map<String,Object> changeToJson(Map<String,Object> mingyuanData){
        Map<String,Object> mapall = new HashMap<String,Object>();
        JSONObject json =  new JSONObject();
        List list= (List)mingyuanData.get("Data");
        String BSID = list.get(0).toString().trim();
        String BTID = list.get(1).toString().trim();
        String BOID = list.get(2).toString().trim();
        String BSXML  = list.get(3).toString().trim();
        String creator = list.get(5).toString().trim();
        cn.hutool.json.JSONObject xmlJSONObj = XML.toJSONObject(BSXML);
        String result = xmlJSONObj.getStr("DATA").trim();
        JSONObject jb = JSON.parseObject(result);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("flow_type",BSID);
        map.put("flow_code",BTID);
        map.put("json_id",BOID);
        map.put("flow_json",result);
        map.put("creator",creator);
        if(null != jb.getString("ProjectGUID") && "" != jb.getString("ProjectGUID")){
            map.put("project_id",jb.getString("ProjectGUID"));
        }else{
            map.put("project_id",jb.getString("ProjGUID"));
        }
        return map;
    }






}
