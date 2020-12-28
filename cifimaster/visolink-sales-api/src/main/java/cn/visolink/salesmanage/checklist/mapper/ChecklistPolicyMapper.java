package cn.visolink.salesmanage.checklist.mapper;

import cn.visolink.salesmanage.checklist.entity.ChecklistPolicy;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 佣金核算单-政策 Mapper 接口
 * </p>
 *
 * @author yangjie
 * @since 2020-05-14
 */
@Mapper
@Repository
public interface ChecklistPolicyMapper extends BaseMapper<ChecklistPolicy> {

    /**
     * 分页查询，佣金核算单-政策
     *
     * @param page page
     * @param map  map
     * @return return
     */
    @Select("SELECT * FROM `cm_checklist_policy` WHERE id = #{map.id}")
    List<Map> getChecklistPolicyListPage(Page<Map> page, @Param(value = "map") Map<String, String> map);
}
