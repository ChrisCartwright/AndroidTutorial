package com.parse.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        Button signUpButton = (Button) findViewById(R.id.upBtn);
        Button signInButton = (Button) findViewById(R.id.inBtn);

        final EditText userText = (EditText) findViewById(R.id.userText);
        final EditText passText = (EditText) findViewById(R.id.passText);

        final TextView errorText = (TextView) findViewById(R.id.errorText);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = userText.getText().toString();
                String password = passText.getText().toString();
                if(!username.isEmpty() && !password.isEmpty()) {
                    ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                Intent mainScreen = new Intent(getApplicationContext(), MainActivity.class);
                                mainScreen.putExtra("username", username);
                                startActivity(mainScreen);
                            } else {
                                errorText.setText("Username already in use: " + e);
                            }
                        }
                    });
                }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userText.getText().toString();
                String password = passText.getText().toString();
                if(!username.isEmpty() && !password.isEmpty()) {
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if(user != null) {
                                Intent mainScreen = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(mainScreen);
                            } else {
                                errorText.setText("Could not Login: " + e);
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
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
