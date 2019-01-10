package top.hunfan.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import top.hunfan.mail.entity.po.MailSendLog;

public interface MailSendLogRepository extends JpaRepository<MailSendLog, Integer> {
}
