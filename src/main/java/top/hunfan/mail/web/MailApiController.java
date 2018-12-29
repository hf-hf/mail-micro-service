package top.hunfan.mail.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import top.hunfan.mail.domain.R;
import top.hunfan.mail.service.MailService;

/**
 * 邮件api
 * @author hefan
 * @date 2018/12/26 16:17
 */
@RestController
@RequestMapping(value = "/mail")
public class MailApiController {

    @Autowired
    private MailService service;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public R sendMail(String to, String title, String content, String attachmentName,
                      MultipartFile attachmentFile) throws Throwable {
        return R.operate(service.sendMail(to, title, content,
                attachmentName, attachmentFile));
    }
}