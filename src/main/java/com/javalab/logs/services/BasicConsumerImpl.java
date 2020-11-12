package com.javalab.logs.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class BasicConsumerImpl implements BasicConsumer {

    private final ConnectionFactory connectionFactory;
    private final String exchangeName;
    private final String exchangeType;
    private Connection connection;

    public BasicConsumerImpl(ConnectionFactory connectionFactory,
                             @Value("${past.exchange.name}") String exchangeName,
                             @Value("${past.exchange.type}") String exchangeType) {
        this.connectionFactory = connectionFactory;
        this.exchangeName = exchangeName;
        this.exchangeType = exchangeType;
    }


    @PostConstruct
    private void init() {
        try {
            connection = connectionFactory.newConnection();
            consume();
        } catch (IOException | TimeoutException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void consume() throws IOException {
        Channel channel = connection.createChannel();
        channel.basicQos(1);
        channel.exchangeDeclare(exchangeName, exchangeType);
        String queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue, exchangeName, "");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };
        channel.basicConsume(queue, true, deliverCallback, consumerTag -> {});
    }
}
