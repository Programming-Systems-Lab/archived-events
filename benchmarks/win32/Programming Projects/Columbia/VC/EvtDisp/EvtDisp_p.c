
#pragma warning( disable: 4049 )  /* more than 64k source lines */

/* this ALWAYS GENERATED file contains the proxy stub code */


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

#if !defined(_M_IA64) && !defined(_M_AXP64)
#define USE_STUBLESS_PROXY


/* verify that the <rpcproxy.h> version is high enough to compile this file*/
#ifndef __REDQ_RPCPROXY_H_VERSION__
#define __REQUIRED_RPCPROXY_H_VERSION__ 475
#endif


#include "rpcproxy.h"
#ifndef __RPCPROXY_H_VERSION__
#error this stub requires an updated version of <rpcproxy.h>
#endif // __RPCPROXY_H_VERSION__


#include "EvtDisp.h"

#define TYPE_FORMAT_STRING_SIZE   37                                
#define PROC_FORMAT_STRING_SIZE   91                                
#define TRANSMIT_AS_TABLE_SIZE    0            
#define WIRE_MARSHAL_TABLE_SIZE   1            

typedef struct _MIDL_TYPE_FORMAT_STRING
    {
    short          Pad;
    unsigned char  Format[ TYPE_FORMAT_STRING_SIZE ];
    } MIDL_TYPE_FORMAT_STRING;

typedef struct _MIDL_PROC_FORMAT_STRING
    {
    short          Pad;
    unsigned char  Format[ PROC_FORMAT_STRING_SIZE ];
    } MIDL_PROC_FORMAT_STRING;


extern const MIDL_TYPE_FORMAT_STRING __MIDL_TypeFormatString;
extern const MIDL_PROC_FORMAT_STRING __MIDL_ProcFormatString;


/* Object interface: IUnknown, ver. 0.0,
   GUID={0x00000000,0x0000,0x0000,{0xC0,0x00,0x00,0x00,0x00,0x00,0x00,0x46}} */


/* Object interface: IAMTSEvtDisp, ver. 0.0,
   GUID={0xD0A4B74F,0xCCB1,0x4E73,{0x89,0x8B,0xE6,0xF2,0xFA,0x9F,0x76,0x52}} */


extern const MIDL_STUB_DESC Object_StubDesc;


extern const MIDL_SERVER_INFO IAMTSEvtDisp_ServerInfo;

#pragma code_seg(".orpc")
static const unsigned short IAMTSEvtDisp_FormatStringOffsetTable[] = 
    {
    0
    };

static const MIDL_SERVER_INFO IAMTSEvtDisp_ServerInfo = 
    {
    &Object_StubDesc,
    0,
    __MIDL_ProcFormatString.Format,
    &IAMTSEvtDisp_FormatStringOffsetTable[-3],
    0,
    0,
    0,
    0
    };

static const MIDL_STUBLESS_PROXY_INFO IAMTSEvtDisp_ProxyInfo =
    {
    &Object_StubDesc,
    __MIDL_ProcFormatString.Format,
    &IAMTSEvtDisp_FormatStringOffsetTable[-3],
    0,
    0,
    0
    };

CINTERFACE_PROXY_VTABLE(4) _IAMTSEvtDispProxyVtbl = 
{
    &IAMTSEvtDisp_ProxyInfo,
    &IID_IAMTSEvtDisp,
    IUnknown_QueryInterface_Proxy,
    IUnknown_AddRef_Proxy,
    IUnknown_Release_Proxy ,
    (void *)-1 /* IAMTSEvtDisp::FireDirectedEvent */
};

const CInterfaceStubVtbl _IAMTSEvtDispStubVtbl =
{
    &IID_IAMTSEvtDisp,
    &IAMTSEvtDisp_ServerInfo,
    4,
    0, /* pure interpreted */
    CStdStubBuffer_METHODS
};


/* Object interface: AsyncIAMTSEvtDisp, ver. 0.0,
   GUID={0xCC8F5CC9,0x3CB4,0x456e,{0xAA,0x1B,0x9C,0xDF,0xE0,0x9A,0x88,0x36}} */


