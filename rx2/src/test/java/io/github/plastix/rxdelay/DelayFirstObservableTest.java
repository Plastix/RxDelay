package io.github.plastix.rxdelay;

import io.reactivex.ObservableTransformer;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("Duplicates")
public class DelayFirstObservableTest {

    TestObserver<Integer> testObserver;

    PublishSubject<Boolean> pauseLifecycle;
    PublishSubject<Integer> source;

    ObservableTransformer<Integer, Integer> transformer;

    @Before
    public void setUp() {
        pauseLifecycle = PublishSubject.create();
        source = PublishSubject.create();

        transformer = RxDelay.delayFirst(pauseLifecycle.hide());

        testObserver = source.hide()
                .compose(transformer)
                .test();
    }

    @Test
    public void emitsSingleItemWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        source.onNext(0);

        testObserver.assertValue(0);
        testObserver.assertComplete();
    }

    @Test
    public void emitsFirstOfTwoItemsWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        source.onNext(0);
        source.onNext(1);

        testObserver.assertValues(0);
        testObserver.assertComplete();
    }

    @Test
    public void emitsFirstOfThreeItemsWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        source.onNext(0);
        source.onNext(1);
        source.onNext(2);

        testObserver.assertValues(0);
        testObserver.assertComplete();
    }

    @Test
    public void noEmissionSingleItemWhenViewIsDetached() {
        pauseLifecycle.onNext(false);
        source.onNext(0);

        testObserver.assertNotComplete();
        testObserver.assertNoValues();
    }

    @Test
    public void noEmissionTwoItemsWhenViewIsDetached() {
        pauseLifecycle.onNext(false);
        source.onNext(0);
        source.onNext(1);

        testObserver.assertNotComplete();
        testObserver.assertNoValues();
    }

    @Test
    public void noEmissionThreeItemsWhenViewIsDetached() {
        pauseLifecycle.onNext(false);
        source.onNext(0);
        source.onNext(1);
        source.onNext(2);

        testObserver.assertNotComplete();
        testObserver.assertNoValues();
    }

    @Test
    public void noEmissionSingleItemWhenViewIsNeverAttached() {
        source.onNext(0);

        testObserver.assertNotComplete();
        testObserver.assertNoValues();
    }

    @Test
    public void noEmissionTwoItemsWhenViewIsNeverAttached() {
        source.onNext(0);
        source.onNext(1);

        testObserver.assertNotComplete();
        testObserver.assertNoValues();
    }

    @Test
    public void noEmissionThreeItemsWhenViewIsNeverAttached() {
        source.onNext(0);
        source.onNext(1);
        source.onNext(2);

        testObserver.assertNotComplete();
        testObserver.assertNoValues();
    }

    @Test
    public void emitsFirstSingleItemWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        testObserver.assertNotComplete();
        testObserver.assertNoValues();

        source.onNext(0);

        pauseLifecycle.onNext(true);

        testObserver.assertValue(0);
        testObserver.assertValueCount(1);
        testObserver.assertComplete();
    }

    @Test
    public void emitsFirstOfTwoItemsWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        testObserver.assertNotComplete();
        testObserver.assertNoValues();

        source.onNext(0);
        source.onNext(1);

        pauseLifecycle.onNext(true);

        testObserver.assertValue(0);
        testObserver.assertValueCount(1);
        testObserver.assertComplete();
    }

    @Test
    public void emitsLastOfThreeItemsWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        testObserver.assertNotComplete();
        testObserver.assertNoValues();

        source.onNext(0);
        source.onNext(1);
        source.onNext(2);

        pauseLifecycle.onNext(true);

        testObserver.assertValue(0);
        testObserver.assertValueCount(1);
        testObserver.assertComplete();
    }

    @Test
    public void emitsErrorWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        Throwable error = new Throwable();
        source.onError(error);

        testObserver.assertNoValues();
        testObserver.assertError(error);
    }

    @Test
    public void noErrorEmittedWhenViewIsDetached() {
        pauseLifecycle.onNext(false);
        Throwable error = new Throwable();
        source.onError(error);

        testObserver.assertNoValues();
        testObserver.assertNotTerminated();
    }

    @Test
    public void noErrorEmittedWhenViewNeverAttached() {
        Throwable error = new Throwable();
        source.onError(error);

        testObserver.assertNoValues();
        testObserver.assertNotTerminated();
    }

    @Test
    public void emitErrorWhenViewReattaches() {
        pauseLifecycle.onNext(false);
        Throwable error = new Throwable();
        source.onError(error);

        pauseLifecycle.onNext(true);

        testObserver.assertNoValues();
        testObserver.assertError(error);
    }

    @Test
    public void emitErrorAfterItemsWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        source.onNext(0);
        source.onNext(1);
        source.onNext(2);
        Throwable error = new Throwable();
        source.onError(error);

        testObserver.assertNoErrors();
        testObserver.assertNoValues();

        pauseLifecycle.onNext(true);

        testObserver.assertValue(0);
        testObserver.assertNoErrors();

    }

    @Test
    public void emitErrorBeforeItemsWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        testObserver.assertNoErrors();
        testObserver.assertNoValues();

        Throwable error = new Throwable();
        source.onError(error);
        source.onNext(0);
        source.onNext(1);
        source.onNext(2);

        pauseLifecycle.onNext(true);

        testObserver.assertNoValues();
        testObserver.assertError(error);
    }

    @Test
    public void emitsErrorInBetweenItemsWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        testObserver.assertNoErrors();
        testObserver.assertNoValues();

        source.onNext(0);

        Throwable error = new Throwable();
        source.onError(error);

        source.onNext(1);
        source.onNext(2);

        pauseLifecycle.onNext(true);

        testObserver.assertValue(0);
        testObserver.assertNoErrors();
    }

    @Test
    public void emitsCompleteWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        source.onComplete();

        testObserver.assertNoValues();
        testObserver.assertComplete();
    }

    @Test
    public void noCompleteEmissionWhenViewIsDetached() {
        pauseLifecycle.onNext(false);
        source.onComplete();

        testObserver.assertNoValues();
        testObserver.assertNotComplete();
    }

    @Test
    public void emitsCompleteWhenViewReattaches() {
        pauseLifecycle.onNext(false);
        source.onComplete();

        pauseLifecycle.onNext(true);

        testObserver.assertNoValues();
        testObserver.assertComplete();
    }
}