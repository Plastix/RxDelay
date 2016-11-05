package io.github.plastix.rxdelay;

import io.reactivex.SingleTransformer;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;
@SuppressWarnings("Duplicates")
public class DelaySingleTransformerTest {

    TestObserver<Integer> testObserver;

    PublishSubject<Boolean> pauseLifecycle;
    PublishSubject<Integer> source;

    SingleTransformer<Integer, Integer> transformer;

    @Before
    public void setUp() {
        pauseLifecycle = PublishSubject.create();
        source = PublishSubject.create();

        transformer = RxDelay.delaySingle(pauseLifecycle.hide());

        testObserver = source.singleOrError()
                .compose(transformer)
                .test();
    }

    @Test
    public void emitsItemWhenViewIsAttached() {
        pauseLifecycle.onNext(true);

        source.onNext(0);
        source.onComplete();

        testObserver.assertValue(0);
        testObserver.assertComplete();
    }

    @Test
    public void emitsNoValueViewIsDetached() {
        pauseLifecycle.onNext(false);

        source.onNext(0);
        source.onComplete();

        testObserver.assertNotTerminated();
        testObserver.assertNoValues();
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
    public void emitsNoValueWhenViewIsNeverDetached() {
        source.onNext(0);
        source.onComplete();

        testObserver.assertNotTerminated();
        testObserver.assertNoValues();

    }

    @Test
    public void emitsNoErrorWhenViewIsNeverDetached() {
        Throwable error = new Throwable();
        source.onError(error);

        testObserver.assertNotTerminated();
        testObserver.assertNoErrors();

    }

    @Test
    public void emitsSingleItemWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        source.onNext(0);
        source.onComplete();

        testObserver.assertNoValues();
        testObserver.assertNotComplete();

        pauseLifecycle.onNext(true);

        testObserver.assertValue(0);
        testObserver.assertComplete();
    }

    @Test
    public void emitErrorWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        Throwable error = new Throwable();
        source.onError(error);

        testObserver.assertNoErrors();

        pauseLifecycle.onNext(true);

        testObserver.awaitTerminalEvent();
        testObserver.assertError(error);
    }

}
