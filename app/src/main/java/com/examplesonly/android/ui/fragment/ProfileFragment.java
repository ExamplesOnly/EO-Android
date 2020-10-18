package com.examplesonly.android.ui.fragment;

import android.graphics.Outline;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.examplesonly.android.adapter.HomeAdapter;
import com.examplesonly.android.databinding.FragmentProfileBinding;
import com.examplesonly.android.model.Example;
import com.examplesonly.android.model.User;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    private HomeAdapter mHomeAdapter;
    private List<Example> mExampleListList = new ArrayList<>();

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        init();
        setDummyData();

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.exampleList.setLayoutManager(layoutManager);

        mHomeAdapter = new HomeAdapter(mExampleListList, getContext());
        binding.exampleList.setAdapter(mHomeAdapter);
    }

    void setDummyData() {
        mExampleListList.add(new Example("123", "a.com",
                "https://images.pexels.com/photos/3184460/pexels-photo-3184460.jpeg?cs=srgb&dl=pexels-fauxels-3184460.jpg&fm=jpg",
                "Smart IOT home automation project with ESP8266 and Arduino",
                new User().setProfilePhoto(
                        "https://media-exp1.licdn.com/dms/image/C4E03AQGLcf5Ji62pXw/profile-displayphoto-shrink_200_200/0?e=1608163200&v=beta&t=2RAFKzeve2uooUiQQDWg7ZscS6lFkPJRgmEBf56M3q4")));

        mExampleListList.add(new Example("123", "a.com",
                "https://images.pexels.com/photos/193349/pexels-photo-193349.jpeg?cs=srgb&dl=pexels-markus-spiske-193349.jpg&fm=jpg",
                "Parsing float as integer in Python using Raspberry Pi.",
                new User().setProfilePhoto(
                        "https://media-exp1.licdn.com/dms/image/C4E03AQGLcf5Ji62pXw/profile-displayphoto-shrink_200_200/0?e=1608163200&v=beta&t=2RAFKzeve2uooUiQQDWg7ZscS6lFkPJRgmEBf56M3q4")));

        mExampleListList.add(new Example("123", "a.com",
                "https://images.pexels.com/photos/3345882/pexels-photo-3345882.jpeg?cs=srgb&dl=pexels-ola-dapo-3345882.jpg&fm=jpg",
                "Creating a wake up word recognizer model using PyTorch.",
                new User().setProfilePhoto(
                        "https://media-exp1.licdn.com/dms/image/C4E03AQGLcf5Ji62pXw/profile-displayphoto-shrink_200_200/0?e=1608163200&v=beta&t=2RAFKzeve2uooUiQQDWg7ZscS6lFkPJRgmEBf56M3q4")));

        mExampleListList.add(new Example("123", "a.com",
                "https://images.pexels.com/photos/3568520/pexels-photo-3568520.jpeg?cs=srgb&dl=pexels-drew-williams-3568520.jpg&fm=jpg",
                "Creating a wake up word recognizer model using PyTorch.",
                new User().setProfilePhoto(
                        "https://media-exp1.licdn.com/dms/image/C4E03AQGLcf5Ji62pXw/profile-displayphoto-shrink_200_200/0?e=1608163200&v=beta&t=2RAFKzeve2uooUiQQDWg7ZscS6lFkPJRgmEBf56M3q4")));

        mExampleListList.add(new Example("123", "a.com",
                "https://images.pexels.com/photos/3861958/pexels-photo-3861958.jpeg?cs=srgb&dl=pexels-thisisengineering-3861958.jpg&fm=jpg",
                "Creating a wake up word recognizer model using PyTorch.",
                new User().setProfilePhoto(
                        "https://media-exp1.licdn.com/dms/image/C4E03AQGLcf5Ji62pXw/profile-displayphoto-shrink_200_200/0?e=1608163200&v=beta&t=2RAFKzeve2uooUiQQDWg7ZscS6lFkPJRgmEBf56M3q4")));

        mExampleListList.add(new Example("123", "a.com",
                "https://images.pexels.com/photos/3862634/pexels-photo-3862634.jpeg?cs=srgb&dl=pexels-thisisengineering-3862634.jpg&fm=jpg",
                "Creating a wake up word recognizer model using PyTorch.",
                new User().setProfilePhoto(
                        "https://media-exp1.licdn.com/dms/image/C4E03AQGLcf5Ji62pXw/profile-displayphoto-shrink_200_200/0?e=1608163200&v=beta&t=2RAFKzeve2uooUiQQDWg7ZscS6lFkPJRgmEBf56M3q4")));

        mExampleListList.add(new Example("123", "a.com",
                "https://images.pexels.com/photos/3862618/pexels-photo-3862618.jpeg?cs=srgb&dl=pexels-thisisengineering-3862618.jpg&fm=jpg",
                "Creating a wake up word recognizer model using PyTorch.",
                new User().setProfilePhoto(
                        "https://media-exp1.licdn.com/dms/image/C4E03AQGLcf5Ji62pXw/profile-displayphoto-shrink_200_200/0?e=1608163200&v=beta&t=2RAFKzeve2uooUiQQDWg7ZscS6lFkPJRgmEBf56M3q4")));

        mExampleListList.add(new Example("123", "a.com",
                "https://images.pexels.com/photos/3912474/pexels-photo-3912474.jpeg?cs=srgb&dl=pexels-thisisengineering-3912474.jpg&fm=jpg",
                "Creating a wake up word recognizer model using PyTorch.",
                new User().setProfilePhoto(
                        "https://media-exp1.licdn.com/dms/image/C4E03AQGLcf5Ji62pXw/profile-displayphoto-shrink_200_200/0?e=1608163200&v=beta&t=2RAFKzeve2uooUiQQDWg7ZscS6lFkPJRgmEBf56M3q4")));

        mHomeAdapter.notifyDataSetChanged();
    }

}