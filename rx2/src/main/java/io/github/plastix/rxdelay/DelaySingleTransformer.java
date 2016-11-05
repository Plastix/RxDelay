package io.github.plastix.rxdelay;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;

import javax.annotation.Nonnull;

final class DelaySingleTransformer<T> implements SingleTransformer<T, T> {

    private final Observable<Boolean> pauseLifecycle;

    DelaySingleTransformer(@Nonnull Observable<Boolean> pauseLifecycle) {
        this.pauseLifecycle = pauseLifecycle;
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream.toObservable()
                .compose(new DelayFirstObservableTransformer<T>(pauseLifecycle))
                .singleOrError();
    }


}
