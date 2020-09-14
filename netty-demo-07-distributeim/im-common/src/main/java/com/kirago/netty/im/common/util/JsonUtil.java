package com.kirago.netty.im.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

@Slf4j
public class JsonUtil {
    
    public static GsonBuilder gsonBuilder = new GsonBuilder();
    
    private static final Gson gson;
    
    static {
        gsonBuilder.disableHtmlEscaping();
        gson = gsonBuilder.create();
    }
    
    public static byte[] pojo2Bytes(Object obj){
        String jsonString = object2JsonString(obj);
        try {
            return jsonString.getBytes("UTF-8");
        }catch (UnsupportedEncodingException e){
            log.error("POJO 转字节组 错误: {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public static String object2JsonString(Object obj){
        return gson.toJson(obj);
    }
    
    public static <T>T bytes2PoJo(byte[] bytes, Class<T> clazz){
        try {
            String jsonString = new String(bytes, "UTF-8");
            T t = jsonString2Object(jsonString, clazz);
            return t;
        }catch (UnsupportedEncodingException e){
            log.error("字节组转 POJO 错误:{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public static <T>T jsonString2Object(String str, Class<T> clazz){
        T t = gson.fromJson(str, clazz);
        return t;
    }
}
