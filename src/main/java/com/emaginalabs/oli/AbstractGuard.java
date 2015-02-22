package com.emaginalabs.oli;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.jmx.export.annotation.ManagedAttribute;

/**
 * Abstract base class for implementing guards.
 *
 */
public abstract class AbstractGuard implements Guard, BeanNameAware {
	private String name;
	
	@ManagedAttribute(description = "Guard name")
	public String getName() { return name; }
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
	 */
	@Override
	public void setBeanName(String beanName) { this.name = beanName; }

}
