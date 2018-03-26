package com.ashtosh23.examassistant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;
import java.io.InputStreamReader;
import java.io.*;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button mselectImage;

    private EditText mtext;

    //  private EditText keyword;

    //  private EditText marks;


    // private ImageView imageView;

   // private TextView rtext;
    private TextView rtext1;
    private TextView rtext2;
    private TextView rtext3;

    private ProgressDialog mprogressDialog;

    private StorageReference mstorage;

    private static final int GALLERY_INTENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mstorage = FirebaseStorage.getInstance().getReference();

        mselectImage = (Button) findViewById( R.id.selectImage );

       // rtext = findViewById( R.id.textView );
        rtext1 = findViewById( R.id.textView2 );
        rtext2 = findViewById( R.id.textView3 );
        rtext3 = findViewById( R.id.textView4 );

        // imageView = findViewById( R.id.imageView );

        mtext = findViewById( R.id.text );

        //  keyword = findViewById( R.id.Keywords );

        //   marks = findViewById( R.id.marks );

        mprogressDialog = new ProgressDialog( this );

        mselectImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent( Intent.ACTION_PICK );

                intent.setType( "image/*" );

                startActivityForResult( intent, GALLERY_INTENT );

            }
        } );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            mprogressDialog.setMessage( "Uploading..." );
            mprogressDialog.show();

            Uri uri = data.getData();

            StorageReference filepath = mstorage.child( "Photos" ).child( uri.getLastPathSegment() );

            filepath.putFile( uri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mprogressDialog.dismiss();

                    final Uri downloadUri = taskSnapshot.getDownloadUrl();

                    Log.i( "url", downloadUri.toString() );

                    String durl = downloadUri.toString();

                    // Picasso.with(MainActivity.this).load(downloadUri).fit().centerCrop().into(imageView);

                    //Picasso.get().load(downloadUri).fit().centerCrop().into( imageView );

                    mtext.setText( (CharSequence) downloadUri.toString() );

                    Toast.makeText( MainActivity.this, "Upload done", Toast.LENGTH_LONG ).show();

                    // makePostRequest();
                    sendPostRequest(downloadUri.toString());


                }

            } );


        }
    }

    private void sendPostRequest(String durl) {
        Log.i( "url1", durl );
        mprogressDialog.setMessage( "Processing..." );
        mprogressDialog.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String paramid = params[0];

                Log.i( "url2", paramid );
                //System.out.println("*** doInBackground ** paramUsername " + paramUsername + " paramPassword :" + paramPassword);

                HttpClient httpClient = new DefaultHttpClient();

                // In a POST request, we don't pass the values in the URL.
                //Therefore we use only the web page URL as the parameter of the HttpPost argument
                HttpPost httpPost = new HttpPost( "https://cryptic-mesa-62652.herokuapp.com/url" );

                // Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
                //uniquely separate by the other end.
                //To achieve that we use BasicNameValuePair
                //Things we need to pass with the POST request
                BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair( "id", paramid );
                // BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair("paramPassword", paramPassword);

                // We add the content that we want to pass with the POST request to as name-value pairs
                //Now we put those sending details to an ArrayList with type safe of NameValuePair
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add( usernameBasicNameValuePair );


                try {
                    // UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs.
                    //This is typically useful while sending an HTTP POST request.
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity( nameValuePairList );

                    // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
                    httpPost.setEntity( urlEncodedFormEntity );

                    try {
                        // HttpResponse is an interface just like HttpPost.
                        //Therefore we can't initialize them
                        HttpResponse httpResponse = httpClient.execute( httpPost );

                        // According to the JAVA API, InputStream constructor do nothing.
                        //So we can't initialize InputStream although it is not an interface
                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader( inputStream );

                        BufferedReader bufferedReader = new BufferedReader( inputStreamReader );

                        StringBuilder stringBuilder = new StringBuilder();

                        String bufferedStrChunk = null;

                        while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                            stringBuilder.append( bufferedStrChunk );
                        }

                        return stringBuilder.toString();

                    } catch (ClientProtocolException cpe) {
                        System.out.println( "First Exception caz of HttpResponese :" + cpe );
                        cpe.printStackTrace();
                    } catch (IOException ioe) {
                        System.out.println( "Second Exception caz of HttpResponse :" + ioe );
                        ioe.printStackTrace();
                    }

                } catch (UnsupportedEncodingException uee) {
                    System.out.println( "An Exception given because of UrlEncodedFormEntity argument :" + uee );
                    uee.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute( result );
               // rtext.setText( result );
                mprogressDialog.dismiss();
                JSONObject jObj = null;
                try {
                    jObj = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String dtext = null;
                String dkey = null;
                String dmark = null;
                try {
                    dtext = jObj.getString("text");
                    dkey = jObj.getString( "keywords" );
                    dmark = jObj.getString( "marks" );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                rtext1.setText( dtext );
                rtext2.setText( dkey );
                rtext3.setText( dmark );
                Log.i( "ddiinh", dtext );
                Log.i( "ddiinf", dkey );
                Log.i( "ddiing", dmark );



                if (result.equals( "working" )) {
                    Toast.makeText( getApplicationContext(), "HTTP POST is working...", Toast.LENGTH_LONG ).show();
                } else {
                    Toast.makeText( getApplicationContext(), "Result", Toast.LENGTH_LONG ).show();
                }
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(durl);
    }
   /* private void makePostRequest() {
        HttpClient httpClient = new DefaultHttpClient();
        // replace with your url
        HttpPost httpPost = new HttpPost("https://cryptic-mesa-62652.herokuapp.com/url");
        //Post Data
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("id", "http://opensourceforu.com/wp-content/uploads/2016/09/Figure-1-Sample-Page-1.jpg"));
        //Encoding POST data
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            // log exception
            e.printStackTrace();
        }
        //making POST request.
        try {
            HttpResponse response = httpClient.execute(httpPost);
            // write response to log
            Log.d("Http Post Response:", response.toString());
        } catch (ClientProtocolException e) {
            // Log exception
            e.printStackTrace();
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
        }
    }
*/
}