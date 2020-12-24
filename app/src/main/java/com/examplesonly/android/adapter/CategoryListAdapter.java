package com.examplesonly.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.examplesonly.android.R;
import com.examplesonly.android.model.Category;
import java.util.List;

public class CategoryListAdapter extends ArrayAdapter implements Filterable {

    private List<Category> categories;
    private Context context;
    private ArrayFilter mFilter;

    public CategoryListAdapter(@NonNull final Context context, final int resource,
            @NonNull final List<Category> categories) {
        super(context, resource, categories);

        this.categories = categories;
        this.context = context;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view;
        view = inflater.inflate(R.layout.view_dropdown_list_item, null);

        TextView textView = view.findViewById(R.id.title);
        textView.setText(categories.get(position).getTitle());

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;

    }

    private static class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(final CharSequence charSequence) {
            return null;
        }

        @Override
        protected void publishResults(final CharSequence charSequence, final FilterResults filterResults) {

        }

        @Override
        public CharSequence convertResultToString(final Object resultValue) {
            Category category = (Category) resultValue;
            return category.getTitle();
        }

    }
}
