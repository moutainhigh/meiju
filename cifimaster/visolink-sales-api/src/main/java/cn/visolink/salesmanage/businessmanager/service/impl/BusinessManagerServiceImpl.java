package cn.visolink.salesmanage.businessmanager.service.impl;

import cn.visolink.salesmanage.businessmanager.dao.BusinessManagerDao;
import cn.visolink.salesmanage.businessmanager.service.BusinessManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BusinessManagerServiceImpl implements BusinessManagerService {

    @Autowired
    BusinessManagerDao business;


    /**
     *区域查看区域查看  项目上报
     * @param
     * @return
     */
    @Override
    public List<Map> regionReportSelect(String regionOrgId,String months) {
        Map<String,Object> map=new HashMap<>();
        map.put("region_org_id",regionOrgId);
        map.put("months",months);
        return business.regionReportSelect(map);
    }
    /**
     *区域查看项目上报合计
     * @param
     * @return
     */
    @Override
    public List<Map> regionFundsSelect(String regionOrgId,String months) {
        Map<String,Object> map=new HashMap<>();
        map.put("region_org_id",regionOrgId);
        map.put("months",months);
        return business.regionFundsSelect(map);
    }
    /**
     *修改合計
     * @param map
     * @return
     */
    @Override
    public Integer regionFundsUpdate(Map<String,Object> map) {

        /*
         * 时间类型转换
         * */
        long data=0;
        if (map.get("months") != null && map.get("months").getClass().isInstance(data)) {
            long longtime=(long)map.get("months");
            String months=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longtime));
            map.put("months",months);
        }
        /* return business.regionFundsUpdate(map); */
        /* sql 拆分，原为查询修改，分开执行 */
        /* bql 2020.07.30 */
        Map<String, Object> regionFunds = business.selectRegionFunds(map);
        if(map==null){
            map=new HashMap<>();
        }
        if(regionFunds!=null&&regionFunds.size()>0){
            map.putAll(regionFunds);
        }
        return business.updateRegionFunds(map);
    }


    /**
     *上报区域项目合計
     * @param map
     * @return
     */
    @Override
    public Integer regionFundsEffective(Map<String,Object> map){
        /*
         * 时间类型转换
         * */
        long data=0;
        if (map.get("months") != null && map.get("months").getClass().isInstance(data)) {
            long longtime=(long)map.get("months");
            String months=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longtime));
            map.put("months",months);
        }
            /*当区域上报的时候更改项目的生效字段，让它锁死不可编辑*/
       if((int)map.get("planStatus")>=2){
           Map lockMap=new HashMap();
           lockMap.put("fatherId",map.get("regionOrgId"));
           lockMap.put("months",map.get("months"));
           lockMap.put("is_effective",1);

           business.lockedProject(lockMap);
       }
                /*
                * 检查上报金额是否大于下达金额,当是上报状态时才审核
                * */
          Map<String,Object> regionmoney= business.testregionEffective(map);
   /*     if(map.get("planStatus").toString().equals("2")){
        if(  Double.parseDouble(regionmoney.get("projectfunds").toString())>=Double.parseDouble(regionmoney.get("regionfunds").toString())){
            return business.regionFundsEffective(map);
        }
        return null;
    }*/

    return business.regionFundsEffective(map);
    }

    /**
     *事业部列表
     * @param
     * @return
     */
    @Override
    public List<Map> businessDepartSelect(String months) {
        Map<String,Object> map=new HashMap<>();
        map.put("months",months);
        return business.businessDepartSelect(map);
    }
    /**
     *事业部异步请求 项目列表
     * @param
     * @return
     */
    @Override
    public List<Map> businessprojectSelect(String regionOrgId,String months) {
        Map<String,Object> map=new HashMap<>();
        map.put("region_org_id",regionOrgId);
        map.put("months",months);
        return business.businessprojectSelect(map);
    }
    /**
     *
     * 合计
     * @param
     * @return
     */
    @Override
    public List<Map> businessTotalSelect(String regionOrgId,String months) {
        Map<String,Object> map=new HashMap<>();
        map.put("region_org_id",regionOrgId);
        map.put("months",months);
        return business.businessTotalSelect(map);
    }

    /**
     *修改项目合计
     * @param map
     * @return
     */
    @Override
    public Integer businessFundsUpdate(Map<String,Object> map){

        /*
         * 时间类型转换
         * */
        long data=0;
        if (map.get("months") != null && map.get("months").getClass().isInstance(data)) {
            long longtime=(long)map.get("months");
            String months=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longtime));
            map.put("months",months);
        }

        return business.businessFundsUpdate(map);
    }

    /**
     *集团确认
     * @param
     * @return
     */
    @Override
    public Integer businessFundsEffective(String months){
        Map<String,Object> map=new HashMap<>();
        map.put("months",months);
        Integer result=0;
        /*
         * 集团确认后,添加之前先删除
         *         添加等级为4的数据
         * */
        business.deleteMonthIndex(months);
        result+= business.insertMonthIndex(map);



        /*将所有已经推送供销存的区域下的项目，若有表一未上报的，改为表一已上报*/
       // business.updateAllRegionStatus(map);
        /*判断集团下所有区域是否有全部上报，若没有则不更改集团状态*/
       List<Map> listmap= business.AllRegionStatus(map);
        if(listmap!=null && listmap.size()>0){
            return result;
        }
        result+= business.businessFundsEffective(map);
        return result;
    }


}
