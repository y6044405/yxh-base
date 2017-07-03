package com.tzg.tool.kit.file;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.tzg.tool.kit.asserts.Assert;
import com.tzg.tool.kit.date.DatePattern;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

/**
 * 文件工具类， 扩展apache FileUtils，提供文件读写、复制、移动、文本文件转码、取文件编码，文本文件批量转码，以及生成图片文件缩略图等方法
 */
public class FileUtils extends org.apache.commons.io.FileUtils {

    private static final Logger logger     = LoggerFactory.getLogger(FileUtils.class);
    public static List<String>  ignoreList = new ArrayList<String>();
    public static String[]      images     = null;

    private final static int IMAGE_SIZE = 120;
    public static final long KB         = 1024;
    public static final long MB         = 1048576;
    public static final long GB         = 1073741824;
    public static final long TB         = 1099511627776L;

    private static CodepageDetectorProxy detector              = null;
    private static CodepageDetectorProxy fastDtector           = null;
    private static ParsingDetector       parsingDetector       = new ParsingDetector(false);
    private static ByteOrderMarkDetector byteOrderMarkDetector = new ByteOrderMarkDetector();

    //default strategy use fastDtector
    private static final boolean DEFALUT_DETECT_STRATEGY = true;

    private static final int MAX_READBYTE_FAST = 8;

    static {
        ignoreList.add(".svn");
        ignoreList.add("CVS");
        ignoreList.add(".cvsignore");
        ignoreList.add(".copyarea.db"); // ClearCase
        ignoreList.add("SCCS");
        ignoreList.add("vssver.scc");
        ignoreList.add(".DS_Store");
        ignoreList.add(".git");
        ignoreList.add(".gitignore");
        images = new String[] { FileType.JPG.toString(), FileType.JPEG.toString(), FileType.BMP.toString(), FileType.GIF.toString(), FileType.PNG.toString() };
    }

    public FileUtils() {
    }

