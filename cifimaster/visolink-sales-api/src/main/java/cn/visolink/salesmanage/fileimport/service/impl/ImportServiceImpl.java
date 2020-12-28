package cn.visolink.salesmanage.fileimport.service.impl;

import cn.visolink.exception.BadRequestException;
import cn.visolink.salesmanage.fileimport.dao.ImportDao;
import cn.visolink.salesmanage.fileimport.service.ImportService;
import cn.visolink.salesmanage.groupmanagement.dao.GroupManageUpdate;
import cn.visolink.salesmanage.monthdetail.dao.MonthManagerMapper;
import cn.visolink.utils.Constant;
import cn.visolink.utils.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * @Auther: GuRui
 * @Date: 2019/10/9 09:28
 * @Description:
 */
@Service
public class ImportServiceImpl implements ImportService {

    @Autowired
    ImportDao importDao;

    @Autowired
    MonthManagerMapper monthManagerService;

    @Autowired
    GroupManageUpdate updateMonthlyService;

    @Override
    public Map monthlyPlanImport(MultipartFile file, String months, int type) {
        Map map = new HashMap(8);
        try {
            File f = File.createTempFile("tmp", null);
            file.transferTo(f);
            f.deleteOnExit();
            FileInputStream fileInputStream;
            XSSFWorkbook workBook;
            Sheet planSheet;
            fileInputStream = new FileInputStream(f);
            workBook = new XSSFWorkbook(fileInputStream);
            planSheet = workBook.getSheetAt(0);
            //导入数据总行数
            int planSheetTotalRows = planSheet.getLastRowNum();
            int startRow = 3;
            /*项目级别单独存储*/
            if (type == Constant.PREPARED_BY_UNIT_TYPE_PROJECT) {
                for (int i = startRow; i <= planSheetTotalRows; i++) {
                    Row row = planSheet.getRow(i);

                    getCellValueAndUpdate(row, type, months);
                }
            } else {

                for (int i = startRow; i <= planSheetTotalRows; i++) {
                    Row row = planSheet.getRow(i);
                    getCellValueAndUpdate(row, type);
                }
            }
        } catch (IllegalStateException e) {
            throw new BadRequestException(-15_1001, "Excel表格内容输入格式不正确，请修改后重新导入");
        } catch (Exception e) {
            throw new BadRequestException(-15_1002, "导入失败，请联系管理员");
        }
        return map;
    }

    private void getCellValueAndUpdate(Row row, int type) {
        Cell cell = row.getCell(0);
        /*String cellValue0 = FileUtils.getCellValue(cell, null);*/
        cell = row.getCell(1);
        String cellValue1 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(2);
        String cellValue2 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(3);
        String cellValue3 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(4);
        String cellValue4 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(5);
        String cellValue5 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(6);
        String cellValue6 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(7 + 4);
        String cellValue7 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(8 + 4);
        String cellValue8 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(9 + 4);
        String cellValue9 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(10 + 4);
        String cellValue10 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(11 + 4);
        String cellValue11 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(12 + 4);
        String cellValue12 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(13 + 4);
        String cellValue13 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(14 + 4);
        String cellValue14 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(15 + 4);
        String cellValue15 = FileUtils.getCellValue(cell, null);

        cell = row.getCell(16 + 4);
        String cellValue16 = FileUtils.getCellValue(cell, null);
        String cellValue17 = "";

        Map map = new HashMap();
   /*     cellValue0 = cellValue0.replace("-", "");
        cellValue0 = cellValue0.replace(" ", "");
        map.put("business_name", cellValue0);*/
        map.put("reserve_can_sell_set", cellValue1);
        map.put("reserve_can_sell_funds", cellValue2);
        map.put("new_reserve_set", cellValue3);
        map.put("new_reserve_funds", cellValue4);
        map.put("total_reserve_set", cellValue5);
        map.put("total_reserve_funds", cellValue6);
        map.put("year_plan_sign", cellValue7);
        map.put("year_grand_total_sign", cellValue8);
        map.put("top_three_month_average_sign_set", cellValue9);
        map.put("top_three_month_average_sign_funds", cellValue10);
        map.put("upper_moon_sign_set", cellValue11);
        map.put("upper_moon_sign_funds", cellValue12);
        map.put("reserve_sign_funds", cellValue13);
        map.put("new_sign_funds", cellValue14);
        map.put("total_sign_funds", cellValue15);
        map.put("prepared_by_unit_type", type);
        if (type == Constant.PREPARED_BY_UNIT_TYPE_GROUP) {
            map.put("basisGuid", cellValue16);
        } else {
            map.put("marketing_promotion_cost", cellValue16);
            cell = row.getCell(17 + 4);
            cellValue17 = FileUtils.getCellValue(cell, null);
            map.put("basisGuid", cellValue17);

        }
        map = Iteratormap(map);
        importDao.updateMonthlyPlanBasis(map);
    }

