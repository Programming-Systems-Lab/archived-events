//
//  This file is part of Siena, a wide-area event notification system.
//  See http://www.cs.colorado.edu/serl/siena/
//
//  Author: Antonio Carzaniga <carzanig@cs.colorado.edu>
//  See the file AUTHORS for full details. 
//
//  Copyright (C) 1998-2002 University of Colorado
//
//  This program is free software; you can redistribute it and/or
//  modify it under the terms of the GNU General Public License
//  as published by the Free Software Foundation; either version 2
//  of the License, or (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307,
//  USA, or send email to serl@cs.colorado.edu.
//
//
// $Id$
//

package siena;

import com.sun.java.util.collections.Collection;
import com.sun.java.util.collections.Set;
import com.sun.java.util.collections.HashSet;
import com.sun.java.util.collections.Map;
import com.sun.java.util.collections.Map.Entry;
import com.sun.java.util.collections.HashMap;
import com.sun.java.util.collections.List;
import com.sun.java.util.collections.LinkedList;
import com.sun.java.util.collections.Iterator;
import com.sun.java.util.collections.ListIterator;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

interface PacketNotifiable {
    //
    // returns false whenever the notification mechanism failed, and
    // therefore this notifiable could be considered unreachable (am I
    // making sense?  this sentence might sound a bit "italian", but I
    // don't have time to fix it now)
    //
    public boolean notify(SENPPacket pkt);
}

//
// this is the abstraction of the subscriber used by the
// HierarchicalDispatcher.  It represents remote as well as local
// notifiable objects.  In addition to that, this object keeps track
// of failed attempts to contact the notifiable object so that
// HierarchicalDispatcher can periodically clean up its subscriber
// tables
//
class Subscriber implements PacketNotifiable {
    public  short		failed_attempts = 0;
    public  long		latest_good	= 0;

    private boolean		suspended	= false;
    private Notifiable		localobj	= null;
    private PacketSender	remoteobj	= null;
    int				refcount	= 0;
    private SENPPacket		spkt		= new SENPPacket();
    public final byte[]		identity;

    public Subscriber(Notifiable n) {
	localobj = n;
	identity = null;
	suspended = false;
	latest_good = 0;
	failed_attempts = 0;
	refcount = 0;
    }

    public Subscriber(String id, Notifiable n) {
	localobj = n;
	identity = id.getBytes();
	suspended = false;
	latest_good = 0;
	failed_attempts = 0;
	refcount = 0;
    }

    public Subscriber(String id, PacketSender ps) 
    throws InvalidSenderException {
	remoteobj = ps;
	identity = id.getBytes();
	suspended = false;
	latest_good = 0;
	failed_attempts = 0;
	refcount = 0;
    }

    public Object getKey() {
	if (identity != null) return new String(identity);
	return localobj;
    }

    public boolean isLocal() {
	return localobj != null;
    }

    synchronized public void addRef() {
	++refcount;
    }

    synchronized public void removeRef() {
	if (refcount > 0) --refcount;
    }

    synchronized public boolean hasNoRefs() {
	return refcount == 0;
    }

    synchronized public void mapHandler(PacketSender ps) 
	throws InvalidSenderException {
	remoteobj = ps;
	localobj = null;
	suspended = false;
	latest_good = 0;
	failed_attempts = 0;
    }

    synchronized public void mapHandler(Notifiable n) {
	remoteobj = null;
	localobj = n;
	suspended = false;
	latest_good = 0;
	failed_attempts = 0;
    }

    synchronized public void suspend() {
	suspended = true;
    }

    synchronized public void resume() {
	suspended = false;
    }

    public int hashCode() {
	return (identity==null) ? 
	    ((localobj == null) ? remoteobj.hashCode() : localobj.hashCode()) 
	    : identity.hashCode();
    }

    public boolean isUnreachable() {
	//
	// the master should never be considered unreachable
	//
	return false;
    }

    private void handleNotifyError(Exception ex) {
	if (failed_attempts == 0) latest_good = System.currentTimeMillis();
	++failed_attempts;
	if (localobj != null) {
	    Logging.prlnerr("error (" + failed_attempts 
			    + ") notifying local client " + localobj.toString());
	} else {
	    Logging.prlnerr("error (" + failed_attempts 
			    + ") sending packet to " + remoteobj.toString());
	}
	Logging.prlnerr(ex.toString());
    }

    synchronized public boolean notify(SENPPacket pkt) {
	if (suspended) return true;
	try {
	    if (localobj != null) {
		//		localobj.notify(new Notification(pkt.event));
		localobj.notify(pkt.event);
	    } else {
		remoteobj.send(pkt.buf, pkt.encode());
	    }
	    failed_attempts = 0;
	    return true;
	} catch (Exception ex) {
	    handleNotifyError(ex);
	    return false;
	}
    }

    synchronized public void notify(Notification n, byte[] our_id) {
	if (suspended) return;
	try {
	    if (localobj != null) {
		localobj.notify(n);
	    } else {
		spkt.init();
		spkt.id = our_id;
		spkt.method = SENP.PUB;
		spkt.event = n;
		spkt.to = identity;
		remoteobj.send(spkt.buf, spkt.encode());
	    }
	} catch (Exception ex) {
	    handleNotifyError(ex);
	}
    }

    synchronized public void notify(Notification [] s, byte[] our_id) {
	if (suspended) return;
	try {
	    if (localobj != null) {
		//
		// here I purposely do not duplicate the sequence for
		// efficiency reasons.  Clients should never modify
		// objects passed through notify().
		//
		localobj.notify(s);
	    } else {
		spkt.init();
		spkt.id = our_id;
		spkt.method = SENP.PUB;
		spkt.events = s;
		spkt.to = identity;
		remoteobj.send(spkt.buf, spkt.encode());
	    }
	} catch (Exception ex) {
	    handleNotifyError(ex);
	}
    }

    synchronized public long getMillisSinceGood() {
	if (suspended || failed_attempts == 0) return 0;
	return System.currentTimeMillis() - latest_good;
    }

    synchronized public short getFailedAttempts() {
	if (suspended) return 0;
	return failed_attempts;
    }

    public String toString() {
	return getKey().toString();
    }
}

class SubscriberIterator {
    private Iterator si;
    
    SubscriberIterator(Iterator i) {
	si = i;
    }

    public boolean hasNext() {
	return si.hasNext();
    }

    public Subscriber next() {
	return (Subscriber)si.next();
    }
}

class SSet {
    protected Set subs;
    
    public SSet() {
	subs = new HashSet();
    }
    
    public boolean addAll(SSet s) {
	return subs.addAll(s.subs);
    }

    public boolean add(Subscriber s) {
	return subs.add(s);
    }

    public boolean contains(Subscriber s) {
	return subs.contains(s);
    }

    public boolean remove(Subscriber s) {
	return subs.remove(s);
    }

    public boolean isEmpty() {
	return subs.isEmpty();
    }

    public SubscriberIterator iterator() {
	return new SubscriberIterator(subs.iterator());
    }
}

class RefSSet extends SSet {
    public RefSSet() {
	super();
    }

    public boolean addAll(SSet s) {
	boolean result = false;
	SubscriberIterator si;
	for(si = s.iterator(); si.hasNext();) {
	    Subscriber sub = si.next();
	    if (subs.add(sub)) {
		sub.addRef();
		result = true;
	    }
	}
	return result;
    }

