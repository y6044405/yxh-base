package com.tzg.tool.kit.bean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 扩展apache BeanUtils
 * getProperty、setProperty一般为apache BeanUtils的方法，只是将check exception改为uncheck exception，这些方法支持对象的属性为字符串类型，
 * getPropertyValue、setPropertyValue为扩展的方法,扩展支持：
 *      支持对象的属性或方法
 *      属性或方法的类型支持boolean类型(按照javaBean标准命名规范boolean类型的方法为is开头)
 *      源对象和目标对象支持Map对象  
 */
public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {
    private static final Logger          LOG           = LoggerFactory.getLogger(BeanUtils.class);
    static Hashtable<Class<?>, Method[]> objectMethods = new Hashtable<Class<?>, Method[]>();

    private static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            Throwable e = ((InvocationTargetException) ex).getTargetException();
            if (e instanceof Error) {
                throw (Error) e;
            }
            throw new UndeclaredThrowableException(ex);
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    /**
     * 克隆对象
     * @param bean
     * @return
     */
    public static Object cloneBean(Object bean) {
        try {
            return org.apache.commons.beanutils.BeanUtils.cloneBean(bean);
        } catch (Exception e) {
            handleReflectionException(e);
            return null;
        }
    }

    /**
     * 复制对象（新建）
     * @param <T>             
     * @param destClass     需要新建的对象class
     * @param orig          源对象
     * @return
     */
    public static <T> T copyProperties(Class<T> destClass, Object orig) {
        try {
            Object target = destClass.newInstance();
            copyProperties((Object) target, orig);
            return (T) target;
        } catch (Exception e) {
            handleReflectionException(e);
            return null;
        }
    }

    /**
     * 属性复制
     * @param dest      -- 目标对象
     * @param orig      -- 源对象
     */
    public static void copyProperties(Object dest, Object orig) {
        try {
            ConvertUtils.register(new DateConverter(null), java.util.Date.class);
            org.apache.commons.beanutils.BeanUtils.copyProperties(dest, orig);
        } catch (Exception e) {
            handleReflectionException(e);
        }
    }

    /**
     * 
     * @param bean
     * @param name
     * @param value
     */
    public static void copyProperty(Object bean, String name, Object value) {
        try {
            org.apache.commons.beanutils.BeanUtils.copyProperty(bean, name, value);
        } catch (Exception e) {
            handleReflectionException(e);
        }
    }

    /**
     * 将对象转成MAP
     * @param bean
     * @return
     */
    public static Map describe(Object bean) {
        try {
            return org.apache.commons.beanutils.BeanUtils.describe(bean);
        } catch (Exception e) {
            handleReflectionException(e);
            return null;
        }
    }

    public static String[] getArrayProperty(Object bean, String name) {
        try {
            return org.apache.commons.beanutils.BeanUtils.getArrayProperty(bean, name);
        } catch (Exception e) {
            handleReflectionException(e);
            return null;
        }
    }

    public static String getIndexedProperty(Object bean, String name, int index) {
        try {
            return org.apache.commons.beanutils.BeanUtils.getIndexedProperty(bean, name, index);
        } catch (Exception e) {
            handleReflectionException(e);
            return null;
        }
    }

    public static String getIndexedProperty(Object bean, String name) {
        try {
            return org.apache.commons.beanutils.BeanUtils.getIndexedProperty(bean, name);
        } catch (Exception e) {
            handleReflectionException(e);
            return null;
        }
    }

    public static String getMappedProperty(Object bean, String name, String key) {
        try {
            return org.apache.commons.beanutils.BeanUtils.getMappedProperty(bean, name, key);
        } catch (Exception e) {
            handleReflectionException(e);
            return null;
        }
    }

    public static String getMappedProperty(Object bean, final String name) {
        try {
            return org.apache.commons.beanutils.BeanUtils.getMappedProperty(bean, name);
        } catch (Exception e) {
            handleReflectionException(e);
            return null;
        }
    }

    public static String getNestedProperty(Object bean, final String name) {
        try {
            return org.apache.commons.beanutils.BeanUtils.getNestedProperty(bean, name);
        } catch (Exception e) {
            handleReflectionException(e);
            return null;
        }
    }

    public static String getProperty(Object bean, final String name) {
        try {
            return org.apache.commons.beanutils.BeanUtils.getProperty(bean, name);
        } catch (Exception e) {
            handleReflectionException(e);
            return null;
        }
    }

    public static String getSimpleProperty(Object bean, final String name) {
        try {
            return org.apache.commons.beanutils.BeanUtils.getSimpleProperty(bean, name);
        } catch (Exception e) {
            handleReflectionException(e);
            return null;
        }
    }

    public static void populate(Object bean, Map properties) {
        try {
            org.apache.commons.beanutils.BeanUtils.populate(bean, properties);
        } catch (Exception e) {
            handleReflectionException(e);
        }
    }

    public static void setProperty(Object bean, final String name, Object value) {
        try {
            org.apache.commons.beanutils.BeanUtils.setProperty(bean, name, value);
        } catch (Exception e) {
            setPropertyValue(bean, name, value, true);
        }
    }

    /**
     * 设置目标对象指定属性的值
     * @param src 源对象
     * @param dest 目标对象
     * @param name 属性名称
     */
    public static void setPropertyValue(Object src, Object dest, final String name) {
        setProperty(dest, name, getPropertyValue(src, name));
    }

    /**
     * 设置目标对象指定属性的值
     * @param src 源对象
     * @param dest 目标对象
     * @param srcName 源对象的属性名称
     * @param destName 目标对象的属性名称
     */
    public static void setPropertyValue(Object src, Object dest, final String srcName, String destName) {
        setProperty(dest, destName, getPropertyValue(src, srcName));
    }

    public static boolean setPropertyValue(Object o, String name, Object value, boolean invokeSetProperty) {
        if (Map.class.isAssignableFrom(o.getClass())) {
            ((Map) o).put(name, value);
            return true;
        }
        if (LOG.isDebugEnabled())
            LOG.debug("IntrospectionUtils: setProperty(" + o.getClass() + " " + name + "=" + value + ")");

        String setter = "set" + capitalize(name);

        try {
            Method methods[] = findMethods(o.getClass());
            Method setPropertyMethodVoid = null;
            Method setPropertyMethodBool = null;

            // First, the ideal case - a setFoo( String ) method
            for (int i = 0; i < methods.length; i++) {
                Class<?> paramT[] = methods[i].getParameterTypes();
                if (paramT.length == 0) {
                    continue;
                }
                if (setter.equals(methods[i].getName()) && paramT.length == 1 && value.getClass().equals(paramT[0])) {
                    methods[i].invoke(o, new Object[] { value });
                    return true;
                }
            }
            // Ok, no setXXX found, try a setProperty("name", "value")
            if (invokeSetProperty && (setPropertyMethodBool != null || setPropertyMethodVoid != null)) {
                Object params[] = new Object[2];
                params[0] = name;
                params[1] = value;
                if (setPropertyMethodBool != null) {
                    try {
                        return ((Boolean) setPropertyMethodBool.invoke(o, params)).booleanValue();
                    } catch (IllegalArgumentException biae) {
                        //the boolean method had the wrong
                        //parameter types. lets try the other
                        if (setPropertyMethodVoid != null) {
                            setPropertyMethodVoid.invoke(o, params);
                            return true;
                        } else {
                            throw biae;
                        }
                    }
                } else {
                    setPropertyMethodVoid.invoke(o, params);
                    return true;
                }
            }

        } catch (IllegalArgumentException ex2) {
            LOG.warn("IAE " + o + " " + name + " " + value, ex2);
        } catch (SecurityException ex1) {
            if (LOG.isDebugEnabled())
                LOG.debug("IntrospectionUtils: SecurityException for " + o.getClass() + " " + name + "=" + value + ")", ex1);
        } catch (IllegalAccessException iae) {
            if (LOG.isDebugEnabled())
                LOG.debug("IntrospectionUtils: IllegalAccessException for " + o.getClass() + " " + name + "=" + value + ")", iae);
        } catch (InvocationTargetException ie) {
            if (LOG.isDebugEnabled())
                LOG.debug("IntrospectionUtils: InvocationTargetException for " + o.getClass() + " " + name + "=" + value + ")", ie);
        }
        return false;
    }

    public static Object invokeMethod(Object target, final String method, Object[] args) {
        try {
            return MethodUtils.invokeMethod(target, method, args);
        } catch (Exception e) {
            handleReflectionException(e);
        }
        return null;
    }

    public static byte[] serialize(Object obj) throws IOException {
        byte[] byteArray = null;
        ByteArrayOutputStream baos = null;
        ObjectOutputStream out = null;
        try {
            // These objects are closed in the finally.
            baos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(baos);
            out.writeObject(obj);
            byteArray = baos.toByteArray();
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return byteArray;
    }

    public static Object getPropertyValue(Object o, String name) {
        if (Map.class.isAssignableFrom(o.getClass())) {
            return ((Map) o).get(name);
        }
        String getter = "get" + capitalize(name);
        String isGetter = "is" + capitalize(name);
        try {
            Method methods[] = findMethods(o.getClass());
            Method getPropertyMethod = null;
            for (int i = 0; i < methods.length; i++) {
                Class<?> paramT[] = methods[i].getParameterTypes();
                if ((getter.equals(methods[i].getName()) ||("get"+name).equals(methods[i].getName()))&& paramT.length == 0) {
                    return methods[i].invoke(o, (Object[]) null);
                }
                if (isGetter.equals(methods[i].getName()) && paramT.length == 0) {
                    return methods[i].invoke(o, (Object[]) null);
                }
                if ("getProperty".equals(methods[i].getName())) {
                    getPropertyMethod = methods[i];
                }
            }
            if (getPropertyMethod != null) {
                Object params[] = new Object[1];
                params[0] = name;
                return getPropertyMethod.invoke(o, params);
            }

        } catch (IllegalArgumentException ex2) {
            LOG.warn("IAE " + o + " " + name, ex2);
        } catch (SecurityException ex1) {
            if (LOG.isDebugEnabled())
                LOG.debug("IntrospectionUtils: SecurityException for " + o.getClass() + " " + name + ")", ex1);
        } catch (IllegalAccessException iae) {
            if (LOG.isDebugEnabled())
                LOG.debug("IntrospectionUtils: IllegalAccessException for " + o.getClass() + " " + name + ")", iae);
        } catch (InvocationTargetException ie) {

            if (LOG.isDebugEnabled())
                LOG.debug("IntrospectionUtils: InvocationTargetException for " + o.getClass() + " " + name + ")");
        }
        return null;
    }

    public static Method findMethod(Class<?> c, String name, Class<?> params[]) {
        Method methods[] = findMethods(c);
        if (methods == null)
            return null;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(name)) {
                Class<?> methodParams[] = methods[i].getParameterTypes();
                if (methodParams == null)
                    if (params == null || params.length == 0)
                        return methods[i];
                if (params == null)
                    if (methodParams == null || methodParams.length == 0)
                        return methods[i];
                if (ArrayUtils.getLength(params) != methodParams.length)
                    continue;
                boolean found = true;
                for (int j = 0; j < params.length; j++) {
                    if (params[j] != methodParams[j]) {
                        found = false;
                        break;
                    }
                }
                if (found)
                    return methods[i];
            }
        }
        return null;
    }

    public static Method[] findMethods(Class<?> c) {
        Method methods[] = objectMethods.get(c);
        if (methods != null)
            return methods;

        methods = c.getMethods();
        objectMethods.put(c, methods);
        return methods;
    }

    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }
}
