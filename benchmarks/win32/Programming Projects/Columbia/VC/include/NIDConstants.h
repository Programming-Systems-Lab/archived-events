#ifndef NIDConstants_H
#define	NIDConstants_H

/**********************************************************************
	Author: Rean Griffith	
	CUID:	117-90-8141
	Description:	Constants used by intrusion detection system

**********************************************************************/

#include <AString.h>

/* Constants for DLL export or import*/
#ifdef EXPORT_NID
	#define NIDExport __declspec( dllexport ) 
#else 
	#define NIDExport __declspec( dllimport ) 
#endif

//Protocol constants
const int TCP_PROTOCOL	= 0;
const int UDP_PROTOCOL	= 1;
const int ICMP_PROTOCOL	= 2;

//ICMP Service constants
const int ICMP_ECHO		= 15;
const int ICMP_REDIRECT	= 25;

//XML Declaration
const AString XML_DECLARATION = "<?xml version='1.0'?>";

//XML Tag constants

//IP packet XML constants
const AString IP_XML_START_TAG					= "<IP>";
const AString IP_XML_END_TAG					= "</IP>";
const AString IP_XML_SOURCE_ADDRESS_START_TAG	= "<IP_SourceAddress>";
const AString IP_XML_SOURCE_ADDRESS_END_TAG		= "</IP_SourceAddress>";
const AString IP_XML_DEST_ADDRESS_START_TAG		= "<IP_DestAddress>";
const AString IP_XML_DEST_ADDRESS_END_TAG		= "</IP_DestAddress>";
const AString IP_XML_PROTOCOL_START_TAG			= "<IP_Protocol>";
const AString IP_XML_PROTOCOL_END_TAG			= "</IP_Protocol>";
const AString IP_XML_DATA_START_TAG				= "<IP_Data>";
const AString IP_XML_DATA_END_TAG				= "</IP_Data>";

//TCP packet XML constants

const AString TCP_XML_START_TAG					= "<TCP>";
const AString TCP_XML_END_TAG					= "</TCP>";
const AString TCP_XML_SOURCE_PORT_START_TAG		= "<TCP_SourcePort>";
const AString TCP_XML_SOURCE_PORT_END_TAG		= "</TCP_SourcePort>";
const AString TCP_XML_DEST_PORT_START_TAG		= "<TCP_DestPort>";
const AString TCP_XML_DEST_PORT_END_TAG			= "</TCP_DestPort>";
const AString TCP_XML_FLAGS_START_TAG			= "<TCP_Flags>";
const AString TCP_XML_FLAGS_END_TAG				= "</TCP_Flags>";
const AString TCP_XML_URG_START_TAG				= "<URG>";
const AString TCP_XML_URG_END_TAG				= "</URG>";
const AString TCP_XML_ACK_START_TAG				= "<ACK>";
const AString TCP_XML_ACK_END_TAG				= "</ACK>";
const AString TCP_XML_PSH_START_TAG				= "<PSH>";
const AString TCP_XML_PSH_END_TAG				= "</PSH>";
const AString TCP_XML_RST_START_TAG				= "<RST>";
const AString TCP_XML_RST_END_TAG				= "</RST>";
const AString TCP_XML_SYN_START_TAG				= "<SYN>";
const AString TCP_XML_SYN_END_TAG				= "</SYN>";
const AString TCP_XML_FIN_START_TAG				= "<FIN>";
const AString TCP_XML_FIN_END_TAG				= "</FIN>";

//ICMP packet XML constants
const AString ICMP_XML_START_TAG				= "<ICMP>";
const AString ICMP_XML_END_TAG					= "</ICMP>";
const AString ICMP_XML_SERVICETYPE_START_TAG	= "<ICMP_Service>";
const AString ICMP_XML_SERVICETYPE_END_TAG		= "</ICMP_Service>";
const AString ICMP_XML_SOURCE_PORT_START_TAG	= "<ICMP_SourcePort>";
const AString ICMP_XML_SOURCE_PORT_END_TAG		= "</ICMP_SourcePort>";
const AString ICMP_XML_DEST_PORT_START_TAG		= "<ICMP_DestPort>";
const AString ICMP_XML_DEST_PORT_END_TAG		= "</ICMP_DestPort>";

//UDP packet XML constants
const AString UDP_XML_START_TAG				= "<UDP>";
const AString UDP_XML_END_TAG				= "</UDP>";
const AString UDP_XML_SOURCE_PORT_START_TAG	= "<UDP_SourcePort>";
const AString UDP_XML_SOURCE_PORT_END_TAG	= "</UDP_SourcePort>";
const AString UDP_XML_DEST_PORT_START_TAG	= "<UDP_DestPort>";
const AString UDP_XML_DEST_PORT_END_TAG		= "</UDP_DestPort>";

//Sensor constants
const AString BROADCAST_ADDRESS_PATTERN1 = "255.";
const AString BROADCAST_ADDRESS_PATTERN2 = ".255";
const int ECHO		= 7;
const int CHARGEN	= 13;
const int PERIM_SENSOR_FLOOD_THRESHOLD_RATE = 100;//100; //if more than 100 packets per sec, possible flood
const int INNER_SENSOR_FLOOD_THRESHOLD_RATE = 120;//if more than 120 packets per sec, possible flood

//Sensor event categories
const int SENSOR_EVENT_CATEGORY_QUERY	= 11;
const int SENSOR_EVENT_CATEGORY_ALERT	= 12;	

//Sensor event types
const int FLOOD_DETECTED		= 20;
const int SIGNATURE_DETECTED	= 21;

//Sensor Event XML constants
const AString SENSOR_EVENT_XML_START_TAG			= "<SensorEvent>";
const AString SENSOR_EVENT_XML_END_TAG				= "</SensorEvent>";
const AString SENSOR_ID_XML_START_TAG				= "<SensorID>";
const AString SENSOR_ID_XML_END_TAG					= "</SensorID>";
const AString SENSOR_EVENT_CATEGORY_XML_START_TAG	= "<SensorEventCategory>";
const AString SENSOR_EVENT_CATEGORY_XML_END_TAG		= "</SensorEventCategory>";
const AString SENSOR_EVENT_TYPE_XML_START_TAG		= "<SensorEventType>";
const AString SENSOR_EVENT_TYPE_XML_END_TAG			= "</SensorEventType>";
const AString SENSOR_EVENT_MESSAGE_XML_START_TAG	= "<SensorEventMessage>";
const AString SENSOR_EVENT_MESSAGE_XML_END_TAG		= "</SensorEventMessage>";
const AString SENSOR_EVENT_DATA_XML_START_TAG		= "<SensorEventData>";
const AString SENSOR_EVENT_DATA_XML_END_TAG			= "</SensorEventData>";

//debugging flag
#define DEBUG_TEST

//application specific HRESULTS
const HRESULT S_ACCEPTABLE_TRAFFIC_RATE = MAKE_HRESULT( SEVERITY_SUCCESS, FACILITY_ITF, 0x200 + 5 );
const HRESULT S_BENIGN_IP_PACKET = MAKE_HRESULT( SEVERITY_SUCCESS, FACILITY_ITF, 0x200 + 6 );
const HRESULT S_THRESHOLD_EXCEEDED = MAKE_HRESULT( SEVERITY_SUCCESS, FACILITY_ITF, 0x200 + 7 );

const int MAX_BUFFER_SIZE = 255;

const AString HOST_IP = "64.212.343.23";
const AString HOST_NAME = "test.columbia.edu";
const AString ADMIN = "Admin";
const AString THRESHOLD_ADJ_MSG = "THRESHOLD_ADJUST";

#endif