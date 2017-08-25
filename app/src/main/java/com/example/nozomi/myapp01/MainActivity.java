package com.example.nozomi.myapp01;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Intent;
import java.util.List;

public class MainActivity extends Activity implements Runnable, SensorEventListener {
    SensorManager sm;
    TextView textView;
    Handler h;
    float gx, gy, gz, xyz;
    private String fileName = "speed.arff";
    private String fileName1 = "stop.csv";
    private String fileName2 = "walk.csv";
    private String fileName3 = "run.csv";
    private Button buttonSave;
    private int stopcount, walkcount, runcount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout ll = new LinearLayout(this);
        setContentView(ll);

        textView = new TextView(this);
        ll.addView(textView);

        h = new Handler();
        h.postDelayed(this, 500);

        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text_view);

        buttonSave = (Button) findViewById(R.id.button_stop_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 立ち状態の合成加速度を取得
                stopcount=1;
            }
        });


        buttonSave = (Button) findViewById(R.id.button_walk_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 歩き状態の合成加速度を取得
                walkcount=1;
            }
        });

        buttonSave = (Button) findViewById(R.id.button_run_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 走り状態の合成加速度を取得
                runcount=1;
            }
        });

        buttonSave = (Button) findViewById(R.id.stop_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopcount=0;
                walkcount=0;
                runcount=0;
            }
        });

        buttonSave = (Button) findViewById(R.id.arff_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // arff作成
                saveFile(fileName);
            }
        });

        buttonSave = (Button) findViewById(R.id.delete_file);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //ファイル削除
                deleteFile(fileName);
                deleteFile(fileName1);
                deleteFile(fileName2);
                deleteFile(fileName3);
            }
        });

        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), weka.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void run() {


    }

    public void saveFile(String file) {//arff
        try {
            FileOutputStream files = openFileOutput(file, Context.MODE_PRIVATE|MODE_APPEND);
            String write = ("@relation file\n\n" +


                    "@attribute Cacceleration real\n" +
                    "@attribute state {stop,walk,run}\n\n" +

                    "@data\n");
            files.write(write.getBytes());
            String writestop=readFile(fileName1);
            files.write(writestop.getBytes());
            String writewalk=readFile(fileName2);
            files.write(writewalk.getBytes());
            String writerun=readFile(fileName3);
            files.write(writerun.getBytes());
            files.flush();
            files.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveFile1(String file1) { //立ち状態の合成加速度を保存
        try {
            FileOutputStream files =  openFileOutput(file1,Context.MODE_PRIVATE | MODE_APPEND);
//            FileOutputStream files =  openFileOutput(file1,Context.MODE_APPEND);
            String write = (Math.sqrt(xyz) +",stop"+"\n");
//            Log.d("myTag", Math.sqrt(xyz) +",stop"+"\n");
            files.write(write.getBytes());
//            System.out.println("HIHIHIHIHIHIHI");
//            Log.d("myTag", "This is my message1");
            files.close();
//            Log.d("myTag", "This is my message2");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveFile2(String file2) { //歩き状態の合成加速度を保存
        try {
            FileOutputStream files = openFileOutput(file2, Context.MODE_PRIVATE | MODE_APPEND);
            String write = (Math.sqrt(xyz) +",walk"+"\n");
            files.write(write.getBytes());
            files.flush();
            files.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveFile3(String file3) { //走り状態の合成加速度を保存
        try {
            FileOutputStream files = openFileOutput(file3, Context.MODE_PRIVATE | MODE_APPEND);
            String write = (Math.sqrt(xyz) +",run"+"\n");
            files.write(write.getBytes());
            files.flush();
            files.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String readFile(String file1) { //csv
        FileInputStream fileInputStream;
        String text = null;
        try {
            fileInputStream = openFileInput(file1);
            byte[] readBytes = new byte[fileInputStream.available()];
            fileInputStream.read(readBytes);
            text = new String(readBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors =
                sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (0 < sensors.size()) {
            sm.registerListener(this, sensors.get(0),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        h.removeCallbacks(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        gx = event.values[0];
        gy = event.values[1];
        gz = event.values[2];
        xyz = gx*gx + gy*gy +gz*gz;

        textView.setText("X軸 : " + gx + "\n"
                + "Y軸 : " + gy + "\n"
                + "Z軸 : " + gz + "\n"
                + "合成加速度：" + Math.sqrt(xyz) + "\n"
        );

        h.postDelayed(this, 500);


        if(stopcount==1) {
            saveFile1(fileName1);

        }
        if (walkcount==1)
            saveFile2(fileName2);
        if (runcount==1)
            saveFile3(fileName3);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}