#include <elvin/elvin.h>
#include <elvin/sync_api.h>
#include <time.h>

#define MAX 100000
static int nEventCount = 0;
#define INTERVAL 12000


static void notify_cb(
    elvin_handle_t handle, elvin_subscription_t subscription,
    elvin_notification_t notification, int is_secure,
    void *ignored, elvin_error_t error)
{
    static clock_t start;
    clock_t finish;
    double dblDuration = 0;

    if( nEventCount == 0 )
    {
	// start timer
        start = clock();
        
	// output first event received

       
    }

    nEventCount++;
   
    if( (nEventCount% (int) INTERVAL) == 0 )
    {
        finish = clock();

        // output another INTERVAL events received
	
        // Work out duration
    	dblDuration = (double) ( ( finish - start ) / (double) ( CLOCKS_PER_SEC ) );
    	printf( "Took %f secs to receive another %d events\n", dblDuration, INTERVAL );
       	printf( "EventCount: %d\n", nEventCount );
    }

    if( nEventCount == (int) MAX )
    {
        finish = clock();

        // output time span to receive MAX events
    	// Work out duration
    	dblDuration = (double) ( ( finish - start ) / (double) ( CLOCKS_PER_SEC ) );
    	printf( "Took %f secs to receive %d events\n", dblDuration, MAX );
        
    }

	
    /*
    char *string;
    
    string = elvin_notification_to_string(notification,
                                          ": ",
                                          "\n",
                                          error);
    printf("%s---\n", string);
    fflush(stdout);
    elvin_notification_to_string_free(string, error);
    */
}

int main(int argc, char* argv[])
{
    elvin_error_t error;
    elvin_handle_t handle;
    elvin_subscription_t sub;

    int do_loop = 1;

    error = elvin_sync_init_default();
    handle = elvin_handle_alloc(error);
  
    // add elvin URL if one passed in
    if( argc == 2 )
    {
    	// sample elvin URL = elvin://canal.psl.cs.columbia.edu

	if (! elvin_handle_append_url(handle, argv[1], error)) 
    	{
                printf("Bad URL: no doughnut \"%s\"\n", optarg);
                exit(1);
    	}
    }

    elvin_handle_set_discovery_scope(handle, "example", error);
    elvin_sync_connect(handle, error);

    sub = elvin_sync_add_subscription(handle,
                                      "require(demo)",
                                      NULL, 1,
                                      notify_cb, NULL,
                                      error);
    printf("waiting for events...\n");
    


    elvin_sync_default_mainloop(&do_loop, error);
    


   return 0;
}

