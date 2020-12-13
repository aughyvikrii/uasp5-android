package com.example.aughyuas;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Payment extends AppCompatActivity implements View.OnClickListener {

    final Calendar myCalendar = Calendar.getInstance();
    private EditText transferDate;
    private EditText fieldUserId;
    private EditText fieldBankName;
    private EditText fieldTransferDate;
    private EditText fieldTransferAmount;
    private EditText fieldAccountNumber;

    private Button buttonConfirmPayment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_form);

        String userID = getIntent().getStringExtra("user_id");

        transferDate = (EditText) findViewById(R.id.transferDate);
        fieldUserId = (EditText) findViewById(R.id.user_id);
        fieldBankName = (EditText) findViewById(R.id.editTextBankName);
        fieldTransferDate = (EditText) findViewById(R.id.transferDate);
        fieldTransferAmount = (EditText) findViewById(R.id.editTextTransferAmount);
        fieldAccountNumber = (EditText) findViewById(R.id.editTextAccountNumber);

        fieldUserId.setText(userID);
        fieldUserId.setVisibility(View.GONE);
        buttonConfirmPayment = (Button) findViewById(R.id.buttonConfirmPayment);

        buttonConfirmPayment.setOnClickListener(this);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        transferDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Payment.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar
                .DAY_OF_MONTH)).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == buttonConfirmPayment) {
            paymentConfirmAction();
        }
    }

    private void updateLabel() {
        String myFormat = "YYY-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        transferDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void paymentConfirmAction() {

        final String user_id = fieldUserId.getText().toString();
        final String bankName = fieldBankName.getText().toString();
        final String transferDate = fieldTransferDate.getText().toString();
        final String transferAmount = fieldTransferAmount.getText().toString();
        final String accountNumber = fieldAccountNumber.getText().toString();

        if( user_id.isEmpty() ) {
            Toast.makeText(getApplicationContext(),"Terjadi kesalahan, user tidak dikenali! ulangi dari awal", Toast.LENGTH_LONG).show();
        } else if( bankName.isEmpty() ) {
            Toast.makeText(getApplicationContext(),"Masukan Nama Bank", Toast.LENGTH_LONG).show();
        } else if( transferDate.isEmpty() ) {
            Toast.makeText(getApplicationContext(),"Masukan Tanggal Transfer", Toast.LENGTH_LONG).show();
        } else if( transferAmount.isEmpty() ) {
            Toast.makeText(getApplicationContext(),"Masukan Nominal Transfer", Toast.LENGTH_LONG).show();
        } else if( accountNumber.isEmpty() ) {
            Toast.makeText(getApplicationContext(),"Masukan Nomor Rekening", Toast.LENGTH_LONG).show();
        } else {
            class paymentConfirmAction extends AsyncTask<Void, Void, String> {
                ProgressDialog loading;

                @Override
                protected void  onPreExecute(){
                    super.onPreExecute();
                    loading = ProgressDialog.show(Payment.this,"Proses konfirmasi...", "Tunggu....",false,false);
                }

                @Override
                protected void onPostExecute(String json) {
                    super.onPostExecute(json);
                    loading.dismiss();
                    if(json.isEmpty()) {
                        Toast.makeText(Payment.this, "Terjadi kesalahan! silahkan coba lagi.", Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(Payment.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                setContentView(R.layout.success_confirm);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                protected String doInBackground(Void... voids) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", user_id);
                    params.put("bank", bankName);
                    params.put("account_number", accountNumber);
                    params.put("transfer_date", transferDate);
                    params.put("transfer_amount", transferAmount);

                    RequestHandler rh = new RequestHandler();
                    String res = rh.sendPostRequest(Configuration.URL_CONFIRM, params);
                    return res;
                }
            }

            paymentConfirmAction ce = new paymentConfirmAction();
            ce.execute();
        }
    }
}
