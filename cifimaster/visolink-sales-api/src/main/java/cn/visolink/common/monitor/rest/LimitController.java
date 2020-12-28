package cn.visolink.common.monitor.rest;

import cn.visolink.limit.annotation.Limit;
import cn.visolink.limit.aspect.LimitType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author WCL
 * 接口限流测试类
 */
@RestController
@RequestMapping("test")
public class LimitController {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();

    /**
     * 测试限流注解，下面配置说明该接口 60秒内最多只能访问 10次，保存到redis的键名为 limit_test，
     */
    @Limit(key = "test", period = 60, count = 10, name = "testLimit", prefix = "limit",limitType = LimitType.IP)
    @GetMapping("/limit")
    public int testLimit() {
        return ATOMIC_INTEGER.incrementAndGet();
    }
}
