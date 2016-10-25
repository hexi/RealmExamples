package com.baidao.realmtest.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by hexi on 16/2/17.
 */
public class Person extends RealmObject {
    // All fields are by default persisted.
    private long id;
    private String name;
    private int age;
    // Other objects in a one-to-one relation must also subclass RealmObject
    private Dog dog;
    // One-to-many relations is simply a RealmList of the objects which also subclass RealmObject
    private RealmList<Cat> cats;

    //you can instruct Realm to ignore a field and not persist it.
    @Ignore
    private int tempReference;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Dog getDog() {
        return dog;
    }

    public void setDog(Dog dog) {
        this.dog = dog;
    }

    public RealmList<Cat> getCats() {
        return cats;
    }

    public void setCats(RealmList<Cat> cats) {
        this.cats = cats;
    }

    public int getTempReference() {
        return tempReference;
    }

    public void setTempReference(int tempReference) {
        this.tempReference = tempReference;
    }
}
