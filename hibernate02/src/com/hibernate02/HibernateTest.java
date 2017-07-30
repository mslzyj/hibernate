package com.hibernate02;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.hibernate.Hibernate;
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

import com.mysql.jdbc.Blob;

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
	public void testSessionCache() {
		News news = (News) session.get(News.class, 1);
		System.out.println(news);
		// System.out.println("test");
	}

	/**
	 * flush：是数据表中的记录和session缓存中的对象的状态保持一致，为了保持一致，可能发送对应的sql语句
	 * 1.调用Transcation的commit（）方法，在这个方法中，先调用session的flush方法，再提交事务
	 * 2.flush方法可能会发送sql语句，不会提交事务
	 * 3.若在session未提交事务或显示的调用session.flush方法之前也有可能会进行flush操作
	 * >执行HQL或QBC查询，会先进行flush操作，以得到数据表的最新的操作
	 * >特殊情况：若数据的Id是由数据库底层自增的方式生成的，则调用save方法时，会立即发送sql语句 因为save方法后，必须保证ID是存在的！
	 */
	@Test
	public void testSessionFlush() {
		News news = (News) session.get(News.class, 1);
		news.setAuthor("A");
		// session.flush();
		// System.out.println("flush");
		// 查询
		News news2 = (News) session.createCriteria(News.class).uniqueResult();
		System.out.println(news2);
	}

	@Test
	public void testSessionFlush2() {
		News news = new News("java", "BB", new Date());
		session.save(news);
	}

	/**
	 * refresh方法：当数据库中的数据发生变化时，调用refresh方法会强制发送select语句，以使session缓存中对象的状态和数据表中对应记录保持一致
	 */
	@Test
	public void testrefresh() {
		News news = (News) session.get(News.class, 5);
		System.out.println(news);
		// 在此操作时对数据库数据进行修改，并且不要设置书屋隔离级别
		session.refresh(news);
		System.out.println(news);
	}

	/**
	 * clear（）清理缓存
	 */
	@Test
	public void testClear() {
		News news = (News) session.get(News.class, 5);
		System.out.println(news);
		// 清理缓存，所以需要再次进行查询
		session.clear();
		news = (News) session.get(News.class, 5);
		System.out.println(news);
	}

	/**
	 * save()方法： 1.使一个临时对象变为持久化对象 2.位对象分配ID 3.在flush缓存时，会发送一条insert语句
	 * 4.在save方法之前设置id是无效的，save正常执行 5.在save方法之后的的id也是不能改变的，（持久化对象的id是不能改变的）
	 */
	@Test
	public void testSave() {
		News news = new News();
		news.setTitle("AA");
		news.setAuthor("aa");
		news.setDate(new Date());
		System.out.println(news);
		session.save(news);
		System.out.println(news);
	}

	/**
	 * persist()方法：也会执行Insert操作 和save（0方法的区别：
	 * 1.如果在persist方法之前设置一个id，则不会执行insert，而是抛出一个异常；
	 */
	@Test
	public void testPersist() {
		News news = new News();
		news.setTitle("AA");
		news.setAuthor("aa");
		news.setDate(new Date());
		System.out.println(news);
		session.persist(news);
		System.out.println(news);
	}

	/**
	 * get（）方法和load（）方法区别：
	 * 
	 * 1.执行get方法，会立即加载对象，而执行load方法，若不适用该对象，则不会立即执行查询操作，而返回一个代理对象
	 * 
	 * get是立即检索，load是延迟检索
	 * 
	 * 2.若对数据表没有对应的记录且session也没有关闭，同时需要使用对象时，
	 * get返回null，load若不适用该对象的任何属性，若需要初始化则抛出异常
	 * 
	 * 3.load方法可能会抛出懒加载(LazyInitializationException)异常，
	 * 在需要初始化代理对象之前已经关闭了session，会抛出异常(在使用语句前面添 加语句session.close();)
	 */
	@Test
	public void testGet() {
		News news = (News) session.get(News.class, 5);
		System.out.println(news);
	}

	public void testLoad() {
		News news = (News) session.load(News.class, 5);
		System.out.println(news);
	}

	/**
	 * update()方法：使游离对象变为持久化对象
	 * 
	 * 1.若更新一个持久化对象，不需要显示的调用update方法，因为在调用transcation的commit方法时会先执行session的flush方法
	 * 2.如果更新游离对象，需要显示的调用session的update方法：把游离对象变为持久化对象
	 * 3.无论要更新的游离对象和数据表是否一致，都会发送update语句 如何能让update方法不在盲目的发出update语句:在
	 * .hbm.xml文件的class节 点设置select-before-update=true(默认为false，通常不需要修改)
	 * 4.若数据表中没有对应的记录（处于游离状态），但调用了update方法，会抛出异常
	 * 5.当update方法关联一个游离对象时，如果在session的缓存中已经存在相同的OID，会抛出异常
	 * 因为在session缓存中不能有两个相同id的对象
	 */
	@Test
	public void testupdate() {
		News news = (News) session.get(News.class, 5);
		/*
		 * 添加以下四条语句，需要显示的调用session的update方法； 第二条语句已经将缓存关闭，第三条语句开了一个新的事物，第
		 * 四条语句的news不session的缓存里面，即news处于游离 状态，如果更新游离对象，此时需要显示的调用update方法：
		 * 如果没有以下四条语句，则不需要显示的调用update方法。
		 */
		transcation.commit();
		session.close();
		session = sessionFactory.openSession();
		transcation = session.beginTransaction();

		session.update(news);
		news.setAuthor("CC");
	}

	/**
	 * SavsOrUpdate(): 1.若OID不为null；数据表中还没有和其对应的记录，会抛出一个异常
	 * 2.了解：OID值等于id的unsaved-value属性值的对象。也会被认为是一个游离对象
	 */
	@Test
	public void testSavsOrUpdate() {
		News news = new News("java", "ee", new Date());
		session.saveOrUpdate(news);
	}

	/**
	 * deltet（）方法：执行删除操作，只要OID和数据表中一条记录对应，就会执行删除操作， 若OID在数表中没有对应的记录，则会抛出异常
	 * 可以通过设置hibernate配置文件hibernate.use_identifier_rollback为true使删除对象后，把其OID置为null；
	 */
	@Test
	public void testDelete() {
		// News news = new News();
		// news.setId(5);

		News news = (News) session.get(News.class, 5);

		session.delete(news);
		System.out.println(news);
	}

	/**
	 * evict：从session缓存中将持久化对象移除
	 */
	@Test
	public void testEvict() {
		News news1 = (News) session.get(News.class, 6);
		News news2 = (News) session.get(News.class, 7);
		news1.setTitle("AA");
		news2.setTitle("BB");
		session.evict(news1);
	}

	@Test
	public void testDowork() {
		session.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				System.out.println(connection);
				// 调用存储过程
			}
		});
	}

	/**
	 * 动态更新：在.hbm.xml中的class中添加 dynamic-update="true",
	 */
	@Test
	public void testDynamicUpdate() {
		News news = (News) session.get(News.class, 1);
		news.setAuthor("Ab");
	}

	@Test
	public void testIDGenerate() {
		News news = new News("BB", "aa", new java.sql.Date(new Date().getTime()));
		session.save(news);
	}

	@Test
	public void testBlob() throws IOException, SQLException   {
		//BOLB写操作
//		News news = new News();
//		news.setTitle("CC");
//		news.setAuthor("cc");
//		news.setDate(new Date());
//		news.setContent("CONTENT");
//
//		InputStream stream = new FileInputStream("333382.jpg");
//		Blob image =  (Blob) Hibernate.getLobCreator(session).createBlob(stream, stream.available());
//		news.setImage(image);
//       
//		session.save(news);
		//BOLB读操作
//		News news = (News) session.get(News.class, 1);
//		Blob image = news.getImage();
//		InputStream inputStream = image.getBinaryStream();
//		System.out.println(inputStream.available());
	}
	
	@Test
	public void testComponent(){
		Worker worker = new Worker();
		Pay pay = new Pay();
		pay.setMonthlyPay(1000);
		pay.setYearPay(80000);
		pay.setVocationWithPay(5);
		
		worker.setName("AA");
		worker.setPay(pay);
		session.save(worker);
		
	}
}
