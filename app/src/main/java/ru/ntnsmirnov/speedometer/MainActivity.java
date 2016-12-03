package ru.ntnsmirnov.speedometer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Speedometer speedometer = (Speedometer)findViewById(R.id.speedometer);
        speedometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speedometer.accelerate();
            }
        });
    }
}
