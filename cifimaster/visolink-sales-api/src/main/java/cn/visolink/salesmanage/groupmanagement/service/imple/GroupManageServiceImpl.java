package cn.visolink.salesmanage.groupmanagement.service.imple;
import cn.visolink.common.security.utils.HttpRequestUtil;
import cn.visolink.salesmanage.fileimport.dao.ImportDao;
import cn.visolink.system.timelogs.bean.SysLog;
import cn.visolink.system.timelogs.dao.TimeLogsDao;
import cn.visolink.utils.DateUtil;
import cn.visolink.utils.UUID;
import cn.visolink.salesmanage.groupmanagement.dao.GroupManageDao;
import cn.visolink.salesmanage.groupmanagement.service.GroupManageService;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wjc
 */
@Service
public class GroupManageServiceImpl implements GroupManageService
{
    @Autowired
    private GroupManageDao groupManageDao;
    @Autowired
    private TimeLogsDao timeLogsDao;

    @Value("${prcDimProjGoal.httpIp}")
    private String prcDimProjGoal;

    @Value("${prcDimProjGoal.apikey}")
    private String prcDimProjapikey;
    /**
     *
     * 查询事业部
     *
     * @return
     */

    @Override
    //@Transactional(rollbackFor = Exception.class)
    public int getBusiness(Map map) {


        //查看月度计划表中是否存在这个年月日
        List<Map> list = groupManageDao.selectPlanMonth(map);
        if(list.size()>0){
            //    if(list.size()>99999){
            return 0;
        }else {
            String oneId = UUID.randomUUID().toString();
            SimpleDateFormat versions = new SimpleDateFormat("yyyyMMdd");
            String nowDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Date dt = new Date();
            String version = versions.format(dt);
            //添加月度计划表
            map.put("guId", oneId);
            String ymd = map.get("months").toString();
            //截取年份
            String year=ymd.substring(0,4);
            //截取月份
            String month=ymd.substring(5,7);
            map.put("planName", year + "年" + month + "月销售计划");
            groupManageDao.insertMonthPlan(map);
            //添加月度指标
            map.put("oneId",oneId);
            map.put("version",version);

            groupManageDao.deleteBasic(map);
            groupManageDao.deleteBasicIndex(map);
            groupManageDao.getBusiness(map);

            //初始化年度计划签约数据
            groupManageDao.initYearPlanSignData(map);

            //获取指定月份最后一天
            String endtime = DateUtil.getLastDayOfMonth(ymd)+ " 23:59:59";
            //获取指定月份第一天
            Calendar c = Calendar.getInstance();
            SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM");
            c.add(Calendar.MONTH, -1);
            String kc_date = simpleDateFormatDay.format(c.getTime());
            String  starttime= DateUtil.getFirstDay(ymd)+ " 00:00:00";
            map.put("startDate",starttime);
            map.put("endDate",endtime);
            map.put("kc_date",kc_date);
            //文博
            //---修改项目供货值、套数、版本
            groupManageDao.updateProjectValue(map);
            //---修改组团供货值
            groupManageDao.updateGroupcodeValue(map);
            //---修改分期供货值
            groupManageDao.updateInstallmentValue(map);
            //---修改业态供货值
            groupManageDao.updateProductValue(map);
            //---修改面地段供货值
            groupManageDao.updateHousePackageValue(map);
            //---修改区域供货值
            groupManageDao.updateAreaValue(map);
            //---修改集团供货值
            groupManageDao.updateGroupValue(map);
            /*//------
            //修改项目供货值
            groupManageDao.updateProjectSupply(map);
            //修改区域供货值
            groupManageDao.updateRegionSupply(map);
            //修改集团供货值
            groupManageDao.updateGroupSupply(map);
            //修改分期供货值
            groupManageDao.updateInstallmentSupply(map);
            //修改组团供货值
            groupManageDao.updateGroupcodeSupply(map);
            //修改业态供货值
            groupManageDao.updateProduceSupply(map);*/
            //修改区域前一个月平均成交率
            groupManageDao.updateRegionOneMonth(map);
            //修改区域前三个月平均成交率
            groupManageDao.updateRegionThreeMonth(map);
            //修改项目前一个月平均成交率
            groupManageDao.updateProjectOneMonth(map);
            //修改项目前三个月平均成交率
            groupManageDao.updateProjectThreeMonth(map);
            //修改集团前一个月平均成交率
            groupManageDao.updateGroupOneMonth(map);
            //修改集团前三个月平均成交率
            groupManageDao.updateGroupThreeMonth(map);
            //修改项目年度计划签约(无用表)
            //groupManageDao.updateProjectYearSign();
            //修改区域年度计划签约(无用表)
            groupManageDao.updateRegionYearSign(map);
            //修改集团年度计划签约(无用表)
            groupManageDao.updateGroupYearSign(map);
            //修改集团的明源数据
            groupManageDao.updateGroupmy(map);
            //修改区域的明源数据
            groupManageDao.updateRegionmy(map);
            //修改把值为空改为零
            groupManageDao.updateIsnull(map);

            //修改汇总数据
            groupManageDao.updateTotalPlan(map);

            //添加月度指标详情
            map.put("type",1);
            groupManageDao.insertMonthPlanIndex(map);
            map.put("type",2);
            groupManageDao.insertMonthPlanIndex(map);
            map.put("type",3);
            groupManageDao.insertMonthPlanIndex(map);

            SysLog sysLog=new SysLog();
            sysLog.setTaskName("月度计划！");
            sysLog.setStartTime(nowDate);
            sysLog.setNote("月度计划初始化数据完成");
            timeLogsDao.insertLogs(sysLog);

        }
        return 1;
    }
    /**
     * 查询月度计划表中是否存在这个月份
     * @param map
     * @return
     */
    @Override
    public List<Map> selectPlanMonth(Map map) {
        List<Map> result = groupManageDao.selectPlanMonth(map);
        return result;
    }

