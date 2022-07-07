package com.test.javalin;

import io.javalin.Javalin;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;

public class HelloJavalin {

    public static void mainCool(String[] args) {
        var app = Javalin.create(c -> {
            c.asyncRequestTimeout = 100L;
        });

        app.get("/", ctx -> {

            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ctx.result("Hello World");
        });
        app.start(8000);
    }

    public static void main(String[] args) {
        var app = Javalin.create(config -> {
            config.configureServletContextHandler(context -> {
                DoSFilter filter = new DoSFilter();
                FilterHolder holder = new FilterHolder(filter);
                String name = "FilterHolder";
                holder.setName(name);
                holder.setInitParameter("maxRequestMs", "1000");
                context.addFilter(holder, "/*", EnumSet.of(DispatcherType.REQUEST));
            });
        });

        app.get("/", ctx -> {
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ctx.result("Hello World");
        });
        app.start(7070);
    }


    public static void maind(String[] args) {
        Javalin app = Javalin.create(config -> {
                Server server = config.inner.server;
                ServletContextHandler context = new ServletContextHandler(server, "/");
                //DoSFilter filter = new DoSFilter();
                //filter.setMaxRequestMs(100);
                FilterHolder holder = new FilterHolder();//filter);
                String name = "FilterHolder";
                holder.setName(name);
                holder.setInitParameter("maxRequestMs", "1000");
                context.addFilter(holder, "/*", EnumSet.of(DispatcherType.REQUEST));
                context.setInitParameter(ServletContextHandler.MANAGED_ATTRIBUTES, name);
                context.addServlet(HelloServlet.class, "/hello");
        });
        app.get("/", new HelloJavalinHandler());
        app.start(7070);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        //response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        response.getWriter().println("Hello World");
    }
}
