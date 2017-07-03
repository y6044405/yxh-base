package com.tzg.tool.kit.monitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  
 * 文件变化监听器  
 * apache的Commons-IO 文件的监控,原理如下：  
 * 由文件监控类FileAlterationMonitor中的线程不停的扫描文件观察器FileAlterationObserver，  
 * 如果有文件的变化，则根据相关的文件比较器，判断文件时新增，还是删除，还是更改。（默认为1000毫秒执行一次扫描）  
 */
public class FileListener extends FileAlterationListenerAdaptor {
    private Logger       log = LoggerFactory.getLogger(FileListener.class);
    private WatchService watcher;

    public static void main(String[] args) throws Exception {
        // 监控目录     
        String rootDir = "D:/srv";
        // 轮询间隔 5 秒     
        long interval = TimeUnit.SECONDS.toMillis(5);
        // 创建一个文件观察器用于处理文件的格式     
         //过滤文件格式     
           FileAlterationObserver _observer = new FileAlterationObserver(     
                                                 rootDir,      
                                                 FileFilterUtils.and(     
                                                  FileFilterUtils.fileFileFilter(),     
                                                  FileFilterUtils.suffixFileFilter(".txt")),  
                                                 null);     
        FileAlterationObserver observer = new FileAlterationObserver(rootDir);
        //设置文件变化监听器
        observer.addListener(new FileListener());
        //创建文件变化监听器     
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
        // 开始监控     
        monitor.start();
//        new FileListener(Paths.get(rootDir)).handleEvents();  
    }

    public FileListener() {
    }
    
    /**
     * 初始化
     * @param path 路径必须存在
     * @throws IOException
     */
    public FileListener(Path path) throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
        path.register(watcher, StandardWatchEventKinds.OVERFLOW, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    public void handleEvents() throws InterruptedException {
        while (true) {
            WatchKey key = watcher.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) {//事件可能lost or discarded  
                    continue;
                }
                WatchEvent<Path> e = (WatchEvent<Path>) event;
                Path fileName = e.context();

                System.out.printf("Event %s has happened,which fileName is %s%n", kind.name(), fileName);
            }
            if (!key.reset()) {
                break;
            }
        }
    }

    /**  
     * 文件创建执行  
     */
    @Override
    public void onFileCreate(File file) {
        log.info("[新建]:" + file.getAbsolutePath());
    }

    /**  
     * 文件创建修改  
     */
    @Override
    public void onFileChange(File file) {
        log.info("[修改]:" + file.getAbsolutePath());
    }

    /**  
     * 文件删除  
     */
    @Override
    public void onFileDelete(File file) {
        log.info("[删除]:" + file.getAbsolutePath());
    }

    /**  
     * 目录创建  
     */
    @Override
    public void onDirectoryCreate(File directory) {
        log.info("[新建]:" + directory.getAbsolutePath());
    }

    /**  
     * 目录修改  
     */
    @Override
    public void onDirectoryChange(File directory) {
        log.info("[修改]:" + directory.getAbsolutePath());
    }

    /**  
     * 目录删除  
     */
    @Override
    public void onDirectoryDelete(File directory) {
        log.info("[删除]:" + directory.getAbsolutePath());
    }

    @Override
    public void onStart(FileAlterationObserver observer) {
        super.onStart(observer);
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
        super.onStop(observer);
    }

}