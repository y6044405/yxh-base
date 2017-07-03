package com.tzg.tool.kit.test.eclipse;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Properties;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DelLastUpdated {
    private static final String KEY_MAVEN_REPO  = "maven.repo";
    private static String       MAVEN_REPO_PATH = "d:/repo";
    private static final String FILE_SUFFIX     = "lastUpdated";
    private static final Log    logger          = LogFactory.getLog(DelLastUpdated.class);


    /**      * @param args      */
    public static void main(String[] args) {
        File mavenRep = new File(MAVEN_REPO_PATH);
        if (!mavenRep.exists()) {
            logger.warn("Maven repos is not exist.");
            return;
        }
        File[] files = mavenRep.listFiles((FilenameFilter) FileFilterUtils.directoryFileFilter());
        delFile(files, null);
        logger.info("Clean lastUpdated files finished.");
    }

    private static void delFile(File[] dirs, File[] files) {
        if (dirs == null || dirs.length == 0) {
            return;
        }
        for (File dir : dirs) {
            File[] childDir = dir.listFiles((FilenameFilter) FileFilterUtils.directoryFileFilter());
            File[] childFiles = dir.listFiles((FilenameFilter) FileFilterUtils.suffixFileFilter(FILE_SUFFIX));
            delFile(childDir, childFiles);
        }
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (file.delete()) {
                logger.info("File: [" + file.getName() + "] has been deleted.");
            }
        }

    }
}
