package com.tzg.tool.kit.test.file.cut;


import java.io.*;  
public class DividedFile {  
    public static void main(String[] args){  
        try{  
            readFile("36.avi");  
        }catch(IOException e){}  
    }  
    public static void writeFile(byte[] buf,String fileDestination) throws IOException    
    {    
        FileOutputStream fos = new FileOutputStream(fileDestination,true);   
        fos.write(buf);  
        fos.close();  
    }    
    //读取文件  
     public static void readFile(String fileSource) throws IOException{  
         FileInputStream fis = new FileInputStream(fileSource);  
         //获取后缀  
         String prefix=fileSource.substring(fileSource.lastIndexOf(".")+1);  
         //String extension = fileSource.substring(fileSource.lastIndexOf('/'),fileSource.indexOf("."));  
         //String trueName = extension.replace("/", "");  
         String trueName=fileSource.substring(0,fileSource.lastIndexOf("."));  
         int fileDivi = 5632*200;  
         System.out.println(trueName);  
         //String trueName = "TextFile";   
         //available()方法可以知道文件具体有多少个字节。   
         int filelen = fis.available();  
         fis.close();  
         System.out.println(filelen);  
         int fl = filelen / fileDivi;  
         byte[][] buf = new byte[fl+1][fileDivi];  
         for(int i = 0; i < fl; i++){  
             //System.out.println(i+":"+buf[i]);  
             FileInputStream fis2 = new FileInputStream(fileSource);  
             fis2.skip(i*fileDivi);  
             fis2.read(buf[i],0,fileDivi);  
            //System.out.println("写入前"+i);  
             if(i < 10){  
                 writeFile(buf[i],"F:\\java/"+trueName+"devided_00"+i+"."+prefix);  
             }else 
                 if(i >= 10&&i < 100)  
                     writeFile(buf[i],"F:\\java/"+trueName+"devided_0"+i+"."+prefix);  
                 else 
                     writeFile(buf[i],"F:\\java/"+trueName+"devided_"+i+"."+prefix);  
             //System.out.println("写入后"+i);  
             fis2.close();  
         }  
         FileInputStream fis2 = new FileInputStream(fileSource);  
         fis2.skip(fl*fileDivi);  
         fis2.read(buf[fl],0,filelen%fileDivi);  
         if(fl < 10){  
             writeFile(buf[fl],"F:\\java/"+trueName+"devided_00"+fl+"."+prefix);  
         }else 
             if(fl >= 10&&fl < 100)  
                 writeFile(buf[fl],"F:\\java/"+trueName+"devided_0"+fl+"."+prefix);  
             else 
                 writeFile(buf[fl],"F:\\java/"+trueName+"devided_"+fl+"."+prefix);  
         fis2.close();  
        // writeFile(buf[fl],trueName+"devided_"+fl+"."+prefix);  
        // fis.close();  
     } 
}