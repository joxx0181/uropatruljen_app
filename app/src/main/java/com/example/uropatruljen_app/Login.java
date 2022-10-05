package com.example.uropatruljen_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity {

    // Initializing
    TextView info;
    EditText input;
    Button loginBTN;
    Thread Thread = null;
    Socket socket;
    String serverIP = "192.168.4.1";
    int serverPORT = 1883;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Getting view that is identified by the android:id
        info = findViewById(R.id.required);
        input = findViewById(R.id.enterModelNum);
        loginBTN = findViewById(R.id.login);

        // Intent function for passing parameters to a new activity
        Intent receivedKeys = getIntent();

        // Using .getStringExtra() for receiving parameters from another activity
        String receivedToShareSSID = receivedKeys.getStringExtra("ssid_key");
        String receivedToSharePass = receivedKeys.getStringExtra("pass_key");

        Thread = new Thread(new SocketThread());
        Thread.start();

        // Perform click event using lambda on loginBTN ( login function )
        loginBTN.setOnClickListener(view -> {

            // Getting modelNum from entered input
            String modelNum = input.getText().toString().trim();

            if (modelNum.isEmpty()) {

                // Using Toast for display a message
                Toast.makeText(getBaseContext(), "Modelnr er ikke indtastet", Toast.LENGTH_SHORT).show();
            }
            else {

                // Creating and starting thread
                try {
                    Thread = new Thread(new SendToSocketThread(receivedToShareSSID, receivedToSharePass,(toHexString(getSHA(modelNum)))));
                }
                catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                Thread.start();
            }
        });
    }

    // Create SocketThread class for obtain connection
    class SocketThread implements Runnable {

        public void run() {

            try {

                // Creating a stream socket connecting to a server (a named host) with a specified port number
                socket = new Socket(serverIP, serverPORT);

                // Using expression lambda to display Toast message in a thread
                Login.this.runOnUiThread(() -> Toast.makeText(getBaseContext(), "Der er forbindelse til uro", Toast.LENGTH_SHORT).show());

            } catch (IOException e) {
                e.printStackTrace();

                // Using expression lambda to display Toast message in a thread
                Login.this.runOnUiThread(() -> Toast.makeText(getBaseContext(), "Ingen forbindelse til uro", Toast.LENGTH_SHORT).show());

                Thread = new Thread(new SocketThread());
                Thread.start();
            }
        }
    }


    // Create SendToSocketThread class for sending info to server
    class SendToSocketThread implements Runnable {

        private final String sendSSID;
        private final String sendPASS;
        private final String sendModelNum;

        SendToSocketThread(String sendSSID, String sendPASS, String sendModelNum) {

            this.sendSSID = sendSSID;
            this.sendPASS = sendPASS;
            this.sendModelNum = sendModelNum;
        }

        @Override
        public void run() {

            try {

                // Using OutputStreamWriter and BufferedWriter to send streamed output to socket
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
                BufferedWriter writeNum = new BufferedWriter(out);
                writeNum.write("////");
                writeNum.write(sendSSID);
                writeNum.write("////");
                writeNum.write(sendPASS);
                writeNum.write("////");
                writeNum.write(sendModelNum);
                writeNum.flush();

                // Using expression lambda to display Toast message in a thread
                Login.this.runOnUiThread(() -> Toast.makeText(getBaseContext(), "Sender til uro..... ", Toast.LENGTH_SHORT).show());

                new Thread(new ReceiveFromSocketThread(sendSSID, sendPASS)).start();
            }
            catch (Exception e) {
                e.printStackTrace();

                // Using expression lambda to display Toast message in a thread
                Login.this.runOnUiThread(() -> Toast.makeText(getBaseContext(), "Noget gik galt....", Toast.LENGTH_SHORT).show());
            }
        }
    }

    // Create ReceiveFromSocketThread class for receiving info from server
    class ReceiveFromSocketThread implements Runnable {

        private final String getSSID;
        private final String getPASS;

        ReceiveFromSocketThread(String getSSID, String getPASS) {

            this.getSSID = getSSID;
            this.getPASS = getPASS;
        }

        @Override
        public void run() {

            try {

                // Using InputStream to receive streamed input from socket
                InputStream stream = socket.getInputStream();
                byte[] recievedToken = new byte[1024];
                int data = stream.read(recievedToken);

                socket.close();

                    // Converting bytes to string
                    String token = new String(recievedToken, StandardCharsets.UTF_8);

                    // Using expression lambda to display Toast message in a thread
                    Login.this.runOnUiThread(() -> Toast.makeText(getBaseContext(),"Besked fra uro: " + token, Toast.LENGTH_SHORT).show());

                    if (data > 0 && !token.isEmpty()) {

                        // Intent function to move to another activity
                        Intent goToHotspot = new Intent(getApplicationContext(), Hotspot.class);

                        // Using .putExtra for sending values to another activity
                        goToHotspot.putExtra("ssid_key", getSSID);
                        goToHotspot.putExtra("pass_key", getPASS);
                        startActivity(goToHotspot);
                        finish();
                    }

            } catch (Exception e) {
                e.printStackTrace();

                // Using expression lambda to display Toast message in a thread
                Login.this.runOnUiThread(() -> Toast.makeText(getBaseContext(), "Ingen besked fra uro", Toast.LENGTH_SHORT).show());

            }
        }
    }

    // Calculate cryptographic hashing value with hash function SHA-256
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest messD = MessageDigest.getInstance("SHA-256");

        // digest() method called to calculate message digest of an input and return array of byte
        return messD.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into a hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 64)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }
}