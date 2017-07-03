package com.tzg.tool.kit.test.bean;

import java.lang.reflect.Field;
import java.util.Date;

import com.tzg.tool.kit.bean.BeanUtils;

public class BeanUtilsTest {
    public static void main(String[] args) {
        JavaBean4Test dest = new JavaBean4Test();
        JavaBean4Test dest2 = new JavaBean4Test();
        System.out.println(dest2.getDate());
        BeanUtils.copyProperties(dest, dest2);
        System.out.println(dest.getDate());
        
        dest2.setDate(new Date());
        System.out.println(dest2.getDate());
        BeanUtils.copyProperties(dest, dest2);
        System.out.println(dest.getDate());
        /*
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("1", new Boolean("1"));
        map.put("2", new Byte((byte)8));
        map.put("3", new Short((short) 16));
        map.put("4", new Character((char) 16));
        map.put("5", (Integer)32);
        map.put("6", 32F);
        map.put("7", 64L);
        map.put("8", 64D);
        map.put("9", new Date());
        Map<String,Object> newMap=new HashMap<String, Object>();
        JavaBean4Test dest=new JavaBean4Test();
        JavaBean4Test dest2=new JavaBean4Test();
        BeanUtils.copyProperties(dest, dest2);
        Field[] fields = JavaBean4Test.class.getDeclaredFields();
        for (String index: map.keySet()) {
            Object value = map.get(index);
            BeanUtils.setPropertyValue(map, newMap, index);
            Field field = getField(fields, value.getClass());
            if(null==field){
                continue;
            }
            BeanUtils.setProperty(dest, field.getName(), value);
            BeanUtil.setValue(dest2, field.getName(), value);
        }
        System.out.println(newMap);
        System.out.println(dest);
        System.out.println(dest2);
        */}

    private static Field getField(Field[] fields, Class clas) {
        for (Field field : fields) {
            if (field.getType().equals(clas)) {
                return field;
            }
        }
        return null;
    }

}
