package com.hibernate.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.hibernate.entities.Department;
import com.hibernate.hibernate.HibernateUtils;

public class DepartmentDao {
	
	private SessionFactory sessionFactory;
	public void setSessionFactory(SessionFactory sessionFactory){
		this.sessionFactory =sessionFactory;
	}
	
    public void save(Department dept){
    	//�ڲ���ȡSession���󣬻�ȡ�͵�ǰ�̰߳󶨵�Session����
    	//1.����Ҫ���ⲿ��Session����
    	//2.���DAO����Ҳ����ʹ��һ������
    	Session session = HibernateUtils.getInstance().getSession();
    	System.out.println(session.hashCode());
    	//session.save(dept);
    }
}
