package cn.visolink.salesmanage.groupmanagement.service.imple;

import cn.visolink.exception.BadRequestException;
import cn.visolink.salesmanage.businessmanager.dao.BusinessManagerDao;
import cn.visolink.salesmanage.groupmanagement.dao.GroupManageUpdate;
import cn.visolink.salesmanage.groupmanagement.service.UpdateMonthlyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UpdateMonthlyServiceImpl implements UpdateMonthlyService {

    @Autowired
    GroupManageUpdate update;


    /*
     * 修改表一暂存后的数据
     * 表一有若干个层级，层层更新
     * */

        @Override
        public Integer updateMonthlyPlan(List<Map> map){

            Integer data=0;
            Map<String, Object> projectmap=null;
            Map<String,Object>   childrenlistone=null;
            Map<String,Object>   childrenlisttwo=null;
            Map<String,Object>   childrenlistthree=null;
            Map<String,Object>  childrenlistfour=null;
           for(int  j=0;j<map.size();j++) {

               projectmap= Iteratormap(map.get(j));

               data += update.updateMonthlyPlan(projectmap);

               List<Map> childrenlist1 = (List<Map>) projectmap.get("children");

               for (int i = 0; i < childrenlist1.size(); i++) {

                  childrenlistone = Iteratormap(childrenlist1.get(i));
                   data += update.updateMonthlyPlan(childrenlistone);
                   List<Map> childrenlist2 = (List<Map>) childrenlist1.get(i).get("children");

                   for (int ii = 0; ii < childrenlist2.size(); ii++) {

                      childrenlisttwo = Iteratormap(childrenlist2.get(ii));
                       data += update.updateMonthlyPlan(childrenlisttwo);
                        /*
                        * 若无children直接返回data
                        * */
                       if(childrenlist2.get(ii).get("children")==null ){
                           continue;
                       }
                       List<Map> childrenlist3 = (List<Map>) childrenlist2.get(ii).get("children");

                       for (int iii = 0; iii < childrenlist3.size(); iii++) {

                           childrenlistthree = Iteratormap(childrenlist3.get(iii));
                           data += update.updateMonthlyPlan(childrenlistthree);
                           /*
                            * 若无children直接返回data
                            * */
                           if(childrenlist3.get(iii).get("children")==null){
                               continue;
                           }

                           List<Map> childrenlist4 = (List<Map>) childrenlist3.get(iii).get("children");


                         for (int iiii = 0; iiii < childrenlist4.size(); iiii++) {

                             childrenlistfour = Iteratormap(childrenlist4.get(iiii));
                               data += update.updateMonthlyPlan(childrenlistfour);
                           }
                       }

                   }
               }
           }
                         return data;
        }

    @Override
    public String getProjectTableOneStatus(String projectId,String months) {
        return update.getProjectTableOneStatus(projectId,months);
    }

    /*
     * 遍历Map所有值，若传进来的数有空，则将它默认为0
     * */
public  Map Iteratormap(Map map1 ){

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
           return map1;
    }


    /*
     * 表一提交
     * */
    @Override
    public int updatePlanEffective(Map<String,Object>  map) {

        try {

            String plan_status=map.get("plan_status")+"";
            if(!map.get("plan_status").toString().equals("0")){
                map.put("is_effective",1);
            }else {
                map.put("is_effective",0);
            }
            if("1".equals(plan_status)){
                map.put("nowDate",new Date());
            }
            if("0".equals(plan_status)||"2".equals(plan_status)){
                map.put("toexamine_time",new Date());
            }
            return update.updatePlanEffective(map);


        }catch (Exception e){
            throw new BadRequestException(14_1014,e);
        }

    }

}
