package pe.edu.fineflow.academic.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.edu.fineflow.academic.application.port.in.ManageAcademicLevelUseCase;
import pe.edu.fineflow.academic.application.port.in.ManageAcademicPeriodUseCase;
import pe.edu.fineflow.academic.application.port.in.ManageClassScheduleUseCase;
import pe.edu.fineflow.academic.application.port.in.ManageClassTaskUseCase;
import pe.edu.fineflow.academic.application.port.in.ManageClassroomUseCase;
import pe.edu.fineflow.academic.application.port.in.ManageCourseAssignmentUseCase;
import pe.edu.fineflow.academic.application.service.ManageAcademicLevelService;
import pe.edu.fineflow.academic.application.service.ManageAcademicPeriodService;
import pe.edu.fineflow.academic.application.service.ManageClassScheduleService;
import pe.edu.fineflow.academic.application.service.ManageClassTaskService;
import pe.edu.fineflow.academic.application.service.ManageClassroomService;
import pe.edu.fineflow.academic.application.service.ManageCourseAssignmentService;
import pe.edu.fineflow.academic.domain.port.out.AcademicLevelRepositoryPort;
import pe.edu.fineflow.academic.domain.port.out.AcademicPeriodRepositoryPort;
import pe.edu.fineflow.academic.domain.port.out.ClassScheduleRepositoryPort;
import pe.edu.fineflow.academic.domain.port.out.ClassTaskRepositoryPort;
import pe.edu.fineflow.academic.domain.port.out.ClassroomRepositoryPort;
import pe.edu.fineflow.academic.domain.port.out.CourseAssignmentRepositoryPort;
import pe.edu.fineflow.academic.domain.port.out.TimeSlotRepositoryPort;

@Configuration
public class BeanConfiguration {

    @Bean
    public ManageAcademicLevelUseCase manageAcademicLevelUseCase(AcademicLevelRepositoryPort repository) {
        return new ManageAcademicLevelService(repository);
    }

    @Bean
    public ManageAcademicPeriodUseCase manageAcademicPeriodUseCase(AcademicPeriodRepositoryPort repository) {
        return new ManageAcademicPeriodService(repository);
    }

    @Bean
    public ManageClassroomUseCase manageClassroomUseCase(ClassroomRepositoryPort repositoryPort) {
        return new ManageClassroomService(repositoryPort);
    }

    @Bean
    public ManageClassScheduleUseCase manageClassScheduleUseCase(ClassScheduleRepositoryPort repositoryPort, TimeSlotRepositoryPort timeSlotRepository) {
        return new ManageClassScheduleService(repositoryPort, timeSlotRepository);
    }

    @Bean
    public ManageClassTaskUseCase manageClassTaskUseCase(ClassTaskRepositoryPort repositoryPort) {
        return new ManageClassTaskService(repositoryPort);
    }

    @Bean
    public ManageCourseAssignmentUseCase manageCourseAssignmentUseCase(CourseAssignmentRepositoryPort repositoryPort) {
        return new ManageCourseAssignmentService(repositoryPort);
    }
}
