// Interface für die repository Komponente welche grob die Informationen des repository zeigt
export interface Project {
  gitlabInstance: string;
  gitlabProjectId: number;
  id: number;
  name: string;
  description: string;
  results: [];
  url: string;
  passedTestsInFilter: number;
  newPassedTestsLastMonth: number;
  passedTestsPerTag: number[];
  newPassedTestsPerTagLastMonth: number[];

  lintingResults: LintingResult[];
}

// Zum speichern der Daten des Projekts
export interface CheckResults {
  checkName: string;
  severity: string;
  result: boolean;
  category: string;
  description: string;
  tag: string;
  fix: string;
  priority: number;
  message: string; // ist Fehlermeldung
}

// Zum Speichern der Daten eines LintingResult
export interface LintingResult {
  projectId: number;
  id: number;
  lintTime: string;
  checkResults: CheckResults[];
}

export interface Config {
  checks: Check[];
}

export interface Check {
  enabled: boolean;
  severity: string;
  description: string;
  message: string;
  fix: string;
  priority: number;
  tag: string;
}
export interface ProjectSize {
  value: string;
  viewValue: string;
}