package io.github.plastix.rxdelay;

import rx.Completable;
import rx.Observable;

import javax.annotation.Nonnull;

final class DelayCompletableTransformer implements Completable.Transformer {

    private final Observable<Boolean> pauseLifecycle;

    DelayCompletableTransformer(@Nonnull Observable<Boolean> pauseLifecycle) {
        this.pauseLifecycle = pauseLifecycle;
    }

    @Override
    public Completable call(final Completable completable) {
        return completable.toObservable()
                .compose(new DelayReplayObservableTransformer<>(pauseLifecycle))
                .toCompletable();

    }
}
