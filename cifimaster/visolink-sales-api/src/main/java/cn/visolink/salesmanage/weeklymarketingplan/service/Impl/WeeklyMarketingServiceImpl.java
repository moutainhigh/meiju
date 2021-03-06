package cn.visolink.salesmanage.weeklymarketingplan.service.Impl;

import cn.visolink.common.security.utils.HttpRequestUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.caopandata.service.CaoPanDataService;
import cn.visolink.salesmanage.datainterface.service.Datainterfaceservice;
import cn.visolink.salesmanage.datainterface.service.impl.DatainterfaceserviceImpl;
import cn.visolink.salesmanage.monthdetail.dao.MonthManagerMapper;
import cn.visolink.salesmanage.packageanddiscount.dao.PackageanddiscountDao;
import cn.visolink.salesmanage.packagedis.dao.PackageDiscountDao;
import cn.visolink.salesmanage.plandatainterface.service.PlanDataInterfaceservice;
import cn.visolink.salesmanage.pricing.dao.PricingMapper;
import cn.visolink.salesmanage.signdata.service.SingDataService;
import cn.visolink.salesmanage.weeklymarketingplan.dao.WeeklyMarketingDao;
import cn.visolink.salesmanage.weeklymarketingplan.model.WeekMarkting;
import cn.visolink.salesmanage.weeklymarketingplan.service.WeeklyMarketingService;
import cn.visolink.utils.Constant;
import cn.visolink.utils.StringUtil;
import com.google.gson.JsonObject;
import io.netty.handler.codec.json.JsonObjectDecoder;
import lombok.Data;
import lombok.val;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.wicket.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
@Service
public class WeeklyMarketingServiceImpl implements WeeklyMarketingService {
    @Autowired
    private WeeklyMarketingDao weeklyMarketingDao;

    @Autowired
    SingDataService singDataService;

    @Autowired
    PlanDataInterfaceservice datainterfaceservice;
    @Autowired
    Datainterfaceservice mingyuanservice;

    @Autowired
    private PackageDiscountDao packageDiscountDao;

    @Autowired
    Datainterfaceservice datainterfaceservices;

    @Autowired
    private PackageanddiscountDao packageanddiscountDao;

    @Autowired
    MonthManagerMapper manager;

    @Value("${uploadPath}")
    private  String uplodepath;

    @Value("${relepath}")
    private  String relepath;

    @Autowired
    private PricingMapper pricingMapper;

    private int rowNum;

