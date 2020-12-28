package cn.visolink.salesmanage.checklist.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.checklist.entity.Checklist;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 佣金核算单 服务类
 * </p>
 *
 * @author yangjie
 * @since 2020-05-14
 */
public interface ChecklistService extends IService<Checklist> {
    /**
     * 新增佣金核算单
     *
     * @param checklist checklist
     * @return return
     */
    ResultBody insertChecklist(Checklist checklist);

    /**
     * 根据id，修改佣金核算单
     *
     * @param checklist checklist
     * @return return
     */
    ResultBody updateChecklistById(Checklist checklist);

    /**
     * 删除佣金核算单
     *
     * @param map     map
     * @param request request
     * @return return
     */
    ResultBody deleteChecklist(Map<String, Object> map, HttpServletRequest request);

    /**
     * 根据id，查询佣金核算单详情
     *
     * @param id id
     * @return return
     */
    ResultBody getChecklistById(String id);

    /**
     * 创建正核算单
     *
     * @param map     map
     * @param request request
     * @return return
     */
    ResultBody createChecklist(Map<String, String> map, HttpServletRequest request);

    /**
     * 分页查询，佣金核算单
     *
     * @param map map
     * @return return
     */
    ResultBody getChecklistListPage(Map<String, String> map);

    /**
     * 分页查询，佣金核算单明细
     *
     * @param map map
     * @return return
     */
    ResultBody getChecklistDetailListPage(Map<String, String> map);

    /**
     * 分页查询，佣金核算单-政策
     *
     * @param map map
     * @return return
     */
    ResultBody getChecklistPolicyListPage(Map<String, String> map);

    /**
     * 关联核算单
     *
     * @param map     map
     * @param request request
     * @return return
     */
    ResultBody relatedChecklist(Map<String, String> map, HttpServletRequest request);

    /**
     * 移除核算单明细
     *
     * @param map     map
     * @param request request
     * @return return
     */
    ResultBody removeChecklistDetail(Map<String, Object> map, HttpServletRequest request);

    /**
     * 修改佣金立项金额
     *
     * @param map     map
     * @param request request
     * @return return
     */
    ResultBody updateProjectAmount(Map<String, String> map, HttpServletRequest request);

    /**
     * 批量修改佣金立项金额
     *
     * @param list    list
     * @param request request
     * @return return
     */
    ResultBody updateBatchProjectAmount(List<Map<String, String>> list, HttpServletRequest request);

    /**
     * 导出核算单
     *
     * @param ids      ids
     * @param request  request
     * @param response response
     */
    void exportChecklist(String ids, HttpServletRequest request, HttpServletResponse response);

    /**
     * 导出核算单明细
     *
     * @param ids      ids
     * @param request  request
     * @param response response
     */
    void exportChecklistDetail(String ids, HttpServletRequest request, HttpServletResponse response);

    /**
     * 导出全部
     *
     * @param map      map
     * @param request  request
     * @param response response
     */
    void exportAll(Map<String, String> map, HttpServletRequest request, HttpServletResponse response);

    /**
     * 导出全部明细
     *
     * @param map      map
     * @param request  request
     * @param response response
     */
    void exportAllDetail(Map<String, String> map, HttpServletRequest request, HttpServletResponse response);

    /**
     * 核算单审批
     *
     * @param map     map
     * @param request request
     * @return return
     */
    ResultBody checklistApprove(Map<String, Object> map, HttpServletRequest request);

    /**
     * 核算单审批回调接口
     *
     * @param map map
     * @return return
     */
    Map approvalCallbackInterface(Map<String, Object> map);

    /**
     * 关联政策
     *
     * @param map     map
     * @param request request
     * @return return
     */
    ResultBody relatedPolicy(Map<String, String> map, HttpServletRequest request);

    /**
     * 移除政策
     *
     * @param map     map
     * @param request request
     * @return return
     */
    ResultBody removePolicy(Map<String, Object> map, HttpServletRequest request);

    /**
     * 查询负核算单信息
     *
     * @param checklistId checklistId
     * @return return
     */
    ResultBody getNegativeChecklist(String checklistId);

    /**
     * 创建负核算单
     *
     * @param map     map
     * @param request request
     * @return return
     */
    ResultBody createNegativeChecklist(Map<String, Object> map, HttpServletRequest request);

    /**
     * 导入核算单明细
     *
     * @param file            file
     * @param dealType        dealType
     * @param calculationType calculationType
     * @return return
     */
    ResultBody importChecklistDetail(MultipartFile file, String dealType, String calculationType);

    /**
     * 获取渠道名称
     *
     * @param map map
     * @return return
     */
    ResultBody getChannelName(Map<String, Object> map);

    /**
     * 核算单撤回
     *
     * @param map map
     * @return return
     */
    ResultBody checklistWithdraw(Map<String, Object> map);

    /**
     * 核算单欠款校验
     *
     * @param map map
     * @return return
     */
    ResultBody checklistArrearsCheck(Map<String, Object> map);

    /**
     * 修改核算单名称
     *
     * @param map map
     * @return return
     */
    ResultBody updateChecklistName(Map<String, String> map);

    /**
     * 测试
     *
     * @param map map
     * @return return
     */
    ResultBody test(Map<String, Object> map);

    /**
     * 修改结佣形式
     *
     * @param map map
     * @return return
     */
    ResultBody updateCommissionType(Map<String, String> map);
}
