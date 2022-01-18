package org.gvamosi.wrapping.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import org.gvamosi.wrapping.model.Wrapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("wrappingService")
public class WrappingService {

	private static final Logger logger = LoggerFactory.getLogger(WrappingService.class);

	private Map<String, Wrapping> results = new ConcurrentHashMap<String, Wrapping>();

	@Async
	public CompletableFuture<Wrapping> getWrapping(Wrapping wrapping, String sessionId) {
		logger.info("Wrap Async WorkId=" + wrapping.getWorkId());

		if (wrapping.getWorkId() == -1) {
			WorkerThread worker = new WorkerThread(wrapping, sessionId);

			worker.run();

			// wait for getting a real work ID
			/*
			 * while (worker.getWrapping().getWorkId() == -1) { }
			 */

			// Artificial delay of 1s for demonstration purposes
			// Thread.sleep(1000L);
		}
		return CompletableFuture.completedFuture(results.get(sessionId + wrapping.getWorkId()));
	}

	/*
	 * The worker thread.
	 */
	private class WorkerThread implements Runnable {

		private Wrapping wrapping;
		private String sessionId;

		public WorkerThread(Wrapping wrapping, String sessionId) {
			this.wrapping = wrapping;
			this.sessionId = sessionId;
		}

		public Wrapping getWrapping() {
			return wrapping;
		}

		@Override
		public void run() {
			long threadId = Thread.currentThread().getId();
			if (wrapping.getWorkId() == -1) {

				// set workId
				wrapping.setWorkId(threadId);
				wrapping.setProcessed(false);
				results.put(sessionId + wrapping.getWorkId(), wrapping);

				// wrapping
				wrapTextGivenLength(getWrapping());
				results.put(sessionId + wrapping.getWorkId(), wrapping);

				// processed true
				getWrapping().setProcessed(true);
				results.put(sessionId + wrapping.getWorkId(), wrapping);
			}
		}
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
