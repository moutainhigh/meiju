package cn.visolink.firstplan.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.message.dao.MessageManagerDao;
import cn.visolink.firstplan.message.service.MessageManagerService;
import cn.visolink.firstplan.planmonitoring.dao.PlanMontitorDao;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import springfox.documentation.spring.web.json.Json;

import java.util.*;

/**
 * @author sjl
 * @Created date 2020/5/26 7:04 下午
 * 消息管理服务
 */
@Service
@Transactional
public class MessageManagerServiceImpl implements MessageManagerService {

    @Autowired
    private MessageManagerDao messageManagerDao;

    @Autowired
    private PlanMontitorDao planMontitorDao;

    /**
     * 查询模版列表
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody queryTemplateList(Map map) {
        try {
            //获取分页参数
            if (map.get("pageIndex") == null) {
                map.put("pageIndex", 1);
            }
            if (map.get("pageSize") == null) {
                map.put("pageSize", 10);
            }
            int pageIndex = Integer.parseInt(map.get("pageIndex").toString());
            int pageSize = Integer.parseInt(map.get("pageSize").toString());
            int i = (pageIndex - 1) * pageSize;
            map.put("pageIndex", i);
            //查询模版列表
            List<Map> templateList = messageManagerDao.queryMessageTemplateList(map);
            //查询模版总数
            String templateTotal = messageManagerDao.queryMessageTemplateListCount(map);
            if (templateTotal == null || "".equals(templateTotal)) {
                templateTotal = "0";
            }
            Map<Object, Object> resultMap = new HashMap<>();
            resultMap.put("templateList", templateList);
            resultMap.put("templateTotal", Integer.parseInt(templateTotal));
            return ResultBody.success(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(-1102, "消息模版列表查询失败:" + e.getCause());
        }

    }

    /**
     * 添加消息模版
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody addMessageTemplate(Map map) {
        try {
            //获取模版id
            String template_id = map.get("id") + "";
            //获取当前登陆的用户名称
            String username = map.get("username") + "";
            //获取发送方式复选数据集
            List<String> openSend = (List<String>) map.get("open_send");
            map.put("open_dingtalk", 0);
            map.put("open_emaill", 0);
            if (openSend != null) {
                if (openSend.contains("钉钉")) {
                    map.put("open_dingtalk", 1);
                }
                if (openSend.contains("邮箱")) {
                    map.put("open_emaill", 1);
                }
            } else {
                return ResultBody.error(-1006, "请至少选择一种发送通道(钉钉|邮箱)");
            }
            String template_send_type = map.get("template_send_type") + "";
            if ("".equals(template_send_type) || "null".equals(template_send_type)) {
                return ResultBody.error(-1007, "请至少选择一种发送方式(手动|自动)");
            }
            //如果模版id不等于空，修改模版信息
            if (!"".equals(template_id) && !"null".equals(template_id)) {
                map.put("updator", username);
                messageManagerDao.updateTemplate(map);
            } else {
                map.put("creator", username);
                map.put("id", UUID.randomUUID().toString());
                messageManagerDao.saveTemplate(map);
            }
            return ResultBody.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-1004, "模版新增/修改失败:" + e.getCause());
        }
    }

    /**
     * 获取标签库
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody getLabelLibraryData(Map map) {
        try {
            //获取消息模块归属模块
            String template_type_name = map.get("template_type_name") + "";
            String template_id = map.get("id") + "";
            //如果模版id不为空，视为编辑模版操作/获取模版详情
            if (!"".equals(template_id) && !"null".equals(template_id)) {
                Map templateInfo = messageManagerDao.getTemplateInfo(template_id);
                if (templateInfo != null) {
                    template_type_name = templateInfo.get("template_type_name") + "";
                }
            }
            //放置返回数据
            Map<Object, Object> resultMap = new HashMap<>();
            if ("".equals(template_type_name) || "null".equals(template_type_name)) {
                //获取可以选择的模版归属模块
                List<Map> templateType = messageManagerDao.getTemplateType(null);
                resultMap.put("labelType", templateType);
                if (templateType != null && templateType.size() > 0) {
                    template_type_name = templateType.get(0).get("template_type_name") + "";
                }
            }
            List<Object> labellist = new ArrayList<>();
            //获取标签库
            List<Map> listByLabelType = messageManagerDao.getLabelListByLabelType(template_type_name);
            //获取可以选择的模版归属模块
            List<Map> templateType = messageManagerDao.getTemplateType(null);
            HashMap<Object, Object> hashMap = new HashMap<>();
            if (templateType != null) {
                hashMap.put("template_type_name", template_type_name);
            }
            if(templateType==null){
                templateType=new ArrayList<>();
            }
            templateType.remove(hashMap);
            templateType.add(hashMap);
            Collections.swap(templateType, templateType.size() - 1, 0);
            if (listByLabelType != null && listByLabelType.size() > 0) {
                //将标签库归类
                for (Map labelMap1 : listByLabelType) {
                    Map<Object, Object> parendMap = new HashMap<>();
                    Map<Object, Object> paramMap = new HashMap<>();
                    String label_class_name = labelMap1.get("label_class_name") + "";
                    paramMap.put("label_class_name", label_class_name);
                    paramMap.put("template_type_name", template_type_name);
                    List<Map> labelListByClassName = messageManagerDao.getLabelListByClassName(paramMap);
                    parendMap.put("label_class_name", label_class_name);
                    parendMap.put("childLabel", labelListByClassName);
                    labellist.add(parendMap);
                }
            }
            LinkedHashSet<Object> hashSet = new LinkedHashSet<Object>(labellist);
            List<Object> arrayList = new ArrayList<>(hashSet);
            resultMap.put("labelType", templateType);
            resultMap.put("labelList", arrayList);
            return ResultBody.success(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(-1004, "标签库获取失败:" + e.getCause());
        }
    }

    /**
     * 设置岗位发送
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody setPositionSending(Map map) {
        String template_id = map.get("template_id") + "";
        if ("null".equalsIgnoreCase(template_id)) {
            return ResultBody.error(-1003, "未获取到消息模版ID,请先设置消息模版!");
        }
        try {
            int pageIndex1 = Integer.parseInt(map.get("pageIndex1").toString());
            int pageSize1 = Integer.parseInt(map.get("pageSize1").toString());
            int i1 = (pageIndex1 - 1) * pageSize1;
            map.put("pageIndex1", i1);
            //根据模版id,查询已经关联的岗位
            List<Map> selectedJobList = messageManagerDao.getSelectedJobList(map);
            //查询已经关联的岗位总数
            String selectedJobListTotal = messageManagerDao.getSelectedJobListTotal(map);
            //查询系统下未关联当前模版的岗位-可选择岗位列表
            int pageIndex2 = Integer.parseInt(map.get("pageIndex2").toString());
            int pageSize2 = Integer.parseInt(map.get("pageSize2").toString());
            int i2 = (pageIndex2 - 1) * pageSize2;
            map.put("pageIndex2", i2);
            System.out.println(map);
            List<Map> notSelectedJobList = messageManagerDao.getNotSelectedJobList(map);
            //查询未关联的岗位总数
            String notSelectedJobListTotal = messageManagerDao.getNotSelectedJobListTotal(map);

            Map<String, Object> resultMap = new HashMap<>();
            //已选择岗位组
            resultMap.put("selectedList", selectedJobList);
            resultMap.put("selectedTotal", selectedJobListTotal);
            //未选择岗位组
            resultMap.put("notSelectedList", notSelectedJobList);
            resultMap.put("notSelectedTotal", notSelectedJobListTotal);
            //查询筛选
            return ResultBody.success(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(-1004, "查询岗位组失败:" + e.getCause());
        }
    }

    /**
     * 模版岗位组设置-保存设置
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody setPositionSave(Map map) {
        String template_id = map.get("template_id") + "";
        try {
            //删除当前模版已经关联的岗位组
            //将用户设置的新的关联岗位组更新进去
            Map jobObject = (Map) map.get("jobObject");
            if (jobObject != null) {
                String id = jobObject.get("id") + "";
                if (!"".equals(id) && !"null".equals(id)) {
                    //删除
                    messageManagerDao.deleteSelectedComjob(id);
                } else {
                    //新增
                    jobObject.put("template_id", template_id);
                    messageManagerDao.updateTemplateRelationcomjob(jobObject);
                }
            }
            return ResultBody.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(-1005, "岗位组更新失败:" + e.getCause());
        }

    }

    /**
     * 消息管理-添加标签
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody saveLabel(Map map) {
        //获取标签名称
        String labelName = map.get("label_name") + "";
        map.put("label_pid", -1);
        map.put("label_level", 1);
        map.put("isDel", 0);
        String label_id = map.get("id") + "";
        if (!"".equals(label_id) && !"null".equals(label_id)) {
            //修改标签
            messageManagerDao.updateLabel(map);
        } else {
            //添加标签
            map.put("isDel", 0);
            messageManagerDao.saveLabel(map);
        }

        return ResultBody.success(null);
    }

    /**
     * 查询标签管理列表
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody queryLabelList(Map map) {
        try {
            //获取分页参数
            if (map.get("pageIndex") == null) {
                map.put("pageIndex", 1);
            }
            if (map.get("pageSize") == null) {
                map.put("pageSize", 10);
            }
            int pageIndex = Integer.parseInt(map.get("pageIndex").toString());
            int pageSize = Integer.parseInt(map.get("pageSize").toString());
            int i = (pageIndex - 1) * pageSize;
            map.put("pageIndex", i);
            List<Map> mapList = messageManagerDao.queryLabelList(map);
            String total = messageManagerDao.queryLabelListTotal(map);
            Map<Object, Object> resultMap = new HashMap<>();
            resultMap.put("labelList", mapList);
            resultMap.put("labelTotal", total);
            return ResultBody.success(resultMap);
        } catch (Exception e) {
            return ResultBody.error(-1100, "标签数据查询失败:" + e.getCause());
        }

    }

    /**
     * 添加模版
     *
     * @param map
     * @return
     */

