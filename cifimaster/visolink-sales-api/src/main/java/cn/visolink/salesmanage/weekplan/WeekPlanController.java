package cn.visolink.salesmanage.weekplan;


import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.groupmanagement.service.GroupManageExportService;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.wicket.core.dbhelper.sql.DBSQLServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjz
 */
@RestController
@RequestMapping("/weekRule")
@Api(tags = "周规则")
public class WeekPlanController {
/*
    @Autowired
    private DBSQLServiceImpl dbsqlService;

    @Autowired
    private GroupManageExportService groupManageExportService;
    @Log("周规则初始化")
    @ApiOperation(value = "周规则初始化")
    @RequestMapping(value = "/getCommonWeekRule",method = RequestMethod.POST)
    public VisolinkResultBody getCommonWeekRule(@RequestBody String params) {
        VisolinkResultBody result=new VisolinkResultBody();
        if(params==null || params.equals("")){
            return result;
        }
        Map basereqMap = JSON.parseObject(params,Map.class);
        List<Map<String,Object>> reqMap = (List<Map<String, Object>>)basereqMap.get("_param");
        //list 为全量集合
        int batchCount = 50; //每批插入数目
        int batchLastIndex = batchCount;
        List<List<Map<String, Object>>> shareList = new ArrayList<>();
        if(reqMap!=null) {
            for (int index = 0; index < reqMap.size(); ) {
                if (batchLastIndex >= reqMap.size()) {
                    batchLastIndex = reqMap.size();
                    shareList.add(reqMap.subList(index, batchLastIndex));
                    break;
                } else {
                    shareList.add(reqMap.subList(index, batchLastIndex));
                    index = batchLastIndex;// 设置下一批下标
                    batchLastIndex = index + (batchCount - 1);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(shareList)) {
            for (List<Map<String, Object>> subList : shareList) {
                //循环插入数据
                if (subList != null && subList.size() > 0) {
                    StringBuffer sql = new StringBuffer();
                    String insertSql = "";
                    for (Map<String, Object> data : subList) {
                        String this_time=data.get("this_time")+"";
                        this_time=this_time==null|| this_time.equals("")||this_time.equals("null")?"":this_time;
                        String how_week=data.get("how_week")+"";
                        how_week=how_week==null|| how_week.equals("")||how_week.equals("null")?"":how_week;
                        String start_time=data.get("start_time")+"";
                        start_time=start_time==null|| start_time.equals("")||start_time.equals("null")?"":start_time;
                        String end_time=data.get("end_time")+"";
                        end_time=end_time==null|| end_time.equals("")||end_time.equals("null")?"":end_time;
                        String day_num=data.get("day_num")+"";
                        day_num=day_num==null|| day_num.equals("")||day_num.equals("null")?"":day_num;
                        sql.append(",('").append(this_time).append("','").append(how_week).append("','").append(start_time);
                        sql.append("','").append(end_time).append("','").append(day_num);
                        sql.append("')");
                    }
                    insertSql = sql.substring(1,sql.length());
                    Map<String, String> map1 = new HashMap<>();
                    map1.put("sql", insertSql);
                    dbsqlService.updateByMap("mm_common_week_rule_insert",map1);
                }
            }
        }
        return result;
    }*/
}
