package com.gmail.akostweb.mortargame;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Администратор on 05.10.2016.
 */

public class MortarGameFragment extends Fragment
{
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
}