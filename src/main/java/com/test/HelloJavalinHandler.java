package com.test;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class HelloJavalinHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var request = ctx.req;
        var response = ctx.res;
        response.getWriter().println("Hello World");
    }
}
