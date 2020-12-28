package cn.visolink.salesmanage.vlink.controller;


import cn.visolink.firstplan.receipt.service.ReceiptService;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.vlink.service.VlinkService;
import cn.visolink.utils.AESUtils;
import cn.visolink.utils.HttpUtils;
import cn.visolink.utils.JSONUtils;
import cn.visolink.utils.RSAUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * <p>
 *  薇链接口 前端控制器
 * </p>
 *
 * @author baoql
 * @since 2020-09-02
 */

@RestController
@RequestMapping("/VlinkDocking")
@Api(tags = "薇链数据接口")
@Slf4j
public class VlinkController {

    private final VlinkService vlinkServiceImpl;

    public VlinkController( VlinkService vlinkServiceImpl) {
        this.vlinkServiceImpl = vlinkServiceImpl;
    }

    @Value("${Vlink.publicKey}")
    private String publicKey;

    @Log("薇链返回数据接口")
    @ApiOperation(value = "薇链返回数据接口")
    @PostMapping("/callBackVlink")
    public void paymentVlinkDate(HttpServletRequest request, HttpServletResponse response) {
        String result = "";
        PrintWriter out = null;
        try {
            String content = HttpUtils.io2String(request.getInputStream());
            String xEntryptKey = request.getHeader("X-VLINK-ENTRYPTKEY");
            String xTimestamp = request.getHeader("X-VLINK-TIMESTAMP");
            String aesKey = RSAUtils.decryptByPublic(xEntryptKey, publicKey);
            vlinkServiceImpl.setVlinkLogs("薇链回调接口密文","X-VLINK-ENTRYPTKEY:"+xEntryptKey+",test:"+content);
            AESUtils aesUtils = new AESUtils(aesKey);
            //解密报文体
            String plaintext = aesUtils.decrypt(content);
            //后续业务处理
            Map<String, Object> map = JSONUtils.toMap(plaintext);
            String funCode = String.valueOf(map.get("funCode"));
            String data = String.valueOf(map.get("data"));

            vlinkServiceImpl.setVlinkLogs("薇链回调接口参数","funCode:"+funCode+",data:"+data);
            if("project_approve_notify".equals(funCode)){
                vlinkServiceImpl.approvalVlinkDate(data);
            }else if ("batch_payment_notify".equals(funCode)){
                vlinkServiceImpl.paymentVlinkDate(data);
            }
            out = response.getWriter();
            result = "SUCCESS";
        } catch (Exception e) {
            result = "FAIL";
        }
        out.write(result);
        out.flush();
        out.close();
    }

}
