package com.hibernate.strategy;

import java.util.List;

import org.hibernate.Hibernate;
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
	public void testClassLevelStrategy() {
		Customer customer = (Customer) session.load(Customer.class, 1);
		System.out.println(customer.getClass());
		
	}
	
	public void testOne2ManyLevelStrategy(){
		//1.һ�Զ���Զ�ļ������ԣ�Ĭ��ʹ�������صļ�������
		Customer customer = (Customer) session.get(Customer.class, 1);
		System.out.println(customer.getCustomerName());
		//2.����ͨ���޸�set��lazy�������޸�Ĭ�ϵļ������ԣ�Ĭ��Ϊtrue����ʹ��Ĭ��ֵ
		System.out.println(customer.getOrders().size());
		//3.lazy����������һ��extra����ǿ���ӳټ�������ֵ�ᾡ���ܵ��ӳټ��ϳ�ʼ����ʱ��
		Order order  = new Order();
		order.setOrderId(1);
		System.out.println(customer.getOrders().contains(order));
		
		//�Լ��ϵ�ĳһ�����������г�ʼ��ʹ�����·���
		Hibernate.initialize(customer.getOrders());
	}
	
	
	/**
	 * setԪ�ص�batch-size���ԣ��趨һ�γ�ʼ��set���ϵ�����
	 */
   @Test
   public void testSetBatchSize(){
	   List<Customer> customers = session.createQuery("FROM Customer").list();
	   System.out.println(customers.size());
       
	   for (Customer customer:customers){
		   if(customer.getOrders()!=null){
			   System.out.println(customer.getOrders().size());
		   }
	   }
   }
   
   /**
    * set���ϵ�fetch���ԣ�ȷ����ʼ��orders���ϵķ�ʽ��������ȡֵ
    * 1.Ĭ��ֵΪselect��ͨ�������ķ�ʽ����ʼ��setԪ��
    * 2.����ȡֵΪsubcelect��ͨ���Ӳ�ѯ�ķ�ʽ����ʼ�����е�set���ϣ�
    *   �Ӳ�ѯ��Ϊwhere�Ծӵ�in���������֣��Ӳ�ѯ����1 ��һ�˵�ID��
    *   ��ʱlazy��Ч����batch-sizeʧЧ��
    * 3.��ȡֵΪjoin����
    *  ��1���ڼ���1 ��һ�˵Ķ���ʱ��ʹ�������������ӣ�ʹ���������ӽ��в�ѯ���ҰѼ������Խ��г�ʼ�����ķ�ʽ����n��һ�˵ļ�������
    *  ��2������lazy����
    *  ��3��HQL��ѯ����fetch=join��ȡֵ
    */
   @Test
   public void testSetFetch(){
	   List<Customer> customers = session.createQuery("FROM Customer").list();
	   System.out.println(customers.size());
       
	   for (Customer customer:customers){
		   if(customer.getOrders()!=null){
			   System.out.println(customer.getOrders().size());
		   }
	   }
   }
   @Test
   public void testSetFetch2(){
	   Customer customer=(Customer) session.get(Customer.class, 1);
	   System.out.println(customer.getOrders().size());
   }

   /**
    * 1.lazyȡֵΪproxy��false�ֱ�����Ӧ�����Բ����ӳټ�������������
    * 2.fetchȡֵΪjoin����ʾʹ�������������ӵķ�ʽ��ʼ��n�Ĺ�����1��һ�˵����ԣ�����lazy����
    * 3.betch-size:��������Ҫ������1����һ�ε�classԪ���У� <class name="Customer" table="CUSTOMERS" batch-size="5">
    * ���ã�һ�γ�ʼ��1����һ�ˣ��������ĸ�����
    */
   @Test
  public void testMany2one(){
//	  Order order= (Order) session.get(Order.class, 1);
//	  System.out.println(order.getCustomer().getCustomerName());
	  
	  List<Order> orders = session.createQuery("FROM Order o").list();
	  for (Order order:orders ){
		  if(order.getCustomer()!=null){
			  System.out.println(order.getCustomer().getCustomerName());
		  }
	  }
  }
}
