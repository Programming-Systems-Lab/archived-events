// AMTSTest.cpp : Implementation of AMTSTest
#include "stdafx.h"
#include "TestEvent.h"
#include "AMTSTest.h"

/////////////////////////////////////////////////////////////////////////////
// AMTSTest

STDMETHODIMP AMTSTest::InterfaceSupportsErrorInfo(REFIID riid)
{
	static const IID* arr[] = 
	{
		&IID_IAMTSTest
	};
	for (int i=0; i < sizeof(arr) / sizeof(arr[0]); i++)
	{
		if (::InlineIsEqualGUID(*arr[i],riid))
			return S_OK;
	}
	return S_FALSE;
}

STDMETHODIMP AMTSTest::TestCall(BSTR bstrData)
{
	AFX_MANAGE_STATE(AfxGetStaticModuleState())

	// TODO: Add your implementation code here

	return S_OK;
}
