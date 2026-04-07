// =============================================================================
// FINE FLOW v4.0 - TypeScript Types
// Based on FINEFLOW_MASTER_v4.0 Oracle SQL Schema
// =============================================================================

// -----------------------------------------------------------------------------
// ENUMS AND UNION TYPES
// -----------------------------------------------------------------------------

export type UserRole = 'ADMIN' | 'COORDINATOR' | 'TEACHER' | 'STUDENT' | 'GUARDIAN';

export type InstitutionType = 'K12' | 'INSTITUTE' | 'UNIVERSITY';

export type SchoolStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';

export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'LOCKED';

export type StudentStatus = 'ACTIVE' | 'INACTIVE' | 'GRADUATED' | 'TRANSFERRED' | 'EGRESADO';

export type TeacherStatus = 'ACTIVE' | 'INACTIVE' | 'ON_LEAVE';

export type DocumentType = 'DNI' | 'CE' | 'PASSPORT' | 'OTHER';

export type BloodType = 'A+' | 'A-' | 'B+' | 'B-' | 'AB+' | 'AB-' | 'O+' | 'O-';

export type PeriodType = 'BIMESTER' | 'TRIMESTER' | 'SEMESTER' | 'ANNUAL';

export type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'LATE' | 'EXCUSED' | 'HOLIDAY';

export type RecordMethod = 'MANUAL' | 'QR' | 'BULK' | 'SYSTEM';

export type TaskType = 'EXAM' | 'QUIZ' | 'HOMEWORK' | 'PROJECT' | 'ORAL' | 'LAB' | 'PARTICIPATION' | 'OTHER';

export type AchievementLevel = 'AD' | 'A' | 'B' | 'C';

export type GuardianRelationship = 'FATHER' | 'MOTHER' | 'GRANDPARENT' | 'SIBLING' | 'UNCLE' | 'LEGAL_GUARDIAN' | 'OTHER';

export type NotificationType = 
  | 'ATTENDANCE_ALERT' 
  | 'SCORE_REGISTERED' 
  | 'JUSTIFICATION_APPROVED'
  | 'JUSTIFICATION_REJECTED' 
  | 'PERIOD_CLOSING' 
  | 'QR_ROTATED'
  | 'REPORT_READY' 
  | 'SYSTEM_MAINTENANCE' 
  | 'GENERAL';

export type JustificationStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'EXPIRED';

export type ReportType = 
  | 'ATTENDANCE_SUMMARY' 
  | 'ATTENDANCE_STUDENT'
  | 'GRADES_SECTION' 
  | 'GRADES_STUDENT' 
  | 'BOLETIN'
  | 'NOMINA' 
  | 'ACTA_NOTAS' 
  | 'DASHBOARD_STATS'
  | 'BLOCKCHAIN_EXPORT' 
  | 'CUSTOM';

export type ReportFormat = 'PDF' | 'XLSX' | 'CSV' | 'JSON';

export type ReportJobStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'EXPIRED';

export type SubscriptionPlan = 'FREE' | 'BASIC' | 'STANDARD' | 'PREMIUM' | 'ENTERPRISE';

export type BillingCycle = 'MONTHLY' | 'ANNUAL' | 'BIANNUAL';

export type SubscriptionStatus = 'ACTIVE' | 'SUSPENDED' | 'CANCELLED' | 'EXPIRED';

export type DayOfWeek = 1 | 2 | 3 | 4 | 5 | 6 | 7;

export type WeekType = 'ALL' | 'ODD' | 'EVEN';

export type SectionChangeReason = 
  | 'ENROLLMENT' 
  | 'PROMOTION' 
  | 'TRANSFER_IN' 
  | 'TRANSFER_OUT'
  | 'GRADUATION' 
  | 'WITHDRAWAL' 
  | 'SECTION_CHANGE' 
  | 'REINSTATEMENT';

export type BlockchainEventType = 
  | 'GENESIS' 
  | 'ATTENDANCE' 
  | 'SCORE' 
  | 'ENROLLMENT'
  | 'TRANSFER' 
  | 'PROMOTION' 
  | 'USER_ACTION' 
  | 'SYSTEM';

export type FeatureCategory = 
  | 'ACADEMIC' 
  | 'ATTENDANCE' 
  | 'GRADING' 
  | 'ADMINISTRATION'
  | 'COMMUNICATION' 
  | 'INNOVATION' 
  | 'SECURITY' 
  | 'INTEGRATION';

// -----------------------------------------------------------------------------
// CORE ENTITIES
// -----------------------------------------------------------------------------

export interface School {
  id: string;
  name: string;
  document_number: string;
  institution_type: InstitutionType;
  address?: string;
  phone?: string;
  email?: string;
  logo_url?: string;
  status: SchoolStatus;
  created_at: Date;
  updated_at: Date;
}

