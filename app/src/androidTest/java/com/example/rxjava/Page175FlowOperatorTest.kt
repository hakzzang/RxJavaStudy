package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class Page175FlowOperatorTest {
    @Test
    fun `Observable_발생과소비과동일한쓰레드에서발생시키기`() {
        // Observable은 생산자(Producer) 소비자(Consumer)로 나눌 수 있다.
        // 생산과 소비가 균형적으로 이루어지고 있는 케이스
        Observable.range(1, Int.MAX_VALUE)
            .map { item ->
                println("#아이템발행:$item")
                return@map item
            }.subscribe { item ->
                Thread.sleep(100)
                println("#아이템소비:$item")
            }
        Thread.sleep(5000L)
    }

    @Test
    fun `Observable_발생과소비과다른쓰레드에서발생시키기`() {
        // 발행의 양이, 소비의 양보다 더 많은 케이스 확인
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

    @Test
    fun `Flowable_발생과소비과다른쓰레드에서발생시키기`() {
        // 생성 : 128개 먼저
        // 소비 : 96개, 둘 의 차이는 32. 생산자가 발행하기까지 걸리는 시간으로 소비자가 기다리는 일을 없게 하기 위해
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

    @Test
    fun `Flowable_interval을_통해서발생시키기`() {
        // 1. error 발생 확인 - OnErrorNotImplementedException
        // 2. flowable의 buffer-size 는 128, 구현부에 있음
        // 3. 시간에 의해서 갭이 발생되게 되면 error 발생
        Flowable.interval(10, TimeUnit.MILLISECONDS)
            .map { item ->
                println("#아이템발행:$item")
                return@map item
            }.observeOn(Schedulers.io()).subscribe( { item ->
                Thread.sleep(100)
                println("#아이템소비:$item")
            }, {
                println("#에러:$it")
            })

        Thread.sleep(10 * 1000)
    }

    @Test
    fun `Flowable_interval을_onBackpressureBuffer를_통해서발생시키기`() {
        //bufferSize- 128, onBackPressureBuffer에서 확인
        //오래된 아이템을 buffer에 유지해놓기 때문에 아이템값을 유지
        Flowable.interval(10, TimeUnit.MILLISECONDS)
            .onBackpressureBuffer()
            .map { item ->
                Thread.sleep(2000)
                println("#아이템발행:$item")
                return@map item
            }.observeOn(Schedulers.io()).subscribe { item ->
                println("#아이템소비:$item")
            }
        Thread.sleep(10 * 1000)
    }

    @Test
    fun `Flowable_interval을_onBackpressureLatest를_통해서발생시키기`() {
        //bufferSize- 128, onBackPressureBuffer에서 확인
        //최신 아이템을 buffer에 유지해놓기 때문에 아이템소비가 빠르게 오름
        val flowable = Flowable.interval(10, TimeUnit.MILLISECONDS)
            .onBackpressureLatest()
            .map { item ->
                println("#아이템발행:$item")
                return@map item
            }.observeOn(Schedulers.io())

        flowable.subscribe { item ->
            Thread.sleep(100)
            println("#아이템소비:$item")
        }

        Thread.sleep(10 * 1000)
    }

    @Test
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
        Thread.sleep(10 * 1000)
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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