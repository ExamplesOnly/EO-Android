package com.solvevolve.examplesonly.ui.activity;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.solvevolve.examplesonly.ui.activity.NewEoActivity.ARG_LAUNCH_MODE;
import static com.solvevolve.examplesonly.ui.activity.ProfileImageActivity.ARG_CROP_RATIO;
import static com.solvevolve.examplesonly.ui.activity.ProfileImageActivity.FRAGMENT_CAMERA;
import static com.solvevolve.examplesonly.ui.activity.ProfileImageActivity.FRAGMENT_CHOOSE_IMAGE;
import static com.yalantis.ucrop.UCrop.EXTRA_OUTPUT_URI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.solvevolve.examplesonly.R;
import com.solvevolve.examplesonly.account.UserDataProvider;
import com.solvevolve.examplesonly.component.BottomSheetOptionsDialog.BottomSheetOptionChooseListener;
import com.solvevolve.examplesonly.databinding.ActivityEditProfileBinding;
import com.solvevolve.examplesonly.model.Category;
import com.solvevolve.examplesonly.model.User;
import com.solvevolve.examplesonly.network.Api;
import com.solvevolve.examplesonly.network.user.UserInterface;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.solvevolve.examplesonly.ui.fragment.AddDemandFragment;
import com.solvevolve.examplesonly.ui.fragment.CameraFragment;
import com.solvevolve.examplesonly.ui.settings.ChangePasswordFragment;
import com.solvevolve.examplesonly.ui.settings.EditProfileFragment;
import com.solvevolve.gallerypicker.view.VideosFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class EditProfileActivity extends AppCompatActivity implements BottomSheetOptionChooseListener {

    private final int OPTION_CHOOSE_PHOTO = 101;
    private final int OPTION_CLICK_PHOTO = 102;
    private final int OPTION_CHOOSE_PHOTO_COVER = 103;
    private final int OPTION_CLICK_PHOTO_COVER = 104;

    private final int GET_PROFILE_IMAGE = 111;
    private final int GET_COVER_IMAGE = 121;

    public static final int FRAGMENT_EDIT_PROFILE = 121;
    public static final int FRAGMENT_CHANGE_PASSWORD = 122;

    private ActivityEditProfileBinding binding;
    private UserDataProvider userDataProvider;
    private UserInterface userInterface;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.topAppBar);
        binding.topAppBar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });


        userInterface = new Api(this).getClient().create(UserInterface.class);
        userDataProvider = UserDataProvider.getInstance(this);

        switchFragment(FRAGMENT_EDIT_PROFILE, "choose_video", null);

    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
