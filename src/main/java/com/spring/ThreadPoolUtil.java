package com.spring;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.validation.constraints.NotNull;

import com.google.common.util.concurrent.MoreExecutors;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.spring.ThreadPoolBuilder.ScheduledThreadPoolBuilder;
import com.spring.ThreadPoolBuilder.FixedThreadPoolBuilder;
public class ThreadPoolUtil {
	private final static int mNumCores = Runtime.getRuntime().availableProcessors();
	private final static ExecutorService executorService = MoreExecutors.listeningDecorator(new FixedThreadPoolBuilder().setPoolSize(100).build());
	private final static ScheduledExecutorService mScheduledExecutorService = MoreExecutors.listeningDecorator(new ScheduledThreadPoolBuilder().setPoolSize(mNumCores + 1).build());

	/***
	 * 初始化线程池
	 * @param task
	 */
	public static void executorThreadPool(Runnable task) {
		executorService.execute(task);
	}

	public static ExecutorService buildExecutorService() {
		return executorService;
	}

	/***
	 * 初始化Scheduled线程池
	 * @param task
	 */
	public static void ScheduledExecutorThreadPool(Runnable task) {
		mScheduledExecutorService.execute(task);
	}

	public static ScheduledExecutorService buildScheduledExeService() {
		return mScheduledExecutorService;
	}

	/**
	 * 创建ThreadFactory，使得创建的线程有自己的名字而不是默认的"pool-x-thread-y"
	 * 
	 * 使用了Guava的工具类
	 * 
	 * @see ThreadFactoryBuilder#build()
	 */
	public static ThreadFactory buildThreadFactory(@NotNull String threadNamePrefix) {
		return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
	}

	/**
	 * 可设定是否daemon, daemon线程在主线程已执行完毕时, 不会阻塞应用不退出, 而非daemon线程则会阻塞.
	 * 
	 * @see   #buildThreadFactory
	 */
	public static ThreadFactory buildThreadFactory(@NotNull String threadNamePrefix, @NotNull boolean daemon) {
		return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
	}

	/**
	 * 防止用户没有捕捉异常导致中断了线程池中的线程, 使得SchedulerService无法继续执行.
	 * 
	 * 在无法控制第三方包的Runnable实现时，调用本函数进行包裹.
	 */
	public static Runnable safeRunnable(@NotNull Runnable runnable) {
		return new SafeRunnable(runnable);
	}

	/**
	 * 保证不会有Exception抛出到线程池的Runnable包裹类，防止用户没有捕捉异常导致中断了线程池中的线程,
	 * 使得SchedulerService无法执行. 在无法控制第三方包的Runnalbe实现时，使用本类进行包裹.
	 */
	public static class SafeRunnable implements Runnable {

		private static Logger logger = LoggerFactory.getLogger(SafeRunnable.class);

		private Runnable runnable;

		public SafeRunnable(Runnable runnable) {
			Validate.notNull(runnable);
			this.runnable = runnable;
		}

		@Override
		public void run() {
			try {
				runnable.run();
			} catch (Throwable e) {
				// catch any exception, because the scheduled thread will break
				// if the exception thrown to outside.
				logger.error("Unexpected error occurred in task", e);
			}
		}
	}
}

