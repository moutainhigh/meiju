package cn.visolink.salesmanage.checklist.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.checklist.entity.ChecklistDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 佣金核算单明细 服务类
 * </p>
 *
 * @author yangjie
 * @since 2020-05-14
 */
public interface ChecklistDetailService extends IService<ChecklistDetail> {
    /**
     * 新增佣金核算单明细
     *
     * @param checklistDetail checklistDetail
     * @return return
     */
    ResultBody insertChecklistDetail(ChecklistDetail checklistDetail);

    /**
     * 根据id，修改佣金核算单明细
     *
     * @param checklistDetail checklistDetail
     * @return return
     */
    ResultBody updateChecklistDetailById(ChecklistDetail checklistDetail);

    /**
     * 删除佣金核算单明细
     *
     * @param ids ids
     * @return return
     */
    ResultBody deleteChecklistDetail(String ids);

    /**
     * 根据id，查询佣金核算单明细详情
     *
     * @param id id
     * @return return
     */
    ResultBody getChecklistDetailById(String id);

    /**
     * 分页查询，佣金核算单明细
     *
     * @param map map
     * @return return
     */
    ResultBody getChecklistDetailListPage(Map<String,String> map);
}
