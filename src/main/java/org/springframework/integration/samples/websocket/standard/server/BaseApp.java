package org.springframework.integration.samples.websocket.standard.server;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public abstract class BaseApp {


    public static void main(String[] args) throws Exception {
    	Class[] clazz = new Class[] { StockApplication.class, NewsApplication.class};
        ConfigurableApplicationContext ctx = SpringApplication.run(clazz, args);
        System.out.println("Hit 'Enter' to terminate");
        System.in.read();
        ctx.close();
    }

}
