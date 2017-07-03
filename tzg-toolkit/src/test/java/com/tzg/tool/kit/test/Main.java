package com.tzg.tool.kit.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tzg.tool.kit.file.FileUtils;
import com.tzg.tool.kit.string.StringUtil;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        String dir = "E:/work/console/prod";
        //        mvSpringFile(dir);
        //        mvDbFile(dir);
        //        delLog4jFile(dir);
        //        ruokouling();
        //        imp();
        //        services(dir);
        //        csrf(dir+"/Tzg-wap");
        System.out.println(StringUtil.toCamelCase("tzg-web", '-'));
    }



   

    /**
     * 截取字符串长字，保留HTML格式
     * @param content
     * @param len 字符长度
     * @author jimmy
     */
    public static String truncateHTML(String content, int len) {
        Document dirtyDocument = Jsoup.parse(content);
        Element source = dirtyDocument.body();
        Document clean = Document.createShell(dirtyDocument.baseUri());
        Element dest = clean.body();
        truncateHTML(source, dest, len);
        return dest.outerHtml();
    }

    /**
     * 使用Jsoup预览
     * @param source  需要过滤的
     * @param dest 过滤后的对象
     * @param len 截取字符长度
     * @author jimmy
     *  <br />eg.<br />
     *
     *  Document  dirtyDocument = Jsoup.parse(sb.toString());<br />
        Element source = dirtyDocument.body();<br />
        Document clean = Document.createShell(dirtyDocument.baseUri());<br />
        Element dest = clean.body();<br />
        int len = 6;<br />
        truncateHTML(source,dest,len);<br />
        System.out.println(dest.html());<br />
     */
    private static void truncateHTML(Element source, Element dest, int len) {
        List<Node> sourceChildren = source.childNodes();
        for (Node sourceChild : sourceChildren) {
            if (sourceChild instanceof Element) {
                Element sourceEl = (Element) sourceChild;
                Element destChild = createSafeElement(sourceEl);
                int txt = dest.text().length();
                if (txt >= len) {
                    break;
                } else {
                    len = len - txt;
                }
                dest.appendChild(destChild);
                truncateHTML(sourceEl, destChild, len);
            } else if (sourceChild instanceof TextNode) {
                int destLeng = dest.text().length();
                if (destLeng >= len) {
                    break;
                }
                TextNode sourceText = (TextNode) sourceChild;
                int txtLeng = sourceText.getWholeText().length();
                if ((destLeng + txtLeng) > len) {
                    int tmp = len - destLeng;
                    String txt = sourceText.getWholeText().substring(0, tmp);
                    TextNode destText = new TextNode(txt, sourceChild.baseUri());
                    dest.appendChild(destText);
                    break;
                } else {
                    TextNode destText = new TextNode(sourceText.getWholeText(), sourceChild.baseUri());
                    dest.appendChild(destText);
                }
            }
        }
    }

    private static void csrf(String dir) throws IOException {
        Collection<File> files = FileUtils.listFiles(new File(dir), new FileFileFilter() {
            @Override
            public boolean accept(File file) {
                //查找vm文件
                boolean accept = file.getName().endsWith(".vm");
                if (!accept) {
                    return accept;
                }
                try {
                    //jsoup也可以做vm文件批量修改插入自定义标签的工作(更简单) 但存在语法转义的问题(&&会被转义)
                    Document doc = Jsoup.parse(file, FileUtils.getEncoding(file));
                    //jsoup过滤vm文件中是否含有post的表单
                    Elements elments = doc.select("form[method='post']");
                    return (null != elments && elments.size() >= 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return accept;
            }

            private static final long serialVersionUID = 1L;

        }, TrueFileFilter.TRUE);
        for (File file : files) {
            List<String> lines = FileUtils.readLines(file);
            int index = -1;
            //form表单中是否已经存在自定义标签(#tokenCode)
            boolean has = false;
            //文件是否被修改(插入自定义标签)
            boolean change = false;
            //form标签是否闭合(form标签占多行的情况)
            boolean close = false;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.indexOf("<form") != -1 && line.indexOf("method=\"post\"") != -1) {
                    index = i + 1;
                }
                //是否读取到form表单
                if (index < 0) {
                    continue;
                }
                //form表单是否闭合(form标签占多行)
                if (!close) {
                    close = line.indexOf(">") != -1;
                    index = close ? index : ++index;
                }
                if (!has) {
                    has = line.indexOf("#tokenCode") != -1;
                }
                if (line.indexOf("</form>") != -1) {
                    if (!has) {
                        change = true;
                        System.out.println(index);
                        lines.add(index, "\t\t#tokenCode");
                    }
                    has = false;
                    index = -1;
                }
            }
            if (!change) {
                continue;
            }
            System.out.println("save:" + file);
            FileUtils.writeLines(file, lines);
            /*//打印输出
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                System.out.println(i+":"+line);
            }*/
        }
    }

    private static void services(String dir) throws IOException {
        Collection<File> files = FileUtils.listFiles(new File(dir), new FileFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith("Impl.java");
            }

            private static final long serialVersionUID = 1L;
        }, TrueFileFilter.TRUE);
        for (File file : files) {
            List<String> lines = FileUtils.readLines(file);
            for (String line : lines) {
                if (line.indexOf("@Service") == -1) {
                    continue;
                }
                if (line.indexOf("\"") != -1) {
                    continue;
                }
                System.out.println(file);
                System.out.println(line);
            }

        }
    }

    private static void delLog4jFile(String dir) throws IOException {
        Collection<File> files = FileUtils.listFiles(new File(dir), new FileFileFilter() {
            @Override
            public boolean accept(File file) {
                //                return file.getPath().endsWith("resources\\config\\dbsource.xml");
                return file.getPath().endsWith("\\resources\\config\\log4j.properties");
            }

            private static final long serialVersionUID = 1L;
        }, TrueFileFilter.TRUE);
        for (File file : files) {
            System.out.println("delete file:" + file);
            file.delete();
            File dirFile = file.getParentFile();
            if (FileUtils.isEmpty(dirFile)) {
                System.out.println("delete dir:" + dirFile);
                FileUtils.deleteDirectory(dirFile);
            }
        }
    }

    private static void mvDbFile(String dir) {
        Collection<File> files = FileUtils.listFiles(new File(dir), new FileFileFilter() {
            @Override
            public boolean accept(File file) {
                //                return file.getPath().endsWith("resources\\config\\dbsource.xml");
                return file.getPath().endsWith("\\resources\\spring\\dbsource.xml");
            }

            private static final long serialVersionUID = 1L;
        }, TrueFileFilter.TRUE);
        for (File file : files) {
            /*
            File dest = new File(file.getParentFile().getParent(),"spring\\spring-"+file.getName());
            try {
                System.out.println("copy file:"+file+" to :"+dest);
                FileUtils.copyFile(file, dest);
                System.out.println("delete file:"+file);
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            */}
    }

    /**
     * 移动spring配置文件 从WEB-INF下移动到src/main/resources/spring下
     * @author:  heyiwu 
     * @param dir 
     * @throws IOException
     */
    private static void mvSpringFile(String dir) throws IOException {
        Collection<File> files = FileUtils.listFiles(new File(dir), new NameFileFilter("web.xml"), TrueFileFilter.TRUE);
        String name = "spring-servlet.xml";
        for (File file : files) {
            String content = FileUtils.readFileToString(file);
            File parent = file.getParentFile();
            if (content.indexOf("webAppRootKey") == -1 || new File(parent, name).exists()) {
                System.out.println(file);
            }
            if (!content.contains("<param-value>/WEB-INF/" + name + "</param-value>")) {
                continue;
            }
            File springDir = new File(parent.getParentFile().getParentFile(), "resources\\spring");
            if (!springDir.exists()) {
                springDir.mkdirs();
            }
            try {
                File src = new File(file.getParentFile(), name);
                File dest = new File(springDir, name);
                System.out.println("copy " + src + " to:" + dest);
                FileUtils.copyFile(src, dest);
                System.out.println("delete file:" + src);
                src.delete();
                String s = content.replace("/WEB-INF/" + name, "classpath:/spring/" + name);
                System.out.println("write file:" + file + " :" + s);
                FileUtils.write(file, s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void test4() throws IOException {
        File dir = new File("E:/work/backstage/branch/ChongGou20151130dev/api/src/main/java");
        Collection<File> files = FileUtils.listFiles(dir, new String[] { "java" }, true);
        for (File file : files) {
            LineIterator it = FileUtils.lineIterator(file);
            try {
                while (it.hasNext()) {
                    String line = it.nextLine();
                    if (line.startsWith("package ")) {
                        if (!line.startsWith("package com.tzg.backstage.api.")) {
                            System.out.println(file);
                            FileUtils.writeStringToFile(file, FileUtils.readFileToString(file).replace("package com.tzg.backstage.api", "package com.tzg.backstage.api."));
                        }
                        break;
                    }
                }
            } finally {
                LineIterator.closeQuietly(it);
            }
        }
    }

    private static void test3() {
        String path = "E:/work/tzg/hnsjb/trunk/exchange";
        final String fileName = "pom.xml";
        Collection<File> files = FileUtils.listFiles(new File(path), new NameFileFilter(fileName) {
            @Override
            public boolean accept(File file) {
                return super.accept(file);
            }
        }, new DirectoryFileFilter() {
            @Override
            public boolean accept(File file) {
                if (!file.isDirectory()) {
                    return super.accept(file);
                }
                if (file.getPath().indexOf(".svn") != -1) {
                    return false;
                }
                return new File(file, fileName).exists();
            }

            private static final long serialVersionUID = 1L;
        });
        String tmp = FileUtils.readLine(FileUtils.getFile("/dataFile/denpendency.txt"));
        for (File file : files) {
            System.out.println(file);
            //根据文件的编码去读取内容 不会乱码
            String content = FileUtils.readLine(file);
            List<String> list = StringUtil.find("<artifactId>jsp-api</artifactId>", 0, content);
            if (!list.isEmpty()) {
                continue;
            }
            list = StringUtil.find("", 0, content);
            String s = "</dependencies>";
            if (!StringUtils.contains(content, s)) {
                continue;
            }
            content = content.replace(s, tmp + System.getProperty("line.separator") + s);
            //如打印文件内容乱码说明文件内容原本已经乱码
            System.out.println(content);
            String filePath = file.getPath();
            //备份文件： 原文件名.bak
            file.renameTo(new File(filePath + ".bak"));

            FileUtils.write(new File(filePath), content, "UTF-8");
        }
    }

    private static void test2() throws IOException {
        if (true) {
            String dirPath = "D:/Java/jboss-4.0.5.GA/server/default/deploy";
            Collection<File> files = FileUtils.listFiles(new File(dirPath), new DirectoryFileFilter() {
                @Override
                public boolean accept(File file) {
                    System.out.println(file);
                    if (file.isFile()) {
                        return false;
                    }
                    return file.getPath().endsWith(".war");
                }

                private static final long serialVersionUID = 1L;
            }, TrueFileFilter.TRUE);
            for (File file2 : files) {
                System.out.println(file2);
            }
            return;
        }
        File dir = new File("E:/work/tzg/exchange");
        //指定路径下
        DirectoryFileFilter dirFilter = new DirectoryFileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return false;
                }
                String parentpath = file.getParentFile().getPath();
                boolean flag = parentpath.endsWith("src\\main\\resources\\rmi") || parentpath.endsWith("src\\main\\resources");
                return file.getName().endsWith(".conf.xml") && flag;
            }
        };
        Collection<File> files = com.tzg.tool.kit.file.FileUtils.listFiles(dir, dirFilter, TrueFileFilter.TRUE);
        for (File file : files) {
            System.out.println(file);
            String parentpath = file.getParentFile().getPath();
            System.out.println("move to :");
            String name = "\\client";
            if (parentpath.endsWith("src\\main\\resources")) {
                name = "\\server";
            }
            File newFile = new File(parentpath + name, file.getName());
            System.out.println(newFile);
            FileUtils.moveFile(file, newFile);
            System.out.println();

        }
        //        test();
    }

    private static void test() throws IOException, FileNotFoundException {
        String name = "d:/rhel-server-6.3-x86_64-dvd.iso";
        //        fileReader(name);
        //        changeFile(name, 0, 5);
        int length = 0x8FFFFFF; // 128 Mb  
        /**
             * RandomAccessFile类：支持对随机存取文件的读取和写入。
             * 随机存取文件的行为类似存储在文件系统中的一个大型字节数组。
             *  FileChannel：用于读取、写入、映射和操作文件的通道。
             *  MappedByteBuffer：直接字节缓冲区，其内容是文件的内存映射区域。 
             *      参数部分：FileChannel.MapMode.READ_WRITE（mode） - 根据是按只读、读取/写入或专用（写入时拷贝）来映射文件，分别为 FileChannel.MapMode 类中所定义的 READ_ONLY、READ_WRITE 或 PRIVATE 之一；
            0（position） - 文件中的位置，映射区域从此位置开始；必须为非负数 ；
            length（size） - 要映射的区域大小；必须为非负数且不大于 Integer.MAX_VALUE ,此值设置过大时全部银蛇到内存就没有意义 了
             */

        MappedByteBuffer out = new RandomAccessFile(name, "rw").getChannel().map(FileChannel.MapMode.READ_WRITE, 0, length);
        for (int i = 0; i < length; i++)
            out.put((byte) 'x');
        System.out.println("Finished  writing ");
        for (int i = length / 2; i < length / 2 + 6; i++)
            System.out.print((char) out.get(i)); // read file  
        System.out.println("is ok.....");
    }

    /**
         * JAVA操作大数据量的文件利用FileReader的会把所有的内容加载到内存中，超大文件时会抛出：OutOfMemoryError: Java heap space，因此没有意义。 
         * @param name
         * @throws FileNotFoundException
         * @throws IOException
         */

    private static void fileReader(String name) throws FileNotFoundException, IOException {/*
                                                                                           int c = 0;
                                                                                           StringBuilder s = new StringBuilder();
                                                                                           FileReader fr = new FileReader(name);
                                                                                           while ((c = fr.read()) != -1) {
                                                                                           s.append((char) c);
                                                                                           }
                                                                                           fr.close();
                                                                                           FileWriter fw = new FileWriter("f:/test.iso");
                                                                                           fw.write(s.toString());
                                                                                           fw.close();
                                                                                           */
    }

    /** 
         *  
        如果要使用BIO，建议使用java.io.RandomAccessFile来做，读取部分信息。 
        如果要使用NIO,建议使用java.nio.channels.FileChannel,使用虚拟内存来Mapping大文件。 
        有如下情况下可以用到内存文件映射技术解决问题: 
         1.不要复制文件中所有的数据，只需要修改文件中局部的数据。 
         2.并行\分段处理大文件。 
             * 修改文件中的某一部分的数据测试:将字定位置的字母改为大写 
             * @param fName :要修改的文件名字 
             * @param start:起始字节 
             * @param len:要修改多少个字节 
             * @return :是否修改成功 
             * @throws Exception:文件读写中可能出的错 
        * @author javaFound 
             */

    public static boolean changeFile(String fName, int start, int len) throws Exception {
        //创建一个随机读写文件对象   
        RandomAccessFile raf = new RandomAccessFile(fName, "rw");
        long totalLen = raf.length();
        System.out.println("文件总长字节是: " + totalLen);
        //打开一个文件通道   
        FileChannel channel = raf.getChannel();
        //映射文件中的某一部分数据以读写模式到内存中   
        java.nio.MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, start, len);
        for (int i = 0; i < len; i++) {
            byte src = buffer.get(i);
            buffer.put(i, (byte) (src - 31));//修改Buffer中映射的字节的值   
            System.out.println("被改为大写的原始字节是:" + src);
        }
        //强制输出,在buffer中的改动生效到文件   
        buffer.force();
        buffer.clear();
        channel.close();
        raf.close();
        return true;
    }

    /**
     * 按原Element重建一个新的Element
     * @param sourceEl
     * @return
     * @author jimmy
     */
    private static Element createSafeElement(Element sourceEl) {
        String sourceTag = sourceEl.tagName();
        Attributes destAttrs = new Attributes();
        Element dest = new Element(Tag.valueOf(sourceTag), sourceEl.baseUri(), destAttrs);
        Attributes sourceAttrs = sourceEl.attributes();
        for (Attribute sourceAttr : sourceAttrs) {
            destAttrs.put(sourceAttr);
        }
        return dest;
    }

}
