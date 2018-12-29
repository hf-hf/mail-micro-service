package top.hunfan.mail.domain;

import java.io.Serializable;

import lombok.Data;

/**
 * 统一返回值
 * @author hefan
 * @date 2018/12/29 10:27
 */
@Data
public class R implements Serializable {
    /**
     * 返回码
     */
    private int code = Code.SUCCEED.getCode();

    /**
     * 返回信息
     */
    private String message = Code.SUCCEED.getMessage();

    /**
     * 返回数据
     */
    private Object data;

    public static R success() {
        return new R();
    }

    public static R fail() {
        return new R(Code.FAILED.getCode(), Code.FAILED.getMessage());
    }

    public static R operate(boolean isSucceed) {
        return isSucceed ? success() : fail();
    }

    public R() {
    }

    public R(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public R(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
