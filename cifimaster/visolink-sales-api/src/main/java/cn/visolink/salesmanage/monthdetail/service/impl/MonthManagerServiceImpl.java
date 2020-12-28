package cn.visolink.salesmanage.monthdetail.service.impl;




import cn.visolink.utils.DateUtil;
import cn.visolink.utils.StringUtil;
import cn.visolink.exception.BadRequestException;
import cn.visolink.salesmanage.monthdetail.dao.MonthManagerMapper;
import cn.visolink.salesmanage.monthdetail.service.MonthManagerService;
//import com.sun.xml.internal.bind.v2.model.core.ID;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class MonthManagerServiceImpl implements MonthManagerService {

    @Autowired
    MonthManagerMapper manager;

    @Value("${loadIp.thisIp}")
    private String thisIp;

    @Value("${uploadPath}")
    private  String uplodepath;

    @Value("${relepath}")
    private  String relepath;


    /**
     * 通过项目ID和月份来查询表二信息（月度计划明细）
     * @param
     * @return
     */
    @Override
    public Map<String,Object> mouthPlanSelect(String userId, String projectId, String months,Integer isEffective){

        try {

        if(isEffective==null){
            isEffective=0;
        }


        Map<String,Object> map=new HashMap<>();
        map.put("projectId",projectId);
        map.put("months",months);
        map.put("isEffective",isEffective);
            map.put("userId",userId);
           Map<String,Object> mouthPlanSelect= manager.mouthPlanSelect(map);

            return mouthPlanSelect;
        }catch(Exception e){
            e.printStackTrace();
            throw new BadRequestException(-14_1001,e);
        }
    }

    /**
     * 通过项目ID和月份来查询表二信息,若没有数据则初始化，被controller调用（月度计划明细）
     * @param
     * @return
     */
    @Override
    public Map<String,Object> allMouthPlanSelect(String userId,String projectId, String months,Integer isEffective){

        /*
         * 以下是需要遍历到表二的字段，表一里的（签约套数  认购套数 签约资金 认购资金）活动资金
         * */
        /*
         * 时间类型转换
         * */
        if(months.indexOf("-")<0){
            long longtime=Long.parseLong(months);
            months=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longtime));
        }

        Map<String,Object> map=new HashMap<>();
        map.put("projectId",projectId);
        map.put("months",months);
        map.put("isEffective",isEffective);
        /*
         * 表二外表字段
         * */
        Map<String,Object> TotalAndPlan=  manager.selectTotalAndPlan(map);
        /*
         * 表二二下费用
         * */
        Map<String,Object> cost= manager.selectPromotionCost(map);
                /*
                * 表二字段
                * */
        Map<String,Object> mouthPlan= mouthPlanSelect(userId,projectId,months,isEffective);
            /*
            * 如果用未上报状态（0）查找不到 被上报的数据，就用上报状态（1）再查一遍，
            * 但如果传进来的本身就是1，那么再查一次结果也一样，这样是为了让0状态找到被上报
            * 的数据
            * */
        if (mouthPlan==null ||mouthPlan.size()<1) {
            mouthPlan= mouthPlanSelect(userId,projectId,months,1);
        }
        /*
         * 如果用未上报状态（0）查找不到 被上报的数据，就用上报状态（2）再查一遍，
         * ，这样是为了让0状态找到被上报
         * 的数据
         * */
        if (mouthPlan==null ||mouthPlan.size()<1) {
            mouthPlan= mouthPlanSelect(userId,projectId,months,2);
        }
            if (mouthPlan!=null && mouthPlan.size()>0){
                /*二下费用
                * */
                    if(cost!=null && cost.size()>0){
                        mouthPlan.put("marketing_promotion_cost",cost.get("marketing_promotion_cost"));
                    }

                if(TotalAndPlan!=null && TotalAndPlan.size()>0){
                    /*
                     * 表二前端的字段有一些来自别的表，放进去
                     * */
                    mouthPlan.putAll(TotalAndPlan);

                    /*
                    * 更新表二外表的字段
                    * */
                    mouthPlan.put("sign_funds", TotalAndPlan.get("total_sign_funds"));
                    mouthPlan.put("sign_number_set", TotalAndPlan.get("total_sign_set"));
                    mouthPlan.put("subscription_number", TotalAndPlan.get("plan_subscription_set"));
                    mouthPlan.put("subscription_funds", TotalAndPlan.get("plan_subscription_funds"));

                }
               return mouthPlan;
            }
        /*
         * 若走到这一步说明没有数据，若状态码不等于0就不初始化，直接返回NULL
         * */
        if(isEffective!=0){
            return null;
        }

            /*
            * 若没有数据则走此方法
            * */
             initialMouthPlan(projectId,months);

       mouthPlan=mouthPlanSelect(userId,projectId,months,isEffective);

        /*
        * 前端有的字段名是和TotalAndPlan里的字段名，所以放进去
        * */
        if(TotalAndPlan!=null && TotalAndPlan.size()>0){

            mouthPlan.putAll(TotalAndPlan);
        }
        if(cost!=null && cost.size()>0){
            mouthPlan.put("marketing_promotion_cost",cost.get("marketing_promotion_cost"));
        }
            return mouthPlan;
    }

        /**
         * 通过项目ID和月份来初始化（月度计划明细）
         * @param
         * @return
         */
    @Override
    public Integer initialMouthPlan(String projectId, String months) {
        try {


            String guid = UUID.randomUUID().toString();

            Map<String, Object> map = new HashMap<>();
            map.put("projectId", projectId);
            map.put("months", months);
            /*  先从表一里获取营销推广费用和计划签约套等字段
             *  再初始化到表二里
             * */
           // Map<String, Object> PromotionCost = manager.selectPromotionCost(map);

            Map<String, Object> TotalAndPlan = manager.selectTotalAndPlan(map);
                /*
                * 费用暂时不从二下拿
                * */
            /*if(PromotionCost!=null && PromotionCost.size()>0){
           }*/

            if(TotalAndPlan!=null && TotalAndPlan.size()>0){
                map.put("sign_funds", TotalAndPlan.get("total_sign_funds"));
                map.put("sign_number_set", TotalAndPlan.get("total_sign_set"));
                map.put("subscription_number", TotalAndPlan.get("plan_subscription_set"));
                map.put("subscription_funds", TotalAndPlan.get("plan_subscription_funds"));
            }
            map.put("cost", 0);

            map.put("guid", guid);

            int i = manager.selectMouthPlan(map);
            if(i<1){
                return manager.initialMouthPlan(map);
            }else{
                return i;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(-14_1002, e);
        }
    }
    /**
     * 通过项目ID和月份来跟新表二信息（月度计划明细）
     * @param map
     * @return
     */
    @Override
    public Integer mouthPlanUpdate( Map<String, Object> map){
        try{
               if(map.get("project_id")!=null){
                map.put("projectId",map.get("project_id"));
            }

               /*
               * 时间类型转换
               * */
            long data=0;
            if (map.get("months") != null && map.get("months").getClass().isInstance(data)) {
                long longtime=(long)map.get("months");
                String months=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longtime));
                map.put("months",months);
            }

            /*
            * 遍历Map所有值，若传进来的数有空，则将它默认为0
            * */
            Iterator iterable= map.entrySet().iterator();
            while (iterable.hasNext()) {
                Map.Entry entry_d = (Map.Entry) iterable.next();
                Object key = entry_d.getKey();
                Object value = entry_d.getValue();
                if(value==null || value==""){
                    value=0;
                }

                map.put(key.toString(),value);
            }



      return manager.mouthPlanUpdate(map);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(-14_1003, e);

        }
    }

    /**
     * 通过项目ID和月份来设置表二（月度计划明细）是否生效状态,暂时被合并到表四上报里
     * @param
     * @return
     */
    @Override
    public Integer mouthPlanEffective(String projectId, String months,Integer isEffective){
        try {
        Map<String,Object> map=new HashMap<>();
        map.put("projectId",projectId);
        map.put("months",months);
        map.put("isEffective",isEffective);
        return manager.mouthPlanEffective(map);
        }catch (Exception e){
            e.printStackTrace();
            throw new BadRequestException(-14_1004,e);
        }
    }




    /**
     * 通过项目ID和月份来设置表 四（月度计划明细）里面的风险
     * @param map
     * @return
     */
    @Override
    public Integer mouthPlanUpdateRisk(Map<String, Object> map){
        try {
           if(map.get("project_id")!=null){
                map.put("projectId",map.get("project_id"));
            }
            /*
             * 转换LONG为日期
             **/
            long a=0;
            if (map.get("months") != null && map.get("months").getClass().isInstance(a)) {

                    long longtime = (long) map.get("months");
                    String months = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longtime));
                    map.put("months", months);
                }
            return  manager.mouthPlanUpdateRisk(map);
        }catch (Exception e){
            e.printStackTrace();
            throw new BadRequestException(-14_1005,e);
        }
    }


    /**
     * 初始化招揽客人的渠道费用明细并表示到前端去
     *
     * @return
     */
    @Override
    public List<Map> initialiseChannelDetail(String projectId, String months,Integer State){


        /*若再渠道费用明细表里没有对应的项目ID和月份，就先将初始化一张明细表
         * 循环所有的招揽客人渠道，并初始化渠道费用明细，先在费用明细表里填上GUID，MATTER ACTION三个字段，其他都置为0
         * 再将项目ID和对应的月份插入到字段中去
         * */
        try{
        List<Map> result=manager.allChannelSelect();
            System.out.println("result"+result.toString());
        /*
        * 先拿到年和月份
        * */
            Integer trimyear=Integer.parseInt(months.substring(0,months.indexOf("-")));

            Integer  trimthree=Integer.parseInt(months.substring(months.indexOf("-")+1,months.indexOf("-")+3));
          /*再减掉三个月,前三月*/
                 Integer agoyear=trimyear;
                 Integer agomonth=trimthree-3;
                  if(agomonth<1){
                      agomonth=(12+trimthree)-3;
                      agoyear--;
                  }
                  /*
                 *
                 算出前第二个月
                 * */
            Integer twomonthagoyear=trimyear;
            Integer twomonthagomonth=trimthree-2;
            if(twomonthagomonth<1){
                twomonthagomonth=(12+trimthree)-2;
                twomonthagoyear--;
            }

            /*算出上个月，也就是减掉1个月*/
            Integer nowyearone=trimyear;
            Integer nowmonthone=trimthree-1;
            if(nowmonthone<1){
                nowmonthone=(12+trimthree)-1;
                nowyearone--;
            }

            /*
            * 前三月初始时间
            * */
                String BeginDate=agoyear+"-"+(agomonth<10? "0"+agomonth:agomonth)+"-01";
            /*
             * 前三月结束时间
             * */
            String endDateThree=agoyear+"-"+(agomonth<10? "0"+agomonth:agomonth)+"-31";
            /*
            * 前二月开始时间
            * */
            String twoMonthBeginDate=twomonthagoyear+"-"+(twomonthagomonth<10? "0"+twomonthagomonth:twomonthagomonth)+"-01";
            /*
             * 前二月结束时间
             * */
            String twoMonthEndDate=twomonthagoyear+"-"+(twomonthagomonth<10? "0"+twomonthagomonth:twomonthagomonth)+"-31";

            /*
             * 前一月开始时间
             * */
            String lastBeginDate=nowyearone+"-"+(nowmonthone<10? "0"+nowmonthone:nowmonthone)+"-01";

            /*
            * 前一月结束时间
            * */
            String EndDate=nowyearone+"-"+(nowmonthone<10? "0"+nowmonthone:nowmonthone)+"-31";
              /*
                * 先找到所有对应策划渠道，有三个月平均来人量和平均成交套数的结果
                * */
            /*
             *找到表三里需要求出前三月月均成交成本的数据
             * 因为并不是每个渠道都有对应，所以找到可以查询的渠道然后来计算，
             * 需要的参数是项目ID，上三个月年月
             * */
            Map<String,Object> oneDealCostMap=new HashMap<>();
            oneDealCostMap.put("projectId",projectId);
            oneDealCostMap.put("year",agoyear);
            oneDealCostMap.put("month",agomonth);
            oneDealCostMap.put("BeginDate",BeginDate);
            oneDealCostMap.put("EndDate",endDateThree);
            List<Map> threeDeallistmap = manager.threeMonthDealCost(oneDealCostMap);

            /*
            * 第二个月
            * */
            Map<String,Object> twoDealCostMap=new HashMap<>();
            twoDealCostMap.put("projectId",projectId);
            twoDealCostMap.put("year",twomonthagoyear);
            twoDealCostMap.put("month",twomonthagomonth);
            twoDealCostMap.put("BeginDate",twoMonthBeginDate);
            twoDealCostMap.put("EndDate",twoMonthEndDate);
            List<Map> twoDeallistmap=manager.threeMonthDealCost(twoDealCostMap);

            /*
            * 上个月的
            * */

            Map<String,Object> threeDealCostMap=new HashMap<>();
            threeDealCostMap.put("projectId",projectId);
            threeDealCostMap.put("year",nowyearone);
            threeDealCostMap.put("month",nowmonthone);
            threeDealCostMap.put("BeginDate",lastBeginDate);
            threeDealCostMap.put("EndDate",EndDate);
            List<Map> oneDeallistmap=manager.threeMonthDealCost(threeDealCostMap);
            /*
             * 现在得到了所有求出前三月成交成本的条件
             * */

            /*
            * 拿成交套数
            * */
            List<Map> MonthlySets=new ArrayList<>();



                Map<String,Object> cnpmap=new HashMap<>();
                cnpmap.put("HKBProjectID",projectId);
                cnpmap.put("BeginDate",BeginDate);
                cnpmap.put("EndDate",EndDate+" 23:59:59");



            MonthlySets= manager.selectAverageMonthlySets(cnpmap);



                /*
                * 循环所有渠道，然后找到相对应的有权责费用和来人量
                * */

                for (int i=0;i<result.size();i++){

                    String channelId=(String)result.get(i).get("guid").toString();
                    String matterName=(String)result.get(i).get("matter_name").toString();
                    String actionName=(String)result.get(i).get("action_name").toString();

                    String MediaTypeGUID=null;
                    if(result.get(i).get("MediaTypeGUID")!=null ){
                        MediaTypeGUID=(String)result.get(i).get("MediaTypeGUID").toString();
                    }
                    System.out.println(MediaTypeGUID+"MediaTypeGUID");
                    String guid = UUID.randomUUID().toString();

                    Map<String,Object> channel=new HashMap<>();

                    channel.put("guid",guid);
                    channel.put("channel_id",channelId);
                    channel.put("matter",matterName);
                    channel.put("action",actionName);
                    channel.put("projectId",projectId);
                    channel.put("months",months);
                    /*
                     * 前三月月均成交成本
                     * */
                    Double dealAvgCost=0.00;
                    /*
                     * 前三月月均来人量
                     * */
                    Integer dealAvgCnt=0;

                    /*
                    * 用在饼状图的，需要这个费用 biscuitMap
                    * */
                    List<Map> biscuitMap=new ArrayList<>();
                    System.out.println("oneDeallistmap"+oneDeallistmap.toString());
                    System.out.println("oneDeallistmap"+twoDeallistmap.toString());
                    System.out.println("oneDeallistmap"+threeDeallistmap.toString());
                    for(Map<String,Object> Deallistmap:oneDeallistmap){


                        biscuitMap.add(Deallistmap);
                        if(Deallistmap.get("MediaTypeGUID")!=null && Deallistmap.get("MediaTypeGUID").toString().equals(MediaTypeGUID)){

                            dealAvgCost+=Double.parseDouble(Deallistmap.get("ftAmounts").toString());
                            dealAvgCnt+=Integer.parseInt(Deallistmap.get("gjcount").toString());
                        }
                    }
                    for(Map<String,Object> Deallistmap:  twoDeallistmap){
                        biscuitMap.add(Deallistmap);
                        if(Deallistmap.get("MediaTypeGUID")!=null && Deallistmap.get("MediaTypeGUID").equals(MediaTypeGUID)){

                            dealAvgCost+=Double.parseDouble(Deallistmap.get("ftAmounts")+"");
                            dealAvgCnt+=Integer.parseInt(Deallistmap.get("gjcount")+"");
                        }
                    }
                    for(Map<String,Object> Deallistmap:  threeDeallistmap){
                        biscuitMap.add(Deallistmap);
                        if(Deallistmap.get("MediaTypeGUID")!=null && Deallistmap.get("MediaTypeGUID").equals(MediaTypeGUID)){

                            dealAvgCost+=Double.parseDouble(Deallistmap.get("ftAmounts").toString());
                            dealAvgCnt+=Integer.parseInt(Deallistmap.get("gjcount").toString());
                        }
                    }
                    System.out.println("dealAvgCost"+dealAvgCost+""+dealAvgCnt);
                    if(State==1){
                        /*
                        * 得到饼状图需要的数据，直接返回
                        * */
                        return biscuitMap;
                    }
                    /*
                    * 该渠道的成交套数
                    * */
                    Integer SetsAvg=0;
                    for (int j=0;j<MonthlySets.size();j++){
                        /*
                         * 找出有值的渠道
                         * */
                        if (MonthlySets.get(j)!=null && MonthlySets.get(j).get("MediaTypeGUID")!=null) {
                            if (MonthlySets.get(j).get("MediaTypeGUID").equals(MediaTypeGUID)) {
                                /*
                                 * 得到前三个月每月平均成交套数
                                 * */
                                SetsAvg = Integer.parseInt(MonthlySets.get(j).get("AverageMonthlySets").toString());

                                SetsAvg = SetsAvg / 3;

                                break;
                            }

                        }   }
                    /*
                     * 现在已得到前三月总来人量，除以三等于月均来人量
                     * */



                    dealAvgCnt=dealAvgCnt/3;
                    /*前三月月均来人量
                     *
                     * */
                    channel.put("first_three_months_monthly_average_monthly_coming_amount",dealAvgCnt);
                    /*
                     * 前三月月均成交套数
                     * */
                    channel.put("first_three_months_average_monthly_sets",SetsAvg);

                    /*
                     * 循环三个月的费用后现在得到了该渠道在三个月内的总费用,
                     * 现在算平均，就除以3，得到每个月平均费用
                     * */
                    dealAvgCost=dealAvgCost/3;
                    /*  已得到每月各渠道的实际权责费用
                     * 现在算前三月每月平均成交成本，该费用除以平均认购套数
                     * 每月各渠道的实际权责费用 / 每月各渠道实际认购套数(取前三个月数据求平均值)
                     * */
                    Double dealMonthCost=0.00;
                    /*
                     * 前三月月均成交成本已得到
                     * */

                       Double dealMonthCostset=0.00;
                    if(SetsAvg!=0){
                        dealMonthCostset=dealAvgCost/SetsAvg;
                    }
                    /*
                     * 前三月每月平均成交成本
                     * */
                    channel.put("first_three_months_average_monthly_transaction_cost",dealMonthCostset.isNaN()?0.00:dealMonthCostset);


                    /*  已得到每月各渠道的实际权责费用
                     * 现在算前三月月均来人成本
                     * 每月各渠道的实际权责费用 / 每月各渠道实际来人量(取前三个月数据求平均值)
                     * */
                    Double dealComingCost=0.00;
                    if(dealAvgCnt!=0){
                        dealComingCost=dealAvgCost/dealAvgCnt;
                    }

                    /*
                     * 前三月月均来人成本
                     * */
                    channel.put("first_three_months_monthly_average_coming_cost",dealComingCost.isNaN()?0.00:dealComingCost);

                    /*
                     * 前三月月均成交率
                     * 每月各渠道的实际认购套数 / 每月各渠道实际来人量(取前三个月数据求平均值)
                     * */
                    Double turnoverRate=0.00;
                    if(dealAvgCnt!=0){
                        turnoverRate=(double)SetsAvg/dealAvgCnt;
                    }
                    /*
                    * 前三月月均成交率
                    * */
                    channel.put("first_three_months_monthly_average_turnover_rate",turnoverRate*100);
                        /*
                        * 初始化
                        * */
                    manager.initialMouthChannelDetail(channel);

                }
        /*
        * 此时表里已经有和项目ID 月份相对应的被初始化的渠道费用明细，将此表示到前端去
        * */
        Map<String,Object> map=new HashMap<>();
        map.put("projectId",projectId);
        map.put("months",months);
        return selectMouthChannelDetail(projectId,months,0);
        }catch (Exception e){
            e.printStackTrace();
            throw new BadRequestException(-14_1006,e);
        }
    }



    /**
     * 通过月份和项目ID来查找渠道费用明细
     *
     * @return
     */
    @Override
    public List<Map> selectMouthChannelDetail(String projectId, String months,Integer isEffective){
       try{
        Map<String,Object> map=new HashMap<>();
        map.put("projectId",projectId);
        map.put("months",months);

        map.put("isEffective",isEffective);
        /*
        * 得到渠道费用等表里数据
        * */
           List<Map> resultmap=  manager.selectMouthChannelDetail(map);

           /*
            * 获取前三个月算法
            * */
           /*
            * 先拿到年和月份
            * */
           Integer trimyear=Integer.parseInt(months.substring(0,months.indexOf("-")));

           Integer  trimthree=Integer.parseInt(months.substring(months.indexOf("-")+1,months.indexOf("-")+3));
           /*再减掉三个月*/
           Integer agoyear=trimyear;
           Integer agomonth=trimthree-3;
           if(agomonth<1){
               agomonth=(12+trimthree)-3;
               agoyear--;
           }
           /*算出当前月，也就是减掉1个月*/
           Integer nowyearone=trimyear;
           Integer nowmonthone=trimthree-1;
           if(nowmonthone<1){
               nowmonthone=(12+trimthree)-1;
               nowyearone--;
           }
           String BeginDate=agoyear+"-"+(agomonth<10? "0"+agomonth:agomonth)+"-01";
           String EndDate=nowyearone+"-"+(nowmonthone<10? "0"+nowmonthone:nowmonthone)+"-31";

             List<Map> realresultmap=new ArrayList<>();
           for(Map<String,Object> everymap:resultmap){

                 /*
              * 求出饼状图的数据，前三个月每个渠道费用在总渠道费用占比
              * */

               String actionname=everymap.get("action").toString();


        List<Map>actionresultmap =initialiseChannelDetail(projectId,months,1);
                        Double biscultEcharts=0.00;

                        /*
                        * 循环饼状图，找到每个对应的上的ACTIONNAME，然后找到里面的值并将它存储
                        * */
                   for(Map actionMap: actionresultmap){

                       if(actionMap.get("action_name").equals(actionname) && actionMap.get("finalftAmounts")!=null){
                           biscultEcharts+= Double.parseDouble(actionMap.get("finalftAmounts").toString()) ;
                       }
                   }
                    /*
                    * 饼状图的数据被存到表三里去
                    * */
               DecimalFormat df=new DecimalFormat("0.00");

               everymap.put("sumAction",df.format((biscultEcharts)) );
               realresultmap.add(everymap);
           }

            return realresultmap;
       }catch (Exception e){
           e.printStackTrace();
           throw new BadRequestException(-14_1008,e);
       }
    }
        /*
        * 查找表三下面柱状图的数据，每月matter金额和成交率
        **/
        @Override
    public List<Map> columnSelect(String projectId, String months,Integer isEffective){
            /*
            * 用来保留两位数
            * */
      DecimalFormat df=new DecimalFormat("0.00");

        List<Map> result=manager.allChannelSelect();
        /*
         * 先拿到年和月份
         * */
        Integer trimyear=Integer.parseInt(months.substring(0,months.indexOf("-")));

        Integer  trimthree=Integer.parseInt(months.substring(months.indexOf("-")+1,months.indexOf("-")+3));
        /*再减掉三个月*/
        Integer agoyear=trimyear;
        Integer agomonth=trimthree-3;
        if(agomonth<1){
            agomonth=(12+trimthree)-3;
            agoyear--;
        }

        /*算出上个月，也就是减掉1个月*/
        Integer nowyearone=trimyear;
        Integer nowmonthone=trimthree-1;
        if(nowmonthone<1){
            nowmonthone=(12+trimthree)-1;
            nowyearone--;
        }

            /*算出第二个月*/
            Integer twoyearone = trimyear;
            Integer twomonthone = trimthree - 2;
            if (twomonthone < 1) {
                twomonthone = (12 + trimthree) - 2;
                twoyearone--;
            }

              /*
               前一个月的时间
               * */
            String oneDate = nowyearone + "-" + (nowmonthone < 10 ? "0" + nowmonthone : nowmonthone);

            /*
               前两个月的时间
               * */
            String twoDate = twoyearone + "-" + (twomonthone < 10 ? "0" + twomonthone : twomonthone);
     /*
               前三个月的
               * */
            String threeDate = agoyear + "-" + (agomonth < 10 ? "0" + agomonth : agomonth);
            /*
            * 本月的，去掉日即可
            * */
         String  currentmonth= months.substring(0,months.lastIndexOf("-"));


            String BeginDate=agoyear+"-"+(agomonth<10? "0"+agomonth:agomonth)+"-01";
        String EndDate=nowyearone+"-"+(nowmonthone<10? "0"+nowmonthone:nowmonthone)+"-31";



                /*
                * 设置一个listmap来存所有的map
                * */
            List<Map> resultmap=new ArrayList<>();

            /*求出每个月的成交套数和来人量，来算出每个月的成交率
             * 上个月的
             * */
            Map<String,Object> oneDealCostMap=new HashMap<>();
            oneDealCostMap.put("projectId",projectId);
            oneDealCostMap.put("HKBProjectID",projectId);
            oneDealCostMap.put("year",nowyearone);
            oneDealCostMap.put("month",nowmonthone);
            oneDealCostMap.put("BeginDate",oneDate +"-01");
            oneDealCostMap.put("EndDate",oneDate +"-31");

            List<Map> oneDeallistmap= manager.threeMonthDealCost(oneDealCostMap);

            List<Map> oneDealSetsmap= manager.selectAverageMonthlySets(oneDealCostMap);

            /*
             * 第二个月
             * */
            Map<String,Object> twoDealCostMap=new HashMap<>();
            twoDealCostMap.put("projectId",projectId);
            twoDealCostMap.put("HKBProjectID",projectId);
            twoDealCostMap.put("year",twoyearone);
            twoDealCostMap.put("month",twomonthone);
            twoDealCostMap.put("BeginDate",twoDate+"-01");
            twoDealCostMap.put("EndDate",twoDate+"-31");

            List<Map> twoDeallistmap=manager.threeMonthDealCost(twoDealCostMap);
            List<Map> twoDealSetsmap= manager.selectAverageMonthlySets(twoDealCostMap);
            /*   d第三个月
             */
            Map<String,Object> threeDealCostMap=new HashMap<>();
            threeDealCostMap.put("projectId",projectId);
            threeDealCostMap.put("HKBProjectID",projectId);
            threeDealCostMap.put("year",agoyear  );
            threeDealCostMap.put("month", agomonth );
            threeDealCostMap.put("BeginDate",threeDate+"-01");
            threeDealCostMap.put("EndDate",threeDate+"-31");
            List<Map>  threeDeallistmap =manager.threeMonthDealCost(threeDealCostMap);
            List<Map> threeDealSetsmap= manager.selectAverageMonthlySets(threeDealCostMap);
 /*
            /*
            /*
            * 得到所有的Mattername
            * */
              List<Map> matternamemap=new ArrayList<>();

            for(int i=0;i<result.size();i++) {

                String MediaTypeGUID= null;
                if(result.get(i).get("MediaTypeGUID")!=null){
                    MediaTypeGUID= result.get(i).get("MediaTypeGUID").toString();
                }
                String actionName= result.get(i).get("action_name").toString();
                String matterName= result.get(i).get("matter_name").toString();
                /*
                * 循环四个装了权责费用的LIst,对应上action，然后找到对应的
                * mattername
                * */
                for(Map oneDealMap: oneDeallistmap){
                  if(oneDealMap.get("MediaTypeGUID")!=null && oneDealMap.get("MediaTypeGUID").toString().equals(MediaTypeGUID)){
                       result.get(i).put("oneSumMatter",oneDealMap.get("ftAmounts"));

                   }
                }

                for(Map twoDealmap: twoDeallistmap){

                    if(twoDealmap.get("MediaTypeGUID")!=null && twoDealmap.get("MediaTypeGUID").toString(). equals(MediaTypeGUID)){
                        result.get(i).put("twoSummatter",twoDealmap.get("ftAmounts"));
                    }
                }

                for(Map threeDealmap: threeDeallistmap){

                    if(threeDealmap.get("MediaTypeGUID")!=null && threeDealmap.get("MediaTypeGUID").toString(). equals(MediaTypeGUID)){
                        result.get(i).put("threeSummatter",threeDealmap.get("ftAmounts"));
                    }
                }


                if(i<result.size()-1 && !(result.get(i).get("matter_name").toString().equals(result.get(i+1).get("matter_name").toString()))){
                   matternamemap.add(result.get(i));

               }
            }

            /*
            * 现在取得4个月的 matter名字，然后遍历所有 action,若干个action属于一个
            * matter名字，把他们的4个月的柱状图加在一起
            *
            * */

        for(Map<String,Object> everymap:matternamemap) {
                 Double oneSumMatter=0.00;
                 Double twoSummatter=0.00;
                    Double threeSummatter=0.00;
                    Double currentSummatter=0.00;
            for(int i=0;i<result.size();i++) {
                if(result.get(i).get("matter_name").equals(everymap.get("matter_name"))){
                            if(result.get(i).get("oneSumMatter")!=null){
                                oneSumMatter+=  Double.parseDouble(result.get(i).get("oneSumMatter").toString()) ;
                            }
                    if(result.get(i).get("twoSummatter")!=null){
                        twoSummatter+=  Double.parseDouble(result.get(i).get("twoSummatter").toString()) ;
                    }
                    if(result.get(i).get("threeSummatter")!=null){
                        threeSummatter+=  Double.parseDouble(result.get(i).get("threeSummatter").toString()) ;
                    }
                }

            }
            /*
             * 本月的柱状
             * */

            Map<String,Object> currentDealCostMap=new HashMap<>();
            currentDealCostMap.put("projectId",projectId);
            currentDealCostMap.put("months",currentmonth);

            currentDealCostMap.put("matter",everymap.get("matter_name"));

              Map currentAction=manager. selectMouthChannelDetailAction(currentDealCostMap);
                    if(currentAction!=null && currentAction.size()>1){
                        currentSummatter=Double.parseDouble(currentAction.get("sumamount").toString()) ;
                    }
            everymap.put("oneSumMatter", df.format( oneSumMatter/10000));
            everymap.put("twoSummatter", df.format( twoSummatter/10000));
            everymap.put("threeSummatter", df.format( threeSummatter/10000));
            everymap.put("currentSummatter", df.format( currentSummatter));
            resultmap.add(everymap);
        }
            /*
             * 装四个月成交率折线的
             * */

            /*
             * 装前三个月的成交率
             * */
            Integer onemonthAmounts=0;
            Integer onemonthgjcount=0;
            /*
            * 每个月的成交套数和每个月的来人量
            * */
            for(Map oneDealMap: oneDeallistmap){
                onemonthgjcount+=Integer.parseInt(oneDealMap.get("gjcount").toString()) ;

            }
            for(Map oneDealSetsMap: oneDealSetsmap){
                onemonthAmounts+=Integer.parseInt(oneDealSetsMap.get("AverageMonthlySets").toString()) ;
            }

            Integer twomonthAmounts=0;
            Integer twomonthgjcount=0;
            for(Map twoDealmap: twoDeallistmap){
                twomonthgjcount+=Integer.parseInt(twoDealmap.get("gjcount").toString()) ;
            }
            for(Map twoDealmap: twoDealSetsmap){
                twomonthAmounts+=Integer.parseInt(twoDealmap.get("AverageMonthlySets").toString()) ;
            }
            Integer threemonthAmounts=0;
            Integer threemonthgjcount=0;
            for(Map threeDealmap: threeDeallistmap){

                threemonthgjcount+=Integer.parseInt(threeDealmap.get("gjcount").toString()) ;
            }
            for(Map threeDealmap: threeDealSetsmap){

                threemonthAmounts+=Integer.parseInt(threeDealmap.get("AverageMonthlySets").toString()) ;
            }
            /*
             * 装成交率合计的
             * */
            Double onemonthRate=0.000000;

                if(onemonthgjcount!=0){
                    onemonthRate=(double)onemonthAmounts/onemonthgjcount ;

                }

            Double twomonthRate=0.0000000;
            if(twomonthgjcount!=0){
                twomonthRate= (double)twomonthAmounts/twomonthgjcount ;
            }
            Double threemonthRate=0.000000;
            if(threemonthgjcount!=0){
                threemonthRate=(double)threemonthAmounts/threemonthgjcount;
            }
            Map<String,Object> transactionMap=new HashMap<>();
            transactionMap.put("matter_name","成交率");
            transactionMap.put("oneSumrate",df.format(onemonthRate) );

            transactionMap.put("twoSumrate",df.format(twomonthRate));

            transactionMap.put("threeSumrate",df.format(threemonthRate));
                /*
                当前月的直接从表上拿
                */
            Map<String, Object> oneTransmap = new HashMap<>();
            oneTransmap.put("projectId", projectId);
            oneTransmap.put("isEffective", isEffective);
            oneTransmap.put("months", currentmonth);
            Map<String, Object> everymap= manager.selectMouthChannelDetailAction(oneTransmap);
           Double currentmonthRate  =Double.parseDouble(everymap.get("nowtransactionRate").toString());
            transactionMap.put("currentSumrate",df.format(currentmonthRate));
            resultmap.add(transactionMap);


            /*
            * 每个渠道的折现
            * */
        for(Map everyresultmap:result){


            /*
             * 去掉错误渠道，后期该判断有可能删除
             * */
            if(everyresultmap.get("action_name")==null || everyresultmap.get("action_name").equals("")){
                continue;
            }


            /*
            设置一个MAP来装所有的前三月的成交率
            */
            Map<String,Object> everyactionmap=new HashMap<>();
            String action= everyresultmap.get("action_name").toString();
            String MediaTypeGUID=null;
            if(everyresultmap.get("MediaTypeGUID")!=null ){
                 MediaTypeGUID= everyresultmap.get("MediaTypeGUID").toString();
            }

                everyactionmap.put("matter_name",action);
                /*
                * 装每个渠道前三个月的每月的成交率
                * */
            Integer oneMonthsChannelAmounts=0;
            Integer oneMonthsChannelgjcount=0;
            for(Map oneDealMap: oneDeallistmap){
                if(oneDealMap.get("MediaTypeGUID")!=null && oneDealMap.get("MediaTypeGUID").toString(). equals(MediaTypeGUID)) {
                oneMonthsChannelgjcount = Integer.parseInt(oneDealMap.get("gjcount").toString());
                    break;
                } }
            for(Map oneDealMap: oneDealSetsmap){
                if(oneDealMap.get("MediaTypeGUID")!=null && oneDealMap.get("MediaTypeGUID").toString(). equals(MediaTypeGUID)) {
                    oneMonthsChannelAmounts = Integer.parseInt(oneDealMap.get("AverageMonthlySets").toString());
                    break;
                } }

            Double oneMonthsChannelRate=0.00;
            if(oneMonthsChannelgjcount!=0){
                oneMonthsChannelRate=(double)oneMonthsChannelAmounts/oneMonthsChannelgjcount ;
            }
                       everyactionmap.put("oneSumrate",df.format(oneMonthsChannelRate));


            Integer twoMonthsChannelAmounts=0;
            Integer twoMonthsChannelgjcount=0;
            for(Map twoDealmap: twoDeallistmap){
                if(twoDealmap.get("MediaTypeGUID")!=null && twoDealmap.get("MediaTypeGUID").toString(). equals(MediaTypeGUID)) {
                    twoMonthsChannelgjcount = Integer.parseInt(twoDealmap.get("gjcount").toString());
                    break;
                } }
            for(Map twoDealmap: twoDealSetsmap){
                if(twoDealmap.get("MediaTypeGUID")!=null &&  twoDealmap.get("MediaTypeGUID").toString(). equals(MediaTypeGUID)) {
                    twoMonthsChannelAmounts = Integer.parseInt(twoDealmap.get("AverageMonthlySets").toString());
                   break;
                } }
            Double twoMonthsChannelRate=0.00;
            if(twoMonthsChannelgjcount!=0){
                twoMonthsChannelRate=(double) twoMonthsChannelAmounts/twoMonthsChannelgjcount;
            }
            everyactionmap.put("twoSumrate",df.format(twoMonthsChannelRate));


            Integer  threeMonthsChannelAmounts=0;
            Integer threeMonthsChannelgjcount=0;
            for(Map threeDealmap: threeDeallistmap){
                if(threeDealmap.get("MediaTypeGUID")!=null &&  threeDealmap.get("MediaTypeGUID").toString(). equals(MediaTypeGUID)) {
                threeMonthsChannelgjcount=Integer.parseInt(threeDealmap.get("gjcount").toString()) ;
                    break;
            }}
            for(Map threeDealmap: threeDealSetsmap){
                if(threeDealmap.get("MediaTypeGUID")!=null && threeDealmap.get("MediaTypeGUID").toString(). equals(MediaTypeGUID)) {
                    threeMonthsChannelAmounts=Integer.parseInt(threeDealmap.get("AverageMonthlySets").toString()) ;
                   break;
                }}
            Double threeMonthsChannelRate=0.00;
            if(threeMonthsChannelgjcount!=0){
                threeMonthsChannelRate=(double) threeMonthsChannelAmounts/threeMonthsChannelgjcount ;
            }
            everyactionmap.put("threeSumrate",df.format(threeMonthsChannelRate));

          /*
               本月的
               * */
            Map<String, Object> currentmattermap = new HashMap<>();
            currentmattermap.put("action", action);
            currentmattermap.put("projectId", projectId);
            currentmattermap.put("isEffective", isEffective);
            currentmattermap.put("months", currentmonth);


            Map<String, Object> currentresultmap = manager.selectAllTheMouthChannelDetailAction(currentmattermap);


            everyactionmap.put("currentSumrate",df.format( Double.parseDouble( currentresultmap.get("nowtransactionRate").toString())));
            resultmap.add(everyactionmap);
        }

        return resultmap;
    }


    /**
     * 通过月份和项目ID来查找渠道费用明细,若有值则直接表示到前端，若没有则初始化到前端
     *
     * @return
     */
    @Override
    public List<Map> allChannelDetailSelect(String projectId, String months,Integer isEffective){
        /*
         * 时间类型转换
         * */
        if(months.indexOf("-")<0){
            long longtime=Long.parseLong(months);
            months=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longtime));
        }


        List<Map> result=selectMouthChannelDetail(projectId,months,isEffective);

        /*
         * 如果用未上报状态（0）查找不到 被上报的数据，就用上报状态（1）再查一遍，
         * 但如果传进来的本身就是1，那么再查一次结果也一样，这样是为了让0状态找到被上报
         * 的数据
         * */
        if (result==null || result.size()<1) {
            result= selectMouthChannelDetail(projectId,months,1);
        }

        if (result==null || result.size()<1) {
            result= selectMouthChannelDetail(projectId,months,2);
        }

        if(result!=null && result.size()>0){


            return result;
        }

        /*
         * 若走到这一步说明没有数据，若状态码不等于0就不初始化，直接返回NULL
         * */
        if(isEffective!=0){
            return null;
        }
        return initialiseChannelDetail(projectId,months,0);


    }


    /**
     * 通过项目ID和月份跟新渠道费用明细
     *
     * @return
     */
    @Override
    public Integer  updateChannelDetail(List<Map> listmap){
        try{
            Integer a=0;

            /* 前端传回来的listmap是一个集合，所有的渠道和对应的项目ID和月份，所以先找出所有的渠道名*/
            /* 代码优化，舍弃没用上的查询 bql 2020.07.28*/
            /* List<Map> channel= manager.allChannelSelect();*/

            for (int i=0;i<listmap.size();i++){
                if (listmap.get(i) != null) {
                    /* 转换LONG为日期 */
                    long data = 0;
                    if (listmap.get(i).get("months") != null && listmap.get(i).get("months").getClass().isInstance(data)) {
                        long longtime = (long) listmap.get(i).get("months");
                        String months = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longtime));
                        listmap.get(i).put("months", months);
                    }
                    if (listmap.get(i).get("create_time") != null && listmap.get(i).get("create_time").getClass().isInstance(data)) {
                        long longtime = (long) listmap.get(i).get("create_time");
                        String create_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longtime));
                        listmap.get(i).put("create_time", create_time);
                    }
                    if (listmap.get(i).get("update_time") != null && listmap.get(i).get("update_time").getClass().isInstance(data)) {
                        long longtime = (long) listmap.get(i).get("update_time");
                        String update_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longtime));
                        listmap.get(i).put("update_time", update_time);
                    }
                    if (listmap.get(i).get("project_id") != null) {
                        listmap.get(i).put("projectId", listmap.get(i).get("project_id"));
                    }

                    /*
                     * 遍历Map所有值，若传进来的数有空，则将它默认为0
                     * */

                    Iterator iterable = listmap.get(i).entrySet().iterator();
                    while (iterable.hasNext()) {
                        Map.Entry entry_d = (Map.Entry) iterable.next();
                        Object key = entry_d.getKey();
                        Object value = entry_d.getValue();
                        if (value == null || value == "") {
                            value = 0;
                        }
                        listmap.get(i).put(key.toString(), value);
                    }
                    /* 注释原循环调用修改方法 */
                    /* a+= manager.updateChannelDetail(listmap.get(i));*/
                }
            }
            /* 添加新批量修改方法 */
            /* bql 2020.07.28 */
            a+= manager.updateChannelDetailBatch(listmap);
            return a;
        }catch (Exception e){
            e.printStackTrace();
            throw new BadRequestException(-14_1011,e);
        }

    }


    /**
     * 通过项目ID和月份来设置费用明细是否激活可用状态
     *
     * @return
     */
    @Override
    public String channelDetailTestEffective(String userId, String projectId, String months,Integer isEffective){
     try{
        Map<String,Object> map=new HashMap<>();
        map.put("projectId",projectId);
        map.put("months",months);
        map.put("isEffective",isEffective);
         DecimalFormat df = new DecimalFormat("0.00");
        /*
        * 表三在上报前先进行和表二的校验
        * */
       List<Map> list3= allChannelDetailSelect(projectId,months,isEffective);


         Double  contractAmount=0.00;
         Double   responsibilityAmount=0.00;
         for( Map<String,Object> everylist:list3){
             contractAmount+=Double.parseDouble(everylist.get("contract_amount").toString()) ;

             responsibilityAmount+=Double.parseDouble(everylist.get("right_responsibility_amount").toString());

         }
         contractAmount=Double.parseDouble(df.format(contractAmount));
         responsibilityAmount=Double.parseDouble(df.format(responsibilityAmount));
         /*表三验证要和表二做校验*/
        Map<String,Object> planSelect= allMouthPlanSelect(userId, projectId,months,0);
                        if(planSelect.get("cost")==null){
                            return "请确认表二费用已填报";
                        }
        /*表三验证要和二下做校验*/
          Map<String,Object> cost= manager.selectPromotionCost(map);

         if(Double.parseDouble(cost.get("marketing_promotion_cost").toString())-contractAmount<0 ){
             return "表三合同金额与下达相差"+df.format((Double.parseDouble(cost.get("marketing_promotion_cost")+""))-contractAmount);
         }
         if(Double.parseDouble(planSelect.get("cost")+"")-responsibilityAmount<0){
             return "表三权责金额与表二相差"+df.format((Double.parseDouble(planSelect.get("cost")+"")-responsibilityAmount));
         }

            return null;
     }catch (Exception e){
         e.printStackTrace();
         throw new BadRequestException(-14_1009,e);
     }
    }


    /**
     * 通过项目ID和月份来查找周计划
     *
     * @return
     */
    @Override
    public List<Map> selectWeeklyPlan(String projectId, String months,Integer isEffective){
        try{
        Map<String,Object> map=new HashMap<>();
        map.put("projectId",projectId);
        map.put("months",months);
        map.put("isEffective",isEffective);

          List<Map> resultMap=  manager.selectWeeklyPlan(map);
          if(resultMap!=null && resultMap.size()>0){
              for(Map map1: resultMap){
                  if(map1.get("start_time")!=null && map1.get("end_time")!=null){
                      List<String> timelist=new ArrayList<>();
                      timelist.add((map1.get("start_time")+"").substring((map1.get("start_time")+"").indexOf("-")+1,(map1.get("start_time")+"").lastIndexOf("-")+3));
                      timelist.add((map1.get("end_time")+"").substring((map1.get("end_time")+"").indexOf("-")+1,(map1.get("end_time")+"").lastIndexOf("-")+3));
                      map1.put("timelist",timelist);
                  }
              }
          }

        return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            throw new BadRequestException(-14_1010,e);
        }
    }


    /**
     * 通过项目ID和月份来查找周计划
     *求上一个月的，在表四中和本月做对比
     * @return
     */
    @Override
    public List<Map> frontselectWeeklyPlan(String projectId, String months,Integer isEffective){
        try{

            Integer trimyear=Integer.parseInt(months.substring(0,months.indexOf("-")));

            Integer  trimthree=Integer.parseInt(months.substring(months.indexOf("-")+1,months.indexOf("-")+3));
            Integer  nowday=Integer.parseInt(months.substring(months.lastIndexOf("-")+1,months.lastIndexOf("-")+3));
            /*算出当前月，也就是减掉1个月*/
            Integer nowyearone=trimyear;
            Integer nowmonthone=trimthree-1;
            if(nowmonthone<1){
                nowmonthone=(12+trimthree)-1;
                nowyearone--;
            }

            String frontmonths=nowyearone+"-"+(nowmonthone<10? "0"+nowmonthone:nowmonthone)+"-"+nowday;

            Map<String,Object> map=new HashMap<>();
            map.put("projectId",projectId);
            map.put("months",frontmonths);



            return manager.selectWeeklyPlan(map);
        }catch (Exception e){
            e.printStackTrace();
            throw new BadRequestException(-14_1010,e);
        }
    }






    /**
     * 通过项目ID和月份来初始化周计划
     *
     * @return
     */
    @Override
    public void initialWeeklyPlan(String projectId, String months){
try{
        Map<String,Object> map=new HashMap<>();

        String guid = UUID.randomUUID().toString();
           List<Map> dateweek= DateUtil.getWeek(months);
        for(int i=0;i<dateweek.size();i++){
            map.put("guid",guid);
            map.put("projectId",projectId);
            map.put("months",months);
            map.put("week_serial_number",dateweek.get(i).get("weekNum"));
            map.put("start_time",dateweek.get(i).get("startTime"));
            map.put("end_time",dateweek.get(i).get("endTime"));
            map.put("day_num",dateweek.get(i).get("day_num"));
             manager.initialWeeklyPlan(map);
        }
}catch (Exception e){
    e.printStackTrace();
    throw new BadRequestException(-14_1010,e);
}
    }

    /**
     * 通过项目ID和月份来跟新周计划
     *
     * @return
     */
    @Override
    public Integer  updateWeeklyPlan(List<Map> listmap) {
        try{


        Integer a=0;
        for (int i = 0; i <listmap.size(); i++) {

            if (listmap.get(i) == null ) {
                continue;
            }
            /*
             * 转换LONG为日期
             * */
                long data=0;
           if (listmap.get(i).get("months") != null && listmap.get(i).get("months").getClass().isInstance(data)) {
              long longtime=(long)listmap.get(i).get("months");


               String months=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longtime));

                listmap.get(i).put("months",months);
            }
             if(listmap.get(i).get("project_id")!=null){
                    listmap.get(i).put("projectId",listmap.get(i).get("project_id"));
                };



            /*
             * 遍历Map所有值，若传进来的数有空，则将它默认为0
             * */
            Iterator iterable= listmap.get(i).entrySet().iterator();
            while (iterable.hasNext()) {
                Map.Entry entry_d = (Map.Entry) iterable.next();
                Object key = entry_d.getKey();
                Object value = entry_d.getValue();
                if(value==null || value==""){
                    value=0;
                }
                listmap.get(i).put(key.toString(),value);
            }


           a+= manager.updateWeeklyPlan(listmap.get(i));

        }
        return a;
        }catch (Exception e){
            e.printStackTrace();
            throw new BadRequestException(-14_1012,e);
        }
    }

    /**
     * 通过项目ID和月份来决定周计划上报状态
     *
     * @return
     */
    @Override
    public String weeklyPlanIsEffective(String projectId, String months,Integer isEffective){
        try{
        Map<String,Object> map=new HashMap<>();
        map.put("projectId",projectId);
        map.put("months",months);
        map.put("isEffective",isEffective);
            /*
            表二表三
            * 表四上报
            * */
            manager. mouthPlanEffective(map);
            manager.channelDetailEffective(map);
            manager.weeklyPlanIsEffective(map);
        return null;
        }catch (Exception e){
            e.printStackTrace();
            throw new BadRequestException(-14_1013,e);
        }
    }
    /**
     * 通过项目ID和月份来查找或初始化周计划，被controller调用
     *
     * @return
     */
    @Override
    public List<Map> allWeeklyPlanSelect(String projectId, String months,Integer isEffective){
        /*
         * 时间类型转换
         * */
        if(months.indexOf("-")<0){
            long longtime=Long.parseLong(months);
            months=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longtime));
        }



        List<Map> Weekly= selectWeeklyPlan(projectId,months,isEffective);


        /*
         * 如果用未上报状态（0）查找不到 被上报的数据，就用上报状态（1）再查一遍，
         * 但如果传进来的本身就是1，那么再查一次结果也一样，这样是为了让0状态找到被上报
         * 的数据
         * */
        if (Weekly==null || Weekly.size()<1) {
            Weekly=selectWeeklyPlan(projectId,months,1);

        }

        if (Weekly==null || Weekly.size()<1) {
            Weekly=selectWeeklyPlan(projectId,months,2);

        }

            if(Weekly!=null && Weekly.size()>0){

                return Weekly;
            }

            /*
            * 若走到这一步说明没有数据，若状态码不等于0就不初始化，直接返回NULL
            * */
         if(isEffective!=0){
             return null;
         }

         initialWeeklyPlan(projectId,months);

       return selectWeeklyPlan(projectId,months,0);
    }


    /**
     * 附件的查找
     *
     * @return
     */
    @Override
    public List<Map> selectAttach(String projectId) {
        List<Map> result= manager.selectAttach(projectId);

        return result;
    }



    /**
     * 附件的上传
     *
     * @return
     */
    @Override
    public  List<Map> UploadAttach(MultipartFile file,HttpServletRequest request) throws IOException {
        List<Map>  result =new ArrayList<Map>();

        List<Map> list=new ArrayList<Map>();


        File directory = new File("cifimaster"+File.separator+"visolink-sales-api"+File.separator+"src"+File.separator+"Uploads");//设定为当前文件夹


        String fileName=file.getOriginalFilename();
            SimpleDateFormat sd=new SimpleDateFormat("yyyyMMddHHmmss");

        String fileTime =sd.format(new Date());
        String newFileName =fileTime + fileName;

        String expName=fileName.substring(fileName.lastIndexOf(".")+1);

        String UserID=request.getParameter("UserID");
        String projectid=request.getParameter("BizID");

        String bizType=request.getParameter("BizType");
        String path=directory.getAbsolutePath()+File.separator+bizType+File.separator;
        String uuid= UUID.randomUUID().toString();
//Uploads/abc
        String contextPath="Uploads"+
                File.separator+bizType+File.separator+uuid+"."+expName;

       // String abcpath = request.getServletContext().getRealPath("/");
        /*
        * 存储路径
        * */
       // String realPath=this.getClass().getResource("/").getPath()  ;



       File saveFile=new File(uplodepath+File.separator+"listfour");

        if(!saveFile.exists()){
            saveFile.mkdirs();
        }
        /*
        * 进行存储
        * */
        file.transferTo(new File(saveFile,newFileName));

        Map<String,Object> returnMap = new HashMap<>();
        /*
         * 保存到磁盘的文件名
         * */
        returnMap.put("ID", uuid);
        /*扩展名*/
        returnMap.put("FileNameSuffix", expName);
        /*原始文件名*/
        returnMap.put("FileNameOld", fileName);
        /*保存到磁盘路径*/
        returnMap.put("SaveUrl", relepath+File.separator+"listfour"+File.separator+newFileName+"?n="+fileName);
        /*文件大小*/
        returnMap.put("FileSize", file.getSize());
        /*创建人*/
        returnMap.put("CreateUser", StringUtil.isEmpty(UserID)?"":UserID);
        /*创建时间*/
        returnMap.put("CreateTime", new Date());

        /*创建项目id*/
        returnMap.put("BizID", projectid);
        manager.insertAttach(returnMap);

        list.add(returnMap);

        result = list;
        return result;
    }
    /**
     * 附件的删除
     *
     * @return
     */
    @Override
    public Integer deleteAttach(String fileID,Integer IsDel){
        Map<String,Object> map=new HashMap<>();
        map.put("ID",fileID);
        map.put("IsDel",IsDel);
        return manager.deleteAttach(map);
    }
    /**
     *表二三四上报前校验
     *
     * @return
     */
    @Override
    public String weeklyPlanTestEffective(String userId,String projectId, String months,Integer isEffective){
        {
            try{
                Map<String,Object> map=new HashMap<>();
                map.put("projectId",projectId);
                map.put("months",months);
                map.put("isEffective",isEffective);

                /*
                 * 若状态为0，说明是驳回，没必要再走一次验证
                 * */
                if(isEffective==1){
                    /*
                     * 表三与表二的验证，若对不上直接返回错误信息
                     * */
                    String resultlist3= channelDetailTestEffective(userId, projectId, months, 0);
                    if(resultlist3!=null){
                        return resultlist3;
                    }
                    /*
                     * 表四再上报之前，先测试是否对应的上表二的数据
                     * */
                    Map<String,Object> testWeekly= manager.testWeeklyPlanSum(map);

                    /*
                     * 表二的数据
                     * */
                    Map<String,Object> testmonth= allMouthPlanSelect(userId,projectId,months,0);
                    DecimalFormat df = new DecimalFormat("0.00");

                    if(Double.parseDouble(testmonth.get("big_card").toString()) -Double.parseDouble(testWeekly.get("big_card").toString())!=0){
                        return "表四大卡与表二相差"+(Double.parseDouble(testmonth.get("big_card").toString())-Double.parseDouble(testWeekly.get("big_card").toString()));
                    }
                    if(Double.parseDouble(testmonth.get("small_card").toString())-Double.parseDouble(testWeekly.get("small_card").toString())!=0){
                        return "表四小卡与表二相差"+(Double.parseDouble(testmonth.get("small_card").toString())-Double.parseDouble(testWeekly.get("small_card").toString()));
                    }
                    if(Double.parseDouble(testmonth.get("come_client_quantity").toString())-Double.parseDouble(testWeekly.get("visit_quantity").toString())!=0){
                        return "表四来人量与表二相差"+(Double.parseDouble(testmonth.get("come_client_quantity").toString())-Double.parseDouble(testWeekly.get("visit_quantity").toString()));
                    }
                    if(Double.parseDouble(testmonth.get("subscription_number").toString())-Double.parseDouble(testWeekly.get("subscription_number_set").toString())!=0){
                        return "表四认购套数与表二相差"+(Double.parseDouble(testmonth.get("subscription_number").toString())-Double.parseDouble(testWeekly.get("subscription_number_set").toString()));
                    }
                    if(Double.parseDouble(testmonth.get("sign_number_set").toString())-Double.parseDouble(testWeekly.get("sign_number_set").toString())!=0){
                        return "表四签约套数与表二相差"+(Double.parseDouble(testmonth.get("sign_number_set").toString())-Double.parseDouble(testWeekly.get("sign_number_set").toString()));
                    }
                    if(Double.parseDouble(testmonth.get("sign_funds").toString())-Double.parseDouble(testWeekly.get("sign_target").toString())!=0){
                        return "表四签约目标与表二相差"+df.format(Double.parseDouble(testmonth.get("sign_funds")+"")-Double.parseDouble(testWeekly.get("sign_target")+""));
                    }
                }
                return null;
            }catch (Exception e){
                e.printStackTrace();
                throw new BadRequestException(-14_1013,e);
            }
        }
    }
    /**
     * 查找项目名
     */
    @Override
    public String  selectProjectName(String projectId){
      return   manager.selectProjectName(projectId);
    }

}



