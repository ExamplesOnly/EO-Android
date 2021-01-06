package com.examplesonly.android.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.examplesonly.android.databinding.FragmentBottomSheetDialogBinding;
import com.examplesonly.android.model.BottomSheetOption;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

public class BottomSheetOptionsDialog extends BottomSheetDialogFragment {

    private FragmentBottomSheetDialogBinding binding;
    private String title;
    private final ArrayList<BottomSheetOption> optionList = new ArrayList<>();

    private BottomSheetOptionChooseListener optionChooseListener;

    public BottomSheetOptionsDialog() {
        // Required empty public constructor
    }

    public BottomSheetOptionsDialog(String title, @NotNull ArrayList<BottomSheetOption> optionList) {
        this.title = title;
        this.optionList.addAll(optionList);

        if (optionList.size() < 2) {
            throw new RuntimeException("Option List must have at least two options");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (optionChooseListener == null || !(getActivity() instanceof BottomSheetOptionChooseListener)) {
            try {
                throw new Exception("Either set BottomSheetOptionChooseListener or parent activity must listen to BottomSheetOptionChooseListener");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentBottomSheetDialogBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        binding.title.setText(title);
        binding.firstIcon.setImageDrawable(optionList.get(0).getDrawable());
        binding.firstTitle.setText(optionList.get(0).getTitle());
        binding.secondIcon.setImageDrawable(optionList.get(1).getDrawable());
        binding.secondTitle.setText(optionList.get(1).getTitle());

        if (optionList.size() < 3) {
            binding.thirdCard.setVisibility(View.GONE);
        } else {
            binding.thirdIcon.setImageDrawable(optionList.get(2).getDrawable());
            binding.thirdTitle.setText(optionList.get(2).getTitle());
        }

        if (title == null) {
            binding.titleContainer.setVisibility(View.GONE);
        }

        if (optionChooseListener == null)
            optionChooseListener = (BottomSheetOptionChooseListener) getActivity();

        binding.firstCard
                .setOnClickListener(view1 -> {
                    optionChooseListener
                            .onBottomSheetOptionChosen(0, optionList.get(0).getId(), optionList.get(0).getData());
                    dismiss();
                });
        binding.secondCard
                .setOnClickListener(view1 -> {
                    optionChooseListener
                            .onBottomSheetOptionChosen(1, optionList.get(1).getId(), optionList.get(1).getData());
                    dismiss();
                });
        binding.thirdCard
                .setOnClickListener(view1 -> {
                    optionChooseListener
                            .onBottomSheetOptionChosen(2, optionList.get(2).getId(), optionList.get(2).getData());
                    dismiss();
                });

        init();

        return view;
    }

    void init() {
        binding.close.setOnClickListener(view -> dismiss());
    }

    public void setTitle(String title) {
        binding.title.setText(title);
    }

    public BottomSheetOptionsDialog setOptionChooseListener(BottomSheetOptionChooseListener optionChooseListener) {
        this.optionChooseListener = optionChooseListener;
        return this;
    }

    public interface BottomSheetOptionChooseListener {

        void onBottomSheetOptionChosen(int index, int id, Object data);
    }
}