package cocoas;

/**
 * 描述：生成源代码文档全过程的相关事件
 * 作者：蒋庆意
 * 日期时间：2021/1/22 9:52
 * <p>
 * cocoasjiang@foxmail.com
 */
public interface GenerateEvent {

    /**未扫描到文件*/
    int EVENT_NO_FILE = 0;

    /**生成源代码文档完成*/
    int EVENT_FINISH = 999;

}
