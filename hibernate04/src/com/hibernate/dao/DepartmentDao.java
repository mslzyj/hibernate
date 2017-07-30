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
    	//内部获取Session对象，获取和当前线程绑定的Session对象
    	//1.不需要从外部绑定Session对象
    	//2.多个DAO方法也可以使用一个事务
    	Session session = HibernateUtils.getInstance().getSession();
    	System.out.println(session.hashCode());
    	//session.save(dept);
    }
}
