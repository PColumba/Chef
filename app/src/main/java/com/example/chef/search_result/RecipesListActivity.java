package com.example.chef.search_result;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chef.R;
import com.example.chef.Recipe;
import com.example.chef.search.RecipesSearchTask;
import com.example.chef.user.UserInfoActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class RecipesListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent() != null) {
            List<Recipe> mRecipesList = getIntent().getParcelableArrayListExtra(RecipesSearchTask.RECIPES_LIST);
            RecipesAdapter recipesAdapter = new RecipesAdapter(this, mRecipesList);
            RecyclerView recipesListView = findViewById(R.id.recipes_list);
            recipesListView.setLayoutManager(new LinearLayoutManager(this));
            recipesListView.setAdapter(recipesAdapter);
        }
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

    public void displayToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}
