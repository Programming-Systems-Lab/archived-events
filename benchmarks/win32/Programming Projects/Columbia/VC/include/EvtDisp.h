
#pragma warning( disable: 4049 )  /* more than 64k source lines */

/* this ALWAYS GENERATED file contains the definitions for the interfaces */


 /* File created by MIDL compiler version 5.03.0280 */
/* at Fri Dec 07 01:27:20 2001
 */
/* Compiler settings for C:\Programming Projects\Columbia\VC\EvtDisp\EvtDisp.idl:
    Oicf (OptLev=i2), W1, Zp8, env=Win32 (32b run), ms_ext, c_ext
    error checks: allocation ref bounds_check enum stub_data 
    VC __declspec() decoration level: 
         __declspec(uuid()), __declspec(selectany), __declspec(novtable)
         DECLSPEC_UUID(), MIDL_INTERFACE()
*/
//@@MIDL_FILE_HEADING(  )


/* verify that the <rpcndr.h> version is high enough to compile this file*/
#ifndef __REQUIRED_RPCNDR_H_VERSION__
#define __REQUIRED_RPCNDR_H_VERSION__ 475
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

#ifndef __EvtDisp_h__
#define __EvtDisp_h__

/* Forward Declarations */ 

#ifndef __IAMTSEvtDisp_FWD_DEFINED__
#define __IAMTSEvtDisp_FWD_DEFINED__
typedef interface IAMTSEvtDisp IAMTSEvtDisp;
#endif 	/* __IAMTSEvtDisp_FWD_DEFINED__ */


#ifndef __AsyncIAMTSEvtDisp_FWD_DEFINED__
#define __AsyncIAMTSEvtDisp_FWD_DEFINED__
typedef interface AsyncIAMTSEvtDisp AsyncIAMTSEvtDisp;
#endif 	/* __AsyncIAMTSEvtDisp_FWD_DEFINED__ */


#ifndef __AMTSEvtDisp_FWD_DEFINED__
#define __AMTSEvtDisp_FWD_DEFINED__

#ifdef __cplusplus
typedef class AMTSEvtDisp AMTSEvtDisp;
#else
typedef struct AMTSEvtDisp AMTSEvtDisp;
#endif /* __cplusplus */

#endif 	/* __AMTSEvtDisp_FWD_DEFINED__ */


/* header files for imported files */
#include "oaidl.h"
#include "ocidl.h"

#ifdef __cplusplus
extern "C"{
#endif 

void __RPC_FAR * __RPC_USER MIDL_user_allocate(size_t);
void __RPC_USER MIDL_user_free( void __RPC_FAR * ); 

#ifndef __IAMTSEvtDisp_INTERFACE_DEFINED__
#define __IAMTSEvtDisp_INTERFACE_DEFINED__

/* interface IAMTSEvtDisp */
/* [unique][helpstring][async_uuid][uuid][object] */ 


EXTERN_C const IID IID_IAMTSEvtDisp;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("D0A4B74F-CCB1-4E73-898B-E6F2FA9F7652")
    IAMTSEvtDisp : public IUnknown
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE FireDirectedEvent( 
            /* [in] */ BSTR bstrData,
            /* [in] */ BSTR bstrDest) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IAMTSEvtDispVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE __RPC_FAR *QueryInterface )( 
            IAMTSEvtDisp __RPC_FAR * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ void __RPC_FAR *__RPC_FAR *ppvObject);
        
        ULONG ( STDMETHODCALLTYPE __RPC_FAR *AddRef )( 
            IAMTSEvtDisp __RPC_FAR * This);
        
        ULONG ( STDMETHODCALLTYPE __RPC_FAR *Release )( 
            IAMTSEvtDisp __RPC_FAR * This);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE __RPC_FAR *FireDirectedEvent )( 
            IAMTSEvtDisp __RPC_FAR * This,
            /* [in] */ BSTR bstrData,
            /* [in] */ BSTR bstrDest);
        
        END_INTERFACE
    } IAMTSEvtDispVtbl;

    interface IAMTSEvtDisp
    {
        CONST_VTBL struct IAMTSEvtDispVtbl __RPC_FAR *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IAMTSEvtDisp_QueryInterface(This,riid,ppvObject)	\
    (This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define IAMTSEvtDisp_AddRef(This)	\
    (This)->lpVtbl -> AddRef(This)

#define IAMTSEvtDisp_Release(This)	\
    (This)->lpVtbl -> Release(This)


#define IAMTSEvtDisp_FireDirectedEvent(This,bstrData,bstrDest)	\
    (This)->lpVtbl -> FireDirectedEvent(This,bstrData,bstrDest)

#endif /* COBJMACROS */


#endif 	/* C style interface */



/* [helpstring] */ HRESULT STDMETHODCALLTYPE IAMTSEvtDisp_FireDirectedEvent_Proxy( 
    IAMTSEvtDisp __RPC_FAR * This,
    /* [in] */ BSTR bstrData,
    /* [in] */ BSTR bstrDest);


void __RPC_STUB IAMTSEvtDisp_FireDirectedEvent_Stub(
    IRpcStubBuffer *This,
    IRpcChannelBuffer *_pRpcChannelBuffer,
    PRPC_MESSAGE _pRpcMessage,
    DWORD *_pdwStubPhase);



#endif 	/* __IAMTSEvtDisp_INTERFACE_DEFINED__ */


#ifndef __AsyncIAMTSEvtDisp_INTERFACE_DEFINED__
#define __AsyncIAMTSEvtDisp_INTERFACE_DEFINED__

/* interface AsyncIAMTSEvtDisp */
/* [uuid][unique][helpstring][object] */ 


EXTERN_C const IID IID_AsyncIAMTSEvtDisp;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("CC8F5CC9-3CB4-456e-AA1B-9CDFE09A8836")
    AsyncIAMTSEvtDisp : public IUnknown
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Begin_FireDirectedEvent( 
            /* [in] */ BSTR bstrData,
            /* [in] */ BSTR bstrDest) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Finish_FireDirectedEvent( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct AsyncIAMTSEvtDispVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE __RPC_FAR *QueryInterface )( 
            AsyncIAMTSEvtDisp __RPC_FAR * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ void __RPC_FAR *__RPC_FAR *ppvObject);
        
        ULONG ( STDMETHODCALLTYPE __RPC_FAR *AddRef )( 
            AsyncIAMTSEvtDisp __RPC_FAR * This);
        
        ULONG ( STDMETHODCALLTYPE __RPC_FAR *Release )( 
            AsyncIAMTSEvtDisp __RPC_FAR * This);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE __RPC_FAR *Begin_FireDirectedEvent )( 
            AsyncIAMTSEvtDisp __RPC_FAR * This,
            /* [in] */ BSTR bstrData,
            /* [in] */ BSTR bstrDest);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE __RPC_FAR *Finish_FireDirectedEvent )( 
            AsyncIAMTSEvtDisp __RPC_FAR * This);
        
        END_INTERFACE
    } AsyncIAMTSEvtDispVtbl;

    interface AsyncIAMTSEvtDisp
    {
        CONST_VTBL struct AsyncIAMTSEvtDispVtbl __RPC_FAR *lpVtbl;
    };

    

