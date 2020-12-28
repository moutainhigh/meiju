package cn.visolink.salesmanage.vlink.service.Impl;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.visolink.firstplan.receipt.dao.ReceiptDao;
import cn.visolink.salesmanage.vlink.dto.VlinkModer;
import cn.visolink.salesmanage.vlink.service.VlinkService;
import cn.visolink.system.timelogs.bean.SysLog;
import cn.visolink.system.timelogs.dao.TimeLogsDao;
import cn.visolink.utils.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;

/**
 * <p>
 *  对接薇链业务层
 * </p>
 *
 * @author baoql
 * @since 2020-09-01
 */

@Service
@Slf4j
public class VlinkServiceImpl implements VlinkService {

    private final TimeLogsDao timeLogsDao;

    private final ReceiptDao receiptDao;

    @Value("${Vlink.url}")
    private String vlinkUrl;

    @Value("${Vlink.groupId}")
    private String groupId;

    @Value("${Vlink.appId}")
    private String appId;

    @Value("${Vlink.appName}")
    private String appName;

    @Value("${Vlink.appPriKey}")
    private String appPriKey;

    @Value("${Vlink.callBackVlink}")
    private String callBackVlink;

    public VlinkServiceImpl(TimeLogsDao timeLogsDao, ReceiptDao receiptDao) {
        this.timeLogsDao = timeLogsDao;
        this.receiptDao = receiptDao;
    }

    /**
     *申请项目通过后回调接口
     *
     * @param data date
     * @param funCode 接口参数
     * @return BaseResponse
     * */
    @Override
    public BaseResponse doPost(Object data, String funCode){
        VlinkModer vlinkModer = new VlinkModer(vlinkUrl, groupId, appId, appName, appPriKey);
        BaseResponse response = new BaseResponse();
        String timestamp = DateUtil.format(new Date(),"YYYYMMddHHmmSSSSS");
        BaseRequest baseRequest = vlinkModer.getBaseRequest(funCode, timestamp);
        baseRequest.setData(data);
        String reqJson = JSON.toJSONString(baseRequest);
        String body = "";
        String entryptKey = vlinkModer.getEntryptKey();
        try {
            body = vlinkModer.getBodyEncrypt(reqJson);
        } catch (Exception e) {
            log.error("VlinkServer."+funCode+" 加密错误="+e.getMessage());
        }
        setVlinkLogs("薇链接口请求数据:"+funCode,"VlinkServer."+funCode+" 请求明文报文="+reqJson);
        System.out.println("VlinkServer."+funCode+" 请求明文报文="+reqJson);

        Map<String, String> headerMap = getHeaderMap(timestamp, entryptKey);
        String result = HttpUtils.sendPost(vlinkUrl, body, headerMap);
        //JSONObject result = HttpRequestUtil.httpPost()
        System.out.println("VlinkServer."+funCode+" 返回报文="+result);
        setVlinkLogs("薇链接口返回数据:"+funCode,"VlinkServer."+funCode+" 请求明文报文="+result);
        Map<String, Object> respMap = JSONUtils.toMap(result);
        String code = String.valueOf(respMap.get("code"));
        String message = String.valueOf(respMap.get("message"));
        if(StringUtils.equals("00000", code)){
            //AES解密
            String resJson;
            try {
                resJson = vlinkModer.getBodyDecrypt(String.valueOf(respMap.get("sign")));
                System.out.println("VlinkServer."+funCode+" 返回明文报文="+resJson);
                response = JSONUtils.toObject(resJson, BaseResponse.class);
            } catch (Exception e) {
                log.error("VlinkServer."+funCode+" 解密错误="+e.getMessage());
            }
            System.out.println();
        }else{
            response.setCode(code);
            response.setMessage(message);
        }
        return response;
    }


    public static String genRandom(int pos){
        Random random = new Random();
        String result="";
        for(int i=0;i<pos;i++){
            result += random.nextInt(10);
        }
        return result;
    }

