package com.examplesonly.android.ui.settings;

import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.examplesonly.android.R;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.component.BottomSheetOptionsDialog;
import com.examplesonly.android.databinding.FragmentEditProfileBinding;
import com.examplesonly.android.model.BottomSheetOption;
import com.examplesonly.android.model.User;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.user.UserInterface;
import com.examplesonly.android.ui.activity.EditProfileActivity;
import com.examplesonly.android.ui.activity.ProfileImageActivity;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.examplesonly.android.ui.activity.EditProfileActivity.FRAGMENT_CHANGE_PASSWORD;
import static com.examplesonly.android.ui.activity.NewEoActivity.ARG_LAUNCH_MODE;
import static com.examplesonly.android.ui.activity.ProfileImageActivity.ARG_CROP_RATIO;
import static com.examplesonly.android.ui.activity.ProfileImageActivity.FRAGMENT_CAMERA;
import static com.examplesonly.android.ui.activity.ProfileImageActivity.FRAGMENT_CHOOSE_IMAGE;

public class EditProfileFragment extends Fragment implements BottomSheetOptionsDialog.BottomSheetOptionChooseListener {


    private final int OPTION_CHOOSE_PHOTO = 101;
    private final int OPTION_CLICK_PHOTO = 102;
    private final int OPTION_CHOOSE_PHOTO_COVER = 103;
    private final int OPTION_CLICK_PHOTO_COVER = 104;

    private final int GET_PROFILE_IMAGE = 111;
    private final int GET_COVER_IMAGE = 121;

    private FragmentEditProfileBinding binding;
    private UserDataProvider userDataProvider;
    private UserInterface userInterface;
    private MenuItem saveBtn;

    private String userFirstName = "";
    private String userLastName = "";
    private String userBio = "";
    //    private String userInterests = "";
    private boolean isDataChanged = false;

    DrawableCrossFadeFactory factory =
            new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();


    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);

        setHasOptionsMenu(true);
        init();

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.save_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        SpannableString s = new SpannableString("SAVE");
        if (isDataChanged) {
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary, getActivity().getTheme()))
                    , 0, s.length(), 0);

            menu.getItem(0).setTitle(s);
            menu.getItem(0).setEnabled(true);
        } else {
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.md_grey_700, getActivity().getTheme()))
                    , 0, s.length(), 0);

            menu.getItem(0).setTitle(s);
            menu.getItem(0).setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            updateAccount();
        }
        return true;
    }

    @Override
    public void onBottomSheetOptionChosen(int index, int id, Object data) {
        Intent profileImage = new Intent(getActivity(), ProfileImageActivity.class);
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

    void init() {
        userInterface = new Api(getContext()).getClient().create(UserInterface.class);
        userDataProvider = UserDataProvider.getInstance(getContext());

        ((EditProfileActivity) getActivity()).getSupportActionBar().setTitle("Edit Profile");

        binding.coverImage.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(final View view, final Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), (int) (view.getHeight() + 40F), 40F);
            }
        });
        binding.coverImage.setClipToOutline(true);

        binding.editDpBtn.setOnClickListener(view -> {
            ArrayList<BottomSheetOption> optionList = new ArrayList<>();
            optionList.add(new BottomSheetOption(OPTION_CHOOSE_PHOTO, "Choose Profile Photo",
                    ContextCompat.getDrawable(getContext(), R.drawable.ic_upload_mono)));
            optionList.add(new BottomSheetOption(OPTION_CLICK_PHOTO, "Take new Profile Photo",
                    ContextCompat.getDrawable(getContext(), R.drawable.ic_camera_mono)));
            BottomSheetOptionsDialog bottomSheet = new BottomSheetOptionsDialog(null, optionList);
            bottomSheet.show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");
        });

        binding.editCoverBtn.setOnClickListener(view -> {
            ArrayList<BottomSheetOption> optionList = new ArrayList<>();
            optionList.add(new BottomSheetOption(OPTION_CHOOSE_PHOTO_COVER, "Choose Cover Photo",
                    ContextCompat.getDrawable(getContext(), R.drawable.ic_upload_mono)));
            optionList.add(new BottomSheetOption(OPTION_CLICK_PHOTO_COVER, "Take new Cover Photo",
                    ContextCompat.getDrawable(getContext(), R.drawable.ic_camera_mono)));
            BottomSheetOptionsDialog bottomSheet = new BottomSheetOptionsDialog(null, optionList);
            bottomSheet.show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");
        });

        binding.changePass.setOnClickListener(v -> {
            ((EditProfileActivity) getActivity()).switchFragment(FRAGMENT_CHANGE_PASSWORD, "change_pass", null);
        });

        updateUserProfile();
        setupTextBox();
    }

    void updateUserProfile() {
        Glide.with(this)
                .load(userDataProvider.getUserProfileImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.color.md_grey_400)
                .transition(withCrossFade(factory))
                .into(binding.profileImage);

        Glide.with(this)
                .load(userDataProvider.getUserCoverImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.color.md_grey_200)
                .transition(withCrossFade(factory))
                .into(binding.coverImage);

        userFirstName = userDataProvider.getUserFirstName();
        userLastName = userDataProvider.getUserLastName();
        userBio = userDataProvider.getUserBio();

        binding.firstNameTxt.setText(userFirstName);
        binding.bioTxt.setText(userBio);
    }

    void setupTextBox() {
        binding.firstNameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
                userFirstName = charSequence.toString();
                verifyChanges();
            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });

        binding.bioTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
                userBio = charSequence.toString();
                verifyChanges();
            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });
    }

    void updateAccount() {
//        binding.progress.setIndeterminate(true);
//        binding.progress.setVisibility(View.VISIBLE);
//        binding.progress.show();
        userInterface.updateUser(
                new User()
                        .setFirstName(userFirstName)
                        .setLastName(userLastName)
                        .setBio(userBio))
                .enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(final Call<HashMap<String, String>> call, final Response<HashMap<String, String>> response) {
                        if (response.isSuccessful()) {
                            User currUser = userDataProvider.getCurrentUser();
                            currUser.setFirstName(userFirstName);
                            currUser.setLastName(userLastName);
                            currUser.setBio(userBio);
                            userDataProvider.saveUserData(currUser);
//                            binding.progress.hide();
                            getActivity().finish();
                            Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getContext(), "Profile Update failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(final Call<HashMap<String, String>> call, final Throwable t) {
//                        binding.progress.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Profile Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void verifyChanges() {
        if (!userFirstName.equals(userDataProvider.getUserFirstName())
                || !userBio.equals(userDataProvider.getUserBio())) {
            isDataChanged = true;
        } else {
            isDataChanged = false;
        }
        getActivity().invalidateOptionsMenu();
    }

}