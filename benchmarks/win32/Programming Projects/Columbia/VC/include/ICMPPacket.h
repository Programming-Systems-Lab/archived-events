// ICMPPacket.h: interface for the ICMPPacket class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_ICMPPACKET_H__E976D1D3_87C4_49A8_B7E2_D842076BDFF9__INCLUDED_)
#define AFX_ICMPPACKET_H__E976D1D3_87C4_49A8_B7E2_D842076BDFF9__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include <NIDPacket.h>
#include <iostream.h>
#include <NIDConstants.h>

class NIDExport ICMPPacket: public Packet  
{
friend ostream& operator << ( ostream& output, const ICMPPacket& rhs );
public:
	ICMPPacket();
	ICMPPacket( const ICMPPacket& rhs );
	virtual ~ICMPPacket();
	
	virtual AString AsXML( void );
	const ICMPPacket& operator=( const ICMPPacket& rhs );

	int m_nServiceType;
};

#endif // !defined(AFX_ICMPPACKET_H__E976D1D3_87C4_49A8_B7E2_D842076BDFF9__INCLUDED_)
