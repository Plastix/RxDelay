package io.github.plastix.rxdelay;

import rx.Observable;
import rx.subjects.ReplaySubject;

import javax.annotation.Nonnull;

final class DelayReplayObservableTransformer<T> implements Observable.Transformer<T, T> {

    private final Observable<Boolean> pauseLifecycle;

    DelayReplayObservableTransformer(@Nonnull Observable<Boolean> pauseLifecycle) {
        this.pauseLifecycle = pauseLifecycle;
    }

    @Override
    public Observable<T> call(final Observable<T> observable) {
        return observable.compose(new DelayObservableTransformer<>(pauseLifecycle, ReplaySubject.<T>create()));
    }
}