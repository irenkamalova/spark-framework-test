package com.test;

import spark.Service;
import spark.Session;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import java.util.Enumeration;

import static spark.Service.ignite;
import static spark.Spark.*;

public class HelloWorld {
    public static void main(String[] args) {
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
}