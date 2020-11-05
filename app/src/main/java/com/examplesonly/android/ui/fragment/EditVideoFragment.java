package com.examplesonly.android.ui.fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.examplesonly.android.R;
import com.examplesonly.android.adapter.CategoryListAdapter;
import com.examplesonly.android.databinding.FragmentEditVideoBinding;
import com.examplesonly.android.model.Category;
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

public class EditVideoFragment extends Fragment implements ThumbnailChooseListener {

    private final String TAG = EditVideoFragment.class.getCanonicalName();
    private static final String ARG_VIDEO_LOCATION = "video_location";
    private FragmentEditVideoBinding binding;
    private String video;
    private VideoInterface videoInterface;
    private CategoryInterface categoryInterface;
    private Bitmap thumbnail;
    private final ArrayList<Category> categoryList = new ArrayList<>();

    public static EditVideoFragment newInstance(String video) {
        EditVideoFragment fragment = new EditVideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VIDEO_LOCATION, video);
        fragment.setArguments(args);
        return fragment;
    }

    public EditVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoInterface = new Api(getContext()).getClient().create(VideoInterface.class);
        categoryInterface = new Api(getContext()).getClient().create(CategoryInterface.class);
        if (getArguments() != null) {
            video = getArguments().getString(ARG_VIDEO_LOCATION);

            MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
            metadataRetriever.setDataSource(video);
            String height = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String width = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String rotation = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);

            Log.e(TAG, "Location -> " + video + " : Height Width -> " + height + " " + width + " : rotation -> "
                    + rotation);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentEditVideoBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        binding.topAppBar.setNavigationOnClickListener(view1 -> {
            Objects.requireNonNull(getActivity()).onBackPressed();
        });

        binding.topAppBar.setOnMenuItemClickListener(menu -> {
            if (menu.getItemId() == R.id.next) {

                if (Objects.requireNonNull(binding.videoTitleTxt.getText()).toString().length() == 0) {
                    Toast.makeText(getContext(), "Add a title", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (binding.categoryTxt.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Select a category", Toast.LENGTH_SHORT).show();
                    return true;
                }

                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.create();
                progressDialog.setProgress(0);
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                uploadVideo(new File(video), progressDialog);
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

//        String[] genders = {"Male", "Female", "Other", "Prefer not to say"};
//        binding.categoryTxt.setAdapter(
//                new ArrayAdapter(Objects.requireNonNull(getContext()), R.layout.view_dropdown_list_item, genders));

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

    void uploadVideo(File imageBytes, ProgressDialog progressDialog) {
        String title = Objects.requireNonNull(binding.videoTitleTxt.getText()).toString();
        String description = Objects.requireNonNull(binding.videoDescTxt.getText()).toString();
        int currentCategory = getCategoryIdFromTitle(binding.categoryTxt.getText().toString());
        String categoryValue = buildCategoryRequest(new String[]{String.valueOf(currentCategory)});

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(imageBytes.getPath());

        String metaHeight = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String metaWidth = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String metaDuration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String metaRotation = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        int rotation = metaRotation == null ? 0 : Integer.parseInt(metaRotation);

        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody categoryValueBody = RequestBody.create(MediaType.parse("text/plain"), categoryValue);
        RequestBody durationBody = RequestBody.create(MediaType.parse("text/plain"), metaDuration);
        RequestBody heightBody = RequestBody.create(MediaType.parse("text/plain"), metaHeight);
        RequestBody widthBody = RequestBody.create(MediaType.parse("text/plain"), metaWidth);
        RequestBody bodyFile = RequestBody.create(MediaType.parse("video/mp4"), imageBytes);
        RequestBody thumbFile = RequestBody
                .create(MediaType.parse("image/png"), MediaUtil.bitmapToFile(thumbnail, getContext()));

        if (rotation == 90 || rotation == 270) {
            RequestBody oldHeightBody = heightBody;
            heightBody = widthBody;
            widthBody = oldHeightBody;
        }

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageBytes.getName(), bodyFile);
        MultipartBody.Part thumb = MultipartBody.Part.createFormData("thumbnail", imageBytes.getName(), thumbFile);

        videoInterface
                .upload(titleBody, descriptionBody, categoryValueBody, durationBody,
                        heightBody, widthBody, body, thumb)
                .enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(@NotNull final Call<HashMap<String, String>> call,
                            @NotNull final Response<HashMap<String, String>> response) {

                        if (response.isSuccessful()) {
                            progressDialog.dismiss();
                            Log.e("EditVideo", "UPLOADED");
                            Objects.requireNonNull(getActivity()).finish();
                        } else {
                            progressDialog.dismiss();
                            try {
                                assert response.errorBody() != null;
                                Log.e("EditVideo", "UPLOAD ERROR: " + response.errorBody().string());
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
                        Log.e("EditVideo", "FAILED");
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