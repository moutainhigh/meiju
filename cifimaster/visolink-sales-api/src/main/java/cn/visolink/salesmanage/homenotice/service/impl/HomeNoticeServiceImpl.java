package cn.visolink.salesmanage.homenotice.service.impl;

import cn.visolink.exception.ResultBody;
import cn.visolink.exception.ResultUtil;
import cn.visolink.salesmanage.homenotice.dao.HomeNoticeDao;
import cn.visolink.salesmanage.homenotice.service.HomeNoticeService;
import cn.visolink.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
public class HomeNoticeServiceImpl implements HomeNoticeService {

    private final HomeNoticeDao homeNoticeDao;

    public HomeNoticeServiceImpl(HomeNoticeDao homeNoticeDao) {
        this.homeNoticeDao = homeNoticeDao;
    }

    /**
     * 获取申请人区域、部门、申请时间
     *
     * @param map map
     * @return map
     * */
    @Override
    public Map<String,Object> getHomeNoticeInfo(Map<String,Object> map){
        if(map.get("id")!=null && !"".equals(map.get("id")+"") ){
            String id = map.get("id")+"";
            map.put("data",homeNoticeDao.selectHomeNoticeById(id));
            map.put("fileList",homeNoticeDao.getFileLists(id));
        }
        return map;
    }

    /**
     * 提示首页公告
     *
     * @param map map
     * @return map
     * */
    @Override
    public ResultBody getHomeNotice(Map<String,Object> map){
        if(map.get("username")!=null){
            Map<String,Object> result = homeNoticeDao.getHomeNotice(map);
            if(!StringUtil.isEmpty(MapUtils.getString(result,"id"))){
                result.put("fileList",homeNoticeDao.getFileLists(MapUtils.getString(result,"id")));
            }
            return ResultUtil.success(result);
        }else{
            return ResultUtil.error(500,"当前登录人异常！");
        }
    }

    /**
     * 已阅
     *
     * @param map map
     * @return map
     * */
    @Override
    public ResultBody intoHomeNoticeRead(Map<String,Object> map){
        if(map.get("username")!=null){
            homeNoticeDao.intoHomeNoticeRead(map);
            return ResultUtil.success("已阅");
        }else{
            return ResultUtil.error(500,"当前登录人异常！");
        }
    }


    /**
     * 查询通告
     *
     * @param map map
     * @return list
     * */
    @Override
    public ResultBody selectHomeNotice(Map<String,Object> map){
        PageHelper.startPage(Integer.parseInt(map.get("pageIndex") + ""), Integer.parseInt(map.get("pageSize") + ""));
        List<Map<String,Object>> list =homeNoticeDao.selectHomeNotice(map);
        for (Map<String,Object> result : list) {
            if(!StringUtil.isEmpty(MapUtils.getString(result,"id"))){
                result.put("fileList",homeNoticeDao.getFileLists(MapUtils.getString(result,"id")));
            }
        }
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
    public int initHomeNotice(HttpServletRequest request,Map<String,Object> map){
        String uuid = UUID.randomUUID().toString();
        if(map.get("id")==null || "".equals(map.get("id"))){
            map.put("id",uuid);
            updateFileList(map,uuid);
            return homeNoticeDao.initHomeNotice(map);
        }else{
            String id = map.get("id")+"";
            Map<String,Object> data = homeNoticeDao.selectHomeNoticeById(id);
            if(data == null){
                map.put("id",uuid);
                updateFileList(map,uuid);
                return homeNoticeDao.initHomeNotice(map);
            }else{
                updateFileList(map,id);
                return homeNoticeDao.updateHomeNotice(map);
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
    public int deleteHomeNotice(Map<String,Object> map){
        return homeNoticeDao.deleteHomeNotice(map);
    }


    /**
     * 失效申请
     *
     * @param map map
     * @return int
     * */
    @Override
    public int isDelFile(Map<String,Object> map){
        return homeNoticeDao.isDelFile(map.get("id")+"");
    }



    /**
     * 修改上传文件bizId
     *
     * @param map map
     * @param id id
     * */
    private void updateFileList(Map<String,Object> map,String id){
        homeNoticeDao.isDelFile(id);
        List<String> list = (List<String>) map.get("fileList");
        if(list!=null && list.size()>0){
            homeNoticeDao.updateFileList(list,id);
        }
    }


}
