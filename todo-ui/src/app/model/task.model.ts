export class Task {
  public id: number;
  public title: string;
  public description: string;
  public teamId: string;
  public assignedTo: string | null;

  constructor(
    id?: number,
    title?: string,
    description?: string,
    teamId?: string,
    assignedTo?: string
  ) {
    this.id = id || 0;
    this.title = title || "";
    this.description = description || "";
    this.teamId = teamId || "";  // Default to an empty string if not provided
    this.assignedTo = assignedTo || null;  // Set to null if undefined
  }
}
