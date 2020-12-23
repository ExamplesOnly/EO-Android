package com.solvevolve.examplesonly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.solvevolve.examplesonly.R;
import com.solvevolve.examplesonly.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileStatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileStatsFragment extends Fragment {

    private static final String ARG_USER = "user";
    private User user;

    public static ProfileStatsFragment newInstance(User user) {
        ProfileStatsFragment fragment = new ProfileStatsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileStatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_stats, container, false);
    }
}