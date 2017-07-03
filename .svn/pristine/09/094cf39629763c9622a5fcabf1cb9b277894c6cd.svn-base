package com.tzg.tool.kit.test.eclipse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class MavenProject {
    /**
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String path = "E:/work/tzg-ex-core/";
        String path2 = "E:/work/tzg-ex-core-case/";
        String srcDir = "E:/work/template/src/";
        String toDir = "E:/workspace/myapp/src/main/java/";

  /*      List<File> list = getFiles(new File("E:/work/exchange/bsmanager.war/WEB-INF/lib"), null,
            ".jar");
        List<File> list2 = getFiles(new File(
            "D:/Java/jboss-4.0.5.GA/server/default/deploy/bsmanager.war/WEB-INF/lib"), null, ".jar");
        for (File file : list2) {
            File f = containt(list, file); 
            if(f!=null){
                System.out.println(f);
            }
        }*/
                movePackage(srcDir, "com.aaron",toDir,"com.freemarker");

        /*  List<File> list = getFiles(new File(path), null, ".java");
          list=getFiles(new File(path2), list, ".java");
          List<File> files=dupNameFiles(list);
          //删除重名(重复的)的java类
          for(File f:files){
              if(f.getPath().startsWith(path2.replaceAll("/","\\\\"))){
                  f.deleteOnExit();
                  System.out.print("delete:\t\t");
              }
              System.out.println(f);
          }*/
        /* for(String key:map.keySet()){
             System.out.println(key);
         }*/

        //从maven本地库(d:/repo)拷贝jars到指定目录
        path="E:/work/nacecsns";
                copyJars2Lib(path, path + "/src/main/webapp/WEB-INF/lib");
        /*//       从当前目录拷贝jars到maven本地库(d:/repo)
                Map<String, File> map = getClasspathJars(path);
                File batFile = genBatFile(path, list, map, "copyFiles.bat");
                execBat(batFile);*/

        //        eclipseNatures();
    }

    private static File containt(List<File> list, File file) {
        for (File f : list) {
            if (f.getName().equals(file.getName())) {
                return null;
            } 
        }
        return file;
    }

    /**
     * 拷贝srcDir下的fromPackage源码包目录到srcDir下的toPackage源码包目录下(非覆盖模式)
     * @param srcDir
     * @param fromPackage
     * @param toPackage
     * @throws Exception
     */
    private static void copyPackage(String srcDir, String fromPackage, String toDir,
                                    String toPackage) throws Exception {
        movePackage(srcDir, fromPackage, toDir, toPackage, false, false);
    }

    /**
     * 拷贝srcDir下的fromPackage源码包目录到srcDir下的toPackage源码包目录下(非覆盖模式)
     * @param srcDir
     * @param fromPackage
     * @param toPackage
     * @throws Exception
     */
    private static void copyPackage(String srcDir, String fromPackage, String toPackage)
                                                                                        throws Exception {
        movePackage(srcDir, fromPackage, srcDir, toPackage, false, false);
    }

    /**
     * 移动srcDir下的fromPackage源码包目录到srcDir下的toPackage源码包目录下(覆盖模式)
     * @param srcDir
     * @param fromPackage
     * @param toPackage
     * @throws Exception
     */
    private static void movePackage(String srcDir, String fromPackage, String toDir,
                                    String toPackage) throws Exception {
        movePackage(srcDir, fromPackage, toDir, toPackage, true, true);
    }

    /**
     * 移动srcDir下的fromPackage源码包目录到srcDir下的toPackage源码包目录下(覆盖模式)
     * @param srcDir
     * @param fromPackage
     * @param toPackage
     * @throws Exception
     */
    private static void movePackage(String srcDir, String fromPackage, String toPackage)
                                                                                        throws Exception {
        movePackage(srcDir, fromPackage, srcDir, toPackage, true, true);
    }

    private static void movePackage(String srcDir, String fromPackage, String toDir,
                                    String toPackage, boolean override, boolean deleteSrc)
                                                                                          throws Exception {
        if (srcDir == null || "".equals(srcDir)) {
            return;
        }
        srcDir = toPath(srcDir);
        toDir = toPath(toDir);
        if (toDir == null || "".equals(toDir)) {
            toDir = srcDir;
        }
        String sPath = toPath(fromPackage);
        List<File> list = getFiles(new File(srcDir + sPath), null, ".java");
        for (File file : list) {
            StringBuffer sb = getFileContent(fromPackage, toPackage, file);
            String path = file.getPath();
            String s = path.substring(path.indexOf(sPath) + sPath.length());
            File toFile = new File(toPath(toDir + toPackage + s));
            File dir = toFile.getParentFile();
            if (!dir.exists()) {
                System.out.println("mkdir:" + dir);
                dir.mkdirs();
            }
            writeFile(override, sb, toFile);
            if (deleteSrc) {
                boolean deleted = file.delete();
                System.out.println(file.getPath() + " deleted:" + deleted);
            }
        }
    }

    private static void writeFile(boolean override, StringBuffer sb, File newfile)
                                                                                  throws IOException {
        if (override || !newfile.exists()) {
            System.out.println("write file:\t" + newfile);
            System.out.println(sb.toString());
            FileWriter wirter = new FileWriter(newfile);
            wirter.write(sb.toString());
            wirter.flush();
            wirter.close();
        }
    }

    private static StringBuffer getFileContent(String fromPackage, String toPackage, File file)
                                                                                               throws FileNotFoundException,
                                                                                               IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        StringBuffer sb = new StringBuffer();
        String inline;
        while (null != (inline = br.readLine())) {
            if (inline.indexOf(fromPackage) != -1) {
                inline = inline.replaceAll(fromPackage, toPackage);
            }
            sb.append(inline).append("\n");
        }
        return sb;
    }

    private static String toPath(String str) {
        if (str == null || "".equals(str)) {
            return str;
        }
        if (str.indexOf(".") != -1)
            str = str.replaceAll("\\.", "\\\\\\\\");
        if (str.indexOf("/") != -1) {
            str = str.replaceAll("/", "\\\\");
        }
        return str;
    }

    /**
     * 生成bat文件
     * @param path
     * @param list
     * @param map
     * @param bat
     * @return
     * @throws IOException
     */
    private static File genBatFile(String path, List<File> list, Map<String, File> map, String bat)
                                                                                                   throws IOException {
        File file = new File(path + "/" + bat);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (File f : list) {
            if (map.containsKey(f.getName())) {
                String str = "copy  " + f.getPath() + " " + map.get(f.getName()) + " >NULL \n";
                System.out.print(str);
                writer.write(str);
            }
        }
        writer.flush();
        writer.close();
        return file;
    }

    /**
     * 进入bat所在目录 执行bat文件
     * @param batFile
     * @throws IOException
     */
    private static void execBat(File batFile) throws IOException {
        String cmd = "c:\\windows\\system32\\cmd.exe /c netstat -an";
        cmd = "cmd  /c " + batFile;
        System.out.println(cmd);
        Runtime r = Runtime.getRuntime();
        File dir = batFile.getParentFile();
        Process p = r.exec(cmd, null, dir);//
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String inline;
        while (null != (inline = br.readLine())) {
            sb.append(inline).append("\n");
        }
        System.out.println(sb.toString());

        File tmp = new File(dir.getPath() + File.separator + "NULL");
        if (tmp.exists()) {
            tmp.delete();
        }
        batFile.delete();
    }

    /**
     * 从classpath中提取jar文件
     * @param path
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static Map<String, File> getClasspathJars(String path) throws FileNotFoundException,
                                                                  IOException {
        Map<String, File> map = new HashMap<String, File>();
        BufferedReader reader = new BufferedReader(new FileReader(new File(path + "/.classpath")));
        String line = "";
        while ((line = reader.readLine()) != null) {
            int index = line.indexOf("M2_REPO");
            if (index != -1) {
                String str = line.substring(index + 7);
                File file = new File(path = "d:" + str.substring(0, str.indexOf("\"")));
                if (!file.exists()) {
                    File dir = file.getParentFile();
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    map.put(file.getName(), dir);
                }
            }
        }
        return map;
    }

    /**
     * 重名文件(路径不同)
     * 
     * @param list
     */
    private static List<File> dupNameFiles(List<File> list) {
        List<File> files = new ArrayList();
        Map<String, File> map = new TreeMap<String, File>();
        for (File f : list) {
            String name = f.getName();
            if (map.containsKey(name)) {
                File file = map.get(name);
                if (!f.getPath().equals(file.getPath())) {
                    System.out.println(f);
                    System.out.println(file);
                    files.add(f);
                    files.add(file);
                }
            } else {
                map.put(name, f);
            }
        }
        return files;
    }

    /**
     * 取目录及子目录下，指定后缀的文件
     * @param file 目录
     * @param list 返回的文件集
     * @param suffix 文件后缀
     * @return
     */
    private static List<File> getFiles(File file, List<File> list, String suffix) {
        if (null == list) {
            list = new ArrayList<File>();
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isFile()) {
                    addFile(f, list, suffix);
                } else {
                    getFiles(f, list, suffix);
                }
            }
        } else {
            addFile(file, list, suffix);
        }
        return list;
    }

    private static void addFile(File file, List<File> list, String suffix) {
        if (file.getName().endsWith(suffix))
            list.add(file);
    }

    /**
     * 从maven本地库(d:/repo)拷贝jars到指定的jar目录
     * @param projectDir 工程目录
     * @param libDirPath jar包目录
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void copyJars2Lib(String projectDir, String libDirPath)
                                                                          throws FileNotFoundException,
                                                                          IOException {
        if (projectDir == null || projectDir.equals(""))
            projectDir = System.getProperty("user.dir");
        if (libDirPath == null || libDirPath.equals(""))
            libDirPath = projectDir + File.separator + "lib";
        projectDir = toPath(projectDir);
        libDirPath = toPath(libDirPath);
        File libDir = new File(libDirPath);
        if (!libDir.exists())
            libDir.mkdirs();
        File f = new File(projectDir + File.separator + ".classpath");
        if (f.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String repo = System.getenv("M2_REPO");
            repo = null == repo ? "d:/repo" : repo;
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.indexOf("<classpathentry") != -1 && line.indexOf("path") != -1
                    && line.indexOf(".jar") != -1) {
                    String path = line.split("path=\"")[1];
                    path = path.substring(0, path.indexOf("\""));
                    if (path.startsWith("M2_REPO")) {
                        path = path.replaceAll("M2_REPO", repo);
                    }
                    //windows cmd /linux shell
                    String cmd = "cmd /c  xcopy /-y " + path.replace("/", "\\") + " " + libDirPath;
                    System.out.println(cmd);
                    Runtime.getRuntime().exec(cmd);
                }
            }
        }

    }

    /**
     * project nature是eclipse开发中一个概念，比如加入javanature就表示此项目是一个java
     * project，会绑定一个java
     * builder用来编译java文件，而webnature告诉MyEclipse这是一个MyEclipse
     * web项目，更多MyEclipse projectNature： webservice--
     * com.genuitec.eclipse.ws.xfire.wsnature facelet--
     * com.genuitec.eclipse.jsf.faceletsnature
     * jsf--com.genuitec.eclipse.jsf.jsfnature
     * struts--com.genuitec.eclipse.cross
     * .easystruts.eclipse.easystrutsnature 或者在插件里面使用这个方法：
     */
    private static void eclipseNatures() {
        /* IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("test");
         try {
             String[] natures = project.getDescription().getNatureIds();
             for (String nature : natures)
                 System.out.println(nature);
         } catch (CoreException e) {
             
         }*/
    }

    /*    public static void appendJavaClassPath(IProject p, IClasspathEntry newEntry) throws JavaModelException {   
            IJavaProject javaProject = JemProjectUtilities.getJavaProject(p);   
            if (javaProject == null)   
                return;   
            IClasspathEntry[] classpath = javaProject.getRawClasspath();   
            List newPathList = new ArrayList(classpath.length);   
            for (int i = 0; i < classpath.length; i++) {   
                IClasspathEntry entry = classpath[i];   
                // fix dup class path entry for .JETEmitter project   
                // Skip the entry to be added if it already exists   
                if (Platform.getOS().equals(Platform.OS_WIN32)) {   
                    if (!entry.getPath().toString().equalsIgnoreCase(newEntry.getPath().toString()))   
                        newPathList.add(entry);   
                    else  
                        return;   
                } else {   
                    if (!entry.getPath().equals(newEntry.getPath()))   
                        newPathList.add(entry);   
                    else  
                        return;   
                }   
            }   
            newPathList.add(newEntry);   
            IClasspathEntry[] newClasspath = (IClasspathEntry[]) newPathList   
                    .toArray(new IClasspathEntry[newPathList.size()]);   
            javaProject.setRawClasspath(newClasspath, new NullProgressMonitor());   
        }*/
}
