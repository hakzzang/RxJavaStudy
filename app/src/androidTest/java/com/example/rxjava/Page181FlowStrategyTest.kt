package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class Page181FlowStrategyTest {
    @Test
    fun `Flowable_interval을_onBackpressureBuffer를_통해서발생시키기`() {
        //진행해야하는 부분
        //181페이지 테스트 코드
        //데이터를 소비할 때까지 데이터를 버퍼에 넣어둠
        Flowable.interval(10, TimeUnit.MILLISECONDS)
            .onBackpressureBuffer()
            .observeOn(Schedulers.io())
            .map { item ->
                Thread.sleep(2000)
                println("#아이템발행:$item")
                return@map item
            }
            .subscribe { item ->
                println("#아이템소비:$item")
            }
        Thread.sleep(10 * 1000)
    }

    @Test
    fun `Flowable_interval을_onBackpressureLatest를_통해서발생시키기`() {
        //bufferSize- 128, onBackPressureBuffer에서 확인
        //interval을 통해서 10ms초마다 아이템을 만들게 되고
        //아이템의 소비는 100ms초마다 아이템이 만들어진다.
        //아이템의 소비는 버퍼에 128개 저장이 될 때까지 아이템을 소비하다가 최신 데이터만 유지하고 모두 버림
        val flowable = Flowable.interval(10, TimeUnit.MILLISECONDS)
            .onBackpressureLatest()
            .observeOn(Schedulers.io())

        flowable.subscribe { item ->
            Thread.sleep(100)
            println("#아이템소비:$item")
        }

        Thread.sleep(30 * 1000)
    }

    @Test
    fun `Flowable_interval을_onBackpressureDrop을_통해서발생시키기_1`() {
        //최신 아이템을 buffer에 유지해놓기 때문에 아이템소비를 버림
        //128 이후에는 아이템을 버림
        Flowable.range(1, 300)
            .onBackpressureDrop()
            .observeOn(Schedulers.io())
            .subscribe {
                Thread.sleep(10)
                println("#아이템소비:$it")
            }
        Thread.sleep(5000)
    }

    @Test
    fun `Flowable_interval을_onBackpressureDrop을_통해서발생시키기_2`() {
        //최신 아이템을 buffer에 유지해놓기 때문에 아이템소비를 버림
        //onBackpressureDrop은 버려지는 아이템에 대한 콜백을 제공한다.
        //onBackpressureLatest와 동일하게 동작한다.
        Flowable.interval(10, TimeUnit.MILLISECONDS)
            .onBackpressureDrop { item ->
                print("#아이템버림:$item")
            }
            .observeOn(Schedulers.io()).subscribe { item ->
                Thread.sleep(100)
                println("#아이템소비:$item")
            }
        Thread.sleep(30 * 1000)
    }

    @Test
    fun `Flowable_create이_BackpressureStrategy_MISSING전략일때_확인`() {
        // 배압 전략을 구현하지 않는다.
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
            .subscribe({ item ->
                println("#아이템소비:$item")
            }, {
                println("#아이템에러:${it.message}")
            })
        Thread.sleep(1000)
    }

    @Test
    fun `Flowable_create이_BackpressureStrategy_ERROR전략일때_확인`() {
        // 스트림에서 소비자가 생산자를 따라가지 못 하는 경우 MissionBackpressureException 발생
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
            .subscribe( { item ->
                println("#아이템소비:$item")
            }, {
                println("#아이템에러:${it.message}")
            })
        Thread.sleep(1000)
    }

    @Test
    fun `Flowable_create이_BackpressureStrategy_BUFFER전략일때_확인`() {
        // 186 페이지 테스트 코드
        // 구독자가 아이템을 소비할 때까지 발행한 아이템을 버퍼에 넣어둔다.
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
                println("#아이템소비:$item")
            }
        Thread.sleep(1000)
    }

    @Test
    fun `Flowable_create이_BackpressureStrategy_DROP전략일때_확인`() {
        // 구독자가 아이템을 소비하느라 생산자를 못 따라가는 경우 발행된 아이템을 모두 무시한다.
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
                println("#아이템소비:$item")
            }
        Thread.sleep(3000)
    }

    @Test
    fun `Flowable_create이_BackpressureStrategy_LATEST전략일때_확인`() {
        // 구독자가 아이템을 받을 준비가 될 때까지 가장 최신의 발행된 아이템들만 유지
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
                println("#아이템소비:$item")
            }
        Thread.sleep(1000)
    }
}