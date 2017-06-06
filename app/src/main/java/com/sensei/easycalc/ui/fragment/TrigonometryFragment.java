package com.sensei.easycalc.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.kyleduo.switchbutton.SwitchButton;
import com.sensei.easycalc.R;

import static com.sensei.easycalc.core.Symbols.ANGLE_UNIT;

public class TrigonometryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate( R.layout.trigonometry_layout, container, false );
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( getContext() );
        FrameLayout frame = ( FrameLayout ) getView().findViewById( R.id.trig_buttons );
        frame.removeAllViews();
        frame.addView( getActivity().getLayoutInflater().inflate( R.layout.trigonometry_layout_normal, null ) );

        if( sharedPreferences.getString( ANGLE_UNIT, getString( R.string.rad ) )
                .equals( getString( R.string.deg ) ) ) {
            SwitchButton s = (SwitchButton)getView().findViewById( R.id.angleUnitSelector );
            s.setChecked( true );
        }

        Button invert = (Button)getView().findViewById( R.id.invert );
        invert.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                FrameLayout frame = ( FrameLayout ) getView().findViewById( R.id.trig_buttons );
                frame.removeAllViews();
                if( frame.getTag().equals( getActivity().getString( R.string.normal ) ) ) {
                    frame.addView( getActivity().getLayoutInflater().inflate( R.layout.trigonometry_layout_inverse, null ) );
                    frame.setTag( getActivity().getString( R.string.inverse ) );
                }
                else {
                    frame.addView( getActivity().getLayoutInflater().inflate( R.layout.trigonometry_layout_normal, null ) );
                    frame.setTag( getActivity().getString( R.string.normal ) );
                }
            }
        } );

    }
}
