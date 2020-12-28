package cn.visolink.salesmanage.businessmanager.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BusinessManagerDao {

    /**
     *区域查看 项目上报
     * @param map
     * @return
     */
    public List<Map> regionReportSelect(Map<String, Object> map);
    /**
     *区域查看项目上报	  合计初始化
     * @param map
     * @return
     */
    public List<Map> regionFundsSelect(Map<String, Object> map);
    /**
     *修改合計
     * @param map
     * @return
     */
    public Integer regionFundsUpdate(Map<String, Object> map);

    /**
     * 修改合計sql拆分 (修改)
     * bql 2020.07.30
     *
     * @param map map
     * @return Integer
     */
    Integer updateRegionFunds(Map<String, Object> map);

    /**
     * 修改合計sql拆分 (查询)
     * bql 2020.07.30
     *
     * @param map map
     * @return Map<String, Object>
     */
    Map<String, Object>  selectRegionFunds(Map<String, Object> map);

    /**
     *上报区域项目合計
     * @param map
     * @return
     */
    public Integer regionFundsEffective(Map<String, Object> map);
    /**
     *验证区域上报指标是否高于下达指标
     * @param map
     * @return
     */
    public Map  testregionEffective(Map<String, Object> map);

    /*
    * 集团查看区域上报数据列表
    * */
    /**
     *事业部列表
     * @param map
     * @return
     */
    public List<Map> businessDepartSelect(Map<String, Object> map);

    /**事业部异步请求 项目列表
     *
     * @param map
     * @return
     */
    public List<Map> businessprojectSelect(Map<String, Object> map);

    /**
     *合计
     * @param map
     * @return
     */
    public List<Map> businessTotalSelect(Map<String, Object> map);

    /**
     *修改项目合计
     * @param map
     * @return
     */
    public Integer businessFundsUpdate(Map<String, Object> map);

    /**
     *集团确认
     * @param
     * @return
     */
    public Integer businessFundsEffective(Map<String, Object> map);
    /**
     *集团确认后
     *         添加等级为4的数据
     * @param
     * @return
     */
    public Integer insertMonthIndex(Map<String, Object> map);
    /**
     *添加之前先删除
     *         添加等级为4的数据
     * @param
     * @return
     */
    public Integer  deleteMonthIndex(String months);
/*
* 锁定项目不让编辑
* */
    public Integer   lockedProject(Map<String, Object> map);

    /*
     * 查看某个月下所有区域
     * */
    List<Map> AllRegionStatus(Map map);

    Integer  updateAllRegionStatus(Map map);

  String  selectOrgId(Map map);

}
