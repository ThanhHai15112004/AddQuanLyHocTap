package com.example.taskmanager.screen.schedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.taskmanager.PublicConstants;
import com.example.taskmanager.R;
import com.example.taskmanager.databinding.FragmentScheduleBinding;
import com.example.taskmanager.model.Schedule;
import com.example.taskmanager.model.Subject;
import com.example.taskmanager.screen.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.app.TimePickerDialog;

public class ScheduleFragment extends Fragment implements ScheduleAdapter.onLongClickInterfaces {
    DatabaseHelper db ;
    int daySelect ;
    String[] days = {"Chủ nhật", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"};

    private ScheduleAdapter scheduleAdapter;
    private List<Schedule> allSchedules;
    FragmentScheduleBinding binding;
    public ScheduleFragment() {
    }


    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new DatabaseHelper(getContext());
        binding.recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(getContext()));

        allSchedules = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(allSchedules);
        scheduleAdapter.setInterfaces(this);
        binding.recyclerViewSchedule.setAdapter(scheduleAdapter);

        Calendar calendar = Calendar.getInstance();
        daySelect = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String today = days[daySelect];
        binding.selectedDate.setText("Lịch học "+today);
        updateScheduleList(daySelect);

        binding.textDateMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMonthPickerDialog();
            }
        });

        binding.addSchedules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddScheduleDialog();
            }
        });

        binding.ivCalendarNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daySelect = (daySelect+1)%7;
                updateScheduleList(daySelect);
                binding.selectedDate.setText("Lịch học "+ days[daySelect]);
            }
        });

        binding.ivCalendarPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(daySelect == 0 ) {
                    daySelect = 6;
                    updateScheduleList(daySelect);
                    binding.selectedDate.setText("Lịch học "+ days[daySelect]);
                    return;
                }
                daySelect = daySelect -1;
                updateScheduleList(daySelect);
                binding.selectedDate.setText("Lịch học "+ days[daySelect]);
            }
        });
    }

    private void showAddScheduleDialog() {
        List<Subject> listSubject = db.getSubjectsByUserId(PublicConstants.user.getId());
        List<String> listSubjectString = new ArrayList<>();

        if(listSubject.isEmpty()) {
            Toast.makeText(getContext(),"Vui lòng thêm môn học",Toast.LENGTH_LONG).show();
            return;
        }

        for (Subject subject: listSubject) {
            listSubjectString.add(subject.getName());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm lịch học");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_schedule, null);
        builder.setView(view);

        EditText edtDescription = view.findViewById(R.id.edtDescription);
        EditText edtStartTime = view.findViewById(R.id.edtStartTime);
        EditText edtEndTime = view.findViewById(R.id.edtEndTime);
        EditText edtLocation = view.findViewById(R.id.edtLocation);

        // Set click listeners for time fields
        edtStartTime.setOnClickListener(v -> showTimePickerDialog(edtStartTime));
        edtEndTime.setOnClickListener(v -> showTimePickerDialog(edtEndTime));

        Spinner spinnerSubject = view.findViewById(R.id.spinnerSubject);
        Spinner spinnerDayOfWeek = view.findViewById(R.id.spinnerDayOfWeek);
        ArrayAdapter<String> adapterDays = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                days
        );
        adapterDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDayOfWeek.setAdapter(adapterDays);

        ArrayAdapter<String> adapterSubject = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                listSubjectString
        );
        adapterSubject.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerSubject.setAdapter(adapterSubject);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String title = listSubject.get(spinnerSubject.getSelectedItemPosition()).getName();
            String description = edtDescription.getText().toString();
            String startTime = edtStartTime.getText().toString();
            String endTime = edtEndTime.getText().toString();
            String location = edtLocation.getText().toString();
            int dayOfWeek = spinnerDayOfWeek.getSelectedItemPosition();
            if(title.isEmpty() || description.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || location.isEmpty()) {
                Toast.makeText(getContext(),"Vui lòng nhập đủ thông tin", Toast.LENGTH_LONG).show();
                return;
            }
            String repeatType = "WEEKLY";

            Schedule newSchedule = new Schedule(
                    listSubject.get(spinnerSubject.getSelectedItemPosition()).getCode(),
                    title,
                    description,
                    dayOfWeek,
                    startTime,
                    endTime,
                    location,
                    repeatType
            );
            db.addSchedule(newSchedule);
            updateScheduleList(daySelect);
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showTimePickerDialog(EditText timeField) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute1) -> {
                    // Format the time as HH:MM
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                    timeField.setText(formattedTime);
                },
                hour,
                minute,
                true // 24-hour format
        );

        timePickerDialog.show();
    }

    private void updateScheduleList(int dayOfWeek) {
        allSchedules = db.getSchedulesByUserId(PublicConstants.user.getId());
        List<Schedule> filtered = new ArrayList<>();
        for (Schedule s : allSchedules) {
            if (s.getDayOfWeek() == dayOfWeek && !filtered.contains(s)) {
                filtered.add(s);
            }
        }
        scheduleAdapter.updateData(filtered);
    }

    private void showMonthPickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, month, dayOfMonth);

                    String selectedMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                            .format(selectedCalendar.getTime());
                    binding.textDateMonth.setText(selectedMonth);

                    int dayOfWeek = selectedCalendar.get(Calendar.DAY_OF_WEEK);
                    daySelect = dayOfWeek - 1;
                    String selectedDay = days[daySelect];
                    binding.selectedDate.setText("Lịch học "+selectedDay);
                    updateScheduleList(daySelect);
                },
                currentYear,
                currentMonth,
                1
        );
        try {
            dialog.getDatePicker().findViewById(
                    Resources.getSystem().getIdentifier("day", "id", "android")
            ).setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.setTitle("Chọn thời gian");
        dialog.show();
    }

    @Override
    public void onLongClick(Schedule schedule) {
        showDialogSelectEditOrDelete(schedule);
    }

    private void showDialogSelectEditOrDelete(Schedule schedule) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Tùy chọn");

        String[] options = {"Sửa lịch học", "Xóa lịch học"};

        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Sửa
                showEditScheduleDialog(schedule);
            } else if (which == 1) {
                // Xóa
                new AlertDialog.Builder(getContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa lịch học này?")
                        .setPositiveButton("Xóa", (confirmDialog, confirmWhich) -> {
                            db.deleteSchedule(schedule.getId());
                            updateScheduleList(daySelect);
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showEditScheduleDialog(Schedule schedule) {
        List<Subject> listSubject = db.getSubjectsByUserId(PublicConstants.user.getId());
        List<String> listSubjectString = new ArrayList<>();

        if (listSubject.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng thêm môn học", Toast.LENGTH_LONG).show();
            return;
        }

        for (Subject subject : listSubject) {
            listSubjectString.add(subject.getName());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sửa lịch học");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_schedule, null);
        builder.setView(view);

        EditText edtDescription = view.findViewById(R.id.edtDescription);
        EditText edtStartTime = view.findViewById(R.id.edtStartTime);
        EditText edtEndTime = view.findViewById(R.id.edtEndTime);
        EditText edtLocation = view.findViewById(R.id.edtLocation);
        Spinner spinnerSubject = view.findViewById(R.id.spinnerSubject);
        Spinner spinnerDayOfWeek = view.findViewById(R.id.spinnerDayOfWeek);

        edtStartTime.setOnClickListener(v -> showTimePickerDialog(edtStartTime));
        edtEndTime.setOnClickListener(v -> showTimePickerDialog(edtEndTime));

        // Set adapter for day of week spinner
        ArrayAdapter<String> adapterDays = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                days
        );
        adapterDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDayOfWeek.setAdapter(adapterDays);

        // Set adapter for subject spinner
        ArrayAdapter<String> adapterSubject = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                listSubjectString
        );
        adapterSubject.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(adapterSubject);

        // Set default values
        edtDescription.setText(schedule.getDescription());
        edtStartTime.setText(schedule.getStartTime());
        edtEndTime.setText(schedule.getEndTime());
        edtLocation.setText(schedule.getLocation());
        spinnerDayOfWeek.setSelection(schedule.getDayOfWeek());

        // Tìm vị trí subject hiện tại
        for (int i = 0; i < listSubject.size(); i++) {
            if (listSubject.get(i).getCode().equals(schedule.getSubjectId())) {
                spinnerSubject.setSelection(i);
                break;
            }
        }

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String title = listSubject.get(spinnerSubject.getSelectedItemPosition()).getName();
            String description = edtDescription.getText().toString();
            String startTime = edtStartTime.getText().toString();
            String endTime = edtEndTime.getText().toString();
            String location = edtLocation.getText().toString();
            int dayOfWeek = spinnerDayOfWeek.getSelectedItemPosition();

            if (title.isEmpty() || description.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || location.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_LONG).show();
                return;
            }

            // Cập nhật lịch
            Schedule updatedSchedule = new Schedule(
                    schedule.getId(),
                    listSubject.get(spinnerSubject.getSelectedItemPosition()).getCode(),
                    title,
                    description,
                    dayOfWeek,
                    startTime,
                    endTime,
                    location,
                    schedule.getRepeatType()
            );

            db.updateSchedule(updatedSchedule);
            updateScheduleList(daySelect);
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

}