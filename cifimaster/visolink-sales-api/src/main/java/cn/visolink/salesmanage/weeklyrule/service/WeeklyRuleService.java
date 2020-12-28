package cn.visolink.salesmanage.weeklyrule.service;

import cn.visolink.exception.ResultBody;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface WeeklyRuleService {
    /*
     * 规则表的查找
     * */
    ResultBody WeeklyRuleSelect(Map map);
    /*
     * 规则表的更新
     * */
    Integer WeeklyRuleUpdate(Map map);
    /*
     * 规则表的删除
     * */
    Integer WeeklyRuleDelete(Map map);
    /*
     * 规则表的插入
     * */
    Integer WeeklyRuleInsert(Map map);


}
