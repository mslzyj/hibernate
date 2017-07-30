package com.hibernate03.one2one.primary;

import static org.junit.Assert.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HibernateTest {

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
	@Test
	public void testSave() {
		Department department = new Department();
		department.setDeptName("DEPT-AA");
  
		Manager manager = new Manager();
		manager.setMgrName("MGR-AA");
		
		//�趨������ϵ
		department.setManager(manager);
		manager.setDepartment(department);
		
		//�Ȳ�����һ����һ��
		session.save(manager);
		session.save(department);
	}
	
	@Test
	public void testGet(){
		//1.Ĭ������¶Թ�������ʹ��������
		Department dept = (Department) session.get(Department.class, 1);
		System.out.println(dept.getDeptName());
		
		  //2.������������쳣�����£�
		 
		
		  //3.��ѯManager�������������Ӧ����dept.manager_id=manager.manager_id
		  //������dept.dept_id = manager.manager_id;
		  Manager manager = dept.getManager();
		  System.out.println(manager.getMgrName());
	}
	
	@Test
	public void testGet2(){
		  //�ڲ�ѯû�������ʵ�����ʱ��ʹ�õ����������ӣ�һ����ѯ��������Ķ��󣬲��Ѿ����г�ʼ��
		  Manager manager = (Manager) session.get(Manager.class, 1);
		  System.out.println(manager.getMgrName());
		  System.out.println(manager.getDepartment().getDeptName());
	}

}
