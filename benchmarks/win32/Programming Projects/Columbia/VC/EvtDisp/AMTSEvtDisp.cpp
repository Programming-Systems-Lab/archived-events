// AMTSEvtDisp.cpp : Implementation of AMTSEvtDisp
#include "stdafx.h"
#include "EvtDisp.h"
#include "AMTSEvtDisp.h"
#include "ServerCallObject.h"
#import <testEvent.tlb> no_namespace named_guids raw_dispinterfaces

/////////////////////////////////////////////////////////////////////////////
// AMTSEvtDisp

STDMETHODIMP AMTSEvtDisp::InterfaceSupportsErrorInfo(REFIID riid)
{
	static const IID* arr[] = 
	{
		&IID_IAMTSEvtDisp
	};
	for (int i=0; i < sizeof(arr) / sizeof(arr[0]); i++)
	{
		if (::InlineIsEqualGUID(*arr[i],riid))
			return S_OK;
	}
	return S_FALSE;
}

STDMETHODIMP AMTSEvtDisp::CreateCall(	REFIID riid1, 
										IUnknown* pUnk, 
										REFIID riid2,
										IUnknown** ppv )
{
	// Validate input parameters.
	if ( riid1 != IID_AsyncIAMTSEvtDisp || ( pUnk != NULL && riid2 != IID_IUnknown ) )
		return E_INVALIDARG;

	//Create a server-side call object and allow COM to aggregate it if
	//pUnk is non-NULL.
	try
	{
		CComPolyObject<CServerCallObject>* pCallObject = NULL;
		HRESULT hr = CComPolyObject<CServerCallObject>::CreateInstance( pUnk, &pCallObject );

		if ( FAILED ( hr ) )
			return hr;
		
		// Query the call object for the requested interface.
		return pCallObject->QueryInterface ( riid2, reinterpret_cast<void**> (ppv) );
	}
	catch(...)
	{
		return E_FAIL;
	}
}

//Synchronous interface
STDMETHODIMP AMTSEvtDisp::FireDirectedEvent(BSTR bstrData, BSTR bstrDest )
{
	IAMTSTest* pEvent = NULL;
	
	
	COSERVERINFO serverInfo;
	MULTI_QI mq;

	serverInfo.dwReserved1 = 0;
	serverInfo.dwReserved2 = 0;
	serverInfo.pAuthInfo = NULL;
	serverInfo.pwszName = bstrDest;

	mq.hr = E_FAIL;
	mq.pIID = &IID_IUnknown; //request IUnknown interface
	mq.pItf = NULL;

	HRESULT hr = E_FAIL;

	//test CoCreateInstanceEx
	hr = CoCreateInstanceEx( CLSID_AMTSTest,
							 NULL,
							 CLSCTX_REMOTE_SERVER,
							 &serverInfo,
							 1,
							&mq );
	if( FAILED( hr ) )
	{
		return E_FAIL;
	}
	
	//Query IUnknown for interface we want
	hr = mq.pItf->QueryInterface( IID_IAMTSTest, reinterpret_cast<void**>( &pEvent ) );
	if( FAILED( hr ) )
		goto EXIT_FIRE_DIRECTED_EVENT;
	
EXIT_FIRE_DIRECTED_EVENT:

	if( pEvent )
		pEvent->Release();

	pEvent = NULL;

	return hr;
}
