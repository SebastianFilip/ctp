package cz.vut.sf.algorithms;

import java.awt.Point;
import java.util.List;

import org.apache.log4j.Logger;

import cz.vut.sf.parsers.BasicCtpParser;

public abstract class LoggerClass {
    protected static final Logger LOG = Logger.getLogger(LoggerClass.class);
    protected static List<Point> poitList = new BasicCtpParser().parseFile("src/main/resources/eyerichx2.ctp").pointList;
}
