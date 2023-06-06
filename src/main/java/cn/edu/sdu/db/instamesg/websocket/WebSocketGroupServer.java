package cn.edu.sdu.db.instamesg.websocket;

import cn.edu.sdu.db.instamesg.tools.messageEncryptAndDecrypt;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/imserver/group/{groupid}/{userid}")
@Component
public class WebSocketGroupServer {
    // <<groupId, userId>, session>
    public static final Map<Pair<String, String>, Session> sessionMap = new ConcurrentHashMap<>();

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

    private void heartbeat(String groupid, String userid) {
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        result.set("onlineMemberNumber", array);
        Map<String, Integer> onlineNumber = new HashMap<>();
        for(Pair<String, String> key: sessionMap.keySet()) {
            if(key.getFirst().equals(groupid)) {
                if(onlineNumber.containsKey(groupid))
                    onlineNumber.put(groupid, onlineNumber.get(groupid) + 1);
                else
                    onlineNumber.put(groupid, 1);
            }
        }
        for(String key: onlineNumber.keySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("groupid", key);
            jsonObject.set("onlineNumber", onlineNumber.get(key));
            array.add(jsonObject);
        }
        sendAllMessage(JSONUtil.toJsonStr(result));
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("groupid") String groupid, @PathParam("userid") String userid) {
        sessionMap.put(Pair.of(groupid, userid), session);
        System.out.println("new connection: " + groupid + " " + userid);
        this.heartbeat(groupid, userid);
    }

    @OnClose
    public void onClose(Session session, @PathParam("groupid") String groupid, @PathParam("userid") String userid) {
        sessionMap.remove(Pair.of(groupid, userid));
        System.out.println("close connection: " + groupid + " " + userid);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("error");
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(Session session, String content) throws UnsupportedEncodingException {
        JSONObject object = JSONUtil.parseObj(content);
        String groupid = object.getStr("groupid");
        String userid = object.getStr("userid");
        String text = object.getStr("text");
        String code = object.getStr("code");
        if(code.equals("heartbeat")) {
            this.heartbeat(groupid, userid);
            return;
        }
        for(Pair<String, String> key: sessionMap.keySet()) {
            if(key.getFirst().equals(groupid) && !key.getSecond().equals(userid)) {
                Session toSession = sessionMap.get(key);
                JSONObject jsonObject = new JSONObject();
                jsonObject.set("groupid", groupid);
                jsonObject.set("userid", userid);
                jsonObject.set("text", messageEncryptAndDecrypt.encryptMessage(text));
                this.sendMessage(jsonObject.toString(), toSession);
            }
        }
    }
}
