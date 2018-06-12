package ru.slisenko.springjmsexample.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Message;
import java.util.UUID;

@Service
public class JmsTextMessageSender {
    private final static Logger LOGGER = LoggerFactory.getLogger(JmsTextMessageSender.class);
    private final static int MESSAGE_COUNT = 10;

    @Autowired
    private JmsTemplate jmsTemplate;

    public void send() throws InterruptedException {
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String text = String.format("JMS message:%d", i);
            jmsTemplate.send(session -> {
                Message message = session.createTextMessage(text);
                String innerMessageId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
                message.setStringProperty("innerMessageId", innerMessageId);
                LOGGER.info("Send message | innerMessageId: {} | text: {}",
                        innerMessageId, text);
                return message;
            });
            Thread.sleep(1000);
        }
    }
}
