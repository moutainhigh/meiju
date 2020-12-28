package cn.visolink.salesmanage.gxcinterface.service.impl;

import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.groupmanagement.controller.GroupMangerUpdateController;
import cn.visolink.salesmanage.gxcinterface.dao.GXCInterfaceDao;
import cn.visolink.salesmanage.gxcinterface.dao.NewGXCInterfaceDao;
import cn.visolink.salesmanage.gxcinterface.service.NewGxcByProjectService;
import cn.visolink.system.timelogs.bean.SysLog;
import cn.visolink.utils.DateUtil;
import cn.visolink.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2020.04.08
 */
@Service
public class NewGxcByProjectServiceImpl implements NewGxcByProjectService {

    @Autowired
    private NewGXCInterfaceDao newGXCInterfaceDao;

    @Autowired
    private GroupMangerUpdateController groupMangerUpdateController;


    @Autowired
    private GXCInterfaceDao gxcinterfaceDao;


    //注入sql操作类
    @Resource(name = "jdbcTemplategxc")
    private JdbcTemplate jdbcTemplategxc;

    /**
     * 增量获取动态货值视图数据
     *
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
        String projectId = "";
        if (params == null || params.size() == 0 || params.get("startTime") == null || params.get("startTime").equals("")) {
            if (params != null) {
                projectId =params.get("projectId")+"";
            }
            mon3 = mon3 + "-01";
        } else {
            projectId =params.get("projectId")+"";
            mon3 = params.get("startTime") + "";
        }
        // and versionTime>= '"+mon3+"'
        String sqls = "SELECT * FROM  v_sman_dynamic_value where projectId='" + projectId + "'and versionTime>= '" + mon3 + "'";
        //查询供货视图数据
        List<Map<String, Object>> listData = jdbcTemplategxc.queryForList(sqls);
        if (listData != null && listData.size() > 0) {
            //根据时间删除
            newGXCInterfaceDao.deleteDynamicValue(mon3, projectId + "");
            //写入动态货值`
            newGXCInterfaceDao.insertvaluedthz(listData);
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
        String projectId = "";
        if (params == null || params.size() == 0 || params.get("startTime") == null || params.get("startTime").equals("")) {
            if (params != null) {
                projectId =params.get("projectId")+"";
            }
            mon3 = mon3 + "-01";
        } else {
            projectId =params.get("projectId")+"";
            mon3 = params.get("startTime") + "";
        }
        //and create_time>= '"+mon3+"'
        String sqls = "SELECT * FROM  v_sman_sign_plan where project_id='" + projectId + "'";
        //查询供货视图数据
        List<Map<String, Object>> listData = jdbcTemplategxc.queryForList(sqls);
        if (listData != null && listData.size() > 0) {
            //根据时间删除
            newGXCInterfaceDao.deleteSignPlan(mon3, projectId + "");
            //写入计划签约
            newGXCInterfaceDao.insertvalueqy(listData);
        }
        //更新数据条数
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setMessages("初始化供销存数据成功!");
        resultBody.setCode(200);
        return resultBody;
    }

    @Override
    public ResultBody insertSupplyPlan(Map params) {
        //前二个月
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -2);
        Date m3 = c.getTime();
        String mon3 = format.format(m3);
        String projectId = "";
        if (params == null || params.size() == 0 || params.get("startTime") == null || params.get("startTime").equals("")) {
            if (params != null) {
                projectId =params.get("projectId")+"";
            }
            mon3 = mon3 + "-01";
        } else {
            projectId =params.get("projectId")+"";
            mon3 = params.get("startTime") + "";
        }
        String sqls = "SELECT * FROM  v_sman_supply_plan where project_id='" + projectId + "'";
        //查询供货视图数据
        List<Map<String, Object>> listData = jdbcTemplategxc.queryForList(sqls);
        if (listData != null && listData.size() > 0) {
            //根据时间删除
            newGXCInterfaceDao.deleteSupplyPlanVersionId(mon3, projectId + "");
            //写入动态货值
            newGXCInterfaceDao.insertvaluegh(listData);
        }
        /**
         * 根据 确认版》定稿版》最新版 规则过滤一下数据
         * */
        //1.先查询出所有分期
/*        List<Map> list = gxcinterfaceDao.getStageList(params.get("projectId").toString());
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
        }*/
        //1.先查询出所有分期  异常取最新版
        List<Map> list = gxcinterfaceDao.getStageList(projectId);
        for (Map map : list) {
         gxcinterfaceDao.delNewOtjerVersion(map.get("stage_id").toString());
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
                "DATE_FORMAT(date_sub(now(),interval 1 MONTH),'%Y-%m') as end_date,\n" +
                "un_sale_stall_num,\n" +
                "un_sale_room_num,\n" +
                "un_sale_stall_price,\n" +
                "un_sale_room_price,building_id,is_parking FROM  v_sman_value_report where project_id='" + params.get("projectId") + "' and end_date= (select end_date from v_sman_value_report order by end_date desc limit 1)";
        //查询供货视图数据
        List<Map<String, Object>> listData = jdbcTemplategxc.queryForList(sqls);
        if (listData != null && listData.size() > 0) {
            //根据时间删除
           newGXCInterfaceDao.deleteReportValue(mon3, params.get("projectId") + "");
            //写入动态货值
            newGXCInterfaceDao.insertReportValue(listData);
        }
        //更新数据条数
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setMessages("初始化供销存数据成功!");
        resultBody.setCode(200);
        return resultBody;
    }


