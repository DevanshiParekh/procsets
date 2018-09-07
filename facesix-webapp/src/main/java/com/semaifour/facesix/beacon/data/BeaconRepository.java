package com.semaifour.facesix.beacon.data;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Beacons repository 
 * 
 * @author mjs
 *
 */
public interface BeaconRepository extends MongoRepository<Beacon, String> {
	public Beacon findById(String id);
	public Beacon findByUuid(String uuid);	
	public List<Beacon> findByUid(String uid);
	public List<Beacon> findByName(String name);
	public List<Beacon> findByStatus(String status);
	public Beacon findByAssignedTo(String assignedTo);
	public List<Beacon>  findByScannerUid(String scannerUid);
	public List<Beacon> findByMacaddr(String macaddr);
	public List<Beacon> findByCid(String cid);
	public List<Beacon> findBySid(String sid);
	public List<Beacon> findBySpid(String spid);
	public List<Beacon> findByReciverId(String uid);
	public List<Beacon> findByReciverinfo(String reciverUid);
	
	@Query("{uuid:?0, major:?1, minor:?2}")
	public Beacon findByUuidAndMajorAndMinor(String uuid, int major, int minor);
	
	@Query("{sid:?0, assignedTo:?1}")
	public List<Beacon> getSavedBeaconBySidAndAssignedTo(String sid, String assignedTo);
	public List<Beacon> findByServerid(String serverid);
	public List<Beacon> findByTagType(String type);
	@Query("{sid:?0, tag_type:?1}")
	public List<Beacon> getSavedBeaconBySidAndTagType(String sid, String type);
	@Query("{cid:?0, assignedTo:?1}")
	public List<Beacon> getSavedBeaconByCidAndAssignedTo(String cid, String name);
	
	@Query("{cid:?0, tag_type:?1}")
	public List<Beacon> getSavedBeaconByCidAndTagType(String cid, String type);
	
	@Query("{macaddr:?0,status:?1}")
	public List<Beacon> getSavedBeaconByMacaddrAndStatus(String macaddr, String status);
	
	@Query("{spid:?0,status:?1}")
	public List<Beacon> getSavedBeaconBySpidAndStatus(String spid, String status);
	
	@Query("{cid:?0,status:?1}")
	public Collection<Beacon> getSavedBeaconByCidAndStatus(String cid, String status);
	
	@Query("{cid:?0,macaddr:?1}")
	public List<Beacon> getSavedBeaconByCidAndMacAddr(String cid, String macaddr);
	
	@Query("{sid:?0,status:?1}")
	public List<Beacon> getSavedBeaconBySidAndStatus(String sid, String status);
	
	@Query("{cid:?0,status:?1,mailsent:?2,lastSeen : { $lt: ?3 }}")
	public List<Beacon> findByCidStatusMailSentLastSeenBefore(String cid,String status,String mailsent,long lastSeen);
	
	@Query("{spid:?0,state:?1,status:?2}")
	public List<Beacon> getSavedBeaconBySpidStateAndStatus(String spid, String state, String status);
	
	@Query("{cid:?0,sid:?1,state:?2,status:?3}")
	public List<Beacon> getSavedBeaconByCidSidStateAndStatus(String cid,String sid, String state, String status);
	
	@Query("{cid:?0,spid:?1,state:?2,status:?3}")
	public List<Beacon> getSavedBeaconByCidSpidStateAndStatus(String cid, String spid, String state, String status);
	
	@Query("{cid:?0,state:?1,status:?2}")
	public Collection<Beacon> getSavedBeaconByCidSpidStateAndStatus(String cid, String state, String status);
	
	@Query("{cid:?0,sid:?1}")
	public List<Beacon> getSavedBeaconByCidAndSid(String cid, String sid);
	
	@Query("{cid:?0,sid:?1,status:?2}")
	public Collection<Beacon> getSavedBeaconByCidSidAndStatus(String cid, String sid, String status);
	
	@Query("{cid:?0,tag_type:?1,status:?2,mailsent:?3,lastSeen : {$lt: ?4 }}")
	public List<Beacon> findByCidTagTypeStatusMailSentLastSeenBefore(String cid, String tagType, String status,
			String sentMail, long time);
	
	@Query("{reciverinfo:?0,status:?1}")
	public List<Beacon> getSavedBeaconByReciverinfoAndStatus(String receiverinfo, String status);
	
	@Query("{cid:?0,status:?1,battery_level:{$lt:?2}}")
	public List<Beacon> findByCidStatusAndBatteryLevel(String cid, String status, int battery_threshold);
	
	@Query("{reciverinfo:?0,state:?1,status:?2}")
	public Collection<Beacon> getSavedBeaconByReciverinfoStateAndStatus(String receiverinfo, String state,
			String status);
	
