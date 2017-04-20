package cz.vut.sf.algorithms;

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
			jTextArea.append("   " + event.getMessage().toString());
			jTextArea.append("\n");
		}

		
	}
}
