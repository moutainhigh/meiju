package cn.visolink.common.redis.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 可自行扩展
 * @author WCL
 * @date 2018-12-10
 */
public interface RedisService {

    /**
     * findById
     * @param key
     * @param pageable
     * @return
     */
    Page findByKey(String key, Pageable pageable);


    /**
     * findById
     * @param key
     * @param
     * @return
     */
    void setRedisObject(String key, Object obj);

    /**
     * 查询验证码的值
     * @param key
     * @return
     */
    String getCodeVal(String key);

    /**
     * 批量查询多个key
     * @param key
     * @return
     */
    Set<String> getLikeKey(String key);

    /**
     * 查询缓存值
     * @param key
     * @return
     */
    Object getVal(String key);

    /**
     * 保存Object
     * @param key
     * @param obj
     */
    void saveObject (String key, Object obj);


    /**
     * 获取Object
     * @param key
     * @param
     * @return
     */
    Object getObject (String key);

    /**
     * 保存String
     * @param key
     * @param val
     */
    void saveString (String key, String val);

    /**
     * 保存Set
     * @param key
     * @param set
     */
    void saveSet (String key, Set set);

    /**
     * 保存Hash
     * @param key
     * @param map
     */
    void saveHash (String key, Map map);


    /**
     * 保存 List
     * @param key
     * @param list
     */
    void saveLeftList (String key, List list);


    /**
     * 获取List
     * @param key
     * @return
     */
    List<Object> getLeftList (String key);



    /**
     * 获取Map
     * @param key
     * @return
     */
    Map getMap (String key);

    /**
     * 获取Map
     * @param key
     * @return
     */
    Set<Object> getSet (String key);


    /**
     * 保存 List
     * @param key
     * @param list
     */
    void saveRightList (String key, List list);


    /**
     * 获取List
     * @param key
     * @return
     */
    List<Object> getRightList (String key);


    /**
     * delete
     * @param key
     */
    void delete(String key);

    /**
     * 清空所有缓存
     */
    void flushdb();

    /**
     * 根据key模糊删除
     * @param key
     */
    void deleteByKeyLike(String key);
}
