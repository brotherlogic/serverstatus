package com.github.brotherlogic.serverstatus;

import java.util.LinkedList;
import java.util.List;

import com.github.brotherlogic.javaserver.JavaServer;

import io.grpc.BindableService;

import java.util.logging.LogManager;

public class Server extends JavaServer {

	Display d;

	public Server(Display d) {
		this.d = d;
	}

	@Override
	public void localServe() {
		d.run();
	}

	public static void main(String[] args) {
		Server s = new Server(new Display(args[0]));
		s.Serve(args[0]);
	}

	@Override
	public String getServerName() {
		return "serverstatus";
	}

	@Override
	public List<BindableService> getServices() {
		return new LinkedList<BindableService>();
	}

}
