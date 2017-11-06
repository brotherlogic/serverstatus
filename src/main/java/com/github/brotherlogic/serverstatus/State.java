package com.github.brotherlogic.serverstatus;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class State {
	private List<Job> jobs;
	
	public State() {
		jobs = new LinkedList<Job>();
	}
}

class Job {
	private String jobName;
	private Map<Address, Calendar> instanceAndUptime;
	
	public Job(String name) {
		jobName = name;
		instanceAndUptime = new HashMap<Address, Calendar>();
	}
	
	public void setUptime(Address addr, Calendar upTime) {
		instanceAndUptime.put(addr, upTime);
	}
	
	public String getName() {
		return jobName;
	}
	
	public Calendar getMaxUptime() {
		Calendar ret = null;
		for (Calendar cal : instanceAndUptime.values()) {
			if (ret == null || cal.before(ret)) {
				ret = cal;
			}
		}
		return ret;
	}
}

class Address {
	private String ip;
	private int port;
	
	public Address(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
}
