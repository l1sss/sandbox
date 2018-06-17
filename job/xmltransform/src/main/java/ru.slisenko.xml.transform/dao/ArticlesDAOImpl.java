package ru.slisenko.xml.transform.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.slisenko.xml.transform.dto.ArticleDTO;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArticlesDAOImpl implements ArticlesDAO {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public ArticlesDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<ArticleDTO> getAll() {
        List<ArticleDTO> articles = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(SQL_SELECT_FROM_ARTICLES);
        for (Map row : rows) {
            ArticleDTO article = new ArticleDTO();
            article.setId(Long.parseLong(String.valueOf(row.get("id_art"))));
            article.setName((String) row.get("name"));
            article.setCode(Long.parseLong(String.valueOf(row.get("code"))));
            article.setUsername((String) row.get("username"));
            article.setGuid((String) row.get("guid"));
            articles.add(article);
        }
        return articles;
    }
}
