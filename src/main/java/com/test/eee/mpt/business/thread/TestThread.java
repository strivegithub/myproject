package com.test.eee.mpt.business.thread;

/**
 * @author zhaoshoukun
 * @date 2016年9月22日
 */
public class TestThread {

	/**
	 * 
	 */
	public TestThread() {
		// TODO Auto-generated constructor stub
		for (int i = 0; i < 20; i++) {
			String nameString = "thread_" + i;
			Thread thread = new Thread(new Do(nameString));
			thread.setName(nameString);	
			thread.start();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new TestThread();
	}

}
