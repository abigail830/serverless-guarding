package com.serverless;

import java.util.Collections;
import java.util.Map;

public class Response {

	private final String message;
	private final Map<String, Object> result;

	public Response(String message) {
		this.message = message;
		result = Collections.emptyMap();
	}

	public Response(String message, Map<String, Object> input) {
		this.message = message;
		this.result = input;
	}

	public String getMessage() {
		return this.message;
	}

	public Map<String, Object> getResult() {
		return this.result;
	}
}
