// SienaConsoleTest.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <sienaconf.h>

#ifdef USING_WIN32
#include <stdlib.h>
#define sleep _sleep
#else
#include <unistd.h>
#endif

#include <iostream.h>
#include <exception>
						// include Siena headers
#include <siena/Siena.h>
#include <siena/ThinClient.h>
#include <time.h>
#include <string>

#include <iostream>

static const char* APPNAME = "SienaConsoleTest";

using namespace std;

const int MAX = 4000;

void ReportEvent( LPCTSTR szEvent )
{
    HANDLE h; 
	int nLen = strlen( szEvent );
	
	if( nLen == 0 )
		return;
	
	LPTSTR* szMsg = new LPTSTR[1];
	szMsg[0] = new char[nLen + 1];
	
	strcpy( szMsg[0], szEvent );
	
	 
    h = RegisterEventSource(NULL,  // uses local computer 
             APPNAME);          // source name 
    if (h == NULL) 
        goto EXIT_REPORT_EVENT; 
 
	if (!ReportEvent(h,         // event log handle 
            EVENTLOG_INFORMATION_TYPE,  // event type 
            0,                  // category zero 
            1,					// event identifier 
            NULL,               // no user security identifier 
            1,                  // one substitution string 
            0,              // no data 
            (LPCTSTR*) szMsg,	// pointer to string array 
            NULL))              // pointer to data 
    {
		//get last error
		//GetLastErrorMessage();
		goto EXIT_REPORT_EVENT;
	}
 
EXIT_REPORT_EVENT:

	if( szMsg[0] )
		delete [] szMsg[0];

	if( szMsg )
		delete [] szMsg;

	szMsg = NULL;

    DeregisterEventSource(h); 
} 

int main (int argc, char *argv[])
{
    try 
	{					// handles cmd-line params
		if (argc != 2) 
		{
			std::cerr << "usage: " << argv[0] << " <uri>" << endl;
			return 1;
		}

		clock_t start;
		clock_t finish;
		double dblDuration = 0;
			
		ThinClient siena(argv[1]);		// creates interface to 
						// given master server
		Notification n;
		n["stock"] = "XYZ";
 
		start = clock();

		for( int i = 0; i < MAX; i++ )
		{
			if( i == 0 )
				ReportEvent( "First Event published" );
			
			n["price"] = i;
			siena.publish(n);
		}

		finish = clock();
		dblDuration = (double) ( ( finish - start ) / (double) ( CLOCKS_PER_SEC ) );
		char szBuffer[33];
		memset( szBuffer, 0, sizeof(szBuffer) );
		_gcvt( dblDuration, 10, szBuffer );
		
		string strMsg = "Took ";
		strMsg += szBuffer;
		strMsg += " secs to publish ";

		memset( szBuffer, 0, sizeof(szBuffer) );
		itoa( MAX, szBuffer, 10 );

		strMsg += szBuffer;
		strMsg += " events";

		std::cout << strMsg << endl;

		ReportEvent( strMsg.c_str() );

		siena.shutdown();			// closes Siena interface
    } 
	catch (exception &ex) 
	{
		std::cout << "error: " << ex.what() << endl;
    }
    return 0;
}


