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

import com.sun.java.util.collections.Set;
import com.sun.java.util.collections.Map;
import com.sun.java.util.collections.HashSet;
import com.sun.java.util.collections.HashMap;
import com.sun.java.util.collections.Iterator;

/** a selection predicate defined over notifications.
 *
 *  The form of a <code>Filter</code> is a conjunction of
 *  <em>constraints</em> (see {@link AttributeConstraint}).  Each
 *  constraint poses an elementary condition on a specific attribute
 *  of the event.  For example a contraint<em>[price &lt; 10]</em>
 *  requires that the event contain an attribute named "price" whose
 *  value is a number less than 10.  A <code>Filter</code> can have
 *  more that one constraint for an attribute.  For example, a
 *  <code>Filter</code> can express things like <em>[model="custom",
 *  price &gt; 10, price &lt;= 20]</em>.<p>
 * 
 *  Every <em>constraint</em> in a <code>Filter</code> implicitly
 *  expresses an existential quantifier over the event.  The filter
 *  of the previous example (<em>[model="custom", price &gt; 10,
 *  price &lt;= 20]</em>) requires that an event contain an attribute
 *  named "model", whose value is the string "custom", and an
 *  attribute named "price" whose value is a number between 10 and 20
 *  (20 included).<p>
 * 
 *  The valid syntax for attribute names is the same for {@link
 *  Notification}.<p>
 * 
 *  @see AttributeConstraint
 *  @see Notification
 **/
public class Filter implements java.io.Serializable {
    //
    // what I really need here is a multimap, but there is no such
    // thing in java.util (JDK 1.2.2), so I'm going to implement it as
    // a map of lists 
    //
    Map constraints;

    /** creates an empty filter.  
     *
     *	creates a filter with no constraints. Notice that an empty
     *	filter does not match any notification.  
     **/
    public Filter() { 
	constraints = new HashMap();
    }

    /** creates a (deep) copy of a given filter.  
     **/
    public Filter(Filter f) {
	//
	// again, since Java doesn't have decent collections
	// (multimaps), I have to do this really horrible copy.
	//
	constraints = new HashMap();
	for(Iterator i = f.constraints.entrySet().iterator(); i.hasNext();) {
	    Map.Entry entry = (Map.Entry)i.next();
	    String name = (String)entry.getKey();
	    Set alist = new HashSet();
	    constraints.put(name,alist); 
	    for(Iterator li = ((Set)entry.getValue()).iterator(); 
		li.hasNext();) 
		alist.add(new AttributeConstraint((AttributeConstraint)li.next()));
	}
    }

    private void writeObject(java.io.ObjectOutputStream out) 
	throws java.io.IOException {
	SENPBuffer b = new SENPBuffer();
	b.encode(this);
	out.writeInt(b.length());
	out.write(b.buf, 0, b.length());
    }

    private void readObject(java.io.ObjectInputStream in)
	throws java.io.IOException, java.lang.ClassNotFoundException {
	int len = in.readInt();
	SENPBuffer b = new SENPBuffer();
	in.readFully(b.buf, 0, len);
	b.init(len);

	Filter f;
	try {
	    f = b.decodeFilter();
	} catch (SENPInvalidFormat ex) {
	    throw new java.io.InvalidObjectException(ex.toString());
	}
	constraints = new HashMap();
	for(Iterator i = f.constraints.entrySet().iterator(); i.hasNext();) {
	    Map.Entry entry = (Map.Entry)i.next();
	    String name = (String)entry.getKey();
	    Set alist = new HashSet();
	    constraints.put(name,alist); 
	    for(Iterator li = ((Set)entry.getValue()).iterator(); 
		li.hasNext();) 
		alist.add(new AttributeConstraint((AttributeConstraint)li.next()));
	}
    }

    /** <code>true</code> <em>iff</em> this filter contains no constraints
     **/
    public boolean isEmpty() {
	return constraints.isEmpty();
    }

    /** puts a constraint <em>a</em> on attribute <em>name</em>. 
     *
     *	Example:
     *	<pre><code>
     *	    Filter f = new Filter();
     *	    AttributeConstraint a;
     *	    a = new AttrbuteConstraint(Op.SS, "soft") 
     *	    f.addConstraint("subject", a);
     *	</pre></code>
     **/
    public void addConstraint(String name, AttributeConstraint a) {
	Set s = (Set)constraints.get(name);
	if (s == null) {
	    s = new HashSet();
	    constraints.put(name,s);
	}
	s.add(a);
    }

