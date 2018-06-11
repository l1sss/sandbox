package ru.slisenko.springjmsexample.dao;

import ru.slisenko.springjmsexample.model.JmsMessageBody;

public interface JmsMessageBodyDao {
    String SQL_INSERT = "INSERT INTO jmsMessagesBodies (jmsMessageId, body) VALUES (?, ?)";

    Long insert(JmsMessageBody body);
}
