var piedata = {
    "device_details": [
        {
            "uid": "18:31:bf:57:df:48",
            "state": "inactive",
            "location": "Bed Room",
            "tx_bytes": 0,
            "rx_bytes": 0,
            "security_type": "WPA2",
            "metrics": {
                "_2g_metrics": {
                    "channel_number": 1,
                    "_2g_station_count": 0,
                    "_2g_good_clients": "0%",
                    "_2g_fair_clients": "0%",
                    "_2g_poor_clients": "0%",
                    "_2g_avg_station_rssi_percentage": 40
                },
                "_5g_metrics": {
                    "channel_number": 36,
                    "_5g_station_count": 0,
                    "_5g_good_clients": "0%",
                    "_5g_fair_clients": "0%",
                    "_5g_poor_clients": "0%",
                    "_5g_avg_station_rssi_percentage": 40
                }
            },
        },
        {
            "uid": "18:31:bf:57:d3:68",
            "state": "active",
            "location": "Living Room",
            "tx_bytes": 0,
            "rx_bytes": 0,
            "security_type": "WPA2",
            "metrics": {
                "_2g_metrics": {
                    "channel_number": 1,
                    "_2g_station_count": 0,
                    "_2g_good_clients": "0%",
                    "_2g_fair_clients": "0%",
                    "_2g_poor_clients": "0%",
                    "_2g_avg_station_rssi_percentage": 40
                },
                "_5g_metrics": {
                    "channel_number": 36,
                    "_5g_station_count": 0,
                    "_5g_good_clients": "0%",
                    "_5g_fair_clients": "0%",
                    "_5g_poor_clients": "0%",
                    "_5g_avg_station_rssi_percentage": 40
                }
            },
        },
        {
            "uid": "18:31:bf:57:dd:50",
            "state": "inactive",
            "location": "kitchen room",
            "tx_bytes": 0,
            "rx_bytes": 0,
            "metrics": {
                "_2g_metrics": {
                    "channel_number": 1,
                    "_2g_station_count": 0,
                    "_2g_good_clients": "0%",
                    "_2g_fair_clients": "0%",
                    "_2g_poor_clients": "0%",
                    "_2g_avg_station_rssi_percentage": 10
                },
                "_5g_metrics": {
                    "channel_number": 36,
                    "_5g_station_count": 0,
                    "_5g_good_clients": "0%",
                    "_5g_fair_clients": "0%",
                    "_5g_poor_clients": "0%",
                    "_2g_avg_station_rssi_percentage": 30
                }
            },
        },
        {
            "uid": "dc:ef:09:e3:32:15",
            "state": "active",
            "location": "balcony",
            "tx_bytes": 0,
            "rx_bytes": 0,
            "security_type": "WPA2",
            "metrics": {
                "_2g_metrics": {
                    "channel_number": 1,
                    "_2g_station_count": 0,
                    "_2g_good_clients": "0%",
                    "_2g_fair_clients": "0%",
                    "_2g_poor_clients": "0%",
                    "_2g_avg_station_rssi_percentage": 40
                },
                "_5g_metrics": {
                    "channel_number": 36,
                    "_5g_station_count": 0,
                    "_5g_good_clients": "0%",
                    "_5g_fair_clients": "0%",
                    "_5g_poor_clients": "0%",
                    "_5g_avg_station_rssi_percentage": 40
                }
            },
        },
        {
            "uid": "dc:ef:09:ea:91:7c",
            "state": "active",
            "location": "balcony-1",
            "tx_bytes": 0,
            "rx_bytes": 0,
            "security_type": "WPA2",
            "metrics": {
                "_2g_metrics": {
                    "channel_number": 1,
                    "_2g_station_count": 22,
                    "_2g_good_clients": "0%",
                    "_2g_fair_clients": "0%",
                    "_2g_poor_clients": "0%",
                    "_2g_avg_station_rssi_percentage": 40
                },
                "_5g_metrics": {
                    "channel_number": 36,
                    "_5g_station_count": 2,
                    "_5g_good_clients": "0%",
                    "_5g_fair_clients": "0%",
                    "_5g_poor_clients": "0%",
                    "_5g_avg_station_rssi_percentage": 40
                }
            },
        }
    ]
}
var topologydata4 = {
    "title": "My Home",
    "topology": [{
        "uid": "18:31:bf:57:d3:68",
        "wan_backhaul": true,
        "location": "Living Room",
        "mesh_links": [
            {//Kitchen
                "uid": "18:31:bf:57:dd:50",
                "rssi": -55,
                "signal_strength_percent": 77,
                "band": "2.4Ghz",
                "_mesh_tx_bytes": 197136,
                "_mesh_rx_bytes": 176215,
                "_mesh_tx_pkts": 351,
                "_mesh_rx_pkts": 294,
                "conn_time_sec": 650
            },
            {//Bedroom
                "uid": "18:31:bf:57:df:48",
                "rssi": -67,
                "signal_strength_percent": 59,
                "band": "2.4Ghz",
                "_mesh_tx_bytes": 187438,
                "_mesh_rx_bytes": 136870,
                "_mesh_tx_pkts": 319,
                "_mesh_rx_pkts": 234,
                "conn_time_sec": 794
            },
            {//Garage
                "uid": "dc:ef:09:e3:32:15",
                "rssi": -67,
                "signal_strength_percent": 59,
                "band": "5Ghz",
                "_mesh_tx_bytes": 187438,
                "_mesh_rx_bytes": 136870,
                "_mesh_tx_pkts": 319,
                "_mesh_rx_pkts": 234,
                "conn_time_sec": 356
            }
        ]
    },
    {
        "uid": "18:31:bf:57:dd:50",
        "wan_backhaul": false,
        "location": "Kitchen",
        "mesh_links": [
            {//Bedroom
                "uid": "18:31:bf:57:df:48",
                "rssi": -55,
                "signal_strength_percent": 77,
                "band": "2.4Ghz",
                "_mesh_tx_bytes": 197136,
                "_mesh_rx_bytes": 176215,
                "_mesh_tx_pkts": 351,
                "_mesh_rx_pkts": 294,
                "conn_time_sec": 650
            },
            {//Living Room
                "uid": "18:31:bf:57:d3:68",
                "rssi": -67,
                "signal_strength_percent": 59,
                "band": "2.4Ghz",
                "_mesh_tx_bytes": 187438,
                "_mesh_rx_bytes": 136870,
                "_mesh_tx_pkts": 319,
                "_mesh_rx_pkts": 234,
                "conn_time_sec": 794
            },
            {//Garage
                "uid": "dc:ef:09:e3:32:15",
                "rssi": -67,
                "signal_strength_percent": 59,
                "band": "5Ghz",
                "_mesh_tx_bytes": 187438,
                "_mesh_rx_bytes": 136870,
                "_mesh_tx_pkts": 319,
                "_mesh_rx_pkts": 234,
                "conn_time_sec": 356
            }
        ]
    },
    {
        "uid": "dc:ef:09:e3:32:15",
        "wan_backhaul": false,
        "location": "Garage",
        "mesh_links": [
            {//Bed room
                "uid": "18:31:bf:57:df:48",
                "rssi": -55,
                "signal_strength_percent": 77,
                "band": "2.4Ghz",
                "_mesh_tx_bytes": 197136,
                "_mesh_rx_bytes": 176215,
                "_mesh_tx_pkts": 351,
                "_mesh_rx_pkts": 294,
                "conn_time_sec": 650
            },
            {//Kitchen
                "uid": "18:31:bf:57:dd:50",
                "rssi": -67,
                "signal_strength_percent": 59,
                "band": "2.4Ghz",
                "_mesh_tx_bytes": 187438,
                "_mesh_rx_bytes": 136870,
                "_mesh_tx_pkts": 319,
                "_mesh_rx_pkts": 234,
                "conn_time_sec": 794
            },
            {//Living room
                "uid": "18:31:bf:57:d3:68",
                "rssi": -67,
                "signal_strength_percent": 59,
                "band": "5Ghz",
                "_mesh_tx_bytes": 187438,
                "_mesh_rx_bytes": 136870,
                "_mesh_tx_pkts": 319,
                "_mesh_rx_pkts": 234,
                "conn_time_sec": 356
            }
        ]
    },
    {
        "uid": "18:31:bf:57:df:48",
        "wan_backhaul": false,
        "location": "Bed Room",
        "mesh_links": [
            {//Garage
                "uid": "dc:ef:09:e3:32:15",
                "rssi": -55,
                "signal_strength_percent": 77,
                "band": "2.4Ghz",
                "_mesh_tx_bytes": 197136,
                "_mesh_rx_bytes": 176215,
                "_mesh_tx_pkts": 351,
                "_mesh_rx_pkts": 294,
                "conn_time_sec": 650
            },
            {//Kitchen
                "uid": "18:31:bf:57:dd:50",
                "rssi": -67,
                "signal_strength_percent": 59,
                "band": "2.4Ghz",
                "_mesh_tx_bytes": 187438,
                "_mesh_rx_bytes": 136870,
                "_mesh_tx_pkts": 319,
                "_mesh_rx_pkts": 234,
                "conn_time_sec": 794
            },
            {//Living room
                "uid": "18:31:bf:57:d3:68",
                "rssi": -67,
                "signal_strength_percent": 59,
                "band": "5Ghz",
                "_mesh_tx_bytes": 187438,
                "_mesh_rx_bytes": 136870,
                "_mesh_tx_pkts": 319,
                "_mesh_rx_pkts": 234,
                "conn_time_sec": 356
            }
        ]
    }
    ]
}

