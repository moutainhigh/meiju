package cn.visolink.salesmanage.fileexport.service;

import cn.visolink.salesmanage.fileexport.model.MonthPlan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface ExportService {

    List<MonthPlan> selectAll();

    void monthlyPlanExport(HttpServletRequest request, HttpServletResponse response, String months, int preparedByUnitType, String guid, List preparedByLevel);

    void monthlyPlanUpExport(HttpServletRequest request, HttpServletResponse response, String months, int preparedByUnitType, String guid, List preparedByLevel);


     void listThreeExport(HttpServletRequest request, HttpServletResponse response, String month, String businessId) throws UnsupportedEncodingException, Exception;

     void monthlyPlanProjectExport(HttpServletRequest request, HttpServletResponse response, String months, int preparedByUnitType, String guid, List preparedByLevel);

    public void listFourExport(HttpServletRequest request, HttpServletResponse response, String month, String businessId) throws Exception;

}
