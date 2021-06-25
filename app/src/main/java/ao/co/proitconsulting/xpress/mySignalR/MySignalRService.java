package ao.co.proitconsulting.xpress.mySignalR;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import ao.co.proitconsulting.xpress.helper.NotificationHelper;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import io.reactivex.Single;

public class MySignalRService extends Service {


    private static final String BASE_URL_XPRESS_EVENTHUB = "https://apixpress.lengueno.com/eventhub";

    private static final String TAG = "TAG_MySignalRService";

    private HubConnection mHubConnection;
    private final IBinder mBinder = new LocalBinder(); // Binder given to clients

    private NotificationHelper notificationHelper;

    public MySignalRService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationHelper = new NotificationHelper(this);
        Log.d(TAG, "onMySignalRService: "+"onCreate()");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onMySignalRService: "+"onStartCommand()");
        startSignalR();
        return result;
    }

    @Override
    public void onDestroy() {

        if (mHubConnection!=null){
            if (mHubConnection.getConnectionState()== HubConnectionState.CONNECTED)
                mHubConnection.stop();
        }


//        mHubConnection.stop();
        Log.d(TAG, "onMySignalRService: "+"onDestroy()");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        startSignalR();
        Log.d(TAG, "onMySignalRService: "+"IBinder_onBind()");
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
            Log.d(TAG, "onMySignalRService: "+"startSignalR() token isEmpty()");
            return;
        }





        mHubConnection = HubConnectionBuilder.create(BASE_URL_XPRESS_EVENTHUB)
                .withAccessTokenProvider(Single.defer(() -> {
                    // Your logic here.
                    return Single.just(token);
                })).build();




        mHubConnection.on("UpdatedUserList",(ConnectionId, users)->{

            Log.d(TAG, "onMySignalRService: "+"UpdatedUserList: "+users);

        },String.class,String.class);

        mHubConnection.on("ReceiveMessage",(message)->{


            String data[] = message.split(":");
            String title = data[0].toUpperCase();
            String body = data[1].replaceFirst(" ","");

            notificationHelper.createNotification(title,body,true);

            Log.d(TAG, "onMySignalRService: "+"ReceiveMessage: "+title+", "+message);

        },String.class);

        if (mHubConnection!=null){
            if (mHubConnection.getConnectionState()== HubConnectionState.DISCONNECTED)
                mHubConnection.start();
        }




    }

}
