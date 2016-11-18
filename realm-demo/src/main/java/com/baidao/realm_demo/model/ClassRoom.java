package com.baidao.realm_demo.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hexi on 16/11/17.
 */

public class ClassRoom extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;

    public ClassRoom() {
    }

    public ClassRoom(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
