package com.tzg.tool.kit.monitor.impl;
/*package com.tzg.tool.kit.monitor.impl;

import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import sun.management.ManagementFactory;

 public class MyPoolSizeCalculator extends PoolSizeCalculator {  
       
      public static void main(String[] args) throws InterruptedException,   
                                                    InstantiationException,   
                                                    IllegalAccessException,  
                                                    ClassNotFoundException {  
          MyPoolSizeCalculator calculator = new MyPoolSizeCalculator();  
       calculator.calculateBoundaries(new BigDecimal(1.0),   
                                      new BigDecimal(100000));  
      }  
       
      protected long getCurrentThreadCPUTime() {  
       return ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();  
      }  
       
      protected Runnable creatTask() {  
       return new AsynchronousTask(0, "IO", 1000000);  
      }  
        
      protected BlockingQueue<Runnable> createWorkQueue() {  
       return new LinkedBlockingQueue<>();  
      }  
       
     } 
*/