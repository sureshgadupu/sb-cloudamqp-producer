package dev.fullstackcode.sb.rabbitmq.producer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {


    @Bean
    Queue queueImages() {
        return new Queue("queue.images", false);
    }

    @Bean
    Queue queueDocs() {
        return new Queue("queue.docs", false);
    }

    @Bean
    Queue queueMessages() {
        return new Queue("queue.messages", false);
    }

    @Bean
    Queue queueJsonMessages() {
        return new Queue("queue.jsonmessages", false);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange("exchange.direct");
    }

    @Bean
    Binding bindingImageQueue(Queue queueImages, DirectExchange exchange) {
        return BindingBuilder.bind(queueImages).to(exchange).with("event_image");
    }

    @Bean
    Binding bindingDocQueue(Queue queueDocs, DirectExchange exchange) {
        return BindingBuilder.bind(queueDocs).to(exchange).with("event_doc");
    }

    @Bean
    Binding bindingMessageQueue(Queue queueMessages, DirectExchange exchange) {
        return BindingBuilder.bind(queueMessages).to(exchange).with("event_message");
    }

    @Bean
    Binding bindingJsonMessageQueue(Queue queueJsonMessages, DirectExchange exchange) {
        return BindingBuilder.bind(queueJsonMessages).to(exchange).with("event_jsonMessage");
    }

    @Bean
    ApplicationRunner runner(ConnectionFactory cf) {
        return args -> cf.createConnection().close();
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

}
