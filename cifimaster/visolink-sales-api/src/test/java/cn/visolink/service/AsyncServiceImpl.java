package cn.visolink.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

/**
 * @Author wcl
 * @Description //TODO
 * @Date 2019/7/17 20:15
 * @Version 1.0
 **/
@Service
public class AsyncServiceImpl implements AsyncService {
    private static Logger logger = LoggerFactory.getLogger(AsyncServiceImpl.class.getName());

    @Async("asyncServiceExecutor")
    public void writeTxt(CountDownLatch countDownLatch){
        try {
        logger.info("线程-" + Thread.currentThread().getId() + "start");
        logger.info(String.valueOf(countDownLatch.getCount()));
        logger.info("线程-" + Thread.currentThread().getId() + "end");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //导入完后减1
            countDownLatch.countDown();
        }
    }
}
