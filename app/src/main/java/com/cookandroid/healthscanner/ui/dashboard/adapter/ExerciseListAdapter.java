package com.cookandroid.healthscanner.ui.dashboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cookandroid.healthscanner.R;
import com.cookandroid.healthscanner.ui.dashboard.exercisedatetable.ExerciseData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.MyViewHolder> {
    Context context;
    ArrayList<ExerciseData> exerciseData;
    FirebaseFirestore db;
    FirebaseUser user;

    public ExerciseListAdapter(Context context, ArrayList<ExerciseData> exerciseData) {
        this.context = context;
        this.exerciseData = exerciseData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.exercise_item,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseListAdapter.MyViewHolder holder, int position) {
        ExerciseData exerciseData1 = exerciseData.get(position);
        Glide.with(holder.itemView)
                .load(exerciseData1.getImage())
                .into(holder.exerciseIm);
        holder.exerciseName.setText(exerciseData1.getExerciseName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                db = FirebaseFirestore.getInstance();
                String uid = user != null ? user.getUid() : null;
                String exercise = exerciseData1.getExercise();
                String exerciseName = exerciseData1.getExerciseName();
                String exerciseExplanation = exerciseData1.getExerciseExplanation();
                String image = exerciseData1.getImage();
                boolean saveData = false;
                CollectionReference collectionReference = db.collection("User").document(uid).collection("Exercise");
                collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("exercise", exercise);
                        user.put("exerciseName",exerciseName);
                        user.put("exerciseExplanation", exerciseExplanation);
                        user.put("image", image);
                        user.put("saveData", saveData);
                        collectionReference.document("dotsavefile").set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        ImageView exerciseIm;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.tv_exercisename_itme);
            exerciseIm = itemView.findViewById(R.id.im_exercise);
        }
    }
}
