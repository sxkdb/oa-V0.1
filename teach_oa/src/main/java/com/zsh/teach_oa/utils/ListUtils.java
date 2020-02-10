package com.zsh.teach_oa.utils;

import java.util.List;
import java.util.stream.Collectors;

public class  ListUtils {

    /**
     * @param list
     * @Description: 把list转换为逗号分隔的字符串
     * @Return: java.lang.String
     * @Author: XXX
     * @Date: 2018/11/15
     */
    public static String listToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            //Lambda表达式
            return list.stream().collect(Collectors.joining(","));
        } else {
            return "";
        }
    }
}
