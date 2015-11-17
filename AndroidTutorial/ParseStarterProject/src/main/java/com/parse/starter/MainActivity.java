/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ListActivity {

  //instantiate adapter for ListView additions
  private ArrayAdapter<String> adapter;

  //instantiate 2 array lists to store post objects and strings (post text)
  ArrayList<String> postList = new ArrayList<>();
  ArrayList<ParseObject> objectList = new ArrayList<>();

  //initialize skip variable to keep track of how many posts have been loaded to screen
  private int skip = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ParseAnalytics.trackAppOpenedInBackground(getIntent());

    //initialize adapter with our list of strings (post text)
    adapter = new ArrayAdapter<>(this, R.layout.activity_listview, postList);
    setListAdapter(adapter);

    //load posts to screen when user navigates to our main activity
    updatePosts();

    //initialize the list view, button and edit text components
    ListView listView = (ListView) findViewById(android.R.id.list);
    final Button button = (Button) findViewById(R.id.postBtn);
    final EditText text = (EditText) findViewById(R.id.newMsg);
    //set the on click listener for our post button
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        //when post button is clicked this code is executed
        String newMessage = text.getText().toString();
        //if the text box is not empty we create post with the text that was entered
        //then reset the text box so it is empty and ready for a new post
        if(!newMessage.isEmpty()) {
          createPost(newMessage);
          text.setText("");
        }
        //if the text box was empty we will update posts
        else {
          updatePosts();
        }

      }
    });

    //set the on click listener for each listview item. this is used to delete posts if you
    //have authorization to do so
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        //here we check if the user that created the object is also the user that is trying
        //to delete the object
        if(objectList.get(position).get("createdBy") == ParseUser.getCurrentUser()) {
          deletePost(position);
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

  public void createPost(String message) {
    //setting the ACL for the posts so that all users can read posts but not edit them. They can
    //only edit (delete) posts if they created them
    ParseACL ACL = new ParseACL();
    ACL.setPublicReadAccess(true);
    ACL.setPublicWriteAccess(false);
    ACL.setWriteAccess(ParseUser.getCurrentUser(), true);

    //creating the post object with the message from the text box and setting a createdBy
    //attribute to keep track of creator.
    ParseObject post = new ParseObject("Post");
    post.put("post", message);
    post.put("createdBy", ParseUser.getCurrentUser());
    post.setACL(ACL);
    post.saveInBackground();

    displayPost(post);
  }

  public void displayPost(ParseObject post) {
    objectList.add(0, post);
    postList.add(0, post.getString("post"));
    adapter.notifyDataSetChanged();
    skip++;
  }

  public void deletePost(int position) {
    AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
    adb.setTitle("Delete?");
    adb.setMessage("Are you sure you want to delete the post: " + postList.get(position));
    final int positionToRemove = position;
    adb.setNegativeButton("Cancel", null);
    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        objectList.get(positionToRemove).deleteInBackground();
        objectList.remove(positionToRemove);
        postList.remove(positionToRemove);
        adapter.notifyDataSetChanged();
        skip--;
      }
    });
    adb.show();
  }

  public void updatePosts() {
    ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
    query.whereExists("post");
    query.orderByAscending("createdAt");
    query.setSkip(skip);
    query.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> postList, ParseException e) {
        if (e == null) {
          for (ParseObject ob : postList) {
            displayPost(ob);
          }
        } else {
          //error
        }
      }
    });
  }



}
