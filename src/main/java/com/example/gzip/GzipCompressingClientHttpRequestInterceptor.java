package com.example.gzip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class GzipCompressingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		List<String> contentEncoding = request.getHeaders().get(HttpHeaders.CONTENT_ENCODING);
		if (contentEncoding != null && contentEncoding.contains("gzip") && !isGzipCompressed(body)) {
			byte[] compressed = compress(body);
			request.getHeaders().setContentLength(compressed.length);
			return execution.execute(request, compressed);
		}
		return execution.execute(request, body);
	}

	static byte[] compress(byte[] body) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos)) {
			gzipOutputStream.write(body);
		}
		return baos.toByteArray();
	}

	public static boolean isGzipCompressed(byte[] data) {
		if (data == null || data.length < 2) {
			return false;
		}
		int header = ((int) data[0] & 0xff) | ((data[1] << 8) & 0xff00);
		return header == GZIPInputStream.GZIP_MAGIC;
	}

}
