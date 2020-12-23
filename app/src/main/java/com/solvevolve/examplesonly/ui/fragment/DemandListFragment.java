package com.solvevolve.examplesonly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.solvevolve.examplesonly.adapter.DemandAdapter;
import com.solvevolve.examplesonly.databinding.FragmentDemandListBinding;
import com.solvevolve.examplesonly.model.Demand;
import com.solvevolve.examplesonly.network.Api;
import com.solvevolve.examplesonly.network.demand.DemandInterface;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DemandListFragment extends Fragment {

    FragmentDemandListBinding binding;
    DemandAdapter demandAdapter;
    private DemandInterface demandInterface;
    private ArrayList<Demand> demandList = new ArrayList<>();

    public DemandListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        demandInterface = new Api(getContext()).getClient().create(DemandInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentDemandListBinding.inflate(inflater, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.demandList.recyclerView.setLayoutManager(layoutManager);

        demandAdapter = new DemandAdapter(demandList, getActivity());
        binding.demandList.recyclerView.setAdapter(demandAdapter);

        updateDemand();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDemand();
    }

    void updateDemand() {
        demandInterface.getDemands().enqueue(new Callback<ArrayList<Demand>>() {
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
                Toast.makeText(getContext(), "Could not load demans", Toast.LENGTH_SHORT).show();

            }
        });
    }
}