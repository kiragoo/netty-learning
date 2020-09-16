package com.kirago.netty.im.common.util;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.Type;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MapperUtil {

    protected static final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    public MapperUtil(){}

    public static MapperFactory getMapperFactory(){
        return mapperFactory;
    }

    public static <A, B> ClassMapBuilder<A, B> classMap(Class<A> aType, Class<B> bType){
        return mapperFactory.classMap(aType, bType);
    }


    public static <T> T convert(Object obj, Class<T> tClass) {
        return mapperFactory.getMapperFacade().map(obj, tClass);
    }

    public static <S, T> void convert(S s, T t) {
        mapperFactory.getMapperFacade().map(s, t);
    }

    static {
        mapperFactory.getConverterFactory().registerConverter(new CustomConverter<Map<String, Object>, Map<String, Object>>() {
            @Override
            public Map<String, Object> convert(Map<String, Object> source, Type<? extends Map<String, Object>> destinationType, MappingContext mappingContext) {
                return new HashMap(source);
            }
        });
        mapperFactory.getConverterFactory().registerConverter(new CustomConverter<Map<Object, Object>, Map<Object, Object>>() {
            @Override
            public Map<Object, Object> convert(Map<Object, Object> source, Type<? extends Map<Object, Object>> destinationType, MappingContext mappingContext) {
                return new HashMap(source);
            }
        });
    }
    
}