    /**
     * 集团月度计划查询
     * @param map
     * @return
     */
    @Override
    public List<Map> getGroupMonthPlan(Map map) {
        String months=map.get("months").toString();
        String year="1970";
        if(!months.equals(year)){
            map.put("months",months);
            List<Map> result = groupManageDao.getGroupMonthPlan(map);
            return result;
        }else{
            months="";
            map.put("months",months);
            List<Map> result = groupManageDao.getGroupMonthPlan(map);
            return result;
        }
    }

    /**
     * 添加月度计划
     *
     * @param map
     * @return
     */
    @Override
    public int insertMonthPlanBasis(Map map) {

        int result = groupManageDao.insertMonthPlanBasis(map);
        return result;
    }

    /**
     * 添加月度计划指标
     *
     * @param map
     * @return
     */
    @Override
    public int insertMonthPlanIndex(Map map) {

        int result = groupManageDao.insertMonthPlanIndex(map);
        return result;
    }

    /**
     * 查询集团月度计划的区域数据
     *
     * @param map
     * @return
     */
    @Override
    public List<Map> getGroupAllMessage(Map map) {
      // prcDimProjGoal="https://service.cifi.com.cn/datacollector/load/prcDimProjGoalMMy";
      // prcDimProjapikey="9c82bcfcc52390bf1346106f5d9f3c9f";
       String months= map.get("months")+"";
       /*截取到年月*/
        months=  months.substring(0,months.lastIndexOf("-"));
        /*换成符合要求的字符串*/
        months= months.replace("-","");
        String createTimeriskresult =HttpRequestUtil.httpGet(prcDimProjGoal+"?params="+months+"&apikey="+prcDimProjapikey,false);
        System.out.println(prcDimProjGoal+"?params="+months+"&apikey="+prcDimProjapikey+"-----prcDimProjapikey");
        Gson gson=new Gson();
        Map<String,Object> GsonMap=new HashMap();
        GsonMap=gson.fromJson((createTimeriskresult+""),GsonMap.getClass());
        System.out.println(GsonMap+"GsonMap");
            /*返回值*/
        List<Map> result = groupManageDao.getGroupAllMessage(map);
        /*得到所有来自数据湖的数据，然后遍历到返回值里*/
      List<Map> AllProject=(List<Map>)GsonMap.get("retData");
        DecimalFormat df = new DecimalFormat("0.00");
        String isBusinessId = MapUtils.getString(map,"bussinesId");
      if(result!=null && result.size()>0 && AllProject!=null && AllProject.size()>0  ){

        for(Map map1: result){
          String businessId= map1.get("business_id")+"";
            for(Map map2: AllProject){
                if (businessId.equals(map2.get("idmProjId") + "") && (map1.get("total_sign_funds") == null || Double.parseDouble(map1.get("total_sign_funds") + "") == 0) && isBusinessId == null){
                    map1.put("total_sign_funds",df.format(Double.parseDouble(map2.get("cntrtAmtGoalM")+"")/10000) );
                }
                if (businessId.equals(map2.get("idmProjId")+"")){
                map1.put("year_check_funds",map2.get("cntrtAmtBudgetY")==null?0:df.format(Double.parseDouble(map2.get("cntrtAmtBudgetY")+"")/10000) );
                map1.put("months_check_funds",map2.get("cntrtAmtBudgetMAccu")==null?0:df.format(Double.parseDouble(map2.get("cntrtAmtBudgetMAccu")+"")/10000) );

                map1.put("year_check_funds_per",map2.get("cntrtAmtBudgetY")==null || Double.parseDouble(map2.get("cntrtAmtBudgetY")+"")==0 ?0:df.format( Double.parseDouble(map1.get("year_grand_total_sign")+"")/(Double.parseDouble(map2.get("cntrtAmtBudgetY")+"")/10000)*100) );
                map1.put("months_check_funds_per",map2.get("cntrtAmtBudgetMAccu")==null || Double.parseDouble(map2.get("cntrtAmtBudgetMAccu")+"")==0  ?0:df.format( Double.parseDouble(map1.get("year_grand_total_sign")+"")/(Double.parseDouble(map2.get("cntrtAmtBudgetMAccu")+"")/10000)*100) );

                }
            }
        }
        for(Map map1: result){
            if((map1.get("type")+"").equals("2") ){
                Double region= 0.00;
                Double region_year_check_funds= 0.00;
                Double region_months_check_funds= 0.00;
                for(Map map2: result){
                if((map2.get("father_id")+"").equals(map1.get("business_unit_id")+"")){
                    Double Project= Double.parseDouble(map2.get("total_sign_funds")+"");
                    region_year_check_funds+=map2.get("year_check_funds")==null?0:Double.parseDouble(map2.get("year_check_funds")+"");
                    region_months_check_funds+=map2.get("months_check_funds")==null?0:Double.parseDouble(map2.get("months_check_funds")+"");
                    region+=Project;
                }
            }
                map1.put("total_sign_funds",df.format(region));
                map1.put("year_check_funds",df.format(region_year_check_funds));
                map1.put("months_check_funds",df.format(region_months_check_funds));
                map1.put("year_check_funds_per",region_year_check_funds==0?0:df.format( Double.parseDouble(map1.get("year_grand_total_sign")+"")/region_year_check_funds*100) );
                map1.put("months_check_funds_per",region_months_check_funds==0?0:df.format( Double.parseDouble(map1.get("year_grand_total_sign")+"")/region_months_check_funds*100) );

            }}

          for(Map map1: result){
              if((map1.get("type")+"").equals("1") ){
                  Double region= 0.00;
                  Double region_year_check_funds= 0.00;
                  Double region_months_check_funds= 0.00;
                  for(Map map2: result){
                      if((map2.get("father_id")+"").equals(map1.get("business_unit_id")+"")){
                          Double Project= Double.parseDouble(map2.get("total_sign_funds")+"");
                          region_year_check_funds+=Double.parseDouble(map2.get("year_check_funds")+"");
                          region_months_check_funds+=Double.parseDouble(map2.get("months_check_funds")+"");

                          region+=Project;
                      }
                  }
                  map1.put("total_sign_funds",df.format(region));
                  map1.put("year_check_funds",df.format(region_year_check_funds));
                  map1.put("months_check_funds",df.format(region_months_check_funds));
                  map1.put("year_check_funds_per",region_year_check_funds==0?0:df.format( Double.parseDouble(map1.get("year_grand_total_sign")+"")/region_year_check_funds*100) );
                  map1.put("months_check_funds_per",region_months_check_funds==0?0:df.format( Double.parseDouble(map1.get("year_grand_total_sign")+"")/region_months_check_funds*100) );

              }}



        }
        return result;
    }

