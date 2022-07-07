package com.test.spark1;

import spark.Request;
import spark.Response;
import spark.RouteImpl;
import spark.Service;
import spark.Session;
import spark.route.HttpMethod;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import java.util.Enumeration;

import static spark.Service.ignite;
import static spark.Spark.*;

public class HelloWorld {
    public static void main1(String[] args) {
        port(8080);
        int maxThreads = 4;
        int minThreads = 1;
        int timeOutMillis = 1;
        threadPool(maxThreads, minThreads, timeOutMillis);
        get("/hello", (request, res) -> {

           Session session = request.session();// session management
            session.attribute("attribute", 5);
            session.maxInactiveInterval(1);



            Thread.sleep(10 * 1000);
            var attr = session.attribute("attribute");
            // even attr will be the same!
            return attr;
        });
    }

    public static void main(String[] args) {
        Service service = ignite().port(8080);
        // no way to get out jetty embedded server
        FilterConfig filterConfig = filterConfig();
        RouteImpl route = new RouteImpl("/hello", "*/*") {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                Thread.sleep(10 * 1000);
                return "Hello Route";
            }
        };
        service.addRoute(HttpMethod.get, new ThrottledRouteImpl("/hello", "*/*", filterConfig, route));
        //service.addRoute(HttpMethod.get, route);

        service.get("/hello5", (request, res) -> {

            Session session = request.session();// session management
            session.attribute("attribute", 5);
            session.maxInactiveInterval(1);



            Thread.sleep(10 * 1000);
            var attr = session.attribute("attribute");
            // even attr will be the same!
            return attr;
        });

        service.init();
    }

    static FilterConfig filterConfig() {
        // todo design filter config
        return new FilterConfig() {
            @Override
            public String getFilterName() {
                return null;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public String getInitParameter(String s) {
                return null;
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return null;
            }
        };
    }
}