package com.github.brotherlogic.serverstatus;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import discovery.Discovery.RegistryEntry;

public class State {
	private Map<String, Job> jobs;

	public State() {
		jobs = new TreeMap<String, Job>();
	}

	public int getNumberOfJobs() {
		return jobs.size();
	}

	public Collection<Job> getJobs() {
		return jobs.values();
	}

	public void update(RegistryEntry entry) {
		if (jobs.containsKey(entry.getName())) {
			jobs.get(entry.getName()).setUptime(new Address(entry.getIp(), entry.getPort()), Calendar.getInstance());
		} else {
			jobs.put(entry.getName(), new Job(entry.getName()));
			jobs.get(entry.getName()).setUptime(new Address(entry.getIp(), entry.getPort()), Calendar.getInstance());
		}
	}

	@Override
	public String toString() {
		return jobs.toString();
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
