//
//  This file is part of Siena, a wide-area event notification system.
//  See http://www.cs.colorado.edu/serl/siena/
//
//  Author: Antonio Carzaniga <carzanig@cs.colorado.edu>
//  See the file AUTHORS for full details. 
//
//  Copyright (C) 1998-2001 University of Colorado
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
import java.net.ServerSocket;
import java.io.IOException;
import com.sun.java.util.collections.Map;
import com.sun.java.util.collections.HashMap;
import com.sun.java.util.collections.Iterator;

/** <em>thin</em> interface to the Siena event notification service.
 * 
 *  <code>ThinClient</code> does not provide an event notification
 *  service by itself, but rather functions as a connection to an
 *  external Siena server.  Therefore, a <code>ThinClient</code>
 *  object must be configured (constructed) with the handler of its
 *  external Siena server.  <p>
 *  
 *  In the simplest case, a <code>ThinClient</code> can be used only to
 *  publish notifications.  For example:
 *  
 *  <code><pre>
 *      ThinClient siena;
 *      siena = new ThinClient("senp://siena.dot.org:2345");
 *      Notification n = new Notification();
 *      n.putAttribute("name", "Antonio");
 *      siena.publish(n);
 *  </pre></code>
 *  
 *  <code>ThinClient</code> implements the {@link Siena} interface, so
 *  it can also be used to subscribe and unsubscribe.
 *  <code>ThinClient</code> uses a {@link PacketReceiver} to receive
 *  notifications from the server.
 *  
 *  @see HierarchicalDispatcher 
 *  @see Siena
 *  @see PacketReceiver
 **/
public class ThinClient implements Siena, Runnable {
    private byte[]		sndbuf		= new byte[SENP.MaxPacketLen];
    private byte[]		master_id	= null;
    private byte[]		master_handler	= null;
    private PacketSender	master		= null;
    private PacketReceiver	listener	= null;
    private String		my_id		= null;
    private Map 		subscribers	= null;

    private SENPPacket		pkt = new SENPPacket();

    static PacketSenderFactory	default_sender_factory 
						= new GenericSenderFactory();

    /** default packet-sender factory for ThinClient interfaces
     *
     *  every new ThinClient uses this factory to create its
     *  connection to its master server
     **/
    static public void setDefaultPacketSenderFactory(PacketSenderFactory f) {
	default_sender_factory = f;
    }

    /** number of threads handling external packets.
     *
     *	The default value of <code>ReceiverThreads</code> is 4.  This
     *	value is used as the default number of receiver threads.
     *
     *	@see #setReceiver(PacketReceiver) 
     **/
    public  int			ReceiverThreads			= 4;

    /** creates a thin client connected to a Siena server.

	@param server the uri of the server to connect to 
                      (e.g., "senp://host.domain.net:7654")
    **/
    public ThinClient(String server) throws InvalidSenderException {
	master = default_sender_factory.createPacketSender(server);
	my_id = SienaId.getId();
	subscribers = new HashMap();
	Monitor.add_node(my_id.getBytes(), Monitor.ThinClientNode);
    }

    private synchronized byte[] mapSubscriber(Notifiable n) {
	if (n == null) return null;
	String newid = my_id + Integer.toString(n.hashCode());
	if (subscribers.put(newid, n) == null) {
	    //
	    // first time we see this subscriber, so we notify the monitor
	    //
	    Monitor.add_node(newid.getBytes(), Monitor.ObjectNode);
	    Monitor.connect(newid.getBytes(), my_id.getBytes());
	}
	return newid.getBytes();
    }

    private synchronized byte[] haveSubscriber(Notifiable n) {
	if (n == null) return null;
	String newid = my_id + Integer.toString(n.hashCode());
	if (subscribers.containsKey(newid)) {
	    return newid.getBytes();
	} else {
	    return null;
	}
    }

    private synchronized void removeSubscriber(Notifiable n) {
	if (n != null) {
	    String sid = my_id + Integer.toString(n.hashCode());
	    subscribers.remove(sid);
	    Monitor.remove_node(sid.getBytes());
	}
    }

    private synchronized Notifiable mapSubscriber(byte[] id) {
	if (id == null) return null;
	return (Notifiable)subscribers.get(new String(id));
    }

    /** sets the <em>packet receiver</em> for this server.  
     *
     *	A <em>packet receiver</em> accepts notifications,
     *	subscriptions, and other requests on some communication
     *	channel.  <code>setReceiver</code> will shut down any
     *	previously activated receiver for this dispatcher.  This
     *	method does not guarantee a transactional switch to a new
     *	receiver.  This means that some requests might get lost while
     *	the server has closed the old port and before it reopens the
     *	new port.
     *
     *  <p>This method simply calls {@link #setReceiver(PacketReceiver,
     *  int)} using {@link #ReceiverThreads} as a default value.
     *
     *	@param r is the receiver 
     *      		   
     *  @see #shutdown()
     *  @see #setReceiver(PacketReceiver, int) 
     **/
    public void setReceiver(PacketReceiver r) {
	setReceiver(r, ReceiverThreads);
    }

