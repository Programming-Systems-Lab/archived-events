// ServerCallObject.cpp : Implementation of CServerCallObject
#include "stdafx.h"
#include "EvtDisp.h"
#include "ServerCallObject.h"

extern FireDirected( _bstr_t bstrtData, _bstr_t bstrtDest );

/////////////////////////////////////////////////////////////////////////////
// CServerCallObject


STDMETHODIMP CServerCallObject::Begin_FireDirectedEvent( BSTR bstrData, BSTR bstrDest )
{
	try
	{
		//access member variable m_bCallInProgress
		Lock();

		//Call object supports one call at a time only
		if( m_bCallInProgress )
		{
			Unlock();
			return ( m_hResultBegin = RPC_S_CALLPENDING );
		}

		m_bCallInProgress = TRUE;
		
		//store local copy of data being passed in
		m_bstrtData = _bstr_t( bstrData ).copy();
		m_bstrtDest = _bstr_t( bstrDest ).copy();

		Unlock ();
	
		//
		// Reset the synchronization object used to signal the stub that the
		// call has returned.
		//
		ISynchronize* pSynchronize = NULL;
		HRESULT hr = ( (AsyncIAMTSEvtDisp*) this)->QueryInterface (IID_ISynchronize, reinterpret_cast<void**> (&pSynchronize) );
	
		if( pSynchronize )
			hr = pSynchronize->Reset();
	
		// Dispatch request to thread pool queue to be serviced subsequently
		if ( !QueueUserWorkItem( ThreadFuncDirected, this, WT_EXECUTEDEFAULT ) ) 
		{
			//report error
			m_bCallInProgress = FALSE;
			m_hResultBegin = E_OUTOFMEMORY;
		
			if( pSynchronize )
			{
				hr = pSynchronize->Signal ();
				pSynchronize->Release ();
				pSynchronize = NULL;
			}
			return m_hResultBegin;
		}

		// Clean up and return.
		if( pSynchronize )
			pSynchronize->Release ();
	
		pSynchronize = NULL;

		return ( m_hResultBegin = S_OK );
	}
	catch(...)
	{
		m_bCallInProgress = FALSE;
		return E_FAIL;
	}
}


STDMETHODIMP CServerCallObject::Finish_FireDirectedEvent()
{
	//if Begin_XXXX method has not been called just exit, nothing 2 do
	if( !m_bCallInProgress )
		return RPC_E_CALL_COMPLETE;

	//return the status code of the Begin_XXXX method
	if( FAILED ( m_hResultBegin ) )
		return m_hResultBegin;

	try
	{
		// If the call hasn't returned, wait until it does.
		ISynchronize* pSynchronize = NULL;
		HRESULT hr = ( (AsyncIAMTSEvtDisp*) this)->QueryInterface (IID_ISynchronize, reinterpret_cast<void**>( &pSynchronize) );
	
		if( pSynchronize )
		{
			hr = pSynchronize->Wait(0, INFINITE);
		
			pSynchronize->Release();
			pSynchronize = NULL;
		}
	}
	catch(...)
	{
	}

	m_bCallInProgress = FALSE;
	return m_hResultFinish;
}

DWORD WINAPI ThreadFuncDirected (LPVOID pThreadParms)
{
	CoInitializeEx (NULL, COINIT_MULTITHREADED);
	
	//cast LPVOID parameter to ServerCallObject
	CServerCallObject* pServerCallObject = (CServerCallObject*) pThreadParms;
	
	//set result code to E_FAIL by default	
	pServerCallObject->m_hResultFinish = E_FAIL;
	// Let the stub know that the call is complete.
	ISynchronize* pSynchronize = NULL;
	_bstr_t bstrtData( L"" );
	_bstr_t bstrtDest( L"" );
	HRESULT hr = E_FAIL;
		
	// Make a local copy of the input parameter
	try
	{
		bstrtData = pServerCallObject->m_bstrtData.copy();
		bstrtDest = pServerCallObject->m_bstrtDest.copy();
	}
	catch(...)
	{
		goto EXIT_THREAD_PROC_DIRECTED;
	}
	
	if( ( bstrtData.length() == 0 || bstrtDest.length() == 0 ) || FireDirected( bstrtData, bstrtDest ) != 0 )
		goto EXIT_THREAD_PROC_DIRECTED;

	//set stauts of Finish_XXXX method
	pServerCallObject->m_hResultFinish = S_OK;
		
EXIT_THREAD_PROC_DIRECTED:
	
	//signal done
	try
	{
		hr = ( (AsyncIAMTSEvtDisp*) pServerCallObject)->QueryInterface(IID_ISynchronize, reinterpret_cast<void**>(&pSynchronize) );
	
		if( pSynchronize )
		{
			hr = pSynchronize->Signal();
			pSynchronize->Release ();
			pSynchronize = NULL;
		}
	}
	catch(...)
	{
	
	}

	CoUninitialize ();
	return 0;
}

