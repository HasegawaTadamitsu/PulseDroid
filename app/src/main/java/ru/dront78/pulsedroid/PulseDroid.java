package ru.dront78.pulsedroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PulseDroid extends Activity {

    private boolean mPlayState = false;
    private PulseSoundThread mPlayThread = null;

    private void displayMessage(int msgResId) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SharedPreferMgr spMgr = new SharedPreferMgr(this, getSharedPreferences("PulseDroid", MODE_PRIVATE));
        spMgr.setValue();

        // here is onButtonClick handler
        final Button playButton = findViewById(R.id.ButtonPlay);
        playButton.setOnClickListener(v -> {
            if (!mPlayState) {
                if (null != mPlayThread) {
                    mPlayThread.terminate();
                    mPlayThread = null;
                }
                final EditText server = findViewById(R.id.EditTextServer);
                final String serverText = server.getText().toString();
                if (serverText.isEmpty()) {
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
                mPlayThread = new PulseSoundThread(serverText, portNumber);
                mPlayThread.setCallback(() -> {
                    mPlayState = false;
                    playButton.setText(getResources().getString(R.string.button_text_play));
                    // displayMessage(R.string.error_message_unknown_error);
                });
                new Thread(mPlayThread).start();
                mPlayState = true;
                playButton.setText(getResources().getString(R.string.button_text_stop));
                displayMessage(R.string.status_message_playing);

            } else {
                if (null != mPlayThread) {
                    mPlayThread.terminate();
                    mPlayThread = null;
                }
                mPlayState = false;
                playButton.setText(getResources().getString(R.string.button_text_play));

            }
        });

        findViewById(R.id.ButtonExit).setOnClickListener(
                v -> {
                    if (null != mPlayThread) {
                        mPlayThread.terminate();
                        mPlayThread = null;
                    }
                    moveTaskToBack(true);
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferMgr spMgr = new SharedPreferMgr(this, getSharedPreferences("PulseDroid", MODE_PRIVATE));
        spMgr.saveValue();
    }
}