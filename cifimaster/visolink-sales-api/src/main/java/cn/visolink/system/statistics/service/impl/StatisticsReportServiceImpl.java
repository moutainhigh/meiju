package cn.visolink.system.statistics.service.impl;
import cn.hutool.core.map.MapUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.statistics.dao.StatisticsReportMapper;
import cn.visolink.system.statistics.service.StatisticsReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author sjl
 * @Created date 2019/11/8 10:49 上午
 */
@Service
public class StatisticsReportServiceImpl implements StatisticsReportService {

    @Autowired
    private StatisticsReportMapper statisticsReportMapper;

    @Override
    public ResultBody getStatisticsReportMenus(Map map) {

        /**
         * 获取该用户经过授权的报表列表
         */
        List<Map> parentList = new ArrayList<>();
        List<Map> syuList = new ArrayList<>();
        List<Map> childList = new ArrayList<>();
        //获取用户名

        Map userName = statisticsReportMapper.getUserName(map);
        String userNames=userName.get("userName")+"";
        List<Map> menuList = statisticsReportMapper.getStatisticsReportMenus(map);
        syuList.addAll(menuList);
        chuliUrl(menuList,userNames);
        if(menuList!=null&&menuList.size()>0){
            for (Map map1 : menuList) {
                String level=map1.get("levels")+"";
                if(level.equals("1")){
                    parentList.add(map1);
                }
                else if("2".equals(level)){
                    childList.add(map1);
                }
            }
        }
        if(parentList!=null&&parentList.size()>0){
            for (Map parent : parentList) {
                List<Object> list = new ArrayList<>();
                for (Map child : childList) {
                    String id=parent.get("id")+"";
                    String pid=child.get("pid")+"";
                    if(id.equals(pid)){
                        list.add(child);
                    }
                }
                parent.put("childMap",list);
            }
        }
        /**
         * 获取用户常用报表列表
         */
        List<Map> useMenuList=statisticsReportMapper.getComomUserReportMenus(map);
        chuliUrl(useMenuList,userNames);
        Map maps = MapUtil.newHashMap();
        Map<Object, Object> hashMap = new HashMap<>();

        if(parentList!=null&&parentList.size()>0){
            //封装常用报表数据
            hashMap.put("cyReport",useMenuList);
            //封装已授权报表数据
            hashMap.put("sqReport",parentList);
        }

        maps.put("content",hashMap);
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setData(maps);
        return resultBody;
    }

    @Override
    public ResultBody addCommomUserReportMenus(Map map) {
        String suID=map.get("suID")+"";
        if(suID==null||"".equals(suID)||"null".equals(suID)){
            statisticsReportMapper.insertCommomUserReportMenus(map);
        }else{
            statisticsReportMapper.addCommomUserReportMenus(map);
        }
        //suID
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setCode(200);
        resultBody.setMessages("添加成功");
        return resultBody;
    }

    public   List<Map> chuliUrl(List<Map> useMenuList,String userNames){
        if(useMenuList!=null&&useMenuList.size()>0){
            for (Map useMenu : useMenuList) {
                String url=useMenu.get("url")+"";
                if(url.contains("$userid$")){
                    String userid$ = url.replace("$userid$", userNames);
                    useMenu.put("url",userid$);
                }
            }
        }
        return useMenuList;
    }
}