extern const MIDL_STUB_DESC Object_StubDesc;


extern const MIDL_SERVER_INFO AsyncIAMTSEvtDisp_ServerInfo;

#pragma code_seg(".orpc")
static const unsigned short AsyncIAMTSEvtDisp_FormatStringOffsetTable[] = 
    {
    34,
    68
    };

static const MIDL_SERVER_INFO AsyncIAMTSEvtDisp_ServerInfo = 
    {
    &Object_StubDesc,
    0,
    __MIDL_ProcFormatString.Format,
    &AsyncIAMTSEvtDisp_FormatStringOffsetTable[-3],
    0,
    0,
    0,
    0
    };

static const MIDL_STUBLESS_PROXY_INFO AsyncIAMTSEvtDisp_ProxyInfo =
    {
    &Object_StubDesc,
    __MIDL_ProcFormatString.Format,
    &AsyncIAMTSEvtDisp_FormatStringOffsetTable[-3],
    0,
    0,
    0
    };

CINTERFACE_PROXY_VTABLE(5) _AsyncIAMTSEvtDispProxyVtbl = 
{
    &AsyncIAMTSEvtDisp_ProxyInfo,
    &IID_AsyncIAMTSEvtDisp,
    IUnknown_QueryInterface_Proxy,
    IUnknown_AddRef_Proxy,
    IUnknown_Release_Proxy ,
    (void *)-1 /* AsyncIAMTSEvtDisp::Begin_FireDirectedEvent */ ,
    (void *)-1 /* AsyncIAMTSEvtDisp::Finish_FireDirectedEvent */
};

CInterfaceStubVtbl _AsyncIAMTSEvtDispStubVtbl =
{
    &IID_AsyncIAMTSEvtDisp,
    &AsyncIAMTSEvtDisp_ServerInfo,
    5,
    0, /* pure interpreted */
    CStdAsyncStubBuffer_METHODS
};

extern const USER_MARSHAL_ROUTINE_QUADRUPLE UserMarshalRoutines[ WIRE_MARSHAL_TABLE_SIZE ];

static const MIDL_STUB_DESC Object_StubDesc = 
    {
    0,
    NdrOleAllocate,
    NdrOleFree,
    0,
    0,
    0,
    0,
    0,
    __MIDL_TypeFormatString.Format,
    1, /* -error bounds_check flag */
    0x50002, /* Ndr library version */
    0,
    0x5030118, /* MIDL Version 5.3.280 */
    0,
    UserMarshalRoutines,
    0,  /* notify & notify_flag routine table */
    0x1, /* MIDL flag */
    0,  /* Reserved3 */
    0,  /* Reserved4 */
    0   /* Reserved5 */
    };

#pragma data_seg(".rdata")

static const USER_MARSHAL_ROUTINE_QUADRUPLE UserMarshalRoutines[ WIRE_MARSHAL_TABLE_SIZE ] = 
        {
            
            {
            BSTR_UserSize
            ,BSTR_UserMarshal
            ,BSTR_UserUnmarshal
            ,BSTR_UserFree
            }

        };


#if !defined(__RPC_WIN32__)
#error  Invalid build platform for this stub.
#endif

#if !(TARGET_IS_NT50_OR_LATER)
#error You need a Windows 2000 Professional or later to run this stub because it uses these features:
#error   [async_uuid] attribute.
#error However, your C/C++ compilation flags indicate you intend to run this app on earlier systems.
#error This app will die there with the RPC_X_WRONG_STUB_VERSION error.
#endif


