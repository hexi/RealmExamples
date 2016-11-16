package com.hexi.module_library2.dao;

import com.hexi.module_library2.model.Cat;
import com.hexi.module_library2.model.Dog;
import com.hexi.module_library2.modules.DomesticAnimalsModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by hexi on 16/11/16.
 */

public class DomesticAnimalsDao {

    private RealmConfiguration realmConfiguration;
    private Realm realm;

    private static DomesticAnimalsDao instance;

    public static DomesticAnimalsDao getInstance() {
        if (instance == null) {
            synchronized (DomesticAnimalsDao.class) {
                if (instance == null) {
                    instance = new DomesticAnimalsDao();
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

    private DomesticAnimalsDao() {
        realmConfiguration = new RealmConfiguration.Builder()
                .name("farm.realm")
                .modules(new DomesticAnimalsModule())
                .build();

        realm = Realm.getInstance(realmConfiguration);
    }

    public void create() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createObject(Cat.class);
                realm.createObject(Dog.class);
            }
        });
    }
}
