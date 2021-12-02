package com.cookandroid.healthscanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseUser user;
    FirebaseFirestore db;
    private String uid;

    private TextView hmName;
    private TextView hmSex;
    private TextView hmHeight;
    private TextView hmWeight;
    private TextView hmExercise;
    private TextView hmBreakfask;
    private TextView hmLunch;
    private TextView hmDinner;

    private ImageView imageView;
    private TextView hmStartDate;
    private TextView hmLastDate;
    private TextView hmBody;
    private TextView hmExerciseName;
    private TextView hmExerciseExplanation;

DrawerLayout drawerLayout;
    ImageView btMenu;
    RecyclerView recyclerView;
    MainAdapter adapter;
    public static ArrayList<String> arrayList = new ArrayList<>();

    public static void closeDrawer(DrawerLayout drawerLayout) {
        //check condition
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //when drawer is open
            //Closer drawers
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            drawerLayout.openDrawer(GravityCompat.START);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        hmName =findViewById(R.id.hm_tvname);
        hmSex=findViewById(R.id.hm_tvsex);
        hmHeight=findViewById(R.id.hm_tvheight);
        hmWeight=findViewById(R.id.hm_tvweight);
        hmExercise=findViewById(R.id.hm_tvexercise);
        hmBreakfask=findViewById(R.id.hm_tvbreakfask);
        hmLunch=findViewById(R.id.hm_tvlunch);
        hmDinner=findViewById(R.id.hm_tvdinner);
        imageView=findViewById(R.id.hm_imexercise);
        hmStartDate=findViewById(R.id.tv_hmstartdate);
        hmLastDate=findViewById(R.id.tv_hmlastdate);
        hmBody=findViewById(R.id.tv_hmbody);
        hmExerciseName=findViewById(R.id.tv_hmexercisename);
        hmExerciseExplanation=findViewById(R.id.tv_exerciseexplanation);

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user !=null ? user.getUid():null;
        db = FirebaseFirestore.getInstance();


        //Assign variable
        drawerLayout = findViewById(R.id.drawer_layout);
        btMenu = findViewById(R.id.bt_menu);
        recyclerView = findViewById(R.id.recycler_view);

        //clear array list
        arrayList.clear();

        //add menu item in array list
        arrayList.add("Home");
        arrayList.add("Profile");
        arrayList.add("Food");
        arrayList.add("Exercising");
        arrayList.add("Logout");

        //Initialize adapter
        adapter = new MainAdapter(this,arrayList);
        //Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //set adapter
        recyclerView.setAdapter(adapter);

        btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open drawer
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        getBadyData();
        todaysMeal();
        getExerciseHM();
    }

    private void getBadyData(){
        db.collection("User").document(uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "Listen failed.", e);
                            return;
                        }
                        else{

                            hmName.setText(documentSnapshot.get("fName").toString().trim());
                            if ((Long)documentSnapshot.get("sex") == 1){
                                hmSex.setText("남성");
                            }
                            else if ((Long)documentSnapshot.get("sex") == 0){
                                hmSex.setText("여성");
                            }
                            hmHeight.setText(documentSnapshot.get("height").toString().trim());
                            hmWeight.setText(documentSnapshot.get("weight").toString().trim());
                            if ((Long)documentSnapshot.get("activityRate") == 0){
                                hmExercise.setText("낮음");
                            }
                            else if ((Long)documentSnapshot.get("activityRate") == 2){
                                hmExercise.setText("보통");
                            }
                            else if ((Long)documentSnapshot.get("activityRate") == 1){
                                hmExercise.setText("높음");
                            }
                        }
                    }
                });
    }

    private void todaysMeal(){
        String [] foodtypelist = {"breakfask","lunch","dinner"};
        String foodtype = "";
        for (int i =0 ; i<3; i++){
            foodtype = foodtypelist[i];
            CollectionReference collectionReference = db.collection("User").document(uid).collection("Food");
            String finalFoodtype = foodtype;
            collectionReference.whereEqualTo("type",finalFoodtype).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("TAG", "Listen failed.", e);
                        return;
                    }
                    int count =0;
                    String str = "";
//                Log.d("TAG","outqueryDocumentSnapshots.size : "+ queryDocumentSnapshots.size());
                    if (queryDocumentSnapshots.size() ==0){
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){
                            Log.d("TAG","zerodc.size : "+ dc.getDocument().getData().size());
                            if (dc.getDocument().getData().get("foodName")!= null) {
                                str += (String) dc.getDocument().getData().get("foodName");
                                if (count<queryDocumentSnapshots.size() -1){
                                    str+="+";
                                    count++;
                                }
                            }
                        }
                    }
                    else {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                            if (ds.get("foodName")!= null) {
                                str += (String) ds.get("foodName");
                                if (count<queryDocumentSnapshots.size() -1){
                                    str+="+";
                                    count++;
                                }

                            }
                        }
                    }

                    if (finalFoodtype =="breakfask"){
                        hmBreakfask.setText(str);
                    }
                    if (finalFoodtype =="lunch"){
                        hmLunch.setText(str);
                    }
                    if (finalFoodtype =="dinner"){
                        hmDinner.setText(str);
                    }
                }
            });
        }
    }

    private void getExerciseHM(){
        db.collection("User").document(uid).collection("Exercise").document("savefile")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "Listen failed.", e);
                            return;
                        }
                        else{
                            if (documentSnapshot.get("startdate")!= null){
                                hmStartDate.setText(documentSnapshot.get("startdate").toString().trim());
                            }
                            if (documentSnapshot.get("lastdate")!= null){
                                hmLastDate.setText(documentSnapshot.get("lastdate").toString().trim());
                            }
                            if (documentSnapshot.get("exercise")!= null){
                                hmBody.setText(documentSnapshot.get("exercise").toString().trim());
                            }
                            if (documentSnapshot.get("exerciseName")!= null){
                                hmExerciseName.setText(documentSnapshot.get("exerciseName").toString().trim());
                            }
                            if (documentSnapshot.get("exerciseExplanation") != null){
                                hmExerciseExplanation.setText(documentSnapshot.get("exerciseExplanation").toString().trim());
                            }

                            Glide.with(getApplicationContext())
                                    .load(documentSnapshot.get("image"))
                                    .into(imageView);
                        }
                    }
                });
    }
    @Override
    protected void onPause() {
        super.onPause();
        //Close drawer
        closeDrawer(drawerLayout);
    }

}