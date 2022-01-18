package org.gvamosi.wrapping;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.gvamosi.wrapping.model.Wrapping;
import org.gvamosi.wrapping.service.WrappingService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

public class WrappingServiceTest {
	
	@Autowired
	private WrappingService wrappingService;
	
	@Before
	public void setUp() {
		wrappingService = new WrappingService();
	}
	
	@Test
	public void testWrapText() throws InterruptedException, ExecutionException {
		String sessionId = "ABC001";
		Wrapping wrapping = new Wrapping();
		wrapping.setWorkId(-1);
		wrapping.setTextToWrap("This is a test sentence to smoke-test line breaking.");
		wrapping = wrappingService.getWrapping(wrapping, sessionId).get();
		Assert.assertNotEquals("Wrapping processed", wrapping.getWorkId(), -1);
		Assert.assertArrayEquals("Wrapped text", wrapping.getWrappedText().toArray(),
				new String[] { "This is a ", "test ", "sentence ", "to ", "smoke-test", "line ", "breaking." });
	}

}
