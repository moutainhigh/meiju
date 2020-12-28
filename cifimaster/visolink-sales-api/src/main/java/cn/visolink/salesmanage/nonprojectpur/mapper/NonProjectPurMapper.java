package cn.visolink.salesmanage.nonprojectpur.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 非工程采购数据处理持久化
 * </p>
 *
 * @author bql
 * @since 2020-11-26
 */
@Mapper
@Repository
public interface NonProjectPurMapper {

    /**
     * 清空非工程采购数据
     *
     * @return row
     * */
    int deleteNonProjectPur();

    /**
     * 根据code删除非工程采购数据
     *
     * @param businessCode 为一code
     * @return row
     * */
    int deleteNonProjectPurByCode(@Param("businessCode") String businessCode);

    /**
     * 初始化非工程采购数据
     *
     * @param list 数据
     * @return row
     * */
    int initNonProjectPur(@Param("list") List<Map<String,Object>> list);




}
