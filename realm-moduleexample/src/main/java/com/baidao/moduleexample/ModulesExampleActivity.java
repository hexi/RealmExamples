package com.baidao.moduleexample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidao.library.Zoo;
import com.baidao.library.model.Cat;
import com.baidao.library.model.Dog;
import com.baidao.library.model.Elephant;
import com.baidao.library.model.Lion;
import com.baidao.library.model.Zebra;
import com.baidao.library.modules.DomesticAnimalsModule;
import com.baidao.library.modules.ZooAnimalsModule;
import com.baidao.moduleexample.model.Cow;
import com.baidao.moduleexample.model.Pig;
import com.baidao.moduleexample.model.Snake;
import com.baidao.moduleexample.model.Spider;
import com.baidao.moduleexample.modules.CreepyAnimalsModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmException;

/**
* This example demonstrates how you can use modules to control which classes belong to which Realms and how you can
 * work with multiple Realms at the same time.
*/
public class ModulesExampleActivity extends Activity {

    public static final String TAG = ModulesExampleActivity.class.getName();
    private LinearLayout rootLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules_example);
        rootLayout = ((LinearLayout) findViewById(R.id.container));
        rootLayout.removeAllViews();

        // The default Realm instance implicitly knows about all classes in the realmModuleAppExample Android Studio
        // module. This does not include the classes from the realmModuleLibraryExample AS module so a Realm using this
        // configuration would know about the following classes: { Cow, Pig, Snake, Spider }
        RealmConfiguration defaultConfig = new RealmConfiguration.Builder().build();

        // It is possible to extend the default schema by adding additional Realm modules using setModule(). This can
        // also be Realm modules from libraries. The below Realm contains the following classes: { Cow, Pig, Snake,
        // Spider, Cat, Dog }
        RealmConfiguration farmAnimalsConfig = new RealmConfiguration.Builder()
                .name("farm.realm")
                .modules(Realm.getDefaultModule(), new DomesticAnimalsModule())
                .build();

        // Or you can completely replace the default schema.
        // This Realm contains the following classes: { Elephant, Lion, Zebra, Snake, Spider }
        RealmConfiguration exoticAnimalsConfig = new RealmConfiguration.Builder()
                .name("exotic.realm")
                .modules(new ZooAnimalsModule(), new CreepyAnimalsModule())
                .build();

        // Multiple Realms can be open at the same time
        showStatus("Opening multiple Realms");
        Realm defaultRealm = Realm.getInstance(defaultConfig);
        Realm farmRealm = Realm.getInstance(farmAnimalsConfig);
        Realm exoticRealm = Realm.getInstance(exoticAnimalsConfig);

        // Objects can be added to each Realm independantly
        showStatus("Create objects in the default Realm");
        defaultRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createObject(Cow.class);
                realm.createObject(Pig.class);
                realm.createObject(Snake.class);
                realm.createObject(Spider.class);
            }
        });

        showStatus("Create objects in the farm Realm");
        farmRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createObject(Cow.class);
                realm.createObject(Pig.class);
                realm.createObject(Cat.class);
                realm.createObject(Dog.class);
            }
        });

        showStatus("Create objects in the exotic Realm");
        exoticRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createObject(Elephant.class);
                realm.createObject(Lion.class);
                realm.createObject(Zebra.class);
                realm.createObject(Snake.class);
                realm.createObject(Spider.class);
            }
        });

        // You can copy objects between Realms
        showStatus("Copy objects between Realms");
        showStatus("Number of pigs on the farm : " + farmRealm.where(Pig.class).count());
        showStatus("Copy pig from defaultRealm to farmRealm");
        Pig defaultPig = defaultRealm.where(Pig.class).findFirst();
        farmRealm.beginTransaction();
        farmRealm.copyToRealm(defaultPig);
        farmRealm.commitTransaction();
        showStatus("Number of pigs on the farm : " + farmRealm.where(Pig.class).count());

        // Each Realm is restricted to only accept the classes in their schema.
        showStatus("Trying to add an unsupported class");
        defaultRealm.beginTransaction();
        try {
            defaultRealm.createObject(Elephant.class);
        } catch (RealmException expected) {
            showStatus("This throws a :" + expected.toString());
        } finally {
            defaultRealm.cancelTransaction();
        }

        // And Realms in library projects are independent from Realms in the app code
        showStatus("Interacting with library code that uses Realm internally");
        int animals = 5;
        Zoo libraryZoo = new Zoo();
        libraryZoo.open();
        showStatus("Adding animals: " + animals);
        libraryZoo.addAnimals(5);
        showStatus("Number of animals in the library Realm:" + libraryZoo.getNoOfAnimals());
        libraryZoo.close();

        // Remember to close all open Realms
        defaultRealm.close();
        farmRealm.close();
        exoticRealm.close();
    }

    private void showStatus(String txt) {
        Log.i(TAG, txt);
        TextView tv = new TextView(this);
        tv.setText(txt);
        rootLayout.addView(tv);
    }
}
