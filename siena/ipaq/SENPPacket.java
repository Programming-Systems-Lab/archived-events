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

import com.sun.java.util.collections.Iterator;
import com.sun.java.util.collections.LinkedList;
import com.sun.java.util.collections.ListIterator;

class SENPBuffer {
    private byte []	sval_buf;
    public byte[]	buf;
    protected int	pos;
    protected int	last;

    public SENPBuffer() {
	buf = new byte[SENP.MaxPacketLen];
	sval_buf = new byte[SENP.MaxPacketLen];
	pos = 0;
	last = 0;
	sval_last = 0;
    }

    public void init() {
	pos = 0;
	last = 0;
	sval_last = 0;
    }

    public void init(int len) {
	pos = 0;
	last = len;
	sval_last = 0;
    }

    public void init(byte[] b) {
	pos = 0;
	last = 0;
	sval_last = 0;
	append(b);
    }

    public void append(byte b) {
	buf[pos++] = b;
    }

    public void append(int x) {
	buf[pos++] = (byte)x;
    }

    public void append(byte[] bytes) {
	for(int i = 0; i < bytes.length; ++i) 
	    buf[pos++] = bytes[i];
    }

    public void append(String s) {
	append(s.getBytes());
    }

    public int length() {
	return last;
    }

    //
    // WARNING: now, since Java doesn't have byte literals in the form
    // of ascii characters like good old 'a' '*' '\n' etc.  I'll have
    // to use the corresponding decimal values.  Which is the
    // Right(tm) way of doing that, according to some clowns out
    // there...
    //

    //
    // token types
    //
    public static final int	T_EOF		= -1;
    public static final int	T_UNKNOWN	= -2;
    //
    // keywords
    //
    public static final int	T_ID		= -3;
    public static final int	T_STR		= -4;
    public static final int	T_INT		= -5;
    public static final int	T_DOUBLE	= -6;
    public static final int	T_BOOL		= -7;
    public static final int	T_OP		= -8;
    public static final int	T_LPAREN	= -9;
    public static final int	T_RPAREN	= -10;

    public short	oval;
    public long		ival;
    public boolean	bval;
    public double	dval;

    private int		sval_last = 0;

    private int nextByte() {
	if (++pos >= last) return -1;
	return buf[pos];
    }

    private int currByte() {
	if (pos >= last) return -1;
	return buf[pos];
    }

    private void pushBack() {
	if (pos > 0) pos--;
    }

    private boolean isCurrentFirstIdentChar() {
	if (pos >= last) return false;
	return (buf[pos] >= 0x41 && buf[pos] <= 0x5a)	// 'A' -- 'Z'
	    || (buf[pos] >= 0x61 && buf[pos] <= 0x7a)	// 'a' -- 'z'
	    || buf[pos] == 0x5f;			// '_'
    }

    private boolean isCurrentIdentChar() {
	if (pos >= last) return false;
	return (buf[pos] >= 0x41 && buf[pos] <= 0x5a)	// 'A' -- 'Z' 
	    || (buf[pos] >= 0x61 && buf[pos] <= 0x7a)	// 'a' -- 'z'
	    || (buf[pos] >= 0x30 && buf[pos] <= 0x39)	// '0' -- '9'
	    || buf[pos] == 0x5f || buf[pos] == 0x24	// '_', '$' 
	    || buf[pos] == 0x2e || buf[pos] == 0x2f;	// '.', '/' 
    }

    byte read_octal() { 
				/* '0' -- '7' */
	byte nb = 0;
	int i = 3;
	do {
	    nb = (byte)(nb * 8 + currByte() - 0x30);
	} while(--i > 0 && ++pos < last 
		&& buf[pos] >= 0x30 && buf[pos] <= 0x37);
	return nb;
    }

