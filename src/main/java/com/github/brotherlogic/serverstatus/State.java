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

	public void update(RegistryEntry entry) {
		if (!jobs.containsKey(entry.getName())) {
            jobs.put(entry.getName(), new Job(entry.getName()));
        }
		jobs.get(entry.getName()).updateInstance(entry);
	}

	@Override
	public String toString() {
		return jobs.toString();
	}
}



class Job {
	private String jobName;
	private List<Instance> instances;

	public Job(String name){
	    jobName = name;
	    instances = new LinkedList<Instance>();
    }

    public List<Instance> getInstances() {
	    return instances;
    }

    public String getName() {
	    return jobName;
    }

    public void updateInstance(RegistryEntry entry) {
	    boolean found = false;
	    for (Instance i : instances){
	        if (i.getEntry().getIp() == entry.getIp() && i.getEntry().getPort() == entry.getPort()) {
	            found = true;
	            i.updateInstance(entry);
            }
        }

        if (!found) {
	        instances.add(new Instance(entry));
        }
    }
}

class Instance {

    private RegistryEntry address;
	private String key;

	public Instance(RegistryEntry entry) {
		updateInstance(entry);
	}

	public void updateInstance(RegistryEntry entry) {
	    address = entry;
    }

	public RegistryEntry getEntry() {
		return address;
	}

	public String getSpecial() {
		return key;
	}

	public boolean isMaster() {
		return getEntry().getMaster();
	}

	public String getUptime() {
		long seconds = (Calendar.getInstance().getTimeInMillis()/1000 - getEntry().getRegisterTime());

		if (seconds > 60 * 60 * 24) {
			return seconds / (60 * 60 * 24) + "d";
		} else if (seconds > 60 * 60) {
			return seconds / (60 * 60) + "h";
		} else if (seconds > 60) {
			return seconds / 60 + "m";
		}

		return "" + seconds;
	}
}
