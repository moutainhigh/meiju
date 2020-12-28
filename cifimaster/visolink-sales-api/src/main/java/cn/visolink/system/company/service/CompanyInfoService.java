package cn.visolink.system.company.service;

import cn.visolink.system.company.model.CompanyInfo;
import cn.visolink.system.company.model.form.CompanyInfoForm;
import cn.visolink.system.company.model.vo.CompanyInfoVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * CompanyInfo服务类
 * </p>
 *
 * @author autoJob
 * @since 2019-08-31
 */
public interface CompanyInfoService extends IService<CompanyInfo> {
    /**
     * 保存信息对象
     *
     * @param record 信息对象
     * @return 影响记录数
     */
    Integer save(CompanyInfoForm record);

    /**
     * 根据主键更新信息对象
     *
     * @param record 信息对象
     * @return 影响记录数
     */
    Integer updateById(CompanyInfoForm record);

    /**
     * 根据主键删除信息对象
     * 逻辑删除,字段改为删除态
     *
     * @param id 主键
     * @return 影响记录数
     */
    Integer deleteById(String id);

    /**
     * 根据主键查询信息对象
     *
     * @param id 主键
     * @return 信息对象
     */
    CompanyInfoVO selectById(String id);

    /**
     * 根据主键查询信息对象
     *
     * @param record 查询请求条件
     * @return 信息列表
     */
    List<CompanyInfoVO> selectAll(CompanyInfoForm record);

    /**
     * 分页查询信息对象
     *
     * @param record 查询请求条件
     * @return 信息列表
     */
    IPage<CompanyInfo> selectPage(CompanyInfoForm record);
}
