package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Observable
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class Page161doOnXXXOperatorTest {

    @Test
    fun `doOnEach연산자`() {
        // 아이템을 발행하기 전에 콜백으로 내용을 확인할 수 있음
        // Notification 형태로 내용을 확인할 수 있음
        Observable.just(1, 2, 3)
            .doOnEach { notification ->
                println("value:${notification.value}")
                println("isOnNext:${notification.isOnNext}")
                println("isOnComplete:${notification.isOnComplete}")
                println("isOnError:${notification.isOnError}")
                println("error:${notification.error}")

                if (notification.error != null) {
                    notification.error.printStackTrace()
                }
            }.subscribe { println("subscribed = $it") }
    }

    @Test
    fun `doOnNext연산자`() {
        // Notification 대신 간단히 발행된 아이템을 확인할 수 있다.
        Observable
            .just(1, 2, 3)
            .doOnNext { item ->
                print("item: $item")
                return@doOnNext
            }
            .subscribe { println("subscribed = $it") }
    }

    @Test
    fun `doOnSubscribe연산자`() {
        // 구독 시마다 콜백을 받을 수 있도록 한다. 매개 변수로 Disposable을 받을 수 있다.
        val src1 = Observable
            .just(1, 2, 3)
            .doOnSubscribe { item ->
                println("구독 시작")
                //return 시키는 값이 없음
                return@doOnSubscribe
            }
        src1.subscribe {
            println("구독된 아이템:$it")
        }
        val test = src1.test()
        test.assertValues(1, 2, 3)

    }

    @Test
    fun `doOnCompleted연산자`() {
        // doOnCompleted 연산자는 Emitter의 onComplete 호출로 Observable이 정상적으로 종료될 때 호출
        val src1 = Observable
            .just(1, 2, 3)
            .doOnComplete {
                println("구독 완료")
                //return 시키는 값이 없음
                return@doOnComplete
            }
        src1.subscribe {
            println(it)
        }
        val test = src1.test()
        test.assertValues(1, 2, 3)

    }

    @Test
    fun `doOnError연산자`() {
        // doOnError 연산자는 Observable 내부에서 onError 호출로 Observable이 정상적으로 종료되지 않을 때 호출
        // 매개변수로 Throwable이 들어온다.
        val src1 = Observable
            .just(1, 2, 3, 0)
            .map { 10 / it }
            .doOnError {
                println("오류:$it")
                //return 시키는 값이 없음
                return@doOnError
            }
        val test = src1.test()
        test.assertError(ArithmeticException::class.java)
    }

    @Test
    fun `doOnCompleted연산자-error가_발생할때_결과확인`() {
        // doOnComplete에서 error 발생 확인
        // error 시에 호출되지 않음
        val src1 = Observable
            .just(1, 2, 3, 0)
            .map { 10 / it }
            .doOnComplete {
                println("doOnComplete")
                //return 시키는 값이 없음
                return@doOnComplete
            }

        val test = src1.test()
        test.assertError(ArithmeticException::class.java)
    }

    @Test
    fun `doOnTerminate연산자-error가_발생할때_결과확인`() {
        // doOnTerminate에서 error 발생 확인
        // error 시에 호출됨
        val src1 = Observable
            .just(1, 2, 3, 0)
            .map { 10 / it }
            .doOnTerminate {
                println("doOnTerminate")
                //return 시키는 값이 없음
                return@doOnTerminate
            }
        val test = src1.test()
        test.assertError(ArithmeticException::class.java)
    }

    @Test
    fun `doOnDispose연산자`() {
        // doOnDispose는 구독 중인 스트림이 dispose 메소드 호출로 폐기되는 경우 호출
        val src1 = Observable
            .interval(500, TimeUnit.MILLISECONDS)
            .doOnDispose {
                println("doOnDispose")
                //return 시키는 값이 없음
                return@doOnDispose
            }
        val result = src1.subscribe({
            println("subscribe:$it")
        }, {

        }, {
            println("doOnCompleted")
        })
        Thread.sleep(1100L)
        result.dispose()
        Thread.sleep(5000L)
        println("isDisposed:${result.isDisposed}")
    }

    @Test
    fun `doFinally연산자`() {
        // Observable이 onError, onComplete또는 스트림이 폐기될 때 doFinally 콜백이 호출
        val src1 = Observable
            .interval(500, TimeUnit.MILLISECONDS)
            .doOnComplete { println("doOnComplete") }
            .doOnTerminate { println("doOnTerminate") }
            .doFinally { println("doFinally") }

        val result = src1.subscribe({
            println("subscribe:$it")
        }, {

        }, {
            println("doOnCompleted")
        })
        Thread.sleep(1100L)
        result.dispose()
        Thread.sleep(1000L)
        println("isDisposed:${result.isDisposed}")
    }
}