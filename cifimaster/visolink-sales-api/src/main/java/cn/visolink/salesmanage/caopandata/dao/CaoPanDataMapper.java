package cn.visolink.salesmanage.caopandata.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2019/11/9 11:06 上午
 */
@Mapper
public interface CaoPanDataMapper {
    /**
     *
     * 清空管控系统签约信息表
     */
    Integer emptySinggingData();

    Integer initedSignData(List<Map<String, Object>> signData);

    void mergeData();

    void deleteCaoPanByDate(String startTime);

    void updateCaoPanInfo(String startTime);
}
