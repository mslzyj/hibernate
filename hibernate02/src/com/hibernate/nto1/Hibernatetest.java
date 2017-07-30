package com.hibernate.nto1;

import static org.junit.Assert.*;

import org.hibernate.LazyInitializationException;
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

	@Test
	public void testMany2One() {
		Customer customer = new Customer();
		customer.setCustomerName("AA");
		
		Order order1 = new Order();
		order1.setOrderName("ORDER-1");
		
		Order order2 = new Order();
		order2.setOrderName("ORDER-2");
		
		//�趨������ϵ
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		//ִ��save����;3��insert
		//�Ȳ���1��һ�Σ��ٲ���n��һ�ˣ�ֻ��INSERT���
		session.save(customer);
		session.save(order1);
		session.save(order2);
		
		//�Ȳ���n��һ�ˣ��ٲ���1��һ�ˣ�3��INSERT��2��UPDATE���
		//�Ȳ������޸ģ��ڲ�����һ��ʱ���޷�ȷ�����ֵ,ֻ�н�һ��һ�˲�����ٶ��ⷢ��update���
		/* session.save(order1);
		   session.save(order2);
		   session.save(customer);
		*/
	}
	
	@Test
	public void testMany2OneoneGet(){
		//1.����ѯ���һ�˵�һ��������Ĭ�������ֻ��ѯ�˶��һ�˵Ķ��󣬶�û�в�ѯ������һ����һ�εĶ��󣨼����ӳ٣�
		Order order = (Order) session.get(Order.class,1);
		System.out.println(order);
		
		
		//2.����Ҫʹ�õ������Ķ���ʱ���ŷ��Ͷ�Ӧ��sql��䣨�����أ�
		Customer customer = order.getCustomer();
		System.out.println(customer.getCustomerName());
		
		//3.���ɶ��һ�˲�ѯһ����һ��ʱ�����ܻᷢ���������쳣:LazyInitializationException
		//����ʱsession�Ѿ��رգ���ᷢ���쳣��Ĭ�������
		//4.��ȡOrder����ʱ��Ĭ������£��������Customers������һ���������
	}
	
	@Test
	public void testUpdat(){
		Order order = (Order) session.get(Order.class, 1);
		order.getCustomer().setCustomerName("AAA");
	}
	
	@Test
	public void testDelete(){
		//�ڲ��趨������ϵ������£���1����һ�˵Ķ�����n����һ�˵Ķ��������ã�����ֱ��ɾ��һ����һ�˵Ķ���
		Customer customer = (Customer) session.get(Customer.class, 1);
		session.delete(customer);
	}
}
