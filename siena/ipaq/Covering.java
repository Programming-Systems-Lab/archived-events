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

// import com.sun.java.util.collections.Iterator;
// import com.sun.java.util.collections.List;
// import com.sun.java.util.collections.Set;
// import com.sun.java.util.collections.HashSet;
// import com.sun.java.util.collections.Map.Entry;
//
// for some misterious reason, importing single classes doesn't work
// here.  Go figure.
//
import com.sun.java.util.collections.*;
//import java.util.*;

/** implementation of the covering relations.

    <code>Covering</code> implements the covering relations that
    determine the semantics of </em>Siena</em>.  This class is used
    internally by the implementation of Siena.  However, it is also
    provided as a public class in the siena package for convenience.
**/
public class Covering {

    /** semantics of operators in filters.
     
	<code>apply_operator(op, x, y)</code> is equivalent to <em>x
        op y</em> 
    **/
    public static boolean apply_operator(short op, 
					 AttributeValue x, AttributeValue y) {
	switch(op) {
	case Op.ANY: 
	    return true;	
	case Op.EQ: 
	    switch(x.getType()) {
	    case AttributeValue.STRING: 
		return y.getType() == AttributeValue.STRING
		    && x.stringValue().equals(y.stringValue());
	    case AttributeValue.BOOL: 
		return y.getType() == AttributeValue.BOOL 
		    && x.booleanValue() == y.booleanValue();
	    case AttributeValue.INT: 
		return (y.getType() == AttributeValue.INT 
			&& x.doubleValue() == y.doubleValue())
		    || (y.getType() == AttributeValue.DOUBLE 
			&& x.doubleValue() == y.doubleValue());
	    case AttributeValue.DOUBLE: 
		return (y.getType() == AttributeValue.INT 
			&& x.doubleValue() == y.doubleValue())
		    || (y.getType() == AttributeValue.DOUBLE 
			&& x.doubleValue() == y.doubleValue());
	    case AttributeValue.NULL:
		return (y.getType() == AttributeValue.NULL);
	    default:					// I should probably
		return false;				// throw an exception
	    }
	case Op.NE: 
	    switch(x.getType()) {
	    case AttributeValue.STRING: 
		return y.getType() != AttributeValue.STRING
		    || ! x.stringValue().equals(y.stringValue());
	    case AttributeValue.BOOL: return y.getType() != AttributeValue.BOOL
					  || x.booleanValue() != y.booleanValue();
	    case AttributeValue.INT: 
		switch(y.getType()) {
		case AttributeValue.INT: 
		    return x.doubleValue() != y.doubleValue();
		case AttributeValue.DOUBLE: 
		    return x.doubleValue() != y.doubleValue();
		default: return true;
		}
	    case AttributeValue.DOUBLE:  
		switch(y.getType()) {
		case AttributeValue.INT:
		    return x.doubleValue() != y.doubleValue();
		case AttributeValue.DOUBLE: 
		    return x.doubleValue() != y.doubleValue();
		default: return true;
		}
	    case AttributeValue.NULL:
		return (y.getType() != AttributeValue.NULL);
	    default:					// I should probably
		return false;				// throw an exception
	    }
	case Op.SS:
	    switch (x.getType()) {
	    case AttributeValue.STRING: 
		return y.getType() == AttributeValue.STRING
		    && x.stringValue().indexOf(y.stringValue()) != -1;
	    default: return false;			// I should probably
	    }						// throw an exception
	case Op.SF: 
	    switch (x.getType()) {
	    case AttributeValue.STRING: 
		return y.getType() == AttributeValue.STRING
		    && x.stringValue().endsWith(y.stringValue());
	    default: return false;			// I should probably
	    }						// throw an exception
	case Op.PF:
	    switch (x.getType()) {
	    case AttributeValue.STRING: 
		return y.getType() == AttributeValue.STRING
		    && x.stringValue().startsWith(y.stringValue());
	    default: return false;			// I should probably
	    }						// throw an exception
	case Op.LT: 
	    switch(x.getType()) {
	    case AttributeValue.STRING: 
		return y.getType() == AttributeValue.STRING
				   && x.stringValue().compareTo(y.stringValue()) < 0;
	    case AttributeValue.INT: 
		return (y.getType() == AttributeValue.INT 
			&& x.doubleValue() < y.doubleValue())
		    || (y.getType() == AttributeValue.DOUBLE 
			&& x.doubleValue() < y.doubleValue());
	    case AttributeValue.BOOL: 
		return y.getType() == AttributeValue.BOOL 
		    && !x.booleanValue() && y.booleanValue();
	    case AttributeValue.DOUBLE: 
		return (y.getType() == AttributeValue.INT 
			&& x.doubleValue() < y.doubleValue())
		    || (y.getType() == AttributeValue.DOUBLE 
			&& x.doubleValue() < y.doubleValue());
	    default:					// I should probably
		return false;				// throw an exception
	    }
	case Op.GT: 
	    switch(x.getType()) {
	    case AttributeValue.STRING: 
		return y.getType() == AttributeValue.STRING
		    && x.stringValue().compareTo(y.stringValue()) > 0;
	    case AttributeValue.INT: 
		return (y.getType() == AttributeValue.INT
			&& x.doubleValue() > y.doubleValue())
		    || (y.getType() == AttributeValue.DOUBLE
			&& x.doubleValue() > y.doubleValue());
	    case AttributeValue.BOOL: 
		return y.getType() == AttributeValue.BOOL 
		    && x.booleanValue() && !y.booleanValue();
	    case AttributeValue.DOUBLE: 
		return (y.getType() == AttributeValue.INT 
			&& x.doubleValue() > y.doubleValue())
		    || (y.getType() == AttributeValue.DOUBLE 
			&& x.doubleValue() > y.doubleValue());
	    case AttributeValue.NULL:
		//
		// I'm not sure about the ``right'' semantics here
		// ...work in progress...
		//
	    default:					// I should probably
		return false;				// throw an exception
	    }
	case Op.LE: 
	    switch(x.getType()) {
	    case AttributeValue.STRING: 
		return y.getType() == AttributeValue.STRING
		    && x.stringValue().compareTo(y.stringValue()) <= 0;
	    case AttributeValue.INT: 
		return (y.getType() == AttributeValue.INT 
			&& x.doubleValue() <= y.doubleValue())
		    || (y.getType() == AttributeValue.DOUBLE 
			&& x.doubleValue() <= y.doubleValue());
	    case AttributeValue.BOOL: 
		return y.getType() == AttributeValue.BOOL 
		    && (!x.booleanValue() || y.booleanValue());
	    case AttributeValue.DOUBLE: 
		return (y.getType() == AttributeValue.INT 
			&& x.doubleValue() <= y.doubleValue())
		    || (y.getType() == AttributeValue.DOUBLE 
			&& x.doubleValue() <= y.doubleValue());
	    case AttributeValue.NULL:
		//
		// I'm not sure about the ``right'' semantics here
		// ...work in progress...
		//
	    default:					// I should probably
		return false;				// throw an exception
	    }
	case Op.GE: 
	    switch(x.getType()) {
	    case AttributeValue.STRING: 
		return y.getType() == AttributeValue.STRING
		    && x.stringValue().compareTo(y.stringValue()) >= 0;
	    case AttributeValue.INT: 
		return (y.getType() == AttributeValue.INT 
			&& x.doubleValue() >= y.doubleValue())
		    || (y.getType() == AttributeValue.DOUBLE 
			&& x.doubleValue() >= y.doubleValue());
	    case AttributeValue.BOOL: 
		return y.getType() == AttributeValue.BOOL 
		    && (x.booleanValue() || !y.booleanValue());
	    case AttributeValue.DOUBLE: 
		return (y.getType() == AttributeValue.INT 
			&& x.doubleValue() >= y.doubleValue())
		    || (y.getType() == AttributeValue.DOUBLE 
			&& x.doubleValue() >= y.doubleValue());
	    case AttributeValue.NULL:
		//
		// I'm not sure about the ``right'' semantics here
		// ...work in progress...
		//
	    default:					// I should probably
		return false;				// throw an exception
	    }
	default:
	    return false;				// exception ?
	}
    }

