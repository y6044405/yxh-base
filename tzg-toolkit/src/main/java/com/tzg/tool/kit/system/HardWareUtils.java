package com.tzg.tool.kit.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.Map;
import java.util.Scanner;

import com.tzg.tool.kit.string.StringUtil;

/**
 * 
 * Filename:    HardWareUtils.java  
 * Description: 获取硬件信息： cpuid、主板id、硬盘id、mac地址  
 * Linux命令：
    0、查看CPUID：dmidecode -t processor | grep 'ID'
    1、查看服务器型号：dmidecode | grep 'Product Name'
    2、查看主板的序列号：dmidecode |grep 'Serial Number'
    3、查看系统序列号：dmidecode -s system-serial-number
    4、查看内存信息：dmidecode -t memory
    5、查看OEM信息：dmidecode -t 11
 * Copyright:   Copyright (c) 2012-2013 All Rights Reserved.
 * Company:     tzg-soft.com Inc.
 * @author:     heyiwu 
 * @version:    1.0  
 * Create at:   2014-2-21 下午05:05:22  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2014-2-21      heyiwu      1.0         1.0 Version  
 *
 */
public class HardWareUtils {
    public static void main(String[] args) {
        System.out.println(System.getProperty("os.name"));
    }

    /**
     * 获取主板序列号
     *
     * @return
     */
    public static String getMotherboardSN() {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String content = null;
            if (isWindows()) {
                content = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n" + "Set colItems = objWMIService.ExecQuery _ \n"
                          + "   (\"Select * from Win32_BaseBoard\") \n" + "For Each objItem in colItems \n" + "    Wscript.Echo objItem.SerialNumber \n"
                          + "    exit for  ' do the first cpu only! \n" + "Next \n";
            } else {
                content = "mySN=`dmidecode -s system-serial-number | grep -v '#'`\n" + "if echo \"${mySN}\" | grep -qiE \"^NotSpecified|^None|^ToBeFilledByO.E.M.|O.E.M.\" ; then\n"
                          + "mySN=`dmidecode -s baseboard-serial-number`\n" + "fi\n" + "if grep -q 'release 4'  /etc/redhat-release ; then\n"
                          + "mySN=`dmidecode | grep -A5 'System Information' | grep 'Serial Number' | awk '{print $3}' | sed 's/^[ \t]*//g' | sed 's/[ \t]$//g'`\n" + "fi\n"
                          + "echo $mySN";
            }
            fw.write(content);
            fw.close();
            Process p = Runtime.getRuntime().exec((isWindows()?"cscript //NoLogo ":"sh ")+ file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {

        }
        return result.trim();
    }

    private static boolean isWindows() {
        return StringUtil.containsIgnoreCase(System.getProperty("os.name"), "win");
    }

    /**
     * 获取硬盘序列号
     *
     * @param drive
     *            盘符
     * @return
     */
    public static String getHardDiskSN(String drive) {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n" + "Set colDrives = objFSO.Drives\n" + "Set objDrive = colDrives.item(\"" + drive + "\")\n"
                         + "Wscript.Echo objDrive.SerialNumber"; // see note
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {

        }
        return result.trim();
    }

    /**
     * 获取CPU序列号
     *
     * @return
     */
    public static String getCPUSerial() {
        String result = "";
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n" + "Set colItems = objWMIService.ExecQuery _ \n"
                         + "   (\"Select * from Win32_Processor\") \n" + "For Each objItem in colItems \n" + "    Wscript.Echo objItem.ProcessorId \n"
                         + "    exit for  ' do the first cpu only! \n" + "Next \n";

            // + "    exit for  \r\n" + "Next";
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
            file.delete();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        if (result.trim().length() < 1 || result == null) {
            result = "无CPU_ID被读取";
        }
        return result.trim();
    }

    /**
     * 获取MAC地址
     * @throws SocketException 
     */
    public static Map<String, String> getMac() throws SocketException {
        return com.tzg.tool.kit.net.NetWork.getNetWorkMacAddress();
    }

}