    /**
     *立项通过后回调薇链
     *
     * @param request request
     * @param receiptId 接口参数
     * @return BaseResponse
     * */
    @Override
    public void vlinkProjectApprove(HttpServletRequest request, String receiptId){
        Map<String,Object>  data = receiptDao.getVlinkDataByReceiptId(receiptId);
        if(data != null && "第三方代付".equals(data.get("commission_type"))){
            Date date = new Date();
            Map<String,Object> mm = new HashMap();
            mm.put("projectNo",data.get("receipt_code"));
            mm.put("projectName",data.get("name"));
            mm.put("beginDate",DateUtil.format(DateUtil.beginOfMonth(date),"yyyy-MM-dd"));
            mm.put("endDate",DateUtil.format(DateUtil.endOfMonth(date),"yyyy-MM-dd"));
            mm.put("num",data.get("num"));
            mm.put("budget",data.get("payment_amount"));
            mm.put("remark","");
            mm.put("notifyUrl",callBackVlink);
            Map<String,Object> map = new HashMap();
            map.put("fileName","付款单"+DateUtil.format(new Date(),"YYYYMMddHHmmSS")+".pdf");
            map.put("fileData",getPdfBase64(request,receiptId));
            mm.put("files",map);
            BaseResponse response = this.doPost(mm, "project_approve");
            JSONObject jsonObject;
            String ss = "";
            if("00000".equals(response.getCode())){
                //jsonObject = JSON.parseObject(JSON.toJSONString(response.getData()));
                ss=JSON.toJSONString(response.getData());
                Map m = (Map) response.getData();
                receiptDao.updateVlinkProjectId(receiptId,m.get("projectId")+"");
            }
        }
        // receiptDao.updateVlinkProjectId(receiptId,receiptId);
    }

    /**
     *申请项目通过回调薇链
     *
     * @param receiptId 接口参数
     * @return BaseResponse
     * */
    @Override
    public void vlinkBatchPayment(String receiptId){
        Map<String,Object>  data = receiptDao.getVlinkDataByReceiptId(receiptId);
        if(data != null && "第三方代付".equals(data.get("commission_type"))) {
            List<Map<String, Object>> list = receiptDao.getVlinkListByReceiptId(receiptId);
            Map map = new HashMap();
            map.put("projectId", data.get("vlink_project_id"));
            map.put("batchNo", data.get("receipt_code"));
            map.put("batchNum", data.get("num"));
            map.put("batchAmount", data.get("payment_amount"));
            map.put("notifyUrl", callBackVlink);
            map.put("remark", "");
            map.put("items", list);

            BaseResponse response = this.doPost(map, "batch_payment");
            JSONObject jsonObject;
            String ss = "";
            if ("00000".equals(response.getCode())) {
                //jsonObject = JSON.parseObject(JSON.toJSONString(response.getData()));
                ss = JSON.toJSONString(response.getData());
            }
        }
    }

    /**
     *申请项目通过后回调接口
     *
     * @param date date
     * */
    @Override
    public void approvalVlinkDate(String date) {
        Map<String,Object> map = JSONUtils.toMap(date);
        if(map != null && "1".equals(map.get("status"))){
            String projectNo = map.get("projectNo")+"";
            if(!"".equals(projectNo)){
                vlinkBatchPayment(projectNo);
            }
        }
    }

    /**
     *付款通过后回调接口
     *
     * @param date date
     * */
    @Override
    public void paymentVlinkDate(String date) {
        Map<String,Object> map = JSONUtils.toMap(date);
        if(map != null){
            String projectNo = map.get("batchNo")+"";
            String status = map.get("status")+"";
            if(!"".equals(projectNo) && !"".equals(status)){
                if("2".equals(status)){
                    status = "0";
                }else if ("其他".equals(status)){
                    status = "1";
                }
                Map m = new HashMap(5);
                m.put("id",projectNo);
                m.put("payment_status",status);
                    receiptDao.updateVlinkPaymentStatus(m);
            }
        }
    }


