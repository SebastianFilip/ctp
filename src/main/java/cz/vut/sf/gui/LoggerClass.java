package cz.vut.sf.gui;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextArea;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;


public abstract class LoggerClass {
    protected static final Logger LOG = Logger.getLogger(LoggerClass.class);
    
	public class StatusMessageAppender extends org.apache.log4j.AppenderSkeleton {
	    private final JTextArea jTextArea;

	    public StatusMessageAppender(JTextArea jTextArea){
	    	this.jTextArea = jTextArea;
	    }
	    
		public void close() {
		}

		public boolean requiresLayout() {
			return false;
		}

		@Override
		protected void append(LoggingEvent event) {
			Date time = new Date(event.getTimeStamp());
			String timeStr = (new SimpleDateFormat("HH:mm:ss")).format(time);
			if(event.getLevel() == Level.INFO){
				jTextArea.append(" ["  + timeStr + "] INFO  " + event.getMessage().toString());
			}else{
				jTextArea.append(" ["  + timeStr + "] DEBUG " + event.getMessage().toString());
			}
			jTextArea.append("\n");
		}

		
	}
}
