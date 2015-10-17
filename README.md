[![Build Status](https://travis-ci.org/joanluk/oli-guards.svg?branch=master)](https://travis-ci.org/joanluk/oli-guards)


Oli is a Spring-based library of components, called _guards_, implementing various patterns for managing app/service
availability, performance and capacity. It's based in part upon patterns in the Release It! book by Michael Nygard.

Harden your app in two easy steps
=================================

Let's say you want to protect an integration point with three guards: a concurrency throttle, a rate limiter and a
circuit breaker. While you could certainly code that logic by hand, with oli you don't need to do that. Instead all it
takes is two easy steps.

**Step 1.** First, you'll need to configure your guards:

```xml
<beans:beans xmlns="http://com.emaginalabs.org/schema/oli"
    xmlns:beans="http://www.springframework.org/schema/beans"
    xmlns:context="http://www.springframework.org/schema/context"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context-3.1.xsd
        http://emaginalabs.org/schema/oli
        http://emaginalabs.org/schema/oli/oli-1.0.xsd">

    <!-- Activate Oli annotations -->
    <annotation-config />
    
    <!-- Guards -->
    <circuit-breaker id="messageServiceBreaker" exceptionThreshold="3" timeout="30000" />
    <concurrency-throttle id="messageServiceThrottle" limit="50" />
    <rate-limiting-throttle id="messageServiceRateLimiter" limit="5000" />

    <!-- Export the guards as MBeans -->
    <context:mbean-export />

</beans:beans>
```

**Step 2.** Second, you'll need to annotate the service methods. I'm assuming a transactional service here, though that's not
required:

```java
@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    @GuardedBy({
        "messageServiceThrottle",
        "messageServiceRateLimiter",
        "messageServiceBreaker"
    })
    public Message getMotd() { ... }

    @GuardedBy({
        "messageServiceThrottle",
        "messageServiceRateLimiter",
        "messageServiceBreaker"
    })
    public List<Message> getMessages() { ... }
}
```

Voila: all calls to the service methods are now guarded by

* a concurrency throttle that rejects requests once there are 50 concurrent requests in the guard
* a rate-limiter that rejects anything beyond the first 5,000 requests in a given hour
* a circuit breakers that trips after three consecutive exceptions, and retries after 30 seconds

Oli applies the guards in the specified order. As an added bonus, the guards are both exposed as MBeans for manual
tripping, resetting, etc. by your NOC should the need arise.

Besides the annotation-based approach illustrated above, the standard template- and AOP-based approaches are also
available.

Available guards
================

This is a new-ish project, so there's not much yet, but here's what exists now:

**Circuit breaker:** Trips after a configurable number of consecutive exceptions, and retries after a configurable
timeout. Eventually it will be possible to trip based of failure rates, and it will be possible to select specific
exception types.

**Concurrency throttle:** A fail-fast concurrency throttle that rejects requests once a configurable concurrency limit
is reached. Eventually throttles will be able to reject requests based on failure to meet SLAs.

**Rate-limiting throttle:** A throttle that rejects requests after the principal reaches a configurable limit on the
number of requests in some time period. The rate limiter uses Spring Security to determine the principal involved.

I very much welcome contributions. It's pretty easy to add a new guard; just look at `com.emaginalabs.oli.guard` to see how
to do it.
