package io.github.plastix.rxdelay;

import io.github.plastix.rxdelay.internal.Preconditions;
import rx.Completable;
import rx.Observable;
import rx.Single;

public final class RxDelay {

    private RxDelay() {
        throw new AssertionError("No instances");
    }

    public static <T> Observable.Transformer<T, T> delayLatest(Observable<Boolean> pauseLifecycle) {
        Preconditions.checkNotNull(pauseLifecycle, "pauseLifecycle == null");
        return new DelayLatestObservableTransformer<>(pauseLifecycle);
    }

    public static <T> Observable.Transformer<T, T> delayFirst(Observable<Boolean> pauseLifecycle) {
        Preconditions.checkNotNull(pauseLifecycle, "pauseLifecycle == null");
        return new DelayFirstObservableTransformer<>(pauseLifecycle);
    }

    public static <T> Observable.Transformer<T, T> delayReplay(Observable<Boolean> pauseLifecycle) {
        Preconditions.checkNotNull(pauseLifecycle, "pauseLifecycle == null");
        return new DelayReplayObservableTransformer<>(pauseLifecycle);
    }

    public static <T> Single.Transformer<T, T> delaySingle(Observable<Boolean> pauseLifecycle) {
        Preconditions.checkNotNull(pauseLifecycle, "pauseLifecycle == null");
        return new DelaySingleTransformer<>(pauseLifecycle);
    }

    public static Completable.Transformer delayCompletable(Observable<Boolean> pauseLifecycle) {
        Preconditions.checkNotNull(pauseLifecycle, "pauseLifecycle == null");
        return new DelayCompletableTransformer(pauseLifecycle);
    }

}
