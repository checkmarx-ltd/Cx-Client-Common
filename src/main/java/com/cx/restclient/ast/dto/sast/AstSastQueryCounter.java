package com.cx.restclient.ast.dto.sast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AstSastQueryCounter implements Serializable{
	private Map queryHighMap = new HashMap<>();
	private Map queryMediumMap = new HashMap<>();
	private Map queryLowMap = new HashMap<>();
	private Map queryInfoMap = new HashMap<>();
	private int highCounter = 0;
	private int mediumCounter = 0;
	private int lowCounter = 0;
	private int infoCounter = 0;

	public int getHighCounter(String queryName) {
		return (int) queryHighMap.get(queryName);
	}

	public int getMediumCounter(String queryName) {
		return (int) queryMediumMap.get(queryName);
	}

	public int getLowCounter(String queryName) {
		return (int) queryLowMap.get(queryName);
	}

	public int getInfoCounter(String queryName) {
		return (int) queryInfoMap.get(queryName);
	}

	public void incrementHighCounter(String queryName) {
		queryHighMap.put(queryName, highCounter++);
	}

	public void incrementMediumCounter(String queryName) {
		queryMediumMap.put(queryName, mediumCounter++);
	}

	public void incrementLowCounter(String queryName) {
		queryLowMap.put(queryName, lowCounter++);
	}

	public void incrementInfoCounter(String queryName) {
		queryInfoMap.put(queryName, infoCounter++);
	}

}