    /**
     * 查询集团下子级的数据
     *
     * @param map
     * @return
     */
    @Override
    public List<Map> getGroupChildMessage(Map map) {
        List<Map> result = groupManageDao.getGroupChildMessage(map);
        return result;
    }

    /**
     * 暂存，下达，上报 （修改状态）
     *
     * @param map
     * @return
     */
    @Override
    public int updatePlanStatus(Map map) {

        Iteratormap((List<Map>) map.get("valuelist"));

        Integer preparedByLevel =Integer.parseInt(map.get("preparedByLevel").toString()) ;
        Integer planStatus =Integer.parseInt(map.get("planStatus").toString()) ;
        Integer levels=1;
        Integer level=2;
        Integer status=1;
        if(preparedByLevel.equals(level) && planStatus.equals(status)){
            //获取集团给区域下发的值
            int groupReleaseRegional = groupManageDao.getGroupReleaseRegional(map);
            //获取区域的合计值
            int regionalAggregate = groupManageDao.getRegionalAggregate(map);

                //修改为下发状态
                groupManageDao.updatePlanStatus(map);
                //修改项目（计划、签约、认购）的费用
                groupManageDao.updateProjectFunds(map);
                groupManageDao.updateIsEffevtive(map);
                return 1;


        }else if(preparedByLevel.equals(levels) && planStatus.equals(status)){
            //修改为下发状态
            groupManageDao.updatePlanStatus(map);
            ////修改区域（计划、签约、认购）的费用
            // groupManageDao.updateRegionFunds(map);
            // groupManageDao.updateIsEffevtive(map);
        }
        else{
            groupManageDao.updatePlanStatus(map);
            groupManageDao.updateIsEffevtive(map);
            return 1;
        }
        return 0;

    }

