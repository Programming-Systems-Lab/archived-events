//
//  This file is part of Siena, a wide-area event notification system.
//  See http://www.cs.colorado.edu/serl/siena/
//
//  Author: Antonio Carzaniga <carzanig@cs.colorado.edu>
//  See the file AUTHORS for full details. 
//
//  Copyright (C) 1998-2002 University of Colorado
//
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//  
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//  
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307,
//  USA, or send email to serl@cs.colorado.edu.
//
//
// $Id$
//
package siena;

import java.io.IOException;

/** creates PacketSenders based on their URI
 *
 *  this factory recognizes "senp:" (see {@link TCPPacketReceiver}),
 *  "udp+senp:" (see {@link UDPPacketReceiver}), and "ka+senp:" (see
 *  {@link KAPacketReceiver}) schemas.
 **/
public class GenericSenderFactory implements PacketSenderFactory {
    public PacketSender createPacketSender(String handler) 
	throws InvalidSenderException {
	//
	// this parses a handler of the form:
	// <schema> "://" <host> [":" <port>] ["/" <path>]
	//
	int pos = handler.indexOf(":");
	if (pos < 0) throw (new InvalidSenderException("can't find schema"));
	String schema = handler.substring(0,pos);
	if (schema.equals(TCPPacketReceiver.protocol_name)) {
	    try {
		return new TCPPacketSender(handler.substring(pos+1, 
							     handler.length()));
	    } catch (IOException ex) {
		throw new InvalidSenderException(ex.getMessage());
	    }
	} else if (schema.equals(UDPPacketReceiver.protocol_name)) {
	    try {
		return new UDPPacketSender(handler.substring(pos+1, 
							     handler.length()));
	    } catch (IOException ex) {
		throw new InvalidSenderException(ex.getMessage());
	    }
	} else if (schema.equals(KAPacketReceiver.protocol_name)) {
	    try {
		return new KAPacketSender(handler.substring(pos+1, 
							    handler.length()));
	    } catch (IOException ex) {
		throw new InvalidSenderException(ex.getMessage());
	    }
	} else { 
	    throw (new InvalidSenderException("unknown schema: " + schema));
	}
    }
}
