package com.tdk.utils;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLSocketFactoryEx extends org.apache.http.conn.ssl.SSLSocketFactory {

	SSLContext mSSLContext = SSLContext.getInstance("TLS");
	
	public SSLSocketFactoryEx(KeyStore keyStore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
	{
		super(keyStore);
		X509TrustManager trustManager = new X509TrustManager()
		{
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
			{
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
			{
			}

			public X509Certificate[] getAcceptedIssuers()
			{
				return null;
			}
		};

		mSSLContext.init(null, new TrustManager[] { trustManager }, null);
	}
	
	
	public Socket createSocket() throws IOException
	{
		return mSSLContext.getSocketFactory().createSocket();
	}

	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException
	{
		return mSSLContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	}
}
