package com.example.pollsystemapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.pollsystemapplication.databinding.ActivityMapPageBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapPage extends FragmentActivity implements OnMapReadyCallback {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button goBack;
    private GoogleMap mMap;
    private ActivityMapPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapPage.this, AdministratorHomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                MapPage.this.finish();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Bundle extras = getIntent().getExtras();
        String pollId = extras.getString("pollId");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Vote> voters = new ArrayList<>();
                List<User> users = new ArrayList<>();
                for (DataSnapshot dataSnapshotVotePolls : snapshot.child("vote").getChildren()) {
                    if (dataSnapshotVotePolls.getKey().equals(pollId)) {
                        for (DataSnapshot dataSnapshotVoteUser : dataSnapshotVotePolls.getChildren()) {
                            for (DataSnapshot dataSnapshotUser : snapshot.child("user").getChildren()) {
                                if (dataSnapshotUser.getKey().equals(dataSnapshotVoteUser.getKey())) {
                                    users.add(dataSnapshotUser.getValue(User.class));
                                    voters.add(dataSnapshotVoteUser.getValue(Vote.class));
                                }
                            }
                        }
                    }
                }
                if (voters.isEmpty()) {
                    Toast.makeText(MapPage.this, "There are no users who voted on this poll.", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < voters.size(); i++) {
                        LatLng location = new LatLng(voters.get(i).getLocation().getLatitude(), voters.get(i).getLocation().getLongitude());
                        mMap.addMarker(new MarkerOptions().position(location).title(users.get(i).email));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}