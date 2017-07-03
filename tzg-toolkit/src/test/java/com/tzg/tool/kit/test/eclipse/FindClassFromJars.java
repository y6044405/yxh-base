package com.tzg.tool.kit.test.eclipse;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindClassFromJars {
    private static final Logger logger = LoggerFactory.getLogger(FindClassFromJars.class);

    public static void main(String[] args) {
        /*args = new String[] { "org.jboss.logging.appender.DailyRollingFileAppender", "org.apache.log4j.Appender" };
        findClass("org.codehaus.jackson.map.ser.BeanPropertyWriter");*/

        args = new String[] { "org.springframework.context.ApplicationContext"  };
        findJars(args, 3, new String[] { "e:/lib"});

    }

    /**
     * 查询格式:[查询参数] [关键字..]<br/>
     * 查询参数:--help:显示"软件帮助信息";<br/>
     * 	查询级别{0,1}(默认为3):
     * 	<li>0:完全匹配;</li>
     * 	<li>1:忽略大小写的完全匹配;</li>
     * 	<li>2:模糊匹配;</li>
     * 	<li>3:忽略大小写的模糊匹配.</li>
     * @param keys
     */
    public static void findJars(String[] keys) {//未考虑将参数写在最后
        int length = keys.length;
        int level = 3;
        File file = new File(".");
        if (length == 0)
            showHelp();
        if (length == 1)
            if ("--help".equalsIgnoreCase(keys[0]) || "/?".equalsIgnoreCase(keys[0]))
                showHelp();
            else
                findJars(keys, 3, file);
        if (length > 1)
            try {
                level = Integer.parseInt(keys[0]);//转换搜索级别
                findJars(keys, level, file);
            } catch (NumberFormatException e) {//转换异常，没有输入搜索级别时
                if (!"--help".equalsIgnoreCase(keys[0]) && !"/?".equalsIgnoreCase(keys[0]))
                    findJars(keys, level, file);
                else
                    showHelp();
            }
    }

    private static void showHelp() {
        System.out.println("查询格式:[查询参数] [关键字..]\n查询参数:--help:显示\"软件帮助信息\";\n查询级别{0,1,2,3}(默认为3):\n0:完全匹配;\n1:忽略大小写的完全匹配;\n2:模糊匹配;\n3:忽略大小写的模糊匹配.");
    }

    private static void findJars(String[] keys, int level, String[] dirs) {
        for (String file : dirs) {
            findJars(keys, level, new File(file));
        }
    }

    private static void findJars(String[] keys, int level, File directory) {
        File[] files = directory.listFiles();
        if (null == files) {
            return;
        }
        for (File file : files) {
            String pathName = file.getName();
            if (file.isDirectory()){
                findJars(keys, level, file);
            }
            if (!pathName.toLowerCase().endsWith(".jar")) {
                continue;
            }
            try {
                String path = file.getPath();
                Enumeration<JarEntry> enu = new JarFile(path).entries();
                while (enu.hasMoreElements()) {
                    String fullName = enu.nextElement().getName();
                    String[] packages = fullName.split("/");
                    String[] fileName = packages[packages.length - 1].split("\\.");// 类完整名
                    String packageName = fullName.replace("/", ".");
                    for (int i = 0; i < keys.length; i++) {// 多参时分为全字匹配与部分匹配
                        switch (level) {
                            case 0:
                                if (fileName[0].equals(keys[i]))// 完全匹配
                                    print(path, packageName);
                                break;
                            case 1:
                                if (fileName[0].equalsIgnoreCase(keys[i]))// 忽略大小写的完全匹配
                                    print(path, packageName);
                                break;
                            case 2:
                                if (packageName.contains(keys[i]))// 模糊匹配
                                    print(path, packageName);
                                break;
                            case 3:
                                if (packageName.toLowerCase().contains(keys[i].toLowerCase()))// 忽略大小写的模糊匹配
                                    print(path, packageName);
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("error!");
                logger.error(e.getLocalizedMessage(), e);
            }
        }
    }

    private static void print(String path, String packageName) {
        System.out.println("找到文件" + packageName + ",\n\t" + path + "\n");
    }

    /** 
     * 根据当前的classpath设置， 
     * 显示出包含指定类的类文件所在 
     * 位置的绝对路径 
     * 
     * @param className <类的名字> 
     */
    public static void findClass(String className) {
        if (!className.startsWith("/")) {
            className = "/" + className;
        }
        className = className.replace('.', '/');
        className = className + ".class";

        java.net.URL classUrl = new FindClassFromJars().getClass().getResource(className);

        if (classUrl != null) {
            System.out.println("\nClass " + className + " found in \n" + classUrl.getFile() + "");
        } else {
            String str = System.getProperty("java.class.path");
            System.out.println("\nClass " + className + "' not found in \n");
            if (!StringUtils.isEmpty(str) && str.indexOf(";") != -1) {
                String[] strs = str.split(";");
                for (String s : strs) {
                    System.out.println(s);
                }
            } else {
                System.out.println(str);
            }
        }
    }
}
