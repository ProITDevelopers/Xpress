package ao.co.proitconsulting.xpress;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class XpressEntregasApplication extends Application {

    private static XpressEntregasApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initRealm();
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
}
