package cn.visolink.salesmanage.workflowchange.service;

import cn.visolink.exception.ResultBody;

import java.util.List;
import java.util.Map;

/**
 *
 *  * @author lihuan
 *  * @since 2019-11-12
 */
public interface WorkflowChangeService {


    /**
     *签约后变更神批发起
     * @param map
     * @return
     */
    public Map workflowSend(Map<String, Object> map);

    /**
     *签约后变更神批表单查询
     * @param jsonId
     * @return
     */
    public List<Map> workflowSelect(String jsonId);

    /**
     *提供给明源的作废接口
     * @param
     * @return
     */
    public Map workflowEnd(Map<String,Object> map);


    /**
     * 外部系统写入数据
     */
    public ResultBody writeBusinessData(Map map);


}
