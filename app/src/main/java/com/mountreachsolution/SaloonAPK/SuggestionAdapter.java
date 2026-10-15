package com.mountreachsolution.SaloonAPK;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> {

    private final List<Suggestion> suggestions;

    public SuggestionAdapter(List<Suggestion> suggestions) {
        this.suggestions = suggestions;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        Suggestion suggestion = suggestions.get(position);
        holder.title.setText(suggestion.getTitle());
        holder.image.setImageResource(suggestion.getImageDrawableId());
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

        SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgSuggestion);
            title = itemView.findViewById(R.id.tvSuggestionTitle);
        }
    }
}