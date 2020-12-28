package cn.visolink.salesmanage.checklist.service.impl;

import cn.visolink.exception.ResultBody;
import cn.visolink.exception.ResultUtil;
import cn.visolink.salesmanage.checklist.entity.ChecklistDetail;
import cn.visolink.salesmanage.checklist.mapper.ChecklistDetailMapper;
import cn.visolink.salesmanage.checklist.service.ChecklistDetailService;
import cn.visolink.utils.PagingParamUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 佣金核算单明细 服务实现类
 * </p>
 *
 * @author yangjie
 * @since 2020-05-14
 */
@Service
public class ChecklistDetailServiceImpl extends ServiceImpl<ChecklistDetailMapper, ChecklistDetail> implements ChecklistDetailService {

    private final ChecklistDetailMapper ChecklistDetailMapper;

    @Autowired
    public ChecklistDetailServiceImpl(ChecklistDetailMapper ChecklistDetailMapper) {
        this.ChecklistDetailMapper = ChecklistDetailMapper;
    }

    /**
     * 新增佣金核算单明细
     *
     * @param ChecklistDetail ChecklistDetail
     * @return return
     */
    @Override
    public ResultBody insertChecklistDetail(ChecklistDetail ChecklistDetail) {
        boolean insert = this.save(ChecklistDetail);
        if (!insert) {
            return ResultUtil.error(500, "新增失败");
        }
        return ResultUtil.success(ChecklistDetail.getId());
    }

    /**
     * 根据id，修改佣金核算单明细
     *
     * @param ChecklistDetail ChecklistDetail
     * @return return
     */
    @Override
    public ResultBody updateChecklistDetailById(ChecklistDetail ChecklistDetail) {
        boolean update = this.updateById(ChecklistDetail);
        if (!update) {
            return ResultUtil.error(500, "修改失败");
        }
        return ResultUtil.success("");
    }

    /**
     * 删除佣金核算单明细
     *
     * @param ids ids
     * @return return
     */
    @Override
    public ResultBody deleteChecklistDetail(String ids) {
        boolean delete = this.removeByIds(Arrays.asList(ids.split(",")));
        if (!delete) {
            return ResultUtil.error(500, "删除失败");
        }
        return ResultUtil.success("");
    }

    /**
     * 根据id，查询佣金核算单明细详情
     *
     * @param id id
     * @return return
     */
    @Override
    public ResultBody getChecklistDetailById(String id) {
        ChecklistDetail ChecklistDetail = this.getById(id);
        return ResultUtil.success(ChecklistDetail);
    }

    /**
     * 分页查询，佣金核算单明细
     *
     * @param map map
     * @return return
     */
    @Override
    public ResultBody getChecklistDetailListPage(Map<String, String> map) {
        Page<Map> page = PagingParamUtil.getPage(map);
        List<Map> list = ChecklistDetailMapper.getChecklistDetailListPage(page, map);
        page.setRecords(list);
        return ResultUtil.success(page);
    }
}
