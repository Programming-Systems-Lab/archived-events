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

import com.sun.java.util.collections.ListIterator;
import com.sun.java.util.collections.LinkedList;
import com.sun.java.util.collections.Iterator;


public class SENP {
    public static final byte ProtocolVersion = 1;

    public static final byte[] Version
    = {0x76, 0x65, 0x72, 0x73, 0x69, 0x6f, 0x6e};	// version
    public static final byte[] To 
    = {0x74, 0x6F};					// to
    public static final byte[] Method 
    = {0x6d, 0x65, 0x74, 0x68, 0x6f, 0x64};		// method
    public static final byte[] Id 
    = {0x69, 0x64};					// id
    public static final byte[] Handler 
    = {0x68, 0x61, 0x6e, 0x64, 0x6c, 0x65, 0x72};	// handler
    public static final byte[] Ttl 
    = {0x74, 0x74, 0x6c};				// ttl

    public static final int	DefaultTtl		= 30;

    public static final int	MaxPacketLen		= 65536;

    public static final byte NOP = 0;
    public static final byte PUB = 1;
    public static final byte SUB = 2;
    public static final byte UNS = 3;
    public static final byte ADV = 4;
    public static final byte UNA = 5;
    public static final byte HLO = 6;
    public static final byte BYE = 7;
    public static final byte SUS = 8;
    public static final byte RES = 9;
    public static final byte MAP = 10;
    public static final byte WHO = 11;
    public static final byte INF = 12;
    public static final byte CNF = 13;
    public static final byte OFF = 14;

    public static final byte[][] Methods = 
    {
	{ 0x4E, 0x4F, 0x50 },	// NOP
	{ 0x50, 0x55, 0x42 },	// PUB
	{ 0x53, 0x55, 0x42 },	// SUB
	{ 0x55, 0x4E, 0x53 },	// UNS
	{ 0x41, 0x44, 0x56 },	// ADV
	{ 0x55, 0x4E, 0x41 },	// UNA
	{ 0x48, 0x4C, 0x4F },	// HLO
	{ 0x42, 0x59, 0x45 },	// BYE
	{ 0x53, 0x55, 0x53 },	// SUS
	{ 0x52, 0x45, 0x53 },	// RES
	{ 0x4D, 0x41, 0x50 },	// MAP
	{ 0x57, 0x48, 0x4f },	// WHO
	{ 0x49, 0x4e, 0x46 },	// INF
	{ 0x43, 0x4e, 0x46 },	// CNF
	{ 0x4F, 0x46, 0x46 }	// OFF
    };

    //
    //  WARNING:  don't mess up the order of operators in this array
    //            it must correspond to the definitions of 
    //            Op.EQ, Op.LT, etc.
    // 
    public static final byte[][] operators = { 
	{0x3f}, // ?
	{0x3d}, // "="
	{0x3c}, // "<"
	{0x3e}, // ">"
	{0x3e, 0x3d}, // ">="
	{0x3c, 0x3d}, // "<="
	{0x3e, 0x2a}, // ">*"
	{0x2a, 0x3c}, // "*<"
	{0x61, 0x6e, 0x79}, // any, 
	{0x21, 0x3d}, // "!="
	{0x2a} // "*" 
    };
    //
    // default port numbers
    //
    public static final int	CLIENT_PORT		= 1936;
    public static final int	SERVER_PORT		= 1969;
    public static final int	DEFAULT_PORT		= 1969;

    public static final byte[] KwdSeparator = { 0x20 }; // ' '
    public static final byte[] KwdSenp 
    = {0x73, 0x65, 0x6e, 0x70}; // senp
    public static final byte[] KwdEvent 
    = {0x65, 0x76, 0x65, 0x6e, 0x74}; // event
    public static final byte[] KwdEvents
    = {0x65, 0x76, 0x65, 0x6e, 0x74, 0x73}; // events
    public static final byte[] KwdFilter 
    = {0x66, 0x69, 0x6c, 0x74, 0x65, 0x72}; // filter
    public static final byte[] KwdPattern 
    = {0x70, 0x61, 0x74, 0x74, 0x65, 0x72, 0x6e}; // pattern 
    public static final byte[] KwdLParen 
    = {0x7b}; // {
    public static final byte[] KwdRParen 
    = {0x7d}; // }
    public static final byte[] KwdEquals 
    = {0x3d}; // =
    public static final byte[] KwdTrue 
    = {0x74, 0x72, 0x75, 0x65}; // true
    public static final byte[] KwdFalse 
    = {0x66, 0x61, 0x6c, 0x73, 0x65}; // false

    public static final byte[] KwdNull 
    = {0x6e, 0x75, 0x6c, 0x6c}; // null

    public static boolean match(byte [] x, byte [] y) {
        if (x == null && y == null) return true;
        if (x == null || y == null || x.length != y.length) return false;
        for(int i = 0; i < x.length; ++i)
            if (x[i] != y[i]) return false;
        return true;
    }
}
