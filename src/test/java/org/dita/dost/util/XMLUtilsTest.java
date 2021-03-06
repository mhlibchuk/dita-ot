/*
 * This file is part of the DITA Open Toolkit project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.dita.dost.util;

import static javax.xml.XMLConstants.*;
import static org.junit.Assert.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Attr;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.junit.Test;

public class XMLUtilsTest {

    @Test
    public void testAddOrSetAttributeAttributesImplStringStringStringStringString() {
        final AttributesImpl atts = new AttributesImpl();
        XMLUtils.addOrSetAttribute(atts, "foo", "foo", "foo", "CDATA", "foo");
        assertEquals(1, atts.getLength());
        XMLUtils.addOrSetAttribute(atts, "bar", "bar", "bar", "CDATA", "bar");
        assertEquals(2, atts.getLength());
        XMLUtils.addOrSetAttribute(atts, "foo", "foo", "foo", "CDATA", "bar");
        assertEquals(2, atts.getLength());
    }

    @Test
    public void testAddOrSetAttributeAttributesImplStringString() {
        final AttributesImpl atts = new AttributesImpl();
        XMLUtils.addOrSetAttribute(atts, "foo", "foo");
        assertEquals(1, atts.getLength());
        XMLUtils.addOrSetAttribute(atts, "bar", "bar");
        assertEquals(2, atts.getLength());
        XMLUtils.addOrSetAttribute(atts, "foo", "bar");
        assertEquals(2, atts.getLength());
    }

    @Test
    public void testAddOrSetAttributeAttributesImplNode() throws ParserConfigurationException {
        final AttributesImpl atts = new AttributesImpl();
        final DOMImplementation dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
        final Document doc = dom.createDocument(null, "foo", null);
        
        doc.getDocumentElement().setAttribute("foo", "foo");
        final Attr att = (Attr) doc.getDocumentElement().getAttributeNode("foo");
        XMLUtils.addOrSetAttribute(atts, att);

        final int i = atts.getIndex(NULL_NS_URI, "foo");
        assertEquals(NULL_NS_URI, atts.getURI(i));
        assertEquals("foo", atts.getQName(i));
        assertEquals("foo", atts.getLocalName(i));
        assertEquals("foo", atts.getValue(i));
        
        doc.getDocumentElement().setAttributeNS(XML_NS_URI, "xml:lang", "en");
        final Attr lang = (Attr) doc.getDocumentElement().getAttributeNodeNS(XML_NS_URI, "lang");
        XMLUtils.addOrSetAttribute(atts, lang);
        
        final int l = atts.getIndex(XML_NS_URI, "lang");
        assertEquals(XML_NS_URI, atts.getURI(l));
        assertEquals("xml:lang", atts.getQName(l));
        assertEquals("lang", atts.getLocalName(l));
        assertEquals("en", atts.getValue(l));
    }

    @Test
    public void testRemoveAttribute() {
        final AttributesImpl atts = new AttributesImpl();
        XMLUtils.addOrSetAttribute(atts, "foo", "foo", "foo", "CDATA", "foo");
        assertEquals(1, atts.getLength());
        XMLUtils.removeAttribute(atts, "foo");
        assertEquals(0, atts.getLength());
    }

    @Test
    public void testGetStringValue() throws ParserConfigurationException {
        final DOMImplementation dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
        final Document doc = dom.createDocument(null, "foo", null);
        
        final Element root = doc.getDocumentElement();
        root.appendChild(doc.createTextNode("foo"));
        assertEquals("foo", XMLUtils.getStringValue(root));
        root.appendChild(doc.createTextNode(" "));
        final Element nested = doc.createElement("ph");
        nested.appendChild(doc.createTextNode("nested"));
        root.appendChild(nested);
        root.appendChild(doc.createTextNode(" bar"));
        assertEquals("foo nested bar", XMLUtils.getStringValue(root));
    }
    
    @Test
    public void testAttributesBuilder() {
        final XMLUtils.AttributesBuilder b = new XMLUtils.AttributesBuilder(); 
        assertEquals(0, b.build().getLength());
        
        b.add("foo", "bar");
        b.add("uri", "foo", "prefix:foo", "CDATA", "qux");
        final Attributes a = b.build();
        assertEquals("bar", a.getValue("foo"));
        assertEquals("qux", a.getValue("prefix:foo"));
        assertEquals(2, a.getLength());
        
        b.add("foo", "quxx");
        final Attributes aa = b.build();
        assertEquals("quxx", aa.getValue("foo"));
        assertEquals(2, aa.getLength());
        
        final AttributesImpl ai = new AttributesImpl();
        ai.addAttribute(NULL_NS_URI, "baz", "baz", "CDATA", "all");
        b.addAll(ai);
        final Attributes aaa = b.build();
        assertEquals("all", aaa.getValue("baz"));
        assertEquals(3, aaa.getLength());
    }

}
