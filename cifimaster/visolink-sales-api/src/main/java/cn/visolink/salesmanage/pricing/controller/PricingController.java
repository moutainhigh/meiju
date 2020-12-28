package cn.visolink.salesmanage.pricing.controller;

import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.datainterface.dao.DatainterfaceDao;
import cn.visolink.salesmanage.pricing.service.PricingService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author sjl
 * @Created date 2019/11/11 3:58 下午
 */
@RestController
@Api("定调价数据获取")
@RequestMapping("pricing")
public class PricingController {


    @Autowired
    private PricingService pricingService;


    @Autowired
    private DatainterfaceDao interfacedao;

    @Log("更新签约数据")
    @CessBody
    @ApiOperation(value = "")
    @PostMapping(value = "/inertface")
    public void inertface() {

    }

    /**
     * 渲染数据（定调价编制页面）
     */
    @Log("渲染数据定调价编制页面")
    @CessBody
    @ApiOperation(value = "渲染数据（定调价编制页面）")
    @PostMapping(value = "/getApplyData")
    public ResultBody getApplyData(@RequestBody Map paramMap) {
        //boid
        try {
            return pricingService.getApplyData(paramMap);
        } catch (Exception e) {
            //打印日志
            throw new BadRequestException(-10089, "定调价编制页面获取渲染数据失败!", e);
        }

    }

    @Log("定调价编制接口")
    @CessBody
    @ApiOperation(value = "获取字典数据（定调价编制接口）")
    @PostMapping(value = "/pricingAuthorizedStrength")
    public ResultBody pricingAuthorizedStrength(@RequestBody Map map) {


        return pricingService.pricingAuthorizedStrength(map);
    }

    /**
     * 获取字典数据（定调价编制页面）
     */
    @Log("获取字典数据")
    @CessBody
    @ApiOperation(value = "获取字典数据（定调价编制页面）")
    @PostMapping(value = "/getPricingDictoryList")
    public ResultBody getPricingDictoryList(@RequestBody Map map) {
        return pricingService.getPricingDictoryList(map);
    }

    /**
     * 上传附件
     */
    @Log("上传附件")
    @CessBody
    @ApiOperation(value = "上传附件")
    @PostMapping(value = "/uploadAttachment")
    public ResultBody uploadAttachment(@RequestParam("file") MultipartFile multipartFile) {
        return pricingService.uploadAttachment(multipartFile);
    }

    @Log("删除附件")
    @CessBody
    @ApiOperation(value = "删除附件")
    @PostMapping(value = "/deleteFile")
    public ResultBody deleteFile(@RequestBody Map map) {
        return pricingService.deleteFile(map);
    }

