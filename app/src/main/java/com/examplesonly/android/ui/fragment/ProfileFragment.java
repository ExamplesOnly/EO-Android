package com.examplesonly.android.ui.fragment;

import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.examplesonly.android.R;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.adapter.HomeAdapter.VideoClickListener;
import com.examplesonly.android.adapter.ProfileVideosAdapter;
import com.examplesonly.android.databinding.FragmentProfileBinding;
import com.examplesonly.android.model.User;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.user.UserInterface;
import com.examplesonly.android.ui.activity.SettingsActivity;
import com.examplesonly.android.ui.view.CategoryGridItemDecoration;
import com.examplesonly.android.ui.view.ProfileGridDecoration;
import java.util.ArrayList;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    private ProfileVideosAdapter profileVideosAdapter;
    private UserDataProvider userDataProvider;
    private UserInterface mUserInterface;
    private ArrayList<Video> mExampleListList = new ArrayList<>();

    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInterface = new Api(getContext()).getClient().create(UserInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        init();
        updateProfile();
//        setDummyData();

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    void init() {
        binding.coverImage.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(final View view, final Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), (int) (view.getHeight() + 40F), 40F);
            }
        });

        binding.coverImage.setClipToOutline(true);
        binding.settings.setOnClickListener(view -> {
            Intent settings = new Intent(getActivity(), SettingsActivity.class);
            startActivity(settings);
        });

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        binding.exampleList.setLayoutManager(layoutManager);
        binding.exampleList.addItemDecoration(new ProfileGridDecoration(10, 2));

        profileVideosAdapter = new ProfileVideosAdapter(mExampleListList, getContext(),
                (VideoClickListener) getActivity());
        binding.exampleList.setAdapter(profileVideosAdapter);

        userDataProvider = new UserDataProvider(Objects.requireNonNull(getContext()));
    }

    void updateProfile() {
        binding.name.setText(getResources().getString(R.string.user_full_name, userDataProvider.getUserFirstName(),
                userDataProvider.getUserLastName()));

        mUserInterface.myVideos().enqueue(new Callback<ArrayList<Video>>() {
            @Override
            public void onResponse(final Call<ArrayList<Video>> call, final Response<ArrayList<Video>> response) {
                if (response.isSuccessful()) {
                    mExampleListList.addAll(response.body());
                    profileVideosAdapter.notifyDataSetChanged();
                    Log.e("PROFILE", "SUCCESS");
                } else {

                    Log.e("PROFILE", "ERROR");
                }
            }

            @Override
            public void onFailure(final Call<ArrayList<Video>> call, final Throwable t) {

                Log.e("PROFILE", "FAIL");
            }
        });
    }