static const MIDL_PROC_FORMAT_STRING __MIDL_ProcFormatString =
    {
        0,
        {

	/* Procedure FireDirectedEvent */

			0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/*  2 */	NdrFcLong( 0x0 ),	/* 0 */
/*  6 */	NdrFcShort( 0x3 ),	/* 3 */
#ifndef _ALPHA_
/*  8 */	NdrFcShort( 0x10 ),	/* x86, MIPS, PPC Stack size/offset = 16 */
#else
			NdrFcShort( 0x20 ),	/* Alpha Stack size/offset = 32 */
#endif
/* 10 */	NdrFcShort( 0x0 ),	/* 0 */
/* 12 */	NdrFcShort( 0x8 ),	/* 8 */
/* 14 */	0x6,		/* Oi2 Flags:  clt must size, has return, */
			0x3,		/* 3 */

	/* Parameter bstrData */

/* 16 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
#ifndef _ALPHA_
/* 18 */	NdrFcShort( 0x4 ),	/* x86, MIPS, PPC Stack size/offset = 4 */
#else
			NdrFcShort( 0x8 ),	/* Alpha Stack size/offset = 8 */
#endif
/* 20 */	NdrFcShort( 0x1a ),	/* Type Offset=26 */

	/* Parameter bstrDest */

/* 22 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
#ifndef _ALPHA_
/* 24 */	NdrFcShort( 0x8 ),	/* x86, MIPS, PPC Stack size/offset = 8 */
#else
			NdrFcShort( 0x10 ),	/* Alpha Stack size/offset = 16 */
#endif
/* 26 */	NdrFcShort( 0x1a ),	/* Type Offset=26 */

	/* Return value */

/* 28 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
#ifndef _ALPHA_
/* 30 */	NdrFcShort( 0xc ),	/* x86, MIPS, PPC Stack size/offset = 12 */
#else
			NdrFcShort( 0x18 ),	/* Alpha Stack size/offset = 24 */
#endif
/* 32 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Begin_FireDirectedEvent */

/* 34 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 36 */	NdrFcLong( 0x0 ),	/* 0 */
/* 40 */	NdrFcShort( 0x3 ),	/* 3 */
#ifndef _ALPHA_
/* 42 */	NdrFcShort( 0x10 ),	/* x86, MIPS, PPC Stack size/offset = 16 */
#else
			NdrFcShort( 0x20 ),	/* Alpha Stack size/offset = 32 */
#endif
/* 44 */	NdrFcShort( 0x0 ),	/* 0 */
/* 46 */	NdrFcShort( 0x8 ),	/* 8 */
/* 48 */	0x26,		/* Oi2 Flags:  clt must size, has return, has async uuid, */
			0x3,		/* 3 */

	/* Parameter bstrData */

/* 50 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
#ifndef _ALPHA_
/* 52 */	NdrFcShort( 0x4 ),	/* x86, MIPS, PPC Stack size/offset = 4 */
#else
			NdrFcShort( 0x8 ),	/* Alpha Stack size/offset = 8 */
#endif
/* 54 */	NdrFcShort( 0x1a ),	/* Type Offset=26 */

	/* Parameter bstrDest */

/* 56 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
#ifndef _ALPHA_
/* 58 */	NdrFcShort( 0x8 ),	/* x86, MIPS, PPC Stack size/offset = 8 */
#else
			NdrFcShort( 0x10 ),	/* Alpha Stack size/offset = 16 */
#endif
/* 60 */	NdrFcShort( 0x1a ),	/* Type Offset=26 */

	/* Return value */

/* 62 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
#ifndef _ALPHA_
/* 64 */	NdrFcShort( 0xc ),	/* x86, MIPS, PPC Stack size/offset = 12 */
#else
			NdrFcShort( 0x18 ),	/* Alpha Stack size/offset = 24 */
#endif
/* 66 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Finish_FireDirectedEvent */

/* 68 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 70 */	NdrFcLong( 0x0 ),	/* 0 */
/* 74 */	NdrFcShort( 0x4 ),	/* 4 */
#ifndef _ALPHA_
/* 76 */	NdrFcShort( 0x8 ),	/* x86, MIPS, PPC Stack size/offset = 8 */
#else
			NdrFcShort( 0x10 ),	/* Alpha Stack size/offset = 16 */
#endif
/* 78 */	NdrFcShort( 0x0 ),	/* 0 */
/* 80 */	NdrFcShort( 0x8 ),	/* 8 */
/* 82 */	0x24,		/* Oi2 Flags:  has return, has async uuid, */
			0x1,		/* 1 */

	/* Return value */

/* 84 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
#ifndef _ALPHA_
/* 86 */	NdrFcShort( 0x4 ),	/* x86, MIPS, PPC Stack size/offset = 4 */
#else
			NdrFcShort( 0x8 ),	/* Alpha Stack size/offset = 8 */
#endif
/* 88 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

			0x0
        }
    };

static const MIDL_TYPE_FORMAT_STRING __MIDL_TypeFormatString =
    {
        0,
        {
			NdrFcShort( 0x0 ),	/* 0 */
/*  2 */	
			0x12, 0x0,	/* FC_UP */
/*  4 */	NdrFcShort( 0xc ),	/* Offset= 12 (16) */
/*  6 */	
			0x1b,		/* FC_CARRAY */
			0x1,		/* 1 */
/*  8 */	NdrFcShort( 0x2 ),	/* 2 */
/* 10 */	0x9,		/* Corr desc: FC_ULONG */
			0x0,		/*  */
/* 12 */	NdrFcShort( 0xfffc ),	/* -4 */
/* 14 */	0x6,		/* FC_SHORT */
			0x5b,		/* FC_END */
/* 16 */	
			0x17,		/* FC_CSTRUCT */
			0x3,		/* 3 */
/* 18 */	NdrFcShort( 0x8 ),	/* 8 */
/* 20 */	NdrFcShort( 0xfffffff2 ),	/* Offset= -14 (6) */
/* 22 */	0x8,		/* FC_LONG */
			0x8,		/* FC_LONG */
/* 24 */	0x5c,		/* FC_PAD */
			0x5b,		/* FC_END */
/* 26 */	0xb4,		/* FC_USER_MARSHAL */
			0x83,		/* 131 */
/* 28 */	NdrFcShort( 0x0 ),	/* 0 */
/* 30 */	NdrFcShort( 0x4 ),	/* 4 */
/* 32 */	NdrFcShort( 0x0 ),	/* 0 */
/* 34 */	NdrFcShort( 0xffffffe0 ),	/* Offset= -32 (2) */

			0x0
        }
    };

