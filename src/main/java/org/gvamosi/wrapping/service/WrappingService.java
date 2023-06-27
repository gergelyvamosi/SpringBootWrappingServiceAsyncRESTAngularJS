package org.gvamosi.wrapping.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.gvamosi.wrapping.model.Wrapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("wrappingService")
public class WrappingService {

	private static final Logger logger = LoggerFactory.getLogger(WrappingService.class);

	@Async("taskExecutor")
	public CompletableFuture<Wrapping> getWrapping(Wrapping wrapping, String sessionId) {
		
		wrapping.setWorkId(Thread.currentThread().getId());
		
		logger.info("Wrap text: " + wrapping.getTextToWrap() + "(" + wrapping.getWorkId() + ")");
		
		wrapTextGivenLength(wrapping);
		wrapping.setProcessed(true);
		return CompletableFuture.completedFuture(wrapping);
	}

	/*
	 * The core wrapper method.
	 */
	private void wrapTextGivenLength(Wrapping wrapping) {
		String splitted[] = wrapping.getTextToWrap().split("\\s+");
		for (int i = 0; i < splitted.length; i++) {
			StringBuilder sb = new StringBuilder();
			int j = 0;
			do {
				sb.append(splitted[i + j]);
				j++;
				if (i + j < splitted.length && sb.length() < wrapping.getWrapLength()) {
					sb.append(" ");
				}
			} while (i + j < splitted.length && sb.length() + splitted[i + j].length() <= wrapping.getWrapLength());
			i += j - 1;
			wrapping.getWrappedText().add(sb.toString());
		}
	}
}
