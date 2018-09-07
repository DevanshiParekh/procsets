package com.semaifour.facesix.data.elasticsearch.device;

import java.util.Map;

public class HeartBeat {
	private long timestamp;
	private String uid;
	private int health;
	private long nextbeat;
	
	public HeartBeat() {
	}
	
	public HeartBeat(Map<String, Object> map) {
		this.timestamp = System.currentTimeMillis();
		this.uid = (String)map.get("uid");
		try {
			Object o = map.get("health");
			if (o instanceof Integer) {
				this.health = ((Integer) o).intValue();
			} else {
				this.health = Integer.parseInt(String.valueOf(map.get("health")));
			}
		} catch (Exception e) {
			this.health = -1;
		}

		try {
			this.nextbeat = Long.parseLong((String)map.get("nextbeat"));
		} catch (Exception e) {
			this.nextbeat = 60000;
		}

	}
	
	
	public String getUid() {
		return uid;
	}
	
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public int getHealth() {
		return health;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public long getNextbeat() {
		return nextbeat;
	}
	
	public void setNextbeat(long nextbeat) {
		this.nextbeat = nextbeat;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public String state() {
		String st = this.health > 0 ? "On" : "Off";
		//if expired health
		if (System.currentTimeMillis() > (this.timestamp + this.nextbeat)) {
			st = "Unknown [" + st + "]";
		}
		return st;
	}
}