    @Autowired
    private CaoPanDataService caoPanDataService;

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    @Override
    public void weekMarketingPlanInitial(){

        DecimalFormat df=new DecimalFormat("0.0000");
        /*
         * ??????????????????????????????????????????????????????????????????????????????
         * */
       Map timemap= weeklyMarketingDao.weekMarketingWeekRuleReal();

     //   Map timemap= weeklyMarketingDao.weekMarketingWeekRule();


      if(timemap!=null && timemap.size()>0){
            /*
             * ????????????????????????
             * */
            List<Map> projectMap= weeklyMarketingDao.weekMarketingbusinessName();
            /*
            * ????????????????????????
            * */
            Map<String,Object> GroupMap= weeklyMarketingDao.weekMarketingGroupName();
            /*
             * ??????ID,???????????????????????????
             * */
            String  groupId =GroupMap.get("business_id").toString();
            String  groupName =GroupMap.get("business_name").toString();
            String  groupType=GroupMap.get("type").toString();

            /*
             * ?????????????????????????????????
             * */
            String months=timemap.get("end_time").toString();

            Integer trimyear=Integer.parseInt(months.substring(0,months.indexOf("-")));

            Integer  trimthree=Integer.parseInt(months.substring(months.indexOf("-")+1,months.indexOf("-")+3));

            /*
            ?????????????????????????????????1??????
            */
            Integer nowyearone=trimyear;
            Integer nowmonthone=trimthree-1;
            if(nowmonthone<1){
                nowmonthone=(12+trimthree)-1;
                nowyearone--;
            }
            /*
             * ????????????????????????????????????????????????
             * */
            String startWeek=timemap.get("start_time").toString();
            String endWeek=timemap.get("end_time").toString();
            Integer howWeek=Integer.parseInt(timemap.get("how_week").toString()) ;
        Integer planWeek=Integer.parseInt(timemap.get("plan_week").toString()) ;

          /*???????????????????????????????????????*/
          boolean flag=false;
          Map deleteMap=new HashMap();
          deleteMap.put("how_week",howWeek);
          deleteMap.put("this_time",endWeek);
       List<Map> deletebefore=   weeklyMarketingDao.deleteBeforeInsert(deleteMap);
                if(deletebefore.size()>0 && deletebefore!=null){
                    flag=true;
                }


            /*
            ?????????????????????????????????
            */
            String foremonthsbegan=nowyearone+"-"+(nowmonthone<10? "0"+nowmonthone:nowmonthone)+"-01";
            String foremonthsend=nowyearone+"-"+(nowmonthone<10? "0"+nowmonthone:nowmonthone)+"-31";

            /*
             * ??????????????????
             * */
            Integer areaTargetMonthBearer=0;
            Double  areaTargetMonthSign=0.00;

            Integer areaTargetWeekBearer = 0;
            Double areaTargetWeekSign =0.00;

            Double areaSubscribePrice=0.00;

            Double areaTargetWeekBearerPer=0.00;

            Double areaTargetWeekSignPer=0.00;

            int areaFactMonthBearerTotal=0;

            Double areaFactMonthBearerPer=0.00;

            Integer areaFactWeekBearer=0;

            Double areaFactWeekBearerPer=0.00;

            Double areaFactMonthSign=0.00;

            Double areaFactMonthSignPer=0.00;

            Double areaFactWeekSign=0.00;

            Double areaFactWeekSignPer=0.00;

            Double areaFactSigned=0.00;

            Double areaPlanWeekBearerGap=0.00;

            Double areaPlanWeekSignGap=0.00;


            /*
             * ???????????????
             * */
            Integer groupTargetMonthBearer=0;
            Double  groupTargetMonthSign=0.00;

            Integer groupTargetWeekBearer = 0;
            Double groupTargetWeekSign =0.00;

            Double groupSubscribePrice=0.00;

            Double groupTargetWeekBearerPer=0.00;

            Double groupTargetWeekSignPer=0.00;

            int groupFactMonthBearerTotal=0;

            Double groupFactMonthBearerPer=0.00;

            Integer groupFactWeekBearer=0;

            Double groupFactWeekBearerPer=0.00;

            Double groupFactMonthSign=0.00;

            Double groupFactMonthSignPer=0.00;

            Double groupFactWeekSign=0.00;

            Double groupFactWeekSignPer=0.00;

            Double groupFactSigned=0.00;

            Double groupPlanWeekBearerGap=0.00;

            Double groupPlanWeekSignGap=0.00;

          String month="";
          if(trimthree<10){
              month="0"+trimthree;
          }else{
              month=trimthree+"";
          }
          /*
           * ????????????????????????
           * */
          String currentmonths=trimyear+"-"+month+"-01";
          /*
           * ??????????????????????????????????????????
           * */
          List<Map> subscribePriceMapTotal=WeeklyPlanRoomTotal(currentmonths,endWeek,2);

           Map<String,Object> datainterMap=new HashMap<>();

           datainterMap.put("months",trimyear+"-"+month);

          datainterMap.put("weekSerialNumber",planWeek);

          datainterMap.put("how_week",howWeek);


          Map datainterTotal=   datainterfaceservice.insertMonthPlan(datainterMap);

          /**
           * ?????????????????????????????????????????????
           *-- ???????????? ??????????????????-- ???????????? -- ??????????????????
           */
          List<Map>  targetMonthBearerMapTotal=(List<Map>) datainterTotal.get("monthData");
            /*?????????LIST?????????????????????*/
          List<Map>  targetMonthAgentTotal= weeklyMarketingDao.selectBasicTrader();
          /**
           * ?????????????????????????????????????????????
           *??????????????????
           * ?????????????????????
           */

          List<Map>  targetWeekBearerMapTotal=(List<Map>) datainterTotal.get("weekData");


         /*

          ???????????????????????????????????????????????????????????????????????????????????????-->
           */
            Map<String,Object> singMap=new HashMap<>();
          singMap.put("startTime",startWeek);
          singMap.put("endTime",endWeek);
              Map<String,Object> SignMapTotal= singDataService.getSingMoneyData(singMap);



          /*
           * ?????????????????????????????????????????????????????????
           * */
          List<Map> factMonthSignMapTotal=(List<Map>) SignMapTotal.get("monthData");


          /*
           * ??????????????????????????????-->
           * */
          List<Map> factWeekSignMapTotal= (List<Map>) SignMapTotal.get("weekData");
          /**
           *  ??????????????????-
           *
           */
          List<Map>  factWeekBearerMapTotal=(List<Map>) SignMapTotal.get("vistiCountDataWeek");
          /**
           *  -???????????????????????????????????????????????????
           *
           */
          List<Map>  factMonthBearerTotalMapTotal=(List<Map>) SignMapTotal.get("vistiCountDataMonth");


          /*
           * ???????????????????????????
           * */

          List<Map> factSignMapTotal=WeeklyPlanRoomTotal(currentmonths,endWeek,1);


          /*???????????????????????????????????????????????????????????????????????????????????????,??????????????????????????????????????????????????????
           * ?????????????????????????????????*/
          Boolean b = true;
          if(b){
              /**
               * ?????????????????????????????????????????????
               *-- ???????????? ??????????????????-- ???????????? -- ??????????????????
               */
              targetMonthBearerMapTotal=WeeklyPlanMonthsTarget(currentmonths);
              /**
               * ?????????????????????????????????????????????
               *??????????????????
               * ?????????????????????
               */
              targetWeekBearerMapTotal=WeeklyPlanWeekklyTarget(currentmonths,planWeek);

              Date date=new Date();
              SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

              /*
               * ?????????????????????????????????????????????????????????
               * */
           //   factMonthSignMapTotal=WeeklyPlanRmbTotal(currentmonths,endWeek);


              /*
               * ??????????????????????????????-->
               * */
           //   factWeekSignMapTotal= WeeklyPlanRmbTotal(startWeek,endWeek);
              /**
               *  -???????????????????????????????????????????????????
               *
               */

            //  factMonthBearerTotalMapTotal=WeeklyPlanCnt(currentmonths,endWeek);
              /**
               *  -???????????????????????????????????????????????????
               *
               */
             // factWeekBearerMapTotal=WeeklyPlanCnt(startWeek,endWeek);
          }


          for(int i=0;i<projectMap.size();i++) {
              if(flag==true){
                for(Map deleteBefore: deletebefore){
                    if(deleteBefore.get("project_id").toString().equals(projectMap.get(i).get("business_id").toString())){
                        projectMap.get(i).put("inhere",true);
                    }
                }}
              /*
               * ??????ID,???????????????????????????
               * */
              String areaIdfather = groupId;
              String areaId = projectMap.get(i).get("fatherid").toString();
              String areaName = projectMap.get(i).get("fathername").toString();
              String areaType = projectMap.get(i).get("fathertype").toString();

              /*
               * ??????ID??????????????????????????????
               * */
              String projectId = projectMap.get(i).get("business_id").toString();
              String projectName = projectMap.get(i).get("business_name").toString();
              String projectType = projectMap.get(i).get("type").toString();
              String projectCode = projectMap.get(i).get("project_code").toString();

              /*???????????????????????????????????????????????????????????????????????????*/

              Map targetMonthBAgent = null;
              if (targetMonthAgentTotal != null && targetMonthAgentTotal.size() > 0) {
                  for (Map maptargetMonthBearerMap : targetMonthAgentTotal) {
                      if (maptargetMonthBearerMap.get("project_id").equals(projectId)) {
                          targetMonthBAgent = maptargetMonthBearerMap;
                          break;
                      }
                  }
              }
              /*???????????????*/
              String trader = null;
              String lateTrader = null;
              String isTrader = null;
              if (targetMonthBAgent != null) {
                  trader = targetMonthBAgent.get("trader") == null ? null : targetMonthBAgent.get("trader") + "";//?????????
                  lateTrader = targetMonthBAgent.get("is_late_trader") == null ? null : targetMonthBAgent.get("is_late_trader") + "";//????????????0-???1-???
                  isTrader = targetMonthBAgent.get("is_trader") == null ? null : targetMonthBAgent.get("is_trader") + "";//????????????0-???1-???
              }

              /*
               * ??????????????????????????????????????????
               * */
              Map subscribePriceMap = null;
              if (subscribePriceMapTotal != null && subscribePriceMapTotal.size() > 0) {
                  for (Map mapsubscribePriceMapTotal : subscribePriceMapTotal) {
                      if (mapsubscribePriceMapTotal.get("project_id").equals(projectId)) {
                          subscribePriceMap = mapsubscribePriceMapTotal;
                          break;
                      }
                  }
              }
              Double subscribePrice = 0.00;
              if (subscribePriceMap != null && subscribePriceMap.size() > 0) {
                  subscribePrice = Double.parseDouble(subscribePriceMap.get("cjRoomTotal").toString());
              }
              /**
               * ?????????????????????????????????????????????
               *-- ???????????? ??????????????????-- ???????????? -- ??????????????????
               */
              Map targetMonthBearerMap = null;
              if (targetMonthBearerMapTotal != null && targetMonthBearerMapTotal.size() > 0) {
                  for (Map maptargetMonthBearerMap : targetMonthBearerMapTotal) {
                      if (maptargetMonthBearerMap.get("project_id").equals(projectCode)) {
                          targetMonthBearerMap = maptargetMonthBearerMap;
                          break;
                      }
                  }
              }


              /**
               * ?????????????????????????????????????????????
               *-- ???????????? ??????????????????-- ???????????? -- ??????????????????
               */
              Integer targetMonthBearer = 0;
              Double targetMonthSign = 0.00;
              if (targetMonthBearerMap != null && targetMonthBearerMap.size() > 0) {
                  targetMonthBearer = Integer.parseInt(targetMonthBearerMap.get("come_client_quantity").toString());
                  targetMonthSign = Double.parseDouble(targetMonthBearerMap.get("total_sign_funds").toString());

              }

                /**
                 * ?????????????????????????????????????????????
                 *??????????????????
                 * ?????????????????????
                 */
                Map<String,Object>  targetWeekBearerMap= null;
              if(targetWeekBearerMapTotal!=null && targetWeekBearerMapTotal.size()>0) {
              for(Map maptargetWeekBearerMap:targetWeekBearerMapTotal ){
                  if(maptargetWeekBearerMap.get("project_id").equals(projectCode)){
                      targetWeekBearerMap=maptargetWeekBearerMap;
                      break;
                  }
              }}
                Integer targetWeekBearer=0;
                Double   targetWeekSign=0.00;
                if(targetWeekBearerMap!=null && targetWeekBearerMap.size()>0){
                    targetWeekBearer=Integer.parseInt(targetWeekBearerMap.get("visit_quantity").toString());
                    targetWeekSign=Double.parseDouble(targetWeekBearerMap.get("sign_target").toString());

                }

           /*
                 * ???????????????
                 * */
                Double targetWeekBearerPer=0.00;

               if(targetWeekBearer!=0 && targetMonthBearer!=0 ){
                    targetWeekBearerPer=(double)targetWeekBearer/targetMonthBearer;

                }

                /*
                 * ????????????
                 * */

                Double targetWeekSignPer=0.00;
              if(targetWeekSign!=0.00 && targetMonthSign!=0.00 ){
                    targetWeekSignPer=(double)targetWeekSign/targetMonthSign;
                }


                /**
                 *  -???????????????????????????????????????????????????
                 *
                 */

              Map<String,Object>  factMonthBearerTotalMap= null;
              if(factMonthBearerTotalMapTotal!=null && factMonthBearerTotalMapTotal.size()>0) {
              for(Map maptargetWeekBearerMap:factMonthBearerTotalMapTotal ){
                  if(maptargetWeekBearerMap.get("project_id").equals(projectId)){
                      factMonthBearerTotalMap=maptargetWeekBearerMap;
                      break;
                  }
              }}
                Integer factMonthBearerTotal=0;
                if(factMonthBearerTotalMap!=null && factMonthBearerTotalMap.size()>0 ){

                        factMonthBearerTotal = Integer.parseInt(factMonthBearerTotalMap.get("visitCount").toString());

                }
                /*
                 * ?????????????????????
                 * */

                Double factMonthBearerPer=0.00;
                if(factMonthBearerTotal!=0 && targetMonthBearer!=0 ){
                    factMonthBearerPer=(double) factMonthBearerTotal/targetMonthBearer;
                }
                /**
                 *  ??????????????????-
                 *
                 */
             Map<String,Object>  factWeekBearerMap= null;
              if(factWeekBearerMapTotal!=null && factWeekBearerMapTotal.size()>0) {

                  for (Map maptargetWeekBearerMap : factWeekBearerMapTotal) {
                      if (maptargetWeekBearerMap.get("project_id").equals(projectId)) {
                          factWeekBearerMap = maptargetWeekBearerMap;
                          break;
                      }
                  }
              }
                Integer factWeekBearer=0;

                if(factWeekBearerMap!=null && factWeekBearerMap.size()>0 ){
                    factWeekBearer=Integer.parseInt(factWeekBearerMap.get("visitCount").toString()) ;
                }



                /*
                 * ?????????????????????
                 * */

                Double factWeekBearerPer=0.00;
                if(factWeekBearer!=0 && targetWeekBearer!=0 ){
                    factWeekBearerPer=(double)factWeekBearer/targetWeekBearer;
                }

                /*
                 * ?????????????????????????????????????????????????????????
                 * */
                Map<String,Object> factMonthSignMap=null;
              if(factMonthSignMapTotal!=null && factMonthSignMapTotal.size()>0) {

                  for (Map maptargetWeekBearerMap : factMonthSignMapTotal) {
                      if (maptargetWeekBearerMap.get("project_id").equals(projectId)) {
                          factMonthSignMap = maptargetWeekBearerMap;
                          break;
                      }
                  }
              }

                Double  factMonthSign=0.00;

                if(factMonthSignMap!=null && factMonthSignMap.size()>0 ){
                    factMonthSign=Double.parseDouble(factMonthSignMap.get("cjRmbTotal").toString()) ;
                }

                /*
                 * ?????????????????????
                 * */
                Double factMonthSignPer=0.00;

                if(factMonthSign!=0.00 && targetMonthSign!=0.00 ){
                    factMonthSignPer=(double) factMonthSign/targetMonthSign;
                }

                /*
                 * ??????????????????????????????-->
                 * */
              Map<String,Object> factWeekSignMap=null;
              if(factWeekSignMapTotal!=null && factWeekSignMapTotal.size()>0) {

                  for(Map maptargetWeekBearerMap:factWeekSignMapTotal ){
                  if(maptargetWeekBearerMap.get("project_id").equals(projectId)){
                      factWeekSignMap=maptargetWeekBearerMap;
                      break;
                  }
              } } Double factWeekSign=0.00;


                if(factWeekSignMap!=null && factWeekSignMap.size()>0 ){
                    factWeekSign=Double.parseDouble(factWeekSignMap.get("cjRmbTotal").toString()) ;
                }
                /*
                 *
                 *?????????????????????
                 */
                Double factWeekSignPer=0.00;

                if(factWeekSign!=0.00 && targetWeekSign!=0.00 ){
                    factWeekSignPer=factWeekSign/targetWeekSign;
                }
                /*
                 * ???????????????????????????
                 * */
              Map<String,Object> factSignMap=null;
              if(factSignMapTotal!=null && factSignMapTotal.size()>0) {
              for(Map maptargetWeekBearerMap:factSignMapTotal ){
                  if(maptargetWeekBearerMap.get("project_id").equals(projectId)){
                      factSignMap=maptargetWeekBearerMap;
                      break;
                  }
              } } Double factSigned=0.00;
                if(factSignMap!=null && factSignMap.size()>0 ){
                    factSigned=Double.parseDouble(factSignMap.get("cjRoomTotal").toString()) ;
                }

                /*
                 * ??????????????????
                 * */
                Double   planWeekBearerGap=(double)targetWeekBearer-factWeekBearer;

                /*
                 * ??????????????????
                 * */
                Double  planWeekSignGap=targetWeekSign-factWeekSign;

                /*
                * ??????????????????????????????????????????1???????????????0?????????
                * */
                int is_self_write=0;
              if(targetMonthSign==null || targetMonthSign==0)
              {
                  is_self_write=1;
              }
            if(is_self_write==1){
              if(targetWeekSign!=null && targetWeekSign!=0  )
              {
                  is_self_write=0;
              }}


                /*
                 * ???????????????Map??????????????????
                 * */
                Map<String,Object> WeeklyMarktingPara=new HashMap<>();


                WeeklyMarktingPara.put("project_id",projectId);
                WeeklyMarktingPara.put("project_code",projectCode);
                WeeklyMarktingPara.put("project_name",projectName);
              WeeklyMarktingPara.put("trader_id",0);
              WeeklyMarktingPara.put("trader_name",trader);
              WeeklyMarktingPara.put("islast",lateTrader);
              WeeklyMarktingPara.put("issales_trade",isTrader);
                WeeklyMarktingPara.put("subscribe_price",subscribePrice);
                WeeklyMarktingPara.put("target_month_bearer",targetMonthBearer);
                WeeklyMarktingPara.put("target_month_sign",targetMonthSign);
                WeeklyMarktingPara.put("target_week_bearer",targetWeekBearer);
                WeeklyMarktingPara.put("target_week_sign",targetWeekSign);

                WeeklyMarktingPara.put("target_week_bearer_per",df.format(targetWeekBearerPer) );
               WeeklyMarktingPara.put("target_week_sign_per",df.format(targetWeekSignPer));

                WeeklyMarktingPara.put("fact_month_bearer_total",factMonthBearerTotal);
                WeeklyMarktingPara.put("fact_month_bearer_per",df.format(factMonthBearerPer));
                WeeklyMarktingPara.put("fact_week_bearer",factWeekBearer);
                WeeklyMarktingPara.put("fact_week_bearer_per",df.format(factWeekBearerPer));
                WeeklyMarktingPara.put("fact_month_sign",factMonthSign);
                WeeklyMarktingPara.put("fact_month_sign_per",df.format(factMonthSignPer));
                WeeklyMarktingPara.put("fact_week_sign",factWeekSign);
                WeeklyMarktingPara.put("fact_week_sign_per",df.format(factWeekSignPer));
                WeeklyMarktingPara.put("fact_signed",factSigned);
                WeeklyMarktingPara.put("plan_reserve",0);
                WeeklyMarktingPara.put("plan_signed",0);
                WeeklyMarktingPara.put("plan_month_lock_price",0);
                WeeklyMarktingPara.put("plan_month_lock_per",0);
                WeeklyMarktingPara.put("plan_month_newsign",0);
                WeeklyMarktingPara.put("plan_month_sign",0);
                WeeklyMarktingPara.put("plan_week_bearer_gap",planWeekBearerGap);
                WeeklyMarktingPara.put("plan_week_sign_gap",planWeekSignGap);
                WeeklyMarktingPara.put("plan_month_sign_gap",0);
                WeeklyMarktingPara.put("gap_cause",null);
                WeeklyMarktingPara.put("is_effective",0);
                WeeklyMarktingPara.put("this_time",months);
                WeeklyMarktingPara.put("how_week",howWeek);
                WeeklyMarktingPara.put("start_time",startWeek);
                WeeklyMarktingPara.put("end_time",endWeek);
                WeeklyMarktingPara.put("create_time",null);
                WeeklyMarktingPara.put("creator",0);
                WeeklyMarktingPara.put("editor",0);
                WeeklyMarktingPara.put("update_time",null);
                WeeklyMarktingPara.put("area_id",areaId);
                WeeklyMarktingPara.put("plan_status",0);
                  WeeklyMarktingPara.put("type",projectType);
              WeeklyMarktingPara.put("checkeds",0);
              WeeklyMarktingPara.put("is_self_write",is_self_write);
              if(projectMap.get(i).get("inhere")!=null){
                  weeklyMarketingDao.updateBeforeInsert(WeeklyMarktingPara);
              }else {
                weeklyMarketingDao.weekMarketingPlanInitial(WeeklyMarktingPara);}
                /*
                 * ?????????????????????
                 * ??????????????????????????????ID??????????????????
                 * ????????????????????????????????????????????????????????????????????????????????????
                 * */

                /*Double areaTargetWeekBearer=0.00;
                    Double areaTargetWeekSign=0.00;*/

                areaTargetMonthBearer+=targetMonthBearer;

               areaTargetMonthSign+=targetMonthSign;

                 areaTargetWeekBearer+=targetWeekBearer;
                   areaTargetWeekSign+=targetWeekSign;

                areaSubscribePrice+=subscribePrice;

                areaFactMonthBearerTotal+=factMonthBearerTotal;

                areaFactWeekBearer+=factWeekBearer;

                areaFactMonthSign+=factMonthSign;

                areaFactWeekSign+=factWeekSign;

                areaFactSigned+=factSigned;

                areaPlanWeekBearerGap+=planWeekBearerGap;

                areaPlanWeekSignGap+=planWeekSignGap;

                /*
                 * ??????????????????ID?????????????????????ID??????????????????ID??????????????????????????????????????????????????????????????????????????????
                 * ????????????
                 *????????????????????????????????????????????????????????????????????????????????????
                 * */
                if((i+1)<projectMap.size() ){
                    if(!(areaId.equals(projectMap.get(i+1).get("fatherid").toString()))){
                        if(areaTargetMonthBearer!=0){
                            areaTargetWeekBearerPer=(double)areaTargetWeekBearer/areaTargetMonthBearer;
                        }

                        if(areaTargetMonthSign!=0.00){
                            areaTargetWeekSignPer=(double)areaTargetWeekSign/areaTargetMonthSign;  }


                        if(areaTargetMonthBearer!=0){
                            areaFactMonthBearerPer =(double) areaFactMonthBearerTotal/areaTargetMonthBearer;

                        }

                        if(areaTargetWeekBearer!=0){
                            areaFactWeekBearerPer=(double)areaFactWeekBearer/areaTargetWeekBearer;
                        }
                        if(areaTargetMonthSign!=0.00){
                            areaFactMonthSignPer =(double) areaFactMonthSign/areaTargetMonthSign;;
                        }
                        if(areaTargetWeekSign!=0.00){
                            areaFactWeekSignPer = (double)areaFactWeekSign/areaTargetWeekSign;
                        }




                        WeeklyMarktingPara.put("project_id",areaId);
                        WeeklyMarktingPara.put("project_code",null);
                        WeeklyMarktingPara.put("project_name",areaName);
                        WeeklyMarktingPara.put("trader_id",null);
                        WeeklyMarktingPara.put("trader_name",null);
                        WeeklyMarktingPara.put("islast",null);
                        WeeklyMarktingPara.put("issales_trade",null);

                        WeeklyMarktingPara.put("subscribe_price",areaSubscribePrice);
                        WeeklyMarktingPara.put("target_month_bearer",areaTargetMonthBearer);
                        WeeklyMarktingPara.put("target_month_sign",areaTargetMonthSign);
                        WeeklyMarktingPara.put("target_week_bearer",areaTargetWeekBearer);
                        WeeklyMarktingPara.put("target_week_sign",areaTargetWeekSign);

                        WeeklyMarktingPara.put("target_week_bearer_per",df.format(areaTargetWeekBearerPer) );
                        WeeklyMarktingPara.put("target_week_sign_per",df.format(areaTargetWeekSignPer));

                        WeeklyMarktingPara.put("fact_month_bearer_total",areaFactMonthBearerTotal);
                        WeeklyMarktingPara.put("fact_month_bearer_per",df.format(areaFactMonthBearerPer));
                        WeeklyMarktingPara.put("fact_week_bearer",areaFactWeekBearer);
                        WeeklyMarktingPara.put("fact_week_bearer_per",df.format(areaFactWeekBearerPer));
                        WeeklyMarktingPara.put("fact_month_sign",areaFactMonthSign);
                        WeeklyMarktingPara.put("fact_month_sign_per",df.format(areaFactMonthSignPer));
                        WeeklyMarktingPara.put("fact_week_sign",areaFactWeekSign);
                        WeeklyMarktingPara.put("fact_week_sign_per",df.format(areaFactWeekSignPer));
                        WeeklyMarktingPara.put("fact_signed",areaFactSigned);
                        WeeklyMarktingPara.put("plan_reserve",0);
                        WeeklyMarktingPara.put("plan_signed",0);
                        WeeklyMarktingPara.put("plan_month_lock_price",0);
                        WeeklyMarktingPara.put("plan_month_lock_per",0);
                        WeeklyMarktingPara.put("plan_month_newsign",0);
                        WeeklyMarktingPara.put("plan_month_sign",0);
                        WeeklyMarktingPara.put("plan_week_bearer_gap",areaPlanWeekBearerGap);
                        WeeklyMarktingPara.put("plan_week_sign_gap",areaPlanWeekSignGap);
                        WeeklyMarktingPara.put("plan_month_sign_gap",0);
                        WeeklyMarktingPara.put("gap_cause",null);
                        WeeklyMarktingPara.put("is_effective",0);
                        WeeklyMarktingPara.put("this_time",months);
                        WeeklyMarktingPara.put("how_week",howWeek);
                        WeeklyMarktingPara.put("start_time",startWeek);
                        WeeklyMarktingPara.put("end_time",endWeek);
                        WeeklyMarktingPara.put("create_time",null);
                        WeeklyMarktingPara.put("creator",0);
                        WeeklyMarktingPara.put("editor",0);
                        WeeklyMarktingPara.put("update_time",null);
                        WeeklyMarktingPara.put("area_id",areaIdfather);
                        WeeklyMarktingPara.put("plan_status",0);
                        WeeklyMarktingPara.put("type",areaType);
                        WeeklyMarktingPara.put("checkeds",0);
                        WeeklyMarktingPara.put("is_self_write",0);
                        if(flag==true){
                            weeklyMarketingDao.updateBeforeInsert(WeeklyMarktingPara);
                        }else {
                        weeklyMarketingDao.weekMarketingPlanInitial(WeeklyMarktingPara);}


                        /*
                         * ?????????????????????????????????????????????
                         * */
                        groupTargetMonthBearer+=areaTargetMonthBearer;
                        groupTargetMonthSign+=areaTargetMonthSign;

                        groupTargetWeekBearer+=areaTargetWeekBearer;
                        groupTargetWeekSign+=areaTargetWeekSign;

                        groupSubscribePrice+=areaSubscribePrice;

                        groupFactMonthBearerTotal+=areaFactMonthBearerTotal;

                        groupFactWeekBearer+=areaFactWeekBearer;

                        groupFactMonthSign+=areaFactMonthSign;

                        groupFactWeekSign+=areaFactWeekSign;

                        groupFactSigned+=areaFactSigned;

                        groupPlanWeekBearerGap+=areaPlanWeekBearerGap;

                        groupPlanWeekSignGap+=areaPlanWeekSignGap;


                        /*
                         * ????????????????????????????????????0
                         * */
                        areaSubscribePrice=0.00;

                        areaTargetWeekBearerPer=0.00;

                        areaTargetWeekSignPer=0.00;
                        areaTargetMonthSign=0.00;
                        areaFactMonthBearerTotal=0;
                        areaTargetMonthBearer=0;
                        areaFactMonthBearerPer=0.00;
                        areaTargetWeekBearer=0;
                        areaFactWeekBearer=0;
                        areaTargetWeekSign=0.00;
                        areaFactWeekBearerPer=0.00;

                        areaFactMonthSign=0.00;

                        areaFactMonthSignPer=0.00;

                        areaFactWeekSign=0.00;

                        areaFactWeekSignPer=0.00;

                        areaFactSigned=0.00;

                        areaPlanWeekBearerGap=0.00;

                        areaPlanWeekSignGap=0.00;

                    }
                }else {
                    /*
                    * ?????????????????????
                    * */
                    if(areaTargetMonthBearer!=0){
                        areaTargetWeekBearerPer=(double)areaTargetWeekBearer/areaTargetMonthBearer;

                    }

                    if(areaTargetMonthSign!=0.00){
                        areaTargetWeekSignPer=(double)areaTargetWeekSign/areaTargetMonthSign;  }



                    if(areaTargetMonthBearer!=0){
                        areaFactMonthBearerPer =(double) areaFactMonthBearerTotal/areaTargetMonthBearer;

                    }
                    if(areaTargetWeekBearer!=0){
                        areaFactWeekBearerPer=(double)areaFactWeekBearer/areaTargetWeekBearer;
                    }
                    if(areaTargetMonthSign!=0.00){
                        areaFactMonthSignPer =(double) areaFactMonthSign/areaTargetMonthSign;
                    }
                    if(areaTargetWeekSign!=0.00){

                        areaFactWeekSignPer = (double)areaFactWeekSign/areaTargetWeekSign;

                        }



                    WeeklyMarktingPara.put("project_id",areaId);
                    /*
                    * ???????????????projectcode
                    * */
                    WeeklyMarktingPara.put("project_code",null);
                    WeeklyMarktingPara.put("project_name",areaName);
                    WeeklyMarktingPara.put("trader_id",null);
                    WeeklyMarktingPara.put("trader_name",null);
                    WeeklyMarktingPara.put("islast",null);
                    WeeklyMarktingPara.put("issales_trade",null);
                    WeeklyMarktingPara.put("subscribe_price",areaSubscribePrice);
                    WeeklyMarktingPara.put("target_month_bearer",areaTargetMonthBearer);
                    WeeklyMarktingPara.put("target_month_sign",areaTargetMonthSign);
                    WeeklyMarktingPara.put("target_week_bearer",areaTargetWeekBearer);
                    WeeklyMarktingPara.put("target_week_sign",areaTargetWeekSign);

                    WeeklyMarktingPara.put("target_week_bearer_per",df.format(areaTargetWeekBearerPer) );
                    WeeklyMarktingPara.put("target_week_sign_per",df.format(areaTargetWeekSignPer));

                    WeeklyMarktingPara.put("fact_month_bearer_total",areaFactMonthBearerTotal);
                    WeeklyMarktingPara.put("fact_month_bearer_per",df.format(areaFactMonthBearerPer));
                    WeeklyMarktingPara.put("fact_week_bearer",areaFactWeekBearer);
                    WeeklyMarktingPara.put("fact_week_bearer_per",df.format(areaFactWeekBearerPer));
                    WeeklyMarktingPara.put("fact_month_sign",areaFactMonthSign);
                    WeeklyMarktingPara.put("fact_month_sign_per",df.format(areaFactMonthSignPer));
                    WeeklyMarktingPara.put("fact_week_sign",areaFactWeekSign);
                    WeeklyMarktingPara.put("fact_week_sign_per",df.format(areaFactWeekSignPer));

                    WeeklyMarktingPara.put("fact_signed",areaFactSigned);
                    WeeklyMarktingPara.put("plan_reserve",0);
                    WeeklyMarktingPara.put("plan_signed",0);
                    WeeklyMarktingPara.put("plan_month_lock_price",0);
                    WeeklyMarktingPara.put("plan_month_lock_per",0);
                    WeeklyMarktingPara.put("plan_month_newsign",0);
                    WeeklyMarktingPara.put("plan_month_sign",0);
                    WeeklyMarktingPara.put("plan_week_bearer_gap",areaPlanWeekBearerGap);
                    WeeklyMarktingPara.put("plan_week_sign_gap",areaPlanWeekSignGap);
                    WeeklyMarktingPara.put("plan_month_sign_gap",0);
                    WeeklyMarktingPara.put("gap_cause",null);
                    WeeklyMarktingPara.put("is_effective",0);
                    WeeklyMarktingPara.put("this_time",months);
                    WeeklyMarktingPara.put("how_week",howWeek);
                    WeeklyMarktingPara.put("start_time",startWeek);
                    WeeklyMarktingPara.put("end_time",endWeek);
                    WeeklyMarktingPara.put("create_time",null);
                    WeeklyMarktingPara.put("creator",0);
                    WeeklyMarktingPara.put("editor",0);
                    WeeklyMarktingPara.put("update_time",null);
                    WeeklyMarktingPara.put("area_id",areaIdfather);
                    WeeklyMarktingPara.put("plan_status",0);
                    WeeklyMarktingPara.put("type",areaType);
                    WeeklyMarktingPara.put("checkeds",0);
                    WeeklyMarktingPara.put("is_self_write",0);
                    if(flag==true){
                        System.out.println("WeeklyMarktingPara"+"true");
                        weeklyMarketingDao.updateBeforeInsert(WeeklyMarktingPara);
                    }else {
                        System.out.println("WeeklyMarktingPara"+"false");
                    weeklyMarketingDao.weekMarketingPlanInitial(WeeklyMarktingPara);}

                    /*
                     * ???????????????????????????????????????
                     * */
                    groupTargetMonthBearer+=areaTargetMonthBearer;
                    groupTargetMonthSign+=areaTargetMonthSign;

                    groupTargetWeekBearer+=areaTargetWeekBearer;
                    groupTargetWeekSign+=areaTargetWeekSign;

                    groupSubscribePrice+=areaSubscribePrice;

                    groupFactMonthBearerTotal+=areaFactMonthBearerTotal;

                    groupFactWeekBearer+=areaFactWeekBearer;

                    groupFactMonthSign+=areaFactMonthSign;

                    groupFactWeekSign+=areaFactWeekSign;

                    groupFactSigned+=areaFactSigned;

                    groupPlanWeekBearerGap+=areaPlanWeekBearerGap;

                    groupPlanWeekSignGap+=areaPlanWeekSignGap;
                    if(groupTargetMonthBearer!=0){
                        groupTargetWeekBearerPer=(double)groupTargetWeekBearer/groupTargetMonthBearer;
                    }

                    if(groupTargetMonthSign!=0.00){
                        groupTargetWeekSignPer=(double)groupTargetWeekSign/groupTargetMonthSign;  }

                    if(groupTargetMonthBearer!=0){
                        groupFactMonthBearerPer =(double) groupFactMonthBearerTotal/groupTargetMonthBearer;

                    }
                    if(groupTargetWeekBearer!=0){
                        groupFactWeekBearerPer=(double)groupFactWeekBearer/groupTargetWeekBearer;
                    }
                    if(groupTargetMonthSign!=0.00){
                        groupFactMonthSignPer =(double) groupFactMonthSign/groupTargetMonthSign;;
                    }
                    if(groupTargetWeekSign!=0.00){
                        groupFactWeekSignPer = (double)groupFactWeekSign/groupTargetWeekSign;
                    }


                    WeeklyMarktingPara.put("project_id",groupId);

                    /*
                     * ????????????projectCode
                     * */
                    WeeklyMarktingPara.put("project_code",null);
                    WeeklyMarktingPara.put("project_name",groupName);
                    WeeklyMarktingPara.put("trader_id",null);
                    WeeklyMarktingPara.put("trader_name",null);
                    WeeklyMarktingPara.put("islast",null);
                    WeeklyMarktingPara.put("issales_trade",null);
                    WeeklyMarktingPara.put("subscribe_price",groupSubscribePrice);
                    WeeklyMarktingPara.put("target_month_bearer",groupTargetMonthBearer);
                    WeeklyMarktingPara.put("target_month_sign",groupTargetMonthSign);
                    WeeklyMarktingPara.put("target_week_bearer",groupTargetWeekBearer);
                    WeeklyMarktingPara.put("target_week_sign",groupTargetWeekSign);

                    WeeklyMarktingPara.put("target_week_bearer_per",df.format(groupTargetWeekBearerPer) );
                    WeeklyMarktingPara.put("target_week_sign_per",df.format(groupTargetWeekSignPer));

                    WeeklyMarktingPara.put("fact_month_bearer_total",groupFactMonthBearerTotal);
                    WeeklyMarktingPara.put("fact_month_bearer_per",df.format(groupFactMonthBearerPer));
                    WeeklyMarktingPara.put("fact_week_bearer",groupFactWeekBearer);
                    WeeklyMarktingPara.put("fact_week_bearer_per",df.format(groupFactWeekBearerPer));
                    WeeklyMarktingPara.put("fact_month_sign",groupFactMonthSign);
                    WeeklyMarktingPara.put("fact_month_sign_per",df.format(groupFactMonthSignPer));
                    WeeklyMarktingPara.put("fact_week_sign",groupFactWeekSign);
                    WeeklyMarktingPara.put("fact_week_sign_per",df.format(groupFactWeekSignPer));

                    WeeklyMarktingPara.put("fact_signed",groupFactSigned);
                    WeeklyMarktingPara.put("plan_reserve",0);
                    WeeklyMarktingPara.put("plan_signed",0);
                    WeeklyMarktingPara.put("plan_month_lock_price",0);
                    WeeklyMarktingPara.put("plan_month_lock_per",0);
                    WeeklyMarktingPara.put("plan_month_newsign",0);
                    WeeklyMarktingPara.put("plan_month_sign",0);
                    WeeklyMarktingPara.put("plan_week_bearer_gap",groupPlanWeekBearerGap);
                    WeeklyMarktingPara.put("plan_week_sign_gap",groupPlanWeekSignGap);
                    WeeklyMarktingPara.put("plan_month_sign_gap",0);
                    WeeklyMarktingPara.put("gap_cause",null);
                    WeeklyMarktingPara.put("is_effective",0);
                    WeeklyMarktingPara.put("this_time",months);
                    WeeklyMarktingPara.put("how_week",howWeek);
                    WeeklyMarktingPara.put("start_time",startWeek);
                    WeeklyMarktingPara.put("end_time",endWeek);
                    WeeklyMarktingPara.put("create_time",null);
                    WeeklyMarktingPara.put("creator",0);
                    WeeklyMarktingPara.put("editor",0);
                    WeeklyMarktingPara.put("update_time",null);
                    WeeklyMarktingPara.put("area_id",groupId);
                    WeeklyMarktingPara.put("plan_status",null);
                    WeeklyMarktingPara.put("type",groupType);
                    WeeklyMarktingPara.put("checkeds",0);
                    WeeklyMarktingPara.put("is_self_write",0);
                    if(flag==true){
                        weeklyMarketingDao.updateBeforeInsert(WeeklyMarktingPara);
                    }else {
                    weeklyMarketingDao.weekMarketingPlanInitial(WeeklyMarktingPara);}



                }

            }

        }


    }


