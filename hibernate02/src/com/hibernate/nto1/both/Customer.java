package com.hibernate.nto1.both;

import java.util.HashSet;
import java.util.Set;

public class Customer {
	private Integer customerId;
	private String customerName;
	
	/**
	 * 1.������������ʱ����ʹ�ýӿ����ͣ���Ϊhibernate�ڻ�ȡ��������ʱ���ص���hibernate���õļ������ͣ�������javase��һ����׼��ʵ��
	 * 2.��Ҫ�����Ͻ��г�ʼ�������Է�ֹ������ָ���쳣
	 */
	private Set<Order> orders = new HashSet<>();

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Set<Order> getOrders() {
		return orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}
	

}
