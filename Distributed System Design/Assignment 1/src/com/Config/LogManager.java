package com.Config;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogManager {
	
	public Handler fileHandler = null;
	public Logger logger = null;
	
	public LogManager(String serverName) {
		logger = Logger.getLogger(serverName);
		
		try {
			
			fileHandler = new FileHandler(com.Config.Constants.LOG_DIR+serverName+"\\"+serverName+".log",true);
			SimpleFormatter simpleFormatter = new SimpleFormatter();
			fileHandler.setFormatter(simpleFormatter);
			// set and config Logger
			logger.setUseParentHandlers(false);
			logger.addHandler(fileHandler);
			logger.setLevel(Level.INFO);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE,"ServerMessage "+e.getMessage());
			e.printStackTrace();
		} 
		
	}
	
}
