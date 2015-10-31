package com.casasmap.ribbit;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {
    public EditText mEmail;
    public EditText mUsername;
    public EditText mPassword;
    public Button mButton;
    public ParseUser mParseUser = new ParseUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mUsername = (EditText) findViewById(R.id.et_username);
        mPassword = (EditText) findViewById(R.id.et_password);
        mEmail = (EditText) findViewById(R.id.et_email);
        mButton = (Button) findViewById(R.id.b_sign_up);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = (mUsername.getText().toString()).trim();
                String password = (mPassword.getText().toString()).trim();
                String email = (mEmail.getText().toString()).trim();

                if(username.isEmpty() || password.isEmpty() || email.isEmpty()){
                 // error in creating account
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setTitle(R.string.title_error);
                    builder.setMessage(R.string.message_error);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    //creating user on Parse.com
                    mParseUser.setUsername(username);
                    mParseUser.setPassword(password);
                    mParseUser.setEmail(email);
                    mParseUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //everything fine
                                //creating the intent to go to inbox
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                //adding flags so they cannot return by clicking back button
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                builder.setTitle(R.string.title_error);
                                builder.setMessage(e.getMessage());
                                builder.setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        }
                    });

                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
