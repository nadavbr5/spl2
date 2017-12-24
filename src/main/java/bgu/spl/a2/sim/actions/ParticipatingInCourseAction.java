package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * this action should be in course actor
 */

public class ParticipatingInCourseAction extends Action<Boolean> {
    private String Student;
    private List<String> Grade=new ArrayList<>();
    private int grade;

    @Override
    protected void start() {
        this.name = "Participate In Course";
        actionState.addRecord(name);
        grade = (Grade.get(0).equals("-") ? 0 : Integer.parseInt(Grade.get(0)));
        ArrayList<Action<?>> actions = new ArrayList<>();
        List<String> pre = ((CoursePrivateState) this.actionState).getPrequisites();
        Action<Boolean> action = new Action<Boolean>() {
            @Override
            protected void start() {
                complete(((StudentPrivateState) this.actionState).getGrades().keySet().containsAll(pre));
            }
        };
        actions.add(action);
        then(actions, () -> addStudentToCourse(action));
        sendMessage(action, Student, new StudentPrivateState());
    }
    private void addStudentToCourse(Action<Boolean> action) {
        if (action.getResult().get() && ((CoursePrivateState) this.actionState).registerStudent(Student)) {
            String course = this.actionActor;
            ArrayList<Action<?>> actions = new ArrayList<>();
            Action<Boolean> addGradeInStudentAction = new Action<Boolean>() {
                @Override
                protected void start() {
                    complete(((StudentPrivateState) this.actionState).addGrade(course, grade));
                }
            };
            actions.add(addGradeInStudentAction);
            then(actions, () -> {
                complete(addGradeInStudentAction.getResult().get());
            });
            sendMessage(addGradeInStudentAction, Student, new StudentPrivateState());
        } else complete(false);
    }


    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setStudent(String student) {
        Student = student;
    }
}

