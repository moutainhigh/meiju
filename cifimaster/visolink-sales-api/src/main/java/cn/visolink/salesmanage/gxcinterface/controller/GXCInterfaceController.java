package cn.visolink.salesmanage.gxcinterface.controller;

import cn.visolink.common.security.utils.HttpRequestUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.gxcinterface.service.impl.GXCInterfaceserviceImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;


/**
 * <p>
 * 数据接口
 * </p>
 *
 * @author jwb
 * @since 2019-10-1
 */
@RestController
@Api(tags = "数据接口")
@Slf4j
@RequestMapping("/gxcinterface")
public class GXCInterfaceController {


    @Autowired
    GXCInterfaceserviceImpl gxcserviceImpl;


    @Log("供货视图")
    @CessBody
    @ApiOperation(value = ",供货视图")
    @PostMapping("/gonghuo")
    public ResultBody  insertvaluegh(@RequestBody Map params) {
        try {
            //供货视图处理逻辑
            ResultBody resultBody = gxcserviceImpl.insertSupplyPlan(params);//gxcserviceImpl.insertvaluegh();
            return  resultBody;
        } catch (Exception e) {
            ResultBody<Object> resultBody = new ResultBody<>();
            resultBody.setMessages("初始化数据失败");
            resultBody.setCode(-1);
            return resultBody;
        }
    }

    @Log("动态货值视图")
    @ApiOperation(value = ",动态货值视图")
    @PostMapping("/dongtaihuozhi")
    public ResultBody  insertvaluedthz(@RequestBody Map params) {
        try {
            //动态货值视图处理逻辑
            ResultBody resultBody = gxcserviceImpl.insertDynamicValue(params);//gxcserviceImpl.insertvaluedthz();
            return  resultBody;
        } catch (Exception e) {
            ResultBody<Object> resultBody = new ResultBody<>();
            resultBody.setMessages("初始化数据失败");
            resultBody.setCode(-1);
            return resultBody;
        }
    }

    @Log("战规货值视图")
    @CessBody
    @ApiOperation(value = ",战规货值视图")
    @PostMapping("/zhanguihuozhi")
    public ResultBody  insertvaluezghz(@RequestBody Map params) {
        try {
            //战规货值视图处理逻辑
            ResultBody resultBody = gxcserviceImpl.insertPlanValue(params);//gxcserviceImpl.insertvaluezghz();
            return  resultBody;
        } catch (Exception e) {
            ResultBody<Object> resultBody = new ResultBody<>();
            resultBody.setMessages("初始化数据失败");
            resultBody.setCode(-1);
            return resultBody;
        }
    }

    @Log("签约视图")
    @CessBody
    @ApiOperation(value = ",签约视图")
    @PostMapping("/qianyue")
    public ResultBody  insertvalueqy(@RequestBody Map params) {
        try {
            //签约视图处理逻辑
            ResultBody resultBody = gxcserviceImpl.insertSignPlan(params);//gxcserviceImpl.insertvalueqy();
            return  resultBody;
        } catch (Exception e) {
            ResultBody<Object> resultBody = new ResultBody<>();
            resultBody.setMessages("初始化数据失败");
            resultBody.setCode(-1);
            return resultBody;
        }
    }


   /* @Log("明源与供销存面积段归集")
    @CessBody
    @ApiOperation(value = ",明源与供销存面积段归集，修改库存可售")
    @PostMapping("/updateAvailableStock")
    public ResultBody  updateAvailableStock() {
        ResultBody<Object> resultBody = new ResultBody<>();
        try {
           //修改库存可售,非车位
           int  i = gxcserviceImpl.updateAvailableStock();
           //修改库存可售,非车位
           int  n = gxcserviceImpl.updateAvailableStockNotCar();
           if(i>=0 && n>=0){
               resultBody.setMessages("库存可售初始化成功");
               resultBody.setData("库存可售修改"+(i+n)+"条");
               resultBody.setCode(200);
               return resultBody;
           }else{
               resultBody.setMessages("库存可售初始化数据失败");
               resultBody.setCode(-1);
               return resultBody;
           }
         } catch (Exception e) {
            resultBody.setMessages("库存可售初始化数据失败");
            resultBody.setCode(-1);
            return resultBody;
        }
    }*/

}




