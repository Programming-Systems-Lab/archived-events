// SienaSubscriberTest.cpp : Defines the entry point for the console application.
//
#include <comdef.h>

#include <string.h>
#include <stdlib.h>
#include <sienaconf.h>

#include <iostream>
#include <exception>
						// include Siena headers
#include <siena/Siena.h>
#include <siena/ThinClient.h>
#include <time.h>
#include <string>

const int MAX = 4000;
const int INTERVAL = 300;

static const char* APPNAME = "SienaSubscriberTest";

using namespace std;

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

static void print_usage(const char * progname) {
    cerr << "usage: " << progname 
	 << " [-udp] [-port <num>] [-host <host>] <uri-master>" << endl;
}


BOOL PackageSienaNotification( Notification* n, VARIANT& vNames, VARIANT& vData );
BOOL UnpackageSienaNotification( const VARIANT& vNames, const VARIANT& vData );

int main (int argc, char *argv[])
{
    bool udp = false;
    unsigned short port = 1969;
    const char * master = NULL;
    const char * thishost = NULL;
	int nNumEvents = 0;
    
    try 
	{
		int i;
		for(i=1; i < argc; ++i) {		// parse cmd-line params
			if(strcmp(argv[i], "-udp")==0) 
			{
				udp = true;
			} 
			else if (strcmp(argv[i], "-port")==0) 
			{
				if (++i < argc) 
				{
					port = atoi(argv[i]);
				} 
				else 
				{
					print_usage(argv[0]);
					return 1;
				}
			} 
			else if (strcmp(argv[i], "-host")==0) 
			{
				if (++i < argc) 
				{
					thishost = argv[i];
				} 
				else 
				{
					print_usage(argv[0]);
					return 1;
				}
			} 
			else 
			{
				master = argv[i];
			}
		}
		if (master == NULL) 
		{
			print_usage(argv[0]);
			return 1;
		}

		ThinClient siena(master);		// create interface to
		Receiver * r;				// given master server
		if (udp) 
		{
			r = new UDPReceiver(port, thishost);// create receiver for 
		} 
		else 
		{				// this interface
			r = new TCPReceiver(port, thishost);
		}
		siena.set_receiver(r);			// set receiver

		char szBuffer[33];
		memset( szBuffer, 0, sizeof(szBuffer) );
		clock_t start;
		clock_t finish;
		double dblDuration = 0;
			
		Filter f;				// create subscription filter
		f.add_constraint("stock", SX_eq, "XYZ");
		//f.add_constraint("price", SX_lt, (long)100);
		siena.subscribe(f);			// subscribe

		Notification* n = NULL;
		
		for( i = 0; i < MAX; i++ )
		{
			n = siena.get_notification();
			if( n != NULL )
			{
				//count number events received
				nNumEvents++;

				if( nNumEvents == 1 )
				{
					//note when first event received
					start = clock();
					string strMsg = "First Event Received";
					ReportEvent( strMsg.c_str() );
				}

				//periodic checks to see how many events received etc.
				if( ( nNumEvents % INTERVAL ) == 0 )
				{
					//note time span
					finish = clock();
					dblDuration = (double) ( ( finish - start ) / (double) ( CLOCKS_PER_SEC ) );

					memset( szBuffer, 0, sizeof(szBuffer) );
					_gcvt( dblDuration, 10, szBuffer );
					
					string strMsg = "Took ";
					strMsg += szBuffer;
					strMsg += " secs to receive another ";

					memset( szBuffer, 0, sizeof(szBuffer) );
					itoa( INTERVAL , szBuffer, 10 );

					strMsg += szBuffer;
					strMsg += " events";

					cout << strMsg << endl;

					ReportEvent( strMsg.c_str() );
				}

				if( nNumEvents == MAX )
				{
					//note time span
					finish = clock();
					dblDuration = (double) ( ( finish - start ) / (double) ( CLOCKS_PER_SEC ) );

					memset( szBuffer, 0, sizeof(szBuffer) );
					_gcvt( dblDuration, 10, szBuffer );
					
					string strMsg = "Took ";
					strMsg += szBuffer;
					strMsg += " secs to receive ";

					memset( szBuffer, 0, sizeof(szBuffer) );
					itoa( (MAX) , szBuffer, 10 );

					strMsg += szBuffer;
					strMsg += " events";

					cout << strMsg << endl;

					ReportEvent( strMsg.c_str() );

				}
			}

			delete (n);
		}
		
		
		/*for (i = 1; i < 20; ++i) 
		{		// read incoming notifications
			n = siena.get_notification();
			if (n != NULL)  
			{
				cout << "stock=" << (*n)["stock"].string_value() << " price=" << (*n)["price"].int_value() << endl;
				
				
				// Package Notification as 2 arrays of VARIANTS 
				
				VARIANT vNames;
				VariantInit( &vNames );
				VARIANT vData;
				VariantInit( &vData );
				try
				{
					PackageSienaNotification( n, vNames, vData );
					UnpackageSienaNotification( vNames, vData );
					cout << endl;
				}
				catch( _com_error& e )
				{
					//report e
					e.Description();
				}
				catch( ... )
				{
					//report unknown error occurred
				}

				
				delete(n);
				
			}
		}*/


		siena.unsubscribe();			// unsubscribe and shutdown 
		siena.shutdown();			// interface
		delete(r);
    } 
	catch (exception &ex) 
	{
		cout << "error: " << ex.what() << endl;
    }
    return 0;
}

