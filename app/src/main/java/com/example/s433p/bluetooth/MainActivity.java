package com.example.s433p.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    int REQUEST_BLUETOOTH = 1000;
    BluetoothAdapter mBTAdapter;

    LinearLayout paried;
    LinearLayout avalable;
    static Context context;

    ArrayList<String> listMac = new ArrayList<>();

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress(); // MAC address
            ParcelUuid[] uuid = device.getUuids();
            int type = -1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                type = device.getType();

            View v = LayoutInflater.from(context).inflate(R.layout.bluetooth_device, null, false);
            LinearLayout content = (LinearLayout) ((LinearLayout) v).getChildAt(1);

            if (listMac.contains(deviceHardwareAddress))
                return;
            listMac.add(deviceHardwareAddress);

            TextView tvname = new TextView(context);
            tvname.setText("[LE]" + deviceName);

            TextView tvaddr = new TextView(context);
            tvaddr.setText(deviceHardwareAddress);

            // TextView tvuuid=new TextView(context);
            // tvuuid.setText(uuid.toString());


            TextView tvtype = new TextView(context);
            tvtype.setText(type + "");

            content.addView(tvname);
            content.addView(tvaddr);
            // content.addView(tvuuid);
            content.addView(tvtype);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            avalable.addView(v);


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        avalable = findViewById(R.id.available);
        paried = findViewById(R.id.paired);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBTAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            GetAvailableBluetoothDevices();
            GetPairedBluetoothDevices();

            Thread xxx=new AcceptThread();
            xxx.start();
        }
    }

    private ArrayList GetPairedBluetoothDevices() {
        ArrayList<BluetoothDevice> arrayOfAlreadyPairedBTDevices = null;


        Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            arrayOfAlreadyPairedBTDevices = new ArrayList<BluetoothDevice>();

            // Loop through paired devices
            for (final BluetoothDevice device : pairedDevices) {

                String name = device.getName();
                String address = device.getAddress();
                int bondState = device.getBondState();
                int type = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                    type = device.getType();
                ParcelUuid[] uuid = device.getUuids();
                String str_uuid = uuid.toString();

                View v = LayoutInflater.from(context).inflate(R.layout.bluetooth_device, null, false);
                LinearLayout content = (LinearLayout) ((LinearLayout) v).getChildAt(1);


                TextView tvname = new TextView(context);
                tvname.setText(name);

                TextView tvaddr = new TextView(context);
                tvaddr.setText(address);

                // TextView tvuuid=new TextView(context);
                // tvuuid.setText(str_uuid);


                TextView tvtype = new TextView(context);
                tvtype.setText(type + "");

                content.addView(tvname);
                content.addView(tvaddr);
                //   content.addView(tvuuid);
                content.addView(tvtype);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ConnectThread(device).start();
                    }
                });

                paried.addView(v);
            }
        }

        return arrayOfAlreadyPairedBTDevices;
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                final String deviceHardwareAddress = device.getAddress(); // MAC address

                if (listMac.contains(deviceHardwareAddress))
                    return;
                listMac.add(deviceHardwareAddress);

                ParcelUuid[] uuid = device.getUuids();
                int type = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                    type = device.getType();

                View v = LayoutInflater.from(context).inflate(R.layout.bluetooth_device, null, false);
                LinearLayout content = (LinearLayout) ((LinearLayout) v).getChildAt(1);


                TextView tvname = new TextView(context);
                tvname.setText(deviceName);

                TextView tvaddr = new TextView(context);
                tvaddr.setText(deviceHardwareAddress);

                //  TextView tvuuid=new TextView(context);
                //   if(null!=uuid)
                //   tvuuid.setText(uuid.toString());


                TextView tvtype = new TextView(context);
                tvtype.setText(type + "");

                content.addView(tvname);
                content.addView(tvaddr);
                //   content.addView(tvuuid);
                content.addView(tvtype);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                        alertDialogBuilder.setNegativeButton("Pair", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pairDevice(deviceHardwareAddress);
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = alertDialogBuilder.create();
                        dialog.show();

                    }
                });

                avalable.addView(v);

            }
        }
    };


// This callback is added to the start scan method as a parameter in this way
// bleAdapter.startLeScan(mLeScanCallback);

    private void GetAvailableBluetoothDevices() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            mBTAdapter.startLeScan(mLeScanCallback);
        } else {
            // BLE is not supported, Don’t use BLE capabilities here
        }
        mBTAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BLUETOOTH) {

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    //    mBTAdapter.cancelDiscovery();
       // unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        unregisterReceiver(receiver);
    }


    public void pairDevice(String address) {

        BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final String TAG="xxxxxx";

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBTAdapter.cancelDiscovery();
            String connectStatus="";
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                connectStatus="Unable to connect; close the socket and return";
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
          ///  manageMyConnectedSocket(mmSocket);
            String connectok="";
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }



    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            // Create a new listening server socket
            try {
                tmp = mBTAdapter.listenUsingRfcommWithServiceRecord("NAME", MY_UUID);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        public void run() {
            setName("AcceptThread");
            BluetoothSocket socket = null;
            // Listen to the server socket if we're not connected
            while (true) {
                try {

                    socket = mmServerSocket.accept();
                    String test="sss";
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted

            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
            }
        }
    }

}
