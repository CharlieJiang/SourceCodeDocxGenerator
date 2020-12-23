package cocoas;

import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 描述：使用Apache poi自动生成软著申请所需的项目源代码Word文档
 * 作者：蒋庆意
 * 日期时间：2020/12/16 13:53
 */
public class CodeDocxGenerator {

//    private static final String PROJECT_PATH = "D:\\git_workspace\\MerchantClient\\app";// 项目路径
    private static String PROJECT_PATH = "";// 项目路径
//    private static final String DOC_SAVE_PATH = PROJECT_PATH + "\\SourceCode.docx";// 生成的源代码Word文档的保存路径
    private static String DOC_SAVE_PATH ="";// 生成的源代码Word文档的保存路径
//    private static final String HEADER = "济享如意商户通V1.0.7";// 软件名称+版本号
    private static String HEADER = "";// 软件名称+版本号
//    private static final String FILE_TYPE = ".java";// 需要查找的文件类型
    private static String FILE_TYPE = "";// 需要查找的文件类型
    private static int totalLines = 0;// 代码总行数
    private static final int MAX_LINES = 52 * 60; // 限制代码的最大行数
    private static final long PAGE_MARGIN_VERTICAL = 1080L;// 页面上下边距
    private static final long PAGE_MARGIN_HORIZONTAL = 720L;// 页面左右边距

    public static void main(String[] args) {
        print("开始");
        // 四个参数处理：项目源代码目录、软件名称、版本号、源码文件类型
        if(args == null || args.length < 4){
            print("参数错误，请输入参数：源代码项目目录、软件名称、版本号、源代码文件类型。参数间以空格区分，文件类型以.开始。");
            System.exit(0);
        }
        PROJECT_PATH = args[0];
        HEADER = args[1] + args[2];
        FILE_TYPE = args[3];
        DOC_SAVE_PATH = PROJECT_PATH + "\\SourceCode.docx";
        print("获取参数成功");
        print("源代码项目目录：" + PROJECT_PATH);
        print("软件名称：" + args[1]);
        print("版本号：" + args[2]);
        print("源代码文件类型：" + FILE_TYPE);
        generateSourceCodeWord(PROJECT_PATH);
    }

    /**
     * 生成源代码Word文档
     * @param projectPath 源代码目录
     */
    private static void generateSourceCodeWord(String projectPath){
        //扫描项目中符合要求的文件
        print("开始扫描文件");
        List<String> files = scanFiles(projectPath);
        print("扫描文件完成");
        print("文件总数：" + files.size());
        // 创建一个Word：存放源代码
        XWPFDocument doc = new XWPFDocument();
        // 设置Word的页边距：保证每页不少于50行代码，且尽量保证每行代码不换行
        setPageMargin(doc,PAGE_MARGIN_VERTICAL,PAGE_MARGIN_HORIZONTAL);
        // 迭代代码文件将源代码写入Word中
        print("开始写入Word文档");
        files.forEach(f ->
                {
                    if(totalLines < MAX_LINES){// 行数达到要求则不再写入
                        writeFileToWord(f,doc);
                    }
                }
        );
        print("写入Word文档完成");
        print("Word文档输出目录：" + DOC_SAVE_PATH);
        // 保存Word文档
        saveWord(doc,DOC_SAVE_PATH);
        print("统计代码行数：" + totalLines);
        // Word添加页眉：显示软件名称、版本号和页码
        createPageHeader(HEADER);
        print("结束");
    }