    int read_string() {
	//
	// here buf[pos] == '"'
	//
	sval_last = 0;
	while(++pos < last)
	    switch (buf[pos]) {
	    case 0x22 /* '"' */: ++pos; return T_STR;
	    case 0x5c /* '\\' */: 
		if (++pos >= last) return T_UNKNOWN;
		switch (buf[pos]) {
		case 0x76 /* 'v' */: 
		    sval_buf[sval_last++] = 0x0b /* '\v' */;break;
		case 0x66 /* 'f' */: 
		    sval_buf[sval_last++] = 0x0c /* '\f' */;break;
		case 0x72 /* 'r' */: 
		    sval_buf[sval_last++] = 0x0d /* '\r' */;break; 
		case 0x6e /* 'n' */: 
		    sval_buf[sval_last++] = 0x0a /* '\n' */; break;
		case 0x74 /* 't' */: 
		    sval_buf[sval_last++] = 0x09 /* '\t' */; break;
		case 0x62 /* 'b' */: 
		    sval_buf[sval_last++] = 0x08 /* '\b' */; break;
		case 0x61 /* 'a' */: 
		    sval_buf[sval_last++] = 0x07 /* '\a' */; break;
		default:
		    if (buf[pos] >= 0x30 && buf[pos] <= 0x37) { 
			sval_buf[sval_last++] = read_octal();
		    } else {
			sval_buf[sval_last++] = buf[pos];
		    }
		}
		break;
	    default:
		sval_buf[sval_last++] = buf[pos];
	    }
	return T_UNKNOWN;
    }

    int read_id() {
	sval_last = 0;
	do {
	    sval_buf[sval_last++] = buf[pos++];
	} while(isCurrentIdentChar());
	return T_ID;
    }

    int read_int() {
	boolean negative = false;
	//
	// here buf[pos] is either a digit or '-'
	//
	if (buf[pos] == 0x2d /* '-' */) {
	    negative = true;
	    ival = 0;
	    if (++pos >= last || buf[pos] < 0x30 || buf[pos] > 0x39)
		return T_UNKNOWN;
	} else {
	    ival = buf[pos] - 0x30;
	    if (++pos >= last || buf[pos] < 0x30 || buf[pos] > 0x39)
		return T_INT;
	}
	do {
	    ival = ival * 10 + buf[pos] - 0x30;
	} while (++pos < last && buf[pos] >= 0x30 && buf[pos] <= 0x39);
	if (negative) ival = -ival;
	return T_INT;
    }

    int read_number() {
	boolean negative = false;
	//
	// here buf[pos] is either a digit or '-'
	//
	if (buf[pos] == 0x2d /* '-' */) {
	    negative = true;
	    if (++pos >= last || buf[pos] < 0x30 || buf[pos] > 0x39)
		return T_UNKNOWN;
	}
	int type;
	if (read_int() == T_UNKNOWN) return T_UNKNOWN;
	type = T_INT;
	dval = ival;
	if (pos < last && buf[pos] == 0x2e /* '.' */) {
	    type = T_DOUBLE;
	    if (++pos >= last || buf[pos] < 0x30 || buf[pos] > 0x39) {
		return T_UNKNOWN;
	    } else {
		dval += read_decimal();
	    }
	}
	if (pos < last)
	    if (buf[pos] == 101 /* 'e' */ || buf[pos] == 69) /* 'E' */ {
		type = T_DOUBLE;
		if (++pos >= last 
		    || ((buf[pos] < 0x30 || buf[pos] > 0x39) 
			&& buf[pos] != 0x2d /* '-' */))
		    return T_UNKNOWN;
		if (read_int() == T_UNKNOWN) return T_UNKNOWN;
		dval *= java.lang.Math.pow(10,ival);
	    }
	if (negative) {
	    if (type == T_INT) {
		ival = -ival;
	    } else {
		dval = -dval;
	    }
	}
	return type;
    }

    double read_decimal() {
	//
	// here buf[pos] is a digit
	//
	long intpart = 0;
	long divisor = 1;
	do {
	    intpart = intpart*10 + (buf[pos] - 0x30);
	    divisor *= 10;
	} while(++pos < last && buf[pos] >= 0x30 && buf[pos] <= 0x39);
	return (1.0 * intpart) / divisor;
    }

