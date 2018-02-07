package com.mobileapps.brad.songscroller;

import android.util.Log;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by brad on 2/6/18.
 */

public class XMLParser {
    private static Document document;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public XMLParser() {
    }

    public XMLParser(String xml) {
        try {
            document = loadXmlFromString(xml);
        }
        catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }

    public static Document loadXmlFromString (String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
       // InputSource is = new InputSource(xml);
        return builder.parse( new InputSource(new StringReader(xml)));
    }

    public String getSingleItem (String tagName) {
        String item = getDocument().getElementsByTagName(tagName).item(0).getTextContent();
        return item == null ? "" : item;
    }
}
