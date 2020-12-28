package cn.visolink.salesmanage.checklist.mapper;

import cn.visolink.salesmanage.checklist.entity.ChecklistDetail;
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
 * 佣金核算单明细 Mapper 接口
 * </p>
 *
 * @author yangjie
 * @since 2020-05-14
 */
@Mapper
@Repository
public interface ChecklistDetailMapper extends BaseMapper<ChecklistDetail> {

    /**
     * 分页查询，佣金核算单明细
     *
     * @param page page
     * @param map  map
     * @return return
     */
    @Select("SELECT t1.*,t2.employee_name FROM `cm_checklist_detail` AS t1 INNER JOIN `cm_commission` AS t2 \n" +
            "ON t1.commission_id = t2.id AND t1.isdel = '0' AND t2.is_del = '0' AND t1.fid = #{map.checklistId} \n" +
            "AND CASE WHEN #{map.keyWord} IS NULL THEN TRUE ELSE \n" +
            "(t1.room_name LIKE CONCAT('%',#{map.keyWord},'%') OR t2.employee_name LIKE CONCAT('%',#{map.keyWord},'%') \n" +
            "OR t2.customer_name LIKE CONCAT('%',#{map.keyWord},'%') OR t2.gain_by LIKE CONCAT('%',#{map.keyWord},'%') \n" +
            "OR t2.bank_name LIKE CONCAT('%',#{map.keyWord},'%') OR t2.bank_num LIKE CONCAT('%',#{map.keyWord},'%') \n" +
            "OR t2.reportIdCard LIKE CONCAT('%',#{map.keyWord},'%')) END ORDER BY t1.business_attribution_code")
    List<Map> getChecklistDetailListPage(Page<Map> page, @Param(value = "map") Map<String, String> map);
}
