package com.slj.util.util;

import java.util.Random;
import java.util.UUID;

/**
 * Created by albert.shen on 2018/9/25.
 */
public class IDUtils {
    public static long genItemId() {
        //取当前时间的长整形值包含毫秒
        long millis = System.currentTimeMillis();
        //long millis = System.nanoTime();
        //加上两位随机数
        Random random = new Random();
        int end2 = random.nextInt(99);
        //如果不足两位前面补0
        String str = millis + String.format("%02d", end2);
        long id = new Long(str);
        return id;
    }

    public static String getStringId(){
        return UUID.randomUUID ().toString ().replace ("-","");
    }
}
