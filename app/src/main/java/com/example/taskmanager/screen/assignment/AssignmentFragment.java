package com.example.taskmanager.screen.assignment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.taskmanager.databinding.FragmentAssignmentBinding;
import com.example.taskmanager.model.Assignment;
import com.example.taskmanager.model.Notification;
import com.example.taskmanager.model.Subject;
import com.example.taskmanager.screen.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignmentFragment extends Fragment {
    List<Subject> listSubject ;

    String[] priorityLever = {"low", "medium", "high"};
    AssignmentAdapter adapter;
    List<Assignment> assignmentList;
    DatabaseHelper db ;
    FragmentAssignmentBinding binding;
    public AssignmentFragment() {
    }
    public static AssignmentFragment newInstance() {
        return new AssignmentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAssignmentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(getContext());
        listSubject = db.getSubjectsByUserId(PublicConstants.user.getId());
        showAssignment();
        binding.btnAddAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAssignmentDialog();
            }
        });
    }

    void showAssignment() {
        assignmentList = new ArrayList<>();
        if(listSubject.isEmpty()) {
            Toast.makeText(getContext(),"Vui long thiet lap mon hoc", Toast.LENGTH_LONG).show();
            return;
        }

        for (Subject subject : listSubject) {
            List<Assignment> temp = db.getAssignmentsBySubjectId(subject.getCode());
            if(temp.isEmpty()) {
                continue;
            }
            assignmentList.addAll(temp);
        }

        adapter = new AssignmentAdapter(assignmentList, getContext());
        binding.recyclerAssignments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerAssignments.setAdapter(adapter);
    }

    private void showAddAssignmentDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_assignment);

        EditText edtTitle = dialog.findViewById(R.id.edtTitle);
        EditText edtDescription = dialog.findViewById(R.id.edtDescription);
        EditText edtDueDate = dialog.findViewById(R.id.edtDueDate);
        EditText edtRemindAt = dialog.findViewById(R.id.edtRemindAt);
        Spinner spnPriority = dialog.findViewById(R.id.spnPriority);
        Spinner spnSubject = dialog.findViewById(R.id.spnSubject);

        Button btnSave = dialog.findViewById(R.id.btnSave);

        // Set up Priority Spinner
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, priorityLever);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPriority.setAdapter(priorityAdapter);

        List<Subject> listSubject = db.getSubjectsByUserId(PublicConstants.user.getId());
        List<String> listSubjectString = new ArrayList<>();

        if (listSubject.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng thêm môn học trước", Toast.LENGTH_LONG).show();
            return;
        }

        for (Subject subject : listSubject) {
            listSubjectString.add(subject.getName());
        }

        // Set up Subject Spinner
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listSubjectString);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSubject.setAdapter(subjectAdapter);

        // Show DatePickerDialog for Due Date
        edtDueDate.setOnClickListener(v -> showDateTimeDialog(edtDueDate));

        // Show DatePickerDialog for Remind Time
        edtRemindAt.setOnClickListener(v -> showDateTimeDialog(edtRemindAt));

        // Save button action
        btnSave.setOnClickListener(view -> {
            String dueDateStr = edtDueDate.getText().toString();
            String remindAtStr = edtRemindAt.getText().toString();

            if (dueDateStr.isEmpty() || remindAtStr.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_LONG).show();
                return;
            }

            // Convert to Date objects for comparison
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            try {
                Date dueDate = sdf.parse(dueDateStr);
                Date remindAt = sdf.parse(remindAtStr);

                // Get current date and time
                Date currentDate = new Date();

                // Check if the due date and remind date are after current time
                if (dueDate.before(currentDate) || remindAt.before(currentDate)) {
                    Toast.makeText(getContext(), "Ngày hạn nộp và ngày nhắc phải sau thời điểm hiện tại", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if due date is after remind date
                if (dueDate.before(remindAt)) {
                    Toast.makeText(getContext(), "Ngày hạn nộp phải sau ngày nhắc", Toast.LENGTH_LONG).show();
                    return;
                }

                // Create a new Assignment object
                Assignment assignment = new Assignment(
                        listSubject.get(spnSubject.getSelectedItemPosition()).getCode(),
                        edtTitle.getText().toString(),
                        edtDescription.getText().toString(),
                        dueDateStr,
                        spnPriority.getSelectedItem().toString(),
                        "pending"
                );
                long assignmentId = db.addAssignment(assignment);

                // Register reminder notification
                Notification notification = new Notification(
                        PublicConstants.user.getId(),
                        assignmentId,
                        0, // Chưa xác định scheduleId, có thể thêm vào sau
                        remindAtStr,
                        false, // Is sent, ban đầu là false
                        edtDescription.getText().toString()
                );
                registerReminderNotification(notification, assignmentId);
                showAssignment();
                Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_LONG).show();
                dialog.dismiss();

            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Định dạng ngày tháng không hợp lệ", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    public void registerReminderNotification(Notification notification, long assignmentId) {
        // Kiểm tra định dạng thời gian "remindAt" có hợp lệ hay không
        if (!isValidTimeFormat(notification.getRemindAt())) {
            Toast.makeText(getContext(), "Thời gian nhắc nhở không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thời gian nhắc nhở từ String vào Calendar
        Calendar reminderTime = getReminderTime(notification.getRemindAt());
        if (reminderTime == null) {
            Toast.makeText(getContext(), "Lỗi thời gian nhắc nhở", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thêm thông báo vào cơ sở dữ liệu
        long notificationId = db.addNotification(notification);

        if (notificationId != -1) {
            scheduleNotification(reminderTime, notificationId, assignmentId);
        } else {
            Toast.makeText(getContext(), "Lỗi khi lưu thông báo", Toast.LENGTH_SHORT).show();
        }
    }

    // Kiểm tra định dạng thời gian hợp lệ (ví dụ: "dd/MM/yyyy HH:mm")
    private boolean isValidTimeFormat(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            sdf.setLenient(false);
            sdf.parse(time);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Chuyển đổi String thành Calendar
    private Calendar getReminderTime(String remindAt) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(remindAt);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            return null;
        }
    }

    private void scheduleNotification(Calendar reminderTime, long notificationId, long assignmentId) {
        // Cấu hình AlarmManager để gửi thông báo
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), NotificationReceiver.class);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("assignmentId", assignmentId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                (int) notificationId, // Sử dụng ID thông báo làm requestCode
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Cài đặt alarm
        if (alarmManager != null) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime.getTimeInMillis(),
                    pendingIntent
            );
        }
    }

    private void showDateTimeDialog(final EditText editText) {
        // Get current date and time
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
            // After date is selected, show TimePickerDialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view1, selectedHour, selectedMinute) -> {
                // Set selected date and time to the EditText
                String dateTime = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear + " " + selectedHour + ":" + selectedMinute;
                editText.setText(dateTime);
            }, hour, minute, true);
            timePickerDialog.show();
        }, year, month, dayOfMonth);

        datePickerDialog.show();
    }
}