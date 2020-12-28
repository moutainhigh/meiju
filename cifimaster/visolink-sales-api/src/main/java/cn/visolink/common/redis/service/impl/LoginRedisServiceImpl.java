package cn.visolink.common.redis.service.impl;

import cn.visolink.common.monitor.domain.vo.RedisVo;
import cn.visolink.common.redis.service.LoginRedisService;
import cn.visolink.common.redis.service.RedisService;
import cn.visolink.utils.PageUtil;
import cn.visolink.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author WCL
 * @date 2018-12-10
 */
@Service
public class LoginRedisServiceImpl implements LoginRedisService {

    @Autowired
    RedisTemplate redisTemplate;

    @Value("${jwt.expiration}")
    private Long expiration;


    @Override
    public Page<RedisVo> findByKey(String key, Pageable pageable){
        List<RedisVo> redisVos = new ArrayList<>();
        if(!"*".equals(key)){
            key = "*" + key + "*";
        }
        for (Object s : redisTemplate.keys(key)) {
            // 过滤掉权限的缓存
            if (s.toString().indexOf("role::loadPermissionByUser") != -1 || s.toString().indexOf("user::loadUserByUsername") != -1) {
                continue;
            }
            RedisVo redisVo = new RedisVo(s.toString(),redisTemplate.opsForValue().get(s.toString()).toString());
            redisVos.add(redisVo);
        }
        Page<RedisVo> page = new PageImpl<RedisVo>(
                PageUtil.toPage(pageable.getPageNumber(),pageable.getPageSize(),redisVos),
                pageable,
                redisVos.size());
        return page;
    }



    @Override
    public void delete(String key) {
        redisTemplate.delete(SecurityUtils.getUsername()+"-"+key);
    }

    @Override
    public void flushdb() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Override
    public String getCodeVal(String key) {
        try {
            String value = redisTemplate.opsForValue().get(SecurityUtils.getUsername()+"-"+key).toString();
            return value;
        }catch (Exception e){
            return "";
        }
    }

    @Override
    public Object getVal(String key) {
        return redisTemplate.opsForValue().get(SecurityUtils.getUsername()+"-"+key);
    }

    @Override
    public void saveString(String key, String val) {
        redisTemplate.opsForValue().set(SecurityUtils.getUsername()+"-"+key,val);
        redisTemplate.expire(key,expiration, TimeUnit.MILLISECONDS);
    }


    @Override
    public void saveSet(String key, Set set) {
        redisTemplate.opsForSet().add(SecurityUtils.getUsername()+"-"+key,set);
        redisTemplate.expire(key,expiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public void saveHash(String key, Map map) {
        redisTemplate.opsForHash().putAll(SecurityUtils.getUsername()+"-"+key,map);
        redisTemplate.expire(key,expiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public void saveLeftList(String key, List list) {
        redisTemplate.opsForList().leftPush(SecurityUtils.getUsername()+"-"+key,list);
        redisTemplate.expire(key,expiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public void saveRightList(String key, List list) {
        redisTemplate.opsForList().rightPush(SecurityUtils.getUsername()+"-"+key,list);
        redisTemplate.expire(key,expiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public List<Object> getLeftList(String key) {
        return  (List<Object>) redisTemplate.opsForList().leftPop(SecurityUtils.getUsername()+"-"+key);
    }

    @Override
    public List<Object> getRightList(String key) {
        return  (List<Object>) redisTemplate.opsForList().rightPop(SecurityUtils.getUsername()+"-"+key);
    }

    @Override
    public Map getMap(String key) {
        return (Map) redisTemplate.opsForHash().entries(SecurityUtils.getUsername()+"-"+key);
    }

    @Override
    public Set<Object> getSet(String key) {
        return redisTemplate.opsForSet().members(SecurityUtils.getUsername()+"-"+key);
    }
}
