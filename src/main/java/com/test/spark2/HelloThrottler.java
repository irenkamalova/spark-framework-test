package com.test.spark2;

import spark.Route;
import spark.Session;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.threadPool;

public class HelloThrottler {

    /**
     * http://localhost:8080/hello - return 5 as an attribute of session
     * http://localhost:8080/helloThrottled - return 408 Request timeout
     */
    public static void main(String[] args) {
        port(8080);
        int maxThreads = 4;
        int minThreads = 1;
        int timeOutMillis = 1;
        threadPool(maxThreads, minThreads, timeOutMillis);
        Route route = (request, res) -> {
            Session session = request.session();
            session.attribute("attribute", 5);
            session.maxInactiveInterval(1);
            Thread.sleep(1000);
            return session.attribute("attribute");
        };

        get("/hello", route);
        get("/helloThrottled", new ThrottledRoute(route, 500));
    }
}
