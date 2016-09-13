package org.springframework.integration.samples.websocket.standard.server;

import java.util.Collections;
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

@Configuration
@EnableAutoConfiguration

public class NewsApplication extends BaseApp{

	public static void main(String[] args) throws Exception {
		Class[] clazz = new Class[] { NewsApplication.class};
		ConfigurableApplicationContext ctx = SpringApplication.run(clazz, args);
		System.out.println("Hit 'Enter' to terminate");
		System.in.read();
		ctx.close();
	}

	@Bean
	public ServerWebSocketContainer newServerWebSocketContainer() {
		return new ServerWebSocketContainer("/news").setAllowedOrigins("*");
	}

	@Bean
	@InboundChannelAdapter(value = "newssplitChannel", poller = @Poller(fixedDelay = "3000", maxMessagesPerPoll = "1"))
	public MessageSource<?> newswebSocketSessionsMessageSource() {
		return new MessageSource<Iterator<String>>() {

			@Override
			public Message<Iterator<String>> receive() {
				return new GenericMessage<Iterator<String>>(newServerWebSocketContainer().getSessions().keySet().iterator());
			}

		};
	}

	@Bean
	public MessageChannel newssplitChannel() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "newssplitChannel")
	public MessageHandler newssplitter() {
		DefaultMessageSplitter splitter = new DefaultMessageSplitter();
		splitter.setOutputChannelName("newsheaderEnricherChannel");
		return splitter;
	}

	@Bean
	public MessageChannel newsheaderEnricherChannel() {
		return new ExecutorChannel(Executors.newCachedThreadPool());
	}

	@Bean
	@Transformer(inputChannel = "newsheaderEnricherChannel", outputChannel = "newstransformChannel")
	public HeaderEnricher newsheaderEnricher() {
		return new HeaderEnricher(Collections.singletonMap(SimpMessageHeaderAccessor.SESSION_ID_HEADER,
				new ExpressionEvaluatingHeaderValueMessageProcessor<Object>("payload", null)));
	}

	@Bean
	@Transformer(inputChannel = "newstransformChannel", outputChannel = "newssendStockChannel")
	public AbstractPayloadTransformer<?, ?> newstransformer() {
		return new AbstractPayloadTransformer<Object, Object>() {
			@Override
			protected Object transformPayload(Object payload) throws Exception {
				Map<String, NewsArticleDTO> returnData = new HashMap<String, NewsArticleDTO>();
				returnData.put("newarticle", new NewsArticleDTO());

				return returnData;
			}

		};
	}

	@Bean
	public MessageChannel newssendStockChannel() {
		return new PublishSubscribeChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "newssendStockChannel")
	public MessageHandler newswebSocketOutboundAdapter() {
		return new WebSocketOutboundMessageHandler(newServerWebSocketContainer());
	}

	@Bean
	@ServiceActivator(inputChannel = "newssendStockChannel")
	public MessageHandler newsloggingChannelAdapter() {
		LoggingHandler loggingHandler = new LoggingHandler("info");
		loggingHandler.setLogExpressionString(
				"'News ' + payload + ' has been sent to the WebSocketSession ' + headers.simpSessionId");
		return loggingHandler;
	}
}
