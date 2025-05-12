package ru.dront78.pulsedroid;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.EditText;

public class SharedPreferMgr {
    private final Activity mActivity;
    private final SharedPreferences mSharedPreferences;

    public SharedPreferMgr(Activity act, SharedPreferences sp) {
        mActivity = act;
        mSharedPreferences = sp;
    }

    public void saveValue() {
        final EditText server = mActivity.findViewById(R.id.EditTextServer);
        final String serverText = server.getText().toString();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Integer.toString(R.id.EditTextServer), serverText);

        final EditText port = mActivity.findViewById(R.id.EditTextPort);
        final String portText = port.getText().toString();
        editor.putString(Integer.toString(R.id.EditTextPort), portText);

        editor.commit();
    }

    public void setValue() {
        final EditText server = mActivity.findViewById(R.id.EditTextServer);
        server.setText(mSharedPreferences.getString(Integer.toString(R.id.EditTextServer), ""));
        final EditText port = mActivity.findViewById(R.id.EditTextPort);
        port.setText(mSharedPreferences.getString(Integer.toString(R.id.EditTextPort), ""));
    }
}
