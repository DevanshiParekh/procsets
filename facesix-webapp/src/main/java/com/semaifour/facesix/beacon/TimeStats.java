package com.semaifour.facesix.beacon;

public class TimeStats {

	private long totalWaitTime;
	private long minWaitTime;
	private long maxWaitTime;
	private long avgWaitTime;

	private long totalElapsedTime;
	private long minElapsedTime;
	private long maxElapsedTime;
	private long avgElapsedTime;
	
	private String location;
	private long visitCount;
	private long totalPatients;
	
	private long totalInTime;
	private long minInTime;
	private long maxInTime;
	private long avgInTime;
	
	public long getTotalWaitTime() {
		return totalWaitTime;
	}

	public void setTotalWaitTime(long totalWaitTime) {
		this.totalWaitTime = totalWaitTime;
	}
	
	public long getMinWaitTime() {
		return minWaitTime;
	}

	public void setMinWaitTime(long minWaitTime) {
		this.minWaitTime = minWaitTime;
	}
	
	public long getMaxWaitTime() {
		return maxWaitTime;
	}

	public void setMaxWaitTime(long maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}
	
	public long getTotalElapsedTime() {
		return totalElapsedTime;
	}

	public void setTotalElapsedTime(long totalElapsedTime) {
		this.totalElapsedTime = totalElapsedTime;
	}
	
	public long getMinElapsedTime() {
		return minElapsedTime;
	}

	public void setMinElapsedTime(long minElapsedTime) {
		this.minElapsedTime = minElapsedTime;
	}

	public long getMaxElapsedTime() {
		return maxElapsedTime;
	}

	public void setMaxElapsedTime(long maxElapsedTime) {
		this.maxElapsedTime = maxElapsedTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public long getVisitCount() {
		return visitCount;
	}

	public long getAvgWaitTime() {
		if(getVisitCount() != 0){
			avgWaitTime = getTotalWaitTime()/getVisitCount();
		}
		return avgWaitTime;
	}

	public void setAvgWaitTime(long avgWaitTime) {
		this.avgWaitTime = avgWaitTime;
	}

	public long getAvgElapsedTime() {
		if(getVisitCount() != 0){
			avgElapsedTime = getTotalElapsedTime()/getVisitCount();
		}
		return avgElapsedTime;
	}

	public void setAvgElapsedTime(long avgElapsedTime) {
		this.avgElapsedTime = avgElapsedTime;
	}

	public long getTotalPatients() {
		return totalPatients;
	}
	
	public void setVisitCount(long visitCount) {
		this.visitCount = visitCount;
	}
	public TimeStats(long value, long visit, String location, Boolean isElapsed, long totVisits) {
		
		if(isElapsed) {
			totalElapsedTime = value;
			minElapsedTime = value;
			maxElapsedTime = value;
		}
		else {
			totalWaitTime = value;
			minWaitTime = value;
			maxWaitTime = value;
		}
		
		totalPatients = totVisits;
		setVisitCount(visit);
		this.setLocation(location);
	}
	
	public TimeStats(long totalHopitalTime, String location, long totPatient) {
		this.totalInTime = totalHopitalTime;
		this.location = location;
		this.totalPatients = totPatient;
		
	}

	public void addWaitVisit(long value) {
		totalWaitTime += value;
		
		/*Evaluate minTime and maxTime*/
		if(value < minWaitTime || minWaitTime == 0) {
			minWaitTime = value;
		}
		if (value > maxWaitTime) {
			maxWaitTime = value;
		}
	}
	
	public void addElapsedVisit(long value) {
		/*Add visitCount only when patient moves in*/
		setVisitCount(getVisitCount() + 1);
		totalElapsedTime += value;
		
		/*Evaluate minTime and maxTime*/
		if(value < minElapsedTime || minElapsedTime == 0) {
			minElapsedTime = value;
		}
		if (value > maxElapsedTime) {
			maxElapsedTime = value;
		}
	}
	
	public void addTotalInTime(long value) {
		totalInTime += value;
		
		/*Evaluate minTime and maxTime*/
		if(value < minInTime || minInTime == 0) {
			minInTime = value;
		}
		if (value > maxInTime) {
			maxInTime = value;
		}
	}
	
	public void calculateAverages() {
		if(visitCount != 0) {
			setAvgElapsedTime(totalElapsedTime/visitCount);
			setAvgWaitTime(totalWaitTime/visitCount);
		}
		if(totalPatients != 0) {
			setAvgInTime(totalInTime/getTotalPatients());
		}
	}

	public long getTotalInTime() {
		return totalInTime;
	}

	public void setTotalInTime(long totalInTime) {
		this.totalInTime = totalInTime;
	}

	public long getMinInTime() {
		return minInTime;
	}

	public void setMinInTime(long minInTime) {
		this.minInTime = minInTime;
	}

	public long getMaxInTime() {
		return maxInTime;
	}

	public void setMaxInTime(long maxInTime) {
		this.maxInTime = maxInTime;
	}

	public long getAvgInTime() {
		if(visitCount != 0){
			avgInTime = totalInTime/totalPatients;
		}
		return avgInTime;
	}

	public void setAvgInTime(long avgInTime) {
		this.avgInTime = avgInTime;
	}

}