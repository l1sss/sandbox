package ru.slisenko.springjmsexample.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Message;
import java.util.UUID;

@Service
public class JmsMessageSender {
    private final static int MESSAGE_COUNT = 5;

    @Autowired
    private JmsTemplate jmsTemplate;

    public void send() throws InterruptedException {
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String text = String.format("JMS message:%d", i);
            jmsTemplate.send(session -> {
                Message message = session.createTextMessage(text);
                String innerMessageId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
                message.setStringProperty("innerMessageId", innerMessageId);
                return message;
            });
            Thread.sleep(1000);
        }
    }
}
