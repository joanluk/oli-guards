package com.emaginalabs.oli;

/**
 * <p>
 * The primary oli abstraction, a <code>Guard</code> is a component that protects your service from performance and
 * availability problems. Examples of guards include circuit breakers and throttles.
 * </p>
 * <p>
 * Guards implement the template pattern, wrapping some action you want to protect with the code to protect it. In
 * general the action is a call to a service method, though it doesn't have to be.
 * </p>
 * <p>
 * There are three different ways to use Oli guards:
 * </p>
 * <ul>
 * <li>The most basic way is to use them programmatically. This is useful when you want fine-grained control over the
 * code you want to wrap, but most of the time you don't need that level of control.</li>
 * <li>You can use AOP to attach guards to methods in a declarative fashion.</li>
 * <li>You can use annotations to attach guards to methods. This is the recommended approach.</li>
 * </ul>
 * <p>
 * In addition to the above, Oli has a custom namespace that allows you to streamline your Spring configuration,
 * including guard definition.
 * </p>
 *
 */
public interface Guard {
	
	/**
	 * Returns the guard name.
	 * 
	 * @return guard name
	 */
	String getName();
	
	/**
	 * Executes the given action inside the guard.
	 * 
	 * @param action action to execute
	 * @return result of the execution
	 * @throws Exception if there's a problem while executing the action
	 */
	<T> T execute(GuardCallback<T> action) throws Exception;
}