    /**
     * 获取目录下的有序文件
     * 
     * @param dir
     *            目录
     * @return
     */
    public static List<File> getSortFiles(File dir) {
        List<File> files = FileUtils.getFiles(dir);
        if (files.isEmpty()) {
            return files;
        }
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().length() - o2.getName().length();
            }
        });
        return files;
    }

    /**
     * 读取二进制文件内容
     * 
     * @param filepath
     *            文件绝对或相对路径，支持jar包中的文件读取
     * @return
     * @throws Exception
     */
    public static byte[] readFile(String filepath) throws Exception {
        Assert.notNull(filepath);
        return readFile(new File(filepath));
    }

    /**
     * 读取二进制文件
     * 
     * @param in
     *            文件输入流
     * @return
     * @throws IOException
     */
    public static byte[] read(InputStream in) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            int b = -1;
            while ((b = in.read()) != -1) {
                baos.write(b);
            }
            return baos.toByteArray();
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(baos);
        }
    }

    /**
     * 读取二进制文件内容
     * 
     * @param file
     *            文件,支持jar包中的文件读取
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static byte[] readFile(File file) throws IOException {
        InputStream in = getInputStream(file.getPath());
        if (null == in) {
            logger.warn("读取{}文件失败，文件不存在!", file);
            return null;
        }
        return read(in);
    }

    /**
     * 根据文件路径获取输入流
     * 
     * @param path
     *            文件路径,相对或绝对路径,支持jar文件
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream getInputStream(String path) throws FileNotFoundException {
        InputStream in = FileUtils.class.getResourceAsStream(path);
        if (null != in) {
            return in;
        }
        File file = getFile(path);
        if (file.exists()) {
            return new FileInputStream(file);
        }
        path = file.getPath();
        int index = path.indexOf(".jar");
        if (index != -1) {
            return getInputStream(path.substring(index + 5).replaceAll("\\\\", "/"));
        }
        return null;
    }

    /**
     * 创建缩略图
     * 
     * @param srcFile
     *            源文件
     * @param destFile
     *            目标文件
     */
    public static void createPreviewImage(String srcFile, String destFile) {
        createPreviewImage(new File(srcFile), new File(destFile), null);
    }

    /**
     * 创建缩略图
     * 
     * @param srcFile
     *            源文件
     * @param destFile
     *            目标文件
     * @param formatName
     *            文件类型 默认jpeg
     */
    public static void createPreviewImage(File srcFile, File destFile, String formatName) {
        try {
            if (StringUtils.isBlank(formatName)) {
                formatName = "jpeg";
            }
            BufferedImage bis = ImageIO.read(srcFile);
            int w = bis.getWidth();
            int h = bis.getHeight();
            int nw = IMAGE_SIZE;
            int nh = (nw * h) / w;
            if (nh > IMAGE_SIZE) {
                nh = IMAGE_SIZE;
                nw = (nh * w) / h;
            }
            double sx = (double) nw / w;
            double sy = (double) nh / h;
            AffineTransform transform = new AffineTransform();
            transform.setToScale(sx, sy);
            AffineTransformOp ato = new AffineTransformOp(transform, null);
            BufferedImage bid = new BufferedImage(nw, nh, BufferedImage.TYPE_3BYTE_BGR);
            ato.filter(bis, bid);
            ImageIO.write(bid, formatName, destFile);
        } catch (Exception e) {
            logger.error("创建缩略图异常:", e);
            throw new RuntimeException(" Failed in create preview image. Error:  " + e.getMessage());
        }
    }

    /**
     * 是否被忽略的文件
     * 
     * @param file
     *            文件
     * @return
     */
    private static boolean isIgnoreFile(File file) {
        if (null == file) {
            return true;
        }
        for (String name : ignoreList) {
            if (name.equals(file.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建文件 文件目录不存在则自动创建
     * 
     * @return
     */
    public static boolean createNewFile(File file) {
        Assert.notNull(file);
        File parent = file.getParentFile();
        if (null != parent && !parent.exists()) {
            parent.mkdirs();
        }
        try {
            return !file.exists() ? file.createNewFile() : true;
        } catch (IOException e) {
            logger.error("create file exception:", e);
        }
        return false;
    }

    /**
     * 获取文件名称
     * 
     * @param filePath
     *            文件路径:文件路径存在或不存在均可;文件名中带点或不带点均可
     * @return
     */
    public static String getName(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return filePath;
        }
        File file = getFile(filePath);
        if (file != null) {
            return file.getName();
        }
        int endIndex = filePath.length();
        if (filePath.indexOf(".") != -1) {
            endIndex = filePath.lastIndexOf(".");
        }
        return filePath.substring(filePath.lastIndexOf(File.separator), endIndex);
    }

    /**
     * 文件分隔符
     * 
     * @deprecated
     * @param filePath
     *            路径
     * @return
     */
    public static String getFileSeparator(String filePath) {
        String separator = File.separator;
        if (filePath.indexOf("/") != -1) {
            separator = "/";
        }
        return separator;
    }

    /**
     * 文件转码 (文本文件)
     * 
     * @param dirPath
     *            工程路径
     * @param suffix
     *            要转码文件的后缀(.java)
     * @param encoding
     *            转换的编码
     */
    public static void transformEncoding(String dirPath, String suffix, String encoding) {
        List<File> list = getFiles(null, dirPath, suffix, true, true);
        if (null == list || list.isEmpty()) {
            return;
        }
        transformEncoding(encoding, list);
    }

    /**
     * 文本文件转码
     * 
     * @param encoding
     *            目标编码
     * @param list
     *            需转码的文本文件集合
     */
    public static void transformEncoding(String encoding, List<File> list) {
        for (File f : list) {
            String e = getEncoding(f);
            if (StringUtils.isEmpty(encoding)) {
                encoding = e;
            }
            if (e.equals(encoding)) {
                logger.debug("srcEncoding({})==dstEncoding({}) skipped f: {}", e, encoding, f.toString());
                continue;
            }
            StringBuilder content = readContent(f, e);
            logger.info("convert file encoding from {} to {}:{}", e, encoding, f);
            write(f, content, encoding);
        }
    }

    /**
     * 取文件的编码格式 异常时默认UTF-8
     * 
     * @param file
     * @return
     */
    public static String getEncoding(File file) {
        String fileCharacterEnding = "UTF-8";
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        detector.add(JChardetFacade.getInstance());
        try {
            Charset charset = detector.detectCodepage(file.toURI().toURL());
            if (charset != null)
                fileCharacterEnding = charset.name();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return fileCharacterEnding;
    }

    /**
     * <p>
     * 获取流编码,不保证完全正确，设置检测策略isFast为true为快速检测策略，false为正常检测
     * InputStream 支持mark,则会在检测后调用reset，外部可重新使用。
     * InputStream 流没有关闭。
     * </p>
     * 
     * 
     * <p>
     *  采用正常检测，读取指定字节数，如果没有指定，默认读取全部字节检测，依次采用的{@link byteOrderMarkDetector}，{@link parsingDetector}，{@link JChardetFacade}， {@link ASCIIDetector}检测。
     *  字节越多检测时间越长，正确率较高。
     * </p>
     *
     * @param in 输入流  isFast 是否采用快速检测编码方式
     * @return Charset The character are now - hopefully - correct。如果为null，没有检测出来。
     * @throws IOException 
     */
    public static Charset getEncoding(InputStream buffIn, boolean isFast) throws IOException {
        return getEncoding(buffIn, buffIn.available(), isFast);
    }

    /**
     *<p>
     *采用快速检测编码方式,最多会扫描8个字节，依次采用的{@link UnicodeDetector}，{@link byteOrderMarkDetector}，
     * {@link JChardetFacade}， {@link ASCIIDetector}检测。对于一些标准的unicode编码，适合这个方式或者对耗时敏感的。
     *</p>
     * @author:  heyiwu 
     * @param buffIn
     * @return
     * @throws IOException
     */
    public static Charset getFastEncoding(InputStream buffIn) throws IOException {
        return getEncoding(buffIn, MAX_READBYTE_FAST, DEFALUT_DETECT_STRATEGY);
    }

    public static Charset getEncoding(InputStream in, int size, boolean isFast) throws IOException {
        try {
            java.nio.charset.Charset charset = null;
            int tmpSize = in.available();
            size = size > tmpSize ? tmpSize : size;
            //if in support mark method, 
            if (in.markSupported()) {
                if (isFast) {
                    size = size > MAX_READBYTE_FAST ? MAX_READBYTE_FAST : size;
                    in.mark(size++);
                    charset = getFastDetector().detectCodepage(in, size);
                } else {
                    in.mark(size++);
                    charset = getDetector().detectCodepage(in, size);
                }
                in.reset();
            } else {
                if (isFast) {
                    size = size > MAX_READBYTE_FAST ? MAX_READBYTE_FAST : size;
                    charset = getFastDetector().detectCodepage(in, size);
                } else {
                    charset = getDetector().detectCodepage(in, size);
                }
            }

            return charset;
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    public static Charset getEncoding(byte[] byteArr, boolean isFast) throws IOException {
        return getEncoding(byteArr, byteArr.length, isFast);
    }

    public static Charset getFastEncoding(byte[] byteArr) throws IOException {
        return getEncoding(byteArr, MAX_READBYTE_FAST, DEFALUT_DETECT_STRATEGY);
    }

    public static Charset getEncoding(byte[] byteArr, int size, boolean isFast) throws IOException {
        size = byteArr.length > size ? size : byteArr.length;
        if (isFast) {
            size = size > MAX_READBYTE_FAST ? MAX_READBYTE_FAST : size;
        }
        ByteArrayInputStream byteArrIn = new ByteArrayInputStream(byteArr, 0, size);
        BufferedInputStream in = new BufferedInputStream(byteArrIn);

        try {
            Charset charset = null;
            if (isFast) {
                charset = getFastDetector().detectCodepage(in, size);
            } else {
                charset = getDetector().detectCodepage(in, size);
            }

            return charset;
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    private static CodepageDetectorProxy getDetector() {
        if (detector == null) {
            detector = CodepageDetectorProxy.getInstance();
            // Add the implementations of info.monitorenter.cpdetector.io.ICodepageDetector: 
            // This one is quick if we deal with unicode codepages:
            detector.add(byteOrderMarkDetector);
            // The first instance delegated to tries to detect the meta charset attribut in html pages.
            detector.add(parsingDetector);
            // This one does the tricks of exclusion and frequency detection, if first implementation is 
            // unsuccessful:
            detector.add(JChardetFacade.getInstance());
            detector.add(ASCIIDetector.getInstance());
        }

        return detector;
    }

    private static CodepageDetectorProxy getFastDetector() {
        if (fastDtector == null) {
            fastDtector = CodepageDetectorProxy.getInstance();
            fastDtector.add(UnicodeDetector.getInstance());
            fastDtector.add(byteOrderMarkDetector);
            fastDtector.add(JChardetFacade.getInstance());
            fastDtector.add(ASCIIDetector.getInstance());
        }
        return fastDtector;
    }

    /**
     * 获取目录下的文件
     * 
     * @param file
     *            目录,此目录必须存在
     * @return 目录不存在时返回null
     */
    public static List<File> getFiles(File file) {
        return getFiles(file, null);
    }

    /**
     * 获取指定路径及子目录下的所有文件,忽略隐藏文件
     * 
     * @param dirPath
     *            指定的目录
     * @return
     */
    public static List<File> getFiles(String dirPath) {
        return getFiles(null, dirPath, null, true, true);
    }

    /**
     * 获取指定路径及子目录下的所有文件,忽略隐藏文件
     * 
     * @param dirPath
     *            指定的目录
     * @param suffix
     *            后缀
     * @return
     */
    public static List<File> getFiles(File file, String suffix) {
        if (!exists(file)) {
            return null;
        }
        return getFiles(null, file.getPath(), suffix, true, true);
    }

    /**
     * 获取指定路径及子目录下的所有文件,忽略隐藏文件
     * 
     * @param dirPath
     *            指定的目录
     * @param suffix
     *            文件后缀
     * @return <br>
     *         -----------------------------------------------------<br>
     * @author flotage
     * @create 2014-12-4 下午01:48:12
     * @note
     */
    public static List<File> getFiles(String dirPath, String suffix) {
        return getFiles(null, dirPath, suffix, true, true);
    }

    /**
     * 获取指定路径下的特定类型的文件集
     * 
     * @param list
     *            文件集合
     * @param dirPath
     *            文件目录
     * @param suffix
     *            文件后缀
     * @param ignore
     *            是否忽略隐藏文件
     * @param sort
     *            是否排序
     * @return
     */
    public static List<File> getFiles(List<File> list, String dirPath, String suffix, boolean ignore, boolean sort) {
        if (null == list)
            list = new ArrayList<File>();
        File file = getFile(dirPath);
        if (!exists(file)) {
            logger.warn("dir:{} is not exist", file);
            return list;
        }
        if (file.isFile()) {
            list.add(file);
            return list;
        }
        File[] files = file.listFiles();
        if (null == files || files.length == 0) {
            return list;
        }
        for (File f : files) {
            if (ignore && isIgnoreFile(f)) {
                continue;
            }
            if (f.isFile()) {
                if ((StringUtils.isBlank(suffix) || f.getName().endsWith(suffix))) {
                    list.add(f);
                }
                continue;
            }
            getFiles(list, f.getPath(), suffix, ignore, sort);
        }

        if (sort) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File o1, File o2) {
                    return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
                }
            });
        }
        return list;
    }

    /**
     * 读取文本文件内容
     * 
     * @param f
     *            文件
     * @param encoding
     *            编码,可为空
     * @return
     */
    public static StringBuilder readContent(File f, String encoding) {
        return readContent(f, encoding, null);
    }

    /**
     * 以行读取的方式,读取文本文件内容
     * 
     * @param file
     *            文本文件
     * @param encoding
     *            文件编码，可为空,为空时读取文件的编码
     * @param enter
     *            换行符,可为空，为空时获取系统的换行符
     * @return
     */
    public static StringBuilder readContent(File file, String encoding, String enter) {
        Assert.notNull(file);
        if (StringUtils.isEmpty(enter)) {
            enter = System.getProperty("line.separator");
        }
        if (StringUtils.isEmpty(encoding)) {
            encoding = getEncoding(file);
        }
        StringBuilder builder = new StringBuilder();
        InputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = getInputStream(file.getPath());
            isr = new InputStreamReader(fis, encoding);
            br = new BufferedReader(isr);
            String data = null;
            while ((data = br.readLine()) != null) {
                builder.append(data + enter);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
                if (null != isr) {
                    isr.close();
                }
                if (null != fis) {
                    fis.close();
                }
            } catch (Exception e) {
            }
        }
        return builder;
    }

    /**
     * 写入文本文件内容
     * 
     * @param f
     * @param content
     * @param encoding
     */
    private static void write(File f, StringBuilder content, String encoding) {
        write(f, content.toString(), encoding);
    }

    /**
     * 根据后缀名判断是否是图片文件 Java SE 6中的ImageIO默认支持JPG，BMP，PNG，GIF等格式
     * 根据后缀名判断不严谨(任意类型的文件更改后缀名),推荐根据文件流判断：@see FileUtils.isImage(File file)
     * 
     * @param ext
     *            后缀名
     * @return
     */
    @Deprecated
    public static boolean isImage(String ext) {
        if (StringUtils.isBlank(ext)) {
            return false;
        }
        String s = ext.toUpperCase();
        return ArrayUtils.contains(images, s);
    }

    /**
     * 取文件后缀名(带.) ,多后缀名取最后一个
     * 
     * @return
     */
    public static String getFileExt(File file) {
        if (file.isFile()) {
            return getFileExt(file.getName());
        }
        return "";
    }

    public static String getFileExt(String name) {
        int index = name.indexOf(".");
        if (index == -1) {
            return "." + name;
        }
        return name.substring(name.lastIndexOf("."), name.length());
    }

    /**
     * 取文件后缀名(不带点) ,多后缀名取最后一个 FilenameUtils.getExtension(name)
     * 
     * @param file
     * @return
     */
    public static String getFileSuffix(File file) {
        String name = getFileExt(file);
        if (name.startsWith(".")) {
            return name.substring(1);
        }
        return name;
    }

    /**
     * 文件最后修改时间
     * 
     * @param file
     * @return
     */
    public static String getLastModified(File file) {
        return getLastModified(file, DatePattern.YYYY_MM_DD_HH_MM_SS.getValue());
    }

    /**
     * 获取目录下的文件数
     * 
     * @param file
     *            目录
     * @return
     */
    public static int getFileNumber(File file) {
        List<File> list = getFiles(file);
        if (null == list || list.isEmpty()) {
            return 0;
        }
        return list.size();
    }

    /**
     * 获取文件最后修改时间
     * 
     * @param file
     * @param pattern
     *            时间格式,不能为空
     * @return
     */
    public static String getLastModified(File file, String pattern) {
        return new SimpleDateFormat(pattern).format(new Date(file.lastModified()));
    }

    /**
     * 读取文本文件
     * 
     * @param file
     * @return
     */
    public static String readLine(File file) {
        return readContent(file, null).toString();
    }

    /**
     * 写二进制文件
     * 
     * @param savePath
     * @param buf
     * @throws Exception
     */
    public static void saveFile(String filePath, byte[] buf) throws Exception {
        Assert.notNull(filePath);
        saveFile(new File(filePath), buf);
    }

    /**
     * 写二进制文件
     * 
     * @param savePath
     * @param buf
     * @throws Exception
     */
    public static void saveFile(File file, byte[] buf) throws Exception {
        FileOutputStream fos = null;
        try {
            createNewFile(file);
            fos = new FileOutputStream(file);
            fos.write(buf);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }

    }

    /**
     * 写文本文件
     * 
     * @param file
     * @param content
     * @throws IOException
     */
    public static void write(File file, String content) {
        write(file, content, "");
    }

    /**
     * 写文件,文件内容不能为空
     * 
     * @param file
     *            文件
     * @param content
     *            内容
     * @param charset
     *            字符集
     * @throws IOException
     */
    public static void write(File file, String content, String charset) {
        write(file, content, charset, false);
    }

    /**
     * 写文件,文件内容不能为空
     * 
     * @param file
     *            文件
     * @param content
     *            内容
     * @param charset
     *            字符集
     * @throws IOException
     */
    public static void write(File file, String content, String charset, boolean append) {
        Assert.notNull(file);
        Assert.notNull(content);
        File parentFile = file.getParentFile();
        if (null != parentFile && !parentFile.exists()) {
            parentFile.mkdirs();
        }
        FileOutputStream fos = null;
        OutputStreamWriter writer = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            if (StringUtils.isBlank(charset)) {
                charset = getEncoding(file);
            }
            fos = new FileOutputStream(file, append);
            writer = new OutputStreamWriter(fos, charset);
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            logger.error("写文件IO异常:", e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (null != writer) {
                    writer.close();
                }
            } catch (IOException e) {
                //
            }
        }
    }

    /**
     * jar中的指定文件解压到指定路径下
     * 
     * @param jar
     *            ： jar文件路径
     * @param list
     *            ：查找到的文件,调用getJarEntries(String jar, String dir, String name)
     * @param path
     *            : 写入文件的路径
     */
    public static List<File> write(String jar, List<JarEntry> list, String path) {
        if (null == list || list.isEmpty()) {
            return null;
        }
        JarFile jarFile = getJarFile(jar);
        List<File> files = new ArrayList<File>(list.size());
        for (JarEntry jarEntry : list) {
            files.add(write(jarFile, jarEntry, path));
        }
        return files;
    }

    /**
     * 解压指定文件到指定路径
     * 
     * @param jarFile
     * @param jarEntry
     * @param path
     * @return
     */
    public static File write(JarFile jarFile, JarEntry jarEntry, String path) {
        try {
            if (!path.endsWith(jarEntry.getName())) {
                path += jarEntry.getName();
            }
            InputStream is = jarFile.getInputStream(jarEntry);
            return writeBytes(path, toByte(is));
        } catch (IOException e) {
            logger.error("IOException:{}", e);
        }
        return null;
    }

    /**
     * 查找/获取jar中的文件
     * 
     * @param jar
     *            ：jar文件路径,eg:/xxx/.../xx.jar
     * @param dir
     *            : jar文件中的目录,eg:META-INF
     * @param name
     *            :jar文件目录下的文件名或后缀,eg:MANIFEST.MF or .xml
     * @return
     */
    public static List<JarEntry> getJarEntries(String jar, String dir, String name) {
        List<JarEntry> list = new ArrayList<JarEntry>();

        try {
            if (StringUtils.isBlank(jar)) {
                return list;
            }
            JarFile jarFile = getJarFile(jar);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith(dir) && entry.getName().endsWith(name)) {
                    list.add(entry);
                }
            }
        } catch (Exception e) {
            logger.error("getJarEntries:jar:file:{}!{}/{} ,exception:{}", jar, dir, name, e);
        }
        return list;
    }

    private static JarFile getJarFile(String jar) {
        if (!jar.startsWith("jar")) {
            jar = "jar:file:" + jar;
        }
        if (!jar.endsWith("!/")) {
            jar += "!/";
        }
        try {
            URL url = new URL(jar);
            JarURLConnection con = (JarURLConnection) url.openConnection();
            return con.getJarFile();
        } catch (MalformedURLException e) {
            logger.error("getJarFile:{},MalformedURLException:{}", jar, e);
        } catch (IOException e) {
            logger.error("getJarFile:{},IOException:{}", jar, e);
        }
        return null;
    }

    /**
     * 获取文件输入流
     * 
     * @param path
     *            文件或文件夹路径
     * @param suffix
     *            path为文件夹时,文件的后缀,path为文件时可为空
     * @return
     * @throws IOException
     */
    public static InputStream getInputStream(String path, String suffix) throws IOException {
        URL url = FileUtils.class.getResource(path);
        if (null == url) {
            File file = getFile(path);
            if (!exists(file)) {
                return null;
            }
            return file.toURI().toURL().openStream();
        }
        return getFileStream(url, suffix);
    }

    /**
     * 获取文件输入流：文件在jar包中时,从jar包中读取jar包中的文件输入流
     * 
     * @param url
     *            文件或jar包文件地址
     * @param suffix
     *            文件的后缀
     * @return 文件输入流
     * @throws IOException
     */
    private static InputStream getFileStream(URL url, String suffix) throws IOException {
        if ("jar".equals(url.getProtocol())) {
            JarURLConnection con = (JarURLConnection) url.openConnection();
            JarEntry entry = con.getJarEntry();
            JarFile jarfile = con.getJarFile();
            return jarfile.getInputStream(getJarEntry(entry, jarfile, suffix));
        }
        File file = new File(url.getFile());
        if (file.isFile()) {
            return file.toURI().toURL().openStream();
        }
        File[] files = file.listFiles();
        if (null == files) {
            return null;
        }
        for (File f : files) {
            if (f.getName().endsWith(suffix)) {
                file = f;
                break;
            }
        }
        return file.toURI().toURL().openStream();
    }

    /**
     * 从jar包中获取文件对象
     * 
     * @param entry
     *            jar包中的目录或文件
     * @param jarfile
     *            jar包
     * @param suffix
     *            文件后缀
     * @return
     */
    private static JarEntry getJarEntry(JarEntry entry, JarFile jarfile, String suffix) {
        if (!entry.isDirectory()) {
            return entry;
        }
        Enumeration<JarEntry> enums = jarfile.entries();
        while (enums.hasMoreElements()) {
            JarEntry jarEntry = enums.nextElement();
            String name = jarEntry.getName();
            if (name.startsWith(entry.getName()) && (null != suffix && name.endsWith(suffix))) {
                entry = jarEntry;
                break;
            }
        }
        return entry;
    }

    /**
     * 获取输入流
     * 
     * @param resourcesPath
     *            相对classpath的路径
     * @param file
     *            resourcesPath不为空时,可为空
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream getInputStream(String resourcesPath, File file) throws FileNotFoundException {
        if (StringUtils.isBlank(resourcesPath)) {
            return new FileInputStream(file);
        }
        InputStream is = getInputStream(resourcesPath);
        if (null == is) {
            is = new FileInputStream(file);
        }
        return is;
    }

    /**
     * 1、获取文件或目录,支持相对路径和绝对路径（优先） 2、不判断文件或目录是否存在
     * 
     * @param path
     *            路径（相对或绝对路径）
     * @return
     */
    public static File getFile(String path) {
        Assert.notNull(path);
        File file = new File(path);
        if (file.exists()) {
            return file;
        }
        URL url = FileUtils.class.getResource(path);
        if (null != url) {
            return new File(url.getPath());
        }
        File relativeFile = getRelativeFile(path);
        if (relativeFile.exists()) {
            return relativeFile;
        }
        return file;
    }

    /**
     * 相对路径的文件(classpath下)
     * 
     * @param name
     * @return
     */
    public static File getRelativeFile(String name) {
        return getRelativeFile(null, name);
    }

    /**
     * 相对路径的文件(classpath下)
     * 
     * @param name
     * @return
     */
    public static File getRelativeFile(String dir, String name) {
        String relativePath = getRelativeDir(dir);
        return new File(relativePath, name);
    }

    /**
     * 相对目录(classpath下)
     * 
     * @param dir
     * @return
     */
    public static String getRelativeDir(String dir) {
        String relativePath = FileUtils.class.getResource("/").getPath();
        if (StringUtils.isNotBlank(dir)) {
            relativePath += dir;
        }
        return relativePath;
    }

    /**
     * 在当前classpath下创建目录
     * 
     * @param dir
     */
    public static void createRelativeDir(String dir) {
        File file = getRelativeFile(dir);
        if (!file.exists())
            file.mkdirs();
    }

    /**
     * 文件是否存在
     * 
     * @param file
     * @return 不存在为true
     */
    public static boolean notExists(File file) {
        return !exists(file);
    }

    /**
     * 文件是否存在
     * 
     * @param file
     * @return 存在为true
     */
    public static boolean exists(File file) {
        boolean exist = (null != file && file.exists());
        if (exist) {
            return exist;
        }
        return false;
    }

    /**
     * 判断文件是否可读写,不可读写时设置为可读写
     * 
     * @param file
     * @return
     */
    public static boolean canReadWrite(File file) {
        if (exists(file)) {
            return canReadWrite(file, true);
        }
        return false;
    }

    /**
     * 判断文件是否可读写,
     * 
     * @param file
     * @param able
     *            不可读写时是否设置为可读写
     * @return
     */
    private static boolean canReadWrite(File file, boolean able) {
        boolean flag = file.canRead() && file.canWrite();
        if (flag) {
            file.setReadable(able);
            file.setWritable(able);
        }
        return file.canRead() && file.canWrite();
    }

    /**
     * 简易方式判断文件或目录是否为空
     * 文件或目录不存在为空
     * 目录下为空文件也算空
     * @author:  heyiwu 
     * @param file
     * @return
     */
    public static boolean isEmpty(File file) {
        if (!exists(file)) {
            return true;
        }
        return (file.isFile() ? file.length() == 0 : FileUtils.sizeOfDirectoryAsBigInteger(file).intValue() == 0);
    }

    /**
     * 简易方式判断文件或目录是否不为空
     * @param file
     * @return
     */
    public static boolean isNotEmpty(File file) {
        return !isEmpty(file);
    }

    /**
     * 取得文件大小
     * 
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSize(File f) {
        long s = -1;
        if (!exists(f)) {
            return s;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            s = fis.available();
        } catch (Exception e) {
            logger.error("获取文件大小异常:", e);
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
        return s;
    }

    /**
     * 格式化/转换文件大小 精准大小(以1024为单位),与实际显示(以1000为单位)的大小有出入 支持的单位： Byte字节简写B KB是千字节
     * 2的10次方 MB是兆 2的20次方 GB是吉字节即千兆 2的30次方 大数据单位: TB是太字节 PB EB 还有ZB、YB
     * 、NB、DB,一般不常用
     * 
     * @param size
     * @return
     */
    public static String getFileSize(long size) {
        if (size <= 0) {
            logger.warn("文件大小小于等于0");
            return String.valueOf(size);
        }
        if (size < KB) {
            // return new DecimalFormat("#.00").format((double) size) + "B";
            return String.format("%d B", (int) size);
        }
        if (size < MB) {
            // return new DecimalFormat("#.00").format((double) size / KB) +
            // "K";
            return String.format("%.2f KB", (float) size / KB);
        }
        if (size < GB) {
            return String.format("%.2f MB", (float) size / MB);
        }
        if (size < TB) {
            return String.format("%.2f GB", (float) size / GB);
        }
        return byteCountToDisplaySize(size);
    }

    /*
     * includes units - EB, PB, TB, GB, MB, KB or bytes
     */
    public static String byteCountToDisplaySize(File file) {
        return FileUtils.byteCountToDisplaySize(file.length());

    }

    /**
     * 拷贝文件 : 读取大文件
     * 
     * @param src
     *            :源文件
     * @param dest
     *            ：目标文件或文件夹(和源文件夹同路径时忽略)
     */
    public static void copyFile(String src, String dest) {
        if (StringUtils.isBlank(src) || StringUtils.isBlank(dest)) {
            logger.error("拷贝文件输入参数不合法:src=[{}],dest=[{}]", src, dest);
            return;
        }
        try {
            File srcFile = new File(src);
            if (!exists(srcFile)) {
                logger.error("拷贝文件输入参数不合法,源文件不存在:srcFile=[{}]", srcFile);
                return;
            }
            File destFile = new File(dest);
            if (destFile.isDirectory()) {
                if (destFile.getPath().equals(srcFile.getPath())) {
                    logger.warn("拷贝文件操作忽略：目标路径和源路径相同");
                    return;
                }
                destFile = new File(destFile.getPath(), srcFile.getName());
            }
            File dir = destFile.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            copyFile(srcFile, destFile);
            /*
             * FileInputStream fin = new FileInputStream(inFile);
             * FileOutputStream fout = new FileOutputStream(destFile);
             * FileChannel fcin = fin.getChannel(); FileChannel fcout =
             * fout.getChannel(); ByteBuffer buffer = ByteBuffer.allocate(1024);
             * while (true) { // clear方法重设缓冲区，使它可以接受读入的数据 buffer.clear(); if
             * (fcin.read(buffer) == -1) { break; } //
             * flip方法让缓冲区可以将新读入的数据写入另一个通道 buffer.flip(); // 从输出通道中将数据写入缓冲区
             * fcout.write(buffer); }
             */
        } catch (Exception e) {
            logger.error("拷贝文件异常:{}", e.getLocalizedMessage());
        }
    }

    /**
     * 读取文件
     */
    public static byte[] readBytes(File file) {
        try {
            return readFile(file);
        } catch (IOException e) {
            logger.error("read file=[{}]", file, e.getLocalizedMessage());
        }
        return new byte[(int) file.length()];
    }

    public static void write(InputStream input, FileOutputStream output) {
        try {
            byte b[] = new byte[1024];
            int j = 0;
            while ((j = input.read(b)) != -1) {
                System.out.print(".");
                output.write(b, 0, j);
            }
            output.flush();
        } catch (Exception e) {
            logger.error("{}", e.getClass(), e);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }

    /**
     * 字节数组写入文件
     */
    public static File writeBytes(String path, byte[] bytes) {
        try {
            File file = new File(path);
            writeByteArrayToFile(file, bytes);
            return file;
        } catch (IOException e) {
            logger.error("write file=[{}], bytes array IO exception:{}", path, e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * 将对象写入文件
     * 
     * @param path
     * @param object
     * @return
     */
    public static File writeObject(String path, Object object) {
        File file = new File(path);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(object);
        } catch (FileNotFoundException e) {
            logger.error("file:{} not found:{}", path, e.getLocalizedMessage());
        } catch (IOException e) {
            logger.error("write file:{} io exception:{}", path, e.getLocalizedMessage());
        } finally {
            IOUtils.closeQuietly(oos);
        }
        return file;
    }

    /**
     * 将指定的对象写入指定的文件
     * 
     * @param file
     *            指定写入的文件
     * @param objs
     *            要写入的对象
     */
    public static void writeObjectArray(String file, Object[] objs) {
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            for (int i = 0; i < objs.length; i++) {
                oos.writeObject(objs[i]);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        } finally {
            IOUtils.closeQuietly(oos);
        }
    }

    public static Object readObject(File file) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(file));
            return in.readObject();
        } catch (FileNotFoundException e) {
            logger.info("read object file not found:{}", e.getLocalizedMessage());
        } catch (IOException e) {
            logger.info("read object IO exception:{}", e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            logger.info("class not found:{}", e.getLocalizedMessage());
        } finally {
            IOUtils.closeQuietly(in);
        }
        return null;
    }

    /**
     * 返回在文件中指定位置的对象
     * 
     * @param file
     *            指定的文件
     * @param i
     *            从1开始
     * @return
     */
    public static Object readObjectArray(String file, int i) {
        ObjectInputStream ois = null;
        Object obj = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            for (int j = 0; j < i; j++) {
                obj = ois.readObject();
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        } finally {
            try {
                if (null != ois)
                    ois.close();
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            }
        }
        return obj;
    }

    /**
     * 检测目录或文件的父目录是否存在，不存在则创建目录
     * 
     * @param dir
     *            目录或文件
     */
    public static void checkAndCreateDir(File dir) {
        if (dir.isFile()) {
            dir = dir.getParentFile();
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 数组转输入流
     * 
     * @param buf
     * @return
     */
    public static final InputStream toStream(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    /**
     * 输入流转数组
     * 
     * @param is
     *            输入流
     * @return
     * @throws IOException
     */
    public static final byte[] toByte(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = is.read(buff, 0, 100)) > 0) {
            baos.write(buff, 0, rc);
        }
        return baos.toByteArray();
    }

    /**
     * 获取图片文件实际类型
     * 
     * @param File
     *            图片文件，检测数据流不能是伪图片文件(其他类型的文件重命名后缀)
     * @return 文件类型
     */
    public final static String getImageFileType(File f) {
        if (!isImage(f)) {
            return null;
        }
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(f);
            Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
            if (!iter.hasNext()) {
                return null;
            }
            ImageReader reader = iter.next();
            iis.close();
            return reader.getFormatName();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取文件类型,包括图片,若格式不是已知(枚举FileType)的格式,则返回null
     * 
     * @param file
     * @return 文件类型
     */
    public final static FileType getFileType(File file) {
        FileType fileType = null;
        if (!file.exists()) {
            return fileType;
        }
        try {
            return getFileType(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.error("{} file not found exception:{}", file, e.getLocalizedMessage());
        }
        return null;
    }

    public final static FileType getFileType(InputStream input) {
        if (null == input) {
            return null;
        }
        try {
            //28 50
            byte[] bytes = new byte[10];
            input.read(bytes, 0, 10);
            return getFileTypeByStream(bytes);
        } catch (Exception e) {
        } finally {
            IOUtils.closeQuietly(input);
        }
        return null;
    }

    /**
     * 根据文件流信息获取文件类型,包括图片,若格式不是已知(枚举FileType)的格式,则返回null
     * 
     * @param bytes
     * @return
     */
    public final static FileType getFileTypeByStream(byte[] bytes) {
        String hex = String.valueOf(toHexStr(bytes));
        for (FileType fileType : FileType.values()) {
            if (hex.toUpperCase().startsWith(fileType.getValue())) {
                return fileType;
            }
        }
        return null;
    }

    /**
     * 判断文件是否为图片
     * 
     * @param file
     *            文件
     * @return true 是 | false 否,空的图片文件返回false
     */
    public static final boolean isImage(File file) {
        try {
            BufferedImage bufreader = ImageIO.read(file);
            return bufreader.getWidth() != 0 && bufreader.getHeight() != 0;
        } catch (Exception e) {
        }
        return false;
    }

    public static final boolean isImage(InputStream input) {
        try {
            BufferedImage bufreader = ImageIO.read(input);
            return bufreader.getWidth() != 0 && bufreader.getHeight() != 0;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 文件头转换十六进制字符串
     * 
     * @param bytes
     * @return
     */
    public final static String toHexStr(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static boolean contains(File file, String s, String charset) {
        FileInputStream fis = null;
        try {
            CharsetDecoder decoder = Charset.forName(charset).newDecoder();
            fis = new FileInputStream(file);
            FileChannel channel = fis.getChannel();
            MappedByteBuffer bytebuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            return String.valueOf(decoder.decode(bytebuffer)).indexOf(s) >= 0;
        } catch (Exception e) {
            logger.error("判断文件：{} 内容是否存在:{},发生异常:{}", file, s, e.getLocalizedMessage());
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return false;
    }

    /**
     * 获取根目录
     * 
     * @param file
     *            文件或目录
     * @return
     */
    public static File getRoot(File file) {
        File f = file;
        if (!file.exists()) {
            logger.warn("[{}] file is not exists", f);
            return f;
        }
        while (file.getParentFile() != null) {
            f = file.getParentFile();
        }
        return f;
    }

    public static HashCode hash(File file) {
        try {
            return Files.hash(file, Hashing.md5());
        } catch (IOException e) {
            logger.error("get file :{} hash {}",file,e.getClass(),e);;
        }
        return null;
    }

}
