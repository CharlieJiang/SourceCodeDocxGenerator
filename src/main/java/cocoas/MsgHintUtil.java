package cocoas;

import javax.swing.*;

/**
 * 描述：消息提示弹窗
 * 作者：蒋庆意
 * 日期时间：2021/1/22 13:44
 * <p>
 * cocoasjiang@foxmail.com
 */
public class MsgHintUtil {

    /**
     * 显示提示对话框
     * @param msg
     */
    public static void showHint(String msg){
        JOptionPane.showMessageDialog(null,msg,"提示",JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * 源代码文档生成成功提示对话框
     * @param savePath  文档保存位置
     * @param pages     文档页数
     * @param lineNums  文档行数
     */
    public static void showFinishHint(String savePath,int pages,int lineNums){
        StringBuilder sb = new StringBuilder();
        sb.append("源代码文档生成成功！").append("\n");
        sb.append("文档位置：").append(savePath).append("\n");
        sb.append("文档页数：").append(pages).append("页").append("\n");
        sb.append("文档行数：").append(lineNums).append("行");
        JOptionPane.showMessageDialog(null,sb.toString(),"完成",JOptionPane.PLAIN_MESSAGE);
    }


}