    public int nextToken() {
	while (true) {
	    switch(currByte()) {
	    case -1: return T_EOF;
	    case 0x22 /* '"' */: return read_string();
	    case 123 /* '{' */: ++pos; return T_LPAREN;
	    case 125 /* '}' */: ++pos; return T_RPAREN;
	    case 33 /* '!' */: 
		switch(nextByte()) {
		case 0x3d /* '=' */: 
		    oval = Op.NE; ++pos; return T_OP;
		default: return T_UNKNOWN;
		}
	    case 42 /* '*' */: 
		switch(nextByte()) {
		case 60 /* '<' */: 
		    oval = Op.SF; ++pos; return T_OP;
		default: 
		    oval = Op.SS; return T_OP;
		}
	    case 0x3d /* '=' */: 
		oval = Op.EQ; ++pos; return T_OP;
	    case 62 /* '>' */: 
		switch(nextByte()) {
		case 42 /* '*' */: 
		    oval = Op.PF; ++pos; return T_OP;
		case 0x3d /* '=' */: 
		    oval = Op.GE; ++pos; return T_OP;
		default:
		    oval = Op.GT; return T_OP;
		}
	    case 60 /* '<' */: 
		switch(nextByte()) {
		case 0x3d /* '=' */: 
		    oval = Op.LE; ++pos; return T_OP;
		default:
		    oval = Op.LT; return T_OP;
		}
	    default:
		if ((buf[pos] >= 0x30 && buf[pos] <= 0x39) /* '0' -- '9' */
		     || buf[pos] == 0x2d /* '-' */) {
		    return read_number();
		} else if (isCurrentFirstIdentChar()) { 
		    return read_id();
		} else {
		    //
		    // I simply ignore characters that I don't understand 
		    //
		    ++pos;
		}
	    }
	}
    }

    public boolean match_sval(byte[] y) {
	if (sval_last == 0 && y == null) return true;
	if (sval_last == 0 || y == null || sval_last != y.length) return false;
	for(int i = 0; i < sval_last; ++i)
	    if (sval_buf[i] != y[i]) return false;
	return true;
    }

    public byte[] copy_sval() {
	byte [] res = new byte[sval_last];
	for(int i = 0; i < sval_last; ++i)
	    res[i] = sval_buf[i];
	return res;
    }

    public String sval_string() {
	return new String(sval_buf, 0, sval_last);
    }

    protected void encode_octal(byte x) {
	buf[pos++] = (byte)(((x >> 6) & 3) + 0x30);
	buf[pos++] = (byte)(((x >> 3) & 7) + 0x30);
	buf[pos++] = (byte)((x & 7) + 0x30);
    }

    protected void encode_decimal(long x) {
	byte[] tmp = new byte[20]; // Log(MAX_LONG)+1
	int p = 0;
	boolean negative = (x<0);
	if (negative) x = -x;

	do {
	    tmp[p++] = (byte)(x % 10 + 0x30 /* '0' */);
	    x /= 10;
	} while (x > 0);
	if (negative) buf[pos++] = 0x2d; /* '-' */
	while(p-- > 0) buf[pos++] = tmp[p];
    }

    void encode(byte[] bv) {
	buf[pos++] = 0x22 /* '"' */;
	for(int i = 0; i < bv.length; ++i) {
	    switch(bv[i]) {
	    case 11 /* '\v' */: 
		buf[pos++] = 0x5c /* '\\' */;
		buf[pos++] = 0x76 /* 'v' */;
		break;
	    case 12 /* '\f' */: 
		buf[pos++] = 0x5c /* '\\' */;
		buf[pos++] = 0x66 /* 'f' */;
		break;
	    case 13 /* '\r' */: 
		buf[pos++] = 0x5c /* '\\' */;
		buf[pos++] = 0x72 /* 'r' */;
		break;
	    case 10 /* '\n' */: 
		buf[pos++] = 0x5c /* '\\' */;
		buf[pos++] = 0x6e /* 'n' */;
		break;
	    case 9 /* '\t' */: 
		buf[pos++] = 0x5c /* '\\' */;
		buf[pos++] = 0x74 /* 't' */;
		break;
	    case 8 /* '\b' */: 
		buf[pos++] = 0x5c /* '\\' */;
		buf[pos++] = 0x62 /* 'b' */;
		break;
	    case 7 /* '\a' */: 
		buf[pos++] = 0x5c /* '\\' */;
		buf[pos++] = 0x61 /* 'a' */;
		break;
	    case 0x22 /* '"' */:
		buf[pos++] = 0x5c /* '\\' */;
		buf[pos++] = 0x22 /* '"' */;
		break;
	    case 0x5c /* '\\' */:
		buf[pos++] = 0x5c /* '\\' */;
		buf[pos++] = 0x5c /* '\\' */;
		break;
	    default:
		if (bv[i] < 0x20 || bv[i] >= 0x7F) {
		    //
		    // here I handle other non-printable characters with
		    // the \xxx octal notation ...work in progress...
		    //
		    buf[pos++] = 0x5c;
		    encode_octal(bv[i]);
		} else {
		    buf[pos++] = bv[i];
		}
	    }
	}
	buf[pos++] = 0x22 /* '"' */;
	if (pos > last) last = pos;
    }

