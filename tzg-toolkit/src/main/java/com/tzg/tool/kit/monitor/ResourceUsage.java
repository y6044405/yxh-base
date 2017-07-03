package com.tzg.tool.kit.monitor;
/*package com.tzg.tool.kit.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tzg.tool.kit.monitor.bean.SystemMXBean;

 

*//**
 * 
 * 获取系统信息.
 * 获取磁盘资料：
wmic DISKDRIVE get deviceid,Caption,size,InterfaceType
获取分区资料：
wmic LOGICALDISK get name,Description,filesystem,size,freespace
获取CPU资料:
wmic cpu get name,addresswidth,processorid
获取主板资料:
wmic BaseBoard get Manufacturer,Product,Version,SerialNumber
获取内存数:
wmic memlogical get totalphysicalmemory
获得品牌机的序列号:
wmic csproduct get IdentifyingNumber
获取声卡资料:
wmic SOUNDDEV get ProductName
获取屏幕分辨率
wmic DESKTOPMONITOR where Status='ok' get ScreenHeight,ScreenWidth
 * 
 *//*
public class ResourceUsage {
	private static final Logger logger = LoggerFactory
			.getLogger(ResourceUsage.class);

	*//**
	 * 获得当前的监控对象.
	 * 
	 * @return 返回构造好的监控对象
	 *//*
	public static SystemMXBean getSystemMXBean() {
		int kb = 1024;
		SystemMXBean infoBean = new SystemMXBean();
		// 剩余内存
		infoBean.setFreeMemory(Runtime.getRuntime().freeMemory() / kb);
		// 最大可使用内存
		infoBean.setMaxMemory(Runtime.getRuntime().maxMemory() / kb);
		infoBean.setOsName(System.getProperty("os.name"));
		// 可使用内存
		infoBean.setTotalMemory(Runtime.getRuntime().totalMemory() / kb);
		//jdk1.7 java.lang.management.ManagementFactory
		OperatingSystemMXBean mxb = (OperatingSystemMXBean) ManagementFactory
				.getOperatingSystemMXBean();

		// 获得线程总数
		ThreadGroup parentThread;
		for (parentThread = Thread.currentThread().getThreadGroup(); parentThread
				.getParent() != null; parentThread = parentThread.getParent())
			;
		// 剩余的物理内存
		infoBean.setFreePhysicalMemory(mxb.getFreePhysicalMemorySize() / kb);
		// 总的物理内存
		infoBean.setTotalMemorySize(mxb.getTotalPhysicalMemorySize() / kb);
		infoBean.setTotalThread(parentThread.activeCount());
		// 已使用的物理内存
		infoBean.setUsedMemory((mxb.getTotalPhysicalMemorySize() - mxb
				.getFreePhysicalMemorySize())
				/ kb);
		infoBean.setCpuRatio(getCpuRatio());
		return infoBean;
	}

	*//**
	 * CPU使用率
	 * 
	 * @return
	 *//*
	private static double getCpuRatio() {
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			return getCpuRatioForWindows();
		}
		return getCpuRateForLinux();
	}

	*//**
	 * linux下采集cpu使用率
	 * 
	 * @return
	 *//*
	private static double getCpuRateForLinux() {
		float cpuUsage = 0;
		long[] cpu1 = getProcStat();
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
		}
		long[] cpu2 = getProcStat();
		if (cpu1[0] != 0 && cpu1[1] != 0 && cpu2[0] != 0 && cpu2[1] != 0) {
			// CPU利用率 = 1- (idle2-idle1)/(cpu2-cpu1)
			cpuUsage = 1 - (float) (cpu2[0] - cpu1[0])
					/ (float) (cpu2[1] - cpu2[1]);
		}
		return cpuUsage;
	}

	private static long[] getProcStat() {
		long[] ret = new long[2];
		InputStream in = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("cat /proc/stat");
			in = process.getInputStream();
			isr = new InputStreamReader(in);
			reader = new BufferedReader(isr);
			// 分别为系统启动后空闲的CPU时间和总的CPU时间
			long idleCpuTime = 0, totalCpuTime = 0;
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("cpu")) {
					continue;
				}
				LoggerFactory.debug(line);
				String[] temp = line.trim().split("\\s+");
				idleCpuTime = Long.parseLong(temp[4]);
				for (String s : temp) {
					if (!s.equals("cpu")) {
						totalCpuTime += Long.parseLong(s);
					}
				}
				ret[0] = idleCpuTime;
				ret[1] = totalCpuTime;
				break;
			}
		} catch (IOException e) {
			LoggerFactory.error(e.getLocalizedMessage());
		} finally {
			try {
				if (null != in) {
					in.close();
				}
				process.destroy();
			} catch (IOException e) {
			}
		}
		return ret;
	}

	*//**
	 * 获得CPU使用率.
	 * 
	 * @return 返回cpu使用率
	 * @author GuoHuang
	 *//*
	private static double getCpuRatioForWindows() {
		try {
			String procCmd = System.getenv("windir")
					+ "\\system32\\wbem\\wmic.exe process get Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
			// 取进程信息
			long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
			Thread.sleep(30);
			long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
			if (c0 != null && c1 != null) {
				long idletime = c1[0] - c0[0];
				long busytime = c1[1] - c0[1];
				final int PERCENT = 100;
				return Double.valueOf(
						PERCENT * (busytime) / (busytime + idletime))
						.doubleValue();
			} else {
				return 0.0;
			}
		} catch (Exception ex) {
			LoggerFactory.error(ex.getLocalizedMessage());
			return 0.0;
		}
	}

	*//**
	 * 
	 * 读取CPU信息.
	 * 
	 * @param proc
	 * @return
	 * @author GuoHuang
	 *//*
	private static long[] readCpu(final Process proc) {
		long[] retn = new long[2];
		try {
			proc.getOutputStream().close();
			InputStreamReader ir = new InputStreamReader(proc.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			String line = input.readLine();
			if (line == null || line.length() < 10) {
				return null;
			}
			int capidx = line.indexOf("Caption");
			int cmdidx = line.indexOf("CommandLine");
			int rocidx = line.indexOf("ReadOperationCount");
			int umtidx = line.indexOf("UserModeTime");
			int kmtidx = line.indexOf("KernelModeTime");
			int wocidx = line.indexOf("WriteOperationCount");
			long idletime = 0;
			long kneltime = 0;
			long usertime = 0;
			while ((line = input.readLine()) != null) {
				if (line.length() < wocidx) {
					continue;
				}
				// 字段顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
				// ThreadCount,UserModeTime,WriteOperation
				String caption = substring(line, capidx, cmdidx - 1).trim();
				String cmd = substring(line, cmdidx, kmtidx - 1).trim();
				if (cmd.indexOf("wmic.exe") >= 0) {
					continue;
				}
				LoggerFactory.debug("{}", line);
				if (caption.equals("System Idle Process")
						|| caption.equals("System")) {
					idletime += Long.valueOf(
							substring(line, kmtidx, rocidx - 1).trim())
							.longValue();
					idletime += Long.valueOf(
							substring(line, umtidx, wocidx - 1).trim())
							.longValue();
					continue;
				}

				kneltime += Long.valueOf(
						substring(line, kmtidx, rocidx - 1).trim()).longValue();
				usertime += Long.valueOf(
						substring(line, umtidx, wocidx - 1).trim()).longValue();
			}
			retn[0] = idletime;
			retn[1] = kneltime + usertime;
			return retn;
		} catch (Exception ex) {
			LoggerFactory.error(ex.getLocalizedMessage());
		} finally {
			try {
				proc.getInputStream().close();
			} catch (Exception e) {
				LoggerFactory.error(e.getLocalizedMessage());
			}
		}
		return null;
	}

	*//**
	 * 测试方法.
	 * 
	 * @param args
	 * @throws Exception
	 * @author GuoHuang
	 *//*
	public static void main(String[] args) throws Exception {
		while (true) {
			SystemMXBean mxBean = getSystemMXBean();
			LoggerFactory.info("操作系统={}", mxBean.getOsName());
			LoggerFactory.info("cpu占有率={}%", mxBean.getCpuRatio());
			LoggerFactory.info("可使用内存={}KB", mxBean.getTotalMemory());
			LoggerFactory.info("剩余内存={}KB", mxBean.getFreeMemory());
			LoggerFactory.info("最大可使用内存={}KB", mxBean.getMaxMemory());

			LoggerFactory.info("总的物理内存={}KB", mxBean.getTotalMemorySize());
			LoggerFactory.info("剩余的物理内存={}KB", mxBean.getFreeMemory());
			LoggerFactory.info("已使用的物理内存={}KB", mxBean.getUsedMemory());
			LoggerFactory.info("线程总数={}", mxBean.getTotalThread());
		}
	}

	public static String substring(String src, int start_idx, int end_idx) {
		byte[] b = src.getBytes();
		String tgt = "";
		for (int i = start_idx; i <= end_idx; i++) {
			tgt += (char) b[i];
		}
		return tgt;
	}
	
	

	*//**
	 * 获取程序进程信息 获取进程信息导出到html文件中： wmic /output:c:\process.html process where
	 * name="java.exe" get CommandLine,ExecutablePath,Description,Name,ProcessId
	 * /format:htable format其他参数:table、list
	 * @param name  程序名称,如：java.exe
	 * @return
	 *//*
	public static List<Map<String, String>> getProcess(String name) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String caption = "CommandLine,Description,ExecutablePath,Name,ProcessId";
		String[] cations = caption.split(",");
		String cmd = "wmic process where name=\"" + name + "\" get " + caption
				+ " /format:list";
		Process process = null;
		InputStream in = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			in = process.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line = null;
			Map<String, String> map = null;
			while ((line = reader.readLine()) != null) {
				int index = line.indexOf("=");
				if (StringUtils.isBlank(line) || index == -1) {
					continue;
				}
				String key = line.substring(0, index);
				if (cations[0].equals(key)) {
					map = new HashMap<String, String>();
				}
				map.put(key, line.substring(index + 1, line.length()));
				if (cations[4].equals(key)) {
					list.add(map);
				}
			}
		} catch (IOException e) {
			
			// log.error(e.getLocalizedMessage());
		} finally {
			try {
				if (null != in)
					in.close();
				process.destroy();
			} catch (IOException e) {
			}

		}
		return list;
	}
}*/