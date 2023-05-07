package cn.edu.sdu.db.instamesg.tools;

import com.amdelamar.jotp.OTP;
import com.amdelamar.jotp.type.Type;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class get2FAUrl {
    public static synchronized String get2FAUrl(String Secret, String username) {
        String otpUrl = OTP.getURL(Secret, 6, Type.TOTP, "InstaMesg-2FA", username);
        String twoFaQrUrl = String.format(
                "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=%s",
                URLEncoder.encode(otpUrl, StandardCharsets.UTF_8));
        return twoFaQrUrl;
    }
}
