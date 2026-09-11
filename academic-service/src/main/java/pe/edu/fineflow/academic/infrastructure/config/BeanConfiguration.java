package pe.edu.fineflow.academic.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.edu.fineflow.academic.application.port.in.ManageAcademicLevelUseCase;
import pe.edu.fineflow.academic.application.port.in.ManageAcademicPeriodUseCase;
import pe.edu.fineflow.academic.application.port.in.ManageClassScheduleUseCase;
import pe.edu.fineflow.academic.application.port.in.ManageClassTaskUseCase;
import pe.edu.fineflow.academic.application.port.in.ManageClassroomUseCase;
import pe.edu.fineflow.academic.application.port.in.ManageCourseAssignmentUseCase;
import pe.edu.fineflow.academic.application.usecase.ManageAcademicLevelUseCaseImpl;
import pe.edu.fineflow.academic.application.usecase.ManageAcademicPeriodUseCaseImpl;
import pe.edu.fineflow.academic.application.usecase.ManageClassScheduleUseCaseImpl;
import pe.edu.fineflow.academic.application.usecase.ManageClassTaskUseCaseImpl;
import pe.edu.fineflow.academic.application.usecase.ManageClassroomUseCaseImpl;
import pe.edu.fineflow.academic.application.usecase.ManageCourseAssignmentUseCaseImpl;
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
        return new ManageAcademicLevelUseCaseImpl(repository);
    }

    @Bean
    public ManageAcademicPeriodUseCase manageAcademicPeriodUseCase(AcademicPeriodRepositoryPort repository) {
        return new ManageAcademicPeriodUseCaseImpl(repository);
    }

    @Bean
    public ManageClassroomUseCase manageClassroomUseCase(ClassroomRepositoryPort repositoryPort) {
        return new ManageClassroomUseCaseImpl(repositoryPort);
    }

    @Bean
    public ManageClassScheduleUseCase manageClassScheduleUseCase(ClassScheduleRepositoryPort repositoryPort, TimeSlotRepositoryPort timeSlotRepository) {
        return new ManageClassScheduleUseCaseImpl(repositoryPort, timeSlotRepository);
    }

    @Bean
    public ManageClassTaskUseCase manageClassTaskUseCase(ClassTaskRepositoryPort repositoryPort) {
        return new ManageClassTaskUseCaseImpl(repositoryPort);
    }

    @Bean
    public ManageCourseAssignmentUseCase manageCourseAssignmentUseCase(CourseAssignmentRepositoryPort repositoryPort) {
        return new ManageCourseAssignmentUseCaseImpl(repositoryPort);
    }
}