    public boolean add(Subscriber s) {
	if (subs.add(s)) {
	    s.addRef();
	    return true;
	} else {
	    return false;
	}
    }

    public boolean remove(Subscriber s) {
	if (subs.remove(s)) {
	    s.removeRef();
	    return true;
	} else {
	    return false;
	}
    }

    protected void finalize() throws Throwable {
	SubscriberIterator si;
	for(si = this.iterator(); si.hasNext();)
	    si.next().removeRef();
    }
}


class Subscription {
    public Set		preset;
    public Set		postset;

    public final Filter	filter;
    public RefSSet subscribers;

    public Subscription(Filter f) {
	preset = new HashSet();
	postset = new HashSet();
	filter = new Filter(f);
	subscribers = new RefSSet(); 
    }

    public Subscription(Filter f, Subscriber s) {
	preset = new HashSet();
	postset = new HashSet();
	filter = new Filter(f);
	subscribers = new RefSSet();
	subscribers.add(s);
    }

    public String toString() {
	Iterator i;
	StringBuffer sb = new StringBuffer();
	sb.append("SUB: " + Integer.toString(hashCode()));
	sb.append("\npreset:");
	for(i = preset.iterator(); i.hasNext();)
	    sb.append(" "+Integer.toString(i.next().hashCode()));
	sb.append("\npostset:");
	for(i = postset.iterator(); i.hasNext();)
	    sb.append(" "+Integer.toString(i.next().hashCode()));
	sb.append("\n" + filter.toString());
	sb.append("\nsubscribers:");
	SubscriberIterator si;
	for(si = subscribers.iterator(); i.hasNext();)
	    sb.append(" "+Integer.toString(i.next().hashCode()));
	sb.append("\n");
	return sb.toString();
    }
}

class Poset {
    private Set		roots;
    
    public Poset() {
	roots = new HashSet();
    }

    public void clear() {
	roots.clear();
    }

    public boolean is_root(Subscription s) {
	return s.preset.isEmpty();
    }

    public boolean empty() {
	return roots.isEmpty();
    }

    public Iterator rootsIterator() {
	return roots.iterator();
    }

    synchronized public void insert(Subscription new_sub, 
				    Collection pre, Collection post) {
	//
	// inserts new_sub into the poset between pre and post.  the
	// connections are rearranged in order to maintain the
	// properties of the poset
	//
	Subscription x; // x always represents something in the preset
	Subscription y; // y has to do with the postset
	Iterator xi, yi;

	if (pre.isEmpty()) {
	    roots.add(new_sub);
	} else {
	    xi = pre.iterator();
	    while(xi.hasNext()) {
		x = (Subscription)xi.next();
		yi = post.iterator();
		while(yi.hasNext()) 
		    disconnect(x, (Subscription)yi.next());
		connect(x, new_sub);
	    }
	}
	yi = post.iterator();
	while(yi.hasNext()) {
	    y = (Subscription)yi.next();
	    connect(new_sub, y);
	}
    }

    synchronized public void disconnect(Subscription x, Subscription y) {
	if (x.postset.remove(y))
	    y.preset.remove(x);
    }

    synchronized public void connect(Subscription x, Subscription y) {
	if (x.postset.add(y))
	    y.preset.add(x);
    }

    synchronized public Set remove(Subscription s) {
	//
	// removes s from the poset returning the set of root
	// subscription uncovered by s
	//
	Set result = new HashSet();
	Subscription x, y;
	Iterator xi, yi;
	//
	// 1. disconnect s from every successor of s but maintains s.postset
	//
	yi = s.postset.iterator();
	while(yi.hasNext()) {
	    y = (Subscription)yi.next();
	    y.preset.remove(s);
	}

	if (s.preset.isEmpty()) {
	    //
	    // 2.1 if s is a root subscription, adds as a root every
	    // successor that remains with an empty preset, i.e.,
	    // every subscription that was a successor of s and that
	    // is now a root subscription...
	    //
	    yi = s.postset.iterator();
	    while(yi.hasNext()) {
		y = (Subscription)yi.next();
		if (y.preset.isEmpty()) {
		    roots.add(y);
		    result.add(y);
		}
	    }
	    roots.remove(s);
	} else {
	    //
	    // 2.2 disconnects every predecessor of s thereby reconnecting
	    // predecessors to successors. A predecessor X is re-connected
	    // to a successor Y only if X does not have an immediate
	    // successor X' that covers Y (see is_indirect_successor).
	    //
	    xi = s.preset.iterator();
	    while(xi.hasNext()) {
		x = (Subscription)xi.next();
		x.postset.remove(s);
		yi = s.postset.iterator();
		while(yi.hasNext()) {
		    y = (Subscription)yi.next();
		    if (!is_indirect_successor(x, y))
			connect(x, y);
		}
	    }
	}
	return result;
    }

    synchronized public boolean is_indirect_successor(Subscription x, 
						      Subscription y) {
	//
	// says whether x indirectly covers y.
	//
	Iterator i = x.postset.iterator();
	while(i.hasNext()) 
	    if (Covering.covers(((Subscription)i.next()).filter, y.filter))
		return true;
	return false;
    }

    synchronized public Set predecessors(Filter f, Subscriber s) {
	//
	// computes the set of immediate predecessors of filter f that
	// do not contain subscriber s.  If the poset contains any
	// subscription covering f that already contains s, then this
	// function returns null.  Otherwise, it returns the
	// collection of predecessors of f.  If the poset contains a
	// filter f'=f, the result will be a set containing that
	// (only) subscription.
	// 
	LinkedList to_visit = new LinkedList();
	Set visited = new HashSet();
	Subscription sub, y;
	Iterator i = roots.iterator();
	boolean found_lower;

	while(i.hasNext()) {
	    sub = (Subscription)i.next();
	    if (Covering.covers(sub.filter, f)) {
		if (sub.subscribers.contains(s)) {
		    return null;
		} else {
		    to_visit.addLast(sub);
		}
	    }
	}
	Set result = new HashSet();

	ListIterator li;
	while((li = to_visit.listIterator()).hasNext()) {
	    sub = (Subscription)li.next();
	    li.remove();
	    i = sub.postset.iterator();
	    found_lower = false;
	    while(i.hasNext()) {
		y = (Subscription)i.next();
		if (visited.add(y)) {
		    if (Covering.covers(y.filter, f)) {
			found_lower = true;
			if (sub.subscribers.contains(s)) {
			    return null;
			} else {
			    to_visit.addLast(y);
			}
		    }
		} else if (!found_lower) {
		    if (Covering.covers(y.filter, f))
			found_lower = true;
		}
	    }
	    if(!found_lower) result.add(sub); 
	}
	return result;
    }

    synchronized public SSet matchingSubscribers(Notification e) {
	//
	// computes the set of subscribers that are interested in e.
	// This includes the subscribers of all the subscriptions in
	// the poset that match e
	//
	SSet result = new SSet();
	Iterator i = roots.iterator();
	LinkedList to_visit = new LinkedList();
	Set visited = new HashSet();
	Subscription sub;

	while(i.hasNext()) {
	    sub = (Subscription)i.next();
	    if (Covering.apply(sub.filter, e)) {
		to_visit.addLast(sub);
		result.addAll(sub.subscribers);
	    }
	}

	ListIterator li;
	while((li = to_visit.listIterator()).hasNext()) {
	    sub = (Subscription)li.next();
	    li.remove();
	    i = sub.postset.iterator();
	    while(i.hasNext()) {
		Subscription y = (Subscription)i.next();
		if (visited.add(y) && Covering.apply(y.filter, e)) {
		    to_visit.addLast(y);
		    result.addAll(y.subscribers);
		}
	    }
	}
	return result;
    }

