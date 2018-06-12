package ru.slisenko.springjmsexample.dao;

import javax.jms.JMSException;
import javax.jms.TextMessage;

public interface JmsTextMessageDao {
    String SQL_INSERT = "INSERT INTO jmsMessages (innerMessageId) VALUES (?)";

    Long insert(TextMessage message) throws JMSException;
}
