package org.gvamosi.wrapping;

import java.util.concurrent.CompletableFuture;

import org.gvamosi.wrapping.model.Wrapping;
import org.gvamosi.wrapping.service.WrappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);
	
	private final WrappingService wrappingService;
	
	public AppRunner(WrappingService wrappingService) {
		this.wrappingService = new WrappingService();
		
	}
	
	@Override
	public void run(String... args) throws Exception {
		
		// Start the clock
	    long start = System.currentTimeMillis();
	    
		// Kick of multiple, asynchronous tasks
		Wrapping wrapping1 = new Wrapping();
		wrapping1.setTextToWrap("Szilva Szilva szilva");
		CompletableFuture<Wrapping> wrapped1 = wrappingService.getWrapping(wrapping1, "1");
		
		Wrapping wrapping2 = new Wrapping();
		wrapping2.setTextToWrap("Meggy Meggy meggy");
		CompletableFuture<Wrapping> wrapped2 = wrappingService.getWrapping(wrapping2, "2");
		
		Wrapping wrapping3 = new Wrapping();
		wrapping3.setTextToWrap("Barack Barack barack");
		CompletableFuture<Wrapping> wrapped3 = wrappingService.getWrapping(wrapping3, "3");
		
		// Wait until they are all done
	    CompletableFuture.allOf(wrapped1, wrapped2, wrapped3).join();

	    // Print results, including elapsed time
	    logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
	    logger.info("--> " + wrapped1.get());
	    logger.info("--> " + wrapped2.get());
	    logger.info("--> " + wrapped3.get());

	}

}
