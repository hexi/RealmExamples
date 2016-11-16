package com.hexi.realm_moduleexample2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hexi.module_library2.dao.DomesticAnimalsDao;
import com.hexi.module_library2.dao.ZooAnimalsDao;
import com.hexi.realm_moduleexample2.model.Cow;
import com.hexi.realm_moduleexample2.model.Pig;
import com.hexi.realm_moduleexample2.model.Snake;
import com.hexi.realm_moduleexample2.model.Spider;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ModuleExample2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void createDomesticAnimal(View view) {
        showStatus("Create objects in the farm Realm");
        DomesticAnimalsDao.getInstance().create();
    }

    public void createZooAnimal(View view) {
        showStatus("Create objects in the exotic Realm");
        ZooAnimalsDao.getInstance().create();
    }

    public void createDefaultAnimal(View view) {
        showStatus("Create objects in the default Realm");
        Realm defaultRealm = Realm.getDefaultInstance();
        defaultRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createObject(Cow.class);
                realm.createObject(Pig.class);
                realm.createObject(Snake.class);
                realm.createObject(Spider.class);
            }
        });

        defaultRealm.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DomesticAnimalsDao.destroyInstance();
        ZooAnimalsDao.destroyInstance();
    }

    private void showStatus(String txt) {
        Log.d(TAG, txt);
    }
}
