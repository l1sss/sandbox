package ru.slisenko.xml.transform;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.slisenko.xml.transform.dao.ArticlesDAO;
import ru.slisenko.xml.transform.dto.ArticleDTO;
import ru.slisenko.xml.transform.xml.XmlTransformer;
import ru.slisenko.xml.transform.xml.XmlWriter;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

public class Main {
    private static final String XML_FILE_NAME = "articles.xml";
    private static final String XLST_FILE_NAME = "articles.xslt";
    private static final String RESULT_FILE_NAME = "result.xml";

    public static void main(String[] args) throws IOException, TransformerException {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("spring/beans.xml");

//        ArticlesDAO articlesDAO = (ArticlesDAO) context.getBean("articlesDAO");
//        List<ArticleDTO> articles = articlesDAO.getAll();
//        System.out.println(articles);
//
//        XmlWriter xmlWriter = (XmlWriter) context.getBean("xmlWriter");
//        xmlWriter.writeToXml(articles, XML_FILE_NAME);

        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
        XmlTransformer transformer = (XmlTransformer) context.getBean("xmlTransformer");
        transformer.transform(XML_FILE_NAME, XLST_FILE_NAME, RESULT_FILE_NAME);
    }
}
