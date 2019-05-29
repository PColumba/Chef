package com.example.chef.search_result_detailed;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chef.R;
import com.example.chef.Recipe;
import com.example.chef.search_result.RecipesAdapter;
import com.example.chef.user.UserInfoActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeDetailsActivity extends AppCompatActivity {

    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mRecipe = getIntent().getParcelableExtra(RecipesAdapter.RECIPE_DETAILS);
        setSupportActionBar(toolbar);
        loadRecipe();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate only if user is logged in
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_user_info) {
            if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                displayToast("You are not logged in!");
                return true;
            }
            else{
                Intent intent = new Intent(this, UserInfoActivity.class);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void addToFavorites(View v){

        List<Recipe> recipesList = new ArrayList<>();
        recipesList.add(mRecipe);
        Map<String,List<Recipe>> recipesMap = new HashMap<>();
        recipesMap.put("recipes",recipesList);

        //check if user is signed in.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null) {
            displayToast("Please sign in to add recipes");
            return;
        }
        //get doc snapshot and update it
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot ds = task.getResult();
                if(ds != null && ds.exists()){
                    //update doc
                    docRef.update("recipes", FieldValue.arrayUnion(mRecipe)).addOnCompleteListener(updateTask -> {
                        if(task.isSuccessful())
                            displayToast("Added to favorites");
                        else
                            displayToast(updateTask.getException().getMessage());
                    });
                }
                else{
                    //create doc with uid as name
                    docRef.set(recipesMap).addOnCompleteListener(creationTask -> {
                        if(task.isSuccessful())
                            displayToast("Added to favorites");
                        else
                            displayToast(creationTask.getException().getMessage());
                    });
                }
            }
            else
                displayToast(task.getException().getMessage());
        });
    }

    public void displayToast(String message){
        Toast.makeText(this, message,Toast.LENGTH_LONG).show();
    }

    private void loadRecipe(){

        ImageView recipeDetailsImageView;
        TextView recipeDetailsLabelView;
        TextView recipeDetailsIngredientsView;
        TextView recipeDetailsPreparationView;

        recipeDetailsImageView = findViewById(R.id.recipe_details_image);
        recipeDetailsLabelView = findViewById(R.id.recipe_details_label);
        recipeDetailsIngredientsView = findViewById(R.id.recipe_details_ingredients);
        recipeDetailsPreparationView = findViewById(R.id.recipe_details_preparation);


        Glide.with(this)
                .load(mRecipe.getImageURL())
                .into(recipeDetailsImageView);
        recipeDetailsLabelView.setText(mRecipe.getLabel());
        recipeDetailsIngredientsView.setText(mRecipe.ingredientsListToString());
        recipeDetailsPreparationView.setText(getString(R.string.preparation_details_disclaimer) + mRecipe.getSourceURL());

    }
}
