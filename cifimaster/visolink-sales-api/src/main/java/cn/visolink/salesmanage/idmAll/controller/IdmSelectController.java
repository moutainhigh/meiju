package cn.visolink.salesmanage.idmAll.controller;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.idmAll.service.IdmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "查询idm")
@RequestMapping("/idmSelect")
public class IdmSelectController {

    @Autowired
    private IdmService idmService;

    @Log("查询idm岗位组")
    @ApiOperation(value = "查询idm岗位组")
    @RequestMapping(value = "/selectSysPostOrg",method = RequestMethod.GET)
    public VisolinkResultBody selectSysPostOrg() {
        VisolinkResultBody bobys=new VisolinkResultBody();
        List<Map> result = idmService.selectIdm(null);
        bobys.setResult(result);
        return bobys;
    }

    @Log("添加岗位组")
    @ApiOperation(value = "添加岗位组")
    @PostMapping(value = "/addSysPostOrg")
    public VisolinkResultBody addSysPostOrg(@RequestBody Map map) {
        VisolinkResultBody bobys=new VisolinkResultBody();
        if(map==null || map.size()==0 || map.get("ids")==null || map.get("ids")==""){
            bobys.setCode(1);
            bobys.setMessages("ids参数不能为空!");
            return bobys;
        }
        Map result = idmService.addIdmCommonjobs(map);
        bobys.setResult(result);
        return bobys;
    }

    @Log("初始化岗位组")
    @ApiOperation(value = "初始化岗位组")
    @PostMapping(value = "/initSysPostOrg")
    //@Scheduled(cron = "0 0 2 * * ?")
    public VisolinkResultBody initSysPostOrg() {
        VisolinkResultBody bobys=new VisolinkResultBody();
        Map result = idmService.sfIdmCommonjobs();
        bobys.setResult(result);
        return bobys;
    }
}
