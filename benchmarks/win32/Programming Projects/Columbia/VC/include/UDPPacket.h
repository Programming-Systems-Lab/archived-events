// UDPPacket.h: interface for the UDPPacket class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_UDPPACKET_H__83E72FB6_072F_4FFE_9C20_9CA14B1CE916__INCLUDED_)
#define AFX_UDPPACKET_H__83E72FB6_072F_4FFE_9C20_9CA14B1CE916__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include <NIDPacket.h>
#include <iostream.h>
#include <NIDConstants.h>

class NIDExport UDPPacket:public Packet  
{
friend ostream& operator << ( ostream& output, const UDPPacket& rhs );
public:
	UDPPacket();
	UDPPacket( const UDPPacket& rhs );
	virtual ~UDPPacket();

	virtual AString AsXML( void );
	const UDPPacket& operator=( const UDPPacket& rhs );

};

#endif // !defined(AFX_UDPPACKET_H__83E72FB6_072F_4FFE_9C20_9CA14B1CE916__INCLUDED_)
