// testAsync.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "testAsync.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

#include <AString.h>
#include <EvtLogger.h>
#include <time.h>

#import<testEvent.tlb> no_namespace named_guids raw_dispinterfaces

#include <EvtDisp.h>
#include <EvtDisp_i.c>

const int MAX = 4000;

/////////////////////////////////////////////////////////////////////////////
// The one and only application object

CWinApp theApp;

using namespace std;

HRESULT SendEventTo( BSTR bstrDataIN, BSTR bstrDestIN, IUnknown* pUnk, EvtLogger& evtlog  )
{
	if( pUnk == NULL )
		return E_FAIL;

	HRESULT hr = E_FAIL;
	ICallFactory* pICallFactory = NULL;
	AsyncIAMTSEvtDisp* pAsync = NULL;

	hr = pUnk->QueryInterface( IID_ICallFactory, reinterpret_cast<void**>( &pICallFactory ) );

	//create call object
	hr = pICallFactory->CreateCall( IID_AsyncIAMTSEvtDisp, 
									NULL,
									IID_AsyncIAMTSEvtDisp,
									(IUnknown**) &pAsync );

	if( FAILED( hr ) )
	{
		evtlog.WriteErrorEvent( "Could not create Call Object" );
		return hr;
	}

	pICallFactory->Release();
	pICallFactory = NULL;
	
	BSTR bstrData = SysAllocString( bstrDataIN );
				
	//destination changes with each iteration (round robin set of targets)
	BSTR bstrDest = SysAllocString( bstrDestIN ); 
		
	//HRESULT lets u know whether your request was queued for
	//servicing
	hr = pAsync->Begin_FireDirectedEvent( bstrData, bstrDest );
	if( FAILED( hr ) )
	{
		evtlog.WriteErrorEvent( "Error executing async call" );
		return hr;
	}

	//release and continue exection
	pAsync->Release();
	pAsync = NULL;
	SysFreeString( bstrData );
	SysFreeString( bstrDest );
	
	return hr;
}

