/*
 * Copyright (c) 2014-2016 Jan Strauß <jan[at]over9000.eu>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package eu.over9000.skadi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorUtil.class);

	private static final long THREAD_TIME_OUT = 2;
	private static final int THREAD_POOL_SIZE = 64;

	private static final ThreadGroup THREAD_GROUP = new ThreadGroup("Skadi-pool");

	private static final Thread.UncaughtExceptionHandler UNCAUGHT_HANDLER = (thread, throwable) -> LOGGER.warn("Uncaught throwable in " + THREAD_GROUP.getName(), throwable);

	private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
		private final AtomicInteger threadNumber = new AtomicInteger(1);

		@Override
		public Thread newThread(final Runnable runnable) {
			final Thread thread = new Thread(THREAD_GROUP, runnable, THREAD_GROUP.getName() + "-thread-" + threadNumber.getAndIncrement(), 0);
			thread.setUncaughtExceptionHandler(UNCAUGHT_HANDLER);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.setDaemon(true);
			return thread;
		}
	};

	private static final ThreadPoolExecutor EXECUTOR_SERVICE = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE, THREAD_TIME_OUT, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), THREAD_FACTORY);

	static {
		EXECUTOR_SERVICE.allowCoreThreadTimeOut(true);
	}

	public static void performShutdown() {
		try {
			EXECUTOR_SERVICE.shutdown();
			EXECUTOR_SERVICE.awaitTermination(5, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			LOGGER.error("exception during shutdown", e);
		}
	}

	public static ExecutorService getExecutorService() {
		return EXECUTOR_SERVICE;
	}
}