const CInterfaceProxyVtbl * _EvtDisp_ProxyVtblList[] = 
{
    ( CInterfaceProxyVtbl *) &_IAMTSEvtDispProxyVtbl,
    ( CInterfaceProxyVtbl *) &_AsyncIAMTSEvtDispProxyVtbl,
    0
};

const CInterfaceStubVtbl * _EvtDisp_StubVtblList[] = 
{
    ( CInterfaceStubVtbl *) &_IAMTSEvtDispStubVtbl,
    ( CInterfaceStubVtbl *) &_AsyncIAMTSEvtDispStubVtbl,
    0
};

PCInterfaceName const _EvtDisp_InterfaceNamesList[] = 
{
    "IAMTSEvtDisp",
    "AsyncIAMTSEvtDisp",
    0
};


#define _EvtDisp_CHECK_IID(n)	IID_GENERIC_CHECK_IID( _EvtDisp, pIID, n)

int __stdcall _EvtDisp_IID_Lookup( const IID * pIID, int * pIndex )
{
    IID_BS_LOOKUP_SETUP

    IID_BS_LOOKUP_INITIAL_TEST( _EvtDisp, 2, 1 )
    IID_BS_LOOKUP_RETURN_RESULT( _EvtDisp, 2, *pIndex )
    
}

static const IID * _AsyncInterfaceTable[] = 
{
    (IID*) &IID_AsyncIAMTSEvtDisp,
    (IID*) -1,
    (IID*) 0
};

const ExtendedProxyFileInfo EvtDisp_ProxyFileInfo = 
{
    (PCInterfaceProxyVtblList *) & _EvtDisp_ProxyVtblList,
    (PCInterfaceStubVtblList *) & _EvtDisp_StubVtblList,
    (const PCInterfaceName * ) & _EvtDisp_InterfaceNamesList,
    0, // no delegation
    & _EvtDisp_IID_Lookup, 
    2,
    6,
    (const IID**) &_AsyncInterfaceTable[0], /* table of [async_uuid] interfaces */
    0, /* Filler1 */
    0, /* Filler2 */
    0  /* Filler3 */
};


