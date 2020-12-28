package cn.visolink.salesmanage.investment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author yangjie
 * @date 2020-10-26
 */
@Mapper
@Repository
public interface DockingInvestmentMapper {

    /**
     * 根据项目id，获取项目编码
     *
     * @param map map
     * @return return
     */
    Map<String, Object> getProjectNumByProjectId(@Param("map") Map<String, Object> map);

    /**
     * 获取业态名称
     *
     * @return return
     */
    List<Map<String, Object>> getDictName();
}
