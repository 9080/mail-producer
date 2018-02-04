package com.helon.mail.service;

import com.helon.mail.entity.MailSend;
import com.helon.mail.enumeration.MailStatus;
import com.helon.mail.enumeration.RedisPriorityQueue;
import com.helon.mail.mapper.MailSend1Mapper;
import com.helon.mail.mapper.MailSend2Mapper;
import com.helon.mail.utils.FastJsonConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: Helon
 * @Description: 邮件发送service
 * @Data: Created in 2018/2/4 11:15
 * @Modified By:
 */
@Service
public class MailSendService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailSendService.class);
    @Resource
    private MailSend1Mapper mailSend1Mapper;
    @Resource
    private MailSend2Mapper mailSend2Mapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * @Author: Helon
     * @Description: 插入发送记录
     * @param mailSend
     * @Data: 2018/2/4 11:23
     * @Modified By:
     */
    public void insert(MailSend mailSend) {
        //获取ID的hashcode
        int hashCode = mailSend.getSendId().hashCode();
        if (hashCode % 2 == 0) {
            mailSend1Mapper.insert(mailSend);
        } else {
            mailSend2Mapper.insert(mailSend);
        }
    }

    /**
     * @Author: Helon
     * @Description: 将发送信息存入到redis队列中
     * @param mailSend
     * @Data: 2018/2/4 15:02
     * @Modified By:
     */
    public void sendRedis(MailSend mailSend) {
       ListOperations<String, String> listOperations = redisTemplate.opsForList();
       Long priority = mailSend.getSendPriority();
       Long ret = 0L;
       Long size = 0L;
        if (priority < 4L) {
            //进入延迟队列
            ret = listOperations.rightPush(RedisPriorityQueue.DEFER_QUEUE.getCode(), FastJsonConvertUtil.convertObjectToJSON(mailSend));
            size = listOperations.size(RedisPriorityQueue.DEFER_QUEUE.getCode());
        } else if (priority > 3L && priority < 7L) {
            //进入普通队列
            ret = listOperations.rightPush(RedisPriorityQueue.NORMAL_QUEUE.getCode(), FastJsonConvertUtil.convertObjectToJSON(mailSend));
            size = listOperations.size(RedisPriorityQueue.NORMAL_QUEUE.getCode());
        } else {
            //紧急队列
            ret = listOperations.rightPush(RedisPriorityQueue.FAST_QUEUE.getCode(), FastJsonConvertUtil.convertObjectToJSON(mailSend));
            size = listOperations.size(RedisPriorityQueue.FAST_QUEUE.getCode());
        }
        //发送次数加1
        mailSend.setSendCount(mailSend.getSendCount() + 1);
        if (ret == size) {
            LOGGER.info("[邮件生产端]-进入队列成功，id:{}", mailSend.getSendId());
            mailSend.setSendStatus(MailStatus.SEND_IN.getCode());
            if (mailSend.getSendId().hashCode() % 2 == 0) {
                mailSend1Mapper.updateByPrimaryKeySelective(mailSend);
            } else {
                mailSend2Mapper.updateByPrimaryKeySelective(mailSend);
            }
        } else {
            LOGGER.info("[邮件生产端]-进入队列失败，等待轮询机制重新投递，id:{}", mailSend.getSendId());
            mailSend.setSendStatus(MailStatus.NEED_ERR.getCode());
            if (mailSend.getSendId().hashCode() % 2 == 0) {
                mailSend1Mapper.updateByPrimaryKeySelective(mailSend);
            } else {
                mailSend2Mapper.updateByPrimaryKeySelective(mailSend);
            }
        }

    }

}
