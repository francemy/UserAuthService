package ao.okayula.forum;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import ao.okayula.forum.security.SecurityConfig;

@SpringBootTest
@Import(SecurityConfig.class)
class ForumApplicationTests {

	@Test
	void contextLoads() {
	}

}
