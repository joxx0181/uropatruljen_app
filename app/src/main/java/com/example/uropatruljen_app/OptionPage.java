package com.example.uropatruljen_app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class OptionPage extends AppCompatActivity {

    // Initializing
    Thread Thread = null;
    Socket socket;
    int serverPORT = 1883;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_page);

        // Port scan for specific device  -  in this case a device named RaspberryPi on port 1883
        InetSocketAddress getIP = new InetSocketAddress("sas9URO", serverPORT);
        String serverIP = getIP.getHostString();

        // Creating and starting thread
        Thread = new Thread(new OptionPage.Socket2Thread(serverIP));
        Thread.start();


        // light function



        // music function
    }

    // Create Socket2Thread class for obtain connection
    class Socket2Thread implements Runnable {

        private final String ip;
        Socket2Thread(String ip) {

            this.ip = ip;
        }

        public void run() {

            try {

                // Creating a stream socket connecting to a server (a named host) with a specified port number
                socket = new Socket(ip, serverPORT);

                // Using expression lambda to display Toast message in a thread
                OptionPage  .this.runOnUiThread(() -> Toast.makeText(getBaseContext(), "Der er forbindelse til uro", Toast.LENGTH_SHORT).show());

            } catch (IOException e) {
                e.printStackTrace();

                // Using expression lambda to display Toast message in a thread
                OptionPage.this.runOnUiThread(() -> Toast.makeText(getBaseContext(), "Ingen forbindelse til uro", Toast.LENGTH_SHORT).show());
            }
        }
    }
}