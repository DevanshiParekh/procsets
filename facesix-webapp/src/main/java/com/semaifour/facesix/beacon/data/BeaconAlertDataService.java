package com.semaifour.facesix.beacon.data;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BeaconAlertDataService {

	Logger LOG = LoggerFactory.getLogger(BeaconAlertDataService.class.getName());

	@Autowired(required = false)
	private BeaconAlertDataRepository repository;

	public BeaconAlertData findById(String id) {
		return repository.findById(id);
	}

	public List<BeaconAlertData> findByCid(String cid) {
		return repository.findByCid(cid);
	}

	public BeaconAlertData save(BeaconAlertData beaconAlertData) {
		beaconAlertData = repository.save(beaconAlertData);
		return beaconAlertData;
	}
	
	public void delete(BeaconAlertData beaconAlertData) {
		repository.delete(beaconAlertData);
	}
}