    /** sets the <em>packet receiver</em> for this server.  
     *
     *	A <em>packet receiver</em> accepts notifications,
     *	subscriptions, and other requests on some communication
     *	channel.  <code>setReceiver</code> will shut down any
     *	previously activated receiver for this dispatcher.  This
     *	method does not guarantee a transactional switch to a new
     *	receiver.  This means that some requests might get lost while
     *	the server has closed the old port and before it reopens the
     *	new port.
     *       
     *	@param r is the receiver 
     *  @param threads is the number of threads associated with the
     *         receiver, and therefore to the whole server.
     *      		   
     *  @see #shutdown()
     **/
    synchronized public void setReceiver(PacketReceiver r, int threads) {
	if (listener != null) {
	    try {
		listener.shutdown();
	    } catch (PacketReceiverException ex) {
		Logging.exerr(ex);
	    }
	    //
	    // this should send a PacketReceiverClosed exception to
	    // every thread that is waiting for packets on the old
	    // listener, which will make them exit normally.  However,
	    // because of bugs in the JVM, or because of bad
	    // implementations of packetReceiver, this might not be
	    // true.  ...work in progress...
	    //
	}
	listener = r;

	if (master != null && !subscribers.isEmpty()) {
	    pkt.init();
	    pkt.method = SENP.MAP;
	    pkt.id = my_id.getBytes();
	    pkt.handler = listener.uri();
	    try {
		master.send(pkt.buf, pkt.encode());
	    } catch (Exception ex) {
		Logging.exerr(ex);
		//
		// I should really do something here
		// ...work in progress...
		//
	    }
	}
	//
	// now fires off the threads that listen to this port
	//
	while (threads-- > 0) 
	    (new Thread(this)).start();
    }

    public void run() {
	SENPPacket rpkt = new SENPPacket();
	int res;
	while(true) {
	    try {
		PacketReceiver r = listener;
		if (r == null) return;
		res = r.receive(rpkt.buf);
		rpkt.init(res); 
		rpkt.decode();
		if (rpkt.ttl > 0) {
		    if (rpkt.method ==  SENP.PUB) {
			Notifiable n = mapSubscriber(rpkt.to);
			if (n != null) {
			    Monitor.notify(rpkt.id, my_id.getBytes());
			    Monitor.notify(my_id.getBytes(), rpkt.to);
			    if (rpkt.event != null) {
				n.notify(rpkt.event);
			    } else if (rpkt.events != null) {
				n.notify(rpkt.events);
			    }
			} else {
			    Logging.prlnerr("ThinClient: warning: unknown id: " + new String(rpkt.id));
			}
		    } else {
			Logging.prlnerr("ThinClient: warning: unable to handle method: " + rpkt.method);
		    }
		}
	    } catch (PacketReceiverClosed ex) {
		if (ex.getIOException() != null) 
		    Logging.exerr(ex);
		return;
	    } catch (PacketReceiverFatalError ex) {
		Logging.exerr(ex);
		return;
	    } catch (PacketReceiverException ex) {
		//
		// non fatal error: just log it and loop
		//
		Logging.exerr(ex);
	    } catch (Exception ex) {
		Logging.exerr(ex);
	    }
	}
    }

    /** suspends the delivery of notifications for a subscriber.
     *
     *	This causes the Siena server to stop sending notification to
     *	the given subscriber.  The server correctly maintains all the
     *	existing subscriptions so that the flow of notification can be
     *	later resumed (with {@link #resume(Notifiable)}). 
     *
     *	@see #resume(Notifiable) 
     **/
    synchronized public void suspend(Notifiable n) throws SienaException {
	pkt.id = haveSubscriber(n);
	if (pkt.id == null) return;
	pkt.method = SENP.SUS;
	pkt.to = master_handler;
	pkt.id = mapSubscriber(n);
	pkt.handler = listener.uri();
	master.send(pkt.buf, pkt.encode());
    }

    /** resumes the delivery of notifications for a subscriber.
     *
     *	This causes the Siena (master) server to resume sending
     *	notification to the given subscriber.
     *
     *	@see #suspend(Notifiable)
     **/
    synchronized public void resume(Notifiable n) throws SienaException {
	pkt.id = haveSubscriber(n);
	if (pkt.id == null) return;
	pkt.method = SENP.RES;
	pkt.to = master_handler;
	pkt.handler = listener.uri();
	master.send(pkt.buf, pkt.encode());
    }

    /** returns the <em>handler</em> of the Siena server associated
	with this dispatcher.
     
	@return URI of the master server.

	@see #ThinClient(String)
    **/
    synchronized public String getServer() {
	if (master_handler == null) return null;
	return new String(master_handler);
    }

