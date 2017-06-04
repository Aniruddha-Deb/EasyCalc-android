package com.sensei.easycalc.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        if( sharedPreferences.getString( ANGLE_UNIT, getString( R.string.rad ) )
                .equals( getString( R.string.deg ) ) ) {
            SwitchButton s = (SwitchButton)getView().findViewById( R.id.angleUnitSelector );
            s.setChecked( true );
        }

    }
}