#endif /* !defined(_M_IA64) && !defined(_M_AXP64)*/


#pragma warning( disable: 4049 )  /* more than 64k source lines */

/* this ALWAYS GENERATED file contains the proxy stub code */


 /* File created by MIDL compiler version 5.03.0280 */
/* at Fri Dec 07 01:27:20 2001
 */
/* Compiler settings for C:\Programming Projects\Columbia\VC\EvtDisp\EvtDisp.idl:
    Oicf (OptLev=i2), W1, Zp8, env=Win64 (32b run,appending), ms_ext, c_ext, robust
    error checks: allocation ref bounds_check enum stub_data 
    VC __declspec() decoration level: 
         __declspec(uuid()), __declspec(selectany), __declspec(novtable)
         DECLSPEC_UUID(), MIDL_INTERFACE()
*/
//@@MIDL_FILE_HEADING(  )

#if defined(_M_IA64) || defined(_M_AXP64)
#define USE_STUBLESS_PROXY


/* verify that the <rpcproxy.h> version is high enough to compile this file*/
#ifndef __REDQ_RPCPROXY_H_VERSION__
#define __REQUIRED_RPCPROXY_H_VERSION__ 475
#endif


#include "rpcproxy.h"
#ifndef __RPCPROXY_H_VERSION__
#error this stub requires an updated version of <rpcproxy.h>
#endif // __RPCPROXY_H_VERSION__


#include "EvtDisp.h"

#define TYPE_FORMAT_STRING_SIZE   39                                
#define PROC_FORMAT_STRING_SIZE   121                               
#define TRANSMIT_AS_TABLE_SIZE    0            
#define WIRE_MARSHAL_TABLE_SIZE   1            

typedef struct _MIDL_TYPE_FORMAT_STRING
    {
    short          Pad;
    unsigned char  Format[ TYPE_FORMAT_STRING_SIZE ];
    } MIDL_TYPE_FORMAT_STRING;

typedef struct _MIDL_PROC_FORMAT_STRING
    {
    short          Pad;
    unsigned char  Format[ PROC_FORMAT_STRING_SIZE ];
    } MIDL_PROC_FORMAT_STRING;


extern const MIDL_TYPE_FORMAT_STRING __MIDL_TypeFormatString;
extern const MIDL_PROC_FORMAT_STRING __MIDL_ProcFormatString;


/* Object interface: IUnknown, ver. 0.0,
   GUID={0x00000000,0x0000,0x0000,{0xC0,0x00,0x00,0x00,0x00,0x00,0x00,0x46}} */


/* Object interface: IAMTSEvtDisp, ver. 0.0,
   GUID={0xD0A4B74F,0xCCB1,0x4E73,{0x89,0x8B,0xE6,0xF2,0xFA,0x9F,0x76,0x52}} */


extern const MIDL_STUB_DESC Object_StubDesc;


extern const MIDL_SERVER_INFO IAMTSEvtDisp_ServerInfo;

#pragma code_seg(".orpc")
static const unsigned short IAMTSEvtDisp_FormatStringOffsetTable[] = 
    {
    0
    };

static const MIDL_SERVER_INFO IAMTSEvtDisp_ServerInfo = 
    {
    &Object_StubDesc,
    0,
    __MIDL_ProcFormatString.Format,
    &IAMTSEvtDisp_FormatStringOffsetTable[-3],
    0,
    0,
    0,
    0
    };

static const MIDL_STUBLESS_PROXY_INFO IAMTSEvtDisp_ProxyInfo =
    {
    &Object_StubDesc,
    __MIDL_ProcFormatString.Format,
    &IAMTSEvtDisp_FormatStringOffsetTable[-3],
    0,
    0,
    0
    };

