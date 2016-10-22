package io.github.plastix.rxdelay;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

import javax.annotation.Nonnull;

final class DelayObservableTransformer<T> implements Observable.Transformer<T, T> {

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final BehaviorSubject<Boolean> lifecycleWrapper = BehaviorSubject.create();
    private final Observable<Boolean> pauseLifecycle;
    private final Subject<T, T> buffer;

    DelayObservableTransformer(@Nonnull Observable<Boolean> pauseLifecycle, @Nonnull Subject<T, T> buffer) {
        this.pauseLifecycle = pauseLifecycle;
        this.buffer = buffer;
    }

    @Override
    public Observable<T> call(final Observable<T> observable) {
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
                        subscriptions.add(observable.subscribe(buffer));

                        // Wrap pauseLifecycle in a subject called lifecycleWrapper
                        // This has to do with the limitation of the switchMap operator
                        subscriptions.add(pauseLifecycle.subscribe(lifecycleWrapper));
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        subscriptions.clear();
                    }
                });
    }


}
