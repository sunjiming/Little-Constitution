package com.spring;


import java.util.Date;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {
	public static void main(String args[]) {
		SpringApplication.run(Application.class, args);
	}
	
	public void run(String... args) throws Exception {
		System.out.println("!!!!!!!!!!!!");
		Response resp;
        String respBody;
        AsyncHttpClient client = JMHttpAsyncClient.create();
        
		RequestBuilder builder = Dsl
				// method 和 url
				.get("https://mp.dayu.com/upload-image")
				// header
				.addHeader("accept-encoding", "gzip, deflate, br")
				// 上传的参数
				.addQueryParam("src", "https://file.evolife.cn/2018/01/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20180102111908-666x440.png")
				.addQueryParam("type", "article_img")
				.addQueryParam("_", new Date().getTime() + "");
		 /**
		  * 如果用post呢怎么发送数据呢？
		  * POST:
		  * 	RequestBuilder builder = Dsl
		  * 			.post(url)
		  * 			.addHeader(key,value)
		  * 			.addFormParam(key,value)
		  * Cookie放在哪里呢？
		  * 	当然是请求头了：addHeader("cookie",cookie)
		  * */
		 resp = client.executeRequest(builder).toCompletableFuture().get();
         respBody = resp.getResponseBody();
         
         System.out.println("------------------------------------------------");
         System.out.println(respBody);
         System.out.println("------------------------------------------------");
	}
}
