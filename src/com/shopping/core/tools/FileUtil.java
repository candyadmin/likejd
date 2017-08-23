 package com.shopping.core.tools;
 
 import java.io.BufferedReader;
 import java.io.BufferedWriter;
 import java.io.File;
 import java.io.FileWriter;
 import java.io.FilenameFilter;
 import java.io.IOException;
 import java.io.StringReader;
 import org.apache.commons.io.FileUtils;
 import org.apache.commons.io.filefilter.AndFileFilter;
 import org.apache.commons.io.filefilter.DirectoryFileFilter;
 import org.apache.commons.io.filefilter.IOFileFilter;
 import org.apache.commons.io.filefilter.NotFileFilter;
 import org.apache.commons.io.filefilter.SuffixFileFilter;
 
 public class FileUtil
 {
   public static boolean string2File(String res, String filePath)
   {
     boolean flag = true;
     BufferedReader bufferedReader = null;
     BufferedWriter bufferedWriter = null;
     try {
       File distFile = new File(filePath);
       if (!distFile.getParentFile().exists())
         distFile.getParentFile().mkdirs();
       bufferedReader = new BufferedReader(new StringReader(res));
       bufferedWriter = new BufferedWriter(new FileWriter(distFile));
       char[] buf = new char[1024];
       int len;
       while ((len = bufferedReader.read(buf)) != -1)
       {
         bufferedWriter.write(buf, 0, len);
       }
       bufferedWriter.flush();
       bufferedReader.close();
       bufferedWriter.close();
     } catch (IOException e) {
       flag = false;
       e.printStackTrace();
     }
     return flag;
   }
 
   public static void copyFile(String resFilePath, String distFolder)
     throws IOException
   {
     File resFile = new File(resFilePath);
     File distFile = new File(distFolder);
     if (resFile.isDirectory())
       FileUtils.copyDirectoryToDirectory(resFile, distFile);
     else if (resFile.isFile())
       FileUtils.copyFileToDirectory(resFile, distFile, true);
   }
 
   public static void deleteFile(String targetPath)
     throws IOException
   {
     File targetFile = new File(targetPath);
     if (targetFile.isDirectory())
       FileUtils.deleteDirectory(targetFile);
     else if (targetFile.isFile())
       targetFile.delete();
   }
 
   public static void moveFile(String resFilePath, String distFolder)
     throws IOException
   {
     File resFile = new File(resFilePath);
     File distFile = new File(distFolder);
     if (resFile.isDirectory())
       FileUtils.copyDirectory(resFile, distFile, true);
     else if (resFile.isFile())
       FileUtils.copyDirectory(resFile, distFile, true);
   }
 
   public static long genFileSize(String distFilePath)
   {
     File distFile = new File(distFilePath);
     if (distFile.isFile())
       return distFile.length();
     if (distFile.isDirectory()) {
       return FileUtils.sizeOfDirectory(distFile);
     }
     return -1L;
   }
 
   public static boolean isExist(String filePath)
   {
     return new File(filePath).exists();
   }
 
   public static String[] listFilebySuffix(String folder, String suffix)
   {
     IOFileFilter fileFilter1 = new SuffixFileFilter(suffix);
     IOFileFilter fileFilter2 = new NotFileFilter(
       DirectoryFileFilter.INSTANCE);
     FilenameFilter filenameFilter = new AndFileFilter(fileFilter1, 
       fileFilter2);
     return new File(folder).list(filenameFilter);
   }
 }
