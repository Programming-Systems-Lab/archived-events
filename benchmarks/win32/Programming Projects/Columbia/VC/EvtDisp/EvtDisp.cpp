// EvtDisp.cpp : Implementation of WinMain


// Note: Proxy/Stub Information
//      To build a separate proxy/stub DLL, 
//      run nmake -f EvtDispps.mk in the project directory.

#include "stdafx.h"
#include "resource.h"
#include <initguid.h>
#include "EvtDisp.h"
#include <list>
#include <map>
#include <string>

#include "EvtDisp_i.c"
#include "AMTSEvtDisp.h"
#include "ServerCallObject.h"
#import <testEvent.tlb> no_namespace named_guids raw_dispinterfaces
#include <time.h>
#include <stdlib.h>


const int MAX = 4000;

using namespace std;

map<_bstr_t,IAMTSTest*> gLookup;

int FireDirected( _bstr_t bstrtData, _bstr_t bstrtDest )
{
	int nRetVal = -1;

	try
	{
		HRESULT hr = E_FAIL;
		IAMTSTest* pEvent = NULL;

		//do lookup for event class pointer
		map<_bstr_t,IAMTSTest*>::iterator it;
		it = gLookup.find( bstrtDest );

		//if not found then no subscribers interested
		if( it == gLookup.end() )
			goto EXIT_FIRE_DIRECTED;
			
		//use pointer to target machine
		pEvent = it->second;

		if( pEvent == NULL ) //invalid entry in lookup table
			goto EXIT_FIRE_DIRECTED;
		
		//fire event
		hr = pEvent->raw_TestCall( bstrtData );
		
		//release interface if we run into problems
		if( FAILED( hr ) )
		{
			//report error
			goto EXIT_FIRE_DIRECTED;
		}

		nRetVal = 0;

	}
	catch( ... )
	{
		//report error
		goto EXIT_FIRE_DIRECTED;
	}

EXIT_FIRE_DIRECTED:

	return nRetVal;	
}

const DWORD dwTimeOut = 5000; // time for EXE to be idle before shutting down
const DWORD dwPause = 1000; // time to wait for threads to finish up

// Passed to CreateThread to monitor the shutdown event
static DWORD WINAPI MonitorProc(void* pv)
{
    CExeModule* p = (CExeModule*)pv;
    p->MonitorShutdown();
    return 0;
}

LONG CExeModule::Unlock()
{
    LONG l = CComModule::Unlock();
    if (l == 0)
    {
        bActivity = true;
        SetEvent(hEventShutdown); // tell monitor that we transitioned to zero
    }
    return l;
}

//Monitors the shutdown event
void CExeModule::MonitorShutdown()
{
    while (1)
    {
        WaitForSingleObject(hEventShutdown, INFINITE);
        DWORD dwWait=0;
        do
        {
            bActivity = false;
            dwWait = WaitForSingleObject(hEventShutdown, dwTimeOut);
        } while (dwWait == WAIT_OBJECT_0);
        // timed out
        if (!bActivity && m_nLockCnt == 0) // if no activity let's really bail
        {
			
#if _WIN32_WINNT >= 0x0400 & defined(_ATL_FREE_THREADED)
            CoSuspendClassObjects();
            if (!bActivity && m_nLockCnt == 0)
#endif
				break;
		}
    }
    CloseHandle(hEventShutdown);
    PostThreadMessage(dwThreadID, WM_QUIT, 0, 0);
}

bool CExeModule::StartMonitor()
{
    hEventShutdown = CreateEvent(NULL, false, false, NULL);
    if (hEventShutdown == NULL)
        return false;
    DWORD dwThreadID;
    HANDLE h = CreateThread(NULL, 0, MonitorProc, this, 0, &dwThreadID);
    return (h != NULL);
}

CExeModule _Module;

BEGIN_OBJECT_MAP(ObjectMap)
OBJECT_ENTRY(CLSID_AMTSEvtDisp, AMTSEvtDisp)
END_OBJECT_MAP()


LPCTSTR FindOneOf(LPCTSTR p1, LPCTSTR p2)
{
    while (p1 != NULL && *p1 != NULL)
    {
        LPCTSTR p = p2;
        while (p != NULL && *p != NULL)
        {
            if (*p1 == *p)
                return CharNext(p1);
            p = CharNext(p);
        }
        p1 = CharNext(p1);
    }
    return NULL;
}

IAMTSTest* GetEventClassInstance( _bstr_t bstrtDest )
{
	if( bstrtDest.length() == 0 )
		return NULL;

	COSERVERINFO serverInfo;
	MULTI_QI mq;

	serverInfo.dwReserved1 = 0;
	serverInfo.dwReserved2 = 0;
	serverInfo.pAuthInfo = NULL;
	serverInfo.pwszName = bstrtDest;
	IAMTSTest* pEvent = NULL;

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
		//report error
		return NULL;
	}
	
	//Query IUnknown for interface we want
	hr = mq.pItf->QueryInterface( IID_IAMTSTest, reinterpret_cast<void**>( &pEvent ) );
	if( FAILED( hr ) )
	{
		//report Error
		return NULL;
	}

	return pEvent; //return interface pointer
}

