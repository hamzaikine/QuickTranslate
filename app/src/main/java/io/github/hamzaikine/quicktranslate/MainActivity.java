package io.github.hamzaikine.quicktranslate;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView;
    Spinner fromSpinner;
    Spinner toSpinner;
    String languageFrom;
    String languageTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // spinners to select languages
        fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        toSpinner = (Spinner) findViewById(R.id.toSpinner);

        String[] languages = {"English","French","Spanish","Arabic"};

        // creating an adapter for the spinners
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,languages);

        //set the adapter to the spinners
        fromSpinner.setAdapter(arrayAdapter);
        toSpinner.setAdapter(arrayAdapter);

        //retrive the language selected
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                languageFrom = adapterView.getItemAtPosition(i).toString();
                if(languageFrom == "English"){
                    languageFrom = "en";
                }else if(languageFrom == "French"){
                    languageFrom = "fr";

                }else if(languageFrom == "Spanish"){
                    languageFrom = "es";
                }else if(languageFrom == "Arabic"){
                    languageFrom = "ar";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                languageTo = adapterView.getItemAtPosition(i).toString();
                if(languageTo == "English"){
                    languageTo = "en";
                }else if(languageTo == "French"){
                    languageTo = "fr";

                }else if(languageTo == "Spanish"){
                    languageTo = "es";
                }else if(languageTo == "Arabic"){
                    languageTo = "ar";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void onTranslate(View view) {
        editText = (EditText) findViewById(R.id.editText);

        String mysteriousText= editText.getText().toString();


        if(!isEmptyEditText(editText)){


            // using save the feed which a class that extends AsyncTask to do interact with api in the background
            new saveTheFeed().execute(mysteriousText,languageFrom,languageTo);

        }else
            Toast.makeText(this,"Please Enter words for translation.",Toast.LENGTH_SHORT).show();



    }

    public boolean isEmptyEditText(EditText editText1){
        return editText1.getText().toString().trim().length() == 0;
    }


    //inner class to interact with google translate api
    class saveTheFeed extends AsyncTask<String, Void, Void> {

        URL url;
        HttpURLConnection urlConnection = null;
        String server_response;
        StringBuilder response;

        @Override
        protected Void doInBackground(String... param) {

            response = new StringBuilder();
            EditText editText = (EditText) findViewById(R.id.editText);
            String word = param[0];
            String From = param[1];
            String To = param[2];
            //"https://api.exchangeratesapi.io/latest"
            try {

                String url1 = "https://translate.googleapis.com/translate_a/single?"+
                        "client=gtx&"+
                        "sl=" + From +
                        "&tl=" + To +
                        "&dt=t&q=" + URLEncoder.encode(word, "UTF-8");

                url = new URL(url1);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("User-Agent","Mozialla/5.0");

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());

                    Log.d("response", server_response);

                    JSONArray jsonArray = new JSONArray(server_response);
                    JSONArray jsonArray2 = (JSONArray) jsonArray.get(0);
                    JSONArray jsonArray3 = (JSONArray) jsonArray2.get(0);


                    response.append(jsonArray3.get(1)+ ": " + jsonArray3.get(0));
                    //outputTranslations(jArray);

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        // Called after doInBackground finishes executing
        @Override
        protected void onPostExecute(Void aVoid) {

            // Put the translations in the TextView
            TextView textView = (TextView) findViewById(R.id.textView);

            //set the response to our text view
            textView.setText(response);

        }


        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }



    }



}
