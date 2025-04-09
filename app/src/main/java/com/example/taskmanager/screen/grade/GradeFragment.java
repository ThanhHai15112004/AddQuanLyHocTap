package com.example.taskmanager.screen.grade;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.taskmanager.PublicConstants;
import com.example.taskmanager.R;
import com.example.taskmanager.databinding.FragmentGradeBinding;
import com.example.taskmanager.model.Grade;
import com.example.taskmanager.model.Subject;
import com.example.taskmanager.screen.DatabaseHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
public class GradeFragment extends Fragment {
    GradeAdapter adapter;
    DatabaseHelper db;
    List<Subject> subjectList ;
    List<String> listNameSubject = new ArrayList<>();
    List<Float> listPoint = new ArrayList<>();
    FragmentGradeBinding binding;
    public GradeFragment() {
    }
    public static GradeFragment newInstance() {
        return new GradeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGradeBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(getContext());
        subjectList = db.getSubjectsByUserId(PublicConstants.user.getId());
        displayGrades();
        displayTrungBinhDiem();
        binding.btnAddGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddGradeDialogWithSubjectSelection();
            }
        });

        binding.showGradeChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị dialog với biểu đồ
                showScoreChartDialog();
            }
        });
    }

    private void showScoreChartDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_chart, null);

        BarChart barChart = view.findViewById(R.id.barChart);

        // Tạo danh sách BarEntry (x: index môn học, y: điểm trung bình)
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < listPoint.size(); i++) {
            entries.add(new BarEntry(i, listPoint.get(i)));
        }

        // Tạo DataSet và thêm vào BarChart
        BarDataSet dataSet = new BarDataSet(entries, "Điểm Trung Bình");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // Tạo BarData và set vào biểu đồ
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.invalidate(); // Cập nhật biểu đồ

        // Tùy chỉnh trục X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(listNameSubject));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Tùy chỉnh trục Y
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);  // Đảm bảo trục y bắt đầu từ 0
        barChart.getAxisRight().setEnabled(false); // Tắt trục y bên phải

        // Tạo AlertDialog với view đã tạo
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Biểu Đồ Điểm Trung Bình")
                .setView(view)
                .setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
    private void displayTrungBinhDiem() {
        listNameSubject = new ArrayList<>();
        listPoint = new ArrayList<>();
        double sumGrage = 0;
        double sumCount = 0;
        if(subjectList.isEmpty()) return;
        for (Subject subject : subjectList) {
            double point = calculateAverage(db.getGradeListBySubjectId(subject.getCode()));
            if(point > 0) {
                listNameSubject.add(subject.getName());
                listPoint.add(Float.parseFloat(point+""));
                sumGrage += point;
                sumCount+=1;
            }
        }
        binding.tvTrungBinh.setText(String.format("Điểm trung bình là: %.2f",sumGrage/sumCount));
    }

    public double calculateAverage(List<Grade> listGrades) {
        if (listGrades.isEmpty()) return 0;
        double total = 0;
        int sumCount = 0;
        for (Grade grade : listGrades) {

            total+= grade.getScore();
            sumCount+= Integer.parseInt(grade.getGradeType());
        }
        return total / sumCount;
    }

    private void displayGrades() {
        adapter = new GradeAdapter(subjectList, getContext());
        binding.rvListGrade.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvListGrade.setAdapter(adapter);
    }

    private void showAddGradeDialogWithSubjectSelection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_grade, null);
        builder.setView(view);

        Spinner spinnerSubjects = view.findViewById(R.id.spinnerSubjects);
        EditText edtTitle = view.findViewById(R.id.edtTitle);
        EditText edtScore = view.findViewById(R.id.edtScore);
        EditText edtDate = view.findViewById(R.id.edtDate);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnAdd = view.findViewById(R.id.btnAdd);

        AlertDialog dialog = builder.create();

        final List<Subject> subjectList = db.getSubjectsByUserId(PublicConstants.user.getId());
        List<String> subjectNames = new ArrayList<>();
        for (Subject subject : subjectList) {
            subjectNames.add(subject.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, subjectNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubjects.setAdapter(adapter);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnAdd.setOnClickListener(v -> {
            int selectedPosition = spinnerSubjects.getSelectedItemPosition();
            if (selectedPosition == -1) {
                Toast.makeText(getContext(), "Vui lòng chọn môn học", Toast.LENGTH_SHORT).show();
                return;
            }

            String subjectId = subjectList.get(selectedPosition).getCode();
            String title = edtTitle.getText().toString().trim();
            String scoreStr = edtScore.getText().toString().trim();
            String date = edtDate.getText().toString().trim();

            if (title.isEmpty() || scoreStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double score = Double.parseDouble(scoreStr);

            Grade grade = new Grade(0, subjectId, title, score, 10, "1", date);
            db.addGrade(grade);

            Toast.makeText(getContext(), "Đã thêm điểm", Toast.LENGTH_SHORT).show();
            dialog.dismiss();

            updateScreen();
        });

        dialog.show();
    }

    private void updateScreen() {
        displayGrades();
        displayTrungBinhDiem();
    }


}