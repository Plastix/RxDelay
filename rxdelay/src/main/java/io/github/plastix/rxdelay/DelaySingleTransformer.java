package io.github.plastix.rxdelay;

import rx.Observable;
import rx.Single;

import javax.annotation.Nonnull;

final class DelaySingleTransformer<T> implements Single.Transformer<T, T> {

    private final Observable<Boolean> pauseLifecycle;

    DelaySingleTransformer(@Nonnull Observable<Boolean> pauseLifecycle) {
        this.pauseLifecycle = pauseLifecycle;
    }

    @Override
    public Single<T> call(final Single<T> single) {
        return single.toObservable()
                .compose(new DelayFirstObservableTransformer<T>(pauseLifecycle))
                .toSingle();

    }

}
