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
// $Id$
//
package siena;

import java.io.IOException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.Socket;

/** receives packets through a UDP port.
 **/
public class UDPPacketReceiver implements PacketReceiver {
    static final String		protocol_name = "udp+senp";
    private DatagramSocket	port;
    private DatagramPacket	packet;
    private byte[]		my_uri;

    /** create a receiver listening to the given UDP port.
     *
     *  @param port_number must be a valid UDP port number, or it can
     *         be 0 in which case a random port is used
     **/
    public UDPPacketReceiver(int port_number) throws IOException {
	port = new DatagramSocket(port_number);
	//	port.setReceiveBufferSize(SENP.MaxPacketLen);
	packet = new DatagramPacket(new byte[1], 1);
	my_uri = (protocol_name + "://" 
		  + InetAddress.getLocalHost().getHostAddress()
		  + ":" + Integer.toString(port.getLocalPort())).getBytes();
    }

    public UDPPacketReceiver(DatagramSocket s) throws UnknownHostException {
	port = s;
	packet = new DatagramPacket(new byte[1], 1);
	my_uri = (protocol_name + "://"
		  + InetAddress.getLocalHost().getHostAddress()
		  + ":" + Integer.toString(port.getLocalPort())).getBytes();
    }

    /** explicitly set the address of this packet receiver.
     *
     *  This method allows to set the host name or IP address
     *  explicitly.  This might be necessary in the cases in which the
     *  java VM can not figure that out reliably.
     *
     **/
    synchronized public void setHostName(String hostname) {
	my_uri = (protocol_name + "://" + hostname + ":" 
		  + Integer.toString(port.getLocalPort())).getBytes();
    }

    /** uri of this packet receiver.
     *
     *  uses the following schema syntax:<br>
     *
     *  <code>udp+senp://</code><em>host</em><em>[<code>:</code>port]</em>
     **/
    public byte[] uri() {
	return my_uri;
    }

    synchronized public void shutdown() {
	System.out.println("shutdown() called...");
	if (port == null) return;
	System.out.println("closing port...");
	port.close();
	port = null;
    }

    synchronized private DatagramSocket getPort() {
	return port;
    }

    public int receive(byte[] buf) throws PacketReceiverException {

	if (port == null) throw(new PacketReceiverClosed());
	try {
	    synchronized (packet) {
		packet.setData(buf);
		System.out.println("receiving...");
		port.receive(packet);
		System.out.println("read packet: " + packet.getLength());
		return packet.getLength();
	    }
	} catch (Exception ex) {
	    if (port != null) {
		Logging.prlnerr("can't get packet on UDP port: " 
				+ Integer.toString(port.getLocalPort()));
		Logging.exerr(ex);
		throw(new PacketReceiverException(ex.toString()));
	    }
	}
	throw new PacketReceiverClosed();
    }

    /** <em>not yet implemented</em>.
     **/
    public int receive(byte[] buf, long timeout) {
	//
	// not yet implemented
	//
	return -1;
    }
}
