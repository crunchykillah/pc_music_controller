package com.example.pccontroller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private Button buttonMute, buttonUnMute, buttonConnect,buttonGoToSystemControls;
    private TextView yourVolume;
    private Socket socket;
    private SeekBar seekBar;
    private EditText editTextIP;
    private EditText editTextPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("new","shit");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextIP = findViewById(R.id.editTextIP);
        editTextPort = findViewById(R.id.editTextPort);
        buttonMute = findViewById(R.id.buttonMute);
        buttonUnMute = findViewById(R.id.buttonUnmute);
        buttonConnect = findViewById(R.id.connectButton);
        seekBar = findViewById(R.id.volumeSeekBar);
        yourVolume = findViewById(R.id.yourVolumeText);
        buttonGoToSystemControls = findViewById(R.id.goToSysButton);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int volume = seekBar.getProgress();
                String volumeString = String.valueOf(volume);
                if (isFieldsNotEmpty()) {
                    sendCommandToServer("/volume;" + volumeString);
                    yourVolume.setText("Your volume now: " + volumeString);
                }
            }
        });
        buttonMute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isFieldsNotEmpty()) { sendCommandToServer("/mute; ");} }
        });
        buttonUnMute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isFieldsNotEmpty()) { sendCommandToServer("/unmute; ");} }
        });
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String serverIP = editTextIP.getText().toString().trim();
                final String serverPortStr = editTextPort.getText().toString().trim();
                if (serverIP.isEmpty() || serverPortStr.isEmpty()) {
                    runOnUi("Please enter server IP and port");
                    return;
                }
                final int serverPort = Integer.parseInt(editTextPort.getText().toString());
                if ((socket == null || !socket.isConnected())) {
                    connectToServer(serverIP, serverPort);
                    buttonConnect.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.aqua)));
                } else {
                    runOnUi("Already connected to the server");
                }
            }
        });
        buttonGoToSystemControls.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SystemControls.class);
                intent.putExtra("serverIP", editTextIP.getText().toString());
                intent.putExtra("serverPort", editTextPort.getText().toString());
                startActivity(intent);
                finish();
            }
        });


    }
    private void connectToServer(final String serverIP, final int serverPort) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    socket = new Socket(serverIP, serverPort);
                    runOnUi("Connection successful");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUi("Error connecting to server");
                }
                return null;
            }
        }.execute();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendCommandToServer(final String command) {
        final String serverIP = editTextIP.getText().toString();
        final int serverPort = Integer.parseInt(editTextPort.getText().toString());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    socket = new Socket(serverIP, serverPort);
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    out.write(command);
                    out.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUi("Error connecting to server" + serverIP + serverPort);
                }
                return null;
            }
        }.execute();
    }
    private void runOnUi(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });

    }
    private boolean isFieldsNotEmpty() {
        boolean flag = false;
        final String serverIP = editTextIP.getText().toString();
        final String serverPortStr = editTextPort.getText().toString().trim();
        if (!serverIP.isEmpty() || !serverPortStr.isEmpty()) {
            flag = true;
        } else {
            runOnUi("Please enter server IP and port");
        }
        return flag;
    }
}
