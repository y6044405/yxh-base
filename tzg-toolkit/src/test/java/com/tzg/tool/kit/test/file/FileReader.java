package com.tzg.tool.kit.test.file;
import java.io.IOException;  
import java.io.InputStream;  
import java.nio.channels.FileChannel;  
import java.util.Arrays;  
  
public class FileReader {  
    public static int SIZE = 1024 * 1024 * 5;  
  
    public static String FILENAME = "homeuser.unl";  
  
    public static long bt;  
  
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
        try {  
              
             bt = System.currentTimeMillis();   
             InputStream is = FileReader.class.getResourceAsStream(FILENAME);   
             readFile(is);  
             System.out.println(System.currentTimeMillis()-bt+":MS");  
               
        } catch (Exception e) {  
              
        }  
    }  
  
    public static void readFile(InputStream is) throws IOException {  
        byte[] buffer = new byte[SIZE];  
        StringBuffer sb = new StringBuffer();  
        int r = 0;  
        int i = 0;  
        while (is.available() > 0) {  
            i++;  
            String partContent = readPartFile(is, buffer, SIZE,  sb);  
            sb = null;  
            //System.out.println(i + "\n" + partContent);  
        }  
        System.out.println("I: " + i);  
    }  
  
    public static String readPartFile(InputStream inputStream, byte[] buffer,  
            int size, StringBuffer sb) {  
        try {  
            InputStream is = inputStream;  
            int r = is.read(buffer);  
            if (r == size) {  
                byte[] other;  
                int count = 0;  
                byte n = (byte) is.read();  
                other = new byte[] { n };  
                sb.append(new String(buffer));  
                sb.append(new String(other));  
                while (n != 10 && n != -1) {  
                    count++;  
                    n = (byte) is.read();  
                    other = new byte[] { n };  
                    sb.append(new String(other));  
                }  
                if (count != 0) {  
                    SIZE += count;  
                }  
                System.out.println("COUNT: " + count);  
            } else {  
                if (null == sb)  
                    sb = new StringBuffer();  
                zjRead(r, sb, buffer);  
            }  
            return sb.toString();  
        } catch (Exception e) {  
              
            return null;  
        }  
    }  
  
    private static void zjRead(int r, StringBuffer sb, byte[] buffer) {  
        byte[] temp = new byte[r];  
        System.arraycopy(buffer, 0, temp, 0, r);  
        sb.append(new String(temp));  
    }  
  
}  
