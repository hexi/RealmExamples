package com.baidao.realm_demo;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by hexi on 16/11/1.
 */

public class OnceCallOperator<T> implements Observable.Operator<T, T> {
    @Override
    public Subscriber<? super T> call(final Subscriber<? super T> child) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                unsubscribe();
                child.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                unsubscribe();
                child.onError(e);
            }

            @Override
            public void onNext(T t) {
                unsubscribe();
                child.onNext(t);
            }
        };
    }
}
