package io.github.plastix.rxdelay;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

import javax.annotation.Nonnull;

final class DelayFirstObservableTransformer<T> implements ObservableTransformer<T, T> {

    private final Observable<Boolean> pauseLifecycle;

    DelayFirstObservableTransformer(@Nonnull Observable<Boolean> pauseLifecycle) {
        this.pauseLifecycle = pauseLifecycle;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.take(1)
                .compose(new DelayLatestObservableTransformer<T>(pauseLifecycle));
    }

}