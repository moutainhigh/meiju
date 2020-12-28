package cn.visolink.salesmanage.signdata.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2019/11/18 8:02 下午
 */
@Mapper
public interface SingDataMapper {

    /**
     * 获取月度签约金额数据
     * @param map
     * @return
     */
    List<Map> getSingMonthData(Map map);
    /**
     * 获取周度签约金额数据
     * @param map
     * @return
     */
    List<Map> getSingWeekData(Map map);
    /**
     * 获取某一时间段的来人量
     */
    List<Map> getVistiCountData(Map map);

}
