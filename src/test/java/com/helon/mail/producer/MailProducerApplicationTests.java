package com.helon.mail.producer;

import com.github.pagehelper.PageHelper;
import com.helon.mail.entity.MailSend;
import com.helon.mail.entity.MstDict;
import com.helon.mail.mapper.MailSend1Mapper;
import com.helon.mail.mapper.MailSend2Mapper;
import com.helon.mail.mapper.MstDictMapper;
import com.helon.mail.service.MstDictService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MailProducerApplication.class)
public class MailProducerApplicationTests {
	@Resource(name="masterDataSource")
	private DataSource masterDataSource;
	@Resource(name="slaveDataSource")
	private DataSource slaveDataSource;
	@Resource
	private MstDictMapper mstDictMapper;
	@Autowired
	private MstDictService mstDictService;
	@Autowired
	private MailSend1Mapper mailSend1Mapper;
	@Autowired
	private MailSend2Mapper mailSend2Mapper;

	@Test
	public void contextLoads() throws Exception{
		Connection c1 = masterDataSource.getConnection("root", "root");
		System.out.println(c1.getMetaData().getURL());
		Connection c2 = slaveDataSource.getConnection("root", "root");
		System.out.println(c2.getMetaData().getURL());
	}

	@Test
	public void test1() throws Exception {
		PageHelper.startPage(1, 2, false);
		List<MstDict> list = mstDictMapper.selectAll();
		for (MstDict md : list) {
			System.out.println(md.getName());
		}
	}

	@Test
	public void testMail() throws Exception{

		MailSend mailSend = mailSend1Mapper.selectById("cebff4ae-29eb-11e8-aa5a-60eb69557725");
//		System.out.println(mailSend.getSendId());
	}

	@Test
	public void test2() throws Exception {
		List<MstDict> list = mstDictService.findByStatus(1);
		for (MstDict md : list) {
			System.out.println(md.getName());
		}
	}

}
