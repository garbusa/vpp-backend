package de.uol.vpp.action.infrastructure.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Konfiguration,
 * definiert die Exchanges und Queues und bindet die Queues an die Exchanges
 */
@Configuration
public class RabbitMQConfig {

    @Value("${vpp.rabbitmq.exchange.actionRequest}")
    String actionRequestExchange;

    @Value("${vpp.rabbitmq.exchange.forecastGeneration}")
    String forecastGenerationExchange;

    @Value("${vpp.rabbitmq.exchange.actionRequestFailed}")
    String actionRequestFailedExchange;


    @Value("${vpp.rabbitmq.queue.load.to.action}")
    String loadToActionQueue;

    @Value("${vpp.rabbitmq.key.load.to.action}")
    String loadToActionKey;

    @Value("${vpp.rabbitmq.queue.production.to.action}")
    String productionToActionQueue;

    @Value("${vpp.rabbitmq.key.production.to.action}")
    String productionToActionKey;

    @Value("${vpp.rabbitmq.queue.action.to.load}")
    String actionToLoadQueue;

    @Value("${vpp.rabbitmq.key.action.to.load}")
    String actionToLoadKey;

    @Value("${vpp.rabbitmq.queue.action.to.production}")
    String actionToProductionQueue;

    @Value("${vpp.rabbitmq.key.action.to.production}")
    String actionToProductionKey;

    @Value("${vpp.rabbitmq.queue.load.to.action.failed}")
    String loadToActionFailedQueue;

    @Value("${vpp.rabbitmq.key.load.to.action.failed}")
    String loadToActionFailedKey;

    @Value("${vpp.rabbitmq.queue.production.to.action.failed}")
    String productionToActionFailedQueue;

    @Value("${vpp.rabbitmq.key.production.to.action.failed}")
    String productionToActionFailedKey;


    @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    Queue loadToActionQueue() {
        return new Queue(loadToActionQueue, false);
    }

    @Bean
    Queue productionToActionQueue() {
        return new Queue(productionToActionQueue, false);
    }

    @Bean
    Queue loadToActionFailedQueue() {
        return new Queue(loadToActionFailedQueue, false);
    }

    @Bean
    Queue productionToActionFailedQueue() {
        return new Queue(productionToActionFailedQueue, false);
    }

    @Bean
    Queue actionToLoadQueue() {
        return new Queue(actionToLoadQueue, false);
    }

    @Bean
    Queue actionToProductionQueue() {
        return new Queue(actionToProductionQueue, false);
    }

    @Bean
    DirectExchange actionRequestExchange() {
        return new DirectExchange(actionRequestExchange);
    }

    @Bean
    DirectExchange forecastGenerationExchange() {
        return new DirectExchange(forecastGenerationExchange);
    }

    @Bean
    DirectExchange actionRequestFailedExchange() {
        return new DirectExchange(actionRequestFailedExchange);
    }

    @Bean
    Binding bindingLoadToAction(Queue loadToActionQueue, DirectExchange forecastGenerationExchange) {
        return BindingBuilder.bind(loadToActionQueue).to(forecastGenerationExchange).with(loadToActionKey);
    }

    @Bean
    Binding bindingProductionToAction(Queue productionToActionQueue, DirectExchange forecastGenerationExchange) {
        return BindingBuilder.bind(productionToActionQueue).to(forecastGenerationExchange).with(productionToActionKey);
    }

    @Bean
    Binding bindingActionToProduction(Queue actionToProductionQueue, DirectExchange actionRequestExchange) {
        return BindingBuilder.bind(actionToProductionQueue).to(actionRequestExchange).with(actionToProductionKey);
    }

    @Bean
    Binding bindingActionToLoad(Queue actionToLoadQueue, DirectExchange actionRequestExchange) {
        return BindingBuilder.bind(actionToLoadQueue).to(actionRequestExchange).with(actionToLoadKey);
    }

    @Bean
    Binding bindingLoadToActionFailed(Queue loadToActionFailedQueue, DirectExchange actionRequestFailedExchange) {
        return BindingBuilder.bind(loadToActionFailedQueue).to(actionRequestFailedExchange).with(loadToActionFailedKey);
    }

    @Bean
    Binding bindingProductionToActionFailed(Queue productionToActionFailedQueue, DirectExchange actionRequestFailedExchange) {
        return BindingBuilder.bind(productionToActionFailedQueue).to(actionRequestFailedExchange).with(productionToActionFailedKey);
    }


}