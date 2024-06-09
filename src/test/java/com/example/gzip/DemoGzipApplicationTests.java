package com.example.gzip;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = "logging.level.am.ik.spring.http=debug")
class DemoGzipApplicationTests {

	@Autowired
	RestClient.Builder restClientBuilder;

	@Autowired
	LogbookClientHttpRequestInterceptor logbookClientHttpRequestInterceptor;

	RestClient restClient;

	@LocalServerPort
	int port;

	protected BasicJsonTester json = new BasicJsonTester(getClass());

	@BeforeEach
	void init() {
		if (this.restClient == null) {
			this.restClient = this.restClientBuilder.baseUrl("http://localhost:" + this.port)
				.requestInterceptor(new GzipCompressingClientHttpRequestInterceptor())
				.requestInterceptor(logbookClientHttpRequestInterceptor)
				.build();
		}
	}

	@Test
	void postGzipEncodedContent() throws Exception {
		ResponseEntity<String> response = this.restClient.post()
			.uri("/")
			.header(HttpHeaders.CONTENT_ENCODING, "gzip")
			.contentType(MediaType.APPLICATION_JSON)
			.body(new ClassPathResource("request.json"))
			.retrieve()
			.toEntity(String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonContent<Object> content = this.json.from(response.getBody());
		assertThat(content).extractingJsonPathStringValue("$.text").isEqualTo("Hell" + ("o".repeat(1024)) + "!");
	}

	@Test
	void postGzipEncodedContent_alreadyCompressed() throws Exception {
		ResponseEntity<String> response = this.restClient.post()
			.uri("/")
			.header(HttpHeaders.CONTENT_ENCODING, "gzip")
			.contentType(MediaType.APPLICATION_JSON)
			.body(new ClassPathResource("request.json.gz"))
			.retrieve()
			.toEntity(String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonContent<Object> content = this.json.from(response.getBody());
		assertThat(content).extractingJsonPathStringValue("$.text").isEqualTo("Hell" + ("o".repeat(1024)) + "!");
	}

	@Test
	void postPlainContent() throws Exception {
		ResponseEntity<String> response = this.restClient.post()
			.uri("/")
			.contentType(MediaType.APPLICATION_JSON)
			.body(new ClassPathResource("request.json"))
			.retrieve()
			.toEntity(String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonContent<Object> content = this.json.from(response.getBody());
		assertThat(content).extractingJsonPathStringValue("$.text").isEqualTo("Hell" + ("o".repeat(1024)) + "!");
	}

}
