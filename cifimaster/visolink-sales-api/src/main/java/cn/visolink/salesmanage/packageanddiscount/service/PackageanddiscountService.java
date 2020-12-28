package cn.visolink.salesmanage.packageanddiscount.service;

import cn.visolink.common.bean.VisolinkResultBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/3/25 11:18 上午
 */
public interface PackageanddiscountService {

    VisolinkResultBody viewPackageDiscount(Map map, HttpServletRequest request);

    VisolinkResultBody getBuildDataByProjectId(Map map);

    VisolinkResultBody savePackageDiscount(Map map,HttpServletRequest request);

    VisolinkResultBody getApplayList(Map map,HttpServletRequest request);

    VisolinkResultBody approvalPushDataForMy(Map map);
    void callinginterface(Map map);
}
