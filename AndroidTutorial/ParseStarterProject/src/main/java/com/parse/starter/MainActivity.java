/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ListAdapter;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ListActivity {
//sign up
  //sign in
  //create/delete post
    //if you have access
  //prepended
  //title and body
  //lost more on seperated pages skip parameter
  //order by creating
  //based on user
  //ACL set public read/write access setread/write
  //yik yak

  private int skip = 0;
  String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry","WebOS","Ubuntu","Windows7","Max OS X"};
  ArrayList<String> listItems = new ArrayList<String>();
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ParseAnalytics.trackAppOpenedInBackground(getIntent());

    //Intent i = getIntent();
    //final String username = i.getStringExtra("username");
    //final ArrayAdapter<String> adapter;
    //adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, listItems);
    //setListAdapter(adapter);
    updatePosts();

    final Button button = (Button) findViewById(R.id.postBtn);
    final EditText text = (EditText) findViewById(R.id.newMsg);
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        ArrayList<String> posts = new ArrayList<>();
        String newMessage = text.getText().toString();
        if(!newMessage.isEmpty()) {
          createPost(newMessage);
          text.setText("");
        }
        else {
          updatePosts();
        }

      }
    });

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
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

  public void createPost(String post) {
    ParseObject message = new ParseObject("Message");
    message.put("message", post);
    message.saveInBackground();
    displayPost(post);
  }

  public void displayPost(String post) {
    skip++;
    final LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
    TextView textView = new TextView(MainActivity.this);
    textView.setText(post);
    layout.addView(textView, 0);
  }

  public void updatePosts() {
    ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
    query.whereExists("message");
    query.orderByAscending("createdAt");
    //query.setSkip(skip);
    query.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> postList, ParseException e) {
        if (e == null) {
          for (ParseObject ob : postList) {
            displayPost(ob.getString("message"));
          }
        } else {
          //error
        }
      }
    });
  }



}
