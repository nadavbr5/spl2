package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * this action should be in course actor
 *
 */

public class ParticipatingInCourseAction extends Action<Boolean>{
    private String studentName;
    private int grade;

    public ParticipatingInCourseAction(String studentName, int grade){
        this.studentName=studentName;
        this.grade=grade;
    }

    @Override
    protected void start() {
        this.name = "Participate In Course";
        actionState.addRecord(name);
        ArrayList<Action<?>> actions= new ArrayList<>();
        List<String > pre = ((CoursePrivateState)this.actionState).getPrequisites();
        Action<Boolean> action = new Action<Boolean>() {
            @Override
            protected void start() {
                complete(((StudentPrivateState)this.actionState).getGrades().keySet().containsAll(pre));
            }
        };
        actions.add(action);
        sendMessage(action,studentName,new StudentPrivateState());
        then(actions,()-> addStudentToCourse(action));
    }

    private void addStudentToCourse(Action<Boolean> action) {
        if (action.getResult().get() && ((CoursePrivateState) this.actionState).registerStudent(studentName)) {
            String course = this.actionActor;
            ArrayList<Action<?>> actions=new ArrayList<>();
            Action<Boolean> addGradeInStudentAction = new Action<Boolean>() {
                @Override
                protected void start() {
                    complete(((StudentPrivateState) this.actionState).addGrade(course, grade));
                }
            };
            actions.add(addGradeInStudentAction);
   sendMessage(addGradeInStudentAction,studentName,new StudentPrivateState());
   then(actions,()->{
       complete(addGradeInStudentAction.getResult().get());
   });
}else complete(false);
    }


}

