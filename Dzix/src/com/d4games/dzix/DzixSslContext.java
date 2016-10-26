package com.d4games.dzix;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DzixSslContext {

	   private static final Logger logger = Logger.getLogger(DzixSslContext.class.getName());
	    private static final String PROTOCOL = "TLS";
	    private final SSLContext _serverContext;

        private static String keystorePath = "";
        private static String keystorePassword = "";

	    public static void setKeystoreInfo(String keystoreFilePath, String keystoreFilePassword) {
	    	keystorePath = keystoreFilePath;
	    	keystorePassword = keystoreFilePassword;
	    }
	    
	    /**
	     * Returns the singleton instance for this class
	     */
	    public static DzixSslContext getInstance() {
	        return SingletonHolder.INSTANCE;
	    }

	    /**
	     * SingletonHolder is loaded on the first execution of Singleton.getInstance() or the first access to
	     * SingletonHolder.INSTANCE, not before.
	     *
	     * See http://en.wikipedia.org/wiki/Singleton_pattern
	     */
	    private interface SingletonHolder {
	        DzixSslContext INSTANCE = new DzixSslContext();
	    }

	    /**
	     * Constructor for singleton
	     */
	    private DzixSslContext() {
	        SSLContext serverContext = null;
	        try {
	            // Key store (Server side certificate)
	            String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
	            if (algorithm == null) {
	                algorithm = "SunX509";
	            }

	            try {
	                String keyStoreFilePath = keystorePath;//System.getProperty("keystore.file.path");
	                String keyStoreFilePassword = keystorePassword;//System.getProperty("keystore.file.password");

	                KeyStore ks = KeyStore.getInstance("JKS");
	                FileInputStream fin = new FileInputStream(keyStoreFilePath);
	                ks.load(fin, keyStoreFilePassword.toCharArray());

	                // Set up key manager factory to use our key store
	                // Assume key password is the same as the key store file
	                // password
	                KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
	                kmf.init(ks, keyStoreFilePassword.toCharArray());

	                // Initialise the SSLContext to work with our key managers.
	                serverContext = SSLContext.getInstance(PROTOCOL);
	                serverContext.init(kmf.getKeyManagers(), null, null);
	            } catch (Exception e) {
	                throw new Error("Failed to initialize the server-side SSLContext", e);
	            }
	        } catch (Exception ex) {
	            logger.log(Level.WARNING, "Error initializing SslContextManager.", ex);
	            System.exit(1);
	        } finally {
	            _serverContext = serverContext;
	        }
	    }

	    /**
	     * Returns the server context with server side key store
	     */
	    public SSLContext serverContext() {
	        return _serverContext;
	    }

}
