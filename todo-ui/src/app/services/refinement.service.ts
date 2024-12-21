import { Injectable } from '@angular/core';
import { io, Socket } from 'socket.io-client';
import { environment } from '../../environments/environment';
import { AppConstants } from '../constants/app.constants';

@Injectable({
  providedIn: 'root'
})
export class RefinementService {
  private socket: Socket;

  constructor() {
    this.socket = io(environment.rooturl + AppConstants.REFINEMENT_SERVICE_API_URL + "/ws");
  }

  connect(teamId: string): void {
    this.socket.emit('/app/session/join', { teamId });
  }

  disconnect(): void {
    this.socket.disconnect();
  }

  emitTaskUpdate(teamId: string, currentTask: string | null): void {
    this.socket.emit('/app/session/task/update', { teamId, currentTask });
  }

  emitStartVoting(teamId: string): void {
    this.socket.emit('/app/session/voting/start', { teamId });
  }

  emitEndVoting(teamId: string): void {
    this.socket.emit('/app/session/voting/end', { teamId });
  }

  emitVote(teamId: string, value: number): void {
    this.socket.emit('/app/session/vote', { teamId, value });
  }

  onSessionUpdate(callback: (data: any) => void): void {
    this.socket.on('/topic/session-updates', callback);
  }
}
