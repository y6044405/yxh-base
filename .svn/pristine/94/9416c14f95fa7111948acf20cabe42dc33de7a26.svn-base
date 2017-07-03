package com.tzg.tool.kit.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Filename: NetWork.java Description:
 * 获取网卡工具类:从JDK1.4开始，Java提供了一个NetworkInterface类。
 * 这个类可以得到本机所有的物理网络接口和虚拟机等软件利用本机的物理网络接口创建的逻辑网络接口的信息。
 * 多用于获取服务端网卡地址，也可用于获取客户端网卡物理地址，但受限于网络、路由等环境，准确率不高,特别是应用于公网时局限性较大. Copyright:
 * Copyright (c) 2012-2013 All Rights Reserved. Company: tzg-soft.com Inc.
 * 
 * @author: heyiwu
 * @version: 1.0 Create at: 2013-1-10 下午12:01:49
 * 
 *           Modification History: Date Author Version Description
 *           ------------------------------------------------------------------
 *           2013-1-10 heyiwu 1.0 1.0 Version
 * 
 */
public class NetWork {
    protected static final Logger logger = LoggerFactory.getLogger(NetWork.class);


    /**
     * 获取所有网卡的名称和物理地址
     * 
     * @return key：网卡名称(简称),value:物理地址
     * @throws SocketException
     */
    public static Map<String, String> getNetWorkMacAddress() throws SocketException {
        return getNetWorkMacAddress(true);
    }

    /**
     * 获取所有网卡的名称和物理地址
     * 
     * @param effective
     *            true：有效地址;false:所有地址
     * @return key：网卡名称(简称),value:物理地址
     * @throws SocketException
     */
    public static Map<String, String> getNetWorkMacAddress(boolean effective) throws SocketException {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration enums = NetworkInterface.getNetworkInterfaces();
        while (enums.hasMoreElements()) {
            NetworkInterface net = (NetworkInterface) enums.nextElement();
            // printParameter(net);
            String mac = getNetWorkMacAddress(net.getHardwareAddress());
            if (null == mac && effective) {
                continue;
            }
            map.put(net.getName(), mac);
        }
        return map;
    }

    /**
     * 获取指定的网卡物理地址
     * 
     * @param ip
     *            域名或ip
     * @return
     */
    public static String getNetWorkMacAddress(String ip) {
        try {
            InetAddress addr = InetAddress.getByName(ip);
            NetworkInterface net = NetworkInterface.getByInetAddress(addr);
            if (null == net) {
                return "";
            }
            return getNetWorkMacAddress(net.getHardwareAddress());
        } catch (SocketException e) {
            logger.error("get macAddress SocketException：{}", e.getLocalizedMessage());
        } catch (UnknownHostException e) {
            logger.error("get macAddress UnknownHostException：{}", e.getLocalizedMessage());
        }
        return "";
    }

    /**
     * 获取网卡物理地址字符串
     * 
     * @param macBytes
     *            网卡物理地址
     * @return
     */
    private static String getNetWorkMacAddress(byte[] macBytes) {
        if (macBytes == null || macBytes.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (byte mac : macBytes) {
            // byte表示范围-128~127，因此>127的数被表示成负数形式，这里+256转换成正数
            int m = mac < 0 ? (mac + 256) : mac;
            String item = Integer.toHexString(m).toUpperCase();
            builder.append((item.length() == 1 ? "0" + item : item) + "-");
        }
        return builder.deleteCharAt(builder.length() - 1).toString();
    }

    /**
     * 打印网络接口相关属性、方法
     * 
     * @param ni
     * @throws SocketException
     */
    public static void printParameter(NetworkInterface ni) throws SocketException {
        System.out.println(" Name = " + ni.getName());
        System.out.println(" Display Name = " + ni.getDisplayName());
        System.out.println(" Is up = " + ni.isUp());
        System.out.println(" Support multicast = " + ni.supportsMulticast());
        System.out.println(" Is loopback = " + ni.isLoopback());
        System.out.println(" Is virtual = " + ni.isVirtual());
        System.out.println(" Is point to point = " + ni.isPointToPoint());
        System.out.println(" Hardware address = " + new String(ni.getHardwareAddress()));
        System.out.println(" MTU = " + ni.getMTU());
        System.out.println("\nList of Interface Addresses:");
        List<InterfaceAddress> list = ni.getInterfaceAddresses();
        Iterator<InterfaceAddress> it = list.iterator();
        while (it.hasNext()) {
            InterfaceAddress ia = it.next();
            System.out.println(" Address = " + ia.getAddress());
            System.out.println(" Broadcast = " + ia.getBroadcast());
            System.out.println(" Network prefix length = " + ia.getNetworkPrefixLength());
        }
    }

    /**
     * 利用操作系统命令获取网卡名称和网卡物理地址 推荐方法:@see getNetWorkMacAddress
     */
    @Deprecated
    public static Map<String, String> getMacAddress() {
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            return getWinMacAddress();
        }
        return getUnixMACAddress();
    }

