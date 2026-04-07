export interface Student {
  id: string;
  schoolId: string;
  sectionId?: string;
  userId?: string;
  firstName: string;
  lastName: string;
  documentType: string;
  documentNumber: string;
  birthDate?: string;
  bloodType?: string;
  photoUrl?: string;
  status: string;
  createdAt?: string;
}

export interface Teacher {
  id: string;
  schoolId: string;
  userId?: string;
  firstName: string;
  lastName: string;
  documentNumber: string;
  specialty?: string;
  phone?: string;
  status: string;
}

export interface Guardian {
  id: string;
  schoolId: string;
  studentId: string;
  userId?: string;
  firstName: string;
  lastName: string;
  relationship: string;
  phone?: string;
  email?: string;
  documentNumber?: string;
  isPrimaryContact: boolean;
}

export interface AcademicLevel {
  id: string;
  name: string;
  orderNum: number;
  isActive: number;
}

export interface SchoolYear {
  id: string;
  name: string;
  gradeNumber: number;
  calendarYear: number;
  academicLevelId: string;
  isActive: number;
}

export interface Section {
  id: string;
  name: string;
  maxCapacity: number;
  tutorId?: string;
  schoolYearId: string;
  isActive: number;
}

export interface Course {
  id: string;
  name: string;
  code?: string;
  description?: string;
  colorHex?: string;
  isActive: number;
}

export interface AcademicPeriod {
  id: string;
  name: string;
  periodType: string;
  startDate: string;
  endDate: string;
  isActive: number;
}
