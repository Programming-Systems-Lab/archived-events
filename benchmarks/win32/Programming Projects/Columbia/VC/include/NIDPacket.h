// Packet.h: interface for the Packet class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_NIDPACKET_H__66A9A348_EE80_419F_9A4A_7A1DA4211039__INCLUDED_)
#define AFX_NIDPACKET_H__66A9A348_EE80_419F_9A4A_7A1DA4211039__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include <NIDConstants.h>

class NIDExport Packet  
{
public:
	Packet();
	virtual ~Packet();

	virtual AString AsXML( void ) = 0;

	int		m_nSourcePort;
	int		m_nDestPort;
	AString m_szData;
};

#endif // !defined(AFX_PACKET_H__66A9A348_EE80_419F_9A4A_7A1DA4211039__INCLUDED_)
