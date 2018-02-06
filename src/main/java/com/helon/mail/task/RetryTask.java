package com.helon.mail.task;

import com.helon.mail.config.database.ReadOnlyConnection;
import com.helon.mail.entity.MailSend;
import com.helon.mail.mapper.MailSend1Mapper;
import com.helon.mail.mapper.MailSend2Mapper;
import com.helon.mail.service.MailSendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Helon
 * @Description:
 * @Data: Created in 2018/2/6 22:33
 * @Modified By:
 */
@Component
public class RetryTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetryTask.class);

    @Autowired
    private MailSendService mailSendService;



    @Scheduled(initialDelay = 5000, fixedDelay = 10000)
    public void retry() {
        LOGGER.info("[发送邮件客户端]-轮询开始，每隔10秒执行一次");
        List<MailSend> list = mailSendService.queryDraftList();
    }


}
