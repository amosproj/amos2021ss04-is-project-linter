// Interface für die repository Komponente welche grob die Informationen des repository zeigt
export interface PagedProjects {
  content: Project[];
  totalElements: number;
}

export interface Project {
  gitlabInstance: string;
  gitlabProjectId: number;
  id: number;
  name: string;
  nameSpace: string;
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
  settings: Settings;
  checks: Check[];
}

interface Settings {
  feedbackMail: string;
}

interface Check {
  enabled: boolean;
  severity: string;
  description: string;
  message: string;
  fix: string;
  priority: number;
  tag: string;
}

export interface Status {
  status: string;
  lastError: string;
  errorTime: string;
  crawlerActive: boolean;
  size: number;
  lintingProgress: number;
  lintingTime: number;
}

export interface Statistics {
  [key: string]: {
    [key: string]: number;
  };
}
// Für angular tiles
export interface Tile {
  color: string;
  cols: number;
  rows: number;
  text: string;
}
