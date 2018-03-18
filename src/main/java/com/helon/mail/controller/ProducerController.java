package com.helon.mail.controller;

import com.helon.mail.constant.Const;
import com.helon.mail.entity.MailSend;
import com.helon.mail.enumeration.MailStatus;
import com.helon.mail.service.MailSendService;
import com.helon.mail.utils.KeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Helon
 * @Description:
 * @Data: Created in 2018/2/3 22:38
 * @Modified By:
 */
@RestController
public class ProducerController {
    /**日志*/
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerController.class);
    @Autowired
    private MailSendService mailSendService;
    /**
     * @Author: Helon
     * @Description: 邮件发送
     * {
    "sendTo": "381614569@qq.com",
    "sendUserId": "123",
    "sendContent": "hello",
    "sendPriority":"2"
    }
     * @param mailSend
     * @Data: 2018/2/4 11:11
     * @Modified By:
     */
    @RequestMapping(value = "/send", produces = {"application/json;charset=UTF-8"})
    public void send(@RequestBody(required = false)MailSend mailSend){
        //1、数据校验

        //2、初始化一些数据

        try {
            mailSend.setSendId(KeyUtil.generatorUUID());//生成ID
            mailSend.setSendCount(0L);
            mailSend.setSendStatus(MailStatus.DRAFT.getCode());
            mailSend.setVersion(0L);
            mailSend.setUpdateBy(Const.SYS_RUNTIME);
            mailSendService.insert(mailSend);
            //3、把数据扔到redis里去，并且更新数据库状态
            mailSendService.sendRedis(mailSend);
        } catch (Exception e) {
            LOGGER.error("[邮件发送生产端]-发送异常:{}", e);
            //抛出运行时异常
            throw new RuntimeException(e);
        }
    }
}
