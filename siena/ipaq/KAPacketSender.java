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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

class KAPacketSender implements PacketSender {
    private InetAddress		ip_address;
    private int			port;
    private Socket		socket;
    private OutputStream	os;
    private long		last_conn = -1;

    public int			DefaultSoTimeout = 5000;
    private int			so_timeout = DefaultSoTimeout;

    public int			DefaultDisconnectTimeout = 5000;
    private int			disconnect_timeout = DefaultSoTimeout;

    
    public KAPacketSender(String h) 
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
	send(packet, 0, packet.length);
    }

    public void send(byte[] packet, int offset, int len) 
	throws PacketSenderException {
	int retries;
	retries = 0;
	while (true) {
	    try {
		if (socket == null) {
		    try {
			socket = new Socket(ip_address, port);
		    } catch (IOException ex) {
			throw new PacketSenderException(ex.getMessage());
		    }
		    if (socket.getInputStream().read()!=KAPacketReceiver.REPLY_OK)
			throw new PacketSenderException("connection rejected");

		    //		    socket.shutdownInput();
		    //		    socket.setSoTimeout(so_timeout);
		    //		    socket.setSendBufferSize(65535);
		    os = socket.getOutputStream();
		}
		synchronized (os) {
		    os.write((len & 0xff00) >>> 8);
		    os.write(len & 0xff);
		    os.write(packet, offset, len);
		    os.flush();
		}
		return;
	    } catch (IOException ex) {
		socket = null;
		os = null;
		if (++retries > 1)
		    throw new PacketSenderException(ex.toString());
	    }
	} 
    }

    public void send(byte[] packet, int len) 
	throws PacketSenderException {
	send(packet, 0, len);
    }

    public String toString() {
	return KAPacketReceiver.protocol_name + "://" + ip_address + ":" + port;
    }
}

