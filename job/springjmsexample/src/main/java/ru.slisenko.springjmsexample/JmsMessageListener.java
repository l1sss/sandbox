package ru.slisenko.springjmsexample;

import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

@Service
public class JmsMessageListener implements SessionAwareMessageListener<TextMessage> {

    @Override
    public void onMessage(TextMessage message, Session session) throws JMSException {
        //System.out.println(Thread.currentThread().getName());
        System.out.printf("Receive: %s with id: %s\n", message.getText(), message.getStringProperty("innerMessageId"));
    }
}
