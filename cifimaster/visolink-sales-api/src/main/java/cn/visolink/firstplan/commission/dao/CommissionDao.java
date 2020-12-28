package cn.visolink.firstplan.commission.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 待结佣 Mapper接口
 * </p>
 *
 * @author baoql
 * @since 2020-05-25
 */

@Mapper
@Repository
public interface CommissionDao {

    /**
     * 旭客客户数据清除
     */
    void delXk();


    /**
     * 旭客客户数据对比查询
     *
     * @param list list
     * @return int
     */
    List<String> selectXKordercst(@Param("tructureList") List<Map<String, Object>> list);


    /**
     * 旭客客户数据添加
     *
     * @param list list
     * @return int
     */
    int insertXkOrdercst(@Param("tructureList") List<Map<String, Object>> list);


    /**
     * 旭客佣金数据修改
     *
     * @param list list
     * @return int
     */
    int updateXkOrdercst(@Param("tructureList") List<Map<String, Object>> list);



    /**
     * 明源佣金数据清除
     */
    void delMy();

    /**
     * 明源佣金局部数据删除
     *
     * @param ids id拼接in字符串
     * @return int
     */
    int delMyOrdercst(String ids);

    /**
     * 明源佣金数据对比查询
     *
     * @param list list
     * @return int
     */
    List<String> selectMyOrdercst(@Param("tructureList") List<Map<String, Object>> list);


    /**
     * 明源佣金数据添加
     *
     * @param list list
     * @return int
     */
    int insertMyOrdercst(@Param("tructureList") List<Map<String, Object>> list);

    /**
     * 明源佣金数据修改
     *
     * @param list list
     * @return int
     */
    int updateMyOrdercst(@Param("tructureList") List<Map<String, Object>> list);

    /**
     * 待结佣局部数据更新
     *
     * @param ids      id拼接in字符串
     */
    void updateMyCommission( @Param("ids")String ids);

    /**
     * 待结佣数据更新
     *
     * @return int
     */
    int insertCommission();


    /**
     * 待结佣数据单项目更新
     *
     * @return int
     */
    int insertCommissionByProject(@Param("projectId")String projectId,@Param("kingdee")String kingdee);

    /**
     * 根据主数据项目id获取金蝶项目id
     *
     * @return int
     */
    String selectProjectId(@Param("projectId")String projectId);

    /**
     * 佣待结佣数据查询
     *
     * @param map 查询条件
     * @return List
     */
    List<Map<String,String>> selectCommission(Map map);


    /**
     * 佣待结佣导出数据导出
     *
     * @param list list
     * @return List
     */
    List<Map<String,String>> selectExcelCommission(List list);

    /**
     * 佣金金额、佣金点位修改
     *
     * @param map      修改数据
     */
    void updateCommission(Map map);

    /**
     * 佣金金额修改
     *
     * @param map      修改数据
     */
    void updateCommissionMoney(Map map);

    /**
     * 佣金点位修改
     *
     * @param map      修改数据
     */
    void updateCommissionPercentage(Map map);

    /**
     * 结佣转不结佣
     *
     * @param list     list
     * @param username 当前登录人
     */
    void updateCommissionIsNo( @Param("list")List list, @Param("username") String username);

    /**
     * 不结佣转结佣
     *
     * @param list     list
     * @param username 当前登录人
     */
    void updateCommissionIsYse(@Param("list")List list, @Param("username") String username);

    /**
     * 不结佣数据添加
     *
     * @param list     list
     * @param map 当前登录人
     */
    void initCommissionNo(@Param("list")List list, @Param("map") Map map);

    /**
     * 不结佣数据删除
     *
     * @param list list
     */
    void delCommissionNo(List list);

    /**
     * 不结佣数据查询
     *
     * @param map 查询条件
     * @return List
     */
    List<Map<String,String>> selectCommissionNo(Map map);

    /**
     * 不结佣数据查询条数
     *
     * @param map 查询条件
     * @return List
     */
    Long selectCommissionNo_COUNT(Map map);

    /**
     * 不结佣修改发放时间
     *
     * @param list     发放数据
     * @param username 当前登陆人
     */
    void updateGrant(@Param("list")List list, @Param("username") String username);

    /**
     * 查询经纪人身份
     *
     * @return List list
     */
    List<Map> getCurrentRole();

    /**
     * 查询渠道类型
     *
     * @return List list
     */
    List<Map> getSourceTypeDesc();

    /**
     * 查询业绩归属
     *
     * @param map  map
     * @return List list
     */
    List<Map> getGainBy(Map map);

    /**
     * 不借用备注修改
     *
     * @param map     修改数据
     * @return Integer
     */
    Integer updateCommissionNo(Map map);

    /**
     * 不借用备注修改
     *
     * @param map     修改数据
     * @return Integer
     */
    Integer updateNoPayment(Map map);

}
