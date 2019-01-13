package top.hunfan.mail.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import top.hunfan.mail.domain.R;
import top.hunfan.mail.service.MailSendLogService;
import top.hunfan.mail.service.MailService;

/**
 * 邮件api
 * @author hf-hf
 * @date 2018/12/26 16:17
 */
@RestController
@RequestMapping(value = "/mail")
public class MailApiController {

    @Autowired
    private MailService service;

    @Autowired
    private MailSendLogService sendLogService;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public R sendMail(String to, String title, String content, String attachmentName,
                      MultipartFile attachmentFile) {
        return service.send(to, title, content, attachmentName, attachmentFile);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R list(int page, int size) {
        return R.success(sendLogService.findByPage(page, size));
    }

    @RequestMapping(value = "/clean", method = RequestMethod.POST)
    public R clean() {
        sendLogService.clean();
        return R.success();
    }

}