    @Log("获取定调价-经营对标-货值数据")
    @ApiOperation(value = "获取定调价-经营对标-货值数据")
    @PostMapping(value = "/getBusinessIndicatorsValue")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"projectId\":\"项目id\",\"TjPlanGUID\":\"调价主键ID\"}", paramType = "body"),
    })
    public ResultBody getBusinessIndicatorsValue(@RequestBody Map map) {
        try {
            return pricingService.getBusinessIndicatorsValue(map);
        } catch (Exception e) {
            //打印日志
            throw new BadRequestException(-16_1001, "定调价编制页面，获取统计数据失败!", e);
        }
    }

    @Log("获取定调价-价格对比数据")
    @ApiOperation(value = "获取定调价-价格对比数据")
    @PostMapping(value = "/getPriceComparisonData")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"projectId\":\"项目id\",\"TjPlanGUID\":\"调价主键ID\",\"isPricing\":\"是否定价，0：定价、1：非定价\"}", paramType = "body"),
    })
    public ResultBody getPriceComparisonData(@RequestBody Map map) {
        try {
            return pricingService.getPriceComparisonData(map);
        } catch (Exception e) {
            //打印日志
            throw new BadRequestException(-16_1001, "定调价编制页面，获取统计数据失败!", e);
        }
    }

    @Log("获取定调价-定调价结果")
    @ApiOperation(value = "获取定调价-定调价结果")
    @PostMapping(value = "/getPriceAdjustmentResult")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"projectId\":\"项目id\",\"TjPlanGUID\":\"调价主键ID\",\"isPricing\":\"是否定价，0：定价、1：非定价\"}", paramType = "body"),
    })
    public ResultBody getPriceAdjustmentResult(@RequestBody Map map) {
        try {
            return pricingService.getPriceAdjustmentResult(map);
        } catch (Exception e) {
            //打印日志
            throw new BadRequestException(-16_1001, "定调价编制页面，获取统计数据失败!", e);
        }
    }

    @Log("获取定调价-规划货值压力测算")
    @ApiOperation(value = "获取定调价-规划货值压力测算")
    @PostMapping(value = "/getPressureMeasurement")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"projectId\":\"项目id\",\"TjPlanGUID\":\"调价主键ID\",\"isPricing\":\"是否定价，0：定价、1：非定价\"}", paramType = "body"),
    })
    public ResultBody getPressureMeasurement(@RequestBody Map map) {
        try {
            return pricingService.getPressureMeasurement(map);
        } catch (Exception e) {
            //打印日志
            throw new BadRequestException(-16_1001, "定调价编制页面，获取统计数据失败!", e);
        }
    }

    @Log("获取定调价-所有统计数据")
    @ApiOperation(value = "获取定调价-所有统计数据")
    @PostMapping(value = "/getAllStatistics")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"projectId\":\"项目id\",\"TjPlanGUID\":\"调价主键ID\",\"isPricing\":\"是否定价，0：定价、1：非定价\",\"isNewEdition\":\"是否新版，0：否、1：是\"}", paramType = "body"),
    })
    public ResultBody getAllStatistics(@RequestBody Map map) {
        try {
            return pricingService.getAllStatistics(map);
        } catch (Exception e) {
            //打印日志
            throw new BadRequestException(-16_1001, "定调价编制页面，获取统计数据失败!", e);
        }
    }

    @Log("定调价-价格监测")
    @ApiOperation(value = "定调价-价格监测")
    @PostMapping(value = "/priceMonitor")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"projectId\":\"项目id\",\"TjPlanGUID\":\"调价主键ID\",\"baseId\":\"主数据表id\",\"isNewEdition\":\"是否新版，0：否、1：是\"}", paramType = "body"),
    })
    public ResultBody priceMonitor(@RequestBody Map map) {
        try {
            return pricingService.priceMonitor(map);
        } catch (Exception e) {
            //打印日志
            throw new BadRequestException(500, "获取价格监测数据失败!", e);
        }
    }

    @Log("获取规划楼栋调整")
    @ApiOperation(value = "获取规划楼栋调整")
    @PostMapping(value = "/getAdjustmentInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"projectId\":\"项目id\",\"TjPlanGUID\":\"调价主键ID\"}", paramType = "body"),
    })
    public ResultBody getAdjustmentInfo(@RequestBody Map map) {
        try {
            return pricingService.getAdjustmentInfo(map);
        } catch (Exception e) {
            //打印日志
            throw new BadRequestException(-16_1001, "获取规划楼栋调整数据失败!", e);
        }
    }

    /*public static void main(String[] args) {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE `mm_ap_set_pricing_attached` (\n" +
                "  `id` varchar(36) CHARACTER SET utf8 NOT NULL COMMENT '主键',\n");

        String[] strArray1 = {"投资版", "年初版", "定调价前", "定调价后", "定调价后减投资版", "定调价后减年初版", "定调价后减定调价前"};
        String[] strArray2 = {"tzb", "ncb", "dtjq", "dtjh", "dtjh_tzb", "dtjh_ncb", "dtjh_dtjq"};
        String[] strArray3 = {"-货值", "-权益前-利润额", "-权益后-利润额", "-利润率", "-IRR", "-回收期"};
        String[] strArray4 = {"_hz", "_qyq_lre", "_qyh_lre", "_lrl", "_irr", "_hsq"};
        for (int j = 0; j < strArray3.length; j++) {
            for (int i = 0; i < strArray1.length; i++) {
                sb.append(MessageFormat.format("  `{1}{3}` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT {0}{2},\n",
                        "'" + strArray1[i], strArray2[i], strArray3[j] + "'", strArray4[j]));
            }
        }

        strArray1 = new String[]{"定调价前-利润额", "定调价前-利润率", "定调价后-利润额", "定调价后-利润率", "投资版-利润额", "投资版-利润率", "金额变动", "利润率变动"};
        strArray2 = new String[]{"dtjq_lre", "dtjq_lrl", "dtjh_lre", "dtjh_lrl", "tzb_lre", "tzb_lrl", "jebd", "lrlbd"};
        strArray3 = new String[]{"已实现-", "当年预计-", "待实现-"};
        strArray4 = new String[]{"ysx_", "dnyj_", "dsx_"};
        for (int j = 0; j < strArray3.length; j++) {
            for (int i = 0; i < strArray1.length; i++) {
                sb.append(MessageFormat.format("  `{3}{1}` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT {2}{0},\n",
                        strArray1[i] + "'", strArray2[i], "'" + strArray3[j], strArray4[j]));
            }
        }

        strArray1 = new String[]{"当批次调价前", "当批次调价后", "差异"};
        strArray2 = new String[]{"dpctjq", "dpctjh", "cy"};
        strArray3 = new String[]{"-权益前-利润额", "-权益后-利润额", "-利润率"};
        strArray4 = new String[]{"_qyq_lre", "_qyh_lre", "_lrl"};
        for (int j = 0; j < strArray3.length; j++) {
            for (int i = 0; i < strArray1.length; i++) {
                sb.append(MessageFormat.format("  `{1}{3}` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT {0}{2},\n",
                        "'" + strArray1[i], strArray2[i], strArray3[j] + "'", strArray4[j]));
            }
        }

        sb.append("  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定调价主表附属表';");
        System.out.println("================");
        System.out.println(sb.toString());
        System.out.println("================");
    }*/
}
