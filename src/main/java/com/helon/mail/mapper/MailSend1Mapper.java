package com.helon.mail.mapper;
import com.helon.mail.config.database.BaseMapper;
import com.helon.mail.entity.MailSend;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
public interface MailSend1Mapper extends BaseMapper<MailSend> {

    int insert(MailSend record);

    int updateByPrimaryKeySelective(MailSend record);

    List<MailSend> queryDraftList();

    MailSend selectById(@Param("sendId") String sendId);

}