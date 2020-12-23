package com.solvevolve.examplesonly.ui.fragment;

import static com.solvevolve.examplesonly.adapter.ExampleAdapter.VIEW_TYPE_EXAMPLE_THREE;
import static com.solvevolve.examplesonly.ui.activity.MainActivity.OPTION_CHOOSE_VIDEO_EOD;
import static com.solvevolve.examplesonly.ui.activity.MainActivity.OPTION_RECORD_VIDEO_EOD;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.solvevolve.examplesonly.R;
import com.solvevolve.examplesonly.adapter.ExampleAdapter;
import com.solvevolve.examplesonly.component.BottomSheetOptionsDialog;
import com.solvevolve.examplesonly.databinding.FragmentDemandDetailsBinding;
import com.solvevolve.examplesonly.handler.VideoClickListener;
import com.solvevolve.examplesonly.model.BottomSheetOption;
import com.solvevolve.examplesonly.model.Demand;
import com.solvevolve.examplesonly.model.Video;
import com.solvevolve.examplesonly.network.Api;
import com.solvevolve.examplesonly.network.demand.DemandInterface;
import java.io.IOException;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class DemandDetailsFragment extends Fragment {

    private static final String ARG_DEMAND_ID = "demand_id";

    private ArrayList<Video> exampleListList = new ArrayList<>();
    private FragmentDemandDetailsBinding binding;
    private DemandInterface demandInterface;
    private ExampleAdapter mExampleAdapter;
    private Demand demand;

    public static DemandDetailsFragment newInstance(Demand demand) {
        DemandDetailsFragment fragment = new DemandDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DEMAND_ID, demand);
        fragment.setArguments(args);
        return fragment;
    }

    public DemandDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            demand = getArguments().getParcelable(ARG_DEMAND_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDemandDetailsBinding.inflate(getLayoutInflater());
        demandInterface = new Api(getContext()).getClient().create(DemandInterface.class);

        if (demand == null) {
            return binding.getRoot();
        }

        setupExamples();
        getVideos(demand.getUuid());

        if (demand.getVideoCount() < 1) {
            binding.noAnswerContainer.setVisibility(View.VISIBLE);
            binding.videosContainer.setVisibility(View.GONE);
        } else {
            binding.noAnswerContainer.setVisibility(View.GONE);
            binding.videosContainer.setVisibility(View.VISIBLE);
        }

        binding.title.setText(demand.getTitle());
        binding.postExample.setOnClickListener(view -> {
            ArrayList<BottomSheetOption> optionList = new ArrayList<>();
            optionList.add(new BottomSheetOption(OPTION_CHOOSE_VIDEO_EOD, "Upload from device",
                    ContextCompat.getDrawable(getActivity(), R.drawable.ic_upload_mono), demand));
            optionList.add(new BottomSheetOption(OPTION_RECORD_VIDEO_EOD, "Record a video",
                    ContextCompat.getDrawable(getActivity(), R.drawable.ic_camera_mono), demand));
            BottomSheetOptionsDialog bottomSheet = new BottomSheetOptionsDialog("Post Example", optionList);
            bottomSheet.show(((FragmentActivity) getActivity()).getSupportFragmentManager(), "EdoVideoBottomSheet");
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getVideos(demand.getUuid());
    }

    void getVideos(String demand) {
        demandInterface.getDemandVideos(demand).enqueue(new Callback<ArrayList<Video>>() {
            @Override
            public void onResponse(final Call<ArrayList<Video>> call, final Response<ArrayList<Video>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Video> videos = response.body();
                    exampleListList.clear();
                    for (int i = 0; i < videos.size(); i++) {
                        exampleListList.add(videos.get(i));
                        mExampleAdapter.notifyDataSetChanged();
                    }
                } else {
                    try {
                        Timber.e(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final Call<ArrayList<Video>> call, final Throwable t) {

            }
        });
    }

    void setupExamples() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.videoList.setLayoutManager(layoutManager);

        mExampleAdapter = new ExampleAdapter(exampleListList, getContext(), (VideoClickListener) getActivity(),
                VIEW_TYPE_EXAMPLE_THREE);
        binding.videoList.setAdapter(mExampleAdapter);
    }
}