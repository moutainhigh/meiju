package cn.visolink;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.visolink.service.AsyncService;
import cn.visolink.utils.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VisolinkSystemApplicationTests {
    private static final Logger logger = LoggerFactory.getLogger(VisolinkSystemApplicationTests.class);

    @Autowired
    AsyncService asyncService;
    private CountDownLatch countDownLatch ;


    @Test
    public void contextLoads() {
        try {
        //计数器数量就等于数量,因为每个会开一个线程
        countDownLatch = new CountDownLatch(100);
            for (int i = 0; i < 100; i++) {
                asyncService.writeTxt(countDownLatch);
                i++;
            }

            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }
    }
    @Test
    public void excel() throws IOException {
        String xlsFile = "f:/poiSXXFSBigData.xlsx"; //输出文件
        //内存中只创建100个对象，写临时文件，当超过100条，就将内存中不用的对象释放。
        Workbook wb = new SXSSFWorkbook(100);			//关键语句
        Sheet sheet = null;		//工作表对象
        Row nRow = null;		//行对象
        Cell nCell = null;		//列对象
        long  startTime = System.currentTimeMillis();	//开始时间
        System.out.println("strat execute time: " + startTime);

        int rowNo = 0;		//总行号
        int pageRowNo = 0;	//页行号
                sheet = wb.createSheet("我的第"+(rowNo)+"个工作簿");//建立新的sheet对象
                sheet = wb.getSheetAt(rowNo);		//动态指定当前的工作表

        for (int i = 0; i < 3000000; i++) {
//            if(rowNo%300000==0){
//                System.out.println("Current Sheet:" + rowNo/300000);
//                pageRowNo = 0;		//每当新建了工作表就将当前工作表的行号重置为0
//            }
            rowNo++;
            nRow = sheet.createRow(pageRowNo++);	//新建行对象

            // 打印每行，每行有6列数据   rsmd.getColumnCount()==6 --- 列属性的个数
            for(int j=0;j<6;j++){
                nCell = nRow.createCell(j);
                nCell.setCellValue(String.valueOf("属性")+i);
            }

            if(rowNo%10000==0){
                System.out.println("row no: " + rowNo);
            }
        }
        long finishedTime = System.currentTimeMillis();	//处理完成时间
        System.out.println("finished execute  time: " + (finishedTime - startTime)/1000 + "m");

        FileOutputStream fOut = new FileOutputStream(xlsFile);
        wb.write(fOut);
        fOut.flush();		//刷新缓冲区
        fOut.close();

        long stopTime = System.currentTimeMillis();		//写文件时间
        System.out.println("write xlsx file time: " + (stopTime - startTime)/1000 + "m");


    }

    @Test
    public  void testExport() throws IOException {
        TemplateExportParams params = new TemplateExportParams(
                "F:\\IdeaWorkSpace\\02_O2O\\visolink\\visolink-system\\src\\main\\resources\\TemplateExcel\\UserPool.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("date", "2014-12-25");
        map.put("money", 2000000.00);
        map.put("upperMoney", "贰佰万");
        map.put("company", "执笔潜行科技有限公司");
        map.put("bureau", "财政局");
        map.put("person", "JueYue");
        map.put("phone", "1879740****");
        List<Map<String, String>> listMap = new ArrayList<  Map<String, String>>();
        for (int i = 0; i < 4; i++) {
            Map<String, String> lm = new HashMap<String, String>();
            lm.put("id", i + 1 + "");
            lm.put("zijin", i * 10000 + "");
            lm.put("bianma", "A001");
            lm.put("mingcheng", "设计");
            lm.put("xiangmumingcheng", "EasyPoi " + i + "期");
            lm.put("quancheng", "开源项目");
            lm.put("sqje", i * 10000 + "");
            lm.put("hdje", i * 10000 + "");

            listMap.add(lm);
        }
        map.put("maplist", listMap);

        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        File savefile = new File("D:/excel/");
        if (!savefile.exists()) {
            savefile.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream("D:/excel/专项支出用款申请书_map.xls");
        workbook.write(fos);
        fos.close();
    }
    @Test
    public  void testBigExport() throws IOException {

//        List<MsgClient> list = new ArrayList<MsgClient>();
//        Workbook workbook = null;
//        Date start = new Date();
//        ExportParams params = new ExportParams("大数据测试", "测试");
//        for (int i = 0; i < 3000000; i++) {  //一百万数据量
//            MsgClient client = new MsgClient();
//            client.setBirthday(new Date());
//            client.setClientName("小明" + i);
//            client.setClientPhone("18797" + i);
//            client.setCreateBy("JueYue");
//            client.setId("1" + i);
//            client.setRemark("测试" + i);
//            list.add(client);
////            if(list.size() == 10000){
//                workbook = ExcelExportUtil.exportBigExcel(params, MsgClient.class, list);
//                list.clear();
////            }
//        }
//        ExcelExportUtil.closeExportBigExcel();
//        System.out.println(new Date().getTime() - start.getTime());
//        File savefile = new File("D:/excel/");
//        if (!savefile.exists()) {
//            savefile.mkdirs();
//        }
//        FileOutputStream fos = new FileOutputStream("D:/excel/ExcelExportBigData.bigDataExport.xlsx");
//        workbook.write(fos);
//        fos.close();


        //。。。。。。。 泛型导出
//        int count= 100000;
//        int size=0;
//        int pageSize = 10000;  //每次查询数量
//        ExcelUtil<Student>  excelUtil= new ExcelUtil<Student>();
//        SXSSFWorkbook workbook = excelUtil.init();
//        Map<String,Object> map=new HashMap<String, Object>()；
//
//        Page page=new Page();
//        size=count/pageSize +1;
//        for(int i=1;i<size+1;i++){
//            page.setCurrentPage(i);
//            page.setPageSize(pageSize );
//            ResultList result = studentService.queryStudent(studentQuery,page);
//            workbook=excelUtil.installWorkbook(workbook,headList,map,excelName,result.getList(),i);
//        }
//        ExcelExport.export(request, response, workbook, excelName);
        //。。。。。。。。

        }



}

