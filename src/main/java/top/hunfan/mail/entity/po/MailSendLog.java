package top.hunfan.mail.entity.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 邮件日志
 * @author hf-hf
 * @date 2019/1/8 15:35
 */
@Entity
@Table(name = "mail_send_log")
@Data
public class MailSendLog {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "form", length = 100)
    private String form;

    @Column(name = "to", length = 100)
    private String to;

    @Column(name = "title", length = 20)
    private String title;

    @Column(name = "content", length = 300)
    private String content;

    @Column(name = "attachmentName", length = 20)
    private String attachmentName;

    @Column(name = "ip", length = 20)
    private String ip;

    /**
     * 发送状态
     * ex：success:200，fail:500
     */
    @Column(name = "sent_code", length = 3)
    private Integer sentCode;

    @Column(name = "sent_message", length = 20)
    private String sentMessage;

    /**
     * 创建时间
     * 默认年月日时分秒 @Temporal(TemporalType.TIMESTAMP)
     */
    @Column(name = "createTime")
    private Date createTime;

}
