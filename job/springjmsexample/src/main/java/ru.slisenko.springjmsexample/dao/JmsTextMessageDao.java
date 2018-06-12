package ru.slisenko.springjmsexample.dao;

import javax.jms.JMSException;
import javax.jms.TextMessage;

public interface JmsTextMessageDao {
    String SQL_INSERT_HEADERS = "INSERT INTO jmsMessages (innerMessageId) VALUES (?)";
    String SQL_INSERT_BODY = "INSERT INTO jmsMessagesBodies (jmsMessageId, body) VALUES (?, ?)";

    void insert(TextMessage message) throws JMSException;
}
