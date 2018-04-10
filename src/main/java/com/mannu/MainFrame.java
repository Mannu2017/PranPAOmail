package com.mannu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

public class MainFrame extends JFrame{
	
	private String[] args;
	
	public MainFrame() {
		Timer timer=new Timer();
		timer.schedule(new SendMail(), 0,1000*60*1440);
	}

	public void setArgs(String[] args) {
		this.args = args;
		
	}

}