    /**
     * ?????????????????????????????????????????????????????????????????? ???????????????????????????
     *
     * @return
     @Override
     */
    @Override
    public List<Map> WeeklyPlanRoomTotal( String BeginDate,String EndDate,Integer time){

       /*????????????????????????TIME???1???????????????????????????TIME???2?????????????????????*/

        Map<String,Object> map =new HashMap<>();

        map.put("BeginDate",BeginDate);
        map.put("EndDate",EndDate);
        map.put("time",time);
        return weeklyMarketingDao.WeeklyPlanRoomTotal(map);
    }

    /**
     *  ???????????????????????????????????????????????????????????? ??????????????????????????????-->
     *
     * @return
     */
    @Override
    public List<Map> WeeklyPlanRmbTotal( String BeginDate,String EndDate){
        Map<String,Object> map =new HashMap<>();

        map.put("BeginDate",BeginDate);
        map.put("EndDate",EndDate);
        return weeklyMarketingDao.WeeklyPlanRmbTotal(map);
    }


    /**
     *  -????????????????????????????????????????????????????????????????????????-
     *
     * @return
     */
    @Override
    public List<Map> WeeklyPlanCnt( String BeginDate,String EndDate){
        Map<String,Object> map =new HashMap<>();

        map.put("BeginDate",BeginDate);
        map.put("EndDate",EndDate);
       List<Map> listmap= weeklyMarketingDao.WeeklyPlanCnt(map);

        return listmap;


    }

