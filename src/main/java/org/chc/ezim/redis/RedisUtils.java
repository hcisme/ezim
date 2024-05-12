package org.chc.ezim.redis;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils<V> {

    @Resource
    private RedisTemplate<String, V> redisConfigTemplate;

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public V getValue(String key) {
        return key == null ? null : redisConfigTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */

    public boolean setValue(String key, V value) {
        try {
            redisConfigTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("设置redis key {}, value {} 失败", key, value);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean setValueAndExpire(String key, V value, long time) {
        try {
            if (time > 0) {
                redisConfigTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                setValue(key, value);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置redis key {}, value {} 失败", key, value);
            return false;
        }
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    public boolean setExpire(String key, long time) {
        try {
            if (time > 0) {
                redisConfigTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 取队列
     */
    public List<V> getQueueList(String key) {
        return redisConfigTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 删除缓存
     *
     * @param key 可以传 一个值 或 多个
     */
    public void delete(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisConfigTemplate.delete(key[0]);
            } else {
                redisConfigTemplate.delete(Arrays.asList(key));
            }
        }
    }

    /**
     * 移除值为value的
     *
     * @param key   键
     * @param value 值
     * @return 移除的个数
     */
    public long remove(String key, V value) {
        try {
            Long num = redisConfigTemplate.opsForList().remove(key, 1, value);
            return num;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean lPush(String key, V value, long time) {
        try {
            redisConfigTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                setExpire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean lPush(String key, List<V> values, long time) {
        try {
            redisConfigTemplate.opsForList().rightPushAll(key, values);
            if (time > 0) {
                setExpire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
