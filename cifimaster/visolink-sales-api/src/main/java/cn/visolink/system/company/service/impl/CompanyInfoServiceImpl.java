package cn.visolink.system.company.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.exception.BaseResultCodeEnum;
import cn.visolink.system.company.dao.CompanyInfoDao;
import cn.visolink.system.company.model.CompanyInfo;
import cn.visolink.system.company.model.form.CompanyInfoForm;
import cn.visolink.system.company.model.vo.CompanyInfoVO;
import cn.visolink.system.company.service.CompanyInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.cess.CessException;
import io.cess.util.PropertyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * CompanyInfo服务实现类
 * </p>
 *
 * @author autoJob
 * @since 2019-08-31
 */
@Service
public class CompanyInfoServiceImpl extends ServiceImpl<CompanyInfoDao, CompanyInfo> implements CompanyInfoService {

    @Override
    public Integer save(CompanyInfoForm record) {
        CompanyInfo data = this.convertDO(record);
        data.setCreateTime(new Date());
        return baseMapper.insert(data);
    }

    @Override
    public Integer updateById(CompanyInfoForm record) {
        CompanyInfo data = this.convertDO(record);
        data.setEdittime(new Date());
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
    public CompanyInfoVO selectById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        CompanyInfo data = baseMapper.selectById(id);
        CompanyInfoVO result = PropertyUtil.copy(data, CompanyInfoVO.class);
        return result;
    }

    @Override
    public List<CompanyInfoVO> selectAll(CompanyInfoForm record) {
        QueryWrapper<CompanyInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(PropertyUtil.copy(record, CompanyInfo.class));
        List<CompanyInfo> list = baseMapper.selectList(queryWrapper);
        return this.convert(list);
    }

    @Override
    public IPage<CompanyInfo> selectPage(CompanyInfoForm record) {
        // form -> do 转换
        CompanyInfo data = PropertyUtil.copy(record, CompanyInfo.class);

        // 分页数据设置
        Page<CompanyInfo> page = new Page<>(record.getCurrent(), record.getSize());
        // 查询条件
        QueryWrapper<CompanyInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(data);
        IPage<CompanyInfo> list = baseMapper.selectPage(page, queryWrapper);

        return list;
    }


    /**
     * Form -> Do
     *
     * @param form 对象
     * @return Do对象
     */
    private CompanyInfo convertDO(CompanyInfoForm form) {
        CompanyInfo data = new CompanyInfo();
        data.setId(form.getId());
        data.setCompanyCode(form.getCompanyCode());
        data.setCompanyName(form.getCompanyName());
        data.setShortName(form.getShortName());
        data.setCreateTime(DateUtil.parseTime(form.getCreateTime()));
        data.setCreator(form.getCreator());
        data.setEdittime(DateUtil.parseTime(form.getEndTime()));
        data.setEditor(form.getEditor());
        data.setNote(form.getNote());
        data.setShowSort(form.getShowSort());
        data.setCompanyAttr(form.getCompanyAttr());
        data.setStatus(form.getStatus());
        data.setIsDel(form.getIsDel());
        data.setStartTime(DateUtil.parseTime(form.getStartTime()));
        data.setEndTime(DateUtil.parseTime(form.getEndTime()));
        return data;
    }

    /**
     * Do -> VO
     *
     * @param list 对象
     * @return VO对象
     */
    private List<CompanyInfoVO> convert(List<CompanyInfo> list) {
        List<CompanyInfoVO> companyInfoList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return companyInfoList;
        }
        for (CompanyInfo source : list) {
            CompanyInfoVO target = new CompanyInfoVO();
            BeanUtils.copyProperties(source, target);
            companyInfoList.add(target);
        }
        return companyInfoList;
    }

}
