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

import java.io.PrintStream;
import java.util.Date;

/**
 *   logging and error reporting facility for Siena.  
 *
 *   <code>Logging</code> allows you to redirect error and log messages
 *   to specific streams.  
 **/
public class Logging {
    static PrintStream	log = null;
    static PrintStream	err = System.err;

    static private String time() {
	return (new Date()).toString() + ": ";
    } 

    synchronized static void exerr(Exception ex) {
	if (err != null) {
	    err.print(time());
	    ex.printStackTrace(err);
	}
    }

    synchronized static void exlog(Exception ex) {
	if (log != null) {
	    log.print(time());
	    ex.printStackTrace(log);
	}
    }

    synchronized static void prerr(String s) {
	if (err != null) err.print(time() + s);
    }

    synchronized static void prlog(String s) {
	if (log != null) log.print(time() + s);
    }

    synchronized static void prlnerr(String s) {
	if (err != null) err.println(time() + s);
    }

    synchronized static void prlnlog(String s) {
	if (log != null) log.println(time() + s);
    }

    /** sets a log and debug stream.  <code>null</code> means no log
        and debug output.

        @param d the new debug output stream 
        @see #getLogStream() */
    synchronized static public void setLogStream(PrintStream s) {
	log = s;
    }

    /** the current debug output stream. <code>null</code> means no
        debug output.

        @return the current debug output stream.
        @see #setLogStream(PrintStream)
    */
    synchronized static public PrintStream getLogStream() {
	return log;
    }

    /** sets an error output stream.  <code>null</code> means no error
        output.

        @param d the new error output stream 
        @see #getErrorStream() */
    synchronized static public void setErrorStream(PrintStream s) {
	err = s;
    }

    /** the current error output stream. <code>null</code> means no
        error output.

        @return the current error output stream.
        @see #setErrorStream(PrintStream)
    */
    synchronized static public PrintStream getErrorStream() {
	return err;
    }
}
