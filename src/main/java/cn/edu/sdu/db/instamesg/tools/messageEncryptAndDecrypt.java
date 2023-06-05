package cn.edu.sdu.db.instamesg.tools;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class messageEncryptAndDecrypt {
    public synchronized static String encryptMessage(String message) throws UnsupportedEncodingException {
        message = java.net.URLEncoder.encode(message, StandardCharsets.UTF_8);
        for(int i = 1;i <= 10;i++) {
            message = Base64.getMimeEncoder().encodeToString(message.getBytes());
            int len = message.length();
            String left = message.substring(0, len / 2), right = message.substring(len / 2);
            message = new StringBuilder(left).reverse().toString() + new StringBuilder(right).reverse().toString();
        }
        return message;
    }

    public synchronized static String decryptMessage(String message) throws UnsupportedEncodingException {
        for(int i = 1;i <= 10;i++) {
            int len = message.length();
            String left = message.substring(0, len / 2), right = message.substring(len / 2);
            message = new StringBuilder(left).reverse().toString() + new StringBuilder(right).reverse().toString();
            message = new String(Base64.getMimeDecoder().decode(message));
        }
        return java.net.URLDecoder.decode(message, StandardCharsets.UTF_8);
    }
}
