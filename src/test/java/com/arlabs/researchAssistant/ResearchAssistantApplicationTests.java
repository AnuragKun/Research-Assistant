package com.arlabs.researchAssistant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "GEMINI_KEY=dummy-test-key")
class ResearchAssistantApplicationTests {

	@Test
	void contextLoads() {
	}

}
