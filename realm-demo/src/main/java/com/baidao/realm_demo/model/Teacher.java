package com.baidao.realm_demo.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by hexi on 16/10/27.
 */

public class Teacher extends RealmObject {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private RealmList<Student> students;

    public RealmList<Student> getStudents() {
        return students;
    }

    public void setStudents(RealmList<Student> students) {
        this.students = students;
    }
}
