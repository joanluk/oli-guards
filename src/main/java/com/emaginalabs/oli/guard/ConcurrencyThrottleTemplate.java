/*
 * Copyright (c) 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.emaginalabs.oli.guard;

import com.emaginalabs.oli.GuardCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import com.emaginalabs.oli.AbstractGuard;
import com.emaginalabs.oli.exception.ConcurrencyLimitExceededException;

import java.util.concurrent.Semaphore;

/**
 * <p>
 * Guard that fails with an exception when a concurrency threshold is exceeded.
 * </p>
 * <p>
 * Implementation is based on a counting semaphore.
 * </p>
 * <p>
 * We expose this guard as a JMX MBean so it can be queried and manipulated in management contexts.
 * </p>
 *
 */
@ManagedResource
public class ConcurrencyThrottleTemplate extends AbstractGuard {
	private static Logger log = LoggerFactory.getLogger(ConcurrencyThrottleTemplate.class);
	
	private final int limit;
	private final Semaphore semaphore;

	/**
	 * Creates a concurrency throttle with the given limit. The throttle rejects requests in excess of the limit.
	 * 
	 * @param limit concurrency limit
	 * @throws IllegalArgumentException if limit &lt; 1
	 */
	public ConcurrencyThrottleTemplate(int limit) {
		if (limit < 1) {
			throw new IllegalArgumentException("limit must be >= 1");
		}
		this.limit = limit;
		this.semaphore = new Semaphore(limit, true);
	}
	
	@ManagedAttribute(description = "Concurrency limit, after which requests are rejected")
	public int getLimit() { return limit; }
	
	public <T> T execute(GuardCallback<T> action) throws Exception {
		if (semaphore.tryAcquire()) {
			try {
				return action.doInGuard();
			} finally {
				semaphore.release();
			}
		} else {
			log.warn("Request rejected: concurrency limit {} exceeded", limit);
			throw new ConcurrencyLimitExceededException(limit);
		}
	}
}
