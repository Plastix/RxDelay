package io.github.plastix.rxdelay;

import rx.Observable;
import rx.Single;

final class DelaySingleTransformer<T> implements Single.Transformer<T, T> {

    private final Observable<Boolean> pauseLifecycle;

    DelaySingleTransformer(Observable<Boolean> pauseLifecycle) {
        this.pauseLifecycle = pauseLifecycle;
    }

    @Override
    public Single<T> call(final Single<T> single) {
        return single.toObservable()
                .compose(new DelayReplayObservableTransformer<T>(pauseLifecycle))
                .toSingle();

    }

}
