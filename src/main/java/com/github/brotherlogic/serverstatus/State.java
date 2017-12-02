package com.github.brotherlogic.serverstatus;

import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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

	public boolean update(RegistryEntry entry, String special) {
		Address a = new Address(entry.getIp(), entry.getPort());
		if (jobs.containsKey(entry.getName())) {
			jobs.get(entry.getName()).setUptime(a, convertTime(entry.getRegisterTime()), entry.getMaster());
                        if (special != null && !special.equals(""))
                          jobs.get(entry.getName()).setSpecial(a, special);
			return true;
		} else {
			jobs.put(entry.getName(), new Job(entry.getName()));
			jobs.get(entry.getName()).setUptime(a, convertTime(entry.getRegisterTime()), entry.getMaster());
                        if (special != null && !special.equals(""))
                          jobs.get(entry.getName()).setSpecial(a, special);
			return false;
		}
	}

	@Override
	public String toString() {
		return jobs.toString();
	}

	public void clean() {
		for (Job j : jobs.values())
			j.clean();
	}
}

class Job {
	private String jobName;
	private Map<Address, Calendar> instanceAndUptime;
	private Map<Address, Long> lastUpdate;
	private Map<Address, Boolean> masterMap;
	private Map<Address, String> specialMap;

	public Job(String name) {
		jobName = name;
		instanceAndUptime = new TreeMap<Address, Calendar>();
		lastUpdate = new TreeMap<Address, Long>();
		masterMap = new TreeMap<Address, Boolean>();
		specialMap = new TreeMap<Address, String>();
	}

	public Collection<Address> getAddresses() {
		return instanceAndUptime.keySet();
	}

	public void setUptime(Address addr, Calendar upTime, boolean master) {
		instanceAndUptime.put(addr, upTime);
		lastUpdate.put(addr, System.currentTimeMillis());
		masterMap.put(addr, master);
	}

	public void setSpecial(Address addr, String special) {
		if (special == null) {
			specialMap.remove(addr);
		} else {
			specialMap.put(addr, special);
		}
	}

	public String getName() {
		return jobName;
	}

	public String getSpecial(Address a) {
		if (specialMap.containsKey(a)) {
			return specialMap.get(a);
		}
		return null;
	}

	public boolean isMaster(Address a) {
		return masterMap.get(a);
	}

	public String getUptime(Address a) {
		long seconds = (Calendar.getInstance().getTimeInMillis() - instanceAndUptime.get(a).getTimeInMillis()) / 1000;

		if (seconds > 60 * 60 * 24) {
			return seconds / (60 * 60 * 24) + "d";
		} else if (seconds > 60 * 60) {
			return seconds / (60 * 60) + "h";
		} else if (seconds > 60) {
			return seconds / 60 + "m";
		}

		return "" + seconds;
	}

	public void clean() {
		List<Address> addresses = new LinkedList<Address>();
		for (Address a : lastUpdate.keySet()) {
			if (System.currentTimeMillis() - lastUpdate.get(a) > 60 * 1000) {
				addresses.add(a);
			}
		}

		for (Address a : addresses) {
			instanceAndUptime.remove(a);
			lastUpdate.remove(a);
			masterMap.remove(a);
		}
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
