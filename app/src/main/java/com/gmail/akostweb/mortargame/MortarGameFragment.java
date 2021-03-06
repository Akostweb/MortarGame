package com.gmail.akostweb.mortargame;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



/**
 * Created by Администратор on 05.10.2016.
 */

public class MortarGameFragment extends Fragment
{
    private static double timeLeft;
    private MortarView mortarView; // custom view to display the game



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view =
                inflater.inflate(R.layout.fragment_game, container, false);


        mortarView = (MortarView) view.findViewById(R.id.mortarView);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);


        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    @Override
    public void onPause()
    {
        super.onPause();
        mortarView.stopGame();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mortarView.releaseResources();
    }

    public void updateTime(SharedPreferences sharedPreferences){
        String time = sharedPreferences.getString(MainActivity.TIME, null);
        timeLeft = Double.parseDouble(time);
        //Toast.makeText(getActivity(), "zna4enie"+timeLeft,Toast.LENGTH_LONG).show();
        //Intent i = new Intent(this, MainActivity.class);
        //i.putExtra("Value", time);
        MortarView.timeNew(timeLeft);

    }




}