package cn.visolink.salesmanage.homeapply.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.exception.ResultUtil;
import cn.visolink.salesmanage.homeapply.dao.HomeApplyDao;
import cn.visolink.salesmanage.homeapply.entity.HomeApply;
import cn.visolink.salesmanage.homeapply.service.HomeApplyService;
import cn.visolink.salesmanage.packageanddiscount.dao.PackageanddiscountDao;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * <p>
 * 夏威审批流业务接口实现
 * </p>
 *
 * @author bql
 * @since 2020-09-16
 */
@Service
public class HomeApplyServiceImpl extends ServiceImpl<HomeApplyDao, HomeApply> implements HomeApplyService {

    private final HomeApplyDao homeApplyDao;
    private final PackageanddiscountDao packageanddiscountDao;

    private static final String ACCOUNT         = "HomeApply_Account";
    private static final String INITIALIZATION  = "HomeApply_Initialization";
    private static final String ADJUSTMENT      = "HomeApply_adjustment";
    private static final String XGHA            = "XG_HA";

    public HomeApplyServiceImpl(HomeApplyDao homeApplyDao,PackageanddiscountDao packageanddiscountDao) {
        this.homeApplyDao = homeApplyDao;
        this.packageanddiscountDao =packageanddiscountDao;
    }


    /**
     * 获取申请人区域、部门、申请时间
     *
     * @param map map
     * @return map
     * */
    @Override
    public Map<String,Object> getApplyInfo(Map<String,Object> map){
        if(map.get("BOID")!=null && !"".equals(map.get("BOID")+"") ){
            String id = map.get("BOID")+"";
            map.put("data",homeApplyDao.selectHomeApplyById(id));
            map.put("fileList",homeApplyDao.getFileLists(id));
        }else{
            String username =map.get("username")+"";
            Map m =homeApplyDao.getBelongDepartment(username);
            if(m == null){
                return null;
            }else{
                map.putAll(m);
                map.put("apply_time", DateUtil.format(new Date(),"yyyy-MM-dd"));
            }
        }
        return map;
    }


    /**
     * 查询审批审请
     *
     * @param map map
     * @return list
     * */
    @Override
    public ResultBody selectHomeApply(Map<String,Object> map){
        setReallyFlowCode(map);
        PageHelper.startPage(Integer.parseInt(map.get("pageIndex") + ""), Integer.parseInt(map.get("pageSize") + ""));
        List<Map<String,Object>> list =homeApplyDao.selectHomeApply(map);
        PageInfo<Map<String,Object>> pageInfo = new PageInfo<>(list);
        ResultBody resultBody = new ResultBody<>();
        resultBody.setData(pageInfo);
        resultBody.setCode(200);
        return resultBody;
    }


    /**
     * 保存申请
     *
     * @param request request
     * @param map map
     * @return int
     * */
    @Override
    public int initHomeApply(HttpServletRequest request,Map<String,Object> map){
        String uuid = UUID.randomUUID().toString();
        setReallyFlowCode(map);
        if(map.get("id")==null || "".equals(map.get("id"))){
            map.put("id",uuid);
            updateFileList(map,uuid);
            return homeApplyDao.initHomeApply(map);
        }else{
            String id = map.get("id")+"";
            HomeApply data = this.getById(id);
            if(data == null){
                map.put("id",uuid);
                updateFileList(map,uuid);
                return homeApplyDao.initHomeApply(map);
            }else{
                updateFileList(map,id);
                return homeApplyDao.updateHomeApply(map);
            }
        }
    }

    /**
     * 失效申请
     *
     * @param map map
     * @return int
     * */
    @Override
    public int deleteHomeApply(Map<String,Object> map){
        return homeApplyDao.deleteHomeApply(map);
    }

    private void updateFileList(Map<String,Object> map,String id){
        List<String> list = (List<String>) map.get("fileList");
        if(list!=null && list.size()>0){
            homeApplyDao.updateFileList(list,id);
        }
    }

    /**
     * 发送申请
     *
     * @param request request
     * @param map map
     * @return boolean
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody initHomeApplyFlow(HttpServletRequest request,Map<String,Object> map){
        try {
            initHomeApply(request,map);
            String jsonMap = JSONObject.toJSONString(map);
            Map<String,Object> flowMap = new HashMap<>();
            flowMap.put("orgName", getBusinessUnitId(map));
            flowMap.put("comcommon", jsonMap);
            flowMap.put("flow_json", jsonMap);
            flowMap.put("json_id", map.get("id"));
            flowMap.put("flow_type", XGHA);
            flowMap.put("flow_code",map.get("flow_code"));
            flowMap.put("TITLE", map.get("apply_theme"));
            flowMap.put("creator", map.get("username"));
            flowMap.put("post_name", map.get("username"));

            if(map.get("id") !=null && !"".equals(map.get("id"))){
                int i = homeApplyDao.selectFlowInfoById(map.get("id")+"");
                if(i>0){
                    homeApplyDao.updateBrokerPolicyFlow(flowMap);
                }else{
                    homeApplyDao.saveBrokerPolicyFlow(flowMap);
                }
            }else{
                homeApplyDao.saveBrokerPolicyFlow(flowMap);
            }
            return ResultUtil.success(flowMap);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.error(500, "申请失败！");
        }
    }


    /**
     * 判定当前申请类型
     *
     * @param data data
     * */
    private void setReallyFlowCode(Map<String,Object> data){
        String code = data.get("flow_code")+"";
        switch (Integer.parseInt(code)) {
            case 0:
                data.put("flow_code","");
                break;
            case 1:
                data.put("flow_code",ACCOUNT);
                break;
            case 2:
                data.put("flow_code",INITIALIZATION);
                break;
            case 3:
                data.put("flow_code",ADJUSTMENT);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + code);
        }
    }

    /**
     * 获取请求头当前登录人
     *
     * @param request request
     * @return username
     * */
    private String getUserName(HttpServletRequest request) {
        String username = null;
        try {
            if (StrUtil.isNotBlank(request.getHeader("employeeName"))) {
                username = URLDecoder.decode(request.getHeader("employeeName"), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return username;
    }

    /**
     * 获取请求头当前登录人的区域
     *
     * @param map map
     * @return businessUnitId
     * */
    private String getBusinessUnitId(Map<String,Object> map){
        if ( "4".equals(map.get("org_level")) ) {
            Map buinessData = packageanddiscountDao.getBuinessData(map.get("org_id") + "");
            return buinessData.get("business_unit_id") + "";
        } else if ("2".equals(map.get("org_level"))) {
            return map.get("org_id") + "";
        } else if ("3".equals(map.get("org_level"))) {
            Map buinessData = packageanddiscountDao.getBuinessDataByOrgId(map.get("org_id") + "");
            return buinessData.get("business_unit_id") + "";
        }else{
            return "";
        }
    }

}
