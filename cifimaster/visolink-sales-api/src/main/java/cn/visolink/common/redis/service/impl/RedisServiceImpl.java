package cn.visolink.common.redis.service.impl;

import cn.visolink.common.redis.service.RedisService;
import cn.visolink.common.monitor.domain.vo.RedisVo;
import cn.visolink.utils.PageUtil;
import cn.visolink.utils.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
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
public class RedisServiceImpl implements RedisService {

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
    public void setRedisObject(String key, Object obj) {
        redisTemplate.opsForValue().set(key,obj);
        //redis2天失效 测试5分钟
        redisTemplate.expire(key,1, TimeUnit.DAYS);
    }


    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void flushdb() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }


    @Override
    public void deleteByKeyLike(String key) {
        Set<String> keys = redisTemplate.keys(key+"*");
        redisTemplate.delete(keys);
    }

    @Override
    public String getCodeVal(String key) {
        try {
            String value = redisTemplate.opsForValue().get(key).toString();
            return value;
        }catch (Exception e){
            return "";
        }
    }

    @Override
    public Set<String> getLikeKey(String key) {
        Set<String> keys = redisTemplate.keys(key + "*");
        return keys;
    }

    @Override
    public Object getVal(String key) {
        return redisTemplate.opsForValue().get(key);
    }


    @Override
    public void saveObject(String key, Object obj) {
        redisTemplate.opsForValue().set(key,obj);
        //redisTemplate.expire(key,expiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public Object getObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void saveString(String key, String val) {
        redisTemplate.opsForValue().set(key,val);
        redisTemplate.expire(key,expiration, TimeUnit.MILLISECONDS);
    }


    @Override
    public void saveSet(String key, Set set) {
        redisTemplate.opsForSet().add(key,set);
        redisTemplate.expire(key,expiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public void saveHash(String key, Map map) {
        redisTemplate.opsForHash().putAll(key,map);
        redisTemplate.expire(key,expiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public void saveLeftList(String key, List list) {
        redisTemplate.opsForList().leftPush(key,list);
        redisTemplate.expire(key,expiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public void saveRightList(String key, List list) {
        redisTemplate.opsForList().rightPush(key,list);
        redisTemplate.expire(key,expiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public List<Object> getLeftList(String key) {
        return  (List<Object>) redisTemplate.opsForList().leftPop(key);
    }

    @Override
    public List<Object> getRightList(String key) {
        return  (List<Object>) redisTemplate.opsForList().rightPop(key);
    }

    @Override
    public Map getMap(String key) {
        return (Map) redisTemplate.opsForHash().entries(key);
    }

    @Override
    public Set<Object> getSet(String key) {
        return redisTemplate.opsForSet().members(key);
    }
}
