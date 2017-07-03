package com.tzg.tool.kit.test.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.commons.io.filefilter.IOFileFilter;
/*
 * 大文件
 */
public class FileCopy {
    private static int BUFFERSIZE = 1024 * 100;

    /** 
     * @param args 
     */
    public static void main(String[] args) {
        String src = "d:/rhel-server-6.3-x86_64-dvd.iso";
        long start = System.currentTimeMillis();
        nioCopy2(src, "f:/test0.iso");
        System.out.println("nio 用时： " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        nioCopy(src, "f:/test1.iso");
        System.out.println("nio 用时： " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        ioCopy(src, "f:/test2.iso");
        System.out.println("io 用时： " + (System.currentTimeMillis() - start));
        
        
        
    }

    public static void ioCopy(String src, String dist) {
        File srcFile = new File(src);
        File distFile = new File(dist);
        if (distFile.exists()) {
            distFile.delete();
        }
        try {
            FileInputStream fin = new FileInputStream(srcFile);
            BufferedInputStream buffIn = new BufferedInputStream(fin);

            FileOutputStream fout = new FileOutputStream(distFile);
            BufferedOutputStream buffout = new BufferedOutputStream(fout);
            byte[] bytes = new byte[BUFFERSIZE];
            while (buffIn.read(bytes) > 0) {
                buffout.write(bytes);
            }
            fin.close();
            buffIn.close();
            fout.close();
            buffout.close();
        } catch (Exception e) {
            
        }

    }

    public static void nioCopy(String src, String dist) {
        try {
            File distFile = new File(dist);
            if (distFile.exists()) {
                distFile.delete();
            }
            FileInputStream fin = new FileInputStream(src);
            FileOutputStream fout = new FileOutputStream(distFile);
            FileChannel inChannel = fin.getChannel();
            FileChannel outChannel = fout.getChannel();
            ByteBuffer buff = ByteBuffer.allocate(BUFFERSIZE);

            while (inChannel.read(buff) > 0) {
                buff.flip();
                if (inChannel.position() == inChannel.size()) {
                    // 判断是不是最后一段数据  
                    break;
                }
                outChannel.write(buff);
                buff.clear();
            }

            int lastRead = (int) (inChannel.size() % BUFFERSIZE);
            byte[] bytes = new byte[lastRead];
            buff.get(bytes, 0, lastRead);
            outChannel.write(ByteBuffer.wrap(bytes));
            buff.clear();

            // 这个使用FileChannel 自带的复制  
            // outChannel.transferFrom(inChannel, 0, inChannel.size());  
            outChannel.close();
            inChannel.close();
            fin.close();
            fout.close();
        } catch (Exception e) {
            
        }
    }
    public static void nioCopy2(String src, String dist) {
        try {
            File distFile = new File(dist);
            if (distFile.exists()) {
                distFile.delete();
            }
            FileInputStream fin = new FileInputStream(src);
            FileOutputStream fout = new FileOutputStream(distFile);
            FileChannel inChannel = fin.getChannel();
            FileChannel outChannel = fout.getChannel();

          /**
            ByteBuffer buff = ByteBuffer.allocate(BUFFERSIZE);
            while (inChannel.read(buff) > 0) {
                buff.flip();
                if (inChannel.position() == inChannel.size()) {
                    // 判断是不是最后一段数据  
                    break;
                }
                outChannel.write(buff);
                buff.clear();
            }

            int lastRead = (int) (inChannel.size() % BUFFERSIZE);
            byte[] bytes = new byte[lastRead];
            buff.get(bytes, 0, lastRead);
            outChannel.write(ByteBuffer.wrap(bytes));
            buff.clear();*/

            // 这个使用FileChannel 自带的复制  
//             outChannel.transferFrom(inChannel, 0, inChannel.size());  
             inChannel.transferTo(0, fin.getChannel().size(), outChannel);  
             
            outChannel.close();
            inChannel.close();
            fin.close();
            fout.close();
        } catch (Exception e) {
            
        }
    }
}