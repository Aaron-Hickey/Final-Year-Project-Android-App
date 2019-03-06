package com.example.hal9000.communitywatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    boolean isMessageValid = false;

    private EditText nameFirst;
    private EditText nameLast;
    private EditText email;
    private EditText password;
    private EditText passwordConfirm;
    private EditText phoneNo;
    private EditText town;
    private EditText county;
    private EditText country;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameFirst = findViewById(R.id.nameFirst);
        nameLast = findViewById(R.id.nameLast);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        passwordConfirm = findViewById(R.id.passwordConfirm);
        phoneNo = findViewById(R.id.phoneNo);
        town = findViewById(R.id.town);
        county = findViewById(R.id.county);
        country = findViewById(R.id.country);
        errorText = findViewById(R.id.errorTextView);

        final Button button = findViewById(R.id.btnRegister);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Register();
            }
        });

        final Button button2 = findViewById(R.id.btnLinkToLoginScreen);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Register()
    {
        isMessageValid = true;
        errorText.setText("");

        final String nameFirstValue = nameFirst.getText().toString();
        final String nameLastValue = nameLast.getText().toString();
        final String emailValue = email.getText().toString();
        final String passwordValue = password.getText().toString();
        String passwordConfirmValue = passwordConfirm.getText().toString();
        final String phoneNoValue = phoneNo.getText().toString();
        final String townValue = town.getText().toString();
        final String countyValue = county.getText().toString();
        final String countryValue = country.getText().toString();


        String url = ServerAddressHandler.getInstance().getAddress()+"registerUser";

        RequestQueue queue = Volley.newRequestQueue(this);

        if(!passwordValue.equals(passwordConfirmValue))
        {
            isMessageValid = false;
            errorText.setText("Password and Confirm Password don't match");
        }

        if(nameFirstValue.equals("") || nameLastValue.equals("") || emailValue.equals("") || passwordValue.equals("") || passwordConfirmValue.equals("") || phoneNoValue.equals("") || townValue.equals("") || countyValue.equals("") || countryValue.equals(""))
        {
            isMessageValid = false;
            errorText.setText("Please fill in all fields");
        }

        PasswordValidator pv = new PasswordValidator();
        if(!pv.is_Valid_Password(passwordValue))
        {
            isMessageValid = false;
            errorText.setText("Password must be at least 8 characters long and must contain at least 1 lowercase letter, 1 uppercase letter and 1 number");
        }

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response,
                                Toast.LENGTH_LONG).show();
                        if(response.equals("Registered Successfully"))
                        {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                        errorText.setText(response);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("nameFirst", nameFirstValue);
                params.put("nameLast", nameLastValue);
                params.put("email", emailValue );
                params.put("password", passwordValue);
                params.put("phone", phoneNoValue);
                params.put("town", townValue);
                params.put("county", countyValue);
                params.put("country", countryValue);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if(isMessageValid == true)
        {
            queue.add(postRequest);
        }
    }
}
