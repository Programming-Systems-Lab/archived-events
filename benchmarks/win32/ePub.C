#include <elvin/elvin.h>
#include <elvin/sync_api.h>
#include <time.h>

#define MAX 100000

int main(int argc, char* argv[])
{
    elvin_error_t error;
    elvin_handle_t handle;
    elvin_notification_t notif;

    //**********************************//
    clock_t start;
    clock_t finish;
    double dblDuration = 0;
    int i = 0;

    //**********************************//

    error = elvin_sync_init_default();
    handle = elvin_handle_alloc(error);

    elvin_handle_set_discovery_scope(handle, "example", error);
    elvin_sync_connect(handle, error);


    start = clock();
    for( i = 0; i < MAX; i++ )
    {
        notif = elvin_notification_alloc(error);
        elvin_notification_add_string(notif, "demo", "from C", error);
        elvin_notification_add_int32(notif, "number", 123, error);
        elvin_sync_notify(handle, notif, 1, NULL, error);
        elvin_notification_free(notif, error);
    }
    finish = clock();

    // Work out duration
    dblDuration = (double) ( ( finish - start ) / (double) ( CLOCKS_PER_SEC ) );
    printf( "Took %f secs to publish %d events\n", dblDuration, MAX );    

    elvin_sync_disconnect(handle, error);
    return 0;
}


