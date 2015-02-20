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

import com.emaginalabs.oli.guard.ConcurrencyThrottleTemplate;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;


/**
 * Parses <code>&lt;kite:concurrency-throttle&gt;</code> elements in Spring application context configuration files.
 *
 */
class ConcurrencyThrottleParser extends AbstractSingleBeanDefinitionParser {
	
	@Override
	protected Class<?> getBeanClass(Element elem) {
		return ConcurrencyThrottleTemplate.class;
	}
	
	@Override
	protected void doParse(Element elem, BeanDefinitionBuilder builder) {
		// FIXME Hm, would like to set the bean definition's source, but the builder.setSource() method is deprecated...
		builder.addConstructorArgValue(elem.getAttribute("limit"));
	}
}