    /**
     * 区域月度计划查询
     * @param map
     * @return
     */
    @Override
    public List<Map> getRegionalMonthPlan(Map map) {
        String months=map.get("months").toString();
        String year="1970";
        if(!months.equals(year)){
            map.put("months",months);
            List<Map> result = groupManageDao.getRegionalMonthPlan(map);

            return result;
        }else{
            months="";
            map.put("months",months);
            List<Map> result = groupManageDao.getRegionalMonthPlan(map);

            return result;
        }

    }
    /**
     * 在区域里获取集团下达数据
     * @param map
     * @return
     */
    @Override
    public List<Map> getGroupReleaseInRegional(Map map) {
        if(map.get("regionOrgId")==null){
            map.put("type",1);
        }else{
            map.put("type",2);
        }
        List<Map> result = groupManageDao.getGroupReleaseInRegional(map);
        result= getDataLake(result,map.get("months")+"" );

        return result;
    }

    /**
     *根据区域id获取事业部数据
     * @param map
     * @return
     */
    @Override
    public List<Map> getBusinessForRegional(Map map) {
        int region = groupManageDao.selectRegionalMonth(map);
        if(region>0){
            return null;
        }else{
            List<Map> result = groupManageDao.getBusinessForRegional(map);
            SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
            Date dt=new Date();
            String version = format.format(dt);
            for (int i=0;i<result.size();i++){
                String uuid = UUID.randomUUID().toString();
                String type = (String) result.get(i).get("type").toString();
                String businessName = (String) result.get(i).get("business_name").toString();
                String businessId = (String) result.get(i).get("business_id").toString();
                String monthlyPlanBasisId = (String) result.get(i).get("monthlyPlanBasisId").toString();
                String monthlyPlanId = (String) result.get(i).get("monthlyPlanId").toString();

                map.put("guID",uuid);
                map.put("monthlyPlaniId",monthlyPlanId);
                map.put("monthlyPlanBasisId",monthlyPlanBasisId);
                map.put("preparedByUnitOrgId",businessId);
                map.put("preparedByLevel",type);
                map.put("preparedByLevelName",businessName);
                map.put("version",version);
                groupManageDao.insertRegionalMonthPlanIndex(map);
            }
            return result;
        }


    }
    /**
     *根据集团id获取事业部数据
     * @param map
     * @return
     */
    @Override
    public List<Map> getBusinessForProject(Map map) {

        List<Map> result = groupManageDao.getBusinessForProject(map);
        return result;
    }