    /** puts a constraint on attribute <em>name</em> using
     *	comparison operator <em>op</em> and a <code>String</code>
     *	argument <em>sval</em>.
     *
     *	<pre><code>
     *	    Filter f = new Filter();
     *	    f.addConstraint("subject", Op.SS, "soft");
     *	</pre></code> 
     **/
    public void addConstraint(String s, short op, String sval) {
	addConstraint(s,new AttributeConstraint(op, sval));
    }

    /** puts a constraint on attribute <em>name</em> using
     *  comparison operator <em>op</em> and a <code>byte[]</code>
     *  argument <em>sval</em>.
     **/
    public void addConstraint(String s, short op, byte[] sval) {
	addConstraint(s,new AttributeConstraint(op, sval));
    }

    /** puts a constraint on attribute <em>name</em> using comparison
     *  operator <em>op</em> and a <code>long</code> argument
     *  <em>lval</em>.
     **/
    public void addConstraint(String s, short op, long lval) {
	addConstraint(s,new AttributeConstraint(op, lval));
    }

    /** puts a constraint on attribute <em>name</em> using comparison
     *	operator <em>op</em> and a <code>boolean</code> argument
     *	<em>bval</em>.
     *
     *  <pre><code>
     *      Filter f = new Filter();
     *	    f.addConstraint("failed", Op.EQ, true);
     *  </pre></code> 
     **/
    public void addConstraint(String s, short op, boolean bval) {
	addConstraint(s,new AttributeConstraint(op, bval));
    }

    /** puts a constraint on attribute <em>name</em> using comparison
     *  operator <em>op</em> and a <code>double</code> argument
     * <em>dval</em>.
     **/
    public void addConstraint(String s, short op, double dval) {
	addConstraint(s,new AttributeConstraint(op, dval));
    }

    /** puts a constraint on attribute <em>name</em> using the
     *  equality operator and a <code>String</code> argument
     *  <em>sval</em>.
     *  
     *  Example:
     *  <pre><code>
     *      Filter f = new Filter();
     *      f.addConstraint("name", "Antonio");
     *  </pre></code> 
     **/
    public void addConstraint(String s, String sval) {
	addConstraint(s,new AttributeConstraint(Op.EQ, sval));
    }

    /** puts a constraint on attribute <em>name</em> using the
     *  equality operator and a <code>byte[]</code> argument
     *  <em>sval</em>.
     **/
    public void addConstraint(String s, byte[] sval) {
	addConstraint(s,new AttributeConstraint(Op.EQ, sval));
    }

    /** puts a constraint on attribute <em>name</em> using the
     *  equality operator and a <code>boolean</code> argument
     *  <em>bval</em>.
     **/
    public void addConstraint(String s, boolean bval) {
	addConstraint(s,new AttributeConstraint(Op.EQ, bval));
    }

    /** puts a constraint on attribute <em>name</em> using the
     *  equality operator and a <code>long</code> argument
     *  <em>lval</em>.
     **/
    public void addConstraint(String s, long lval) {
	addConstraint(s,new AttributeConstraint(Op.EQ, lval));
    }

    /** puts a constraint on attribute <em>name</em> using the
     *  equality operator and a <code>double</code> argument
     *  <em>dval</em>.
     **/
    public void addConstraint(String s, double dval) {
	addConstraint(s,new AttributeConstraint(Op.EQ, dval));
    }

    /** returns true if this filter contains at least one constraint
     *  for the specified attribute
     **/
    public boolean containsConstraint(String s) {
	return constraints.containsKey(s);
    }

    /** removes all the constraints for the specified attribute.
     *  
     *  @return true if any constraints existed and has been removed
     **/
    public boolean removeConstraints(String s) {
	return constraints.remove(s) != null;
    }

    /** removes all constraints.
     **/
    public void clear() {
	constraints.clear();
    }

    /** returns an iterator for the set of attribute (constraint)
     *  names of this <code>Filter</code>.
     **/
    public Iterator constraintNamesIterator() {
	return constraints.keySet().iterator();
    }

    /** returns an iterator for the set of constraints over attribute
     *  <em>name</em> of this <code>Filter</code>.
     **/
    public Iterator constraintsIterator(String name) {
	Set s = (Set)constraints.get(name);
	if (s == null) return null;
	return s.iterator();
    }

    public String toString() {
	SENPBuffer b = new SENPBuffer();
	b.encode(this);
	return new String(b.buf, 0, b.length());
    }
}
