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

        main쓰레드_스케듈러_테스트코드()
        subscribeOn_io쓰레드_스케듈러_테스트코드()
        computation쓰레드_스케듈러_테스트코드()
        subscribeOn_io쓰레드_174_스케듈러_테스트코드()
    }

    //page171
    private fun `main쓰레드_스케듈러_테스트코드`() {
        println("##########main쓰레드_171-start########")
        val src = Observable.create<Int> { emitter ->
            for (number in 0 until 3) {
                val threadName = Thread.currentThread().name
                println("#main쓰레드_171-create : $threadName number:$number")
                emitter.onNext(number)
                Thread.sleep(100L)
            }
            emitter.onComplete()
        }.doFinally {
            println("##########main쓰레드_171-end########")
        }
        src.subscribe { number ->
            val subscribedThreadName = Thread.currentThread().name
            println("#main쓰레드-subscribe : $subscribedThreadName number:$number")
        }
    }

    private fun `subscribeOn_io쓰레드_스케듈러_테스트코드`() {
        //구독과 아이템 발급 모두 subscribeOn에 의해서 변함
        println("##########subscribeOn_io쓰레드_173-start########")
        val src = Observable.create<Int> { emitter ->
            for (number in 0 until 3) {
                val threadName = Thread.currentThread().name
                println("#subscribeOn_io쓰레드_173-create : $threadName number:$number")
                emitter.onNext(number)
                Thread.sleep(100L)
            }
            emitter.onComplete()
        }.doFinally {
            println("##########subscribeOn_io쓰레드_173-end########")
        }
        src.subscribeOn(Schedulers.io()).subscribe { number ->
            val subscribedThreadName = Thread.currentThread().name
            println("#subscribeOn_io쓰레드_173-subscribe : $subscribedThreadName number:$number")
        }
    }

    private fun `computation쓰레드_스케듈러_테스트코드`() {
        //구독과 아이템 발급 모두 subscribeOn에 의해서 변함
        println("##########computation쓰레드_174-start########")
        val src = Observable.create<Int> { emitter ->
            for (number in 0 until 3) {
                val threadName = Thread.currentThread().name
                println("#computation쓰레드_174-create : $threadName number:$number")
                emitter.onNext(number)
                Thread.sleep(100L)
            }
            emitter.onComplete()
        }.doFinally {
            println("##########computation쓰레드_174-end########")
        }
        src
            .observeOn(Schedulers.computation())
            .subscribeOn(Schedulers.io()).subscribe { number ->
            val subscribedThreadName = Thread.currentThread().name
            println("#computation쓰레드_174-subscribe : $subscribedThreadName number:$number")
        }
    }

    private fun `subscribeOn_io쓰레드_174_스케듈러_테스트코드`() {
        //구독과 아이템 발급 모두 subscribeOn에 의해서 변함
        println("##########subscribeOn_io쓰레드_174-start########")
        val src = Observable
            .interval(200L, TimeUnit.MILLISECONDS)
            .doFinally {
                println("##########subscribeOn_io쓰레드_174-end########")
            }

        src.subscribe { time ->
            val subscribedThreadName = Thread.currentThread().name
            println("#subscribeOn_io쓰레드_174-subscribe : $subscribedThreadName number:$time")
        }
    }
}