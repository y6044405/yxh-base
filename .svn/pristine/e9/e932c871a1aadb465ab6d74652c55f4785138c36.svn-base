package com.tzg.tool.kit.test.mapper;

import java.io.FileInputStream;

/**
 * class版本查看  
 *
 */
public class JavaVersionUtil {

    /**
     * 版本号对应：
     *  6.0  : 版本号(version):50.0
     *  5.0  : 版本号(version):49.0  
     *  1.4  : 版本号(version):46.0 
     *  1.3  : 版本号(version):45.3 
     */
    public static void main(String args[]) {
        try {
            String str="D:/Java/apache-tomcat-7.0.32/webapps/ROOT/WEB-INF/classes/com/alibaba/dubbo/registry/common/StatusManager.class";
            // 读取文件数据,文件是当前目录下的First.class 
            FileInputStream fis = new FileInputStream(str);
            int length = fis.available();
            // 文件数据  
            byte[] data = new byte[length];
            // 读取文件到字节数组  
            fis.read(data);
            // 关闭文件  fis.close();
            // 解析文件数据 
            parseFile(data);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void parseFile(byte[] data) {
        // 输出魔数 
//        System.out.print("魔数(magic):0x");
        System.out.print(Integer.toHexString(data[0]).substring(6).toUpperCase());
        System.out.print(Integer.toHexString(data[1]).substring(6).toUpperCase());
        System.out.print(Integer.toHexString(data[2]).substring(6).toUpperCase());
        System.out.println(Integer.toHexString(data[3]).substring(6).toUpperCase());
        // 主版本号和次版本号码  
        int minor_version = (((int) data[4]) << 8) + data[5];
        int major_version = (((int) data[6]) << 8) + data[7];
        System.out.println("版本号(version):" + major_version + "." + minor_version);
    }
}
