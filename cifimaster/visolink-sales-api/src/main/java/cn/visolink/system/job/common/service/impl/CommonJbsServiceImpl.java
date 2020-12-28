package cn.visolink.system.job.common.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.exception.BaseResultCodeEnum;
import cn.visolink.system.job.common.dao.CommonJobsDao;
import cn.visolink.system.job.common.model.CommonJobs;
import cn.visolink.system.job.common.model.form.CommonJobsForm;
import cn.visolink.system.job.common.model.vo.CommonJobsVO;
import cn.visolink.system.job.common.service.CommonJobsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.cess.CessException;
import io.cess.util.PropertyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;


/**
 * <p>
 * CommonJobs服务实现类
 * </p>
 *
 * @author autoJob
 * @since 2019-08-29
 */
@Service
public class CommonJbsServiceImpl extends ServiceImpl<CommonJobsDao, CommonJobs> implements CommonJobsService {

    @Override
    public Integer save(CommonJobsForm record) {
        CommonJobs data = this.convertDO(record);
        data.setCreateTime(new Date());
        return baseMapper.insert(data);
    }

    @Override
    public Integer updateById(CommonJobsForm record) {
        CommonJobs data = this.convertDO(record);
        data.setEditTime(new Date());
        return baseMapper.updateById(data);
    }

    @Override
    public Integer deleteById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        return baseMapper.deleteById(id);
    }

    @Override
    public CommonJobsVO selectById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        CommonJobs data = baseMapper.selectById(id);
        CommonJobsVO result = PropertyUtil.copy(data, CommonJobsVO.class);
        return result;
    }

    @Override
    public List<CommonJobsVO> selectAll(CommonJobsForm record) {
        QueryWrapper<CommonJobs> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(PropertyUtil.copy(record, CommonJobs.class));
        List<CommonJobs> list = baseMapper.selectList(queryWrapper);
        return this.convert(list);
    }

    @Override
    public IPage<CommonJobsVO> selectPage(CommonJobsForm record) {
        // form -> do 转换
        CommonJobs data = PropertyUtil.copy(record, CommonJobs.class);
        // 分页数据设置
        Page<CommonJobs> page = new Page<>(record.getCurrent(), record.getSize());
        // 查询条件
        QueryWrapper<CommonJobs> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(data);
        IPage<CommonJobs> list = baseMapper.selectPage(page, queryWrapper);
        IPage<CommonJobsVO> iPage = new Page<>();
        iPage.setRecords(PropertyUtil.copy(list.getRecords(), CommonJobsVO.class));
        iPage.setCurrent(list.getCurrent());
        iPage.setSize(list.getSize());
        iPage.setTotal(list.getTotal());
        iPage.setPages(list.getPages());
        return iPage;
    }

    @Override
    public PageInfo<CommonJobsVO> commonJobsSelectAll(CommonJobsForm commonjobsForm) {
        PageHelper.startPage(commonjobsForm.getCurrent(),commonjobsForm.getSize());
        List<CommonJobsVO> list = baseMapper.findListBySystemCommonJobs(commonjobsForm);
        return new PageInfo<CommonJobsVO>(list);
    }


    @Override
    public PageInfo getJobSByCommonJob(String commonJobId,Integer pageSize,Integer pageNum) {
         PageHelper.startPage(pageNum,pageSize);
         List<Map> list=baseMapper.getJobSByCommonJob(commonJobId);
        return new PageInfo(list);
    }

    /*
     * 四层级数据授权
     * */
    @Override
    public List<Map> getFourOrgData() {
        return baseMapper.getFourOrgData();
    }


    @Override
    public Map findSystemCommonJobAuth(Map map) {
        HashMap<Object, Object> resultMap = MapUtil.newHashMap();
        List<Map> menusListByPermissions = baseMapper.findMenusListByPermissions(map);
        List<Map> functionsListByPermissions = baseMapper.findFunctionsListByPermissions(map);
        List<Map> commonJobFunctionsByPermissions = baseMapper.findCommonJobFunctionsByPermissions(map);
        resultMap.put("登录人有权限的菜单",menusListByPermissions);
        resultMap.put("查找已授权的功能列表",functionsListByPermissions);
        resultMap.put("查找该岗位已有的菜单和功能",commonJobFunctionsByPermissions);
        return resultMap;
    }

    @Override
    public int systemCommonJob_Insert(CommonJobsForm commonJobsForm) {
        return baseMapper.systemCommonJob_Insert(commonJobsForm);
    }

    @Override
    public String getJobCodeMax() {
        return baseMapper.getJobCodeMax();
    }


    /**
     * Form -> Do
     *
     * @param form 对象
     * @return Do对象
     */
    private CommonJobs convertDO(CommonJobsForm form) {
        CommonJobs data = new CommonJobs();
        data.setId(form.getId());
        data.setJobCode(form.getJobCode());
        data.setJobName(form.getJobName());
        data.setJobDesc(form.getJobDesc());
        data.setAuthCompanyId(form.getAuthCompanyId());
        data.setProductId(form.getProductId());
        data.setCreator(form.getCreator());
        data.setCreateTime(DateUtil.parseTime(form.getCreateTime()));
        data.setEditor(form.getEditor());
        data.setEditTime(DateUtil.parseTime(form.getEditTime()));
        /*data.setStatus(form.getStatus());
        data.setIsDel(form.getIsDel());*/
        return data;
    }

    /**
     * Do -> VO
     *
     * @param list 对象
     * @return VO对象
     */
    private List<CommonJobsVO> convert(List<CommonJobs> list) {
        List<CommonJobsVO> commonjobsList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return commonjobsList;
        }
        for (CommonJobs source : list) {
            CommonJobsVO target = new CommonJobsVO();
            BeanUtils.copyProperties(source, target);
            commonjobsList.add(target);
        }
        return commonjobsList;
    }



}
