package com.learn.android.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.learn.android.R;

/**
 * Home Activity is the main Activity of the Application.
 */
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	//Initialise Firebase Variables.
	FirebaseAuth auth = FirebaseAuth.getInstance();
	FirebaseUser user = auth.getCurrentUser();

	//Declare UI Variables.
	public static NavController navController;
	private DrawerLayout drawerLayout;
	private AppBarConfiguration appBarConfiguration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		//Set up Navigation View.
		BottomNavigationView navView = findViewById(R.id.nav_view);
		drawerLayout = findViewById(R.id.container);
		NavigationView navigationView = findViewById(R.id.main_sidebar);
		navigationView.setNavigationItemSelectedListener(this);

		//Setup Toolbar with NavController.
		appBarConfiguration = new AppBarConfiguration.Builder(
				R.id.navigation_home,
				R.id.navigation_learn,
				R.id.navigation_social,
				R.id.navigation_tools,
				R.id.navigation_profile
		).setDrawerLayout(drawerLayout).build();
		navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
		NavigationUI.setupWithNavController(navView, navController);
		NavigationUI.setupWithNavController(navigationView, navController);

		//Check if the user logged in.
		boolean justLoggedIn = getIntent().getBooleanExtra("signIn", false);
		if (justLoggedIn) {
			Toast.makeText(this, "Welcome Back, " + user.getDisplayName(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
			drawerLayout.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onSupportNavigateUp() {
		return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
			drawerLayout.closeDrawer(GravityCompat.START);
		}
		if (item.getItemId() == R.id.nav_tools) {
			navController.navigate(R.id.navigation_tools);
		} else if (item.getItemId() == R.id.navigation_profile) {
			navController.navigate(R.id.navigation_profile);
		}
		return true;
	}
}
