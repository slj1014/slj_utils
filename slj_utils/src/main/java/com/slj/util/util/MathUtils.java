/**
 * @(#)MathUtils.java Created by albert.shen on 2019/12/25   14:59
 * <p>
 * Copyrights (C) 2019保留所有权利
 */

package com.slj.util.util;

import java.util.ArrayList;
import java.util.List;

/**
 * (类型功能说明描述)
 *
 * <p>
 * 修改历史:                                 <br>  
 * 修改日期           修改人员       版本       修改内容<br>  
 * -------------------------------------------------<br>  
 * 2019/12/25 14:59   albert.shen     1.0       初始化创建<br>
 * </p> 
 *
 * @author albert.shen
 * @version 1.0
 * @since JDK1.8
 */
public class MathUtils {
    /**
     * 获取与运算的结果的信息集合V2
     * 返回2的几次方，示例：2^3 返回：3
     * 升级版
     *
     * @param value
     * @return
     */
    public static List<Integer> GetBitNumsV2(int value) {
        List<Integer> result = new ArrayList<>();
        int index = 1;
        while ((int) Math.pow(2, index) <= value) {
            int c_value = (int) Math.pow(2, index);
            if ((value & c_value) == c_value) {
                result.add(index);
            }
            index += 1;
        }
        return result;
    }

  /*  public static void main(String[] args) {
        List<Integer> s= GetBitNumsV2((int) (Math.pow(2,1)+Math.pow(2,2)+Math.pow(2,3)));
        s.forEach(i->{
            System.out.println(i);
        });
    }*/
}