CINTERFACE_PROXY_VTABLE(4) _IAMTSEvtDispProxyVtbl = 
{
    &IAMTSEvtDisp_ProxyInfo,
    &IID_IAMTSEvtDisp,
    IUnknown_QueryInterface_Proxy,
    IUnknown_AddRef_Proxy,
    IUnknown_Release_Proxy ,
    (void *)-1 /* IAMTSEvtDisp::FireDirectedEvent */
};

const CInterfaceStubVtbl _IAMTSEvtDispStubVtbl =
{
    &IID_IAMTSEvtDisp,
    &IAMTSEvtDisp_ServerInfo,
    4,
    0, /* pure interpreted */
    CStdStubBuffer_METHODS
};


/* Object interface: AsyncIAMTSEvtDisp, ver. 0.0,
   GUID={0xCC8F5CC9,0x3CB4,0x456e,{0xAA,0x1B,0x9C,0xDF,0xE0,0x9A,0x88,0x36}} */


extern const MIDL_STUB_DESC Object_StubDesc;


extern const MIDL_SERVER_INFO AsyncIAMTSEvtDisp_ServerInfo;

#pragma code_seg(".orpc")
static const unsigned short AsyncIAMTSEvtDisp_FormatStringOffsetTable[] = 
    {
    44,
    88
    };

static const MIDL_SERVER_INFO AsyncIAMTSEvtDisp_ServerInfo = 
    {
    &Object_StubDesc,
    0,
    __MIDL_ProcFormatString.Format,
    &AsyncIAMTSEvtDisp_FormatStringOffsetTable[-3],
    0,
    0,
    0,
    0
    };

static const MIDL_STUBLESS_PROXY_INFO AsyncIAMTSEvtDisp_ProxyInfo =
    {
    &Object_StubDesc,
    __MIDL_ProcFormatString.Format,
    &AsyncIAMTSEvtDisp_FormatStringOffsetTable[-3],
    0,
    0,
    0
    };

CINTERFACE_PROXY_VTABLE(5) _AsyncIAMTSEvtDispProxyVtbl = 
{
    &AsyncIAMTSEvtDisp_ProxyInfo,
    &IID_AsyncIAMTSEvtDisp,
    IUnknown_QueryInterface_Proxy,
    IUnknown_AddRef_Proxy,
    IUnknown_Release_Proxy ,
    (void *)-1 /* AsyncIAMTSEvtDisp::Begin_FireDirectedEvent */ ,
    (void *)-1 /* AsyncIAMTSEvtDisp::Finish_FireDirectedEvent */
};

CInterfaceStubVtbl _AsyncIAMTSEvtDispStubVtbl =
{
    &IID_AsyncIAMTSEvtDisp,
    &AsyncIAMTSEvtDisp_ServerInfo,
    5,
    0, /* pure interpreted */
    CStdAsyncStubBuffer_METHODS
};

extern const USER_MARSHAL_ROUTINE_QUADRUPLE UserMarshalRoutines[ WIRE_MARSHAL_TABLE_SIZE ];

static const MIDL_STUB_DESC Object_StubDesc = 
    {
    0,
    NdrOleAllocate,
    NdrOleFree,
    0,
    0,
    0,
    0,
    0,
    __MIDL_TypeFormatString.Format,
    1, /* -error bounds_check flag */
    0x50002, /* Ndr library version */
    0,
    0x5030118, /* MIDL Version 5.3.280 */
    0,
    UserMarshalRoutines,
    0,  /* notify & notify_flag routine table */
    0x1, /* MIDL flag */
    0,  /* Reserved3 */
    0,  /* Reserved4 */
    0   /* Reserved5 */
    };

#pragma data_seg(".rdata")

static const USER_MARSHAL_ROUTINE_QUADRUPLE UserMarshalRoutines[ WIRE_MARSHAL_TABLE_SIZE ] = 
        {
            
            {
            BSTR_UserSize
            ,BSTR_UserMarshal
            ,BSTR_UserUnmarshal
            ,BSTR_UserFree
            }

        };


#if !defined(__RPC_WIN64__)
#error  Invalid build platform for this stub.
#endif