int _tmain(int argc, TCHAR* argv[], TCHAR* envp[])
{
	int nRetCode = 0;

	// initialize MFC and print and error on failure
	if (!AfxWinInit(::GetModuleHandle(NULL), NULL, ::GetCommandLine(), 0))
	{
		// TODO: change error code to suit your needs
		cerr << _T("Fatal Error: MFC initialization failed") << endl;
		nRetCode = 1;
	}
	else
	{
		//CoInitializeEx( NULL, COINIT_MULTITHREADED );
		CoInitialize( NULL );
		EvtLogger evtlog( "testAsync" );
		clock_t start;
		clock_t finish;
		double dblDuration = 0;
		
		try
		{
			IUnknown* pUnk = NULL;
					
			HRESULT hr = CoCreateInstance( CLSID_AMTSEvtDisp,
											NULL,
											CLSCTX_SERVER,
											IID_IUnknown,
											reinterpret_cast<void**>( &pUnk ) );

			if( FAILED( hr ) )
			{
				return -1;
			}

			BOOL bSentLastEvent = FALSE;
			
			evtlog.WriteInfoEvent( "Start sending events" );
			start = clock();
			
			for( int i = 0; i < MAX; i++ )
			{
				BSTR bstrData = SysAllocString( L"test data" );
				//BSTR bstrDest1 = SysAllocString( L"128.59.23.34"); //Astor
				//BSTR bstrDest2 = SysAllocString( L"128.59.23.43"); //Bowery 
				//BSTR bstrDest3 = SysAllocString( L"128.59.23.13"); //Extreme
				//BSTR bstrDest4 = SysAllocString( L"128.59.23.46" ); //Bleecker
				BSTR bstrDest5 = SysAllocString( L"128.59.23.42" ); //Grand

				//if( FAILED( SendEventTo( bstrData, bstrDest1, pUnk, evtlog ) ) )
				//	evtlog.WriteErrorEvent( "Error sending event to Dest1" );
				//if( FAILED( SendEventTo( bstrData, bstrDest2, pUnk, evtlog ) ) )
					//evtlog.WriteErrorEvent( "Error sending event to Dest2" );
				//if( FAILED( SendEventTo( bstrData, bstrDest3, pUnk, evtlog ) ) )
				//	evtlog.WriteErrorEvent( "Error sending event to Dest3" );
				//if( FAILED( SendEventTo( bstrData, bstrDest4, pUnk, evtlog ) ) )
				//	evtlog.WriteErrorEvent( "Error sending event to Dest4" );
				if( FAILED( SendEventTo( bstrData, bstrDest5, pUnk, evtlog ) ) )
					evtlog.WriteErrorEvent( "Error sending event to Dest5" );

				SysFreeString( bstrData );
				
				//SysFreeString( bstrDest1 );
				//SysFreeString( bstrDest2 );
				//SysFreeString( bstrDest3 );
				//SysFreeString( bstrDest4 );
				SysFreeString( bstrDest5 );
				
			}
			
			finish = clock();

			// Work out duration
			dblDuration = (double) ( ( finish - start ) / (double) ( CLOCKS_PER_SEC ) );
						
			AString szMsg = "Took ";
			szMsg += AString( dblDuration );
			szMsg += " secs";
			szMsg += " to publish ";
			szMsg +=  AString( MAX );
			szMsg += " events";
			evtlog.WriteInfoEvent( szMsg );
			
			if( pUnk )
				pUnk->Release();

			pUnk = NULL;
			
			CoUninitialize();
		}
		catch( _com_error& e )
		{
			AString szData( "Caught COM exception: " );
			szData += (char*) e.Description();
			evtlog.WriteErrorEvent( szData );
		}
		catch( ... )
		{
			evtlog.WriteErrorEvent( "Caught unknown exception" );
		}
		
		
		
		
		
		
		
		
		
		
		
		
		


		/*try
		{
			CoInitialize( NULL );

			IUnknown* pUnk = NULL;
			AsyncIAMTSEvtDisp* pAsync;
		
			HRESULT hr = CoCreateInstance( CLSID_AMTSEvtDisp,
											NULL,
											CLSCTX_SERVER,
											IID_IUnknown,
											reinterpret_cast<void**>( &pUnk ) );

			if( FAILED( hr ) )
			{
				return -1;
			}

			BOOL bSentLastEvent = FALSE;
						
			ICallFactory* pICallFactory = NULL; 
			
			int i = MAX;//4000;//5000;//10000;

			evtlog.WriteInfoEvent( "Start sending events" );
			//Fire and forget
			while( i > 0 )
			{
				hr = pUnk->QueryInterface( IID_ICallFactory, reinterpret_cast<void**>( &pICallFactory ) );

				//create call object
				hr = pICallFactory->CreateCall( IID_AsyncIAMTSEvtDisp, 
												NULL,
												IID_AsyncIAMTSEvtDisp,
												(IUnknown**) &pAsync );

				if( FAILED( hr ) )
				{
					evtlog.WriteErrorEvent( "Could not create Call Object" );
					return -1;
				}

				pICallFactory->Release();
				pICallFactory = NULL;
							
				/**************************************************************************/
				// Testing publishing undirected events, destination IP pre-set

				/*BSTR bstrData = SysAllocString( L"test data" );
				//HRESULT lets u know whether your request was queued for
				//servicing
				hr = pAsync->Begin_FireEvent( bstrData );
				if( FAILED( hr ) )
				{
					evtlog.WriteErrorEvent( "Error executing async call" );
					return -1;
				}

				//only if u care about the return values
				//hr = pAsync->Finish_FireEvent(); */
				/****************************************************************************/



				/****************************************************************************/
				// Testing publishing directed events, destination IP variable
/*
				BSTR bstrData = SysAllocString( L"test data" );
				
				//destination changes with each iteration (round robin set of targets)
				BSTR bstrDest = SysAllocString( L"128.59.23.34"); //Astor
				
				
				
				//HRESULT lets u know whether your request was queued for
				//servicing
				hr = pAsync->Begin_FireDirectedEvent( bstrData, bstrDest );
				if( FAILED( hr ) )
				{
					evtlog.WriteErrorEvent( "Error executing async call" );
					return -1;
				}

				//only if u care about the return values
				//hr = pAsync->Finish_FireEvent();
				/****************************************************************************/
/*



				//release and continue exection
				pAsync->Release();
				pAsync = NULL;
				SysFreeString( bstrData );
				SysFreeString( bstrDest );
				i--;

				if( i == 0 && !bSentLastEvent )
				{
					evtlog.WriteInfoEvent( "Done sending events" );
					Sleep( 50000 );
					i++;
					bSentLastEvent = TRUE;
				}
			}

			CoUninitialize();
		}
		catch( _com_error& e )
		{
			AString szData( "Caught COM exception: " );
			szData += (char*) e.Description();
			evtlog.WriteErrorEvent( szData );
		}
		catch( ... )
		{
			evtlog.WriteErrorEvent( "Caught unknown exception" );
		}*/
	}

	return nRetCode;
}