    public void encode(AttributeValue a) {
	switch(a.getType()) {
	case AttributeValue.LONG: 
	    encode_decimal(a.longValue()); 
	    break;
	case AttributeValue.BOOL: 
	    append(a.booleanValue() ? SENP.KwdTrue : SENP.KwdFalse); 
	    break;
	case AttributeValue.DOUBLE: 
	    append(Double.toString(a.doubleValue()));
	    break;
	case AttributeValue.BYTEARRAY: 
	    encode(a.byteArrayValue());
	    break;
	case AttributeValue.NULL: 
	    append(SENP.KwdNull);
	    break;
	default:
	    // should throw an exception here 
	    // ...work in progress...
	}
	if (pos > last) last = pos;
    }

    public void encode(Notification e) {
	append(SENP.KwdEvent);
	append(SENP.KwdLParen);
	Iterator i = e.attributeNamesIterator();
	while(i.hasNext()) {
	    append(SENP.KwdSeparator);
	    String name = (String)i.next();
	    append(name);
	    append(SENP.KwdEquals);
	    encode(e.getAttribute(name));
	}
	append(SENP.KwdRParen);
	if (pos > last) last = pos;
    }

    public void encode(Pattern p) {
	append(SENP.KwdPattern);
	append(SENP.KwdLParen);
	for(int i = 0; i < p.filters.length; ++i) {
	    append(SENP.KwdSeparator);
	    encode(p.filters[i]);
	}
	append(SENP.KwdRParen);
	if (pos > last) last = pos;
    }

    public void encode(Notification[] s) {
	append(SENP.KwdEvents);
	append(SENP.KwdLParen);
	for(int i = 0; i < s.length; ++i) {
	    append(SENP.KwdSeparator);
	    encode(s[i]);
	}
	append(SENP.KwdRParen);
	if (pos > last) last = pos;
    }

    public void encode(Filter f) {
	append(SENP.KwdFilter);
	append(SENP.KwdLParen);
	Iterator i = f.constraintNamesIterator();
	while(i.hasNext()) {
	    String name = (String)i.next();
	    Iterator j = f.constraintsIterator(name); 
	    while(j.hasNext()) {
		append(SENP.KwdSeparator);
		append(name + " ");
		encode((AttributeConstraint)j.next());
	    }
	}
	append(SENP.KwdRParen);
	if (pos > last) last = pos;
    }


    public void encode(AttributeConstraint a) {
	append(SENP.operators[a.op]);
	if (a.op == Op.ANY) return;
	encode(a.value);
	if (pos > last) last = pos;
    }

    AttributeValue decodeAttribute() throws SENPInvalidFormat {
	switch(nextToken()) {
	case T_ID: 
	    if (match_sval(SENP.KwdTrue)) return new AttributeValue(true);
	    if (match_sval(SENP.KwdFalse)) return new AttributeValue(false);
	    if (match_sval(SENP.KwdNull)) return new AttributeValue();
	    return new AttributeValue(copy_sval());
	case T_STR: return new AttributeValue(copy_sval());
	case T_INT: return new AttributeValue(ival);
	case T_BOOL: return new AttributeValue(bval);
	case T_DOUBLE: return new AttributeValue(dval);
	default:
	    throw(new SENPInvalidFormat("<int>, <string>, <bool> or <double>"));
	}
    }

    static final String ErrAttrName = "<attribute-name>";
    static final String ErrParam 
	= "`event' or `filter' or `pattern' or `events'";
    static final String ErrEvent = "`event'";
    static final String ErrFilter = "`filter'";

    AttributeConstraint decodeAttributeConstraint() 
	throws SENPInvalidFormat {
	switch (nextToken()) {
	case T_ID: 
	    if (match_sval(SENP.operators[Op.ANY])) { 
		return new AttributeConstraint(Op.ANY, (AttributeValue)null);
	    } else {
		throw(new SENPInvalidFormat(T_OP));
	    }
	case T_OP: {
	    short op = oval;
	    return new AttributeConstraint(op, decodeAttribute());
	}
	default:
	    throw(new SENPInvalidFormat(T_OP));
	}
    }

    Notification decodeNotification() 
	throws SENPInvalidFormat {
	if (nextToken() != T_LPAREN) 
	    throw(new SENPInvalidFormat(T_LPAREN, new String(SENP.KwdLParen)));
	int ttype;
	Notification e = new Notification();
	while ((ttype = nextToken()) != T_RPAREN) {
	    if (ttype != T_ID && ttype != T_STR)
		throw(new SENPInvalidFormat(T_ID, ErrAttrName));
	    String name = sval_string();
	    if (nextToken() != T_OP || oval != Op.EQ)
		throw(new SENPInvalidFormat(T_OP, new String(SENP.KwdEquals)));
	    e.putAttribute(name, decodeAttribute());
	}
	return e;
    }

