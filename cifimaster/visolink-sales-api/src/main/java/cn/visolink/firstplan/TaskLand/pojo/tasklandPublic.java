package cn.visolink.firstplan.TaskLand.pojo;


import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.math.BigDecimal;
import java.util.*;

public class tasklandPublic {


    /**
     * 为空字段校验
     * @param mapList 需要处理的数组结集合
     * @return 处理以后的结果
     */
    public List<Map> publicNull(List<Map> mapList){
        List<Map> resultList = new ArrayList<>();
        for (int i = 0; i < mapList.size(); i++) {
            Map<Object, Object> resultMap = new HashMap<>();
            Map<String,Object> map = mapList.get(i);
            for (Map.Entry<String,Object> entry:map.entrySet()){
                String value=entry.getValue()+"";
                if(!"".equals(value)&&!"null".equalsIgnoreCase(value)&&!"NaN".equals(value)){
                    resultMap.put(entry.getKey(),entry.getValue());
                }
            }
            resultList.add(resultMap);
        }

        return resultList;
    }


    /**
     *货值结构数据导出
     *
     * @param tollerlist 货值结构数组集合
     * @param tollerMap 货值结构通过特殊方式处理后的数组集合
     * @param sheetAt
     * @param cellStyle
     * @param isType 类型
     * @return 返回最后一行的行数
     */
    public Integer ExcelPublicToller(List<Map> tollerlist, List<Map> tollerMap, XSSFSheet sheetAt, CellStyle cellStyle,String isType){
        //定义货值结构字段数组（拿地后）
        String[] avgPriceArray = {"product_type","land_front_area", "land_front_avg_price", "land_front_value", "land_front_open_price", "land_front_cost_standard", "land_front_avg_flow",
                "will_area", "will_avg_price","will_front_value","will_front_open_price","will_front_cost_standard","will_front_avg_flow",
                "land_back_area","land_back_avg_price","land_back_value","land_back_open_price","land_back_cost_standard","land_back_avg_flow"};
        //定义货值结构字段数组（顶设1）
        String[] avgPriceArrayTopOne = {"product_type","land_back_area","land_back_avg_price","land_back_value","land_back_open_price","land_back_cost_standard",
                "land_back_avg_flow","designone_area","designone_avg_price","designonel_front_value","designone_front_open_price","designonel_front_cost_standard",
                "designone_front_avg_flow","vs_all_avg_price","vs_value_price","vs_open_price","vs_hardcover_price"};
        //下移行数
        int moveDownNum = tollerlist.size()+tollerMap.size()+1;
        //原始数据开始赋值的行数
        int num = 7;
        int numStart = 7;
        //控制业态类型合并单元格
        boolean isTrue = false;
        //控制循环货值结构的行数
        Integer rowValue = null;
        try {
            if(tollerlist.size()>0){
                sheetAt.shiftRows(numStart,sheetAt.getLastRowNum(),moveDownNum,true,false);
                //判断拿地后和顶设1不同的操作
                if(!"1".equals(isType)){
                    rowValue = 20;
                }else {
                    rowValue = 18;
                }
                //计算合计用的数组
                List<Map> hjMap = new ArrayList<>();
                //获取最终数组进行赋值
                for (int k = 0; k < tollerMap.size(); k++) {
                    isTrue = true;
                    List<Map> childMap = (List<Map>) tollerMap.get(k).get("child");
                    String operation_type =  tollerMap.get(k).get("operation_type")+"";
                    Map jsMap = xjResult(childMap,"1");
                    childMap.add(jsMap);
                    hjMap.add(jsMap);
                    //如果是最后一次循环，，则计算合计
                    if(k == tollerMap.size()-1){
                        Map heMap = xjResult(hjMap,"2");
                        childMap.add(heMap);
                    }

                    //从第七行开始赋值
                    num = num+childMap.size();
                    for (int i = numStart; i < num; i++) {

                        XSSFRow atRow_toller = sheetAt.createRow(i);

                        //给第一格赋值
                        if(isTrue || i==num-1){
                            if(i==num-1){
                                operation_type = "合计";
                            }
                            XSSFCell cell1 = atRow_toller.createCell(0);
                            cell1.setCellStyle(cellStyle);
                            if (!"".equals(operation_type) && !"null".equals(operation_type)) {
                                cell1.setCellValue(operation_type);
                            }
                        }

                        //从第2格开始赋值
                        for (int j = 1; j < rowValue; j++) {
                            isTrue = false;
                            XSSFCell cell_toller = atRow_toller.createCell(j);
                            cell_toller.setCellStyle(cellStyle);
                            Map openAvgMap = childMap.get(i - numStart);
                            String value = "";
                            if(!"1".equals(isType)){
                                value = openAvgMap.get(avgPriceArray[j - 1]) + "";
                            }else {
                                value = openAvgMap.get(avgPriceArrayTopOne[j - 1]) + "";
                            }

                            if (!"".equals(value) && !"null".equals(value)) {
                                cell_toller.setCellValue(value);
                            }else {
                                cell_toller.setCellValue("--");
                            }
                        }
                    }
                    if(childMap.size()>1){
                        int lastNum = 0;
                        if(k == tollerMap.size()-1){
                            lastNum = num-2;
                            CellRangeAddress region = new CellRangeAddress(num-1, num-1, 0, 1);
                            sheetAt.addMergedRegion(region);
                        }else {
                            lastNum = num-1;
                        }
                        CellRangeAddress region = new CellRangeAddress(numStart, lastNum, 0, 0);
                        sheetAt.addMergedRegion(region);

                    }
                    numStart = numStart+childMap.size();

                }
            }

        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
        return numStart;
    }

    /**
     * 户型结构数据导出
     *
     * @param roomlist 户型结构数据导出
     * @param roomMap 户型结构通过特殊方式处理后的数组集合
     * @param sheetAt
     * @param cellStyle
     * @param startRoom 开始的行数
     * @return 返回最后一行的行数
     */
    public Integer ExcePublicRoom(List<Map> roomlist,List<Map> roomMap, XSSFSheet sheetAt, CellStyle cellStyle,Integer startRoom,String isType){
        //定义货值结构字段数组
        String[] avgPriceArray = {"product_type","room_area","room_num","room_per"};
        //定义货值结构字段数组（顶设1）
        String[] avgPriceArrayTopOne = {"product_type","land_back_room_area","land_back_room_num","land_back_room_per","designone_room_area",
        "designone_room_num","designone_room_per","designone_room_fun","designone_south_num","vs_designone_door_num","vs_designone_room_area",
        "vs_designone_room_num","vs_designone_room_per"};
        //下移行数
        int moveDownNum = roomlist.size()+roomMap.size()+1;
        //控制业态类型合并单元格
        boolean isTrue = false;
        int numStart = startRoom;
        //控制循环货值结构的行数
        Integer rowValue = null;
        try {
            if(roomlist.size()>0){
                sheetAt.shiftRows(startRoom,sheetAt.getLastRowNum(),moveDownNum,true,false);
                //判断拿地后和顶设1不同的操作
                if(!"1".equals(isType)){
                    rowValue = 5;
                }else {
                    rowValue = 14;
                }
                //计算合计用的数组
                List<Map> hjMap = new ArrayList<>();
                //获取最终数组进行赋值
                for (int k = 0; k < roomMap.size(); k++) {
                    isTrue = true;
                    List<Map> childMap = (List<Map>) roomMap.get(k).get("child");
                    String operation_type =  roomMap.get(k).get("operation_type")+"";
                    Map jsMap = xjResultRoom(childMap,"1");
                    childMap.add(jsMap);
                    hjMap.add(jsMap);
                    //如果是最后一次循环，则计算合计
                    if(k == roomMap.size()-1){
                        Map heMap = xjResultRoom(hjMap,"2");
                        childMap.add(heMap);
                    }
                    //从头开始赋值
                    startRoom = startRoom+childMap.size();
                    for (int i = numStart; i < startRoom; i++) {

                        XSSFRow atRow_toller = sheetAt.createRow(i);

                        //给第一格赋值
                        if (isTrue || i == startRoom - 1) {
                            if (i == startRoom - 1) {
                                operation_type = "合计";
                            }
                            XSSFCell cell1 = atRow_toller.createCell(0);
                            cell1.setCellStyle(cellStyle);
                            if (!"".equals(operation_type) && !"null".equals(operation_type)) {
                                cell1.setCellValue(operation_type);
                            }
                        }

                        //从第2格开始赋值
                        for (int j = 1; j < rowValue; j++) {
                            isTrue = false;
                            XSSFCell cell_toller = atRow_toller.createCell(j);
                            cell_toller.setCellStyle(cellStyle);
                            Map openAvgMap = childMap.get(i - numStart);
                            String value = "";
                            if(!"1".equals(isType)){
                                value = openAvgMap.get(avgPriceArray[j - 1]) + "";
                            }else {
                                value = openAvgMap.get(avgPriceArrayTopOne[j - 1]) + "";
                            }

                            if (!"".equals(value) && !"null".equals(value)) {
                                cell_toller.setCellValue(value);
                            }else {
                                cell_toller.setCellValue("--");
                            }
                        }
                    }
                    if(childMap.size()>1){
                        int lastNum = 0;
                        if(k == roomMap.size()-1){
                            lastNum = startRoom-2;
                            CellRangeAddress region = new CellRangeAddress(startRoom-1, startRoom-1, 0, 1);
                            sheetAt.addMergedRegion(region);
                        }else {
                            lastNum = startRoom-1;
                        }
                        CellRangeAddress region = new CellRangeAddress(numStart, lastNum, 0, 0);
                        sheetAt.addMergedRegion(region);

                    }
                    numStart = numStart+childMap.size();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
        return numStart;
    }

    /**
     *
     * 计算货值结构的小计和合计
     * @param childMap 需要计算的map
     * @param type 类型 ，小计（1）和合计（2）
     * @return 计算结果
     */
    public Map xjResult(List<Map> childMap,String type){
        //计算小计
        Map jsMap = new HashMap();
        Double xjland_front_areaRe = 0.00;
        Double xjland_front_valueRe = 0.00;
        Double xjwill_areaRe = 0.00;
        Double xjwill_front_valueRe = 0.00;
        Double xjland_back_valueRe = 0.00;
        Double xjland_back_areaRe = 0.00;

        Double xjdesignone_areaRe = 0.00;
        Double xjdesignonel_front_valueRe = 0.00;
        Double xjvs_value_priceRe = 0.00;

        for (int n = 0; n < childMap.size(); n++) {
            String xjland_front_area = childMap.get(n).get("land_front_area")+"";
            String xjland_front_value = childMap.get(n).get("land_front_value")+"";
            String xjwill_area = childMap.get(n).get("will_area")+"";
            String xjwill_front_value = childMap.get(n).get("will_front_value")+"";
            String xjland_back_value = childMap.get(n).get("land_back_value")+"";
            String xjland_back_area = childMap.get(n).get("land_back_area")+"";
            String xjdesignone_area = childMap.get(n).get("designone_area")+"";
            String xjdesignonel_front_value = childMap.get(n).get("designonel_front_value")+"";
            String xjvs_value_price = childMap.get(n).get("vs_value_price")+"";

            if(!"".equals(xjland_front_area) && !"null".equals(xjland_front_area)){
                Double hj = Double.parseDouble(xjland_front_area);
                xjland_front_areaRe = addPublic(xjland_front_areaRe,hj);
            }
            if(!"".equals(xjland_front_value) && !"null".equals(xjland_front_value)){
                Double hj = Double.parseDouble(xjland_front_value);
                xjland_front_valueRe = addPublic(xjland_front_valueRe,hj);
            }
            if(!"".equals(xjwill_area) && !"null".equals(xjwill_area)){
                Double hj = Double.parseDouble(xjwill_area);
                xjwill_areaRe = addPublic(xjwill_areaRe,hj);
            }
            if(!"".equals(xjwill_front_value) && !"null".equals(xjwill_front_value)){
                Double hj = Double.parseDouble(xjwill_front_value);
                xjwill_front_valueRe = addPublic(xjwill_front_valueRe,hj);
            }
            if(!"".equals(xjland_back_value) && !"null".equals(xjland_back_value)){
                Double hj = Double.parseDouble(xjland_back_value);
                xjland_back_valueRe = addPublic(xjland_back_valueRe,hj);
            }
            if(!"".equals(xjland_back_area) && !"null".equals(xjland_back_area)){
                Double hj = Double.parseDouble(xjland_back_area);
                xjland_back_areaRe = addPublic(xjland_back_areaRe,hj);
            }
            if(!"".equals(xjdesignone_area) && !"null".equals(xjdesignone_area)){
                Double hj = Double.parseDouble(xjdesignone_area);
                xjdesignone_areaRe = addPublic(xjdesignone_areaRe,hj);
            }
            if(!"".equals(xjdesignonel_front_value) && !"null".equals(xjdesignonel_front_value)){
                Double hj = Double.parseDouble(xjdesignonel_front_value);
                xjdesignonel_front_valueRe = addPublic(xjdesignonel_front_valueRe,hj);
            }
            if(!"".equals(xjvs_value_price) && !"null".equals(xjvs_value_price)){
                Double hj = Double.parseDouble(xjvs_value_price);
                xjvs_value_priceRe = addPublic(xjvs_value_priceRe,hj);
            }

        }
        jsMap.put("land_front_area",xjland_front_areaRe);
        jsMap.put("land_front_value",xjland_front_valueRe);
        jsMap.put("will_area",xjwill_areaRe);
        jsMap.put("will_front_value",xjwill_front_valueRe);
        jsMap.put("land_back_value",xjland_back_valueRe);
        jsMap.put("land_back_area",xjland_back_areaRe);
        jsMap.put("designone_area",xjdesignone_areaRe);
        jsMap.put("designonel_front_value",xjdesignonel_front_valueRe);
        jsMap.put("vs_value_price",xjvs_value_priceRe);
        if("1".equals(type)){
            jsMap.put("product_type","小计");
        }else {
            jsMap.put("product_type","合计");
        }


        return jsMap;

    }

    /**
     *
     * 计算户型的小计和合计
     * @param childMap 户型数组集合
     * @param type 需要计算的类型---小计（1）和合计（2）
     * @return 计算结果
     */
    public Map xjResultRoom(List<Map> childMap,String type){
        //计算小计
        Map jsMap = new HashMap();
        Double xjroom_numRe = 0.00;
        Double xjland_back_room_numRe = 0.00;
        Double xjdesignone_room_numRe = 0.00;
        Double xjvs_designone_room_numRe = 0.00;
        for (int n = 0; n < childMap.size(); n++) {
            String xjroom_num = childMap.get(n).get("room_num")+"";
            if(!"".equals(xjroom_num) && !"null".equals(xjroom_num)){
                Double hj = Double.parseDouble(xjroom_num);
                xjroom_numRe = addPublic(xjroom_numRe,hj);
            }
            String xjland_back_room_num = childMap.get(n).get("land_back_room_num")+"";
            if(!"".equals(xjland_back_room_num) && !"null".equals(xjland_back_room_num)){
                Double hj = Double.parseDouble(xjland_back_room_num);
                xjland_back_room_numRe = addPublic(xjland_back_room_numRe,hj);
            }
            String xjdesignone_room_num = childMap.get(n).get("designone_room_num")+"";
            if(!"".equals(xjdesignone_room_num) && !"null".equals(xjdesignone_room_num)){
                Double hj = Double.parseDouble(xjdesignone_room_num);
                xjdesignone_room_numRe = addPublic(xjdesignone_room_numRe,hj);
            }
            String xjvs_designone_room_num = childMap.get(n).get("vs_designone_room_num")+"";
            if(!"".equals(xjvs_designone_room_num) && !"null".equals(xjvs_designone_room_num)){
                Double hj = Double.parseDouble(xjvs_designone_room_num);
                xjvs_designone_room_numRe = addPublic(xjvs_designone_room_numRe,hj);
            }

        }
        jsMap.put("room_num",xjroom_numRe);
        jsMap.put("land_back_room_num",xjland_back_room_numRe);
        jsMap.put("designone_room_num",xjdesignone_room_numRe);
        jsMap.put("vs_designone_room_num",xjvs_designone_room_numRe);
        if("1".equals(type)){
            jsMap.put("product_type","小计");
        }else {
            jsMap.put("product_type","合计");
        }


        return jsMap;

    }


    /**
     *
     * @param timeNode 时间节点map
     * @param sheetAt
     * @param numberRoom 户型结束后得到的行数
     */
    public void ExcelPublicTime(Map timeNode, XSSFSheet sheetAt,Integer numberRoom){
        try {
            //摘牌时间
            Integer delistingDatetime = numberRoom+4;
            //顶设1时间
            Integer designoneDatetime = numberRoom+5;
            //顶设2时间
            Integer designtwoDatetime = numberRoom+6;
            //售楼处开放时间
            Integer salesDatetime = numberRoom+7;
            //样板段开放时间
            Integer sample_openDatetime = numberRoom+8;
            //顶设一时间
            Integer model_openDatetime = numberRoom+9;
            //开盘时间
            Integer openDatetime = numberRoom+10;

            if(timeNode!=null){
                String delisting_time = timeNode.get("delisting_time")+"";
                String designone_time = timeNode.get("designone_time")+"";
                String designtwo_time = timeNode.get("designtwo_time")+"";
                String sales_time = timeNode.get("sales_time")+"";
                String sample_open_time = timeNode.get("sample_open_time")+"";
                String model_open_time = timeNode.get("model_open_time")+"";
                String open_time = timeNode.get("open_time")+"";
                if(!"".equals(delisting_time)&&!"null".equals(delisting_time)){
                    XSSFRow atRowdelisting_time = sheetAt.getRow(delistingDatetime);
                    XSSFCell celldelisting_time = atRowdelisting_time.getCell(1);
                    celldelisting_time.setCellValue(delisting_time);
                }
                if(!"".equals(designone_time)&&!"null".equals(designone_time)){
                    XSSFRow atRowdelisting_time = sheetAt.getRow(designoneDatetime);
                    XSSFCell celldelisting_time = atRowdelisting_time.getCell(1);
                    celldelisting_time.setCellValue(designone_time);
                }
                if(!"".equals(designtwo_time)&&!"null".equals(designtwo_time)){
                    XSSFRow atRowdelisting_time = sheetAt.getRow(designtwoDatetime);
                    XSSFCell celldelisting_time = atRowdelisting_time.getCell(1);
                    celldelisting_time.setCellValue(designtwo_time);
                }
                if(!"".equals(sales_time)&&!"null".equals(sales_time)){
                    XSSFRow atRowdelisting_time = sheetAt.getRow(salesDatetime);
                    XSSFCell celldelisting_time = atRowdelisting_time.getCell(1);
                    celldelisting_time.setCellValue(sales_time);
                }
                if(!"".equals(sample_open_time)&&!"null".equals(sample_open_time)){
                    XSSFRow atRowdelisting_time = sheetAt.getRow(sample_openDatetime);
                    XSSFCell celldelisting_time = atRowdelisting_time.getCell(1);
                    celldelisting_time.setCellValue(sample_open_time);
                }
                if(!"".equals(model_open_time)&&!"null".equals(model_open_time)){
                    XSSFRow atRowdelisting_time = sheetAt.getRow(model_openDatetime);
                    XSSFCell celldelisting_time = atRowdelisting_time.getCell(1);
                    celldelisting_time.setCellValue(model_open_time);
                }
                if(!"".equals(open_time)&&!"null".equals(open_time)){
                    XSSFRow atRowdelisting_time = sheetAt.getRow(openDatetime);
                    XSSFCell celldelisting_time = atRowdelisting_time.getCell(1);
                    celldelisting_time.setCellValue(open_time);
                }
            }
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    /**
     * 销售目标导出
     *
     * @param sales
     *          销售目标数组集合
     * @param numberRoom
     *          初始化行数，户型填充结束后得到的行数
     * */
    public Integer ExcelPublicSales(List<Map> sales,XSSFSheet sheetAt,CellStyle cellStyle,Integer numberRoom,String isType){
        //需要导出的字段
        String[] avgPriceArray = {"sales_time","land_front_price","will_price","land_back_price"};
        //开始填充数据的行数
        Integer startNum = numberRoom + 15;
        //需要下移的行数（+1是因为还算了一个整盘合计的行数）
        Integer moveDownNum = sales.size()+1;
        //返回的行数（+1是因为还算了一个整盘合计的行数）
        Integer endNum = startNum+moveDownNum+1;
        //重新放首开的数组
        List<Map> listMap = new ArrayList<>();
        try {
            if(sales.size()>0){
                if(!"1".equals(isType)){
                    sheetAt.shiftRows(startNum,sheetAt.getLastRowNum(),moveDownNum,true,false);
                }
                Map mapZphj = new HashMap();
                Double land_front_price = 0.00;
                Double will_price = 0.00;
                Double land_back_price = 0.00;
                for (int i = 0; i < sales.size(); i++) {
                    String sales_time = sales.get(i).get("sales_time")+"";
                    if("首开".equals(sales_time)){
                        listMap.add(sales.get(i));
                        sales.remove(i);
                        i--;
                        continue;
                    }
                    //计算整盘合计
                    String hjland_front_price = sales.get(i).get("land_front_price")+"";
                    if(!"".equals(hjland_front_price) && !"null".equals(hjland_front_price)){
                        Double hj = Double.parseDouble(hjland_front_price);
                        land_front_price = addPublic(land_front_price,hj);
                    }
                    String hjwill_price = sales.get(i).get("will_price")+"";
                    if(!"".equals(hjwill_price) && !"null".equals(hjwill_price)){
                        Double hj = Double.parseDouble(hjwill_price);
                        will_price = addPublic(will_price,hj);
                    }
                    String hjland_back_price = sales.get(i).get("land_back_price")+"";
                    if(!"".equals(hjland_back_price) && !"null".equals(hjland_back_price)){
                        Double hj = Double.parseDouble(hjland_back_price);
                        land_back_price = addPublic(land_back_price,hj);
                    }
                }

                //添加整盘合计到数组中
                mapZphj.put("sales_time","整盘合计");
                mapZphj.put("land_front_price",land_front_price);
                mapZphj.put("will_price",will_price);
                mapZphj.put("land_back_price",land_back_price);
                sales.add(mapZphj);

                for (int n = 0; n < listMap.size(); n++) {
                    XSSFRow atRow_toller = sheetAt.createRow(startNum+n);
                    Map openAvgMap = listMap.get(n);
                    for (int j = 0; j < 4; j++) {
                        XSSFCell cell_toller = atRow_toller.createCell(j);
                        cell_toller.setCellStyle(cellStyle);

                        String value = openAvgMap.get(avgPriceArray[j]) + "";
                        if (!"".equals(value) && !"null".equals(value)) {
                            cell_toller.setCellValue(value);
                        }else {
                            cell_toller.setCellValue("--");
                        }
                    }
                }

                for (int k = 0; k < moveDownNum-1; k++) {
                    XSSFRow atRow_toller = sheetAt.createRow(startNum+k+1);
                    Map openAvgMap = sales.get(k);
                    for (int j = 0; j < 4; j++) {
                        XSSFCell cell_toller = atRow_toller.createCell(j);
                        cell_toller.setCellStyle(cellStyle);

                        String value = openAvgMap.get(avgPriceArray[j]) + "";
                        if (!"".equals(value) && !"null".equals(value)) {
                            cell_toller.setCellValue(value);
                        }else {
                            cell_toller.setCellValue("--");
                    }
                }
            }
        }

        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
        return endNum;
    }

    /**
     * 费用导出
     * @param cost 费用数据集合
     * @param sheetAt
     * @param numberRoom 初始化行数，户型填充结束后得到的行数
     */
    public void ExcelPublicCose(List<Map> cost,XSSFSheet sheetAt,Integer numberRoom){
        //需要导出的字段
        String[] avgPriceArray = {"land_front_price","will_price","land_back_price","land_front_per","will_per","land_per"};
        //开始填充数据的行数
        Integer startNum = numberRoom + 3;
        Integer startNumRate = numberRoom + 4;
        try {
            if(cost.size()>0){
                Map costMap = cost.get(0);
                for (int i = 0; i < 6; i++) {
                    String value = costMap.get(avgPriceArray[i])+"";
                    if(i<3){
                        XSSFRow costRow = sheetAt.getRow(startNum);
                        XSSFCell costRowCell = costRow.getCell(i+1);
                        costRowCell.setCellValue(value);
                    }else {
                        XSSFRow costRow = sheetAt.getRow(startNumRate);
                        XSSFCell costRowCell = costRow.getCell(i-2);
                        costRowCell.setCellValue(value);
                    }
                }

            }
        }catch (Exception e){

        }

    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1
     *            被加数
     * @param v2
     *            加数
     * @return 两个参数的和
     */
    public static double addPublic(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

}
