package com.github.brotherlogic.serverstatus;

import java.util.Calendar;
import java.util.Collection;
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

	private Calendar convertTime(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time * 1000);
		return cal;
	}

	public void update(RegistryEntry entry) {
		if (jobs.containsKey(entry.getName())) {
			jobs.get(entry.getName()).setUptime(new Address(entry.getIp(), entry.getPort()),
					convertTime(entry.getRegisterTime()));
		} else {
			jobs.put(entry.getName(), new Job(entry.getName()));
			jobs.get(entry.getName()).setUptime(new Address(entry.getIp(), entry.getPort()),
					convertTime(entry.getRegisterTime()));
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
		instanceAndUptime = new TreeMap<Address, Calendar>();
	}

	public Collection<Address> getAddresses() {
		return instanceAndUptime.keySet();
	}

	public void setUptime(Address addr, Calendar upTime) {
		instanceAndUptime.put(addr, upTime);
	}

	public String getName() {
		return jobName;
	}

	public String getUptime(Address a) {
		long seconds = (Calendar.getInstance().getTimeInMillis() - instanceAndUptime.get(a).getTimeInMillis()) / 1000;
		return "" + seconds;
	}
}

class Address implements Comparable<Address> {
	@Override
	public int compareTo(Address o) {
		int val = ip.compareTo(o.ip);
		if (val == 0)
			val = o.port - port;
		return val;
	}

	private String ip;
	private int port;

	public Address(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	@Override
	public String toString() {
		return ip + ":" + port;
	}
}
