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
	 * ���������
	 * 1.�����������������Ҫ�����������ݱ��У��������ܱȵ�һ����
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
	 * ��ѯ��
	 * 1.��ѯ�����¼��ֻ��Ҫ��ѯ�������ݱ�
	 * 2.��������:��һ�������Ӳ�ѯ
	 * �ŵ㣺
	 * 1.����Ҫʹ�ñ������
	 * 2.������е��ֶ�����ӷǿ�Լ��
	 * 3.û��������ֶ�
	 */
	@Test
	public void testQuery(){
	   List<Person> persons = session.createQuery("FROM Person").list();
	   System.out.println(persons.size());
	   List<Student> students = session.createQuery("FROM Person").list();
	   System.out.println(students.size());
	}

}