    /**
     * ?????????????????????????????????????????????
     *-- ???????????? ??????????????????-- ???????????? -- ??????????????????
     * @return
     */
    @Override
    public List<Map> WeeklyPlanMonthsTarget( String months){
        Map<String,Object> map =new HashMap<>();

        map.put("months",months);
        return weeklyMarketingDao.WeeklyPlanMonthsTarget(map);
    }

    /**
     * ?????????????????????????????????????????????
     *?????????????????? ??????????????????
     * @return
     */
    @Override
    public List<Map> WeeklyPlanWeekklyTarget( String months,Integer weekSerialNumber){
        Map<String,Object> map =new HashMap<>();

        map.put("months",months);
        map.put("weekSerialNumber",weekSerialNumber);
        return weeklyMarketingDao.WeeklyPlanWeekklyTarget(map);
    }



    /**
     * ??????????????????????????????
     *
     * @return
     */
    @Override
    public ResultBody weekMarketingPlanEffective(Map map){
        ResultBody resultBody=new ResultBody();
                /*?????????????????????????????????????????????????????????????????????*/
         /*   Map windowMap= weeklyMarketingDao.selectWindowTime(map);
                 if((windowMap.get("is_window")+"").equals("1")){
                     resultBody.setMessages("?????????"+windowMap.get("windows_start")+"???"+windowMap.get("windows_end")+"???????????????");
                     resultBody.setCode(500);
                     return resultBody;
                 }*/

        /*
        * ??????is_effective????????????????????????????????????
        * */

            /*??????????????????????????????????????????areaid???project?????????????????????*/
        Map  reusltMap= weeklyMarketingDao.selectCheckeds(map);

        if(map.get("type").toString(). equals("2") && map.get("plan_status").toString().equals("1")) {
          /*??????????????????????????????????????????*/
            Integer planStatus=Integer.parseInt(reusltMap.get("plan_status")+"");
            if( planStatus>=1 ){
               // resultBody.setMessages("?????????????????????????????????????????????????????????????????????");
                resultBody.setCode(500);
                return resultBody;
            }

            Map areamap = new HashMap();
            areamap.put("is_effective", 1);
            areamap.put("this_time", map.get("this_time").toString());

            areamap.put("how_week", map.get("how_week"));
            areamap.put("area_id", map.get("project_id").toString());

           weeklyMarketingDao.weekMarketingPlanEffective(areamap);

        //    weeklyMarketingDao.ProjectSelectupdatesum(areamap);
        }
        /*ruo ??????????????????????????????????????????????????????????????????*/
        if(map.get("type").toString(). equals("2") && map.get("plan_status").toString().equals("-1")) {

            Map areamap = new HashMap();

            areamap.put("this_time", map.get("this_time").toString());
            areamap.put("is_effective", 0);
            areamap.put("how_week", map.get("how_week"));
            areamap.put("area_id", map.get("project_id").toString());

          weeklyMarketingDao.selectAreaProject(areamap);
         //   weeklyMarketingDao.ProjectSelectupdatesum(areamap);


        }
        if(map.get("type").toString().equals("3") && map.get("is_regionwrite")==null) {

            if((map.get("plan_status")+"").equals("1") || (map.get("plan_status")+"").equals("2")) {



                Integer checkeds = Integer.parseInt(reusltMap.get("checkeds") + "");
                Integer planStatus = Integer.parseInt(reusltMap.get("plan_status") + "");
                if (checkeds >= 1 || planStatus >= 1) {
                    resultBody.setMessages("?????????????????????????????????????????????????????????????????????");
                    resultBody.setCode(500);
                    return resultBody;
                }
            } }

        Integer result=  weeklyMarketingDao.weekMarketingPlanEffective(map);
        if(map.get("type").toString(). equals("2")){
        weeklyMarketingDao.AreaSelectupdatesum(map);}
        /*
        * ?????????????????????????????????????????????????????????
        * */

      /*  if(map.get("area_id")!=null  ) {
            map.put("area_id", map.get("area_id").toString());}
        if(map.get("type").toString(). equals("3")){
            weeklyMarketingDao.ProjectSelectupdatesum(map);
        }*/
        resultBody.setData(result);
        resultBody.setCode(200);



        return resultBody;


    }
    /**
     * ??????????????????????????????
     *
     * @return
     */
    @Override
    public Integer weekMarketingPlanUpdate(Map map){


            /*
             * ??????Map?????????????????????????????????????????????????????????0
             * */
            /*??????????????????????????????????????????????????????????????????????????????????????????*/
        if((map.get("type")+"").equals("3") && map.get("is_regionwrite")==null) {
                Map mapone=  weeklyMarketingDao.selectCheckeds(map);
                if(mapone!=null  && mapone.size()>0 ){
                if(Integer.parseInt(mapone.get("plan_status")+"")>0 || Integer.parseInt(mapone.get("checkeds")+"")>0 ){
                    return null;
                }}
        }


            System.out.println(map.toString());
            Iterator iterable= map.entrySet().iterator();
            while (iterable.hasNext()) {
                Map.Entry entry_d = (Map.Entry) iterable.next();
                if(entry_d!=null) {
                    Object key = entry_d.getKey();
                    Object value = entry_d.getValue();
                    if (value =="") {
                        value = null;
                    }
                    String value2 = value + "";

                    map.put(key.toString(), value);
                }   }
     List<Map>  gapMap=  weeklyMarketingDao. selectGapList();
            if(map.get("minor_details")!=null){
                for(Map map1: gapMap){
                    if(map.get("minor_details").toString().equals(map1.get("DictName").toString())){
                        map.put("minor_details",map1.get("DictCode"));
                        break;
                    }
                }
            }
        if(map.get("gap_cause")!=null){
            for(Map map1: gapMap){
                if(map.get("gap_cause").toString().equals(map1.get("DictName").toString())){
                    map.put("gap_cause",map1.get("DictCode"));
                    break;
                }
            }
        }

        return  weeklyMarketingDao.weekMarketingPlanUpdate(map);




    }

