package ru.slisenko.xml.transform.dao;

import ru.slisenko.xml.transform.dto.ArticleDTO;

import java.util.List;

public interface ArticlesDAO {
    String SQL_SELECT_FROM_ARTICLES = "SELECT id_art, name, code, username, guid FROM articles";

    List<ArticleDTO> getAll();
}
