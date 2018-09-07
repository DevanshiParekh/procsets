package com.semaifour.facesix.data.jdbc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semaifour.facesix.domain.JSONMap;

/**
 * NetcatRowProcessor  is a row processor that forwards each row as a json string to a network host:port.
 * 
 * @author mjs
 *
 */
public class NetcatRowProcessor extends RowProcessor {
	static Logger LOG = LoggerFactory.getLogger(NetcatRowProcessor.class.getName());
	
	private String host;
	private int port;
	private Socket socket;
	private PrintWriter outToServer;
	private BufferedReader inFromServer;
	
	public NetcatRowProcessor(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	@Override
	public void begin() {
		LOG.info("begin - connect");
		try {
			socket = new Socket(host, port);
			outToServer = new PrintWriter(socket.getOutputStream(), true);
			inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (Exception e) {
			LOG.error("Error creating connection to " + host + ":" + port, e);
		}
	}

	@Override
	public void end() {
		LOG.info("end - close");
		try {
			socket.close();
		} catch (Exception e) {
			LOG.error("Error closing connection to " + host + ":" + port, e);
		}
	}

	@Override
	public void offer(JSONMap map) {	
		LOG.info("offered :" + map.toJSONString());
		try {
			if (outToServer != null) {
				outToServer.println(map.toJSONString());
			}
			if (inFromServer != null && inFromServer.ready()) {
				String ack = inFromServer.readLine();
				LOG.info("ACK : " + ack);
			}

		} catch (Exception e) {
			LOG.error("Error closing connection to " + host + ":" + port, e);
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public PrintWriter getOutToServer() {
		return outToServer;
	}

	public void setOutToServer(PrintWriter outToServer) {
		this.outToServer = outToServer;
	}

	public BufferedReader getInFromServer() {
		return inFromServer;
	}

	public void setInFromServer(BufferedReader inFromServer) {
		this.inFromServer = inFromServer;
	}
}
