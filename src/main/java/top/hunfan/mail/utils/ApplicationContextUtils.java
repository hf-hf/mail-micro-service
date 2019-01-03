package top.hunfan.mail.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 上下文容器获取类
 * @ClassName ApplicationContextUtils
 * @Description 
 * @author hf-hf
 * @date 2017年7月5日 下午2:26:45
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {

	private static ApplicationContext ac;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ApplicationContextUtils.ac = applicationContext;
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) ac.getBean(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		return ac.getBean(requiredType);
	}

}