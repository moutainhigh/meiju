package cn.visolink.system.org.dao;

import cn.visolink.system.org.model.Organization;
import cn.visolink.system.org.model.form.OrganizationForm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author autoJob
 * @since 2019-08-28
 */
public interface OrganizationDao extends BaseMapper<Organization> {
    /**
     * 根据产品Id和父级Id查找组织树
     * @return
     */
    public List<Organization> findOrgListByOrgIdAndProIdAndCompanyId(Map map);

    /**
     * 根据产品Id和父级Id查找组织树
     * @param page
     * @param authCompanyId
     * @param productId
     * @param orgId
     * @param pId
     * @return
     */
    public Page<Organization> findOrgListByOrgIdAndProIdAndCompanyId(Page page, @Param("authCompanyId") String authCompanyId, @Param("productId") String productId,
                                                                     @Param("orgId") String orgId, @Param("pId") String pId);


    /**
     * 根据组织Id修改组织状态，禁用/启用
     * @return
     */
    public Integer updateStatusById(OrganizationForm organizationForm);

    /**
     * 根据上级组织ID查询子组织
     * @return
     */
    public List<OrganizationForm> queryChildOrgs(Map map);
    public String queryChildOrgsCount(Map map);
    public Map getParentProject(String id);
    public void updateChildFullPath(Map paramMap);

    public List<Map> getAreaProjectRel(Map map);
    public List<Map> getCityRel(Map map);

    public List<Map> getAreaProjectItemRel(Map map);

    public void  delAreaProjectRel(String jobId);

    public void addAreaProjectRel(List<Map> params);

    public List<Map> selectUserId(String obj);

    public void addUserProjectRel(String obj);

    public Map getJiTuanProjectItemRel(Map map);

    public void deleteUserProjectRel(Map map);

    int synOrgFourLevel();

    int updateProjectOrgId();

    int updateOrgLevel();

    int updateJituanLevel();

    List<Map> getQuYuCommony();

    int updateQuYuLevel(Map map);

    int updateQuYuLevelTwo(Map map);

    List<Map> getCityCompany();

    int updateCityLevel(Map map);

    List<Map> getProject();

    int updateProjectLevel(Map map);

    List<Map> getFourLevelRel();

    int delJobOrgRel();

    int insertJobOrgRel(List<Map> list);

    int updateStageLevel(Map map);

    List<Map> getJobOrgRel();

    Map getParentOrg(String orgId);

    List<Map> getUserJobs(String userId);

    Map getProjectJob(@Param("userId") String userId,@Param("menuId") String menuId,@Param("projectId") String projectId);

    Map getNextProjectJob(@Param("userId") String userId,@Param("menuId") String menuId);



}
