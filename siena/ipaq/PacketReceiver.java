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

/** abstract packet receiver.
 *
 *  <p>Encapsulates a passive acceptor of packets.  This acceptor is
 *  <em>passive</em> in the sense that it uses the caller's thread to
 *  accept and assemble the incoming packet.
 *  <code>PacketReceiver</code>s, together with their corresponding
 *  <code>PacketSender</code>s, form the communication layer
 *  underneath the distributed network of Siena components.
 *
 *  <p>The implementations of Siena (see {@link
 *  HierarchicalDispatcher} and {@link ThinClient}) use a
 *  <code>PacketReceiver</code> as their acceptor of external
 *  subscriptions, notifications, etc.
 *
 *  <p>This version of Siena includes only a simple implementation on
 *  top of TCP/IP ({@link TCPPacketReceiver}).  Future versions will
 *  include support for encapsulation into other protocols, such as
 *  SMTP and HTTP.
 *
 *  @see PacketSender
 **/
public interface PacketReceiver {

    /** external identifier for this receiver.
     *
     *  An external identifier must allow other applications to
     *  identify and contact this receiver.  Every implementation of
     *  this interface must agree on a format for external
     *  identifiers.  The current implementation uses a URL-like
     *  syntax.
     *
     *  <p>Notice also that every implementation of a
     *  <code>PacketReceiver</code> must have a corresponding
     *  <code>PacketSender</code>, and that
     *  <code>PacketSenderFactory</code> must understand its
     *  <em>uri</em> to construct the corresponding
     *  <code>PacketSender</code>.
     *
     *  @see PacketSender
     *  @see PacketSenderFactory
     **/
    public byte[] uri();

    /** receives a packet in the given buffer.
     *
     *  @return the number of bytes read into the buffer.  The return
     *          value <em>must not be negative</em>.  On error conditions,
     *          this method must throw an exception.
     *
     *  @exception PacketReceiverException in case an error occurrs
     *		while reading.
     **/
    public int receive(byte[] packet) throws PacketReceiverException;

    /** receives a packet in the given buffer, with the given timeout.
     *
     *  @return the number of bytes read into the buffer.  The return
     *          value <em>must not be negative</em>.  On error conditions,
     *          this method must throw an exception.
     *
     *  @exception PacketReceiverException in case an error occurrs
     *		while reading. 
     *
     *  @exception TimeoutExpired in case the timout expires before a
     *		packet is available
     **/
    public int receive(byte[] packet, long timeout) 
	throws PacketReceiverException, TimeoutExpired;

    /** closes the receiver.
     **/
    public void shutdown() throws PacketReceiverException;
}
