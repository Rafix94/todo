import {Injectable} from '@angular/core';
import {Client, IMessage} from '@stomp/stompjs';
import {environment} from "../../environments/environment";
import {AppConstants} from "../constants/app.constants";
import {BehaviorSubject} from "rxjs";
import {KeycloakService} from "keycloak-angular";

@Injectable({
  providedIn: 'root',
})
export class RefinementService {
  private stompClient: Client | null = null;
  private connected: boolean = false;
  private connectedSubject = new BehaviorSubject<boolean>(false);
  public connected$ = this.connectedSubject.asObservable();

  constructor(private keycloakService: KeycloakService) {
  }

  async connect(): Promise<void> {
    if (this.connected) {
      return;
    }
    const token = await this.keycloakService.getToken();

    const wsUrl = `${environment.wsurl}${AppConstants.REFINEMENT_SERVICE_API_URL}/ws`;
    this.stompClient = new Client({
      brokerURL: wsUrl,
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      debug: (msg: string) => console.log(`STOMP Debug: ${msg}`),
      reconnectDelay: 5000,
    });

    this.stompClient.onConnect = () => {
      this.connected = true;
      console.log('Connected to WebSocket.');
      this.connectedSubject.next(true);
    };

    this.stompClient.onWebSocketError = (error) => {
      console.error('WebSocket error occurred:', error);
    };

    this.stompClient.onStompError = (frame) => {
      console.error('STOMP error:', frame.headers['message']);
      console.error('Additional details:', frame.body);
    };

    this.stompClient.activate();
  }

  disconnectSocket(): void {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.connected = false;
      console.log('Disconnected from WebSocket.');
    }
  }

  send(destination: string, payload: any): void {
    if (this.stompClient?.connected) {
      this.stompClient.publish({
        destination,
        body: JSON.stringify(payload),
      });
      console.log(`Message sent to ${destination}:`, payload);
    } else {
      console.error('WebSocket is not connected. Unable to send message.');
    }
  }

  emitJoinSession(teamId: string, userId: string): void {
    this.emitMessage({teamId, userId}, '/app/session/join');
  }

  emitLeaveSession(teamId: string, userId: string): void {
    this.emitMessage({teamId, userId}, '/app/session/leave');
  }

  emitStartVoting(teamId: string): void {
    this.emitMessage({teamId}, '/app/session/voting/start');
  }

  emitTaskUpdate(teamId: string, taskTitle: string, taskDescription: string): void {
    this.emitMessage({teamId, taskTitle, taskDescription}, '/app/session/task/update');
  }

  emitStopVoting(teamId: string): void {
    this.emitMessage({teamId}, '/app/session/voting/stop');
  }

  emitVote(teamId: string, userId: string, vote: number): void {
    this.emitMessage({teamId, userId, vote}, '/app/session/voting/vote');
  }

  private emitMessage(body: any, destination: string) {
    if (this.stompClient?.connected) {
      this.send(destination, body);
    } else {
      console.error('WebSocket is not connected. Attempting to reconnect...');
      this.connect();
      setTimeout(() => {
        if (this.stompClient?.connected) {
          this.send(destination, body);
        } else {
          console.error('Failed to reconnect WebSocket.');
        }
      }, 1000);
    }
  }

  subscribeToSessionStateUpdates(teamId: string, callback: (state: any) => void): void {
    if (this.stompClient) {
      this.stompClient.subscribe(`/topic/session/${teamId}`, (message: IMessage) => {
        const state = JSON.parse(message.body);
        callback(state);
      });
    }
  }

  //
  // subscribeToVotingStatus(callback: (data: any) => void): void {
  //   if (this.stompClient) {
  //     this.stompClient.subscribe('/topic/voting/status', (message: IMessage) => {
  //       const body = JSON.parse(message.body);
  //       console.log('Voting Status Update:', body);
  //       callback(body);
  //     });
  //   } else {
  //     console.error('WebSocket is not connected. Unable to subscribe to voting status.');
  //   }
  // }
  //

}
