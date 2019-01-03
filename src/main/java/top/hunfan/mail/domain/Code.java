package top.hunfan.mail.domain;

/**
 * 响应异常代码
 * @author hf-hf
 * @date 2018/12/29 10:27
 */
public enum Code {
    /**
     * 200 成功
     */
    SUCCEED(200, "发送成功"),

    /**
     * 500 失败
     */
    FAILED(500, "发送失败");

    /**
     * 业务异常编码
     */
    private int code;

    /**
     * 异常信息
     */
    private String message;

    Code(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
