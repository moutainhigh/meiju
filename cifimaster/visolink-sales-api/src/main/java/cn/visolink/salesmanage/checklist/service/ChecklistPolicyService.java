package cn.visolink.salesmanage.checklist.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.checklist.entity.ChecklistPolicy;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 佣金核算单-政策 服务类
 * </p>
 *
 * @author yangjie
 * @since 2020-05-14
 */
public interface ChecklistPolicyService extends IService<ChecklistPolicy> {
    /**
     * 新增佣金核算单-政策
     *
     * @param checklistPolicy checklistPolicy
     * @return return
     */
    ResultBody insertChecklistPolicy(ChecklistPolicy checklistPolicy);

    /**
     * 根据id，修改佣金核算单-政策
     *
     * @param checklistPolicy checklistPolicy
     * @return return
     */
    ResultBody updateChecklistPolicyById(ChecklistPolicy checklistPolicy);

    /**
     * 删除佣金核算单-政策
     *
     * @param ids ids
     * @return return
     */
    ResultBody deleteChecklistPolicy(String ids);

    /**
     * 根据id，查询佣金核算单-政策详情
     *
     * @param id id
     * @return return
     */
    ResultBody getChecklistPolicyById(String id);

    /**
     * 分页查询，佣金核算单-政策
     *
     * @param map map
     * @return return
     */
    ResultBody getChecklistPolicyListPage(Map<String,String> map);
}
