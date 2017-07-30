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
		//1.一对多或多对多的集合属性，默认使用懒加载的检索策略
		Customer customer = (Customer) session.get(Customer.class, 1);
		System.out.println(customer.getCustomerName());
		//2.可以通过修改set的lazy属性来修改默认的检索策略，默认为true，常使用默认值
		System.out.println(customer.getOrders().size());
		//3.lazy还可以设置一个extra，增强的延迟检索，该值会尽可能的延迟集合初始化的时机
		Order order  = new Order();
		order.setOrderId(1);
		System.out.println(customer.getOrders().contains(order));
		
		//对集合的某一个代理对象进行初始化使用以下方法
		Hibernate.initialize(customer.getOrders());
	}
	
	
	/**
	 * set元素的batch-size属性：设定一次初始化set集合的数量
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
    * set集合的fetch属性：确定初始化orders集合的方式，有以下取值
    * 1.默认值为select，通过正常的方式来初始化set元素
    * 2.可以取值为subcelect，通过子查询的方式来初始化所有的set集合，
    *   子查询作为where自居的in的条件出现，子查询所有1 的一端的ID，
    *   此时lazy有效，但batch-size失效。
    * 3.若取值为join，则：
    *  （1）在加载1 的一端的对象时，使用迫切左外连接（使用左外连接进行查询，且把集合属性进行初始化）的方式检索n的一端的集合属性
    *  （2）忽略lazy属性
    *  （3）HQL查询忽略fetch=join的取值
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
    * 1.lazy取值为proxy和false分别代表对应的属性采用延迟检索和立即检索
    * 2.fetch取值为join，表示使用迫切左外连接的方式初始化n的关联的1的一端的属性，忽略lazy属性
    * 3.betch-size:该属性需要设置在1的那一段的class元素中： <class name="Customer" table="CUSTOMERS" batch-size="5">
    * 作用：一次初始化1的这一端，代理对象的个数。
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
