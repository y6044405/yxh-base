package com.tzg.tool.kit.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VpnUtil {
	private static final Logger logger = LoggerFactory.getLogger(VpnUtil.class);

	public static void main(String[] args) throws IOException {
		logger.info("连接vpn：{}",
				connectVPN("VPN", "tzg", "gd@1qaz2wsx") ? "成功!" : "失败!");
		logger.info("断开vpn连接:{}", disconnectVPN("VPN"));
	}

	/**
	 * 拨号连接VPN 注意：
	 * <p>
	 * 1、利用windows下的rasdial命令,限windows环境下调用 2、VPN连接必须先创建好
	 * </p>
	 * 
	 * @param vpnName
	 *            已创建的VPN名称,在网络连接中显示的名称
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 * @throws IOException
	 */
	public synchronized static boolean connectVPN(String vpnName,
			String username, String password) throws IOException {
		String cmd = "rasdial " + vpnName + " " + username + " " + password;
		logger.info("exec cmd:{}", cmd);
		String result = executeCmd(cmd);
		logger.debug("connect {} VPN,result:{}", vpnName, result);
		return (result != null && (result.contains("已经连接") || result
				.contains("已连接")));
	}

	/**
	 * 断开VPN连接
	 * <p>
	 * 限windows环境下调用
	 * </p>
	 * 
	 * @param vpnName
	 *            vpn名称
	 * @return
	 * @throws IOException
	 */
	public synchronized static boolean disconnectVPN(String vpnName)
			throws IOException {
		String cmd = "rasdial " + vpnName + " /disconnect";
		String result = executeCmd(cmd);
		logger.debug("断开{} vpn连接:{}", vpnName, result);
		return (result != null && !result.contains("没有连接"));
	}

	private synchronized static String executeCmd(String cmd)
			throws IOException {
		Process process = Runtime.getRuntime().exec("cmd /c " + cmd);
		StringBuilder sbCmd = new StringBuilder();
		String charset = System.getProperty("sun.jnu.encoding");
		if (null == charset || "".equals(charset)) {
			charset = System.getProperty("file.encoding");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(process
				.getInputStream(), charset));
		String line = "";
		while ((line = br.readLine()) != null) {
			sbCmd.append(line);
		}
		return sbCmd.toString();
	}
}
