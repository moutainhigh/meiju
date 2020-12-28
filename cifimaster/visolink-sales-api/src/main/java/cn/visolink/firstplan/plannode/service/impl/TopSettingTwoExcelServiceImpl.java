package cn.visolink.firstplan.plannode.service.impl;

import cn.visolink.exception.BadRequestException;
import cn.visolink.firstplan.fpdesigntwo.dao.DesignTwoIndexDao;
import cn.visolink.firstplan.fpdesigntwo.service.DesignTwoIndexService;
import cn.visolink.firstplan.plannode.dao.PlanNodeDao;
import cn.visolink.firstplan.plannode.service.TopSettingTwoExcelService;
import cn.visolink.utils.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author bao
 * @date 2020-04-20
 */


@Service
@Transactional(readOnly = true)
public class TopSettingTwoExcelServiceImpl implements TopSettingTwoExcelService {

    @Autowired
    private DesignTwoIndexService designTwoIndexService;

    @Autowired
    private DesignTwoIndexDao designTwoIndexDao;

    @Autowired
    private PlanNodeDao planNodeDao;

    /**
     * 核心指标数据导出
     * @param request
     * @param response
     * @param map
     * */
    @Override
    public void exportExcelIndicators(HttpServletRequest request, HttpServletResponse response, Map map) {
        /*获取要导出的数据*/
        if(map.get("plan_id")==null || map.get("plan_id")==""){
            Map map1=  designTwoIndexDao.selectPlanId(map);
            map.put("plan_id",map1.get("plan_id"));
        }
        Map result = designTwoIndexService.selectAllCodeIndex(map);
        Map time = (Map) result.get("time");
        Map di= planNodeDao.selectDesigntwoIndicators(map);
        List<Map> price =(List) result.get("price");
        if(di != null){
            result.putAll(di);
        }
        try {
            XSSFWorkbook workbook = getWorkbook(request,"topSettingTwo_indicators_excel.xlsx");
            XSSFSheet sheet = workbook.getSheetAt(0);
            Map project =designTwoIndexDao.selectProjectName(map.get("plan_id")+"");
            Cell top = sheet.getRow(0).getCell(0);
            top.setCellValue(project.get("project_name")+" - 顶设2 -  核心指标 - 填报导出数据");

            CellStyle style2 = sheet.getRow(18).getCell(3).getCellStyle();
            CellStyle style3 = sheet.getRow(6).getCell(1).getCellStyle();
            setRow(sheet,2,1,result.get("browse_num")+"",style3);
            setRow(sheet,6,1,time.get("delisting_time")+"",style3);
            setRow(sheet,7,1,time.get("designone_time")+"",style3);
            setRow(sheet,8,1,time.get("designtwo_time")+"",style3);
            setRow(sheet,9,1,time.get("sales_time")+"",style3);
            setRow(sheet,10,1,time.get("sample_open_time")+"",style3);
            setRow(sheet,11,1,time.get("model_open_time")+"",style3);
            setRow(sheet,12,1,time.get("open_time")+"",style3);

            int[] rowNum ={16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36};
            int[] rowType ={3,3,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2};
            List<int[]> cellList = new ArrayList<>();
            int[] cellNum = null;
            for(int i = 0;i<21;i++){
                cellNum= new int[]{3,4,5};
                cellList.add(cellNum);
            }
            List<String[]> keyList = new ArrayList<>();
            keyList.add(new String[]{"invest_take_card_time", "rules_take_card_time", "designtwo_take_card_time"});
            keyList.add(new String[]{"invest_time","rules_time","designtwo_time"});
            keyList.add(new String[]{"invest_take_card_num","rules_take_card_num","designtwo_take_card_num"});
            keyList.add(new String[]{"invest_take_card_value","rules_take_card_value","designtwo_take_card_value"});
            keyList.add(new String[]{"invest_push_num","rules_push_num","designtwo_push_num"});
            keyList.add(new String[]{"invest_push_value","rules_push_value","designtwo_push_value"});
            keyList.add(new String[]{"invest_selling_num","rules_selling_num","designtwo_selling_num"});
            keyList.add(new String[]{"invest_selling_value","rules_selling_value","designtwo_selling_value"});
            keyList.add(new String[]{"invest_selling_take","rules_selling_take","designtwo_selling_take"});
            keyList.add(new String[]{"invest_selling_push","rules_selling_push","designtwo_selling_push"});
            keyList.add(new String[]{"","",""});
            keyList.add(new String[]{"","","cost_open_sales_price"});
            keyList.add(new String[]{"","","open_designtwo_selling"});
            keyList.add(new String[]{"","","cost_sales_generalize_price"});
            keyList.add(new String[]{"","","sales_designtwo_selling"});
            keyList.add(new String[]{"cost_invest_open_year_per","cost_rules_open_year_per","cost_designtwo_open_year_per"});
            keyList.add(new String[]{"cost_invest_all_sales_per","cost_rules_all_sales_per","cost_designtwo_all_sales_per"});
            keyList.add(new String[]{"invest_create_per","rules_create_per","designtwo_create_per"});
            keyList.add(new String[]{"invest_all_per","rules_all_per","designtwo_all_per"});
            keyList.add(new String[]{"invest_irr","rules_irr","designtwo_irr"});
            keyList.add(new String[]{"invest_payback","rules_payback","designtwo_payback"});
            setRow(workbook,sheet,rowNum,rowType,cellList,keyList,result);


            int firstRow = 26;
            Row row =sheet.getRow(26);
            if( price!=null && price.size()>0){
                for(int p = 0;p<price.size();p++){
                    Map priceMap =price.get(p);
                    String[] aa = {priceMap.get("product_type")+"",
                            priceMap.get("invest_open_avg_price")+"",
                            priceMap.get("rules_open_avg_price")+"",
                            priceMap.get("designtwo_open_avg_price")+""};
                    if(p >0){
                        sheet.shiftRows(firstRow, sheet.getLastRowNum(),1,true,false);
                        Row rownh = sheet.createRow(firstRow);
                        copyRow(workbook,sheet,row,rownh,true);
                    }
                    if(p!=0 && p==price.size()-1){
                        CellRangeAddress callRange1 = new CellRangeAddress(26,firstRow,0,0);//架构信息设定
                        sheet.addMergedRegion(callRange1);
                        CellRangeAddress callRange2 = new CellRangeAddress(26,firstRow,1,1);//架构信息设定
                        sheet.addMergedRegion(callRange2);
                    }
                    setRow(sheet,firstRow,new int[]{2,3, 4 , 5},aa,style2);
                    firstRow++;
                }
            }

            this.exportExcelResponse(response,"顶设2-核心指标",workbook);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(1001, "读取模版文件失败!");
        }
    }


