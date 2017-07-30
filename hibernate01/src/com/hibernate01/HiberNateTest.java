package com.hibernate01;

import java.sql.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Test;

public class HiberNateTest {

	@Test
	public void test() {
		//1.����һ��SessionFactory�������̰߳�ȫ��
		SessionFactory sessionFactory = null;
		//(1)����Configuration���󣬶�Ӧhibernate�Ļ���������Ϣ�Ͷ����ϵӳ����Ϣ
		Configuration configuration = new Configuration().configure();
		//(2)����һ��ServiceRegistry����hibernate�κ����ö���Ҫ�ڸö�����ע��������Ч
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		//(3)
		sessionFactory =configuration.buildSessionFactory(serviceRegistry);
		//2.����һ��Session����
		Session session = sessionFactory.openSession();
		//3.��������
		org.hibernate.Transaction transaction = session.beginTransaction();
		
		//4.ִ�б������
		News news = new News("java","test",new Date(new java.util.Date().getTime()));
		session.save(news);
		/**
		 * ��ȡ���ݿ�������
		 * News news2 = (News) session.get(News.class, 1);
		 * System.out.println(news2);
		 */
		//5.�ύ����
		transaction.commit();
		//6.�ر�Session
		session.close();
		//7.�ر�SessionFactory
		sessionFactory.close();
	}

}
