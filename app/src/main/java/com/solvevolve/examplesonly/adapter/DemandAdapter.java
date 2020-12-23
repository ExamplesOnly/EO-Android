package com.solvevolve.examplesonly.adapter;

import static com.solvevolve.examplesonly.ui.activity.MainActivity.INDEX_DEMAND_DETAILS;
import static com.solvevolve.examplesonly.ui.activity.MainActivity.OPTION_CHOOSE_VIDEO_EOD;
import static com.solvevolve.examplesonly.ui.activity.MainActivity.OPTION_RECORD_VIDEO_EOD;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.solvevolve.examplesonly.R;
import com.solvevolve.examplesonly.component.BottomSheetOptionsDialog;
import com.solvevolve.examplesonly.databinding.ViewDemandItemBinding;
import com.solvevolve.examplesonly.handler.FragmentChangeListener;
import com.solvevolve.examplesonly.model.BottomSheetOption;
import com.solvevolve.examplesonly.model.Demand;
import com.solvevolve.examplesonly.network.Api;
import com.solvevolve.examplesonly.network.demand.DemandInterface;
import java.util.ArrayList;

public class DemandAdapter extends RecyclerView.Adapter<DemandAdapter.ViewHolder> {

    private final ArrayList<Demand> mList;
    public final Activity activity;
    LayoutInflater inflater;

    public DemandAdapter(final ArrayList<Demand> list, final Activity activity) {
        mList = list;
        this.activity = activity;
        inflater = LayoutInflater.from(this.activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new ViewHolder(ViewDemandItemBinding.inflate(inflater, parent, false), activity);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private DemandInterface demandInterface;
        ViewDemandItemBinding binding;
        Activity activity;

        public ViewHolder(@NonNull final ViewDemandItemBinding itemView, Activity activity) {
            super(itemView.getRoot());
            binding = itemView;
            this.activity = activity;
            demandInterface = new Api(activity).getClient().create(DemandInterface.class);
        }

        void bind(Demand demand) {
            binding.title.setText(demand.getTitle());

            if (demand.getVideoCount() > 0) {
                binding.exampleCount.setText(demand.getVideoCount() + " Example");
            } else {
                binding.exampleCount.setText(R.string.no_example_yet);
            }

            binding.demandCard.setOnClickListener(view -> {
                if (activity instanceof FragmentChangeListener) {
                    FragmentChangeListener fragmentChangeListener = (FragmentChangeListener) activity;
                    fragmentChangeListener.switchFragment(INDEX_DEMAND_DETAILS, demand);
                }

            });
            binding.postExample.setOnClickListener(view -> {
                ArrayList<BottomSheetOption> optionList = new ArrayList<>();
                optionList.add(new BottomSheetOption(OPTION_CHOOSE_VIDEO_EOD, "Upload from device",
                        ContextCompat.getDrawable(activity, R.drawable.ic_upload_mono), demand));
                optionList.add(new BottomSheetOption(OPTION_RECORD_VIDEO_EOD, "Record a video",
                        ContextCompat.getDrawable(activity, R.drawable.ic_camera_mono), demand));
                BottomSheetOptionsDialog bottomSheet = new BottomSheetOptionsDialog("Post Example", optionList);
                bottomSheet.show(((FragmentActivity) activity).getSupportFragmentManager(), "EdoVideoBottomSheet");
            });

//            binding.bookmarkDemand.setOnClickListener(view -> {
//                demandInterface.bookmarkDemand(demand.getUuid()).enqueue();
//            });
        }
    }
}
