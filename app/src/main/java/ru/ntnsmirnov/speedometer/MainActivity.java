package ru.ntnsmirnov.speedometer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Thread thread;
    boolean stop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Speedometer speedometer = (Speedometer) findViewById(R.id.speedometer);
        final Random rand = new Random();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (!stop) {
                    final int value = rand.nextInt(270);
                    speedometer.post(new Runnable() {
                        @Override
                        public void run() {
                            speedometer.setSpeed(value);
                        }
                    });
                    Log.d("runable", "run: newSpeed=" + value);
                    try {
                        Thread.sleep(900);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread = new Thread(runnable);
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop = true;
    }
}