    /** covering between two attribute constraints.  

	<em>true</em> when <code>af1</code> defines a set of attribute
        values <em>S</em><sub>af1</sub> that contains the set of
        attribute values <em>S</em><sub>af2</sub> defined by
        <code>af2</code>, in other words true <em>iff af2 ==&gt;
        af1</em>, i.e., for every <code>AttributeValue</code>
        <em>x</em>: <em>x op2 f2 ==&gt; x op1 f1</em> where <em>op2</em>
        is the operator defined by af2 and <em>f2</em> is the value
        defined by af2. Same thing for af1.  
    */
    public static boolean covers(AttributeConstraint af1, 
				 AttributeConstraint af2) {
	// WARNING!
	// this is a crucial function! it is also quite tricky, think
	// twice before you change this implementation!
	//
	// All Siena operators define transitive relations, except for
	// NE.
	//
	// trivial cases
	//
	// {x any} covers everything
	//
	if (af1.op == Op.ANY) return true;
	//
	// nothing covers {x any} (except {x any}, see above)
	//
	if (af2.op == Op.ANY) return false;
	//
	// {x != a} C {x op b} <-- not a op b
	//
	if (af1.op == Op.NE) 
	    return !apply_operator(af2.op,af1.value, af2.value);
	//
	// same operators (we already excluded af1.op == NE)
	//
	if (af2.op == af1.op)
	    return apply_operator(af1.op, af2.value, af1.value) 
		|| apply_operator(Op.EQ, af2.value, af1.value);
	//
	// else I must consider the implications between DIFFERENT operators
	//
	switch(af2.op) {
	case Op.EQ: return apply_operator(af1.op, af2.value, af1.value);
	case Op.LT: return af1.op == Op.LE 
		     && apply_operator(Op.LT, af2.value, af1.value);
	case Op.LE: return af1.op == Op.LT
		     && apply_operator(Op.GT, af1.value, af2.value);
	case Op.GT: return af1.op == Op.GE
		     && apply_operator(Op.LE, af1.value, af2.value);
	case Op.GE: return af1.op == Op.GT
		     && apply_operator(Op.LT, af1.value, af2.value);
	case Op.SF: return af1.op == Op.SS
		     && apply_operator(Op.SS, af2.value , af1.value);
	case Op.PF: 
	    switch(af1.op) {
	    case Op.SS: 
		return apply_operator(Op.SS, af2.value,af1.value);
	    case Op.GT: 
		return apply_operator(Op.LE, af1.value, af2.value)
		    && !apply_operator(Op.PF, af1.value, af2.value);
	    case Op.LT: 
		return apply_operator(Op.GE, af1.value, af2.value)
		    && !apply_operator(Op.PF, af1.value, af2.value);
	    case Op.GE: 
		return apply_operator(Op.LT, af1.value, af2.value);
	    case Op.LE: 
		return apply_operator(Op.GT, af1.value, af2.value);
	    default: return false;
	    }
	default: return false;
	}
    }