    /**
     * 全盘量价规划数据导出
     * @param request
     * @param response
     * @param map
     * */
    @Override
    public void exportExcelVolumePricePlanning(HttpServletRequest request, HttpServletResponse response, Map map) {
        /*获取要导出的数据*/
        Map result = designTwoIndexService.selectAllPlan(map);
        List<Map> big = (List) result.get("big");
        List<Map> plan = (List) result.get("plan");
        try {
            XSSFWorkbook workbook = getWorkbook(request,"topSettingTwo_volume_price_planning_excel.xlsx");
            XSSFSheet sheet = workbook.getSheetAt(0);
            Map project =designTwoIndexDao.selectProjectName(map.get("plan_id")+"");
            Cell top = sheet.getRow(0).getCell(0);
            top.setCellValue(project.get("project_name")+" - 顶设2 -  全盘量价规划 - 填报导出数据");

            int firstRow = 5 ;
            int lastRow = 6 ;

            int[] collNum = null;

            Row row1 =sheet.getRow(3);
            Cell coll1 = row1.getCell(8);
            Cell coll2 = row1.getCell(9);
            Cell coll3 = row1.getCell(10);
            Cell coll4 = row1.getCell(11);
            Row row2 =sheet.getRow(4);
            Cell coll5 = row2.getCell(8);
            Cell coll6 = row2.getCell(9);
            Cell coll7 = row2.getCell(10);
            Cell coll8 = row2.getCell(11);

            CellStyle style1 =coll5.getCellStyle();
            CellStyle style2 =coll6.getCellStyle();

            Double tInvest = 0.00;
            Double tRules = 0.00;
            Double tBig = 0.00;
            Double tSupply =0.00;
            Double tSell =0.00;

            Row rowh = sheet.getRow(5);
            Row rowb = sheet.getRow(6);

            Row browh = sheet.getRow(11);
            Row browb = sheet.getRow(12);



            int[] ncollNum =  new int[]{};
            String[] ncollValue = new String[]{};
            List<String> tlist= null;
            if( plan!=null && plan.size()>0){
                for(int i = 0;i<plan.size();i++){
                    Map priceMap =plan.get(i);
                    List<Map> productList = (List<Map>) priceMap.get("product");

                    if(productList.size()>1){
                        int s =productList.size()-1;
                        this.removeMergedRegion(sheet, firstRow,2);
                        sheet.shiftRows(firstRow, sheet.getLastRowNum(),s,true,false);
                        if(i>=1) {
                            sheet.shiftRows(firstRow, sheet.getLastRowNum(), 2, true, false);
                        }
                        for(int sb = 0 ;sb<s;sb++){
                            Row rownh = sheet.createRow(firstRow+sb);
                            copyRow(workbook,sheet,rowh,rownh,true);
                        }
                        CellRangeAddress callRange = new CellRangeAddress(firstRow,firstRow+1+s,0,0);//架构信息设定
                        sheet.addMergedRegion(callRange);
                        lastRow = lastRow+s;
                    }
                    int[] subtotal = {8,9,10,11};
                    String[] acollValue =new String[]{};
                    Double ainvestPrice= 0.0;
                    Double arulesPrice= 0.0;
                    Double abigPrice= 0.0;

                    Double aSupply = 0.00;
                    Double aSell = 0.00;
                    List<String> alist=new ArrayList();
                    for(int p = 0 ; p<productList.size();p++){
                        Map producteMap =productList.get(p);
                        Double investPrice = stringToDouble(producteMap.get("all_invest_value_price")+"");
                        Double rulesPrice = stringToDouble(producteMap.get("all_rules_value_price")+"");
                        Double bigPrice = stringToDouble(producteMap.get("all_big_value_price")+"");
                        List<Map> childList = (List<Map>) producteMap.get("child");
                        collNum = new int[]{2,3,4,5,6,7,8,9,10,11};
                        double[] aa = {stringToDouble(producteMap.get("all_invest_avg_price")+""),investPrice,
                                stringToDouble(producteMap.get("all_rules_avg_price")+""),rulesPrice,
                                stringToDouble(producteMap.get("all_big_avg_price")+""),bigPrice};
                        for(int c= 0;c<childList.size();c++){
                            Map childMap =childList.get(c);
                            Double oneyearSupply = stringToDouble(childMap.get("oneyear_supply")+"");
                            Double oneyearSell = stringToDouble(childMap.get("oneyear_sell")+"");
                            if(c>=1){
                                int num =collNum.length ;
                                collNum = amend(collNum,4);
                                collNum[num] = num+2;
                                collNum[num+1] = num+3;
                                collNum[num+2] = num+4;
                                collNum[num+3] = num+5;
                                if(p == productList.size()-1) {
                                    int num3 = subtotal.length;
                                    subtotal = amend(subtotal, 4);
                                    subtotal[num3] = num + 2;
                                    subtotal[num3 + 1] = num + 3;
                                    subtotal[num3 + 2] = num + 4;
                                    subtotal[num3 + 3] = num + 5;
                                }
                                Cell lcell = row1.createCell(num+2);
                                Cell lcel2 = row1.createCell(num+3);
                                Cell lcel3 = row1.createCell(num+4);
                                Cell lcel4 = row1.createCell(num+5);
                                Cell lcel5 = row2.createCell(num+2);
                                Cell lcel6 = row2.createCell(num+3);
                                Cell lcel7 = row2.createCell(num+4);
                                Cell lcel8 = row2.createCell(num+5);
                                copyCell(workbook,coll1,lcell,true);
                                copyCell(workbook,coll2,lcel2,true);
                                copyCell(workbook,coll3,lcel3,true);
                                copyCell(workbook,coll4,lcel4,true);
                                copyCell(workbook,coll5,lcel5,true);
                                copyCell(workbook,coll6,lcel6,true);
                                copyCell(workbook,coll7,lcel7,true);
                                copyCell(workbook,coll8,lcel8,true);
                                lcell.setCellValue(childMap.get("product_year")+"");
                            }else{
                                coll1.setCellValue(childMap.get("product_year")+"");
                            }
                            if(p == productList.size()-1){
                                alist.add(childMap.get("product_year")+"");
                            }
                            int num =aa.length;
                            aa = amend(aa,4);
                            aa[num] =  oneyearSupply;
                            aa[num+1] =  oneyearSell;
                            aa[num+2] =  stringToDouble(childMap.get("oneyear_selling_per")+"");
                            aa[num+3] =  stringToDouble(childMap.get("oneyear_avg_price")+"");
                        }
                        if(i>=1){
                            if(productList.size()==1) {
                                sheet.shiftRows(firstRow, sheet.getLastRowNum(), 2, true, false);
                            }
                            Row rownh = sheet.createRow(firstRow+p);
                            Row rownb = sheet.createRow(lastRow);
                            copyRow(workbook,sheet,rowh,rownh,true);
                            copyRow(workbook,sheet,rowb,rownb,true);
                            Cell cellh = rownh.getCell(0);
                            cellh.setCellValue(priceMap.get("operation_type")+"");
                            Cell cellb = rownh.getCell(1);
                            cellb.setCellValue(producteMap.get("product_type")+"");
                        }else{
                            Cell cellh = rowh.getCell(0);
                            cellh.setCellValue(priceMap.get("operation_type")+"");
                            Cell cellb = rowh.getCell(1);
                            cellb.setCellValue(producteMap.get("product_type")+"");
                        }
                        setRow(sheet,firstRow+p,collNum,aa,style2);
                        tInvest = tInvest + investPrice;
                        tRules = tRules + rulesPrice;
                        tBig = tBig + bigPrice;
                        ainvestPrice =ainvestPrice+investPrice;
                        arulesPrice =arulesPrice+rulesPrice;
                        abigPrice =abigPrice+bigPrice;
                    }

                    for (String s:alist) {
                        for(Map mp: productList){
                                List<Map> childList = (List<Map>) mp.get("child");
                                for(Map m :childList){
                                    if(s.equals(m.get("product_year")+"")){
                                        Double oneyearSupply = stringToDouble(m.get("oneyear_supply")+"");
                                        Double oneyearSell = stringToDouble(m.get("oneyear_sell")+"");
                                        aSupply=aSupply+oneyearSupply;
                                        aSell =aSell+oneyearSell;
                                    }
                                }
                        }
                        int num3 = acollValue.length;
                        acollValue = amend(acollValue,4);
                        acollValue[num3] = aSupply+"";
                        acollValue[num3+1]= aSell +"";
                        acollValue[num3+2]= "";
                        acollValue[num3+3]= "";
                        aSupply=0.00;
                        aSell=0.00;
                    }
                    tlist=alist;
                    setRow(sheet,lastRow,new int[]{2,3,4,5,6,7},new String[] {"——",doubleToTwo(ainvestPrice)+"","——",doubleToTwo(arulesPrice)+"","——",doubleToTwo(abigPrice)+""},style2);
                    setRow(sheet,lastRow,subtotal,acollValue,style2);
                    if(productList.size()>1){
                        int s =productList.size()-1;
                        firstRow = firstRow + 2+s;
                    }else{
                        firstRow = firstRow + 2;
                    }
                    lastRow  = lastRow + 2;
                }
            }else{
                collNum = new int[]{2,3,4,5,6,7,8,9,10,11};
                setRow(sheet,firstRow,collNum,new String[]{"——","——","——","——","——","——","——","——","——","——"},style1);
                setRow(sheet,lastRow,collNum,new String[]{"——","——","——","——","——","——","——","——","——","——"},style1);
            }
            int csz = 8;
            if(tlist!= null && tlist.size()>=1){
                for(String s:tlist){
                    for(Map p :plan){
                        List<Map> productList = (List<Map>) p.get("product");
                        for(Map mp: productList){
                            List<Map> childList = (List<Map>) mp.get("child");
                            for(Map m :childList){
                                if(s.equals(m.get("product_year")+"")){
                                    Double oneyearSupply = stringToDouble(m.get("oneyear_supply")+"");
                                    Double oneyearSell = stringToDouble(m.get("oneyear_sell")+"");
                                    tSupply=tSupply+oneyearSupply;
                                    tSell =tSell+oneyearSell;
                                }
                            }
                        }
                    }
                    int num3 = ncollValue.length;
                    ncollValue = amend(ncollValue,4);
                    ncollValue[num3] = doubleToTwo(tSupply)+"";
                    ncollValue[num3+1]= doubleToTwo(tSell) +"";
                    ncollValue[num3+2]= "";
                    ncollValue[num3+3]= "";
                    tSupply=0.00;
                    tSell=0.00;

                    int num2 =ncollNum.length ;
                    ncollNum = amend(ncollNum,4);
                    ncollNum[num2]   = csz;
                    ncollNum[num2+1] = csz+1;
                    ncollNum[num2+2] = csz+2;
                    ncollNum[num2+3] = csz+3;
                    csz = csz+4;
                }
            }

            setRow(sheet,firstRow, new int[]{2,3,4,5,6,7} ,new String[]{"——",doubleToTwo(tInvest)+"","——",doubleToTwo(tRules)+"","——",doubleToTwo(tBig)+""},style2);
            setRow(sheet,firstRow, ncollNum ,ncollValue,style2);

            firstRow = firstRow + 4;
            lastRow  = lastRow + 4;

            /*合计*/
            Double bTotal =0.00;
            Double bInvest = 0.00;
            Double bVsInvest = 0.00;
            Double bRules = 0.00;
            Double bVsRules =0.00;
            /*小计*/
            Double xTotal =0.00;
            Double xInvest = 0.00;
            Double xVsInvest = 0.00;
            Double xRules = 0.00;
            Double xVsRules =0.00;

            int[] bCollNum = new int[]{2,3,4,5,6,7,8};

            int bfirstRow =firstRow;
            int ii = 0;
            if(big!=null && big.size()>0){
                for(int i = 0;i<big.size();i++){
                    Map bigMap =big.get(i);
                    Double bigTotal = stringToDouble(bigMap.get("big_total_value")+"");
                    Double investTotal = stringToDouble(bigMap.get("invest_total_value")+"");
                    Double vsInvest = stringToDouble(bigMap.get("vs_invest")+"");
                    Double rulesTotal = stringToDouble(bigMap.get("rules_total")+"");
                    Double vsRules = stringToDouble(bigMap.get("vs_rules")+"");

                    String investCause = bigMap.get("invest_cause")+"";
                    String rulesCause = bigMap.get("rules_cause")+"";
                    if(StringUtil.isEmpty(investCause)){
                        investCause="";
                    }
                    if(StringUtil.isEmpty(rulesCause)){
                        rulesCause="";
                    }
                    String[] aa = {bigTotal+"",
                            investTotal+"",
                            vsInvest+"",
                            investCause,
                            rulesTotal+"",
                            vsRules+"",
                            rulesCause};
                    xTotal=xTotal+bigTotal;
                    xInvest=xInvest+investTotal;
                    xVsInvest=xVsInvest+vsInvest;
                    xRules=xRules+rulesTotal;
                    xVsRules=xVsRules+vsRules;
                    String[] subtotal = {doubleToTwo(xTotal)+"",
                            doubleToTwo(xInvest)+"",
                            doubleToTwo(xVsInvest)+"",
                            "——",
                            doubleToTwo(xRules)+"",
                            doubleToTwo(xVsRules)+"",
                            "——"};
                    if(i>=1){
                        if(bigMap.get("operation_type").equals(big.get(i-1).get("operation_type"))){
                            firstRow = firstRow-1;
                            lastRow = lastRow-1;
                            sheet.shiftRows(firstRow, sheet.getLastRowNum(),1,true,false);
                        }else{
                            sheet.shiftRows(firstRow, sheet.getLastRowNum(),2,true,false);
                            Row rownb = sheet.createRow(lastRow);
                            copyRow(workbook,sheet,browb,rownb,true);
                        }
                        Row rownh = sheet.createRow(firstRow);
                        copyRow(workbook,sheet,browh,rownh,true);
                        Cell cell = rownh.getCell(0);
                        cell.setCellValue(bigMap.get("operation_type")+"");
                        Cell cell2 = rownh.getCell(1);
                        cell2.setCellValue(bigMap.get("product_type")+"");
                    }else{
                        Cell cell = browh.getCell(0);
                        cell.setCellValue(bigMap.get("operation_type")+"");
                        Cell cell2 = browh.getCell(1);
                        cell2.setCellValue(bigMap.get("product_type")+"");
                    }
                    setRow(sheet,firstRow,bCollNum,aa,style2);
                    setRow(sheet,lastRow,bCollNum,subtotal,style2);
                    /*合计计算*/
                    bTotal = bTotal + bigTotal;
                    bInvest = bInvest + investTotal;
                    bVsInvest = bVsInvest + vsInvest;
                    bRules = bRules + rulesTotal;
                    bVsRules = bVsRules + vsRules;

                    if(i<big.size()-1){
                        if(!bigMap.get("operation_type").equals(big.get(i+1).get("operation_type"))){
                            xTotal=0.00;
                            xInvest=0.00;
                            xVsInvest=0.00;
                            xRules=0.00;
                            xVsRules=0.00;
                            if(ii !=0){
                                CellRangeAddress callRange = new CellRangeAddress(bfirstRow,lastRow,0,0);//架构信息设定
                                sheet.addMergedRegion(callRange);
                                ii = 0;
                            }
                            bfirstRow = firstRow+2;
                        }else{
                            ii++;
                        }
                    }
                    browh = sheet.getRow(firstRow);
                    browb = sheet.getRow(lastRow);
                    /*追加行*/
                    firstRow = firstRow + 2;
                    lastRow  = lastRow + 2;
                }
            }
            setRow(sheet,firstRow,bCollNum ,new String[]{doubleToTwo(bTotal)+"",doubleToTwo(bInvest)+"",doubleToTwo(bVsInvest)+"","——",doubleToTwo(bRules)+"",doubleToTwo(bVsRules)+"","——"},style2);


            this.exportExcelResponse(response,"顶设2-全盘量价规划",workbook);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(1001, "读取模版文件失败!");
        }
    }

