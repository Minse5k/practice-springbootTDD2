package com.back;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // application-test.yml 적용 → 메모리 DB 사용
class BackApplicationTests {

	@Test
	void contextLoads() {
	}

}
