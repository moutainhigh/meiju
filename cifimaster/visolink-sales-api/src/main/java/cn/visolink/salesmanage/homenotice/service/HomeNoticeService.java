package cn.visolink.salesmanage.homenotice.service;


import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.homeapply.entity.HomeApply;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * <p>
 * 夏威审批流业务接口
 * </p>
 *
 * @author bql
 * @since 2020-09-16
 */

public interface HomeNoticeService {


    /**
     * 提示首页公告
     *
     * @param map map
     * @return map
     * */
    ResultBody getHomeNotice(Map<String,Object> map);

    /**
     * 已阅
     *
     * @param map map
     * @return map
     * */
     ResultBody intoHomeNoticeRead(Map<String,Object> map);

    /**
     * 获取单个公告数据
     *
     * @param map map
     * @return map
     * */
    Map<String,Object> getHomeNoticeInfo(Map<String,Object> map);

    /**
     * 查询审批审请
     *
     * @param map map
     * @return ResultBody
     * */
    ResultBody selectHomeNotice(Map<String, Object> map);

    /**
     * 保存通知
     *
     * @param request request
     * @param map map
     * @return int
     * */
    int initHomeNotice(HttpServletRequest request, Map<String, Object> map);

    /**
     * 失效通知
     *
     * @param map map
     * @return int
     * */
    int deleteHomeNotice(Map<String, Object> map);


    /**
     * 失效文件
     *
     * @param map map
     * @return int
     * */
    int isDelFile(Map<String, Object> map);
}
