package ru.dront78.pulsedroid;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

public class PulseSoundThread implements Runnable {
    private final String mServer;
    private final int mPort;
    private boolean mTerminate = false;
    private boolean mRunning = false;

    public PulseSoundThread(String server, int port) {
        mServer = server;
        mPort = port;
    }

    public void terminate() {
        mTerminate = true;
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void run() {
        Socket sock = null;
        BufferedInputStream audioData = null;
        mRunning = false;
        try {
            sock = new Socket(mServer, mPort);
        } catch (UnknownHostException e) {
            terminate();
            e.printStackTrace();
            return;
        } catch (IOException e) {
            terminate();
            e.printStackTrace();
            return;
        } catch (SecurityException e) {
            // connect to the given address and port.
            terminate();
            e.printStackTrace();
            return;
        }

        if (!mTerminate) {
            try {
                audioData = new BufferedInputStream(sock.getInputStream());
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                terminate();
                e.printStackTrace();
                return;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                terminate();
                e.printStackTrace();
                return;
            }
        }

        // Create AudioPlayer
        /*
         * final int sampleRate = AudioTrack
         * .getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
         */
        // TODO native audio?
        final int sampleRate = 48000;

        int musicLength = AudioTrack.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, musicLength,
                AudioTrack.MODE_STREAM);
        audioTrack.play();

        mRunning = true;

        // TODO buffer size computation
        byte[] audioBuffer = new byte[musicLength * 8];

        while (!mTerminate) {
            try {
                int sizeRead = audioData.read(audioBuffer, 0, musicLength * 8);
                int sizeWrite = audioTrack.write(audioBuffer, 0, sizeRead);
                if (sizeWrite == AudioTrack.ERROR_INVALID_OPERATION) {
                    sizeWrite = 0;
                }
                if (sizeWrite == AudioTrack.ERROR_BAD_VALUE) {
                    sizeWrite = 0;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                mRunning = false;
                return;
            }
        }

        audioTrack.stop();
        sock = null;
        audioData = null;
        mRunning = false;
    }
}
