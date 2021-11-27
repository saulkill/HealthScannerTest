package com.cookandroid.healthscanner.ui.food;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cookandroid.healthscanner.R;
import com.cookandroid.healthscanner.ui.food.adapter.FoodAdapter;
import com.cookandroid.healthscanner.ui.food.datatable.Food;
import com.cookandroid.healthscanner.ui.food.foodgraph.GraphData;
import com.cookandroid.healthscanner.ui.food.foodgraph.ValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FoodActivity extends AppCompatActivity {
//음식 화면 리스트
    RecyclerView recyclerView;
    ArrayList<Food> foodArrayList;
    FoodAdapter foodAdapter;
    FirebaseUser user;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    BarChart barChart;
    String uid;
    String chage =null;
    List<GraphData> graphData = new ArrayList<>();
    ArrayList<String> xLabel = new ArrayList<>();

    float kAverage;
    float pAverage;
    float cAverage;
    float fAverage;
    //ArrayList<Food> foodArrayLists = new ArrayList<Food>();
    BarData data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        radioGroup = findViewById(R.id.radioGroup);
        radioButton1 = findViewById(R.id.radioButton);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        barChart = (BarChart)findViewById(R.id.chart1);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user !=null ? user.getUid():null;

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching : Data...");
        progressDialog.show();
        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        recyclerView = findViewById(R.id.foodrecyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        foodArrayList = new ArrayList<Food>();
        foodAdapter = new FoodAdapter(FoodActivity.this,foodArrayList,chage);

        recyclerView.setAdapter(foodAdapter);
        EventChangeListener();
        RadioButtonTextEventChange();

    }

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(i == R.id.radioButton){
                chage ="breakfask";
                foodAdapter = new FoodAdapter(FoodActivity.this,foodArrayList,chage);
                recyclerView.setAdapter(foodAdapter);

                }
            else if(i == R.id.radioButton2){ chage ="lunch";
                foodAdapter = new FoodAdapter(FoodActivity.this,foodArrayList,chage);
                recyclerView.setAdapter(foodAdapter);
            }
            else if(i == R.id.radioButton3){ chage ="dinner";
                foodAdapter = new FoodAdapter(FoodActivity.this,foodArrayList,chage);
                recyclerView.setAdapter(foodAdapter);
            }
        }
    };
    void RadioButtonTextEventChange() {

        DocumentReference documentReference = db.collection("User").document(uid).collection("Food").document("breakfask");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
           @Override
           public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
               if (documentSnapshot != null && documentSnapshot.exists()) {
                        radioButton1.setText("아침 : " + documentSnapshot.getData().get("foodName").toString());
                       Faveragelist(180,1,1);
               }
           }
       });
       DocumentReference documentReference1 = db.collection("User").document(uid).collection("Food").document("lunch");
       documentReference1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
           @Override
           public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
               if (documentSnapshot != null && documentSnapshot.exists()){
                   radioButton2.setText("점심 : " + documentSnapshot.getData().get("foodName").toString());
                   Faveragelist(180,1,1);
               }

           }
       });
        DocumentReference documentReference2 = db.collection("User").document(uid).collection("Food").document("dinner");
        documentReference2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()){
                    radioButton3.setText("저녁 : " + documentSnapshot.getData().get("foodName").toString());barChart.clearAnimation();
                    Faveragelist(180,1,1);
                }

            }
        });


    }

    private void EventChangeListener(){
        db.collection("Food").orderBy("foodName", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                        if (e !=null){
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Firestore errer", e.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()){
                            if(dc.getType() == DocumentChange.Type.ADDED){
                                foodArrayList.add(dc.getDocument().toObject(Food.class));
                            }
                            foodAdapter.notifyDataSetChanged();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });
    }

    private void setFoodData(List<GraphData> GraphData){
        ArrayList<BarEntry> values = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        int green = Color.rgb(110,190,102);
        int red = Color.rgb(211,87,44);
        for(int i=0; i<GraphData.size(); i++){
            GraphData graphData = GraphData.get(i);
            BarEntry entry = new BarEntry(graphData.xValue,graphData.yValue );
            values.add(entry);
            if (graphData.yValue<=0){
                colors.add(red);
            }
            else{
                colors.add(green);
            }
        }
        if (barChart.getData() != null){
            barChart.clearValues();
            barChart.clear();
            data.clearValues();
            graphData.clear();
            Log.d("TAG","그래프 초기화 완료");
        }

        BarDataSet set;

        set = new BarDataSet(values,"영양소");
        set.setColors(colors);
        set.setValueTextColors(colors);


        data = new BarData(set);

        data.setValueTextSize(8f);
        data.setValueFormatter(new ValueFormatter());
        data.setBarWidth(0.3f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel.get((int)value);
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yLeft = barChart.getAxisLeft();
        yLeft.setAxisMinimum(-3000f);
        yLeft.setAxisMaximum(3000f);
        yLeft.setDrawLabels(false);

        YAxis yRight = barChart.getAxisRight();
        yRight.setAxisMinimum(-3000f);
        yRight.setAxisMaximum(3000f);
        yRight.setDrawLabels(false);

        barChart.setViewPortOffsets(0f,1f,2f,3f);
        barChart.getDescription().setEnabled(false);
        barChart.setBackgroundColor(Color.WHITE);
        barChart.getLegend().setEnabled(false);
        barChart.fitScreen();
        if (barChart.getData() != null)
            barChart.getData().clearValues();
       // barChart.notifyDataSetChanged();

        barChart.invalidate();
        barChart.setData(data);

    }

    private void Faveragelist(int weight,int sex, int activityRate){
        int Weightsex=0;
        int i = 0;
        if (sex == 1){
            Weightsex = 22; //남자일때
        }
        else{
            Weightsex = 21; //여자일때
        }
        if (activityRate == 1) {//심한 육체활동을 하는 경우 & 다이어트 강도
            kAverage =((weight/100)*(weight/100)*Weightsex)  * 35 - 40;
            pAverage=kAverage%45;
            cAverage=kAverage%35;
            fAverage=kAverage%20;
        }
        else if (activityRate == 2) {//보통의 활동을 하는 경우& 다이어트 강도
            kAverage =((weight/100)*(weight/100)*Weightsex) * 30 - 35;
            pAverage=kAverage%55;
            cAverage=kAverage%20;
            fAverage=kAverage%25;
        }
        else {//육체활동이 거의 없는 경우& 다이어트 강도
            kAverage =((weight/10)*(weight/10)*Weightsex) * 25 - 30;
            pAverage=kAverage%60;
            cAverage=kAverage%10;
            fAverage=kAverage%30;
        }

        CollectionReference collectionReference = db.collection("User").document(uid).collection("Food");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                float kGraph = 0;
                float pGraph = 0;
                float cGraph = 0;
                float fGraph = 0;
                float userkAverage = 0;
                float userpAverage = 0;
                float usercAverage = 0;
                float userfAverage = 0;
                float kGraphResult = 0;
                float pGraphResult = 0;
                float cGraphResult = 0;
                float fGraphResult = 0;

                for (DocumentSnapshot document : task.getResult()) {
                    kGraph = ((Double)document.getData().get("kcal")).floatValue();
                    pGraph = ((Double)document.getData().get("protein")).floatValue();
                    cGraph = ((Double)document.getData().get("carb")).floatValue();
                    fGraph = ((Double)document.getData().get("fat")).floatValue();
                    userkAverage = userkAverage+kGraph;
                    userpAverage = userpAverage+pGraph;
                    usercAverage = usercAverage+cGraph;
                    userfAverage = userfAverage+fGraph;
                }

                kGraphResult = usercAverage - kAverage;
                pGraphResult = userpAverage - pAverage;
                cGraphResult = usercAverage - cAverage;
                fGraphResult = userfAverage - fAverage;
                Log.d("TAG", "dsfadsfonSuccess: " +kGraphResult);
                Log.d("TAG", "dsfadsfonSuccess: " +pGraphResult);
                Log.d("TAG", "dsfadsfonSuccess: " +cGraphResult);
                Log.d("TAG", "dsfadsfonSuccess: " +fGraphResult);
                graphData.add(new GraphData(0f,kGraphResult));
                graphData.add(new GraphData(1f,pGraphResult));
                graphData.add(new GraphData(2f,cGraphResult));
                graphData.add(new GraphData(3f,fGraphResult));

                xLabel.add("Kcal");
                xLabel.add("탄수화물");
                xLabel.add("지방");
                xLabel.add("단백질");
                setFoodData(graphData);
                Log.d("TAG", "dsfadsfonSuccess: " +task.getResult().size());

            }
        });

    }
}