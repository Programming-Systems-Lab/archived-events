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
import java.net.ServerSocket;
import java.net.Socket;

/** receives packets through a TCP port.
 *
 *  Receives packets through one-time connections to a TCP port.
 *  Accepts connections to a local port, reads one packet from the
 *  accepted socket, and closes the socket.
 **/
public class TCPPacketReceiver implements PacketReceiver {
    static final String	protocol_name = "senp";
    private ServerSocket	port;
    private byte[]		my_uri;

    /** create a receiver listening to the given port.
     *
     *  @param port_number must be a valid TCP port number, or it can
     *         be 0 in which case a random port is used
     *
     *  @exception if an I/O error occurs when opening the socket.
     *             typically, when the given port is already in use.
     **/
    public TCPPacketReceiver(int port_number) throws IOException {
	port = new ServerSocket(port_number);
	my_uri = (protocol_name + "://" 
		  + InetAddress.getLocalHost().getHostAddress()
		  + ":" + Integer.toString(port.getLocalPort())).getBytes();
    }

    /** create a receiver listening to the given port with a given
     *  maximum queue for TCP connections.
     *
     *  @param port_number must be a valid TCP port number, or it can
     *         be 0 in which case a random port is used
     *
     *  @exception if an I/O error occurs when opening the socket.
     *             typically, when the given port is already in use.
     **/
    public TCPPacketReceiver(int port_number, int qsize) throws IOException {
	port = new ServerSocket(port_number, qsize);
	my_uri = (protocol_name + "://"
		  + InetAddress.getLocalHost().getHostAddress()
		  + ":" + Integer.toString(port.getLocalPort())).getBytes();
    }

    public TCPPacketReceiver(ServerSocket s) throws UnknownHostException {
	port = s;
	my_uri = (protocol_name + "://"
		  + InetAddress.getLocalHost().getHostAddress()
		  + ":" + Integer.toString(port.getLocalPort())).getBytes();
    }

    /** explicitly set the address of this packet receiver.
     *
     *  This method allows to set the host name or IP address
     *  explicitly.  This might be necessary in the cases in which the
     *  java VM can not figure that out reliably.
     **/
    synchronized public void setHostName(String hostname) {
	my_uri = (protocol_name + "://" + hostname + ":" 
		  + Integer.toString(port.getLocalPort())).getBytes();
    }

    /** uri of this packet receiver.
     *
     *  uses the following schema syntax:<br>
     *
     *  <code>senp://</code><em>host</em><em>[<code>:</code>port]</em>
     **/
    public byte[] uri() {
	return my_uri;
    }

    synchronized public void shutdown() {
	if (port == null) return;
	try {
	    port.close();
	    //
	    // this should terminate all the connection handlers
	    // attached to that port.  They should receive an
	    // IOException on accept() ...they should, but on some
	    // implementations of the JVM they don't.  In
	    // particular, jvm-1.3rc1-linux-i386 is buggy.
	    //
	} catch (IOException ex) {
	    Logging.exerr(ex);
	    //
	    // what can I do here? ...work in progress...
	    //
	}
	port = null;
    }

    public int receive(byte[] buf) throws PacketReceiverException {
	try {
	    Socket sock;
	    //
	    // I use this variable to avoid a race condition that may
	    // occurr if someone calls shutdown() after port == null,
	    // and before port.accept().
	    //
	    ServerSocket p = port;
	    if (p == null) throw(new PacketReceiverClosed());
	    try {
		sock = p.accept();
	    } catch (IOException ex) {
		//
		// I interpret this as a shutdown().
		//
		throw new PacketReceiverClosed(ex);
	    }

	    java.io.InputStream input = sock.getInputStream();
	    
	    int offset = 0;
	    int res;
	    
	    try {
		while((res = input.read(buf, offset, buf.length - offset)) >= 0)
		    offset += res;
		sock.close();
	    } catch (Exception ex) {
		Logging.exerr(ex);
		throw(new PacketReceiverException(ex.toString()));
	    }
	    return offset;
	} catch (java.io.IOException ex) {
	    //
	    // port closed.
	    //
	    return 0;
	}
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
