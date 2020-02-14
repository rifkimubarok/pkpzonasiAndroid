package id.go.kemdikbud.pkpberbasiszonasi.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.go.kemdikbud.pkpberbasiszonasi.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_notification, container, false);

//        Set Toolbar Fragment
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.notifikasi));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        return v;
    }

}
