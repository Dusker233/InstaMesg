package cn.edu.sdu.db.instamesg.tools;

import java.util.Base64;

public class messageEncryptAndDecrypt {
    public synchronized static String encryptMessage(String message) {
        for(int i = 1;i <= 10;i++) {
            message = Base64.getEncoder().encodeToString(message.getBytes());
            int len = message.length();
            String left = message.substring(0, len / 2), right = message.substring(len / 2);
            message = new StringBuilder(left).reverse().toString() + new StringBuilder(right).reverse().toString();
        }
        return message;
    }

    public synchronized static String decryptMessage(String message) {
        for(int i = 1;i <= 10;i++) {
            int len = message.length();
            String left = message.substring(0, len / 2), right = message.substring(len / 2);
            message = new StringBuilder(left).reverse().toString() + new StringBuilder(right).reverse().toString();
            message = new String(Base64.getDecoder().decode(message));
        }
        return message;
    }
}
