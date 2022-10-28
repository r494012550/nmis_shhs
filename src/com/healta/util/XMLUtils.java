/*
 *  $Id: XMLUtils.java 116 2011-07-08 20:51:14Z scott.herman@unconxio.us $
 *
 *  This code is derived from public domain sources. Commercial use is allowed.
 *  However, all rights remain permanently assigned to the public domain.
 *
 *  XMLUtils.java : A class of static XML utility methods.
 *
 *  Copyright (C) 2009, 2010  Scott Herman
 *
 *  This is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This code is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this code.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.healta.util;

import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



/**
 * Some static XML handling utilities and tools.
 * @author scott
 */
public class XMLUtils {
   public static final boolean NO_HEADER = false;
   /**
    * Reads the argument XML input stream.
    * @param inStream An input stream carrying the only subject XML
    * @return The root node of the read DOM document.
    */
   public static Node readXML(InputStream inStream) {
      try {
         DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = builderFactory.newDocumentBuilder();

         Document doc = builder.parse(inStream);
         inStream.close();
         return doc.getDocumentElement();
      } catch (SAXException saxEx) {
         throw new IllegalArgumentException(null,  saxEx);
      } catch (IOException ioEx) {
         throw new IllegalArgumentException(null,  ioEx);
      } catch (ParserConfigurationException parsEx) {
         throw new IllegalArgumentException(null,  parsEx);
      } // try - catch
   } // readXML


   /**
    * Reads the argument XML input String.
    * @param xmlString The XML input String to be parsed.
    * @return The root node of the read DOM document.
    */
   public static Node readXML(String xmlString) {
      try {
         DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = builderFactory.newDocumentBuilder();

         Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
         return doc.getDocumentElement();
      } catch (SAXException saxEx) {
         throw new IllegalArgumentException("readXML() Caught SAXException: ",  saxEx);
      } catch (IOException ioEx) {
         throw new IllegalArgumentException("readXML() Caught IOException: "
                                          + ioEx.getMessage(),  ioEx);
      } catch (ParserConfigurationException parsEx) {
         throw new IllegalArgumentException("readXML() Caught ParserConfigurationException: ",  parsEx);
      } // try - catch
   } // readXML


   public static Node readXML(URI uri) {
      try {
         DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = builderFactory.newDocumentBuilder();

         Document doc = builder.parse(uri.toString());
         return doc.getDocumentElement();
      } catch (SAXException saxEx) {
         throw new IllegalArgumentException("readXML() Caught SAXException: ",  saxEx);
      } catch (IOException ioEx) {
         throw new IllegalArgumentException("readXML() Caught IOException: "
                                          + ioEx.getMessage(),  ioEx);
      } catch (ParserConfigurationException parsEx) {
         throw new IllegalArgumentException("readXML() Caught ParserConfigurationException: ",  parsEx);
      } // try - catch
   } // readXML


   /**
    * Returns a String representation of the DOM hierarchy rooted at the argument Node.
    * @param node The root Node of the DOM hierarchy to translate.
    * @return A String representation of the DOM hierarchy rooted at the
    * argument Node, or null if the operation fails.
    */
   public static String toXMLString(Node node, boolean header) {
      try {
         Source source = new DOMSource(node);
         StringWriter stringWriter = new StringWriter();
         Result result = new StreamResult(stringWriter);
         TransformerFactory factory = TransformerFactory.newInstance();
         Transformer transformer = factory.newTransformer();
         if (!header) transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
         transformer.transform(source, result);
         return stringWriter.getBuffer().toString();
      } catch (TransformerConfigurationException e) {
         e.printStackTrace();
      } catch (TransformerException e) {
         e.printStackTrace();
      } // try - catch

      return null;
   } // toXMLString


   public static String toXMLString(Node node) {
      return toXMLString(node, true);
   } // toXMLString


   /**
    * Creates and returns a XML element as a String.
    * @param tagName  The name of the element.
    * @param content  The text content of the element, which may be,
    *                 or include XML.
    * @return         The element as a XML String.
    */
   public static String elementString(String tagName, String content) {
      return elementString(tagName, null, content);
   } // elementString


   /**
    * Creates and returns a XML element as a String. 
    * @param tagName     The name of the element.
    * @param attributes  A Attribute map of any attributes to be included in the
    *                    XML element.
    * @param content     The text content of the element, which may be,
    *                    or include XML.
    * @return            The XML element as a String.
    */
   public static String elementString(String tagName, AttributeMap attributes, String content) {
      if (tagName == null || tagName.isEmpty()) return null;

      StringBuffer buildBuffer = new StringBuffer("<").append(tagName);

      if (attributes != null && attributes.isNotEmpty()) buildBuffer.append(attributes.toString());

      if (content == null || content.isEmpty()) {
         return buildBuffer.append(" />").toString();
      } // if

      buildBuffer.append(">").append(content);
      return buildBuffer.append("</").append(tagName).append(">").toString();
   } // elementString


