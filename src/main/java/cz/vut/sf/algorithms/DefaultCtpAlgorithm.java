package cz.vut.sf.algorithms;
import cz.vut.sf.ctp.*;
import cz.vut.sf.gui.LoggerClass;

public abstract class DefaultCtpAlgorithm extends LoggerClass{
	protected final DefaultCtp ctp;
	protected final Agent agent;
	public abstract Result solve();
	public DefaultCtpAlgorithm(DefaultCtp ctp, Agent agent){
		this.ctp = ctp;
		this.agent = agent;
	}
}
