package io.github.plastix.rxdelay;

import org.junit.Before;
import org.junit.Test;
import rx.Completable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

@SuppressWarnings("Duplicates")
public class DelayCompletableTransformerTest {

    TestSubscriber<Integer> testSubscriber;

    PublishSubject<Boolean> pauseLifecycle;
    PublishSubject<Integer> source;

    Completable.Transformer transformer;

    @Before
    public void setUp() {
        testSubscriber = TestSubscriber.create();

        pauseLifecycle = PublishSubject.create();
        source = PublishSubject.create();

        transformer = RxDelay.delayCompletable(pauseLifecycle.asObservable());

        source.toCompletable()
                .compose(transformer)
                .subscribe(testSubscriber);
    }

    @Test
    public void completesWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        source.onCompleted();

        testSubscriber.assertCompleted();
    }

    @Test
    public void noCompletionWhenViewIsDetached() {
        pauseLifecycle.onNext(false);
        source.onCompleted();

        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertNotCompleted();
    }

    @Test
    public void emitsErrorWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        Throwable error = new Throwable();
        source.onError(error);

        testSubscriber.assertError(error);
    }

    @Test
    public void emitsNoErrorWhenViewIsDetached() {
        pauseLifecycle.onNext(false);
        Throwable error = new Throwable();
        source.onError(error);

        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertNoErrors();
    }

    @Test
    public void emitsNoCompletionWhenViewIsNeverDetached() {
        source.onCompleted();

        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertNotCompleted();
    }

    @Test
    public void emitsNoErrorWhenViewIsNeverDetached() {
        Throwable error = new Throwable();
        source.onError(error);

        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertNoErrors();

    }

    @Test
    public void emitsCompletionWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        testSubscriber.assertNotCompleted();

        source.onCompleted();

        pauseLifecycle.onNext(true);

        testSubscriber.assertCompleted();
    }

    @Test
    public void emitErrorWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        testSubscriber.assertNoErrors();

        Throwable error = new Throwable();
        source.onError(error);

        pauseLifecycle.onNext(true);

        testSubscriber.assertError(error);
    }

}
