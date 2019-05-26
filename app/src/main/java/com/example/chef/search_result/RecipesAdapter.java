package com.example.chef.search_result;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chef.R;
import com.example.chef.Recipe;
import com.example.chef.search_result_detailed.RecipeDetailsActivity;

import java.lang.ref.WeakReference;
import java.util.List;
import com.bumptech.glide.Glide;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private List<Recipe> mRecipes;
    private Context mContext;

    public static final String RECIPE_DETAILS = "com.example.chef.search_resutl.RecipesAdapter.lRecipe";

    RecipesAdapter(Context context, List<Recipe> recipes){
        this.mRecipes = recipes;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecipesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecipesAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.recipes_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesAdapter.ViewHolder viewHolder, int i) {
        viewHolder.bind(mRecipes.get(i));
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mRecipeImage;
        private TextView mRecipeLabel;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mRecipeImage = itemView.findViewById(R.id.recipe_image);
            mRecipeLabel = itemView.findViewById(R.id.recipe_label);
            itemView.setOnClickListener(this);
        }

        void bind(Recipe currentRecipe){
            mRecipeLabel.setText(currentRecipe.getLabel());
            Glide.with(mContext)
                    .load(currentRecipe.getImageURL())
                    .into(mRecipeImage);
        }


        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, RecipeDetailsActivity.class);
            intent.putExtra(RECIPE_DETAILS, mRecipes.get(getAdapterPosition()));
            mContext.startActivity(intent);
        }
    }




}