package cn.visolink.salesmanage.riskcontrolmanager.dao;

import cn.visolink.salesmanage.riskcontrolmanager.model.InsideBe;
import cn.visolink.salesmanage.riskcontrolmanager.model.OutsideBe;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface RiskContolDao {
   /*插入风控表*/
   Integer updateRiskControl(List<Map<String, Object>> list);
   /*从风控表里找最近的一次创建时间*/
   String createTimeSelect();

   /*插入买方表*/
   Integer insertBuyer(List<Map<String, Object>> list);
   /*查找项目ID*/
   List<String> selectUnitId();
   /*查找风控表数据*/
   List<Map>  selectRiskInfor(Map map);
   /*查找风控概述表数据*/
  Integer  selectRiskSurface();
   /*插入风控概述表数据*/
      // Integer  insertRiskSurface(List list);
   /*风控里表数据*/
   List<Map> selectRiskInside(Map map);
/*查找所有和买方表对应的上的GUID*/
   List<String> AllOrderGuid();
   /*查找事业部名字和ID*/
   List<Map> selectBusinessName();
   /*查找风控表数据,Excel专用*/
   List<Map>  selectRiskInforExcel(Map map);

   /*若用户没有选择CHANNEL条件，先走这个外部查询SQL*/
   List<Map>  selectOutsideBe(Map map);
   /*每次刷新之前都要清空概述表里的数据*/
 // Integer deleteRisk();
/*每次刷新之前都要清空详细表近30天里的数据*/
   Integer  deleteRiskInfo();
   /*根据用户ID来选择删除一些项目*/
   Integer   deleteInfoByClient(Map map);
/*在更新买方表之前，先修改对应的买方表里的数据*/
   Integer updateBuyerBefore(Map map);
   /*在更新买方表之前，先查找对应的买方表里的数据*/
   Map  selectInfoDetail(Map map);
   /*初始化ORder中间表用于查询风控数据*/
  Integer insertOrderFkUp();
/*算外部合计*/
Map  selectOutsideGroupAll(Map map);
   /*若用户没有选择CHANNEL条件，先走这个外部查询SQL,导出专用*/
   List<OutsideBe>   selectOutsideBeByExport(Map map);

   /*风控里表数据,导出专用*/
   List<InsideBe> selectRiskInsideByExport(Map map);

   Integer deleteOrderFkUpBefore();

   Integer deleteRiskSurfaceBefore();
}
