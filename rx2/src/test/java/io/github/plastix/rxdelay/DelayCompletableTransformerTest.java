package io.github.plastix.rxdelay;

import io.reactivex.Completable;
import io.reactivex.CompletableTransformer;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("Duplicates")
public class DelayCompletableTransformerTest {

    TestObserver testObserver;

    PublishSubject<Boolean> pauseLifecycle;
    PublishSubject<Integer> source;

    CompletableTransformer transformer;

    @Before
    public void setUp() {
        pauseLifecycle = PublishSubject.create();
        source = PublishSubject.create();

        transformer = RxDelay.delayCompletable(pauseLifecycle.hide());

        testObserver = source.ignoreElements()
                .compose(transformer)
                .test();
    }

    @Test
    public void completesWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        source.onComplete();

        testObserver.assertComplete();
    }

    @Test
    public void noCompletionWhenViewIsDetached() {
        pauseLifecycle.onNext(false);
        source.onComplete();

        testObserver.assertNotTerminated();
        testObserver.assertNotComplete();
    }

    @Test
    public void emitsErrorWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        Throwable error = new Throwable();
        source.onError(error);

        testObserver.assertError(error);
    }

    @Test
    public void emitsNoErrorWhenViewIsDetached() {
        pauseLifecycle.onNext(false);
        Throwable error = new Throwable();
        source.onError(error);

        testObserver.assertNotTerminated();
        testObserver.assertNoErrors();
    }

    @Test
    public void emitsNoCompletionWhenViewIsNeverDetached() {
        source.onComplete();

        testObserver.assertNotTerminated();
        testObserver.assertNotComplete();
    }

    @Test
    public void emitsNoErrorWhenViewIsNeverDetached() {
        Throwable error = new Throwable();
        source.onError(error);

        testObserver.assertNotTerminated();
        testObserver.assertNoErrors();

    }

    @Test
    public void emitsCompletionWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        testObserver.assertNotComplete();

        source.onComplete();

        pauseLifecycle.onNext(true);

        testObserver.assertComplete();
    }

    @Test
    public void emitErrorWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        testObserver.assertNoErrors();

        Throwable error = new Throwable();
        source.onError(error);

        pauseLifecycle.onNext(true);

        testObserver.assertError(error);
    }

}
