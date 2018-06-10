package ru.slisenko.activemq.example;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JmsProducer extends Thread implements AutoCloseable {
    private final static String DEF_QUEUE = "testQueue";
    private final ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private MessageProducer producer;

    public JmsProducer(String url) throws JMSException {
        connectionFactory = new ActiveMQConnectionFactory(url);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination dest = session.createQueue(DEF_QUEUE);
        producer = session.createProducer(dest);
    }

    public void send(String text) throws JMSException {
        TextMessage textMessage = session.createTextMessage();
        textMessage.setText(text);
        producer.send(textMessage);
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