    public static boolean apply(AttributeConstraint ac, AttributeValue av) {
	return apply_operator(ac.op, av, ac.value);
    }

    /**
     *  semantics of subscriptions
     */
    public static boolean apply(Filter f, Notification e) {
	for(Iterator i = f.constraints.entrySet().iterator(); i.hasNext();) { 
	    Map.Entry entry = (Map.Entry)i.next();
	    String name = (String)entry.getKey();
	    AttributeValue ea = (AttributeValue)e.getAttribute(name);
	    if (ea == null) return false;
	    for (Iterator li = ((Set)entry.getValue()).iterator(); 
		 li.hasNext();)
		if (!apply((AttributeConstraint)li.next(), ea))
		    return false;
	}
	return true;
    }

    /** covering relation between two filters.

	true iff for all notifications <em>n</em>: apply(f2,n) ==&gt;
	apply(f1,n) 
    */
    public static boolean covers(Filter f1, Filter f2)
    {
	//
	// true iff f2 ==> f1  
	// I think this expression translates into:
	// for each attribute filter af1 in f1, there exist at least one
	// corresponding (same name) attribute filter af2 in f2 such that 
	// af1 covers af2
	//
	// I'm not 100% sure of the demonstration though, the idea is that
	// attribute filters define ``connected'' subsets of ``ordered''
	// sets (which has changed since I added NE)... I think I
	// should also assume that f2 is not `null' (i.e., contradictory)
	// ...work in progress...
	//
	Iterator fi1;
	Iterator fi2;
	for(fi1 = f1.constraints.entrySet().iterator(); fi1.hasNext();) {
	    Map.Entry fe1 = (Map.Entry)fi1.next();
	    for(Iterator i = ((Set)fe1.getValue()).iterator(); i.hasNext();) {
		    boolean found = false;
		    Iterator i2 = f2.constraintsIterator((String)fe1.getKey());
		    if (i2 == null) return false;
		    AttributeConstraint c = (AttributeConstraint)i.next();
		    while(!found && i2.hasNext()) 
			found = covers(c, (AttributeConstraint)i2.next());
		    if (!found) return false;
		}
	}
	return true;
    }

    /** covering relation between two patterns.
     */
    public static boolean covers(Pattern p1, Pattern p2) {
	if (p1.filters.length != p2.filters.length) return false;
	for(int i = 0; i < p1.filters.length; ++i) 
	    if (!covers(p1.filters[i], p2.filters[i]))
		return false;
	return true;
    }
}
