// AMTSEvtDisp.h : Declaration of the AMTSEvtDisp

#ifndef __AMTSEVTDISP_H_
#define __AMTSEVTDISP_H_

#include "resource.h"       // main symbols

/////////////////////////////////////////////////////////////////////////////
// AMTSEvtDisp
class ATL_NO_VTABLE AMTSEvtDisp : 
	public CComObjectRootEx<CComMultiThreadModel>,
	public CComCoClass<AMTSEvtDisp, &CLSID_AMTSEvtDisp>,
	public ISupportErrorInfo,
	public IAMTSEvtDisp,
	public ICallFactory //implemement ICallFactory interface
{
public:
	AMTSEvtDisp()
	{
	}

DECLARE_REGISTRY_RESOURCEID(IDR_AMTSEVTDISP)

DECLARE_PROTECT_FINAL_CONSTRUCT()

BEGIN_COM_MAP(AMTSEvtDisp)
	COM_INTERFACE_ENTRY(IAMTSEvtDisp)
	COM_INTERFACE_ENTRY(ISupportErrorInfo)
	COM_INTERFACE_ENTRY(ICallFactory)
END_COM_MAP()

// ISupportsErrorInfo
	STDMETHOD(InterfaceSupportsErrorInfo)(REFIID riid);

// IAMTSEvtDisp
public:
	STDMETHOD(FireDirectedEvent)(/*[in]*/ BSTR bstrData, /*[in]*/ BSTR bstrDest );

// ICallFactory
	STDMETHOD(CreateCall)( REFIID riid1, 
							IUnknown* pUnk, 
							REFIID riid2,
							IUnknown** ppv );
};

#endif //__AMTSEVTDISP_H_
