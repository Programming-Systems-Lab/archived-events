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

import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;

/** packet sender based on one-time TCP connections 
 **/
class TCPPacketSender implements PacketSender {
    private InetAddress		ip_address;
    private int			port;

    public TCPPacketSender(String h) 
	throws InvalidSenderException, java.net.UnknownHostException {

	if (h.indexOf("//", 0) != 0)
	    throw (new InvalidSenderException("expecting `//'"));

	int port_end_pos = -1;
	int host_end_pos = h.indexOf(":", 2);

	if (host_end_pos < 0) {
	    port = -1;
	    host_end_pos = h.indexOf("/", 2);
	    if (host_end_pos < 0) host_end_pos = h.length();
	} else {
	    port_end_pos = h.indexOf("/", host_end_pos);
	    if (port_end_pos < 0) port_end_pos = h.length();

	    if (host_end_pos+1 < port_end_pos) {
		port = Integer.decode(h.substring(host_end_pos+1,
						  port_end_pos)).intValue();
	    } else {
		port = -1;
	    }
	}
	String hostname = h.substring(2, host_end_pos);
	if (port == -1) {
	    port = SENP.SERVER_PORT;
	}
	ip_address = InetAddress.getByName(hostname);
    }

    public void send(byte[] packet) throws PacketSenderException {
	try {
	    Socket s = new Socket(ip_address, port);
	    //s.setSendBufferSize(65535);
	    s.getOutputStream().write(packet);
	    s.close();
	} catch (IOException ex) {
	    throw new PacketSenderException(ex.getMessage());
	}
    }

    public void send(byte[] packet, int offset, int len) 
	throws PacketSenderException {
	try {
	    Socket s = new Socket(ip_address, port);
	    //s.setSendBufferSize(65535);
	    s.getOutputStream().write(packet, offset, len);
	    s.close();
	} catch (IOException ex) {
	    throw new PacketSenderException(ex.getMessage());
	}
    }

    public void send(byte[] packet, int len) 
	throws PacketSenderException {
	try {
	    Socket s = new Socket(ip_address, port);
	    //s.setSendBufferSize(65535);
	    s.getOutputStream().write(packet, 0, len);
	    s.close();
	} catch (IOException ex) {
	    throw new PacketSenderException(ex.getMessage());
	}
    }

    public String toString() {
	return "senp://" + ip_address + ":" + port;
    }
}
