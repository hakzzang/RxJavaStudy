package com.example.rxjava

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rxjava.databinding.ActivityMainBinding
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)

//        newThreadTestCode()
//        trampolineTestCode()
//        page172TestCode()
//        page173TestCode()
        page174TestCode_observeOn()
//        page174TestCode_computation()
    }

    private fun newThreadTestCode() {
        // newThread 스케줄러
        // 새로운 스레드를 만들어 어떤 동작을 실행하고 싶을 때, 요청을 받을 때마다 새로운 스레드를 생성
        // subscribeOn : Schedulers.newThread(), observeOn : Schedulers.newThread(), 이름 : RxNewThreadScheduler
        println("##########start##########")
        val src = Observable.create<Int> { emitter ->
            for (number in 0 until 3) {
                val threadName = Thread.currentThread().name
                println("#Subs1 on $threadName :$number")
                emitter.onNext(number)
                Thread.sleep(100L)
            }
            emitter.onComplete()
        }.doFinally {
            println("##########end##########")
        }
        src.subscribeOn(Schedulers.newThread()).subscribe { number ->
            val subscribedThreadName = Thread.currentThread().name
            println("#Obsv1 on : $subscribedThreadName :$number")
        }

        Thread.sleep(1000L)
        val src2 = Observable.create<Int> { emitter ->
            for (number in 0 until 3) {
                val threadName = Thread.currentThread().name
                println("#Subs2 on $threadName :$number")
                emitter.onNext(number)
                Thread.sleep(100L)
            }
            emitter.onComplete()
        }.doFinally {
            println("##########end##########")
        }
        src2.subscribeOn(Schedulers.newThread()).subscribe { number ->
            val subscribedThreadName = Thread.currentThread().name
            println("#Obsv2 on : $subscribedThreadName :$number")
        }
        Thread.sleep(1000L)
    }

    private fun trampolineTestCode() {
        // trampoline 스케줄러
        // 새로운 스레드를 생성하지 않고, 현재 스레드에 무한한 크기의 대기행렬(Queue)을 생성하는 스케줄러
        // 큐에 작업을 넣은 후 1개씩 꺼내어 동작하기 때문에 순서가 보장됩니다.
        // subscribeOn : Schedulers.newThread(), observeOn : Schedulers.newThread(), 이름 : RxNewThreadScheduler
        println("##########start##########")
        val src = Observable.create<Int> { emitter ->
            for (number in 0 until 3) {
                val threadName = Thread.currentThread().name
                println("#Subs1 on $threadName :$number")
                emitter.onNext(number)
                Thread.sleep(100L)
            }
            emitter.onComplete()
        }.doFinally {
            println("##########end##########")
        }
        src.subscribeOn(Schedulers.trampoline()).subscribe { number ->
            val subscribedThreadName = Thread.currentThread().name
            println("#Obsv1 on : $subscribedThreadName :$number")
        }

        val src2 = Observable.create<Int> { emitter ->
            for (number in 0 until 3) {
                val threadName = Thread.currentThread().name
                println("#Subs2 on $threadName :$number")
                emitter.onNext(number)
                Thread.sleep(100L)
            }
            emitter.onComplete()
        }.doFinally {
            println("##########end##########")
        }
        src2.subscribeOn(Schedulers.trampoline()).subscribe { number ->
            val subscribedThreadName = Thread.currentThread().name
            println("#Obsv2 on : $subscribedThreadName :$number")
        }
    }

    private fun `page172TestCode`() {
        // 페이지 172, 테스트 코드
        // 기본적으로 Observer가 선언되고 구독되는 스레드에서 동작한다.
        // subscribeOn : main 쓰레드, observeOn : main 쓰레드, 이름 : main
        println("##########start##########")
        val src = Observable.create<Int> { emitter ->
            for (number in 0 until 3) {
                val threadName = Thread.currentThread().name
                println("#Subs on $threadName :$number")
                emitter.onNext(number)
                Thread.sleep(100L)
            }
            emitter.onComplete()
        }.doFinally {
            println("##########end##########")
        }
        src.subscribeOn(Schedulers.newThread()).subscribe { number ->
            val subscribedThreadName = Thread.currentThread().name
            println("#Obsv on : $subscribedThreadName :$number")
        }
    }

    private fun `page173TestCode`() {
        // 페이지 173, 테스트 코드
        // 1. subscribeOn 연산자만 있고, observeOn이 없다면 해당 스케줄러는 체인 전체에 작용한다.
        // subscribeOn : Schedulers.io(), observeOn : Schedulers.io(), 이름 : RxCachedThreadScheduler-X
        println("##########start########")
        val src = Observable.create<Int> { emitter ->
            for (number in 0 until 3) {
                val threadName = Thread.currentThread().name
                println("#Subs on $threadName :$number")
                emitter.onNext(number)
                Thread.sleep(100L)
            }
            emitter.onComplete()
        }.doFinally {
            println("##########end########")
        }
        src.subscribeOn(Schedulers.io()).subscribe { number ->
            val subscribedThreadName = Thread.currentThread().name
            println("#Obsv on : $subscribedThreadName :$number")
        }
    }

    private fun `page174TestCode_observeOn`() {
        // 페이지 174, 테스트 코드
        // 1. subscribeOn 연산자만 있고, observeOn이 없다면 해당 스케줄러는 체인 전체에 작용한다.
        // subscribeOn : Schedulers.io(), observeOn Schedulers.computation(),
        // 이름 : RxCachedThreadScheduler-X, RxComputationThreadPool
        println("##########start##########")
        val src = Observable.create<Int> { emitter ->
            for (number in 0 until 3) {
                val threadName = Thread.currentThread().name
                println("#Subs on $threadName :$number")
                emitter.onNext(number)
                Thread.sleep(100L)
            }
            emitter.onComplete()
        }.doFinally {
            println("##########end##########")
        }
        src
            .observeOn(Schedulers.computation())
            .subscribeOn(Schedulers.io()).subscribe { number ->
                val subscribedThreadName = Thread.currentThread().name
                println("#Obsv on : $subscribedThreadName :$number")
        }
    }

    private fun `page174TestCode_computation`() {
        // 페이지 174 하단 테스트 코드
        // interval, timer, replay, buffer 등의 연산자는 computation 스케줄러로 사용됨
        println("##########start##########")
        val src = Observable
            .interval(200L, TimeUnit.MILLISECONDS)
            .doFinally {
                println("##########end##########")
            }

        src.subscribe { time ->
            val subscribedThreadName = Thread.currentThread().name
            println("#Obsv on : $subscribedThreadName :$time")
        }
    }
}