package cocoas;

/**
 * 描述：日志输出管理
 * 作者：蒋庆意
 * 日期时间：2021/1/18 11:36
 * <p>
 * cocoasjiang@foxmail.com
 */
public class LogUtils {

    // 打印信息
    public static void print(String msg){
        System.out.print(msg + " ");
    }

    // 打印信息（自动换行）
    public static void println(String msg){
        System.out.println(msg);
    }

    // 打印错误信息
    public static void error(String msg){
        System.err.println(msg);
    }

}
