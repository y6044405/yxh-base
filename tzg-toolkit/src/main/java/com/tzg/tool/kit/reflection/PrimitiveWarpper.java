package com.tzg.tool.kit.reflection;

/**
 * 
 * Filename:    PrimitiveType.java  
 * Description: 基本数据类型的包装类(以及String)  
 * Copyright:   Copyright (c) 2012-2013 All Rights Reserved.
 * Company:     tzg-soft.com Inc.
 * @author:     heyiwu 
 * @version:    1.0  
 * Create at:   2012-9-21 上午09:42:57  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2012-9-21      heyiwu      1.0         1.0 Version  
 *
 */
public enum PrimitiveWarpper {
    BOOLEAN(Boolean.class, Boolean.TYPE), CHAR(Character.class, Character.TYPE), STRING(String.class, Character.TYPE), BYTE(Byte.class, Byte.TYPE), SHORT(Short.class, Short.TYPE), INT(
                                                                                                                                                                                        Integer.class,
                                                                                                                                                                                        Integer.TYPE), LONG(
                                                                                                                                                                                                            Long.class,
                                                                                                                                                                                                            Long.TYPE), FLOAT(
                                                                                                                                                                                                                              Float.class,
                                                                                                                                                                                                                              Float.TYPE), DOUBLE(
                                                                                                                                                                                                                                                  Double.class,
                                                                                                                                                                                                                                                  Double.TYPE);

    private Class<?> clazz;
    private Class<?> type;

    PrimitiveWarpper(Class<?> clazz, Class<?> type) {
        this.clazz = clazz;
        this.type = type;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println(isPrimitive(Boolean.class));
        System.out.println(isPrimitive(boolean.class));
    }

    /**
     * 判断是否是基本数据类型或其对应的包装类
     * @param clas
     * @return
     */
    public static Boolean isPrimitive(Class<?> clas) {
        if (clas == null) {
            return false;
        }
        if (clas.isPrimitive()) {
            //基本数据类型
            return true;
        }
        PrimitiveWarpper[] dataTypes = PrimitiveWarpper.values();
        for (PrimitiveWarpper dataType : dataTypes) {
            if (dataType.getClazz().isAssignableFrom(clas)) {
                return true;
            }
        }
        return false;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

}