	@Query("{spid:?0,state:?1,status:?2,tag_type:?3}")
	public List<Beacon> findBySpidStateStatusAndTagType(String spid, String state, String status, String tagtype);
	
	@Query("{cid:?0,tag_type:?1,status:?2}")
	public List<Beacon> getSavedBeaconByCidTagTypeAndStatus(String cid, String tagtype, String status);
	
	@Query("{cid:?0,tag_type:?1,status:?2,lastSeen : {$lt: ?3 }}")
	public List<Beacon> findByCidTagTypeStatusLastSeenBefore(String cid, String tagtype, String status, long inactivityTime);
	
	@Query("{cid:?0,tag_type:?1,status:?2,macaddr:{$in:?3}}")
	public List<Beacon> findByCidTagTypeStatusAndMacaddr(String cid, String tagtype, String status, List<String> macaddrs);
	
	@Query("{cid:?0,tag_type:?1,status:?2,sid:{$nin:?3}}")
	public List<Beacon> findByCidTagTypeStatusAndNotSid(String cid, String tagtype, String status, List<String> sid);
	
	@Query("{cid:?0,tag_type:?1,status:?2,spid:{$nin:?3}}")
	public List<Beacon> findByCidTagTypeStatusAndNotSpid(String cid, String tagtype, String status, List<String> spid);
	
	@Query("{cid:?0,tag_type:?1,status:?2,reciverinfo:{$nin:?3}}")
	public List<Beacon> findByCidTagTypeStatusAndNotReceiverInfo(String cid, String tagtype, String status, List<String> receiverinfo);
	
	@Query("{cid:?0,tag_type:?1,status:?2,macaddr:{$in:?3},sid:{$nin:?4}}")
	public List<Beacon> findByCidTagTypeStatusTagIdsAndNotSid(String cid, String tagtype, String status,
			List<String> macaddr, List<String> sid);
	
	@Query("{cid:?0,tag_type:?1,status:?2,macaddr:{$in:?3},spid:{$nin:?4}}")
	public List<Beacon> findByCidTagTypeStatusTagIdsAndNotSpid(String cid, String tagtype, String status,
			List<String> macaddr, List<String> spid);
	
	@Query("{cid:?0,tag_type:?1,status:?2,macaddr:{$in:?3},reciverinfo:{$nin:?4}}")
	public List<Beacon> findByCidTagTypeStatusTagIdsAndNotReceiverInfo(String cid, String tagtype, String status,
			List<String> macaddr, List<String> receiverinfo);
	
/*	@Query("{cid:?0,tag_type:?1,status:?2,assignedTo:{$in:?3}}")
	public List<Beacon> findByCidTagTypeStatusAndAssignedTo(String cid, String tagtype, String status,
			List<String> assignedto);*/
	
	@Query("{cid:?0,status:?1,macaddr:{$in:?2}}")
	public List<Beacon> findByCidStatusAndMacaddrs(String cid, String status, List<String> list);
	
	@Query("{cid:?0,tag_type:?1,status:?2,sid:{$in:?3},lastSeen:{$lt:?4}}")
	public List<Beacon> findByCidTagTypeStatusSidsAndLastSeenBefore(String cid, String tagtype, String status,
			List<String> sid, long inactivityTime);
	
	@Query("{cid:?0,tag_type:?1,status:?2,spid:{$in:?3},lastSeen:{$lt:?4}}")
	public List<Beacon> findByCidTagTypeStatusSpidsAndLastSeenBefore(String cid, String tagtype, String status,
			List<String> spid, long inactivityTime);
	
	@Query("{cid:?0,tag_type:?1,status:?2,reciverinfo:{$in:?3},lastSeen:{$lt:?4}}")
	public List<Beacon> findByCidTagTypeStatusReceiverInfosAndLastSeenBefore(String cid, String tagtype, String status,
			List<String> receiverinfo, long inactivityTime);
	
	@Query("{cid:?0,tag_type:?1,status:?2,macaddr:{$in:?3},sid:{$in:?4},lastSeen:{$lt:?5}}")
	public List<Beacon> findByCidTagTypeStatusTagIdsSidsAndLastSeenBefore(String cid, String tagtype, String status,
			List<String> macaddr, List<String> sid,long lastSeen);
	
	@Query("{cid:?0,tag_type:?1,status:?2,macaddr:{$in:?3},spid:{$in:?4},lastSeen:{$lt:?5}}")
	public List<Beacon> findByCidTagTypeStatusTagIdsSpidsAndLastSeenBefore(String cid, String tagtype, String status,
			List<String> macaddr, List<String> spid,long lastSeen);
	
	@Query("{cid:?0,tag_type:?1,status:?2,macaddr:{$in:?3},reciverinfo:{$in:?4},lastSeen:{$lt:?5}}")
	public List<Beacon> findByCidTagTypeStatusTagIdsReceiverInfosAndLastSeenBefore(String cid, String tagtype,
			String status, List<String> macaddr, List<String> receiverinfo,long lastSeen);
	