    /**
     * 创建页码：通过在页眉中插入Word中代表页码的域代码{PAGE  \* MERGEFORMAT}来显示页码
     * @param paragraph 段落
     */
    private static void createPageNum(XWPFParagraph paragraph){
        // Word中域代码的语法是 {域名称 指令 可选开关} ，其中大括号不能直接写，只能通过代码来生成或表示
        // 下面三个步骤就是创建左大括号{、域代码内容、右大括号}
        // 创建左大括号{
        XWPFRun run = paragraph.createRun();
        CTFldChar fldChar = run.getCTR().addNewFldChar();
        fldChar.setFldCharType(STFldCharType.Enum.forString("begin"));

        // 创建域代码内容
        run = paragraph.createRun();
        CTText ctText = run.getCTR().addNewInstrText();
        ctText.setStringValue("PAGE  \\* MERGEFORMAT");
        ctText.setSpace(SpaceAttribute.Space.Enum.forString("preserve"));

        // 创建右大括号}
        fldChar = run.getCTR().addNewFldChar();
        fldChar.setFldCharType(STFldCharType.Enum.forString("end"));
    }

    /**
     * Word添加页眉
     * @param header 页眉内容
     */
    private static void createPageHeader(String header){
        try {
            // 以已存在的Word文件创建文档对象
            XWPFDocument doc = new XWPFDocument(new FileInputStream(new File(DOC_SAVE_PATH)));

            //生成偶数页的页眉
            createPageHeader(doc,HeaderFooterType.EVEN,header);

            //生成奇数页的页眉
            createPageHeader(doc,HeaderFooterType.DEFAULT,header);

            // 反射添加页眉
            Field filedSet = XWPFDocument.class.getDeclaredField("settings");
            filedSet.setAccessible(true);
            XWPFSettings xwpfsettings = (XWPFSettings) filedSet.get(doc);

            Field filedCtSet = XWPFSettings.class.getDeclaredField("ctSettings");
            filedCtSet.setAccessible(true);
            CTSettings ctSettings = (CTSettings) filedCtSet.get(xwpfsettings);
            ctSettings.addNewEvenAndOddHeaders();

            // 保存文档
            doc.write(new FileOutputStream(DOC_SAVE_PATH));
            doc.close();
        }catch (Exception e){
            error("Word添加页眉出错：" + e.getMessage());
        }
    }

    /**
     * 创建页眉，页眉内容包含软件名称、版本号和页码。
     * <br/>其中软件名称和版本号合并居左，页码居右
     * @param doc
     * @param type 页眉类型：决定创建的是奇数页页眉还是偶数页页眉
     * @param header 页眉显示的文本内容
     */
    private static void createPageHeader(XWPFDocument doc, HeaderFooterType type, String header){
        // 创建页眉段落
        XWPFParagraph  paragraph = doc.createHeader(type).createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);// 页眉内容左对齐
        paragraph.setVerticalAlignment(TextAlignment.CENTER);// 页眉内容垂直居中
//        paragraph.setBorderTop(Borders.THICK);

        // 创建tab，用于定位页码，让页码居右显示
        CTTabStop tabStop = paragraph.getCTP().getPPr().addNewTabs().addNewTab();
        tabStop.setVal(STTabJc.RIGHT);
        int twipsPerInch =  720;
        tabStop.setPos(BigInteger.valueOf(15 * twipsPerInch));

