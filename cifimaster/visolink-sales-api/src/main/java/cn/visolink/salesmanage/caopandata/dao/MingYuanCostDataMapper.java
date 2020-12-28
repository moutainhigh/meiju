package cn.visolink.salesmanage.caopandata.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2019/12/10 4:04 下午
 */
@Mapper
public interface MingYuanCostDataMapper {
    //清空明源费用在本系统的数据
    Integer emptyMingYuanCost();

    Integer initedCostData(List<Map<String, Object>> signData);
}
