package top.hunfan.mail.aspect;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.hunfan.mail.domain.Constants;
import top.hunfan.mail.domain.R;
import top.hunfan.mail.entity.po.MailSendLog;
import top.hunfan.mail.service.MailSendLogService;
import top.hunfan.mail.utils.StringTools;
import top.hunfan.mail.utils.ThreadLocalUtils;

/**
 * 邮件发送日志切面
 * @author hf-hf
 * @date 2019/1/9 14:15
 */
@Slf4j
@Aspect
@Component
@Order(2)
public class MailSendLogAspect {

    /**
     * 定义线程池
     * @author hf-hf
     * @date 2019/1/10 11:56
     */
    private final ExecutorService SAVE_LOG_EXECUTOR_SERVICE = Executors.newFixedThreadPool(10);

    @Autowired
    private MailSendLogService sendLogService;

    /**
     * 定义切点
     * @author hf-hf
     * @date 2019/1/10 11:56
     */
    @Pointcut("execution(public * top.hunfan.mail.service.MailService.send(..)) ")
    public void log(){
        
    }
    
    @AfterReturning(returning="result", pointcut="log()")
    public void doAfterReturning(JoinPoint joinPoint, R result) {
        ServletRequestAttributes sra =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        // 下面两个数组中，参数值和参数名的个数和位置是一一对应的。
        // 参数值
        Object[] args = joinPoint.getArgs();
        // 参数名
        String[] argNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();
        // 异步存储日志
        CompletableFuture.runAsync(new SaveLogThread(sendLogService, args, argNames,
                ThreadLocalUtils.get(Constants.CURRENT_MAIL_FROM), request.getRemoteHost(),
                result.getCode(), result.getMessage()), SAVE_LOG_EXECUTOR_SERVICE);
        log.debug("方法返回值：" + result);
    }

    /**
     * 保存日志线程
     * @author hf-hf
     * @date 2019/1/10 11:52
     */
    @AllArgsConstructor
    public static class SaveLogThread extends Thread{

        private MailSendLogService sendLogService;

        private Object[] args;

        private String[] argNames;

        private String form;

        private String ip;

        private Integer code;

        private String message;

        @Override
        public void run() {
            ThreadLocalUtils.remove(Constants.CURRENT_MAIL_FROM);
            MailSendLog sendLog = new MailSendLog();
            for(int i=0;i < argNames.length;i++){
                if("attachmentFile".equals(argNames[i])){
                    continue;
                }
                Field field = ReflectionUtils.findField(sendLog.getClass(), argNames[i]);
                Column column = field.getAnnotation(Column.class);
                field.setAccessible(true);
                String objStr = null == column ? StringTools.asString(args[i])
                        : StringTools.asString(args[i], column.length());
                if(null != objStr){
                    try {
                        field.set(sendLog, objStr);
                    } catch (IllegalAccessException e) {

                    }
                }
            }
            sendLog.setForm(this.form);
            sendLog.setIp(this.ip);
            sendLog.setSentCode(this.code);
            sendLog.setSentMessage(this.message);
            sendLog.setCreateTime(new Date());
            sendLogService.save(sendLog);
        }
    }
}