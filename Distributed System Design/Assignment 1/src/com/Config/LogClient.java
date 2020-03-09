package com.Config;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogClient {
	public Handler consoleHandler = null;
	public Handler fileHandler = null;
	public Logger logger;
	
	public LogClient(String ManagerID) {
		logger = Logger.getLogger(ManagerID);
		try {
			consoleHandler = new ConsoleHandler();
			fileHandler = new FileHandler(ManagerID + ".log");//creating the client log file with managerID
			//configuring the logger
			logger.addHandler(consoleHandler);
			logger.addHandler(fileHandler);
			consoleHandler.setLevel(Level.ALL);
			fileHandler.setLevel(Level.ALL);
			logger.setLevel(Level.ALL);
			logger.config("Logger configuration done!");
			
		} catch (Exception e) {
			logger.log(Level.SEVERE,"Exception in logger :: "+e.getMessage());
		}
	}
}
