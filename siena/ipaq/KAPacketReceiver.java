//
//  This file is part of Siena, a wide-area event notification system.
//  See http://www.cs.colorado.edu/serl/siena/
//
//  Author: Antonio Carzaniga <carzanig@cs.colorado.edu>
//  See the file AUTHORS for full details. 
//
//  Copyright (C) 1998-2000 University of Colorado
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

import com.sun.java.util.collections.LinkedList;
import java.io.InterruptedIOException;
import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

class KADescrQueue {
    private int		size = 0;
    private KAConnDescr first = null;
    private KAConnDescr last = null;
    private KAConnDescr available = null;
    private boolean	active = true;

    synchronized int size() { return size; }

    synchronized void put(Socket s) {
	KAConnDescr kd;

	if (available == null) {
	    kd = new KAConnDescr(s);
	} else {
	    kd = available;
	    available = available.next;
	    kd.init(s);
	}
	if (last != null) last.next = kd;
	last = kd;
	kd.next = null;
	if (first == null) first = kd;
	++size;
	notify();
    }

    synchronized void put(KAConnDescr kd) {
	if (last != null) last.next = kd;
	last = kd;
	kd.next = null;
	if (first == null) first = kd;
	++size;
	notify();
    }

    synchronized KAConnDescr get() throws InterruptedException {
	KAConnDescr kd;
	while(first == null) {
	    if (!active) return null;
	    wait();
	}
	kd = first;
	first = kd.next;
	kd.next = null;
	if (last == kd) last = null;
	--size;
	return kd;
    }

    synchronized void recycle(KAConnDescr kd) {
	if (kd.sock != null)
	    try { kd.sock.close(); } catch (IOException ex) { }
	kd.sock = null;
	kd.stream = null;
	kd.next = available;
	available = kd;
    }

    synchronized void shutdown() {
	KAConnDescr kd = first;
	while(kd != null) {
	    kd.close();
	    kd = kd.next;
	}
	available = null;
	last = null;
	size = 0;
	active = false;
	notifyAll();
    }
}

class KAConnDescr {
    Socket	sock;
    InputStream stream;
    int		count;
    long	last_good;
    KAConnDescr next;

    public KAConnDescr(Socket s) {
	init(s);
    }

    public void init(Socket s)  {
	sock = s;
	stream = null;
	count = 0;
    	last_good = System.currentTimeMillis();
    }

    synchronized public void close() {
	stream = null;
	if (sock != null) 
	    try { 
		sock.close(); 
	    } catch (IOException ex) {
		//
		// what else should I do here?
		//
	    } 
	sock = null;
    }

    synchronized public int receive(byte [] buf) 
	throws InterruptedException, IOException {
	if (sock == null) return -1;
	if (stream == null)
	    stream = new BufferedInputStream(sock.getInputStream());
	int res = 0;
	int len = 0;
	int len1 = 0;
	int pos = 0;
	//
	// I need to handle timout on read a bit better... 
	// ...work in progress...
	//
	sock.setSoTimeout(5000);

	//
	// we read the msb and lsb of the length.  We break if
	// we can't read them or if the resulting length is 0
	//
	len = stream.read();
	if (len < 0) return -1;
	len1 = stream.read();
	if (len1 < 0) return -1;
	len = (len << 8) | (len1 & 0xff);
	if (len < 0) return -1;
	pos = 0;
	while(pos < len) {
	    if ((res = stream.read(buf,pos, len - pos)) < 0) 
		return -1;
	    pos += res;
	}
	//	    last_good = System.currentTimeMillis();
	++count;
	return len;
    }
}

/** receives packets through a TCP port.
 *
 *  Uses persistent TCP connections to receive one or more packets.
 **/
public class KAPacketReceiver implements PacketReceiver, Runnable {
    public static final byte	REPLY_REJECT	= 0;
    public static final byte	REPLY_OK	= 1;

    static final String		protocol_name = "ka+senp";
    private ServerSocket	port;
    private Socket[]		connection;
    private byte[]		my_uri;
    private Thread		acceptor;

    static public int		DefaultAcceptTimeout = 5000;
    public int			accept_timeout = DefaultAcceptTimeout;

    static public int		DefaultReceiveTimeout = 3000;
    public int			receive_timeout = DefaultReceiveTimeout;

    static public int		DefaultMaxReceiveCount = -1;
    public int			receive_max_count = DefaultMaxReceiveCount;

    static public int		DefaultMaxActiveConnections = 20;
    public int			max_active_connections = DefaultMaxActiveConnections;

    private KADescrQueue	active_connections = new KADescrQueue();

