package by.bsu.webchat.storage.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;

import by.bsu.webchat.model.Message;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class XMLHistoryUtil {
    private static final String STORAGE_LOCATION = "D:\\history.xml";
    private static final String MESSAGES = "messages";
    private static final String MESSAGE = "message";
    private static final String ID = "id";
    private static final String USER = "user";
    private static final String TEXT = "text";
    private static final String DATE = "date";
    private static final String DELETE = "delete";

    public static synchronized void createStorage() throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement(MESSAGES);
        doc.appendChild(rootElement);

        Transformer transformer = getTransformer();

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
        transformer.transform(source, result);
    }
    private static Transformer getTransformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        // Formatting XML properly
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return transformer;
    }
    public static synchronized void addData(Message message) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement();

        Element messElement = document.createElement(MESSAGE);
        root.appendChild(messElement);

        messElement.setAttribute(ID, message.getId());

        Element text = document.createElement(TEXT);
        text.appendChild(document.createTextNode(message.getText()));
        messElement.appendChild(text);

        Element date = document.createElement(DATE);
        date.appendChild(document.createTextNode(message.getDate()));
        messElement.appendChild(date);

        Element user = document.createElement(USER);
        user.appendChild(document.createTextNode(message.getUser()));
        messElement.appendChild(user);

        Element delete = document.createElement(DELETE);
        delete.appendChild(document.createTextNode(message.isDelete()));
        messElement.appendChild(delete);


        DOMSource source = new DOMSource(document);

        Transformer transformer = getTransformer();

        StreamResult result = new StreamResult(STORAGE_LOCATION);
        transformer.transform(source, result);
    }

    public static synchronized void updateData(Message message) throws ParserConfigurationException, SAXException, IOException, TransformerException, XPathExpressionException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Node mesToUpdate = getNodeById(document, message.getId());

        if (mesToUpdate != null) {

            NodeList childNodes = mesToUpdate.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {

                Node node = childNodes.item(i);

                if (TEXT.equals(node.getNodeName())) {
                    node.setTextContent(message.getText());
                }

                if (USER.equals(node.getNodeName())) {
                    node.setTextContent(message.getUser());
                }

                if (DATE.equals(node.getNodeName())) {
                    node.setTextContent(message.getDate());
                }

                if (DELETE.equals(node.getNodeName())) {
                    node.setTextContent(message.isDelete());
                }

            }

            Transformer transformer = getTransformer();

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
            transformer.transform(source, result);
        } else {
            throw new NullPointerException();
        }
    }

    private static Node getNodeById(Document document, String id) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression xPathExpression = xPath.compile("//" + MESSAGE + "[@id='" + id + "']");
        return (Node) xPathExpression.evaluate(document, XPathConstants.NODE);
    }

    public static synchronized boolean doesStorageExist() {
        File file = new File(STORAGE_LOCATION);
        return file.exists();
    }

    public static synchronized List<Message> getMessages() throws SAXException, IOException, ParserConfigurationException {
        return getSubMessagesByIndex(0); // Return all tasks from history
    }

    public static synchronized List<Message> getSubMessagesByIndex(int index) throws ParserConfigurationException, SAXException, IOException {
        List<Message> messages = new ArrayList<Message>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement(); // Root <tasks> element
        NodeList mesList = root.getElementsByTagName(MESSAGE);
        for (int i = index; i < mesList.getLength(); i++) {
            Element mesElement = (Element) mesList.item(i);
            String id = mesElement.getAttribute(ID);
            String text = mesElement.getElementsByTagName(TEXT).item(0).getTextContent();
            String user = mesElement.getElementsByTagName(USER).item(0).getTextContent();
            String date = mesElement.getElementsByTagName(DATE).item(0).getTextContent();
            String delete = mesElement.getElementsByTagName(DELETE).item(0).getTextContent();
            messages.add(new Message(id, user, text, date, delete));
        }
        return messages;
    }



    public static synchronized int getStorageSize() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();
        return root.getElementsByTagName(MESSAGE).getLength();
    }


}
