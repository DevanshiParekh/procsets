package com.semaifour.facesix.beacon.data;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportBeaconRepository extends MongoRepository<ReportBeacon, String>{

	ReportBeacon findOneByMacaddr(String macaddr);

}
