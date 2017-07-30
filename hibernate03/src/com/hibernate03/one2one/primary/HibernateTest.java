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
		
		//设定关联关系
		department.setManager(manager);
		manager.setDepartment(department);
		
		//先插入哪一个都一样
		session.save(manager);
		session.save(department);
	}
	
	@Test
	public void testGet(){
		//1.默认情况下对挂链属性使用懒加载
		Department dept = (Department) session.get(Department.class, 1);
		System.out.println(dept.getDeptName());
		
		  //2.会出现懒加载异常，如下：
		 
		
		  //3.查询Manager对象的连接条件应该是dept.manager_id=manager.manager_id
		  //而不是dept.dept_id = manager.manager_id;
		  Manager manager = dept.getManager();
		  System.out.println(manager.getMgrName());
	}
	
	@Test
	public void testGet2(){
		  //在查询没有外键的实体对象时，使用的是左外连接，一并查询出相关联的对象，并已经进行初始化
		  Manager manager = (Manager) session.get(Manager.class, 1);
		  System.out.println(manager.getMgrName());
		  System.out.println(manager.getDepartment().getDeptName());
	}

}
