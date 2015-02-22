package com.emaginalabs.oli;

/**
 * Callback interface for the {@link Guard} template.
 *
 */
public interface GuardCallback<T> {
	
	/**
	 * @return execution result
	 * @throws Exception if there's a problem executing the callback
	 */
	T doInGuard() throws Exception;
}