        // 创建显示header的XWPFRun，XWPFRun代表一个文本显示区域
        XWPFRun run = paragraph.createRun();
        run.setText(header);
        run.addTab();// 在header后面追加一个tab，这样页码就只能在tab后面显示，也就是变相让页面居右
        createPageNum(paragraph);// 创建页码
    }

    /**
     * 设置Word的页边距：上下边距控制每页至少显示50行，左右边距控制每行代码尽量不会自动换行
     * @param doc
     * @param marginVertical 上下边距
     * @param marginHorizontal 左右边距
     */
    private static void setPageMargin(XWPFDocument doc,long marginVertical,long marginHorizontal){
        CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
        CTPageMar pageMar = sectPr.addNewPgMar();
        pageMar.setTop(BigInteger.valueOf(marginVertical));
        pageMar.setBottom(BigInteger.valueOf(marginVertical));
        pageMar.setLeft(BigInteger.valueOf(marginHorizontal));
        pageMar.setRight(BigInteger.valueOf(marginHorizontal));
    }

    /**
     * 扫描项目中符合要求文件，并将文件路径存放到列表中
     * @param dir
     * @return
     */
    private static List<String> scanFiles(String dir){
        File rootFile = new File(dir);
        if(!rootFile.isDirectory()){
            print("项目路径错误：" + dir);
            exit();
        }
        return collectFilesFromDir(dir);
    }

    /**
     * 将文件目录中符合要求的文件的文件路径添加到List中
     * @param dir
     * @return
     */
    private static List<String> collectFilesFromDir(String dir){
        File dirFile = new File(dir);
        if(!dirFile.isDirectory()){
            return new ArrayList<>();
        }
        List<String> files = new ArrayList<>();
        File[] subFiles = dirFile.listFiles();
        Arrays.stream(subFiles)
                .forEach(
                        f -> {
                            // 特殊目录过滤
                            if(f.isDirectory() && !f.getName().equals("build") && !f.getName().equals("zxing")){
                                // 继续迭代目录
                                files.addAll(collectFilesFromDir(f.getAbsolutePath()));
                            }else if(f.getAbsolutePath().endsWith(FILE_TYPE)){
                                // 添加文件路径
                                files.add(f.getAbsolutePath());
                            }
                        }
                );
        return files;
    }

    /**
     * 单个源码文件写入Word
     * @param filePath 源码文件路径
     */
    private static void writeFileToWord(String filePath, XWPFDocument doc){
        print(getFileName(filePath));
        // 写入文件标题
        XWPFParagraph titleP = doc.createParagraph();// 新建文件标题段落
        XWPFRun titleRun = titleP.createRun();// 创建段落文本
        titleRun.setText(getFileName(filePath));
        totalLines++;// 文件名行计数

        // 写入文件内容
        XWPFParagraph paragraph = doc.createParagraph(); // 新建文件内容段落
        // 设置段落对齐方式
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        paragraph.setSpacingLineRule(LineSpacingRule.EXACT);

        XWPFRun run ;
        List<String> lines = readFile(filePath);
        for(int i = 0;i < lines.size();i++){// 将代码一行行写入Word中
            run = paragraph.createRun();// 创建段落文本
            run.setText(lines.get(i));// 设置段落文本
            if(i < lines.size() - 1){// 最后一行不用换行：防止两个源码文件间出现空行
                run.addBreak();// 设置换行
            }
            totalLines++;// 代码行计数
            if(lines.get(i).length() > 125){// 当一行代码的长度超过125时，应该会发生换行，一行代码在Word中可能会变成两行甚至更多行
                totalLines++;// 代码自动换行计数
            }
        }
    }

    /**
     * 读取文件内容：过滤空行和注释
     * @param filePath
     * @return
     */
    private static List<String> readFile(String filePath){
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
            error("读取文件<" + filePath + ">出错：" + e.getMessage());
        } catch (IOException e) {
            error("读取文件<" + filePath + ">出错：" + e.getMessage());
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
        if(null == line || line.length() == 0){
            return false;
        }
        // 过滤注释
        if(line.trim().startsWith("/") || line.trim().startsWith("*")){
            return false;
        }
        return true;
    }

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
     * Word保存到本地
     * @param doc
     */
    public static void saveWord(XWPFDocument doc, String savePath){
        // 创建文件输出流：保存Word到本地
        try {
            FileOutputStream fout = new FileOutputStream(savePath);
            doc.write(fout);
            fout.close();
        } catch (FileNotFoundException e) {
            error("保存Word文档到本地时发生错误：" + e.getMessage());
        } catch (IOException e) {
            error("保存Word文档到本地时发生错误：" + e.getMessage());
        }
    }

    /**
     * 退出
     */
    private static void exit(){
        System.exit(0);
    }

    // 打印信息
    public static void print(String msg){
        System.out.println(msg);
    }

    // 打印错误信息
    public static void error(String msg){
        System.err.println(msg);
    }


}