var topologydata3 = {
    "title": "My Home",
    "topology": [{
        "uid": "18:31:bf:57:d3:68",
        "wan_backhaul": true,
        "location": "Living Room",
        "mesh_links": [
            
            {//Bedroom
                "uid": "18:31:bf:57:df:48",
                "rssi": -67,
                "signal_strength_percent": 99,
                "band": "2.4Ghz",
                "_mesh_tx_bytes": 187438,
                "_mesh_rx_bytes": 136870,
                "_mesh_tx_pkts": 319,
                "_mesh_rx_pkts": 234,
                "conn_time_sec": 794
            },
            {//Garage
                "uid": "dc:ef:09:e3:32:15",
                "rssi": -67,
                "signal_strength_percent": 99,
                "band": "5Ghz",
                "_mesh_tx_bytes": 187438,
                "_mesh_rx_bytes": 136870,
                "_mesh_tx_pkts": 319,
                "_mesh_rx_pkts": 234,
                "conn_time_sec": 356
            }
        ]
    },
    
    {
        "uid": "dc:ef:09:e3:32:15",
        "wan_backhaul": false,
        "location": "Garage",
        "mesh_links": [
            {//Bed room
                "uid": "18:31:bf:57:df:48",
                "rssi": -55,
                "signal_strength_percent": 77,
                "band": "2.4Ghz",
                "_mesh_tx_bytes": 197136,
                "_mesh_rx_bytes": 176215,
                "_mesh_tx_pkts": 351,
                "_mesh_rx_pkts": 294,
                "conn_time_sec": 650
            },
            
            {//Living room
                "uid": "18:31:bf:57:d3:68",
                "rssi": -67,
                "signal_strength_percent": 59,
                "band": "5Ghz",
                "_mesh_tx_bytes": 187438,
                "_mesh_rx_bytes": 136870,
                "_mesh_tx_pkts": 319,
                "_mesh_rx_pkts": 234,
                "conn_time_sec": 356
            }
        ]
    },
    {
        "uid": "18:31:bf:57:df:48",
        "wan_backhaul": false,
        "location": "Bed Room",
        "mesh_links": [
            {//Garage
                "uid": "dc:ef:09:e3:32:15",
                "rssi": -55,
                "signal_strength_percent": 77,
                "band": "2.4Ghz",
                "_mesh_tx_bytes": 197136,
                "_mesh_rx_bytes": 176215,
                "_mesh_tx_pkts": 351,
                "_mesh_rx_pkts": 294,
                "conn_time_sec": 650
            },
            {//Living room
                "uid": "18:31:bf:57:d3:68",
                "rssi": -67,
                "signal_strength_percent": 59,
                "band": "5Ghz",
                "_mesh_tx_bytes": 187438,
                "_mesh_rx_bytes": 136870,
                "_mesh_tx_pkts": 319,
                "_mesh_rx_pkts": 234,
                "conn_time_sec": 356
            }
        ]
    }
    ]
}

