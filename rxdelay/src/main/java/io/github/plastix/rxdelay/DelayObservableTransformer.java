package io.github.plastix.rxdelay;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.subscriptions.Subscriptions;

final class DelayObservableTransformer<T> implements Observable.Transformer<T, T> {

    private final Subscription[] subscription = {Subscriptions.unsubscribed()};
    private final PublishSubject<Boolean> lifecycleWrapper;
    private final Observable<Boolean> pauseLifecycle;
    private final Subject<T, T> buffer;

    DelayObservableTransformer(Observable<Boolean> pauseLifecycle, Subject<T, T> buffer) {
        this.pauseLifecycle = pauseLifecycle;
        this.buffer = buffer;
        this.lifecycleWrapper = PublishSubject.create();
    }

    @Override
    public Observable<T> call(final Observable<T> observable) {
        // Wrap pauseLifecycle in a subject called lifecycleWrapper
        // This has to do with the limitation of the switchMap operator
        pauseLifecycle.subscribe(lifecycleWrapper);

        return lifecycleWrapper
                .switchMap(new Func1<Boolean, Observable<T>>() {
                    @Override
                    public Observable<T> call(Boolean isEnabled) {
                        // If our observable is enabled, send events from our
                        // buffer downstream, else send nothing
                        if (isEnabled) {
                            // switchMap ignores completion events from the
                            // inner observable. When our inner observable completes
                            // Make sure to forward to the lifecycle subject
                            // This ensures that our new observable completes
                            // when the composed observable does
                            return buffer.doOnCompleted(new Action0() {
                                @Override
                                public void call() {
                                    lifecycleWrapper.onCompleted();
                                }
                            });
                        } else {
                            return Observable.never();
                        }
                    }
                })
                // Start filling our buffer when we subscribe
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        subscription[0] = observable.subscribe(buffer);
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        subscription[0].unsubscribe();
                    }
                });
    }


}
