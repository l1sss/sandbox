package ru.slisenko.springjmsexample.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.sql.PreparedStatement;

@Component
public class JmsTextMessageDaoImpl implements JmsTextMessageDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(JmsTextMessageDaoImpl.class);
    private String innerMessageId;
    private String body;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void insert(TextMessage message) throws JMSException {
        innerMessageId = message.getStringProperty("innerMessageId");
        body = message.getText();
        Long tableMessageId = insertHeaders();
        insertBody(tableMessageId);
    }

    private Long insertHeaders() throws JMSException {
        PreparedStatementCreator creator = connection -> {
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT_HEADERS, new String[]{"id"});
            statement.setString(1, innerMessageId);
            return statement;
        };
        LOGGER.info("Message is saved in DB | innerMessageId: {} | text: {}", innerMessageId, body);
        return getKey(creator);
    }

    private Long getKey(PreparedStatementCreator creator) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(creator, holder);
        return holder.getKey().longValue();
    }

    private void insertBody(Long tableMessageId) {
        PreparedStatementCreator creator = connection -> {
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT_BODY, new String[]{"id"});
            statement.setLong(1, tableMessageId);
            statement.setString(2, body);
            if (10 / 2 == 5) throw new RuntimeException("Exception in second persist");
            return statement;
        };
        jdbcTemplate.update(creator);
        LOGGER.info("MessageBody is saved in DB | innerMessageId: {} | text: {}",
                innerMessageId, body);
    }
}
