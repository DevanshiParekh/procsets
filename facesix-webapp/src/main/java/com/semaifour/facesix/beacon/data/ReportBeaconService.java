package com.semaifour.facesix.beacon.data;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportBeaconService {

	private static String classname 	= ReportBeaconService.class.getName();

	Logger LOG = LoggerFactory.getLogger(classname);
	
	@Autowired(required = false)
	private ReportBeaconRepository repository;
	
	public ReportBeacon findOneByMacaddr(String tagid) {
		return repository.findOneByMacaddr(tagid);
	}

	public ReportBeacon save(ReportBeacon reportBeacon) {
		return repository.save(reportBeacon);
	}

	public List<ReportBeacon> saveList(List<ReportBeacon> reportBeaconList) {
		return repository.save(reportBeaconList);
	}

}
