package com.semaifour.facesix.data.elasticsearch.notif;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface NotificationRepository extends ElasticsearchRepository<Notification, String> {

}