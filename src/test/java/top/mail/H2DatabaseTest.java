package top.mail;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;
import top.hunfan.mail.MailMicroServiceApplication;
import top.hunfan.mail.domain.Code;
import top.hunfan.mail.entity.po.MailSendLog;
import top.hunfan.mail.repository.MailSendLogRepository;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MailMicroServiceApplication.class)
public class H2DatabaseTest {

    @Autowired
    private MailSendLogRepository mailSendLogRepository;

    @Test
    public void save(){
        MailSendLog mailSendLog = new MailSendLog();
        mailSendLog.setForm("1234@qq.com");
        mailSendLog.setTo("xxxx@qq.com");
        mailSendLog.setTitle("测试");
        mailSendLog.setContent("12321");
        mailSendLog.setAttachmentName("123.jpg");
        mailSendLog.setIp("127.0.0.1");
        mailSendLog.setSentCode(Code.SUCCEED.getCode());
        mailSendLog.setCreateTime(new Date());
        MailSendLog save = mailSendLogRepository.save(mailSendLog);
        Assert.notNull(save,"save error！");
    }

    @Test
    public void findAll(){
        Page<MailSendLog> list = mailSendLogRepository.findAll(PageRequest.of(0, 1));
        Assert.notNull(list,"findAll error！");
    }

}
