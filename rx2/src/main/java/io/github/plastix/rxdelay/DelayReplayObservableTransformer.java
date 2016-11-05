package io.github.plastix.rxdelay;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.subjects.ReplaySubject;

import javax.annotation.Nonnull;

final class DelayReplayObservableTransformer<T> implements ObservableTransformer<T, T> {

    private final Observable<Boolean> pauseLifecycle;

    DelayReplayObservableTransformer(@Nonnull Observable<Boolean> pauseLifecycle) {
        this.pauseLifecycle = pauseLifecycle;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.compose(new DelayObservableTransformer<>(pauseLifecycle, ReplaySubject.<T>create()));
    }

}