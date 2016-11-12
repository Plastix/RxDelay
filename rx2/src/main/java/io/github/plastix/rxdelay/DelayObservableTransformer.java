package io.github.plastix.rxdelay;

import io.github.plastix.rxdelay.internal.DisposableDelegateObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import javax.annotation.Nonnull;

final class DelayObservableTransformer<T> implements ObservableTransformer<T, T> {

    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private final BehaviorSubject<Boolean> lifecycleWrapper = BehaviorSubject.create();
    private final Observable<Boolean> pauseLifecycle;
    private final Subject<T> buffer;

    DelayObservableTransformer(@Nonnull Observable<Boolean> pauseLifecycle, @Nonnull Subject<T> buffer) {
        this.pauseLifecycle = pauseLifecycle;
        this.buffer = buffer;
    }

    @Override
    public ObservableSource<T> apply(final Observable<T> upstream) {
        return lifecycleWrapper
                .switchMap(new Function<Boolean, ObservableSource<? extends T>>() {
                    @Override
                    public ObservableSource<? extends T> apply(Boolean isEnabled) throws Exception {
                        // buffer downstream, else send nothing
                        if (isEnabled) {
                            return buffer.doOnComplete(new Action() {
                                @Override
                                public void run() throws Exception {
                                    lifecycleWrapper.onComplete();
                                }
                            });
                        } else {
                            return Observable.never();
                        }
                    }
                })
                // Start filling our buffer when we subscribe
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        subscriptions.add(upstream.subscribeWith(new DisposableDelegateObserver<>(buffer)));

                        // Wrap pauseLifecycle in a subject called lifecycleWrapper
                        // This has to do with the limitation of the switchMap operator
                        subscriptions.add(pauseLifecycle.subscribeWith(new DisposableDelegateObserver<>(lifecycleWrapper)));

                    }
                })
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        subscriptions.clear();
                    }
                });
    }

}
