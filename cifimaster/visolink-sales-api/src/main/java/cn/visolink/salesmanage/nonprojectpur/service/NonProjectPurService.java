package cn.visolink.salesmanage.nonprojectpur.service;


import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 非工程采购数据处理接口
 * </p>
 *
 * @author bql
 * @since 2020-11-26
 */

public interface NonProjectPurService {

    /**
     * 初始化非工程采购数据
     *
     * */
    void initNonProjectPur();


    /**
     * 明源对接数据接口，修改付款单状态
     *
     * @param request request
     * @param dateList 参数
     * @return ResultBody
     */
    Map updateNonProjectPur(HttpServletRequest request,  List<Map<String, Object>> dateList);
}
