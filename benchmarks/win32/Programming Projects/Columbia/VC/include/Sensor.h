// Sensor.h: interface for the Sensor class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_SENSOR_H__5B7285E9_CF13_47FD_9D59_CBB6087E415C__INCLUDED_)
#define AFX_SENSOR_H__5B7285E9_CF13_47FD_9D59_CBB6087E415C__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

/**********************************************************************
	Author: Rean Griffith	
	CUID:	117-90-8141
	Description:	Base class for all sensors

**********************************************************************/

#include <AString.h>
#include <EvtLogger.h>
#include <NIDConstants.h>

class Sensor  
{
public:
	//	Destructor
	virtual ~Sensor();
	//	Function receives and IP packet and monitors the 
	//	receipt rate of packets per second. Returns false
	//	if ThresholdRate exceeded 
	virtual HRESULT ReceiveIPPacket( BSTR bstrIPPacket );
	//	Function sends a console alert event
	virtual HRESULT SendSensorAlert( BSTR bstrSource, BSTR bstrMsg, BSTR bstrData );

protected:
	// Protected constructor, class not intended to be standalone
	Sensor( AString szClassname );	
	
	//	Member variables
	COleDateTime m_lastEventArrival;
	long m_lngEventCount;
	bool m_bFirstEvent;
	EvtLogger m_EvtLogger;
	long m_lngThresholdRate;
	AString m_szClassname;

};

#endif // !defined(AFX_SENSOR_H__5B7285E9_CF13_47FD_9D59_CBB6087E415C__INCLUDED_)