    private void getCellValueAndUpdate(Row row, int type, String months) {
        Cell cell = row.getCell(0);
        /* String cellValue0 = FileUtils.getCellValue(cell, null);*/
        cell = row.getCell(1);
        String cellValue1 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(2);
        String cellValue2 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(3);
        String cellValue3 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(4);
        String cellValue4 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(5);
        String cellValue5 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(6);
        String cellValue6 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(7 + 4);
        String cellValue7 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(8 + 4);
        String cellValue8 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(9 + 4);
        String cellValue9 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(10 + 4);
        String cellValue10 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(11 + 4);
        String cellValue11 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(12 + 4);
        String cellValue12 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(13 + 4);
        String cellValue13 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(14 + 4);
        String cellValue14 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(15 + 4);
        String cellValue15 = FileUtils.getCellValue(cell, null);

        cell = row.getCell(16 + 4);
        String cellValue16 = FileUtils.getCellValue(cell, null);
        String cellValue17 = "";
        String cellValue18 = "";
        String cellValue19 = "";
        String cellValue20 = "";
        String cellValue21 = "";
        String cellValue22 = "";
        String cellValue23 = "";
        String cellValue24 = "";
        Map map = new HashMap();
      /*  cellValue0 = cellValue0.replace("-", "");
        cellValue0 = cellValue0.replace(" ", "");*/
        map.put("months", months);
        //map.put("business_name", cellValue0);
        map.put("reserve_can_sell_set", cellValue1);
        map.put("reserve_can_sell_funds", cellValue2);
        map.put("new_reserve_set", cellValue3);
        map.put("new_reserve_funds", cellValue4);
        map.put("total_reserve_set", cellValue5);
        map.put("total_reserve_funds", cellValue6);
        map.put("year_plan_sign", cellValue7);
        map.put("year_grand_total_sign", cellValue8);
        map.put("top_three_month_average_sign_set", cellValue9);
        map.put("top_three_month_average_sign_funds", cellValue10);
        map.put("upper_moon_sign_set", cellValue11);
        map.put("upper_moon_sign_funds", cellValue12);

        map.put("reserve_sign_set", cellValue13 == null ? 0 : cellValue13);
        map.put("reserve_sign_funds", cellValue14 == null ? 0 : cellValue14);
        map.put("new_sign_set", cellValue15 == null ? 0 : cellValue15);

        map.put("new_sign_funds", cellValue16 == null ? 0 : cellValue16);
        cell = row.getCell(17 + 4);
        cellValue17 = FileUtils.getCellValue(cell, null);
        map.put("total_sign_set", cellValue17 == null ? 0 : cellValue17);

        cell = row.getCell(18 + 4);
        cellValue18 = FileUtils.getCellValue(cell, null);
        map.put("total_sign_funds", cellValue18 == null ? 0 : cellValue18);

        cell = row.getCell(19 + 4);
        cellValue19 = FileUtils.getCellValue(cell, null);
        map.put("plan_subscription_set", cellValue19 == null ? 0 : cellValue19);
        cell = row.getCell(20 + 4);
        cellValue20 = FileUtils.getCellValue(cell, null);
        map.put("plan_subscription_funds", cellValue20 == null ? 0 : cellValue20);

        if (type == 3) {
            cell = row.getCell(21 + 4);
            cellValue21 = FileUtils.getCellValue(cell, null);
            map.put("top_three_month_average_turnover_rate", cellValue21);
            cell = row.getCell(22 + 4);
            cellValue22 = FileUtils.getCellValue(cell, null);
            map.put("last_month_turnover_rate", cellValue22);
            cell = row.getCell(23 + 4);
            cellValue23 = FileUtils.getCellValue(cell, null);
            map.put("plan_turnover_rate", cellValue23);

        }
        cell = row.getCell(24 + 4);
        cellValue24 = FileUtils.getCellValue(cell, null);

        map.put("business_id", cellValue24);

        map = Iteratormap(map);
        updateMonthlyService.updateMonthlyPlan(map);

    }






