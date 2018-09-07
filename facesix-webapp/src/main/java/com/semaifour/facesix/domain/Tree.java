package com.semaifour.facesix.domain;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Tree {
	
	private String status;
	private String message;
	private Link[] links;
	private Node[] nodes;
	private Graph[] graphs;
	
	public Tree(String status, String message,Link[] links, Node[] nodes) {
		this.setStatus(status);
		this.setMessage(message);
		this.setLinks(links);
		this.setNodes(nodes);
		setGraphs(new Graph[0]);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Link[] getLinks() {
		return links;
	}

	public void setLinks(Link[] links) {
		this.links = links;
	}

	public Node[] getNodes() {
		return nodes;
	}

	public void setNodes(Node[] nodes) {
		this.nodes = nodes;
	}
	
	public String toJSONString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
		    mapper.setSerializationInclusion(Include.NON_NULL);
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Graph[] getGraphs() {
		return graphs;
	}

	public void setGraphs(Graph[] graphs) {
		this.graphs = graphs;
	}
}
