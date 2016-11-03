package com.test.eee.mpt.business.thread;

/**
 * @author zhaoshoukun
 * @date 2016年9月22日
 */
public class Do implements Runnable {
	private int cnt = 0;
	private String nameString = "";
	/**
	 * 
	 */
	public Do(String n) {
		// TODO Auto-generated constructor stub
		nameString = n;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (cnt < 100) {
			// TODO Auto-generated method stub
			try {
				System.out.println("Do.run() name " + nameString + ", cnt = " + cnt + " ,time = " + System.currentTimeMillis());
				cnt++;
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
