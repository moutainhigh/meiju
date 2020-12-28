package cn.visolink.salesmanage.reportmenumanager.service.impl;

import cn.visolink.exception.BadRequestException;
import cn.visolink.salesmanage.reportmenumanager.dao.ReportMenuDao;
import cn.visolink.salesmanage.reportmenumanager.service.ReportMenuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
@Service
@Transactional(rollbackFor = Exception.class)
public class ReportMenuServiceImpl implements ReportMenuService {
    @Autowired
    ReportMenuDao reportMenuDao;
    /*
     * 插入一张报表
     * */
    @Override
    public Integer insertReportMenu(Map<String, Object> map) {
        /*
        * 默认新增菜单等级为3，若没有PID说明是2级菜单，就将LEVEL=1
        * */
        map.put("levels",2);
        String pid=map.get("parentId")+"";
        if("".equals(pid)||"null".equals(pid)||"-1".equals(pid)){
            Map<String,Object> bestmenu= reportMenuDao.selectBestMenu();
            map.put("parentId",bestmenu.get("id"));
            map.put("levels",1);
        }else{
            map.put("parentId",pid);
        }
        /*
         * 遍历Map所有值，若传进来的数有空，则将它默认为NULL
         * */
        Iterator iterable= map.entrySet().iterator();
        while (iterable.hasNext()) {
            Map.Entry entry_d = (Map.Entry) iterable.next();
            Object key = entry_d.getKey();
            Object value = entry_d.getValue();
            if(value==""){
                value=null;
            }


            map.put(key.toString(),value);
        }
        return reportMenuDao.insertReportMenu(map);
    }
    /*
     * 跟新一张报表
     * */
    @Override
    public Integer updateReportMenu(Map<String, Object> map) {
        //parentId;
        String parentId=null;
        parentId=map.get("parentId")+"";
        if("-1".equals(parentId)||"null".equals(parentId)||"".equals(parentId)){
            //查询全部报表的id
            parentId= reportMenuDao.getParentId();
        }
        map.put("parentId",parentId);
        return reportMenuDao.updateReportMenu(map);
    }
    /*
     * 删除一张报表
     * */
    @Override
    public Integer deleteReportMenu(Map<String, Object> map) {
            /*
            * 删除前先判断它下面有没有子菜单,若有直接返回NULL
            * */
           Map<String,Object> deleteMap=new HashMap<>();
        deleteMap.put("PID",map.get("id").toString());
          List<Map> list=  reportMenuDao.selectReportMenu(deleteMap);
          if(list!=null && list.size()>0){
              return null;
          }
        return reportMenuDao.deleteReportMenu(map);
    }
    /*
     * 查找报表
     * */
    @Override
    public PageInfo selectReportMenu(Map map) {

        /**
         * 前台需要翻页，前台增加pagenum和pagesize两个参数
         * ，在param里封装pagenum和pagesize两个参数来用于在后台进行翻页
         * */

            if (!map.isEmpty()) {
                if(map.get("pageNum")==null){
                    map.put("pageNum",1);
                }
                if(map.get("pageSize")==null){
                    map.put("pageSize",10);
                }
                PageHelper.startPage((Integer)map.get("pageNum"),(Integer) map.get("pageSize"));

                List<Map> list= reportMenuDao.selectReportMenu(map);

                PageInfo<Map> page=new PageInfo<>(list);
                return page;
            }

        return null;
    }
}
