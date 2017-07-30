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
	 * flush�������ݱ��еļ�¼��session�����еĶ����״̬����һ�£�Ϊ�˱���һ�£����ܷ��Ͷ�Ӧ��sql���
	 * 1.����Transcation��commit��������������������У��ȵ���session��flush���������ύ����
	 * 2.flush�������ܻᷢ��sql��䣬�����ύ����
	 * 3.����sessionδ�ύ�������ʾ�ĵ���session.flush����֮ǰҲ�п��ܻ����flush����
	 * >ִ��HQL��QBC��ѯ�����Ƚ���flush�������Եõ����ݱ�����µĲ���
	 * >��������������ݵ�Id�������ݿ�ײ������ķ�ʽ���ɵģ������save����ʱ������������sql��� ��Ϊsave�����󣬱��뱣֤ID�Ǵ��ڵģ�
	 */
	@Test
	public void testSessionFlush() {
		News news = (News) session.get(News.class, 1);
		news.setAuthor("A");
		// session.flush();
		// System.out.println("flush");
		// ��ѯ
		News news2 = (News) session.createCriteria(News.class).uniqueResult();
		System.out.println(news2);
	}

	@Test
	public void testSessionFlush2() {
		News news = new News("java", "BB", new Date());
		session.save(news);
	}

	/**
	 * refresh�����������ݿ��е����ݷ����仯ʱ������refresh������ǿ�Ʒ���select��䣬��ʹsession�����ж����״̬�����ݱ��ж�Ӧ��¼����һ��
	 */
	@Test
	public void testrefresh() {
		News news = (News) session.get(News.class, 5);
		System.out.println(news);
		// �ڴ˲���ʱ�����ݿ����ݽ����޸ģ����Ҳ�Ҫ�������ݸ��뼶��
		session.refresh(news);
		System.out.println(news);
	}

	/**
	 * clear����������
	 */
	@Test
	public void testClear() {
		News news = (News) session.get(News.class, 5);
		System.out.println(news);
		// �����棬������Ҫ�ٴν��в�ѯ
		session.clear();
		news = (News) session.get(News.class, 5);
		System.out.println(news);
	}

	/**
	 * save()������ 1.ʹһ����ʱ�����Ϊ�־û����� 2.λ�������ID 3.��flush����ʱ���ᷢ��һ��insert���
	 * 4.��save����֮ǰ����id����Ч�ģ�save����ִ�� 5.��save����֮��ĵ�idҲ�ǲ��ܸı�ģ����־û������id�ǲ��ܸı�ģ�
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
	 * persist()������Ҳ��ִ��Insert���� ��save��0����������
	 * 1.�����persist����֮ǰ����һ��id���򲻻�ִ��insert�������׳�һ���쳣��
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
	 * get����������load������������
	 * 
	 * 1.ִ��get���������������ض��󣬶�ִ��load�������������øö����򲻻�����ִ�в�ѯ������������һ���������
	 * 
	 * get������������load���ӳټ���
	 * 
	 * 2.�������ݱ�û�ж�Ӧ�ļ�¼��sessionҲû�йرգ�ͬʱ��Ҫʹ�ö���ʱ��
	 * get����null��load�������øö�����κ����ԣ�����Ҫ��ʼ�����׳��쳣
	 * 
	 * 3.load�������ܻ��׳�������(LazyInitializationException)�쳣��
	 * ����Ҫ��ʼ���������֮ǰ�Ѿ��ر���session�����׳��쳣(��ʹ�����ǰ���� �����session.close();)
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
	 * update()������ʹ��������Ϊ�־û�����
	 * 
	 * 1.������һ���־û����󣬲���Ҫ��ʾ�ĵ���update��������Ϊ�ڵ���transcation��commit����ʱ����ִ��session��flush����
	 * 2.����������������Ҫ��ʾ�ĵ���session��update����������������Ϊ�־û�����
	 * 3.����Ҫ���µ������������ݱ��Ƿ�һ�£����ᷢ��update��� �������update��������äĿ�ķ���update���:��
	 * .hbm.xml�ļ���class�� ������select-before-update=true(Ĭ��Ϊfalse��ͨ������Ҫ�޸�)
	 * 4.�����ݱ���û�ж�Ӧ�ļ�¼����������״̬������������update���������׳��쳣
	 * 5.��update��������һ���������ʱ�������session�Ļ������Ѿ�������ͬ��OID�����׳��쳣
	 * ��Ϊ��session�����в�����������ͬid�Ķ���
	 */
	@Test
	public void testupdate() {
		News news = (News) session.get(News.class, 5);
		/*
		 * �������������䣬��Ҫ��ʾ�ĵ���session��update������ �ڶ�������Ѿ�������رգ���������俪��һ���µ������
		 * ��������news��session�Ļ������棬��news�������� ״̬���������������󣬴�ʱ��Ҫ��ʾ�ĵ���update������
		 * ���û������������䣬����Ҫ��ʾ�ĵ���update������
		 */
		transcation.commit();
		session.close();
		session = sessionFactory.openSession();
		transcation = session.beginTransaction();

		session.update(news);
		news.setAuthor("CC");
	}

	/**
	 * SavsOrUpdate(): 1.��OID��Ϊnull�����ݱ��л�û�к����Ӧ�ļ�¼�����׳�һ���쳣
	 * 2.�˽⣺OIDֵ����id��unsaved-value����ֵ�Ķ���Ҳ�ᱻ��Ϊ��һ���������
	 */
	@Test
	public void testSavsOrUpdate() {
		News news = new News("java", "ee", new Date());
		session.saveOrUpdate(news);
	}

	/**
	 * deltet����������ִ��ɾ��������ֻҪOID�����ݱ���һ����¼��Ӧ���ͻ�ִ��ɾ�������� ��OID��������û�ж�Ӧ�ļ�¼������׳��쳣
	 * ����ͨ������hibernate�����ļ�hibernate.use_identifier_rollbackΪtrueʹɾ������󣬰���OID��Ϊnull��
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
	 * evict����session�����н��־û������Ƴ�
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
				// ���ô洢����
			}
		});
	}

	/**
	 * ��̬���£���.hbm.xml�е�class����� dynamic-update="true",
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
		//BOLBд����
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
		//BOLB������
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