/*	Function PackageSienaNotification, takes a Siena Notification, 
	a set of attribute name, value pairs and represents it as 2
	variant arrays. 
	One variant, vNames, holds an array of variants representing 
	the attribute names in the notification.
	The other variant, vData, holds an array of variants reprsenting
	the value (and type) information of the attribute value.

	Input parameters:	n		- pointer to a Siena Notification 
						vNames	- reference to a variant 
						vData	- reference to a variant
	
	Return values:		On Succes returns TRUE
						On Error returns FALSE */

BOOL PackageSienaNotification( Notification* n, VARIANT& vNames, VARIANT& vData )
{
	//take a siena notification and produce 2 arrays of variant
	//one array contains the names of the attribute in the event
	//the other contains the values and types of the attribute values
	
	BOOL bReturn = FALSE;
	VARIANT vAttName; //store attribute name
	VARIANT vAttData; //store atribute data
	VariantInit( &vAttName ); //init variant
	VariantInit( &vAttData ); //init variant
	map<string, AttributeValue>::iterator it;//iterator for notification map
	long ix[1]; //array to hold indecies in SAFEARRAY
	HRESULT hr = E_FAIL; 

	//if no valid notification pointer, exit nothing to do
	if( !n )
		return FALSE;

	//Clear variants
	VariantClear( &vNames );
	VariantClear( &vData );
	
	//set variant types and init SAFRARRAY pointer
	vNames.vt = VT_ARRAY;
	vNames.parray = NULL;

	vData.vt = VT_ARRAY;
	vData.parray = NULL;
	
	//get number of elements in Notification map
	int nNumElements = n->size();
	
	//if notification map contains no entries, exit nothing to do
	if( nNumElements == 0 )
		return FALSE;

	/*	set bounds for safearray (can be used for both arrays since 
		they will have the same number of elements) */
	SAFEARRAYBOUND rgsabound[1];
    
	rgsabound[0].cElements	= nNumElements; //number of elements in dimension
	rgsabound[0].lLbound	= 0; //array lower bound starts from 0
	
	//create array of VARIANTS to hold BSTR
	vNames.parray = SafeArrayCreate( VT_VARIANT, 1, rgsabound );
	
	if( !vNames.parray  )
	{
		//report out of memory condition
		goto EXIT_PACKAGE_SIENA_NOTIFICATION;
	}
	
	//create array of VARIANTS
	vData.parray = SafeArrayCreate( VT_VARIANT, 1, rgsabound );
	if( !vData.parray  )
	{
		//report out of memory condition
		goto EXIT_PACKAGE_SIENA_NOTIFICATION;
	}

	//set index lower bound
	ix[0] = rgsabound[0].lLbound;
	
	//go thru notifcation map and put data into SafeArrays
	for( it = n->begin(); it != n->end(); it++ )
	{
		//store attribute name
		string szName = (*it).first;
		_bstr_t bstrtName = _bstr_t( szName.c_str() );
		vAttName.vt = VT_BSTR;
		vAttName.bstrVal = SysAllocString( bstrtName.copy() );

		//get attribute data
		AttributeValue att = (*it).second;
		string szTemp = "";
		char* str = NULL;
		_bstr_t bstrtData( L"" );

		switch( att.type() )
		{
			case SX_null: break; //nothing to do

			case SX_string:		//get string value, pack in BSTR
								szTemp = att.string_value();
								bstrtData = _bstr_t( szTemp.c_str() );
								vAttData.vt = VT_BSTR;
								vAttData.bstrVal = SysAllocString( bstrtData.copy() );
								break; 
			
			case SX_bool:		//get value pack in VARIANT_BOOL
								vAttData.vt = VT_BOOL;
								if( att.bool_value() )
									vAttData.boolVal = VARIANT_TRUE;
								else vAttData.boolVal = VARIANT_FALSE;
								break;
			
			case SX_date:		//pack in DATE
								break;

			case SX_double:		//pack in double
								vAttData.vt = VT_R8;
								vAttData.dblVal = att.double_value();
								break;

			case SX_integer:	vAttData.vt = VT_I4;
								vAttData.lVal = att.int_value();

								break;

			default: break; //unknown type, report unknown type
		}
		
		//put variant data in safe arrays at index ix[]
		hr = SafeArrayPutElement( vNames.parray, ix, &vAttName );
		if( FAILED( hr ) )
		{
			//report error
			goto EXIT_PACKAGE_SIENA_NOTIFICATION;
		}
		
		hr = SafeArrayPutElement( vData.parray, ix, &vAttData );
		if( FAILED( hr ) )
		{
			//report error
			goto EXIT_PACKAGE_SIENA_NOTIFICATION;
		}

		ix[0]++; //increment index
		//clear variants and go repeat as necessary
		VariantClear( &vAttName );
		VariantClear( &vAttData );
	}   
	bReturn = TRUE;
	
EXIT_PACKAGE_SIENA_NOTIFICATION:

	if( !bReturn ) //something went wrong, deallocate what memory u can
	{
		if( vNames.parray )
		{
			hr = SafeArrayDestroy( vNames.parray );
		
			if( FAILED( hr ) )
			{
				//report error
			}
		}

		if( vData.parray )
		{
			hr = SafeArrayDestroy( vData.parray );

			if( FAILED( hr ) )
			{
				//report error
			}
		}

		//clear variants
		VariantClear( &vNames );
		VariantClear( &vData );
	}
	return bReturn;
}