    /*@Override
    public void listThreePlanImport(MultipartFile file, String month, String projectId) {

        try {
            File f = File.createTempFile("tmp", null);
            file.transferTo(f);
            f.deleteOnExit();
            FileInputStream fileInputStream;
            XSSFWorkbook workBook;
            Sheet planSheet;
            fileInputStream = new FileInputStream(f);
            workBook = new XSSFWorkbook(fileInputStream);
            planSheet = workBook.getSheetAt(0);
            //导入数据总行数
            int planSheetTotalRows = planSheet.getLastRowNum();
            int startRow = 5;
            for (int i = startRow; i <= planSheetTotalRows; i++) {
                Row row = planSheet.getRow(i);
                geListThreeValueAndUpdate(row, month,projectId);
            }
        } catch (IllegalStateException e) {
            throw new BadRequestException(-15_1001, "Excel表格内容输入格式不正确，请修改后重新导入");
        } catch (Exception e) {
            throw new BadRequestException(-15_1002, "导入失败，请联系管理员");
        }

    }*/

    @Override
    public void listThreePlanImport(MultipartFile file, String month, String projectId) {

        try {
            File f = File.createTempFile("tmp", null);
            file.transferTo(f);
            f.deleteOnExit();
            FileInputStream fileInputStream;
            XSSFWorkbook workBook;
            Sheet planSheet;
            fileInputStream = new FileInputStream(f);
            workBook = new XSSFWorkbook(fileInputStream);
            planSheet = workBook.getSheetAt(0);
            //导入数据总行数
            int planSheetTotalRows = planSheet.getLastRowNum();
            int startRow = 5;
            List<Map> maps = new ArrayList<>(planSheetTotalRows);
            for (int i = startRow; i <= planSheetTotalRows; i++) {
                Row row = planSheet.getRow(i);
                Map map = geListThreeValueAndUpdate(row, month, projectId);
                maps.add(map);
            }
            monthManagerService.updateChannelDetailBatch(maps);
        } catch (IllegalStateException e) {
            throw new BadRequestException(-15_1001, "Excel表格内容输入格式不正确，请修改后重新导入");
        } catch (Exception e) {
            throw new BadRequestException(-15_1002, "导入失败，请联系管理员");
        }

    }

    private Map geListThreeValueAndUpdate(Row row, String months, String projectId) {

        Cell cell = row.getCell(2);
        String cellValue2 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(3);
        String cellValue3 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(4);
        String cellValue4 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(5);
        String cellValue5 = FileUtils.getCellValue(cell, null);
        cellValue5 = cellValue5.substring(0, cellValue5.lastIndexOf("%"));
        cell = row.getCell(6);
        String cellValue6 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(7);
        String cellValue7 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(8);
        String cellValue8 = FileUtils.getCellValue(cell, null);
        cellValue8 = cellValue8.substring(0, cellValue8.lastIndexOf("%"));
        cell = row.getCell(9);
        String cellValue9 = FileUtils.getCellValue(cell, null);
        cellValue9 = cellValue9.substring(0, cellValue9.lastIndexOf("%"));
        cell = row.getCell(10);
        String cellValue10 = FileUtils.getCellValue(cell, null);
        cellValue10 = cellValue10.substring(0, cellValue10.lastIndexOf("%"));
        cell = row.getCell(11);
        String cellValue11 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(12);
        String cellValue12 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(13);
        String cellValue13 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(14);
        String cellValue14 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(15);
        String cellValue15 = FileUtils.getCellValue(cell, null);

        cell = row.getCell(16);
        String cellValue16 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(17);
        String cellValue17 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(1);
        String cellValue1 = FileUtils.getCellValue(cell, null);


        Map map = new HashMap(32);

        map.put("subscription_number", cellValue2);
        map.put("subscription_amount", cellValue3);
        map.put("first_three_months_average_monthly_sets", cellValue4);

        map.put("come_client_quantity", cellValue6);
        map.put("first_three_months_monthly_average_monthly_coming_amount", cellValue7);
        map.put("coming_proportion", cellValue8);
        map.put("first_three_months_monthly_average_turnover_rate", cellValue9);
        map.put("turnover_rate", cellValue10);
        map.put("contract_amount", cellValue11);
        map.put("right_responsibility_amount", cellValue12);
        map.put("first_three_months_average_monthly_transaction_cost", cellValue13);
        map.put("transaction_cost", cellValue14);

        map.put("first_three_months_monthly_average_coming_cost", cellValue15);
        map.put("coming_cost", cellValue16);
        map.put("months", months);
        map.put("projectId", projectId);
        map.put("actionName", cellValue1);
        map.put("channel_id", cellValue17);

        return map;
    }

