package com.baidao.realm_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.baidao.realm_demo.model.Student;
import com.baidao.realm_demo.model.Teacher;

import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String RETIRED_TEACHER = "retired_teacher";
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

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < 2; i++) {
                    Teacher managedTeacher = realm.createObject(Teacher.class);
                    managedTeacher.setName("Teacher_" + i);
                    int studentSize = 10;
                    int start = i * studentSize;
                    int end = start + studentSize;
                    for (int j = start; j < end; j++) {
                        Student managedStudent = realm.createObject(Student.class);
                        managedStudent.name = "Student_" + j;
                        managedTeacher.getStudents().add(managedStudent);
                    }
                }
            }
        });

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Teacher teacher = realm.createObject(Teacher.class);
                teacher.setName(RETIRED_TEACHER);
            }
        });

        Log.d(TAG, "===end create===");
    }

    public void asyncQueryNoExistingRaw(View view) {
        Log.d(TAG, "===start asyncQueryNoExistingRaw===");

        String noExistingTeacher = "FangCan";
        countStudentsOfTeacher(noExistingTeacher)
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

    public void asyncQueryExistingRaw(View view) {
        Log.d(TAG, "===start asyncQueryExistingRaw===");

        Teacher teacher = realm.where(Teacher.class)
                .findFirst();
        if (teacher.isValid()) {
            countStudentsOfTeacher(teacher.getName())
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
        }

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
