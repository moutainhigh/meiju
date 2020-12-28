package cn.visolink.salesmanage.packagedis.service;

import cn.visolink.exception.ResultBody;

import java.util.List;
import java.util.Map;

/**
 * @Auther: Wjc
 * @Date: 2019/10/11 0011 16:08
 * @Description:一揽子折扣
 */
public interface PackageDiscontService {
    /**
     * 查询登录人下的项目
     * @param map
     * @return
     */
    List<Map> getProjects(Map map);
    /**
     * 查询项目的业态
     * @param map
     * @return
     */
    List<Map> getFormats(Map map);
    /**
     * 查询项目的楼栋
     * @param map
     * @return
     */
    List<Map> getBuilding(Map map);

    /**
     * 获取液态和楼栋
     */
    ResultBody getBuildingAndFormatsData(Map map);
    /**
     * 添加一揽子折扣
     * @param map
     * @return
     */
    ResultBody insertPackageDis(Map map);


    /**
     * 添加一揽子分期
     * @param map
     * @return
     */
    ResultBody insertPackageStages(Map map);


    ResultBody packageStagesApply(Map map);
    //获取一揽子折扣数据
    ResultBody packageStagesDisApply(Map map);



    /**
     * 一揽子分期折扣列表
     *
     */
    ResultBody stagesSelect(Map map);

    String getFlowCode(String josnid);
    /**
     * 获取流程数据详情
     */
    ResultBody getFlowDataInfo(String jsonId);

    //设置窗口期
    ResultBody windowPhase();

}