    synchronized public Set successors(Filter f, Collection pred) {
	//
	// given a filter f and a set pred, the set of the immediate
	// predecessors of f in the poset, computes the set of
	// immediate successors of f in the poset.
	//
	// Idea: I must walk through the sub-poset of of this poset
	// that is covered by the set of predecessors (the whole poset
	// if there are no predecessors), looking for filters f1, f2,
	// ..., fn that are covered by f.  I do that by using a queue
	// of filters to_visit.
	// 
	// to_visit is initialized with the element of pred or with
	// the root filters. For every f' in to_visit, if f' is covered
	// by f then I remove f' from to_visit and I add it to the
	// result.  If f' is not covered by f, then I add all its
	// successors that I haven't visited to to_visit.
	//
	LinkedList to_visit = new LinkedList();
	Set visited = new HashSet();
	Subscription sub, y;
	Iterator i;
	//
	// initialize to_visit
	//
	if (pred == null || pred.isEmpty()) {
	    to_visit.addAll(roots);
	} else {
	    i = pred.iterator();
	    while(i.hasNext()) {
		to_visit.addLast((Subscription)i.next());
	    }
	}
	visited.addAll(to_visit);
	Set result = new HashSet();
	ListIterator li;
	while((li = to_visit.listIterator()).hasNext()) {
	    sub = (Subscription)li.next();
	    li.remove();
	    if (Covering.covers(f, sub.filter)) {
		result.add(sub);
	    } else {
		i = sub.postset.iterator();
		while(i.hasNext()) {
		    y = (Subscription)i.next();
		    if (visited.add(y)) {
			to_visit.addLast(y);
		    }
		}
	    }
	}

	return result;
    }

    synchronized public Subscription insert_subscription(Filter f, 
							 Subscriber s) {
	//
	// inserts a subscription in the poset
	//
	Set pred = predecessors(f,s);
	Subscription sub; 
	if (pred == null) return null;
	if (pred.size() == 1) {
	    sub = (Subscription)pred.iterator().next();
	    if (Covering.covers(f,sub.filter)) {
		//
		// pred contains exactly f, so simply add s to the set
		// of subscribers
		//
		sub.subscribers.add(s);
		clear_subposet(sub, s);
		return sub;
	    }
	}
	sub = new Subscription(f, s);
	insert(sub, pred, successors(f, pred));
	clear_subposet(sub, s);
	return sub;
    }

    synchronized public void clear_subposet(Subscription start, Subscriber s) {
	//
	// removes subscriber s from all the subscriptions covered by
	// start, excluding start itself.  This also removes
	// subscriptions that remain with no subscribers.
	//
	LinkedList to_visit = new LinkedList();
	Set visited = new HashSet();

	to_visit.addAll(start.postset);

	ListIterator li;
	while((li = to_visit.listIterator()).hasNext()) {
	    Subscription sub = (Subscription)li.next();
	    li.remove();
	    if (visited.add(sub)) {
		if (sub.subscribers.remove(s)) {
		    if (sub.subscribers.isEmpty()) remove(sub);
		} else {
		    to_visit.addAll(sub.postset);
		}
	    }
	}
    }

    synchronized public Set to_remove(Filter f, Subscriber s) {
	//
	// removes subscriber s from the subscriptions covered by f.
	// If f==null, it removes s from all the subscriptions in the
	// poset. Returns the set of empty subscriptions, i.e., those
	// that remain with no subscribers.
	//
	Set result = new HashSet();
	LinkedList to_visit = new LinkedList();
	Subscription sub;

	if (f == null) {
	    //
	    // f==null ==> universal filter (same thing as BYE)
	    // so my starting point is the set of root subscriptions
	    //
	    to_visit.addAll(roots);
	} else {
	    Set pred = predecessors(f, s);
	    if (pred != null && pred.size() == 1) {
		sub = (Subscription)pred.iterator().next();
		if (Covering.covers(f,sub.filter)) {
		    //
		    // pred contains exactly f, so remove s and see if
		    // f remains with no subscribers
		    //
		    if (sub.subscribers.remove(s)) {
			if (sub.subscribers.isEmpty()) 
			    result.add(sub);
		    }

		    return result;
		}
	    }
	    to_visit.addAll(successors(f, pred));
	}

	Set visited = new HashSet();
	ListIterator li;

	while((li = to_visit.listIterator()).hasNext()) {
	    sub = (Subscription)li.next();
	    li.remove();
	    if (visited.add(sub)) {
		if (sub.subscribers.remove(s)) {
		    if (sub.subscribers.isEmpty()) 
			result.add(sub);
		} else {
		    to_visit.addAll(sub.postset);
		}
	    }
	}
	return result;
    }

    synchronized public void print(PrintStream out) {
	//
	// prints the poset
	//
	LinkedList to_visit = new LinkedList();
	Set visited = new HashSet();

	to_visit.addAll(roots);

	ListIterator li;
	while((li = to_visit.listIterator()).hasNext()) {
	    Subscription sub = (Subscription)li.next();
	    li.remove();
	    if (visited.add(sub)) {
		out.println(sub.toString());
		to_visit.addAll(sub.postset);
	    }
	}
    }
}

class Publication {
    public SENPPacket		packet;
    public PacketNotifiable	handler;
    public Publication		next;

    public Publication(SENPPacket p, PacketNotifiable s) {
	packet = p;
	handler = s;
    }
}

class EmptyPatternException extends SienaException {
    // ...work in progress...
}

//
// we implement pattern recognition with PatternMatcher.  A
// PatternMatcher is a ``parser'' of event sequences.  A
// PatternMatcher receives notifications (single events) from Siena
// and holds a list of subscribers.  When a PatternMatcher recognizes
// a pattern, it notifies the list of subscribers passing the matching
// sequence of events.
//
class PatternMatcher implements Notifiable {
    public RefSSet		subscribers	= null;
    public Pattern		pattern		= null;
    private LinkedList		notifications	= null;
    private byte[]		our_id;

    public PatternMatcher(Pattern p, byte[] id) {
	pattern = new Pattern(p);
	notifications = new LinkedList();
	subscribers = new RefSSet();
	our_id = id;
    }

    public void notify(Notification s[]) {}

