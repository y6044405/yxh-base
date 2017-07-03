package com.tzg.tool.kit.test.file.cut;

import java.io.*;
import java.util.ArrayList;

public class MergedFile //字节流的读写文件简介     
{
    public static void main(String[] args) throws IOException {
        MergedFile fileStream = new MergedFile();
        fileStream.refreshFileList("F:\\java");

    }

    //遍历目录获取所有需要合并的的文件  
    private static ArrayList filelist = new ArrayList();

    public void refreshFileList(String strPath) {
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        if (files == null)
            return;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                refreshFileList(files[i].getAbsolutePath());
            } else {
                String strFileName = files[i].getAbsolutePath().toLowerCase();
                try {
                    //int fileNum = 0;  
                    //fileNum++;  
                    readFile(strFileName);
                    //prefix 截取文件后缀  
                    String prefix = strFileName.substring(strFileName.lastIndexOf(".") + 1);
                    writeFile(strFileName, "merged." + prefix);
                } catch (IOException e) {
                }
                System.out.println("---" + strFileName);
                filelist.add(files[i].getAbsolutePath());
            }
        }
    }

    //写文件的方法     
    public static void writeFile(String filesource, String fileDestination) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileDestination, true);
        byte[] buf = readFile(filesource).buf;
        fos.write(buf);
        fos.close();
    }

    //使用FileInputStream中的available()方法读文件存入到buf中,并获取文件长度和buf    
    public static class ReadFile {
        public ReadFile(byte[] b, int fl) {
            buf = b;
            filelen = fl;
        }

        public byte[] getBuf() {
            return buf;
        }

        public int getFileLen() {
            return filelen;
        }

        private byte[] buf;
        private int    filelen;
    }

    public static ReadFile readFile(String filesource) throws IOException {
        FileInputStream fis = new FileInputStream(filesource);
        //available()方法可以知道文件具体有多少个字节。   
        int filelen = fis.available();
        byte[] buf = new byte[filelen];
        fis.read(buf);
        fis.close();
        return new ReadFile(buf, filelen);
    }
}