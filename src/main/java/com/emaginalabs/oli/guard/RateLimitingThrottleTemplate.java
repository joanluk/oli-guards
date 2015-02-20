/*
 * Copyright (c) 2012 the original author or authors.
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

import com.emaginalabs.oli.AbstractGuard;
import com.emaginalabs.oli.GuardCallback;
import com.emaginalabs.oli.exception.RateLimitExceededException;
import com.emaginalabs.oli.exception.UnauthenticatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * A rate-limiting throttle. Currently this guard rate-limits on an hourly basis. A future version of this guard will
 * allow a range of options with respect to the time window.
 * </p>
 * <p>
 * Note that all counts reset on the hour, as measured by the wall clock. This allows us to avoid tracking individual
 * client requests and their timestamps.
 * </p>
 * <p>
 * We expose this guard as a JMX MBean so it can be queried and manipulated in management contexts.
 * </p>
 *
 */
@ManagedResource
public class RateLimitingThrottleTemplate extends AbstractGuard {
	private static final int MILLIS_PER_HOUR = 1000 * 60 * 60;
	private static Logger log = LoggerFactory.getLogger(RateLimitingThrottleTemplate.class);
	
	private final int limit;
	
	private volatile int currentHour;
	private final Map<Object, Integer> counts = new ConcurrentHashMap<Object, Integer>();
	
	/**
	 * @param limit maximum number of requests permitted in the time window
	 */
	public RateLimitingThrottleTemplate(int limit) {
		if (limit < 1) {
			throw new IllegalArgumentException("limit must be >= 1");
		}
		this.limit = limit;
		this.currentHour = (int) (System.currentTimeMillis() / MILLIS_PER_HOUR);
	}
	
	@ManagedAttribute(description = "Hourly per-principal rate limit, after which requests are rejected")
	public int getLimit() { return limit; }
	
	public <T> T execute(GuardCallback<T> action) throws Exception {
		resetCountsOnTheHour();
		Object principal = getPrincipal();
		int count = getCount(principal);
		
		if (++count <= limit) {
			log.debug("principal={}, count={}", principal, count);
			counts.put(principal, count);
			return action.doInGuard();
		} else {
			log.warn("Request rejected: rate limit {} exceeded", limit);
			throw new RateLimitExceededException(limit);
		}
	}
	
	private void resetCountsOnTheHour() {
		int newHour = (int) (System.currentTimeMillis() / MILLIS_PER_HOUR);
		if (newHour > currentHour) {
			this.currentHour = newHour;
			counts.clear();
		}
	}
	
	private Object getPrincipal() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication auth = context.getAuthentication();
		
		// FIXME There's probably a better way to detect anonymous auth.
		if (auth == null || auth instanceof AnonymousAuthenticationToken) {
			log.debug("Authentication required");
			throw new UnauthenticatedException();
		}
		
		return auth.getPrincipal();
	}
	
	private int getCount(Object principal) {
		Integer count = counts.get(principal);
		if (count == null) { count = 0; }
		return count;
	}
}
