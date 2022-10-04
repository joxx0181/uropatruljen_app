package com.example.uropatruljen_app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.net.InetSocketAddress;
import java.net.Socket;

public class OptionPage extends AppCompatActivity {

    // Initializing
    Socket socket;
    int serverPORT = 1883;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_page);

        // Port scan for specific device  -  in this case a device named RaspberryPi on port 1883
        InetSocketAddress getIP = new InetSocketAddress("sas9URO", serverPORT);
        String serverIP = getIP.getHostString();






        // camera function not implemented


        // light function



        // music function
    }
}