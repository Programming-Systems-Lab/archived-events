// AMTSTestSubscriber.cpp : Implementation of AMTSTestSubscriber
#include "stdafx.h"
#include "TestSubscriber.h"
#include "AMTSTestSubscriber.h"
#include <time.h>

const int MAX = 4000;
const int INTERVAL = 300;

/////////////////////////////////////////////////////////////////////////////
// AMTSTestSubscriber

STDMETHODIMP AMTSTestSubscriber::InterfaceSupportsErrorInfo(REFIID riid)
{
	static const IID* arr[] = 
	{
		&IID_IAMTSTestSubscriber
	};
	for (int i=0; i < sizeof(arr) / sizeof(arr[0]); i++)
	{
		if (::InlineIsEqualGUID(*arr[i],riid))
			return S_OK;
	}
	return S_FALSE;
}

HRESULT AMTSTestSubscriber::Activate()
{
	HRESULT hr = GetObjectContext(&m_spObjectContext);
	if (SUCCEEDED(hr))
		return S_OK;
	return hr;
} 

BOOL AMTSTestSubscriber::CanBePooled()
{
	return TRUE;
} 

void AMTSTestSubscriber::Deactivate()
{
	m_spObjectContext.Release();
}

STDMETHODIMP AMTSTestSubscriber::TestCall(BSTR bstrData)
{
	clock_t finish;
	double dblDuration = 0;	

	if( m_lngEventCount == 0 )
	{
		m_start = clock();
	}
			
	InterlockedIncrement( &m_lngEventCount );

	if( (m_lngEventCount % INTERVAL) == 0 )
    {
        finish = clock();

        // output another INTERVAL events received
        // Work out duration
        dblDuration = (double) ( ( finish - m_start ) / (double) ( CLOCKS_PER_SEC ) );
                
		AString szMsg = "Took ";
		szMsg += AString( dblDuration );
		szMsg += " secs";
		szMsg += " to receive another ";
		szMsg +=  AString( INTERVAL );
		szMsg += " events";
		m_EvtLogger.WriteInfoEvent( szMsg );
    }

    if( m_lngEventCount == MAX )
    {
        finish = clock();

        // output time span to receive MAX events
        // Work out duration
        dblDuration = (double) ( ( finish - m_start ) / (double) ( CLOCKS_PER_SEC ) );
        		
		AString szMsg = "Took ";
		szMsg += AString( dblDuration );
		szMsg += " secs";
		szMsg += " to receive ";
		szMsg +=  AString( MAX );
		szMsg += " events";

		m_lngEventCount = 0;

		m_EvtLogger.WriteInfoEvent( szMsg );
      
    }
	
	/*if( m_bFirstEvent )
	{
		//get current time
		m_lastEventArrival = COleDateTime::GetCurrentTime();
		//reset event count
		m_lngEventCount = 0;
		//reset first event received
		m_bFirstEvent = false;
	}

	//get time now
	COleDateTime Now( COleDateTime::GetCurrentTime() );
	COleDateTimeSpan spanElapsed = Now - m_lastEventArrival;

	if( spanElapsed.GetTotalSeconds() <= 1 )
		m_lngEventCount++;
	else 
	{
		m_bFirstEvent = true; //reset counters
		AString szData( "Event Rate per sec: " );
		szData += AString( m_lngEventCount );
		m_EvtLogger.WriteInfoEvent( szData );
	}*/

	return S_OK;
}


