// ServerCallObject.h : Declaration of the CServerCallObject

#ifndef __SERVERCALLOBJECT_H_
#define __SERVERCALLOBJECT_H_

#include "resource.h"       // main symbols
#include <COMDEF.h>
#import <testEvent.tlb> no_namespace named_guids raw_dispinterfaces
DWORD WINAPI ThreadFuncUndirected (LPVOID pThreadParms);
DWORD WINAPI ThreadFuncDirected (LPVOID pThreadParms);

const int BUFFER_LEN = 33;

/////////////////////////////////////////////////////////////////////////////
// CServerCallObject
class ATL_NO_VTABLE CServerCallObject : 
	public CComObjectRootEx<CComMultiThreadModel>,
	public AsyncIAMTSEvtDisp
{
public:
	CServerCallObject():m_bCallInProgress( FALSE ),
						m_hResultBegin( E_FAIL ),
						m_hResultFinish( E_FAIL ),
						m_bstrtData( L"" ),
						m_bstrtDest( L"" )			
	{}

CComPtr<IUnknown> m_spUnkInner;	// Aggregated event object's IUnknown

DECLARE_PROTECT_FINAL_CONSTRUCT()
DECLARE_GET_CONTROLLING_UNKNOWN ()

BEGIN_COM_MAP(CServerCallObject)
	COM_INTERFACE_ENTRY(AsyncIAMTSEvtDisp)
	COM_INTERFACE_ENTRY_AUTOAGGREGATE(IID_ISynchronize, m_spUnkInner.p, CLSID_ManualResetEvent)
END_COM_MAP()

// IServerCallObject
public:

// AsyncIAMTSEvtDisp
public:
	//STDMETHOD(Begin_FireEvent)( /*[in]*/ BSTR bstrData );
	//STDMETHOD(Finish_FireEvent)();

	STDMETHOD(Begin_FireDirectedEvent)( /*[in]*/ BSTR bstrData, /*[in]*/ BSTR bstrDest );
	STDMETHOD(Finish_FireDirectedEvent)();

//variables
public:
	BOOL m_bCallInProgress; //signal whether call object busy 
	HRESULT m_hResultBegin; //status code of begin operation
	HRESULT m_hResultFinish; //status code of finish operation
	_bstr_t m_bstrtData; //store local copy of data passed in
	_bstr_t m_bstrtDest; //store local copy of destination

private:
};

#endif //__SERVERCALLOBJECT_H_
