// IPPacket.h: interface for the IPPacket class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_IPPACKET_H__B9E1D8D7_A95D_4DF3_A7A3_D1DC18DEA464__INCLUDED_)
#define AFX_IPPACKET_H__B9E1D8D7_A95D_4DF3_A7A3_D1DC18DEA464__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include <iostream.h>
#include <NIDConstants.h>

class NIDExport IPPacket
{
friend ostream& operator <<( ostream& output, const IPPacket& rhs );
public:
	IPPacket();
	IPPacket( const IPPacket& rhs ); //copy constructor
	virtual ~IPPacket();

	virtual AString AsXML( void );
	const IPPacket& operator=( const IPPacket& rhs );
	
	AString		m_szSourceIP;
	AString		m_szDestIP;
	int			m_nProtocol;
	AString		m_szData;	
};

#endif // !defined(AFX_IPPACKET_H__B9E1D8D7_A95D_4DF3_A7A3_D1DC18DEA464__INCLUDED_)
