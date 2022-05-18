package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.exceptions.OnErrorNotImplementedException
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Page156ErrorHandlingOperatorTest {
    @Test
    fun `onError를통한_이벤트통지_비처리_테스트`() {
        // 예외 사항을 알리고자 onError를 호출하여 오류 이벤트를 통지한다.
        // a를 integer로 변환하지 못 하여 Exception이 발생한다.
        val src1 = Observable
            .just("1", "2", "a", "3")
            .map { it.toInt() }

        src1.subscribe { println(it) }
    }

    @Test
    fun `onError를통한_이벤트통지_처리_테스트_`() {
        // onError 를 통해서 해당 에러를 처리할 수 있다.
        val src1 = Observable
            .just("1", "2", "a", "3")
            .map { it.toInt() }

        src1.subscribe({
            println("onNext:$it")
        }, {
            println("onError:${it.message}")
        }, {
            println("onCompleted")
        })
        val test = src1.test()
        test.assertError(NumberFormatException::class.java)
    }

    @Test
    fun `onErrorReturn연산자_테스트`() {
        // 오류가 발생하면 아이템 발행을 종료하고, 오류 처리를 위한 함수를 실행한다.
        val src1 = Observable
            .just("1", "2", "a", "4")
            .map { it.toInt() }
            .onErrorReturn { return@onErrorReturn -1 }

        src1.subscribe({
            println("onNext:$it")
        }, {
            println("onError:${it.message}")
        }, {
            println("onCompleted")
        })
        val test = src1.test()
        test.assertValues(1, 2, -1)
    }

    @Test
    fun `onErrorResumeNext연산자_테스트`() {
        // 오류가 발생하면 아이템 발행을 종료하고, 오류 처리를 위한 다른 Observable 소스로 스트림을 대체한다.
        val src1 = Observable
            .just("1", "2", "a", "4")
            .map { it.toInt() }
            .onErrorResumeNext { return@onErrorResumeNext Observable.just(100, 200, 300) }

        src1.subscribe({
            println("onNext:$it")
        }, {
            println("onError:${it.message}")
        }, {
            println("onCompleted")
        })
        val test = src1.test()
        test.assertValues(1, 2, 100, 200, 300)
    }

    @Test
    fun `retry연산자_테스트`() {
        // retry 연산자는 Observable을 재구독하도록 한다.
        // retry에 지정한 times의 횟수만큼 재실행할 수 있다.
        val src1 = Observable
            .just("1", "2", "a", "4")
            .map { it.toInt() }
            .retry(1)

        src1.subscribe({
            println("onNext:$it")
        }, {
            println("onError:${it}")
        }, {
            println("onCompleted")
        })
        val test = src1.test()
        test.assertValues(1, 2, 1, 2)
        test.assertError(NumberFormatException::class.java)
    }
}