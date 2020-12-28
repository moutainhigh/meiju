package cn.visolink.system.org.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.org.model.Organization;
import cn.visolink.system.org.model.form.OrganizationForm;
import cn.visolink.system.org.model.vo.OrganizationVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Organization服务类
 * </p>
 *
 * @author autoJob
 * @since 2019-08-28
 */
public interface OrganizationService extends IService<Organization> {
    /**
     * 保存信息对象
     *
     * @param record 信息对象
     * @return 影响记录数
     */
    OrganizationForm save(OrganizationForm record);

    /**
     * 根据主键更新信息对象
     *
     * @param record 信息对象
     * @return 影响记录数
     */
    Integer updateById(OrganizationForm record);

    /**
     * 组织机构禁用/启用
     *
     * @returnString id, Integer status
     */
    Integer updateStatusById(OrganizationForm organizationForm);

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
    OrganizationVO selectById(String id);

    /**
     * 根据主键查询信息对queryChildOrgs象
     *
     * @param record 查询请求条件
     * @return 信息列表
     */
    Map selectAll(OrganizationForm record);

    /**
     * 分页查询信息对象
     *
     * @param record 查询请求条件
     * @return 信息列表
     */
    IPage<OrganizationVO> selectPage(OrganizationForm record);

    Map queryChildOrgs(Map map);

    List<Map> getAreaProjectRel(Map map);

    void addAreaProjectRel(List<Map> parmas);

    Map getJiTuanProjectItemRel(Map map);


    /*
    * 把组织分为四层级
    * */
    int synOrgFourLevel();

    ResultBody checkUserJobOrg(String userId, String menuId, String projectId);
}
