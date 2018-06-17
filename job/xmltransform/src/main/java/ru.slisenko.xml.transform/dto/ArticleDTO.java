package ru.slisenko.xml.transform.dto;

public class ArticleDTO {
    private long id;
    private String name;
    private long code;
    private String username;
    private String guid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public String toString() {
        return "ArticleDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code=" + code +
                ", username='" + username + '\'' +
                ", guid='" + guid + '\'' +
                '}';
    }
}
