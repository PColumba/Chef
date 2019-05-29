package com.example.chef.user;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chef.R;
import com.example.chef.Recipe;

import com.example.chef.search_result.RecipesAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {

    private List<Recipe> mFavoriteRecipes;
    private RecipesAdapter mRecipesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        TextView userEmail = findViewById(R.id.user_email);

        if(currentUser != null)
            userEmail.setText(currentUser.getEmail());

        mFavoriteRecipes = new ArrayList<>();
        mRecipesAdapter = new RecipesAdapter(this, mFavoriteRecipes);
        RecyclerView recipesListView = findViewById(R.id.favorite_recipes_view);
        recipesListView.setLayoutManager(new LinearLayoutManager(this));
        recipesListView.setAdapter(mRecipesAdapter);

    }

    public void showFavorites(View v) {

        //do not add to already present recipes
        if (mFavoriteRecipes.size() != 0)
            return;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            displayToast("Please sign in to view recipes");
            return;
        }
        else {
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot ds = task.getResult();
                    if (ds != null && ds.exists()) {
                        try {
                            List<Recipe> recipesList = new ArrayList<>();
                            ArrayList<Object> recipesMaps = (ArrayList<Object>) ds.get("recipes");
                            for (int i = 0; i < recipesMaps.size(); i++) {
                                HashMap<String, Object> recipeMap = (HashMap<String, Object>) recipesMaps.get(i);
                                String label = (String) recipeMap.get("label");
                                String imageURL = (String) recipeMap.get("imageURL");
                                String sourceURL = (String) recipeMap.get("sourceURL");
                                List<String> ingredientsList = (List<String>) recipeMap.get("ingredientsList");
                                Recipe recipe = new Recipe(label, imageURL, sourceURL, ingredientsList);
                                recipesList.add(recipe);
                            }
                            mFavoriteRecipes.addAll(recipesList);
                            mRecipesAdapter.notifyDataSetChanged();
                        }
                        catch (ClassCastException e){
                            displayToast("Ups, something went wrong");
                        }
                    } else
                        displayToast("You have no favorite recipes");
                } else {
                    displayToast(task.getException().getMessage());
                }
            });
        }
    }

    public void displayToast(String message){
        Toast.makeText(this, message,Toast.LENGTH_LONG).show();
    }
}