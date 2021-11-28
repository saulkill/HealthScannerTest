package com.cookandroid.healthscanner.ui.food.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.healthscanner.R;
import com.cookandroid.healthscanner.ui.food.datatable.DeleteFood;
import com.cookandroid.healthscanner.ui.food.datatable.Food;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FoodDeleteListAdapter extends RecyclerView.Adapter<FoodDeleteListAdapter.MyViewHolder> {

    Context context;
    ArrayList<DeleteFood> foodArrayList;
    FirebaseFirestore db;
    FirebaseUser user;

    public FoodDeleteListAdapter( Context context, ArrayList<DeleteFood> foodArrayList) {
        this.context = context;
        this.foodArrayList = foodArrayList;
    }

    @NonNull
    @Override
    public FoodDeleteListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.alert_dialog_border,parent,false);

        MyViewHolder holder = new MyViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodDeleteListAdapter.MyViewHolder holder, int position) {
        DeleteFood deleteFood = foodArrayList.get(position);
        holder.foodName.setText(deleteFood.getFoodName());
        String deletefile = deleteFood.getFileName();
//        if (deletefile !=null){
            holder.deletIm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    db = FirebaseFirestore.getInstance();
                    String uid = user != null ? user.getUid() : null;
                    db.collection("User").document(uid).collection("Food").document(deletefile).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("TAG", "Error deleting document", e);
                                }
                            });
                    notifyDataSetChanged();
                }
            });
//        }

    }

    @Override
    public int getItemCount() {
        return foodArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView foodName;
        ImageView deletIm;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.deleteName);
            deletIm = itemView.findViewById(R.id.delete_im);
        }
    }
}
