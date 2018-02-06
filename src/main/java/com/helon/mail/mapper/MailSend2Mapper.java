package com.helon.mail.mapper;


import com.helon.mail.config.database.BaseMapper;
import com.helon.mail.entity.MailSend;

import java.util.List;

public interface MailSend2Mapper extends BaseMapper<MailSend> {

    int insert(MailSend record);

    int updateByPrimaryKeySelective(MailSend record);

    List<MailSend> queryDraftList();
}