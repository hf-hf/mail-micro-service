package top.hunfan.mail.roundrobin;

/**
 * 调用程序
 * @author hf-hf
 * @date 2018/12/27 10:17
 */
public interface Invoker {
    /**
     * 是否可用
     */
    Boolean isAvailable();

    /**
     * 标识
     */
    String id();
}