package com.cookandroid.healthscanner.ui.food.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.healthscanner.R;
import com.cookandroid.healthscanner.ui.food.datatable.Food;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
                    if(chage != null){
                        CollectionReference collectionReference = db.collection("User").document(uid).collection("Food");
                        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    int count =0;
                                    long now = System.currentTimeMillis();
                                    Date date = new Date(now);
                                    SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                                    String type="";
                                    Random random = new Random();
                                    String strDate = dateFormat.format(date);
                                    for (DocumentSnapshot document : task.getResult()){
                                        if (chage.equals((String) document.getData().get("type")) == true){
                                            type= (String)document.getData().get("type");
//                                        Log.d("FoodAdapter", "type : " + type);
                                            count++;
                                        }

//                                    Log.d("FoodAdapter", "count : " + count);
//                                    Log.d("FoodAdapter", "now : " + now);
//                                    Log.d("FoodAdapter", "date : " + date);
//                                    Log.d("FoodAdapter", "strDate : "+ strDate);
                                    }
                                    if (count <5){
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("type",chage);
                                        user.put("foodName", dbfoodName);
                                        user.put("protein", dbprotein);
                                        user.put("carb", dbcarb);
                                        user.put("fat", dbfat);
                                        user.put("kcal", dbkcal);
                                        user.put("date",strDate);
                                        user.put("fileName",chage+now);
                                        collectionReference.document(chage + now).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                                }

                            }
                        });
                    }
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
