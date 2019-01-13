package top.hunfan.mail.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * ThreadLocal工具类
 * @author hf-hf
 * @date 2019/1/11 16:15
 */
public class ThreadLocalUtils {
    private static final ThreadLocal<Map<String, Object>> local = ThreadLocal.withInitial(() -> new HashMap<>());

    public static <T> T put(String key, T value) {
        local.get().put(key, value);
        return value;
    }

    public static void remove(String key) {
        local.get().remove(key);
    }

    public static void clear() {
        local.remove();
    }

    @SuppressWarnings("unchecked")
	public static <T> T get(String key) {
        return ((T) local.get().get(key));
    }
}
