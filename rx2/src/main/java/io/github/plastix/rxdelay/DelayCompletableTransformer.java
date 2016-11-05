package io.github.plastix.rxdelay;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Observable;

import javax.annotation.Nonnull;

final class DelayCompletableTransformer implements CompletableTransformer {

    private final Observable<Boolean> pauseLifecycle;

    DelayCompletableTransformer(@Nonnull Observable<Boolean> pauseLifecycle) {
        this.pauseLifecycle = pauseLifecycle;
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        return upstream.toObservable()
                .compose(new DelayReplayObservableTransformer<>(pauseLifecycle))
                .ignoreElements();
    }
}
