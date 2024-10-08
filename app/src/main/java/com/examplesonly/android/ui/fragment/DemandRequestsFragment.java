package com.examplesonly.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.examplesonly.android.adapter.DemandAdapter;
import com.examplesonly.android.databinding.FragmentDemandRequestsBinding;
import com.examplesonly.android.model.Demand;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.user.UserInterface;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DemandRequestsFragment extends Fragment {

    private FragmentDemandRequestsBinding binding;
    DemandAdapter demandAdapter;
    private UserInterface userInterface;
    private final ArrayList<Demand> demandList = new ArrayList<>();

    public DemandRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInterface = new Api(getContext()).getClient().create(UserInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDemandRequestsBinding.inflate(inflater, container, false);
        setupDemand();
        return binding.getRoot();
    }


    void setupDemand() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.demandList.recyclerView.setLayoutManager(layoutManager);

        demandAdapter = new DemandAdapter(demandList, getActivity());
        binding.demandList.recyclerView.setAdapter(demandAdapter);

        userInterface.getDemands().enqueue(new Callback<ArrayList<Demand>>() {
            @Override
            public void onResponse(final Call<ArrayList<Demand>> call, final Response<ArrayList<Demand>> response) {
                if (response.isSuccessful()) {
                    demandList.clear();
                    demandList.addAll(response.body());
                    demandAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Could not load demands", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(final Call<ArrayList<Demand>> call, final Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Could not load demands", Toast.LENGTH_SHORT).show();

            }
        });
    }
}