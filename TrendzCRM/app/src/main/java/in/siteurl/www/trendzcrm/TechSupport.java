package in.siteurl.www.trendzcrm;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class TechSupport extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    SharedPreferences prefs;

    EditText sub,msg;
    Dialog alertDialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.homemenu:
                Intent intent=new Intent(TechSupport.this,Home.class);
                SharedPreferences preferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
               // intent.putExtra("response",preferences.getString("responseatoz",null));
                startActivity(intent);
                return true;
            case R.id.chngpswd:
                Intent intent2=new Intent(TechSupport.this,ChangePassword.class);
                startActivity(intent2);
                return true;
            case R.id.logout:
                Intent intent3=new Intent(TechSupport.this,Logout.class);
                startActivity(intent3);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // method to raise data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_support);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sub=(EditText) findViewById(R.id.techSuppoSub);
        msg=(EditText)findViewById(R.id.techSuppoMsg);
        prefs = getSharedPreferences("LoginPref", MODE_PRIVATE);

        alertDialog = new Dialog(this); checkConnection();
        Snackbar.make(findViewById(R.id.snackcoord),"Kindly leave your message. . . Our tech support team will get in touch with you soon. . .",Snackbar.LENGTH_LONG).show();
    }



    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
        return isConnected;
    }
    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        if (isConnected) {
            //Method to signin
            //Signinwithmail(email, password);
            if(alertDialog.isShowing())
            {
                alertDialog.dismiss();
            }
        } else {
            shownointernetdialog();
        }
    }
    //To show no internet dialog
    private void shownointernetdialog() {
        //alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.nointernet);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        Button retry = alertDialog.findViewById(R.id.exit_btn);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                //checkConnection();
                System.exit(0);
            }
        });
        alertDialog.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    ConnectivityReceiver mNetworkReceiver=new ConnectivityReceiver();

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

        VendorApplication.getInstance().setConnectivityListener(this);

    }

    public void finishGo(View view) {
        //


        if (sub.getText().length()<3)
            sub.setError("Enter valid subject");
        else if (msg.getText().length()<3)
            msg.setError("Enter valid message");
        else {
    /*    AlertDialog.Builder builder=new AlertDialog.Builder(TechSupport.this);
        builder.setMessage("Your message has been sent successfully. . .");

            View vie= LayoutInflater.from(TechSupport.this).inflate(R.layout.alertokay,null);
            ImageView wokay=vie.findViewById(R.id.wokay);
            wokay.setBackgroundResource(R.drawable.wokaytrendz);
            AnimationDrawable frameAnimation = (AnimationDrawable) wokay.getBackground();
            frameAnimation.start();//-=-=====----=-=-=_+_+-+-+-=_+-=_=_+-=_+-=_=-=_=_+-+_=_=_+-+-+-=_=_+_+-=_=-+_=_=_+-=_
            builder.setView(vie);


            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    //finishAffinity();
                }
               // startActivity((new Intent(TechSupport.this,Login.class)));

            }
        });
        builder.setCancelable(false);
        builder.show();
*/
    if (checkConnection())
        callTechSuppoAPI();
       // finish();
    }}


    //Handling Volley Error
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            //Toast.makeText(TechSupport.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(TechSupport.this);
            loginErrorBuilder.setTitle("Error");
            loginErrorBuilder.setMessage(message);
            loginErrorBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            loginErrorBuilder.show();
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }
    // call api to register the complaint

    private void callTechSuppoAPI() {
         {
            StringRequest addticketreqst=new StringRequest(Request.Method.POST, "http://apartmentsmysore.in/crm/addticket",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(AddTicket.this, response, Toast.LENGTH_SHORT).show();
                            try {
                                if ((new JSONObject(response)).getString("Message").toString().contains("Your ticket has been submitted")){
                                    AlertDialog.Builder builder=new AlertDialog.Builder(TechSupport.this);
                                    builder.setMessage("Your query has been submitted successfully. . . ");

                                    View vie=LayoutInflater.from(TechSupport.this).inflate(R.layout.alertokay,null);
                                    ImageView wokay=vie.findViewById(R.id.wokay);
                                    wokay.setBackgroundResource(R.drawable.wokaytrendz);
                                    AnimationDrawable frameAnimation = (AnimationDrawable) wokay.getBackground();
                                    frameAnimation.start();//-=-=====----=-=-=_+_+-+-+-=_+-=_=_+-=_+-=_=-=_=_+-+_=_=_+-+-+-=_=_+_+-=_=-+_=_=_+-=_
                                    builder.setView(vie);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            //startActivity((new Intent(AddTicket.this,Login.class)));
                                            finish();
                                        }
                                    });
                                    builder.setCancelable(false);
                                    builder.show();
                                    //passwordED.setError((new JSONObject(response)).getString("Message").toString());
                                    //loginED.setText("");
                                    // Toast.makeText(Login.this, (new JSONObject(response)).getString("Message").toString(), Toast.LENGTH_SHORT).show();

                                }
                                else if ((new JSONObject(response)).getString("Message").toString().contains("Invalid login details")){
                                    AlertDialog.Builder builder=new AlertDialog.Builder(TechSupport.this);
                                    builder.setMessage((new JSONObject(response)).getString("Message").toString());

                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                finishAffinity();
                                            }
                                            startActivity((new Intent(TechSupport.this,Logout.class)));

                                        }
                                    });
                                    builder.setCancelable(false);
                                    builder.show();
                                    //passwordED.setError((new JSONObject(response)).getString("Message").toString());
                                    //loginED.setText("");
                                    // Toast.makeText(Login.this, (new JSONObject(response)).getString("Message").toString(), Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //dialog.dismiss();
                    //Toast.makeText(TechSupport.this, error.toString(), Toast.LENGTH_LONG).show();
                    if (error.networkResponse != null) {
                        parseVolleyError(error);
                    }
                    if (error instanceof ServerError) {
                        Toast.makeText(TechSupport.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                        Log.d("Error", String.valueOf(error instanceof ServerError));
                        error.printStackTrace();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(TechSupport.this, "Authentication Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Authentication Error");
                        error.printStackTrace();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(TechSupport.this, "Parse Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Parse Error");
                        error.printStackTrace();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(TechSupport.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                        Log.d("Error", "No Connection Error");
                        error.printStackTrace();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(TechSupport.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Network Error");
                        error.printStackTrace();
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(TechSupport.this, "Timeout Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Timeout Error");
                        error.printStackTrace();
                    } else {
                        Toast.makeText(TechSupport.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }            }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    HashMap<String,String> params=new HashMap<>();
                    params.put("customer_id", prefs.getString("customer_id","you got nothing"));
                    params.put("sid", prefs.getString("sid","you got nothing"));
                    params.put("api_key","4c0c39c32f8339ab25fd7afb05eccf0efd1dba49");
                    params.put("unit_id","    ");
                    params.put("subject",sub.getText().toString());
                    params.put("message",msg.getText().toString());
                    // Toast.makeText(AddTicket.this, params.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("opopop",prefs.toString());
                    return params;

                }
            };
            SingleTon.getInnstance(TechSupport.this).addREquest(addticketreqst);
            addticketreqst.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        }}


}
