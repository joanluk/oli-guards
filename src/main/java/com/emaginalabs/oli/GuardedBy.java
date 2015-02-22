package com.emaginalabs.oli;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Annotation indicating methods to be protected by one or more guards. A future version of Kite will support using this
 * annotation on classes to guard all methods.
 * </p>
 * <p>IMPORTANT: The ordering of the guards is significant: we process guards earlier in the list before guards later in
 * the list. This is very important, since you don't want to put a guard that generates client-induced exceptions behind
 * a circuit breaker. For example, you don't want a rate limiter behind a circuit breaker because then the client can
 * take down the service by exceding its rate limit. A future version on the library will address this potential issue
 * in some way, perhaps by having a fixed order for the guards, perhaps by having circuit breakers ignore client
 * exceptions, or some combination of the two.
 * </p>
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GuardedBy {

	String[] value() default "";
}
