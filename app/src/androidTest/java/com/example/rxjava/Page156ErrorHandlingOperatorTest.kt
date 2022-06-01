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
        // 157페이지의 상단의 테스트 코드
        // 테스트 코드 설명 : Observable에서 Exception이 발생할 때, onError가 없다면 에러 처리를 하지 못하고 종료된다.
        val src1 = Observable
            .just("1", "2", "a", "3")
            .map { it.toInt() }

        src1.subscribe { println(it) }
    }

    @Test
    fun `onError를통한_이벤트통지_처리_테스트_`() {
        // onError 를 통해서 해당 에러를 처리할 수 있다.
        // 157페이지의 하단의 테스트 코드
        // 테스트 코드 설명 : Observable의 Exception이 발생할 때, onError 처리가 추가된 로직
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
        // 158페이지의 테스트 코드
        // onErrorReturn을 통해서 에러가 발생할 때, 아이템 발행을 종료하고
        // onError가 발생하지 않고, 오류 처리를 하기 위한 함수
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
        // 159페이지의 테스트 코드
        // onErrorResumeNext을 통해서 에러가 발생할 때, 기존 스트림을 종료하고 새로운 스트림으로 대체한다.
        // onError가 발생하지 않고, 오류 처리를 하기 위한 함수
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
        // 160의 테스트 코드 및 161의 테스트 코드
        // error가 발생할 때, Observable을 재구독하도록 하는 함수
        // 테스트 코드 설명 : retry를 통해서 재구독하는데, retry의 times를 별도로 셋팅하지 않으면
        // 무한하게 재구독하기 때문에 명시해주고 오류를 처리해주도록 해야한다.
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