    @Deprecated
    public static Map<String, String> getWinMacAddress() {
        Map<String, String> map = new HashMap<String, String>();
        try {
            String command = "cmd.exe /c ipconfig /all";
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            String name = "";
            while ((line = br.readLine()) != null) {
                if (line.indexOf("Description") > 0) {
                    name = line.substring(line.indexOf(":") + 2);
                }
                if (line.indexOf("Physical Address") > 0) {
                    String mac = line.substring(line.indexOf(":") + 2);
                    map.put(name, mac);
                }
            }
            br.close();
        } catch (IOException e) {
            logger.warn("获取windos网卡地址IO异常:" + e.getLocalizedMessage());
        }
        return map;
    }

    /**
     * 获取unix网卡的mac地址. 非windows的系统默认调用本方法获取.如果有特殊系统请继续扩充新的取mac地址方法.
     * 
     * @return mac地址
     */
    @Deprecated
    public static Map<String, String> getUnixMACAddress() {
        Map<String, String> map = new HashMap<String, String>();
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("ifconfig");// linux下的命令，
            // 一般取eth0作为本地主网卡
            // 显示信息中包含有mac地址信息
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            String name = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (line.toLowerCase().startsWith("eth")) {
                    name = line.substring(0, line.indexOf(" ")).trim();
                }
                int index = line.toLowerCase().indexOf("hwaddr");
                if (index >= 0) {
                    map.put(name, line.substring(index + "hwaddr".length() + 1).trim());
                }
            }
        } catch (IOException e) {
            logger.warn("获取网卡地址IO异常:" + e.getLocalizedMessage());
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
                logger.warn("获取网卡地址,关闭IO异常:" + e1.getLocalizedMessage());
            }
            bufferedReader = null;
            process = null;
        }

        return map;
    }

    /**
     * 获取本机IP
     * @param all 是否获取本机所有ip(是否包含虚拟网卡的ip)
     * @return
     */
    public static String[] getLocalHostIP(boolean all) {
        Set<String> set = new HashSet<String>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface net = (NetworkInterface) interfaces.nextElement();
                Enumeration<InetAddress> enus = net.getInetAddresses();
                while (enus.hasMoreElements()) {
                    InetAddress addr = (InetAddress) enus.nextElement();
                    String ip = addr.getHostAddress();
                    if (ip.indexOf(":") != -1 || ip.startsWith("127.0")
                        || (!all && (!ip.startsWith("192.168") || net.getDisplayName().startsWith("VMware") || net.getDisplayName().startsWith("VirtualBox")))) {
                        continue;
                    }
                    logger.debug(net.getDisplayName() + " [" + net.getName() + "]:" + addr.getHostAddress());
                    set.add(addr.getHostAddress());
                }
            }
        } catch (SocketException e) {

        }
        return set.toArray(new String[] {});
    }

    /** 
       * 获取MAC地址,length为0时,
       * 为获取失败，有可能有多个mac地址 
       * @since jdk6 
       * @return List<String> 
       */
    public static List<String> getMac() {
        try {
            Collection<String> values = getNetWorkMacAddress().values();
            String[] arrays = values.toArray(new String[] {});
            return Arrays.asList(arrays);
        } catch (SocketException e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }
}
