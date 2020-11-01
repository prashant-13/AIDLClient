package com.example.aidlclient;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aidlserver.IAdd;
import com.example.aidlserver.IDBCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnOrientation;
    private TextView tvOuput;
    protected IAdd addService;
    private String Tag = "Client Application";
    private String serverAppUri = "com.example.aidlserver";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOrientation = (Button) findViewById(R.id.bt_orientation);
        btnOrientation.setOnClickListener(this);

        tvOuput = (TextView) findViewById(R.id.tv_output);

        initConnection();
    }

    private void initConnection() {
        if (addService == null) {
            Intent intent = new Intent(IAdd.class.getName());
            /*this is service name which has been declared in the server's manifest file in service's intent-filter*/
            intent.setAction("service.calc");
            /*From 5.0 annonymous intent calls are suspended so replacing with server app's package name*/
            intent.setPackage("com.example.aidlserver");
            // binding to remote service
            bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(Tag, "Service Connected");
            addService = IAdd.Stub.asInterface((IBinder) iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(Tag, "Service Disconnected");
            addService = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    @Override
    public void onClick(View view) {
        if (appInstalledOrNot(serverAppUri)) {
            switch (view.getId()) {
                case R.id.bt_orientation:
                    try {
                        addService.getOrientationSenderData(serviceCallback);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    btnOrientation.setVisibility(View.GONE);
                    break;
            }
        } else {
            Toast.makeText(MainActivity.this, "Server App not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (addService == null) {
            initConnection();
        }
    }


    IDBCallback.Stub serviceCallback = new IDBCallback.Stub() {

        @Override
        public void handleOrientationDetail(final String orientationData) throws RemoteException {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvOuput.setText(orientationData);
                }
            });
        }
    };

}