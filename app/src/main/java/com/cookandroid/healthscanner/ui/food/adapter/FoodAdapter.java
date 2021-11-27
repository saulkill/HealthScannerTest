package com.cookandroid.healthscanner.ui.food.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.healthscanner.R;
import com.cookandroid.healthscanner.ui.food.datatable.Food;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    String chage;
    Context context;
    ArrayList<Food> foodArrayList;
    FirebaseFirestore db;
    FirebaseUser user;
    public FoodAdapter(Context context, ArrayList<Food> foodArrayList, String chage) {
        this.context = context;
        this.foodArrayList = foodArrayList;
        this.chage = chage;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.food_item,parent,false);

        return new FoodViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodArrayList.get(position);

        holder.foodName.setText(food.getFoodName());
        holder.protein.setText(String.valueOf(food.getProtein()));
        holder.carb.setText(String.valueOf(food.getCarb()));
        holder.fat.setText(String.valueOf(food.getFat()));
        holder.kcal.setText(String.valueOf(food.getKcal()) +"Kcal");
        if (chage !=null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    db = FirebaseFirestore.getInstance();
                    String uid = user != null ? user.getUid() : null;
                    String dbfoodName = food.getFoodName();
                    float dbprotein = food.getProtein();
                    float dbcarb = food.getCarb();
                    float dbfat = food.getFat();
                    float dbkcal = food.getKcal();
                    DocumentReference documentReference = db.collection("User").document(uid).collection("Food").document(chage);
                    Map<String, Object> user = new HashMap<>();
                    user.put("foodName", dbfoodName);
                    user.put("protein", dbprotein);
                    user.put("carb", dbcarb);
                    user.put("fat", dbfat);
                    user.put("kcal", dbkcal);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Log.d(TAG,"onSuccess: user Profile is created for"+uid);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Log.d(TAG,"onSuccess: "+e.toString());
                        }
                    });

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return foodArrayList.size();
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, protein, carb, fat, kcal;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.tvfoodname);
            protein = itemView.findViewById(R.id.tvprotein);
            carb = itemView.findViewById(R.id.tvcarbohydrate);
            fat = itemView.findViewById(R.id.tvfat);
            kcal = itemView.findViewById(R.id.tvkcal);
        }
    }
}
