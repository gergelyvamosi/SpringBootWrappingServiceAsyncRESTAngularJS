package org.gvamosi.wrapping.controller;

import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import org.gvamosi.wrapping.model.Wrapping;
import org.gvamosi.wrapping.service.WrappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.SessionScope;

@RestController
@SessionScope
public class WrappingController {

	private static final Logger logger = LoggerFactory.getLogger(WrappingController.class);
	
	@Autowired
	WrappingService wrappingService;

	@PostMapping(path = "/api/LineBreak", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Wrapping> wrapText(@RequestBody Wrapping wrapping, HttpServletRequest request) {
		wrapping.setProcessed(false);
		return getWrapping(wrapping, request.getSession(false).getId());
	}

	@GetMapping(path = "/api/LineBreak/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Wrapping> getWrappingByWorkId(@PathVariable int id, HttpServletRequest request) {
		Wrapping dummy = new Wrapping();
		dummy.setWorkId(id);
		return getWrapping(dummy, request.getSession(false).getId());
	}
	
	/*
	 * To be pragmatic. :)
	 */
	private ResponseEntity<Wrapping> getWrapping(Wrapping wrapping, String sessionId)  {
		Wrapping result = null;
		try {
			result = wrappingService.getWrapping(wrapping, sessionId).get();
		} catch (InterruptedException | ExecutionException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if (result == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} else if (result.isProcessed()) {
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
		}
	}

}
