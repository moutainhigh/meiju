package cn.visolink.firstplan.skipnodeupload.service.impl;

import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.TaskLand.service.TakeLandService;
import cn.visolink.firstplan.skipnodeupload.dao.SkipNodeUploadFileDao;
import cn.visolink.firstplan.skipnodeupload.service.SkipNodeUploadFileService;
import cn.visolink.salesmanage.flieUtils.dao.FileDao;
import cn.visolink.salesmanage.workflowchange.dao.WorkflowChangeDao;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/6/28 3:31 下午
 */
@Service
@Transactional
public class SkipNodeUploadFileServiceImpl implements SkipNodeUploadFileService {
    @Autowired
    private SkipNodeUploadFileDao skipNodeUploadFileDao;
    @Autowired
    private FileDao fileDao;
    @Autowired
    private TakeLandService takeLandService;
    @Autowired
    private WorkflowChangeDao workflowChangeDao;
    /**
     * 查询补录的附件列表
     * @param map
     * @return
     */
    @Override
    public ResultBody getRepairFileList(Map map) {
        try {

            Map<Object, Object> resultMap = new HashMap<>();
            map.put("BOID", map.get("flow_id") + "");
            List workflowSelect = workflowChangeDao.workflowSelect(map);
            if(workflowSelect!=null&&workflowSelect.size()>0){
                resultMap.put("flowData", workflowSelect.get(0));
            }
            List<Map> fileList = skipNodeUploadFileDao.getRepairFileList(map.get("flow_id") + "");
            Map nodeDataByFlowId = skipNodeUploadFileDao.getNodeDataByFlowId(map.get("flow_id") + "");
            resultMap.put("nodeInfo",nodeDataByFlowId);
            resultMap.put("fileList",fileList);
            return ResultBody.success(resultMap);
        }catch (Exception e){
            return ResultBody.error(-1004,"补录附件列表查询失败:"+e.toString());
        }
    }

    @Override
    public ResultBody saveRepairFile(Map map, HttpServletRequest request) {
        try {
            String flow_id=map.get("flow_id")+"";
            String button=map.get("button")+"";

            String plan_node_id=map.get("plan_node_id")+"";
            String nodeName = skipNodeUploadFileDao.getNodeName(plan_node_id);
            //清楚先前上传的附件
            fileDao.delFileByBizId(flow_id);
            //保存新附件
            List<Map> fileList = (List<Map>) map.get("fileList");
            if(fileList!=null&&fileList.size()>0){
                for (Map fileMap : fileList) {
                    fileMap.put("flow_id",flow_id);
                    skipNodeUploadFileDao.updateFileStatus(fileMap);
                }
            }

            if("submit".equals(button)||"ksApproval".equals(button)){
                //获取流程数据
                HashMap<Object, Object> paramMap = new HashMap<>();
                paramMap.put("json_id",flow_id);
                paramMap.put("project_id",map.get("project_id")+"");
                paramMap.put("flow_code","Supplementary_record");
                //区分首开计划还是补录附件
                String username = request.getHeader("username");
                paramMap.put("orgName","fp_Supplementary_record");
                paramMap.put("creator",username);
                paramMap.put("comcommon", JSON.toJSONString(map));
                paramMap.put("TITLE",nodeName+"附件补录");
                paramMap.put("flow_type","fp_Supplementary_record");
                if("submit".equals(button)){
                    takeLandService.insertFlow(paramMap);
                    return ResultBody.success(paramMap);
                }else{
                    //快速审批
                    skipNodeUploadFileDao.updateFileStatusIseffective(flow_id);
                    return ResultBody.success(null);
                }
            }
            return ResultBody.success(null);
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-1005,"附件保存/提交失败:"+e.toString());
        }
    }

    @Override
    public ResultBody applayCallback(Map paramMap) {
        try {
            //快速审批
           String eventType=paramMap.get("eventType")+"";

            String json_id=paramMap.get("businesskey")+"";
           if("4".equals(eventType)){
               skipNodeUploadFileDao.updateFileStatusIseffective(json_id);
               return ResultBody.success(null);
           }else{
               return ResultBody.success(null);
           }
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-1095,"审批失败回调!");
        }


    }

}
