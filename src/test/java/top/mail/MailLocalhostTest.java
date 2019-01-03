package top.mail;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import top.hunfan.mail.MailMicroServiceApplication;
import top.hunfan.mail.utils.MailUtil;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MailMicroServiceApplication.class)
public class MailLocalhostTest {

    private static final String MAIL_URL = "http://127.0.0.1:12345/api/v0.0.1/mail/send";

    private static final String EQUAL_SIGN = "=";

    //发送多收件人，使用;分隔收件人邮箱
    private static final String TEST_MAIL = "xxxxx@qq.com";

    private static final String PARAM_SEPARATOR = "&";

    @Test
    public void testSendTextMaiLocal1() {
        StringBuilder builder = new StringBuilder(MAIL_URL);
        builder.append("to")
                .append(EQUAL_SIGN)
                .append(TEST_MAIL)
                .append(PARAM_SEPARATOR)
                .append("title")
                .append("我是没有附件的主题")
                .append(PARAM_SEPARATOR)
                .append("content")
                .append("我是没有附件的内容");
        RestTemplate rest = new RestTemplate();
        assertTrue(rest.postForObject(builder.toString(), null, Boolean.class));
    }

    @Test
    public void testSendAttachmentMailLocal() throws Throwable {
        File folder = new File("F://");
        if (!folder.exists() || !folder.isDirectory()) {
            FileUtils.forceMkdir(folder);
        }

        File attachmentFile = new File(folder, "1.txt");
        if (attachmentFile.exists() && attachmentFile.isFile()) {
            FileUtils.forceDelete(attachmentFile);
        }

        FileUtils.writeStringToFile(attachmentFile, "hello \r\n mail \r\n" +
                RandomStringUtils.random(10), "utf8");
        attachmentFile.createNewFile();

        MultiValueMap<String, Object> param = new LinkedMultiValueMap<String, Object>();
        param.add("to", TEST_MAIL);
        param.add("title", RandomStringUtils.random(5));
        param.add("content", RandomStringUtils.random(256));
        param.add("attachmentName", RandomStringUtils.random(4) + ".txt");

        FileSystemResource resource = new FileSystemResource(attachmentFile);
        param.add("attachmentFile", resource);

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(param);

        RestTemplate rest = new RestTemplate();
        ResponseEntity<Boolean> response = rest.exchange(MAIL_URL, HttpMethod.POST,
                httpEntity, Boolean.class);

        assertTrue(response.getBody());
    }

    @Test
    public void parallelSendTest(){
        String to = TEST_MAIL;
        String subject = "并发邮件主题";
        String content = "并发邮件内容";

        List<CompletableFuture<Boolean>> tasks = Arrays.asList(new int[]{1, 2, 3}).stream()
                .map(n -> CompletableFuture.supplyAsync(() -> {
                    for(int i= 0;i < 3;i++){
                        MailUtil.getInstance().send(to, subject + n, content + n);
                        if(i == 2){
                            log.debug(Thread.currentThread().getId() + " finish!");
                        }
                    }
                    return true;
                })).collect(Collectors.toList());

        tasks.stream().map(CompletableFuture::join)
                .forEach(r -> log.debug(r + ""));
    }

}
