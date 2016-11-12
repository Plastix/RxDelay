package io.github.plastix.rxdelay;

import org.junit.Before;
import org.junit.Test;
import rx.Single;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

@SuppressWarnings("Duplicates")
public class DelaySingleTransformerTest {

    TestSubscriber<Integer> testSubscriber;

    PublishSubject<Boolean> pauseLifecycle;
    PublishSubject<Integer> source;

    Single.Transformer<Integer, Integer> transformer;

    @Before
    public void setUp() {
        testSubscriber = TestSubscriber.create();

        pauseLifecycle = PublishSubject.create();
        source = PublishSubject.create();

        transformer = RxDelay.delaySingle(pauseLifecycle.asObservable());

        source.toSingle()
                .compose(transformer)
                .subscribe(testSubscriber);
    }

    @Test
    public void emitsItemWhenViewIsAttached() {
        pauseLifecycle.onNext(true);

        source.onNext(0);
        source.onCompleted();

        testSubscriber.assertValue(0);
        testSubscriber.assertCompleted();
    }

    @Test
    public void emitsNoValueViewIsDetached() {
        pauseLifecycle.onNext(false);

        source.onNext(0);
        source.onCompleted();

        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertNoValues();
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
    public void emitsNoValueWhenViewIsNeverDetached() {
        source.onNext(0);
        source.onCompleted();

        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertNoValues();

    }

    @Test
    public void emitsNoErrorWhenViewIsNeverDetached() {
        Throwable error = new Throwable();
        source.onError(error);

        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertNoErrors();

    }

    @Test
    public void emitsSingleItemWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        source.onNext(0);
        source.onCompleted();

        testSubscriber.assertNoValues();
        testSubscriber.assertNotCompleted();

        pauseLifecycle.onNext(true);

        testSubscriber.assertValue(0);
        testSubscriber.assertCompleted();
    }

    @Test
    public void emitErrorWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        Throwable error = new Throwable();
        source.onError(error);

        testSubscriber.assertNoErrors();

        pauseLifecycle.onNext(true);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertError(error);
    }

}
