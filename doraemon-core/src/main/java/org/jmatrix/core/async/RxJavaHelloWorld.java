package org.jmatrix.core.async;

import rx.Observable;
import rx.Subscriber;

/**
 * @author jmatrix
 * @date 16/5/20
 */
public class RxJavaHelloWorld {
    public static void main(String[] args) {
        Observable<String> stringObservable = Observable.create(new Observable.OnSubscribe<String>() {
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("hello");
                subscriber.onCompleted();
            }
        });

        Subscriber<String> stringSubscriber = new Subscriber<String>() {
            public void onCompleted() {

            }

            public void onError(Throwable throwable) {

            }

            public void onNext(String s) {
                System.out.println(s);
            }
        };

        stringObservable.subscribe(stringSubscriber);
    }
}
