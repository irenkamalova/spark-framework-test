package com.test.spark2;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

public class ThrottledRoute implements Route {

    private final Scheduler scheduler;
    private final Route route;
    private final long maxRequestMs;
    private static final Logger LOG = Log.getLogger(ThrottledRoute.class);

    public ThrottledRoute(Route route, long maxRequestMs) {
        this.route = route;
        this.maxRequestMs = maxRequestMs;
        scheduler = startScheduler();
    }

    private Scheduler startScheduler() {
        try {
            Scheduler result = new ScheduledExecutorScheduler(String.format("Long-Request-Scheduler-%x", hashCode()), false);
            result.start();
            return result;
        } catch (Exception exception) {
            LOG.warn("Failed to start Scheduler");
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        final Thread thread = Thread.currentThread();
        Runnable requestTimeout = () -> onRequestTimeout(request.raw(), response.raw(), thread);
        Scheduler.Task task = scheduler.schedule(requestTimeout, getMaxRequestMs(), TimeUnit.MILLISECONDS);
        try {
            return route.handle(request, response);
        }
        finally {
            task.cancel();
        }
    }

    private long getMaxRequestMs() {
        return maxRequestMs;
    }

    protected void onRequestTimeout(HttpServletRequest request, HttpServletResponse response, Thread handlingThread) {
        try {
            LOG.debug("Timing out {}", request);
            response.sendError(HttpStatus.REQUEST_TIMEOUT_408);
        } catch (Exception exception) {
            LOG.warn(exception);
        } finally {
            handlingThread.interrupt();
        }
    }
}
