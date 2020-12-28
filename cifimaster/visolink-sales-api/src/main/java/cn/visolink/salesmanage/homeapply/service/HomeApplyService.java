package cn.visolink.salesmanage.homeapply.service;


import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.homeapply.entity.HomeApply;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 夏威审批流业务接口
 * </p>
 *
 * @author bql
 * @since 2020-09-16
 */

public interface HomeApplyService extends IService<HomeApply> {

    /**
     * 获取申请人区域、部门、申请时间
     *
     * @param map map
     * @return map
     * */
    Map<String,Object> getApplyInfo(Map<String,Object> map);

    /**
     * 查询审批审请
     *
     * @param map map
     * @return ResultBody
     * */
    ResultBody selectHomeApply(Map<String,Object> map);

    /**
     * 保存申请
     *
     * @param request request
     * @param map map
     * @return int
     * */
    int initHomeApply(HttpServletRequest request, Map<String,Object> map);

    /**
     * 失效申请
     *
     * @param map map
     * @return int
     * */
    int deleteHomeApply(Map<String,Object> map);
    /**
     * 发送申请
     *
     * @param request request
     * @param map map
     * @return boolean
     * */
    ResultBody initHomeApplyFlow(HttpServletRequest request,Map<String,Object> map);
}
