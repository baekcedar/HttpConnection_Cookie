package com.baekcedar.android.remote_httpurlconnection_cookie;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView textResult;
    Button signBtn;
    EditText editId,editPw;
    ProgressDialog progress;

    SharedPreferences sp ;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp  = getApplicationContext().getSharedPreferences("cookie", 0);
        editor = sp.edit();
        textResult = (TextView) findViewById(R.id.textResult);
        signBtn = (Button) findViewById(R.id.signBtn);
        editId = (EditText) findViewById(R.id.editId);
        editPw = (EditText) findViewById(R.id.editPw);

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        textResult.setText(sp.getString("USERID", "") + ";");
    }

    private void signIn()  {
        HashMap userInfoMap =   new HashMap();
//        try{
//
//            String user_Id = editId.getText().toString();
//            byte[] encrypted = user_Id.getBytes("UTF-8");
//            String enStr = Base64.encodeToString(encrypted, 0);
//            Log.i("TEST", enStr);
//
//        }catch (Exception e){e.printStackTrace();}

        userInfoMap.put("user_Id", editId.getText().toString());
        userInfoMap.put("user_Pw", editPw.getText().toString());

        new AsyncTask<Map, Void, String>(){

            @Override
            protected String doInBackground(Map... params) {
                String result = "";
                String webURL = "http://192.168.0.171/setcookie.jsp";

                try {
                    result = Remote.postData(webURL, params[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = new ProgressDialog(MainActivity.this);
                progress.setTitle("다운로드");
                progress.setMessage("download");
                progress.setProgressStyle((ProgressDialog.STYLE_SPINNER));
                progress.setCancelable(false);
                progress.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);





                StringBuffer sb=  new StringBuffer();
                List<HttpCookie> cookies =  Remote.cookieManager.getCookieStore().getCookies();
                for( HttpCookie cookie : cookies){
                    sb.append(cookie.getName()+"="+cookie.getValue()+"\n");
                    editor.putString(cookie.getName(),cookie.getValue());
                }
                editor.commit();
                textResult.setText(sb.toString());

                progress.dismiss();
            }
        }.execute(userInfoMap);
    }
}
