package com.example.nozomi.myapp01;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import java.util.List;
import android.view.View;
import android.widget.Button;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.trees.J48;


public class weka extends Activity implements Runnable, SensorEventListener {
    SensorManager sm;
    private TextView textView;
    float gx, gy, gz;
    int walkcount=0;
    double xyz,Rxyz;
    private Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        textView = (TextView) findViewById(R.id.text_view);



        Button returnButton = (Button) findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gx = event.values[0];
            gy = event.values[1];
            gz = event.values[2];
            xyz = gx*gx + gy*gy + gz*gz;
            Rxyz=Math.sqrt(xyz);

            try {
                DataSource source = new DataSource("/data/data/com.example.nozomi.myapp01/files/speed.arff");
                Instances instances = source.getDataSet();
                J48 j48 = new J48();
                instances.setClassIndex(1);
                j48.buildClassifier(instances);


                Evaluation eval = new Evaluation(instances);
                eval.evaluateModel(j48, instances);
                System.out.println(eval.toSummaryString());



                FastVector out = new FastVector(3);
                out.addElement("stop");
                out.addElement("walk");
                out.addElement("run");
                Attribute state = new Attribute("state", out, 1);
                Attribute Cacceleration = new Attribute("Cacceleration", 0);


                Instance instance = new DenseInstance(1);
                instance.setValue(Cacceleration,Rxyz);
                instance.setDataset(instances);


                double result = j48.classifyInstance(instance);

                if(result==0.0)
                textView.setText("停止状態");
                if(result==1.0) {
                    textView.setText("歩き状態");
                    ++walkcount;
                }
                if (walkcount==80)//warning
                   setScreenSub();
                if(result==2.0)
                    textView.setText("走り状態");
                System.out.println(result);//denn
            } catch (Exception e) {
                e.printStackTrace();
            }
         }
        }

    private void setScreenSub(){
        //setContentView(R.layout.warning_xml);
        textView.setText("Warning!");
        returnButton = (Button) findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walkcount=0;
                finish();
            }
        });
    }

    @Override
    public void run() {
    }
    @Override
    protected void onResume() {
        super.onResume();
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors =
                sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (0 < sensors.size()) {
            sm.registerListener(this, sensors.get(0),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
