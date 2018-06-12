package ru.slisenko.springjmsexample.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.sql.PreparedStatement;

@Component
public class JmsTextMessageDaoImpl implements JmsTextMessageDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(JmsTextMessageBodyDaoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Long insert(final TextMessage message) throws JMSException {
        PreparedStatementCreator creator = connection -> {
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT, new String[]{"id"});
            String innerMessageId = null;
            try {
                innerMessageId = message.getStringProperty("innerMessageId");
            } catch (JMSException e) {
                throw new RuntimeException(e.getMessage());
            }
            statement.setString(1, innerMessageId);
            return statement;
        };
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(creator, holder);
        LOGGER.info("Message is saved in DB | innerMessageId: {} | text: {}",
                message.getStringProperty("innerMessageId"), message.getText());
        return holder.getKey().longValue();
    }
}
