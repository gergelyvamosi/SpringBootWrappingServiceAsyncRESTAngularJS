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

	public Wrapping getWrapping(long workId, String sessionId) {
		return results.get(sessionId + workId);
	}

	public Wrapping wrapText(Wrapping wrapping, String sessionId) throws InterruptedException, ExecutionException {
		return executeWorkerThread(wrapping, sessionId).get();
	}

	@Async
	public CompletableFuture<Wrapping> executeWorkerThread(Wrapping wrapping, String sessionId) throws InterruptedException {
		logger.info("Wrap Async WorkId=" + wrapping.getWorkId());
		
		if (wrapping.getWorkId() == -1) {
			WorkerThread worker = new WorkerThread(wrapping, sessionId);
			
			worker.run();
			
			// wait for getting a real work ID
			/*while (worker.getWrapping().getWorkId() == -1) {
			}*/

			// Artificial delay of 1s for demonstration purposes
			//Thread.sleep(1000L);

			return CompletableFuture.completedFuture(worker.getWrapping());
		} else {
			return CompletableFuture.completedFuture(results.get(sessionId + wrapping.getWorkId()));
		}
	}

	public class WorkerThread implements Runnable {

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
			if (getWrapping().getWorkId() == -1) {

				// set workId
				getWrapping().setWorkId(threadId);
				getWrapping().setProcessed(false);
				results.put(sessionId + getWrapping().getWorkId(), getWrapping());

				// wrapping
				wrapTextGivenLength(getWrapping());
				results.put(sessionId + getWrapping().getWorkId(), getWrapping());

				// processed true
				getWrapping().setProcessed(true);
				results.put(sessionId + getWrapping().getWorkId(), getWrapping());
			}
		}
	}

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
