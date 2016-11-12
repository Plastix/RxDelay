package io.github.plastix.rxdelay.internal;

import io.reactivex.Observer;
import io.reactivex.observers.DisposableObserver;

import javax.annotation.Nonnull;

/**
 * DisposableObserver that delegates all callbacks to another Observer.
 */
public class DisposableDelegateObserver<T> extends DisposableObserver<T> {

    private Observer<T> delegate;

    public DisposableDelegateObserver(@Nonnull Observer<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onNext(T value) {
        delegate.onNext(value);
    }

    @Override
    public void onError(Throwable e) {
        delegate.onError(e);
    }

    @Override
    public void onComplete() {
        delegate.onComplete();
    }
}
