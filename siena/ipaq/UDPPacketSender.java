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
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.io.IOException;

/** UDP packet sender
 **/
class UDPPacketSender implements PacketSender {
    private InetAddress		ip_address;
    private int			port;
    private DatagramSocket	socket;
    private DatagramPacket	packet;

    public UDPPacketSender(String h) 
	throws InvalidSenderException, 
	       java.net.UnknownHostException,
	       java.net.SocketException {

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
	socket = new DatagramSocket();
	//socket.setSendBufferSize(SENP.MaxPacketLen);
	//
	// looks like I have to use a buffer in the constructor even
	// though I will never use that buffer to send data
	//
	packet = new DatagramPacket(new byte[1], 1, ip_address, port);
    }

    synchronized public void shutdown() {
	if (socket == null) return;
	socket.close();
	socket = null;
    }

    synchronized public void send(byte[] buf) throws PacketSenderException {
	if (socket == null) 
	    throw new PacketSenderException("sender shut down");
	try {
	    packet.setLength(buf.length);
	    packet.setData(buf);
	    socket.send(packet);
	} catch (IOException ex) {
	    throw new PacketSenderException(ex.getMessage());
	}
    }

    synchronized public void send(byte[] buf, int offset, int len)
	throws PacketSenderException {
	if (socket == null) 
	    throw new PacketSenderException("sender shut down");
	try {
	    packet.setLength(buf.length);
	    packet.setData(buf);
	    socket.send(packet);
	} catch (IOException ex) {
	    throw new PacketSenderException(ex.getMessage());
	}
    }

    synchronized public void send(byte[] buf, int len)
	throws PacketSenderException {
	if (socket == null) 
	    throw new PacketSenderException("sender shut down");
	try {
	    packet.setLength(len);
	    packet.setData(buf);
	    packet.setLength(len);
	    socket.send(packet);
	} catch (IOException ex) {
	    throw new PacketSenderException(ex.getMessage());
	}
    }

    public String toString() {
	return "udp+senp://" + ip_address + ":" + port;
    }
}
