package io.github.plastix.rxdelay;

import io.github.plastix.rxdelay.internal.Preconditions;
import io.reactivex.CompletableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public final class RxDelay {

    private RxDelay() {
        throw new AssertionError("No instances");
    }

    @Nonnull
    @CheckReturnValue
    public static <T> ObservableTransformer<T, T> delayLatest(@Nonnull Observable<Boolean> pauseLifecycle) {
        Preconditions.checkNotNull(pauseLifecycle, "pauseLifecycle == null");
        return new DelayLatestObservableTransformer<T>(pauseLifecycle);
    }

    @Nonnull
    @CheckReturnValue
    public static <T> ObservableTransformer<T, T> delayFirst(@Nonnull Observable<Boolean> pauseLifecycle) {
        Preconditions.checkNotNull(pauseLifecycle, "pauseLifecycle == null");
        return new DelayFirstObservableTransformer<T>(pauseLifecycle);
    }

    @Nonnull
    @CheckReturnValue
    public static <T> ObservableTransformer<T, T> delayReplay(@Nonnull Observable<Boolean> pauseLifecycle) {
        Preconditions.checkNotNull(pauseLifecycle, "pauseLifecycle == null");
        return new DelayReplayObservableTransformer<T>(pauseLifecycle);
    }

    @Nonnull
    @CheckReturnValue
    public static <T> SingleTransformer<T, T> delaySingle(@Nonnull Observable<Boolean> pauseLifecycle) {
        Preconditions.checkNotNull(pauseLifecycle, "pauseLifecycle == null");
        return new DelaySingleTransformer<T>(pauseLifecycle);
    }

    @Nonnull
    @CheckReturnValue
    public static CompletableTransformer delayCompletable(@Nonnull Observable<Boolean> pauseLifecycle) {
        Preconditions.checkNotNull(pauseLifecycle, "pauseLifecycle == null");
        return new DelayCompletableTransformer(pauseLifecycle);
    }

}
