
export class Task {

  public id: number;
  public title: string;
  public description: string;
  public dueDate: Date;
  public priority: string;
  public status: string;
  public category: string;
  constructor(id?: number,
              title?: string,
              description?: string,
              dueDate?: Date,
              priority?: string,
              status?: string,
              category?: string){
    this.id = id || 0;
    this.title = title || "";
    this.description = description || "";
    this.dueDate = dueDate!;
    this.priority = priority || "";
    this.status = status || "";
    this.category = category || "";
  }

}
