package ru.slisenko.springjmsexample.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.slisenko.springjmsexample.model.JmsTextMessageBody;

import java.sql.PreparedStatement;

@Component
public class JmsTextMessageBodyDaoImpl implements JmsTextMessageBodyDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(JmsTextMessageBodyDaoImpl.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Long insert(final JmsTextMessageBody body) {
        PreparedStatementCreator creator = connection -> {
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT, new String[] {"id"});
                    statement.setLong(1, body.getJmsMessageId());
                    statement.setString(2, body.getText());
                    return statement;
        };
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(creator, holder);
        LOGGER.info("MessageBody is saved in DB | innerMessageId: {} | text: {}",
                body.getJmsMessageId(), body.getText());
        return holder.getKey().longValue();
    }
}
