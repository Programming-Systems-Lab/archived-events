// TCPPacket.h: interface for the TCPPacket class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_TCPPACKET_H__80EF1780_6287_46E0_A7DD_F472BE86E8A9__INCLUDED_)
#define AFX_TCPPACKET_H__80EF1780_6287_46E0_A7DD_F472BE86E8A9__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include <NIDPacket.h>
#include <iostream.h>
#include <NIDConstants.h>

class NIDExport TCPPacket:public Packet  
{
friend ostream& operator <<( ostream& output, const TCPPacket& rhs );
public:
	TCPPacket();
	TCPPacket( const TCPPacket& rhs );
	virtual ~TCPPacket();
	
	virtual AString AsXML( void );
	const TCPPacket& operator=( const TCPPacket& rhs );

	BOOL	m_bURG;
	BOOL	m_bACK;
	BOOL	m_bPSH;
	BOOL	m_bRST;
	BOOL	m_bSYN;
	BOOL	m_bFIN;
};

#endif // !defined(AFX_TCPPACKET_H__80EF1780_6287_46E0_A7DD_F472BE86E8A9__INCLUDED_)
