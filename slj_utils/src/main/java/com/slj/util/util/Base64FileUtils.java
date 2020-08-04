package com.slj.util.util;

import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author: albert.shen
 * @create: 2019-01-01 10:52
 * @program: SLJ_UTILS
 * @description: ${description}
 **/
public class Base64FileUtils {
    public static String uploadFile(String base64Data,String filePath,String fileName){
        base64Data = base64Data.replaceAll("data:image/jpeg;base64,", "");
        BASE64Decoder decoder = new BASE64Decoder();
        // Base64解码
        byte[] imageByte = null;
        OutputStream imageStream=null;
        String filename = filePath + "/" + fileName;
        try {
            imageByte = decoder.decodeBuffer(base64Data);
            for (int i = 0; i < imageByte.length; ++i) {
                if (imageByte[i] < 0) {// 调整异常数据
                    imageByte[i] += 256;
                }
            }
            // 创建文件件
            File file = new File(filePath);
            if (!file.exists()) {// 如果文件夹不存在
                file.mkdir();// 创建文件夹
            }
            // 生成文件
            File imageFile = new File(filename);
            if(!imageFile.exists()){
                imageFile.createNewFile();
            }
            imageStream = new FileOutputStream(imageFile);
            imageStream.write(imageByte);
            imageStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(imageStream!=null){
                try {
                    imageStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filename;
    }
}
