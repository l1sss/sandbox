package ru.slisenko.springjmsexample.dao;

import javax.jms.TextMessage;

public interface JmsMessageDao {
    String SQL_INSERT = "INSERT INTO jmsMessages (innerMessageId) VALUES (?)";

    Long insert(TextMessage message);
}
