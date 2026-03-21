package com.ai.basecommon.utils;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.util.MappingValidator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class DozerUtil {


    private static Mapper dozerMapper = DozerBeanMapperBuilder.buildDefault();

    /**
     * list->list
     *
     * @param source
     * @param destinationClass
     * @return
     */
    public static List maps(final List source, Class destinationClass) {
        List desList = new ArrayList<>();
        if (source == null){
            return desList;
        }

        try{
            MappingValidator.validateMappingRequest(source, destinationClass);
            for (Object src : source) {
                Object des = dozerMapper.map(src, destinationClass);
                desList.add(des);
            }
        }catch (Exception e){
            System.out.println("对象转换出错："+e.getMessage());
        }
        return desList;
    }

    /**
     * object-.object
     *
     * @param source
     * @param destinationClass
     * @param <T>
     * @return
     */

    public static <T> T map(final Object source, Class<T> destinationClass) {
        if (source == null){
            return null;
        }
        try{
            MappingValidator.validateMappingRequest(source, destinationClass);
            return dozerMapper.map(source, destinationClass);
        }catch (Exception e){
            System.out.println("对象转换错误：" + e.getMessage());
            return null;
        }
    }


    /**
     * 对象转换 支持私有属性和内部类
     * @param fromPojo
     * @param toPojo
     * @return
     */
    public static Object pojoToPojo(Object fromPojo,Object toPojo){
        Class fromPojoClass = fromPojo.getClass();
        Method fromPojoClassDeclaredMethods[] = fromPojoClass.getDeclaredMethods();
        Class toPojoClass = toPojo.getClass();
        for (int i = 0; i < fromPojoClassDeclaredMethods.length; i++) {
            if(fromPojoClassDeclaredMethods[i].getName().indexOf("get")==0){
                String toName = fromPojoClassDeclaredMethods[i].getName().replace("get","");
                toName = toName.substring(0,1).toLowerCase() + toName.substring(1);
                try {
                    Field toPojoClassDeclaredField =  toPojoClass.getDeclaredField(toName);
                    toPojoClassDeclaredField.setAccessible(true);
                    toPojoClassDeclaredField.set(toPojo,fromPojoClassDeclaredMethods[i].invoke(fromPojo));
                }catch (NoSuchFieldException e){
//                    System.out.println("目标POJO没有["+toName+"]字段,将忽略");
                }catch(IllegalAccessException e){
//                    System.out.println("目标POJO没有["+toName+"]字段,将忽略");
                }catch (InvocationTargetException e){
//                    System.out.println("目标POJO没有["+toName+"]字段,将忽略");
                }
            }
        }
        return toPojo;
    }


}
