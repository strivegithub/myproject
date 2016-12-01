package com.test.eee.mpt.business.redis.bean;

import java.io.Serializable;

/**
 * @author zhaoshoukun
 * @date 2016年12月1日
 */
public class A implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	public A() {
		// TODO Auto-generated constructor stub
	}
	
	private int num;
	private String name;
	private int[] arrayInt;
	private String[] arrayName;
	/**
	 * @return the num
	 */
	public int getNum() {
		return num;
	}
	/**
	 * @param num the num to set
	 */
	public void setNum(int num) {
		this.num = num;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the arrayInt
	 */
	public int[] getArrayInt() {
		return arrayInt;
	}
	/**
	 * @param arrayInt the arrayInt to set
	 */
	public void setArrayInt(int[] arrayInt) {
		this.arrayInt = arrayInt;
	}
	/**
	 * @return the arrayName
	 */
	public String[] getArrayName() {
		return arrayName;
	}
	/**
	 * @param arrayName the arrayName to set
	 */
	public void setArrayName(String[] arrayName) {
		this.arrayName = arrayName;
	}
	
	public String toString()
	{
		StringBuffer string = new StringBuffer();
		string.append("\nname = " + name);
		string.append("\nnum = " + num);
		if (arrayInt != null) {
			for (int j = 0; j < arrayInt.length; j++) {
				string.append("\narrayInt = " + arrayInt[j]);
			}
		}
		if (arrayName != null) {
			for (int j = 0; j < arrayName.length; j++) {
				string.append("\narrayName = " + arrayName[j]);
			}
		}

		return string.toString();
	}
	
	

}
