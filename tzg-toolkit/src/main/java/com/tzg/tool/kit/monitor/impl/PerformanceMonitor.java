package com.tzg.tool.kit.monitor.impl;

import java.util.LinkedList;
import java.util.Random;

/** */
/**
 * 本类用于示范使用 Runtime 检查系统运行情况。
 * 将 Runtime 作为可变成员，是为多系统公用检查预留的设计。
 * @author    Shane Loo Li
 */
public class PerformanceMonitor {
    /** */
    /**
         * 此量控制程序运行时间。此值越大，此演示程序运行时间越长。
         */
    static public int runLoopTimes = 55;

    /** */
    /**
         * 此为每次检测间隔的时间片数。此值越大，间隔时间越长。
         */
    static public int waitTime     = 1500000;

    static public void main(String[] arguments) throws Exception {
        Runtime context = Runtime.getRuntime();
        final PerformanceMonitor monitor = new PerformanceMonitor(context);
        final LinkedList<String> pretendedMemory = new LinkedList<String>();
        new Thread(new Runnable() {
            public void run() {
                for (int j = -1; ++j != runLoopTimes;) {
                    // 检查系统情况
                    monitor.checkAll();

                    // 每次检查运行情况之后，都会休息 1000 个时间片
                    for (int i = -1; ++i != waitTime;)
                        Thread.yield();

                    // 每次检查之后，会制造一些对象，记录其中一部分，并删除些老对象
                    for (int i = -1; ++i != 20000;) {
                        StringBuilder builder = new StringBuilder();
                        Random ran = new Random();
                        for (int index = -1; ++index != 100;)
                            builder.append((char) (ran.nextInt(26) + 64));
                        String garbage = new String(builder.toString());
                        garbage = garbage.substring(garbage.length());
                        pretendedMemory.add(builder.toString());
                    }
                    int deleteCount = new Random().nextInt(15000);
                    for (int i = -1; ++i != deleteCount;)
                        pretendedMemory.removeFirst();

                    System.out.println("-----------");
                }
            }
        }).start();
    }

    private Runtime context;
    private double  maxFreeMemory;

    private long    lastFreeMemory;
    private long    lastTotalMemory;
    private long    lastMaxMemory;

    private double  lastMemoryRate;

    public PerformanceMonitor(Runtime context) {
        this.context = context;
    }

    public void checkAll() {
        this.monitorMemory();
        this.monitorCpu();
    }

    /** */
    /**
         * 本方法比较当前空余内存与历史记录最大空余内存的关系。若空余内存过小，则执行垃圾回收。
         */
    public void monitorMemory() {
        // 求空余内存，并计算空余内存比起最大空余内存的比例
        long freeMemory = this.context.freeMemory();
        if (freeMemory > this.maxFreeMemory)
            this.maxFreeMemory = Long.valueOf(freeMemory).doubleValue();
        double memoryRate = freeMemory / this.maxFreeMemory;
        System.out.println("There are " + memoryRate * 100 + "% free memory.");

        // 如果内存空余率在变小，则一切正常；否则需要报告内存变化情况
        if (memoryRate >= this.lastMemoryRate)
            this.reportMemoryChange();

        // 如果内存空余率很低，则执行内存回收
        if (freeMemory / this.maxFreeMemory < 0.3) {
            System.out.print("System will start memory Garbage Collection.");
            System.out.println(" Now we have " + freeMemory / 1000 + " KB free memory.");
            this.context.gc();
            System.out.println("After the Garbage Collection, we have " + this.context.freeMemory() / 1000 + " KB free memory.");
        }

        // 记录内存信息
        this.recordMemoryInfo(memoryRate);
    }

    /** */
    /**
         * 报告内存变化情况
         */
    private void reportMemoryChange() {
        System.out.print("Last freeMemory = " + this.lastFreeMemory / 1000 + " KB,");
        System.out.println(" now it is " + this.context.freeMemory() / 1000 + " KB.");
        System.out.print("Last totalMemory = " + this.lastTotalMemory / 1000 + " KB,");
        System.out.println(" now it is " + this.context.totalMemory() / 1000 + " KB.");
        System.out.print("Last maxMemory = " + this.lastMaxMemory / 1000 + " KB,");
        System.out.println(" now it is " + this.context.maxMemory() / 1000 + " KB.");
    }

    /** */
    /**
         * 记录本次内存信息。
         */
    private void recordMemoryInfo(double memoryRate) {
        this.lastFreeMemory = this.context.freeMemory();
        this.lastMaxMemory = this.context.maxMemory();
        this.lastTotalMemory = this.context.totalMemory();
        this.lastMemoryRate = memoryRate;
    }

    /** */
    /**
         * 监测 CPU 的方法。
         */
    public void monitorCpu() {
        int cpuCount = this.context.availableProcessors();
        if (cpuCount > 1)
            System.out.println("CPU have " + cpuCount + " processors.");
    }
}