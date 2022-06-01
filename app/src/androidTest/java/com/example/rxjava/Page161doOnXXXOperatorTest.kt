package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Notification
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class Page161doOnXXXOperatorTest {

    @Test
    fun `doOnEach연산자`() {
        // 아이템을 발행하기 전에 콜백으로 내용을 확인할 수 있음
        // Notification 형태로 내용을 확인할 수 있음
        // 162페이지 테스트 코드
        // 매개변수로 Notification을 받는게 특징
        // 테스트 코드 설명 : subscribe가 되기 전에 doOnEach를 통해서 확인할 수 있는게 특징
        Observable.just(1, 2, 3)
            .doOnEach { notification: Notification<Int> ->
                println("#doOnEach")
                println("value:${notification.value}")
                println("isOnNext:${notification.isOnNext}")
                println("isOnComplete:${notification.isOnComplete}")
                println("isOnError:${notification.isOnError}")
                println("error:${notification.error}")

                if (notification.error != null) {
                    notification.error.printStackTrace()
                }
            }.subscribe {
                println("#onNext")
                println("item = $it")
            }
    }

    @Test
    fun `doOnNext연산자`() {
        // Notification 대신 간단히 발행된 아이템을 확인할 수 있다.
        // 164페이지 테스트 코드
        // 매개변수로 Consumer를 받는게 특징
        // doOnEach와 비슷하지만, Consumer가 파라매터로 되어 확인할 수 있는게 특징
        // doOnNext에서 에러가 발생시에 onError로 가는게 특징
        Observable
            .just(1, 2, 3)
            .doOnNext { item: Int ->
                println("#doOnNext")
                if (item > 1) {
                    throw IllegalArgumentException()
                }
                println("item: $item")
                return@doOnNext
            }
            .subscribe({
                println("#onNext")
                println("item = $it")
            }, { throwable ->
                println("#onError")
                println("error = ${throwable.message}")
            })
    }

    @Test
    fun `doOnSubscribe연산자`() {
        // 구독 시마다 콜백을 받을 수 있도록 한다. 매개 변수로 Disposable을 받을 수 있다.
        // 165페이지 코드
        // 매개변수로 Disposable이 받는게 특징
        val src1 = Observable
            .just(1, 2, 3)
            .doOnSubscribe { disposable: Disposable ->
                println("구독 시작!")
                //return 시키는 값이 없음
                return@doOnSubscribe
            }
        src1.subscribe {
            println("#onNext")
            println("item = $it")
        }
    }

    @Test
    fun `doOnCompleted연산자`() {
        // doOnCompleted 연산자는 Emitter의 onComplete 호출로 Observable이 정상적으로 종료될 때 호출
        // 166페이지 테스트 코드
        // 매개변수로 받는게 없음
        val src1 = Observable
            .just(1, 2, 3)
            .doOnComplete {
                println("구독 완료")
                return@doOnComplete
            }
        src1.subscribe {
            println("#onNext")
            println("item = $it")
        }
    }

    @Test
    fun `doOnError연산자`() {
        // doOnError 연산자는 Observable 내부에서 onError 호출로 Observable이 정상적으로 종료되지 않을 때 호출
        // 매개변수로 Throwable이 들어온다.
        val src1 = Observable
            .just(1, 2, 3, 0)
            .map { 10 / it }
            .doOnError { throwable:Throwable ->
                println("#doOnError")
                println("오류:$throwable")
                return@doOnError
            }
        src1.subscribe({
            println("#onNext")
            println("item = $it")
        }, {
            println("#onError")
            println("error = ${it.message}")
        })
    }

    @Test
    fun `doOnTerminate_연산자-error가_발생할때_결과확인`() {
        // 페이지168 테스트 코드
        // doOnTerminate는 Observable이 종료될 때 호출되는 콜백
        // doOnComplete와의 차이점은 doOnTerminate는 오류가 발생해도 콜백이 발생
        val src1 = Observable
            .just(1, 2, 3, 0)
            .map { 10 / it }
            .doOnComplete {
                println("doOnComplete")
                //return 시키는 값이 없음
                return@doOnComplete
            }
            .doOnTerminate {
                println("doOnTerminate")
                //return 시키는 값이 없음
                return@doOnTerminate
            }

        src1.subscribe({
            println("#onNext")
            println("item = $it")
        }, {
            println("#onError")
            println("error = ${it.message}")
        })
    }

    @Test
    fun `doOnDispose연산자`() {
        // doOnDispose는 구독 중인 스트림이 dispose 메소드 호출로 폐기되는 경우 호출
        // 169 페이지 테스트 코드
        // 500ms 인터벌을 주고, 1.1초 후에 dispose한다.
        // dispose 하고, doOnDispose 확인
        val src1 = Observable
            .interval(500, TimeUnit.MILLISECONDS)
            .doOnDispose {
                println("#doOnDispose")
                return@doOnDispose
            }

        val result = src1.subscribe({
            println("#onNext")
            println("item = $it")
        }, {
            println("#onError")
            println("error = ${it.message}")
        }, {
            println("#doOnCompleted")
        })

        Thread.sleep(1100L)
        result.dispose()
        Thread.sleep(1000L)
        println("isDisposed:${result.isDisposed}")
    }

    @Test
    fun `doFinally연산자`() {
        // Observable이 onError, onComplete또는 스트림이 폐기될 때 doFinally 콜백이 호출
        val src1 = Observable
            .interval(500, TimeUnit.MILLISECONDS)
            .doOnComplete { println("#doOnComplete") }
            .doOnTerminate { println("#doOnTerminate") }
            .doFinally { println("#doFinally") }

        val result = src1.subscribe({
            println("#onNext")
            println("item = $it")
        }, {
            println("#onError")
            println("error = ${it.message}")
        }, {
            println("#doOnCompleted")
        })
        Thread.sleep(1100L)
        result.dispose()
        Thread.sleep(1000L)
        println("isDisposed:${result.isDisposed}")
    }
    // MainActivity로 이동
}