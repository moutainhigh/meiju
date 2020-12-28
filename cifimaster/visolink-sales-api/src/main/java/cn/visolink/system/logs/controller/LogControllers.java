package cn.visolink.system.logs.controller;

import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.logs.service.LogServices;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author：sjl
 * @date： 2019/10/24 10:17
 */
@RestController
@Api(tags = "日志管理")
@RequestMapping("/log")
public class LogControllers {

    @Autowired
    private LogServices logServices;

    @Log("查询日志信息")
    @CessBody
    @ResponseBody
    @RequestMapping(value = "/queryLogInfo", method = RequestMethod.POST)
    public Map queryLogInfo(@RequestBody Map paramMap) {
        try {
            Map resultMap = logServices.queryLogInfo(paramMap);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
            return null;
    }
}
