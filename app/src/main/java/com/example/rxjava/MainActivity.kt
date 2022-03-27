package com.example.rxjava

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.example.rxjava.databinding.ActivityMainBinding
import io.reactivex.rxjava3.core.Observable

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)

        main쓰레드_스케듈러_테스트코드()
    }

    //page171
    private fun `main쓰레드_스케듈러_테스트코드`() {
        println("##########main쓰레드_스케듈러_테스트코드-start########")
        val src = Observable.create<Int> { emitter ->
            for (number in 0 until 3) {
                val threadName = Thread.currentThread().name
                println("#쓰레드-create : $threadName number:$number")
                emitter.onNext(number)
                Thread.sleep(100L)
            }
            emitter.onComplete()
        }
        src.subscribe { number ->
            val subscribedThreadName = Thread.currentThread().name
            println("#쓰레드-subscribe : $subscribedThreadName number:$number")
        }
        println("##########main쓰레드_스케듈러_테스트코드-end########")
    }
}