   /**
    * Returns the name of the first XML tag in the argument string.
    * @param inStr The argument String to search.
    * @return The name of the first XML tag in the argument string,
    * or null if the operation fails.
    */
   public static String firstTag(String inStr) {
      int startIndex = inStr.indexOf("<");
      if (startIndex < 0) return null;

      // skip comments
      while (inStr.substring(startIndex).equals("<!--")) {
         inStr = inStr.substring(startIndex + 4);
         startIndex = inStr.indexOf("-->");
         if (startIndex < 0) return null;

         startIndex = inStr.indexOf("<");
         if (startIndex < 0) return null;
      } // while

      int tagOffset = 1;
      while (Character.isWhitespace(inStr.charAt(startIndex + tagOffset))) {
         ++tagOffset;
      } // while

      int tokenLen = 0;
      if (isStartChar(inStr.charAt(startIndex + tagOffset))) {
         ++tokenLen;
         while (isNameChar(inStr.charAt(startIndex + tagOffset + tokenLen))) {
            ++tokenLen;
         } // while
      } // if

      char endChar = inStr.charAt(startIndex + tagOffset + tokenLen);
      if (!(Character.isWhitespace(endChar) || (endChar == '>') ) ) {
         return null;
      } // if

      return inStr.substring(startIndex + tagOffset, startIndex + tagOffset + tokenLen);
   } // firstTag

   /**
    * Extracts outermost level XML from the argument String.
    * @param inStr The argument String to search.
    * @return The XML String minus any leading or trailing characters which
    * are not part of the XML, or null if the operation fails.
    */
   public static String extractXML(String inStr) {
      String elementName = firstTag(inStr);
      if (elementName == null)  return null;

      inStr = startXMLFor(elementName, inStr);
      return extractXML(elementName, inStr);
   } // extractXML

   /**
    * NameStartChar ::=	":" | [A-Z] | "_" | [a-z]
    * @param charV
    * @return
    */
   private static boolean isStartChar(char charV) {
      if (Character.isLetter(charV)) return true;
      if (charV == ':')              return true;
      if (charV == '_')              return true;
      return false;
   } // isStartChar

   /**
    * NameChar ::= NameStartChar | "-" | "." | [0-9]
    * @param charV
    * @return
    */
   private static boolean isNameChar(char charV) {
      if (isStartChar(charV))       return true;
      if (Character.isDigit(charV)) return true;
      if (charV == '-')             return true;
      if (charV == '.')             return true;
      return false;
   } // isNameChar


   /**
    * Extracts outermost level XML, that starts with the argument element, from
    * the argument String.
    * @param inStr The argument String to search.
    * @return The XML String minus any leading or trailing characters which
    * are not part of the specified XML, or null if the operation fails.
    */
   public static String extractXML(String elementName, String argStr) {
      argStr = XMLUtils.startXMLFor(elementName, argStr);
      if (argStr == null) return null;
      
      int tokenIndex = argStr.toLowerCase().indexOf(elementName.toLowerCase());
      
      int offset = tokenIndex + elementName.length();
      String remainder = argStr.substring(offset).toLowerCase();

      // look for "/>
      int endIndex = remainder.indexOf("/>");
      if (endIndex >= 0) {
         int checkIndex = remainder.indexOf("<");
         if (checkIndex < 0) {
            return argStr.substring(0, endIndex + offset + 2);
         }  // if
      } // if

      // otherwise look for the end
      endIndex = remainder.indexOf(elementName.toLowerCase());
      if (endIndex < 0)  return null;
      while (endIndex > 0) {
         endIndex += elementName.length();
         while (remainder.charAt(endIndex) != '>'
         &&     Character.isWhitespace(remainder.charAt(endIndex))) {
            ++endIndex;
         } // while

         if (remainder.charAt(endIndex) == '>') {
            return argStr.substring(0, ++endIndex + offset);
         } // if

         offset += endIndex;
         remainder = remainder.substring(endIndex);
         endIndex = remainder.indexOf(elementName.toLowerCase());
      } // while

      return null;
   } // extractXML


   private static String startXMLFor(String elementName, String argStr) {
      int tokenIndex = argStr.toLowerCase().indexOf(elementName.toLowerCase());
      if (tokenIndex < 1)   return null;

      int startIndex = tokenIndex;
      while (startIndex >= 0
      && argStr.charAt(startIndex) != '<'
      && (  Character.isWhitespace(argStr.charAt(startIndex))
         || startIndex == tokenIndex)) {
         --startIndex;
      } // while

      if (startIndex < 0 || argStr.charAt(startIndex) != '<') return null;
      if (startIndex == 0) return argStr;
      return argStr.substring(startIndex);
   } // startXMLFor


   public static boolean equalsAny(String str, String[] strs) {
      int strCount = strs.length;

      for (int index = 0; index < strCount; ++index) {
         if (str.equals(strs[index])) return true;
      } // for

      return false;
   } // equalsAny


   /**
    * Finds the node of the argument name in the tree under the argument node.
    * @param name The name of the node to search for, case insensitive.
    * @param node The root node of the tree under which to look for the named node.
    * @return the first found occurrence of the named node (breadth first),
    * or null if the node is not found.
    */
   public static Node findChild(String name, Node node) {
      if (node == null) return null;

      if (node.hasChildNodes()) {
         NodeList kids = node.getChildNodes();
         int numKids = kids.getLength();
         for (int index = 0; index < numKids; ++index) {
            Node kid = kids.item(index);
            if (kid.getNodeName().equalsIgnoreCase(name)) return kid;
         } // for

         for (int index = 0; index < numKids; ++index) {
            Node found = findChild(name, kids.item(index));
            if (found != null) return found;
         } // for
      } // if

      return null;
   } // findChild

   public static boolean isElement(Node node) {
      return node.getNodeType() == Node.ELEMENT_NODE;
   } // isElement

} // XMLUtils
