package top.hunfan.mail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import top.hunfan.mail.entity.po.MailSendLog;
import top.hunfan.mail.repository.MailSendLogRepository;

/**
 * 邮件发送日志
 * @author hf-hf
 * @date 2019/1/9 14:09
 */
@Service
public class MailSendLogService {

    @Autowired
    private MailSendLogRepository sendLogRepository;

    public Page<MailSendLog> findByPage(int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return sendLogRepository.findAll(PageRequest.of(page, size, sort));
    }

    public void clean(){
        sendLogRepository.deleteAll();
    }

    public void delete(Integer id){
        sendLogRepository.deleteById(id);
    }

    public MailSendLog save(MailSendLog mailSendLog){
        return sendLogRepository.save(mailSendLog);
    }

}
