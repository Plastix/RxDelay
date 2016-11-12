# RxDelay
Delay reactive emissions based on lifecycle events. Like [RxLifecycle](https://github.com/trello/RxLifecycle) but "pauses" observables instead of forcing completion.

## Usage
You must provide an `Observable<Boolean>` which gives RxDelay information on how to delay emissions from the source observable.

```java
myObservable
  .compose(RxDelay.delayLatest(pauseLifecycle))
  .subscribe();
```

When the `pauseLifecycle` observable emits `False`, `myObservable` emissions will be delayed until `pauseLifecycle` emits `True`. RxDelay supports different transformers which treat emissions while paused differently. See the list of transformers below.

## Installation 
Add to top level *gradle.build* file:

```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

Add to app module *gradle.build* file:
```gradle
dependencies {

    // For RxJava 1
    compile 'com.github.Plastix:RxDelay:rx1:0.5.0'

    // For RxJava 2
    compile 'com.github.Plastix:RxDelay:rx2:0.5.0'
}
```

## Transformers
RxDelay comes with three main observable transformers. All transformers emit data normally while the observable is "enabled" —that is, the last event emitted by the lifecycle observable is `True`. 

All transformers stop (or delay) emission from the source observable when the lifecycle observable emits `False`. The three transformers differ in how they handle data emitted while the source observable is "paused".

* **DelayFirst**

   DelayFirst will take only the first emission from the source observable and then emit it when the lifecycle observable emits `True`. The composed observable will complete after the source observable emits a single item. However, this completion may be delayed along with the original single item emitted from the source.

* **DelayLatest**

   DelayLatest will take the latest emission from the source and then emit it when the lifecycle observable emits `True`. The composed observable may repeat the last emission if no new item is emitted while the observable is "paused".

* **DelayReplay**

   DelayReplay will replay all previously seen emissions when the lifecycle observable emits `True`.

## Single and Completable
RxDelay supports both `Single` and `Completable` using `RxDelay.delaySingle` and `RxDelay.delayCompletable`. These transformers will delay the single emission until the lifecycle emits `True` (analogous to `DelayFirst`).

## Unsubscription
RxDelay does not automatically unsubscribe your observables for you! Make sure to always unsubscribe accordingly to avoid memory leaks.

## Attributions
RxDelay is inspired by the following libraries:

* [Nucleus](https://github.com/konmik/nucleus)
* [RxLifecycle](https://github.com/trello/RxLifecycle)
* [Arctor](https://github.com/alapshin/arctor)


## License
```
The MIT License (MIT)
=====================

Copyright © 2016 Plastix

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the “Software”), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
```
