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

import java.io.IOException;

/** packet receiver has been closed */
public class PacketReceiverClosed extends PacketReceiverException {
    public IOException ioex;

    public PacketReceiverClosed(IOException ex) {
	super("packet receiver has been closed: " + ex.toString());
	ioex = ex;
    }

    public PacketReceiverClosed() {
	super("packet receiver has been closed");
	ioex = null;
    }

    /** IOException that caused this PacketReceiverClosed exception
     * 
     *  @return IOException that caused this PacketReceiverClosed
     *          exception or <code>null</code>
     **/
    public IOException getIOException() {
	return ioex;
    }
}
