package com.example.platon_backend;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.text.intl.Locale;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "34.134.185.32";
    private static String TAG = "coldchain backend";

    private EditText mEditTextId;
    private EditText mEditTextTerminal_time;
    private EditText mEditTextServer_time;
    /*private EditText mEditTextX;
    private EditText mEditTextY;
    private EditText mEditTextbat_level;*/
    private TextView mTextViewResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextId = (EditText)findViewById(R.id.editText_main_id);
        mEditTextTerminal_time = (EditText)findViewById(R.id.editText_main_Terminal_time);
        // 여기까지 작업했음
        mEditTextServer_time = (EditText)findViewById(R.id.editText_main_Server_time);

        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        Button buttonInsert = (Button)findViewById(R.id.button_main_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TimeZone tz;                                        // 객체 생성
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                tz = TimeZone.getTimeZone("Asia/Seoul");  // TimeZone에 표준시 설정
                dateFormat.setTimeZone(tz);                    //DateFormat에 TimeZone 설정

                Date date = new Date();                        // 현재 날짜가 담긴 Date 객체 생성


                String Tempa[] = {"23","25","19","16","15","14","22"};
                String Huma[] = {"1.1","1.2","1.3","1.4","1.5","305","302"};
                String Winda[] = {"2.1","2.2","2.3","2.4","2.5","62","70"};
                /*String bat_levela[] = {"90","91","92","93","94","95","75"};*/

                String Temp = mEditTextId.getText().toString();
                String Hum = mEditTextTerminal_time.getText().toString();
//                String Terminal_time = mFormat.format(mDate);
                String Wind = mEditTextServer_time.getText().toString();
                /*String x = mEditTextX.getText().toString();
                String y = mEditTextY.getText().toString();
                String bat_level = mEditTextbat_level.getText().toString();*/

                if(Temp.isEmpty()){

                    for(int i=0;i<7;i++) {

                        Temp = Tempa[i];
                        Hum =  Huma[i]; // 단말 TimeStamp
                        Wind = Winda[i];
                        /*Hum =  dateFormat.format(date); // 단말 TimeStamp
                        Wind = "none";
                         x = xa[i];
                         y = ya[i];
                         bat_level = bat_levela[i];*/

                        InsertData task = new InsertData();
                        task.execute("http://" + IP_ADDRESS + "/insert.php", Temp, Hum, Wind/*, x, y, bat_level*/);
                    }
                }else {
                   /* Hum =  dateFormat.format(date);
                    Wind = "none"; */

                    InsertData task = new InsertData();
                    task.execute("http://" + IP_ADDRESS + "/insert.php", Temp, Hum, Wind/*, x, y, bat_level*/);
                }

                mEditTextId.setText("");
                mEditTextTerminal_time.setText("");
                mEditTextServer_time.setText("");
                /*mEditTextX.setText("");
                mEditTextY.setText("");
                mEditTextbat_level.setText("");*/

            }
        });

    }

    class InsertData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String Temp = (String)params[1];
            String Hum = (String)params[2];
            String Wind = (String)params[3];
            /*String x = (String)params[4];
            String y = (String)params[5];
            String bat_level = (String)params[6];*/


            String serverURL = (String)params[0];
            String postParameters = "Temp=" + Temp + "&Hum=" + Hum + "&Wind=" + Wind;



            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

}