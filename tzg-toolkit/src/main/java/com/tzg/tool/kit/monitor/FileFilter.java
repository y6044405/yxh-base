package com.tzg.tool.kit.monitor;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SizeFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

/**
 * 
 * Filename:    FileFilter.java  
 * Description: 文件查找过滤  
 * IOFileFilter
    1.基本功能过滤器
        类型：DirectoryFileFilter、FileFileFilter
        大小：EmptyFileFilter、SizeFileFilter
        时间：AgeFileFilter
        名称：NameFileFilter、PrefixFileFilter、SuffixFileFilter、RegexFileFilter、WildcardFileFilter
        读写属性：CanReadFileFilter、CanWriteFileFilter
        隐藏属性：HiddenFileFilter
    2.逻辑关系过滤器
        逻辑与：AndFileFilter
        逻辑或：OrFileFilter
        逻辑非：NotFileFilter
        永真/假：TrueFileFilter、FalseFileFilter
 * Copyright:   Copyright (c) 2012-2013 All Rights Reserved.
 * Company:     tzg-soft.com Inc.
 * @author:     heyiwu 
 * @version:    1.0  
 * Create at:   2013-1-9 下午02:34:43  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2013-1-9      heyiwu      1.0         1.0 Version  
 *
 */
public class FileFilter {
    private PrefixFileFilter    prefixF;
    private SuffixFileFilter    suffixF;
    private AgeFileFilter       ageF;
    private SizeFileFilter      sizeF;
    private DirectoryFileFilter directoryF;

    public static void main(String args[]) {
        //按照如下规则查找:
        FileFilter filter = new FileFilter();
        //指定目录下
        File baseDir = new File("E:/work/exchange/");

        /* Calendar instance = Calendar.getInstance();
         //2013.1.2   
         instance.set(2013, Calendar.JANUARY, 1, 23, 59, 59);
         //创建日期在指定日期之前
         filter.setFileAge(instance.getTime(), true);
         //大于2M
         filter.setFileSize(1024 * 1024 * 2);
         */

        // rmi前缀
        List<String> prefixs = new ArrayList<String>();
        prefixs.add("rmi");
        filter.setFilePrefix(prefixs);
        // 指定后缀的文件
        List<String> suffixs = new ArrayList<String>();
        suffixs.add(".xml");
        suffixs.add(".properties");
        filter.setFileSuffix(suffixs);

        //指定路径下
        DirectoryFileFilter directoryF = new DirectoryFileFilter() {
            @Override
            public boolean accept(File file) {
                String path = MessageFormat.format("src{0}main{0}resources", File.separator);
                boolean flag = file.getPath().indexOf(path) > 0;
                if (flag) {
                    System.out.println(file + "\n");
                }
                return flag;
            }
        };
        filter.setDirectoryF(directoryF);

        List<File> list = filter.search(baseDir);
        System.out.println("\n found items: " + list.size());
        for (File file : list) {
            System.out.println(file + ", \t\t\t\tsize:" + FileUtils.byteCountToDisplaySize(file.length()));
        }

    }

    public List<File> search(File baseDir) {
        IOFileFilter fullConditions = new AndFileFilter(unionFilters());
        return search(baseDir, fullConditions);
    }

    public List<File> search(File baseDir, IOFileFilter fullConditions) {
        FileSearcher searcher = new FileSearcher(baseDir, fullConditions);
        Thread thread = new Thread(searcher);
        thread.start();
        try {
            thread.join(); // child thread join to main thread
        } catch (InterruptedException e) {
            
        }
        return searcher.getSearchList();
    }

    public void setFilePrefix(List<String> prefix) {
        prefixF = new PrefixFileFilter(prefix, IOCase.SYSTEM);
    }

    public void setFileSuffix(List<String> suffix) {
        suffixF = new SuffixFileFilter(suffix, IOCase.SYSTEM);
    }

    public void setFileAge(Date cutoffDate) {
        ageF = new AgeFileFilter(cutoffDate);
    }

    public void setFileAge(Date cutoffDate, boolean acceptOlder) {
        ageF = new AgeFileFilter(cutoffDate, acceptOlder);
    }

    public void setFileSize(long size) {
        sizeF = new SizeFileFilter(size);
    }

    public void setFileSize(long size, boolean acceptLarger) {
        sizeF = new SizeFileFilter(size, acceptLarger);
    }

    List<IOFileFilter> unionFilters() {
        List<IOFileFilter> allFilters = new ArrayList<IOFileFilter>();
        if (null != prefixF)
            allFilters.add(prefixF);
        if (null != suffixF)
            allFilters.add(suffixF);
        if (null != ageF)
            allFilters.add(ageF);
        if (null != sizeF)
            allFilters.add(sizeF);
        if (null != directoryF) {
            allFilters.add(directoryF);
        }
        return allFilters;
    }

    class FileSearcher extends DirectoryWalker implements Runnable {
        private volatile boolean cancelled   = false;
        private File             basePath;
        private List<File>       finalResult = new ArrayList<File>();
        private long             startTime;
        private long             endTime;

        public FileSearcher(File basePath, IOFileFilter filter) {
            super(FileFilterUtils.directoryFileFilter(), filter, -1);
            this.basePath = basePath;
        }

        public void run() {
            filter();
        }

        public void filter() {
            List<File> result = new ArrayList<File>();
            try {
                startTime = new Date().getTime();
                walk(basePath, result);
            } catch (IOException e) {
                
            }
            endTime = new Date().getTime();
            finalResult = result;
        }

        public List<File> getResult() {
            return finalResult;
        }

        public List<File> getSearchList() {
            long elapsed = endTime - startTime;
            System.out.println("\n time costing: " + elapsed + "ms");
            return finalResult;
        }

        protected boolean handleDirectory(File directory, int depth, Collection results) throws IOException {
            return true;
        }

        protected void handleFile(File file, int depth, Collection results) throws IOException {
            results.add(file);
        }

        public void cancel() {
            cancelled = true;
        }

        protected boolean handleIsCancelled(File file, int depth, Collection results) {
            return cancelled;
        }

        protected void handleCancelled(File startDirectory, Collection results, CancelException cancel) {
            if (cancelled) {
                cancel();
            }
            System.out.println("\n cancelled by external or interal thread");
            finalResult = (List<File>) results;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }
    }

    public DirectoryFileFilter getDirectoryF() {
        return directoryF;
    }

    public void setDirectoryF(DirectoryFileFilter directoryF) {
        this.directoryF = directoryF;
    }
}