//    void setDummyData() {
//        mExampleListList.add(new Example("123", "a.com",
//                "https://images.pexels.com/photos/3184460/pexels-photo-3184460.jpeg?cs=srgb&dl=pexels-fauxels-3184460.jpg&fm=jpg",
//                "Smart IOT home automation project with ESP8266 and Arduino",
//                new User().setProfilePhoto(
//                        "https://media-exp1.licdn.com/dms/image/C4E03AQGLcf5Ji62pXw/profile-displayphoto-shrink_200_200/0?e=1608163200&v=beta&t=2RAFKzeve2uooUiQQDWg7ZscS6lFkPJRgmEBf56M3q4")));
//
//        mExampleListList.add(new Example("123", "a.com",
//                "https://images.pexels.com/photos/193349/pexels-photo-193349.jpeg?cs=srgb&dl=pexels-markus-spiske-193349.jpg&fm=jpg",
//                "Parsing float as integer in Python using Raspberry Pi.",
//                new User().setProfilePhoto(
//                        "https://media-exp1.licdn.com/dms/image/C4E03AQGLcf5Ji62pXw/profile-displayphoto-shrink_200_200/0?e=1608163200&v=beta&t=2RAFKzeve2uooUiQQDWg7ZscS6lFkPJRgmEBf56M3q4")));
//
//        mExampleListList.add(new Example("123", "a.com",
//                "https://images.pexels.com/photos/3345882/pexels-photo-3345882.jpeg?cs=srgb&dl=pexels-ola-dapo-3345882.jpg&fm=jpg",
//                "Creating a wake up word recognizer model using PyTorch.",
//                new User().setProfilePhoto(
//                        "https://media-exp1.licdn.com/dms/image/C4E03AQGLcf5Ji62pXw/profile-displayphoto-shrink_200_200/0?e=1608163200&v=beta&t=2RAFKzeve2uooUiQQDWg7ZscS6lFkPJRgmEBf56M3q4")));
//
//        mExampleListList.add(new Example("123", "a.com",
//                "https://images.pexels.com/photos/3568520/pexels-photo-3568520.jpeg?cs=srgb&dl=pexels-drew-williams-3568520.jpg&fm=jpg",
//                "Creating a wake up word recognizer model using PyTorch.",
//                new User().setProfilePhoto(
//                        "https://media-exp1.licdn.com/dms/image/C4E03AQGLcf5Ji62pXw/profile-displayphoto-shrink_200_200/0?e=1608163200&v=beta&t=2RAFKzeve2uooUiQQDWg7ZscS6lFkPJRgmEBf56M3q4")));
//
//        mExampleListList.add(new Example("123", "a.com",
//                "https://images.pexels.com/photos/3861958/pexels-photo-3861958.jpeg?cs=srgb&dl=pexels-thisisengineering-3861958.jpg&fm=jpg",
//                "Creating a wake up word recognizer model using PyTorch.",
//                new User().setProfilePhoto(
//                        "https://media-exp1.licdn.com/dms/image/C4E03AQGLcf5Ji62pXw/profile-displayphoto-shrink_200_200/0?e=1608163200&v=beta&t=2RAFKzeve2uooUiQQDWg7ZscS6lFkPJRgmEBf56M3q4")));
//
//        mExampleListList.add(new Example("123", "a.com",
//                "https://images.pexels.com/photos/3862634/pexels-photo-3862634.jpeg?cs=srgb&dl=pexels-thisisengineering-3862634.jpg&fm=jpg",
//                "Creating a wake up word recognizer model using PyTorch.",
//                new User().setProfilePhoto(
//                        "https://media-exp1.licdn.com/dms/image/C4E03AQGLcf5Ji62pXw/profile-displayphoto-shrink_200_200/0?e=1608163200&v=beta&t=2RAFKzeve2uooUiQQDWg7ZscS6lFkPJRgmEBf56M3q4")));
//
//        mExampleListList.add(new Example("123", "a.com",
//                "https://images.pexels.com/photos/3862618/pexels-photo-3862618.jpeg?cs=srgb&dl=pexels-thisisengineering-3862618.jpg&fm=jpg",
//                "Creating a wake up word recognizer model using PyTorch.",
//                new User().setProfilePhoto(
//                        "https://media-exp1.licdn.com/dms/image/C4E03AQGLcf5Ji62pXw/profile-displayphoto-shrink_200_200/0?e=1608163200&v=beta&t=2RAFKzeve2uooUiQQDWg7ZscS6lFkPJRgmEBf56M3q4")));
//
//        mExampleListList.add(new Example("123", "a.com",
//                "https://images.pexels.com/photos/3912474/pexels-photo-3912474.jpeg?cs=srgb&dl=pexels-thisisengineering-3912474.jpg&fm=jpg",
//                "Creating a wake up word recognizer model using PyTorch.",
//                new User().setProfilePhoto(
//                        "https://media-exp1.licdn.com/dms/image/C4E03AQGLcf5Ji62pXw/profile-displayphoto-shrink_200_200/0?e=1608163200&v=beta&t=2RAFKzeve2uooUiQQDWg7ZscS6lFkPJRgmEBf56M3q4")));
//
//    }

}