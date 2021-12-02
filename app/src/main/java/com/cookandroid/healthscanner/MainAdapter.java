package com.cookandroid.healthscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.cookandroid.healthscanner.ui.dashboard.DashboardActivity;
import com.cookandroid.healthscanner.ui.food.FoodActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    //Initialize variable
    Activity activity;
    ArrayList<String> arrayList1;

    //create constructor
    public MainAdapter(Activity activity,ArrayList<String> arrayList1){
        this.activity = activity;
        this.arrayList1 = arrayList1;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Initialize view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_drawer_main,parent, false);
        //Return holder view
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //set text on text view
        holder.textView.setText(arrayList1.get(position));

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get click item position
                int position = holder.getAdapterPosition();
                //Check condition
                switch (position){
                    case 0:
                        //when position is equal to 0
                        //Redirect to home page
                        activity.startActivity(new Intent(activity,MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        activity.finishAffinity();
                        break;
                    case 1:
                        //where position is equal to 1
                        //Redirect to profile page
                        activity.startActivity(new Intent(activity,Profile.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        activity.finishAffinity();
                        break;
                    case 2:
                        //where position is equal to 1
                        //Redirect to food page
                        activity.startActivity(new Intent(activity, FoodActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        activity.finishAffinity();

                        break;

                    case 3:
                        //where position is equal to 3
                        //Redirect to Dashboard page
                        activity.startActivity(new Intent(activity, DashboardActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        activity.finishAffinity();
                        break;

                    case 4:
                        //when position is equal 4
                        //initialize alert dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        //set title
                        builder.setTitle("Logout");
                        //set message
                        builder.setMessage("로그아웃 하시겠습니까?");
                        //'예' 누를 시
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //모든 활동 종료
                                FirebaseAuth.getInstance().signOut();
                                activity.startActivity(new Intent(activity, LoginActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                activity.finishAffinity();
                                //EXIT app
                               // System.exit(0);
                            }
                        });
                        //'아니오' 누를 시
                        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Dismiss dialog
                                dialog.dismiss();
                            }
                        });
                        //show dialog
                        builder.show();
                        break;

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        //Return array list size
        return arrayList1.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Initialize variable
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Assign variable
            textView = itemView.findViewById(R.id.text_view);
        }
    }
}
