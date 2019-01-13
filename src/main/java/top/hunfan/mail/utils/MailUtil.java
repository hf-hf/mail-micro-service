package top.hunfan.mail.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;
import top.hunfan.mail.domain.Constants;
import top.hunfan.mail.roundrobin.RoundRobin;
import top.hunfan.mail.roundrobin.RoundRobinFactory;

/**
 * 邮件发送，支持多邮箱配置、轮询、加权轮询
 * @author hf-hf
 * @date 2018/12/27 10:58
 */
@Slf4j
public class MailUtil {

    private static final String MAIL_PROPERTIES = "mail%d.properties";

    private static final String CHART_SET_UTF8 = "UTF-8";

    private static final String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";

    private static final String READ_SYSTEM_PATH_FILE = "READ_SYSTEM_PATH_FILE";

    private static final String READ_CLASS_PATH_FILE = "READ_CLASS_PATH_FILE";

    private boolean initComplete = false;

    private RoundRobin roundRobin;

    /**
     * 通过单例对象获取
     * @author hf-hf
     * @date 2018/12/27 14:59
     * @param roundRobinType    轮询类型
     */
    private MailUtil(String roundRobinType) {
        //优先读取外部配置文件
        readConfigFiles(READ_SYSTEM_PATH_FILE);
        if(MailManager.propertiesMap.isEmpty()){
            log.info("read system path is null");
            readConfigFiles(READ_CLASS_PATH_FILE);
        }
        if(!MailManager.propertiesMap.isEmpty()){
            roundRobin = RoundRobinFactory.create(roundRobinType,
                    MailManager.propertiesMap.values());
        }
        if(null != roundRobin){
            initComplete = true;
        }
        log.info("load mail properties finish,success count={}", MailManager.propertiesMap.size());
    }

