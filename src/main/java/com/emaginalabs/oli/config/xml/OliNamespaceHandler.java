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
package com.emaginalabs.oli.config.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * <code>NamespaceHandler</code> allowing for declarative Oli configuration using either XML or annotations. This
 * class registers parsers for the different namespace elements so we can process them.
 *
 */
public class OliNamespaceHandler extends NamespaceHandlerSupport {
	private static Logger log = LoggerFactory.getLogger(OliNamespaceHandler.class);

	/**
	 * Registers bean definition parsers for the various custom top-level Kite tags, such as
	 * <code>&lt;kite:circuit-breaker&gt;</code>.
	 */
	public void init() {
		log.info("Initializing KiteNamespaceHandler");
		registerBeanDefinitionParser("annotation-config", new AnnotationConfigParser());
		registerBeanDefinitionParser("guard-list-advice", new GuardListAdviceParser());
		registerBeanDefinitionParser("circuit-breaker", new CircuitBreakerParser());
		registerBeanDefinitionParser("concurrency-throttle", new ConcurrencyThrottleParser());
		registerBeanDefinitionParser("rate-limiting-throttle", new RateLimitingThrottleParser());
	}
}