/*	Function UnpackageSienaNotification, takes the two variants 
	containing the representation of a Siena Notification as a
	SAFEARRAY of attribute names, vNames, and a SAFEARRAY of
	attribute values, vData and prints the contents to the screen.
	
	Parameters	-	vNames a variant containing a SAFEARRAY of attribute
					names, stored as BSTRs
					vData a variant containing a SAFEARRAY of attribute
					values, stored as VARIANT

	Return values:	On Success returns TRUE
					On Error returns FALSE
*/
BOOL UnpackageSienaNotification( const VARIANT& vNames, const VARIANT& vData )
{
	BOOL bReturn = FALSE;
	HRESULT hr = E_FAIL;
	long ix[1];
	ix[0] = 0;
	long lngULimit = 0;
	_bstr_t bstrtAttName;
	VARIANT vAttData;
	VariantInit( &vAttData ); //init variant
	VARIANT vAttName;
	VariantInit( &vAttName ); //init variant

	//expecting 2 variant arrays
	if( vNames.vt != VT_ARRAY || vData.vt != VT_ARRAY )
	{
		//report error
		goto EXIT_UNPACKAGE_SIENA_NOTIFICATION;
	}

	//get lower and upper bounds of vNames array
	hr = SafeArrayGetLBound( vNames.parray, 1, &ix[0] );
	if( FAILED( hr ) )
	{
		//report error
		goto EXIT_UNPACKAGE_SIENA_NOTIFICATION;
	}

	hr = SafeArrayGetUBound( vNames.parray, 1, &lngULimit );
	if( FAILED( hr ) )
	{
		//report error
		goto EXIT_UNPACKAGE_SIENA_NOTIFICATION;
	}

	/*	For quick error checks both arrays should have the same 
		lowerbound and upper bound */

	for( ix[0]; ix[0] <= lngULimit; ix[0]++ )
	{
		//get data out
		hr = SafeArrayGetElement( vNames.parray, ix, &vAttName );
		if( FAILED( hr ) )
		{
			//report error
			goto EXIT_UNPACKAGE_SIENA_NOTIFICATION;
		}
		if( vAttName.vt != VT_BSTR )
		{
			//unexpected type, report error
			goto EXIT_UNPACKAGE_SIENA_NOTIFICATION;
		}
		
		//get attribute name
		bstrtAttName = _bstr_t( vAttName.bstrVal );

		cout << "Attribute Name: " << (char*) _bstr_t( vAttName.bstrVal ) << endl;
		
		hr = SafeArrayGetElement( vData.parray, ix, &vAttData );
		if( FAILED( hr ) )
		{
			//report error
			goto EXIT_UNPACKAGE_SIENA_NOTIFICATION;
		}
		//get value, currently looking for int, double, bstr, bool
		
		switch( vAttData.vt )
		{
			//integer	
			case VT_I4: cout << "Attribute Type - Integer" << endl;
						cout << "Attribute Value - " << vAttData.lVal << endl;
						break;
			
			//string
			case VT_BSTR:	cout << "Attribute Type - Basic STRing" << endl;
							cout << "Attribute Value - " << (char*) _bstr_t ( vAttData.bstrVal ) << endl;
							break;
			
			//double
			case VT_R8:		cout << "Attribute Type - Double" << endl;
							cout << "Attribute Value - " << vAttData.dblVal << endl;
							break;

			//boolean
			case VT_BOOL:	cout << "Attribute Type - Variant Boolean" << endl;
							cout << "Attribute Value - ";
							if( vAttData.boolVal == VARIANT_TRUE )
								cout << "Variant TRUE";
							else cout << "Variant FALSE";
							cout << endl;
							break;

			default: break; //report unexpected type
		}

	}//end for
	bReturn = TRUE;

EXIT_UNPACKAGE_SIENA_NOTIFICATION:

	return bReturn;
}