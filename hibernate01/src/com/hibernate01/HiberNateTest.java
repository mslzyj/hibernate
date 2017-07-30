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
		//1.创建一个SessionFactory对象，是线程安全的
		SessionFactory sessionFactory = null;
		//(1)创建Configuration对象，对应hibernate的基本配置信息和对象关系映射信息
		Configuration configuration = new Configuration().configure();
		//(2)创建一个ServiceRegistry对象；hibernate任何配置度需要在该对象中注册后才能有效
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		//(3)
		sessionFactory =configuration.buildSessionFactory(serviceRegistry);
		//2.创建一个Session对象
		Session session = sessionFactory.openSession();
		//3.开启事务
		org.hibernate.Transaction transaction = session.beginTransaction();
		
		//4.执行保存操作
		News news = new News("java","test",new Date(new java.util.Date().getTime()));
		session.save(news);
		/**
		 * 获取数据库中数据
		 * News news2 = (News) session.get(News.class, 1);
		 * System.out.println(news2);
		 */
		//5.提交事务
		transaction.commit();
		//6.关闭Session
		session.close();
		//7.关闭SessionFactory
		sessionFactory.close();
	}

}