    /*private void geListThreeValueAndUpdate(Row row,  String months,String projectId) {

        Cell cell = row.getCell(2);
        String cellValue2 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(3);
        String cellValue3 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(4);
        String cellValue4 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(5);
        String cellValue5 = FileUtils.getCellValue(cell, null);
        cellValue5=cellValue5.substring(0,cellValue5.lastIndexOf("%"));
        cell = row.getCell(6);
        String cellValue6 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(7);
        String cellValue7 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(8);
        String cellValue8 = FileUtils.getCellValue(cell, null);
        cellValue8=cellValue8.substring(0,cellValue8.lastIndexOf("%"));
        cell = row.getCell(9);
        String cellValue9 = FileUtils.getCellValue(cell, null);
        cellValue9=cellValue9.substring(0,cellValue9.lastIndexOf("%"));
        cell = row.getCell(10);
        String cellValue10 = FileUtils.getCellValue(cell, null);
        cellValue10=cellValue10.substring(0,cellValue10.lastIndexOf("%"));
        cell = row.getCell(11);
        String cellValue11 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(12);
        String cellValue12 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(13);
        String cellValue13 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(14);
        String cellValue14 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(15);
        String cellValue15 = FileUtils.getCellValue(cell, null);

        cell = row.getCell(16);
        String cellValue16 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(17);
        String cellValue17 = FileUtils.getCellValue(cell, null);
        cell = row.getCell(1);
        String cellValue1 = FileUtils.getCellValue(cell, null);


        Map map = new HashMap();

        map.put("subscription_number", cellValue2);
        map.put("subscription_amount", cellValue3);
        map.put("first_three_months_average_monthly_sets", cellValue4);

        map.put("come_client_quantity", cellValue6);
        map.put("first_three_months_monthly_average_monthly_coming_amount", cellValue7);
        map.put("coming_proportion", cellValue8);
        map.put("first_three_months_monthly_average_turnover_rate", cellValue9);
        map.put("turnover_rate", cellValue10);
        map.put("contract_amount", cellValue11);
        map.put("right_responsibility_amount", cellValue12);
        map.put("first_three_months_average_monthly_transaction_cost", cellValue13);
        map.put("transaction_cost", cellValue14);

        map.put("first_three_months_monthly_average_coming_cost", cellValue15);
        map.put("coming_cost", cellValue16);
        map.put("months", months);
        map.put("projectId", projectId);
        map.put("actionName", cellValue1);
        map.put("channel_id", cellValue17);

        monthManagerService.updateChannelDetail(map);
    }*/

    /*
     * 遍历Map所有值，若传进来的数有空，则将它默认为0
     * */
    public Map Iteratormap(Map map1) {

        Iterator iterable = map1.entrySet().iterator();
        while (iterable.hasNext()) {
            Map.Entry entry_d = (Map.Entry) iterable.next();
            Object key = entry_d.getKey();
            Object value = entry_d.getValue();
            if (value == null || value == "") {
                value = 0;
            }
            map1.put(key.toString(), value);
        }
        return map1;
    }

}


