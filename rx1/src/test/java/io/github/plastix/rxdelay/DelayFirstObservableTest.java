package io.github.plastix.rxdelay;

import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

@SuppressWarnings("Duplicates")
public class DelayFirstObservableTest {

    TestSubscriber<Integer> testSubscriber;

    PublishSubject<Boolean> pauseLifecycle;
    PublishSubject<Integer> source;

    Observable.Transformer<Integer, Integer> transformer;

    @Before
    public void setUp() {
        testSubscriber = TestSubscriber.create();

        pauseLifecycle = PublishSubject.create();
        source = PublishSubject.create();

        transformer = RxDelay.delayFirst(pauseLifecycle.asObservable());

        source.asObservable()
                .compose(transformer)
                .subscribe(testSubscriber);
    }

    @Test
    public void emitsSingleItemWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        source.onNext(0);

        testSubscriber.assertValue(0);
        testSubscriber.assertCompleted();
    }

    @Test
    public void emitsFirstOfTwoItemsWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        source.onNext(0);
        source.onNext(1);

        testSubscriber.assertValues(0);
        testSubscriber.assertCompleted();
    }

    @Test
    public void emitsFirstOfThreeItemsWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        source.onNext(0);
        source.onNext(1);
        source.onNext(2);

        testSubscriber.assertValues(0);
        testSubscriber.assertCompleted();
    }

    @Test
    public void noEmissionSingleItemWhenViewIsDetached() {
        pauseLifecycle.onNext(false);
        source.onNext(0);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();
    }

    @Test
    public void noEmissionTwoItemsWhenViewIsDetached() {
        pauseLifecycle.onNext(false);
        source.onNext(0);
        source.onNext(1);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();
    }

    @Test
    public void noEmissionThreeItemsWhenViewIsDetached() {
        pauseLifecycle.onNext(false);
        source.onNext(0);
        source.onNext(1);
        source.onNext(2);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();
    }

    @Test
    public void noEmissionSingleItemWhenViewIsNeverAttached() {
        source.onNext(0);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();
    }

    @Test
    public void noEmissionTwoItemsWhenViewIsNeverAttached() {
        source.onNext(0);
        source.onNext(1);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();
    }

    @Test
    public void noEmissionThreeItemsWhenViewIsNeverAttached() {
        source.onNext(0);
        source.onNext(1);
        source.onNext(2);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();
    }

    @Test
    public void emitsFirstSingleItemWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();

        source.onNext(0);

        pauseLifecycle.onNext(true);

        testSubscriber.assertValue(0);
        testSubscriber.assertValueCount(1);
        testSubscriber.assertCompleted();
    }

    @Test
    public void emitsFirstOfTwoItemsWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();

        source.onNext(0);
        source.onNext(1);

        pauseLifecycle.onNext(true);

        testSubscriber.assertValue(0);
        testSubscriber.assertValueCount(1);
        testSubscriber.assertCompleted();
    }

    @Test
    public void emitsLastOfThreeItemsWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();

        source.onNext(0);
        source.onNext(1);
        source.onNext(2);

        pauseLifecycle.onNext(true);

        testSubscriber.assertValue(0);
        testSubscriber.assertValueCount(1);
        testSubscriber.assertCompleted();
    }

    @Test
    public void emitsErrorWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        Throwable error = new Throwable();
        source.onError(error);

        testSubscriber.assertNoValues();
        testSubscriber.assertError(error);
    }

    @Test
    public void noErrorEmittedWhenViewIsDetached() {
        pauseLifecycle.onNext(false);
        Throwable error = new Throwable();
        source.onError(error);

        testSubscriber.assertNoValues();
        testSubscriber.assertNoTerminalEvent();
    }

    @Test
    public void noErrorEmittedWhenViewNeverAttached() {
        Throwable error = new Throwable();
        source.onError(error);

        testSubscriber.assertNoValues();
        testSubscriber.assertNoTerminalEvent();
    }

    @Test
    public void emitErrorWhenViewReattaches() {
        pauseLifecycle.onNext(false);
        Throwable error = new Throwable();
        source.onError(error);

        pauseLifecycle.onNext(true);

        testSubscriber.assertNoValues();
        testSubscriber.assertError(error);
    }

    @Test
    public void emitErrorAfterItemsWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        source.onNext(0);
        source.onNext(1);
        source.onNext(2);
        Throwable error = new Throwable();
        source.onError(error);

        testSubscriber.assertNoErrors();
        testSubscriber.assertNoValues();

        pauseLifecycle.onNext(true);

        testSubscriber.assertValue(0);
        testSubscriber.assertNoErrors();

    }

    @Test
    public void emitErrorBeforeItemsWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        testSubscriber.assertNoErrors();
        testSubscriber.assertNoValues();

        Throwable error = new Throwable();
        source.onError(error);
        source.onNext(0);
        source.onNext(1);
        source.onNext(2);

        pauseLifecycle.onNext(true);

        testSubscriber.assertNoValues();
        testSubscriber.assertError(error);
    }

    @Test
    public void emitsErrorInBetweenItemsWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        testSubscriber.assertNoErrors();
        testSubscriber.assertNoValues();

        source.onNext(0);

        Throwable error = new Throwable();
        source.onError(error);

        source.onNext(1);
        source.onNext(2);

        pauseLifecycle.onNext(true);

        testSubscriber.assertValue(0);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void emitsCompleteWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        source.onCompleted();

        testSubscriber.assertNoValues();
        testSubscriber.assertCompleted();
    }

    @Test
    public void noCompleteEmissionWhenViewIsDetached() {
        pauseLifecycle.onNext(false);
        source.onCompleted();

        testSubscriber.assertNoValues();
        testSubscriber.assertNotCompleted();
    }

    @Test
    public void emitsCompleteWhenViewReattaches() {
        pauseLifecycle.onNext(false);
        source.onCompleted();

        pauseLifecycle.onNext(true);

        testSubscriber.assertNoValues();
        testSubscriber.assertCompleted();
    }
}