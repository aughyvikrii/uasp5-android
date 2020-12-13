package com.example.aughyuas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    private EditText editTextEmail;
    private Button btnCheckEmail;

    private EditText fieldEmail;
    private EditText fieldName;
    private EditText fieldIdentityNumber;
    private EditText fieldAddress;

    private Button btnRegister;

    private Button btnConfirmation;

    private EditText userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Intent newIntent = new Intent(this, Payment.class);
//        startActivity(newIntent);

        setContentView(R.layout.activity_main);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        btnCheckEmail = (Button) findViewById(R.id.check_email);

        btnCheckEmail.setOnClickListener(this);
    }

    private void checkEmail() {
        final String email = editTextEmail.getText().toString().trim();

        if( email.isEmpty() ) {
            Toast.makeText(getApplicationContext(),"Email required", Toast.LENGTH_LONG).show();
        } else {
            class CheckEmail extends AsyncTask<Void, Void, String> {
                ProgressDialog loading;

                @Override
                protected void  onPreExecute(){
                    super.onPreExecute();
                    loading = ProgressDialog.show(MainActivity.this,"Proses cek email...", "Tunggu....",false,false);
                }

                @Override
                protected void onPostExecute(String json) {
                    super.onPostExecute(json);
                    loading.dismiss();
                    if(json.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Terjadi kesalahan! silahkan coba lagi.", Toast.LENGTH_LONG).show();
                    } else {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            if( obj.getString("status") != "true" ) {
                                try {
                                    Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                registerForm(email);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                protected String doInBackground(Void... voids) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put(Configuration.KEY_USER_EMAIL,email);

                    RequestHandler rh = new RequestHandler();
                    String res = rh.sendPostRequest(Configuration.URL_CHECK_EMAIL, params);
                    return res;
                }
            }

            CheckEmail ce = new CheckEmail();
            ce.execute();
        }
    }

    private void registerForm(String email) {
        setContentView(R.layout.register_form);

        fieldEmail = (EditText) findViewById(R.id.editTextEmail);
        fieldName = (EditText) findViewById(R.id.editTextName);
        fieldIdentityNumber = (EditText) findViewById(R.id.editTextIdentityNumber);
        fieldAddress = (EditText) findViewById(R.id.editTextAddress);

        fieldEmail.setText(email);

        btnRegister = (Button) findViewById(R.id.buttonRegister);

        btnRegister.setOnClickListener(this);

    }

    private void registerAccount() {
        final String email = fieldEmail.getText().toString().trim();
        final String name = fieldName.getText().toString().trim();
        final String identityNumber = fieldIdentityNumber.getText().toString().trim();
        final String address = fieldAddress.getText().toString().trim();

        if( email.isEmpty() ) {
            Toast.makeText(getApplicationContext(),"Masukan email", Toast.LENGTH_LONG).show();
        } else if( name.isEmpty() ) {
            Toast.makeText(getApplicationContext(),"Masukan nama lengkap", Toast.LENGTH_LONG).show();
        } else if( identityNumber.isEmpty() ) {
            Toast.makeText(getApplicationContext(),"Masukan Nomor Identitas", Toast.LENGTH_LONG).show();
        } else if( address.isEmpty() ) {
            Toast.makeText(getApplicationContext(),"Masukan alamat lengkap", Toast.LENGTH_LONG).show();
        } else {
            class registerAccount extends AsyncTask<Void, Void, String> {
                ProgressDialog loading;

                @Override
                protected void  onPreExecute(){
                    super.onPreExecute();
                    loading = ProgressDialog.show(MainActivity.this,"Proses pendaftaran...", "Tunggu....",false,false);
                }

                @Override
                protected void onPostExecute(String json) {
                    super.onPostExecute(json);
                    loading.dismiss();
                    if(json.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Terjadi kesalahan! silahkan coba lagi.", Toast.LENGTH_LONG).show();
                    } else {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            if( obj.getString("status") != "true" ) {
                                try {
                                    Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                JSONObject userData = new JSONObject(obj.getString("data"));
                                confirmView(userData.getString("user_id").toString().trim());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                protected String doInBackground(Void... voids) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("email",email);
                    params.put("name", name);
                    params.put("identity_number", identityNumber);
                    params.put("address", address);

                    RequestHandler rh = new RequestHandler();
                    String res = rh.sendPostRequest(Configuration.URL_REGISTER, params);
                    return res;
                }
            }

            registerAccount ce = new registerAccount();
            ce.execute();
        }
    }

    private void confirmView(String user_id) {
        setContentView(R.layout.confirm_view);

        userID = (EditText) findViewById(R.id.user_id);
        userID.setText(user_id);
        userID.setVisibility(View.GONE);

        btnConfirmation = (Button) findViewById(R.id.btnConfirmation);

        btnConfirmation.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == btnCheckEmail) {
            checkEmail();
        } else if(v == btnRegister) {
            registerAccount();
        } else {
            Intent newIntent = new Intent(this,Payment.class);
            userID = (EditText) findViewById(R.id.user_id);

            newIntent.putExtra("user_id", userID.getText().toString());

            startActivity(newIntent);
        }
    }
}