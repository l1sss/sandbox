package ru.slisenko.xml.transform.xml;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

public class XmlTransformer {

    public void transform(String source, String xslt, String result) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(new File(xslt)));
        transformer.transform(new StreamSource(new File(source)), new StreamResult(new File(result)));
    }
}
