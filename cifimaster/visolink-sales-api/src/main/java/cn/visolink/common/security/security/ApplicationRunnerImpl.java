package cn.visolink.common.security.security;

import cn.visolink.common.redis.service.RedisService;
import cn.visolink.constant.VisolinkConstant;
import cn.visolink.firstplan.dataAccess.dao.DataAccessDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author yhx
 * createTime 2018-11-07 22:37
 **/
@Component
public class ApplicationRunnerImpl implements ApplicationRunner {

    @Autowired
    private RedisService redisService;

    @Autowired
    DataAccessDao dataAccessDao;
    @Override
    public void run(ApplicationArguments args) throws Exception {

        int loginOutStatus = dataAccessDao.getLoginOutStatus();
        if(loginOutStatus==1){
            redisService.deleteByKeyLike(VisolinkConstant.TOKEN_KEY);
            System.out.println("重新启动完成！！！所有用户缓存信息已清除！");
        }else{
            System.out.println("重新启动完成！！！本次重启不清除用户缓存信息！");
        }

        //System.out.println();
    }
}