    /**
     *添加区域月度计划指标
     * @param map
     * @return
     */
    @Override
    public int insertRegionalMonthPlanIndex(Map map) {
        int result= groupManageDao.insertRegionalMonthPlanIndex(map);
        return result;
    }
    /**
     *获取区域初始化数据
     * @param map
     * @return
     */
    @Override
    public List<Map> getRegionalMessage(Map map) {
        List<Map> result = groupManageDao.getRegionalMessage(map);
        result=  getDataLake(result,map.get("months")+"");
        return result;
    }
    /**
     * 判断区域月份是否存在
     * @param map
     * @return
     */
    @Override
    public int selectRegionalMonth(Map map) {

        int result = groupManageDao.selectRegionalMonth(map);
        return result;
    }
    /**
     * 判断项目月份是否存在
     * @param map
     * @return
     */
    @Override
    public int selectProjectMonth(Map map)
    {
        int result = groupManageDao.selectProjectMonth(map);
        return result;
    }

    /**
     * 区域指标细化
     * @param map
     * @return
     */
    @Override
    public List<Map> getRegionChildMessage(Map map) {
        List<Map> result = groupManageDao.getRegionChildMessage(map);
        return result;
    }

    /**
     * 项目月度计划查询
     * @param map
     * @return
     */
    @Override
    public List<Map> getProjectMonthPlan(Map map) {
        String months=map.get("months").toString();
        String report_time=map.get("report_time")+"";
        String toexamine_time=map.get("toexamine_time")+"";

        String year="1970";
        if (!"".equals(report_time) && !"null".equals(report_time)) {
            String[] replace = report_time.replace("[", "").replace("]", "").split(",");
            map.put("startTime1", replace[0]);
            map.put("endTime1", replace[1]);
            map.put("report_time",replace[0]);
        }else{
            map.remove("report_time");
        }
        if (!"".equals(toexamine_time) && !"null".equals(toexamine_time)) {
            String[] replace = toexamine_time.replace("[", "").replace("]", "").split(",");
            map.put("startTime2", replace[0]);
            map.put("endTime2", replace[1]);
            map.put("toexamine_time",replace[0]);
        }else{
            map.remove("toexamine_time");
        }
        if(!months.equals(year)){
            map.put("months",months);
            List<Map> result = groupManageDao.getProjectMonthPlan(map);
            return result;
        }else{
            months="";
            map.put("months",months);
            List<Map> result = groupManageDao.getProjectMonthPlan(map);
            return result;
        }

    }
    /**
     * 从项目月度计划获取区域下达数据
     * @param map
     * @return
     */
    @Override
    public List<Map> getRegionalReleaseInProject(Map map) {

        List<Map> result = groupManageDao.getRegionalReleaseInProject(map);
        result = getDataLake(result,map.get("months")+""  );
        return result;
    }

    /**
     *添加项目月度计划指标
     * @param map
     * @return
     */
    @Override
    public int insertProjectMonthPlanIndex(Map map) {
        int result=1;
        int project = groupManageDao.selectProjectMonth(map);
        if(project>0){
            return 0;
        }else{
            SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
            Date dt=new Date();
            String version = format.format(dt);
            List<Map> groupMessage = groupManageDao.getBusinessForProject(map);
            for (int i = 0; i < groupMessage.size(); i++) {
                String uuid = UUID.randomUUID().toString();
                String type = (String) groupMessage.get(i).get("type").toString();
                String businessName = (String) groupMessage.get(i).get("business_name").toString();
                String businessId = (String) groupMessage.get(i).get("business_id").toString();
                String monthlyPlanBasisId = (String) groupMessage.get(i).get("monthlyPlanBasisId").toString();
                String monthlyPlanId = (String) groupMessage.get(i).get("monthlyPlanId").toString();
                map.put("guID", uuid);
                map.put("monthlyPlaniId", monthlyPlanId);
                map.put("monthlyPlanBasisId", monthlyPlanBasisId);
                map.put("preparedByUnitOrgId", businessId);
                map.put("preparedByLevel", type);
                map.put("preparedByLevelName", businessName);
                map.put("version",version);
                groupManageDao.insertProjectMonthPlanIndex(map);
            }

            return result;
        }

    }

