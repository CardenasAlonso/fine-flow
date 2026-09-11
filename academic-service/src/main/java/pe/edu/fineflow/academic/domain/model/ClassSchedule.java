package pe.edu.fineflow.academic.domain.model;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
public class ClassSchedule extends BaseDomainEntity {

    private String scheduleVersionId;
    private String courseAssignmentId;
    private String sectionId;
    private String teacherId;
    private String classroomId;
    private String timeSlotId;
    private Integer dayOfWeek;
    private String weekType;
    private String colorHex;
    private String notes;
    private Integer isActive;
    private String createdBy;

    public static ClassSchedule create(String scheduleVersionId, String courseAssignmentId,
                                        String sectionId, String teacherId, String timeSlotId,
                                        Integer dayOfWeek) {
        ClassSchedule cs = new ClassSchedule();
        cs.setScheduleVersionId(scheduleVersionId);
        cs.setCourseAssignmentId(courseAssignmentId);
        cs.setSectionId(sectionId);
        cs.setTeacherId(teacherId);
        cs.setTimeSlotId(timeSlotId);
        cs.setDayOfWeek(dayOfWeek);
        cs.setIsActive(1);
        return cs;
    }

    public void applyFrom(ClassSchedule source) {
        this.classroomId = source.classroomId;
        this.timeSlotId = source.timeSlotId;
        this.dayOfWeek = source.dayOfWeek;
        this.weekType = source.weekType;
        this.colorHex = source.colorHex;
        this.notes = source.notes;
    }
}
