package com.example.calculator;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.VIBRATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bnv;
    private Stack<Integer> fragments;
    private int curFragmentId;
    private boolean backPressed, first;
    private static HashMap<String, BigDecimal> curList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        //ask for permissions
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                VIBRATE,
                INTERNET,
                READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        //for one time animation
        first = true;

        //for navbar behavior
        fragments = new Stack<>();
        backPressed = false;

        //initialize bottom nav
        bnv = findViewById(R.id.bnv);
        bnv.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.basic:
                            openFragment(BasicFragment.newInstance());
                            curFragmentId = R.id.basic;
                            return true;
                        case R.id.tip:
                            openFragment(TipsFragment.newInstance());
                            curFragmentId = R.id.tip;
                            return true;
                        case R.id.stocks:
                            openFragment(StocksFragment.newInstance());
                            curFragmentId = R.id.stocks;
                            return true;
                        case R.id.bitwise:
                            openFragment(BitwiseFragment.newInstance());
                            curFragmentId = R.id.bitwise;
                            return true;
                        case R.id.convert:
                            openFragment(ConvertFragment.newInstance());
                            curFragmentId = R.id.convert;
                            return true;
                    }
                    return false;
                });

        //start with basic calculator
        bnv.setSelectedItemId(R.id.basic);

        //disable reselection
        bnv.setOnNavigationItemReselectedListener(item -> {
            //do nothing
        });

        //load currency rates
        new Thread(() -> {
            if (isInternetAvailable()) {
                curList = new HashMap<>();
                CurrencyRequest request = new CurrencyRequest();
                request.execute();
            } else {
                Log.e("Exception", "Internet not available");
            }
        }).start();
    }

    /**
     * Navigate through app with back button.
     */
    @Override
    public void onBackPressed() {
        if (fragments.isEmpty() || curFragmentId == R.id.basic) {
            finish();
        } else {
            int id = fragments.pop();
            backPressed = true;
            bnv.setSelectedItemId(id);
            curFragmentId = id;
        }
    }

    /**
     * Checks if the user allowed certain permissions.
     *
     * @param context     current context
     * @param permissions permissions to check
     * @return true if allowed, false if not
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Handles opening fragments.
     *
     * @param fragment Fragment to open.
     */
    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //only animate first fragment
        if (first) {
            transaction.setCustomAnimations(R.anim.slide_from_bottom, 0);
            first = false;
        } else {
            transaction.setCustomAnimations(R.anim.enter_fragment, 0);
        }

        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        if (!backPressed)
            fragments.push(curFragmentId);
        else
            backPressed = false;
    }

    /**
     * Checks if internet is available.
     *
     * @return true if internet is available and false otherwise
     */
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Makes the request to load the xml file using okhttp.
     */
    private class CurrencyRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            try {
                String url = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                assert response.body() != null;
                return response.body().string();
            } catch (IOException ioe) {
                Log.e("Exception", "Currency request failed: " + ioe);
                return "";
            }
        }

        @Override
        protected void onPostExecute(String xml) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new InputSource(new StringReader(xml)));
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("Cube");

                //add elements to map of currency and rates
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        if (!eElement.getAttribute("currency").equals(""))
                            curList.put(eElement.getAttribute("currency"),
                                    new BigDecimal(eElement.getAttribute("rate")));

                    }
                }

                writeToFile();
            } catch (Exception e) {
                Log.e("Exception", "Failed to retrieve rates: " + e);
            }
        }
    }

    /**
     * Saves the current curList to file
     */
    private void writeToFile() {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    this.openFileOutput("rates.txt", Context.MODE_PRIVATE));

            for (Map.Entry<String, BigDecimal> entry : curList.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString();
                outputStreamWriter.write(key + ":" + value + "\n");
            }

            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e);
        }
    }
}