	@Query("{cid:?0,tag_type:?1,status:?2,sid:{$in:?3},lastSeen:{$gt:?4},localInactivityMailSent:?5}")
	public List<Beacon> findByCidTagTypeStatusSpidsAndLastSeenAfterMailSent(String cid, String tagtype, String status,
			List<String> sid, long inactivityTime, String localInactivityMailSent);
	
	@Query("{cid:?0,tag_type:?1,status:?2,spid:{$in:?3},lastSeen:{$gt:?4},localInactivityMailSent:?5}")
	public List<Beacon> findByCidTagTypeStatusSidsAndLastSeenAfterMailSent(String cid, String tagtype, String status,
			List<String> sid, long inactivityTime, String localInactivityMailSent);
	
	@Query("{cid:?0,tag_type:?1,status:?2,reciverinfo:{$in:?3},lastSeen:{$gt:?4},localInactivityMailSent:?5}")
	public List<Beacon> findByCidTagTypeStatusReceiverInfosAndLastSeenAfterMailSent(String cid, String tagtype,
			String status, List<String> receiverinfo, long inactivityTime, String localInactivityMailSent);
	
	@Query("{cid:?0,tag_type:?1,status:?2,macaddr:{$in:?3},sid:{$in:?4},lastSeen:{$gt:?5},localInactivityMailSent:?6}")
	public List<Beacon> findByCidTagTypeStatusTagIdsSidsAndLastSeenAfterMailSent(String cid, String tagtype,
			String status, List<String> macaddr, List<String> sid, long inactivityTime, String activeMailSent);
	
	@Query("{cid:?0,tag_type:?1,status:?2,macaddr:{$in:?3},spid:{$in:?4},lastSeen:{$gt:?5},localInactivityMailSent:?6}")
	public List<Beacon> findByCidTagTypeStatusTagIdsSpidsAndLastSeenAfterMailSent(String cid, String tagtype,
			String status, List<String> macaddr, List<String> spid, long inactivityTime, String activeMailSent);
	
	@Query("{cid:?0,tag_type:?1,status:?2,macaddr:{$in:?3},reciverinfo:{$in:?4},lastSeen:{$gt:?5},localInactivityMailSent:?6}")
	public List<Beacon> findByCidTagTypeStatusTagIdsReceiverInfosAndLastSeenAfterMailSent(String cid, String tagtype,
			String status, List<String> macaddr, List<String> receiverinfo, long inactivityTime, String activeMailSent);
	
	@Query("{reciverinfo:?0,status:?1}")
	public List<Beacon> findByreciverinfoAndStatus(String reciverinfo, String status);
	
	@Query("{reciverinfo:?0,tag_type:?1,status:?2}")
	public List<Beacon> getSavedBeaconByRecieverInfoTagTypeAndStatus(String reciverinfo, String type, String status);
	
	@Query("{spid:?0,tag_type:?1,status:?2}")
	public List<Beacon> getSavedBeaconBySpidTagTypeAndStatus(String spid, String type, String status);
	
	@Query("{spid:?0,tag_type:{$nin:?1},status:?2}")
	public List<Beacon> getSavedBeaconBySpidNotInTagTypeAndStatus(String spid, List<String> tagtypeList, String status);
	
	@Query("{spid:?0,tag_type:{$in:?1},status:?2}")
	public List<Beacon> getSavedBeaconBySpidInTagTypeAndStatus(String spid, List<String> tagtypeList, String status);
	
	@Query("{reciverinfo:?0,tag_type:{$nin:?1},status:?2}")
	public List<Beacon> getSavedBeaconByRecieverInfoNotInTagTypeAndStatus(String reciverinfo, List<String> tagtypeList, String status);
	
	@Query("{reciverinfo:?0,tag_type:{$in:?1},status:?2}")
	public List<Beacon> getSavedBeaconByRecieverInfoInTagTypeAndStatus(String reciverinfo, List<String> tagtypeList, String status);
	
	@Query("{spid:?0,status:?1,assignedTo:?2}")
	public List<Beacon> getSavedBeaconBySpidStatusAndAssignedto(String spid, String status, String assingedto);
	
	@Query("{cid:?0,assignedTo:?1,status:?2,reciverinfo:?3}")
	public List<Beacon> findByCidAssingnedtoStatusAndReciverinfo(String cid, String assignedTo, String status,String reciverinfo);
	
	@Query("{macaddr:{$in:?0}}")
	public List<Beacon> findByMacaddrs(List<String> macaddr);
	
	@Query("{macaddr:{$in:?0},mailsent:?1,lastSeen : { $lt: ?2 }}")
	public List<Beacon> findByMacaddrsMailSentAndLastSeenBefore(List<String> macaddr,String mailsent ,long lastseen);
}

