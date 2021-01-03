package com.examplesonly.android.adapter;

import static com.examplesonly.android.ui.activity.MainActivity.INDEX_DEMAND_DETAILS;
import static com.examplesonly.android.ui.activity.MainActivity.OPTION_CHOOSE_VIDEO_EOD;
import static com.examplesonly.android.ui.activity.MainActivity.OPTION_RECORD_VIDEO_EOD;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.examplesonly.android.R;
import com.examplesonly.android.component.BottomSheetOptionsDialog;
import com.examplesonly.android.databinding.ViewDemandItemBinding;
import com.examplesonly.android.handler.FragmentChangeListener;
import com.examplesonly.android.model.BottomSheetOption;
import com.examplesonly.android.model.Demand;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.demand.DemandInterface;
import com.examplesonly.gallerypicker.utils.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

            if (demand.getCreatedAt() != null)
                binding.time.setText(new DateUtil()
                        .getPrettyDateString(StringToDate(demand.getCreatedAt()).getTime()));

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


    static public Date StringToDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
