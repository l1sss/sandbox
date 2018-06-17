package ru.slisenko.xml.transform.xml;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import ru.slisenko.xml.transform.dto.ArticleDTO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class XmlWriter {

    public void writeToXml(List<ArticleDTO> articles, String fileName) throws IOException {
        Document doc = new Document();
        doc.setRootElement(new Element("articles"));
        for (ArticleDTO article : articles) {
            Element articleElement = new Element("article");
            articleElement.setAttribute("id_art", String.valueOf(article.getId()));
            articleElement.setAttribute("name", article.getName());
            articleElement.setAttribute("code", String.valueOf(article.getCode()));
            articleElement.setAttribute("username", article.getUsername());
            articleElement.setAttribute("guid", article.getGuid());
            doc.getRootElement().addContent(articleElement);
        }
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        xmlOutputter.output(doc, new FileOutputStream(fileName));
    }
}