    /**
     * 首开前费用计划数据导出
     * @param request
     * @param response
     * @param planNodeId
     * */
    @Override
    public void exportExcelCostPlan(HttpServletRequest request, HttpServletResponse response, String planNodeId) {
        Map mmp = new HashMap();
        mmp.put("plan_node_id",planNodeId);
        if(mmp.get("plan_id")==null || mmp.get("plan_id")==""){
            Map map1=  designTwoIndexDao.selectPlanId(mmp);
            mmp.put("plan_id",map1.get("plan_id"));
        }
        /*获取要导出的数据*/
        Map results = designTwoIndexService.selectOpenCostByPlanNodeId(planNodeId,mmp.get("plan_id")+"");
        List<Map> result= (List<Map>) results.get("costList");

        try {
            XSSFWorkbook workbook = this.getWorkbook(request,"topSettingTwo_costPlan_excel.xlsx");
            XSSFSheet sheet = workbook.getSheetAt(0);

            Map project =designTwoIndexDao.selectProjectName(mmp.get("plan_id")+"");
            Cell top = sheet.getRow(0).getCell(0);
            top.setCellValue(project.get("project_name")+" - 顶设2 -  首开费用计划 - 填报导出数据");

            int rows = 4 ;
            int colls = 3 ;
            Row row1 = sheet.getRow(3);
            Row row2 = sheet.getRow(4);
            Cell cell2 = row2.getCell(1);
            Double aallsum = 0.00;
            Double publicPrice = 0.00;
            Double channelPrice = 0.00;
            Double activityPrice = 0.00;
            Double makePrice = 0.00;
            Double datumPrice = 0.00;
            Double salesAgencyPrice = 0.00;
            Double salesPlacePrice = 0.00;
            Double propertyPrice = 0.00;
            Double propertyQtPrice = 0.00;
            Double maintenancePrice = 0.00;
            Double payrollPrice = 0.00;
            Double workPrice = 0.00;
            Double travelPrice = 0.00;
            Double servePrice = 0.00;
            Double trafficPrice = 0.00;
            Double governmentPrice = 0.00;
            Double salesQt = 0.00;
            CellStyle style2 =  sheet.getRow(5).getCell(2).getCellStyle();
            if(result!=null && result.size()>0){
                for(Map map:result){
                    aallsum = aallsum +Double.parseDouble(map.get("allsum")+"");
                    publicPrice =publicPrice +Double.parseDouble(map.get("public_price")+"");
                    channelPrice =channelPrice +Double.parseDouble(map.get("channel_price")+"");
                    activityPrice =activityPrice +Double.parseDouble(map.get("activity_price")+"");
                    makePrice =makePrice +Double.parseDouble(map.get("make_price")+"");
                    datumPrice =datumPrice +Double.parseDouble(map.get("datum_price")+"");
                    salesAgencyPrice =salesAgencyPrice +Double.parseDouble(map.get("sales_agency_price")+"");
                    salesPlacePrice =salesPlacePrice +Double.parseDouble(map.get("sales_place_price")+"");
                    propertyPrice =propertyPrice +Double.parseDouble(map.get("property_price")+"");
                    propertyQtPrice =propertyQtPrice +Double.parseDouble(map.get("property_qt_price")+"");
                    maintenancePrice =maintenancePrice +Double.parseDouble(map.get("maintenance_price")+"");
                    payrollPrice =payrollPrice +Double.parseDouble(map.get("payroll_price")+"");
                    workPrice =workPrice +Double.parseDouble(map.get("work_price")+"");
                    travelPrice =travelPrice +Double.parseDouble(map.get("travel_price")+"");
                    servePrice =servePrice +Double.parseDouble(map.get("serve_price")+"");
                    trafficPrice =trafficPrice +Double.parseDouble(map.get("traffic_price")+"");
                    governmentPrice =governmentPrice +Double.parseDouble(map.get("government_price")+"");
                    salesQt =salesQt +Double.parseDouble(map.get("sales_qt")+"");

                    Cell lcell2 = row2.createCell(colls);
                    copyCell(workbook,cell2,lcell2,true);
                    lcell2.setCellValue(map.get("months")+"");
                    setRow(sheet,rows+1,colls,map.get("allsum")+"",style2);
                    setRow(sheet,rows+2,colls,map.get("public_price")+"",style2);
                    setRow(sheet,rows+3,colls,map.get("channel_price")+"",style2);
                    setRow(sheet,rows+4,colls,map.get("activity_price")+"",style2);
                    setRow(sheet,rows+5,colls,map.get("make_price")+"",style2);
                    setRow(sheet,rows+6,colls,map.get("datum_price")+"",style2);
                    setRow(sheet,rows+7,colls,map.get("sales_agency_price")+"",style2);
                    setRow(sheet,rows+8,colls,map.get("sales_place_price")+"",style2);

                    setRow(sheet,rows+11,colls,map.get("property_price")+"",style2);
                    setRow(sheet,rows+12,colls,map.get("property_qt_price")+"",style2);
                    setRow(sheet,rows+13,colls,map.get("maintenance_price")+"",style2);
                    setRow(sheet,rows+14,colls,map.get("payroll_price")+"",style2);
                    setRow(sheet,rows+15,colls,map.get("work_price")+"",style2);
                    setRow(sheet,rows+16,colls,map.get("travel_price")+"",style2);
                    setRow(sheet,rows+17,colls,map.get("serve_price")+"",style2);
                    setRow(sheet,rows+18,colls,map.get("traffic_price")+"",style2);

                    setRow(sheet,rows+21,colls,map.get("government_price")+"",style2);
                    setRow(sheet,rows+22,colls,map.get("sales_qt")+"",style2);
                    colls++;
                }
            }

            setRow(sheet,rows+1,2,aallsum+"",style2);
            setRow(sheet,rows+2,2,publicPrice+"",style2);
            setRow(sheet,rows+3,2,channelPrice+"",style2);
            setRow(sheet,rows+4,2,activityPrice+"",style2);
            setRow(sheet,rows+5,2,makePrice+"",style2);
            setRow(sheet,rows+6,2,datumPrice+"",style2);
            setRow(sheet,rows+7,2,salesAgencyPrice+"",style2);
            setRow(sheet,rows+8,2,salesPlacePrice+"",style2);

            setRow(sheet,rows+11,2,propertyPrice+"",style2);
            setRow(sheet,rows+12,2,propertyQtPrice+"",style2);
            setRow(sheet,rows+13,2,maintenancePrice+"",style2);
            setRow(sheet,rows+14,2,payrollPrice+"",style2);
            setRow(sheet,rows+15,2,workPrice+"",style2);
            setRow(sheet,rows+16,2,travelPrice+"",style2);
            setRow(sheet,rows+17,2,servePrice+"",style2);
            setRow(sheet,rows+18,2,trafficPrice+"",style2);

            setRow(sheet,rows+21,2,governmentPrice+"",style2);
            setRow(sheet,rows+22,2,salesQt+"",style2);

            this.removeMergedRegion(sheet, 3,3);
            this.removeMergedRegion(sheet, 2,1);
            Cell cell3 = sheet.getRow(2).getCell(7);
            Cell lcell3 =sheet.getRow(2).createCell(colls-1);
            Cell cell1 = row1.getCell(7);
            Cell lcell1 = row1.createCell(colls-1);
            copyCell(workbook,cell1,lcell1,true);
            copyCell(workbook,cell3,lcell3,true);
            CellRangeAddress callRange1 = new CellRangeAddress(3,3,3,colls-1);//架构信息设定
            CellRangeAddress callRange2 = new CellRangeAddress(2,2,0,colls-1);//架构信息设定
            sheet.addMergedRegion(callRange1);
            sheet.addMergedRegion(callRange2);

            this.exportExcelResponse(response,"顶设2-首开前费用计划",workbook);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(1001, "读取模版文件失败!");
        }
    }

