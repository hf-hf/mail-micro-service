package top.hunfan.mail.service;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import top.hunfan.mail.domain.R;
import top.hunfan.mail.utils.MailUtil;

/**
 * 邮件服务
 * @author hf-hf
 * @date 2018/12/26 16:13
 */
@Service
public class MailService {

	@Value("${file.folder}")
	private String fileFolder;

	/**
	 * 发送邮件
	 * @author hf-hf
	 * @date 2018/12/26 16:14
	 * @param to				收件人
	 * @param title				标题
	 * @param content			内容
	 * @param attachmentFile	附件
	 * @throws Throwable
	 */
	public boolean sendMail(String to, String title, String content, String attachmentName,
                      MultipartFile attachmentFile) throws Throwable {
		File tempFile = null;
		if (attachmentFile != null) {
			File tempFolder = new File(fileFolder);
			if (!tempFolder.exists() || !tempFolder.isDirectory()) {
				FileUtils.forceMkdir(tempFolder);
			}
			//attachmentFile.getOriginalFilename() + RandomStringUtils.randomNumeric(5)
			tempFile = new File(fileFolder, attachmentName);
			tempFile.createNewFile();
			attachmentFile.transferTo(tempFile);
		}
        return MailUtil.getInstance().send(to, title, content, tempFile);
	}

    /**
     * 发送邮件
     * @author hf-hf
     * @date 2018/12/26 16:14
     * @param to				收件人
     * @param title				标题
     * @param content			内容
     * @param attachmentFile	附件
     */
	public R send(String to, String title, String content, String attachmentName,
                  MultipartFile attachmentFile){
        try {
            return R.operate(sendMail(to, title, content,
                    attachmentName, attachmentFile));
        } catch (Throwable e){
            return R.fail(e.getMessage());
        }
    }

}