static const MIDL_PROC_FORMAT_STRING __MIDL_ProcFormatString =
    {
        0,
        {

	/* Procedure FireDirectedEvent */

			0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/*  2 */	NdrFcLong( 0x0 ),	/* 0 */
/*  6 */	NdrFcShort( 0x3 ),	/* 3 */
/*  8 */	NdrFcShort( 0x20 ),	/* ia64, axp64 Stack size/offset = 32 */
/* 10 */	NdrFcShort( 0x0 ),	/* 0 */
/* 12 */	NdrFcShort( 0x8 ),	/* 8 */
/* 14 */	0x46,		/* Oi2 Flags:  clt must size, has return, has ext, */
			0x3,		/* 3 */
/* 16 */	0xa,		/* 10 */
			0x5,		/* Ext Flags:  new corr desc, srv corr check, */
/* 18 */	NdrFcShort( 0x0 ),	/* 0 */
/* 20 */	NdrFcShort( 0x2 ),	/* 2 */
/* 22 */	NdrFcShort( 0x0 ),	/* 0 */
/* 24 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter bstrData */

/* 26 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 28 */	NdrFcShort( 0x8 ),	/* ia64, axp64 Stack size/offset = 8 */
/* 30 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter bstrDest */

/* 32 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 34 */	NdrFcShort( 0x10 ),	/* ia64, axp64 Stack size/offset = 16 */
/* 36 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Return value */

/* 38 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 40 */	NdrFcShort( 0x18 ),	/* ia64, axp64 Stack size/offset = 24 */
/* 42 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Begin_FireDirectedEvent */

/* 44 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 46 */	NdrFcLong( 0x0 ),	/* 0 */
/* 50 */	NdrFcShort( 0x3 ),	/* 3 */
/* 52 */	NdrFcShort( 0x20 ),	/* ia64, axp64 Stack size/offset = 32 */
/* 54 */	NdrFcShort( 0x0 ),	/* 0 */
/* 56 */	NdrFcShort( 0x8 ),	/* 8 */
/* 58 */	0x66,		/* Oi2 Flags:  clt must size, has return, has async uuid, has ext, */
			0x3,		/* 3 */
/* 60 */	0xa,		/* 10 */
			0x5,		/* Ext Flags:  new corr desc, srv corr check, */
/* 62 */	NdrFcShort( 0x0 ),	/* 0 */
/* 64 */	NdrFcShort( 0x2 ),	/* 2 */
/* 66 */	NdrFcShort( 0x0 ),	/* 0 */
/* 68 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter bstrData */

/* 70 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 72 */	NdrFcShort( 0x8 ),	/* ia64, axp64 Stack size/offset = 8 */
/* 74 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter bstrDest */

/* 76 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 78 */	NdrFcShort( 0x10 ),	/* ia64, axp64 Stack size/offset = 16 */
/* 80 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Return value */

/* 82 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 84 */	NdrFcShort( 0x18 ),	/* ia64, axp64 Stack size/offset = 24 */
/* 86 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Finish_FireDirectedEvent */

/* 88 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 90 */	NdrFcLong( 0x0 ),	/* 0 */
/* 94 */	NdrFcShort( 0x4 ),	/* 4 */
/* 96 */	NdrFcShort( 0x10 ),	/* ia64, axp64 Stack size/offset = 16 */
/* 98 */	NdrFcShort( 0x0 ),	/* 0 */
/* 100 */	NdrFcShort( 0x8 ),	/* 8 */
/* 102 */	0x64,		/* Oi2 Flags:  has return, has async uuid, has ext, */
			0x1,		/* 1 */
/* 104 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 106 */	NdrFcShort( 0x0 ),	/* 0 */
/* 108 */	NdrFcShort( 0x0 ),	/* 0 */
/* 110 */	NdrFcShort( 0x0 ),	/* 0 */
/* 112 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Return value */

/* 114 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 116 */	NdrFcShort( 0x8 ),	/* ia64, axp64 Stack size/offset = 8 */
/* 118 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

			0x0
        }
    };

static const MIDL_TYPE_FORMAT_STRING __MIDL_TypeFormatString =
    {
        0,
        {
			NdrFcShort( 0x0 ),	/* 0 */
/*  2 */	
			0x12, 0x0,	/* FC_UP */
/*  4 */	NdrFcShort( 0xe ),	/* Offset= 14 (18) */
/*  6 */	
			0x1b,		/* FC_CARRAY */
			0x1,		/* 1 */
/*  8 */	NdrFcShort( 0x2 ),	/* 2 */
/* 10 */	0x9,		/* Corr desc: FC_ULONG */
			0x0,		/*  */
/* 12 */	NdrFcShort( 0xfffc ),	/* -4 */
/* 14 */	NdrFcShort( 0x1 ),	/* Corr flags:  early, */
/* 16 */	0x6,		/* FC_SHORT */
			0x5b,		/* FC_END */
/* 18 */	
			0x17,		/* FC_CSTRUCT */
			0x3,		/* 3 */
/* 20 */	NdrFcShort( 0x8 ),	/* 8 */
/* 22 */	NdrFcShort( 0xfffffff0 ),	/* Offset= -16 (6) */
/* 24 */	0x8,		/* FC_LONG */
			0x8,		/* FC_LONG */
/* 26 */	0x5c,		/* FC_PAD */
			0x5b,		/* FC_END */
/* 28 */	0xb4,		/* FC_USER_MARSHAL */
			0x83,		/* 131 */
/* 30 */	NdrFcShort( 0x0 ),	/* 0 */
/* 32 */	NdrFcShort( 0x8 ),	/* 8 */
/* 34 */	NdrFcShort( 0x0 ),	/* 0 */
/* 36 */	NdrFcShort( 0xffffffde ),	/* Offset= -34 (2) */

			0x0
        }
    };