//        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        return super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public void onBottomSheetOptionChosen(final int index, final int id, final Object data) {
        Intent profileImage = new Intent(EditProfileActivity.this, ProfileImageActivity.class);
        Bundle dataBundle = new Bundle();

        switch (id) {
            case OPTION_CHOOSE_PHOTO:
                dataBundle.putInt(ARG_LAUNCH_MODE, FRAGMENT_CHOOSE_IMAGE);
                dataBundle.putIntArray(ARG_CROP_RATIO, new int[]{1, 1});
                profileImage.putExtras(dataBundle);
                startActivityForResult(profileImage, GET_PROFILE_IMAGE);
                break;
            case OPTION_CLICK_PHOTO:
                dataBundle.putInt(ARG_LAUNCH_MODE, FRAGMENT_CAMERA);
                dataBundle.putIntArray(ARG_CROP_RATIO, new int[]{1, 1});
                profileImage.putExtras(dataBundle);
                startActivityForResult(profileImage, GET_PROFILE_IMAGE);
                break;
            case OPTION_CHOOSE_PHOTO_COVER:
                dataBundle.putInt(ARG_LAUNCH_MODE, FRAGMENT_CHOOSE_IMAGE);
                dataBundle.putIntArray(ARG_CROP_RATIO, new int[]{16, 6});
                profileImage.putExtras(dataBundle);
                startActivityForResult(profileImage, GET_COVER_IMAGE);
                break;
            case OPTION_CLICK_PHOTO_COVER:
                dataBundle.putInt(ARG_LAUNCH_MODE, FRAGMENT_CAMERA);
                dataBundle.putIntArray(ARG_CROP_RATIO, new int[]{16, 6});
                profileImage.putExtras(dataBundle);
                startActivityForResult(profileImage, GET_COVER_IMAGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {

        if (resultCode != RESULT_CANCELED) {
            if (requestCode == GET_PROFILE_IMAGE && data != null) {
                Uri profileImageUri = data.getParcelableExtra(EXTRA_OUTPUT_URI);
                Timber.e("onActivityResult %s", profileImageUri);
                uploadProfileImage(profileImageUri, GET_PROFILE_IMAGE);
                finish();
            } else if (requestCode == GET_COVER_IMAGE && data != null) {
                Uri coverImageUri = data.getParcelableExtra(EXTRA_OUTPUT_URI);
                Timber.e("onActivityResult %s", coverImageUri);
                uploadProfileImage(coverImageUri, GET_COVER_IMAGE);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root);
        if (fragment instanceof EditProfileFragment) {
            finish();
        }

        super.onBackPressed();
    }

//    @Override
//    public boolean onPrepareOptionsMenu(final Menu menu) {
//        Timber.e("onPrepareOptionsMenu %s", isDataChanged);
//        SpannableString s = new SpannableString("SAVE");
//        if (isDataChanged) {
//            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary, getTheme()))
//                    , 0, s.length(), 0);
//
//            menu.getItem(0).setTitle(s);
//            menu.getItem(0).setEnabled(true);
//        } else {
//            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.md_grey_700, getTheme()))
//                    , 0, s.length(), 0);
//
//            menu.getItem(0).setTitle(s);
//            menu.getItem(0).setEnabled(false);
//        }
//        return true;
//    }


//    void init() {
//
//        setSupportActionBar(binding.topAppBar);
//        binding.topAppBar.setNavigationOnClickListener(listener -> {
//            if (isDataChanged) {
//                new MaterialAlertDialogBuilder(this)
//                        .setCancelable(false)
//                        .setTitle("Unsaved Changes")
//                        .setMessage("You have unsaved changes. Are you sure you want to cancel?")
//                        .setNeutralButton("Cancel", null)
//                        .setNegativeButton("Discard", (dialogInterface, i) -> finish())
////                        .setPositiveButton("Save", (dialogInterface, i) -> updateAccount())
//                        .show();
//            } else {
//                finish();
//            }
//        });
//
//        userInterface.getInterest().enqueue(new Callback<ArrayList<Category>>() {
//            @Override
//            public void onResponse(final Call<ArrayList<Category>> call,
//                                   final Response<ArrayList<Category>> response) {
//                if (response.isSuccessful()) {
//
//                    Timber.e("getInterest");
//                    ArrayList<Category> interestList = response.body();
//
//                    if (interestList.size() > 0) {
//                        for (int i = 0; i < interestList.size(); i++) {
//                            Timber.e(interestList.get(i).getTitle());
//
//                            Chip chip = new Chip(EditProfileActivity.this);
//                            chip.setText(interestList.get(i).getTitle());
////                            binding.categoryChipGroup.addView(chip);
//                        }
//                    }
//
//                    Chip editChip = new Chip(EditProfileActivity.this);
//                    editChip.setChipBackgroundColorResource(R.color.colorPrimary);
//                    editChip.setTextColor(getResources().getColor(R.color.white, getTheme()));
//                    editChip.setText("Edit Interests");
//                    editChip.setCheckable(true);
//                    editChip.setChecked(true);
//                    editChip.setCheckedIcon(getDrawable(R.drawable.ic_add_mono));
//                    editChip.setOnClickListener(view -> {
//                        Intent editInterest = new Intent(EditProfileActivity.this, ChooseCategoryActivity.class);
//                        startActivity(editInterest);
//                    });
////                    binding.categoryChipGroup.addView(editChip);
////                    Paris.style(editChip).apply(R.style.Widget_MaterialComponents_Chip_Choice);
//
//                } else {
//                    try {
//                        Timber.e("getInterest ERROR %s", response.errorBody().string());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(final Call<ArrayList<Category>> call, final Throwable t) {
//
//                Timber.e("getInterest onFailure");
//            }
//        });
//
//    }

    public void setFragment(Fragment frag) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.root, frag).commit();
    }

    public void switchFragment(int step, String tag, String data) {
        Fragment fragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragment = getSupportFragmentManager().findFragmentByTag(tag);

        switch (step) {
            case FRAGMENT_EDIT_PROFILE:
                if (fragment == null) {
                    fragment = new EditProfileFragment();
                }
                break;
            case FRAGMENT_CHANGE_PASSWORD:
                if (fragment == null) {
                    fragment = new ChangePasswordFragment();
                }
                break;

        }

        fragmentTransaction.replace(R.id.root, fragment, tag);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    void uploadProfileImage(@NotNull Uri image, @NotNull int imageType) {
        progressDialog = new ProgressDialog(this);
        progressDialog.create();
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        File imageFile = new File(Objects.requireNonNull(image.getPath()));
        RequestBody imageBody = RequestBody
                .create(MediaType.parse("image/png"), imageFile);
        MultipartBody.Part profileImageBody = MultipartBody.Part
                .createFormData("file", imageFile.getName(), imageBody);

        switch (imageType) {
            case GET_PROFILE_IMAGE:
                progressDialog.setMessage("Uploading profile photo...");
                progressDialog.show();
                userInterface.updateProfileImage(profileImageBody).enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(final @NotNull Call<HashMap<String, String>> call,
                                           final @NotNull Response<HashMap<String, String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            User currUser = userDataProvider.getCurrentUser();
                            currUser.setProfilePhoto(response.body().get("url"));
                            userDataProvider.saveUserData(currUser);
                            Toast.makeText(EditProfileActivity.this, "Profile photo updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Failed to update profile photo", Toast.LENGTH_SHORT).show();
                        }
//                        progressDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onFailure(final @NotNull Call<HashMap<String, String>> call,
                                          final @NotNull Throwable t) {

                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Failed to update profile photo", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                break;
            case GET_COVER_IMAGE:
                progressDialog.setMessage("Uploading cover photo...");
                progressDialog.show();
                userInterface.updateCoverImage(profileImageBody).enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(final @NotNull Call<HashMap<String, String>> call,
                                           final @NotNull Response<HashMap<String, String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            User currUser = userDataProvider.getCurrentUser();
                            currUser.setCoverPhoto(response.body().get("url"));
                            userDataProvider.saveUserData(currUser);

                            Toast.makeText(EditProfileActivity.this, "Cover photo updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Failed to update cover photo", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onFailure(final @NotNull Call<HashMap<String, String>> call,
                                          final @NotNull Throwable t) {

                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Failed to update cover photo", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                break;
        }

    }
}