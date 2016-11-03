package com.test.eee.mpt.main;

/**
 * Hello world!
 *
 */
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		DOMConfigurator.configure("resources/config/log4j.xml");
		Logger logger = Logger.getLogger(App.class);
		logger.info("info Hello World!");
		logger.debug("debug Hello World");
		logger.warn("warn Hello World");
		logger.error("error Hello World");
		logger.fatal("fatal Hello World");
		
		try {
			int a = 8;
			int b = 0;
			int c = a / b;
		} catch (Exception e) {
			// TODO: handle exception
			logger.fatal("Exp : ", e);
		}

		
	}
	
	
}
