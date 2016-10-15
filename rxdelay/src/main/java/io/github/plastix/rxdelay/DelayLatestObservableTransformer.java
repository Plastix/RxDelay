package io.github.plastix.rxdelay;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.ReplaySubject;

final class DelayLatestObservableTransformer<T> implements Observable.Transformer<T, T> {

    private final Observable<Boolean> pauseLifecycle;

    DelayLatestObservableTransformer(Observable<Boolean> pauseLifecycle) {
        this.pauseLifecycle = pauseLifecycle;
    }

    @Override
    public Observable<T> call(final Observable<T> observable) {
        return observable.compose(new DelayObservableTransformer<>(pauseLifecycle, ReplaySubject.<T>createWithSize(1)));
    }
}