    /** create a receiver listening to the a random port.
     *
     *  @exception IOException if an I/O error occurs when opening the
     *		socket port.
     **/
    public KAPacketReceiver() throws IOException {
	port = new ServerSocket(0);
	my_uri = (protocol_name + "://" 
		  + InetAddress.getLocalHost().getHostAddress()
		  + ":" + Integer.toString(port.getLocalPort())).getBytes();

	acceptor = new Thread(this);
	acceptor.start();
    }

    /** create a receiver listening to the given port.
     *
     *  @param port_number must be a valid TCP port number, or it can
     *         be 0 in which case a random port is used
     *
     *  @exception IOException if an I/O error occurs when opening the
     *		socket.  typically, when the given port is already in use.
     **/
    public KAPacketReceiver(int port_number) throws IOException {
	port = new ServerSocket(port_number);
	my_uri = (protocol_name + "://" 
		  + InetAddress.getLocalHost().getHostAddress()
		  + ":" + Integer.toString(port.getLocalPort())).getBytes();

	acceptor = new Thread(this);
	acceptor.start();
    }

    /** create a receiver listening to the given port with a given
     *  maximum queue for TCP connections.
     *
     *  @param port_number must be a valid TCP port number, or it can
     *		be 0 in which case a random port is used
     *
     *  @exception IOException if an I/O error occurs when opening the
     *		socket.  typically, when the given port is already in use.
     **/
    public KAPacketReceiver(int port_number, int qsize) 
	throws IOException {
	port = new ServerSocket(port_number, qsize);
	my_uri = (protocol_name + "://"
		  + InetAddress.getLocalHost().getHostAddress()
		  + ":" + Integer.toString(port.getLocalPort())).getBytes();
    }

    /** create a receiver listening to the given port.
     *
     *  @param s server socket used to accept connections.
     *
     *  @exception UnknownHostException if an error occurrs while
     *		resolving the hostname for this host.
     **/
    public KAPacketReceiver(ServerSocket s) throws UnknownHostException {
	port = s;
	my_uri = (protocol_name + "://"
		  + InetAddress.getLocalHost().getHostAddress()
		  + ":" + Integer.toString(port.getLocalPort())).getBytes();
    }

    /** explicitly set the address of this packet receiver.
     *
     *  This method allows to set the host name or IP address
     *  explicitly.  This might be necessary in the cases in which the
     *  Java VM can not reliably figure that out by itself.
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
	active_connections.shutdown();
    }

    //
    // acceptor thread routine
    //
    public void run() {
	Socket s;
 	try {
 	    port.setSoTimeout(accept_timeout);
 	} catch (SocketException ex) {
 	    Logging.prlnerr("error setting SO_TIMOUT for server socket " 
 			    + port.toString());
 	    Logging.prlnerr(ex.toString());
 	    //
 	    // what can I do here? ...work in progress...
 	    //
 	}

	while(port != null) {
	    try {
		s = port.accept();
		if (max_active_connections < 0 ||
		    active_connections.size() < max_active_connections) {
		    //		    s.setReceiveBufferSize(65535); // I made this up...
		    s.getOutputStream().write(REPLY_OK);
		    //s.shutdownOutput();
		    active_connections.put(s);
		} else {
		    // 
		    // reject this one
		    //
		    Logging.prlnlog("rejecting KA connection");
		    s.getOutputStream().write(REPLY_REJECT);
		    s.close();
		}
	    } catch (InterruptedIOException ex) {
		// do nothing here.  we will simply get out of the
		// while loop if port == null...
	    } catch (IOException ex) {
		//
		// I interpret this as a shutdown()
		//
		shutdown();
		return;
	    }
	    s = null;
	}
    }

    public int receive(byte [] buf) throws PacketReceiverException {
	KAConnDescr kd;
	int res;
	while (port != null) {
	    try {
		kd = active_connections.get();
	    } catch (InterruptedException ex) {
		throw new PacketReceiverException(ex.toString());
	    }
	    if (kd == null) throw new PacketReceiverClosed();
	    res = -1;
	    try {
		res = kd.receive(buf);
	    } catch (EOFException ex) {
		//
		// we simply ignore this exception, and continue the loop
		// as long as port != null
		//
	    } catch (InterruptedIOException ex) {
		//
		// we simply ignore this exception, recycle the
		// socket, and continue the loop as long as port !=
		// null
		//
	    } catch (Exception ex) {
		ex.printStackTrace();
		throw new PacketReceiverException(ex.toString());
	    } finally {
		if (res < 0) {
		    if (port != null) 
			active_connections.recycle(kd);
		} else {
		    active_connections.put(kd);
		    return res;
		}
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
