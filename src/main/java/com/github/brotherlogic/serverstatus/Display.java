package com.github.brotherlogic.serverstatus;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Display extends JFrame {

	Model m;

	public Display(String bServer) {
		m = new Model(bServer);
		this.getRootPane().setLayout(new GridLayout(0, 2));
	}

	public void showModel() {
		System.out.println("Updating model");
		m.update();
		this.getRootPane().removeAll();
		int numberOfJobs = m.getNumberOfJobs();

		int counter = 0;
		for (Job j : m.getJobs()) {
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(0, 1));
			JLabel label = new JLabel(j.getName());
			label.setHorizontalAlignment(JLabel.LEFT);
			panel.add(label);

			for (Address addr : j.getAddresses()) {
				JLabel inLabel = new JLabel(addr.toString() + ": " + j.getUptime(addr));
				inLabel.setHorizontalAlignment(JLabel.RIGHT);
				panel.add(inLabel);
			}

			this.getRootPane().add(panel);
		}

		System.out.println("Shown " + m.getJobs().size() + " Jobs");
		this.revalidate();
	}

	public void run() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setSize(new Dimension(800, 480));
		this.setLocationRelativeTo(null);
		this.revalidate();
		this.setVisible(true);

		Thread updater = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000 * 30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					showModel();
				}
			}
		});
		updater.start();
	}

}
