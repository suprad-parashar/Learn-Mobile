package com.example.android.learn;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Home Activity is the main Activity of the Application.
 */
public class HomeActivity extends AppCompatActivity {

    //Initialise Firebase Variables.
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    //Declare UI Variables.
    public static NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Set up Navigation View.
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_learn,
                R.id.navigation_social,
                R.id.navigation_tools,
                R.id.navigation_profile
        ).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //Check if the user logged in.
        boolean justLoggedIn = getIntent().getBooleanExtra("signIn", false);
        if (justLoggedIn) {
            Toast.makeText(this, "Welcome Back, " + user.getDisplayName(), Toast.LENGTH_LONG).show();
        }
    }
}
