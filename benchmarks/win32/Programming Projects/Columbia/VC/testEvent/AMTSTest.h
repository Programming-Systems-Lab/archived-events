// AMTSTest.h : Declaration of the AMTSTest

#ifndef __AMTSTEST_H_
#define __AMTSTEST_H_

#include "resource.h"       // main symbols

/////////////////////////////////////////////////////////////////////////////
// AMTSTest
class ATL_NO_VTABLE AMTSTest : 
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<AMTSTest, &CLSID_AMTSTest>,
	public ISupportErrorInfo,
	public IDispatchImpl<IAMTSTest, &IID_IAMTSTest, &LIBID_TESTEVENTLib>
{
public:
	AMTSTest()
	{
	}

DECLARE_REGISTRY_RESOURCEID(IDR_AMTSTEST)

DECLARE_PROTECT_FINAL_CONSTRUCT()

BEGIN_COM_MAP(AMTSTest)
	COM_INTERFACE_ENTRY(IAMTSTest)
	COM_INTERFACE_ENTRY(IDispatch)
	COM_INTERFACE_ENTRY(ISupportErrorInfo)
END_COM_MAP()

// ISupportsErrorInfo
	STDMETHOD(InterfaceSupportsErrorInfo)(REFIID riid);

// IAMTSTest
public:
	STDMETHOD(TestCall)(/*[in]*/ BSTR bstrData);
};

#endif //__AMTSTEST_H_
