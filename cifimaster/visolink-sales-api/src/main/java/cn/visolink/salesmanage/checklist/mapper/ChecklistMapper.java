package cn.visolink.salesmanage.checklist.mapper;

import cn.visolink.salesmanage.checklist.entity.Checklist;
import cn.visolink.salesmanage.checklist.entity.ChecklistDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 佣金核算单 Mapper 接口
 * </p>
 *
 * @author yangjie
 * @since 2020-05-14
 */
@Mapper
@Repository
public interface ChecklistMapper extends BaseMapper<Checklist> {

    /**
     * 根据ids，获取 未在核算单内的待结佣
     *
     * @param commissionIds commissionIds
     * @return return
     */
    @Select("<script>" +
            "SELECT * FROM `cm_commission` WHERE is_del = 0 AND is_bill = 1 AND id IN" +
            "<foreach collection='commissionIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<Map<String, Object>> getNotCommissionByIds(@Param("commissionIds") List<String> commissionIds);

    /**
     * 根据ids，获取 待结佣
     *
     * @param commissionIds commissionIds
     * @return return
     */
    @Select("<script>" +
            "SELECT * FROM `cm_commission` WHERE is_del = 0 AND id IN" +
            "<foreach collection='commissionIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<Map<String, String>> getCommissionByIds(@Param("commissionIds") List<String> commissionIds);

    /**
     * 获取 当期日期，最大的核算单编号
     *
     * @param checklistCodeDate checklistCodeDate
     * @return return
     */
    @Select("SELECT MAX(checklist_code) FROM `cm_checklist` WHERE isdel = 0 \n" +
            "AND create_time >= DATE_ADD(#{checklistCodeDate},INTERVAL 0 DAY) AND create_time < DATE_ADD(#{checklistCodeDate},INTERVAL 1 DAY)")
    String getMaxChecklistCode(@Param("checklistCodeDate") String checklistCodeDate);

    /**
     * 修改待结佣状态 在核算单内
     *
     * @param commissionIds commissionIds
     * @return return
     */
    @Update("<script>" +
            "UPDATE `cm_commission` SET is_bill = 0 WHERE is_del = 0 AND is_bill = 1 AND id IN" +
            "<foreach collection='commissionIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    Integer updateCommissionByIds(@Param("commissionIds") List<String> commissionIds);

    /**
     * 修改 待结佣状态（在核算单外）
     *
     * @param commissionIds commissionIds
     * @return return
     */
    @Update("<script>" +
            "UPDATE `cm_commission` SET is_bill = 1 WHERE is_del = 0 AND is_bill = 0 AND id IN" +
            "<foreach collection='commissionIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    Integer updateCommissionByIds2(@Param("commissionIds") List<String> commissionIds);

    /**
     * 分页查询，佣金核算单
     *
     * @param page page
     * @param map  map
     * @return return
     */
    @Select("SELECT * FROM `cm_checklist` AS t1 WHERE isdel = 0 \n" +
            "AND CASE WHEN #{map.orgId} IS NULL THEN TRUE ELSE org_id IN " +
            "(SELECT DISTINCT orgId FROM `s_jobsorgrel` WHERE " +
            "CASE WHEN #{map.orgLevel} = 1 THEN groupOrgId = #{map.orgId} " +
            "WHEN #{map.orgLevel} = 2 THEN areaOrgId = #{map.orgId} " +
            "WHEN #{map.orgLevel} = 3 THEN cityOrgId = #{map.orgId} " +
            "WHEN #{map.orgLevel} = 4 THEN projectOrgId = #{map.orgId} ELSE FALSE END) END \n" +
            "AND CASE WHEN #{map.dealType} IS NULL THEN TRUE ELSE deal_type = #{map.dealType} END \n" +
            "AND CASE WHEN #{map.isFather} IS NULL THEN TRUE ELSE is_father = #{map.isFather} END \n" +
            "AND CASE WHEN #{map.checklistCode} IS NULL OR #{map.checklistCode} = '' THEN TRUE ELSE checklist_code LIKE CONCAT('%',#{map.checklistCode},'%') END \n" +
            "AND CASE WHEN #{map.projectStatus} IS NULL OR #{map.projectStatus} = '全部' THEN TRUE ELSE project_status IN('${map.projectStatus}') END \n" +
            "AND CASE WHEN #{map.isAbnormal} IS NULL OR #{map.isAbnormal} = '全部' THEN TRUE " +
            "WHEN #{map.isAbnormal} = '是' THEN is_abnormal = 1 ELSE is_abnormal = 0 END \n" +
            "AND CASE WHEN #{map.isSettle} IS NULL OR #{map.isSettle} = '全部' THEN TRUE WHEN #{map.isSettle} = '是' " +
            "THEN is_father = 0 OR 0 = " +
            "(SELECT COUNT(*) FROM `cm_checklist_detail` AS t3 INNER JOIN `cm_commission` AS t2 \n" +
            "ON t3.commission_id = t2.id AND t3.isdel = 0 AND t2.is_del = 0 \n" +
            "AND t3.outstanding_amount != 0 AND t2.my_STATUS != '关闭' WHERE t3.checklist_id = t1.id) " +
            "ELSE is_father = 1 AND 0 < " +
            "(SELECT COUNT(*) FROM `cm_checklist_detail` AS t3 INNER JOIN `cm_commission` AS t2 \n" +
            "ON t3.commission_id = t2.id AND t3.isdel = 0 AND t2.is_del = 0 \n" +
            "AND t3.outstanding_amount != 0 AND t2.my_STATUS != '关闭' WHERE t3.checklist_id = t1.id) END \n" +
            "AND CASE WHEN #{map.startTime} IS NULL THEN TRUE ELSE create_time >= #{map.startTime} END \n" +
            "AND CASE WHEN #{map.endTime} IS NULL THEN TRUE ELSE create_time <= DATE_ADD(#{map.endTime},INTERVAL 1 DAY) END \n" +
            "AND CASE WHEN #{map.keyWord} IS NULL OR #{map.keyWord} = '' THEN TRUE ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '核算单编号' THEN (checklist_code LIKE CONCAT('%',#{map.keyWord},'%') OR fcode LIKE CONCAT('%',#{map.keyWord},'%')) ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '核算单名称' THEN checklist_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '立项编号' THEN project_code LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "id IN(SELECT DISTINCT t1.fid FROM `cm_checklist_detail` AS t1 INNER JOIN `cm_commission` AS t2 \n" +
            "ON t1.commission_id = t2.id AND t1.isdel = '0' AND t2.is_del = '0' \n" +
            "AND CASE WHEN #{map.channelName} IS NULL OR #{map.channelName} = '全部' THEN TRUE ELSE t1.business_attribution_code = #{map.channelName} END \n" +
            "AND CASE WHEN #{map.keyWordType} = '房间信息' THEN t2.room_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '置业顾问' THEN t2.employee_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '客户姓名' THEN t2.customer_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '报备人' THEN t2.gain_by LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '银行卡号' THEN t2.bank_num LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '身份证号' THEN t2.reportIdCard LIKE CONCAT('%',#{map.keyWord},'%') ELSE FALSE \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END " +
            "UNION \n" +
            "SELECT DISTINCT t1.checklist_id FROM `cm_checklist_detail` AS t1 INNER JOIN `cm_commission` AS t2 \n" +
            "ON t1.commission_id = t2.id AND t1.isdel = '0' AND t2.is_del = '0' \n" +
            "AND CASE WHEN #{map.channelName} IS NULL OR #{map.channelName} = '全部' THEN TRUE ELSE t1.business_attribution_code = #{map.channelName} END \n" +
            "AND CASE WHEN #{map.keyWordType} = '房间信息' THEN t2.room_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '置业顾问' THEN t2.employee_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '客户姓名' THEN t2.customer_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '报备人' THEN t2.gain_by LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '银行卡号' THEN t2.bank_num LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '身份证号' THEN t2.reportIdCard LIKE CONCAT('%',#{map.keyWord},'%') ELSE FALSE \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END) \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END ORDER BY ${map.orderBy}")
    List<Map> getChecklistListPage(Page<Map> page, @Param(value = "map") Map<String, String> map);

    /**
     * 根据搜索条件，获取核算单id
     *
     * @param map map
     * @return return
     */
    @Select("SELECT id FROM `cm_checklist` AS t1 WHERE isdel = 0 \n" +
            "AND CASE WHEN #{map.orgId} IS NULL THEN TRUE ELSE org_id IN " +
            "(SELECT DISTINCT orgId FROM `s_jobsorgrel` WHERE " +
            "CASE WHEN #{map.orgLevel} = 1 THEN groupOrgId = #{map.orgId} " +
            "WHEN #{map.orgLevel} = 2 THEN areaOrgId = #{map.orgId} " +
            "WHEN #{map.orgLevel} = 3 THEN cityOrgId = #{map.orgId} " +
            "WHEN #{map.orgLevel} = 4 THEN projectOrgId = #{map.orgId} ELSE FALSE END) END \n" +
            "AND CASE WHEN #{map.dealType} IS NULL THEN TRUE ELSE deal_type = #{map.dealType} END \n" +
            "AND CASE WHEN #{map.isFather} IS NULL THEN TRUE ELSE is_father = #{map.isFather} END \n" +
            "AND CASE WHEN #{map.checklistCode} IS NULL OR #{map.checklistCode} = '' THEN TRUE ELSE checklist_code LIKE CONCAT('%',#{map.checklistCode},'%') END \n" +
            "AND CASE WHEN #{map.projectStatus} IS NULL OR #{map.projectStatus} = '全部' THEN TRUE ELSE project_status IN('${map.projectStatus}') END \n" +
            "AND CASE WHEN #{map.isAbnormal} IS NULL OR #{map.isAbnormal} = '全部' THEN TRUE " +
            "WHEN #{map.isAbnormal} = '是' THEN is_abnormal = 1 ELSE is_abnormal = 0 END \n" +
            "AND CASE WHEN #{map.isSettle} IS NULL OR #{map.isSettle} = '全部' THEN TRUE WHEN #{map.isSettle} = '是' " +
            "THEN is_father = 0 OR 0 = " +
            "(SELECT COUNT(*) FROM `cm_checklist_detail` AS t3 INNER JOIN `cm_commission` AS t2 \n" +
            "ON t3.commission_id = t2.id AND t3.isdel = 0 AND t2.is_del = 0 \n" +
            "AND t3.outstanding_amount != 0 AND t2.my_STATUS != '关闭' WHERE t3.checklist_id = t1.id) " +
            "ELSE is_father = 1 AND 0 < " +
            "(SELECT COUNT(*) FROM `cm_checklist_detail` AS t3 INNER JOIN `cm_commission` AS t2 \n" +
            "ON t3.commission_id = t2.id AND t3.isdel = 0 AND t2.is_del = 0 \n" +
            "AND t3.outstanding_amount != 0 AND t2.my_STATUS != '关闭' WHERE t3.checklist_id = t1.id) END \n" +
            "AND CASE WHEN #{map.startTime} IS NULL THEN TRUE ELSE create_time >= #{map.startTime} END \n" +
            "AND CASE WHEN #{map.endTime} IS NULL THEN TRUE ELSE create_time <= DATE_ADD(#{map.endTime},INTERVAL 1 DAY) END \n" +
            "AND CASE WHEN #{map.keyWord} IS NULL OR #{map.keyWord} = '' THEN TRUE ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '核算单编号' THEN (checklist_code LIKE CONCAT('%',#{map.keyWord},'%') OR fcode LIKE CONCAT('%',#{map.keyWord},'%')) ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '核算单名称' THEN checklist_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '立项编号' THEN project_code LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "id IN(SELECT DISTINCT t1.fid FROM `cm_checklist_detail` AS t1 INNER JOIN `cm_commission` AS t2 \n" +
            "ON t1.commission_id = t2.id AND t1.isdel = '0' AND t2.is_del = '0' \n" +
            "AND CASE WHEN #{map.channelName} IS NULL OR #{map.channelName} = '全部' THEN TRUE ELSE t1.business_attribution_code = #{map.channelName} END \n" +
            "AND CASE WHEN #{map.keyWordType} = '房间信息' THEN t2.room_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '置业顾问' THEN t2.employee_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '客户姓名' THEN t2.customer_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '报备人' THEN t2.gain_by LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '银行卡号' THEN t2.bank_num LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '身份证号' THEN t2.reportIdCard LIKE CONCAT('%',#{map.keyWord},'%') ELSE FALSE \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END " +
            "UNION \n" +
            "SELECT DISTINCT t1.checklist_id FROM `cm_checklist_detail` AS t1 INNER JOIN `cm_commission` AS t2 \n" +
            "ON t1.commission_id = t2.id AND t1.isdel = '0' AND t2.is_del = '0' \n" +
            "AND CASE WHEN #{map.channelName} IS NULL OR #{map.channelName} = '全部' THEN TRUE ELSE t1.business_attribution_code = #{map.channelName} END \n" +
            "AND CASE WHEN #{map.keyWordType} = '房间信息' THEN t2.room_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '置业顾问' THEN t2.employee_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '客户姓名' THEN t2.customer_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '报备人' THEN t2.gain_by LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '银行卡号' THEN t2.bank_num LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '身份证号' THEN t2.reportIdCard LIKE CONCAT('%',#{map.keyWord},'%') ELSE FALSE \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END) \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END ORDER BY ${map.orderBy}")
    List<String> getChecklistIds(@Param(value = "map") Map<String, String> map);

    /**
     * 分页查询，佣金核算单明细
     *
     * @param page page
     * @param map  map
     * @return return
     */
    @Select("SELECT t1.*,t2.business_unit_name,t2.project_name,t2.room_name,t2.transaction_status,t2.now_price, \n" +
            "t2.back_price,t2.collection_proportion,t2.source_type_desc,t2.gain_by,t2.current_role,t2.employee_name, \n" +
            "t2.customer_name,t2.subscription_date,t2.signing_date,t2.bank_num,t2.bank_name,t2.reportIdCard, \n" +
            "t2.customer_mobile,t2.report_mobile,t2.my_STATUS,t2.transactionStatus,t2.built_up_area," +
            "t3.project_status,t3.commission_type " +
            "FROM `cm_checklist_detail` AS t1 INNER JOIN `v_commission` AS t2 \n" +
            "ON t1.commission_id = t2.id AND t1.isdel = '0' AND t2.is_del = '0' " +
            "INNER JOIN `cm_checklist` AS t3 ON t1.checklist_id = t3.id AND t3.isdel = '0' " +
            "WHERE ((t1.fid = #{map.checklistId} and t3.project_status = 4 ) OR t1.checklist_id = #{map.checklistId}) \n" +
            "AND CASE WHEN #{map.startKeyWord} IS NULL OR #{map.startKeyWord} = '' THEN TRUE ELSE \n" +
            "CASE WHEN #{map.keyWordType2} = '回款比例' THEN t2.collection_proportion >= #{map.startKeyWord} ELSE \n" +
            "CASE WHEN #{map.keyWordType2} = '佣金点位' THEN t1.commission_point >= #{map.startKeyWord} ELSE FALSE END \n" +
            "END \n" +
            "END \n" +
            "AND CASE WHEN #{map.endKeyWord} IS NULL OR #{map.endKeyWord} = '' THEN TRUE ELSE \n" +
            "CASE WHEN #{map.keyWordType2} = '回款比例' THEN t2.collection_proportion <= #{map.endKeyWord} ELSE \n" +
            "CASE WHEN #{map.keyWordType2} = '佣金点位' THEN t1.commission_point <= #{map.endKeyWord} ELSE FALSE END \n" +
            "END \n" +
            "END \n" +
            "AND CASE WHEN #{map.startSubscribeTime} IS NULL THEN TRUE ELSE t2.subscription_date >= #{map.startSubscribeTime} END \n" +
            "AND CASE WHEN #{map.endSubscribeTime} IS NULL THEN TRUE ELSE t2.subscription_date <= DATE_ADD(#{map.endSubscribeTime},INTERVAL 1 DAY) END \n" +
            "AND CASE WHEN #{map.startSignTime} IS NULL THEN TRUE ELSE t2.signing_date >= #{map.startSignTime} END \n" +
            "AND CASE WHEN #{map.endSignTime} IS NULL THEN TRUE ELSE t2.signing_date <= DATE_ADD(#{map.endSignTime},INTERVAL 1 DAY) END \n" +
            "AND CASE WHEN #{map.startProjectAmount} IS NULL THEN TRUE ELSE t1.project_amount >= #{map.startProjectAmount} END\n" +
            "AND CASE WHEN #{map.endProjectAmount} IS NULL THEN TRUE ELSE t1.project_amount <= #{map.endProjectAmount} END \n" +
            "AND CASE WHEN #{map.isSettle} IS NULL OR #{map.isSettle} = '全部' THEN TRUE ELSE \n" +
            "CASE WHEN #{map.isSettle} = '是' THEN t1.outstanding_amount = 0 OR t2.my_STATUS = '关闭' ELSE \n" +
            "CASE WHEN #{map.isSettle} = '否' THEN t1.outstanding_amount != 0 AND t2.my_STATUS != '关闭' ELSE FALSE END \n" +
            "END \n" +
            "END \n" +
            "AND CASE WHEN #{map.keyWord} IS NULL OR #{map.keyWord} = '' THEN TRUE ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '房间信息' THEN t2.room_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '置业顾问' THEN t2.employee_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '客户姓名' THEN t2.customer_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '报备人' THEN t2.gain_by LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '银行卡号' THEN t2.bank_num LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '身份证号' THEN t2.reportIdCard LIKE CONCAT('%',#{map.keyWord},'%') ELSE FALSE \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END ORDER BY ${map.orderBy}")
    List<Map> getChecklistDetailListPage(Page<Map> page, @Param("map") Map<String, String> map);

    /**
     * 获取 核算单明细id
     *
     * @param map map
     * @return return
     */
    @Select("SELECT t1.id FROM `cm_checklist_detail` AS t1 INNER JOIN `v_commission` AS t2 \n" +
            "ON t1.commission_id = t2.id AND t1.isdel = '0' AND t2.is_del = '0' " +
            "INNER JOIN `cm_checklist` AS t3 ON t1.checklist_id = t3.id AND t3.isdel = '0' " +
            "WHERE (t1.fid = #{map.checklistId} OR t1.checklist_id = #{map.checklistId}) \n" +
            "AND CASE WHEN #{map.startKeyWord} IS NULL OR #{map.startKeyWord} = '' THEN TRUE ELSE \n" +
            "CASE WHEN #{map.keyWordType2} = '回款比例' THEN t2.collection_proportion >= #{map.startKeyWord} ELSE \n" +
            "CASE WHEN #{map.keyWordType2} = '佣金点位' THEN t1.commission_point >= #{map.startKeyWord} ELSE FALSE END \n" +
            "END \n" +
            "END \n" +
            "AND CASE WHEN #{map.endKeyWord} IS NULL OR #{map.endKeyWord} = '' THEN TRUE ELSE \n" +
            "CASE WHEN #{map.keyWordType2} = '回款比例' THEN t2.collection_proportion <= #{map.endKeyWord} ELSE \n" +
            "CASE WHEN #{map.keyWordType2} = '佣金点位' THEN t1.commission_point <= #{map.endKeyWord} ELSE FALSE END \n" +
            "END \n" +
            "END \n" +
            "AND CASE WHEN #{map.startSubscribeTime} IS NULL THEN TRUE ELSE t2.subscription_date >= #{map.startSubscribeTime} END \n" +
            "AND CASE WHEN #{map.endSubscribeTime} IS NULL THEN TRUE ELSE t2.subscription_date <= DATE_ADD(#{map.endSubscribeTime},INTERVAL 1 DAY) END \n" +
            "AND CASE WHEN #{map.startSignTime} IS NULL THEN TRUE ELSE t2.signing_date >= #{map.startSignTime} END \n" +
            "AND CASE WHEN #{map.endSignTime} IS NULL THEN TRUE ELSE t2.signing_date <= DATE_ADD(#{map.endSignTime},INTERVAL 1 DAY) END \n" +
            "AND CASE WHEN #{map.startProjectAmount} IS NULL THEN TRUE ELSE t1.project_amount >= #{map.startProjectAmount} END\n" +
            "AND CASE WHEN #{map.endProjectAmount} IS NULL THEN TRUE ELSE t1.project_amount <= #{map.endProjectAmount} END \n" +
            "AND CASE WHEN #{map.isSettle} IS NULL OR #{map.isSettle} = '全部' THEN TRUE ELSE \n" +
            "CASE WHEN #{map.isSettle} = '是' THEN t1.outstanding_amount = 0 OR t2.my_STATUS = '关闭' ELSE \n" +
            "CASE WHEN #{map.isSettle} = '否' THEN t1.outstanding_amount != 0 AND t2.my_STATUS != '关闭' ELSE FALSE END \n" +
            "END \n" +
            "END \n" +
            "AND CASE WHEN #{map.keyWord} IS NULL OR #{map.keyWord} = '' THEN TRUE ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '房间信息' THEN t2.room_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '置业顾问' THEN t2.employee_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '客户姓名' THEN t2.customer_name LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '报备人' THEN t2.gain_by LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '银行卡号' THEN t2.bank_num LIKE CONCAT('%',#{map.keyWord},'%') ELSE \n" +
            "CASE WHEN #{map.keyWordType} = '身份证号' THEN t2.reportIdCard LIKE CONCAT('%',#{map.keyWord},'%') ELSE FALSE \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END \n" +
            "END ORDER BY ${map.orderBy}")
    List<String> getChecklistDetailIds(@Param("map") Map<String, String> map);

    /**
     * 分页查询，佣金核算单-政策
     *
     * @param page page
     * @param map  map
     * @return return
     */
    @Select("SELECT * FROM `cm_policy` WHERE isdel = 0 \n" +
            "AND id IN(SELECT DISTINCT policy_id FROM `cm_checklist_policy` WHERE isdel = 0 AND checklist_id = #{map.checklistId})")
    List<Map> getChecklistPolicyListPage(Page<Map> page, @Param("map") Map<String, String> map);

    /**
     * 根据核算单id，删除核算单
     *
     * @param map map
     * @return return
     */
    @Update("<script>" +
            "UPDATE `cm_checklist` SET isdel = 1,editor = #{map.userId},edit_time = #{map.date} \n" +
            "WHERE isdel = 0 AND id IN" +
            "<foreach collection='map.ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    Integer removeChecklistByIds(@Param("map") Map<String, Object> map);

    /**
     * 根据核算单id，删除核算单明细
     *
     * @param map map
     * @return return
     */
    @Update("<script>" +
            "UPDATE `cm_checklist_detail` SET isdel = 1,editor = #{map.userId},edit_time = #{map.date} \n" +
            "WHERE isdel = 0 AND checklist_id IN" +
            "<foreach collection='map.ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    Integer removeChecklistDetailByChecklistIds(@Param("map") Map<String, Object> map);

    /**
     * 根据核算单id，删除核算单明细和负核算单明细
     *
     * @param map map
     * @return return
     */
    @Update("<script>" +
            "UPDATE `cm_checklist_detail` SET isdel = 1,editor = #{map.userId},edit_time = #{map.date} \n" +
            "WHERE isdel = 0 AND fid IN" +
            "<foreach collection='map.ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    Integer removeChecklistDetailByChecklistIds2(@Param("map") Map<String, Object> map);

    /**
     * 根据核算单ids，获取 待结佣ids
     *
     * @param map map
     * @return return
     */
    @Select("<script>" +
            "SELECT commission_id FROM `cm_checklist_detail` WHERE isdel = 0 AND checklist_id IN" +
            "<foreach collection='map.ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<String> getCommissionIdsByChecklistIds(@Param("map") Map<String, Object> map);

    /**
     * 根据核算单ids，获取 结佣明细 交易id
     *
     * @param map map
     * @return return
     */
    @Select("<script>" +
            "SELECT transaction_id FROM `cm_checklist_detail` WHERE isdel = 0 AND checklist_detail_type = 1 AND checklist_id IN" +
            "<foreach collection='map.ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<String> getTransactionIdsByChecklistIds(@Param("map") Map<String, Object> map);

    /**
     * 修改 核算单 佣金立项金额
     *
     * @param id id
     * @return return
     */
    @Update("UPDATE `cm_checklist` SET edit_time = NOW(),project_amount = \n" +
            "(SELECT SUM(project_amount) FROM `cm_checklist_detail` WHERE isdel = 0 AND fid = #{id}) \n" +
            "WHERE isdel = 0 AND id = #{id}")
    Integer updateProjectAmount(@Param("id") String id);

    /**
     * 批量修改 核算单 佣金立项金额
     *
     * @param checklistDetailIds checklistDetailIds
     * @return return
     */
    @Update("<script>" +
            "UPDATE `cm_checklist` AS t1 SET edit_time = NOW(),project_amount = \n" +
            "(SELECT SUM(project_amount) FROM `cm_checklist_detail` WHERE isdel = 0 AND fid = t1.id) \n" +
            "WHERE isdel = 0 AND id IN " +
            "(SELECT DISTINCT checklist_id FROM `cm_checklist_detail` WHERE isdel = 0 AND checklist_detail_type = 1 AND id IN" +
            "<foreach collection='checklistDetailIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach> )" +
            "</script>")
    Integer updateProjectAmountByChecklistDetailIds(@Param("checklistDetailIds") List<Object> checklistDetailIds);

    /**
     * 根据ids，获取核算单明细
     *
     * @param ids ids
     * @return return
     */
    @Select("<script>" +
            "SELECT t1.*,t2.business_unit_name,t2.project_name,t2.room_name,t2.transaction_status,t2.now_price, \n" +
            "t2.back_price,t2.collection_proportion,t2.source_type_desc,t2.gain_by,t2.current_role,t2.employee_name, \n" +
            "t2.customer_name,t2.subscription_date,t2.signing_date,t2.bank_num,t2.bank_name,t2.reportIdCard, \n" +
            "t2.customer_mobile,t2.report_mobile,t2.transactionStatus,FORMAT(t2.built_up_area,2) as built_up_area," +
            "t3.checklist_name,t3.checklist_code,t3.project_code,t3.project_status,t3.creator_name,t3.project_name,t3.commission_type \n" +
            "FROM `cm_checklist_detail` AS t1 INNER JOIN `v_commission` AS t2 \n" +
            "ON t1.commission_id = t2.id AND t1.isdel = 0 AND t2.is_del = 0 INNER JOIN `cm_checklist` AS t3 \n" +
            "ON t1.checklist_id = t3.id AND t3.isdel = 0 AND t1.id IN \n" +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach> ORDER BY id" +
            "</script>")
    List<Map> getChecklistDetailListByIds(@Param("ids") List<String> ids);

    /**
     * 根据ids，获取未关联负核算单的明细
     *
     * @param ids ids
     * @return return
     */
    @Select("<script>" +
            "SELECT t1.*,t2.business_unit_name,t2.project_name,t2.room_name,t2.transaction_status,t2.now_price, \n" +
            "t2.back_price,t2.collection_proportion,t2.source_type_desc,t2.gain_by,t2.current_role,t2.employee_name, \n" +
            "t2.customer_name,t2.subscription_date,t2.signing_date,t2.bank_num,t2.bank_name,t2.reportIdCard, \n" +
            "t2.customer_mobile,t2.report_mobile,t2.transactionStatus,t2.built_up_area," +
            "t3.checklist_code,t3.project_code,t3.project_status,t3.creator_name,t3.project_name \n" +
            "FROM `cm_checklist_detail` AS t1 INNER JOIN `v_commission` AS t2 \n" +
            "ON t1.commission_id = t2.id AND t1.isdel = 0 AND t1.is_negative = 0 AND t2.is_del = 0 INNER JOIN `cm_checklist` AS t3 \n" +
            "ON t1.checklist_id = t3.id AND t3.isdel = 0 AND t1.id IN \n" +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach> ORDER BY id" +
            "</script>")
    List<Map> getNotNegativeChecklistDetailListByIds(@Param("ids") List<String> ids);

    /**
     * 根据核算单明细ids，获取 待结佣ids
     *
     * @param map map
     * @return return
     */
    @Select("<script>" +
            "SELECT commission_id FROM `cm_checklist_detail` WHERE isdel = 0 AND id IN" +
            "<foreach collection='map.ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<String> getCommissionIdsByChecklistDetailIds(@Param("map") Map<String, Object> map);

    /**
     * 查询负核算单信息
     *
     * @param checklistId checklistId
     * @return return
     */
    @Select("SELECT t1.id,t2.room_name,t1.project_amount,t1.amount_closed,t1.outstanding_amount FROM `cm_checklist_detail` AS t1 \n" +
            "INNER JOIN `cm_commission` AS t2 ON t1.commission_id = t2.id AND t1.isdel = 0 AND t2.is_del = 0 \n" +
            "AND t2.my_STATUS = '关闭' AND t1.outstanding_amount > 0 AND t1.is_negative = 0 AND t1.checklist_id = #{checklistId}")
    List<Map> getNegativeChecklist(@Param("checklistId") String checklistId);

    /**
     * 根据id，删除核算单明细
     *
     * @param map map
     * @return return
     */
    @Update("<script>" +
            "UPDATE `cm_checklist_detail` SET isdel = 1,editor = #{map.userId},edit_time = #{map.date} \n" +
            "WHERE isdel = 0 AND id IN" +
            "<foreach collection='map.ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    Integer removeChecklistDetailByDetailId(@Param("map") Map<String, Object> map);

    /**
     * 核算单审批
     *
     * @param map map
     * @return return
     */
    @Update("UPDATE `cm_checklist` SET project_status = 2,auditor = #{map.username},edit_time = NOW() WHERE isdel = 0 AND id = #{map.checklistId}")
    Integer checklistApprove(@Param("map") Map<String, Object> map);

    /**
     * 审批回调，修改核算单（已立项）
     *
     * @param map map
     * @return return
     */
    @Update("UPDATE `cm_checklist` SET project_status = #{map.projectStatus},project_code = #{map.projectCode},project_time = #{map.projectTime} " +
            ",edit_time = NOW() " +
            "WHERE isdel = 0 AND id = #{map.checklistId}")
    Integer updateChecklistProject(@Param("map") Map<String, Object> map);

    /**
     * 审批回调，修改核算单（非已立项）
     *
     * @param map map
     * @return return
     */
    @Update("<script>" +
            "UPDATE `cm_checklist` SET project_status = #{map.projectStatus},edit_time = NOW() \n" +
            "WHERE isdel = 0 AND id IN" +
            "<foreach collection='map.ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    Integer updateChecklistProject2(@Param("map") Map<String, Object> map);

    /**
     * 审批回调，修改核算单（非已立项）
     *
     * @param map map
     * @return return
     */
    @Update("<script>" +
            "UPDATE `cm_checklist` SET project_status = #{map.projectStatus},edit_time = NOW(),project_code = '',project_time = null \n" +
            "WHERE isdel = 0 AND id IN" +
            "<foreach collection='map.ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    Integer updateChecklistProject3(@Param("map") Map<String, Object> map);


    /**
     * 审批回调，修改核算单明细（已立项）
     *
     * @param map map
     * @return return
     */
    @Update("UPDATE `cm_checklist_detail` SET project_code = #{map.projectCode},project_time = #{map.projectTime},edit_time = NOW() " +
            "WHERE isdel = 0 AND checklist_id = #{map.checklistId}")
    Integer updateChecklistDetailProject(@Param("map") Map<String, Object> map);

    /**
     * 根据核算单ids，获取明细的ids
     *
     * @param checklistIds checklistIds
     * @return return
     */
    @Select("<script>" +
            "SELECT id FROM `cm_checklist_detail` WHERE isdel = 0 AND (checklist_id IN" +
            "<foreach collection='checklistIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "OR fid IN" +
            "<foreach collection='checklistIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            ") ORDER BY fid" +
            "</script>")
    List<String> getChecklistDetailIdsByIds(@Param("checklistIds") List<String> checklistIds);

    /**
     * 移除 核算单-政策
     *
     * @param map map
     * @return return
     */
    @Update("<script>" +
            "UPDATE `cm_checklist_policy` SET isdel = 1,editor = #{map.userId},edit_time = #{map.date} \n" +
            "WHERE isdel = 0 AND checklist_id = #{map.checklistId} AND policy_id IN" +
            "<foreach collection='map.ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    Integer removeChecklistPolicyByPolicyId(@Param("map") Map<String, Object> map);

    /**
     * 修改核算单明细 已关联负核算单
     *
     * @param checklistDetailIds checklistDetailIds
     * @return return
     */
    @Update("<script>" +
            "UPDATE `cm_checklist_detail` SET is_negative = 1,edit_time = NOW() WHERE isdel = 0 AND is_negative = 0 AND id IN" +
            "<foreach collection='checklistDetailIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    Integer updateIsNegativeByIds(@Param("checklistDetailIds") List<String> checklistDetailIds);

    /**
     * 修改 正核算单明细（未关联负核算单）
     *
     * @param checklist checklist
     * @return return
     */
    @Update("UPDATE `cm_checklist_detail` SET is_negative = 0,edit_time = NOW() WHERE isdel = 0 AND checklist_id = #{checklist.fid} AND commission_id \n" +
            "IN(SELECT * FROM (SELECT commission_id FROM `cm_checklist_detail` WHERE isdel = 0 AND checklist_id = #{checklist.id}) AS t1)")
    Integer updateIsNegativeToZero(@Param("checklist") Checklist checklist);

    /**
     * 修改 正核算单明细（负核算单立项通过）
     *
     * @param checklist checklist
     * @return return
     */
    @Update("UPDATE `cm_checklist_detail` SET is_negative = 2,edit_time = NOW() WHERE isdel = 0 AND checklist_id = #{checklist.fid} AND commission_id \n" +
            "IN(SELECT * FROM (SELECT commission_id FROM `cm_checklist_detail` WHERE isdel = 0 AND checklist_id = #{checklist.id}) AS t1)")
    Integer updateIsNegativeToTwo(@Param("checklist") Checklist checklist);

    /**
     * 修改 正核算单明细（未关联负核算单）
     *
     * @param checklistDetail checklistDetail
     * @return return
     */
    @Update("UPDATE `cm_checklist_detail` SET is_negative = 0,edit_time = NOW() WHERE isdel = 0 AND checklist_id = #{checklistDetail.fid} AND commission_id = #{checklistDetail.commissionId}")
    Integer updateIsNegativeToZeroByChecklistDetail(@Param("checklistDetail") ChecklistDetail checklistDetail);

    /**
     * 获取 欠款明细的id
     *
     * @param checklistId checklistId
     * @return return
     */
    @Select("SELECT id FROM `cm_checklist_detail` WHERE isdel = 0 AND checklist_detail_type = 3 AND checklist_id = #{checklistId}")
    List<String> getArrearsChecklistDetailIds(@Param("checklistId") String checklistId);

    /**
     * 导入核算单明细的修改
     *
     * @param map map
     * @return return
     */
    @Update("UPDATE `cm_checklist_detail` AS t1,`cm_checklist` AS t2 SET t1.edit_time = NOW(),t1.project_amount = #{map.project_amount}," +
            "t1.commission_point = #{map.commission_point},t1.outstanding_amount = #{map.project_amount},t1.unpaid_amount = #{map.project_amount} \n" +
            "WHERE t1.checklist_id = t2.id AND t1.isdel = 0 AND t2.isdel = 0 \n" +
            "AND checklist_detail_type = 1 AND t1.id = #{map.id} AND (t2.project_status = 1 OR t2.project_status = 5)\n")
    Integer importChecklistDetail(@Param("map") Map map);

    /**
     * 根据 业务归属人，获取 立项金额 总值
     *
     * @param checklistId checklistId
     * @return return
     */
    @Select("SELECT business_attribution_code,SUM(project_amount) AS project_amount FROM `cm_checklist_detail` \n" +
            "WHERE isdel = 0 AND checklist_id = #{checklistId} GROUP BY business_attribution_code")
    List<Map> getProjectAmountByBusinessAttributionCode(@Param("checklistId") String checklistId);

    /**
     * 获取渠道名称
     *
     * @param map map
     * @return return
     */
    @Select("SELECT gain_num,gain_by FROM `cm_commission` WHERE is_del = 0 AND gain_num IS NOT NULL AND gain_num <> '' \n" +
            "AND gain_by IS NOT NULL AND gain_by <> '' AND id \n" +
            "IN(SELECT commission_id FROM `cm_checklist_detail` WHERE isdel = 0 AND checklist_id \n" +
            "IN(SELECT id FROM `cm_checklist` WHERE isdel = 0 AND deal_type = '中介成交' " +
            "AND CASE WHEN #{map.orgId} IS NULL THEN TRUE ELSE org_id IN " +
            "(SELECT DISTINCT orgId FROM `s_jobsorgrel` WHERE " +
            "CASE WHEN #{map.orgLevel} = 1 THEN groupOrgId = #{map.orgId} " +
            "WHEN #{map.orgLevel} = 2 THEN areaOrgId = #{map.orgId} " +
            "WHEN #{map.orgLevel} = 3 THEN cityOrgId = #{map.orgId} " +
            "WHEN #{map.orgLevel} = 4 THEN projectOrgId = #{map.orgId} ELSE FALSE END) END \n" +
            ")) \n" +
            "GROUP BY gain_num,gain_by")
    List<Map> getChannelName(@Param("map") Map<String, Object> map);

    /**
     * 核算单撤回
     *
     * @param map map
     * @return return
     */
    @Update("UPDATE `cm_checklist` SET project_status = 1,edit_time = NOW() WHERE isdel = 0 AND project_status = 2 AND id = #{map.checklistId}")
    Integer checklistWithdraw(@Param("map") Map<String, Object> map);

    /**
     * 校验 结佣明细 是否录入 佣金金额或佣金点位
     *
     * @param checklistId checklistId
     * @return return
     */
    @Select("SELECT COUNT(*) AS count FROM `cm_checklist_detail` WHERE isdel = 0 " +
            "AND (project_amount IS NULL OR project_amount = 0 OR commission_point IS NULL OR commission_point = 0) " +
            "AND checklist_detail_type = 1 AND checklist_id = #{checklistId}")
    Long isEntryProjectAmountAndCommissionPoint(@Param("checklistId") String checklistId);

    /**
     * 校验核算单 是否关联 政策
     *
     * @param checklistId checklistId
     * @return return
     */
    @Select("SELECT COUNT(*) AS count FROM `cm_checklist_policy` WHERE isdel = 0 AND checklist_id = #{checklistId}")
    Long isRelatedPolicy(@Param("checklistId") String checklistId);

    /**
     * 核算单欠款校验
     *
     * @param map map
     * @return return
     */
    @Select("SELECT DISTINCT gain_by FROM `cm_commission` " +
            "WHERE is_del = 0 AND is_bill = 1 AND transaction_status = '付款后退房' AND gain_num \n" +
            "IN(SELECT DISTINCT business_attribution_code FROM `cm_checklist_detail` " +
            "WHERE isdel = 0 AND checklist_id = #{map.checklistId})")
    List<String> checklistArrearsCheck(@Param("map") Map<String, Object> map);

    /**
     * 根据 核算单id，获取 负核算单id
     *
     * @param ids ids
     * @return return
     */
    @Select("<script>" +
            "SELECT id FROM `cm_checklist` WHERE isdel = 0 AND is_father = 0 AND fid IN" +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<String> getNegativeChecklistIdsByIds(@Param("ids") List<String> ids);

    /**
     * 修改 核算单的异常状态
     *
     * @param fid fid
     * @return return
     */
    @Update("UPDATE `cm_checklist` SET is_abnormal = 0,edit_time = NOW() WHERE isdel = 0 AND id = #{fid} AND \n" +
            "(SELECT COUNT(*) FROM `cm_checklist_detail` AS t1 INNER JOIN `cm_commission` AS t2 ON \n" +
            "t1.commission_id = t2.id AND t1.isdel = 0 AND t2.is_del = 0 \n" +
            "AND t1.checklist_id = #{fid} AND t2.my_STATUS = '关闭' AND t1.is_negative != 2 AND t1.outstanding_amount > 0) = 0")
    Integer updateChecklistIsAbnormal(@Param("fid") String fid);

    /**
     * 修改 核算单的异常状态
     *
     * @param fid fid
     * @return return
     */
    @Update("UPDATE `cm_checklist` SET is_abnormal = 0 WHERE isdel = 0 AND id = #{fid}" )
    Integer updateChecklistIsAbnormalForce(@Param("fid") String fid);

    /**
     * 修改核算单名称
     *
     * @param map map
     * @return return
     */
    @Update("UPDATE `cm_checklist` SET checklist_name = #{map.checklistName},edit_time = NOW() WHERE isdel = 0 AND id = #{map.checklistId}")
    Integer updateChecklistName(@Param("map") Map<String, String> map);

    /**
     * 修改结佣形式
     *
     * @param map map
     * @return return
     */
    @Update("UPDATE `cm_checklist` SET commission_type = #{map.commissionType},edit_time = NOW() WHERE isdel = 0 AND id = #{map.checklistId}")
    Integer updateCommissionType(@Param("map") Map<String, String> map);

    /**
     * 判断名称是否重复
     *
     * @param map map
     * @return return
     */
    @Select("SELECT id FROM `cm_checklist` WHERE isdel = 0 AND id != #{map.checklistId} AND checklist_name = #{map.checklistName} LIMIT 1")
    Map duplicateNameCheck(@Param("map") Map map);
}
