package com.test.eee.mpt.business.redis;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

import com.test.eee.mpt.business.redis.bean.A;

/**
 * @author zhaoshoukun
 * @date 2016年11月29日
 */
public class RedisTest {
	Jedis js = new Jedis("127.0.0.1", 6379);
	/**
	 * 
	 */
	public RedisTest() {
		// TODO Auto-generated constructor stub
		js.auth("redis_lantu_20!%");
		System.out.println("RedisTest.RedisTest() " + js.info());
		

		
	}
	
	public static void main(String[] args) {
		RedisTest rds = new RedisTest();
		rds.testString();
		
		rds.testList();
		
		rds.testObject();
		
		
		Set<String> keySet = rds.js.keys("*");
		System.out.println("RedisTest.RedisTest() size = " + keySet.size());
		for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			System.out.println("RedisTest.RedisTest() key = " + string);
//			System.out.println("RedisTest.RedisTest() value = " + rds.js.get(string));
		}
		
	}
	
	public void testString() {
		String codeString = js.set("str1", "hello world");
		System.out.println("set codeString = " + codeString);
		
		String resultString = js.get("str1");
		System.out.println("get resultString = " + resultString);
	}
	
	public void testList() {
		int len = 5;
		for (int i = 0; i < len; i++) {
			js.lpush("list", String.valueOf(i));
		}
		
		List<String> list = js.lrange("list", 0, len);
		for (String string : list) {
			System.out.println("list get = " + string);
		}

	}
	
	public void testObject()
	{
		A a = new A();
		a.setName("zhang san");
		a.setNum(10);
		String[] book = {"english","math"};
		int[] bookNum = {5,3};
		a.setArrayName(book);
		a.setArrayInt(bookNum);
		js.set("a".getBytes(), SerializationUtil.serialize(a));
		
		
		byte[] data = js.get("a".getBytes());
		A cA = (A)SerializationUtil.deserialize(data);
		System.out.println("RedisTest.testObject() cA " + cA.toString());
		
	}
}
