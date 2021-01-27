package cocoas;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 描述：文件操作帮助类
 * 作者：蒋庆意
 * 日期时间：2021/1/18 11:27
 * <p>
 * cocoasjiang@foxmail.com
 */
public class FileUtils {

    /**
     * 获取文件名
     * @param filePath 文件路径
     * @return
     */
    public static String getFileName(String filePath){
        if(null == filePath || new File(filePath).isDirectory()){
            return "";
        }
        File file = new File(filePath);
        return file.getName();
    }

    /**
     * 判断文件是否符合指定的文件类型
     * @param f
     * @param fileTypes
     * @return
     */
    public static boolean matchFile(File f, List<String> fileTypes){
        if(f == null || f.isDirectory()){
            return false;
        }
        for(String fileType : fileTypes){
            if(f.getAbsolutePath().endsWith(fileType)){
                return true;
            }
        }
        return false;
    }


    /**
     * 按行读取文件内容：过滤空行和注释
     * @param filePath
     * @return
     */
    public static List<String> readFile(String filePath){
        if(null == filePath || new File(filePath).isDirectory()){
            return new ArrayList<>();
        }
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
            String line = reader.readLine();
            while(null != line){
                if(filterLine(line)){// 过滤不符合要求的代码行
                    lines.add(line);
                }
                line = reader.readLine();
            }
            return lines;
        } catch (FileNotFoundException e) {
            LogUtils.error("读取文件<" + filePath + ">出错：" + e.getMessage());
        } catch (IOException e) {
            LogUtils.error("读取文件<" + filePath + ">出错：" + e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * 过滤不符合要求的代码行
     * @param line
     * @return
     */
    public static boolean filterLine(String line){
        // 过滤空行
        if(null == line || line.trim().length() == 0){
            return false;
        }
        String lineTrim = line.trim();
        // 过滤一般的行注释//、块注释/* */、文档注释/** */
        if(lineTrim.startsWith("//") || lineTrim.startsWith("/*")
                || lineTrim.startsWith("*")){
            return false;
        }
        // 过滤html、xml注释：<!-- -->
        if(lineTrim.startsWith("<!--")){
            return false;
        }
        // 过滤PHP、Python的行注释：#
        if(lineTrim.startsWith("#")){
            return false;
        }
        // Python的块注释用法比较复杂，不太适合自动过滤
        return true;
    }

    /**
     * 扫描项目中符合要求文件，并将文件路径存放到列表中
     * @param dir
     * @param fileTypes
     * @param ignoreDirs
     * @return
     */
    public static List<String> scanFiles(String dir, List<String> fileTypes, List<String> ignoreDirs){
        File rootFile = new File(dir);
        if(!rootFile.isDirectory()){
            LogUtils.println("项目路径错误：" + dir);
            return new ArrayList<>();
        }
        return collectFilesFromDir(dir,fileTypes,ignoreDirs);
    }

    /**
     * 将文件目录中符合要求的文件的文件路径添加到List中
     * @param dir
     * @param fileTypes
     * @param ignoreDirs
     * @return
     */
    private static List<String> collectFilesFromDir(String dir, List<String> fileTypes, List<String> ignoreDirs){
        File dirFile = new File(dir);
        if(!dirFile.isDirectory()){
            return new ArrayList<>();
        }
        List<String> files = new ArrayList<>();// 扫描到的文件路径
        File[] subFiles = dirFile.listFiles();
        if(subFiles == null || subFiles.length <= 0){
            return files;
        }
        Arrays.stream(subFiles)
                .forEach(
                        f -> {
                            // 特殊目录过滤
                            if(f.isDirectory() && !f.getName().equals("build") && !f.getName().equals("zxing") && !isIgnoreDir(f,ignoreDirs)){
                                // 继续迭代目录
                                files.addAll(collectFilesFromDir(f.getAbsolutePath(),fileTypes,ignoreDirs));
                            }else if(FileUtils.matchFile(f,fileTypes)){
                                // 添加文件路径
                                files.add(f.getAbsolutePath());
                            }
                        }
                );
        return files;
    }

    /**
     * 判断文件目录是否是需要过滤的目录
     * @param f
     * @param ignoreDirs
     * @return
     */
    private static boolean isIgnoreDir(File f, List<String> ignoreDirs){
        if(ignoreDirs == null || ignoreDirs.isEmpty()){
            return false;
        }
        for(String ignoreDir: ignoreDirs){
            if(f.getName().equals(ignoreDir)){
                return true;
            }
        }
        return false;
    }

    /**
     * 退出
     */
    private static void exit(){
        System.exit(0);
    }

}
