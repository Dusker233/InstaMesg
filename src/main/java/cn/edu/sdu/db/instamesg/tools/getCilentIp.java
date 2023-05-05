package cn.edu.sdu.db.instamesg.tools;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class getCilentIp {
    public static synchronized String getIP() {
        HttpServletRequest request = null;
        try {
            request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (Exception e) {
            e.printStackTrace();
            return "Can't get IP";
        }
        return request.getRemoteAddr();
    }
}