    /**
     * 数据生成pdf后转为 Base64
     *
     * @param request request
     * @param receiptId 付款单id
     * @return Base64
     * */
    public String getPdfBase64(HttpServletRequest request,String receiptId){
        List<Map<String,Object>> ll = receiptDao.getVlinkListDataByReceiptId(receiptId);
        Document document = new Document(new RectangleReadOnly(842F,595F));
        //导出模版路径
        // String
        String pdfPath = request.getServletContext().getRealPath("/")+"TemplateExcel" +
                File.separator + "付款单单明细" + DateUtil.format(new Date(),"YYYYMMddHHmmSS")+".pdf";
        try {
            PdfWriter writer = PdfWriter.getInstance(document,new FileOutputStream(pdfPath));
            document.open();
            /*获取兼容中文PDF*/
            Font font = getFont(5, Font.NORMAL);
            PdfPTable t = new PdfPTable(13);
            float[] columnWidths1 = { 0.03f,0.04f,0.08f,0.05f,0.09f,0.08f,0.06f,0.10f,0.13f,0.08f,0.05f,0.06f,0.04f};
            t.setWidths(columnWidths1);
            t.addCell(new Phrase("序号",font));
            t.addCell(new Phrase("业绩归属",font));
            t.addCell(new Phrase("身份证号",font));
            t.addCell(new Phrase("开户行",font));
            t.addCell(new Phrase("银行卡号",font));
            t.addCell(new Phrase("付款申请金额（元）",font));
            t.addCell(new Phrase("客户姓名",font));
            t.addCell(new Phrase("客户电话",font));
            t.addCell(new Phrase("房间信息",font));
            t.addCell(new Phrase("认购时间",font));
            t.addCell(new Phrase("签约时间",font));
            t.addCell(new Phrase("成交金额（元）",font));
            t.addCell(new Phrase("置业顾问",font));
            for(int i =0;i<ll.size();i++){
                Map<String,Object> mm = ll.get(i);
                t.addCell(new Phrase((i+1)+"",font));
                t.addCell(new Phrase(mm.get("gainBy") != null ? mm.get("gainBy") + "" : "",font));
                t.addCell(new Phrase(mm.get("reportIdCard") != null ? mm.get("reportIdCard") + "" : "",font));
                t.addCell(new Phrase(mm.get("bank_name") != null ? mm.get("bank_name") + "" : "",font));
                t.addCell(new Phrase(mm.get("bank_num") != null ? mm.get("bank_num") + "" : "",font));
                t.addCell(new Phrase(mm.get("application_amount") != null ? mm.get("application_amount") + "" : "",font));
                t.addCell(new Phrase(mm.get("customer_name") != null ? mm.get("customer_name") + "" : "",font));
                t.addCell(new Phrase(mm.get("customerMobile") != null ? mm.get("customerMobile") + "" : "",font));
                t.addCell(new Phrase(mm.get("room_name") != null ? mm.get("room_name") + "" : "",font));
                t.addCell(new Phrase(mm.get("subscription_date") != null ? mm.get("subscription_date") + "" : "",font));
                t.addCell(new Phrase(mm.get("signing_date") != null ? mm.get("signing_date") + "" : "",font));
                t.addCell(new Phrase(mm.get("subscription_price") != null ? mm.get("subscription_price") + "" : "",font));
                t.addCell(new Phrase(mm.get("employee_name") != null ? mm.get("employee_name") + "" : "",font));
            }
            document.add(t);
            document.close();
            File file = new File(pdfPath);
            String pdfBase64 = Base64.encode(file);
            System.out.println(pdfBase64);
            file.delete();
            return pdfBase64;
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Font getFont( float size, int style) throws DocumentException, IOException {
        BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        return new Font(bf, size, style);
    }

    public Map<String, String> getHeaderMap(String timestamp, String entryptKey) {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("X-VLINK-GROUP-ID", groupId);
        headerMap.put("X-VLINK-APP-ID", appId);
        try {
            headerMap.put("X-VLINK-APP-NAME", URLEncoder.encode(appName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("url编码失败");
        }
        headerMap.put("X-VLINK-ENTRYPTKEY", entryptKey);
        headerMap.put("X-VLINK-TIMESTAMP", timestamp);
        headerMap.put("Content-Type", "application/json;charset=utf-8");
        return headerMap;
    }

    @Override
    public void setVlinkLogs(String taskName, String note){
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(DateUtil.formatDateTime(new Date()));
        sysLog.setTaskName(taskName);
        sysLog.setNote(note);
        timeLogsDao.insertLogs(sysLog);
    }
}
