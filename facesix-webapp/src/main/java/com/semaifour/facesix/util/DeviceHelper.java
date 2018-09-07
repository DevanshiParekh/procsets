package com.semaifour.facesix.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.device.Device;
import com.semaifour.facesix.domain.Link;
import com.semaifour.facesix.domain.Node;
import com.semaifour.facesix.domain.Tree;

public class DeviceHelper {

	public static String toJSON4D3Network(Device device) throws JsonParseException, JsonMappingException, IOException {
		String conf = device.getConf();
		Map<String, Object> confMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		confMap = mapper.readValue(conf, new TypeReference<Map<String, Object>>(){});
		
		ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<Link> links = new ArrayList<Link>();
		//root node 
		Node node = new Node(device.getUid(), "online", "wifi");
		//0.element
		nodes.add(node);

		int source = 0;
		int target = nodes.size(); //size == next free position

		Link link = null;
		List<Object> lits = (List<Object>) confMap.get("radio2g");
		Map<Object, Object> map = null;
		//there will be only one item in the radio2g
		if( lits != null && lits.size() > 0) {
			map = (Map<Object, Object>) lits.get(0);
			node = new Node(MapUtils.getString(map, "wifi-device"), "online", "radio2g");
			node.putAll(map);
			nodes.add(node);
			link = new Link(source, target, "online"); //attach raido2g to root device
			links.add(link);
			
			//move source to previous radio node
			source = target; //
			target = nodes.size(); //size == next free position. i.e where next node will be inserted
			lits = (List<Object>) confMap.get("interfaces2g");
			for (Object obj : lits) {
				map = (Map<Object, Object>) obj;
				node = new Node(MapUtils.getString(map, "ssid"), "online", "interfaces2g");
				node.putAll(map);
				nodes.add(node);
				link = new Link(source, target++, "online");
				links.add(link);
			}
			
		}
		
		//reset source root, now, we are gonna read 5g
		source = 0;
		target = nodes.size(); //size == next free position. i.e where next node will be inserted
		lits = (List<Object>) confMap.get("radio5g");
		//there will be only one item in the radio2g
		if( lits != null && lits.size() > 0) {
			map = (Map<Object, Object>) lits.get(0);
			node = new Node(MapUtils.getString(map, "wifi-device"), "online", "radio5g");
			node.putAll(map);
			nodes.add(node);
			link = new Link(0, target, "online"); //attach raido5g to root device
			links.add(link);
			
			//move source to last 'radio' node inserted
			source = target;
			target = nodes.size(); //size == next free position
			lits = (List<Object>) confMap.get("interfaces5g");
			for (Object obj : lits) {
				map = (Map<Object, Object>) obj;
				node = new Node(MapUtils.getString(map, "ssid"), "online", "interfaces5g");
				node.putAll(map);
				nodes.add(node);
				link = new Link(source, target++, "online");
				links.add(link);
			}
			
		}
			
		Tree tree = new Tree("online", "success", links.toArray(new Link[links.size()]), nodes.toArray(new Node[nodes.size()]));
		
		conf = tree.toJSONString();
		
		return conf;
	}
	
	public static String toJSON4D3BeacondeviceNetwork(BeaconDevice device) throws JsonParseException, JsonMappingException, IOException {
		String conf = device.getConf();
		Map<String, Object> confMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		confMap = mapper.readValue(conf, new TypeReference<Map<String, Object>>(){});
		
		ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<Link> links = new ArrayList<Link>();
		//root node 
		Node node = new Node(device.getUid(), "online", "wifi");
		//0.element
		nodes.add(node);

		int source = 0;
		int target = nodes.size(); //size == next free position

		Link link = null;
		List<Object> lits = (List<Object>) confMap.get("radio2g");
		Map<Object, Object> map = null;
		//there will be only one item in the radio2g
		if( lits != null && lits.size() > 0) {
			map = (Map<Object, Object>) lits.get(0);
			node = new Node(MapUtils.getString(map, "wifi-device"), "online", "radio2g");
			node.putAll(map);
			nodes.add(node);
			link = new Link(source, target, "online"); //attach raido2g to root device
			links.add(link);
			
			//move source to previous radio node
			source = target; //
			target = nodes.size(); //size == next free position. i.e where next node will be inserted
			lits = (List<Object>) confMap.get("interfaces2g");
			for (Object obj : lits) {
				map = (Map<Object, Object>) obj;
				node = new Node(MapUtils.getString(map, "ssid"), "online", "interfaces2g");
				node.putAll(map);
				nodes.add(node);
				link = new Link(source, target++, "online");
				links.add(link);
			}
			
		}
		
		//reset source root, now, we are gonna read 5g
		source = 0;
		target = nodes.size(); //size == next free position. i.e where next node will be inserted
		lits = (List<Object>) confMap.get("radio5g");
		//there will be only one item in the radio2g
		if( lits != null && lits.size() > 0) {
			map = (Map<Object, Object>) lits.get(0);
			node = new Node(MapUtils.getString(map, "wifi-device"), "online", "radio5g");
			node.putAll(map);
			nodes.add(node);
			link = new Link(0, target, "online"); //attach raido5g to root device
			links.add(link);
			
			//move source to last 'radio' node inserted
			source = target;
			target = nodes.size(); //size == next free position
			lits = (List<Object>) confMap.get("interfaces5g");
			for (Object obj : lits) {
				map = (Map<Object, Object>) obj;
				node = new Node(MapUtils.getString(map, "ssid"), "online", "interfaces5g");
				node.putAll(map);
				nodes.add(node);
				link = new Link(source, target++, "online");
				links.add(link);
			}
			
		}
			
		Tree tree = new Tree("online", "success", links.toArray(new Link[links.size()]), nodes.toArray(new Node[nodes.size()]));
		
		conf = tree.toJSONString();
		
		return conf;
	}
}
