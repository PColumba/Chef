package com.example.chef.search;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chef.R;
import com.example.chef.user.UserInfoActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements TextView.OnEditorActionListener, AdapterView.OnItemSelectedListener {

    private List<String> mIngredients;
    private Spinner mPreparationTimeSpinner;
    private Spinner mRecipeCountSpinner;
    private Spinner mIngredientsCountSpinner;
    private RecipesSearchTask mRecipeSearchTask;
    private int mPreparationTime = 60;
    private int mRecipeCount = 5;
    private int mIngredientsCount = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        EditText ingredientsInputField = findViewById(R.id.ingredients_input_field);
        ingredientsInputField.setOnEditorActionListener(this);
        initSpinners();
        initRecycler();
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

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE) {
            mIngredients.add(v.getText().toString());
            v.setText(null);
        }
        return false;
    }

    public void searchForRecipes(View view) {
        if(mRecipeSearchTask == null)
            mRecipeSearchTask = (RecipesSearchTask) new RecipesSearchTask(getApplicationContext()).
                execute(new RecipesSearch(mIngredients,mRecipeCount,mPreparationTime,mIngredientsCount));
        else if(mRecipeSearchTask.getStatus() == AsyncTask.Status.FINISHED)
            mRecipeSearchTask = (RecipesSearchTask) new RecipesSearchTask(getApplicationContext()).
                    execute(new RecipesSearch(mIngredients,mRecipeCount,mPreparationTime,mIngredientsCount));
    }

    public void displayToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent == mRecipeCountSpinner)
            mRecipeCount = Integer.valueOf((String) parent.getAdapter().getItem(position));
        else if(parent == mPreparationTimeSpinner) {
            String value = (String) parent.getAdapter().getItem(position);
            if(value == getString(R.string.spinner_no_limt))
                mPreparationTime = getResources().getInteger(R.integer.spinner_no_limit);
            else
                mPreparationTime = Integer.valueOf((String) parent.getAdapter().getItem(position));
        }
        else if(parent == mIngredientsCountSpinner) {
            String value = (String) parent.getAdapter().getItem(position);
            if(value == getString(R.string.spinner_no_limt))
                mIngredientsCount = getResources().getInteger(R.integer.spinner_no_limit);
            else
                mIngredientsCount = Integer.valueOf((String) parent.getAdapter().getItem(position));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }

    private void initSpinners(){
        mRecipeCountSpinner = findViewById(R.id.recipeCount_spinner);
        mPreparationTimeSpinner = findViewById(R.id.preparationTime_spinner);
        mIngredientsCountSpinner = findViewById(R.id.ingredientsCount_spinner);
        ArrayAdapter<CharSequence> recipeCountSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.recipeCount_spinner_data, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> preparationTimeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.preparationTime_spinner_data, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> mIngredientsCountSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.ingredientsCount_spinner_data, android.R.layout.simple_spinner_item);
        mRecipeCountSpinner.setAdapter(recipeCountSpinnerAdapter);
        mPreparationTimeSpinner.setAdapter(preparationTimeSpinnerAdapter);
        mIngredientsCountSpinner.setAdapter(mIngredientsCountSpinnerAdapter);
        mRecipeCountSpinner.setOnItemSelectedListener(this);
        mPreparationTimeSpinner.setOnItemSelectedListener(this);
        mIngredientsCountSpinner.setOnItemSelectedListener(this);
        mRecipeCountSpinner.setSelection(getResources().getInteger(R.integer.recipeCount_default_spinner_position));
        mPreparationTimeSpinner.setSelection(getResources().getInteger(R.integer.preparationTime_default_spinner_position));
        mIngredientsCountSpinner.setSelection(getResources().getInteger(R.integer.ingredientsCount_default_spinner_position));
    }

    private void initRecycler(){
        mIngredients = new ArrayList<>();
        RecyclerView ingredientsRecyclerView = findViewById(R.id.ingredients_list);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(this, mIngredients);
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);
    }
}
