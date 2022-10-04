package com.example.uropatruljen_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import java.util.List;

@SuppressWarnings("ALL")
public class Hotspot extends ListActivity {

    // Initializing
    private final String hoturoSSID = "prog";
    private final String hoturoPASS = "Alvorlig5And";
    WifiManager wifiOBJ;
    WifiScanReceiver wifiReceiver;
    ListView list;
    String[] allWifi;
    TextView availableWifi;
    EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot);

        // Getting view that is identified by the android:id
        availableWifi = findViewById(R.id.availablewifi);

        list = getListView();

        String[] PERMS_INITIAL = {Manifest.permission.ACCESS_FINE_LOCATION,};
        ActivityCompat.requestPermissions(this, PERMS_INITIAL, 127);

        // Using WifiManager to get application context and wifi services
        wifiOBJ = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        // Using WifiInfo to get state of a wifi connection
        WifiInfo wifiInfo = wifiOBJ.getConnectionInfo();
        String currentSSID = wifiInfo.getSSID();

        // Using .startScan() for wifi scan to be started
        wifiReceiver = new WifiScanReceiver();
        wifiOBJ.startScan();

        // Reset wifi with Disable and Enable wifi
        wifiOBJ.setWifiEnabled(false);
        wifiOBJ.setWifiEnabled(true);

        if(!currentSSID.contains("prog")) {


                finallyConnect(hoturoPASS, hoturoSSID);

                // listening to a list with all avaiable wifi using an on click function
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        // SSID of selected wifi from list of all avaiable wifi
                        String ssid = ((TextView) view).getText().toString();

                        GetPersonalWifi(ssid);
                    }
                });

        }
        else {

            // Intent function for passing parameter to a new activity
            Intent receivedKeys = getIntent();

            // Using .getStringExtra() for receiving parameter from another activity
            String personalSSID = receivedKeys.getStringExtra("ssid_key");
            String personalPASS = receivedKeys.getStringExtra("pass_key");

            do {

                finallyConnect(personalPASS, personalSSID);

            } while (!currentSSID.contains(personalSSID));

            if (currentSSID.contains(personalSSID)) {

                Intent goToOption = new Intent(Hotspot.this, OptionPage.class);
                startActivity(goToOption);
            }

        }
    }

    protected void onPause() {
        unregisterReceiver(wifiReceiver);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    // Create WifiScanReceiver class to use BroadcastReceiver for receiving wifi state connection information
    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")

        public void onReceive(Context c, Intent intent) {

            List<ScanResult> wifiScanList = wifiOBJ.getScanResults();
            allWifi = new String[wifiScanList.size()];

            for (int i = 0; i < wifiScanList.size(); i++) {
                allWifi[i] = ((wifiScanList.get(i)).toString());
            }

            String[] filtered = new String[wifiScanList.size()];
            int counter = 0;
            for (String eachWifi : allWifi) {
                String[] temp = eachWifi.split(",");

                filtered[counter] = temp[0].substring(5).trim();//+"\n" + temp[2].substring(12).trim()+"\n" +temp[3].substring(6).trim();//0->SSID, 2->Key Management 3-> Strength

                counter++;

            }
            list.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.list_item, R.id.field, filtered));
        }
    }

    private void finallyConnect(String networkPass, String networkSSID) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

        // remember id
        int netId = wifiOBJ.addNetwork(wifiConfig);
        wifiOBJ.disconnect();
        wifiOBJ.enableNetwork(netId, true);
        wifiOBJ.reconnect();

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"\"" + networkSSID + "\"\"";
        conf.preSharedKey = "\"" + networkPass + "\"";
        wifiOBJ.addNetwork(conf);
    }

    private void GetPersonalWifi(final String pSSID) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.connect);
        dialog.setTitle("Tilslut til Network");
        TextView selectedSSID = (TextView) dialog.findViewById(R.id.textSSID1);

        Button dialogBNT = (Button) dialog.findViewById(R.id.okButton);
        TextView textInfo = (TextView) dialog.findViewById(R.id.timeline);
        pass = (EditText) dialog.findViewById(R.id.password);
        selectedSSID.setText(pSSID);

        // Perform click event using lambda on dialogBNT -  if button is clicked, start intent
        dialogBNT.setOnClickListener(v -> {
            String passwordEntered = pass.getText().toString();

            // Intent function to move to another activity
            Intent goTologin = new Intent(getApplicationContext(), Login.class);

            // Using .putExtra for sending values to another activity
            goTologin.putExtra("ssid_key", pSSID);
            goTologin.putExtra("pass_key", passwordEntered);
            startActivity(goTologin);
            finish();

            dialog.dismiss();
        });
        dialog.show();
    }
}