package cn.edu.sdu.db.instamesg;

import cn.xuyanwu.spring.file.storage.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFileStorage
@SpringBootApplication
public class InstaMesgApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstaMesgApplication.class, args);
	}

}
