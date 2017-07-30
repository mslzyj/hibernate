package com.hibernate.nto1.both;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.hibernate.nto1.both.Customer;

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

	
	//先插入多的一端，在插入1的一端
	@Test
	public void testMany2One() {
		Customer customer = new Customer();
		customer.setCustomerName("BB");
		
		Order order1 = new Order();
		order1.setOrderName("ORDER-3");
		
		Order order2 = new Order();
		order2.setOrderName("ORDER-4");
		
		//设定关联关系
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		customer.getOrders().add(order1);
		customer.getOrders().add(order1);
		
		/*
		执行save操作;3个insert，两个update，因为1的一端和多的一端都维护关联关系，所以会多出update
		session.save(customer);
		session.save(order1);
		session.save(order2);

		执行save操作;3个insert，4个update；因为1的一端和多的一端都维护关联关系，所以会多出update
		session.save(order1);
		session.save(order2);
		session.save(customer);
		
		*/
		//先插入1的一端，在插入多的一端，设置了inverse="true"之后的情况:可以在1的一端的set节点指定inverse="true"来使1的一端放弃恢复关联关系，则不会多出update语句
		session.save(customer);
		session.save(order1);
		session.save(order2);
	}
	
	//先插入1 的一端再插入多的一端
	@Test
	public void testOne2ManyGet() {
		//1.对多的一端的集合使用延迟加载
		Customer customer = (Customer) session.get(Customer.class,1);
		System.out.println(customer.getCustomerName());
		//2.返回的多的一端的集合时Hibernate内置的集合类型，该类型具有延迟加载和存放代理对象的功能
		System.out.println(customer.getOrders().getClass());
		//3.可能会发生懒加载异常:LazyInitializationException,如下:
		    //session.close();
		   //System.out.println(customer.getOrders().size());
	    //4.在需要使用集合中元素的时候需要初始化。
	}
	
	@Test
	public void testMany2OneoneGet(){
		//1.若查询多的一端的一个对象，则默认情况下只查询了多的一端的对象，而没有查询关联的一的那一段的对象（关联延迟）
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
		Customer customer = (Customer) session.get(Customer.class,1);
		customer.getOrders().iterator().next().setOrderName("EE");
	}
	
	@Test
	public void testDelete(){
		//在不设定级联关系的情况下，且1的这一端的对象由n的这一端的对象再引用，则不能直接删除一的这一端的对象，
		//cascade="delete":设置级联删除,可以删除
		Customer customer = (Customer) session.get(Customer.class, 1);
		session.delete(customer);
	}
	
	@Test
	public void testCascade(){
		Customer customer = (Customer) session.get(Customer.class, 1);
		customer.getOrders().clear();
	}
	@Test
	public void test(){
		
	}
}
