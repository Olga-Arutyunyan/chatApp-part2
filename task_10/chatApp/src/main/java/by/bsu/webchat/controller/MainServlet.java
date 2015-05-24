package by.bsu.webchat.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import by.bsu.webchat.util.MessageUtil;
import by.bsu.webchat.util.ServletUtil;
import by.bsu.webchat.model.Message;
import by.bsu.webchat.storage.xml.XMLHistoryUtil;
import static by.bsu.webchat.util.MessageUtil.stringToJson;
import static by.bsu.webchat.util.MessageUtil.jsonToMessage;
import static by.bsu.webchat.util.MessageUtil.TOKEN;
import static by.bsu.webchat.util.MessageUtil.MESSAGES;
import static by.bsu.webchat.util.MessageUtil.getIndex;
import static by.bsu.webchat.util.MessageUtil.getToken;
import static  by.bsu.webchat.storage.xml.XMLHistoryUtil.getMessages;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;


import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.json.simple.JSONObject;

import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

@WebServlet("/chat")
public class MainServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(MainServlet.class);

    @Override
    public void init() throws ServletException {
        try {
            loadHistory();
            for (Message mes : getMessages()){
                logger.info(mes.getDate() + " " + mes.getUser() +  " : " + mes.getText());
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //BasicConfigurator.configure();
        logger.info("doPost");
        String data = ServletUtil.getMessageBody(request);

        try {
            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
           
            XMLHistoryUtil.addData(message);
            logger.info(message.getUser() + ":" + message.getText());
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (Exception e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String token = request.getParameter(TOKEN);


        try {
            if (token != null && !"".equals(token)) {
                int index = getIndex(token);

                String messages;
                messages = formResponse(index);

                response.setCharacterEncoding(ServletUtil.UTF_8);
                response.setContentType(ServletUtil.APPLICATION_JSON);
                PrintWriter out = response.getWriter();
                out.print(messages);
                out.flush();
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' parameter needed");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    private String formResponse(int index) throws SAXException, IOException, ParserConfigurationException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MESSAGES, XMLHistoryUtil.getSubMessagesByIndex(index));
        jsonObject.put(TOKEN, getToken(XMLHistoryUtil.getStorageSize()));
        return jsonObject.toJSONString();
    }

    private void loadHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException {
        if (!XMLHistoryUtil.doesStorageExist()) {
            XMLHistoryUtil.createStorage();
            addStubData();
        }
    }
    private void addStubData() throws ParserConfigurationException, TransformerException {
        Message[] stubMessages = {
                new Message("1", "Sam Baker", "Hello! What is your name?","1"),
        };
        for (Message message : stubMessages) {
            try {
                XMLHistoryUtil.addData(message);
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }
}


