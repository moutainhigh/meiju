package cn.visolink.firstplan.skipnodeupload.service;

import cn.hutool.http.HttpRequest;
import cn.visolink.exception.ResultBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/6/28 3:30 下午
 */
public interface SkipNodeUploadFileService {
    //查询补录附件列表
    public ResultBody getRepairFileList(Map map);

    //略过节点补录附件保存/提交
    public ResultBody saveRepairFile(Map map, HttpServletRequest request);

    //oa审批通过回调接口
    public ResultBody applayCallback(Map paramMap);
}
