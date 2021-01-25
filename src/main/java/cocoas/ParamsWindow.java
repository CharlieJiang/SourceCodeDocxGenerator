package cocoas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：获取源代码文档生成参数的可视化页面
 * 作者：蒋庆意
 * 日期时间：2021/1/20 16:37
 * <p>
 * cocoasjiang@foxmail.com
 */
public class ParamsWindow {

    private static boolean isHalf = false;// 文档是否分为前后各30页
    private static String dirPath = "";// 项目路径
    private static String[] params;// 参数数组
    private List<String> ignoreDirs;// 需要扫描时忽略的文件夹

    public ParamsWindow(){
        createParamsWindow();
    }

    public static void main(String[] args) {
        new ParamsWindow();
    }

    /**
     * 创建参数输入窗口
     */
    private void createParamsWindow(){
        JFrame window = new JFrame();
        window.setTitle("源代码文档自动生成工具");// 窗口标题
        JPanel panel = new JPanel();// 总容器

        /*##必填部分##*/
        // 必填部分标题
        JLabel labelForceTitle = new JLabel("--------必填部分--------");
        labelForceTitle.setForeground(Color.red);
        // 源代码项目目录
        JPanel panelDir = new JPanel();// 项目目录容器
        JLabel labelDir = new JLabel("项目目录：");
        JTextField inputDir = new JTextField();
        Dimension inputSize = new Dimension(200,30);// 输入框尺寸
        inputDir.setPreferredSize(inputSize);
        JButton btnDir = new JButton("选择");
        btnDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 创建文件选择器
                JFileChooser fileChooser = new JFileChooser("C:\\");
                // 设置只选择文件夹
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(null);
                if(result == JFileChooser.APPROVE_OPTION){
                    File selectedDir = fileChooser.getSelectedFile();
                    dirPath = selectedDir.getAbsolutePath();
                    LogUtils.println("选择文件夹：" + dirPath);
                    inputDir.setText(dirPath);
                }else{
                    LogUtils.println("未选择文件夹");
                }
            }
        });
        panelDir.add(labelDir);
        panelDir.add(inputDir);
        panelDir.add(btnDir);
        // 写入方式
        JPanel panelType = new JPanel();// 写入方式容器
        JLabel labelType = new JLabel("写入方式：");
        ButtonGroup btnGroup = new ButtonGroup();// 单选按钮组
        JRadioButton btnRadioOrder = new JRadioButton("顺序写入60页",true);
        btnRadioOrder.setPreferredSize(new Dimension(133,30));
        btnRadioOrder.addActionListener(new ActionListener() {// 单选按钮点击监听
            @Override
            public void actionPerformed(ActionEvent e) {
                isHalf = false;
            }
        });
        JRadioButton btnRadioUnOrder = new JRadioButton("前后各30页");
        btnRadioUnOrder.setPreferredSize(new Dimension(133,30));
        btnRadioUnOrder.addActionListener(new ActionListener() {// 单选按钮点击监听
            @Override
            public void actionPerformed(ActionEvent e) {
                isHalf = true;
            }
        });
        btnGroup.add(btnRadioOrder);
        btnGroup.add(btnRadioUnOrder);
        panelType.add(labelType);
        panelType.add(btnRadioOrder);
        panelType.add(btnRadioUnOrder);
        // 文件类型
        JPanel panelFileType = new JPanel();// 文件类型容器
        JLabel labelFileType = new JLabel("文件类型：");
        JTextField inputFileType = new JTextField();
        inputFileType.setPreferredSize(new Dimension(266,30));
        panelFileType.add(labelFileType);
        panelFileType.add(inputFileType);

        /*##非必填部分##*/
        // 非必填部分标题
        JLabel labelUnForceTitle = new JLabel("--------非必填部分--------");
