package com.example.rxjava

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rxjava.databinding.ActivityMainBinding
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)

//        main쓰레드_스케듈러_테스트코드()
//        subscribeOn_io쓰레드_스케듈러_테스트코드()
//        computation쓰레드_스케듈러_테스트코드()
//        subscribeOn_io쓰레드_174_스케듈러_테스트코드()
//        Observable_발생과소비과동일한쓰레드에서발생시키기()
//        Observable_발생과소비과다른쓰레드에서발생시키기()
//        Flowable_interval을_onBackpressureBuffer를_통해서발생시키기()
//        Flowable_interval을_onBackpressureLatest를_통해서발생시키기()
//        Flowable_create이_BackpressureStrategy_DROP전략일때_확인()
//        Flowable_create이_BackpressureStrategy_BUFFER전략일때_확인()
//        Flowable_create이_BackpressureStrategy_ERROR전략일때_확인()
//        Flowable_create이_BackpressureStrategy_MISSING전략일때_확인()
        Flowable_create이_BackpressureStrategy_LATEST전략일때_확인()
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

    fun `Observable_발생과소비과동일한쓰레드에서발생시키기`() {
        Observable.range(1, Int.MAX_VALUE)
            .map { item ->
                println("#아이템발행:$item")
                return@map item
            }.subscribe { item ->
                Thread.sleep(100)
                println("#아이템소비:$item")
            }
        Thread.sleep(30 * 1000)
    }

    fun `Observable_발생과소비과다른쓰레드에서발생시키기`() {
        Observable.range(1, Int.MAX_VALUE)
            .map { item ->
                println("#아이템발행:$item")
                return@map item
            }.observeOn(Schedulers.io()).subscribe { item ->
                Thread.sleep(100)
                println("#아이템소비:$item")
            }
        Thread.sleep(30 * 1000)
    }

    fun `Flowable_발생과소비과다른쓰레드에서발생시키기`() {
        Flowable.range(1, Int.MAX_VALUE)
            .map { item ->
                println("#아이템발행:$item")
                return@map item
            }.observeOn(Schedulers.io()).subscribe { item ->
                Thread.sleep(100)
                println("#아이템소비:$item")
            }
        Thread.sleep(10 * 1000)
    }

    fun `Flowable_interval을_통해서발생시키기`() {
        // 1. error 발생 확인 - OnErrorNotImplementedException
        // 2. flowable의 buffer-size 는 128, 구현부에 있음
        // 3. 시간에 의해서 갭이 발생되게 되면 error 발생
        Flowable.interval(100, TimeUnit.MILLISECONDS)
            .map { item ->
                println("#아이템발행:$item")
                return@map item
            }.observeOn(Schedulers.io()).subscribe { item ->
                Thread.sleep(1000)
                println("#아이템소비:$item")
            }

        Thread.sleep(10 * 1000)
    }

    fun `Flowable_interval을_onBackpressureBuffer를_통해서발생시키기`() {
        //bufferSize- 128, onBackPressureBuffer에서 확인
        //오래된 아이템을 buffer에 유지해놓기 때문에 아이템값을 유지
        Flowable.interval(10, TimeUnit.MILLISECONDS)
            .onBackpressureBuffer()
            .map { item ->
                println("#아이템발행:$item")
                return@map item
            }.observeOn(Schedulers.io()).subscribe { item ->
                Thread.sleep(100)
                println("#아이템소비:$item")
            }
    }

    fun `Flowable_interval을_onBackpressureLatest를_통해서발생시키기`() {
        //bufferSize- 128, onBackPressureBuffer에서 확인
        //최신 아이템을 buffer에 유지해놓기 때문에 아이템소비가 빠르게 오름
        Flowable.interval(10, TimeUnit.MILLISECONDS)
            .onBackpressureLatest()
            .map { item ->
                println("#아이템발행:$item")
                return@map item
            }.observeOn(Schedulers.io()).subscribe { item ->
                Thread.sleep(100)
                println("#아이템소비:$item")
            }
    }

    fun `Flowable_interval을_onBackpressureDrop을_통해서발생시키기`() {
        //bufferSize- 128, onBackPressureBuffer에서 확인
        //최신 아이템을 buffer에 유지해놓기 때문에 아이템소비를 버림
        Flowable.interval(10, TimeUnit.MILLISECONDS)
            .onBackpressureDrop { item ->
                print("#아이템버림:$item")
            }
            .map { item ->
                println("#아이템발행:$item")
                return@map item
            }.observeOn(Schedulers.io()).subscribe { item ->
                Thread.sleep(100)
                println("\n#아이템소비:$item")
            }
    }

    fun `Flowable_create이_BackpressureStrategy_BUFFER전략일때_확인`() {
        Flowable.create<Int>({ emitter ->
            for (a in 0 .. 1000) {
                if(emitter.isCancelled) {
                    return@create
                }
                emitter.onNext(a)
            }
            emitter.onComplete()
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.io())
            .subscribe { item ->
                println("\n#아이템소비:$item")
            }
    }

    fun `Flowable_create이_BackpressureStrategy_DROP전략일때_확인`() {
        Flowable.create<Int>({ emitter ->
            for (a in 0 .. 1000) {
                if(emitter.isCancelled) {
                    return@create
                }
                emitter.onNext(a)
            }
            emitter.onComplete()
        }, BackpressureStrategy.DROP).subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.io())
            .subscribe { item ->
                println("\n#아이템소비:$item")
            }
    }

    fun `Flowable_create이_BackpressureStrategy_ERROR전략일때_확인`() {
        Flowable.create<Int>({ emitter ->
            for (a in 0 .. 1000) {
                if(emitter.isCancelled) {
                    return@create
                }
                emitter.onNext(a)
            }
            emitter.onComplete()
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.io())
            .subscribe { item ->
                println("\n#아이템소비:$item")
            }
    }

    fun `Flowable_create이_BackpressureStrategy_MISSING전략일때_확인`() {
        Flowable.create<Int>({ emitter ->
            for (a in 0 .. 1000) {
                if(emitter.isCancelled) {
                    return@create
                }
                emitter.onNext(a)
            }
            emitter.onComplete()
        }, BackpressureStrategy.MISSING).subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.io())
            .subscribe { item ->
                println("\n#아이템소비:$item")
            }
    }

    fun `Flowable_create이_BackpressureStrategy_LATEST전략일때_확인`() {
        Flowable.create<Int>({ emitter ->
            for (a in 0 .. 1000) {
                if(emitter.isCancelled) {
                    return@create
                }
                emitter.onNext(a)
            }
            emitter.onComplete()
        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.io())
            .subscribe { item ->
                println("\n#아이템소비:$item")
            }
    }
}