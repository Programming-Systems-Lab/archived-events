
#pragma warning( disable: 4049 )  /* more than 64k source lines */

/* this ALWAYS GENERATED file contains the definitions for the interfaces */


 /* File created by MIDL compiler version 5.03.0280 */
/* at Fri Dec 07 01:38:05 2001
 */
/* Compiler settings for C:\Programming Projects\Columbia\VC\testSubscriber\testSubscriber.idl:
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

#ifndef __testSubscriber_h__
#define __testSubscriber_h__

/* Forward Declarations */ 

#ifndef __IAMTSTestSubscriber_FWD_DEFINED__
#define __IAMTSTestSubscriber_FWD_DEFINED__
typedef interface IAMTSTestSubscriber IAMTSTestSubscriber;
#endif 	/* __IAMTSTestSubscriber_FWD_DEFINED__ */


#ifndef __AMTSTestSubscriber_FWD_DEFINED__
#define __AMTSTestSubscriber_FWD_DEFINED__

#ifdef __cplusplus
typedef class AMTSTestSubscriber AMTSTestSubscriber;
#else
typedef struct AMTSTestSubscriber AMTSTestSubscriber;
#endif /* __cplusplus */

#endif 	/* __AMTSTestSubscriber_FWD_DEFINED__ */


/* header files for imported files */
#include "oaidl.h"
#include "ocidl.h"

#ifdef __cplusplus
extern "C"{
#endif 

void __RPC_FAR * __RPC_USER MIDL_user_allocate(size_t);
void __RPC_USER MIDL_user_free( void __RPC_FAR * ); 

#ifndef __IAMTSTestSubscriber_INTERFACE_DEFINED__
#define __IAMTSTestSubscriber_INTERFACE_DEFINED__

/* interface IAMTSTestSubscriber */
/* [unique][helpstring][dual][uuid][object] */ 


EXTERN_C const IID IID_IAMTSTestSubscriber;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("BDDC1F3B-F17A-46C6-963B-E7C7BEAAE706")
    IAMTSTestSubscriber : public IDispatch
    {
    public:
    };
    
#else 	/* C style interface */

    typedef struct IAMTSTestSubscriberVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE __RPC_FAR *QueryInterface )( 
            IAMTSTestSubscriber __RPC_FAR * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ void __RPC_FAR *__RPC_FAR *ppvObject);
        
        ULONG ( STDMETHODCALLTYPE __RPC_FAR *AddRef )( 
            IAMTSTestSubscriber __RPC_FAR * This);
        
        ULONG ( STDMETHODCALLTYPE __RPC_FAR *Release )( 
            IAMTSTestSubscriber __RPC_FAR * This);
        
        HRESULT ( STDMETHODCALLTYPE __RPC_FAR *GetTypeInfoCount )( 
            IAMTSTestSubscriber __RPC_FAR * This,
            /* [out] */ UINT __RPC_FAR *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE __RPC_FAR *GetTypeInfo )( 
            IAMTSTestSubscriber __RPC_FAR * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo __RPC_FAR *__RPC_FAR *ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE __RPC_FAR *GetIDsOfNames )( 
            IAMTSTestSubscriber __RPC_FAR * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR __RPC_FAR *rgszNames,
            /* [in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID __RPC_FAR *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE __RPC_FAR *Invoke )( 
            IAMTSTestSubscriber __RPC_FAR * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS __RPC_FAR *pDispParams,
            /* [out] */ VARIANT __RPC_FAR *pVarResult,
            /* [out] */ EXCEPINFO __RPC_FAR *pExcepInfo,
            /* [out] */ UINT __RPC_FAR *puArgErr);
        
        END_INTERFACE
    } IAMTSTestSubscriberVtbl;

    interface IAMTSTestSubscriber
    {
        CONST_VTBL struct IAMTSTestSubscriberVtbl __RPC_FAR *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IAMTSTestSubscriber_QueryInterface(This,riid,ppvObject)	\
    (This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define IAMTSTestSubscriber_AddRef(This)	\
    (This)->lpVtbl -> AddRef(This)

#define IAMTSTestSubscriber_Release(This)	\
    (This)->lpVtbl -> Release(This)


#define IAMTSTestSubscriber_GetTypeInfoCount(This,pctinfo)	\
    (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo)

#define IAMTSTestSubscriber_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo)

#define IAMTSTestSubscriber_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)

#define IAMTSTestSubscriber_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)


#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IAMTSTestSubscriber_INTERFACE_DEFINED__ */



#ifndef __TESTSUBSCRIBERLib_LIBRARY_DEFINED__
#define __TESTSUBSCRIBERLib_LIBRARY_DEFINED__

/* library TESTSUBSCRIBERLib */
/* [helpstring][version][uuid] */ 


EXTERN_C const IID LIBID_TESTSUBSCRIBERLib;

EXTERN_C const CLSID CLSID_AMTSTestSubscriber;

#ifdef __cplusplus

class DECLSPEC_UUID("1F487EC0-2E2C-4945-90DD-BF7F39CE2835")
AMTSTestSubscriber;
#endif
#endif /* __TESTSUBSCRIBERLib_LIBRARY_DEFINED__ */

/* Additional Prototypes for ALL interfaces */

/* end of Additional Prototypes */

#ifdef __cplusplus
}
#endif

#endif


