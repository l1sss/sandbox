package ru.slisenko.springjmsexample.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Service;
import ru.slisenko.springjmsexample.dao.JmsTextMessageDao;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

@Service
public class JmsTextMessageListener implements SessionAwareMessageListener<TextMessage> {
    private final static Logger LOGGER = LoggerFactory.getLogger(JmsTextMessageListener.class);

    @Autowired
    private JmsTextMessageDao jmsTextMessageDao;

    @Override
    public void onMessage(TextMessage message, Session session) throws JMSException {
//        System.out.println(Thread.currentThread().getName());
        LOGGER.info("Received message | innerMessageId: {} | text: {}",
                message.getStringProperty("innerMessageId"), message.getText());
        persist(message);
    }


    private void persist(TextMessage message) throws JMSException {
        jmsTextMessageDao.insert(message);
    }
}