    /**
     * 读取外部配置
     * @param fileName
     * @return
     */
    private InputStream readSystemPath(String fileName){
        String path = System.getProperty("user.dir") + File.separator + fileName;
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            // ignore
        }
        return in;
    }

    /**
     * 读取内部文件
     * @author hf-hf
     * @date 2018/12/29 9:25
     */
    private InputStream readClassPath(String fileName){
        return this.getClass().getClassLoader().getResourceAsStream(fileName);
    }

    /**
     * 读取配置文件
     * @author hf-hf
     * @date 2018/12/29 9:34
     */
    private void readConfigFiles(String readPath){
        InputStream in = null;
        Properties properties = null;
        String fileName = "";
        for (int i = 0; ; i++) {
            fileName = String.format(MAIL_PROPERTIES, i);
            in = READ_SYSTEM_PATH_FILE.equals(readPath)
                    ? readSystemPath(fileName) : readClassPath(fileName);
            if (in == null) {
                break;
            }
            log.info("load " + fileName);
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                properties = new Properties();
                properties.load(reader);
                if(!checkMailConifg(properties)){
                    //throw new PropertyException(readPath + " " + fileName + "config error!");
                    log.info(readPath + " " + fileName + "config error!");
                    break;
                }
                MailManager.putBoth(properties.get("mail.id"), properties);
            } catch (Exception e) {
                log.error("load " + readPath + " " + fileName + "error！",e);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("in close error", e);
                }
            }
        }
    }

    /**
     * 校验mail配置项
     * @author hf-hf
     * @date 2018/12/28 16:03
     */
    private boolean checkMailConifg(Properties properties){
        if(null == properties.get("mail.id")){
            log.error("mail id is not null!");
            return false;
        }
        if(null == properties.get("mail.username")
                || "".equals(properties.get("mail.username"))){
            log.error("mail username is not null!");
            return false;
        }
        if(null == properties.get("mail.password")
                || "".equals(properties.get("mail.password"))){
            log.error("mail password is not null!");
            return false;
        }
        if(null != properties.get("mail.weight")
                && !(StringUtils.isNumeric((String) properties.get("mail.weight")))){
            log.error("mail weight requirement is number!");
            return false;
        }
        if(null == properties.getProperty("mail.isAvailable")){
            log.error("mail isAvailable is not null!");
            return false;
        }
        try {
            Boolean tmp = Boolean.valueOf((String) properties.get("mail.isAvailable"));
        } catch (Exception e){
            log.error("mail isAvailable requirement is boolean!");
            return false;
        }
        return true;
    }

    /**
     * 发送邮件
     * @param to            收件人，多个收件人用 {@code ;} 分隔
     * @param subject       主题
     * @param content       内容
     * @return              如果邮件发送成功，则返回 {@code true}，否则返回 {@code false}
     */
    public boolean send(String to, String subject, String content) {
        return send(to, null, null, subject,
                content, null, null);
    }

    /**
     * 发送邮件
     * @param to            收件人，多个收件人用 {@code ;} 分隔
     * @param subject       主题
     * @param content       内容
     * @param attachment    附件
     * @return              如果邮件发送成功，则返回 {@code true}，否则返回 {@code false}
     */
    public boolean send(String to, String subject, String content, File attachment) {
        File[] attachments = null;
        if(null != attachment){
            attachments = new File[]{attachment};
        }
        return send(to, null, null, subject,
                content, null, attachments);
    }

    /**
     * 发送邮件(负载均衡)
     *
     * @param key           负载均衡key
     * @param to            收件人，多个收件人用 {@code ;} 分隔
     * @param cc            抄送人，多个抄送人用 {@code ;} 分隔
     * @param bcc           密送人，多个密送人用 {@code ;} 分隔
     * @param subject       主题
     * @param content       内容，可引用内嵌图片，引用方式：{@code <img src="cid:内嵌图片文件名" />}
     * @param images        内嵌图片
     * @param attachments   附件
     * @return              如果邮件发送成功，则返回 {@code true}，否则返回 {@code false}
     */
    public boolean sendByLoadBalance(String key, String to, String cc,
                                     String bcc, String subject, String content,
                                     File[] images, File[] attachments){
        log.info("loadBalanceKey={}", key);
        Properties properties = MailManager.getProperties(key);
        log.info("properties={}", properties);
        Session session = MailManager.getSession(key);
        MimeMessage message = new MimeMessage(session);
        String username = properties.getProperty("mail.username");
        ThreadLocalUtils.put(Constants.CURRENT_MAIL_FROM, username);
        try {
            message.setFrom(new InternetAddress(username));
            addRecipients(message, Message.RecipientType.TO, to);
            if (cc != null) {
                addRecipients(message, Message.RecipientType.CC, cc);
            }
            if (bcc != null) {
                addRecipients(message, Message.RecipientType.BCC, bcc);
            }
            message.setSubject(subject, CHART_SET_UTF8);
            // 最外层部分
            MimeMultipart wrapPart = new MimeMultipart("mixed");
            MimeMultipart htmlWithImageMultipart = new MimeMultipart("related");
            // 文本部分
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(content, CONTENT_TYPE_HTML);
            htmlWithImageMultipart.addBodyPart(htmlPart);
            // 内嵌图片部分
            addImages(images, htmlWithImageMultipart);
            MimeBodyPart htmlWithImageBodyPart = new MimeBodyPart();
            htmlWithImageBodyPart.setContent(htmlWithImageMultipart);
            wrapPart.addBodyPart(htmlWithImageBodyPart);
            // 附件部分
            addAttachments(attachments, wrapPart);
            message.setContent(wrapPart);
            Transport.send(message);
            return true;
        } catch (Exception e) {
            log.error("sendByLoadBalance error!", e.getMessage(),
                    "loadBalanceKey={}, properties={}, to={}, cc={}, "
                    + "bcc={}, subject={}, content={}, images={}, attachments={}",
                    key, properties, to, cc,
                    bcc, subject, content, images, attachments, e);
        }
        return false;
    }

    /**
     * 发送邮件
     *
     * @param to            收件人，多个收件人用 {@code ;} 分隔
     * @param cc            抄送人，多个抄送人用 {@code ;} 分隔
     * @param bcc           密送人，多个密送人用 {@code ;} 分隔
     * @param subject       主题
     * @param content       内容，可引用内嵌图片，引用方式：{@code <img src="cid:内嵌图片文件名" />}
     * @param images        内嵌图片
     * @param attachments   附件
     * @return              如果邮件发送成功，则返回 {@code true}，否则返回 {@code false}
     */
    public boolean send(String to, String cc, String bcc,
                        String subject, String content,
                        File[] images, File[] attachments) {
        if(!initComplete){
            throw new ExceptionInInitializerError("mail init error！");
        }
        // 负载均衡实现
        String key = roundRobin.select().id();
        if(StringUtils.isEmpty(key)){
            //TODO invoker降级，移除
            throw new RuntimeException("轮询异常！");
        }
        return sendByLoadBalance(key, to, cc, bcc, subject, content, images, attachments);
    }

    /**
     * 追加附件
     * @author hf-hf
     * @date 2018/12/27 16:53
     * @param attachments
     * @param wrapPart
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    private void addAttachments(File[] attachments, MimeMultipart wrapPart)
            throws MessagingException, UnsupportedEncodingException {
        if (null != attachments && attachments.length > 0) {
            for (int i = 0; i < attachments.length; i++) {
                MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                DataHandler dataHandler = new DataHandler(new FileDataSource(attachments[i]));
                String fileName = dataHandler.getName();
                attachmentBodyPart.setDataHandler(dataHandler);
                // 显示指定文件名（防止文件名乱码）
                attachmentBodyPart.setFileName(MimeUtility.encodeText(fileName));
                wrapPart.addBodyPart(attachmentBodyPart);
            }
        }
    }

    /**
     * 追加内嵌图片
     * @author hf-hf
     * @date 2018/12/27 16:53
     * @param images
     * @param multipart
     * @throws MessagingException
     */
    private void addImages(File[] images, MimeMultipart multipart) throws MessagingException {
        if (null != images && images.length > 0) {
            for (int i = 0; i < images.length; i++) {
                MimeBodyPart imagePart = new MimeBodyPart();
                DataHandler dataHandler = new DataHandler(new FileDataSource(images[i]));
                imagePart.setDataHandler(dataHandler);
                imagePart.setContentID(images[i].getName());
                multipart.addBodyPart(imagePart);
            }
        }
    }

    /**
     * 追加发件人
     * @author hf-hf
     * @date 2018/12/27 15:36
     */
    private void addRecipients(MimeMessage message, Message.RecipientType type,
                               String recipients) throws MessagingException {
        String[] addresses = recipients.split(";");
        for (int i = 0; i < addresses.length; i++) {
            message.addRecipients(type, addresses[i]);
        }
    }

    /**
     * 静态内部类
     */
    private static class MailUtilHolder {
        private static Environment env = ApplicationContextUtils.getBean("environment");
        private static final MailUtil instance =
                new MailUtil(env.getProperty("mail.roundrobin.type"));
    }

    /**
     * 获取邮件发送工具
     * @return
     */
    public static final MailUtil getInstance() {
        return MailUtilHolder.instance;
    }

}