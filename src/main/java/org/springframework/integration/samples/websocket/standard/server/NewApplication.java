package org.springframework.integration.samples.websocket.standard.server;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.splitter.DefaultMessageSplitter;
import org.springframework.integration.transformer.AbstractPayloadTransformer;
import org.springframework.integration.transformer.HeaderEnricher;
import org.springframework.integration.transformer.support.ExpressionEvaluatingHeaderValueMessageProcessor;
import org.springframework.integration.websocket.ServerWebSocketContainer;
import org.springframework.integration.websocket.outbound.WebSocketOutboundMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;

/**
 * Created by Krishna Lingashetty on 9/13/16.
 */
@Configuration
@EnableAutoConfiguration
public class NewApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(NewApplication.class, args);
        System.out.println("Hit 'Enter' to terminate");
        System.in.read();
        ctx.close();
    }

    @Bean
    public ServerWebSocketContainer serverWebSocketContainer() {
        return new ServerWebSocketContainer("/stock").setAllowedOrigins("*");
    }

    @Bean
    @InboundChannelAdapter(value = "splitChannel", poller = @Poller(fixedDelay = "1000", maxMessagesPerPoll = "1"))
    public MessageSource<?> webSocketSessionsMessageSource() {
        return new MessageSource<Iterator<String>>() {

            @Override
            public Message<Iterator<String>> receive() {
                return new GenericMessage<Iterator<String>>(serverWebSocketContainer().getSessions().keySet().iterator());
            }

        };
    }

    @Bean
    public MessageChannel splitChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "splitChannel")
    public MessageHandler splitter() {
        DefaultMessageSplitter splitter = new DefaultMessageSplitter();
        splitter.setOutputChannelName("headerEnricherChannel");
        return splitter;
    }

    @Bean
    public MessageChannel headerEnricherChannel() {
        return new ExecutorChannel(Executors.newCachedThreadPool());
    }

    @Bean
    @Transformer(inputChannel = "headerEnricherChannel", outputChannel = "transformChannel")
    public HeaderEnricher headerEnricher() {
        return new HeaderEnricher(Collections.singletonMap(SimpMessageHeaderAccessor.SESSION_ID_HEADER,
                new ExpressionEvaluatingHeaderValueMessageProcessor<Object>("payload", null)));
    }

    @Bean
    @Transformer(inputChannel = "transformChannel", outputChannel = "sendStockChannel")
    public AbstractPayloadTransformer<?, ?> transformer() {
        return new AbstractPayloadTransformer<Object, Object>() {
            @Override
            protected Object transformPayload(Object payload) throws Exception {
                Map<String, String> returnData = new HashMap<String, String>();
                double stockPrice = 25.0f + Math.random() * 2,
                    base = 26.0;

                returnData.put("stockPrice", String.format("%.2f", stockPrice));

                returnData.put("changeValue", String.format("%.2f", stockPrice - base));
                returnData.put("changePct", String.format("%.2f", ((stockPrice - base) / base) * 100));

                return returnData;
            }

        };
    }

    @Bean
    public MessageChannel sendStockChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "sendStockChannel")
    public MessageHandler webSocketOutboundAdapter() {
        return new WebSocketOutboundMessageHandler(serverWebSocketContainer());
    }

    @Bean
    @ServiceActivator(inputChannel = "sendStockChannel")
    public MessageHandler loggingChannelAdapter() {
        LoggingHandler loggingHandler = new LoggingHandler("info");
        loggingHandler.setLogExpressionString(
                "'The time ' + payload + ' has been sent to the WebSocketSession ' + headers.simpSessionId");
        return loggingHandler;
    }
}