    @Override
    public ResultBody getTemplateInfo(Map map) {
        //获取模版id
        String id = map.get("id") + "";
        if ("".equals(id) || "null".equals(id)) {
            return ResultBody.error(-1105, "未获取到模版实例!");
        }
        Map templateInfo = messageManagerDao.getTemplateInfo(id);
        return ResultBody.success(templateInfo);
    }

    @Override
    public ResultBody getTypeList(Map map) {
        return ResultBody.success(messageManagerDao.getTemplateType(null));
    }

    @Override
    public ResultBody deleteLabel(String id) {
        messageManagerDao.deleteLabel(id);
        return ResultBody.success(null);
    }

    @Override
    public ResultBody getMessageInfoById(Map map) {
        String id = map.get("id") + "";
        if ("".equals(id) || "null".equals(id)) {
            return ResultBody.error(-1006, "消息查询失败!");
        }
        try {
            Map infoById = messageManagerDao.getMessageInfoById(id);
            return ResultBody.success(infoById);
        } catch (Exception e) {
            return ResultBody.error(-1840, "消息查询失败,失败原因:" + e.getCause());

        }


    }

    @Override
    public ResultBody queryMessageList(Map map) {
        List<String> message_send_tim = (List<String>) map.get("message_send_time");
        if (message_send_tim != null && message_send_tim.size() > 0) {
            map.put("startTime", message_send_tim.get(0));
            map.put("endTime", message_send_tim.get(1));
            map.put("time", "haveTime");
        }
        String message_send_status=map.get("message_send_status")+"";

        //获取分页参数
        if (map.get("pageIndex") == null) {
            map.put("pageIndex", 1);
        }
        if (map.get("pageSize") == null) {
            map.put("pageSize", 10);
        }
        int pageIndex = Integer.parseInt(map.get("pageIndex") + "");
        int pageSize = Integer.parseInt(map.get("pageSize").toString());
        int i = (pageIndex - 1) * pageSize;
        map.put("pageIndex", i);
        //获取列表类型
        if("1".equals(message_send_status)){
            map.remove("create_time");
            map.put("message_send_time","1");
        }else{
            map.remove("message_send_time");
            map.put("create_time","1");
        }
        List<Map> messageList = messageManagerDao.queryMessageList(map);
        if (messageList != null && messageList.size() > 0) {
            for (Map messageMap : messageList) {
                String message_id=messageMap.get("id")+"";
                List<Map>  sendUserList= messageManagerDao.getMessageForUserList(message_id);
                if("1".equals(message_send_status)){
                    if (sendUserList != null && sendUserList.size() > 0) {
                        List<Map> sendedUserList = messageManagerDao.getSendedUserList(messageMap);
                        if(sendedUserList!=null&&sendedUserList.size()>0){
                            for (Map sendUser : sendedUserList) {
                                String sendUserName=sendUser.get("userName")+"";
                                for (Map userMap : sendUserList) {
                                    String userName=userMap.get("userName")+"";
                                    if(sendUserName.contains(userName)){
                                        userMap.put("isSend",1);
                                    }
                                }
                            }
                        }
                    }

                }

                List<Map> cloneByStream = ObjectUtil.cloneByStream(sendUserList);
                messageMap.put("sendUserList", cloneByStream);
            }
        }
        String total = messageManagerDao.queryMessageListTotal(map);
        Map<Object, Object> resultMap = new HashMap<>();
        resultMap.put("messageList", messageList);
        resultMap.put("messageListTotal", total);

        return ResultBody.success(resultMap);
    }

