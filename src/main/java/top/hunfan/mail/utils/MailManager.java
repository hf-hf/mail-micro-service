package top.hunfan.mail.utils;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

/**
 * 负载均衡
 * @author hf-hf
 * @date 2018/12/27 14:40
 */
public class MailManager {

    public static Map<String, Properties> propertiesMap = new ConcurrentHashMap<>();

    public static Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    public static void putProperties(String key, Properties properties){
        propertiesMap.put(key, properties);
    }

    public static void putSession(String key, Session session){
        sessionMap.put(key, session);
    }

    public static void putBoth(String key, Properties properties){
        putProperties(key, properties);
        // 此处要用 Session#getInstance，Session#getDefaultInstance 为单例
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty("mail.username"),
                        properties.getProperty("mail.password"));
            }
        });
        if(null != properties.getProperty("mail.debug")){
            session.setDebug(Boolean.valueOf(properties.getProperty("mail.debug")));
        }
        putSession(key, session);
    }

    public static void putBoth(Object key, Properties properties){
        putBoth(String.valueOf(key), properties);
    }

    public static Session getSession(String key){
        return sessionMap.get(key);
    }

    public static Properties getProperties(String key){
        return propertiesMap.get(key);
    }

}
