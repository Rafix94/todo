export class Task {
  public id: number;
  public title: string;
  public description: string;
  public teamId: string;
  public assignedTo: string | null;
  public weight?: number;

  constructor(
    id?: number,
    title?: string,
    description?: string,
    teamId?: string,
    assignedTo?: string,
    weight?: number
  ) {
    this.id = id || 0;
    this.title = title || "";
    this.description = description || "";
    this.teamId = teamId || "";
    this.assignedTo = assignedTo || null;
    this.weight = weight;
  }
}