#ifdef COBJMACROS


#define AsyncIAMTSEvtDisp_QueryInterface(This,riid,ppvObject)	\
    (This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define AsyncIAMTSEvtDisp_AddRef(This)	\
    (This)->lpVtbl -> AddRef(This)

#define AsyncIAMTSEvtDisp_Release(This)	\
    (This)->lpVtbl -> Release(This)


#define AsyncIAMTSEvtDisp_Begin_FireDirectedEvent(This,bstrData,bstrDest)	\
    (This)->lpVtbl -> Begin_FireDirectedEvent(This,bstrData,bstrDest)

#define AsyncIAMTSEvtDisp_Finish_FireDirectedEvent(This)	\
    (This)->lpVtbl -> Finish_FireDirectedEvent(This)

#endif /* COBJMACROS */


#endif 	/* C style interface */



/* [helpstring] */ HRESULT STDMETHODCALLTYPE AsyncIAMTSEvtDisp_Begin_FireDirectedEvent_Proxy( 
    AsyncIAMTSEvtDisp __RPC_FAR * This,
    /* [in] */ BSTR bstrData,
    /* [in] */ BSTR bstrDest);


void __RPC_STUB AsyncIAMTSEvtDisp_Begin_FireDirectedEvent_Stub(
    IRpcStubBuffer *This,
    IRpcChannelBuffer *_pRpcChannelBuffer,
    PRPC_MESSAGE _pRpcMessage,
    DWORD *_pdwStubPhase);


/* [helpstring] */ HRESULT STDMETHODCALLTYPE AsyncIAMTSEvtDisp_Finish_FireDirectedEvent_Proxy( 
    AsyncIAMTSEvtDisp __RPC_FAR * This);


void __RPC_STUB AsyncIAMTSEvtDisp_Finish_FireDirectedEvent_Stub(
    IRpcStubBuffer *This,
    IRpcChannelBuffer *_pRpcChannelBuffer,
    PRPC_MESSAGE _pRpcMessage,
    DWORD *_pdwStubPhase);



#endif 	/* __AsyncIAMTSEvtDisp_INTERFACE_DEFINED__ */



#ifndef __EVTDISPLib_LIBRARY_DEFINED__
#define __EVTDISPLib_LIBRARY_DEFINED__

/* library EVTDISPLib */
/* [helpstring][version][uuid] */ 


EXTERN_C const IID LIBID_EVTDISPLib;

EXTERN_C const CLSID CLSID_AMTSEvtDisp;

#ifdef __cplusplus

class DECLSPEC_UUID("F692437D-8029-452E-A60B-3D1490C62960")
AMTSEvtDisp;
#endif
#endif /* __EVTDISPLib_LIBRARY_DEFINED__ */

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


