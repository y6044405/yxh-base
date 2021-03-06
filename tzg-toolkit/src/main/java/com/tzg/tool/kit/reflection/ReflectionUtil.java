package com.tzg.tool.kit.reflection;

import java.beans.Introspector;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public class ReflectionUtil {
    private final static Logger logger     = LoggerFactory.getLogger(ReflectionUtil.class);
    private ClassUtils          classUtils = new ClassUtils();

    public ReflectionUtil() {
    }

    public static final String ARRAY_SUFFIX        = "[]";
    private static Class       PRIMITIVE_CLASSES[];
    private static final Class EMPTY_CLASS_ARRAY[] = new Class[0];

    static {
        PRIMITIVE_CLASSES = (new Class[] { Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE });
    }

    public static Object newInstance(String clas) {
        try {
            Class<?> clazz = Class.forName(clas);
            return clazz.newInstance();
        } catch (Exception e) {
            logger.error("new class({}) instance  exception:{}", clas, e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * 通过类加载机制返回类对象
     * 
     * @param name
     * @param classLoader
     * @return
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static Class forName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        Class clazz = resolvePrimitiveClassName(name);
        if (clazz != null)
            return clazz;
        if (name.endsWith("[]")) {
            String elementClassName = name.substring(0, name.length() - "[]".length());
            Class elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        } else {
            return Class.forName(name, true, classLoader);
        }
    }

    /*
     * 根据方法名查找方法对象:忽略方法参数（返回第一个匹配的方法）
     */
    public static Method findMethodIgnoreParameter(Class<?> clazz, String name) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }

    /**
     * 解析原始数据类型
     * 
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Class resolvePrimitiveClassName(String name) {
        if (name.length() <= 8) {
            for (int i = 0; i < PRIMITIVE_CLASSES.length; i++) {
                Class clazz = PRIMITIVE_CLASSES[i];
                if (clazz.getName().equals(name))
                    return clazz;
            }
        }
        return null;
    }

    /**
     * 
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getShortNameAsProperty(Class clazz) {
        return Introspector.decapitalize(getShortName(clazz));
    }

    public static String getShortNameForField(Field field) {
        return Introspector.decapitalize(field.getName());
    }

    /**
     * 获取方法的名称
     * 
     * @param method
     * @return
     */
    public static String getShortNameForMethod(Method method) {
        String name = method.getName();
        if (name.startsWith("is"))
            name = name.substring("is".length());
        else if (name.startsWith("get"))
            name = name.substring("get".length());
        else
            throw new IllegalArgumentException((new StringBuilder()).append("Method [").append(method.getName()).append("] is not formed as a JavaBean property").toString());
        return Introspector.decapitalize(name);
    }

    /**
     * 获取一个类的ShortName 如：com.easyway.A 返回 A
     * 
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getShortName(Class clazz) {
        return getShortName(clazz.getName());
    }

    /**
     * 判断一个类是否为内部类并获取一个类的ShortName
     * 
     * @param className
     * @return
     */
    public static String getShortName(String className) {
        int lastDotIndex = className.lastIndexOf('.');
        int nameEndIndex = className.indexOf("$$");
        if (nameEndIndex == -1)
            nameEndIndex = className.length();
        String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
        shortName = shortName.replace('$', '.');
        return shortName;
    }

    /**
     * 获取一个方法所在类的全名
     * 
     * @param method
     *            方法名称
     * @return
     */
    public static String getQualifiedMethodName(Method method) {
        return (new StringBuilder()).append(method.getDeclaringClass().getName()).append(".").append(method.getName()).toString();
    }

    /**
     * 根据类，方法名称和参数查找方法
     * 
     * @param clazz
     *            类名
     * @param methodName
     *            方法名称
     * @param paramTypes
     *            参数类型
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean hasMethod(Class clazz, String methodName, Class paramTypes[]) {
        try {
            clazz.getMethod(methodName, paramTypes);
            return true;
        } catch (NoSuchMethodException ex) {
            return false;
        }
    }

    /**
     * 根据类和方法名返回方法的个数
     * 
     * @param clazz
     * @param methodName
     * @return
     */
    @SuppressWarnings("unchecked")
    public static int getMethodCountForName(Class clazz, String methodName) {
        int count = 0;
        do {
            for (int i = 0; i < clazz.getDeclaredMethods().length; i++) {
                Method method = clazz.getDeclaredMethods()[i];
                if (methodName.equals(method.getName()))
                    count++;
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        return count;
    }

    /**
     * 
     * @param clazz
     * @param methodName
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean hasAtLeastOneMethodWithName(Class clazz, String methodName) {
        do {
            for (int i = 0; i < clazz.getDeclaredMethods().length; i++) {
                Method method = clazz.getDeclaredMethods()[i];
                if (methodName.equals(method.getName()))
                    return true;
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        return false;
    }

    /**
     * 获取静态的方法的
     * 
     * @param clazz
     * @param methodName
     * @param args
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Method getStaticMethod(Class clazz, String methodName, Class args[]) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, args);
            if ((method.getModifiers() & Modifier.STATIC) != 0)
                return method;
        } catch (NoSuchMethodException ex) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static String addResourcePathToPackagePath(Class clazz, String resourceName) {
        if (!resourceName.startsWith("/"))
            return (new StringBuilder()).append(classPackageAsResourcePath(clazz)).append("/").append(resourceName).toString();
        else
            return (new StringBuilder()).append(classPackageAsResourcePath(clazz)).append(resourceName).toString();
    }

    @SuppressWarnings("unchecked")
    public static String classPackageAsResourcePath(Class clazz) {
        if (clazz == null || clazz.getPackage() == null)
            return "";
        else
            return clazz.getPackage().getName().replace('.', '/');
    }

    /**
     * 根据对象获取所有的接口
     * 
     * @param object
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Class[] getAllInterfaces(Object object) {
        Set interfaces = getAllInterfacesAsSet(object);
        return (Class[]) (Class[]) interfaces.toArray(new Class[interfaces.size()]);
    }

    /**
     * 根据类获取所有的接口
     * 
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Class[] getAllInterfacesForClass(Class clazz) {
        Set interfaces = getAllInterfacesForClassAsSet(clazz);
        return (Class[]) (Class[]) interfaces.toArray(new Class[interfaces.size()]);
    }

    /**
     * 根据对象获取所有的接口
     * 
     * @param object
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Set getAllInterfacesAsSet(Object object) {
        return getAllInterfacesForClassAsSet(object.getClass());
    }

    /**
     * 根据类获取所有的接口
     * 
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Set getAllInterfacesForClassAsSet(Class clazz) {
        Set interfaces = new HashSet();
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            for (int i = 0; i < clazz.getInterfaces().length; i++) {
                Class ifc = clazz.getInterfaces()[i];
                interfaces.add(ifc);
            }
        }
        return interfaces;
    }

    /**
     * 检测一个方法或者一个属性是否为Public 修饰
     * 
     * @param object
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isPublic(Class clazz, Member member) {
        return Modifier.isPublic(member.getModifiers()) && Modifier.isPublic(clazz.getModifiers());
    }

    /**
     * 检测一个Class是否为Abstract 修饰
     * 
     * @param object
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isAbstractClass(Class clazz) {
        int modifier = clazz.getModifiers();
        return Modifier.isAbstract(modifier) || Modifier.isInterface(modifier);
    }

    /**
     * 根据一个类获取一个默认的无参数的构造函数
     * 
     * @param object
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Constructor getDefaultConstructor(Class clazz) {
        if (isAbstractClass(clazz))
            return null;
        try {
            Constructor constructor = clazz.getDeclaredConstructor(EMPTY_CLASS_ARRAY);
            if (!isPublic(clazz, constructor))
                constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException nme) {
            return null;
        }
    }

    /**
     * 根据一个类和对应输入参数，获取一个对应参数的构造函数
     * 
     * @param object
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Constructor getConstructor(Class clazz, Class parameterTypes[]) {
        if (isAbstractClass(clazz))
            return null;
        try {
            Constructor constructor = clazz.getConstructor(parameterTypes);
            if (!isPublic(clazz, constructor))
                constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException nme) {
            return null;
        }
    }

    /**
     * 将一个完整的类名装换为资源名称路径
     * 
     * @param resourcePath
     * @return
     */
    public static String convertResourcePathToClassName(String resourcePath) {
        return resourcePath.replace('/', '.');
    }

    public static String convertClassNameToResourcePath(String className) {
        return className.replace('.', '/');
    }

    /**
     * 获取一个对象的属性
     * 
     * @param <T>
     * @param object
     * @param propertyName
     * @return
     * @throws NoSuchFieldException
     */
    @SuppressWarnings("unchecked")
    public static <T> T getDeclaredFieldValue(Object object, String propertyName) throws NoSuchFieldException {
        Field field = getDeclaredField(object.getClass(), propertyName);
        boolean accessible = field.isAccessible();
        Object result = null;
        synchronized (field) {
            field.setAccessible(true);
            try {
                result = field.get(object);
            } catch (IllegalAccessException e) {
                throw new NoSuchFieldException("No such field: " + object.getClass() + '.' + propertyName);
            } finally {
                field.setAccessible(accessible);
            }
        }
        return (T) result;
    }

    /**
     * 查找对应类的属性字段
     * 
     * @param clazz
     * @param propertyName
     * @return
     * @throws NoSuchFieldException
     */
    public static Field getDeclaredField(Class<?> clazz, String propertyName) throws NoSuchFieldException {
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {

            try {
                return superClass.getDeclaredField(propertyName);
            } catch (NoSuchFieldException e) {
                // Field不在当前类定义,继续向上转型

            }
        }
        throw new NoSuchFieldException("No such field: " + clazz.getName() + '.' + propertyName);
    }

    /**
     * 获取一个类的所有的属性
     * 
     * @param clazz
     * @return
     */
    public static Field[] getDeclaredFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Class<?> superClass = clazz; superClass.equals(Object.class); superClass = superClass.getSuperclass()) {
            fields = (Field[]) ArrayUtils.addAll(fields, superClass.getDeclaredFields());
        }
        return fields;
    }

    /**
     * 获取指定对象的所有成员属性的值
     * @author:  heyiwu 
     * @param e 对象
     * @param fields 可变参数(0个或多个参数),成员属性的Field
     * @return 对象数组:所有成员属性的值
     * @throws NoSuchFieldException
     */
    public static Object[] getFieldsValue(Object e, Field... fields) throws NoSuchFieldException {
        if (null == fields || fields.length == 0) {
            fields = ReflectionUtil.getDeclaredFields(e.getClass());
        }
        int len = fields.length;
        Object[] values = new Object[len];
        for (int i = 0; i < len; i++) {
            values[i] = ReflectionUtil.getDeclaredFieldValue(e, fields[i].getName());
        }
        return values;
    }

    /**
     * 获取Method的形参名称列表
     * 
     * @param method
     *            需要解析的方法
     * @return 形参名称列表, 如果没有调试信息, 将返回null
     */
    public static List<String> getParamNames(Method method) {
        try {
            int size = method.getParameterTypes().length;
            if (size == 0)
                return new ArrayList<String>(0);
            List<String> list = getParamNames(method.getDeclaringClass()).get(getKey(method));
            if (list != null && list.size() != size)
                return list.subList(0, size);
            return list;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取Constructor的形参名称列表
     * 
     * @param constructor
     *            需要解析的构造函数
     * @return 形参名称列表, 如果没有调试信息, 将返回null
     */
    public static List<String> getParamNames(Constructor<?> constructor) {
        try {
            int size = constructor.getParameterTypes().length;
            if (size == 0)
                return new ArrayList<String>(0);
            List<String> list = getParamNames(constructor.getDeclaringClass()).get(getKey(constructor));
            if (list != null && list.size() != size)
                return list.subList(0, size);
            return list;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取一个类的所有方法/构造方法的形参名称Map
     * 
     * @param klass
     *            需要解析的类
     * @return 所有方法/构造方法的形参名称Map
     * @throws IOException
     *             如果有任何IO异常,不应该有,如果是本地文件,那100%遇到bug了
     */
    public static Map<String, List<String>> getParamNames(Class<?> klass) throws IOException {
        InputStream in = klass.getResourceAsStream("/" + klass.getName().replace('.', '/') + ".class");
        return getParamNames(in);
    }

    public static Map<String, List<String>> getParamNames(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(in));
        Map<String, List<String>> names = new HashMap<String, List<String>>();
        Map<Integer, String> strs = new HashMap<Integer, String>();
        dis.skipBytes(4);// Magic
        dis.skipBytes(2);// 副版本号
        dis.skipBytes(2);// 主版本号

        // 读取常量池
        int constant_pool_count = dis.readUnsignedShort();
        for (int i = 0; i < (constant_pool_count - 1); i++) {
            byte flag = dis.readByte();
            switch (flag) {
                case 7:// CONSTANT_Class:
                    dis.skipBytes(2);
                    break;
                case 9:// CONSTANT_Fieldref:
                case 10:// CONSTANT_Methodref:
                case 11:// CONSTANT_InterfaceMethodref:
                    dis.skipBytes(2);
                    dis.skipBytes(2);
                    break;
                case 8:// CONSTANT_String:
                    dis.skipBytes(2);
                    break;
                case 3:// CONSTANT_Integer:
                case 4:// CONSTANT_Float:
                    dis.skipBytes(4);
                    break;
                case 5:// CONSTANT_Long:
                case 6:// CONSTANT_Double:
                    dis.skipBytes(8);
                    i++;// 必须跳过一个,这是class文件设计的一个缺陷,历史遗留问题
                    break;
                case 12:// CONSTANT_NameAndType:
                    dis.skipBytes(2);
                    dis.skipBytes(2);
                    break;
                case 1:// CONSTANT_Utf8:
                    int len = dis.readUnsignedShort();
                    byte[] data = new byte[len];
                    dis.read(data);
                    strs.put(i + 1, new String(data, "UTF-8"));// 必然是UTF8的
                    break;
                case 15:// CONSTANT_MethodHandle:
                    dis.skipBytes(1);
                    dis.skipBytes(2);
                    break;
                case 16:// CONSTANT_MethodType:
                    dis.skipBytes(2);
                    break;
                case 18:// CONSTANT_InvokeDynamic:
                    dis.skipBytes(2);
                    dis.skipBytes(2);
                    break;
                default:
                    throw new RuntimeException("Impossible!! flag=" + flag);
            }
        }

        dis.skipBytes(2);// 版本控制符
        dis.skipBytes(2);// 类名
        dis.skipBytes(2);// 超类

        // 跳过接口定义
        int interfaces_count = dis.readUnsignedShort();
        dis.skipBytes(2 * interfaces_count);// 每个接口数据,是2个字节

        // 跳过字段定义
        int fields_count = dis.readUnsignedShort();
        for (int i = 0; i < fields_count; i++) {
            dis.skipBytes(2);
            dis.skipBytes(2);
            dis.skipBytes(2);
            int attributes_count = dis.readUnsignedShort();
            for (int j = 0; j < attributes_count; j++) {
                dis.skipBytes(2);// 跳过访问控制符
                int attribute_length = dis.readInt();
                dis.skipBytes(attribute_length);
            }
        }

        // 开始读取方法
        int methods_count = dis.readUnsignedShort();
        for (int i = 0; i < methods_count; i++) {
            dis.skipBytes(2); // 跳过访问控制符
            String methodName = strs.get(dis.readUnsignedShort());
            String descriptor = strs.get(dis.readUnsignedShort());
            short attributes_count = dis.readShort();
            for (int j = 0; j < attributes_count; j++) {
                String attrName = strs.get(dis.readUnsignedShort());
                int attribute_length = dis.readInt();
                if ("Code".equals(attrName)) { // 形参只在Code属性中
                    dis.skipBytes(2);
                    dis.skipBytes(2);
                    int code_len = dis.readInt();
                    dis.skipBytes(code_len); // 跳过具体代码
                    int exception_table_length = dis.readUnsignedShort();
                    dis.skipBytes(8 * exception_table_length); // 跳过异常表

                    int code_attributes_count = dis.readUnsignedShort();
                    for (int k = 0; k < code_attributes_count; k++) {
                        int str_index = dis.readUnsignedShort();
                        String codeAttrName = strs.get(str_index);
                        int code_attribute_length = dis.readInt();
                        if ("LocalVariableTable".equals(codeAttrName)) {// 形参在LocalVariableTable属性中
                            int local_variable_table_length = dis.readUnsignedShort();
                            List<String> varNames = new ArrayList<String>(local_variable_table_length);
                            for (int l = 0; l < local_variable_table_length; l++) {
                                dis.skipBytes(2);
                                dis.skipBytes(2);
                                String varName = strs.get(dis.readUnsignedShort());
                                dis.skipBytes(2);
                                dis.skipBytes(2);
                                if (!"this".equals(varName)) // 非静态方法,第一个参数是this
                                    varNames.add(varName);
                            }
                            names.put(methodName + "," + descriptor, varNames);
                        } else
                            dis.skipBytes(code_attribute_length);
                    }
                } else
                    dis.skipBytes(attribute_length);
            }
        }
        dis.close();
        return names;
    }

    /**
     * 传入Method或Constructor,获取getParamNames方法返回的Map所对应的key
     */
    public static String getKey(Object obj) {
        StringBuilder sb = new StringBuilder();
        if (obj instanceof Method) {
            sb.append(((Method) obj).getName()).append(',');
            getDescriptor(sb, (Method) obj);
        } else if (obj instanceof Constructor) {
            sb.append("<init>,"); // 只有非静态构造方法才能用有方法参数的,而且通过反射API拿不到静态构造方法
            getDescriptor(sb, (Constructor<?>) obj);
        } else
            throw new RuntimeException("Not Method or Constructor!");
        return sb.toString();
    }

    public static void getDescriptor(StringBuilder sb, Method method) {
        sb.append('(');
        for (Class<?> klass : method.getParameterTypes())
            getDescriptor(sb, klass);
        sb.append(')');
        getDescriptor(sb, method.getReturnType());
    }

    public static void getDescriptor(StringBuilder sb, Constructor<?> constructor) {
        sb.append('(');
        for (Class<?> klass : constructor.getParameterTypes())
            getDescriptor(sb, klass);
        sb.append(')');
        sb.append('V');
    }

    /**
     * 本方法来源于ow2的asm库的Type类
     */
    public static void getDescriptor(final StringBuilder buf, final Class<?> c) {
        Class<?> d = c;
        while (true) {
            if (d.isPrimitive()) {
                char car;
                if (d == Integer.TYPE) {
                    car = 'I';
                } else if (d == Void.TYPE) {
                    car = 'V';
                } else if (d == Boolean.TYPE) {
                    car = 'Z';
                } else if (d == Byte.TYPE) {
                    car = 'B';
                } else if (d == Character.TYPE) {
                    car = 'C';
                } else if (d == Short.TYPE) {
                    car = 'S';
                } else if (d == Double.TYPE) {
                    car = 'D';
                } else if (d == Float.TYPE) {
                    car = 'F';
                } else /* if (d == Long.TYPE) */ {
                    car = 'J';
                }
                buf.append(car);
                return;
            } else if (d.isArray()) {
                buf.append('[');
                d = d.getComponentType();
            } else {
                buf.append('L');
                String name = d.getName();
                int len = name.length();
                for (int i = 0; i < len; ++i) {
                    char car = name.charAt(i);
                    buf.append(car == '.' ? '/' : car);
                }
                buf.append(';');
                return;
            }
        }
    }

    /**
     * 查找包下的class
     * 
     * @param packageNames
     * @return
     */
    public static List<String> findClasses(String... packageNames) {
        List<String> list = new ArrayList<String>();
        for (String packageName : packageNames) {
            try {
                String[] array = findClasses(packageName);
                list.addAll(Arrays.asList(array));
            } catch (IOException e) {

            }
        }
        return list;
    }

    /**
     * 查找包下的class
     * 
     * @param packageName
     *            包名
     * @return
     * @throws IOException
     */
    public static String[] findClasses(String packageName) throws IOException {
        return findClasses(packageName, new ArrayList<String>(), new ArrayList<String>());
    }

    /**
     * 查找包下的class
     * 
     * @param packageName
     * @param included
     * @param excluded
     * @return
     * @throws IOException
     */
    public static String[] findClasses(String packageName, List<String> included, List<String> excluded) throws IOException {
        if (StringUtils.isBlank(packageName)) {
            return null;
        }
        String packageOnly = packageName;
        boolean recursive = false;
        if (packageName.endsWith(".*")) {
            packageOnly = packageName.substring(0, packageName.lastIndexOf(".*"));
            recursive = true;
        }

        List<String> vResult = new ArrayList<String>();
        String packageDirName = packageOnly.replace('.', '/');
        Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
        logger.debug(" getResources: {}", dirs);
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();
            logger.debug(" url:{} ", url);

            if ("file".equals(protocol)) {
                findClasses(packageOnly, included, excluded, URLDecoder.decode(url.getFile(), "UTF-8"), recursive, vResult);
            } else if ("jar".equals(protocol)) {
                JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.charAt(0) == '/') {
                        name = name.substring(1);
                    }
                    if (!name.startsWith(packageDirName)) {
                        continue;
                    }

                    int i = name.lastIndexOf('/');
                    if (i != -1) {
                        packageName = name.substring(0, i).replace('/', '.');
                    }
                    logger.debug(" Package name is :{}", packageName);
                    if ((i == -1) || !recursive) {
                        continue;
                    }
                    if (!name.endsWith(".class") || entry.isDirectory()) {
                        continue;
                    }

                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                    logger.debug(" Found class {}, seeing it if it's included or excluded", className);
                    includeOrExcludeClass(packageName, className, included, excluded, vResult);

                }
            }
        }
        return vResult.toArray(new String[vResult.size()]);
    }

    private static void findClasses(String packageName, List<String> included, List<String> excluded, String packagePath, final boolean recursive, List<String> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        if (null == dirfiles) {
            return;
        }
        logger.debug(" Looking for test classes in the directory: {}", dir);
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findClasses(packageName + "." + file.getName(), included, excluded, file.getAbsolutePath(), recursive, classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);

                logger.debug(" Found class {}, seeing it if it's included or excluded", className);
                includeOrExcludeClass(packageName, className, included, excluded, classes);
            }
        }
    }

    private static void includeOrExcludeClass(String packageName, String className, List<String> included, List<String> excluded, List<String> classes) {
        if (!isIncluded(className, included, excluded)) {
            logger.debug("Excluding class:{} ", className);
            return;
        }
        logger.debug("Including class:{}", className);
        classes.add(packageName + '.' + className);
    }

    private static boolean isIncluded(String name, List<String> included, List<String> excluded) {
        boolean result = false;
        boolean isIncluded = CollectionUtils.isEmpty(included) ? true : find(name, included);
        boolean isExcluded = CollectionUtils.isEmpty(excluded) ? false : find(name, excluded);
        if (isIncluded && !isExcluded) {
            result = true;
        } else if (isExcluded) {
            result = false;
        } else {
            result = included.size() == 0;
        }
        return result;
    }

    private static boolean find(String name, List<String> list) {
        for (String regexpStr : list) {
            if (Pattern.matches(regexpStr, name))
                return true;
        }
        return false;
    }

    /**
     * 获取成员属性的泛型
     * @author:  heyiwu 
     * @param f 成员属性
     * @return
     */
    public static Class<?>[] getActGenericType(Field f) {
        Type type = f.getGenericType();
        if (null == type || !(type instanceof ParameterizedType)) {
            return EMPTY_CLASS_ARRAY;
        }
        ParameterizedType pt = (ParameterizedType) type;
        Type[] args = pt.getActualTypeArguments();
        if (null == args) {
            return EMPTY_CLASS_ARRAY;
        }
        if (args.length == 1) {
            return new Class<?>[] { (Class<?>) args[0] };
        }
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = (Class<?>) args[i];
        }
        return types;
    }

    public static Class<?>[] getActGenericType(Method method) {
        Type[] types = method.getGenericParameterTypes();
        LinkedList<Class<?>> list = new LinkedList<>();
        for (Type type : types) {
            if (!(type instanceof ParameterizedType)) {
                continue;
            }
            ParameterizedType pType = (ParameterizedType) type;
            Type[] args = pType.getActualTypeArguments();
            if (null == args) {
                continue;
            }
            if (args.length == 1) {
                list.add((Class<?>) args[0]);
            }
            for (int i = 0; i < types.length; i++) {
                list.add((Class<?>) args[i]);
            }
        }
        return list.toArray(new Class[] {});
    }

    public static <T> Class<T> getSuperClassGenricType(final Class clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    @SuppressWarnings("rawtypes")
    public static Class getSuperClassGenricType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }
        return (Class) params[index];
    }
}
