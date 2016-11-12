package io.github.plastix.rxdelay;

import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

@SuppressWarnings("Duplicates")
public class DelayLatestObservableTransformerTest {

    TestSubscriber<Integer> testSubscriber;

    PublishSubject<Boolean> pauseLifecycle;
    PublishSubject<Integer> source;

    Observable.Transformer<Integer, Integer> transformer;

    @Before
    public void setUp() {
        testSubscriber = TestSubscriber.create();

        pauseLifecycle = PublishSubject.create();
        source = PublishSubject.create();

        transformer = RxDelay.delayLatest(pauseLifecycle.asObservable());

        source.asObservable()
                .compose(transformer)
                .subscribe(testSubscriber);
    }

    @Test
    public void emitsSingleItemWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        source.onNext(0);

        testSubscriber.assertValue(0);
        testSubscriber.assertNoTerminalEvent();
    }

    @Test
    public void emitsTwoItemsWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        source.onNext(0);
        source.onNext(1);

        testSubscriber.assertValues(0, 1);
        testSubscriber.assertNoTerminalEvent();
    }

    @Test
    public void emitsThreeItemsWhenViewIsAttached() {
        pauseLifecycle.onNext(true);
        source.onNext(0);
        source.onNext(1);
        source.onNext(2);

        testSubscriber.assertValues(0, 1, 2);
        testSubscriber.assertNoTerminalEvent();
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
    public void emitsLastSingleItemWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();

        source.onNext(0);

        pauseLifecycle.onNext(true);

        testSubscriber.assertValue(0);
        testSubscriber.assertValueCount(1);
        testSubscriber.assertNotCompleted();
    }

    @Test
    public void emitsLastOfTwoItemsWhenViewReattaches() {
        pauseLifecycle.onNext(false);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();

        source.onNext(0);
        source.onNext(1);

        pauseLifecycle.onNext(true);

        testSubscriber.assertValue(1);
        testSubscriber.assertValueCount(1);
        testSubscriber.assertNotCompleted();
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

        testSubscriber.assertValue(2);
        testSubscriber.assertValueCount(1);
        testSubscriber.assertNotCompleted();
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

        testSubscriber.assertValue(2);
        testSubscriber.assertValueCount(1);
        testSubscriber.assertError(error);
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
        testSubscriber.assertValueCount(1);
        testSubscriber.assertError(error);
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

    @Test
    public void emitsOneItemUponMultipleReattachment() {
        testSubscriber.assertNoValues();
        testSubscriber.assertNotCompleted();

        pauseLifecycle.onNext(true);
        source.onNext(0);

        testSubscriber.assertValue(0);
        testSubscriber.assertValueCount(1);

        pauseLifecycle.onNext(false);

        testSubscriber.assertValueCount(1);

        pauseLifecycle.onNext(true);

        testSubscriber.assertValues(0, 0);
        testSubscriber.assertValueCount(2);
    }

    @Test
    public void emitsTwoItemsUponMultipleReattachment() {
        testSubscriber.assertNoValues();
        testSubscriber.assertNotCompleted();

        pauseLifecycle.onNext(true);
        source.onNext(0);

        testSubscriber.assertValue(0);
        testSubscriber.assertValueCount(1);


        pauseLifecycle.onNext(false);
        source.onNext(1);

        testSubscriber.assertValue(0);
        testSubscriber.assertValueCount(1);

        pauseLifecycle.onNext(true);

        testSubscriber.assertValueCount(2);
        testSubscriber.assertValues(0, 1);
    }

    @Test
    public void emitsThreeItemsUponMultipleReattachment() {
        testSubscriber.assertNoValues();
        testSubscriber.assertNotCompleted();

        pauseLifecycle.onNext(true);
        source.onNext(0);

        testSubscriber.assertValue(0);
        testSubscriber.assertValueCount(1);


        pauseLifecycle.onNext(false);
        source.onNext(1);
        source.onNext(2);

        testSubscriber.assertValue(0);
        testSubscriber.assertValueCount(1);

        pauseLifecycle.onNext(true);

        testSubscriber.assertValueCount(2);
        testSubscriber.assertValues(0, 2);
    }

    @Test
    public void emitsErrorUponMultipleReattachment() {
        testSubscriber.assertNoValues();
        testSubscriber.assertNotCompleted();

        pauseLifecycle.onNext(true);
        source.onNext(0);

        testSubscriber.assertValue(0);
        testSubscriber.assertValueCount(1);

        pauseLifecycle.onNext(false);

        testSubscriber.assertValueCount(1);

        Throwable error = new Throwable();
        source.onError(error);

        pauseLifecycle.onNext(true);

        testSubscriber.assertValues(0, 0);
        testSubscriber.assertValueCount(2);
        testSubscriber.assertError(error);

    }

    @Test
    public void emitsErrorAndItemUponMultipleReattachment() {
        testSubscriber.assertNoValues();
        testSubscriber.assertNotCompleted();

        pauseLifecycle.onNext(true);
        source.onNext(0);

        testSubscriber.assertValue(0);
        testSubscriber.assertValueCount(1);

        pauseLifecycle.onNext(false);

        testSubscriber.assertValueCount(1);

        source.onNext(1);
        Throwable error = new Throwable();
        source.onError(error);

        pauseLifecycle.onNext(true);

        testSubscriber.assertValues(0, 1);
        testSubscriber.assertValueCount(2);
        testSubscriber.assertError(error);

    }

    @Test
    public void emitsCompletionUponMultipleReattachment() {
        testSubscriber.assertNoValues();
        testSubscriber.assertNotCompleted();

        pauseLifecycle.onNext(true);
        source.onNext(0);

        testSubscriber.assertValue(0);
        testSubscriber.assertValueCount(1);

        pauseLifecycle.onNext(false);

        testSubscriber.assertValueCount(1);

        source.onCompleted();

        pauseLifecycle.onNext(true);

        testSubscriber.assertValues(0, 0);
        testSubscriber.assertValueCount(2);
        testSubscriber.assertCompleted();

    }

    @Test
    public void emitsCompletionAndItemUponMultipleReattachment() {
        testSubscriber.assertNoValues();
        testSubscriber.assertNotCompleted();

        pauseLifecycle.onNext(true);
        source.onNext(0);

        testSubscriber.assertValue(0);
        testSubscriber.assertValueCount(1);

        pauseLifecycle.onNext(false);

        testSubscriber.assertValueCount(1);

        source.onNext(1);
        source.onCompleted();

        pauseLifecycle.onNext(true);

        testSubscriber.assertValues(0, 1);
        testSubscriber.assertValueCount(2);
        testSubscriber.assertCompleted();
    }

    @Test
    public void completionWhenLifecycleCompletes() {
        pauseLifecycle.onCompleted();

        testSubscriber.assertNoValues();
        testSubscriber.assertCompleted();
    }
}