    public void notify(Notification n) {
	//
	// This method receives the elementary components of a pattern
	// and does the matching.
	//
	// WARNING: this is my own naive matching algorithm.  It is
	// certainly not optimized and it might not even be correct!
	// I should look up Knuth-Morris-Pratt matching algorithm,
	// but I have no time now...
	//
	// ...work in progress... bigtime.
	//
	int curr = notifications.size();
	notifications.addLast(new Notification(n));
	//
	// pattern.length must be > 0 (see constructor)
	//
	if (Covering.apply(pattern.filters[curr], n)) {
	    if (++curr == pattern.filters.length) {
		//
		// MATCHED!  builds the array of notifications,
		//
		Notification sequence[] = new Notification[curr];
		while(--curr >= 0) 
		    sequence[curr] = (Notification)notifications.removeLast();
		//
		// notify subscribers
		//
		SubscriberIterator i = subscribers.iterator();
		while(i.hasNext()) 
		    i.next().notify(sequence, our_id);

	    }
	} else {
	    //
	    // no match here, I've got to backtrack...  try to cut the
	    // head of the queue of notifications and match a
	    // shorter (most recent) prefix of filters.
	    //
	    for(notifications.removeFirst(); !notifications.isEmpty(); 
		notifications.removeFirst()) {
		ListIterator li = notifications.listIterator();
		for(curr = 0; li.hasNext(); ++curr)
		    if (!Covering.apply(pattern.filters[curr], 
					(Notification)li.next())) break;
		if (!li.hasNext()) {
		    //
		    // found a shorter prefix
		    //
		    return;
		}
	    }
	}
    }
}

/** implementation of a Siena event notification service.
 *
 *  This is the primary implementation of the Siena event notification
 *  service. A <code>HierarchicalDispatcher</code> can serve as a
 *  Siena event service for local (same Java VM) clients as well as
 *  remote clients.  <code>HierarchicalDispatcher</code>s can also be
 *  combined in a distributed architecture with other dispatchers.
 *  Every dispatcher can be connected to a <em>master</em> dispatcher,
 *  thereby forming a hierarchical structure.  The hierarchy of
 *  dispatchers is assembled incrementally as new dispatchers are
 *  created and connected to a master that already belongs to the
 *  hierarchy.
 *  
 *  <p>A <code>HierarchicalDispatcher</code> uses a {@link
 *  PacketReceiver} to receive notifications, subscriptions and
 *  unsubscriptions from external clients and from its <em>master</em>
 *  dispatcher.  In order to receive and process external requests, a
 *  <code>HierarchicalDispatcher</code> can either use a pool of
 *  internal threads, or it can use users' threads.  See {@link
 *  #DefaultThreadCount}, {@link #setReceiver(PacketReceiver)}, and
 *  {@link #setReceiver(PacketReceiver, int)}
 *  
 *  @see Siena
 *  @see ThinClient
 **/
public class HierarchicalDispatcher implements Siena,  Runnable {
    private Poset			subscriptions	= new Poset();
    private Map				contacts	= new HashMap();
    private byte[]			master_id	= null;
    private byte[]			master_handler	= null;
    private PacketSender		master		= null;
    private PacketReceiver		listener	= null;
    private byte[]			my_identity	= null;
    private List			matchers	= new LinkedList();

    private SENPPacket			spkt = new SENPPacket();
    private byte []			sndbuf = new byte[SENP.MaxPacketLen];

    private PacketSenderFactory		sender_factory;
    static private PacketSenderFactory	default_sender_factory 
					  = new GenericSenderFactory();

    /** sets the packet-sender factory associated with this
     *  HierarchicalDispatcher
     *
     *  @see #setDefaultPacketSenderFactory(PacketSenderFactory)
     **/
    public void setPacketSenderFactory(PacketSenderFactory f) {
	sender_factory = f;
    }

    /** default packet-sender factory for HierarchicalDispatcher
     *  interfaces
     *
     *  every new HierarchicalDispatcher objects is assigned this
     *  factory
     *
     *  @see #setPacketSenderFactory(PacketSenderFactory)
     **/
    static public void setDefaultPacketSenderFactory(PacketSenderFactory f) {
	default_sender_factory = f;
    }

    /** default number of threads handling external requests.
     *
     *  Every HierarchicalDispatcher creates a pool of threads to read
     *  and process incoming requests.  This parameter determines the
     *  default number of threads in the pool.  The initial default
     *  value is 5. See {@link #setReceiver(PacketReceiver, int)} for
     *  the semantics of this value.  <code>DefaultThreadCount</code>
     *  is used to create threads upon call to {@link
     *  #setReceiver(PacketReceiver)}.
     *
     *	@see #setReceiver(PacketReceiver) 
     *	@see #setReceiver(PacketReceiver,int) 
     *	@see #setMaster(String)
     **/
    public  int			DefaultThreadCount = 5;

    /** number of failed notifications before a subscriber is
     *	implicitly disconnected.
     *
     *	The default value of <code>MaxFailedConnectionNumber</code> is 2.
     *
     *  HierachicalDispatcher implements a garbage collection
     *  mechanism for unreachable subscribers.  This mechanism
     *  implicitly unsubscribes a client when the dispatcher fails to
     *  connect to the client for a given number of times and after a
     *  given number of milliseconds.  More formally, the dispatcher
     *  considers the sequence of consecutive failed connections not
     *  followed by any successful connection.  This sequence is
     *  charachterized by two parameters: its <em>length</em> and its
     *  <em>duration</em>.<p>
     *
     *	<code>MaxFailedConnectionsNumber</code> represents the upper
     *	bound to the length of the sequence, while
     *	<code>MaxFailedConnectionsDuration</code> represents the upper
     *	bound to the duration of the sequence.  For both parameters, a
     *	negative value means
     *	<em>infinity</em>. <code>removeUnreachableSubscriber()</code>
     *	removes all the subscriptions of those subscribers that have
     *	not been reachable for more than
     *	<code>MaxFailedConnectionsNumber</code> times and more than
     *	<code>MaxFailedConnectionsDuration</code> milliseconds.
     *	Formally, a subscriber that has not been reachable for
     *	<em>T</em> milliseconds for <em>N</em> notifications will be
     *	removed according to the following conditions:
     *
     *	if (MaxFailedConnectionsNumber &gt;= 0 &amp;&amp; MaxFailedConnectionsDuration &gt;= 0) {
     *      if (T &gt; MaxFailedConnectionsDuration &amp;&amp; N &gt; MaxFailedConnectionsNumber)
     *          removeit!
     *	} else if (MaxFailedConnectionsNumber &gt;= 0) {
     *      if (N &gt; MaxFailedConnectionsNumber)
     *          remove it!
     *	} else if (MaxFailedConnectionsDuration &gt;= 0) {
     *	    if (T &gt; MaxFailedConnectionsDuration)
     *		remove it!
     *	}
     *
     *	@see #MaxFailedConnectionsDuration
     **/
    public  int			MaxFailedConnectionsNumber	= 2;

    /** milliseconds before automatic unsubscription is activated.
     *
     *  The default value of <code>MaxFailedConnectionsDuration</code>
     *  is 5000 (i.e., 5 seconds).
     *  
     *  @see #MaxFailedConnectionsNumber 
     **/
    public  long		MaxFailedConnectionsDuration	= 5000;
    
    /** creates a dispatcher with a specific <em>identity</em>.  
     *
     *	Every object involved in Siena communications must have a
     *	unique identity.  Therefore the scope of the identity of this
     *	dispatcher is the entire event notifications service.
     *
     *	@param id identity given to the dispatcher.  It is crucial
     *	          that the identity of a dispatcher is unique over the
     *	          whole event notification service.
     **/
    public HierarchicalDispatcher(String id) {
	my_identity = id.getBytes();
	sender_factory = default_sender_factory;
	Monitor.add_node(my_identity, Monitor.SienaNode);
    }

