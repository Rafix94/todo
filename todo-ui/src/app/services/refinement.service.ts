import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import {environment} from "../../environments/environment";
import {AppConstants} from "../constants/app.constants";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root',
})
export class RefinementService {
  private stompClient: Client | null = null;
  private connected: boolean = false;
  private connectedSubject = new BehaviorSubject<boolean>(false);
  public connected$ = this.connectedSubject.asObservable();

  constructor() {}

  connect(teamId: string): void {
    if (this.connected) {
      return;
    }

    const wsUrl = `${environment.wsurl}${AppConstants.REFINEMENT_SERVICE_API_URL}/ws`;
    this.stompClient = new Client({
      brokerURL: wsUrl,
      connectHeaders: {
        Authorization: 'Bearer YOUR_ACCESS_TOKEN',
        teamId,
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
    if (this.stompClient?.connected) {
      this.send('/app/session/join', { teamId, userId });
    } else {
      console.error('WebSocket is not connected. Attempting to reconnect...');
      this.connect(teamId);
      setTimeout(() => {
        if (this.stompClient?.connected) {
          this.send('/app/session/join', { teamId, userId });
        } else {
          console.error('Failed to reconnect WebSocket.');
        }
      }, 1000);
    }
  }

  subscribeToParticipants(callback: (data: any) => void): void {
    if (this.stompClient) {
      this.stompClient.subscribe('/topic/session/participants', (message: IMessage) => {
        const body = JSON.parse(message.body);
        console.log('Participant Update:', body);
        callback(body);
      });
    } else {
      console.error('WebSocket is not connected. Unable to subscribe to participants.');
    }
  }
}
