package cn.visolink.system.parameter.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.visolink.exception.BadRequestException;
import cn.visolink.system.parameter.dao.ParameterManagementDao;
import cn.visolink.system.parameter.service.ParameterManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Transactional(rollbackFor = Exception.class)
public class ParameterManagementServiceImpl implements ParameterManagementService {

    @Autowired
    private ParameterManagementDao parameterDao;

    /**
     * 查询系统所有的参数
     *
     * @param reqMap
     */
    @Override
    public List<Map> getSystemAllparams(HashMap<String, String> reqMap) {
        //查询所有的参数
        List<Map> resMap = parameterDao.getSystemAllParams(reqMap);
        //结果集合
        ArrayList<Object> resultList = new ArrayList<>();
        //一级菜单
        ArrayList<Map> oneList = new ArrayList<>();
        //二级菜单
        CopyOnWriteArrayList<Map> twoList = new CopyOnWriteArrayList<>();

        //遍历查询参数
        for (Map map : resMap) {

            String levels = String.valueOf(map.get("Levels"));
            //一级菜单
            if ("0".equals(levels)) {
                oneList.add(map);
                continue;
            }
            //二级菜单
            if ("1".equals(levels)) {
                twoList.add(map);
                continue;
            }
        }

        //遍历一级菜单
        for (Map oneMap : oneList) {
            String id = String.valueOf(oneMap.get("ID"));
            //遍历二级菜单
            ArrayList<Object> list = new ArrayList<>();
            for (Map twoMap : twoList) {
                String pid = String.valueOf(twoMap.get("PID"));
                if (id.equals(pid)) {
                    list.add(twoMap);
                    twoList.remove(twoMap);
                }
            }
            oneMap.put("Children", list);
        }
        return oneList;
    }

    @Override
    public List<Map> getDicByCodeList(Map map) {
        return parameterDao.getDicByCodeList(map);
    }

    @Override
    public List<Map> getDicByCodeLevelList(Map map) {
        return parameterDao.getDicByCodeLevelList(map);
    }

    /**
     * 系统新增参数
     *
     * @param reqMap
     * @return
     */
    @Override
    public int saveSystemParam(Map reqMap) {

        reqMap.put("ID", UUID.randomUUID().toString());
        try {
            Map paramCodeExists = parameterDao.getSystemParamCodeExists(reqMap);
            if (MapUtil.isEmpty(paramCodeExists)) {
                throw new BadRequestException("添加新菜单失败！");
            }

            String IsReadOnly = reqMap.get("IsReadOnly") + "";
            if (reqMap.get("IsReadOnly") == null || "".equals(IsReadOnly) || "null".equalsIgnoreCase(IsReadOnly)) {
                reqMap.put("IsReadOnly", "1");
            }
            int i = parameterDao.insertSystemParam(reqMap);
            return i;

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new BadRequestException("添加新菜单失败！");
        }
    }

    /**
     * 系统修改参数
     *
     * @param reqMap
     * @return
     */
    @Override
    public int modifySystemParam(Map reqMap) {

        return parameterDao.modifySystemParam(reqMap);
    }

    /**
     * 删除系统参数
     *
     * @param reqMap
     * @return
     */
    @Override
    public int removeSystemParam(Map reqMap) {

        return parameterDao.removeSystemParam(reqMap);
    }

    /**
     * 查询子集参数（树形）
     *
     * @param reqMap
     * @return
     */
    @Override
    public List<Map> getSystemTreeChildParams(Map reqMap) {

        return parameterDao.getSystemTreeChildParams(reqMap);
    }

    @Override
    public Map getSystemChildParams(Map reqMap) {
        int pageIndex = Integer.parseInt(reqMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(reqMap.get("pageSize").toString());
        int i = (pageIndex - 1) * pageSize;
        reqMap.put("pageIndex", i);
        List<Map> projectChildParams = parameterDao.getSystemChildParams(reqMap);
        Map systemChildParamsCount = parameterDao.getSystemChildParamsCount(reqMap);
        Map<String, Object> map = MapUtil.newHashMap();
        map.put("systemParams", projectChildParams);
        map.put("count", systemChildParamsCount.get("count"));
        return map;
    }

    /**
     * 启用/禁用参数
     *
     * @param reqMap
     * @return
     */
    @Override
    public int modifySystemParamStatus(Map reqMap) {
        return parameterDao.modifySystemParamStatus(reqMap);
    }
}