    /**
     * 客储计划数据导出
     * @param request
     * @param response
     * @param map
     * */
    @Override
    public void exportExcelCustomerSavingsPlan(HttpServletRequest request, HttpServletResponse response, Map map) {
        if(map.get("plan_id")==null || map.get("plan_id")==""){
            Map map1=  designTwoIndexDao.selectPlanId(map);
            map.put("plan_id",map1.get("plan_id"));
        }
        Map result=designTwoIndexService.selectStorageNodePlan(map);
        try {
            XSSFWorkbook workbook = this.getWorkbook(request,"topSettingTwo_CustomerSavingsPlan_excel.xlsx");
            XSSFSheet sheet = workbook.getSheetAt(0);
            Map project =designTwoIndexDao.selectProjectName(map.get("plan_id")+"");
            Cell top = sheet.getRow(0).getCell(0);
            top.setCellValue(project.get("project_name")+" - 顶设2 - 客储达成进度 - 填报导出数据");
            CellStyle style2 = sheet.getRow(4).getCell(2).getCellStyle();

            int rows = 4 ;
            String bigWay= "";
            String littleWay= "";
            List<Map> nodePlan = (List<Map>) result.get("NodePlan");
            if(nodePlan!=null && nodePlan.size()>0){
                for(Map m:nodePlan){
                    if(rows ==4 ){
                        bigWay = m.get("big_way")+"";
                        littleWay = m.get("little_way")+"";
                    }
                    setRow(sheet,rows,0,m.get("nide_name")+"",style2);
                    setRow(sheet,rows,1,m.get("node_time")+"",style2);
                    setRow(sheet,rows,2,m.get("report_num")+"",style2);
                    setRow(sheet,rows,3,m.get("visit_num")+"",style2);
                    setRow(sheet,rows,4,m.get("little_num")+"",style2);
                    setRow(sheet,rows,5,m.get("little_per")+"",style2);
                    setRow(sheet,rows,6,m.get("big_num")+"",style2);
                    setRow(sheet,rows,7,m.get("big_per")+"",style2);
                    setRow(sheet,rows,8,m.get("sub_num")+"",style2);
                    setRow(sheet,rows,9,m.get("make_per")+"",style2);
                    rows++;
                }
            }
            int firstRow = 12;
            Row row = sheet.getRow(13);
            List<Map> week = (List<Map>) result.get("Week");
            Double all = 0.00;
            if(week!=null && week.size()>0){
                for(int i =0;i<week.size();i++){
                    Map w=week.get(i);
                    Double planAdd = 0.00;
                    if(!StringUtil.isEmpty(w.get("plan_add")+"")){
                        planAdd =Double.parseDouble(w.get("plan_add")+"");
                    }
                    all = all + planAdd;
                    if(i>1){
                        sheet.shiftRows(firstRow, sheet.getLastRowNum(),1,true,false);
                        Row rownh = sheet.createRow(firstRow);
                        copyRow(workbook,sheet,row,rownh,true);
                        setRow(sheet,firstRow,2,planAdd+"",style2);
                        setRow(sheet,firstRow,3,w.get("plan_total")+"",style2);
                        setRow(sheet,firstRow,4,w.get("plan_task_per")+"",style2);
                    }else if(i==1){
                        setRow(sheet,firstRow,2,planAdd+"",style2);
                        setRow(sheet,firstRow,3,w.get("plan_total")+"",style2);
                        setRow(sheet,firstRow,4,w.get("plan_task_per")+"",style2);
                    }
                    setRow(sheet,firstRow,0,w.get("week")+"",style2);
                    setRow(sheet,firstRow,1,w.get("day_date")+"",style2);
                    firstRow++;
                }
            }
            setRow(sheet,12,2,all+"",style2);

            setRow(sheet,firstRow+4,1,littleWay,style2);
            setRow(sheet,firstRow+5,1,bigWay,style2);

            this.exportExcelResponse(response,"顶设2-储客计划",workbook);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(1001, "读取模版文件失败!");
        }
    }


