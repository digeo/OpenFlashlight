package gr.digkas.openflashlight;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static Camera camera = null;
    private Switch switchTurnOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchTurnOnOff = (Switch) findViewById(R.id.switchTurnOnOff);
        Context context = this;
        PackageManager pm = context.getPackageManager();
        // if device support camera?
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            switchTurnOnOff.setClickable(false);
            switchTurnOnOff.setEnabled(false);
            Toast.makeText(getApplicationContext(), "The device's camera does not support flash.", Toast.LENGTH_LONG).show();
            return;
        } else {
            if (!isFlashTurnedOn()) {
                switchTurnOnOff.setChecked(true);
                turnOn();
            }
            else {
                setCheckedState();
            }
        }

        switchTurnOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    turnOn();
                } else {
                    turnOff();
                }
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCheckedState();
    }

    private void setCheckedState() {
        if (camera != null) {
            Camera.Parameters p = camera.getParameters();
            String flashMode = p.getFlashMode();
            //switchTurnOnOff.setEnabled(flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH));
            switchTurnOnOff.setChecked(flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (camera != null)
            turnOff();
    }

    private void turnOff() {
        Camera.Parameters p = camera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(p);
        camera.stopPreview();
        camera.release();
        camera = null;

        Toast.makeText(getApplicationContext(), "OFF", Toast.LENGTH_LONG).show();
    }

    private void turnOn() {
        camera = Camera.open();
        Camera.Parameters p = camera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(p);
        camera.startPreview();

        Toast.makeText(getApplicationContext(), "ON", Toast.LENGTH_LONG).show();
    }

    private boolean isFlashTurnedOn() {
        if (camera != null) {
            Camera.Parameters p = camera.getParameters();
            return p.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH);
        } else {
            return false;
        }
    }
}
