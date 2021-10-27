package kr.hs.ss.s211027202;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    GameGraphic gg;
    SensorManager sm;
    Sensor sensor;

    int width, height;
    int shake_x, shake_y;
    int box_x, box_y;
    String game = "노게임";
    int shake = 0;

    int SEN = 5000;

    float x, y, z;
    float last_x, last_y, last_z;
    float speed;
    long lTime;
    long cTime;
    long gTime;
    int time = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gg = new GameGraphic(this);
        setContentView(gg);
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);

        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;
        shake_x = width/2;
        shake_y = height/2;
        box_x = 0;
        box_y = height*2/3;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (game.equals("노게임")) {
            if (event.getY() > box_y) {
                timer.start();
                game = "게임중";
                shake = 0;
                time = 30;
            }
        }
        return super.onTouchEvent(event);
    }

    CountDownTimer timer = new CountDownTimer(30 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            time--;
            gg.invalidate();
        }

        @Override
        public void onFinish() {
            game = "노게임";
            gg.invalidate();
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (game.equals("게임중")) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                cTime = System.currentTimeMillis();
                gTime = cTime-lTime;
                if (gTime > 100) {
                    lTime = cTime;
                    x = event.values[0];
                    y = event.values[1];
                    z = event.values[2];
                    speed = Math.abs(x + y + z - last_x - last_y - last_z)/gTime*1000;
                    if (speed > SEN) {
                        shake++;
                    }
                    last_x = x;
                    last_y = y;
                    last_z = z;
                    gg.invalidate();
                }

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class GameGraphic extends View {

        public GameGraphic(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint p = new Paint();
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize(100);
            canvas.drawText("shake : " + shake, shake_x, shake_y, p);
            p.setColor(Color.YELLOW);
            canvas.drawRect(box_x, box_y, width, height, p);
            p.setColor(Color.BLACK);
            if (game.equals("노게임")) {
                canvas.drawText("시작", width/2, height*5/6, p);
            } else {
                canvas.drawText("남은 시간 : " + time + "초", width/2, height*5/6, p);
            }
        }
    }
}