    /**
     * 编辑消息详情
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody updateMessageInfo(Map map) {
        Object id = map.get("id");
        if (id != null && !"".equals(id)) {
            messageManagerDao.updateMessageInfo(map);
            return ResultBody.success(null);
        } else {
            return ResultBody.error(-1078, "未找到该消息!");
        }

    }

    @Override
    public ResultBody queryMessageInfo(Map map) {
        String id = map.get("id") + "";
        //查询消息详情
        Map messageInfo = messageManagerDao.queryMessageInfo(id);
        if (messageInfo != null && messageInfo.size() > 0) {
            return ResultBody.success(messageInfo);
        } else {
            //如果消息没有查到，查询模版详情
            Map templateInfo = messageManagerDao.getTemplateInfo(id);
            return ResultBody.success(templateInfo);
        }
    }

    @Override
    public ResultBody deleteMessageById(Map map) {
        String id = map.get("id") + "";
        messageManagerDao.deleteMessageById(id);
        return ResultBody.success(null);
    }

    /**
     * 查询事业部和群聊列表
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody queryBusinessGroupChat(Map map) {
        try {
            List<Map> groupChatList = messageManagerDao.queryGroupChatList(map);
            if (groupChatList != null && groupChatList.size() > 0) {
                for (Map groupChatMap : groupChatList) {
                    List<Map> buinessList = (List<Map>) groupChatMap.get("buiness_info");
                    if (buinessList != null && buinessList.size() > 0) {
                        groupChatMap.put("buiness_info", buinessList);
                    }
                }
            }
            return ResultBody.success(groupChatList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(-3001, "查询群聊列表失败!");
        }
    }

    @Override
    public ResultBody deleteBusinessGroupChat(Map map) {
        try {
            messageManagerDao.deleteBusinessGroupChat(map);
            return ResultBody.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(-2005, "删除失败:" + e.toString());
        }
    }

    @Override
    public ResultBody addBusinessGroupChat(Map map) {
        String id = map.get("id") + "";
        try {
            Map<Object, Object> paramMap = new HashMap<>();
            paramMap.put("robot_url", map.get("robot_url"));

            //校验群聊机器人是否已存在
            Map checkBuinessGroup = messageManagerDao.checkBuinessGroup(paramMap);
            if (checkBuinessGroup != null && checkBuinessGroup.size() > 0) {
                String ids = checkBuinessGroup.get("id") + "";
                if (!"null".equals(id) && !"".equals(id) && !ids.equals(id)) {
                    return ResultBody.error(-2006, "更新失败，此群聊机器人地址在" + checkBuinessGroup.get("group_chat_name") + "群聊中已配置!");
                } else if ("null".equals(id) || "".equals(id)) {
                    return ResultBody.error(-2006, "更新失败，此群聊机器人地址在" + checkBuinessGroup.get("group_chat_name") + "群聊中已配置!");
                }
            }
            //校验区域集团配置是否穿插
            List<Map> buiness_info = (List<Map>) map.get("buiness_info");
            if (buiness_info != null && buiness_info.size() > 0) {
                paramMap.remove("robot_url");
                for (Map buinessMap : buiness_info) {
                    String buiness_id = buinessMap.get("business_unit_id") + "";
                    String business_unit_name = buinessMap.get("business_unit_name") + "";
                    paramMap.put("buiness_id", buiness_id);
                    Map checkBuinessGroups = messageManagerDao.checkBuinessGroup(paramMap);
                    if (checkBuinessGroups != null && checkBuinessGroups.size() > 0) {
                        String ids2 = checkBuinessGroups.get("id") + "";
                        if (!"null".equals(id) && !"".equals(id) && !ids2.equals(id)) {
                            return ResultBody.error(-2006, "更新失败，" + business_unit_name + "在群聊" + checkBuinessGroups.get("group_chat_name") + "中已存在");
                        } else if ("null".equals(id) || "".equals(id)) {
                            return ResultBody.error(-2006, "更新失败，" + business_unit_name + "在群聊" + checkBuinessGroups.get("group_chat_name") + "中已存在");
                        }
                    }

                }
            }
            //修改
            String buiness_infos = map.get("buiness_info").toString();
            if (!"".equals(id) && !"null".equals(id)) {
                map.put("buiness_info", buiness_infos);
                messageManagerDao.updateBusinessGroupChat(map);
            } else {
                //添加
                map.put("buiness_info", buiness_infos);
                map.put("id", UUID.randomUUID().toString());
                messageManagerDao.addBuinessGroupChat(map);
            }
            return ResultBody.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(-2995, "更新失败!:" + e.toString());
        }
    }

    @Override
    public ResultBody queryBuinessGroupChatInfo(Map map) {
        try {
            Map chatInfo = messageManagerDao.queryBuinessGroupChatInfo(map);
            //查询所有区域集团列表
            List<Map> idmBuinessData = planMontitorDao.getIdmBuinessData();

            List<Map> buinessList = new ArrayList<>();
            if (chatInfo != null && chatInfo.size() > 0) {
                List<Map> buiness_info = (List<Map>) chatInfo.get("buiness_info");
                if (buiness_info != null && buiness_info.size() > 0) {
                    for (Map map1 : buiness_info) {
                        String ids = map1.get("business_unit_id") + "";
                        for (Map idmBuinessDatum : idmBuinessData) {
                            String id = idmBuinessDatum.get("business_unit_id") + "";
                            if (!ids.equals(id)) {
                                buiness_info.add(idmBuinessDatum);
                            }
                        }
                    }
                }
                chatInfo.put("buiness_info", buiness_info);
            }
            return ResultBody.success(chatInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(-20056, "查询详情失败!");
        }
        /**
         * 获取配置的通用岗位下的人员
         */

    }

    @Override
    public ResultBody queryUserList(Map map) {
        try {
            List<Map> userList = new ArrayList<>();
            String orgId = "";
            List<Map> paresUserList = new ArrayList<>();
            List<Map> zhuyaoUserList = new ArrayList<>();
            //查询当前消息所属的区域、项目、集团、城市
            Map belongOrgMap = messageManagerDao.getProjectBelongOrg(map);
            List<Map> commonJobHrType = messageManagerDao.getCommonJobHrType(map);
            if (commonJobHrType != null && commonJobHrType.size() > 0) {
                for (Map hrTypeMap : commonJobHrType) {
                    //获取该通用岗位所属的组织层级
                    List<Map> userLists = null;

                    String jobDesc = hrTypeMap.get("JobDesc") + "";
                    orgId = getOrgId(jobDesc, belongOrgMap, orgId);
                    hrTypeMap.put("orgId", orgId);
                    //查询主岗人员
                    hrTypeMap.put("CurrentJob", 1);
                    userLists = messageManagerDao.getUserList(hrTypeMap);
                    //如果主岗人员不为空
                    if (userLists != null && userLists.size() > 0) {
                        //存储主岗
                        zhuyaoUserList.addAll(userLists);
                    } else {
                        //查询兼岗
                        hrTypeMap.put("CurrentJob", 0);
                        userLists = messageManagerDao.getUserList(hrTypeMap);
                        if (userLists != null && userLists.size() > 0) {
                            //存储兼岗
                            paresUserList.addAll(userLists);
                        }
                    }
                }
            }
            if (paresUserList != null && paresUserList.size() > 0) {
                //遍历兼岗
                for (int i = 0; i < paresUserList.size(); i++) {
                    String jgusername = paresUserList.get(i).get("username") + "";
                    //遍历主岗
                    if (zhuyaoUserList != null && zhuyaoUserList.size() > 0) {
                        for (int j = 0; j < zhuyaoUserList.size(); j++) {
                            String zgusername = zhuyaoUserList.get(j).get("username") + "";
                            //如果兼岗中有和主岗重复的账号/人员
                            if (jgusername.equals(zgusername)) {
                                //移除兼岗中的，以主岗为准
                                paresUserList.remove(i);
                            }
                        }
                    }
                }
            }

            //合并主岗和兼岗
            if (paresUserList != null) {
                userList.addAll(paresUserList);
            }
            if(zhuyaoUserList!=null){
                userList.addAll(zhuyaoUserList);
            }
            return ResultBody.success(userList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(1005, "查询人员列表失败:" + e.toString());
        }

    }

    public String getOrgId(String jobDesc, Map belongOrgMap, String orgId) {
        switch (jobDesc) {
            case "项目层级":
                String project_id = belongOrgMap.get("project_id") + "";
                return project_id;
            case "城市层级":
                String city_id = belongOrgMap.get("city_id") + "";
                return city_id;
            case "区域层级":
                String business_unit_id = belongOrgMap.get("business_unit_id") + "";
                return business_unit_id;
            case "集团层级":
                String jtid = "00000001";
                return jtid;
            default:
                return "00000001";
        }
    }

    /**
     * 初始化消息待发送人员列表
     * @param map
     * @return
     */
    @Override
    public ResultBody initMessageSendUsers(Map map){
        //查询出所有的消息
        List<Map> messageList = messageManagerDao.queryMessageList(map);
        if(messageList!=null&&messageList.size()>0){
            for (Map messageMap : messageList) {
                ResultBody resultBody = queryUserList(messageMap);
                if(resultBody.getCode()==200){
                    List<Map> userList = (List<Map>) resultBody.getData();
                    if(userList!=null&&userList.size()>0){
                        for (Map userMap : userList) {
                            userMap.put("message_id",messageMap.get("id")+"");
                            messageManagerDao.insertUserList(userMap);
                        }
                    }
                }
            }
        }
        return ResultBody.success(null);
    }
}