const CInterfaceProxyVtbl * _EvtDisp_ProxyVtblList[] = 
{
    ( CInterfaceProxyVtbl *) &_IAMTSEvtDispProxyVtbl,
    ( CInterfaceProxyVtbl *) &_AsyncIAMTSEvtDispProxyVtbl,
    0
};

const CInterfaceStubVtbl * _EvtDisp_StubVtblList[] = 
{
    ( CInterfaceStubVtbl *) &_IAMTSEvtDispStubVtbl,
    ( CInterfaceStubVtbl *) &_AsyncIAMTSEvtDispStubVtbl,
    0
};

PCInterfaceName const _EvtDisp_InterfaceNamesList[] = 
{
    "IAMTSEvtDisp",
    "AsyncIAMTSEvtDisp",
    0
};


#define _EvtDisp_CHECK_IID(n)	IID_GENERIC_CHECK_IID( _EvtDisp, pIID, n)

int __stdcall _EvtDisp_IID_Lookup( const IID * pIID, int * pIndex )
{
    IID_BS_LOOKUP_SETUP

    IID_BS_LOOKUP_INITIAL_TEST( _EvtDisp, 2, 1 )
    IID_BS_LOOKUP_RETURN_RESULT( _EvtDisp, 2, *pIndex )
    
}

static const IID * _AsyncInterfaceTable[] = 
{
    (IID*) &IID_AsyncIAMTSEvtDisp,
    (IID*) -1,
    (IID*) 0
};

const ExtendedProxyFileInfo EvtDisp_ProxyFileInfo = 
{
    (PCInterfaceProxyVtblList *) & _EvtDisp_ProxyVtblList,
    (PCInterfaceStubVtblList *) & _EvtDisp_StubVtblList,
    (const PCInterfaceName * ) & _EvtDisp_InterfaceNamesList,
    0, // no delegation
    & _EvtDisp_IID_Lookup, 
    2,
    6,
    (const IID**) &_AsyncInterfaceTable[0], /* table of [async_uuid] interfaces */
    0, /* Filler1 */
    0, /* Filler2 */
    0  /* Filler3 */
};


#endif /* defined(_M_IA64) || defined(_M_AXP64)*/

