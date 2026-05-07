package id.ac.ui.cs.advprog.bidmartcatalog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    // ── Exchange ─────────────────────────────────────────────────────────────

    public static final String EXCHANGE_AUCTION_EVENTS = "auction.events";

    // ── Routing Keys ─────────────────────────────────────────────────────────

    public static final String RK_BID_PLACED      = "auction.event.bid-placed";
    public static final String RK_AUCTION_CLOSED  = "auction.event.auction-closed";

    // ── Queue Names ───────────────────────────────────────────────────────────

    public static final String QUEUE_BID_PLACED     = "catalog.bid-placed";
    public static final String QUEUE_AUCTION_CLOSED = "catalog.auction-closed";

    // ── DLQ Names ─────────────────────────────────────────────────────────────

    public static final String DLQ_BID_PLACED     = "catalog.bid-placed.dlq";
    public static final String DLQ_AUCTION_CLOSED = "catalog.auction-closed.dlq";
    public static final String DLX_NAME           = "auction.events.dlx";

    // ── Beans: Exchange ───────────────────────────────────────────────────────

    @Bean
    TopicExchange auctionEventsExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_AUCTION_EVENTS).durable(true).build();
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(DLX_NAME).durable(true).build();
    }

    // ── Beans: Queues ─────────────────────────────────────────────────────────

    @Bean
    Queue bidPlacedQueue() {
        return QueueBuilder.durable(QUEUE_BID_PLACED)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_BID_PLACED)
                .build();
    }

    @Bean
    Queue auctionClosedQueue() {
        return QueueBuilder.durable(QUEUE_AUCTION_CLOSED)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_AUCTION_CLOSED)
                .build();
    }

    @Bean
    Queue bidPlacedDlq() {
        return QueueBuilder.durable(DLQ_BID_PLACED).build();
    }

    @Bean
    Queue auctionClosedDlq() {
        return QueueBuilder.durable(DLQ_AUCTION_CLOSED).build();
    }

    // ── Beans: Bindings ───────────────────────────────────────────────────────

    @Bean
    Binding bidPlacedBinding() {
        return BindingBuilder.bind(bidPlacedQueue())
                .to(auctionEventsExchange())
                .with(RK_BID_PLACED);
    }

    @Bean
    Binding auctionClosedBinding() {
        return BindingBuilder.bind(auctionClosedQueue())
                .to(auctionEventsExchange())
                .with(RK_AUCTION_CLOSED);
    }

    @Bean
    Binding bidPlacedDlqBinding() {
        return BindingBuilder.bind(bidPlacedDlq())
                .to(deadLetterExchange())
                .with(DLQ_BID_PLACED);
    }

    @Bean
    Binding auctionClosedDlqBinding() {
        return BindingBuilder.bind(auctionClosedDlq())
                .to(deadLetterExchange())
                .with(DLQ_AUCTION_CLOSED);
    }

    // ── Beans: Converter & Template ───────────────────────────────────────────

    @Bean
    MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        // Manual ack: consumer mengontrol kapan pesan di-ack
        factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.MANUAL);
        return factory;
    }
}
