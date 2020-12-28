package cn.visolink.salesmanage.riskcontrolmanager.service;


import cn.visolink.exception.ResultBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface RiskControlService {
        /*初始化风控表数据*/
    public Boolean getData(Map map);

    /*查找风控表数据*/
    ResultBody<Object> selectRiskInfor(Map map);

    /*风控里表数据*/
    ResultBody<Object> selectRiskInside(Map map);
    /*更新买方表*/
   void setBuyer(String queryStartDate,String queryEndDate);

    void riskDataExport(HttpServletRequest request, HttpServletResponse response, Map map,String inside);
    /*查找事业部名字和ID*/
    List<Map> selectBusinessName();

}
