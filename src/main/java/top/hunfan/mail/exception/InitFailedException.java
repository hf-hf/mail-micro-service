package top.hunfan.mail.exception;

/**
 * 初始化失败异常
 * @author hefan
 * @date 2020/3/6 16:35
 */
public class InitFailedException extends RuntimeException {

	private static final long serialVersionUID = 3307905635315821033L;

	public InitFailedException(Throwable e) {
		super(e);
	}

	public InitFailedException(String message) {
		super(message);
	}

}