    /** creates a dispatcher with a randomly-generated
     *	(probabilistically unique) <em>identity</em>.
     **/
    public HierarchicalDispatcher() {
	my_identity = SienaId.getId().getBytes();
	sender_factory = default_sender_factory;
	Monitor.add_node(my_identity, Monitor.SienaNode);
    }


    /** process a single request, using the caller's thread.
     *
     *  The default value of <code>MaxFailedConnectionsDuration</code>
     *  is 5000 (i.e., 5 seconds).
     *  
     *  @see #DefaultThreadCount
     *  @see #setReceiver(PacketReceiver) 
     *  @see #setReceiver(PacketReceiver, int) 
     **/
    public void processOneRequest() throws SienaException {
	SENPPacket req = SENPPacket.allocate();
	try {
	    int res;
	    req.init();
	    res = listener.receive(req.buf);
	    Logging.prlnlog(new String(req.buf, 0, res));
	    req.init(res);
	    req.decode();
	    if (req != null) processRequest(req);
	} finally {
	    SENPPacket.recycle(req);
	}
    }
    
    public void run() {
	SENPPacket req = SENPPacket.allocate();
	int res;
	while(true) {
	    try {
		res = listener.receive(req.buf);
		req.init(res);
		req.decode();
		processRequest(req);
	    } catch (SENPInvalidFormat ex) {
		Logging.prlnerr("invalid request: " + ex.toString());
	    } catch (PacketReceiverClosed ex) {
		if (ex.getIOException() != null) 
		    Logging.prlnerr("error in packet receiver: " 
				    + ex.toString());
		SENPPacket.recycle(req);
		return;
	    } catch (PacketReceiverFatalError ex) {
		Logging.prlnerr("fatal error in packet receiver: " 
				+ ex.toString());
		SENPPacket.recycle(req);
		return;
	    } catch (PacketReceiverException ex) {
		Logging.prlnerr("non-fatal error in packet receiver: " 
				+ ex.toString());
	    }
	}
    }

