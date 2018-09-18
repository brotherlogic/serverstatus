package com.github.brotherlogic.serverstatus;

import java.awt.Dimension;
import java.awt.Font;
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
		m.update();
		this.getRootPane().removeAll();
		int numberOfJobs = m.getNumberOfJobs();

		int counter = 0;
		for (Job j : m.getJobs()) {
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(0, 1));
			JLabel label = new JLabel(j.getName());
			Font f1 = label.getFont().deriveFont(Font.PLAIN, 10);
			label.setFont(f1);
			label.setHorizontalAlignment(JLabel.LEFT);
			panel.add(label);

			for (Instance inst : j.getInstances()) {
				String text = inst.getEntry().getIdentifier() + "@" + inst.getEntry().getPort() + ": " + inst.getUptime();
				if (inst.getSpecial() != null) {
					text += " [" + inst.getSpecial() + " ]";
				}
				JLabel inLabel = new JLabel(text);
				Font f = inLabel.getFont().deriveFont(Font.PLAIN, 10);

				if (inst.isMaster()) {
					inLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
				} else {
					inLabel.setFont(f.deriveFont(f.getStyle() & ~Font.BOLD));
				}
				inLabel.setHorizontalAlignment(JLabel.RIGHT);
				panel.add(inLabel);
			}

			this.getRootPane().add(panel);
		}

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