    /**
     * 获取项目初始化数据
     * @param map
     * @return
     */
    @Override
    public List<Map> getProjectMessage(Map map) {
        /*
         * 转换LONG为日期
         * */
        long data=0;
        if (map.get("months") != null && map.get("months").getClass().isInstance(data)) {
            long longtime=(long)map.get("months");


            String months=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longtime));

            map.put("months",months);
        }


        List<Map> result = groupManageDao.getProjectMessage(map);
        result=getDataLake(result,map.get("months")+"");
        return result ;
    }

    @Override
    public Map getProjectAreaID(String projectId) {
        return groupManageDao.getProjectAreaID(projectId);
    }

    /**
     * 查询区域下项目未上报的条数
     * @param map
     * @return
     */
    @Override
    public int getRegionUnderProject(Map map) {
        int regionUnderProject = groupManageDao.getRegionUnderProject(map);
        if(regionUnderProject>0){
            return 0;
        }else{
            return 1;
        }
    }

    /**
     * 获取集团下区域未上报的条数
     * @param map
     * @return
     */
    @Override
    public int getGroupUnderRegion(Map map) {

        int result = groupManageDao.getGroupUnderRegion(map);
        if(result>0){
            return result;
        }else{
            return 0;
        }
    }

    /**
     * 修改集团的状态
     * @param map
     * @return
     */
    @Override
    public int updateGroupPlanStatus(Map map) {
        int result = groupManageDao.updateGroupPlanStatus(map);
        return result;
    }

    /**
     * 修改区域的费用值
     * @param map
     * @return
     */
    @Override
    public int updateRegionFunds(Map map) {
        int result = groupManageDao.updateRegionFunds(map);
        return result;
    }

    /**
     * 修改项目的费用值
     * @param map
     * @return
     */
    @Override
    public int updateProjectFunds(Map map) {
        int result = groupManageDao.updateProjectFunds(map);
        return result;
    }
    /* 递归存数据*/
    public  void Iteratormap(List<Map> list){
        for(Map map1:list){
            Iterator iterable= map1.entrySet().iterator();
            while (iterable.hasNext()) {
                Map.Entry entry_d = (Map.Entry) iterable.next();
                Object key = entry_d.getKey();
                Object value = entry_d.getValue();
                if(value==null || value==""){
                    value=0;
                }
                map1.put(key.toString(),value);
            }
            groupManageDao.updateMonthlyPlanBasis(map1);
            if(map1.get("children")!=null){
                Iteratormap((List<Map>) map1.get("children"));
            }}

    }
    /*获取来自数据湖的数据*/
    public List<Map> getDataLake(List<Map> result,String  months) {

        /*截取到年月*/
        months = months.substring(0, months.lastIndexOf("-"));
        /*换成符合要求的字符串*/
        months = months.replace("-", "");
        String createTimeriskresult = HttpRequestUtil.httpGet(prcDimProjGoal + "?params=" + months + "&apikey=" + prcDimProjapikey, false);
        System.out.println(prcDimProjGoal + "?params=" + months + "&apikey=" + prcDimProjapikey + "-----prcDimProjapikey");
        Gson gson = new Gson();
        Map<String, Object> GsonMap = new HashMap();
        GsonMap = gson.fromJson((createTimeriskresult + ""), GsonMap.getClass());
        System.out.println(GsonMap + "GsonMap");

        /*得到所有来自数据湖的数据，然后遍历到返回值里*/
        List<Map> AllProject = (List<Map>) GsonMap.get("retData");
        DecimalFormat df = new DecimalFormat("0.00");
        if (result != null && result.size() > 0 && AllProject != null && AllProject.size() > 0) {

            for (Map map1 : result) {

                String businessId = map1.get("business_unit_id") + "";
                for (Map map2 : AllProject) {
                    if (businessId.equals(map2.get("idmProjId") + "")) {

                    map1.put("year_check_funds", map2.get("cntrtAmtBudgetY")==null?0:df.format(Double.parseDouble(map2.get("cntrtAmtBudgetY") + "") / 10000));
                    map1.put("months_check_funds", map2.get("cntrtAmtBudgetMAccu")==null?0:df.format(Double.parseDouble(map2.get("cntrtAmtBudgetMAccu") + "") / 10000));

                    map1.put("year_check_funds_per", map2.get("cntrtAmtBudgetY")==null || Double.parseDouble(map2.get("cntrtAmtBudgetY") + "")==0?0:df.format(Double.parseDouble(map1.get("year_grand_total_sign") + "") / (Double.parseDouble(map2.get("cntrtAmtBudgetY") + "") / 10000)*100));
                    map1.put("months_check_funds_per", map2.get("cntrtAmtBudgetMAccu")==null || Double.parseDouble(map2.get("cntrtAmtBudgetMAccu") + "")==0?0:df.format(Double.parseDouble(map1.get("year_grand_total_sign") + "") / (Double.parseDouble(map2.get("cntrtAmtBudgetMAccu") + "") / 10000)*100));
                    }

                } }
            for (Map map1 : result) {
                if ((map1.get("type") + "").equals("2")) {
                    Double region = 0.00;
                    Double region_year_check_funds = 0.00;
                    Double region_months_check_funds = 0.00;
                    for (Map map2 : result) {
                        if ((map2.get("father_id") + "").equals(map1.get("business_unit_id") + "")) {
                            Double Project =map2.get("total_sign_funds")==null?0: Double.parseDouble(map2.get("total_sign_funds") + "");
                            region_year_check_funds += map2.get("year_check_funds")==null?0:Double.parseDouble(map2.get("year_check_funds") + "");
                            region_months_check_funds +=map2.get("months_check_funds")==null?0: Double.parseDouble(map2.get("months_check_funds") + "");
                            region += Project;
                        }
                    }
                    map1.put("total_sign_funds", df.format(region));
                    map1.put("year_check_funds", df.format(region_year_check_funds));
                    map1.put("months_check_funds", df.format(region_months_check_funds));
                    map1.put("year_check_funds_per", region_year_check_funds==0?0:df.format(Double.parseDouble(map1.get("year_grand_total_sign") + "") / region_year_check_funds*100));
                    map1.put("months_check_funds_per", region_months_check_funds==0?0:df.format(Double.parseDouble(map1.get("year_grand_total_sign") + "") / region_months_check_funds*100));

                }
            }

            for (Map map1 : result) {
                if ((map1.get("type") + "").equals("1")) {
                    Double region = 0.00;
                    Double region_year_check_funds = 0.00;
                    Double region_months_check_funds = 0.00;
                    for (Map map2 : result) {
                        if ((map2.get("father_id") + "").equals(map1.get("business_unit_id") + "")) {
                            Double Project = Double.parseDouble(map2.get("total_sign_funds") + "");
                            region_year_check_funds +=map2.get("year_check_funds")==null?0: Double.parseDouble(map2.get("year_check_funds") + "");
                            region_months_check_funds +=map2.get("months_check_funds")==null?0: Double.parseDouble(map2.get("months_check_funds") + "");

                            region += Project;
                        }
                    }
                    map1.put("total_sign_funds", df.format(region));
                    map1.put("year_check_funds", df.format(region_year_check_funds));
                    map1.put("months_check_funds", df.format(region_months_check_funds));
                    map1.put("year_check_funds_per", region_year_check_funds==0?0:df.format(Double.parseDouble(map1.get("year_grand_total_sign") + "") / region_year_check_funds*100));
                    map1.put("months_check_funds_per", region_months_check_funds==0?0:df.format(Double.parseDouble(map1.get("year_grand_total_sign") + "") / region_months_check_funds*100));

                }
            }

        }
        return  result;
    }
}
