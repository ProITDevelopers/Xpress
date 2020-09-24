package ao.co.proitconsulting.xpress;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import ao.co.proitconsulting.xpress.mySignalR.MySignalRService;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class XpressEntregasApplication extends Application {

    private static XpressEntregasApplication mInstance;

    private MySignalRService mService;
    private boolean mBound = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initRealm();

        Intent intent = new Intent();
        intent.setClass(this, MySignalRService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public static XpressEntregasApplication getInstance() {
        return mInstance;
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration defaultRealmConfiguration = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(getString(R.string.xpress_realm_db))
                .build();
        Realm.setDefaultConfiguration(defaultRealmConfiguration);
        Realm.compactRealm(defaultRealmConfiguration);
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            MySignalRService.LocalBinder binder = (MySignalRService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
