package cn.visolink.firstplan.message.controller;


import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.message.dao.NodeMessageSendDao;
import cn.visolink.firstplan.message.dao.TemplateEnginedao;
import cn.visolink.firstplan.message.service.impl.TemplateEngineServiceImpl;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "消息管理接口API")
@Slf4j
@RequestMapping("/messageManager")
public class NodeMessageSendController {

    @Autowired
    private NodeMessageSendDao nodeMessageSendDao;


    @Autowired
    private TemplateEnginedao templateEnginedao;


    @Autowired
    private TemplateEngineServiceImpl templateEngineService;

    @PostMapping("/sendNodeOverdueMes")
    public ResultBody sendNodeOverdueMes(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 0:00:00");
        List<Map> list = nodeMessageSendDao.getSendData(simpleDateFormat.format(new Date()));
        for (Map map : list) {
            System.out.println(map);
        }
        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("template_name", "三大件延期提醒");
        Map templateInfo = templateEnginedao.getTemplateInfo(paramMap);
        if (templateInfo != null) {
            //获取对应消息模版的消息标题
            String template_title = templateInfo.get("template_title") + "";
            //获取对应消息模版的消息内容
            String template_info = templateInfo.get("template_info") + "";
            //获取三大件延期
            if (list != null && list.size() > 0) {
                for (Map threePiecesDatum : list) {
                    String project_id = threePiecesDatum.get("project_id") + "";
                    //返回替换后的数据集
                    System.out.println(threePiecesDatum);
                    Map resultMap = templateEngineService.replaceData(threePiecesDatum, template_title, template_info);
                    //生成消息
                    templateEngineService.messageGeneration(resultMap, templateInfo, project_id);
                }
            }
        }
        return ResultBody.success("推送成功！");
    }
}
