package com.test.eee.mpt.business.redis;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
		
//		rds.testSet();
		
		rds.testMap();
		
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
	
	
	public void testSet()
	{
		js.hset("set", "f", "v");
		js.hset("set", "f", "v");
		js.hset("set", "f2", "v2");
		
		js.hset("set2", "ff2", "vv2");
		
		Map<String, String> allMap = js.hgetAll("set");
		
		System.out.println("-----------------");
		
		Set<Entry<String, String>> allkeySet = allMap.entrySet();
		for (Iterator iterator = allkeySet.iterator(); iterator.hasNext();) {
			Entry<String, String> entry = (Entry<String, String>) iterator.next();
			System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
		}
		System.out.println("-----------------");
		
		Set<String> sets = allMap.keySet();
		for (Iterator iterator = sets.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			System.out.println("RedisTest.testSet() map xxx " + allMap.get(string));
		}
		
		String resString = js.hget("set", "f");
		System.out.println("RedisTest.testSet() f " + resString);
		resString = js.hget("set", "f2");
		System.out.println("RedisTest.testSet() f2 " + resString);
	}
	
	public void testMap()
	{
		js.mset("set3", "f");
		js.mset("set3", "f");
		js.mset("set3", "f3");
		js.mset("set4", "f2");
		
		js.mset("set5", "ff2");
		
		List<String> allMap = js.mget("set3");
		for (Iterator iterator = allMap.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			System.out.println("testMap:  " + string);
		}

	}
}
