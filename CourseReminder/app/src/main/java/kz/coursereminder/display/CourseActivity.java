package kz.coursereminder.display;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import kz.coursereminder.R;
import kz.coursereminder.adapters.CourseAssignmentAdapter;
import kz.coursereminder.adapters.CourseRecyclerItemTouchHelper;
import kz.coursereminder.controllers.CourseActivityController;
import kz.coursereminder.controllers.CourseActivityPopUpManager;

public class CourseActivity extends AppCompatActivity implements View.OnLongClickListener,
CourseRecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    /**
     * Course Activity Controller
     */
    CourseActivityController courseActivityController;
    CourseActivityPopUpManager popUpManager;

    /**
     * Arraylist of all textViews
     * index 0 -> course name
     * index 1 -> course info
     * index 2 -> course notes
     */
    private ArrayList<TextView> textViews = new ArrayList<>();
    CourseAssignmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        // get current course info
        String name = getIntent().getStringExtra("name");
        courseActivityController = new CourseActivityController(this, name);
        popUpManager = new CourseActivityPopUpManager(this, courseActivityController);
        // display current course info
        findTextView();
        displayCourseInfo();
        // Button listeners
        deleteButtonListener();
        assignmentButtonListener();
        courseDisplayEditListener();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                back();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * Finds the layout views
     */
    private void findTextView() {
        textViews.add((TextView) findViewById(R.id.course_name));
        textViews.add((TextView) findViewById(R.id.course_info));
        textViews.add((TextView) findViewById(R.id.course_notes));
    }
    /**
     * Displays the course info on activity
     */
    public void displayCourseInfo() {
        courseActivityController.updateController();
        setTitle(courseActivityController.getCurrentCourse().getName());
        textViews.get(0).setText(courseActivityController.getCurrentCourse().getName());
        textViews.get(1).setText(courseActivityController.getCurrentCourse().getInfo());
        textViews.get(2).setText(courseActivityController.getCurrentCourse().getNotes());
        updateAssignment();
    }
    /**
     * Display assignment listview information
     */
    private void updateAssignment() {
        RecyclerView recyclerView = findViewById(R.id.course_assignment_list);
        adapter = new CourseAssignmentAdapter(this,
                courseActivityController);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        ItemTouchHelper.SimpleCallback itemTouchHelper =
                new CourseRecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);
    }
    /**
     * Add new assignment button
     */
    private void assignmentButtonListener() {
        Button addAssignment = findViewById(R.id.course_add_assignment_button);
        addAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAssignmentCreationActivity();
            }
        });
    }
    /**
     * Start AssignmentCreationActivity
     */
    private void startAssignmentCreationActivity() {
        Intent assignmentCreation = new Intent(this, AssignmentCreationActivity.class);
        assignmentCreation.putExtra("course", courseActivityController.getCurrentCourse());
        startActivityForResult(assignmentCreation, 111);
    }
    /**
     * Add new grade button
     */
    private void gradeButtonListener() {
        Button addGrade = findViewById(R.id.course_add_grade_button);
        addGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO add something
            }
        });
    }
    /**
     * Delete course button
     */
    private void deleteButtonListener() {
        Button delete = findViewById(R.id.course_delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpManager.showCourseDeletePopUp();
            }
        });
    }
    /**
     * Activate textView hold listeners
     */
    private void courseDisplayEditListener() {
        for (int i = 0; i < 3; i++) {
            textViews.get(i).setOnLongClickListener(this);
        }
    }
    /**
     * Go back to dashboard
     */
    public void back() {
        Intent resultIntent = new Intent();
        setResult(AppCompatActivity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.course_name:
                popUpManager.showCourseNameEditPopUp();
                return true;
            case R.id.course_info:
                popUpManager.showCourseInfoEditPopUp();
                return true;
            case R.id.course_notes:
                popUpManager.showCourseNotesEditPopUp();
        }
        return false;
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        popUpManager.showAssignmentDeletePopUp(position, adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * OnResume of Activity
     */
    @Override
    protected void onResume() {
        super.onResume();
        displayCourseInfo();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 111) && (resultCode == AppCompatActivity.RESULT_OK)) {
            displayCourseInfo();
        }
    }
}