export interface User {
  id: string;
  school_id: string;
  email: string;
  password_hash?: string;
  role: UserRole;
  first_name: string;
  last_name: string;
  status: UserStatus;
  avatar_url?: string;
  last_login_at?: Date;
  created_at?: Date;
  updated_at?: Date;
}

// -----------------------------------------------------------------------------
// ACADEMIC STRUCTURE
// -----------------------------------------------------------------------------

export interface AcademicLevel {
  id: string;
  school_id: string;
  name: string;
  order_num: number;
  is_active: boolean;
}

export interface SchoolYear {
  id: string;
  school_id: string;
  academic_level_id: string;
  name: string;
  grade_number: number;
  calendar_year: number;
  is_active: boolean;
}

export interface AcademicPeriod {
  id: string;
  school_id: string;
  school_year_id: string;
  name: string;
  period_type: PeriodType;
  start_date: Date;
  end_date: Date;
  is_active: boolean;
  created_at: Date;
}

export interface Section {
  id: string;
  school_id: string;
  school_year_id: string;
  name: string;
  max_capacity: number;
  tutor_id?: string;
  is_active: boolean;
  created_at: Date;
}

// -----------------------------------------------------------------------------
// PEOPLE
// -----------------------------------------------------------------------------

export interface Student {
  id: string;
  school_id: string;
  section_id?: string;
  user_id?: string;
  first_name: string;
  last_name: string;
  document_type: DocumentType;
  document_number: string;
  birth_date?: Date;
  blood_type?: BloodType;
  address?: string;
  photo_url?: string;
  qr_secret?: string;
  qr_rotated_at?: Date;
  status: StudentStatus;
  created_at: Date;
  updated_at: Date;
}

export interface Guardian {
  id: string;
  school_id: string;
  user_id?: string;
  student_id: string;
  first_name: string;
  last_name: string;
  document_number?: string;
  relationship: GuardianRelationship;
  phone?: string;
  email?: string;
  is_primary_contact: boolean;
  created_at: Date;
}

export interface Teacher {
  id: string;
  school_id: string;
  user_id?: string;
  first_name: string;
  last_name: string;
  document_number: string;
  specialty?: string;
  phone?: string;
  status: TeacherStatus;
  hired_at?: Date;
  created_at: Date;
  updated_at: Date;
}

// -----------------------------------------------------------------------------
// CURRICULUM (MINEDU CNEB)
// -----------------------------------------------------------------------------

export interface Course {
  id: string;
  school_id: string;
  name: string;
  code?: string;
  description?: string;
  color_hex?: string;
  is_active: boolean;
  created_at: Date;
}

export interface CourseAssignment {
  id: string;
  school_id: string;
  course_id: string;
  section_id: string;
  teacher_id: string;
  academic_period_id: string;
  hours_per_week: number;
  is_active: boolean;
  created_at: Date;
}

export interface CourseCompetency {
  id: string;
  school_id: string;
  course_id: string;
  name: string;
  description?: string;
  weight: number; // 0-100
  is_active: boolean;
  created_at: Date;
}

export interface ClassTask {
  id: string;
  school_id: string;
  course_assignment_id: string;
  competency_id: string;
  academic_period_id: string;
  title: string;
  description?: string;
  task_type: TaskType;
  max_score: number;
  due_date?: Date;
  is_active: boolean;
  created_at: Date;
}

// -----------------------------------------------------------------------------
// TRANSACTIONAL (ATTENDANCE & GRADES)
// -----------------------------------------------------------------------------

export interface Attendance {
  id: string;
  school_id: string;
  student_id: string;
  course_assignment_id?: string;
  attendance_date: Date;
  status: AttendanceStatus;
  check_in_time?: string;
  record_method: RecordMethod;
  justification_reason?: string;
  registered_by?: string;
  created_at: Date;
}

export interface StudentScore {
  id: string;
  school_id: string;
  student_id: string;
  class_task_id: string;
  score: number; // 0-20
  comments?: string;
  registered_by?: string;
  registered_at: Date;
  updated_at: Date;
}

// -----------------------------------------------------------------------------
// SCHEDULES
// -----------------------------------------------------------------------------

export interface Classroom {
  id: string;
  school_id: string;
  name: string;
  code?: string;
  building?: string;
  floor?: number;
  capacity?: number;
  has_projector?: boolean;
  has_ac?: boolean;
  is_lab?: boolean;
  is_active: boolean;
  created_at: Date;
}

export interface TimeSlot {
  id: string;
  school_id: string;
  name: string;
  start_time: string; // HH:MM
  end_time: string; // HH:MM
  slot_type: 'CLASS' | 'BREAK' | 'LUNCH';
  order_num: number;
  is_active: boolean;
}

