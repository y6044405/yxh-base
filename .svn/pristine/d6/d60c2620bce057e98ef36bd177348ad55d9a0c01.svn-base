package com.tzg.tool.kit.test.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.tzg.tool.kit.file.FileUtils;

public class TransformFileEncoding {
    public static void main(String[] args) throws IOException {
        String dir = "D:/db";
        String destEncoding = "UTF-8";
        String suffix="html";
        transform(dir, suffix,destEncoding);
//        filter(FileUtils.readFileToString(new File(dir,"applicationContext-common.xml")));
    }

    public static String filter(String str) {
        System.out.println(str.length());
        str=str.replaceAll(  
            "[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");
        System.out.println(str.length());
        return str;  
    }

    /**
     * 文本文件转码
     * @param dir   目录路径
     * @param destEncoding 目标编码
     * @throws IOException
     */
    public static void transform(String dir,String suffix, String destEncoding) throws IOException {
        List<File> files = FileUtils.getFiles(new File(dir), suffix);
        transform(destEncoding, files);
    }

    /**
     * 文本文件转码
     * @param destEncoding
     * @param files
     * @throws IOException
     */
    public static void transform(String destEncoding, List<File> files) throws IOException {
        for (File file : files) {
            String encoding = FileUtils.getEncoding(file);
            if (destEncoding.equals(encoding)) {
                continue;
            }
            System.out.println(file + " \n transform file encoding from " + encoding + " to " + destEncoding);
            StringBuilder content = FileUtils.readContent(file, encoding);
            FileUtils.write(file, content, destEncoding);
            System.out.println("file content:\n" + FileUtils.readContent(file, destEncoding));
        }
    }
}
