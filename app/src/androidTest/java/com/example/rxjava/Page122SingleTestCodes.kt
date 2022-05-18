package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.observables.ConnectableObservable
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.AssertionError
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class Page122SingleTestCodes {
    @Test
    fun `PAGE122_Single의just에서_아이템발행_테스트`() {
        // Single에서는 아이템이 1개까지만 출력이 가능하다.
        val single = Single.just("Hello World")
        single.subscribe({
            println("onSuccess:$it")
        }, {
            println("onError:$it")
        })
        val test = single.test()
        test.assertValue("Hello World")
    }

    @Test
    fun `PAGE122_Single의create에서_아이템발행_테스트`() {
        // Single의 create는 1개가 정상적으로 발행이 된다.
        val single = Single.create<String> { emitter ->
            emitter.onSuccess("Hello")
        }
        single.subscribe({
            println("onSuccess:$it")
        }, {
            println("onError:$it")
        })
        val test = single.test()
        test.assertValues("Hello")
    }

    @Test
    fun `PAGE122_Single의create에서_아이템2개발행_테스트`() {
        // Single의 create는 2개가 발행이 될 경우에 에러가 발생한다.
        val single = Single.create<String> { emitter ->
            emitter.onSuccess("Hello")
            emitter.onSuccess("World")
        }

        single.subscribe({
            println("onSuccess:$it")
        }, {
            println("onError:$it")
        })
        val test = single.test()
        test.assertError(AssertionError::class.java)
    }

    @Test
    fun `PAGE123_Single의toObservable_아이템발행_테스트`() {
        // Single을 이용해 정상적으로 Observable로 변환이 가능하다.
        val observable = Single.just("Hello World").toObservable()

        observable.subscribe({
            println("onNext:$it")
        }, {
            println("onError:$it")
        }, {
            println("onComplete")
        })
        val test = observable.test()
        test.assertValue("Hello World")
    }

    @Test
    fun `PAGE123_Maybe의create에서_onSuccess출력_테스트`() {
        // Maybe는 Single과 동일하게 하나의 아이템까지 출력이 가능하다.
        val maybe = Maybe.create<Int> { emitter ->
            emitter.onSuccess(100)
            emitter.onSuccess(200)
            emitter.onComplete()
        }
        maybe.doOnSuccess { println("doOnSuccess1") }
            .doOnComplete { println("doOnComplete1") }
            .subscribe(System.out::println)

        val test = maybe.test()
        test.assertValues(100)
    }

    @Test
    fun `PAGE123_Maybe의create에서_onComplete출력_테스트`() {
        // Maybe는 Single과 다르게 아이템이 출력되지 않아도 된다.
        val maybe = Maybe.create<Int> { emitter -> emitter.onComplete() }
        maybe.doOnSuccess { println("doOnSuccess1") }
            .doOnComplete { println("doOnComplete1") }
            .subscribe(System.out::println)
        maybe.subscribe({
            println("onSuccess2")
        }, {
            println("onError")
        }, {
            println("onComplete2")
        })

        val test = maybe.test()
        test.assertComplete()
    }

    @Test
    fun `PAGE123_Maybe_just_출력_테스트`() {
        //Maybe 또한 Single과 같이 1개의 아이템을 출력
        val maybe = Maybe.just("Hello")

        maybe.subscribe({
            println(it)
        }, {
            println(it.message)
        }, {
            println("onComplete")
        })
        val test = maybe.test()
        test.assertValue("Hello")
    }

    @Test
    fun `PAGE124_Maybe의Observable변환_출력_테스트`() {
        // Observable을 이용해서 Maybe 호출시에 동작 확인
        val src1: Observable<Int> = Observable.just(1, 2, 3)
        val srcMaybe1: Maybe<Int> = src1.firstElement()
        srcMaybe1.subscribe({
            println("#1: $it")
        }, {
            println("onError #1!")
        }, {
            println("onComplete #1!")
        })

        val src2: Observable<Int> = Observable.empty()
        val srcMaybe2: Maybe<Int> = src2.firstElement()
        srcMaybe2.subscribe({
            println("#2: $it")
        }, {
            println("onError #2!")
        }, {
            println("onComplete #2!")
        })
    }

    @Test
    fun `PAGE124_Completable의create를_통한_onComplete호출_확인`() {
        // Completable을 통해서 완료 확인
        val completable = Completable.create { emitter ->
            emitter.onComplete()
        }
        completable.subscribe {
            println("completed1")
        }
        val test = completable.test()
        test.assertComplete()
    }

    @Test
    fun `PAGE124_Completable의fromRunnable실행`() {
        // Completable을 통해서 완료 확인
        val completable = Completable.fromRunnable {
            println("runnable2")
        }
        completable.subscribe {
            //onCompleted 호출
            println("completed2")
        }
        val test = completable.test()
        test.assertComplete()
    }

    @Test
    fun `Observable_interval에서sleep시_아이템출력확인`() {
        // Cold Observable 확인
        val src = Observable.interval(1, TimeUnit.SECONDS)
        src.subscribe {
            println("#1:$it")
        }
        Thread.sleep(3000L)
        src.subscribe {
            println("#2:$it")
        }
        Thread.sleep(3000L)
    }

    @Test
    fun `publish연산자와_connect연산자를_통해서_HotObservable확인`() {
        // publish 연산자를 통해서 ConnectableObservable을 생성 하고,
        // connect 연산자를 통해서 Hot Observable 확인
        val src: ConnectableObservable<Long> = Observable.interval(1, TimeUnit.SECONDS).publish()
        src.connect()
        src.subscribe{
            println("#1:$it")
        }
        Thread.sleep(3000L)
        src.subscribe {
            println("#2:$it")
        }
        Thread.sleep(3000L)
    }

    @Test
    fun `publish연산자와_autoConnect연산자를_통해서_HotObservable확인`() {
        // publish 연산자를 통해서 ConnectableObservable을 생성 하고,
        // autoConnect 연산자를 통해서 Hot Observable 확인
        // autoConnect가 1이여서 바로 데이터를 확인할 수 있음
        val src:Observable<Long> = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .publish()
            .autoConnect(1)

        src.subscribe(
            { println("#A:$it") },
            {},
            { println("#Completed A")}
        )
        Thread.sleep(5000L)
        src.subscribe(
            { println("#B:$it") },
            {},
            { println("#Completed A")}
        )
        Thread.sleep(5000L)
    }

    @Test
    fun `publish연산자와_autoConnect연산자_2를_통해서_HotObservable확인`() {
        // publish 연산자를 통해서 ConnectableObservable을 생성 하고,
        // autoConnect 연산자를 통해서 Hot Observable 확인
        // autoConnect가 2이여서 5초 후에 데이터를 확인할 수 있음
        val src:Observable<Long> = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .publish()
            .autoConnect(2)

        src.subscribe(
            { println("#A:$it") },
            {},
            { println("#Completed A")}
        )
        Thread.sleep(5000L)
        src.subscribe(
            { println("#B:$it") },
            {},
            { println("#Completed A")}
        )
        Thread.sleep(5000L)
    }

    @Test
    fun `Observable의intervable으로Disposable_dispose_메소드실행`() {
        // Disposable의 dispose로 인해서 3.5초까지 발행된 아이템만 출력
        val source: Observable<Long> = Observable.interval(1, TimeUnit.SECONDS)
        val disposable: Disposable = source.subscribe {
            println("#1:$it")
        }

        Thread {
            Thread.sleep(3500L)
            disposable.dispose()
        }.start()
        Thread.sleep(5000L)
    }

    @Test
    fun `Observable을통해서CompositeDisposable값출력확인`() {
        // CompositeDisposable의 dispose를 통해서 일괄적으로 3.5초까지 발행된 아이템만 출력
        val compositeDisposable = CompositeDisposable()
        val source = Observable.interval(
            100, TimeUnit.MILLISECONDS
        )
        val d1 = source.subscribe { println("#1:$it") }
        val d2 = source.subscribe { println("#2:$it") }
        val d3 = source.subscribe { println("#3:$it") }
        compositeDisposable.addAll(d1, d2, d3)
        Thread.sleep(350L)
        compositeDisposable.dispose()
        Thread.sleep(500L)
    }
}