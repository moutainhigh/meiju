package cn.visolink.salesmanage.checklist.service.impl;

import cn.visolink.exception.ResultBody;
import cn.visolink.exception.ResultUtil;
import cn.visolink.salesmanage.checklist.entity.ChecklistPolicy;
import cn.visolink.salesmanage.checklist.mapper.ChecklistPolicyMapper;
import cn.visolink.salesmanage.checklist.service.ChecklistPolicyService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 佣金核算单-政策 服务实现类
 * </p>
 *
 * @author yangjie
 * @since 2020-05-14
 */
@Service
public class ChecklistPolicyServiceImpl extends ServiceImpl<ChecklistPolicyMapper, ChecklistPolicy> implements ChecklistPolicyService {

    private final ChecklistPolicyMapper checklistPolicyMapper;

    @Autowired
    public ChecklistPolicyServiceImpl(ChecklistPolicyMapper checklistPolicyMapper) {
        this.checklistPolicyMapper = checklistPolicyMapper;
    }

    /**
     * 新增佣金核算单-政策
     *
     * @param checklistPolicy checklistPolicy
     * @return return
     */
    @Override
    public ResultBody insertChecklistPolicy(ChecklistPolicy checklistPolicy) {
        boolean insert = this.save(checklistPolicy);
        if (!insert) {
            return ResultUtil.error(500, "新增失败");
        }
        return ResultUtil.success(checklistPolicy.getId());
    }

    /**
     * 根据id，修改佣金核算单-政策
     *
     * @param checklistPolicy checklistPolicy
     * @return return
     */
    @Override
    public ResultBody updateChecklistPolicyById(ChecklistPolicy checklistPolicy) {
        boolean update = this.updateById(checklistPolicy);
        if (!update) {
            return ResultUtil.error(500, "修改失败");
        }
        return ResultUtil.success("");
    }

    /**
     * 删除佣金核算单-政策
     *
     * @param ids ids
     * @return return
     */
    @Override
    public ResultBody deleteChecklistPolicy(String ids) {
        boolean delete = this.removeByIds(Arrays.asList(ids.split(",")));
        if (!delete) {
            return ResultUtil.error(500, "删除失败");
        }
        return ResultUtil.success("");
    }

    /**
     * 根据id，查询佣金核算单-政策详情
     *
     * @param id id
     * @return return
     */
    @Override
    public ResultBody getChecklistPolicyById(String id) {
        ChecklistPolicy checklistPolicy = this.getById(id);
        return ResultUtil.success(checklistPolicy);
    }

    /**
     * 分页查询，佣金核算单-政策
     *
     * @param map map
     * @return return
     */
    @Override
    public ResultBody getChecklistPolicyListPage(Map<String, String> map) {
        Page<Map> page = new Page<>(1, 10);
        List<Map> list = checklistPolicyMapper.getChecklistPolicyListPage(page, map);
        page.setRecords(list);
        return ResultUtil.success(page);
    }
}
