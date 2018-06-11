package ru.slisenko.springjmsexample.model;

public class JmsMessageBody {
    private Long id;
    private Long jmsMessageId;
    private String text;

    public JmsMessageBody(Long jmsMessageId, String text) {
        this.jmsMessageId = jmsMessageId;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJmsMessageId() {
        return jmsMessageId;
    }

    public void setJmsMessageId(Long jmsMessageId) {
        this.jmsMessageId = jmsMessageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