    Notification [] decodeNotifications() 
	throws SENPInvalidFormat {
	if (nextToken() != T_LPAREN) 
	    throw(new SENPInvalidFormat(T_LPAREN, new String(SENP.KwdLParen)));
	LinkedList l = new LinkedList();
	int ttype;

	while ((ttype = nextToken()) != T_RPAREN) {
	    if (ttype != T_ID && !match_sval(SENP.KwdEvent))
		throw(new SENPInvalidFormat(T_ID, ErrEvent));
	    l.addLast(decodeNotification());
	}
	Notification [] res = new Notification[l.size()];
	int i = 0;
	for(ListIterator li = l.listIterator(); li.hasNext(); ++i)
	    res[i] = (Notification)li.next();
	return res;
    }

    Pattern decodePattern() 
	throws SENPInvalidFormat {
	if (nextToken() != T_LPAREN) 
	    throw(new SENPInvalidFormat(T_LPAREN, new String(SENP.KwdLParen)));
	LinkedList l = new LinkedList();
	int ttype;

	while ((ttype = nextToken()) != T_RPAREN) {
	    if (ttype != T_ID && ttype != T_STR
		&& !match_sval(SENP.KwdFilter))
		throw(new SENPInvalidFormat(T_ID, ErrFilter));
	    l.addLast(decodeFilter());
	}
	Filter [] ff = new Filter[l.size()];
	int i = 0;
	for(ListIterator li = l.listIterator(); li.hasNext(); ++i)
	    ff[i] = (Filter)li.next();
	return new Pattern(ff);
    }

    Filter decodeFilter() throws SENPInvalidFormat {
	if (nextToken() != T_LPAREN) 
	    throw(new SENPInvalidFormat(T_LPAREN, new String(SENP.KwdLParen)));
	Filter f = new Filter();
	int ttype;
	while ((ttype = nextToken()) != T_RPAREN) {
	    if (ttype != T_ID && ttype != T_STR) 
		throw(new SENPInvalidFormat(T_ID, ErrAttrName));

	    f.addConstraint(sval_string(), decodeAttributeConstraint());
	}
	return f;
    }
}

class SENPPacket extends SENPBuffer {
    private SENPPacket		next;

    public byte			version;
    public byte			method;
    public byte			ttl;
    public byte[]		to;
    public byte[]		id;
    public byte[]		handler;
    
    public Notification		event;
    public Filter		filter;
    public Pattern		pattern;
    public Notification[]	events;

    public SENPPacket() {
	super();
	init();
    }

    public void init(int len) {
	pos = 0;
	last = len;

	next = null;
	version = SENP.ProtocolVersion;
	method = SENP.NOP;
	ttl = SENP.DefaultTtl;
	to = null;
	id = null;
	handler = null;
    
	event = null;
	filter = null;
	pattern = null;
	events = null;
    }

    public void init(byte[] b) {
	pos = 0;
	last = b.length;

	append(b);

	next = null;
	version = SENP.ProtocolVersion;
	method = SENP.NOP;
	ttl = SENP.DefaultTtl;
	to = null;
	id = null;
	handler = null;
    
	event = null;
	filter = null;
	pattern = null;
	events = null;
    }

    public void init() {
	pos = 0;
	last = 0;

	next = null;
	version = SENP.ProtocolVersion;
	method = SENP.NOP;
	ttl = SENP.DefaultTtl;
	to = null;
	id = null;
	handler = null;
    
	event = null;
	filter = null;
	pattern = null;
	events = null;
    }

    public String toString() {
	encode();
	return new String(buf, 0, length());
    }

    static private SENPPacket packet_cache = null;

    synchronized static public SENPPacket allocate() {
	SENPPacket res;
	if (packet_cache == null) {
	    res = new SENPPacket();
	} else {
	    res = packet_cache;
	    packet_cache = packet_cache.next;
	    res.init();
	}
	return res;
    }

    synchronized static public void recycle(SENPPacket p) {
	p.next = packet_cache;
	packet_cache = p;
    }