/////////////////////////////////////////////////////////////////////////////
//
extern "C" int WINAPI _tWinMain(HINSTANCE hInstance, 
    HINSTANCE /*hPrevInstance*/, LPTSTR lpCmdLine, int /*nShowCmd*/)
{
    lpCmdLine = GetCommandLine(); //this line necessary for _ATL_MIN_CRT

#if _WIN32_WINNT >= 0x0400 & defined(_ATL_FREE_THREADED)
    HRESULT hRes = CoInitializeEx(NULL, COINIT_MULTITHREADED);
#else
    HRESULT hRes = CoInitialize(NULL);
#endif
    _ASSERTE(SUCCEEDED(hRes));
    _Module.Init(ObjectMap, hInstance, &LIBID_EVTDISPLib);
    _Module.dwThreadID = GetCurrentThreadId();
    TCHAR szTokens[] = _T("-/");

    int nRet = 0;
    BOOL bRun = TRUE;
    LPCTSTR lpszToken = FindOneOf(lpCmdLine, szTokens);
    while (lpszToken != NULL)
    {
        if (lstrcmpi(lpszToken, _T("UnregServer"))==0)
        {
            _Module.UpdateRegistryFromResource(IDR_EvtDisp, FALSE);
            nRet = _Module.UnregisterServer(TRUE);
            bRun = FALSE;
            break;
        }
        if (lstrcmpi(lpszToken, _T("RegServer"))==0)
        {
            _Module.UpdateRegistryFromResource(IDR_EvtDisp, TRUE);
            nRet = _Module.RegisterServer(TRUE);
            bRun = FALSE;
            break;
        }
        lpszToken = FindOneOf(lpszToken, szTokens);
    }

    if (bRun)
    {
        _Module.StartMonitor();
#if _WIN32_WINNT >= 0x0400 & defined(_ATL_FREE_THREADED)
        hRes = _Module.RegisterClassObjects(CLSCTX_LOCAL_SERVER, 
            REGCLS_MULTIPLEUSE | REGCLS_SUSPENDED);
        _ASSERTE(SUCCEEDED(hRes));
        hRes = CoResumeClassObjects();
#else
        hRes = _Module.RegisterClassObjects(CLSCTX_LOCAL_SERVER, 
            REGCLS_MULTIPLEUSE);
#endif
        _ASSERTE(SUCCEEDED(hRes));


		/*************************************************************************/
		//init lookup table of known addresses
		
		_bstr_t bstrtDest1 = L"128.59.23.34"; //Astor
		_bstr_t bstrtDest2 = L"128.59.23.43"; //Bowery
		_bstr_t bstrtDest3 = L"128.59.23.13"; //Extreme		
		_bstr_t bstrtDest4 = L"128.59.23.46"; //Bleeker
		_bstr_t bstrtDest5 = L"128.59.23.42"; //Grand

		list<_bstr_t> lstIPs;
		list<_bstr_t>::iterator ipIT;
		//lstIPs.push_back( bstrtDest1 );
		//lstIPs.push_back( bstrtDest2 );
		//lstIPs.push_back( bstrtDest3 );
		//lstIPs.push_back( bstrtDest4 );
		lstIPs.push_back( bstrtDest5 );
		
		for( ipIT = lstIPs.begin(); ipIT != lstIPs.end(); ipIT++ )
		{
			_bstr_t bstrtIP = *ipIT;
			IAMTSTest* pTemp = GetEventClassInstance( bstrtIP );

			if( pTemp != NULL )
			{
				pair<map<_bstr_t,IAMTSTest*>::iterator,bool> res = gLookup.insert( make_pair<_bstr_t,IAMTSTest*>( bstrtIP, pTemp ) );
				if( !res.second )
				{
					//report error
				}
			}
			
			pTemp = NULL;
		}
		
		//cycle thru map, double check
		map<_bstr_t,IAMTSTest* >::iterator mapIT;

		for( mapIT = gLookup.begin(); mapIT != gLookup.end(); mapIT++ )
		{
			_bstr_t bstrtDest = mapIT->first;
			IAMTSTest* pEvent = mapIT->second;

		}

		/*************************************************************************/
		
		MSG msg;
        while (GetMessage(&msg, 0, 0, 0))
            DispatchMessage(&msg);

        _Module.RevokeClassObjects();
        Sleep(dwPause); //wait for any threads to finish
    }

	_Module.Term();
    CoUninitialize();
    return nRet;
}
