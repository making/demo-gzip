package com.example.gzip;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping(path = "/")
	public String hello() {
		return "Hell" + ("o".repeat(1024)) + "!";
	}

	@PostMapping(path = "/")
	public JsonNode hello(@RequestBody JsonNode body) {
		return body;
	}

}
