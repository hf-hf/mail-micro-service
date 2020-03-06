package top.hunfan.mail.exception;

/**
 * 轮询错误异常
 * @author hefan
 * @date 2020/3/6 16:35
 */
public class RoundRobinErrorException extends RuntimeException {

    private static final long serialVersionUID = 8181837055804508934L;

    public RoundRobinErrorException(Throwable e) {
        super(e);
    }

    public RoundRobinErrorException(String message) {
        super(message);
    }

}
