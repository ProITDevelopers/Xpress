package ao.co.proitconsulting.xpress.mySignalR;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import ao.co.proitconsulting.xpress.helper.NotificationHelper;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import io.reactivex.Single;

public class MySignalRService extends Service {

    private HubConnection mHubConnection;
    private final IBinder mBinder = new LocalBinder(); // Binder given to clients

    NotificationHelper notificationHelper;

    public MySignalRService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationHelper = new NotificationHelper(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        startSignalR();
        return result;
    }

    @Override
    public void onDestroy() {

        if (mHubConnection.getConnectionState()== HubConnectionState.CONNECTED)
            mHubConnection.stop();

//        mHubConnection.stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        startSignalR();
        return mBinder;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public MySignalRService getService() {
            // Return this instance of SignalRService so clients can call public methods
            return MySignalRService.this;
        }
    }

    /**
     * method for clients (activities)
     */
//    public void sendMessage(String message) {
//        String SERVER_METHOD_SEND = "Send";
//        mHubProxy.invoke(SERVER_METHOD_SEND, message);
//    }

    private void startSignalR() {

        String token = AppPrefsSettings.getInstance().getAuthToken();

        if (TextUtils.isEmpty(token)) {
            return;
        }


        String serverUrl = "https://apivendas.xpressentregas.com/eventhub";


        mHubConnection = HubConnectionBuilder.create(serverUrl)
                .withAccessTokenProvider(Single.defer(() -> {

                    // Your logic here.
                    return Single.just(token);
                })).build();




        mHubConnection.on("UpdatedUserList",(ConnectionId, users)->{



        },String.class,String.class);

        mHubConnection.on("ReceiveMessage",(message)->{


            String data[] = message.split(":");
            String title = data[0].toUpperCase();
            String body = data[1].replaceFirst(" ","");

            notificationHelper.createNotification(title,body,true);


        },String.class);

        if (mHubConnection.getConnectionState()== HubConnectionState.DISCONNECTED)
            mHubConnection.start();


    }

}
