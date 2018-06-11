package ru.slisenko.springjmsexample.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Service;
import ru.slisenko.springjmsexample.dao.JmsMessageBodyDao;
import ru.slisenko.springjmsexample.dao.JmsMessageDao;
import ru.slisenko.springjmsexample.model.JmsMessageBody;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

@Service
public class JmsMessageListener implements SessionAwareMessageListener<TextMessage> {

    @Autowired
    private JmsMessageDao jmsMessageDao;

    @Autowired
    private JmsMessageBodyDao jmsMessageBodyDao;

    @Override
    public void onMessage(TextMessage message, Session session) throws JMSException {
        //System.out.println(Thread.currentThread().getName());
        System.out.printf("Receive: %s with id: %s\n", message.getText(), message.getStringProperty("innerMessageId"));
        persist(message);
    }

    private void persist(TextMessage message) throws JMSException {
        Long tableMessageId = jmsMessageDao.insert(message);
        JmsMessageBody body = new JmsMessageBody(tableMessageId, message.getText());
        jmsMessageBodyDao.insert(body);
    }
}
