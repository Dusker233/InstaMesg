package cn.edu.sdu.db.instamesg;

import cn.edu.sdu.db.instamesg.controller.FriendController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import cn.edu.sdu.db.instamesg.tools.getClientIp;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class InstaMesgApplicationTests {

	private MockHttpServletRequest request;


	@Test
	void testNonIP() {
		request = null;
//		request.setCharacterEncoding("UTF-8");
//		MockHttpServletRequest request = new MockHttpServletRequest();
		String ip = getClientIp.getIP(request);
		assertEquals("Can't get IP", ip);
	}

	@Test
	void testIP() {
		request = new MockHttpServletRequest();
		request.setCharacterEncoding("UTF-8");
		String testIp = "192.168.31.96";
		String ip = getClientIp.getIP(request);
		assertEquals(testIp, ip);
	}
}
