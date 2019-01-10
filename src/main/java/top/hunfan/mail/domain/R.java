package top.hunfan.mail.domain;

import java.io.Serializable;

import lombok.Data;

/**
 * 统一返回值
 * @author hf-hf
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
        return success(null);
    }

    public static R success(Object data) {
        return new R(Code.SUCCEED.getCode(), Code.SUCCEED.getMessage(), data);
    }

    public static R fail() {
        return new R(Code.FAILED.getCode(), Code.FAILED.getMessage());
    }

    public static R fail(String message) {
        return new R(Code.FAILED.getCode(), message);
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
