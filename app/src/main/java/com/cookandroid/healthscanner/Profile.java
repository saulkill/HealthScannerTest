package com.cookandroid.healthscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cookandroid.healthscanner.ui.food.FoodActivity;
import com.cookandroid.healthscanner.ui.food.adapter.FoodAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {
    private static final String TAG = "ProfileAtivity";
    //Initialize variable
    DrawerLayout drawerLayout;
    ImageView btMenu;
    RecyclerView recyclerView;
    EditText ppedName;
    EditText ppedEmail;
    EditText ppedPhone;
    EditText ppedHeight;
    EditText ppedWeight;
    RadioGroup radioGroupSex;
    RadioButton radioButtonSex1;
    RadioButton radioButtonSex2;
    RadioGroup radioGroup2;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    Button buttonSave;
    Button buttonCancel;

    FirebaseUser user;
    private String uid;
    FirebaseFirestore db;

    private Long sex = null;
    private Long activityRate =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ppedName = findViewById(R.id.pp_edName);
        ppedEmail = findViewById(R.id.pp_edemail);
        ppedPhone = findViewById(R.id.pp_edphone);
        ppedHeight = findViewById(R.id.pp_edheight);
        ppedWeight = findViewById(R.id.pp_edweight);

        radioGroupSex = findViewById(R.id.rg_sex1);
        radioButtonSex1 = findViewById(R.id.rb_sex1);
        radioButtonSex1 = findViewById(R.id.rb_sex2);
        radioGroup2 = findViewById(R.id.rg_exercise);
        radioButton1 = findViewById(R.id.rb_lowexercise);
        radioButton2 = findViewById(R.id.rb_exercise);
        radioButton3 = findViewById(R.id.rb_highexercise);

        buttonSave = findViewById(R.id.bt_ppsave);
        buttonCancel = findViewById(R.id.bt_ppcancel);

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user !=null ? user.getUid():null;
        db = FirebaseFirestore.getInstance();
        //Assign variable
        drawerLayout = findViewById(R.id.drawer_layoutprofile);
        btMenu = findViewById(R.id.bt_menu);
        recyclerView = findViewById(R.id.recycler_view);

        //Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //set profile
        recyclerView.setAdapter(new MainAdapter(this,MainActivity.arrayList));

        btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open drawer
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        radioGroupSex.setOnCheckedChangeListener(sexRadioGroupButtonChangeListener);
        radioGroup2.setOnCheckedChangeListener(RadioGroupButtonChangeListener);

        edTextget();
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btSaveSet(sex,activityRate);
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });
    }
    RadioGroup.OnCheckedChangeListener sexRadioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(i == R.id.rb_sex1){
                sex = Long.valueOf(1);
            }
            else if(i == R.id.rb_sex2){
                sex = Long.valueOf(0);
            }
        }
    };
    RadioGroup.OnCheckedChangeListener RadioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(i == R.id.rb_lowexercise){
                activityRate = Long.valueOf(0);
            }
            else if(i == R.id.rb_exercise){
                activityRate = Long.valueOf(2);
            }
            else if(i == R.id.rb_highexercise){
                activityRate = Long.valueOf(1);
            }
        }
    };
    private void edTextget(){
        db.collection("User").document(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                ppedName.setText((String)task.getResult().get("fName"));
                ppedEmail.setText((String)task.getResult().get("email"));
                ppedPhone.setText((String)task.getResult().get("phone"));
                if (task.getResult().get("height")!=null)
                    ppedHeight.setText((String)task.getResult().get("height").toString());
                if (task.getResult().get("weight")!=null)
                    ppedWeight.setText((String)task.getResult().get("weight").toString());
                if ((Long)task.getResult().get("sex") ==Long.valueOf(1))
                    radioButtonSex1.setChecked(true);
                if ((Long)task.getResult().get("sex") ==Long.valueOf(0))
                    radioButtonSex2.setChecked(true);
                if ((Long)task.getResult().get("activityRate") ==Long.valueOf(0))
                    radioButton1.setChecked(true);
                if ((Long)task.getResult().get("activityRate") ==Long.valueOf(2))
                    radioButton2.setChecked(true);
                if ((Long)task.getResult().get("activityRate") ==Long.valueOf(1))
                    radioButton3.setChecked(true);


            }
        });
    }
    private void btSaveSet(Long sex, Long activityRate){
        String edName =null;
        String edEmail = null;
        String ephone=null;
        Double edHeight=null;
        Double edWeight=null;

        if (String.valueOf(ppedName.getText()) != null) {
            edName = String.valueOf(ppedName.getText());
        }
        if (String.valueOf(ppedEmail.getText()) != null) {
            edEmail = String.valueOf(ppedEmail.getText());
        }
        if (String.valueOf(ppedPhone.getText()) != null) {
            ephone = String.valueOf(ppedPhone.getText());
        }
        if (String.valueOf(ppedHeight.getText()) != null) {
            edHeight = Double.parseDouble(ppedHeight.getText().toString());
        }
        if (String.valueOf(ppedWeight.getText()) != null) {
            edWeight = Double.parseDouble(ppedWeight.getText().toString());
        }

        Map<String, Object> save = new HashMap<>();
        save.put("fName", edName);
        save.put("email",edEmail);
        save.put("height", edHeight);
        save.put("phone", ephone);
        save.put("weight", edWeight);
        save.put("sex", sex);
        save.put("activityRate", activityRate);

        db.collection("User").document(uid)
                .set(save, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        //Close drawer
        MainActivity.closeDrawer(drawerLayout);
    }
}