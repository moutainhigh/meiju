package cn.visolink.salesmanage.nonprojectpur.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.exception.ResultUtil;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.nonprojectpur.service.NonProjectPurService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 非工程采购数据处理控制器
 * </p>
 *
 * @author bql
 * @since 2020-11-26
 */
@RestController
@Api(tags = "待结佣金数据接口")
@Slf4j
@RequestMapping("/nonProjectPur")
public class NonProjectPurController {

    private final NonProjectPurService nonProjectPurService;

    public NonProjectPurController(NonProjectPurService nonProjectPurService) {
        this.nonProjectPurService = nonProjectPurService;
    }


    @Log("初始化非工程采购数据")
    @ApiOperation(value = "初始化非工程采购数据")
    @PostMapping("/initNonProjectPur")
    public ResultBody initNonProjectPur() {
        try {
            nonProjectPurService.initNonProjectPur();
            return ResultBody.success("");
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }



    @Log("修改非工程采购数据")
    @ApiOperation(value = "修改非工程采购数据")
    @PostMapping("/updateNonProjectPur")
    @ApiImplicitParam(name = "map", value = "map")
    public Map updateNonProjectPur(HttpServletRequest request, @RequestBody Map map) {
        String requstTime = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
        try {
            // 参数校验
            if (CollUtil.isEmpty(map)) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，参数错误");
            }
            Map<String, Object> requestInfoMap = (Map<String, Object>) map.get("requestInfo");
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) requestInfoMap.get("data");

            // 参数校验
            if (CollUtil.isEmpty(dataList) || dataList.size() ==0) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，参数错误");
            }
            // 调用服务
            return nonProjectPurService.updateNonProjectPur(request,dataList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.getErrorMap(requstTime, "调用失败");
        }
    }

}
