package com.slj.util.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by albert.shen on 2018/7/11.
 */
public class PropertyUtil {
    private static final Logger logger = LoggerFactory.getLogger (PropertyUtil.class);
    private static Properties props;
    public static PropertyUtil getInstance(String resourceName) {
        return new PropertyUtil (resourceName);
    }

    private PropertyUtil(String resourceName) {
        props = new Properties ( );
        InputStream in = null;
        try {
            in = PropertyUtil.class.getClassLoader ( ).getResourceAsStream ( resourceName + ".properties");
            props.load (in);
        } catch (FileNotFoundException e) {
            logger.error ("properties文件未找到");
        } catch (IOException e) {
            logger.error ("出现IOException");
        } finally {
            try {
                if (null != in) {
                    in.close ( );
                }
            } catch (IOException e) {
                logger.error ("properties文件流关闭出现异常");
            }
        }
    }

    public static String getProperty(String key) {
        return new String(props.getProperty(key));
    }
}