    /**
     * ?????????????????????
     *
     * @return
     */
    @Override
    public List<Map> ProjectSelectWeekly(Map<String, Object> map){
        String report_time=map.get("report_time")+"";
        String toexamine_time=map.get("toexamine_time")+"";
        System.err.println(report_time);
        System.err.println(toexamine_time);
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

        return weeklyMarketingDao.ProjectSelectWeekly(map);
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    @Override
    public List<Map> groupSelectWeekly(Map<String, Object> map){

        return weeklyMarketingDao.groupSelectWeekly(map);
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    @Override
    public List<Map> regionSelectWeekly(Map<String, Object> map)
    {

        List<Map> resultmap=weeklyMarketingDao.regionSelectWeekly(map);



        return resultmap;
    }
    /**regionSelectWeekly
     * ?????????????????????????????????
     *
     * @return
     */
    @Override
    public List<Map> ProjectExamineWeekly(Map<String, Object> map){
        List<Map>  gapMap=  weeklyMarketingDao. selectGapList();
        List<Map> result= weeklyMarketingDao.ProjectExamineWeekly(map);

                for(Map Map1:   result){
                    System.out.println(Map1);
                    if(Map1.get("minor_details")!=null){
                        for(Map map1: gapMap){
                            if(Map1.get("minor_details").toString().equals(map1.get("DictCode").toString())){
                                Map1.put("minor_details",map1.get("DictName"));
                                break;
                            }
                        }
                    }
                    if(Map1.get("gap_cause")!=null){
                        for(Map map1: gapMap){
                            if(Map1.get("gap_cause").toString().equals(map1.get("DictCode").toString())){
                                Map1.put("gap_cause",map1.get("DictName"));
                                break;
                            }
                        }
                    }

                }


            /*??????????????????*/
        List<Map> gap=  weeklyMarketingDao.selectGapList();
        Map<String,Object> gapmap=new HashMap<>();
        gapmap.put("gap",gap);
        result.add(gapmap);

        if((map.get("type")+"").equals("2") && (map.get("business_unit_id")==null )){
            Map flowIdMap = weeklyMarketingDao.getFlowStatus(result.get(0).get("id")+"");
            if(flowIdMap!=null  &&  flowIdMap.get("flow_status")!=null){

                result.get(0).put("flow_status",flowIdMap.get("flow_status"));
                if(flowIdMap.get("flow_id")!=null){
                    result.get(0).put("flow_id",flowIdMap.get("flow_id"));
                }
            }

        }

        return result;
    }
    /**
     * ??????????????????????????????????????????
     *
     * @return
     */
    @Override
    public List<Map> weekMarketingWeekSelect(Map<String, Object> map){

        return weeklyMarketingDao.weekMarketingWeekSelect(map);
    }

    @Override
    public void weeklyDataExport(HttpServletRequest request, HttpServletResponse response, String thisTime, int howWeek, int weeklyType, String projectId,String areaReport) {

        Map<String, Object> result = new HashMap<>(16);
        String planName = "";
        String basePath;
        String templatePath;
        String targetFileDir;
        String targetfilePath;
        FileInputStream templateInputStream = null;
        FileOutputStream fileOutputStream = null;
        Workbook targetWorkBook;
        XSSFSheet targetSheet;
        boolean flag=false;
        switch (weeklyType) {
            case Constant.PREPARED_BY_UNIT_TYPE_GROUP:
                planName = "?????????????????????";
                flag=true;
                break;
            case Constant.PREPARED_BY_UNIT_TYPE_REGION:
                planName = "?????????????????????";
                flag=true;
                break;
            case Constant.PREPARED_BY_UNIT_TYPE_PROJECT:
                planName = "?????????????????????";
                break;
            default:
                planName = "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new Date());
        planName += time;

        try {
//            basePath = "E:/xuhui/marketing-control-api/cifimaster/visolink-sales-api/src";

              basePath = request.getServletContext().getRealPath("/");


            templatePath = File.separator + "TemplateExcel" + File.separator + "weekMarketingPlan.xlsx";
            //??????????????????????????????
            targetFileDir = "Uploads" + File.separator + "DownLoadTemporaryFiles";
            // ?????????????????????
            targetfilePath = targetFileDir + File.separator + planName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            planName = URLEncoder.encode(planName + ".xlsx", "utf-8").replace("+", "%20");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");

            //??????content-disposition??????????????????????????????????????????????????????
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(planName.getBytes(), "utf-8"));
            //??????????????????????????????

            //??????????????????????????????
    //    String  realpath= this.getClass().getResource("/").getPath()  ;

      //   realpath=realpath.substring(0,realpath.indexOf("/target"))+File.separator+"src"+File.separator+"main"+File.separator+"webapp"+templatePath;

       //  File templateFile = new File(realpath);



          templatePath = basePath + templatePath;
           //templatePath="/Users/WorkSapce/Java/????????????/Java/marketing-control-api/cifimaster/visolink-sales-api/src/main/webapp/TemplateExcel/weekMarketingPlan.xlsx";
    File templateFile = new File(templatePath);
            if (!templateFile.exists()) {
                templateFile.mkdirs();
                throw new ServiceException("-1", "????????????????????????????????????????????????");
            }
            //?????????????????????????????????
            File targetFileDirFile = new File(targetFileDir);
            if (!targetFileDirFile.exists()) {
                targetFileDirFile.mkdirs();
            }
            //?????????????????????
            templateInputStream = new FileInputStream(templateFile);
            targetWorkBook = new XSSFWorkbook(templateInputStream);
            targetSheet = (XSSFSheet) targetWorkBook.getSheetAt(0);
            targetWorkBook.setSheetName(0, "?????????????????????");
            //????????????????????????
            int maxTemplateRows = targetSheet.getLastRowNum();
            //???????????????????????????

            List<WeekMarkting> mapList = new ArrayList<>();
            Map temMap = new HashMap(16);
            temMap.put("how_week", howWeek);
            temMap.put("project_id", projectId);
            temMap.put("weeklyType", weeklyType);
            temMap.put("this_time", thisTime);
            WeekMarkting map = weeklyMarketingDao.selectWeeklyMarketPlanByProjectIdByExport(temMap);
            map.setRow(Constant.WEEK_START_ROW);
            mapList.add(map);
            String Boss=null;
            if(projectId.equals("00000001")){
                Boss="true";
                map.setBoss(Boss);
            }
            map.setAreaReport(areaReport);
            selectAllMonthPlanByFatherId(mapList, Constant.WEEK_START_ROW, map, thisTime, howWeek, weeklyType, areaReport);
            //?????????
            int startRows = Constant.WEEK_START_ROW;
            Row row0 = targetSheet.getRow(1);
            int maxCellNum = row0.getPhysicalNumberOfCells();
            CellStyle style = row0.getCell(0).getCellStyle();
            style.setLocked(true);
            style.setWrapText(true);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setAlignment(HorizontalAlignment.CENTER);
            /*??????????????????????????????????????????*/
         String  startTime= map.getStart_time();
            String  endTime= map.getEnd_time();
            startTime=  startTime.substring(startTime.lastIndexOf("-")+1,startTime.lastIndexOf("-")+3);
            endTime=  endTime.substring(endTime.lastIndexOf("-")+1,endTime.lastIndexOf("-")+3);
                if(map.getHow_week() != 1){
                    startTime= Integer.parseInt(startTime)+1+"";
                }
                Cell  cell9= row0.createCell(10);
            cell9.setCellStyle(style);
            cell9.setCellValue(startTime+"-"+endTime+"?????????");

            Cell  cell11= row0.createCell(12);
            cell11.setCellStyle(style);
            cell11.setCellValue(startTime+"-"+endTime+"???????????????");

            Row row1 = targetSheet.getRow(2);
           Cell cell15= row1.createCell(16);
            Cell cell16= row1.createCell(17);
            Cell cell19= row1.createCell(20);
            Cell cell20= row1.createCell(21);

            cell15.setCellStyle(style);
            cell15.setCellValue(startTime+"-"+endTime+"?????????(???)");

            cell16.setCellStyle(style);
            cell16.setCellValue(startTime+"-"+endTime+"??????????????????");

            cell19.setCellStyle(style);
            cell19.setCellValue(startTime+"-"+endTime+"???????????????????????????");

            cell20.setCellStyle(style);
            cell20.setCellValue(startTime+"-"+endTime+"??????????????????");

            List<Map<String, String>> groupRowlist = new ArrayList<Map<String, String>>();
            //????????????????????????
            List<Map>  gapMap=  weeklyMarketingDao. selectGapList();


            for (WeekMarkting weekMap : mapList) {


                    if(weekMap.getMinor_details()!=null){
                        for(Map map1: gapMap){
                            if(weekMap.getMinor_details().equals(map1.get("DictCode").toString())){
                                weekMap.setMinor_details(map1.get("DictName")+"");
                                break;
                            }
                        }
                    }
                    if(weekMap.getGap_cause()!=null){
                        for(Map map1: gapMap){
                            if(weekMap.getGap_cause().equals(map1.get("DictCode").toString())){
                                weekMap.setGap_cause(map1.get("DictName")+"");
                                break;
                            }
                        }
                    }

                XSSFRow positionRow = targetSheet.createRow(startRows);
                positionRow.setHeightInPoints(20);
                //?????????????????????????????????????????? ???business_name???


                if ((int) weekMap.getType() == 2) {
                    int row = (int) weekMap.getRow();
                    int startIndex = row;
                    int endIndex = 0;
                    Map groupIndex = new HashMap(16);
                    for (WeekMarkting regionMap : mapList) {
                        String fatherId = regionMap.getFather_id();
                        row = regionMap.getRow();

                        if (  weekMap.getGuid()!=null &&  weekMap.getGuid().equals(fatherId)) {
                            endIndex = row - 1;
                        }
                    }
                    groupIndex.put("startIndex", String.valueOf(startIndex));
                    groupIndex.put("endIndex", String.valueOf(endIndex));
                    groupIndex.put("list", groupIndex);
                    groupRowlist.add(groupIndex);

                }
                startRows++;
                if(areaReport==null){
                    setValueToExcelCell(targetWorkBook, positionRow, weekMap,startRows);
                }else {
                    setValueToExcelCellByFlow(targetWorkBook, positionRow, weekMap,startRows);
                }


            }

            //???????????????Excel?????????????????????
            for (Map<String, String> rowMap : groupRowlist) {
                int startIndex = Integer.valueOf(rowMap.get("startIndex"));
                int endIndex = Integer.valueOf(rowMap.get("endIndex"));
                targetSheet.groupRow(startIndex, endIndex);
            }

            targetSheet.setColumnHidden(maxCellNum, true);
            targetSheet.setRowSumsBelow(false);
            targetSheet.createFreezePane(2, 3,2,3);
            targetSheet.setForceFormulaRecalculation(true);

            //????????????
            if(areaReport==null){
                fileOutputStream = new FileOutputStream(targetfilePath);
                targetWorkBook.write(response.getOutputStream());
            }

            //?????????????????????
            if(areaReport!=null){
                File saveFile=new File(uplodepath+File.separator+"weekAreaFile");

                if(!saveFile.exists()){
                    saveFile.mkdirs();
                }
                targetfilePath=uplodepath+File.separator+"weekAreaFile"+File.separator+areaReport+ ".xlsx";
                fileOutputStream = new FileOutputStream(targetfilePath);
                targetWorkBook.write(fileOutputStream);
            }

        } catch (ServiceException se) {
            se.printStackTrace();
            System.out.println(se.getResponseMsg());
            result.put("key", se.getResponseMsg());
        } catch (UnsupportedEncodingException e) {
            System.out.print("????????????????????????");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (templateInputStream != null) {
                    templateInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setValueToExcelCell(Workbook targetWorkBook, XSSFRow positionRow, WeekMarkting weekMap,int startRows) {
      CellStyle cs = targetWorkBook.createCellStyle();
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setWrapText(true);

        CellStyle csone=targetWorkBook.createCellStyle();

        XSSFDataFormat format= (XSSFDataFormat) targetWorkBook.createDataFormat();
        csone.setBorderBottom(BorderStyle.THIN);
        csone.setBorderTop(BorderStyle.THIN);
        csone.setBorderLeft(BorderStyle.THIN);
        csone.setBorderRight(BorderStyle.THIN);
        csone.setAlignment(HorizontalAlignment.CENTER);
        csone.setDataFormat(format.getFormat("0.00"));

        XSSFCell cell0 = positionRow.createCell(0);

        XSSFCell cell1 = positionRow.createCell(1);
        XSSFCell cellreport = positionRow.createCell(2);
        XSSFCell cell2 = positionRow.createCell(3);
        XSSFCell cell3 = positionRow.createCell(4);
        XSSFCell cell4 = positionRow.createCell(5);
        XSSFCell cell5 = positionRow.createCell(6);
        XSSFCell cell6 = positionRow.createCell(7);
        XSSFCell cell7 = positionRow.createCell(8);
        XSSFCell cell8 = positionRow.createCell(9);
        XSSFCell cell9 = positionRow.createCell(10);
        XSSFCell cell10 = positionRow.createCell(11);
        XSSFCell cell11 = positionRow.createCell(12);
        XSSFCell cell12 = positionRow.createCell(13);
        XSSFCell cell13 = positionRow.createCell(14);
        XSSFCell cell14 = positionRow.createCell(15);
        XSSFCell cell15 = positionRow.createCell(16);
        XSSFCell cell16 = positionRow.createCell(17);
        XSSFCell cell17 = positionRow.createCell(18);
        XSSFCell cell18 = positionRow.createCell(19);
        XSSFCell cell19 = positionRow.createCell(20);
        XSSFCell cell20 = positionRow.createCell(21);
        XSSFCell cell21 = positionRow.createCell(22);
        XSSFCell cell22 = positionRow.createCell(23);
        XSSFCell cell23 = positionRow.createCell(24);
        XSSFCell cell24 = positionRow.createCell(25);
        XSSFCell cell25 = positionRow.createCell(26);
        XSSFCell cell26 = positionRow.createCell(27);
        XSSFCell cell27 = positionRow.createCell(28);
        XSSFCell cell28 = positionRow.createCell(29);
        XSSFCell cell29 = positionRow.createCell(30);
        XSSFCell cell30 = positionRow.createCell(31);
        XSSFCell cell31 = positionRow.createCell(32);
        XSSFCell cell32 = positionRow.createCell(33);
        XSSFCell cell33 = positionRow.createCell(34);
        XSSFCell cell34 = positionRow.createCell(35);
        XSSFCell cell35 = positionRow.createCell(36);
        XSSFCell cell36 = positionRow.createCell(37);
        setCellValueStyle(weekMap.getProject_name(), cell0, cs);


        String report_time = weekMap.getReport_time();
        setCellValueStyle(weekMap.getPlan_status(), cell1, cs);
        setCellValueStyle(weekMap.getProject_code(), cell2, cs);
        setCellValueStyle(weekMap.getReport_time(),cellreport,cs);
        setCellValueStyle(weekMap.getTrader_name(), cell3, cs);

        setCellValueStyle(weekMap.getIslast(), cell4, cs);

        setCellValueStyle( weekMap.getIssales_trade(), cell5, cs);
        cell6.setCellStyle(csone);
        if (weekMap.getSubscribe_price() == null){
            cell6.setCellValue("");
        }else {
            cell6.setCellValue(weekMap.getSubscribe_price());
        }
        cell7.setCellStyle(cs);
        if (weekMap.getTarget_month_bearer()== null){
            cell7.setCellValue("");
        }else {
            cell7.setCellValue(weekMap.getTarget_month_bearer());
        }
        cell8.setCellStyle(csone);
        if (weekMap.getTarget_month_sign() == null){
            cell8.setCellValue("");
        }else {
            cell8.setCellValue(weekMap.getTarget_month_sign());
        }
        cell9.setCellStyle(cs);
        if (weekMap.getTarget_week_bearer() == null){
            cell9.setCellValue("");
        }else {
            cell9.setCellValue( weekMap.getTarget_week_bearer());
        }

       // setCellValueStyle("subscribe_price", weekMap, cell6, csone);
      //  setCellValueStyle("target_month_bearer", weekMap, cell7, cs);
       // setCellValueStyle("target_month_sign", weekMap, cell8, csone);
      //  setCellValueStyle("target_week_bearer", weekMap, cell9, cs);
     //   setCellValueStyle("target_week_sign", weekMap, cell10, csone);
        cell10.setCellStyle(csone);
        if (weekMap.getTarget_week_sign()== null){
            cell10.setCellValue("");
        }else {
            cell10.setCellValue(weekMap.getTarget_week_sign());
        }
     //   weekMap.put("target_week_bearer_per",((BigDecimal)weekMap.get("target_week_bearer_per")).doubleValue()*100+"%") ;
      //  setCellValueStyle("target_week_bearer_per", weekMap, cell11, csone);
        cell11.setCellStyle(csone);
        if (weekMap.getTarget_week_bearer_per()==null){
            cell11.setCellValue("");
        }else {
            cell11.setCellFormula(weekMap.getTarget_week_bearer_per()+"&\"%\"");
        }

     //   weekMap.put("target_week_sign_per",((BigDecimal)weekMap.get("target_week_sign_per")).doubleValue()*100+"%") ;
       // setCellValueStyle("target_week_sign_per", weekMap, cell12, csone);
        cell12.setCellStyle(csone);
        if (weekMap.getTarget_week_sign_per() == null){
            cell12.setCellValue("");
        }else {
            cell12.setCellFormula(weekMap.getTarget_week_sign_per()+"&\"%\"");
        }
       // setCellValueStyle("fact_month_bearer_total", weekMap, cell13, cs);
        cell13.setCellStyle(cs);
        if (weekMap.getFact_month_bearer_total()== null){
            cell13.setCellValue("");
        }else {
            cell13.setCellValue(weekMap.getFact_month_bearer_total());
        }
      //  weekMap.put("fact_month_bearer_per",((BigDecimal)weekMap.get("fact_month_bearer_per")).doubleValue()*100+"%") ;
       // setCellValueStyle("fact_month_bearer_per", weekMap, cell14, csone);
        cell14.setCellStyle(csone);
        if (weekMap.getFact_month_bearer_per()== null){
            cell14.setCellValue("");
        }else {
            cell14.setCellFormula(weekMap.getFact_month_bearer_per()+"&\"%\"");
        }

       // setCellValueStyle("fact_week_bearer", weekMap, cell15, cs);
        cell15.setCellStyle(cs);
        if (weekMap.getFact_week_bearer()== null){
            cell15.setCellValue("");
        }else {
            cell15.setCellValue(weekMap.getFact_week_bearer());
        }
      //  weekMap.put("fact_week_bearer_per",((BigDecimal)weekMap.get("fact_week_bearer_per")).doubleValue()*100+"%") ;
     //   setCellValueStyle("fact_week_bearer_per", weekMap, cell16, csone);

        cell16.setCellStyle(csone);
        if (weekMap.getFact_week_bearer_per()== null){
            cell16.setCellValue("");
        }else {
            cell16.setCellFormula(weekMap.getFact_week_bearer_per()+"&\"%\"");
        }
       // setCellValueStyle("fact_month_sign", weekMap, cell17, csone);
        cell17.setCellStyle(csone);
        if (weekMap.getFact_month_sign()== null){
            cell17.setCellValue("");
        }else {
            cell17.setCellValue(weekMap.getFact_month_sign());
        }
      //  weekMap.put("fact_month_sign_per",((BigDecimal)weekMap.get("fact_month_sign_per")).doubleValue()*100+"%") ;
     //   setCellValueStyle("fact_month_sign_per", weekMap, cell18, csone);
        cell18.setCellStyle(csone);
        if (weekMap.getFact_month_sign_per()== null){
            cell18.setCellValue("");
        }else {
            cell18.setCellFormula(weekMap.getFact_month_sign_per()+"&\"%\"");
        }


      //  setCellValueStyle("fact_week_sign", weekMap, cell19, csone);
        cell19.setCellStyle(csone);
        if (weekMap.getFact_week_sign()== null){
            cell19.setCellValue("");
        }else {
            cell19.setCellValue(weekMap.getFact_week_sign());
        }
     //   weekMap.put("fact_week_sign_per",((BigDecimal)weekMap.get("fact_week_sign_per")).doubleValue()*100+"%") ;
     //   setCellValueStyle("fact_week_sign_per", weekMap, cell20, csone);
        cell20.setCellStyle(csone);
        if (weekMap.getFact_week_sign_per()== null){
            cell20.setCellValue("");
        }else {
            cell20.setCellFormula(weekMap.getFact_week_sign_per()+"&\"%\"");
        }

       // setCellValueStyle("fact_signed", weekMap, cell21, csone);
      //  setCellValueStyle("plan_reserve", weekMap, cell22, csone);
      //  setCellValueStyle("plan_signed", weekMap, cell23, csone);
      // setCellValueStyle("plan_month_lock_price", weekMap, cell24, cs);
        cell21.setCellStyle(csone);
        cell22.setCellStyle(csone);
        cell23.setCellStyle(csone);
        if (weekMap.getFact_signed()== null){
            cell21.setCellValue("");
        }else {
            cell21.setCellValue(weekMap.getFact_signed());
        }
        if (weekMap.getPlan_reserve()== null){
            cell22.setCellValue("");
        }else {
            cell22.setCellValue(weekMap.getPlan_reserve());
        }
        if (weekMap.getPlan_signed()== null){
            cell23.setCellValue("");
        }else {
            cell23.setCellValue(weekMap.getPlan_signed());
        }
        cell24.setCellStyle(cs);
        cell24.setCellFormula("SUM(Y"+startRows+"+X"+startRows+"+S"+startRows+")");

     //   setCellValueStyle("plan_month_lock_per", weekMap, cell25, csone);
        cell25.setCellStyle(csone);
        cell25.setCellFormula("IFERROR(ROUND(Z"+startRows+"/"+"J"+startRows+ "*10000/100,2),0)&\"%\"");
      //  setCellValueStyle("plan_month_newsign", weekMap, cell26, csone);
        cell26.setCellStyle(csone);
        if (weekMap.getPlan_month_newsign() == null){
            cell26.setCellValue("");
        }else {
            cell26.setCellValue(weekMap.getPlan_month_newsign());
        }
      //  setCellValueStyle("plan_month_sign", weekMap, cell27, csone);
        cell27.setCellFormula("SUM(AB"+startRows+"+Z"+startRows+")");
        cell27.setCellStyle(csone);
      //  setCellValueStyle("plan_week_bearer_gap", weekMap, cell28, cs);
        cell28.setCellStyle(cs);
        if (weekMap.getPlan_week_bearer_gap()== null){
            cell28.setCellValue("");
        }else {
            cell28.setCellValue(weekMap.getPlan_week_bearer_gap());
        }
      //  setCellValueStyle("plan_week_sign_gap", weekMap, cell29, csone);
        cell29.setCellStyle(csone);
        if (weekMap.getPlan_week_sign_gap()== null){
            cell29.setCellValue("");
        }else {
            cell29.setCellValue(weekMap.getPlan_week_sign_gap());
        }
        cell30.setCellFormula("AC"+startRows+"-"+"J"+startRows);
        cell30.setCellStyle(csone);
      //  setCellValueStyle("plan_month_sign_gap", weekMap, cell30, csone);
        setCellValueStyle(weekMap.getGap_cause(), cell31, cs);
        setCellValueStyle( weekMap.getCause_details(), cell32, cs);
        setCellValueStyle( weekMap.getMinor_details(), cell33, cs);
        setCellValueStyle( weekMap.getDetailed_description(), cell34, cs);
        setCellValueStyle( weekMap.getPolicy_for_target(), cell35, cs);
        setCellValueStyle(weekMap.getRemarks(), cell36, cs);
    }

        /*???????????????????????????????????????*/
    private void setValueToExcelCellByFlow(Workbook targetWorkBook, XSSFRow positionRow, WeekMarkting weekMap,int startRows) {
        CellStyle cs = targetWorkBook.createCellStyle();
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setWrapText(true);

        CellStyle csone=targetWorkBook.createCellStyle();

        XSSFDataFormat format= (XSSFDataFormat) targetWorkBook.createDataFormat();
        csone.setBorderBottom(BorderStyle.THIN);
        csone.setBorderTop(BorderStyle.THIN);
        csone.setBorderLeft(BorderStyle.THIN);
        csone.setBorderRight(BorderStyle.THIN);
        csone.setAlignment(HorizontalAlignment.CENTER);
        csone.setDataFormat(format.getFormat("0.00"));




        XSSFCell cell0 = positionRow.createCell(0);

        XSSFCell cell1 = positionRow.createCell(1);
        XSSFCell cellreport = positionRow.createCell(2);
        XSSFCell cell2 = positionRow.createCell(3);
        XSSFCell cell3 = positionRow.createCell(4);
        XSSFCell cell4 = positionRow.createCell(5);
        XSSFCell cell5 = positionRow.createCell(6);
        XSSFCell cell6 = positionRow.createCell(7);
        XSSFCell cell7 = positionRow.createCell(8);
        XSSFCell cell8 = positionRow.createCell(9);
        XSSFCell cell9 = positionRow.createCell(10);
        XSSFCell cell10 = positionRow.createCell(11);
        XSSFCell cell11 = positionRow.createCell(12);
        XSSFCell cell12 = positionRow.createCell(13);
        XSSFCell cell13 = positionRow.createCell(14);
        XSSFCell cell14 = positionRow.createCell(15);
        XSSFCell cell15 = positionRow.createCell(16);
        XSSFCell cell16 = positionRow.createCell(17);
        XSSFCell cell17 = positionRow.createCell(18);
        XSSFCell cell18 = positionRow.createCell(19);
        XSSFCell cell19 = positionRow.createCell(20);
        XSSFCell cell20 = positionRow.createCell(21);
        XSSFCell cell21 = positionRow.createCell(22);
        XSSFCell cell22 = positionRow.createCell(23);
        XSSFCell cell23 = positionRow.createCell(24);
        XSSFCell cell24 = positionRow.createCell(25);
        XSSFCell cell25 = positionRow.createCell(26);
        XSSFCell cell26 = positionRow.createCell(27);
        XSSFCell cell27 = positionRow.createCell(28);
        XSSFCell cell28 = positionRow.createCell(29);
        XSSFCell cell29 = positionRow.createCell(30);
        XSSFCell cell30 = positionRow.createCell(31);
        XSSFCell cell31 = positionRow.createCell(32);
        XSSFCell cell32 = positionRow.createCell(33);
        XSSFCell cell33 = positionRow.createCell(34);
        XSSFCell cell34 = positionRow.createCell(35);
        XSSFCell cell35 = positionRow.createCell(36);
        XSSFCell cell36 = positionRow.createCell(37);
     /*
        XSSFCell cell0 = positionRow.createCell(0);
        XSSFCell cell1 = positionRow.createCell(1);
        XSSFCell cell2 = positionRow.createCell(2);
        XSSFCell cell3 = positionRow.createCell(3);
        XSSFCell cell4 = positionRow.createCell(4);
        XSSFCell cell5 = positionRow.createCell(5);
        XSSFCell cell6 = positionRow.createCell(6);
        XSSFCell cell7 = positionRow.createCell(7);
        XSSFCell cell8 = positionRow.createCell(8);
        XSSFCell cell9 = positionRow.createCell(9);
        XSSFCell cell10 = positionRow.createCell(10);
        XSSFCell cell11 = positionRow.createCell(11);
        XSSFCell cell12 = positionRow.createCell(12);
        XSSFCell cell13 = positionRow.createCell(13);
        XSSFCell cell14 = positionRow.createCell(14);
        XSSFCell cell15 = positionRow.createCell(15);
        XSSFCell cell16 = positionRow.createCell(16);
        XSSFCell cell17 = positionRow.createCell(17);
        XSSFCell cell18 = positionRow.createCell(18);
        XSSFCell cell19 = positionRow.createCell(19);
        XSSFCell cell20 = positionRow.createCell(20);
        XSSFCell cell21 = positionRow.createCell(21);
        XSSFCell cell22 = positionRow.createCell(22);
        XSSFCell cell23 = positionRow.createCell(23);
        XSSFCell cell24 = positionRow.createCell(24);
        XSSFCell cell25 = positionRow.createCell(25);
        XSSFCell cell26 = positionRow.createCell(26);
        XSSFCell cell27 = positionRow.createCell(27);
        XSSFCell cell28 = positionRow.createCell(28);
        XSSFCell cell29 = positionRow.createCell(29);
        XSSFCell cell30 = positionRow.createCell(30);
        XSSFCell cell31 = positionRow.createCell(31);
        XSSFCell cell32 = positionRow.createCell(32);
        XSSFCell cell33 = positionRow.createCell(33);
        XSSFCell cell34 = positionRow.createCell(34);
        XSSFCell cell35 = positionRow.createCell(35);
        XSSFCell cell36 = positionRow.createCell(36);*/
        setCellValueStyle(weekMap.getProject_name(), cell0, cs);
        setCellValueStyle(weekMap.getReport_time(),cellreport,cs);
        setCellValueStyle(weekMap.getPlan_status(), cell1, cs);
        setCellValueStyle(weekMap.getProject_code(), cell2, cs);
        setCellValueStyle(weekMap.getTrader_name(), cell3, cs);

        setCellValueStyle(weekMap.getIslast(), cell4, cs);

        setCellValueStyle( weekMap.getIssales_trade(), cell5, cs);
        cell6.setCellStyle(csone);
        if (weekMap.getSubscribe_price() == null){
            cell6.setCellValue("");
        }else {
            cell6.setCellValue(weekMap.getSubscribe_price());
        }
        cell7.setCellStyle(cs);
        if (weekMap.getTarget_month_bearer()== null){
            cell7.setCellValue("");
        }else {
            cell7.setCellValue(weekMap.getTarget_month_bearer());
        }
        cell8.setCellStyle(csone);
        if (weekMap.getTarget_month_sign() == null){
            cell8.setCellValue("");
        }else {
            cell8.setCellValue(weekMap.getTarget_month_sign());
        }
        cell9.setCellStyle(cs);
        if (weekMap.getTarget_week_bearer() == null){
            cell9.setCellValue("");
        }else {
            cell9.setCellValue( weekMap.getTarget_week_bearer());
        }

        // setCellValueStyle("subscribe_price", weekMap, cell6, csone);
        //  setCellValueStyle("target_month_bearer", weekMap, cell7, cs);
        // setCellValueStyle("target_month_sign", weekMap, cell8, csone);
        //  setCellValueStyle("target_week_bearer", weekMap, cell9, cs);
        //   setCellValueStyle("target_week_sign", weekMap, cell10, csone);
        cell10.setCellStyle(csone);
        if (weekMap.getTarget_week_sign() == null){
            cell10.setCellValue("");
        }else {
            cell10.setCellValue(weekMap.getTarget_week_sign());
        }
        //   weekMap.put("target_week_bearer_per",((BigDecimal)weekMap.get("target_week_bearer_per")).doubleValue()*100+"%") ;
        //  setCellValueStyle("target_week_bearer_per", weekMap, cell11, csone);
        cell11.setCellStyle(csone);
        if (weekMap.getTarget_week_bearer_per()== null){
            cell11.setCellValue("");
        }else {
            cell11.setCellValue(weekMap.getTarget_week_bearer_per()+"%");
        }

        //   weekMap.put("target_week_sign_per",((BigDecimal)weekMap.get("target_week_sign_per")).doubleValue()*100+"%") ;
        // setCellValueStyle("target_week_sign_per", weekMap, cell12, csone);
        cell12.setCellStyle(csone);
        if (weekMap.getTarget_week_sign_per() == null){
            cell12.setCellValue("");
        }else {
            cell12.setCellValue(weekMap.getTarget_week_sign_per()+"%");
        }
        // setCellValueStyle("fact_month_bearer_total", weekMap, cell13, cs);
        cell13.setCellStyle(cs);
        if (weekMap.getFact_month_bearer_total() == null){
            cell13.setCellValue("");
        }else {
            cell13.setCellValue(weekMap.getFact_month_bearer_total());
        }
        //  weekMap.put("fact_month_bearer_per",((BigDecimal)weekMap.get("fact_month_bearer_per")).doubleValue()*100+"%") ;
        // setCellValueStyle("fact_month_bearer_per", weekMap, cell14, csone);
        cell14.setCellStyle(csone);
        if (weekMap.getFact_month_bearer_per()== null){
            cell14.setCellValue("");
        }else {
            cell14.setCellValue(weekMap.getFact_month_bearer_per()+"%");
        }

        // setCellValueStyle("fact_week_bearer", weekMap, cell15, cs);
        cell15.setCellStyle(cs);
        if (weekMap.getFact_week_bearer()== null){
            cell15.setCellValue("");
        }else {
            cell15.setCellValue(weekMap.getFact_week_bearer());
        }
        //  weekMap.put("fact_week_bearer_per",((BigDecimal)weekMap.get("fact_week_bearer_per")).doubleValue()*100+"%") ;
        //   setCellValueStyle("fact_week_bearer_per", weekMap, cell16, csone);

        cell16.setCellStyle(csone);
        if (weekMap.getFact_week_bearer_per()== null){
            cell16.setCellValue("");
        }else {
            cell16.setCellValue(weekMap.getFact_week_bearer_per()+"%");
        }
        // setCellValueStyle("fact_month_sign", weekMap, cell17, csone);
        cell17.setCellStyle(csone);
        if (weekMap.getFact_month_sign()== null){
            cell17.setCellValue("");
        }else {
            cell17.setCellValue(weekMap.getFact_month_sign());
        }
        //  weekMap.put("fact_month_sign_per",((BigDecimal)weekMap.get("fact_month_sign_per")).doubleValue()*100+"%") ;
        //   setCellValueStyle("fact_month_sign_per", weekMap, cell18, csone);
        cell18.setCellStyle(csone);
        if (weekMap.getFact_month_sign_per()== null){
            cell18.setCellValue("");
        }else {
            cell18.setCellValue(weekMap.getFact_month_sign_per()+"%");
        }




        //  setCellValueStyle("fact_week_sign", weekMap, cell19, csone);
        cell19.setCellStyle(csone);
        if (weekMap.getFact_week_sign()== null){
            cell19.setCellValue("");
        }else {
            cell19.setCellValue(weekMap.getFact_week_sign());
        }
        //   weekMap.put("fact_week_sign_per",((BigDecimal)weekMap.get("fact_week_sign_per")).doubleValue()*100+"%") ;
        //   setCellValueStyle("fact_week_sign_per", weekMap, cell20, csone);
        cell20.setCellStyle(csone);
        if (weekMap.getFact_week_sign_per() == null){
            cell20.setCellValue("");
        }else {
            cell20.setCellValue(weekMap.getFact_week_sign_per()+"%");
        }

        // setCellValueStyle("fact_signed", weekMap, cell21, csone);
        //  setCellValueStyle("plan_reserve", weekMap, cell22, csone);
        //  setCellValueStyle("plan_signed", weekMap, cell23, csone);
        // setCellValueStyle("plan_month_lock_price", weekMap, cell24, cs);
        cell21.setCellStyle(csone);
        cell22.setCellStyle(csone);
        cell23.setCellStyle(csone);
        if (weekMap.getFact_signed()== null){
            cell21.setCellValue("");
        }else {
            cell21.setCellValue(weekMap.getFact_signed());
        }
        if (weekMap.getPlan_reserve()== null){
            cell22.setCellValue("");
        }else {
            cell22.setCellValue(weekMap.getPlan_reserve());
        }
        if (weekMap.getPlan_signed()== null){
            cell23.setCellValue("");
        }else {
            cell23.setCellValue(weekMap.getPlan_signed());
        }
        cell24.setCellStyle(cs);
        if (weekMap.getPlan_month_lock_price()== null){
            cell24.setCellValue("");
        }else {
            cell24.setCellValue(weekMap.getPlan_month_lock_price());
        }
        //   setCellValueStyle("plan_month_lock_per", weekMap, cell25, csone);
        cell25.setCellStyle(csone);
        if (weekMap.getPlan_month_lock_per()==null){
            cell25.setCellValue("");
        }else {
            cell25.setCellValue(weekMap.getPlan_month_lock_per()+"%");
        }

        //  setCellValueStyle("plan_month_newsign", weekMap, cell26, csone);
        cell26.setCellStyle(csone);
        if (weekMap.getPlan_month_newsign() == null){
            cell26.setCellValue("");
        }else {
            cell26.setCellValue(weekMap.getPlan_month_newsign());
        }
        //  setCellValueStyle("plan_month_sign", weekMap, cell27, csone);
        if (weekMap.getPlan_month_sign()== null){
            cell27.setCellValue("");
        }else {
            cell27.setCellValue(weekMap.getPlan_month_sign());
        }
        cell27.setCellStyle(csone);
        //  setCellValueStyle("plan_week_bearer_gap", weekMap, cell28, cs);
        cell28.setCellStyle(cs);
        if (weekMap.getPlan_week_bearer_gap()== null){
            cell28.setCellValue("");
        }else {
            cell28.setCellValue(weekMap.getPlan_week_bearer_gap());
        }
        //  setCellValueStyle("plan_week_sign_gap", weekMap, cell29, csone);
        cell29.setCellStyle(csone);
        if (weekMap.getPlan_week_sign_gap() == null){
            cell29.setCellValue("");
        }else {
            cell29.setCellValue(weekMap.getPlan_week_sign_gap());
        }
        if (weekMap.getPlan_month_sign_gap()== null){
            cell30.setCellValue("");
        }else {
            cell30.setCellValue(weekMap.getPlan_month_sign_gap());
        }

        cell30.setCellStyle(csone);
        //  setCellValueStyle("plan_month_sign_gap", weekMap, cell30, csone);
        setCellValueStyle(weekMap.getGap_cause(), cell31, cs);
        setCellValueStyle( weekMap.getCause_details(), cell32, cs);
        setCellValueStyle( weekMap.getMinor_details(), cell33, cs);
        setCellValueStyle( weekMap.getDetailed_description(), cell34, cs);
        setCellValueStyle( weekMap.getPolicy_for_target(), cell35, cs);
        setCellValueStyle(weekMap.getRemarks(), cell36, cs);
    }



    private void setCellValueStyle(String s,  XSSFCell cell, CellStyle cs) {
            if(s==null){
                cell.setCellValue("");
            }else {
                cell.setCellValue(s);
            }


        cell.setCellStyle(cs);
    }


    private void selectAllMonthPlanByFatherId(List<WeekMarkting> lists, int row, WeekMarkting map, String thisTime, int howWeek, int weeklyType,String areaReport) {


        rowNum = row + 1;
        map.setRow(rowNum);
        map.setThis_time(thisTime);
        map.setHow_week(howWeek);
        map.setWeeklyType(weeklyType);

        List<WeekMarkting> childPlanData = weeklyMarketingDao.selectWeekMarketByFatherIdByExport(map);

        if (childPlanData != null && childPlanData.size() != 0) {
            for (WeekMarkting child : childPlanData) {
                child.setBoss(map.getBoss());
                child.setAreaReport(areaReport);
                lists.add(child);
                selectAllMonthPlanByFatherId(lists, rowNum, child, thisTime, howWeek, weeklyType,areaReport);
            }
        }


    }

    /*
     * ????????????????????????
     * */
    @Override
   public Map updateCheckeds(Map map){


     Integer result=  weeklyMarketingDao.updateCheckeds(map);
            if( map.get("type").toString() .equals("3")){
              weeklyMarketingDao.ProjectSelectupdatesum(map);

            }

        if(map.get("area_id") !=null && map.get("type").toString(). equals("3")){
            map.put("project_id",map.get("area_id"));
            map.put("business_unit_id" ,null);
          List<Map> resultMap=  weeklyMarketingDao.ProjectExamineWeekly(map);
          return resultMap.get(0);
        }
        return  null;
    }
    /*???????????????*/
        @Override
        public String reportExcel(HttpServletRequest request, HttpServletResponse response,Map map){





            String creator= map.get("creator")+"";
            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           String createTime=sf.format(new Date());
           String thisTime= map.get("this_time")+"";
            int howWeek=Integer.parseInt(map.get("how_week")+"");
            String projectName= map.get("project_name")+"";
            String thisDate=  map.get("Date")+"";
            String projectId=  map.get("project_id")+"";
            String id=  map.get("id")+"";
        String UserID= map.get("userId")+"";
            Map flowMap=new HashMap();
            String buinsData[] = {"10010000", "10060000", "10080000", "10020000", "10040000", "10270000"};
            String orgNameData[] = {"??????", "??????", "??????", "??????", "??????", "??????"};
            List<String> asList = Arrays.asList(buinsData);
            int indexOf = asList.indexOf(projectId);
            if (indexOf == -1) {
                flowMap.put("orgname", "?????????");
            } else {
                flowMap.put("orgname", orgNameData[indexOf]);
            }
            flowMap.put("creator",creator);
            flowMap.put("zddate",createTime);
            flowMap.put("baseId",id);
            flowMap.put("jsonId",UUID.randomUUID().toString());
            flowMap.put("flowType","week_area");
            flowMap.put("flowCode","week_area_report");
            flowMap.put("projectId",projectId);
            flowMap.put("stageId",projectId);
            flowMap.put("flowStatus",1);
           String uuid=  UUID.randomUUID().toString();
            flowMap.put("id",uuid );
            /*????????????????????????*/
            String areaReport=projectName+thisDate+"??????";
            flowMap.put("title",areaReport);





            /*
             * ???excel????????????????????????????????????????????????areaReport???????????????????????????????????????????????????????????????
             * */

                    weeklyDataExport(request, response,thisTime, howWeek, Constant.PREPARED_BY_UNIT_TYPE_REGION,projectId,areaReport);
           Map returnMap=new HashMap();


            /*
             * ???????????????,??????????????????????????????????????????4??????????????????????????????
             * ????????????????????????????????????????????????????????????????????????
             *
             * */
            Map checkedsMap=new HashMap();
            checkedsMap.put("project_id",projectId);
            checkedsMap.put("this_time",thisTime);
            checkedsMap.put("how_week",howWeek);
          Map resultCheckdes=  weeklyMarketingDao.selectCheckeds(checkedsMap);
            if(Integer.parseInt(resultCheckdes.get("plan_status")+"")>0 ){
                return "?????????";
            }

            Map flowIdMap = weeklyMarketingDao.getFlowId(flowMap.get("baseId")+"");
            if(flowIdMap!=null && flowIdMap.size()>0){
                System.out.println(flowIdMap.toString()+"flowIdMap");
                if((flowIdMap.get("flow_status")+"").equals("3")){
                    return "??????";
                }else {
                    flowMap.remove("flowStatus");
                    flowMap.remove("jsonId");
                    weeklyMarketingDao.updateFlowData(flowMap);

                    return    flowIdMap.get("json_id")+"";

                }

            }else {
                packageDiscountDao.createFlowData(flowMap);
            }
            /*
             * ???????????????????????????
             * */
            returnMap.put("ID",UUID.randomUUID().toString());
            /*?????????*/
            returnMap.put("FileNameSuffix", ".xlsx");
            /*???????????????*/
            returnMap.put("FileNameOld", areaReport+".xlsx");
            /*?????????????????????*/
            returnMap.put("SaveUrl", relepath+File.separator+"weekAreaFile"+File.separator+areaReport+".xlsx"+"?n="+areaReport+".xlsx");
            /*????????????*/
            returnMap.put("FileSize", null);
            /*?????????*/
           returnMap.put("CreateUser", StringUtil.isEmpty(UserID)?"":UserID);
            /*????????????*/
            returnMap.put("CreateTime", new Date());

            /*????????????id*/
           returnMap.put("BizID", flowMap.get("baseId"));
            manager.insertAttach(returnMap);


            return  flowMap.get("jsonId")+"";
        }


}