    public int encode() {
	pos = 0;
	last = 0;
	append(SENP.KwdSenp);
	append(SENP.KwdLParen);

	append(SENP.Version);
	append(SENP.KwdEquals);
	encode_decimal(version);
	append(SENP.KwdSeparator);

	append(SENP.Method);
	append(SENP.KwdEquals);
	encode(SENP.Methods[method]);
	append(SENP.KwdSeparator);

	append(SENP.Ttl);
	append(SENP.KwdEquals);
	encode_decimal(ttl);

	if (id != null) {
	    append(SENP.KwdSeparator);
	    append(SENP.Id);
	    append(SENP.KwdEquals);
	    encode(id);	    
	}

	if (to != null) {
	    append(SENP.KwdSeparator);
	    append(SENP.To);
	    append(SENP.KwdEquals);
	    encode(to);	    
	}

	if (handler != null) {
	    append(SENP.KwdSeparator);
	    append(SENP.Handler);
	    append(SENP.KwdEquals);
	    encode(handler);	    
	}

	append(SENP.KwdRParen);

	if (event != null) {
	    append(SENP.KwdSeparator);
	    encode(event);	    
	} else if (filter != null) {
	    append(SENP.KwdSeparator);
	    encode(filter); 
	} else if (pattern != null) {
	    append(SENP.KwdSeparator);
	    encode(pattern); 
	} else if (events != null) {
	    append(SENP.KwdSeparator);
	    encode(events); 
	}
	if (pos > last) last = pos;
	return last;
    }

    public void decode() throws SENPInvalidFormat {
	int ttype;

	pos = 0;
	if (nextToken() != T_ID || !match_sval(SENP.KwdSenp)) 
	    throw(new SENPInvalidFormat(T_ID, new String(SENP.KwdSenp)));

	if (nextToken() != T_LPAREN) 
	    throw(new SENPInvalidFormat(T_LPAREN, new String(SENP.KwdLParen)));

	String name;

	while ((ttype = nextToken()) != T_RPAREN) {
	    if (ttype != T_ID) 
		throw(new SENPInvalidFormat(T_ID, ErrAttrName));

	    if (nextToken() != T_OP || oval != Op.EQ)
		throw(new SENPInvalidFormat(T_OP, new String(SENP.KwdEquals)));

	    if (match_sval(SENP.Method)) {
		switch(nextToken()) {		
		case T_ID:
		case T_STR:
		    for (byte mi = 0; mi < SENP.Methods.length; ++mi)
			if (match_sval(SENP.Methods[mi])) {
			    method = mi;
			    break;
			}
		    break;
		default:
		    throw(new SENPInvalidFormat(T_ID, "expecting method"));
		}
	    } else if (match_sval(SENP.Ttl)) {
		if (nextToken() == T_INT) {
		    ttl = (byte)ival;
		} else {
		    throw(new SENPInvalidFormat(T_INT, "expecting ttl value"));
		}
	    } else if (match_sval(SENP.Version)) {
		if (nextToken() == T_INT) {
		    version = (byte)ival;
		} else {
		    throw(new SENPInvalidFormat(T_INT, 
						"expecting version value"));
		}
	    } else if (match_sval(SENP.Id)) {
		if (nextToken() == T_STR) {
		    id = copy_sval();
		} else {
		    throw(new SENPInvalidFormat(T_STR, "expecting id value"));
		}
	    } else if (match_sval(SENP.To)) {
		if (nextToken() == T_STR) {
		    to = copy_sval();
		} else {
		    throw(new SENPInvalidFormat(T_STR, "expecting to value"));
		}
	    } else if (match_sval(SENP.Handler)) {
		if (nextToken() == T_STR) {
		    handler = copy_sval();
		} else {
		    throw(new SENPInvalidFormat(T_STR, 
						"expecting handler value"));
		}
	    } else {
		    throw(new SENPInvalidFormat(T_STR, "unknown header field " 
						+ sval_string()));
	    }
	}
	//
	// now reads the optional parameter: either a filter or an event
	//
	switch (nextToken()) {
	case T_EOF: return;
	case T_ID: 
	    if (match_sval(SENP.KwdFilter)) {
		filter = decodeFilter();
	    } else if (match_sval(SENP.KwdEvent)) {
		event = decodeNotification();
	    } else if (match_sval(SENP.KwdEvents)) {
		events = decodeNotifications();
	    } else if (match_sval(SENP.KwdPattern)) {
		pattern = decodePattern();
	    } 
	    return;
	default:
	    throw(new SENPInvalidFormat(ErrParam));
	}
    }
}