    /**
     * 根据模板名称生成 poi XSSFWorkbook
     * @param request
     * @param excelName 模板名称
     * */
    @Override
    public XSSFWorkbook  getWorkbook(HttpServletRequest request, String excelName) throws Exception{
        //服务器模版读取
        String filePath;
        String realpath = request.getServletContext().getRealPath("/");

        //导出模版路径
        String templatePath = "TemplateExcel" + File.separator + excelName;
        filePath = realpath + templatePath;
        File templateFile = new File(filePath);
        if (!templateFile.exists()) {
            throw new BadRequestException(1001, "未读取到配置的导出模版，请先配置导出模版!");
        }
        FileInputStream fileInputStream = null;

        //使用poi读取模版文件
        fileInputStream = new FileInputStream(filePath);
        return new XSSFWorkbook(fileInputStream);
    }


    /**
     * 将处理好的workbook转换成字节流存入response域，返回给客户端
     * @param response  response
     * @param excelName 导出Excel文件名称
     * @param workbook  workbook
     * */
    @Override
    public void exportExcelResponse(HttpServletResponse response, String excelName, XSSFWorkbook workbook) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = sdf.format(new Date());
        String fileName = excelName + formatDate + ".xlsx";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
        workbook.write(response.getOutputStream());
        response.getOutputStream().flush();
    }


    /**
     * 数据存放如行内(单行单列)
     * @param XSSFSheet sheet
     * @param rowNum    行数
     * @param cellNum   列数
     * @param value     value
     * @param style     默认使用当前格内样式
     * */
    public void setRow(XSSFSheet sheet,int rowNum,int cellNum,String value,CellStyle style){
        Row row = sheet.getRow(rowNum);
        if(row == null){
            row=sheet.createRow(rowNum);
        }
        Cell cell =row.getCell(cellNum);
        if(cell ==null){
            cell =row.createCell(cellNum);
            cell.setCellStyle(style);
        }
        if(StringUtil.isEmpty(value)){
            cell.setCellValue("");
        }else{
            cell.setCellValue(value);
        }
    }

    /**
     * 数据存放如行内（单行多列）（字符串）
     * @param sheet
     * @param rowNum
     * @param cellNum
     * @param values
     * @param style 默认使用当前格内样式
     * */
    public void setRow(XSSFSheet sheet,int rowNum,int[] cellNum,String[] values,CellStyle style){
        Row row = sheet.getRow(rowNum);
        if(row == null){
            row=sheet.createRow(rowNum);
        }
        for(int i=0;i<cellNum.length;i++){
            Cell cell =row.getCell(cellNum[i]);
            if(cell ==null){
                cell =row.createCell(cellNum[i]);
                cell.setCellStyle(style);
            }
            if(values[i] == null){
                cell.setCellValue("");
            }else{
                cell.setCellValue(values[i]);
            }
        }
    }

    /**
     * 数据存放如行内（但行多列）（double）
     * @param sheet
     * @param rowNum
     * @param cellNum
     * @param values
     * @param style 默认使用当前格内样式
     * */
    public void setRow(XSSFSheet sheet,int rowNum,int[] cellNum,double[] values,CellStyle style){
        Row row = sheet.getRow(rowNum);
        if(row == null){
            row=sheet.createRow(rowNum);
        }
        for(int i=0;i<cellNum.length;i++){
            Cell cell =row.getCell(cellNum[i]);
            if(cell ==null){
                cell =row.createCell(cellNum[i]);
                cell.setCellStyle(style);
            }
            cell.setCellValue(values[i]);
        }
    }

    /**
     * 数据存放如行内（多行多列）
     * @param sheet
     * @param rowNum
     * @param rowType 行内格式  1：文字格式；2：数字格式；3：时间格式（默认使用当前格内样式）
     * @param cellList
     * @param keys
     * @param map
     * */
    public void setRow(XSSFWorkbook workbook,XSSFSheet sheet,int[] rowNum,int[] rowType, List<int[]> cellList,List<String[]> keys,Map map){
        CellStyle style1 =getRowStyleHeader(workbook,1);
        for (int i = 0 ;i<rowNum.length;i++){
            int type = rowType[i];
            Row row = sheet.getRow(rowNum[i]);
            int[] cellNum = cellList.get(i);
            String[] key = keys.get(i);
            for(int c = 0 ;c<cellNum.length;c++){
                Cell cell =row.getCell(cellNum[c]);
                if(cell==null){
                    cell=row.createCell(cellNum[c]);
                    cell.setCellStyle(style1);
                }
                //cell.setCellValue(map.get(key[i])+"");
                if(StringUtil.isEmpty(key[c]+"")){
                    cell.setCellValue("——");
                }else if(StringUtil.isEmpty(map.get(key[c]+"")+"")){
                    if("——".equals(map.get(key[c]+""))){
                        cell.setCellValue("——");
                    }else{
                        cell.setCellValue("");
                    }
                }else{
                    if(type ==1 ){
                        cell.setCellValue(map.get(key[c]+"")+"");
                    }else if(type ==2 ){
                        cell.setCellValue(Double.parseDouble(map.get(key[c]+"")+""));
                    }else if(type == 3){
                        cell.setCellValue(map.get(key[c]+"")+"");
                    }
                }
            }
        }
    }



    /**
     * 返回不同格式的样式
     * @param workbook  workbook
     * @param type      1：文字格式；2：数字格式；3：时间格式；
     * */
    private CellStyle getRowStyleHeader(XSSFWorkbook workbook,int type) {
        // 生成一个样式
        CellStyle style = this.getRowStyleHeader(workbook);
        if(type == 1){
            // 生成一个字体
            Font font = workbook.createFont();
            font.setFontName("Arial");
            font.setColor(HSSFColor.WHITE.index);
            font.setFontHeightInPoints((short) 10);
            style.setWrapText(true);
            // font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            // 把字体应用到当前的样式
            style.setFont(font);
        }else if(type == 2){
            XSSFDataFormat format = workbook.createDataFormat();
            style.setDataFormat(format.getFormat("#,###.00"));
            //style.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,###,00"));
        }else if(type == 3){
            style.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
        }
        return style;
    }

    /**
     * 生成一个样式
     * @param workbook  workbook
     * @return          CellStyle
     * */
    private CellStyle getRowStyleHeader(XSSFWorkbook workbook) {
        CellStyle  style = workbook.createCellStyle();

        // 设置这些样式
        style.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setBottomBorderColor(HSSFColor.GREY_40_PERCENT.index);
        style.setLeftBorderColor(HSSFColor.GREY_40_PERCENT.index);
        style.setRightBorderColor(HSSFColor.GREY_40_PERCENT.index);
        style.setTopBorderColor(HSSFColor.GREY_40_PERCENT.index);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }



    public static int[] amend(int[] obj,int num){//如果是int类数组
        int [] o = new int[obj.length+num];
        for (int i = 0; i < obj.length; i++) {
            o[i]=obj[i];
        }
        return o;
    }
    public static double[] amend(double[] obj,int num){//如果是double类数组
        double [] o = new double[obj.length+num];
        for (int i = 0; i < obj.length; i++) {
            o[i]=obj[i];
        }
        return o;
    }
    public static String[] amend(String[] obj,int num){//如果是String类数组
        String [] o = new String[obj.length+num];
        for (int i = 0; i < obj.length; i++) {
            o[i]=obj[i];
        }
        return o;
    }


    /**
     *
     * 行复制功能
     * @param fromRow
     * @param toRow
     * */
    public static void copyRow(XSSFWorkbook wb,Sheet sheet,Row fromRow,Row toRow,boolean copyValueFlag){
        toRow.setHeight(fromRow.getHeight());
        for(int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
            if(cellRangeAddress.getFirstRow() == fromRow.getRowNum()) {
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(toRow.getRowNum(), (toRow.getRowNum() +
                        (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())), cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());
                sheet.addMergedRegion(newCellRangeAddress);
            }
        }
        for (Iterator cellIt = fromRow.cellIterator(); cellIt.hasNext();) {
            Cell tmpCell = (Cell) cellIt.next();
            Cell newCell = toRow.createCell(tmpCell.getColumnIndex());
            copyCell(wb,tmpCell, newCell, copyValueFlag);
        }
    }
    /**
     * 复制单元格
     *
     * @param srcCell
     * @param distCell
     * @param copyValueFlag
     * true则连同cell的内容一起复制
     *
     */
    public static void copyCell(XSSFWorkbook wb,Cell srcCell, Cell distCell,
                          boolean copyValueFlag) {
         CellStyle newstyle=wb.createCellStyle();
         newstyle.cloneStyleFrom(srcCell.getCellStyle());
         //样式
         distCell.setCellStyle(newstyle);
         //评论
         if (srcCell.getCellComment() != null) {
              distCell.setCellComment(srcCell.getCellComment());
         }
         // 不同数据类型处理
         int srcCellType = srcCell.getCellType();
         distCell.setCellType(srcCellType);
         if (copyValueFlag) {
              if (srcCellType == Cell.CELL_TYPE_NUMERIC) {
                   if (DateUtil.isCellDateFormatted(srcCell)) {
                        distCell.setCellValue(srcCell.getDateCellValue());
                   } else {
                        distCell.setCellValue(srcCell.getNumericCellValue());
                   }
              } else if (srcCellType == Cell.CELL_TYPE_STRING) {
                   distCell.setCellValue(srcCell.getRichStringCellValue());
              } else if (srcCellType == Cell.CELL_TYPE_BLANK) {
                   // nothing21
              } else if (srcCellType == Cell.CELL_TYPE_BOOLEAN) {
                   distCell.setCellValue(srcCell.getBooleanCellValue());
              } else if (srcCellType == Cell.CELL_TYPE_ERROR) {
                   distCell.setCellErrorValue(srcCell.getErrorCellValue());
              } else if (srcCellType == Cell.CELL_TYPE_FORMULA) {
                   distCell.setCellFormula(srcCell.getCellFormula());
              } else { // nothing29
              }
         }
    }

    /**
     * 获取区域 Region
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    public void removeMergedRegion(Sheet sheet,int row ,int column){
        int sheetMergeCount = sheet.getNumMergedRegions();//获取所有的单元格
        int index = 0;//用于保存要移除的那个单元格序号
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i); //获取第i个单元格
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();
            if(row >= firstRow && row <= lastRow)
            {
                if(column >= firstColumn && column <= lastColumn)
                {
                    index = i;
                }
            }
        }
        sheet.removeMergedRegion(index);//移除合并单元格
    }


    public double stringToDouble(String ss){
        if(StringUtil.isEmpty(ss)){
            return 0.00;
        }else{
            return doubleToTwo(Double.parseDouble(ss));
        }
    }

    public double doubleToTwo(Double ss){
        BigDecimal bd = new BigDecimal(ss);
        return bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
