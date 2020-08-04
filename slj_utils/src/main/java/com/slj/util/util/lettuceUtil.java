package com.slj.util.util;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.SetArgs;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import java.util.concurrent.TimeUnit;

/**
 * Created by albert.shen on 2018/12/11.
 */
public class lettuceUtil {
    private static GenericObjectPool<StatefulRedisConnection<String, String>> pool;

    private static long REDIS_COMMAND_OPERATION_TIMEOUT = 3000;

    static {
        RedisURI redisUri = RedisURI.Builder.redis("localhost")
                .withPort (6379)
                .withPassword("admin")
                .withDatabase(8)
                .build();
        RedisClient client = RedisClient.create(redisUri);
        pool = ConnectionPoolSupport
                .createGenericObjectPool(() -> client.connect(), new GenericObjectPoolConfig ());
    }

    public static boolean set(String key,String value,long expire ){
        StatefulRedisConnection<String, String>
                connection=null;
        boolean flag=false;
        try{
            connection = pool.borrowObject();
            RedisAsyncCommands<String,String> commands =  connection.async ();
            //设置过期时间，1分钟
            SetArgs setArgs=new SetArgs ();
            setArgs.ex (expire);
            RedisFuture<String> future =  commands.set (key,value,setArgs);
            flag = future.await(REDIS_COMMAND_OPERATION_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace ( );
        }finally {
            if(connection!=null) {
                pool.returnObject (connection);
            }
        }
        return flag;
    }

    public static String get(String key){
        StatefulRedisConnection<String, String>
                connection=null;
        String result = null;
        try {
            connection = pool.borrowObject();
            RedisAsyncCommands<String,String> commands =  connection.async ();
            RedisFuture<String> future= commands.get (key);
            result = future.get(REDIS_COMMAND_OPERATION_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace ( );
        }finally {
            if(connection!=null) {
                pool.returnObject (connection);
            }
        }
        return result;
    }

    public static boolean del(String key){
        StatefulRedisConnection<String, String>
                connection=null;
        boolean flag=false;
        try {
            connection= pool.borrowObject ();
            RedisAsyncCommands<String,String> commands =  connection.async ();
            RedisFuture<Long> future= commands.del (key);
            flag = future.await(REDIS_COMMAND_OPERATION_TIMEOUT,TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace ( );
        }finally {
            if(connection!=null){
                pool.returnObject (connection);
            }
        }
        return flag;
    }
}

