package ru.dront78.pulsedroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PulseDroid extends Activity {

    boolean playState = false;
    PulseSoundThread playThread = null;

    private void displayMessage(int msgResId) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // here is onButtonClick handler
        final Button playButton = findViewById(R.id.ButtonPlay);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!playState) {
                    if (null != playThread) {
                        playThread.terminate();
                        playThread = null;
                    }
                    final EditText server = findViewById(R.id.EditTextServer);
                    final String serverText = server.getText().toString();
                    if (serverText.equals("")) {
                        displayMessage(R.string.error_message_need_server_input);
                        return;
                    }
                    final EditText port = findViewById(R.id.EditTextPort);
                    int portNumber;
                    try {
                        portNumber = Integer.parseInt(port.getText().toString());
                    } catch (NumberFormatException e) {
                        displayMessage(R.string.error_message_need_port_input);
                        return;
                    }
                    playThread = new PulseSoundThread(serverText, portNumber);
                    new Thread(playThread).start();
                    playState = true;
                    playButton.setText(getResources().getString(R.string.button_text_stop));
                    displayMessage(R.string.status_message_playing);

                } else {
                    if (null != playThread) {
                        playThread.terminate();
                        playThread = null;
                    }
                    playState = false;
                    playButton.setText(getResources().getString(R.string.button_text_play));

                }
            }
        });

        findViewById(R.id.ButtonExit).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (null != playThread) {
                            playThread.terminate();
                            playThread = null;
                        }
                        moveTaskToBack(true);
                    }
                });
    }


}