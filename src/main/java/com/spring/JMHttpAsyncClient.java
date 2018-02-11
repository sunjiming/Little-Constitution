package com.spring;

import static org.asynchttpclient.Dsl.config;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.netty.ssl.DefaultSslEngineFactory;


import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * 单例模式：饿汉模式
 * 
 * */
public class JMHttpAsyncClient {
	
	private static AsyncHttpClientConfig CONFIG;
	static {
		try {
			final SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();

			CONFIG = config().setConnectTimeout(10 * 1000)// 连接超时时间
					.setRequestTimeout(5 * 60 * 1000)// 请求超时时间
					.setPooledConnectionIdleTimeout(100)// 连接池超时
					.setKeepAlive(true)// http的长连接
					.setSslContext(sslContext) // https
					.setSslEngineFactory(new DefaultSslEngineFactory())
					.setMaxConnections(3000)// 最大总连接数
					.setMaxConnectionsPerHost(50)// 最大连接数
					.setThreadPoolName("threadPool-JMHttpAsyncClient")// 线程池的名字
					.setThreadFactory(ThreadPoolUtil.buildThreadFactory("JM-Prefix", true))//
					.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36")//
					.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private final static AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient(CONFIG);

	public static AsyncHttpClient create() {
		return asyncHttpClient;
	}
}
