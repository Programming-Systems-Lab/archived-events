
#pragma warning( disable: 4049 )  /* more than 64k source lines */

/* this ALWAYS GENERATED file contains the definitions for the interfaces */


 /* File created by MIDL compiler version 5.03.0280 */
/* at Fri Dec 07 00:27:52 2001
 */
/* Compiler settings for C:\Programming Projects\Columbia\VC\testEvent\testEvent.idl:
    Oicf (OptLev=i2), W1, Zp8, env=Win32 (32b run), ms_ext, c_ext
    error checks: allocation ref bounds_check enum stub_data 
    VC __declspec() decoration level: 
         __declspec(uuid()), __declspec(selectany), __declspec(novtable)
         DECLSPEC_UUID(), MIDL_INTERFACE()
*/
//@@MIDL_FILE_HEADING(  )


/* verify that the <rpcndr.h> version is high enough to compile this file*/
#ifndef __REQUIRED_RPCNDR_H_VERSION__
#define __REQUIRED_RPCNDR_H_VERSION__ 440
#endif

#include "rpc.h"
#include "rpcndr.h"

#ifndef __RPCNDR_H_VERSION__
#error this stub requires an updated version of <rpcndr.h>
#endif // __RPCNDR_H_VERSION__

#ifndef COM_NO_WINDOWS_H
#include "windows.h"
#include "ole2.h"
#endif /*COM_NO_WINDOWS_H*/

#ifndef __testEvent_h__
#define __testEvent_h__

/* Forward Declarations */ 

#ifndef __IAMTSTest_FWD_DEFINED__
#define __IAMTSTest_FWD_DEFINED__
typedef interface IAMTSTest IAMTSTest;
#endif 	/* __IAMTSTest_FWD_DEFINED__ */


#ifndef __AMTSTest_FWD_DEFINED__
#define __AMTSTest_FWD_DEFINED__

#ifdef __cplusplus
typedef class AMTSTest AMTSTest;
#else
typedef struct AMTSTest AMTSTest;
#endif /* __cplusplus */

#endif 	/* __AMTSTest_FWD_DEFINED__ */


/* header files for imported files */
#include "oaidl.h"
#include "ocidl.h"

#ifdef __cplusplus
extern "C"{
#endif 

void __RPC_FAR * __RPC_USER MIDL_user_allocate(size_t);
void __RPC_USER MIDL_user_free( void __RPC_FAR * ); 

#ifndef __IAMTSTest_INTERFACE_DEFINED__
#define __IAMTSTest_INTERFACE_DEFINED__

/* interface IAMTSTest */
/* [unique][helpstring][dual][uuid][object] */ 


EXTERN_C const IID IID_IAMTSTest;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("44091FF9-CB08-4DD6-943B-093E7F339794")
    IAMTSTest : public IDispatch
    {
    public:
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE TestCall( 
            /* [in] */ BSTR bstrData) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IAMTSTestVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE __RPC_FAR *QueryInterface )( 
            IAMTSTest __RPC_FAR * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ void __RPC_FAR *__RPC_FAR *ppvObject);
        
        ULONG ( STDMETHODCALLTYPE __RPC_FAR *AddRef )( 
            IAMTSTest __RPC_FAR * This);
        
        ULONG ( STDMETHODCALLTYPE __RPC_FAR *Release )( 
            IAMTSTest __RPC_FAR * This);
        
        HRESULT ( STDMETHODCALLTYPE __RPC_FAR *GetTypeInfoCount )( 
            IAMTSTest __RPC_FAR * This,
            /* [out] */ UINT __RPC_FAR *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE __RPC_FAR *GetTypeInfo )( 
            IAMTSTest __RPC_FAR * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo __RPC_FAR *__RPC_FAR *ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE __RPC_FAR *GetIDsOfNames )( 
            IAMTSTest __RPC_FAR * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR __RPC_FAR *rgszNames,
            /* [in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID __RPC_FAR *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE __RPC_FAR *Invoke )( 
            IAMTSTest __RPC_FAR * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS __RPC_FAR *pDispParams,
            /* [out] */ VARIANT __RPC_FAR *pVarResult,
            /* [out] */ EXCEPINFO __RPC_FAR *pExcepInfo,
            /* [out] */ UINT __RPC_FAR *puArgErr);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE __RPC_FAR *TestCall )( 
            IAMTSTest __RPC_FAR * This,
            /* [in] */ BSTR bstrData);
        
        END_INTERFACE
    } IAMTSTestVtbl;

    interface IAMTSTest
    {
        CONST_VTBL struct IAMTSTestVtbl __RPC_FAR *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IAMTSTest_QueryInterface(This,riid,ppvObject)	\
    (This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define IAMTSTest_AddRef(This)	\
    (This)->lpVtbl -> AddRef(This)

#define IAMTSTest_Release(This)	\
    (This)->lpVtbl -> Release(This)


#define IAMTSTest_GetTypeInfoCount(This,pctinfo)	\
    (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo)

#define IAMTSTest_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo)

#define IAMTSTest_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)

#define IAMTSTest_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)


#define IAMTSTest_TestCall(This,bstrData)	\
    (This)->lpVtbl -> TestCall(This,bstrData)

#endif /* COBJMACROS */


#endif 	/* C style interface */



/* [helpstring][id] */ HRESULT STDMETHODCALLTYPE IAMTSTest_TestCall_Proxy( 
    IAMTSTest __RPC_FAR * This,
    /* [in] */ BSTR bstrData);


void __RPC_STUB IAMTSTest_TestCall_Stub(
    IRpcStubBuffer *This,
    IRpcChannelBuffer *_pRpcChannelBuffer,
    PRPC_MESSAGE _pRpcMessage,
    DWORD *_pdwStubPhase);



#endif 	/* __IAMTSTest_INTERFACE_DEFINED__ */



#ifndef __TESTEVENTLib_LIBRARY_DEFINED__
#define __TESTEVENTLib_LIBRARY_DEFINED__

/* library TESTEVENTLib */
/* [helpstring][version][uuid] */ 


EXTERN_C const IID LIBID_TESTEVENTLib;

EXTERN_C const CLSID CLSID_AMTSTest;

#ifdef __cplusplus

class DECLSPEC_UUID("0D0C9FAC-76AF-4723-8CF1-58D29E7DE3FE")
AMTSTest;
#endif
#endif /* __TESTEVENTLib_LIBRARY_DEFINED__ */

/* Additional Prototypes for ALL interfaces */

unsigned long             __RPC_USER  BSTR_UserSize(     unsigned long __RPC_FAR *, unsigned long            , BSTR __RPC_FAR * ); 
unsigned char __RPC_FAR * __RPC_USER  BSTR_UserMarshal(  unsigned long __RPC_FAR *, unsigned char __RPC_FAR *, BSTR __RPC_FAR * ); 
unsigned char __RPC_FAR * __RPC_USER  BSTR_UserUnmarshal(unsigned long __RPC_FAR *, unsigned char __RPC_FAR *, BSTR __RPC_FAR * ); 
void                      __RPC_USER  BSTR_UserFree(     unsigned long __RPC_FAR *, BSTR __RPC_FAR * ); 

/* end of Additional Prototypes */

#ifdef __cplusplus
}
#endif

#endif


