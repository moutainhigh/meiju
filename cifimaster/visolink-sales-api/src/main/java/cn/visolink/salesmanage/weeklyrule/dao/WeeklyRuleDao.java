package cn.visolink.salesmanage.weeklyrule.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface WeeklyRuleDao {
/*
* 规则表的查找
* */
   List<Map> WeeklyRuleSelect();
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
