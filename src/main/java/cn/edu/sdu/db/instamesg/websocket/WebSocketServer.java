package cn.edu.sdu.db.instamesg.websocket;

import cn.edu.sdu.db.instamesg.tools.messageEncryptAndDecrypt;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/imserver/{userid}")
@Component
public class WebSocketServer {
    public static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userid") String userid) {
        sessionMap.put(userid, session);
        System.out.println("new connection: " + userid);
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        result.set("users", array);
        for(Object key: sessionMap.keySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("userid", key);
            array.add(jsonObject);
        }
        sendAllMessage(JSONUtil.toJsonStr(result));
    }

    @OnClose
    public void onClose(Session session, @PathParam("userid") String userid) {
        sessionMap.remove(userid);
        System.out.println("close connection: " + userid);
    }

    @OnMessage
    public void onMessage(String content, Session session, @PathParam("userid") String userid) throws UnsupportedEncodingException {
        JSONObject object = JSONUtil.parseObj(content);
        String to = object.getStr("to"); // 发送给谁
        String text = object.getStr("text"); // 消息文本
        String code = object.getStr("code"); // 消息类型
        if(code.equals("heartbeat")) {
            System.out.println("heartbeat: " + userid + " " + text);
            JSONObject result = new JSONObject();
            JSONArray array = new JSONArray();
            result.set("users", array);
            for(Object key: sessionMap.keySet()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.set("userid", key);
                array.add(jsonObject);
            }
            sendAllMessage(JSONUtil.toJsonStr(result));
            return;
        }
        Session toSession = sessionMap.get(to);
        if(toSession != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("from", userid);
            jsonObject.set("text", messageEncryptAndDecrypt.encryptMessage(text));
            this.sendMessage(jsonObject.toString(), toSession);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("error");
        error.printStackTrace();
    }

    private void sendMessage(String message, Session toSession) {
        try {
            JSONObject jsonObject = JSONUtil.parseObj(message);
            String text = jsonObject.getStr("text");
            text = messageEncryptAndDecrypt.decryptMessage(text);
            jsonObject.set("text", text);
            toSession.getBasicRemote().sendText(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendAllMessage(String message) {
        try {
            for(Session session: sessionMap.values())
                session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
