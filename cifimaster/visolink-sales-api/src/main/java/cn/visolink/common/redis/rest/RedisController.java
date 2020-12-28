package cn.visolink.common.redis.rest;

import cn.visolink.constant.VisolinkConstant;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.common.monitor.domain.vo.RedisVo;
import cn.visolink.common.redis.service.RedisService;
import cn.visolink.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author WCL
 * @date 2018-12-10
 */
@RestController
@RequestMapping("api")
public class RedisController {

    @Autowired
    private RedisService redisService;

    @Log("查询Redis缓存")
    @GetMapping(value = "/redis")
    public ResponseEntity getRedis(String key, Pageable pageable){
//        String username = SecurityUtils.getUsername();
        return new ResponseEntity(redisService.findByKey(key,pageable), HttpStatus.OK);
    }

    @Log("删除Redis缓存")
    @DeleteMapping(value = "/redis/del")
    public ResponseEntity delete(@RequestBody RedisVo resources){
        redisService.delete(resources.getKey());
        return new ResponseEntity(HttpStatus.OK);
    }

//    @Log("清空Redis缓存")
//    @DeleteMapping(value = "/redis/all")
//    public ResponseEntity deleteAll(){
//        redisService.flushdb();
//        return new ResponseEntity(HttpStatus.OK);
//    }
}
