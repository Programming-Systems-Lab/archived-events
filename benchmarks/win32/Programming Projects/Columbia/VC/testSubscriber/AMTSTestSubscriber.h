// AMTSTestSubscriber.h : Declaration of the AMTSTestSubscriber

#ifndef __AMTSTESTSUBSCRIBER_H_
#define __AMTSTESTSUBSCRIBER_H_

#include "resource.h"       // main symbols
#include <mtx.h>
#include <AString.h>
#include <EvtLogger.h>
#include <time.h>
#import "C:\PROGRAMMING PROJECTS\COLUMBIA\bin\testEvent.dll" raw_interfaces_only, raw_native_types, no_namespace, named_guids 

/////////////////////////////////////////////////////////////////////////////
// AMTSTestSubscriber
class ATL_NO_VTABLE AMTSTestSubscriber : 
	public CComObjectRootEx<CComMultiThreadModel>,
	public CComCoClass<AMTSTestSubscriber, &CLSID_AMTSTestSubscriber>,
	public ISupportErrorInfo,
	public IDispatchImpl<IAMTSTestSubscriber, &IID_IAMTSTestSubscriber, &LIBID_TESTSUBSCRIBERLib>,
	public IDispatchImpl<IAMTSTest, &IID_IAMTSTest, &LIBID_TESTEVENTLib>,
	public IObjectControl
{
public:
	AMTSTestSubscriber():m_lastEventArrival( COleDateTime::GetCurrentTime() ), m_lngEventCount( 0 ), m_bFirstEvent( true ),m_EvtLogger( "AMTSTestSubscriber" )
	{

	}

DECLARE_REGISTRY_RESOURCEID(IDR_AMTSTESTSUBSCRIBER)

DECLARE_PROTECT_FINAL_CONSTRUCT()

//DECLARE_CLASSFACTORY_SINGLETON(AMTSTestSubscriber) 

BEGIN_COM_MAP(AMTSTestSubscriber)
	COM_INTERFACE_ENTRY(IAMTSTestSubscriber)
//DEL 	COM_INTERFACE_ENTRY(IDispatch)
	COM_INTERFACE_ENTRY(ISupportErrorInfo)
	COM_INTERFACE_ENTRY2(IDispatch, IAMTSTestSubscriber)
	COM_INTERFACE_ENTRY(IAMTSTest)
	COM_INTERFACE_ENTRY(IObjectControl)
END_COM_MAP()

// ISupportsErrorInfo
	STDMETHOD(InterfaceSupportsErrorInfo)(REFIID riid);

// IObjectControl
public:
	STDMETHOD(Activate)();
	STDMETHOD_(BOOL, CanBePooled)();
	STDMETHOD_(void, Deactivate)();

	CComPtr<IObjectContext> m_spObjectContext;


// IAMTSTestSubscriber
public:
		
// IAMTSTest
	STDMETHOD(TestCall)(BSTR bstrData);

private:
	COleDateTime m_lastEventArrival;
	long m_lngEventCount;
	bool m_bFirstEvent;
	EvtLogger m_EvtLogger;
	clock_t m_start;
};

#endif //__AMTSTESTSUBSCRIBER_H_
