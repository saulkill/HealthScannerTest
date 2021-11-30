package com.cookandroid.healthscanner.ui.dashboard;

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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.cookandroid.healthscanner.MainActivity;
import com.cookandroid.healthscanner.MainAdapter;
import com.cookandroid.healthscanner.R;
import com.cookandroid.healthscanner.ui.dashboard.adapter.ExerciseListAdapter;
import com.cookandroid.healthscanner.ui.dashboard.exercisedatetable.ExerciseData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private Spinner sp_parts;
    private TextView exerciseName;
    private TextView exerciseExplanation;
    private TextView tvStart;
    private TextView tvLast;
    private ImageButton dateButton;
    private ImageButton saveButton;
    private String exci ="";
    private FirebaseUser user;
    private FirebaseFirestore db;
    private String uid;
    private ArrayList<ExerciseData> exerciseDataArrayList;
    private RecyclerView recyclerView;
    private ExerciseListAdapter exerciseListAdapter;

    //Initialize variable
    DrawerLayout drawerLayout;
    ImageView btMenu;
    RecyclerView recyclerViewMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user !=null ? user.getUid():null;
        db = FirebaseFirestore.getInstance();

        sp_parts = (Spinner)findViewById(R.id.sp_parts);
        exerciseName = (TextView)findViewById(R.id.tv_exerciseName);
        exerciseExplanation = (TextView)findViewById(R.id.tv_exercise);
        tvStart = (TextView)findViewById(R.id.tv_sdtext);
        tvLast=(TextView)findViewById(R.id.tv_ldtext);
        dateButton = (ImageButton)findViewById(R.id.ib_startdate);
        saveButton = (ImageButton)findViewById(R.id.ib_save);
//Assign variable
        drawerLayout = findViewById(R.id.drawer_layout);
        btMenu = findViewById(R.id.bt_menu);
        recyclerViewMenu = findViewById(R.id.recycler_view);
        //Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //set profile
        recyclerView.setAdapter(new MainAdapter(this, MainActivity.arrayList));


        recyclerView = findViewById(R.id.rc_exercise);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        exerciseDataArrayList = new ArrayList<ExerciseData>();
        exerciseListAdapter = new ExerciseListAdapter(DashboardActivity.this,exerciseDataArrayList);
        recyclerView.setAdapter(exerciseListAdapter);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        DashboardActivity.this,

                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)


                );

                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open drawer
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        sp_parts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                exci = adapterView.getItemAtPosition(position).toString();

                SpinnerList(exci);
                Log.d("TAG","글자 확인 : " + exci);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        getInTextView();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvStart.setText("");
                tvLast.setText("");
                DocumentReference documentReference =db.collection("User").document(uid).collection("Exercise").document("dotsavefile");
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult() != null){
                            String uid = user != null ? user.getUid() : null;
                            String exercise =(String) task.getResult().get("exercise");
                            String exerciseName = (String) task.getResult().get("exerciseName");
                            String exerciseExplanation = (String) task.getResult().get("exerciseExplanation");
                            String image = (String) task.getResult().get("image");
                            String startdate = (String) task.getResult().get("startdate");
                            String lastdave =  (String) task.getResult().get("lastdave");
                            boolean saveData = true;
                            Map<String, Object> user = new HashMap<>();
                            user.put("exercise", exercise);
                            user.put("exerciseName",exerciseName);
                            user.put("exerciseExplanation", exerciseExplanation);
                            user.put("image", image);
                            user.put("saveData", saveData);
                            user.put("startdate", startdate);
                            user.put("lastdave",lastdave);
                            db.collection("User").document(uid).collection("Exercise").document("savefile")
                                    .set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                        else{

                        }

                    }
                });

            }
        });
    }
    void SpinnerList(String exercise){

        db.collection("ExerciseList").whereEqualTo("exercise",exercise)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                exerciseDataArrayList.clear();
                for (DocumentSnapshot doc : task.getResult()){
                    exerciseDataArrayList.add(doc.toObject(ExerciseData.class));
                    //recyclerView.setAdapter(exerciseListAdapter);
                    exerciseListAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    void getInTextView(){
        db.collection("User").document(uid).collection("Exercise").document("dotsavefile")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "Listen failed.", e);
                            return;
                        }
                        else{

                            exerciseName.setText((String)documentSnapshot.get("exerciseName"));
                            exerciseExplanation.setText((String)documentSnapshot.get("exerciseExplanation"));
                        }
                    }
                });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        String startdate = year+"/"+(++monthOfYear)+"/"+dayOfMonth;
        String lastdave =  yearEnd+"/"+(++monthOfYearEnd)+"/"+dayOfMonthEnd;
        Map<String, Object> date = new HashMap<>();
        date.put("startdate", startdate);
        date.put("lastdave",lastdave);
        db.collection("User").document(uid).collection("Exercise").document("dotsavefile")
                .set(date, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        tvStart.setText(startdate);
        tvLast.setText(lastdave);

    }
    @Override
    protected void onPause() {
        super.onPause();
        //Close drawer
        MainActivity.closeDrawer(drawerLayout);
    }
}
