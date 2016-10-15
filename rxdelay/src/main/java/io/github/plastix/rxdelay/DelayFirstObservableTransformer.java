package io.github.plastix.rxdelay;

import rx.Observable;

final class DelayFirstObservableTransformer<T> implements Observable.Transformer<T, T> {

    private final Observable<Boolean> pauseLifecycle;

    DelayFirstObservableTransformer(Observable<Boolean> pauseLifecycle) {
        this.pauseLifecycle = pauseLifecycle;
    }

    @Override
    public Observable<T> call(Observable<T> observable) {
        return observable.take(1)
                .compose(new DelayReplayObservableTransformer<T>(pauseLifecycle));
    }
}