package io.github.plastix.rxdelay;

import io.github.plastix.rxdelay.internal.Preconditions;
import rx.Completable;
import rx.Observable;
import rx.Single;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public final class RxDelay {

    private RxDelay() {
        throw new AssertionError("No instances");
    }

    @Nonnull
    @CheckReturnValue
    public static <T> Observable.Transformer<T, T> delayLatest(@Nonnull Observable<Boolean> pauseLifecycle) {
        Preconditions.checkNotNull(pauseLifecycle, "pauseLifecycle == null");
        return new DelayLatestObservableTransformer<>(pauseLifecycle);
    }

    @Nonnull
    @CheckReturnValue
    public static <T> Observable.Transformer<T, T> delayFirst(@Nonnull Observable<Boolean> pauseLifecycle) {
        Preconditions.checkNotNull(pauseLifecycle, "pauseLifecycle == null");
        return new DelayFirstObservableTransformer<>(pauseLifecycle);
    }

    @Nonnull
    @CheckReturnValue
    public static <T> Observable.Transformer<T, T> delayReplay(@Nonnull Observable<Boolean> pauseLifecycle) {
        Preconditions.checkNotNull(pauseLifecycle, "pauseLifecycle == null");
        return new DelayReplayObservableTransformer<>(pauseLifecycle);
    }

    @Nonnull
    @CheckReturnValue
    public static <T> Single.Transformer<T, T> delaySingle(@Nonnull Observable<Boolean> pauseLifecycle) {
        Preconditions.checkNotNull(pauseLifecycle, "pauseLifecycle == null");
        return new DelaySingleTransformer<>(pauseLifecycle);
    }

    @Nonnull
    @CheckReturnValue
    public static Completable.Transformer delayCompletable(@Nonnull Observable<Boolean> pauseLifecycle) {
        Preconditions.checkNotNull(pauseLifecycle, "pauseLifecycle == null");
        return new DelayCompletableTransformer(pauseLifecycle);
    }

}
