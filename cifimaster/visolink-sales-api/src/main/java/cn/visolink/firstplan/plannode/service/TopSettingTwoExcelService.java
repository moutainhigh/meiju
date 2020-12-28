package cn.visolink.firstplan.plannode.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author bao
 * @date 2020-04-20
 */


public interface TopSettingTwoExcelService {

    /**
     * 核心指标数据导出
     *
     * @param request  request
     * @param response response
     * @param map      map
     */
    void exportExcelIndicators(HttpServletRequest request, HttpServletResponse response, Map map);

    /**
     * 全盘量价规划数据导出
     *
     * @param request  request
     * @param response response
     * @param map      map
     */
    void exportExcelVolumePricePlanning(HttpServletRequest request, HttpServletResponse response, Map map);

    /**
     * 首开前费用计划数据导出
     *
     * @param request    request
     * @param response   response
     * @param planNodeId String
     */
    void exportExcelCostPlan(HttpServletRequest request, HttpServletResponse response, String planNodeId);

    /**
     * 客储计划数据导出
     *
     * @param request  request
     * @param response response
     * @param map      map
     */
    void exportExcelCustomerSavingsPlan(HttpServletRequest request, HttpServletResponse response, Map map);


    /**
     * 导出根据模板文件明返回Workbook
     *
     * @param request   request
     * @param excelName 文件名称（带后缀）
     * @return workbook
     * @throws Exception
     */
    XSSFWorkbook getWorkbook(HttpServletRequest request, String excelName) throws Exception;


    /**
     * 导出根据模板文件明返回Workbook
     *
     * @param response  response
     * @param excelName 导出文件名称
     * @param workbook  处理后的workbook
     * @throws Exception
     */
    void exportExcelResponse(HttpServletResponse response, String excelName, XSSFWorkbook workbook) throws Exception;

}

