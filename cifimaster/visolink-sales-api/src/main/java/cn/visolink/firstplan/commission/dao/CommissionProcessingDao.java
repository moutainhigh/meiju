package cn.visolink.firstplan.commission.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 待结佣数据清洗 Mapper接口
 * </p>
 *
 * @author baoql
 * @since 2020-05-25
 */

@Mapper
@Repository
public interface CommissionProcessingDao {

    /**
     * 修改立项后、渠道变更情况的，交易数据
     *
     * @param ids      id拼接in字符串
     * @return int
     * */
    List<String> selectMyStatus(@Param("ids")String ids);

    /**
     * 将交易状态置为失效
     *
     * @param list list
     * @return int
     * */
    int updateMyStatus(List<String> list);

    /**
     * 查询明源为激活 and 本系统内为激活，的条数
     *
     * @param ids      id拼接in字符串
     * @return int
     * */
    int selectNewSourceTypeDesc(@Param("ids")String ids);

    /**
     * 查询明源为激活 and 本系统内为激活，的条数
     *
     * @param ids      id拼接in字符串
     * @return int
     * */
    int initNewSourceTypeDesc(@Param("ids")String ids);

    /**
     * 添加渠道变化后的新数据，交易id与原数据相同，状态置为激活
     *
     * @param ids      id拼接in字符串
     * @return int
     * */
    int countUpdateCmCommission(@Param("ids")String ids);

    /**
     * 更新明源为激活 and 本系统内为激活，的数据内容
     *
     * @param ids      id拼接in字符串
     * @return int
     * */
    int updateCmCommission(@Param("ids")String ids);

    /**
     * 查询明源为关闭 and 本系统内为激活，的数据
     *
     * @param ids      id拼接in字符串
     * @return list
     * */
    List<String> selectNewStatus(@Param("ids")String ids);

    /**
     * 更新明源为关闭 and 本系统内为激活，的数据的状态置为关闭
     *
     * @param list list
     * @return row
     * */
    int updateNewStatus(List<String> list);


    /**
     * 查询付款通过后，状态刚刚被置为失效的数据，为欠款条数
     *
     * @return row
     * */
    int countCommission();

    /**
     * 添加付款通过后，状态刚刚被置为失效的数据，为欠款数据
     *
     * @return row
     * */
    int insertCommission();

    /**
     * 查询付款通过后，状态刚刚被置为失效的数据，为欠款条数
     *
     * @param ids      id拼接in字符串
     * @return row
     * */
    int countQkCommission(@Param("ids")String ids);

    /**
     * 添加付款通过后，状态刚刚被置为失效的数据，为欠款数据
     *
     * @param ids      id拼接in字符串
     * @return row
     * */
    int insertQkCommission(@Param("ids")String ids);

    /**
     * 查询立项后退房的核算单数据
     *
     * @param ids      id拼接in字符串
     * @return list
     * */
    List<String> selectIsAbnormal(@Param("ids")String ids);

    /**
     * 把立项后退房的核算单状态置为异常
     *
     * @param list list
     * @return row
     * */
    int updateIsAbnormal(List<String> list);

    /**
     * 同步orgId
     *
     * @param ids      id拼接in字符串
     * @return row
     * */
    int updateOrgId(@Param("ids")String ids);
}
