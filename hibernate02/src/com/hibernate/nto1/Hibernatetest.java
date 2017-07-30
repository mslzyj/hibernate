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
		
		//设定关联关系
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		//执行save操作;3条insert
		//先插入1的一段，再插入n的一端，只有INSERT语句
		session.save(customer);
		session.save(order1);
		session.save(order2);
		
		//先插入n的一端，再插入1的一端：3条INSERT，2条UPDATE语句
		//先插入再修改：在插入多的一端时，无法确定外键值,只有将一的一端插入后，再额外发送update语句
		/* session.save(order1);
		   session.save(order2);
		   session.save(customer);
		*/
	}
	
	@Test
	public void testMany2OneoneGet(){
		//1.若查询多的一端的一个对象，则默认情况下只查询了多的一端的对象，而没有查询关联的一的那一段的对象（加载延迟）
		Order order = (Order) session.get(Order.class,1);
		System.out.println(order);
		
		
		//2.在需要使用到关联的对象时，才发送对应的sql语句（懒加载）
		Customer customer = order.getCustomer();
		System.out.println(customer.getCustomerName());
		
		//3.在由多的一端查询一的那一段时，可能会发生懒加载异常:LazyInitializationException
		//若此时session已经关闭，则会发生异常，默认情况下
		//4.获取Order对象时，默认情况下，其关联的Customers对象是一个代理对象
	}
	
	@Test
	public void testUpdat(){
		Order order = (Order) session.get(Order.class, 1);
		order.getCustomer().setCustomerName("AAA");
	}
	
	@Test
	public void testDelete(){
		//在不设定级联关系的情况下，且1的这一端的对象由n的这一端的对象再引用，则不能直接删除一的这一端的对象，
		Customer customer = (Customer) session.get(Customer.class, 1);
		session.delete(customer);
	}
}