export interface ScheduleVersion {
  id: string;
  school_id: string;
  school_year_id: string;
  name: string;
  valid_from: Date;
  valid_to?: Date;
  status: 'DRAFT' | 'ACTIVE' | 'ARCHIVED';
  created_by: string;
  approved_by?: string;
  approved_at?: Date;
  created_at: Date;
}

export interface ClassSchedule {
  id: string;
  school_id: string;
  schedule_version_id: string;
  course_assignment_id: string;
  classroom_id: string;
  time_slot_id: string;
  day_of_week: DayOfWeek;
  week_type: WeekType;
  color_hex?: string;
  notes?: string;
  is_active: boolean;
  created_at: Date;
}

export interface ScheduleException {
  id: string;
  school_id: string;
  class_schedule_id: string;
  exception_date: Date;
  exception_type: 'CANCELLED' | 'MOVED' | 'SUBSTITUTION';
  replacement_teacher_id?: string;
  replacement_classroom_id?: string;
  replacement_time_slot_id?: string;
  reason?: string;
  created_by: string;
  created_at: Date;
}

// -----------------------------------------------------------------------------
// SUPPORT & OPERATIONS
// -----------------------------------------------------------------------------

export interface Notification {
  id: string;
  school_id: string;
  user_id?: string;
  target_role?: UserRole;
  notification_type: NotificationType;
  title: string;
  body: string;
  action_url?: string;
  metadata_json?: string;
  is_read: boolean;
  read_at?: Date;
  expires_at?: Date;
  created_at: Date;
}

export interface Justification {
  id: string;
  school_id: string;
  student_id: string;
  attendance_id: string;
  requested_by: string;
  reason: string;
  document_url?: string;
  status: JustificationStatus;
  reviewed_by?: string;
  review_note?: string;
  auto_approved: boolean;
  requested_at: Date;
  reviewed_at?: Date;
  expires_at?: Date;
}

export interface ReportJob {
  id: string;
  school_id: string;
  requested_by: string;
  report_type: ReportType;
  format: ReportFormat;
  parameters_json?: string;
  status: ReportJobStatus;
  file_path?: string;
  file_size_kb?: number;
  error_detail?: string;
  progress_pct: number;
  requested_at: Date;
  started_at?: Date;
  completed_at?: Date;
  expires_at?: Date;
  download_count: number;
}

export interface AuditLog {
  id: string;
  school_id?: string;
  user_id?: string;
  action: string;
  entity_type?: string;
  entity_id?: string;
  old_value_json?: string;
  new_value_json?: string;
  ip_address?: string;
  user_agent?: string;
  result: 'SUCCESS' | 'FAILURE' | 'ERROR' | 'BLOCKED';
  error_detail?: string;
  duration_ms?: number;
  created_at: Date;
}

export interface SystemConfig {
  id: string;
  school_id: string;
  config_key: string;
  config_value: string;
  value_type: 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON' | 'DATE';
  description?: string;
  is_sensitive: boolean;
  updated_by?: string;
  updated_at: Date;
  created_at: Date;
}

// -----------------------------------------------------------------------------
// SUBSCRIPTIONS & BILLING
// -----------------------------------------------------------------------------

export interface SchoolSubscription {
  id: string;
  school_id: string;
  plan_name: SubscriptionPlan;
  max_students: number;
  max_teachers: number;
  max_storage_gb: number;
  features_json?: string;
  billing_cycle: BillingCycle;
  price_monthly_soles?: number;
  starts_at: Date;
  expires_at?: Date;
  is_trial: boolean;
  status: SubscriptionStatus;
  payment_ref?: string;
  created_at: Date;
  updated_at: Date;
}

export interface FeatureFlag {
  id: string;
  school_id: string;
  feature_name: string;
  enabled: boolean;
  description?: string;
  plan_required?: SubscriptionPlan;
  rollout_pct: number;
  created_at: Date;
  updated_at: Date;
}

export interface FeatureCatalog {
  feature_key: string;
  display_name: string;
  description: string;
  category: FeatureCategory;
  default_k12: boolean;
  default_institute: boolean;
  default_university: boolean;
  min_plan: SubscriptionPlan;
  is_core: boolean;
  sort_order: number;
}

// -----------------------------------------------------------------------------
// BLOCKCHAIN
// -----------------------------------------------------------------------------

export interface BlockchainBlock {
  id: string;
  school_id: string;
  block_index: number;
  event_type: BlockchainEventType;
  entity_id?: string;
  entity_type?: string;
  payload?: string;
  previous_hash: string;
  hash: string;
  created_by?: string;
  created_at: Date;
}

