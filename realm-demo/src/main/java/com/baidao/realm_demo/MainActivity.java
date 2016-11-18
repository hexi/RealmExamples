package com.baidao.realm_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.baidao.realm_demo.model.ClassRoom;
import com.baidao.realm_demo.model.Desk;
import com.baidao.realm_demo.model.Student;
import com.baidao.realm_demo.model.Teacher;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String RETIRED_TEACHER = "retired_teacher";
    private static final String UN_EXISTING_TEACHER = "un_existing_teacher";
    private static final String DESK0 = "desk0";
    private static final String DESK1 = "desk1";
    private static final String CLASS_ROOM_XDF = "xdf_class";
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });

        addTeacher(null);

        addARetiredTeacher();

        addXdfClassRoom();
        Log.d(TAG, "===end create===");
    }

    private void addXdfClassRoom() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ClassRoom classRoom = realm.copyToRealm(new ClassRoom(CLASS_ROOM_XDF, "新东方"));

                Desk desk0 = realm.copyToRealmOrUpdate(new Desk(DESK0, "我是第一张桌子"));
                desk0.setClassRoom(classRoom);

                Desk desk1 = realm.copyToRealmOrUpdate(new Desk(DESK1, "我是第二张桌子"));
                desk1.setClassRoom(classRoom);

                realm.copyToRealmOrUpdate(new Desk("desk2", "我是没人要的桌子1"));
                realm.copyToRealmOrUpdate(new Desk("desk3", "我是没人要的桌子2"));
            }
        });
    }

    public void updateXdfClassName(View view) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(new ClassRoom(CLASS_ROOM_XDF, "大新东方"));
            }
        });
    }

    public void queryXdfDesk(View view) {
        realm.where(ClassRoom.class)
                .equalTo("id", CLASS_ROOM_XDF)
                .findFirstAsync()
                .<ClassRoom>asObservable()
                .filter(new Func1<ClassRoom, Boolean>() {
                    @Override
                    public Boolean call(ClassRoom classRoom) {
                        return classRoom.isLoaded();
                    }
                })
                .flatMap(new Func1<ClassRoom, Observable<RealmResults<Desk>>>() {
                    @Override
                    public Observable<RealmResults<Desk>> call(ClassRoom classRoom) {
                        if (!classRoom.isValid()) {
                            return Observable.just(null);
                        }
                        return Realm.getDefaultInstance()
                                .where(Desk.class)
                                .equalTo("classRoom.id", classRoom.getId())
                                .findAllAsync()
                                .asObservable()
                                .filter(new Func1<RealmResults<Desk>, Boolean>() {
                                    @Override
                                    public Boolean call(RealmResults<Desk> desks) {
                                        return desks.isLoaded();
                                    }
                                });
                    }
                })
                .lift(new OnceCallOperator<RealmResults<Desk>>())
                .subscribe(new Subscriber<RealmResults<Desk>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(RealmResults<Desk> desks) {
                        if (desks == null) {
                            Log.d(TAG, "===queryXdfDesk desk.size is 0");
                        }
                        Log.d(TAG, "===queryXdfDesk desk.size: " + desks.size());
                        for (Desk desk : desks) {
                            Log.d(TAG, "===queryXdfDesk desk: " + desk.getName());
                        }
                    }
                });
    }

    public void queryAllDesk(View view) {
        realm.where(Desk.class)
                .findAllAsync()
                .asObservable()
                .filter(new Func1<RealmResults<Desk>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<Desk> desks) {
                        return desks.isLoaded();
                    }
                })
                .lift(new OnceCallOperator<RealmResults<Desk>>())
                .subscribe(new Subscriber<RealmResults<Desk>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(RealmResults<Desk> desks) {
                        if (desks == null) {
                            Log.d(TAG, "===queryAllDesk desk.size is 0");
                        }
                        Log.d(TAG, "===queryAllDesk desk.size: " + desks.size());
                        for (Desk desk : desks) {
                            Log.d(TAG, "===queryAllDesk desk: " + desk.getName());
                        }
                    }
                });
    }

    private void addARetiredTeacher() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Teacher teacher = realm.createObject(Teacher.class);
                teacher.setName(RETIRED_TEACHER);
            }
        });
    }

    private AtomicInteger teacherCounter = new AtomicInteger(0);
    private AtomicInteger studentCounter = new AtomicInteger(0);

    public void addTeacher(View view) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int startTeacher = teacherCounter.get();
                int endTeacher = teacherCounter.addAndGet(2);
                for (int i = startTeacher; i < endTeacher; i++) {
                    Teacher managedTeacher = realm.createObject(Teacher.class);
                    managedTeacher.setName("Teacher_" + i);
                    int studentSize = 10;
                    int startStudent = studentCounter.get();
                    int endStudent = studentCounter.addAndGet(studentSize);
                    for (int j = startStudent; j < endStudent; j++) {
                        Student managedStudent = realm.createObject(Student.class);
                        managedStudent.name = "Student_" + j;
                        managedTeacher.getStudents().add(managedStudent);
                    }
                }
            }
        });
    }

    public void addStudent(View view) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Student managedStudent = realm.createObject(Student.class);
                managedStudent.name = "Student_" + studentCounter.getAndAdd(1);
            }
        });
    }

    public void syncQueryNoExistingRaw(View view) {
        Teacher teacher = realm.where(Teacher.class)
                .equalTo("name", UN_EXISTING_TEACHER)
                .findFirst();
        if (teacher != null) {
            Log.d(TAG, String.format("===teacher,isLoaded:%b, isValid:%b",
                    teacher.isLoaded(),
                    teacher.isValid()));
        } else {
            Log.d(TAG, "===teacher is not found===");
        }
    }

    public void asyncQueryNoExistingRaw(View view) {
        Log.d(TAG, "===start asyncQueryNoExistingRaw===");

        countStudentsOfTeacher(UN_EXISTING_TEACHER)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "===onError ", e);
                    }

                    @Override
                    public void onNext(Integer studentsCount) {
                        Log.d(TAG, "===onNext, studentCount: " + studentsCount);
                    }
                });

        Log.d(TAG, "===end asyncQueryNoExistingRaw===");
    }

    private Observable<Integer> countStudentsOfTeacher(String teacherName) {
        return realm.where(Teacher.class)
                .equalTo("name", teacherName)
                .findFirstAsync()
                .<Teacher>asObservable()
                .filter(new Func1<Teacher, Boolean>() {
                    @Override
                    public Boolean call(Teacher teacher) {
                        Log.d(TAG, "===filter, isLoaded: " + teacher.isLoaded());
                        return teacher.isLoaded();
                    }
                })
                .flatMap(new Func1<Teacher, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Teacher teacher) {
                        if (!teacher.isValid()) {
                            //这条数据并不存在
                            return Observable.just(0);
                        }
                        return Observable.just(teacher.getStudents().size());
                    }
                });
    }

    public void syncQueryExistingRaw(View view) {
        Teacher teacher = realm.where(Teacher.class)
                .equalTo("name", "Teacher_0")
                .findFirst();
        if (teacher != null) {
            Log.d(TAG, String.format("===syncQueryExistingRaw, teacher.name:%s, teacher.students.count:%d",
                    teacher.getName(), teacher.getStudents().size()));
        } else {
            Log.d(TAG, "===syncQueryExistingRaw teacher is not found===");
        }
    }

    public void asyncQueryExistingRaw(View view) {
        Log.d(TAG, "===start asyncQueryExistingRaw===");

//        Teacher teacher = realm.where(Teacher.class)
//                .findFirst();
//        if (teacher.isValid()) {
//            countStudentsOfTeacher(teacher.getName())
//                    .subscribe(new Subscriber<Integer>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            Log.e(TAG, "===onError ", e);
//                        }
//
//                        @Override
//                        public void onNext(Integer studentsCount) {
//                            Log.d(TAG, "===onNext, studentCount: " + studentsCount);
//                        }
//                    });
//        }

        countStudentsOfTeacher("Teacher_0")
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "===onError ", e);
                    }

                    @Override
                    public void onNext(Integer studentsCount) {
                        Log.d(TAG, "===onNext, studentCount: " + studentsCount);
                    }
                });

        Log.d(TAG, "===end asyncQueryExistingRaw===");
    }

    public void queryRetiredTeacher(View view) {
        Log.d(TAG, "===start queryRetiredTeacher===");

        countStudentsOfTeacher(RETIRED_TEACHER)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "===onError ", e);
                    }

                    @Override
                    public void onNext(Integer studentsCount) {
                        Log.d(TAG, "===onNext, studentCount: " + studentsCount);
                    }
                });

        Log.d(TAG, "===end queryRetiredTeacher===");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }
}
