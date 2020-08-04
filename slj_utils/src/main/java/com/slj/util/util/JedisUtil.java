package com.slj.util.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class JedisUtil {
    private static Logger logger= LoggerFactory.getLogger (JedisUtil.class);

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;

    private static JedisPool jedisPool = null;

    private static final String LOCK_SUCCESS = "OK";

    private static final String SET_IF_NOT_EXIST = "NX";

    private static final String SET_WITH_EXPIRE_TIME = "EX";  //EX代表秒，PX代表毫秒

    private static final int REDIS_DB_INDEX= 1;

    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 初始化Redis连接池
     */
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxIdle(3000);  //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config, "localhost",6379
                   , 3000, "admin");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取Jedis实例
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                resource.select (REDIS_DB_INDEX);
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 释放jedis资源
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
    }

    public static void hset(String key,String filed,String value){
        Jedis jedis=null;
        try {
            jedis =getJedis ( );
            jedis.hset (key, filed, value);
        }finally {
            returnResource(jedis);
        }
    }

    public static Map<String,String> hgetAll (String key){
        Map<String,String> map=new HashMap<> ();
        Jedis jedis=null;
        try {
            jedis=getJedis ();
            map=jedis.hgetAll (key);
        }finally {
            returnResource(jedis);
        }
        return map;
    }

    public static void del(String key){
        Jedis jedis=null;
        try{
            jedis=getJedis ();
            jedis.del (key);
        }finally {
            returnResource (jedis);
        }
    }

    public static void setString(String key,String value){
        Jedis jedis=null;
        try{
            jedis=getJedis ();
            jedis.set (key,value);
        }finally {
            returnResource (jedis);
        }
    }

    public static Map<String,String> getAllString (){
        Map<String,String> map=new HashMap<> ();
        Jedis jedis=null;
        try {
            jedis=getJedis ();
            jedis.keys ("*");
        }finally {
            returnResource(jedis);
        }
        return map;
    }

    /**
     * 尝试获取分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public static boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {
        Jedis jedis=null;
        try{
            jedis=getJedis ();
            String result=jedis.set (lockKey,requestId,SET_IF_NOT_EXIST,SET_WITH_EXPIRE_TIME,expireTime);
            if (LOCK_SUCCESS.equals(result)) {
                logger.info ("获取redis锁");
                return true;
            }
        }finally {
            returnResource (jedis);
        }
        return false;
    }

    /**
     * 释放分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseDistributedLock(String lockKey, String requestId) {
        Jedis jedis=null;
        try{
            jedis=getJedis ();
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
            if (RELEASE_SUCCESS.equals(result)) {
                logger.info ("释放redis锁");
                return true;
            }
        }finally {
            returnResource (jedis);
        }
        return false;
    }
}