    synchronized private void unsubscribeAll() {
	if (master != null) {
	    pkt.init();
	    pkt.method = SENP.BYE;
	    pkt.to = master_handler;
	    Iterator i = subscribers.keySet().iterator();
	    while(i.hasNext()) {
		try {
		    pkt.id = ((String)i.next()).getBytes();
		    master.send(pkt.buf, pkt.encode());
		} catch (Exception ex) {
		    Logging.exerr(ex);
		    //
		    // what should I do here?
		    // ...work in progress...
		    //
		}
	    }
	    subscribers.clear();
	    master = null;
	    master_handler = null;
	}
    }

    synchronized public void publish(Notification n) throws SienaException {
	if (n == null) return;

	pkt.init();
	pkt.event = n;
	pkt.method = SENP.PUB;
	pkt.id = my_id.getBytes();
	pkt.to = master_handler;
	master.send(pkt.buf, pkt.encode());
    }

    public void subscribe(Filter f, Notifiable n) throws SienaException {
	if (n == null) return;
	if (f == null) {
	    //
	    // null filters are not allowed in subscriptions this is a
	    // design choice, we could accept null filters with the
	    // semantics of the universal filter: one that matches
	    // every notification
	    //
	    throw (new SienaException("null filter"));
	}
	if (listener == null) {
	    try {
		setReceiver(new TCPPacketReceiver(0));
	    } catch (IOException ex) {
		throw (new SienaException(ex.toString()));
	    }
	}
	pkt.init();
	pkt.filter = f;
	pkt.method = SENP.SUB;
	pkt.id = mapSubscriber(n);
	pkt.handler = listener.uri();
	pkt.to = master_handler;
	master.send(pkt.buf, pkt.encode());
    }

    public void subscribe(Pattern p, Notifiable n) throws SienaException {
	if (n == null) return;
	if (p == null) {
	    //
	    // null patterns are not allowed in subscriptions 
	    //
	    throw (new SienaException("null pattern"));
	}
	if (listener == null) {
	    try {
		setReceiver(new TCPPacketReceiver(0));
	    } catch (IOException ex) {
		throw (new SienaException(ex.toString()));
	    }
	}
	pkt.init();
	pkt.pattern = p;
	pkt.method = SENP.SUB;
	pkt.id = mapSubscriber(n);
	pkt.handler = listener.uri();
	pkt.to = master_handler;
	master.send(pkt.buf, pkt.encode());
    }

    public void unsubscribe(Filter f, Notifiable n) throws SienaException {
	if (n == null || listener == null) return;

	pkt.init();
	pkt.id = haveSubscriber(n);
	if (pkt.id != null) {
	    pkt.handler = listener.uri();
	    pkt.to = master_handler;
	    if (f == null) {
		removeSubscriber(n);
		pkt.method = SENP.BYE;
	    } else {
		pkt.method = SENP.UNS;
		pkt.filter = f;
	    }
	    master.send(pkt.buf, pkt.encode());
	}
    }

    public void unsubscribe(Pattern p, Notifiable n) throws SienaException {
	if (n == null || listener == null) return;

	pkt.init();
	pkt.id = haveSubscriber(n);
	if (pkt.id != null) {
	    pkt.handler = listener.uri();
	    pkt.to = master_handler;
	    if (p == null) {
		removeSubscriber(n);
		pkt.method = SENP.BYE;
	    } else {
		pkt.method = SENP.UNS;
		pkt.pattern = p;
	    }
	    master.send(pkt.buf, pkt.encode());
	}
    }

    /** closes this dispatcher.
     
        If this dispatcher has an active listener then closes the
        active listener.  It also unsubscribes everything with its
        master server.
      
        @see #setReceiver(PacketReceiver) 
     **/
    synchronized public void shutdown() {
	unsubscribeAll();
	if (listener != null) 
	    try {
		listener.shutdown();
	    } catch (PacketReceiverException ex) {
		Logging.exerr(ex);
	    }
	listener = null;
    }

    synchronized public void advertise(Filter f, String id) 
	throws SienaException {
	if (id == null) return;
	//
	// I haven't thought about what to do here.
	//
	if (f == null) {
	    //
	    // I haven't thought about what to do here.
	    //
	    throw (new SienaException("null filter"));
	}
	pkt.init();
	pkt.filter = f;
	pkt.method = SENP.ADV;
	pkt.id = id.getBytes();
	pkt.handler = listener.uri();
	pkt.to = master_handler;
	master.send(pkt.buf, pkt.encode());
    }

    synchronized public void unadvertise(Filter f, String id) 
	throws SienaException {
	if (id == null) return;
	//
	// I haven't thought about what to do here.
	//
	pkt.init();
	pkt.id = id.getBytes();
	pkt.handler = listener.uri();
	pkt.to = master_handler;
	pkt.method = SENP.UNA;
	pkt.filter = f;
	// 
	// should I have a special method for UNA all (when f == null)?
	// ...work in progress...
	//
	master.send(pkt.buf, pkt.encode());
    }

    public void unadvertise(String id) throws SienaException {
	unadvertise(null, id);
    }

    public void unsubscribe(Notifiable n) throws SienaException {
	unsubscribe((Filter)null, n);
    }
}
