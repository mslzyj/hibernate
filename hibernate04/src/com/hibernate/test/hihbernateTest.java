package com.hibernate.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hibernate.dao.DepartmentDao;
import com.hibernate.entities.Department;
import com.hibernate.entities.Employee;
import com.hibernate.hibernate.HibernateUtils;

import net.sf.ehcache.search.expression.Criteria;

public class hihbernateTest {
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
	 * �༶��Ķ�������,��ѯ���Σ�ֻ��ӡһ��sql���
	 */
	@Test
	public void testHibernateSecondLevelCache() {
		Employee employee1 = (Employee) session.get(Employee.class, 1);
		System.out.println(employee1.getName());
		
		transcation.commit();
		session.close();
		
		session = sessionFactory.openSession();
		transcation = session.beginTransaction();
		
		Employee employee2 = (Employee) session.get(Employee.class, 1);
		System.out.println(employee2.getName());
	}
	
	
	/**
	 * ���ϼ���Ķ�������
	 */
	@Test
	public void testCollectionSecondLevelCache(){
		Department department = (Department) session.get(Department.class, 1);
		System.out.println(department.getName());
		System.out.println(department.getEmps().size());
		
		transcation.commit();
		session.close();
		
		session = sessionFactory.openSession();
		transcation = session.beginTransaction();
		
		Department department2 = (Department) session.get(Department.class, 1);
		System.out.println(department2.getName());
		System.out.println(department2.getEmps().size());
		
	}
	
	
	/**
	 * ��ѯ����
	 */
	@Test
	public void testQueryCache(){
		Query query = session.createQuery("FROM Employee");
		//���ò�ѯ����
		query.setCacheable(true);
		
		List<Employee> emps = query.list();
		System.out.println(emps.size());
		
		emps = query.list();
		System.out.println(emps.size());
		
		/*���ò�ѯ�����
		 * Criteria criteria = (Criteria) session.createCriteria(Employee.class);
		((Query) criteria).setCacheable(true);*/
	}
	
	/**
	 * ����ʱ�������
	 */
	@Test
	public void testUpdateTimeStampCache(){
		Query query = session.createQuery("FROM Employee");
		//���ò�ѯ����
		query.setCacheable(true);
		
		List<Employee> emps = query.list();
		System.out.println(emps.size());
		
		
		Employee employee = (Employee) session.get(Employee.class, 1);
		employee.setSalary(20000);
		
		emps = query.list();
		System.out.println(emps.size());
	}
	
   @Test
   public  void testQueryiterate(){
	   Department dept = (Department) session.get(Department.class, 1);
	   System.out.println(dept.getName());
	   System.out.println(dept.getEmps().size());
	   
	   Query query = session.createQuery("FROM Employee e WHERE e.dept.id=1");
//	   List<Employee> emps = query.list();
//	   System.out.println(emps.size());
	   
	   Iterator<Employee> empIt = query.iterate();
	   while(empIt.hasNext()){
		   System.out.println(empIt.next().getName());
	   }
   }
   
   @Test
   public void testmanagerSession(){
	   //��ȡsession
	   //��������
	   Session session = HibernateUtils.getInstance().getSession();
	   System.out.println(session.hashCode());
	   Transaction transcation = session.beginTransaction();
	   
	   DepartmentDao dao = new DepartmentDao();
	   
	   Department dept = new Department();
	   dept.setName("SS");
	   
	   dao.save(dept);
	   dao.save(dept);
	   dao.save(dept);
	   
	   //��Session����thread����ģ������ύ��ع�����ʱ���Ѿ��ر�Session��
	   transcation.commit();
	   System.out.println(session.isOpen());
   }

   /**
    * ��������
    */
   @Test
   public void testBatch(){
	   session.doWork(new Work() {
		
		@Override
		public void execute(Connection arg0) throws SQLException {
			// ͨ��JDBCԭ����API���в�����Ч����ߣ��ٶ����
			
		}
	});
   }
   
}
