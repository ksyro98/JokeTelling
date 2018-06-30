package com.udacity.gradle.builditbigger;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.jokecontainer.JokeActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.R;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivityFragment extends Fragment {
    @Nullable
    private SimpleIdlingResource idlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource(){
        if(idlingResource == null){
            idlingResource = new SimpleIdlingResource();
        }

        return idlingResource;
    }


    @BindView(R.id.tell_joke_button) Button tellJokeButton;
    @BindView(R.id.adView) AdView adView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, root);

        tellJokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleCloudEndpointTask task = new GoogleCloudEndpointTask();
                task.execute();
            }
        });

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);


        return root;
    }

    /**
     * This method starts an Activity to display a joke.
     * The joke is passed to the Activity as an Intent extra.
     *
     * @param joke the joke to display in the Activity
     */
    public void tellJoke(String joke) {
        Intent intent = new Intent(getContext(), JokeActivity.class);

        intent.putExtra(JokeActivity.JOKE, joke);
        startActivity(intent);
    }

    /**
     * An AsyncTask to get the data from the Google Cloud Endpoints.
     * idlingResource values also change in the AsyncTask for testing purposes.
     */
    class GoogleCloudEndpointTask extends AsyncTask<Void, Void, String>{
        private MyApi myApiService = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            getIdlingResource();
            if(idlingResource != null){
                idlingResource.setIdleState(false);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            if(myApiService == null){
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(),
                        null)
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
                                request.setDisableGZipContent(true);
                            }
                        });

                myApiService = builder.build();

            }

            try {

                return myApiService.getJoke().execute().getData();
            }
            catch (IOException e){
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            tellJoke(response);

            if(idlingResource != null){
                idlingResource.setIdleState(true);
            }
        }
    }
}