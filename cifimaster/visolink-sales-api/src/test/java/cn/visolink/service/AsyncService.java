package cn.visolink.service;

import java.util.concurrent.CountDownLatch;

/**
 * @Author wcl
 * @Description //TODO
 * @Date 2019/7/17 20:15
 * @Version 1.0
 **/
public interface AsyncService {
    /**
     *  执行异步任务
     */
    void writeTxt(CountDownLatch countDownLatch);

}