    synchronized void removeUnreachableSubscriber(Subscriber sub) {
	if (MaxFailedConnectionsNumber < 0 && MaxFailedConnectionsDuration < 0)
	    return;

	boolean to_remove = false;
	if (MaxFailedConnectionsNumber >= 0 
	    && MaxFailedConnectionsDuration >= 0) {
	    //
	    // pay attention to both time and count (conjunction)
	    //
	    if (sub.getMillisSinceGood() > MaxFailedConnectionsDuration
		&& sub.getFailedAttempts() > MaxFailedConnectionsNumber)
		to_remove = true;
	} else if (MaxFailedConnectionsNumber >= 0) {
	    if (sub.getFailedAttempts() > MaxFailedConnectionsNumber)
		to_remove = true;
	} else if (MaxFailedConnectionsDuration >= 0) {
	    if (sub.getMillisSinceGood() > MaxFailedConnectionsDuration)
		to_remove = true;
	}
	if (to_remove) {
	    Logging.prlnlog("removing unreachable subscriber " 
			    + sub.toString());
	    unsubscribe(null, sub, null);
	}
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
     *  int)} using {@link #DefaultThreadCount} as a default value.
     *
     *	@param r is the receiver 
     *      		   
     *  @see #shutdown()
     *  @see #setReceiver(PacketReceiver, int) 
     **/
    public void setReceiver(PacketReceiver r) {
	setReceiver(r, DefaultThreadCount);
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
     *	@param r the packet receiver
     *  @param threads is the number of threads associated with the
     *  receiver, and therefore to the whole server.  A positive value
     *  causes this dispatcher to create threads.  A value of 0 causes
     *  the dispatcher not to create any thread, In this case, the
     *  application must explicitly call {@link #processOneRequest()}.
     *
     *  @see #shutdown()
     *  @see #setMaster(String) 
     **/
    synchronized public void setReceiver(PacketReceiver r, int threads) {
	if (listener != null) {
	    try {
		listener.shutdown();
	    } catch (PacketReceiverException ex) {
		Logging.exerr(ex);
		//
		// ...work in progress...
		//
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

	if (master != null) {
	    spkt.init();
	    spkt.method = SENP.MAP;
	    spkt.id = my_identity;
	    spkt.handler = listener.uri();
	    try {
		master.send(spkt.buf, spkt.encode());
	    } catch (Exception ex) {
		Logging.prlnerr("error sending packet to " 
				+ master.toString());
		Logging.exerr(ex);
		//
		// I should really do something here
		// ...work in progress...
		//
	    }
	}
	//
	// now fires off the reader threads for this dispatcher
	//
	while (threads-- > 0) {
	    Thread t = new Thread(this);
	    //
	    // Perhaps I should set t as a deamon thread...
	    //
	    t.start();
	}
    }
    
    /** creates a TCP-based <em>packet receiver</em> for this server.
     *
     *	@deprecated as of Siena 1.1.0, replaced by {@link
     *              #setReceiver(PacketReceiver) setReceiver()}
     *
     *  @param port is the port number allocated by the listener,
     *	            <code>0</code> allocates a random available port
     *	            number, the default value for Siena is
     *	            <code>SENP.DEFAULT_PORT</code>
     *      		   
     *  @exception IOException when the given port is already in use	   
     **/
    synchronized public void setListener(int port) throws IOException {
	setReceiver(new TCPPacketReceiver(port));
    }


    /** connects this dispatcher to a <em>master</em> dispatcher.
     *	
     *	If this dispatcher is already connected to a master
     *	dispatcher, <code>setMaster</code> disconnects the old one and
     *	connects the new one, thereby unsubscribing all the top-level
     *	subscriptions and resubscribing with the new one.  This method
     *	should be used only when this dispatcher <em>needs</em> to
     *	switch to a different master, it is not necessary (it is in
     *	fact very inefficient) to set the master before every
     *	subscription or notification.
     *
     *	<p>This method does not guarantee a transactional switch.  This
     *	means that some notifications might be lost when the server
     *	has detached from the old master and before it re-subscribes
     *	with the new master.
     *
     *	<p>If this dispatcher does not have a <em>packet receiver</em>
     *	associated with it, <code>setMaster</code> implicitly sets up
     *	one for the dispatcher.  The default receiver is a
     *	<code>TCPPacketReceiver</code> listening to a randomly
     *	allocated port.  If you are not happy with the default
     *	decision, you should call <code>setReceiver()</code> before
     *	you call <code>setMaster</code>.
     *
     *	@param uri is the external identifier of the master dispatcher
     *	           (e.g., * <code>"senp://host.domain.edu:8765"</code>)
     *  @see #setReceiver(PacketReceiver)
     *  @see #shutdown() 
     **/
    synchronized public void setMaster(String uri) 
	throws InvalidSenderException, java.io.IOException {

	PacketSender new_master = sender_factory.createPacketSender(uri);

	disconnectMaster();
	boolean new_listener = false;
	if (listener == null) {
	    setReceiver(new TCPPacketReceiver(0));
	    new_listener = true;
	}
	master_handler = uri.getBytes();
	master = new_master;
	//
	// sends a WHO packet to figure out the identity of the master
	// server.  This dispatcher uses the "to" field of the SENP
	// packet to tell the master server the handler used by this
	// server to reach the master server.  (see reply_who())
	//
	try {
	    spkt.init();
	    spkt.method = SENP.WHO;
	    spkt.ttl = 2;			// round-trip
	    spkt.to = master_handler;
	    spkt.id = my_identity;
	    spkt.handler = listener.uri();
	    master.send(spkt.buf, spkt.encode());
	    //
	    // perhaps I should sit here waiting for the INF response
	    // of the server
	    //
	    // ...to be continued...
	    //
	} catch (Exception ex) {
	    Logging.prlnerr("error sending packet to " + master.toString());
	    Logging.exerr(ex);
	    master = null;
	    master_handler = null;
	    if (new_listener) {
		try {
		    listener.shutdown();
		} catch (PacketReceiverException pex) {
		    Logging.exerr(pex);
		}
	    }
	    //
	    // of course I should do something here...
	    // ...work in progress...
	    //
	}
	//
	// sends all the top-level subscriptions to the new master
	//
	for(Iterator i = subscriptions.rootsIterator(); i.hasNext();) {
	    Subscription s = (Subscription)i.next();
	    try {
		spkt.init();
		spkt.method = SENP.SUB;
		spkt.ttl = SENP.DefaultTtl;
		spkt.id = my_identity;
		spkt.handler = listener.uri();
		spkt.filter = s.filter;
		master.send(spkt.buf, spkt.encode());
	    } catch (Exception ex) {
		Logging.prlnerr("error sending packet to " + master.toString());
		Logging.exerr(ex);
		//
		// of course I should do something here...
		// ...work in progress...
		//
	    }
	}
    }

    /** suspends the connection with the <em>master</em> server of
     *	this dispatcher.  
     *
     *	This causes the <em>master</em> server to stop sending
     *	notification to this dispatcher.  The master correctly
     *	maintains all the existing subscriptions so that the flow of
     *	notification can be later resumed (see {@link #resumeMaster()
     *	resumeMaster}.  This operation can be used when this
     *	dispatcher, that is this virtual machine, is going to be
     *	temporarily disconnected from the network or somehow
     *	unreachable from its master server.
     *
     *	@see #resumeMaster() 
     **/
    synchronized public void suspendMaster() {
	try {
	    spkt.init();
	    spkt.method = SENP.SUS;
	    spkt.to = master_handler;
	    spkt.id = my_identity;
	    spkt.handler = listener.uri();
	    master.send(spkt.buf, spkt.encode());
	} catch (Exception ex) {
	    Logging.prlnerr("error sending packet to " + master.toString());
	    Logging.exerr(ex);
	    //
	    // of course I should do something here...
	    // ...work in progress...
	    //
	}
    }

    /** resumes the connection with the <em>master</em> server.
     *
     *	This causes the <em>master</em> server to resume sending
     *	notification to this dispatcher.
     *
     *	@see #suspendMaster()
     **/
    synchronized public void resumeMaster() {
	try {
	    spkt.init();
	    spkt.method = SENP.RES;
	    spkt.to = master_handler;
	    spkt.id = my_identity;
	    spkt.handler = listener.uri();
	    master.send(spkt.buf, spkt.encode());
	} catch (Exception ex) {
	    Logging.prlnerr("error sending packet to " + master.toString());
	    Logging.exerr(ex);
	    //
	    // of course I should do something here...
	    // ...work in progress...
	    //
	}
    }

    /** returns the identity of this dispatcher.
     *
     *  every object in a Siena network has a unique identifier.  This
     *  method returns the identifier of this dispatcher.  
     *
     *  @see #HierarchicalDispatcher(String)
     **/
    synchronized public String getIdentity() {
	return new String(my_identity);
    }

    /** returns the <em>uri</em> of the master server associated
     *	with this dispatcher.
     *     
     *  @return URI of the master server or <code>null</code> if the
     *		master server is not set
     *
     *  @see #setMaster(String)
     **/
    synchronized public String getMaster() {
	if (master_handler == null) return null;
	return new String(master_handler);
    }

    /** returns the listener associated with this dispatcher.
     *
     *	@return receiver of this dispatcher (possibly null)
     *     
     *  @see #setReceiver(PacketReceiver)
     **/
    synchronized public PacketReceiver getReceiver() {
	return listener;
    }

    synchronized private void disconnectMaster() {
	if (master != null) {
	    try {
		spkt.init();
		spkt.method = SENP.BYE;
		spkt.id = my_identity;
		spkt.to = master_handler;
		master.send(spkt.buf, spkt.encode());
	    } catch (PacketSenderException ex) {
		Logging.prlnerr("error sending packet to " 
				+ master.toString() + ": " + ex.toString());
		//
		// well, what would you do in this case?
		// ...work in progress...
		//
	    }
	    master = null;
	    master_handler = null;
	}
    }

    private void processRequest(SENPPacket req) {
	Logging.prlnlog("processRequest: " + req);
	if (req == null) {
	    Logging.prlnerr("processRequest: null request");
	    return;
	}
		
	if (req.ttl <= 0) return;
	req.ttl--;
	try {
	    switch(req.method) {
	    case SENP.PUB: publish(req); break;
	    case SENP.SUB: subscribe(req); break;
	    case SENP.BYE: req.pattern = null; req.filter = null;
	    case SENP.UNS: unsubscribe(req); break;
	    case SENP.WHO: reply_who(req); break;
	    case SENP.INF: get_info(req); break;
	    case SENP.SUS: suspend(req); break;
	    case SENP.RES: resume(req); break;
	    case SENP.MAP: map(req); break;
	    case SENP.CNF: configure(req); break;
	    case SENP.OFF: 
		shutdown(); 
		//
		// BEGIN_UNOFFICIAL_PATCH
		try { Thread.sleep(500); } catch (Exception ex) {};
		System.exit(0);
		// END_UNOFFICIAL_PATCH
		//
		break;
	    case SENP.NOP: break;
	    default:
		Logging.prlnerr("processRequest: unknown method: " + req);
		//
		// can't handle this request (yet)
		// ...work in progress...
		//
	    }
	} catch (Exception ex) {
	    Logging.exerr(ex);
	    //
	    // log something here ...work in progress...
	    //
	}
    }
    
    synchronized private void configure(SENPPacket req) {
	if (req.ttl == 0) return;
	if (req.handler == null) {
	    Logging.prlnlog("reconfigure: disconnecting form master");
	    disconnectMaster();
	} else {
	    try {
		String new_master = new String(req.handler);
		Logging.prlnlog("reconfigure: switching to master " 
				+ new_master);
		setMaster(new_master);
	    } catch (Exception ex) {
		Logging.prlnerr("configure: failed reconfiguration request: " 
				+ ex.toString());
	    }
	}
    }

    synchronized private void map(SENPPacket req) {
	if (req.id == null || req.ttl == 0 || req.handler == null) return;
	Subscriber s = (Subscriber)contacts.get(new String(req.id));
	if (s != null) 
	    try {
		s.mapHandler(sender_factory.createPacketSender(new String(req.handler)));
	    } catch (InvalidSenderException ex) {
		Logging.prlnerr("error while mapping handler " 
				+ new String(req.handler) + ": " + ex);
		Logging.prlnerr("client not remapped");
	    }
    }

    synchronized private void suspend(SENPPacket req) {
	if (req.id == null || req.ttl == 0) return;
	Subscriber s = (Subscriber)contacts.get(new String(req.id));
	if (s != null) s.suspend();
    }

    synchronized private void resume(SENPPacket req) {
	if (req.id == null || req.ttl == 0) return;
	Subscriber s = (Subscriber)contacts.get(new String(req.id));
	if (s != null) s.resume();
    }

    private void reply_who(SENPPacket req) {
	if (req.handler == null || req.ttl == 0) return;
	try {
	    PacketSender ps;
	    ps = sender_factory.createPacketSender(new String(req.handler));
	    
	    req.method = SENP.INF;
	    req.id = my_identity;
	    //
	    // it is important that this dispatcher returns the
	    // handler used by the client, as opposed to this
	    // listener's handler, so that the client can match
	    // handler and identity.
	    //
	    req.handler = req.to;
	    req.to = null;
	    ps.send(req.buf, req.encode());
	} catch (Exception ex) {
	    Logging.prlnerr("can't contact handler: " 
			    + new String(req.handler) + ": " + ex.toString());
	}
    }

    synchronized private void get_info(SENPPacket req) {
	if (req.handler == null || req.id == null) return;
	//
	// right now, the dispatcher uses INF (info) packets (only) to
	// figure out the identity of its master server...  ...so this
	// test is not strictly necessary.  It's here to prevent
	// spurious (or malicious) INF packets from screwing up this
	// dispatcher
	//
	if (SENP.match(req.handler, master_handler)) {
	    master_id = req.id;
	    Monitor.connect(my_identity, master_id);
	} else {
	    Logging.prlnerr("Warning: unknown handler in INF message: "
			    + req + "\nexpecting: `"+master_handler+"'");
	}
    }

    private void publish(SENPPacket req) {
	if (req.event == null) {
	    Logging.prlnerr("Warning: null event in PUB message: "+req);
	    return;
	}
	byte[] sender = req.id;
	req.id = my_identity;
	if (sender != null) Monitor.notify(sender, my_identity);
	//
	// first forward to master
	//
	if (master != null  && req.ttl > 0 && 
	    (sender == null || !SENP.match(sender, master_id))) {
		req.to = (master_id != null) ? master_id : master_handler;
		try {
		    master.send(req.buf, req.encode());
		} catch (Exception ex) {
		    Logging.prlnerr("error sending packet to master " 
				    + master.toString());
		    Logging.prlnerr(ex.toString());
		}
	    }
	//
	// then find all the interested subscribers
	//
	SubscriberIterator i;
	i = subscriptions.matchingSubscribers(req.event).iterator();
	while(i.hasNext()) {
	    Subscriber s = i.next();
	    if ((sender == null || !SENP.match(sender, s.identity)) && 
		(req.ttl > 0 || s.isLocal())) {
		req.to = s.identity;
		if (!s.notify(req))
		    removeUnreachableSubscriber((Subscriber)s);
	    }
	}
    }

    private void subscribe(SENPPacket req) 
	throws InvalidSenderException, SienaException {
	if (req.filter == null && req.pattern == null) {
	    //
	    // null filters/patterns are not allowed in subscriptions
	    // this is a design choice, we could accept null filters
	    // with the semantics of the universal filter: one that
	    // matches every notification
	    //
	    Logging.prlnerr("subscribe: null filter/pattern in subscription");
	    return;
	}

	Monitor.subscribe(req.id, my_identity);
	Subscriber s = map_subscriber(req);
	if (s == null) {
	    Logging.prlnerr("subscribe: unknown subscriber: " 
			    + req.toString());
	    return;
	}
	if (req.filter != null) {
	    subscribe(req.filter, s, req);
	} else {
	    //
	    // must be req.pattern != null
	    //
	    subscribe(req.pattern, s);
	}
    }

    synchronized private void unsubscribe(Filter f, Subscriber s, 
					  SENPPacket req) {
	//
	// for global unsubscriptions (f == null)
	// removes all the patterns as well
	// 
	if (f == null) unsubscribe((Pattern)null, s);
	Set to_remove = subscriptions.to_remove(f, s);
	if (to_remove.isEmpty()) return;

	if (req == null) req = spkt;

	req.id = my_identity;
	req.handler = listener.uri();
	req.to = master_id;

	Set new_roots = null;
	for(Iterator i = to_remove.iterator(); i.hasNext();) {
	    Subscription sub = (Subscription)i.next();
	    if (new_roots == null) new_roots = new HashSet();

	    new_roots.addAll(subscriptions.remove(sub));
	    if (master != null && master_id != null
		&& !SENP.match(s.identity, master_id) // this should never fail
		&& req.ttl > 0) {
		try {
		    req.method = SENP.UNS;
		    req.filter = sub.filter;
		    master.send(req.buf, req.encode());
		} catch (Exception ex) {
		    Logging.prlnerr("unsubscribe: error sending packet to master " + master.toString() + ": " + ex.toString());
		}
	    }
	}

	if (new_roots != null) {
	    if (master != null && master_id != null
		&& !SENP.match(s.identity, master_id) // this should never fail
		&& req.ttl > 0) {
		for(Iterator ri = new_roots.iterator(); ri.hasNext();) {
		    try {
			req.method = SENP.SUB;
			req.filter = ((Subscription)ri.next()).filter;
			master.send(req.buf, req.encode());
		    } catch (Exception ex) {
			Logging.prlnerr("unsubscribe: error sending packet to master " + master.toString() + ": " + ex.toString());
		    }
		}
	    }
	}

	if (s.hasNoRefs()) {
	    Logging.prlnlog("removing " + s.toString() + " from contacts list");
	    contacts.remove(s.getKey());
	}
    }

    synchronized private void unsubscribe(SENPPacket req) 
	throws InvalidSenderException, SienaException {

	Subscriber s = find_subscriber(req);
	if (s == null) return;
	if (req.pattern != null) {
	    Monitor.disconnect(req.id, my_identity);
	    unsubscribe(req.pattern, s);
	} else {
	    Monitor.unsubscribe(req.id, my_identity);
	    unsubscribe(req.filter, s, req);
	}
    }

    synchronized private Subscriber map_subscriber(SENPPacket req) 
	throws InvalidSenderException {
	if (req.id == null)
	    return null;
	String id = new String(req.id);
	Subscriber s = (Subscriber)contacts.get(id);
	if (s == null) {
	    if (req.handler != null) {
		s = new Subscriber(id, sender_factory.createPacketSender(new String(req.handler)));
		contacts.put(s.getKey(), s);
	    }
	} else if (req.handler != null) {
	    s.mapHandler(sender_factory.createPacketSender(new String(req.handler)));
	}
	return s;
    }

    synchronized private Subscriber map_subscriber(Notifiable notifiable) {
	Subscriber s = (Subscriber)contacts.get(notifiable);
	if (s == null) {
	    s = new Subscriber(notifiable);
	    contacts.put(s.getKey(), s);
	}
	return s;
    }

    synchronized private Subscriber find_subscriber(SENPPacket req) 
	throws InvalidSenderException {
	if (req.id == null)
	    return null;
	return (Subscriber)contacts.get(new String(req.id));
    }

    synchronized private Subscriber find_subscriber(Notifiable n) {
	if (n == null)
	    return null;
	return (Subscriber)contacts.get(n);
    }

    /** closes this dispatcher.  
     *
     *	If this dispatcher has an active listener then closes the
     *	active listener.  If this dispatcher has a master server, then
     *	cancels every subscription with the master server.
     *	<p>
     *
     *	Some implementations of the Java VM do not respond correctly
     *	to shutdown().  In particular, Sun's jvm1.3rc1-linux is known
     *	not to work correctly.  Sun's jvm1.3-solaris, jvm1.3-win32,
     *	and jvm1.2.2-linux work correctly.
     *
     *	@see #setReceiver(PacketReceiver)
     *	@see #setMaster(String) 
     **/
    synchronized public void shutdown() {
	disconnectMaster();
	if (listener != null)
	    try { 
		listener.shutdown(); 
	    } 
	    catch (PacketReceiverException ex) {
		Logging.prlnerr("error shutting down packet receiver: " 
				+ ex.toString());
	    }
	Monitor.remove_node(my_identity);
    }

    /** removes all subscriptions from any notifiable.  
     *
     *  clears all the subscriptions from local or remote clients.  In
     *  case this server has a master server, it instructs the master
     *  server to clear all the subscriptions associated with this
     *  dispatcher.  This method is also useful in case this
     *  dispatcher re-uses both a listener and an identity from a
     *  previous dispatcher that crashed or did not otherwise shutdown
     *  properly.
     *
     *	@see #shutdown()
     **/
    synchronized public void clearSubscriptions() throws SienaException {
	subscriptions.clear();
	contacts.clear();
	if (master != null) {
	    SENPPacket spkt = SENPPacket.allocate();
	    spkt.method = SENP.BYE;
	    spkt.id = my_identity;
	    spkt.to = master_handler;
	    try {
		master.send(spkt.buf, spkt.encode());
	    } finally {
		SENPPacket.recycle(spkt);
	    }
	}
    }

    public void publish(Notification e) throws SienaException {
	SENPPacket spkt = SENPPacket.allocate();
	//	req.event = new Notification(e);
	spkt.event = e;
	spkt.method = SENP.PUB; 
	spkt.id = my_identity;
	try {
	    publish(spkt);
	} finally {
	    SENPPacket.recycle(spkt);
	}
    }

    synchronized public void suspend(Notifiable n) throws SienaException {
	if (n == null) return;
	Subscriber s;
	s = (Subscriber)contacts.get(n);
	if (s != null) s.suspend();
    }

    synchronized public void resume(Notifiable n) throws SienaException {
	if (n == null) return;
	Subscriber s;
	s = (Subscriber)contacts.get(n);
	if (s != null) s.resume();
    }

    synchronized public void subscribe(Filter f, Notifiable n) 
	throws SienaException {
	if (f == null) {
	    //
	    // null filters are not allowed in subscriptions this is a
	    // design choice, we could accept null filters with the
	    // semantics of the universal filter: one that matches
	    // every notification
	    //
	    Logging.prlnerr("subscribe: null filter in subscription");
	    return;
	}

	Subscriber s = map_subscriber(n);
	if (s == null) {
	    Logging.prlnerr("subscribe: unknown local subscriber");
	    return;
	}
	subscribe(f, s, null);
    }

    synchronized private void subscribe(Filter f, Subscriber s, 
					SENPPacket req) throws SienaException {
	Subscription sub = subscriptions.insert_subscription(f, s);
	if (sub == null) return;
	    
	if (subscriptions.is_root(sub) && master != null
	    && (req == null || req.ttl > 0)) {
	    try { 
		if (req == null) { req = spkt; req.init(); }
		req.method = SENP.SUB;
		req.id = my_identity;
		req.handler = listener.uri();
		req.to = (master_id != null) ? master_id : master_handler;
		req.filter = f;
		master.send(req.buf, req.encode());
	    } catch (Exception ex) {
		Logging.prlnerr("error sending packet to " + master.toString());
		Logging.exerr(ex);
		//
		// log something here ...work in progress...
		//
	    }
	}
    }

    synchronized public void subscribe(Pattern p, Notifiable n)
	throws SienaException {
	Subscriber s = map_subscriber(n);
	if (s == null) return;
	subscribe(p,s);
    }

    synchronized private void subscribe(Pattern p, Subscriber s)
	throws SienaException {
	//
	// warning: this is a naive implementation.  
	// ...work in progress...
	//
	PatternMatcher m;
	
	ListIterator li = matchers.listIterator(); 
	while(li.hasNext()) {
	    m = (PatternMatcher)li.next();
	    if (Covering.covers(m.pattern, p)) {
		if (m.subscribers.contains(s)) 
		    return;
		if (Covering.covers(p, m.pattern)) {
		    m.subscribers.add(s);
		    return;
		}
	    } else if (Covering.covers(p, m.pattern)) {
		if (m.subscribers.remove(s)) { 
		    if (m.subscribers.isEmpty()) {
			for(int j = 0; j < m.pattern.filters.length; ++j)
			    unsubscribe(m.pattern.filters[j], m);
			//
			// FIXME: this is a bug!  in fact this
			// li.remove() is executed right before a
			// li.hasNext()---the problem is the screwed
			// up semantics of iterators in Java... which
			// may make sense for a generic iterator, but
			// is incredibly stupid for a list iterator:
			// it should be possible to call li.remove()
			// and assume that li shifted to the next
			// element (so that li.hasNext() would work as
			// expected).  This demonstrates once again
			// that defining collections and iterators
			// with generic classes is not always a good
			// idea ... well, enough flames for now.
			// Anyway, this needs to be fixed!
			//
			li.remove(); 
		    }
		}
	    }
	}

	m = new PatternMatcher(p, my_identity);
	m.subscribers.add(s);
	for(int i = 0; i < p.filters.length; ++i)
	    subscribe(p.filters[i], m);
	matchers.add(m);
    }

    synchronized public void unsubscribe(Filter f, Notifiable n) {
	Subscriber s = find_subscriber(n);
	if (s == null) return;
	unsubscribe(f, s, null);
    } 

    synchronized public void unsubscribe(Pattern p, Notifiable n) {
	Subscriber s = map_subscriber(n);
	if (s == null) return;
	unsubscribe(p,s);
    }

    synchronized private void unsubscribe(Pattern p, Subscriber s) {
	//
	// warning: this is a naive implementation.  
	// ...work in progress...
	//
	PatternMatcher m;
	
	ListIterator li = matchers.listIterator(); 
	while(li.hasNext()) {
	    m = (PatternMatcher)li.next();
	    if (p == null || Covering.covers(p, m.pattern)) {
		if (m.subscribers.remove(s)) {
		    if (m.subscribers.isEmpty()) {
			for(int j = 0; j < m.pattern.filters.length; ++j)
			    unsubscribe(m.pattern.filters[j], m);
			//
			// FIXME: this is a bug!  see same comment above
			//
			li.remove(); 
		    }
		}
	    }
	}
	if (s.hasNoRefs()) {
	    Logging.prlnlog("removing " + s.toString() + " from contacts list");
	    contacts.remove(s.getKey());
	}
    }

    /** this method has no effect.  
     *
     *	Method not implemented in this Siena server. 
     **/
    public void advertise(Filter f, String id) throws SienaException {};

    /** this method has no effect.  
     *
     *  Method not implemented in this Siena server. 
     **/
    public void unadvertise(Filter f, String id) throws SienaException {};

    /** this method has no effect.  
     *
     *	Method not implemented in this Siena server. 
     **/
    public void unadvertise(String id) throws SienaException {};

    synchronized public void unsubscribe(Notifiable n) throws SienaException {
	unsubscribe((Filter)null, n);
    }
}
