package com.tzg.tool.kit.test.file.cut;

import java.io.BufferedReader;     
import java.io.BufferedWriter;     
import java.io.File;     
import java.io.FileInputStream;     
import java.io.FileOutputStream;     
import java.io.IOException;     
import java.io.InputStream;     
import java.io.InputStreamReader;     
import java.io.OutputStream;     
import java.io.OutputStreamWriter;     
    
/**   
 * 对文件进行分割与合并的工具   
 *    
 * @author Administrator   
 */    
public class CutJoinFile {     
    
  private static int MAX_PART = 100; // 最大分割100分，以免手误引起麻烦     
    
  public static void main(String[] args) throws IOException {     
    String filname = "d:/cvs.rar";     
    cutFileBin(filname, 2 * 1024 * 1024); // 二进制文件为例     
    joinFileBin(filname); // 二进制文件为例     
  }     
    
  /**   
   * 分割文本文件   
   *    
   * @param fileName 文件名   
   * @param size 每个分割小文件的尺寸   
   * @throws IOException   
   */    
  public static void cutFileText(String fileName, int size) throws IOException {     
    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));     
    int index = fileName.lastIndexOf(".");     
    String ext = "";     
    if (index > 0) {     
      ext = fileName.substring(index);     
      fileName = fileName.substring(0, index);     
    }     
    int len;     
    int count = 1;     
    char[] cbuf = new char[size];     
    while ((len = reader.read(cbuf, 0, size)) != -1) {     
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName + "_" + count + ext)));     
      writer.write(cbuf, 0, len);     
      writer.flush();     
      writer.close();     
      count++;     
      if (count > MAX_PART) {     
        break;     
      }     
    }     
  }     
    
  /**   
   * 分割二进制文件   
   *    
   * @param fileName 文件名   
   * @param size 每个分割小文件的尺寸   
   * @throws IOException   
   */    
  public static void cutFileBin(String fileName, int size) throws IOException {     
    InputStream is = new FileInputStream(fileName);     
    int index = fileName.lastIndexOf(".");     
    String ext = "";     
    if (index > 0) {     
      ext = fileName.substring(index);     
      fileName = fileName.substring(0, index);     
    }     
    int len;     
    int count = 1;     
    byte[] cbuf = new byte[size];     
    while ((len = is.read(cbuf, 0, size)) != -1) {     
      OutputStream os = new FileOutputStream(fileName + "_" + count + ext);     
      os.write(cbuf, 0, len);     
      os.close();     
      count++;     
      if (count > MAX_PART) {     
        break;     
      }     
    }     
  }     
    
  /**   
   * 合并文本文件   
   *    
   * @param fileName 文件名，不包括编号部分   
   * @throws IOException   
   */    
  public static void joinFileText(String fileName) throws IOException {     
    int index = fileName.lastIndexOf(".");     
    String ext = "";     
    if (index > 0) {     
      ext = fileName.substring(index);     
      fileName = fileName.substring(0, index);     
    }     
    
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName + "_join" + ext)));     
    int len;     
    char[] cbuf = new char[1024 * 100];     
    File file;     
    for (int i = 1; i <= Integer.MAX_VALUE; i++) {     
      file = new File(fileName + "_" + i + ext);     
      if (!file.exists()) {     
        break;     
      }     
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));     
      while ((len = reader.read(cbuf, 0, cbuf.length)) != -1) {     
        writer.write(cbuf, 0, len);     
        writer.flush();     
      }     
      reader.close();     
    }     
    writer.close();     
  }     
    
  /**   
   * 合并二进制文件   
   *    
   * @param fileName 文件名，不包括编号部分   
   * @throws IOException   
   */    
  public static void joinFileBin(String fileName) throws IOException {     
    int index = fileName.lastIndexOf(".");     
    String ext = "";     
    if (index > 0) {     
      ext = fileName.substring(index);     
      fileName = fileName.substring(0, index);     
    }     
    OutputStream writer = new FileOutputStream(fileName + "_join" + ext);     
    int len;     
    byte[] cbuf = new byte[1024 * 100];     
    File file;     
    for (int i = 1; i <= Integer.MAX_VALUE; i++) {     
      file = new File(fileName + "_" + i + ext);     
      if (!file.exists()) {     
        break;     
      }     
      InputStream reader = new FileInputStream(file);     
      while ((len = reader.read(cbuf, 0, cbuf.length)) != -1) {     
        writer.write(cbuf, 0, len);     
        writer.flush();     
      }     
      reader.close();     
    }     
    writer.close();     
  }     
}    