//        labelUnForceTitle.setForeground(Color.green);
        // 软件名称
        JPanel panelName = new JPanel();// 软件名称容器
        JLabel labelName = new JLabel("软件名称：");
        JTextField inputName = new JTextField();
        inputName.setPreferredSize(new Dimension(266,30));
        panelName.add(labelName);
        panelName.add(inputName);
        // 软件版本
        JPanel panelVersion = new JPanel();// 软件版本容器
        JLabel labelVersion = new JLabel("软件版本：");
        JTextField inputVersion = new JTextField();
        inputVersion.setPreferredSize(new Dimension(266,30));
        panelVersion.add(labelVersion);
        panelVersion.add(inputVersion);
        // 扫描忽略目录
        JPanel panelIgnore = new JPanel();// 忽略目录容器
        JLabel labelIgnore = new JLabel("忽略目录：");
        JTextField inputIgnore = new JTextField();
        inputIgnore.setPreferredSize(new Dimension(266,30));
        panelIgnore.add(labelIgnore);
        panelIgnore.add(inputIgnore);

        /*##启动按钮##*/
        JButton btnStart = new JButton("开始");
        btnStart.addActionListener(new ActionListener() {// 开始按钮点击处理
            @Override
            public void actionPerformed(ActionEvent e) {
                inputCheck(inputDir.getText(),isHalf,inputFileType.getText(),inputName.getText(),inputVersion.getText(),inputIgnore.getText());
            }
        });

        // 各分容器添加到一个总容器中
        panel.add(labelForceTitle);
        panel.add(panelDir);
        panel.add(panelType);
        panel.add(panelFileType);
        panel.add(labelUnForceTitle);
        panel.add(panelName);
        panel.add(panelVersion);
        panel.add(panelIgnore);
        panel.add(btnStart);


        window.add(panel);
//        window.setBounds(300,200,400,500);
        window.setSize(new Dimension(400,500));// 窗口大小设置
        window.setLocationRelativeTo(null);// 窗口居中显示
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * 输入参数检测
     * @param dir
     * @param isHalf
     * @param fileTypes
     * @param name
     * @param version
     * @param ignoreDirs
     */
    private void inputCheck(String dir,boolean isHalf,String fileTypes,String name,String version,String ignoreDirs ){
        if(dir == null || dir.trim().isEmpty()){// 项目目录空判断
            MsgHintUtil.showHint("请选择或输入项目源码目录！");
            return;
        }else if(!(new File(dir).isDirectory())){
            MsgHintUtil.showHint("请选择或输入正确的项目源码目录！");
            return;
        }
        if(fileTypes == null || fileTypes.trim().isEmpty()){
            MsgHintUtil.showHint("请输入需要写入的源码文件类型！（提示：源码文件类型应以.开头，如.java，多个文件类型间以空格区分）");
            return;
        }

        // 收集数据
        params = new String[15];
        params[0] = dir.trim();// 项目目录
        params[1] = name ==null ? "":name.trim();// 软件名称
        params[2] = version ==null ? "":version.trim();// 软件版本
        params[3] = String.valueOf(isHalf);// 写入方式
        // 源码文件类型检查
        String[] fileTypeArray = fileTypes.trim().split(" ");
        for(int i = 0;i < fileTypeArray.length;i++){
            if(fileTypeArray[i] == null || fileTypeArray[i].trim().isEmpty() || !fileTypeArray[i].trim().startsWith(".")){
                MsgHintUtil.showHint("请输入的源码文件类型有误，请重新输入！\n（提示：源码文件类型应以.开头，如.java，多个文件类型间以空格区分）");
                return;
            }else{
                params[(3 + 1 + i)] = fileTypeArray[i].trim();
            }
        }
        // 忽略目录检查
        fileTypeArray = ignoreDirs.trim().split(" ");
        if(fileTypeArray != null && fileTypeArray.length > 0){
            this.ignoreDirs = new ArrayList<>();
            for(int i = 0;i < fileTypeArray.length;i++){
                if(fileTypeArray[i] != null && !fileTypeArray[i].trim().isEmpty()){
                    this.ignoreDirs.add(fileTypeArray[i].trim());
                }
            }
        }
        start(params);
    }

    /**
     * 启动源代码文档自动生成程序
     * @param paramArray
     */
    private void start(String[] paramArray){
        CodeDocxGenerator cdg = new CodeDocxGenerator();
        cdg.start(paramArray,ignoreDirs);
    }


}
