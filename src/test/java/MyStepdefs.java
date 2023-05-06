import cn.edu.sdu.db.instamesg.controller.UserController;
import io.cucumber.java.en.Given;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import jakarta.servlet.http.HttpSession;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources")
public class MyStepdefs {
    @Given("用户名为{string}，密码为{string}，邮箱为{string}")
    public void 用户名为密码为邮箱为(String arg0, String arg1, String arg2) {
        HttpSession session = null;
        UserController userController = new UserController();
        try {
            userController.register(arg0, arg1, arg2, null, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
