package com.android.mdw.demo;

import java.util.List;

import com.android.mdw.control.KalmanFilter;
import com.android.mdw.model.Data;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity implements SensorEventListener {
    private long last_update = 0, last_movement = 0;
    private float prevX = 0, prevY = 0, prevZ = 0;
    private float curX = 0, curY = 0, curZ = 0;
    
    private long last_update_g = 0, last_movement_g = 0;
    private float prevX_g = 0, prevY_g = 0, prevZ_g = 0;
    private float curX_g = 0, curY_g = 0, curZ_g = 0;
    
    private Data data = new Data();
    private KalmanFilter kf = new KalmanFilter();
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);        
        if (sensors.size() > 0) {
        	sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
        }
    }
    
    @Override
    protected void onStop() {
    	SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);    	
        sm.unregisterListener(this);
        super.onStop();
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
        	switch (event.sensor.getType()){
        	case Sensor.TYPE_ACCELEROMETER:
        		long current_time = event.timestamp;
                
                curX = event.values[0];
                curY = event.values[1];
                curZ = event.values[2];
                
                if (prevX == 0 && prevY == 0 && prevZ == 0) {
                    last_update = current_time;
                    last_movement = current_time;
                    prevX = curX;
                    prevY = curY;
                    prevZ = curZ;
                }

                long time_difference = current_time - last_update;
                if (time_difference > 0) {
                    float movement = Math.abs((curX + curY + curZ) - (prevX - prevY - prevZ)) / time_difference;
                    int limit = 1500;
                    float min_movement = 1E-6f;
                    if (movement > min_movement) {
//                        if (current_time - last_movement >= limit) {                    	
//                            Toast.makeText(getApplicationContext(), "Hay movimiento de " + movement, Toast.LENGTH_SHORT).show();
//                        }
                        last_movement = current_time;
                    }
                    prevX = curX;
                    prevY = curY;
                    prevZ = curZ;
                    last_update = current_time;
                }
                
                
                ((TextView) findViewById(R.id.txtAccX)).setText("Aceler—metro X: " + curX);
                ((TextView) findViewById(R.id.txtAccY)).setText("Aceler—metro Y: " + curY);
                ((TextView) findViewById(R.id.txtAccZ)).setText("Aceler—metro Z: " + curZ);
                break;
        	case Sensor.TYPE_GYROSCOPE:
        		long current_time_g = event.timestamp;
                
                curX_g = event.values[0];
                curY_g = event.values[1];
                curZ_g = event.values[2];
                
                if (prevX_g == 0 && prevY_g == 0 && prevZ_g == 0) {
                    last_update_g = current_time_g;
                    last_movement_g = current_time_g;
                    prevX_g = curX_g;
                    prevY_g = curY_g;
                    prevZ_g = curZ_g;
                }

                long time_difference_g = current_time_g - last_update_g;
                if (time_difference_g > 0) {
                    float movement = Math.abs((curX_g + curY_g + curZ_g) - (prevX_g - prevY_g - prevZ_g)) / time_difference_g;
                    int limit = 1500;
                    float min_movement = 1E-6f;
                    if (movement > min_movement) {
//                        if (current_time_g - last_movement_g >= limit) {                    	
                            //Toast.makeText(getApplicationContext(), "Hay movimiento de " + movement, Toast.LENGTH_SHORT).show();
//                        }
                        last_movement_g = current_time_g;
                    }
                    prevX_g = curX_g;
                    prevY_g = curY_g;
                    prevZ_g = curZ_g;
                    last_update_g = current_time_g;
                }
                
                
                ((TextView) findViewById(R.id.txtGirX)).setText("Giroscopio X: " + curX_g);
                ((TextView) findViewById(R.id.txtGirY)).setText("Giroscopio Y: " + curY_g);
                ((TextView) findViewById(R.id.txtGirZ)).setText("Giroscopio Z: " + curZ_g);
                break;
    		default:
    			break;        		
        	}
        	
        	data.setAccelerometerX(curX);
        	data.setAccelerometerY(curY);
        	data.setAccelerometerZ(curZ);
        	
        	data.setGiroX(curX_g);
        	data.setGiroY(curY_g);
        	data.setGiroZ(curZ_g);
        	
        	double[] filtro = new double[3];
        	
        	filtro = kf.filtrar(data);
        	
        	((TextView) findViewById(R.id.txtGirX)).setText("Giroscopio filtro X: " + filtro[0]);
            ((TextView) findViewById(R.id.txtGirY)).setText("Giroscopio filtro Y: " + filtro[1]);
            ((TextView) findViewById(R.id.txtGirZ)).setText("Giroscopio filtro Z: " + filtro[2]);
        	
        }
		
	}    
    
}