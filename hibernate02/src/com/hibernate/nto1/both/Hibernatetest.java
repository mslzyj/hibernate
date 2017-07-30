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

	
	//�Ȳ�����һ�ˣ��ڲ���1��һ��
	@Test
	public void testMany2One() {
		Customer customer = new Customer();
		customer.setCustomerName("BB");
		
		Order order1 = new Order();
		order1.setOrderName("ORDER-3");
		
		Order order2 = new Order();
		order2.setOrderName("ORDER-4");
		
		//�趨������ϵ
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		customer.getOrders().add(order1);
		customer.getOrders().add(order1);
		
		/*
		ִ��save����;3��insert������update����Ϊ1��һ�˺Ͷ��һ�˶�ά��������ϵ�����Ի���update
		session.save(customer);
		session.save(order1);
		session.save(order2);

		ִ��save����;3��insert��4��update����Ϊ1��һ�˺Ͷ��һ�˶�ά��������ϵ�����Ի���update
		session.save(order1);
		session.save(order2);
		session.save(customer);
		
		*/
		//�Ȳ���1��һ�ˣ��ڲ�����һ�ˣ�������inverse="true"֮������:������1��һ�˵�set�ڵ�ָ��inverse="true"��ʹ1��һ�˷����ָ�������ϵ���򲻻���update���
		session.save(customer);
		session.save(order1);
		session.save(order2);
	}
	
	//�Ȳ���1 ��һ���ٲ�����һ��
	@Test
	public void testOne2ManyGet() {
		//1.�Զ��һ�˵ļ���ʹ���ӳټ���
		Customer customer = (Customer) session.get(Customer.class,1);
		System.out.println(customer.getCustomerName());
		//2.���صĶ��һ�˵ļ���ʱHibernate���õļ������ͣ������;����ӳټ��غʹ�Ŵ������Ĺ���
		System.out.println(customer.getOrders().getClass());
		//3.���ܻᷢ���������쳣:LazyInitializationException,����:
		    //session.close();
		   //System.out.println(customer.getOrders().size());
	    //4.����Ҫʹ�ü�����Ԫ�ص�ʱ����Ҫ��ʼ����
	}
	
	@Test
	public void testMany2OneoneGet(){
		//1.����ѯ���һ�˵�һ��������Ĭ�������ֻ��ѯ�˶��һ�˵Ķ��󣬶�û�в�ѯ������һ����һ�εĶ��󣨹����ӳ٣�
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
		Customer customer = (Customer) session.get(Customer.class,1);
		customer.getOrders().iterator().next().setOrderName("EE");
	}
	
	@Test
	public void testDelete(){
		//�ڲ��趨������ϵ������£���1����һ�˵Ķ�����n����һ�˵Ķ��������ã�����ֱ��ɾ��һ����һ�˵Ķ���
		//cascade="delete":���ü���ɾ��,����ɾ��
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
