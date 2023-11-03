package com.example.pccontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SystemControls extends AppCompatActivity {
    private Button buttonShutDownPC, buttonShutDownServer, buttonStandBy;
    private Intent intent;
    String serverIP, serverPortStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_layout);
        buttonShutDownPC = findViewById(R.id.buttonShutdownPC);
        buttonStandBy = findViewById(R.id.buttonStandby);
        buttonShutDownServer = findViewById(R.id.buttonShutdownServer);
        Intent intent = getIntent();
        serverIP = intent.getStringExtra("serverIP");
        serverPortStr = intent.getStringExtra("serverPort");
        buttonShutDownServer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isFieldsNotEmpty()) {
                    sendCommandToServer("/server_shutdown");}
            }
        });
        buttonShutDownPC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isFieldsNotEmpty()) {
                    sendCommandToServer("/shutdown; ");
                }
            }
        });
        buttonStandBy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isFieldsNotEmpty()) {
                    sendCommandToServer("/standby; ");
                }
            }
        });
    }
    private void sendCommandToServer(final String command) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Socket socket = new Socket(serverIP, Integer.parseInt(serverPortStr));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    out.write(command);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SystemControls.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return null;
            }
        }.execute();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (!serverIP.isEmpty() || !serverPortStr.isEmpty()) {
            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ip", serverIP);
            editor.putString("port", serverPortStr);
            editor.apply();
        }
    }
    private boolean isFieldsNotEmpty() {
        boolean flag = false;
        if (!serverIP.isEmpty() || !serverPortStr.isEmpty()) {
            flag = true;
        } else {
            runOnUi("Please enter server IP and port");
        }
        return flag;
    }
    private void runOnUi(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SystemControls.this, text, Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}