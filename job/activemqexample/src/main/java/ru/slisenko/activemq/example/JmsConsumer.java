package ru.slisenko.activemq.example;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JmsConsumer implements MessageListener, AutoCloseable {
    private final ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    public JmsConsumer(String url, String queue) throws JMSException {
        connectionFactory = new ActiveMQConnectionFactory(url);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination dest = session.createQueue(queue);
        consumer = session.createConsumer(dest);
        consumer.setMessageListener(this);
    }

    public void onMessage(Message msg) {
        if (msg instanceof TextMessage) {
            try {
                System.out.println("Received message: " + ((TextMessage) msg).getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        } else System.out.println("Received message: " + msg.getClass().getName());
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
