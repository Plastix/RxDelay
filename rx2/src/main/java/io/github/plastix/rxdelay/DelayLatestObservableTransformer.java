package io.github.plastix.rxdelay;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.subjects.ReplaySubject;

import javax.annotation.Nonnull;

final class DelayLatestObservableTransformer<T> implements ObservableTransformer<T, T> {

    private final Observable<Boolean> pauseLifecycle;

    DelayLatestObservableTransformer(@Nonnull Observable<Boolean> pauseLifecycle) {
        this.pauseLifecycle = pauseLifecycle;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.compose(new DelayObservableTransformer<>(pauseLifecycle, ReplaySubject.<T>createWithSize(1)));
    }

}