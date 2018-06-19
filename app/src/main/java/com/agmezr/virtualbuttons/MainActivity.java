package com.agmezr.virtualbuttons;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * The main activity contains 3 buttons. 2 for volume control and another one for locking the screen.
 * Admin permissions are needed to lock the screen so before locking the screen it will ask the user to enable them.
 *
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Use the audio manager to adjust volume.
     * @see <a href="https://developer.android.com/reference/android/media/AudioManager">Audio Manager</a>
     *
     */
    private AudioManager audioManager;



    /**
     * An interface to manage policies, used to lock screen
     * @see <a href="https://developer.android.com/reference/android/app/admin/DevicePolicyManager">Device Policy Manager</a>
     */
    private DevicePolicyManager dpm;


    /**
     * Identifier for the AdminReceiver class
     * @see <a href="https://developer.android.com/reference/android/content/ComponentName">Component Name</a>
     */
    private ComponentName compName;

    /**
     * An int to identify the response for admin permission
     */

    private static final int ADMIN_RESULT = 1234;

    /**
     * Tag used for the log
     */
    private static final String TAG = "VirtualButtons";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        compName = new ComponentName(this, AdminReceiver.class);
        dpm = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);

        // Init buttons
        Button volumeDown = findViewById(R.id.btn_volume_down);
        Button volumeUp = findViewById(R.id.btn_volume_up);
        Button lockScreen = findViewById(R.id.btn_lock);

        // Set listeners
        volumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
            }
        });

        volumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
            }
        });


        lockScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check if admin permissions are active
                if (dpm.isAdminActive(compName)){
                    dpm.lockNow();
                }else{
                    //Create intent to ask for admin permission
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,R.string.permission_needed);
                    startActivityForResult(intent, ADMIN_RESULT);
                }
            }
        });

    }

    /**
     * This method is called after the user cancel or grants the admin permission
     *
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADMIN_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
