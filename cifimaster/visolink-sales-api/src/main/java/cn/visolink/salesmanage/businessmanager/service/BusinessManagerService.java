package cn.visolink.salesmanage.businessmanager.service;

import java.util.List;
import java.util.Map;


public interface BusinessManagerService {
    /*
     *区域查看项目上报数据
     * */
    /**
     *区域查看区域查看  项目上报
     * @param
     * @return
     */
    public List<Map> regionReportSelect(String regionOrgId, String months);
    /**
     *区域查看项目上报合计
     * @param
     * @return
     */
    public List<Map> regionFundsSelect(String regionOrgId, String months);
    /**
     *修改合計
     * @param map
     * @return
     */
    public Integer regionFundsUpdate(Map<String, Object> map);

    /**
     *上报区域项目合計
     * @param map
     * @return
     */
    public Integer regionFundsEffective(Map<String, Object> map);


    /**
     *事业部列表
     * @param
     * @return
     */
    public List<Map> businessDepartSelect(String months);

    /**
     *事业部异步请求 项目列表
     * @param
     * @return
     */
    public List<Map> businessprojectSelect(String regionOrgId, String months);

    /**
     *
     * 合计
     * @param
     * @return
     */
    public List<Map> businessTotalSelect(String regionOrgId, String months);

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
    public Integer businessFundsEffective(String months);
}
