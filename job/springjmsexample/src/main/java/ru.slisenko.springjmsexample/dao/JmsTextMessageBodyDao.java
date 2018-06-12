package ru.slisenko.springjmsexample.dao;

import ru.slisenko.springjmsexample.model.JmsTextMessageBody;

public interface JmsTextMessageBodyDao {
    String SQL_INSERT = "INSERT INTO jmsMessagesBodies (jmsMessageId, body) VALUES (?, ?)";

    Long insert(JmsTextMessageBody body);
}
