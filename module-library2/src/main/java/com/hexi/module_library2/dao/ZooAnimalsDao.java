package com.hexi.module_library2.dao;

import com.hexi.module_library2.model.Elephant;
import com.hexi.module_library2.model.Lion;
import com.hexi.module_library2.model.Zebra;
import com.hexi.module_library2.modules.ZooAnimalsModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by hexi on 16/11/16.
 */

public class ZooAnimalsDao {

    private RealmConfiguration realmConfiguration;
    private Realm realm;

    private static ZooAnimalsDao instance;

    public static ZooAnimalsDao getInstance() {
        if (instance == null) {
            synchronized (ZooAnimalsDao.class) {
                if (instance == null) {
                    instance = new ZooAnimalsDao();
                }
            }
        }
        return instance;
    }

    public static void destroyInstance() {
        if (instance == null) {
            return;
        }
        instance.destroy();
        instance = null;
    }

    private void destroy() {
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    private ZooAnimalsDao() {
        realmConfiguration = new RealmConfiguration.Builder()
                .name("exotic.realm")
                .modules(new ZooAnimalsModule())
                .build();
        realm = Realm.getInstance(realmConfiguration);
    }

    public void create() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createObject(Elephant.class);
                realm.createObject(Lion.class);
                realm.createObject(Zebra.class);
            }
        });
    }

    public Realm getRealm() {
        return this.realm;
    }
}
