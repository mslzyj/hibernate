package com.hibernate.joined.subclass;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Hibernatetest {
	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transcation;

	@Before
	public void init() {
		Configuration configuration = new Configuration().configure();
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties())
				.buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		session = sessionFactory.openSession();
		transcation = session.beginTransaction();

		// System.out.println("init");
	}

	@After
	public void destroy() {
		transcation.commit();
		session.close();
		sessionFactory.close();

		// System.out.println("destroy");
	}

	/**
	 * 插入操作：
	 * 1.对于子类对象，至少需要插入两种数据表中，插入性能比第一个差
	 */
	@Test
	public void testSave() {
      Person person =new Person();
      person.setAge(20);
      person.setName("AA");
      
      session.save(person);
      
      Student student = new Student();
      student.setAge(10);
      student.setName("BB");
      student.setSchool("qianghua");
      session.save(student);
      
	}
	
	
	/**
	 * 查询：
	 * 1.查询父类记录，只需要查询多张数据表
	 * 2.对于子类:做一个内连接查询
	 * 优点：
	 * 1.不需要使用辨别着列
	 * 2.子类独有的字段能添加非空约束
	 * 3.没有冗余的字段
	 */
	@Test
	public void testQuery(){
	   List<Person> persons = session.createQuery("FROM Person").list();
	   System.out.println(persons.size());
	   List<Student> students = session.createQuery("FROM Person").list();
	   System.out.println(students.size());
	}

}
