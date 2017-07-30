package com.hibernate.subclass;

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
	 * 1.对于子类对象，只需把记录插入到一张数据表中
	 * 2.辨别着列由hibernate自动维护
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
	 * 1.查询父类记录，只需要查询一张数据表
	 * 2.对于子类:也只需要查询一张数据表
	 * 缺点：
	 * 1.使用了辨别折列
	 * 2.子类独有的字段不能添加非空约束
	 * 3.若继承参差比较深，则数据表的字段也会较多
	 */
	@Test
	public void testQuery(){
	   List<Person> persons = session.createQuery("FROM Person").list();
	   System.out.println(persons.size());
	   List<Student> students = session.createQuery("FROM Person").list();
	   System.out.println(students.size());
	}

}