    @Override
    public ResultBody insertReportValueAll() {
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
                "DATE_FORMAT(date_sub(now(),interval 1 MONTH),'%Y-%m') as end_date,\n" +
                "un_sale_stall_num,\n" +
                "un_sale_room_num,\n" +
                "un_sale_stall_price,\n" +
                "un_sale_room_price,building_id,is_parking FROM  v_sman_value_report where   end_date=(select end_date from v_sman_value_report order by end_date desc limit 1)";
        //查询供货视图数据
        List<Map<String, Object>> listData = jdbcTemplategxc.queryForList(sqls);
        if (listData != null && listData.size() > 0) {
            //根据时间删除
            newGXCInterfaceDao.deleteReportValueAll(mon3);
            //写入动态货值
            newGXCInterfaceDao.insertReportValue(listData);
        }
        //更新数据条数
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setMessages("初始化供销存数据成功!");
        resultBody.setCode(200);
        return resultBody;
    }

    /**
    * 初始化所有供货
    * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody getSupplyValue() throws ParseException {
        gxcinterfaceDao.delSupply();
        SimpleDateFormat   sdf = new SimpleDateFormat("yyyy-MM");
        Calendar dd = Calendar.getInstance();//定义日期实例
        Calendar dd2 = Calendar.getInstance();//定义日期实例
        dd.setTime(sdf.parse("2020-01"));//设置日期起始时间
        dd2.setTime(sdf.parse(sdf.format(new Date())));
        dd2.add(Calendar.MONTH,-1);
        while(!dd.getTime().after(sdf.parse(sdf.format(dd2.getTime())))) {//判断是否到结束日期//判断是否到结束日期
            String startTime = sdf.format(dd.getTime());
            dd.add(Calendar.MONTH, 1);
          //  String endTime = sdf.format(dd.getTime());
          //  String sqls="SELECT * FROM  v_sman_supply_plan where create_time >='"+startTime+"' and create_time < '"+endTime+"'";
            String sqls="SELECT * FROM  v_sman_supply_plan where affiliation_month ='"+startTime+"'";
            System.out.println("======="+sqls);
            //查询供货视图数据
            List<Map<String, Object>> listData = jdbcTemplategxc.queryForList(sqls);
            if (listData.size()>10000){
               List res = averageAssign(listData,15);
                for (Object re : res) {
                    gxcinterfaceDao.insertvaluegh((List<Map<String, Object>>) re);
                }
            }else {
                gxcinterfaceDao.insertvaluegh(listData);
            }
        }
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
        return ResultBody.success("成功！");
    }

    @Override
    public void initmmidm(String projectId) {


        //删除并新增区域项目关系
        newGXCInterfaceDao.insertprojectrel(projectId);
        //修改区域排序
        newGXCInterfaceDao.updateprojectrel(projectId);
        //初始组织表（已使用idm组织信息）
        //初始化项目分期
        newGXCInterfaceDao.insertProjectStagerel(projectId);

        List<Map> groupList = newGXCInterfaceDao.getGroupIdByProjectId(projectId);
        List<Map> productList = newGXCInterfaceDao.getProductIdByProjectId(projectId);
        String group_id = "";
        String product = "";

        for (Map map : groupList) {
            group_id += "'" + map.get("group_id") + "',";
        }
        for (Map map : productList) {
            product += "'" + map.get("product_id") + "',";
        }
        String resGroupId = group_id.substring(0, group_id.length() - 1);
        String resProductId = product.substring(0, product.length() - 1);
        System.out.println(resProductId);
        //初始化分期组团关系表
        newGXCInterfaceDao.insertStageGroup(resGroupId);
        //组团楼栋
        newGXCInterfaceDao.insertGroupDesignbuildrel(resGroupId);
        //组团业态
        newGXCInterfaceDao.insertproductgroup(resGroupId);
        //初始化产品楼栋关系表
        newGXCInterfaceDao.insertproductrel(resProductId);
        //初始化产品面积段关系表
        newGXCInterfaceDao.insertproducareatrel(resGroupId);
        //集团到面积段关系表
        newGXCInterfaceDao.insertmainrel(projectId);
        //初始化整合关系
    }


    @Override
    public void updataBusinsee(String projectId) {
        List<Map> groupList = newGXCInterfaceDao.getGroupIdByProjectId(projectId);
        List<Map> productList = newGXCInterfaceDao.getProductIdByProjectId(projectId);
        List<Map> houseList = newGXCInterfaceDao.getHouseIdByProjectId(projectId);
        List<Map> stageList = newGXCInterfaceDao.getStageIdByProjectId(projectId);
        String qyId = newGXCInterfaceDao.getQyIdByProjectId(projectId);
        String group_id = "";
        String product = "";
        String house_id = "";
        String stage_id = "";

        for (Map map1 : groupList) {
            group_id += "'" + map1.get("group_id") + "',";
        }
        for (Map map2 : stageList) {
            stage_id += "'" + map2.get("stage_id") + "',";
        }
        for (Map map3 : productList) {
            product += "'" + map3.get("product_id") + "',";
        }
        for (Map map4 : houseList) {
            house_id += "'" + map4.get("house_package_id") + "',";
        }
        String resGroupId = group_id.substring(0, group_id.length() - 1);
        String resProductId = product.substring(0, product.length() - 1);
        String resHouseId = house_id.substring(0, house_id.length() - 1);
        String resStageId = stage_id.substring(0, stage_id.length() - 1);
        System.out.println("业态ID"+resProductId);
        System.out.println("组团ID"+resGroupId);
        System.out.println("面积段ID"+resHouseId);
        System.out.println("分期ID"+resStageId);
        List<Map> sysorg = newGXCInterfaceDao.selectquyu(projectId);//查询所有的区域集团跟事业部
        newGXCInterfaceDao.deletebu(projectId,resGroupId,resProductId,resHouseId,resStageId,qyId);
      /*  Map map = new HashMap();
        map.put("org_id", "f20c7c1f-d941-11e9-abaa-00163e05721e");
        map.put("Sort_code", "0");
        map.put("father_id", "-1");
        map.put("type", "1");
        map.put("orgdep_id", "00000001");
        map.put("org_name", "旭辉集团");
        map.put("is_delete_flag", "0");
        map.put("monthly_weekly_type", "1");*/
        //sysorg.add(0, map);
        List<Map> list = new ArrayList<>();
        String uid = null;
        String puid = null;
        String suid = null;
        String guid = null;
        String buid = null;
        String luid = null;
        int code = 0;
        for (int i = 0; i <= sysorg.size(); i++) {
            if (i == sysorg.size()) {
                continue;
            }
            uid = UUID.randomUUID().toString();
            sysorg.get(i).put("Sort_code", code++);
            sysorg.get(i).put("guid", uid);
                sysorg.get(i).put("father_id", "f20c7c1f-d941-11e9-abaa-00163e05721e");
                sysorg.get(i).put("type", "2");
            //添加前先修改表
            newGXCInterfaceDao.insertbusinessunit(sysorg.get(i));//添加事业部

            List<Map> businessunit = newGXCInterfaceDao.selectProjectt(String.valueOf(sysorg.get(i).get("orgdep_id")), projectId);


            //添加项目
            for (int j = 0; j <= businessunit.size(); j++) {
                if (j == businessunit.size()) {
                    continue;
                }
                //区分月度项目周度项目
                String monthly_weekly_type = businessunit.get(j).get("monthly_type") + "";
                monthly_weekly_type = monthly_weekly_type.equals("1") ? monthly_weekly_type : "0";
                puid = UUID.randomUUID().toString();
                businessunit.get(j).put("type", "3");//添加类型
                businessunit.get(j).put("father_id", sysorg.get(i).get("org_id"));//添加区域集团的父id
                businessunit.get(j).put("guid", puid);//添加事业表的guid
                businessunit.get(j).put("region_org_id", sysorg.get(i).get("orgdep_id"));
                businessunit.get(j).put("Sort_code", code++);
                newGXCInterfaceDao.insertprojects(businessunit.get(j));//添加项目
                String flag = businessunit.get(j).get("flag") + "";
                String monthly_type = businessunit.get(j).get("monthly_type") + "";

                //周度不生成分期之下
                if (monthly_type == null || monthly_type == "" || "null".equals(monthly_type)) {
                    continue;
                }
                //查询分期
                List<Map> businessstaging = newGXCInterfaceDao.selectStagingg(String.valueOf((businessunit.get(j).get("project_id"))));
                //查询分期
                for (int l = 0; l <= businessstaging.size(); l++) {
                    if (l == businessstaging.size()) {
                        continue;
                    }
                    suid = UUID.randomUUID().toString();
                    if (businessstaging == null || businessstaging.size() == 0 || businessstaging.get(l) == null || businessstaging.get(l).size() == 0) {
                        break;
                    }
                    businessstaging.get(l).put("type", "4");//添加类型
                    businessstaging.get(l).put("father_id", businessunit.get(j).get("pro_id"));//添加区域集团的父id
                    businessstaging.get(l).put("guid", suid);//添加事业表的guid
                    businessstaging.get(l).put("region_org_id", sysorg.get(i).get("orgdep_id"));
                    businessstaging.get(l).put("project_org_id", businessunit.get(j).get("project_id"));
                    businessstaging.get(l).put("Sort_code", code++);
                    businessstaging.get(l).put("monthly_weekly_type", monthly_weekly_type);
                    newGXCInterfaceDao.insertstagings(businessstaging.get(l));//添加分期


                    //查询出来的组团
                    List<Map> group = newGXCInterfaceDao.selectGroupp(String.valueOf(businessstaging.get(l).get("stage_id")), projectId);
                    //添加组团

                    for (int n = 0; n <= group.size(); n++) {
                        if (n == group.size()) {
                            continue;
                        }
                        if (group == null || group.size() == 0 || group.get(n) == null || group.get(n).size() == 0) {
                            break;
                        }
                        guid = UUID.randomUUID().toString();
                        group.get(n).put("type", "5");//添加类型
                        group.get(n).put("father_id", suid);//添加区域集团的父id
                        group.get(n).put("guid", guid);//添加事业表的guid
                        group.get(n).put("region_org_id", sysorg.get(i).get("orgdep_id"));
                        group.get(n).put("project_org_id", businessunit.get(j).get("project_id"));
                        group.get(n).put("stage_org_id", businessstaging.get(l).get("stage_code"));
                        group.get(n).put("Sort_code", code++);
                        group.get(n).put("monthly_weekly_type", monthly_weekly_type);
                        newGXCInterfaceDao.insertgroups(group.get(n));//添加组团

                        //             list.add(group.get(n));
                        //根据组团id查询业态
                           List<Map> designbuild = newGXCInterfaceDao.selectDesignBuildd(String.valueOf(group.get(n).get("group_id")),projectId);
                        String num = null;

                          for (int o = 0; o <= designbuild.size(); o++) {
                            if (o == designbuild.size()) {
                                continue;
                            }
                            if (designbuild == null || designbuild.size() == 0 || designbuild.get(o) == null || designbuild.get(o).size() == 0) {
                                break;
                            }
                            System.out.println(designbuild.get(o).get("product_code") + "product");
                            buid = UUID.randomUUID().toString();
                            designbuild.get(o).put("type", "6");//添加类型
                            designbuild.get(o).put("father_id", guid);//添加区域集团的父id
                            designbuild.get(o).put("guid", buid);//添加事业表的guid
                            designbuild.get(o).put("region_org_id", sysorg.get(i).get("orgdep_id"));
                            designbuild.get(o).put("project_org_id", businessunit.get(j).get("project_id"));
                            designbuild.get(o).put("stage_org_id", businessstaging.get(l).get("stage_code"));
                            designbuild.get(o).put("group_org_id", group.get(n).get("group_code"));
                            designbuild.get(o).put("Sort_code", code++);
                            System.out.println("------------------------新增了" + code + "条");
                            designbuild.get(o).put("monthly_weekly_type", monthly_weekly_type);
                              newGXCInterfaceDao.insertesignbuilds(designbuild.get(o));//添加业态

                            //nos数据不生产面积段
                        if ("1".equals(flag)) {
                                continue;
                            }
                            //根据业态查询面积段
                            List<Map> lou = newGXCInterfaceDao.selectArea(String.valueOf(designbuild.get(o).get("product_code")),projectId);

                                for (int p = 0; p <= lou.size(); p++) {
                                if (p == lou.size()) {
                                    continue;
                                }
                                if (lou == null || lou.size() == 0 || lou.get(p) == null || lou.get(p).size() == 0) {
                                    break;
                                }
                                luid = UUID.randomUUID().toString();
                                lou.get(p).put("type", "7");//添加类型
                                lou.get(p).put("father_id", buid);//添加区域集团的父id
                                lou.get(p).put("guid", luid);//添加事业表的guid
                                lou.get(p).put("region_org_id", sysorg.get(i).get("orgdep_id"));
                                lou.get(p).put("project_org_id", businessunit.get(j).get("project_id"));
                                lou.get(p).put("stage_org_id", businessstaging.get(l).get("stage_code"));
                                lou.get(p).put("group_org_id", group.get(n).get("group_code"));
                                lou.get(p).put("product_org_id", designbuild.get(o).get("product_code"));
                                lou.get(p).put("Sort_code", code++);
                                lou.get(p).put("monthly_weekly_type", monthly_weekly_type);
                                    newGXCInterfaceDao.insertArea(lou.get(p));//添加面积段

                            }

                        }
                    }
               }
            }
        }
        //月计划初始化对应区域monthly_weekly_type=1字段
        newGXCInterfaceDao.updateBusinessUnit();
                    }


    @Override
    public ResultBody intiSingDataAll(String projectId) {
        List<Map> groupList = newGXCInterfaceDao.getGroupIdByProjectId(projectId);
        List<Map> productList = newGXCInterfaceDao.getProductIdByProjectId(projectId);
        List<Map> houseList = newGXCInterfaceDao.getHouseIdByProjectId(projectId);
        List<Map> stageList = newGXCInterfaceDao.getStageIdByProjectId(projectId);
        String qyId = newGXCInterfaceDao.getQyIdByProjectId(projectId);
        String group_id = "";
        String product = "";
        String house_id = "";
        String stage_id = "";

        for (Map map1 : groupList) {
            group_id += "'" + map1.get("group_id") + "',";
        }
        for (Map map2 : stageList) {
            stage_id += "'" + map2.get("stage_id") + "',";
        }
        for (Map map3 : productList) {
            product += "'" + map3.get("product_id") + "',";
        }
        for (Map map4 : houseList) {
            house_id += "'" + map4.get("house_package_id") + "',";
        }
        String resGroupId = group_id.substring(0, group_id.length() - 1);
        String resProductId = product.substring(0, product.length() - 1);
        String resHouseId = house_id.substring(0, house_id.length() - 1);
        String resStageId = stage_id.substring(0, stage_id.length() - 1);
        System.out.println("业态ID"+resProductId);
        System.out.println("组团ID"+resGroupId);
        System.out.println("面积段ID"+resHouseId);
        System.out.println("分期ID"+resStageId);
        //设置时间格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat yearformat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthformat = new SimpleDateFormat("MM");
        Calendar ca = Calendar.getInstance();
        //当前年
        int nowYear = Integer.parseInt(yearformat.format(ca.getTime()));
        //当前月
        int nowMonth = Integer.parseInt(monthformat.format(ca.getTime()));
        //获得实体类
        int addYear=2020;
        ca.set(Calendar.YEAR, addYear);
        //设置月份
        ca.set(Calendar.MONTH,0);
        while (addYear<=nowYear){
            //设置最后一天
            ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
            //最后一天格式化
            String lastDay = format.format(ca.getTime());
            //累计年度
            addYear = Integer.parseInt(yearformat.format(ca.getTime()));
            if(addYear>nowYear){
                return null;
            }
            int year = ca.get(Calendar.YEAR);
            // 获取月，这里需要需要月份的范围为0~11，因此获取月份的时候需要+1才是当前月份值
            int month = ca.get(Calendar.MONTH) + 1;

            String monthAdd="";
            if(month<10){
                monthAdd="0"+month;
            }else{
                monthAdd=month+"";
            }
            Map paramMap=new HashMap();
            paramMap.put("monthStartTime",year+"-"+monthAdd+"-01 00:00:00");
            paramMap.put("monthSendTime",lastDay+" 23:59:59");

            //当前年月
            if(addYear==nowYear && month==nowMonth){
                addYear++;
            }
            paramMap.put("projectId",projectId);
            paramMap.put("resGroupId",resGroupId);
            paramMap.put("resProductId",resProductId);
            paramMap.put("resHouseId",resHouseId);
            paramMap.put("qyId",qyId);
            paramMap.put("resStageId",resStageId);
           newGXCInterfaceDao.initializationSingingData(paramMap);
            ca.add(Calendar.MONTH,1);
        }
        return null;
    }


    /**
     *
     * 查询事业部
     *
     * @return
     */

    @Override
    public int getBusiness(Map map) {
        List<Map> groupList = newGXCInterfaceDao.getGroupIdByProjectId(map.get("projectId")+"");
        List<Map> productList = newGXCInterfaceDao.getProductIdByProjectId(map.get("projectId")+"");
        List<Map> houseList = newGXCInterfaceDao.getHouseIdByProjectId(map.get("projectId")+"");
        List<Map> stageList = newGXCInterfaceDao.getStageIdByProjectId(map.get("projectId")+"");
        String qyId = newGXCInterfaceDao.getQyIdByProjectId(map.get("projectId")+"");
        String group_id = "";
        String product = "";
        String house_id = "";
        String stage_id = "";

        for (Map map1 : groupList) {
            group_id += "'" + map1.get("group_id") + "',";
        }
        for (Map map4 : stageList) {
            stage_id += "'" + map4.get("stage_id") + "',";
        }
        for (Map map2 : productList) {
            product += "'" + map2.get("product_id") + "',";
        }
        for (Map map3 : houseList) {
            house_id += "'" + map3.get("house_package_id") + "',";
        }
        String resGroupId = group_id.substring(0, group_id.length() - 1);
        String resProductId = product.substring(0, product.length() - 1);
        String resHouseId = house_id.substring(0, house_id.length() - 1);
        String resStageId = stage_id.substring(0, stage_id.length() - 1);
        System.out.println("业态ID"+resProductId);
        System.out.println("组团ID"+resGroupId);
        System.out.println("面积段ID"+resHouseId);
        System.out.println("分期ID"+resStageId);
        System.out.println(qyId);
        map.put("resGroupId",resGroupId);
        map.put("resProductId",resProductId);
        map.put("resHouseId",resHouseId);
        map.put("resStageId",resStageId);
        map.put("qyId",qyId);
        //查看月度计划表中是否存在这个年月日
        //先删除

        List<Map> list = newGXCInterfaceDao.selectPlanMonth(map);
        String oneId = cn.visolink.utils.UUID.randomUUID().toString();
        if(list.size()>0){
            map.put("oneId",list.get(0).get("guid"));
        }else {
            map.put("oneId",oneId);
        }

        SimpleDateFormat versions = new SimpleDateFormat("yyyyMMdd");
        String nowDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Date dt = new Date();
        String version = versions.format(dt);
        //添加月度计划表

        String ymd = map.get("months").toString();
        //截取年份
        String year=ymd.substring(0,4);
        //截取月份
        String month=ymd.substring(5,7);
        map.put("planName", year + "年" + month + "月销售计划");


        //newGXCInterfaceDao.insertMonthPlan(map);
        //添加月度指标

        map.put("version",version);
        //先删除
        newGXCInterfaceDao.deleteBasic(map);
        newGXCInterfaceDao.deleteBasicIndex(map);
        newGXCInterfaceDao.getBusiness(map);

        //初始化年度计划签约数据
        newGXCInterfaceDao.initYearPlanSignData(map);

        //获取指定月份最后一天
        String endtime = DateUtil.getLastDayOfMonth(ymd)+ " 23:59:59";
        //获取指定月份第一天
        String  starttime= DateUtil.getFirstDay(ymd)+ " 00:00:00";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM");
        c.add(Calendar.MONTH, -1);
        String kc_date = simpleDateFormatDay.format(c.getTime());
        map.put("startDate",starttime);
        map.put("endDate",endtime);
        map.put("kc_date",kc_date);
        //---修改项目供货值、套数、版本
        newGXCInterfaceDao.updateProjectValue(map);
        //---修改组团供货值
        newGXCInterfaceDao.updateGroupcodeValue(map);
        //---修改分期供货值
        newGXCInterfaceDao.updateInstallmentValue(map);
        //---修改业态供货值
        newGXCInterfaceDao.updateProductValue(map);
        //---修改面地段供货值
        newGXCInterfaceDao.updateHousePackageValue(map);
        //---修改区域供货值
            newGXCInterfaceDao.updateAreaValue(map);
            //---修改集团供货值
            newGXCInterfaceDao.updateGroupValue(map);

            //修改区域前一个月平均成交率
            newGXCInterfaceDao.updateRegionOneMonth(map);
            //修改区域前三个月平均成交率
               newGXCInterfaceDao.updateRegionThreeMonth(map);
            //修改项目前一个月平均成交率
            newGXCInterfaceDao.updateProjectOneMonth(map);
            //修改项目前三个月平均成交率
            newGXCInterfaceDao.updateProjectThreeMonth(map);
            //修改集团前一个月平均成交率
            newGXCInterfaceDao.updateGroupOneMonth(map);
            //修改集团前三个月平均成交率
            newGXCInterfaceDao.updateGroupThreeMonth(map);

            //修改区域年度计划签约(无用表)
            newGXCInterfaceDao.updateRegionYearSign(map);
            //修改集团年度计划签约(无用表)
            newGXCInterfaceDao.updateGroupYearSign(map);
            //修改集团的明源数据
            newGXCInterfaceDao.updateGroupmy(map);
            //修改区域的明源数据
            newGXCInterfaceDao.updateRegionmy(map);
            //修改把值为空改为零
            newGXCInterfaceDao.updateIsnull(map);

            //修改汇总数据
            newGXCInterfaceDao.updateTotalPlan(map);

        //添加月度指标详情
        map.put("type",1);
        newGXCInterfaceDao.insertMonthPlanIndex(map);
        map.put("type",2);
        newGXCInterfaceDao.insertMonthPlanIndex(map);
        map.put("type",3);
        newGXCInterfaceDao.insertMonthPlanIndex(map);

        /*驳回表一 二三四*/
        List<Map> paramList = new ArrayList<>();
        Map paramMap = new HashMap();
        paramMap.put("business_id",map.get("projectId"));
        paramMap.put("months",map.get("months"));
        paramMap.put("plan_status",0);
        paramMap.put("preparedByLevel",3);
        paramMap.put("region_org_id",qyId);
        paramList.add(paramMap);
        groupMangerUpdateController.updatePlanEffective(paramList);

        SysLog sysLog=new SysLog();
        sysLog.setTaskName("月度计划！");
        sysLog.setStartTime(nowDate);
        sysLog.setNote("月度计划初始化数据完成");
        //timeLogsDao.insertLogs(sysLog);
        return 1;
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



