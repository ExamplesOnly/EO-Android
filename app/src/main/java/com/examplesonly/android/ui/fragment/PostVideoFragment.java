package com.examplesonly.android.ui.fragment;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import android.app.ProgressDialog;
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
import androidx.fragment.app.Fragment;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.examplesonly.android.R;
import com.examplesonly.android.adapter.CategoryListAdapter;
import com.examplesonly.android.databinding.FragmentPostVideoBinding;
import com.examplesonly.android.model.Category;
import com.examplesonly.android.model.Demand;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.category.CategoryInterface;
import com.examplesonly.android.network.video.VideoInterface;
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

            MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
            metadataRetriever.setDataSource(video);
            String height = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String width = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String rotation = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
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
            Objects.requireNonNull(getActivity()).onBackPressed();
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

                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.create();
                progressDialog.setProgress(0);
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                processVideo(new File(video), progressDialog);
            }
            return true;
        });

        binding.thumbnailCard.setOnClickListener(v -> {
            startActivityForResult(ThumbyActivity
                    .startIntent(Objects.requireNonNull(getActivity()), Uri.fromFile(new File(video)), 0), 1010);
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
                            new CategoryListAdapter(Objects.requireNonNull(getContext()),
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

    void processVideo(File videoFile, ProgressDialog progressDialog) {

        progressDialog.setMessage("Optimizing video...");
        String convertedFile = Objects.requireNonNull(getContext()).getCacheDir().getPath() + "/" + System
                .currentTimeMillis() + ".mp4";
        String videoFileName = videoFile.getPath().replace(" ", "\\ ");

        Timber.e(videoFileName);
        Timber.e(convertedFile);

        FFmpeg.executeAsync(
                "-y -i \"" + videoFile.getPath() + "\" -c:v libx264 -crf 23 "
                        + "-preset ultrafast "
                        + "-x264-params opencl=true "
//                        + "-hwaccel auto "
                        + "-movflags +faststart "
//                        + "-me_method zero "
                        + "-tune fastdecode "
                        + "-tune zerolatency "
                        + convertedFile,
                (executionId1, returnCode) -> {
                    if (returnCode == RETURN_CODE_SUCCESS) {
                        uploadVideo(new File(convertedFile), progressDialog);
                        Timber.e("Async command execution completed successfully.");
                    } else if (returnCode == RETURN_CODE_CANCEL) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Failed to optimize video", Toast.LENGTH_SHORT).show();
                        Timber.e("Async command execution cancelled by user.");
                    } else {
                        Toast.makeText(getContext(), "Failed to optimize video", Toast.LENGTH_SHORT).show();
                        Timber.e("Async command execution failed with returnCode %d.",
                                returnCode);
                        progressDialog.dismiss();
                    }
                });
    }

    void uploadVideo(File videoFile, ProgressDialog progressDialog) {

        RequestBody titleBody = null, descriptionBody = null, categoryValueBody = null, demandBody = null;

        progressDialog.setMessage("Uploading video...");
        String title = Objects.requireNonNull(binding.videoTitleTxt.getText()).toString();
        String description = Objects.requireNonNull(binding.videoDescTxt.getText()).toString();
        int currentCategory = getCategoryIdFromTitle(binding.categoryTxt.getText().toString());
        String categoryValue = buildCategoryRequest(new String[]{String.valueOf(currentCategory)});

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(videoFile.getPath());

        String metaHeight = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String metaWidth = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String metaDuration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String metaRotation = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        int rotation = metaRotation == null ? 0 : Integer.parseInt(metaRotation);

        if (demand != null) {
            demandBody = RequestBody.create(MediaType.parse("text/plain"), demand.getUuid());
        } else {
            titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
            descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);
            categoryValueBody = RequestBody.create(MediaType.parse("text/plain"), categoryValue);
        }
        RequestBody durationBody = RequestBody.create(MediaType.parse("text/plain"), metaDuration);
        RequestBody heightBody = RequestBody.create(MediaType.parse("text/plain"), metaHeight);
        RequestBody widthBody = RequestBody.create(MediaType.parse("text/plain"), metaWidth);
        RequestBody bodyFile = RequestBody.create(MediaType.parse("video/mp4"), videoFile);
        RequestBody thumbFile = RequestBody
                .create(MediaType.parse("image/png"), MediaUtil.bitmapToFile(thumbnail, getContext()));

        if (rotation == 90 || rotation == 270) {
            RequestBody oldHeightBody = heightBody;
            heightBody = widthBody;
            widthBody = oldHeightBody;
        }

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", videoFile.getName(), bodyFile);
        MultipartBody.Part thumb = MultipartBody.Part.createFormData("thumbnail", videoFile.getName(), thumbFile);

        videoInterface
                .upload(titleBody, descriptionBody, categoryValueBody, durationBody,
                        heightBody, widthBody, demandBody, body, thumb)
                .enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(@NotNull final Call<HashMap<String, String>> call,
                            @NotNull final Response<HashMap<String, String>> response) {

                        if (response.isSuccessful()) {
                            progressDialog.dismiss();
                            Timber.e("UPLOADED");
                            Objects.requireNonNull(getActivity()).finish();
                        } else {
                            progressDialog.dismiss();
                            try {
                                assert response.errorBody() != null;
                                Timber.e("UPLOAD ERROR: %s", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull final Call<HashMap<String, String>> call,
                            @NotNull final Throwable t) {
                        t.printStackTrace();
                        progressDialog.dismiss();
                        Timber.e("FAILED");
                    }
                });
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