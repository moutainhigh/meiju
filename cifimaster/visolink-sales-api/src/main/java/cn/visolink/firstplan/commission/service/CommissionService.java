package cn.visolink.firstplan.commission.service;

import cn.visolink.exception.ResultBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 待结佣 Service接口
 * </p>
 *
 * @author baoql
 * @since 2020-05-25
 */

public interface CommissionService {

    /**
     * 待结佣数据初始化(全量)
     *
     * @return boolean
     */
    boolean initCommission();

    /**
     * 待结佣数据初始化（增量）
     * bql 2020.07.29
     *
     * @param modifiedTime 获取开始日期（结束时间为当前时间）
     * @return boolean
     */
    boolean initCommission(String modifiedTime);

    /**
     * 根据交易id待结佣数据初始化
     * bql 2020.09.21
     *
     * @param ids 交易id
     * @return boolean
     */
    boolean commissionProcessing(String ids);

    /**
     * 待结佣数据初始化
     * bql 2020.07.29
     *
     * @param modifiedTime 获取开始日期（结束时间为当前时间）
     * @return boolean
     */
    boolean initCommission(String modifiedTime,String projectId);

    /**
     * 明源数据局部更新
     *
     * @param list list
     * @return ResultBody
     */
    ResultBody updateMyTrade(List<String> list);

    /**
     * 待结佣数据查询
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody selectCommission(Map<String, Object> map);

    /**
     * 待结佣数据导出
     *
     * @param request  request
     * @param response response
     * @param ids      ids
     */
    void exportExcelCommission(HttpServletRequest request, HttpServletResponse response, String ids,Map<String,Object> map,String type);

    /**
     * 不结佣数据导出
     *
     * @param request  request
     * @param response response
     * @param map      map
     */
    void exportExcelCommissionNo(HttpServletRequest request, HttpServletResponse response,Map<String,Object> map);

    /**
     * 不结佣数据修改
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody updateCommissionNo(Map map);

    /**
     * 不结佣发放金额修改
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody updateCommissionNoPayment(Map map);


    /**
     * 待结佣数据导入
     *
     * @param file   file
     * @param months months
     * @return ResultBody
     */
    ResultBody commissionImport(MultipartFile file, String months);

    /**
     * 不结佣数据添加
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody initCommissionNo(Map map);

    /**
     * 不结佣数据查询
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody selectCommissionNo(Map<String, Object> map);

    /**
     * 不结佣修改发放时间
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody updateGrant(Map map);

    /**
     * 查询经纪人身份
     *
     * @return ResultBody
     */
    ResultBody getCurrentRole();

    /**
     * 判断当前成交类型
     *
     * @param map map
     * @return map
     */
    Map<String, Object> setSourceTypeDesc(Map<String, Object> map);


    /**
     * 分页数据查询合并方法
     *
     * @param map  参数
     * @param maps 结果
     * @return map
     */
    ResultBody getResultBody(Map<String, Object> map, List<Map<String,String>> maps);

    /**
     * 查询业绩归属
     *
     * @param map  参数
     * @return map
     */
    ResultBody getGainBy(Map map);
}
