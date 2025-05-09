package com.mobshep.mobileshepherd;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * This file is part of the Security Shepherd Project.
 *
 * <p>The Security Shepherd project is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.<br>
 *
 * <p>The Security Shepherd project is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.<br>
 *
 * <p>You should have received a copy of the GNU General Public License along with the Security
 * Shepherd project. If not, see <http://www.gnu.org/licenses/>.
 *
 * @author Sean Duggan
 */
public class BrokenCrypto extends MainActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  Button messageOne, messageTwo, messageThree, messageFour, messageFive;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.broken_layout);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    referenceXML();
    startTimerOne();
    startTimerTwo();
    startTimerThree();
    startTimerFour();
    startTimerFive();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  private void referenceXML() {
    // TODO Auto-generated method stub
    messageOne = (Button) findViewById(R.id.Message1);
    messageTwo = (Button) findViewById(R.id.Message2);
    messageThree = (Button) findViewById(R.id.Message3);
    messageFour = (Button) findViewById(R.id.Message4);
    messageFive = (Button) findViewById(R.id.Message5);
    messageOne.setVisibility(View.INVISIBLE);
    messageTwo.setVisibility(View.INVISIBLE);
    messageThree.setVisibility(View.INVISIBLE);
    messageFour.setVisibility(View.INVISIBLE);
    messageFive.setVisibility(View.INVISIBLE);
  }

  private void startTimerOne() {
    final Handler handler = new Handler();
    Runnable runnable =
        new Runnable() {
          public void run() {

            try {
              Thread.sleep(2000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            handler.post(
                new Runnable() {
                  public void run() {
                    messageOne.setVisibility(View.VISIBLE);
                  }
                });
          }
        };
    new Thread(runnable).start();
  }

  private void startTimerTwo() {
    final Handler handler = new Handler();
    Runnable runnable =
        new Runnable() {
          public void run() {

            try {
              Thread.sleep(4000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            handler.post(
                new Runnable() {
                  public void run() {
                    messageTwo.setVisibility(View.VISIBLE);
                  }
                });
          }
        };
    new Thread(runnable).start();
  }

  private void startTimerThree() {
    final Handler handler = new Handler();
    Runnable runnable =
        new Runnable() {
          public void run() {

            try {
              Thread.sleep(6000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            handler.post(
                new Runnable() {
                  public void run() {
                    messageThree.setVisibility(View.VISIBLE);
                  }
                });
          }
        };
    new Thread(runnable).start();
  }

  private void startTimerFour() {
    final Handler handler = new Handler();
    Runnable runnable =
        new Runnable() {
          public void run() {

            try {
              Thread.sleep(8000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            handler.post(
                new Runnable() {
                  public void run() {
                    messageFour.setVisibility(View.VISIBLE);
                  }
                });
          }
        };
    new Thread(runnable).start();
  }

  private void startTimerFive() {
    final Handler handler = new Handler();
    Runnable runnable =
        new Runnable() {
          public void run() {

            try {
              Thread.sleep(10000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            handler.post(
                new Runnable() {
                  public void run() {
                    messageFive.setVisibility(View.VISIBLE);
                  }
                });
          }
        };
    new Thread(runnable).start();
  }

  // The following method allows a user to click one of the messages which
  // appear in the xml layout and copy it directly to their clipboard

  public void copyMessage1(View v) {

    String copiedMessage = messageOne.getText().toString();

    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData clip = ClipData.newPlainText("message1", copiedMessage);
    clipboard.setPrimaryClip(clip);

    showToast();
  }

  public void copyMessage2(View v) {

    String copiedMessage2 = messageTwo.getText().toString();

    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData clip = ClipData.newPlainText("message2", copiedMessage2);
    clipboard.setPrimaryClip(clip);

    showToast();
  }

  public void copyMessage3(View v) {

    String copiedMessage3 = messageThree.getText().toString();

    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData clip = ClipData.newPlainText("message3", copiedMessage3);
    clipboard.setPrimaryClip(clip);

    showToast();
  }

  public void copyMessage4(View v) {

    String copiedMessage4 = messageFour.getText().toString();

    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData clip = ClipData.newPlainText("message4", copiedMessage4);
    clipboard.setPrimaryClip(clip);

    showToast();
  }

  public void copyMessage5(View v) {

    String copiedMessage5 = messageFive.getText().toString();

    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData clip = ClipData.newPlainText("message5", copiedMessage5);
    clipboard.setPrimaryClip(clip);

    showToast();
  }

  private void showToast() {

    Toast copied =
        Toast.makeText(BrokenCrypto.this, "Message copied to clipboard.", Toast.LENGTH_LONG);
    copied.show();
  }
}
