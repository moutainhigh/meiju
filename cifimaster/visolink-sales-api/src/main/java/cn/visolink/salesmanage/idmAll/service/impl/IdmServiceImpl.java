package cn.visolink.salesmanage.idmAll.service.impl;

import cn.visolink.salesmanage.idmAll.dao.IdmMapper;
import cn.visolink.salesmanage.idmAll.service.IdmService;
import cn.visolink.system.org.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class IdmServiceImpl implements IdmService {

    @Autowired
    IdmMapper manager;
    @Autowired
    public OrganizationService organizationService;
    @Override
    public List<Map> selectIdm(Map map) {
        return manager.selectIdm(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map addIdmCommonjobs(Map params) {
        if(params==null || params.isEmpty()|| params.size()==0 || params.get("ids")==null || params.get("ids")==""){
            return null;
        }
        List<String> ids=(List)params.get("ids");

       // List<String> ids = JSON.parseArray(,String.class);
        //String ids=params.get("ids").toString().replace("[","(").replace("]",")");
        Map map=new HashMap();
        map.put("ids",ids);
        //插入中间表，in会报错，不成功，只能建中间表关联
        manager.insertIdmCommonjobs(ids);
        //生成通用岗位
        manager.addIdmCommonjobs();
        //数据授权初始化
        manager.addIUserProject();
        //初始化四层级
        organizationService.synOrgFourLevel();
        return null;
    }

    /**
     * 每天凌晨两点定时初始化
     * @return
     */
    @Override
    public Map sfIdmCommonjobs() {
        //生成通用岗位
        manager.addIdmCommonjobs();
        //数据授权初始化
        manager.addIUserProject();
        organizationService.synOrgFourLevel();
        return null;
    }


}