// -----------------------------------------------------------------------------
// STUDENT HISTORY
// -----------------------------------------------------------------------------

export interface StudentSectionHistory {
  id: string;
  school_id: string;
  student_id: string;
  from_section_id?: string;
  to_section_id?: string;
  change_reason: SectionChangeReason;
  notes?: string;
  changed_by: string;
  changed_at: Date;
  effective_date: Date;
}

// -----------------------------------------------------------------------------
// CHAT / AI ASSISTANT
// -----------------------------------------------------------------------------

export interface ChatSession {
  id: string;
  school_id: string;
  user_id: string;
  user_role: UserRole;
  session_token?: string;
  started_at: Date;
  last_message_at?: Date;
  is_active: boolean;
  ended_at?: Date;
}

export interface ChatMessage {
  id: string;
  session_id: string;
  role: 'user' | 'assistant' | 'system';
  content: string;
  sources_json?: string;
  confidence?: number;
  tokens_used: number;
  created_at: Date;
}

export interface MineduDocument {
  id: string;
  title: string;
  doc_type: 'CURRICULO_NACIONAL' | 'PROGRAMA_CURRICULAR' | 'RESOLUCION' | 'DECRETO' | 'DIRECTIVA' | 'GUIA_DOCENTE' | 'ESTANDARES' | 'OTHER';
  year?: number;
  file_path?: string;
  description?: string;
  chunks_count: number;
  indexed_at?: Date;
  is_active: boolean;
  checksum?: string;
  created_at: Date;
}

// -----------------------------------------------------------------------------
// VIEW TYPES (for frontend display)
// -----------------------------------------------------------------------------

export interface CompetencyAverage {
  school_id: string;
  student_id: string;
  student_first_name: string;
  student_last_name: string;
  student_document: string;
  section_id: string;
  section_name: string;
  course_id: string;
  course_name: string;
  competency_id: string;
  competency_name: string;
  competency_weight: number;
  academic_period_id: string;
  academic_period_name: string;
  total_tasks_graded: number;
  average_score: number;
  min_score: number;
  max_score: number;
  achievement_level: AchievementLevel;
  achievement_label: string;
}

export interface AttendanceSummary {
  school_id: string;
  section_id: string;
  section_name: string;
  school_year_id: string;
  school_year_name: string;
  grade_number: number;
  course_id?: string;
  course_name?: string;
  attendance_date: Date;
  total_students: number;
  present_count: number;
  late_count: number;
  absent_count: number;
  excused_count: number;
  attendance_rate: number;
}

export interface StudentFinalGrade {
  school_id: string;
  student_id: string;
  student_first_name: string;
  student_last_name: string;
  section_id: string;
  section_name: string;
  course_id: string;
  course_name: string;
  academic_period_id: string;
  academic_period_name: string;
  competencies_count: number;
  total_scores: number;
  final_weighted_score: number;
  simple_average: number;
  achievement_level: AchievementLevel;
  achievement_label: string;
}

// -----------------------------------------------------------------------------
// UI HELPERS
// -----------------------------------------------------------------------------

export interface MenuItem {
  icon: string;
  label: string;
  href: string;
  roles: UserRole[];
  badge?: number;
}

export interface BreadcrumbItem {
  label: string;
  href?: string;
}

// Helper to get achievement level from score
export function getAchievementLevel(score: number): { level: AchievementLevel; label: string } {
  if (score >= 18) return { level: 'AD', label: 'Destacado' };
  if (score >= 14) return { level: 'A', label: 'Logrado' };
  if (score >= 11) return { level: 'B', label: 'En Proceso' };
  return { level: 'C', label: 'En Inicio' };
}

// Helper to get status badge color
export function getStatusColor(status: string): string {
  switch (status) {
    case 'ACTIVE':
    case 'PRESENT':
    case 'APPROVED':
    case 'COMPLETED':
      return 'bg-green-500/10 text-green-500';
    case 'LATE':
    case 'PENDING':
    case 'PROCESSING':
      return 'bg-amber-500/10 text-amber-500';
    case 'ABSENT':
    case 'REJECTED':
    case 'FAILED':
    case 'INACTIVE':
    case 'LOCKED':
      return 'bg-red-500/10 text-red-500';
    case 'EXCUSED':
    case 'SUSPENDED':
      return 'bg-blue-500/10 text-blue-500';
    default:
      return 'bg-muted text-muted-foreground';
  }
}

// Day of week labels in Spanish
export const DAY_LABELS: Record<DayOfWeek, string> = {
  1: 'Lunes',
  2: 'Martes',
  3: 'Miercoles',
  4: 'Jueves',
  5: 'Viernes',
  6: 'Sabado',
  7: 'Domingo',
};
