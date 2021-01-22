package com.examplesonly.android.ui.fragment;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.examplesonly.android.R;
import com.examplesonly.android.adapter.CategoryListAdapter;
import com.examplesonly.android.component.EoAlertDialog.EoAlertDialog;
import com.examplesonly.android.databinding.FragmentPostVideoBinding;
import com.examplesonly.android.model.Category;
import com.examplesonly.android.model.Demand;
import com.examplesonly.android.model.VideoUploadData;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.category.CategoryInterface;
import com.examplesonly.android.network.video.VideoInterface;
import com.examplesonly.android.network.video.VideoUploadService;
import com.examplesonly.android.ui.activity.LaunchActivity;
import com.examplesonly.android.ui.activity.MainActivity;
import com.examplesonly.android.ui.activity.NewEoActivity.ThumbnailChooseListener;
import com.examplesonly.android.util.MediaUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import org.buffer.android.thumby.ThumbyActivity;
import org.buffer.android.thumby.util.ThumbyUtils;
import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class PostVideoFragment extends Fragment implements ThumbnailChooseListener {

    private static final String ARG_VIDEO_LOCATION = "video_location";
    private static final String ARG_VIDEO_DEMAND = "video_demand";
    private FragmentPostVideoBinding binding;
    private String video;
    private Demand demand;
    private VideoInterface videoInterface;
    private CategoryInterface categoryInterface;
    private Bitmap thumbnail;
    private final ArrayList<Category> categoryList = new ArrayList<>();

    public static PostVideoFragment newInstance(String video, Demand demand) {
        PostVideoFragment fragment = new PostVideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VIDEO_LOCATION, video);
        args.putParcelable(ARG_VIDEO_DEMAND, demand);
        fragment.setArguments(args);
        return fragment;
    }

    public PostVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoInterface = new Api(getContext()).getClient().create(VideoInterface.class);
        categoryInterface = new Api(getContext()).getClient().create(CategoryInterface.class);
        if (getArguments() != null) {
            video = getArguments().getString(ARG_VIDEO_LOCATION);
            demand = getArguments().getParcelable(ARG_VIDEO_DEMAND);

        } else {

        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPostVideoBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        if (demand != null) {
            binding.eodPostContainer.setVisibility(View.VISIBLE);
            binding.newPostDataContainer.setVisibility(View.GONE);

            binding.demandTitle.setText(demand.getTitle());
            binding.demandCategory.setText(demand.getCategory().getTitle());
        }

        binding.topAppBar.setNavigationOnClickListener(view1 -> {
            requireActivity().onBackPressed();
        });

        binding.topAppBar.setOnMenuItemClickListener(menu -> {

            if (menu.getItemId() == R.id.publish) {

                if (demand == null) {
                    if (Objects.requireNonNull(binding.videoTitleTxt.getText()).toString().length() == 0) {
                        Toast.makeText(getContext(), "Add a title", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    if (binding.categoryTxt.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Select a category", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                uploadVideo(new File(video));
            }
            return true;
        });

        binding.thumbnailCard.setOnClickListener(v -> {
            startActivityForResult(ThumbyActivity
                    .startIntent(requireActivity(), Uri.fromFile(new File(video)), 0), 1010);
        });

        binding.categoryTxt.setEnabled(false);
        binding.categoryTextField.setEnabled(false);

        categoryInterface.getCategories().enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(final Call<ArrayList<Category>> call,
                                   final Response<ArrayList<Category>> response) {
                if (response.isSuccessful()) {

                    binding.categoryTxt.setEnabled(true);
                    binding.categoryTextField.setEnabled(true);

                    categoryList.addAll(response.body());
                    binding.categoryTxt.setAdapter(
                            new CategoryListAdapter(requireContext(),
                                    R.layout.view_dropdown_list_item,
                                    categoryList));

                }
            }

            @Override
            public void onFailure(final Call<ArrayList<Category>> call, final Throwable t) {

            }
        });
        if (!video.isEmpty()) {
            getVideoData(video);
        }

        return view;
    }

    @Override
    public void onThumbnailChosen(final long thumbPosition) {
        Bitmap bitmap = ThumbyUtils
                .getBitmapAtFrame(getContext(), Uri.fromFile(new File(video)), thumbPosition, -1, -1);
        thumbnail = bitmap;
        binding.thumbnail.setImageBitmap(thumbnail);
    }

    void getVideoData(String location) {
        Bitmap myBitmap = BitmapFactory.decodeFile(location);
        if (myBitmap == null) {
            myBitmap = ThumbnailUtils.createVideoThumbnail(location, Thumbnails.FULL_SCREEN_KIND);
            thumbnail = myBitmap;
            binding.thumbnail.setImageBitmap(thumbnail);
        }
    }

    void uploadVideo(File videoFile) {
        String title = Objects.requireNonNull(binding.videoTitleTxt.getText()).toString();
        String description = Objects.requireNonNull(binding.videoDescTxt.getText()).toString();
        int currentCategory = getCategoryIdFromTitle(binding.categoryTxt.getText().toString());
        String categoryValue = buildCategoryRequest(new String[]{String.valueOf(currentCategory)});

        Intent uploadIntent = new Intent(requireActivity(), VideoUploadService.class);
        uploadIntent.putExtra("videoData", new VideoUploadData()
                .setTitle(title).setDescription(description)
                .setCategoryValue(categoryValue)
                .setDemand(demand == null ? null : demand.getUuid())
                .setVideoFilePath(videoFile.getPath())
                .setThumbFilePath(MediaUtil.bitmapToFile(thumbnail, getContext()).getPath()));
        VideoUploadService.enqueueWork(requireActivity(), uploadIntent);

        EoAlertDialog uploadingDialog = new EoAlertDialog.Builder(getContext())
                .setDialogIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_upload, getActivity().getTheme()))
                .setIconTint(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getActivity().getTheme()))
                .setTitle("Uploading Example")
                .setDescription("We are processing your example. You can keep using ExamplesOnly till we process and upload.")
                .setCancelable(false)
                .setPositiveText("Got it!")
                .setPositiveClickListener(appCompatDialog -> {
                    appCompatDialog.dismiss();
                    getActivity().finish();
                })
                .create();

        uploadingDialog.show();
    }

    String buildCategoryRequest(String[] idList) {
        StringBuilder start = new StringBuilder("{ \"categories\": [");
        String end = "]}";

        for (int i = 0; i < idList.length; i++) {
            if (i == 0) {
                start.append("\"").append(idList[i]).append("\"");
            } else {
                start.append(",\"").append(idList[i]).append("\"");
            }
        }
        start.append(end);
        return start.toString();
    }

    int getCategoryIdFromTitle(String title) {
        int i = 0;
        int catId = -1;
        for (i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getTitle().equals(title)) {
                catId = categoryList.get(i).getId();
            }
        }
        return catId;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}