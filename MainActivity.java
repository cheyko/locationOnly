package com.cheyko.locationonly;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_LOCATION = 1;
    private static final double SPEED_LIMIT = 12;
    Button button;
    TextView textView1, textView2;
    LocationManager locationManager;
    public String latitude,longitude, outputString = new String("..");
    public long expectedTime, timeTravelledThus, expectedCurTime;
    private double speedSum = 0,routeDistance,busDis,busCurSpeed, myLatti, myLongi,
            busCurAvgSpeed,intervalTime, currentDistanceTravelled = 0,prevDistTrav;
    public BigDecimal busStartLat, busStartLongi,lat, lon,busMid1Lat,busMid1Longi,busMid2Lat,
            busMid2Longi,busEndLat, busEndLongi ;
    private int numEntered;

    private DatabaseReference ref,routeRef;
    ListView listViewDisplacements;
    public List<Displacements> displacementsList;
    public List<Displacements> routeList;

    public Date currentTime, startTime;
    public EditText busNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        textView1 = findViewById(R.id.database_values);
        textView2 = findViewById(R.id.text_location);
        button = findViewById(R.id.button_location);

        listViewDisplacements = findViewById(R.id.listViewDisplacements);

        displacementsList = new ArrayList<>();
        routeList = new ArrayList<>();

        speedSum += SPEED_LIMIT;

        busNumber = findViewById(R.id.busnumber);

        button.setOnClickListener(this);

        // Get a reference to our posts
        ref = FirebaseDatabase.getInstance().getReference("displacements");
        routeRef = FirebaseDatabase.getInstance().getReference("routes");
        currentTime = Calendar.getInstance().getTime();
        //theDatabase = FirebaseDatabase.getInstance().getReference("displacements");
    }


    @Override
    protected void onStart() {
        super.onStart();

        //      Attach a listener to read the data at our posts reference

        routeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                routeList.clear();
                for(DataSnapshot routeSnapshot : dataSnapshot.getChildren()){

                    Displacements routeDetails = routeSnapshot.getValue(Displacements.class);
                    routeList.add(routeDetails);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    public void isBefitting(double prevDist, double totalDistance, long expectingTime,
                            long timeThus,long prevTime, double curDisTrav, double avgBusSpeed){


        double distanceLeft = totalDistance - curDisTrav;

        if (prevDist == 0){
            expectedCurTime = (long)((curDisTrav/totalDistance) * expectingTime);
            }
        else if (prevDist != 0){
            expectedCurTime = (long)((curDisTrav/prevDist) * prevTime);
        }

        long timeLeft = (long) (distanceLeft / avgBusSpeed);
        Toast.makeText(this," expected time left for bus: "+ timeLeft,Toast.LENGTH_LONG).show();


        String testing = new String ();


        if (expectedCurTime == timeThus){//(calculatedTime == (expectingTime - timeThus)){

            Toast.makeText(this,"Bus on perfect timing",Toast.LENGTH_LONG).show();

        }else if(expectedCurTime > timeThus){//(calculatedTime > (expectingTime - timeThus)){

            long diff = (expectedCurTime - timeThus);//calculatedTime - (expectingTime - timeThus);
            testing = "expTime: " + expectedCurTime + " expTime:  " + expectingTime + " timeThus: "
                    + timeThus + " diff: " + diff;
            Toast.makeText(this,"Bus is running Late .... " + testing,Toast.LENGTH_LONG).show();


        }else if(expectedCurTime < timeThus){//(calculatedTime < (expectingTime - timeThus)){

            long diff = ( timeThus - expectedCurTime );//(expectingTime - timeThus) - calculatedTime;
            testing = "expTime: " + expectedCurTime  + " expTime:  " + expectingTime + " timeThus: "
                    + timeThus + " diff: " + diff;
            Toast.makeText(this,"Bus is ahead of schedule .... " + testing,Toast.LENGTH_LONG).show();

        }
        expectedTime = timeLeft;//calculatedTime;
        //prevDistTrav = curDisTrav;
    }


    @Override
    public void onClick(View view) {



        String input = busNumber.getText().toString();
        numEntered = Integer.parseInt(input);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }

        //System.out.println("testing");

        for(int i = 0 ; i < routeList.size(); i++){
            if(routeList.get(i).getBusNumber() == numEntered){
                if(routeList.get(i).getTimeThus() == 0){
                    startTime = routeList.get(i).getCurrentTime();
                    busStartLat = new BigDecimal(routeList.get(i).getPoints().getLatti());
                    busStartLongi = new BigDecimal(routeList.get(i).getPoints().getLongi());
                }
                else if(routeList.get(i).getTimeThus() == 1){
                    busMid1Lat = new BigDecimal(routeList.get(i).getPoints().getLatti());
                    busMid1Longi = new BigDecimal(routeList.get(i).getPoints().getLongi());
                }else if(routeList.get(i).getTimeThus() == 2){
                    busMid2Lat = new BigDecimal(routeList.get(i).getPoints().getLatti());
                    busMid2Longi = new BigDecimal(routeList.get(i).getPoints().getLongi());
                }else if(routeList.get(i).getTimeThus() == 3){
                    busEndLat = new BigDecimal(routeList.get(i).getPoints().getLatti());
                    busEndLongi = new BigDecimal(routeList.get(i).getPoints().getLongi());
                }

            }
        }


        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //textView1.setText("Testing again ...... 1 ");
                //Displacements displacements = dataSnapshot.getValue(Displacements.class);
                //System.out.println(displacements);

                displacementsList.clear();
                for(DataSnapshot displacementSnapshot : dataSnapshot.getChildren()){

                    Displacements displacement = displacementSnapshot.getValue(Displacements.class);

                    if (displacement.getBusNumber() == numEntered) {


                        lat = new BigDecimal(displacement.getPoints().getLatti());
                        lon = new BigDecimal(displacement.getPoints().getLongi());
                        textView1.setText("latitude = " + lat + " longitude =  " + lon);

                        displacementsList.add(displacement);
                    }

                }

                //DisplacementList adapter = new DisplacementList(MainActivity.this,displacementsList);
                //listViewDisplacements.setAdapter(adapter);

                ////////// handle optimization
                int k = displacementsList.size();

                if (k < 2){

                    outputString = "Bus is currently at the Depot";

                }else{

                    for (int j = 0; j < k - 1; j++) {

                        BigDecimal x1 = new BigDecimal(displacementsList.get(j).getPoints().getLatti());
                        BigDecimal y1 = new BigDecimal(displacementsList.get(j).getPoints().getLongi());
                        BigDecimal x2 = new BigDecimal(displacementsList.get(j + 1).getPoints().getLatti());
                        BigDecimal y2 = new BigDecimal(displacementsList.get(j + 1).getPoints().getLongi());
                        intervalTime = displacementsList.get(j + 1).getTimeThus() - displacementsList.get(j).getTimeThus();

                        busDis = Haversine.distance(x1, y1, x2, y2);
                        busCurSpeed = busDis / intervalTime;

                        speedSum += busCurSpeed;

                        busCurAvgSpeed = speedSum / (k);
                    }

                    routeDistance = (Haversine.distance(busStartLat, busStartLongi, busMid1Lat, busMid1Lat)+
                                    Haversine.distance(busMid1Lat, busMid1Longi, busMid2Lat, busMid2Longi)+
                                    Haversine.distance(busMid2Lat, busMid2Longi, busEndLat, busEndLongi) );

                    currentDistanceTravelled = Haversine.distance(busStartLat, busStartLongi, lat, lon);
                    prevDistTrav = Haversine.distance(busStartLat, busStartLongi, new BigDecimal(displacementsList.get(k - 2).getPoints().getLatti()), new BigDecimal(displacementsList.get(k - 2).getPoints().getLongi()));

                    timeTravelledThus = displacementsList.get(k - 1).getTimeThus();
                    long prevTime = displacementsList.get(k - 2).getTimeThus();

                    // expectedTime is calculated in function --> isBenifitting() but is initialized as zero.
                    if ((expectedTime == 0) && (busCurAvgSpeed != 0)) {
                        expectedTime = (long) (routeDistance / busCurAvgSpeed);
                    }else if ((expectedTime == 0) && (busCurAvgSpeed != 0)){
                        expectedTime = (long) (routeDistance / SPEED_LIMIT);
                    }

                    //busExpectedArrivalTime = (long) (routeDistance / SPEED_LIMIT);
                    isBefitting(prevDistTrav, routeDistance, expectedTime, timeTravelledThus, prevTime, currentDistanceTravelled, busCurAvgSpeed);
                    outputString = "Bus is on the move";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                //textView1.setText("Testing again ...... 2 ");
            }
        });

        Toast.makeText(this, outputString,Toast.LENGTH_LONG).show();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                myLatti = location.getLatitude();
                myLongi = location.getLongitude();
                latitude = String.valueOf(myLatti);
                longitude = String.valueOf(myLongi);

                textView2.setText("Your current location is"+ "\n" + "Lattitude = " + latitude
                        + "\n" + "Longitude = " + longitude);

            } else  if (location1 != null) {
                myLatti = location.getLatitude();
                myLongi = location.getLongitude();
                latitude = String.valueOf(myLatti);
                longitude = String.valueOf(myLongi);

                textView2.setText("Your current location is"+ "\n" + "Lattitude = " + latitude
                        + "\n" + "Longitude = " + longitude);


            } else  if (location2 != null) {
                myLatti = location.getLatitude();
                myLongi = location.getLongitude();
                latitude = String.valueOf(myLatti);
                longitude = String.valueOf(myLongi);

                textView2.setText("Your current location is"+ "\n" + "Lattitude = " + latitude
                        + "\n" + "Longitude = " + longitude);

            }else{

                Toast.makeText(this,"Unable to Trace your location",Toast.LENGTH_SHORT).show();

            }
        }
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