var topologydata2 = {
    "title": "My Home",
    "topology": [{
        "uid": "18:31:bf:57:d3:68",
        "wan_backhaul": true,
        "location": "Living Room",
        "mesh_links": [
            
            {//Bedroom
                "uid": "18:31:bf:57:df:48",
                "rssi": -67,
                "signal_strength_percent": 59,
                "band": "2.4Ghz",
                "_mesh_tx_bytes": 187438,
                "_mesh_rx_bytes": 136870,
                "_mesh_tx_pkts": 319,
                "_mesh_rx_pkts": 234,
                "conn_time_sec": 794
            }
        ]
    },
    {
        "uid": "18:31:bf:57:df:48",
        "wan_backhaul": false,
        "location": "Bed Room",
        "mesh_links": [
            {//Living room
                "uid": "18:31:bf:57:d3:68",
                "rssi": -67,
                "signal_strength_percent": 59,
                "band": "5Ghz",
                "_mesh_tx_bytes": 187438,
                "_mesh_rx_bytes": 136870,
                "_mesh_tx_pkts": 319,
                "_mesh_rx_pkts": 234,
                "conn_time_sec": 356
            }
        ]
    }
    ]
}

var topologydata1 = {
    "title": "My Home",
    "topology": [{
        "uid": "18:31:bf:57:d3:68",
        "wan_backhaul": true,
        "location": "Living Room",
        "mesh_links": [
            
            
        ]
    }
    ]
}
var clientsummary = {
    "wireless_client_details": [
        {
            "mac_address": "a0:32:99:40:51:12",
            "uid": "dc:ef:09:ea:91:7c",
            "location": "bed room",
            "bssid": "11:22:33:44:55:66",
            "ssid": "senthilhome-2g",
            "rssi": -59,
            "signal_strength": 40,
            "_peer_tx_bytes": 13000,
            "_peer_rx_bytes": 35000,
            "_11r": false,
            "_11v": false,
            "_11k": false,
            "client_type": "11a",
            "conn_time_sec": 40000,
            "no_of_streams": 1,
            "os": "android",
            "host_name": "LENOVOBE...",
            "radio": "2.4Ghz",
            "ip": "192.168.40.1"
        },
        {
            "mac_address": "dc:ef:ca:83:b0:93",
            "uid": "dc:ef:09:ea:91:7c",
            "location": "bed room",
            "bssid": "11:22:33:44:55:66",
            "ssid": "senthilhome-2g",
            "rssi": -39,
            "_peer_tx_bytes": 1000,
            "_peer_rx_bytes": 30000,
            "_11r": false,
            "l": false,
            "_11k": false,
            "client_type": "11b",
            "conn_time": 30000,
            "no_of_streams": 2,
            "os": "laptop",
            "devtype": "MURATAMA...",
            "radio": "2.4Ghz",
            "ip": "192.168.1.2"
        }
    ]
}
var histogramdata = {
    "device_metrics_histogram ": [
        {
            "memory": {
                "mem_percentage": 17,
                "time": "2018-07-04 05:14:25.997"
            },
            "cpu": {
                "cpu_percentage": 2,
                "time": "2018-07-04 05:14:25.997"
            },
            "tx_rx_histogram": [
                {
                    "tx_bytes": 2359466,
                    "rx_bytes": 1954228,
                    "time": "2018-07-04 05:14:25.997"
                },
                {
                    "tx_bytes": 2353969,
                    "rx_bytes": 1952191,
                    "time": "2018-07-04 05:11:25.742"
                },
                {
                    "tx_bytes": 2325813,
                    "rx_bytes": 1938734,
                    "time": "2018-07-04 05:08:25.740"
                },
                {
                    "tx_bytes": 2225589,
                    "rx_bytes": 1912323,
                    "time": "2018-07-04 05:05:25.604"
                },
                {
                    "tx_bytes": 2225198,
                    "rx_bytes": 1912096,
                    "time": "2018-07-04 05:02:25.602"
                }
            ]
        }
    ]
}
