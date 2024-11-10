import {TeamMemberDto} from "./teamMemberDto";

export interface TeamDetailsDto {
  id: string;
  name: string;
  members: TeamMemberDto[];
}
