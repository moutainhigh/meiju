package cn.visolink.salesmanage.gxcinterface.service.impl;

import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.datainterface.dao.DatainterfaceDao;
import cn.visolink.salesmanage.datainterface.service.Datainterfaceservice;
import cn.visolink.salesmanage.gxcinterface.dao.GXCInterfaceDao;
import cn.visolink.salesmanage.gxcinterface.service.GXCInterfaceservice;
import cn.visolink.system.timelogs.bean.SysLog;
import cn.visolink.system.timelogs.dao.TimeLogsDao;
import cn.visolink.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 *
 * @author jwb
 * @date 2019-9-20
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GXCInterfaceserviceImpl implements GXCInterfaceservice {

    @Autowired
    private GXCInterfaceDao gxcinterfaceDao;


    //注入sql操作类
    @Resource(name="jdbcTemplategxc")
    private JdbcTemplate jdbcTemplategxc;

    /**
     * 增量获取动态货值视图数据
     * @return
     */
    @Override
    public ResultBody insertDynamicValue(Map params) {
        //前二个月
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -2);
        Date m3 = c.getTime();
        String mon3 = format.format(m3);
        if(params==null || params.size()==0 || params.get("startTime")==null ||  params.get("startTime").equals("")){
            mon3=mon3+"-01";
        }else{
            mon3=params.get("startTime")+"";
        }
        String sqls="SELECT * FROM  v_sman_dynamic_value where versionTime>= '"+mon3+"'";
        //查询供货视图数据
        List<Map<String, Object>> listData = jdbcTemplategxc.queryForList(sqls);
        if(listData!=null && listData.size()>0){
            //根据时间删除
            gxcinterfaceDao.deleteDynamicValue(mon3);
            //写入动态货值
            gxcinterfaceDao.insertvaluedthz(listData);
        }
        //更新数据条数
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setMessages("初始化供销存数据成功!");
        resultBody.setCode(200);
        return resultBody;
    }

    @Override
    public ResultBody insertPlanValue(Map params) {
        //前二个月
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -2);
        Date m3 = c.getTime();
        String mon3 = format.format(m3);
        if(params==null || params.size()==0 || params.get("startTime")==null ||  params.get("startTime").equals("")){
            mon3=mon3+"-01";
        }else{
            mon3=params.get("startTime")+"";
        }
        String sqls="SELECT * FROM  v_sman_plan_value where versionTime>= '"+mon3+"'";
        //查询供货视图数据
        List<Map<String, Object>> listData = jdbcTemplategxc.queryForList(sqls);
        if(listData!=null && listData.size()>0){
            //根据时间删除
            gxcinterfaceDao.deletePlanValueByVersionId(mon3);
            //写入战规货值
            gxcinterfaceDao.insertvaluezghz(listData);
        }
        //更新数据条数
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setMessages("初始化供销存数据成功!");
        resultBody.setCode(200);
        return resultBody;
    }

    @Override
    public ResultBody insertSignPlan(Map params) {
        //前二个月
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -2);
        Date m3 = c.getTime();
        String mon3 = format.format(m3);
        if(params==null || params.size()==0 || params.get("startTime")==null ||  params.get("startTime").equals("")){
            mon3=mon3+"-01";
        }else{
            mon3=params.get("startTime")+"";
        }
        String sqls="SELECT * FROM  v_sman_sign_plan where create_time>= '"+mon3+"'";
        //查询供货视图数据
        List<Map<String, Object>> listData = jdbcTemplategxc.queryForList(sqls);
        if(listData!=null && listData.size()>0){
            //根据时间删除
            gxcinterfaceDao.deleteSignPlan(mon3);
            //写入计划签约
            gxcinterfaceDao.insertvalueqy(listData);
        }
        //更新数据条数
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setMessages("初始化供销存数据成功!");
        resultBody.setCode(200);
        return resultBody;
    }

    @Override
    public ResultBody insertSupplyPlan(Map params) {
        //上个月
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        Date m3 = c.getTime();
        String mon3 = format.format(m3);
        if(params==null || params.size()==0 || params.get("startTime")==null ||  params.get("startTime").equals("")){
            mon3=mon3+"-01";
        }else{
            mon3=params.get("startTime")+"";
        }
       // String sqls="SELECT * FROM  v_sman_supply_plan where create_time>= '"+mon3+"'";
        String sqls="SELECT * FROM  v_sman_supply_plan where affiliation_month>= '"+mon3+"'";
        //查询供货视图数据
        List<Map<String, Object>> listData = jdbcTemplategxc.queryForList(sqls);
        if(listData!=null && listData.size()>0){
            //根据时间删除
            gxcinterfaceDao.deleteSupplyPlanVersionId(mon3);
            if (listData.size()>10000){
                List res = averageAssign(listData,10);
                for (Object re : res) {
                    gxcinterfaceDao.insertvaluegh((List<Map<String, Object>>) re);
                }
            }else {
                gxcinterfaceDao.insertvaluegh(listData);
            }
            //写入动态货值
        }
        /**
        * 根据 确认版》定稿版》最新版 规则过滤一下数据
        * */
        //1.先查询出所有分期
        List<Map> list = gxcinterfaceDao.getStageList(null);
        for (Map map : list) {
            // 1.首先查询确认版
            String qrDate = gxcinterfaceDao.getQrDate(map.get("stage_id").toString());
            if(!StringUtil.isEmpty(qrDate)){
                // 确认版存在 删除其他版本
                gxcinterfaceDao.delQrOtherVersion(map.get("stage_id").toString(),qrDate);
            }else {
                String dgDate = gxcinterfaceDao.getDgDate(map.get("stage_id").toString());
                if(!StringUtil.isEmpty(dgDate)){
                    // 定稿版存在 删除其他版本
                    gxcinterfaceDao.delDgOtherVersion(map.get("stage_id").toString(),dgDate);
                }else {
                    gxcinterfaceDao.delNewOtjerVersion(map.get("stage_id").toString());
                }
            }
        }
        //更新数据条数
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setMessages("初始化供销存数据成功!");
        resultBody.setCode(200);
        return resultBody;
    }


    @Override
    public ResultBody insertReportValue(Map params) {
        //前二个月
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        Date m3 = c.getTime();
        String mon3 = format.format(m3);

        String sqls = "SELECT id,\n" +
                "region_id,\n" +
                "region_name,\n" +
                "project_id,\n" +
                "project_code,\n" +
                "project_name,\n" +
                "projectf_id,\n" +
                "projectf_name,\n" +
                "group_id,\n" +
                "group_name,\n" +
                "product_name,\n" +
                "product_code,\n" +
                "mini_granularity_name,\n" +
                "version_id,\n" +
                "version_name,\n" +
                "version_type,\n" +
                "version_num,\n" +
                "version_date,\n" +
                "end_date,\n" +
                "un_sale_stall_num,\n" +
                "un_sale_room_num,\n" +
                "un_sale_stall_price,\n" +
                "un_sale_room_price,building_id,is_parking FROM  v_sman_value_report where  end_date= '" + mon3 + "'";
        //查询供货视图数据
        List<Map<String, Object>> listData = jdbcTemplategxc.queryForList(sqls);
        if (listData != null && listData.size() > 0) {
            //根据时间删除
            gxcinterfaceDao.deleteReportValue(mon3);
            //写入动态货值
            gxcinterfaceDao.insertReportValue(listData);
        }
        //更新数据条数
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setMessages("初始化供销存数据成功!");
        resultBody.setCode(200);
        return resultBody;
    }


    //供货视图数据处理
    @Override
    //每月一号零点执行此方法
   // @Scheduled(cron="0 0 0 1 * ?")
    @Transactional
    public ResultBody insertvaluegh( ) {
        Map<Object, Object> resulMap = new HashMap<>();
        Integer initedNumber=0;
        Integer integer=0;
        long startTime=System.currentTimeMillis();   //获取开始时间
        //供销存数据查询sql语句
      /*  String sqls="SELECT cw,temp1.project_id,temp1.project_code ,temp1.project_name ,temp1.stage_id ,temp1.stage_name ,temp1.is_parking ,temp1.group_id ,temp1.group_name ,temp1.pro_product_code ,temp1.pro_product_type ,temp1.product_code ,temp1.product_name ,temp1.design_build_id ,temp1.bld_prd_id ,temp1.supply_date ,temp1.supply_date_actual,SUM(room_num) room_num ,sum(temp1.parking_num) parking_num,SUM(room_num_actual)room_num_actual,SUM(parking_num_actual) parking_num_actual\n" +
                ",SUM(sale_area) sale_area,SUM(area_actual) area_actual ,SUM(house_commodity_value)house_commodity_value ,SUM(commodity_value)commodity_value ,SUM(not_commodity_value)not_commodity_value ,SUM(house_commodity_value_actual)house_commodity_value_actual ,SUM(commodity_value_actual)commodity_value_actual ,SUM(not_commodity_value_actual)not_commodity_value_actual ,temp1.version_id,temp1.create_time,temp1.version_num,temp1.version_name,temp1.version_type\n" +
                "from (select case when is_parking = 1 then '可售车位' else house_package_name  end cw,a.*  from  v_sman_supply_plan  a)temp1 GROUP BY temp1.stage_id ,temp1.group_id,cw";*/
        //查询供销存不包含未推售车位的数据
        String sqls="SELECT * FROM  v_sman_supply_plan WHERE type != 'org_forsaleparking'";
        //查询供货视图数据
        List<Map<String, Object>> listData = jdbcTemplategxc.queryForList(sqls);
        if(listData!=null&&listData.size()>0){
            //更新数据条数
            integer = listData.size();
            //插入之前清空表
            initedNumber =  gxcinterfaceDao.deletegh();
            //将供销存查询的数据插入销管数据库
            gxcinterfaceDao.insertvaluegh(listData);
        }
        long endTime=System.currentTimeMillis(); //获取结束时间
        String message1="本次初始化共清除"+initedNumber+"条数据";
        String message2="本次初始化共更新"+integer+"条数据";
        String message3="本次数据更新耗时"+(endTime-startTime)/1000+"s";
        resulMap.put("message1",message1);
        resulMap.put("message2",message2);
        resulMap.put("message3",message3);
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setData(resulMap);
        resultBody.setMessages("初始化供销存数据成功!");
        resultBody.setCode(200);
        return resultBody;
    }

    //动态货值视图数据处理
    @Override
    @Transactional
    public ResultBody insertvaluedthz( ) {
        Map<Object, Object> resulMap = new HashMap<>();
        Integer initedNumber=0;
        Integer integer=0;
        long startTime=System.currentTimeMillis();   //获取开始时间
        //供销存数据查询sql语句
        String sqls="select *  from  v_sman_dynamic_value";
        //查询动态货值视图数据
        List<Map<String, Object>> listData = jdbcTemplategxc.queryForList(sqls);
        if(listData!=null&&listData.size()>0){
            //更新数据条数
            integer = listData.size();
            //插入之前清空动态货值表
            initedNumber =  gxcinterfaceDao.deletedthz();
            //将供销存查询的数据插入销管数据库
            gxcinterfaceDao.insertvaluedthz(listData);
        }
        long endTime=System.currentTimeMillis(); //获取结束时间
        String message1="本次初始化共清除"+initedNumber+"条数据";
        String message2="本次初始化共更新"+integer+"条数据";
        String message3="本次数据更新耗时"+(endTime-startTime)/1000+"s";
        resulMap.put("message1",message1);
        resulMap.put("message2",message2);
        resulMap.put("message3",message3);
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setData(resulMap);
        resultBody.setMessages("初始化供销存数据成功!");
        resultBody.setCode(200);
        return resultBody;

    }

    //战规货值视图数据处理
    @Override
    @Transactional
    public ResultBody insertvaluezghz() {
        Map<Object, Object> resulMap = new HashMap<>();
        Integer initedNumber=0;
        Integer integer=0;
        long startTime=System.currentTimeMillis();   //获取开始时间
        //供销存数据查询sql语句
        String sqls="select *  from  v_sman_plan_value";
        //查询战规货值视图数据
        List<Map<String, Object>> listData = jdbcTemplategxc.queryForList(sqls);
        if(listData!=null&&listData.size()>0){
            //更新数据条数
            integer = listData.size();
            //插入之前清空动态货值表
            initedNumber =  gxcinterfaceDao.deletezghz();
            //将供销存查询的数据插入销管数据库
            gxcinterfaceDao.insertvaluezghz(listData);
        }
        long endTime=System.currentTimeMillis(); //获取结束时间
        String message1="本次初始化共清除"+initedNumber+"条数据";
        String message2="本次初始化共更新"+integer+"条数据";
        String message3="本次数据更新耗时"+(endTime-startTime)/1000+"s";
        resulMap.put("message1",message1);
        resulMap.put("message2",message2);
        resulMap.put("message3",message3);
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setData(resulMap);
        resultBody.setMessages("初始化供销存数据成功!");
        resultBody.setCode(200);
        return resultBody;
    }

    //签约视图数据处理
    @Override
    //每月一号零点执行此方法
   // @Scheduled(cron="0 0 0 1 * ?")
    @Transactional
    public ResultBody insertvalueqy() {
        Map<Object, Object> resulMap = new HashMap<>();
        Integer initedNumber=0;
        Integer integer=0;
        long startTime=System.currentTimeMillis();   //获取开始时间
        //供销存数据查询sql语句
        String sqls="select *  from  v_sman_sign_plan";
        //查询签约视图数据
        List<Map<String, Object>> listData = jdbcTemplategxc.queryForList(sqls);
        if(listData!=null&&listData.size()>0){
            //更新数据条数
            integer = listData.size();
            //插入之前清空签约视图表
            initedNumber =  gxcinterfaceDao.deleteqy();
            //将供销存查询的数据插入销管数据库
            gxcinterfaceDao.insertvalueqy(listData);
        }
        long endTime=System.currentTimeMillis(); //获取结束时间
        String message1="本次初始化共清除"+initedNumber+"条数据";
        String message2="本次初始化共更新"+integer+"条数据";
        String message3="本次数据更新耗时"+(endTime-startTime)/1000+"s";
        resulMap.put("message1",message1);
        resulMap.put("message2",message2);
        resulMap.put("message3",message3);
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setData(resulMap);
        resultBody.setMessages("初始化供销存数据成功!");
        resultBody.setCode(200);
        return resultBody;
    }

     //明源与供销存面积段归集，修改库存可售，正常非车位的
     @Override
     public int updateAvailableStock() {
        int i = gxcinterfaceDao.updateAvailableStock();
        return i;
      }

    //明源与供销存面积段归集，修改库存可售，车位的
    @Override
    public int updateAvailableStockNotCar() {
        int i = gxcinterfaceDao.updateAvailableStockNotCar();
        return i;
    }



    public static List<Map<String, Object>> averageAssign(List<Map<String, Object>> source, int n) {
        List result = new ArrayList<>();
        //(先计算出余数)
        int remainder = source.size() % n;
        //然后是商
        int number = source.size() / n;
        //偏移量
        int offset = 0;
        for (int i = 0; i < n; i++) {
            List<Map<String, Object>> value;
            if (remainder > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remainder--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }

}
