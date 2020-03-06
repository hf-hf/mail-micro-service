package top.hunfan.mail.exception;

/**
 * 无效发信邮箱
 * @author hefan
 * @date 2020/3/6 16:35
 */
public class InvalidFromException extends RuntimeException {

    private static final long serialVersionUID = -7263869434483606944L;

    public InvalidFromException(Throwable e) {
        super(e);
    }

    public InvalidFromException(String message) {
        super(message);
    }

}
